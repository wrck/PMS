package com.dp.plat.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解：基于 Bucket4j 令牌桶 + Redis 分布式存储实现。
 *
 * <p>标注在 Controller 方法上时，由 {@code RateLimitAspect} 切面拦截，
 * 根据限流 Key 在 Redis 中维护一个令牌桶，超出容量即拒绝请求并抛出
 * {@code BusinessException}（由全局异常处理器转换为 429 响应）。</p>
 *
 * <p>{@link #key()} 支持 SpEL 表达式（如 {@code "#request.projectId"}、
 * {@code "#userId"}、{@code "#id"} 等），可针对用户/资源维度精细化限流；
 * 留空时使用「方法签名 + 参数哈希」作为默认 Key。</p>
 *
 * <p>典型用法：</p>
 * <pre>
 * &#64;RateLimit(key = "#request.projectId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
 * public Result&lt;Project&gt; create(&#64;RequestBody ProjectCreateRequest request) { ... }
 *
 * &#64;RateLimit(capacity = 30)  // 全局默认：30/分钟
 * public Result&lt;?&gt; update(&#64;RequestBody Project project) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流 Key，支持 SpEL 表达式（如 {@code "#userId"}、{@code "#request.projectId"}）。
     * 留空时使用「方法全限定名 + 参数哈希」作为默认 Key。
     */
    String key() default "";

    /**
     * 令牌桶容量（最大突发请求数）。默认 100。
     */
    int capacity() default 100;

    /**
     * 每个补充周期内补充的令牌数。默认 100。
     */
    int refillTokens() default 100;

    /**
     * 令牌补充周期（秒）。默认 60 秒。
     */
    int refillPeriodSeconds() default 60;

    /**
     * 限流触发后返回给前端的提示消息。
     */
    String message() default "请求过于频繁，请稍后再试";
}
