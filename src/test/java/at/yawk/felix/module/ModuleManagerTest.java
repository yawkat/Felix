package at.yawk.felix.module;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class ModuleManagerTest {
    @Test
    public void testMissing1() throws Exception {
        ModuleManager moduleManager = ModuleManager.create();
        assertFalse(moduleManager.has(CorrectModule.class));
        assertFalse(moduleManager.optional(CorrectModule.class).isPresent());
        assertEquals(0, moduleManager.all(CorrectModule.class).count());
    }

    @Test(expected = IllegalStateException.class)
    public void testMissing2() throws Exception {
        ModuleManager moduleManager = ModuleManager.create();
        moduleManager.get(CorrectModule.class);
    }

    @Test
    public void testRegisterObject() throws Exception {
        ModuleManager moduleManager = ModuleManager.create();
        assertFalse(moduleManager.has(CorrectModule.class));

        CorrectModule module = new CorrectModule();
        moduleManager.registerModuleObject(module);

        assertTrue(moduleManager.has(CorrectModule.class));
        assertEquals(module, moduleManager.get(CorrectModule.class));
    }

    @Test
    public void testRegisterClass() throws Exception {
        ModuleManager moduleManager = ModuleManager.create();
        assertFalse(moduleManager.has(CorrectModule.class));

        moduleManager.registerModule(CorrectModule.class);

        assertTrue(moduleManager.has(CorrectModule.class));
    }

    @Test
    public void testRegisterClassDuplicate() throws Exception {
        ModuleManager moduleManager = ModuleManager.create();
        assertFalse(moduleManager.has(CorrectModule.class));

        moduleManager.registerModule(CorrectModule.class);
        assertTrue(moduleManager.has(CorrectModule.class));
        moduleManager.registerModule(CorrectModule.class);

        assertEquals(1, moduleManager.all(CorrectModule.class).count());
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterObjectDuplicate() throws Exception {
        ModuleManager moduleManager = ModuleManager.create();
        assertFalse(moduleManager.has(CorrectModule.class));

        moduleManager.registerModuleObject(new CorrectModule());
        assertTrue(moduleManager.has(CorrectModule.class));
        moduleManager.registerModuleObject(new CorrectModule());
    }

    @Test
    public void testInitializeInterface() {
        ModuleManager moduleManager = ModuleManager.create();
        CorrectModuleInit module = new CorrectModuleInit();
        moduleManager.registerModuleObject(module);
        assertTrue(module.called);
    }

    @Test
    public void testInitializeAnnotation1() {
        ModuleManager moduleManager = ModuleManager.create();
        AtomicBoolean called = new AtomicBoolean(false);
        moduleManager.registerModuleObject(new CorrectModule() {
            @Init
            public void initialize(ModuleManager manager) {
                called.set(true);
            }
        });
        assertTrue(called.get());
    }

    @Test
    public void testInitializeAnnotation2() {
        ModuleManager moduleManager = ModuleManager.create();
        AtomicBoolean called = new AtomicBoolean(false);
        moduleManager.registerModuleObject(new CorrectModule() {
            @Init
            public void initialize() {
                called.set(true);
            }
        });
        assertTrue(called.get());
    }

    @AnnotatedModule
    private static class CorrectModule {}

    private static class CorrectModuleInit extends CorrectModule implements InitializableModule {
        boolean called = false;

        @Override
        public void initialize(ModuleManager manager) {
            called = true;
        }
    }
}
