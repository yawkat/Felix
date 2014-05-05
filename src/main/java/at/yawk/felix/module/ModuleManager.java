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
     * Returns whether a module of the given type exists or is currently being loaded.
     */
    public boolean hasOrLoading(@NonNull Class<?> clazz) {
        return allUnloaded(clazz).findAny().isPresent();
    }

    /**
     * Returns whether a module of the exact given type (no submodule) exists or is currently being loaded.
     */
    public boolean hasOrLoadingExact(@NonNull Class<?> clazz) {
        return allUnloaded(clazz).filter(wrapper -> wrapper.of == clazz).findAny().isPresent();
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
        return allUnloaded(clazz).filter(w -> w.valid).map(w -> (M) w.module.get());
    }

    @NonNull
    private Stream<ModuleWrapper> allUnloaded(@NonNull Class<?> clazz) {
        return modules.getOrDefault(clazz, Collections.emptySet()).stream();
    }

    /**
     * Returns all modules.
     */
    @NonNull
    public Stream<Object> all() {
        return all(Object.class);
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
    public void registerModule(Class<?> type, RegistrationProperties properties) {
        loadModule(type, properties, Optional.empty());
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
    public void registerModuleObject(@NonNull Object module, RegistrationProperties registrationProperties) {
        loadModule(module, registrationProperties, Optional.empty());
    }

    /**
     * Register a module with default settings and no dependencies. Primary use is creating modules from anonymous
     * classes. No duplicate detection is performed.
     */
    public void registerModuleAnonymous(@NonNull Object module) {
        loadModule(module, RegistrationProperties.anonymous, Optional.of(ModuleProperties.create()));
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
     * Load a module by class.
     *
     * @param knownProperties If not empty, these properties will be used instead of the found ones.
     */
    @Synchronized
    private void loadModule(Class<?> moduleClass,
                            RegistrationProperties properties,
                            Optional<ModuleProperties> knownProperties) {
        if (!properties.getDuplicateFinder().register(this, moduleClass)) { return; }

        ModuleWrapper wrapper = makeWrapper(moduleClass);
        try {
            // try to create a new instance
            Constructor<?> constructor = moduleClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object module = constructor.newInstance();

            wrapper.module = Optional.of(module);

            loadModule(wrapper, properties, knownProperties);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        } finally {
            finalizeWrapper(wrapper);
        }
    }

    /**
     * Load a module by object.
     *
     * @param knownProperties If not empty, these properties will be used instead of the found ones.
     */
    @Synchronized
    private void loadModule(Object module,
                            RegistrationProperties properties,
                            Optional<ModuleProperties> knownProperties) {
        if (!properties.getDuplicateFinder().register(this, module)) { return; }

        // reserve wrapper space
        ModuleWrapper wrapper = makeWrapper(module.getClass());
        try {
            wrapper.module = Optional.of(module);
            loadModule(wrapper, properties, knownProperties);
        } finally {
            finalizeWrapper(wrapper);
        }
    }

    /**
     * Actually load a module including dependencies.
     */
    private void loadModule(ModuleWrapper wrapper,
                            RegistrationProperties properties,
                            Optional<ModuleProperties> knownProperties) {
        // search properties
        ModuleProperties moduleProperties = knownProperties.orElseGet(() -> {
            // no known properties, search
            return findProperties(wrapper.module.get()).orElseThrow(() -> {
                // no properties found, not a module
                return new IllegalArgumentException(
                        "No module properties found. Either annotate your module with @AnnotatedModule or extend the " +
                        "Module class."
                );
            });
        });

        wrapper.properties = Optional.of(moduleProperties);

        // unregister excluded classes now that we know what they are
        unregisterExcluded(wrapper);

        // load dependencies (this may throw)
        wrapper.properties.get().getDependencies().forEach(clazz -> {
            properties.getDependencyResolutionStrategy().loadDependency(this, clazz, properties);
        });

        // dependencies loaded successfully, initialize
        initialize(wrapper.module.get());

        // no exceptions
        wrapper.valid = true;

        // load soft dependencies (this may throw)
        wrapper.properties.get().getSoftDependencies().forEach(clazz -> {
            properties.getDependencyResolutionStrategy().loadDependency(this, clazz, properties);
        });
    }

    private ModuleWrapper makeWrapper(Class<?> of) {
        ModuleWrapper wrapper = new ModuleWrapper(of);
        FelixUtil.getSuperClasses(of).forEach(on -> {
            // if no modules of the same class are registered yet we must create the list
            Collection<ModuleWrapper> collected =
                    modules.computeIfAbsent(on, c -> new CopyOnWriteArrayList<ModuleWrapper>());

            collected.add(wrapper);
        });
        return wrapper;
    }

    private void finalizeWrapper(ModuleWrapper wrapper) {
        // not loaded successfully
        if (!wrapper.valid) {
            FelixUtil.getSuperClasses(wrapper.of).forEach(on -> unregisterWrapper(wrapper, on));
        }
    }

    private void unregisterExcluded(ModuleWrapper wrapper) {
        wrapper.properties.get().getExcludedClasses().stream()
                // keep object so all() works
                .filter(c -> c != Object.class).forEach(on -> unregisterWrapper(wrapper, on));
    }

    private void unregisterWrapper(ModuleWrapper wrapper, Class<?> on) {
        // must have been registered before so we can assume the mapping exists
        modules.get(on).remove(wrapper);
    }

    private void initialize(Object module) {
        initializers.forEach(ini -> ini.initialize(this, module));
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = {"of", "module"})
    private class ModuleWrapper {
        /**
         * Base class
         */
        @NonNull private final Class<?> of;
        /**
         * Module, empty during registration
         */
        @NonNull private Optional<Object> module = Optional.empty();
        /**
         * Properties, empty during registration
         */
        @NonNull private Optional<ModuleProperties> properties = Optional.empty();
        /**
         * Only true after successful registration
         */
        @NonNull private boolean valid = false;
    }

}
