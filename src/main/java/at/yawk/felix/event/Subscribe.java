/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark an event handler method. Methods annotated with this must have exactly one argument of the
 * event type they wish to subscribe to.
 *
 * @author Jonas Konrad (yawkat)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    /**
     * Methods with a lower priority (&lt;) will be called first.
     */
    int priority() default EventHandler.DEFAULT_PRIORITY;

    /**
     * Whether concurrent calls are allowed.
     */
    boolean parallel() default true;
}
