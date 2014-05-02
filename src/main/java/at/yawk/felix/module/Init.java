package at.yawk.felix.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods in modules that are annotated with this will be run when their module is initialized.
 *
 * @author Jonas Konrad (yawkat)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
    public static int DEFAULT_PRIORITY = 0;

    /**
     * Methods with a lower priority (&lt;) will be initialized first.
     */
    int priority() default DEFAULT_PRIORITY;
}
