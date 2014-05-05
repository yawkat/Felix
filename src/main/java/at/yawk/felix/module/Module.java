package at.yawk.felix.module;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class for modules. Extending this class will allow registration in ModuleManager.
 * <p>
 * This class does not have to be extended for all modules: Annotating with AnnotatedModule will work as well
 * independently.
 *
 * @author Jonas Konrad (yawkat)
 */
public abstract class Module implements InitializableModule {
    private final Lock dependencyLock = new ReentrantLock();

    private Thread dependencyListThread = null;

    private boolean dependenciesLoaded = false;
    private final Set<Class<?>> dependencies = new HashSet<>();
    private final Set<Class<?>> softDependencies = new HashSet<>();

    private void doListDependencies() {
        if (dependenciesLoaded) { return; }
        dependencyLock.lock();
        dependencyListThread = Thread.currentThread();
        try {
            if (!dependenciesLoaded) {
                listDependencies();
                dependenciesLoaded = true;
            }
        } finally {
            dependencyListThread = null;
            dependencyLock.unlock();
        }
    }

    public final ModuleProperties getProperties() {
        doListDependencies();
        return ModuleProperties.create(dependencies, Collections.emptySet(), softDependencies);
    }

    /**
     * Put all your require() and requireSoft() calls in here.
     */
    protected void listDependencies() {}

    /**
     * Require a module as a dependency.
     */
    protected final void require(Class<?> moduleType) {
        checkRequire(moduleType);
        dependencies.add(moduleType);
    }

    /**
     * Require a module as a soft dependency, will be loaded after initialization.
     */
    protected final void requireSoft(Class<?> moduleType) {
        checkRequire(moduleType);
        softDependencies.add(moduleType);
    }

    private void checkRequire(Class<?> moduleType) {
        if (Thread.currentThread() != dependencyListThread) {
            if (dependencyListThread == null) {
                throw new IllegalStateException("Can only require other modules inside listDependencies");
            } else {
                throw new IllegalStateException(
                        "Can only require other modules from the thread listDependencies is called from");
            }
        }
    }

    @Override
    public void initialize(ModuleManager manager) {}
}
