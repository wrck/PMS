package com.dp.plat.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口幂等性注解：基于 Redis SETNX（{@code SET key value NX EX ttl}）实现。
 *
 * <p>标注在 Controller 方法上时，由 {@code IdempotentAspect} 切面拦截，
 * 在业务方法执行前以「幂等键 + 处理中标记」抢占 Redis 锁：
 * <ul>
 *   <li>第一次请求：抢占成功 → 执行业务方法；执行完成后按策略保留/更新键值</li>
 *   <li>重复请求：抢占失败 → 按 {@link Policy} 策略拒绝或返回首次结果</li>
 *   <li>业务异常：删除幂等键，允许客户端重试</li>
 * </ul>
 *
 * <p>{@link #key()} 支持 SpEL 表达式（可引用 {@code #request} 即
 * {@code HttpServletRequest}，以及方法参数）。留空时切面自动从当前请求的
 * {@code X-Idempotent-Key} 请求头读取，配合前端拦截器自动生成 UUID 即可实现透明幂等。</p>
 *
 * <p>典型用法：</p>
 * <pre>
 * // 1. 默认：从请求头 X-Idempotent-Key 读取，重复提交返回 409
 * &#64;Idempotent
 * public Result&lt;Project&gt; create(&#64;RequestBody Project project) { ... }
 *
 * // 2. 自定义 TTL + 返回首次结果策略（适用于查询型副作用接口）
 * &#64;Idempotent(ttl = 120, policy = Idempotent.Policy.RETURN_FIRST_RESULT)
 * public Result&lt;?&gt; expensiveAction(&#64;RequestBody ActionRequest request) { ... }
 *
 * // 3. SpEL 表达式：按业务字段拼接幂等键
 * &#64;Idempotent(key = "#request.projectId + ':' + #request.action")
 * public Result&lt;?&gt; act(&#64;RequestBody ActionRequest request) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等键，支持 SpEL 表达式。
     *
     * <p>留空（默认）时切面自动从当前 HTTP 请求的 {@code X-Idempotent-Key}
     * 请求头读取。SpEL 上下文中可用变量：</p>
     * <ul>
     *   <li>{@code #request}：当前 {@link jakarta.servlet.http.HttpServletRequest}</li>
     *   <li>方法参数名（依赖 {@code -parameters} 编译选项）</li>
     * </ul>
     */
    String key() default "";

    /**
     * 幂等键 TTL（秒）。默认 60 秒。
     *
     * <p>TTL 应略大于业务方法的最大执行时间，保证在首次请求尚未完成时
     * 重复请求能被正确拦截；同时避免 Redis 中残留过多过期键。</p>
     */
    int ttl() default 60;

    /**
     * 重复请求冲突策略。默认 {@link Policy#REJECT}。
     */
    Policy policy() default Policy.REJECT;

    /**
     * 重复请求触发时返回给前端的提示消息（仅 {@link Policy#REJECT} 策略生效）。
     */
    String message() default "请勿重复提交";

    /**
     * 重复请求冲突策略。
     */
    enum Policy {
        /**
         * 拒绝：抛出 {@code BusinessException}（code=409），由全局异常处理器返回 409 响应。
         *
         * <p>适用于创建/更新类接口，重复提交应明确告知用户失败。</p>
         */
        REJECT,

        /**
         * 返回首次结果：将首次执行结果序列化为 JSON 存入 Redis，重复请求直接反序列化返回。
         *
         * <p>适用于「副作用接口 + 客户端需要拿到首次返回值」的场景，
         * 例如发起支付、生成订单等。注意：返回值必须可被 Jackson 序列化。</p>
         */
        RETURN_FIRST_RESULT
    }
}
