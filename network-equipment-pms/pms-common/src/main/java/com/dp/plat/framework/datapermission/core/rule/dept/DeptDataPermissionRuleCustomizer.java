package com.dp.plat.framework.datapermission.core.rule.dept;

/**
 * {@link DeptDataPermissionRule} 的自定义配置接口
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 *
 * <p>业务模块通过实现该接口（注册为 Spring Bean），调用
 * {@link DeptDataPermissionRule#addDeptColumn(Class, String)} /
 * {@link DeptDataPermissionRule#addUserColumn(Class, String)} 配置需要过滤的表与列。
 *
 * @author yudao
 */
@FunctionalInterface
public interface DeptDataPermissionRuleCustomizer {

    /**
     * 自定义该权限规则
     * 1. 调用 {@link DeptDataPermissionRule#addDeptColumn(Class, String)} 方法，配置基于 dept_id 的过滤规则
     * 2. 调用 {@link DeptDataPermissionRule#addUserColumn(Class, String)} 方法，配置基于 user_id 的过滤规则
     *
     * @param rule 权限规则
     */
    void customize(DeptDataPermissionRule rule);

}
