package at.yawk.felix.event;

import at.yawk.felix.FelixUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Event handler strategy that looks for Subscribe annotations.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor
abstract class AnnotationEventHandlerFinderStrategy<A extends Annotation> implements EventHandlerFinderStrategy {
    static final EventHandlerFinderStrategy instance =
            new AnnotationEventHandlerFinderStrategy<Subscribe>(Subscribe.class) {
                @Override
                protected EventHandler<?> makeHandler(Subscribe annotation, Object object, Method method) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    assert parameterTypes.length == 1 : method;
                    EventHandler.ThrowingConsumer<Object> consumer;
                    if (annotation.parallel()) {
                        consumer = event -> method.invoke(object, event);
                    } else {
                        consumer = event -> {
                            synchronized (method) {
                                method.invoke(object, event);
                            }
                        };
                    }
                    return new EventHandler<>(parameterTypes[0], consumer, annotation.priority());
                }
            };

    private final Class<A> marker;

    @NonNull
    @Override
    @SuppressWarnings("deprecation")
    public Stream<EventHandler<?>> findEventHandlers(@NonNull Object in) {
        Stream<Class<?>> classes = FelixUtil.getSuperClasses(in.getClass());
        return classes.flatMap(c -> Stream.of(c.getDeclaredMethods()))
                // search for subscribe annotations
                .filter(m -> m.isAnnotationPresent(marker))
                        // filter overridden methods so they don't appear twice
                        // wrap
                .map(MethodInheritanceEqualityWrapper::new)
                        // filter
                .distinct()
                        // unwrap
                .map(wrapper -> wrapper.method)
                        // create event handlers
                .map(method -> {
                    method.setAccessible(true);
                    A annotation = method.getAnnotation(marker);
                    return makeHandler(annotation, in, method);
                });
    }

    protected abstract EventHandler<?> makeHandler(A annotation, Object object, Method method);

    /**
     * Helper class that
     */
    @RequiredArgsConstructor
    private static class MethodInheritanceEqualityWrapper {
        @NonNull private final Method method;

        private static boolean same(Method a, Method b) {
            if (a.getDeclaringClass() != b.getDeclaringClass()) { return false; }
            if (!a.getName().equals(b.getName())) { return false; }
            if (!Arrays.equals(a.getParameterTypes(), b.getParameterTypes())) { return false; }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MethodInheritanceEqualityWrapper)) { return false; }
            Method m = ((MethodInheritanceEqualityWrapper) o).method;
            return same(this.method, m);
        }

        @Override
        public int hashCode() {
            return method.getDeclaringClass().hashCode() ^
                   (method.getName().hashCode() * 31) ^
                   (Arrays.hashCode(method.getParameterTypes()) * 31 * 31);
        }
    }
}
