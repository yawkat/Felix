Felix, a java 8 modularization framework
========================================

*Because I am bad at naming my projects.*

Felix is a framework that allows easy and fast modularized coding in java 8.

Modules
-------

### Features

- A flexible annotation- or class-driven framework
- Anonymous modules
- Possibility of adding custom module parsers (resource-driven etc)
- Multiple modules of the same type including java 8 parallel action execution
- Easy providers
- Easy initialization of modules

### Usage

#### Annotations

```Java
@AnnotatedModule(dependencies = { OtherModule1.class, OtherModule2.class })
class MyModule {}
```

#### Extending

```Java
class MyModule extends Module {
    @Override
    protected void listDependencies() {
        require(OtherModule1.class);
        require(OtherModule2.class);
    }
}
```

#### Anonymous

```Java
ModuleManager mm = ModuleManager.create();
mm.registerModuleAnonymous(new MyProvider() {
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }
});
```

#### Initialization

```Java
@AnnotatedModule
class MyModule {
    @Init
    public void doWhatever() {}

    @Init
    public void doWhatever(ModuleManager mm) {}
}
```

```Java
@AnnotatedModule
class MyModule implements InitializableModule {
    @Subscribe
    public void initialize(ModuleManager mm) {}
}
```

Both work for the Module class as well.

#### Accessing other modules

```Java
ModuleManager mm = ModuleManager.create();
mm.get(MyModule.class).doSomething();
```

```Java
ModuleManager mm = ModuleManager.create();
mm.forEach(MyModule.class, MyModule::doSomething);
mm.forEach(MyModule.class, module -> module.doSomething());
mm.forEach(MyModule.class, module -> {
    module.doSomething();
});
```

`forEach` is preferred over `get` as it allows for multiple modules. It also utilizes work stealing when run inside a
 ForkJoinPool.

#### Registering new modules

```Java
ModuleManager mm = ModuleManager.create();
mm.registerModuleObject(new MyModule());
// needs an empty constructor
mm.registerModule(MyModule.class);
```

#### Providers

All registered modules will also be registered for their interfaces. This means that you can simply call
`moduleManager.get(MyProviderInterface.class)` and it will return any registered module that implements
`MyProviderInterface`.

If you want to register a module but don't want to register it for one or more of its interfaces,
you can use the `excludedFromRegistration` property of the `AnnotatedModule` annotation when declaring it.

Events
------

Note that the event framework is completely independent from the module framework though they work well together.

### Features

- Flexible annotation-driven event handlers
- Allows for customized discovery of event handlers
- Compatibility with the google guava @Subscribe annotation
- Proper handling of overridden event handler methods
- Event handler priorities
- Parallel event execution when wanted (does not honour priority)
- Customizable exception handlers

### Usage

```Java
class MyHandler {
    @Subscribe
    public void moo(MyEvent event) {
        System.out.println(event.getMessage())
    }
}

EventBus bus = EventBus.create();
bus.subscribe(new MyHandler());
bus.post(new MyEvent("Hello World!"));
```

```Java
EventBus bus = EventBus.create();
bus.subscribe(MyEvent.class, event -> System.out.println(event.getMessage()));
bus.post(new MyEvent("Hello World!"));
```
