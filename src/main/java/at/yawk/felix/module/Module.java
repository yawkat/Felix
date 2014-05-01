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
    private Set<Class<?>> dependencies = null;

    private Set<Class<?>> doListDependencies() {
        dependencyLock.lock();
        dependencyListThread = Thread.currentThread();
        try {
            if (dependencies != null) { return dependencies; }

            dependencies = new HashSet<>();
            listDependencies();
            return dependencies;
        } finally {
            dependencyListThread = null;
            dependencyLock.unlock();
        }
    }

    public final ModuleProperties getProperties() {
        return ModuleProperties.create(doListDependencies(), Collections.emptySet());
    }

    protected void listDependencies() {}

    protected final void require(Class<?> moduleType) {
        if (Thread.currentThread() != dependencyListThread) {
            if (dependencyListThread == null) {
                throw new IllegalStateException("Can only require other modules inside listDependencies");
            } else {
                throw new IllegalStateException(
                        "Can only require other modules from the thread listDependencies is called from");
            }
        }

        dependencies.add(moduleType);
    }

    @Override
    public void initialize(ModuleManager manager) {}
}
