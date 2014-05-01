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
            return !moduleManager.has(clazz);
        }

        @Override
        public boolean register(ModuleManager moduleManager, Object module) {
            if (register(moduleManager, module.getClass())) {
                return true;
            } else {
                throw new IllegalStateException(
                        "A module of the type " + module.getClass() + " is already registered!");
            }
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
