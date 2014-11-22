package at.yawk.felix.module;

import at.yawk.felix.module.annotated.*;
import at.yawk.felix.module.annotated.Module;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

/**
 * PropertyDiscoveryStrategy for the .annotated package annotations.
 *
 * @author yawkat
 */
class AnnotatedPropertyDiscoveryStrategy2 implements PropertyDiscoveryStrategy {
    static final PropertyDiscoveryStrategy instance = new AnnotatedPropertyDiscoveryStrategy2();

    private AnnotatedPropertyDiscoveryStrategy2() {}

    @Override
    public Optional<ModuleProperties> findProperties(@NonNull Object module) {
        Set<Class<?>> dependencies = new HashSet<>();
        Set<Class<?>> softDependencies = new HashSet<>();
        Set<Class<?>> excludeRegistration = new HashSet<>();

        Module moduleAnnotation = collectAnnotations(
                module.getClass(),
                dependencies,
                softDependencies,
                excludeRegistration
        );

        if (moduleAnnotation == null) {
            return Optional.empty();
        }

        ModuleProperties properties =
                ModuleProperties.create(dependencies, excludeRegistration, softDependencies);

        return Optional.of(properties);
    }

    private static at.yawk.felix.module.annotated.Module collectAnnotations(
            Class<?> on,
            Set<Class<?>> dependencies,
            Set<Class<?>> softDependencies,
            Set<Class<?>> excludeRegistration
    ) {
        if (on == null) { return null; }

        for (Dependency dependency : on.getAnnotationsByType(Dependency.class)) {
            (dependency.soft() ? softDependencies : dependencies).add(dependency.value());
        }
        for (Register register : on.getAnnotationsByType(Register.class)) {
            Class<?> as = register.as();
            if (!as.isAssignableFrom(on)) {
                throw new InvalidModuleException(
                        on + " is annotated to be registered as " + as + " even though it doesn't extend it!");
            }
            if (!register.register()) {
                excludeRegistration.add(as);
            }
        }
        at.yawk.felix.module.annotated.Module module =
                on.getAnnotation(at.yawk.felix.module.annotated.Module.class);

        at.yawk.felix.module.annotated.Module superAnnotation =
                collectAnnotations(on.getSuperclass(), dependencies, softDependencies, excludeRegistration);
        if (module == null) {
            module = superAnnotation;
        }
        for (Class<?> itf : on.getInterfaces()) {
            at.yawk.felix.module.annotated.Module itfAnnotation =
                    collectAnnotations(itf, dependencies, softDependencies, excludeRegistration);
            if (module == null) {
                module = itfAnnotation;
            }
        }
        return module;
    }
}
