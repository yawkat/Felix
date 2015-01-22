/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module.annotated;

import java.lang.annotation.*;

/**
 * Marker annotation for modules. Must be present on at least one superclass of a module or the module itself.
 *
 * @author yawkat
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {}
