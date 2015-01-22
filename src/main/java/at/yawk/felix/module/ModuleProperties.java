/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
    private static final ModuleProperties empty = create(Collections.emptySet());

    /**
     * Create an empty ModuleProperties instance.
     */
    public static ModuleProperties create() {
        return empty;
    }

    /**
     * Create ModuleProperties with the given dependencies.
     */
    public static ModuleProperties create(Set<Class<?>> dependencies) {
        return create(dependencies, Collections.emptySet());
    }

    /**
     * Create ModuleProperties with the given dependencies and excluded classes.
     */
    public static ModuleProperties create(Set<Class<?>> dependencies, Set<Class<?>> excludedClasses) {
        return create(dependencies, excludedClasses, Collections.emptySet());
    }

    /**
     * The dependencies of this module.
     */
    private final Set<Class<?>> dependencies;
    /**
     * What classes this module should <i>not</i> be registered as: The default behaviour would be registering this
     * module as all its superclasses and interfaces.
     */
    private final Set<Class<?>> excludedClasses;
    /**
     * The soft dependencies of this module. These are loaded <i>after</i> the module itself so they should not be
     * relied upon in the init methods.
     */
    private final Set<Class<?>> softDependencies;

    public Set<Class<?>> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    public Set<Class<?>> getSoftDependencies() {
        return Collections.unmodifiableSet(softDependencies);
    }

    public Set<Class<?>> getExcludedClasses() {
        return Collections.unmodifiableSet(excludedClasses);
    }
}
