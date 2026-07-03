package com.dp.plat.system.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for operation logging. Apply to controller methods to record
 * an operation log entry via {@link com.dp.plat.system.aop.OperLogAspect}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * Operation title (module name).
     */
    String title() default "";

    /**
     * Business type: 1=add, 2=update, 3=delete, 4=export, 5=import, other=query.
     */
    int businessType() default 0;

    /**
     * Whether to save the request parameters.
     */
    boolean isSaveRequestData() default true;

    /**
     * Whether to save the response result.
     */
    boolean isSaveResponseData() default true;
}
