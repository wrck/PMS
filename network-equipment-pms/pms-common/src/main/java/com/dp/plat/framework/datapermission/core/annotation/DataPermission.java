package com.dp.plat.framework.datapermission.core.annotation;

import com.dp.plat.framework.datapermission.core.rule.DataPermissionRule;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * <p>可声明在类或者方法上，标识使用的数据权限规则。即使不添加 {@code @DataPermission} 注解，
 * 默认是开启状态；可通过设置 {@code enable = false} 禁用。
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 *
 * @author yudao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 当前类或方法是否开启数据权限
     */
    boolean enable() default true;

    /**
     * 生效的数据权限规则数组，优先级高于 {@link #excludeRules()}
     */
    Class<? extends DataPermissionRule>[] includeRules() default {};

    /**
     * 排除的数据权限规则数组，优先级最低
     */
    Class<? extends DataPermissionRule>[] excludeRules() default {};

}
