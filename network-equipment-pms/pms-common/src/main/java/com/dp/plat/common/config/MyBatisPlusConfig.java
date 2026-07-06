package com.dp.plat.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.dp.plat.common.util.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus configuration: pagination interceptor, optimistic lock interceptor,
 * optional custom inner interceptors (e.g. data permission), and a
 * {@link MetaObjectHandler} for auto-filling audit fields.
 *
 * <p><b>慢 SQL 监控注册说明</b>：{@link com.dp.plat.common.mybatis.SlowSqlInterceptor}
 * 为 MyBatis 原生 {@code org.apache.ibatis.plugin.Interceptor}（非 MyBatis-Plus 的
 * {@link InnerInterceptor}），<b>不能</b>通过 {@link MybatisPlusInterceptor#addInnerInterceptor(InnerInterceptor)}
 * 注册。其标注了 {@code @Component}，会被 MyBatis Spring 自动发现并注册为独立插件，
 * 与下方的 {@code mybatisPlusInterceptor} 平行生效，无需在此处手动添加。</p>
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            ObjectProvider<InnerInterceptor> innerInterceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // Add any custom InnerInterceptor beans discovered in the context
        // (e.g. DataPermissionInterceptor from pms-system). Ordering: data permission
        // is applied last so it operates on the original SQL before pagination wraps it.
        innerInterceptors.orderedStream().forEach(interceptor::addInnerInterceptor);
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                String currentUser = SecurityUtils.getCurrentUsername();
                LocalDateTime now = LocalDateTime.now();
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
                this.strictInsertFill(metaObject, "updateBy", String.class, currentUser);
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                String currentUser = SecurityUtils.getCurrentUsername();
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictUpdateFill(metaObject, "updateBy", String.class, currentUser);
            }
        };
    }
}
