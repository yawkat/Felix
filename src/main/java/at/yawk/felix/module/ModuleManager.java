package at.yawk.felix.module;

import at.yawk.felix.FelixUtil;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.concurrent.ThreadSafe;
import lombok.*;

/**
 * Main module manager class.
 *
 * @author Jonas Konrad (yawkat)
 */
@NoArgsConstructor(staticName = "create")
@ThreadSafe
public class ModuleManager {
    /**
     * All modules. Each value must only contain modules of the key type.
     */
    private final ConcurrentMap<Class<?>, Collection<ModuleWrapper>> modules = new ConcurrentHashMap<>();

    private final List<PropertyDiscoveryStrategy> discoveryStrategies = new CopyOnWriteArrayList<>();
    private final List<Initializer> initializers = new CopyOnWriteArrayList<>();

    {
        discoveryStrategies.add(AnnotatedPropertyDiscoveryStrategy.instance);
        discoveryStrategies.add(ModuleClassDiscoveryStrategy.instance);

        initializers.add(AnnotationInitializer.instance);
        initializers.add(InterfaceInitializer.instance);
    }

    /**
     * Return a module by class.
     *
     * @throws java.lang.IllegalStateException if no module of that type is registered.
     */
    @NonNull
    public <M> M get(@NonNull Class<M> clazz) {
        return optional(clazz).orElseThrow(() -> new IllegalStateException("No such module: " + clazz));
    }

    /**
     * Returns whether a module of the given type exists.
     */
    public boolean has(@NonNull Class<?> clazz) {
        return optional(clazz).isPresent();
    }

    /**
     * Returns any registered module of the given type or an empty optional if none was found.
     */
    @NonNull
    public <M> Optional<M> optional(@NonNull Class<M> clazz) {
        return all(clazz).findAny();
    }

    /**
     * Returns all modules of the given type.
     */
    @NonNull
    public <M> Stream<M> all(@NonNull Class<M> clazz) {
        return modules.getOrDefault(clazz, Collections.emptySet()).stream().map(w -> (M) w.module);
    }

    /**
     * Perform an action for each module of the given type. Execution is parallel if in a ForkJoinPool.
     */
    public <M> void forEach(@NonNull Class<M> clazz, @NonNull Consumer<M> action) {
        all(clazz).parallel().forEach(action);
    }

    public void addPropertyDiscoveryStrategy(@NonNull PropertyDiscoveryStrategy strategy) {
        discoveryStrategies.add(0, strategy);
    }

    public void addInitializer(Initializer initializer) {
        initializers.add(initializer);
    }

    /**
     * Attempt to register a module by class. The class needs to have an empty constructor.
     * <p>
     * Duplicate modules are ignored: If a module of the same type already exists, no registration is performed.
     */
    public void registerModule(Class<?> type) {
        registerModule(type, RegistrationProperties.defaults);
    }

    /**
     * Attempt to register a module by class. The class needs to have an empty constructor.
     */
    @Synchronized
    public void registerModule(Class<?> type, RegistrationProperties properties) {
        if (!properties.getDuplicateFinder().register(this, type)) { return; }

        Object module;
        try {
            // try to create a new instance
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            module = constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }

        registerModuleObject(module, properties);
    }

    /**
     * Attempt to register a module object.
     *
     * @throws java.lang.IllegalStateException if a module of the same type already exists.
     */
    public void registerModuleObject(@NonNull Object module) {
        registerModuleObject(module, RegistrationProperties.defaults);
    }

    /**
     * Register a module object.
     */
    @Synchronized
    public void registerModuleObject(@NonNull Object module, RegistrationProperties registrationProperties) {
        if (!registrationProperties.getDuplicateFinder().register(this, module)) { return; }

        Optional<ModuleProperties> optionalProperties = findProperties(module);
        ModuleProperties properties = optionalProperties.orElseThrow(() -> new IllegalArgumentException(
                "No module properties found. Either annotate your module with @AnnotatedModule or extend the Module " +
                "class."
        ));
        for (Class<?> dependency : properties.getDependencies()) {
            registrationProperties.getDependencyResolutionStrategy()
                                  .loadDependency(this, dependency, registrationProperties);
        }
        registerModuleWithoutDependencies(module, properties);
    }

    /**
     * Find the module properties of a given module.
     */
    private Optional<ModuleProperties> findProperties(@NonNull Object module) {
        return discoveryStrategies.stream()
                                  .map(strategy -> strategy.findProperties(module))
                                  .filter(Optional::isPresent)
                                  .map(Optional::get)
                                  .findFirst();
    }

    /**
     * Register a module. Dependency checks are not performed.
     */
    private void registerModuleWithoutDependencies(@NonNull Object module, @NonNull ModuleProperties properties) {
        ModuleWrapper wrapper = new ModuleWrapper(module, properties);
        Stream<Class<?>> classes =
                FelixUtil.getSuperClasses(module.getClass()).filter(c -> !properties.getExcludedClasses().contains(c));

        classes.parallel().forEach(clazz -> registerModule(wrapper, clazz));

        initialize(module);
    }

    private void initialize(Object module) {
        initializers.forEach(ini -> ini.initialize(this, module));
    }

    /**
     * Register a module for a type.
     */
    private void registerModule(ModuleWrapper module, Class<?> clazz) {
        // this is assumed when accessing the modules map
        assert clazz.isInstance(module.module) : module.module + " " + clazz;

        // if no modules of the same class are registered yet we must create the list
        Collection<ModuleWrapper> collected =
                modules.computeIfAbsent(clazz, c -> new CopyOnWriteArrayList<ModuleWrapper>());

        collected.add(module);
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "module")
    private class ModuleWrapper {
        @NonNull private final Object module;
        @NonNull private final ModuleProperties properties;
    }

}
