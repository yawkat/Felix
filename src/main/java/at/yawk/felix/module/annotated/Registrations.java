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
