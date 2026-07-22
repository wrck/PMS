package com.dp.plat.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.dp.plat.framework.mybatis.core.handler.DefaultDBFieldHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus configuration: pagination interceptor, optimistic lock interceptor,
 * optional custom inner interceptors (e.g. data permission), and a
 * {@link MetaObjectHandler} for auto-filling audit fields.
 *
 * <p><b>MetaObjectHandler</b>：复用 yudao {@link DefaultDBFieldHandler}，对
 * {@link com.dp.plat.framework.mybatis.core.dataobject.BaseDO} 实体填充
 * creator/updater（当前登录用户编号），同时兼容历史
 * {@link com.dp.plat.common.entity.BaseEntity} 的 createBy/updateBy/deleted 字段。
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

    /**
     * 复用 yudao {@link DefaultDBFieldHandler} 作为审计字段自动填充处理器。
     *
     * <p>同时处理 {@code BaseDO}（creator/updater/deleted:Boolean）与历史
     * {@code BaseEntity}（createBy/updateBy/deleted:Integer）。
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultDBFieldHandler();
    }
}

