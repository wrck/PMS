package com.dp.plat.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking a Mapper method for data-scope filtering.
 *
 * <p>When present, the {@code DataPermissionInterceptor} appends a SQL filter
 * that restricts the result set to rows the current user is allowed to see
 * (by default, rows they created themselves). Administrators bypass filtering.</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * Alias for the department column (e.g. {@code d} for {@code sys_dept d}).
     * Reserved for future dept-based filtering.
     */
    String deptAlias() default "";

    /**
     * Alias for the user column (e.g. {@code u} for {@code sys_user u}).
     * Reserved for future user-based filtering.
     */
    String userAlias() default "";
}
