/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

/**
 * Strategy that defines how duplicate modules should be treated.
 *
 * @author Jonas Konrad (yawkat)
 */
public interface DuplicateFinder {
    /**
     * Simply ignore modules that have already been registered when trying to register them by class and throw an
     * IllegalStateException when trying to register duplicate module classes by object.
     */
    public static final DuplicateFinder IGNORE_DUPLICATE_CLASSES = new DuplicateFinder() {
        @Override
        public boolean register(ModuleManager moduleManager, Class<?> clazz) {
            return !moduleManager.hasOrLoading(clazz);
        }

        @Override
        public boolean register(ModuleManager moduleManager, Object module) {
            if (!moduleManager.hasOrLoadingExact(module.getClass())) {
                return true;
            } else {
                throw new IllegalStateException(
                        "A module of the type " + module.getClass() + " is already registered!");
            }
        }
    };
    /**
     * Simply ignore modules that have already been registered when trying to register them by class and allow
     * duplicate modules when registering them directly (anonymous or object).
     */
    public static final DuplicateFinder IGNORE_DUPLICATE_CLASSES_ALLOW_DIRECT = new DuplicateFinder() {
        @Override
        public boolean register(ModuleManager moduleManager, Class<?> clazz) {
            return !moduleManager.hasOrLoading(clazz);
        }

        @Override
        public boolean register(ModuleManager moduleManager, Object module) {
            return true;
        }
    };

    /**
     * Called before a module is instantiated and registered.
     * <p>
     * <b>This method may throw exceptions that will then be forwarded to the registering code.</b>
     *
     * @return Whether to continue registering this module.
     */
    boolean register(ModuleManager moduleManager, Class<?> clazz);

    /**
     * Called before a module is registered. Will also be called after #register(ModuleManager, Class).
     * <p>
     * <b>This method may throw exceptions that will then be forwarded to the registering code.</b>
     *
     * @return Whether to continue registering this module.
     */
    default boolean register(ModuleManager moduleManager, Object module) {
        return register(moduleManager, module.getClass());
    }
}
