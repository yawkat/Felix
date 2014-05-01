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
