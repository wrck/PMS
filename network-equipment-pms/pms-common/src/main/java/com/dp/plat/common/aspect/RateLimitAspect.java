package com.dp.plat.common.aspect;

import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.exception.RateLimitExceededException;
import com.dp.plat.common.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * 限流切面：基于 Bucket4j 令牌桶 + Redis 分布式存储。
 *
 * <p>拦截所有标注 {@link RateLimit} 的方法，根据限流 Key（支持 SpEL）
 * 在 Redis 中维护一个分布式令牌桶，超限则抛出 {@link RateLimitExceededException}
 * （携带 {@code ResultCode.TOO_MANY_REQUESTS}），由 {@code GlobalExceptionHandler}
 * 统一转换为 HTTP 429 + {@code Retry-After} 响应。</p>
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>使用 {@link ProxyManager} 实现真正的分布式令牌桶（非本地桶）</li>
 *   <li>Key 支持 SpEL：可引用方法参数（{@code #request.projectId}）、内置变量 {@code #userId}</li>
 *   <li>默认 Key = 「类名.方法名:userId:参数哈希」，保证按用户隔离且可识别重复提交</li>
 *   <li>构造器注入 {@link ProxyManager}，便于单元测试 Mock</li>
 * </ul>
 */
@Slf4j
@Aspect
public class RateLimitAspect {

    /** Redis Key 前缀，便于运维检索与统计。 */
    private static final String KEY_PREFIX = "rate_limit:";

    /** SpEL 表达式解析器（线程安全）。 */
    private final ExpressionParser parser = new SpelExpressionParser();

    /** 方法参数名发现器。 */
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /** Bucket4j 分布式代理管理器（Redis 后端）。 */
    private final ProxyManager<byte[]> proxyManager;

    /**
     * 构造器注入 {@link ProxyManager}。
     *
     * @param proxyManager Bucket4j 分布式代理管理器
     */
    public RateLimitAspect(ProxyManager<byte[]> proxyManager) {
        this.proxyManager = proxyManager;
    }

    /**
     * 环绕通知：拦截 {@link RateLimit} 注解方法，执行令牌桶消费。
     *
     * @param joinPoint  AOP 连接点
     * @param rateLimit  限流注解
     * @return 原方法返回值
     * @throws Throwable 原方法抛出的异常，或限流触发的 {@link RateLimitExceededException}
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 1. 构建令牌桶配置
        BucketConfiguration configuration = buildConfiguration(rateLimit);

        // 2. 解析限流 Key（SpEL 或默认）
        String resolvedKey = resolveKey(joinPoint, rateLimit);
        byte[] redisKey = (KEY_PREFIX + resolvedKey).getBytes(StandardCharsets.UTF_8);

        // 3. 获取/创建分布式令牌桶
        BucketProxy bucket = proxyManager.builder().build(redisKey, () -> configuration);

        // 4. 尝试消费 1 个令牌
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // 消费成功，放行
            if (log.isDebugEnabled()) {
                log.debug("限流放行: key={}, remaining={}", resolvedKey, probe.getRemainingTokens());
            }
            return joinPoint.proceed();
        }

        // 5. 限流触发：计算 Retry-After 秒数并抛出限流异常
        long retryAfterSeconds = computeRetryAfterSeconds(probe);
        log.warn("限流触发: key={}, remainingTokens={}, retryAfter={}s",
                resolvedKey, probe.getRemainingTokens(), retryAfterSeconds);
        throw new RateLimitExceededException(rateLimit.message(), retryAfterSeconds);
    }

    /**
     * 根据注解参数构建 {@link BucketConfiguration}：
     * 容量 = {@code capacity}，按 {@code refillTokens}/{@code refillPeriodSeconds} 周期补充。
     */
    private BucketConfiguration buildConfiguration(RateLimit rateLimit) {
        if (rateLimit.capacity() <= 0 || rateLimit.refillTokens() <= 0
                || rateLimit.refillPeriodSeconds() <= 0) {
            throw new IllegalArgumentException("RateLimit 参数必须为正数: capacity="
                    + rateLimit.capacity() + ", refillTokens=" + rateLimit.refillTokens()
                    + ", refillPeriodSeconds=" + rateLimit.refillPeriodSeconds());
        }
        Bandwidth limit = Bandwidth.classic(
                rateLimit.capacity(),
                Refill.intervally(rateLimit.refillTokens(),
                        Duration.ofSeconds(rateLimit.refillPeriodSeconds())));
        return BucketConfiguration.builder().addLimit(limit).build();
    }

    /**
     * 解析限流 Key：
     * <ul>
     *   <li>注解 {@code key()} 非空 → 解析 SpEL 表达式</li>
     *   <li>否则 → 默认 Key = 「类名.方法名:userId:参数哈希」</li>
     * </ul>
     */
    private String resolveKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodSignature = method.getDeclaringClass().getSimpleName()
                + "." + method.getName();

        String spelKey = rateLimit.key();
        if (StringUtils.hasText(spelKey)) {
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                    joinPoint.getTarget(), method, joinPoint.getArgs(), nameDiscoverer);
            // 注入当前用户 ID 到 SpEL 上下文，便于使用 #userId
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId != null) {
                context.setVariable("userId", currentUserId);
            }
            String username = SecurityUtils.getCurrentUsername();
            if (username != null) {
                context.setVariable("username", username);
            }
            Expression expression = parser.parseExpression(spelKey);
            Object value = expression.getValue(context);
            String evaluatedKey = Optional.ofNullable(value).map(Object::toString).orElse("null");
            return methodSignature + ":" + evaluatedKey;
        }

        // 默认 Key：方法签名 + 当前用户 + 参数哈希（按用户隔离 + 防重复提交）
        String userId = Optional.ofNullable(SecurityUtils.getCurrentUserId())
                .map(String::valueOf).orElse("anonymous");
        int argsHash = Arrays.deepHashCode(joinPoint.getArgs());
        return methodSignature + ":" + userId + ":" + Integer.toHexString(argsHash);
    }

    /**
     * 根据 {@link ConsumptionProbe} 计算建议的 Retry-After 秒数（向上取整，最小 1 秒）。
     */
    private long computeRetryAfterSeconds(ConsumptionProbe probe) {
        long nanosToWait = probe.getNanosToWaitForRefill();
        if (nanosToWait <= 0) {
            return 1L;
        }
        return Math.max(1L, (long) Math.ceil(nanosToWait / 1_000_000_000.0));
    }
}
