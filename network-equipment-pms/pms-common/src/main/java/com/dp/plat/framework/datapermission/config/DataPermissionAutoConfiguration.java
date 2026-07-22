package com.dp.plat.framework.datapermission.config;

import com.dp.plat.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import com.dp.plat.framework.datapermission.core.db.DataPermissionRuleHandler;
import com.dp.plat.framework.datapermission.core.rule.DataPermissionRule;
import com.dp.plat.framework.datapermission.core.rule.DataPermissionRuleFactory;
import com.dp.plat.framework.datapermission.core.rule.DataPermissionRuleFactoryImpl;
import com.dp.plat.framework.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 数据权限的自动配置
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 *
 * <p><b>调整说明</b>：原 yudao 使用 {@code @AutoConfiguration} + Spring Boot 自动装配
 * （通过 {@code spring.factories} / {@code AutoConfiguration.imports} 注册）。本仓库采用
 * 普通 {@code @Configuration}，由主应用的 {@code @SpringBootApplication(scanBasePackages = "com.dp.plat")}
 * 统一扫描，效果等价但更直观。
 *
 * <p>注意：仅当 Spring 容器中存在至少一个 {@link DataPermissionRule} Bean 时，框架才真正生效。
 * 例如：业务模块通过实现 {@code DeptDataPermissionRuleCustomizer} 注册部门数据权限规则后，
 * {@link DeptDataPermissionAutoConfiguration} 才会创建 {@code DeptDataPermissionRule} Bean。
 *
 * @author yudao
 */
@Configuration
public class DataPermissionAutoConfiguration {

    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DataPermissionRuleFactoryImpl(rules);
    }

    @Bean
    public DataPermissionRuleHandler dataPermissionRuleHandler(MybatisPlusInterceptor interceptor,
                                                               DataPermissionRuleFactory ruleFactory) {
        // 创建 DataPermissionRuleHandler，并包装为 MyBatis-Plus 的 DataPermissionInterceptor
        DataPermissionRuleHandler handler = new DataPermissionRuleHandler(ruleFactory);
        DataPermissionInterceptor inner = new DataPermissionInterceptor(handler);
        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面。这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return handler;
    }

    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }

}
