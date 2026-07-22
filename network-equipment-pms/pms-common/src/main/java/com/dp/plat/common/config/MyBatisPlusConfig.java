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
 * optional custom inner interceptors, and a {@link MetaObjectHandler} for auto-filling audit fields.
 *
 * <p><b>数据权限说明</b>：yudao 数据权限框架
 * （{@link com.dp.plat.framework.datapermission.config.DataPermissionAutoConfiguration}）
 * 在本 Bean 创建后，通过 {@link com.dp.plat.framework.mybatis.core.util.MyBatisUtils#addInterceptor}
 * 将 MyBatis-Plus 的 {@code DataPermissionInterceptor} 插入到链首（index=0），
 * 以确保数据权限过滤在分页之前生效（MyBatis-Plus 官方规定）。</p>
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
        // 注册容器中其它自定义 InnerInterceptor Bean（例如业务模块的租户隔离拦截器等）。
        // 注意：yudao 数据权限拦截器由 DataPermissionAutoConfiguration 单独插入到链首，
        // 不通过此 ObjectProvider 注册，避免与分页插件顺序冲突。
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

