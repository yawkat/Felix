package at.yawk.felix.module;

import at.yawk.felix.FelixUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Calls all methods annotated with Init on initialization.
 *
 * @author Jonas Konrad (yawkat)
 */
class AnnotationInitializer implements Initializer {
    static final Initializer instance = new AnnotationInitializer();

    @SuppressWarnings("deprecation")
    @Override
    public void initialize(ModuleManager moduleManager, Object module) {
        FelixUtil.getSuperClasses(module.getClass())
                .flatMap(clazz -> Stream.concat(Arrays.stream(clazz.getDeclaredMethods()),
                                                Arrays.stream(clazz.getDeclaredFields())))
                .filter(annotated -> annotated.isAnnotationPresent(Init.class))
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(Init.class).priority()))
                .forEachOrdered(annotated -> {
                    if (annotated instanceof Method) {
                        Method method = (Method) annotated;
                        Class<?>[] parameters = method.getParameterTypes();
                        try {
                            annotated.setAccessible(true);
                            if (parameters.length == 0) {
                                method.invoke(module);
                            } else if (Arrays.equals(parameters, new Class[]{ ModuleManager.class })) {
                                method.invoke(module, moduleManager);
                            }
                        } catch (ReflectiveOperationException e) {
                            throw new InvalidModuleException(e);
                        }
                    } else {
                        Field field = (Field) annotated;
                        field.setAccessible(true);
                        try {
                            if (field.getType() == ModuleManager.class) {
                                field.set(module, moduleManager);
                            }
                        } catch (IllegalAccessException e) {
                            throw new InvalidModuleException(e);
                        }
                    }
                });
    }
}
