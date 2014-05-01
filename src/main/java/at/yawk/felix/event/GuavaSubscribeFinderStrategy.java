package at.yawk.felix.event;

import java.lang.reflect.Method;

/**
 * EventHandler strategy that looks for the guava Subscribe annotation in case someone used those accidently or they
 * want to use them for abstraction.
 *
 * @author Jonas Konrad (yawkat)
 */
class GuavaSubscribeFinderStrategy extends AnnotationEventHandlerFinderStrategy<com.google.common.eventbus.Subscribe> {
    static final EventHandlerFinderStrategy instance =
            new GuavaSubscribeFinderStrategy(com.google.common.eventbus.Subscribe.class);

    GuavaSubscribeFinderStrategy(Class marker) {
        super(marker);
    }

    @Override
    protected EventHandler<?> makeHandler(com.google.common.eventbus.Subscribe annotation,
                                          Object object,
                                          Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        assert parameterTypes.length == 1 : method;
        EventHandler.ThrowingConsumer<Object> consumer;
        consumer = event -> {
            synchronized (method) {
                method.invoke(object, event);
            }
        };
        return new EventHandler<>(parameterTypes[0], consumer, EventHandler.DEFAULT_PRIORITY);
    }
}
