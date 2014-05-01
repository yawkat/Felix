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
