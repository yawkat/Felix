package at.yawk.felix.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import org.junit.Test;

public class ModuleClassDiscoveryStrategyTest {
    @Test
    public void testSuccessful() {
        Optional<ModuleProperties> properties =
                ModuleClassDiscoveryStrategy.instance.findProperties(new CorrectModule());
        assertTrue(properties.isPresent());
    }

    @Test
    public void testFailed() {
        Optional<ModuleProperties> properties =
                ModuleClassDiscoveryStrategy.instance.findProperties(new IncorrectModule());
        assertFalse(properties.isPresent());
    }

    private class CorrectModule extends Module {}

    private class IncorrectModule {}
}
