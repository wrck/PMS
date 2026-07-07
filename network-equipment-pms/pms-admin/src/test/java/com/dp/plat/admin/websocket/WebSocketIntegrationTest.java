package com.dp.plat.admin.websocket;

import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.ws.NotificationPublisher;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.crypto.SecretKey;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * WebSocket（STOMP）集成测试（Task 13.1）。
 *
 * <p>覆盖通知中心实时推送通道的核心场景：握手鉴权、订阅广播频道、跨实例广播消息
 * 投递、心跳保活、用户频道隔离。STOMP 端点为 {@code /ws}，前端按
 * {@code /topic/notification/{userId}} 订阅当前用户的广播频道。</p>
 *
 * <p>鉴权策略：握手阶段从 {@code token} 查询参数或 {@code Authorization} 头提取 JWT，
 * 用 jjwt 解析出 {@code userId}。本测试使用测试环境密钥
 * （{@code application-test.yml#jwt.secret}）签发合法 JWT，并构造无效 JWT 验证拒绝路径。</p>
 *
 * <p>继承自 {@link AbstractIntegrationTest}，由其提供 Testcontainers（MySQL + Redis）与
 * {@code @EnabledIfSystemProperty(docker.available=true)} 条件跳过。真实端口通过
 * {@link LocalServerPort} 注入。</p>
 *
 * <p>注意：不使用 {@code @Transactional}。{@link NotificationSubscriber} 在 Redis 监听线程
 * 中异步处理消息并回查 {@code pms_notification} 表，测试事务的未提交数据对异步线程不可见，
 * 故此处采用直连 JDBC 插入并提交，测试结束后清理。</p>
 */
@TestPropertySource(properties = "spring.flyway.clean-disabled=false")
class WebSocketIntegrationTest extends AbstractIntegrationTest {

    /** STOMP 端点路径。 */
    private static final String WS_ENDPOINT = "/ws";
    /** 单用户通知广播频道前缀。 */
    private static final String TOPIC_PREFIX = "/topic/notification/";
    /** 握手等待超时（秒）。 */
    private static final long HANDSHAKE_TIMEOUT_SECONDS = 10;
    /** 测试用 userId 区段，便于 @AfterEach 精准清理。 */
    private static final long TEST_USER_ID_BASE = 900_000L;

    @LocalServerPort
    private int port;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private NotificationPublisher notificationPublisher;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanupTestNotifications() {
        // 清理本测试类插入的通知记录，避免影响后续集成测试（容器内数据库仍共享）
        jdbcTemplate.update("DELETE FROM pms_notification WHERE user_id >= ?", TEST_USER_ID_BASE);
    }

    /** 构造 WebSocketStompClient，注册 JSON 消息转换器以解析 Notification 报文。 */
    private WebSocketStompClient stompClient() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stomp = new WebSocketStompClient(webSocketClient);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        return stomp;
    }

    /** 使用测试密钥签发合法 JWT，subject 为 userId。 */
    private String validJwt(long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .signWith(signingKey())
                .compact();
    }

    /** 一个明显非法的 JWT（未签名 / 篡改载荷）。 */
    private String invalidJwt() {
        return "invalid.jwt.token";
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /** 构造 STOMP 连接 URL：{@code ws://localhost:{port}/ws?token={jwt}}。 */
    private String wsUrl(String token) {
        return "ws://localhost:" + port + WS_ENDPOINT + "?token=" + token;
    }

    /** 直连 JDBC 持久化一条通知并提交，返回自增主键 id。 */
    private Long persistNotification(long userId) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO pms_notification (user_id, title, content, category, biz_type, read_status, channel, created_at, created_by) "
                        + "VALUES (?,?,?,?,?,?,?,?,?)",
                userId, "测试通知", "WebSocket 集成测试广播内容", "SYSTEM", "WS_TEST",
                "UNREAD", "WS", now, userId);
        return jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM pms_notification WHERE user_id = ?", Long.class, userId);
    }

    @Test
    @DisplayName("握手成功：携带合法 JWT 的 STOMP 连接能够建立")
    void handshake_withValidJwt_shouldConnect() throws Exception {
        StompSession session = stompClient()
                .connectAsync(wsUrl(validJwt(TEST_USER_ID_BASE + 1)), new WebSocketHttpHeaders(),
                        new StompSessionHandlerAdapter() {})
                .get(HANDSHAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(session.isConnected(), "携带合法 JWT 时 STOMP 会话应处于已连接状态");
        session.disconnect();
    }

    @Test
    @DisplayName("握手失败：缺少 JWT 时握手被拒绝")
    void handshake_withoutJwt_shouldBeRejected() {
        // 不携带 token 查询参数，握手拦截器返回 false，连接应在超时时间内失败
        String url = "ws://localhost:" + port + WS_ENDPOINT;
        AtomicReference<Throwable> error = new AtomicReference<>();
        try {
            stompClient()
                    .connectAsync(url, new WebSocketHttpHeaders(),
                            new StompSessionHandlerAdapter() {
                                @Override
                                public void handleTransportError(Throwable exception) {
                                    error.set(exception);
                                }
                            })
                    .get(HANDSHAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            fail("缺少 JWT 时握手应被拒绝，但连接成功建立");
        } catch (Exception expected) {
            // 握手被拒绝：connectAsync 抛出 ExecutionException / TimeoutException
            assertTrue(expected.getCause() != null || error.get() != null,
                    "缺少 JWT 时应触发传输错误或连接异常");
        }
    }

    @Test
    @DisplayName("握手失败：携带无效 JWT 时握手被拒绝")
    void handshake_withInvalidJwt_shouldBeRejected() {
        AtomicReference<Throwable> error = new AtomicReference<>();
        try {
            stompClient()
                    .connectAsync(wsUrl(invalidJwt()), new WebSocketHttpHeaders(),
                            new StompSessionHandlerAdapter() {
                                @Override
                                public void handleTransportError(Throwable exception) {
                                    error.set(exception);
                                }
                            })
                    .get(HANDSHAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            fail("携带无效 JWT 时握手应被拒绝，但连接成功建立");
        } catch (Exception expected) {
            assertTrue(expected.getCause() != null || error.get() != null,
                    "无效 JWT 时应触发传输错误或连接异常");
        }
    }

    @Test
    @DisplayName("订阅 /topic/notification/{userId} 后通过 NotificationPublisher 广播可收到消息")
    void subscribe_shouldReceiveBroadcastNotification() throws Exception {
        long userId = TEST_USER_ID_BASE + 2;
        StompSession session = stompClient()
                .connectAsync(wsUrl(validJwt(userId)), new WebSocketHttpHeaders(),
                        new StompSessionHandlerAdapter() {})
                .get(HANDSHAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Notification> received = new AtomicReference<>();

        session.subscribe(TOPIC_PREFIX + userId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Notification.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.set((Notification) payload);
                latch.countDown();
            }
        });

        // 持久化通知记录后，通过 Redis 广播频道发布；NotificationSubscriber 监听到后
        // 会回查 pms_notification 并推送到 /topic/notification/{userId}。
        Long notificationId = persistNotification(userId);
        notificationPublisher.publish(userId, notificationId);

        boolean got = latch.await(15, TimeUnit.SECONDS);
        assertTrue(got, "订阅后应在超时时间内收到广播通知");
        assertTrue(received.get() != null, "收到的通知载荷不应为空");
        session.disconnect();
    }

    @Test
    @DisplayName("心跳机制：broker 心跳配置生效，连接保持活跃")
    void heartbeat_shouldKeepConnectionAlive() throws Exception {
        StompSession session = stompClient()
                .connectAsync(wsUrl(validJwt(TEST_USER_ID_BASE + 3)), new WebSocketHttpHeaders(),
                        new StompSessionHandlerAdapter() {})
                .get(HANDSHAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // 连接建立后短暂休眠，验证会话在心跳周期内不会自动断开
        Thread.sleep(2_000L);
        assertTrue(session.isConnected(), "心跳启用后会话应保持连接");

        // 主动断开不应抛异常
        assertDoesNotThrow(session::disconnect);
    }

    @Test
    @DisplayName("频道隔离：A 用户订阅不到发往 B 用户的广播")
    void channelIsolation_userAshouldNotReceiveUserBBroadcast() throws Exception {
        long userA = TEST_USER_ID_BASE + 4;
        long userB = TEST_USER_ID_BASE + 5;
        StompSession sessionA = stompClient()
                .connectAsync(wsUrl(validJwt(userA)), new WebSocketHttpHeaders(),
                        new StompSessionHandlerAdapter() {})
                .get(HANDSHAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        sessionA.subscribe(TOPIC_PREFIX + userA, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Notification.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                latch.countDown();
            }
        });

        // 向 userB 发布广播，userA 不应收到
        Long notificationIdForB = persistNotification(userB);
        notificationPublisher.publish(userB, notificationIdForB);

        boolean got = latch.await(3, TimeUnit.SECONDS);
        assertFalse(got, "userA 不应收到发往 userB 频道的广播");
        sessionA.disconnect();
    }
}
