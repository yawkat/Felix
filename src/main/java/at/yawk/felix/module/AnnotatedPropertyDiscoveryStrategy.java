package at.yawk.felix.module;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;

/**
 * PropertyDiscoveryStrategy for the AnnotatedModule annotation.
 *
 * @author Jonas Konrad (yawkat)
 */
class AnnotatedPropertyDiscoveryStrategy implements PropertyDiscoveryStrategy {
    static final PropertyDiscoveryStrategy instance = new AnnotatedPropertyDiscoveryStrategy();

    private AnnotatedPropertyDiscoveryStrategy() {}

    @Override
    public Optional<ModuleProperties> findProperties(@NonNull Object module) {
        // find the most specific properties
        return findProperties(module.getClass()).findFirst();
    }

    /**
     * Recursively find all annotations on the given class and all superclasses. Most specific annotations come first.
     */
    private static Stream<ModuleProperties> findProperties(Class<?> type) {
        if (type == Object.class) {
            return Stream.empty();
        } else {
            Stream<ModuleProperties> self = Stream.of(type)
                    // check for annotation
                    .filter(clazz -> clazz.isAnnotationPresent(AnnotatedModule.class))
                            // map to annotation
                    .map(clazz -> clazz.getAnnotation(AnnotatedModule.class))
                            // convert annotation
                    .map(AnnotatedPropertyDiscoveryStrategy::toProperties);

            // search parent classes as well
            return Stream.concat(self, findProperties(type.getSuperclass()));
        }
    }

    private static ModuleProperties toProperties(AnnotatedModule annotation) {
        return ModuleProperties.create(ImmutableSet.copyOf(annotation.dependencies()),
                                       ImmutableSet.copyOf(annotation.excludedFromRegistration()));
    }
}
