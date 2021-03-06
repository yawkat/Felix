/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import org.junit.Test;

/**
 * @author Jonas Konrad (yawkat)
 */
public class AnnotatedPropertyDiscoveryStrategyTest {
    @Test
    public void testSuccessful() {
        Optional<ModuleProperties> properties =
                AnnotatedPropertyDiscoveryStrategy.instance.findProperties(new CorrectModule());
        assertEquals(ImmutableSet.of(Integer.class, String.class), properties.get().getDependencies());
        assertEquals(ImmutableSet.of(Object.class), properties.get().getExcludedClasses());
    }

    @Test
    public void testFailed() {
        Optional<ModuleProperties> properties =
                AnnotatedPropertyDiscoveryStrategy.instance.findProperties(new IncorrectModule());
        assertFalse(properties.isPresent());
    }

    @AnnotatedModule(dependencies = {Integer.class, String.class}, excludedFromRegistration = Object.class)
    private class CorrectModule {}

    private class IncorrectModule {}
}
