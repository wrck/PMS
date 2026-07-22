package com.dp.plat.common.aspect;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.dp.plat.common.annotation.Idempotent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * 幂等性切面：基于 Redis SETNX（{@code SET key value NX EX ttl}）实现接口幂等保护。
 *
 * <p>拦截所有标注 {@link Idempotent} 的方法，执行流程：</p>
 * <ol>
 *   <li>解析幂等键：{@code key()} 留空 → 从 {@code X-Idempotent-Key} 请求头读取；
 *       非空 → 作为 SpEL 表达式解析（上下文含 {@code #request} 和方法参数）</li>
 *   <li>若解析结果为空（客户端未携带幂等键）：跳过幂等校验，直接执行业务方法
 *       <strong>（兼容非浏览器客户端，但会丧失幂等保护）</strong></li>
 *   <li>使用 {@code setIfAbsent(key, "PROCESSING", Duration.ofSeconds(ttl))} 抢占 Redis 锁：
 *     <ul>
 *       <li>抢占成功 → 执行业务方法；成功后按策略更新 Redis 值，异常时删除键允许重试</li>
 *       <li>抢占失败 → 命中幂等，按 {@link Idempotent.Policy} 处理：
 *         <ul>
 *           <li>{@code REJECT}：抛出 {@link ServiceException}（code=409）</li>
 *           <li>{@code RETURN_FIRST_RESULT}：从 Redis 读取首次结果 JSON 反序列化返回</li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>使用 {@link StringRedisTemplate}，键值均为字符串，便于运维排查</li>
 *   <li>"PROCESSING" 标记位区分「处理中」与「已完成」，避免在首次请求未完成时
 *       误返回空结果（{@code RETURN_FIRST_RESULT} 策略下若读到 PROCESSING 仍按 REJECT 处理）</li>
 *   <li>业务异常时主动 {@code delete} 释放键，允许客户端重试</li>
 *   <li>构造器注入，便于单元测试 Mock</li>
 * </ul>
 */
@Slf4j
@Aspect
public class IdempotentAspect {

    /** Redis Key 前缀，便于运维检索与统计。 */
    private static final String KEY_PREFIX = "idempotent:";

    /** 处理中标记值，写入 Redis 表示业务方法正在执行（尚未产生结果）。 */
    private static final String PROCESSING = "PROCESSING";

    /** 默认请求头名称。 */
    public static final String HEADER_IDEMPOTENT_KEY = "X-Idempotent-Key";

    /** SpEL 表达式解析器（线程安全）。 */
    private final ExpressionParser parser = new SpelExpressionParser();

    /** 方法参数名发现器。 */
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /** Redis 字符串模板。 */
    private final StringRedisTemplate redisTemplate;

    /** Jackson 序列化器（用于 RETURN_FIRST_RESULT 策略缓存返回值）。 */
    private final ObjectMapper objectMapper;

    /**
     * 构造器注入。
     *
     * @param redisTemplate Redis 字符串模板
     * @param objectMapper  Jackson 序列化器
     */
    public IdempotentAspect(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 环绕通知：拦截 {@link Idempotent} 注解方法，执行幂等校验。
     *
     * @param joinPoint  AOP 连接点
     * @param idempotent 幂等注解
     * @return 原方法返回值（或 RETURN_FIRST_RESULT 策略下的缓存结果）
     * @throws Throwable 原方法抛出的异常，或幂等冲突触发的 {@link ServiceException}
     */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 1. 解析幂等键
        String idempotentKey = resolveKey(idempotent, joinPoint);
        if (!StringUtils.hasText(idempotentKey)) {
            // 客户端未提供幂等键：跳过幂等保护，直接执行（兼容性优先）
            if (log.isDebugEnabled()) {
                log.debug("幂等键为空，跳过幂等校验: method={}", joinPoint.getSignature().toShortString());
            }
            return joinPoint.proceed();
        }

        String redisKey = KEY_PREFIX + idempotentKey;
        Duration ttl = Duration.ofSeconds(idempotent.ttl());

        // 2. SETNX 抢占：成功=true（首次请求），失败=false（重复请求）
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, PROCESSING, ttl);

        if (Boolean.FALSE.equals(acquired)) {
            // 3. 命中幂等：根据策略处理重复请求
            return handleDuplicateRequest(joinPoint, idempotent, redisKey);
        }

        // 4. 抢占成功，执行业务方法
        try {
            Object result = joinPoint.proceed();
            // 5. 业务方法执行成功：按策略更新 Redis 值
            if (idempotent.policy() == Idempotent.Policy.RETURN_FIRST_RESULT && result != null) {
                try {
                    String json = objectMapper.writeValueAsString(result);
                    // 覆盖写入首次结果（保持原 TTL，便于后续重复请求复用）
                    redisTemplate.opsForValue().set(redisKey, json, ttl);
                } catch (Exception ex) {
                    // 序列化失败不阻断主流程，但记录日志（此时后续重复请求将走 REJECT 分支）
                    log.warn("幂等首次结果序列化失败，后续重复请求将走 REJECT 策略: key={}, err={}",
                            redisKey, ex.getMessage());
                }
            }
            // REJECT 策略：保持 PROCESSING 标记不变（防止 TTL 窗口内重复提交）
            return result;
        } catch (Throwable ex) {
            // 6. 业务方法异常：删除幂等键，允许客户端重试
            try {
                redisTemplate.delete(redisKey);
            } catch (Exception delEx) {
                log.warn("删除幂等键失败（业务异常后）: key={}, err={}", redisKey, delEx.getMessage());
            }
            throw ex;
        }
    }

    /**
     * 处理重复请求：根据 {@link Idempotent.Policy} 决定拒绝或返回首次结果。
     *
     * @param joinPoint  AOP 连接点（用于获取返回类型以反序列化）
     * @param idempotent 幂等注解
     * @param redisKey   Redis 完整键
     * @return RETURN_FIRST_RESULT 策略下返回首次结果；REJECT 策略下不会返回（抛异常）
     * @throws ServiceException REJECT 策略或 RETURN_FIRST_RESULT 无法返回时抛出
     */
    private Object handleDuplicateRequest(ProceedingJoinPoint joinPoint, Idempotent idempotent,
                                          String redisKey) throws ServiceException {
        String existing = redisTemplate.opsForValue().get(redisKey);

        // RETURN_FIRST_RESULT 策略：仅当 Redis 中已有「首次结果」（非 PROCESSING）时复用
        if (idempotent.policy() == Idempotent.Policy.RETURN_FIRST_RESULT
                && StringUtils.hasText(existing) && !PROCESSING.equals(existing)) {
            log.info("幂等命中（RETURN_FIRST_RESULT），返回首次结果: key={}", redisKey);
            try {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Class<?> returnType = signature.getReturnType();
                return objectMapper.readValue(existing, returnType);
            } catch (Exception ex) {
                // 反序列化失败（如返回类型变更）：降级为 REJECT 行为
                log.warn("幂等首次结果反序列化失败，降级为 REJECT: key={}, err={}",
                        redisKey, ex.getMessage());
            }
        }

        // 默认/降级：REJECT 策略
        log.info("幂等命中（REJECT），拒绝重复请求: key={}, policy={}",
                redisKey, idempotent.policy());
        throw new ServiceException(409, idempotent.message());
    }

    /**
     * 解析幂等键：
     * <ul>
     *   <li>{@code key()} 留空 → 从当前 HTTP 请求的 {@code X-Idempotent-Key} 头读取</li>
     *   <li>{@code key()} 非空 → 作为 SpEL 表达式解析，上下文含：
     *     <ul>
     *       <li>{@code #request}：当前 {@link HttpServletRequest}（可调用 {@code getHeader}）</li>
     *       <li>方法参数（按参数名引用，如 {@code #projectId}）</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param idempotent 幂等注解
     * @param joinPoint  AOP 连接点
     * @return 解析后的幂等键，可能为 null/空字符串
     */
    private String resolveKey(Idempotent idempotent, ProceedingJoinPoint joinPoint) {
        String spel = idempotent.key();
        if (StringUtils.hasText(spel)) {
            // SpEL 表达式解析
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                    joinPoint.getTarget(), method, joinPoint.getArgs(), nameDiscoverer);
            // 注入 HttpServletRequest 到 SpEL 上下文
            HttpServletRequest request = currentRequest();
            if (request != null) {
                context.setVariable("request", request);
            }
            try {
                Expression expression = parser.parseExpression(spel);
                Object value = expression.getValue(context);
                return value == null ? null : value.toString();
            } catch (Exception ex) {
                log.warn("幂等 SpEL 解析失败，降级为请求头读取: spel={}, err={}", spel, ex.getMessage());
                // 降级：从请求头读取
                return readFromHeader();
            }
        }
        // 默认：从请求头读取
        return readFromHeader();
    }

    /**
     * 从当前 HTTP 请求的 {@code X-Idempotent-Key} 头读取幂等键。
     *
     * @return 幂等键，无请求上下文或无该头时返回 null
     */
    private String readFromHeader() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(HEADER_IDEMPOTENT_KEY);
    }

    /**
     * 从 {@link RequestContextHolder} 获取当前 HTTP 请求（可能为 null，如异步任务）。
     *
     * @return 当前 {@link HttpServletRequest}，无则 null
     */
    private HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest();
        }
        return null;
    }
}
