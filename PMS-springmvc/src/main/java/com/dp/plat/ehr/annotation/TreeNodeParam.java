package com.dp.plat.ehr.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * treeNode的字段名称对应关系<br>
 * fields字段规则，treeNodeField:entityField<br>
 * 例如：id:id，text:title
 * @author w02611
 *
 */
@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
public @interface TreeNodeParam {

	/**
	 * treeNode的对应的属性名
	 * @return
	 */
	public String field() default "";
	/**
	 * treeNode的对应的属性名数组
	 * 
	 * @return
	 */
	public String[] fields() default {};
}