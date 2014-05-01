package at.yawk.felix.module;

import java.util.Collections;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Data set of meta-information on a module.
 *
 * @author Jonas Konrad (yawkat)
 */
@NotThreadSafe
@AllArgsConstructor(staticName = "create")
@EqualsAndHashCode
public class ModuleProperties {
    /**
     * The dependencies of this module.
     */
    private final Set<Class<?>> dependencies;
    /**
     * What classes this module should NOT be registered as: The default behaviour would be registering this module
     * as all its superclasses and interfaces.
     */
    private final Set<Class<?>> excludedClasses;

    public Set<Class<?>> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    public Set<Class<?>> getExcludedClasses() {
        return Collections.unmodifiableSet(excludedClasses);
    }
}
