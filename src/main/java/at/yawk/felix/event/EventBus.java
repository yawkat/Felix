/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.event;

import at.yawk.felix.FelixUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Event bus.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor(staticName = "create")
public class EventBus {
    private final ExceptionHandler exceptionHandler;

    private final ConcurrentMap<Class<?>, Collection<EventHandler<?>>> handlers = new ConcurrentHashMap<>();

    private final Map<Class<?>, EventHandler[]> bakedHandlers = new ConcurrentHashMap<>();

    private final List<EventHandlerFinderStrategy> discoveryStrategies = new CopyOnWriteArrayList<>();

    {
        discoveryStrategies.add(AnnotationEventHandlerFinderStrategy.instance);
        discoveryStrategies.add(GuavaSubscribeFinderStrategy.instance);
    }

    public static EventBus create() { return create(ExceptionHandler.PRINT_TRACE); }

    public void addEventHandlerFinderStrategy(@NonNull EventHandlerFinderStrategy strategy) {
        discoveryStrategies.add(0, strategy);
    }

    /**
     * Post an event. Event handler priorities are followed (as long as event handlers are of the same type).
     */
    public <Event> Event post(@NonNull Event event) {
        return post(event, false);
    }

    /**
     * Post an event in a parallel mode. If we are in a ForkJoinPool, event processing will be done across its threads.
     * Priorities might not be honoured.
     */
    public <Event> Event postParallel(@NonNull Event event) {
        return post(event, true);
    }

    private <Event> Event post(@NonNull Event event, boolean parallel) {
        return doPost(event, parallel);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private <Event> Event doPost(@NonNull Event event, boolean parallel) {
        // the action (consume, handle any errors)
        Consumer<EventHandler<? super Event>> action = handler -> {
            try {
                handler.getHandler().consume(event);
            } catch (Throwable t) {
                try {
                    exceptionHandler.onException(event, t, handler);
                } catch (Throwable u) {
                    // we can't do much more
                    //noinspection CallToPrintStackTrace
                    u.printStackTrace();
                }
            }
        };
        // for all superclasses as well
        // as priorities are not shared across event types anyway we can use parallel without any problems.
        Stream<EventHandler> handlers = FelixUtil.getSuperClasses(event.getClass()).flatMap(type -> {
            EventHandler[] list = bakedHandlers.get(type);
            // check if we actually have any handlers
            return list == null ? Stream.empty() : Stream.of(list);
        });
        if (parallel) {
            // ignore priority
            handlers.parallel().forEach((Consumer) action);
        } else {
            // follow priority
            handlers.sequential().forEachOrdered((Consumer) action);
        }
        return event;
    }

    /**
     * Use the registered strategies to add any event handlers in the given object.
     */
    @NonNull
    public SubscribeHandle subscribe(@NonNull Object handler) {
        List<SubscribeHandle> handles = new ArrayList<>();
        discoveryStrategies.stream()
                           .flatMap(strategy -> strategy.findEventHandlers(handler))
                           .sequential()
                           .forEach(h -> handles.add(subscribeEventHandler(h)));
        return SubscribeHandle.join(handles);
    }

    /**
     * Dynamically subscribe to an event.
     */
    @NonNull
    public <Event> SubscribeHandle subscribe(@NonNull Class<Event> type,
                                             @NonNull EventHandler.ThrowingConsumer<? super Event> handler) {
        return subscribe(type, handler, EventHandler.DEFAULT_PRIORITY);
    }

    /**
     * Dynamically subscribe to an event.
     */
    @NonNull
    public <Event> SubscribeHandle subscribe(@NonNull Class<Event> type,
                                             @NonNull EventHandler.ThrowingConsumer<? super Event> handler,
                                             int priority) {
        return subscribeEventHandler(new EventHandler<>(type, handler, priority));
    }

    /**
     * Dynamically subscribe to an event.
     */
    @NonNull
    public SubscribeHandle subscribeEventHandler(@NonNull EventHandler<?> eventHandler) {
        return subscribe(eventHandler, eventHandler.getType());
    }

    @NonNull
    private SubscribeHandle subscribe(@NonNull EventHandler<?> eventHandler, @NonNull Class<?> on) {
        // we do supertype recursion in the post method so we don't need it here
        Collection<EventHandler<?>> handlerList = handlers.computeIfAbsent(on,
                                                                           clazz -> Collections.synchronizedSet(new HashSet<>()));
        handlerList.add(eventHandler);
        bake(on);
        return () -> {
            handlerList.remove(eventHandler);
            bake(on);
        };
    }

    private void bake(Class<?> eventType) {
        Collection<EventHandler<?>> handlerQueue = handlers.get(eventType);
        EventHandler[] handlerList = handlerQueue.toArray(new EventHandler[handlerQueue.size()]);
        Arrays.sort(handlerList);
        bakedHandlers.put(eventType, handlerList);
    }
}
