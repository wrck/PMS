package com.dp.plat.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注在 Controller 方法上，由
 * {@code com.dp.plat.system.aop.OperLogAspect} 切面统一记录操作日志。
 *
 * <p>放置于 pms-common 模块，便于所有业务模块（project/asset/implementation 等）
 * 共享使用，避免业务模块反向依赖 pms-system 模块。</p>
 *
 * <p>businessType 取值约定：1=新增，2=修改，3=删除，4=导出，5=导入，其他=查询。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 操作模块标题（如"用户管理"）。
     */
    String title() default "";

    /**
     * 业务类型：1=新增，2=修改，3=删除，4=导出，5=导入，其他=查询。
     */
    int businessType() default 0;

    /**
     * 是否保存请求参数。
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应结果。
     */
    boolean isSaveResponseData() default true;
}
