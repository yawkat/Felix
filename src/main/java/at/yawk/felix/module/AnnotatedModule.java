package at.yawk.felix.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Module annotation.
 * <p>
 * Classes with this annotation (and their subclasses) can be used by ModuleManager.
 *
 * @deprecated Replaced with @Module, @Dependency and @Register
 * @author Jonas Konrad (yawkat)
 */
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotatedModule {
    /**
     * Dependencies of this module. Will be loaded before initialization (but not necessarily before module
     * construction!).
     */
    Class[] dependencies() default {};

    /**
     * Soft dependencies of this module. Will be loaded after initialization.
     */
    Class[] softDependencies() default {};

    /**
     * Classes that this module should <i>not</i> be registered as.
     */
    Class[] excludedFromRegistration() default {};
}
