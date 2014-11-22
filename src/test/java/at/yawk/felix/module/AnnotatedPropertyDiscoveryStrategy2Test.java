package at.yawk.felix.module;

import at.yawk.felix.module.annotated.Dependency;
import at.yawk.felix.module.annotated.Module;
import at.yawk.felix.module.annotated.Register;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Jonas Konrad (yawkat)
 */
public class AnnotatedPropertyDiscoveryStrategy2Test {
    @Test
    public void testSuccessful() {
        Optional<ModuleProperties> properties =
                AnnotatedPropertyDiscoveryStrategy2.instance.findProperties(new CorrectModule());
        assertEquals(ImmutableSet.of(Integer.class, String.class), properties.get().getDependencies());
        assertEquals(ImmutableSet.of(Object.class), properties.get().getExcludedClasses());
    }

    @Test
    public void testFailed() {
        Optional<ModuleProperties> properties =
                AnnotatedPropertyDiscoveryStrategy2.instance.findProperties(new IncorrectModule());
        assertFalse(properties.isPresent());
    }

    @Module
    @Dependency(Integer.class)
    @Dependency(String.class)
    @Register(as = Object.class, register = false)
    private class CorrectModule {}

    private class IncorrectModule {}
}
