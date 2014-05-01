package at.yawk.felix.module;

import java.util.Optional;
import lombok.NonNull;

/**
 * PropertyDiscoveryStrategy for extensions of the Module class.
 *
 * @author Jonas Konrad (yawkat)
 */
class ModuleClassDiscoveryStrategy implements PropertyDiscoveryStrategy {
    static final PropertyDiscoveryStrategy instance = new ModuleClassDiscoveryStrategy();

    private ModuleClassDiscoveryStrategy() {}

    @Override
    public Optional<ModuleProperties> findProperties(@NonNull Object module) {
        return Optional.of(module).filter(m -> m instanceof Module) // check if this is actually a module
                .map(m -> ((Module) m).getProperties()); // return properties
    }
}
