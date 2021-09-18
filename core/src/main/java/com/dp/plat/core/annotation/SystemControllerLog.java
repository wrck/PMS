package com.dp.plat.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 拦截Controller
 */

@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemControllerLog {
	/**
	 * 描述
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * 需要忽略的参数
	 * 
	 * @return
	 */
	String[] ignoreParams() default {};

	/**
	 * 对描述处理的开始和结束标记
	 * 
	 * @return
	 */
	String[] splitFlags() default {};
}
