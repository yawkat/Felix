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
