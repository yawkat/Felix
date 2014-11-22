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
