package com.dp.plat.common.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 敏感端点 IP 维度限流过滤器（本地令牌桶）。
 *
 * <p>针对登录、验证码等高风险端点，按客户端 IP 维度执行严格限流，
 * 防止暴力破解、撞库、验证码刷量等攻击。</p>
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>使用 Bucket4j 本地令牌桶（{@link ConcurrentHashMap} 按 IP 隔离），
 *       适用于单实例部署；多实例需改用 Redis 分布式桶（见 {@code RateLimitAspect}）</li>
 *   <li>默认限流策略：10 次/分钟/IP（可配置）</li>
 *   <li>超限直接返回 HTTP 429 + {@code Retry-After} 响应头（JSON 错误体）</li>
 *   <li>使用 {@link OncePerRequestFilter} 保证单次请求只过滤一次</li>
 *   <li>IP 提取顺序：X-Forwarded-For → X-Real-IP → Proxy-Client-IP → RemoteAddr</li>
 * </ul>
 *
 * <p>注意：Servlet 6.0 不再提供 {@code HttpServletResponse.SC_TOO_MANY_REQUESTS} 常量，
 * 本类直接使用整数 {@code 429} 设置状态码。</p>
 */
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    /** HTTP 429 状态码（Servlet 6.0 无 SC_TOO_MANY_REQUESTS 常量，直接使用整数）。 */
    private static final int SC_TOO_MANY_REQUESTS = 429;

    /** Retry-After HTTP 响应头名称（RFC 7231）。 */
    private static final String HEADER_RETRY_AFTER = "Retry-After";

    /** 默认桶容量（最大突发请求数）。 */
    private static final int DEFAULT_CAPACITY = 10;

    /** 默认每周期补充令牌数。 */
    private static final int DEFAULT_REFILL_TOKENS = 10;

    /** 默认补充周期（秒）。 */
    private static final int DEFAULT_REFILL_PERIOD_SECONDS = 60;

    /** 受限流保护的敏感端点（Ant 风格）。 */
    private static final List<String> DEFAULT_PROTECTED_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/captcha",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    );

    /** 超限响应体（JSON）。 */
    private static final String REJECTED_RESPONSE_BODY =
            "{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\",\"data\":null}";

    /** Ant 路径匹配器。 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** IP → 令牌桶 映射（本地内存，按 IP 隔离）。 */
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /** 受保护端点列表。 */
    private final List<String> protectedPaths;

    /** 桶容量。 */
    private final int capacity;

    /** 每周期补充令牌数。 */
    private final int refillTokens;

    /** 补充周期（秒）。 */
    private final int refillPeriodSeconds;

    /**
     * 使用默认配置构造：10 次/分钟，敏感端点列表见 {@link #DEFAULT_PROTECTED_PATHS}。
     */
    public RateLimitFilter() {
        this(DEFAULT_PROTECTED_PATHS, DEFAULT_CAPACITY, DEFAULT_REFILL_TOKENS,
                DEFAULT_REFILL_PERIOD_SECONDS);
    }

    /**
     * 自定义配置构造。
     *
     * @param protectedPaths      受保护端点（Ant 风格）
     * @param capacity            桶容量
     * @param refillTokens        每周期补充令牌数
     * @param refillPeriodSeconds 补充周期（秒）
     */
    public RateLimitFilter(List<String> protectedPaths, int capacity, int refillTokens,
                           int refillPeriodSeconds) {
        this.protectedPaths = protectedPaths;
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodSeconds = refillPeriodSeconds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!isProtected(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = extractClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 消费成功，放行
            if (log.isDebugEnabled()) {
                log.debug("IP 限流放行: ip={}, path={}, remaining={}",
                        clientIp, path, probe.getRemainingTokens());
            }
            filterChain.doFilter(request, response);
            return;
        }

        // 限流触发：写 429 + Retry-After
        long retryAfterSeconds = computeRetryAfterSeconds(probe);
        log.warn("IP 限流触发: ip={}, path={}, remainingTokens={}, retryAfter={}s",
                clientIp, path, probe.getRemainingTokens(), retryAfterSeconds);
        rejectWithTooManyRequests(response, retryAfterSeconds);
    }

    /**
     * 判断请求路径是否为受保护端点。
     */
    private boolean isProtected(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        for (String pattern : protectedPaths) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建新令牌桶（懒加载，按 IP 首次访问时创建）。
     */
    private Bucket createNewBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(
                capacity,
                Refill.intervally(refillTokens, Duration.ofSeconds(refillPeriodSeconds)));
        Bucket bucket = Bucket.builder().addLimit(limit).build();
        log.debug("为 IP 创建新令牌桶: ip={}, capacity={}/{}/{}s",
                ip, capacity, refillTokens, refillPeriodSeconds);
        return bucket;
    }

    /**
     * 提取客户端真实 IP：
     * 优先级 X-Forwarded-For → X-Real-IP → Proxy-Client-IP → WL-Proxy-Client-IP → RemoteAddr。
     * X-Forwarded-For 含多 IP 时取首个（最原始客户端）。
     */
    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isUnknown(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isUnknown(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能形如 "client, proxy1, proxy2"，取首个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip == null ? "unknown" : ip.trim();
    }

    private boolean isUnknown(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 写入 429 响应：HTTP 状态码 429 + Retry-After 头 + JSON 错误体。
     */
    private void rejectWithTooManyRequests(HttpServletResponse response, long retryAfterSeconds)
            throws IOException {
        response.setStatus(SC_TOO_MANY_REQUESTS);
        response.setHeader(HEADER_RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(REJECTED_RESPONSE_BODY);
        response.getWriter().flush();
    }

    /**
     * 根据 {@link ConsumptionProbe} 计算 Retry-After 秒数（向上取整，最小 1 秒）。
     */
    private long computeRetryAfterSeconds(ConsumptionProbe probe) {
        long nanosToWait = probe.getNanosToWaitForRefill();
        if (nanosToWait <= 0) {
            return 1L;
        }
        return Math.max(1L, (long) Math.ceil(nanosToWait / 1_000_000_000.0));
    }
}
