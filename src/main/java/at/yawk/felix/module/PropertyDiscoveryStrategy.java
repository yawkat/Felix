package at.yawk.felix.module;

import java.util.Optional;
import javax.annotation.concurrent.ThreadSafe;
import lombok.NonNull;

/**
 * Strategy that can be used to try to find ModuleProperties for given module objects.
 *
 * @author Jonas Konrad (yawkat)
 */
@ThreadSafe
public interface PropertyDiscoveryStrategy {
    /**
     * Try to find the properties of a module.
     *
     * @return The found properties or an empty optional if no properties were found and other strategies should be
     * attempted.
     */
    @NonNull
    Optional<ModuleProperties> findProperties(@NonNull Object module);
}
