package com.dp.plat.notification.ws;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.crypto.SecretKey;
import java.util.Map;

/**
 * WebSocket (STOMP) 配置：通知中心实时推送通道。
 *
 * <p>暴露 STOMP 端点 {@code /ws} 供原生浏览器 WebSocket 连接（不启用 SockJS）。
 * JWT 鉴权在握手阶段完成：从 {@code token} 查询参数或 {@code Authorization: Bearer xxx}
 * 请求头提取 JWT，用 jjwt 解析出 {@code userId}，存入 STOMP 会话属性
 * {@code sessionAttributes["userId"]}，供下游处理逻辑识别连接用户。</p>
 *
 * <p>通知通过广播频道 {@code /topic/notification/{userId}} 推送，前端按当前登录
 * 用户 id 订阅对应频道。该方案不依赖 STOMP CONNECT 的 Principal，更适合
 * 原生 WebSocket 客户端。</p>
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String SESSION_ATTR_USER_ID = "userId";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int MESSAGE_SIZE_LIMIT = 64 * 1024;
    private static final long HEARTBEAT_MS = 10_000L;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey signingKey;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 为 SimpleBroker 心跳提供独立 TaskScheduler，避免与 WebSocket 配置形成循环依赖。
        ThreadPoolTaskScheduler heartbeatScheduler = new ThreadPoolTaskScheduler();
        heartbeatScheduler.setPoolSize(1);
        heartbeatScheduler.setThreadNamePrefix("ws-heartbeat-");
        heartbeatScheduler.initialize();
        // /topic、/queue 为 broker 广播目的地前缀；心跳 10s（入站/出站）保持连接活跃。
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{HEARTBEAT_MS, HEARTBEAT_MS})
                .setTaskScheduler(heartbeatScheduler);
        // /app 为应用目的地前缀，消息路由到 @MessageMapping 方法。
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor());
        // 不调用 withSockJS()：前端使用原生 WebSocket API。
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        // 入站消息大小上限 64KB，防止超大帧耗尽内存。
        registry.setMessageSizeLimit(MESSAGE_SIZE_LIMIT);
    }

    /**
     * 握手拦截器：从升级请求中提取 JWT（{@code token} 查询参数或 {@code Authorization} 头），
     * 解析成功后将 {@code userId} 存入会话属性；解析失败则拒绝握手。
     */
    private final class JwtHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) {
            String token = extractToken(request);
            if (token == null) {
                log.warn("WebSocket 握手缺少 JWT token，拒绝连接");
                return false;
            }
            Long userId = parseUserId(token);
            if (userId == null) {
                log.warn("WebSocket 握手 JWT 解析失败，拒绝连接");
                return false;
            }
            attributes.put(SESSION_ATTR_USER_ID, userId);
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
            // no-op
        }

        private String extractToken(ServerHttpRequest request) {
            // 1. 优先从 Authorization: Bearer xxx 请求头提取
            String auth = request.getHeaders().getFirst(AUTH_HEADER);
            if (auth != null && auth.startsWith(BEARER_PREFIX)) {
                return auth.substring(BEARER_PREFIX.length()).trim();
            }
            // 2. 兜底从 ?token=xxx 查询参数提取（原生 WebSocket 客户端无法自定义请求头）
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpRequest = servletRequest.getServletRequest();
                String tokenParam = httpRequest.getParameter("token");
                if (tokenParam != null && !tokenParam.isBlank()) {
                    return tokenParam.trim();
                }
            }
            return null;
        }

        private Long parseUserId(String token) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                return Long.valueOf(claims.getSubject());
            } catch (Exception e) {
                log.warn("JWT 解析失败: {}", e.getMessage());
                return null;
            }
        }

        private SecretKey getSigningKey() {
            if (signingKey == null) {
                signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
            }
            return signingKey;
        }
    }
}
