package at.yawk.felix.event;

import at.yawk.felix.FelixUtil;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

/**
 * Event bus.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor(staticName = "create")
public class EventBus {
    private final ExceptionHandler exceptionHandler;

    private final ConcurrentMap<Class<?>, Queue<EventHandler<?>>> handlers = new ConcurrentHashMap<>();
    private final List<EventHandlerFinderStrategy> discoveryStrategies = new CopyOnWriteArrayList<>();

    {
        discoveryStrategies.add(AnnotationEventHandlerFinderStrategy.instance);
        discoveryStrategies.add(GuavaSubscribeFinderStrategy.instance);
    }

    public static EventBus create() { return create(ExceptionHandler.PRINT_TRACE); }

    public void addEventHandlerFinderStrategy(EventHandlerFinderStrategy strategy) {
        discoveryStrategies.add(0, strategy);
    }

    /**
     * Post an event. Event handler priorities are followed (as long as event handlers are of the same type).
     */
    public <Event> Event post(Event event) {
        return post(event, false);
    }

    /**
     * Post an event in a parallel mode. If we are in a ForkJoinPool, event processing will be done across its
     * threads. Priorities might not be honoured.
     */
    public <Event> Event postParallel(Event event) {
        return post(event, true);
    }

    private <Event> Event post(Event event, boolean parallel) {
        // for all superclasses as well
        // as priorities are not shared across event types anyway we can use parallel without any problems.
        FelixUtil.getSuperClasses(event.getClass()).parallel().forEach(type -> {
            Queue<EventHandler<? super Event>> list = (Queue) handlers.get(type);
            // check if we actually have any handlers
            if (list != null) {
                // the action (consume, handle any errors)
                Consumer<EventHandler<? super Event>> action = handler -> {
                    try {
                        handler.getHandler().consume(event);
                    } catch (Throwable t) {
                        try {
                            exceptionHandler.onException(event, t, handler);
                        } catch (Throwable u) {
                            // we can't do much more
                            u.printStackTrace();
                        }
                    }
                };
                if (parallel) {
                    // ignore priority
                    list.parallelStream().forEach(action);
                } else {
                    // follow priority
                    list.stream().forEachOrdered(action);
                }
            }
        });
        return event;
    }

    /**
     * Use the registered strategies to add any event handlers in the given object.
     */
    public void subscribe(Object handler) {
        discoveryStrategies.stream()
                           .flatMap(strategy -> strategy.findEventHandlers(handler))
                           .forEach(this::subscribeEventHandler);
    }

    /**
     * Dynamically subscribe to an event.
     */
    public <Event> void subscribe(Class<Event> type,
                                  EventHandler.ThrowingConsumer<? super Event> handler) {
        subscribe(type, handler, EventHandler.DEFAULT_PRIORITY);
    }

    /**
     * Dynamically subscribe to an event.
     */
    public <Event> void subscribe(Class<Event> type,
                                  EventHandler.ThrowingConsumer<? super Event> handler,
                                  int priority) {
        subscribeEventHandler(new EventHandler<>(type, handler, priority));
    }

    /**
     * Dynamically subscribe to an event.
     */
    public void subscribeEventHandler(EventHandler<?> eventHandler) {
        subscribe(eventHandler, eventHandler.getType());
    }

    private void subscribe(EventHandler<?> eventHandler, Class<?> on) {
        // we do supertype recursion in the post method so we don't need it here
        Queue<EventHandler<?>> handlerList =
                handlers.computeIfAbsent(on, clazz -> new PriorityQueue<EventHandler<?>>());
        handlerList.offer(eventHandler);
    }
}
