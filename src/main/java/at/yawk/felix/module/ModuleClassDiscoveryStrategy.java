/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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

    @NonNull
    @Override
    public Optional<ModuleProperties> findProperties(@NonNull Object module) {
        return Optional.of(module).filter(m -> m instanceof Module) // check if this is actually a module
                .map(m -> ((Module) m).getProperties()); // return properties
    }
}
