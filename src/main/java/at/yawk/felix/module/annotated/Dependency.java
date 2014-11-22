package at.yawk.felix.module.annotated;

import java.lang.annotation.*;

/**
 * Specify a dependency for this module and its subclasses.
 *
 * @author yawkat
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Dependencies.class)
public @interface Dependency {
    /**
     * The dependency.
     */
    Class<?> value();

    /**
     * Whether this is a "soft" dependency: Soft dependencies are not automatically loaded, but will be loaded before
     * this module if it is a hard dependency of another module.
     */
    boolean soft() default false;
}
