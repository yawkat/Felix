/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

/**
 * Worker that helps initializing newly registered modules.
 *
 * @author Jonas Konrad (yawkat)
 */
public interface Initializer {
    /**
     * Called after module registration. Call all initializers handled by this class in the given module.
     */
    void initialize(ModuleManager moduleManager, Object module);
}
