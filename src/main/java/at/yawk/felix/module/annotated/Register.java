/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module.annotated;

import java.lang.annotation.*;

/**
 * Specify whether a module should be registered as a given class.
 *
 * @author yawkat
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Registrations.class)
public @interface Register {
    /**
     * The class this module should / should not be registered as. Must be a superclass of the annotated class or the
     * annotated class itself.
     */
    Class<?> as();

    /**
     * Whether the module should be registered as this class.
     */
    boolean register();
}
