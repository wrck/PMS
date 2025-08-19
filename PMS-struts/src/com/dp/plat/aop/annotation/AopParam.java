/**
 * 
 */
package com.dp.plat.aop.annotation;

import java.lang.annotation.*;

/**
 * @author W02611
 *
 */
@Target({ ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AopParam {
    String value() default "";
}
