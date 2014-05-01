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
