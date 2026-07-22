package com.dp.plat.framework.datapermission.config;

import com.dp.plat.framework.common.biz.system.permission.PermissionCommonApi;
import com.dp.plat.framework.datapermission.core.rule.dept.DeptDataPermissionRule;
import com.dp.plat.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.dp.plat.framework.security.core.LoginUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 基于部门的数据权限配置
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 *
 * <p>仅当业务模块注册了至少一个 {@link DeptDataPermissionRuleCustomizer} Bean 时才会装配，
 * 自动创建 {@link DeptDataPermissionRule} 并应用所有 customizer 配置（addDeptColumn/addUserColumn）。
 * 同时需要业务模块实现 {@link PermissionCommonApi}（由 pms-system 在 B5 提供）。
 *
 * @author yudao
 */
@Configuration
@ConditionalOnClass(LoginUser.class)
@ConditionalOnBean(value = {DeptDataPermissionRuleCustomizer.class})
public class DeptDataPermissionAutoConfiguration {

    @Bean
    public DeptDataPermissionRule deptDataPermissionRule(PermissionCommonApi permissionApi,
                                                         List<DeptDataPermissionRuleCustomizer> customizers) {
        // 创建 DeptDataPermissionRule 对象
        DeptDataPermissionRule rule = new DeptDataPermissionRule(permissionApi);
        // 补全表配置
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }

}
