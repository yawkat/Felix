/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

/**
 * An initializer that accepts instances of InitializableModule.
 *
 * @author Jonas Konrad (yawkat)
 */
class InterfaceInitializer implements Initializer {
    static final Initializer instance = new InterfaceInitializer();

    @Override
    public void initialize(ModuleManager moduleManager, Object module) {
        if (module instanceof InitializableModule) {
            ((InitializableModule) module).initialize(moduleManager);
        }
    }
}
