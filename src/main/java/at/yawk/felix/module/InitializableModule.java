/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

/**
 * Interface to allow initialization of modules registered to ModuleManager.
 *
 * @author Jonas Konrad (yawkat)
 */
public interface InitializableModule {
    /**
     * Called after this module was registered in the given ModuleManager.
     */
    void initialize(ModuleManager manager);
}
