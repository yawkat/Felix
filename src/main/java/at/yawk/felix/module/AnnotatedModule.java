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
 * @author Jonas Konrad (yawkat)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotatedModule {
    Class[] dependencies() default {};

    Class[] excludedFromRegistration() default {};
}
