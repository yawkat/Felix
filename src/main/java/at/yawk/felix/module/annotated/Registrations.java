/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module.annotated;

import java.lang.annotation.*;

/**
 * Wrapper annotation for @Register so multiple registration rules can be specified.
 *
 * @author yawkat
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Registrations {
    Register[] value();
}
