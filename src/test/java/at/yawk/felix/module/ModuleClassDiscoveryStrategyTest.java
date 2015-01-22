/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
