package at.yawk.felix.module;

import at.yawk.felix.FelixUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Calls all methods annotated with Init on initialization.
 *
 * @author Jonas Konrad (yawkat)
 */
class AnnotationInitializer implements Initializer {
    static final Initializer instance = new AnnotationInitializer();

    @Override
    public void initialize(ModuleManager moduleManager, Object module) {
        FelixUtil.getSuperClasses(module.getClass())
                 .flatMap(clazz -> Stream.of(clazz.getDeclaredMethods()))
                 .filter(method -> method.isAnnotationPresent(Init.class))
                 .sorted(Comparator.comparingInt(m -> m.getAnnotation(Init.class).priority()))
                 .forEachOrdered(method -> {
                     Class<?>[] parameters = method.getParameterTypes();
                     try {
                         method.setAccessible(true);
                         if (parameters.length == 0) {
                             method.invoke(module);
                         } else if (Arrays.equals(parameters, new Class[] {ModuleManager.class})) {
                             method.invoke(module, moduleManager);
                         }
                     } catch (ReflectiveOperationException e) {
                         throw new IllegalStateException(e);
                     }
                 });
    }
}
