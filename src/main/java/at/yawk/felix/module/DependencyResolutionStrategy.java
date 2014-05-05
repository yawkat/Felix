package at.yawk.felix.module;

import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;

/**
 * Strategy that should be followed when trying to resolve dependencies.
 *
 * @author Jonas Konrad (yawkat)
 */
@ThreadSafe
public interface DependencyResolutionStrategy {
    /**
     * Registers all missing dependencies using ModuleManager.register(Class).
     */
    public static final DependencyResolutionStrategy REGISTER = (moduleManager, dependency, registrationProperties) -> {
        moduleManager.registerModule(dependency,
                                     registrationProperties.withDuplicateFinder(DuplicateFinder.IGNORE_DUPLICATE_CLASSES));
    };
    /**
     * Fails registration by throwing an UnsupportedOperationException.
     */
    public static final DependencyResolutionStrategy FAIL = (moduleManager, dependency, registrationProperties) -> {
        throw new UnsupportedOperationException("Missing dependency " + dependency);
    };

    /**
     * Attempt to require a dependency.
     * <p>
     * <b>This method may throw exceptions that will then be forwarded to the registering code.</b>
     *
     * @param dependency             The dependency
     * @param registrationProperties The registration properties used. In most cases, these should be used for further
     *                               calls.
     */
    void loadDependency(@NonNull ModuleManager moduleManager,
                        @NonNull Class<?> dependency,
                        @NonNull RegistrationProperties registrationProperties);
}
