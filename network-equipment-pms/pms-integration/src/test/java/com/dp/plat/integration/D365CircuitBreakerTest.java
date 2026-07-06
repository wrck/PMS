package com.dp.plat.integration;

import com.dp.plat.common.exception.IntegrationException;
import com.dp.plat.integration.config.D365Properties;
import com.dp.plat.integration.d365.mapper.D365InvoiceMapper;
import com.dp.plat.integration.d365.mapper.D365PurchaseReceiptMapper;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.d365.PurchaseReceiptHeader;
import com.dp.plat.integration.model.d365.TokenResponse;
import com.dp.plat.integration.oauth.OAuthTokenCache;
import com.dp.plat.integration.service.IIntegrationLogService;
import com.dp.plat.integration.service.impl.D365IntegrationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * 单元测试：验证 D365 集成的 Resilience4j CircuitBreaker 状态机流程。
 *
 * <p>测试覆盖 CircuitBreaker 的完整生命周期：
 * <ol>
 *   <li>CLOSED → OPEN：连续失败触发熔断</li>
 *   <li>OPEN 状态调用被拒绝（CallNotPermittedException），触发 fallback</li>
 *   <li>OPEN → HALF_OPEN：等待 wait-duration 后自动转半开</li>
 *   <li>HALF_OPEN → CLOSED：半开态成功调用后恢复</li>
 * </ol>
 *
 * <p><b>测试策略</b>：{@code @CircuitBreaker} 注解依赖 Spring AOP 代理生效，
 * 纯 Mockito 单元测试中无法触发。因此本测试手动构造一个与 {@code application.yml}
 * 中 {@code d365CircuitBreaker} 配置一致的 {@link CircuitBreaker} 实例，
 * 通过 {@link CircuitBreaker#executeSupplier} 装饰 D365 服务调用
 * （与 AOP aspect 内部行为一致），验证状态转换。
 * 底层 HTTP 调用通过 Mockito 模拟，无需真实 D365 服务。</p>
 */
@ExtendWith(MockitoExtension.class)
class D365CircuitBreakerTest {

    private static final String TOKEN_URL = "http://d365.test/oauth2/v2.0/token";
    private static final String BASE_URL = "http://d365.test/api";

    @Mock
    private D365Properties d365Properties;

    @Mock
    private RestTemplate integrationRestTemplate;

    @Mock
    private IIntegrationLogService integrationLogService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private D365PurchaseReceiptMapper d365PurchaseReceiptMapper;

    @Mock
    private D365InvoiceMapper d365InvoiceMapper;

    @Mock
    private OAuthTokenCache oauthTokenCache;

    private D365IntegrationServiceImpl d365Service;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        d365Service = new D365IntegrationServiceImpl(
                d365Properties, integrationRestTemplate, integrationLogService,
                objectMapper, applicationContext, oauthTokenCache,
                d365PurchaseReceiptMapper, d365InvoiceMapper);

        // OAuthTokenCache.getToken 委托给 supplier 执行实际的 token 请求，
        // 使现有的 integrationRestTemplate mock 仍能控制 token 获取的成功/失败。
        lenient().when(oauthTokenCache.getToken(eq("d365"), any())).thenAnswer(invocation -> {
            java.util.function.Supplier<OAuthTokenCache.TokenInfo> supplier = invocation.getArgument(1);
            OAuthTokenCache.TokenInfo info = supplier.get();
            return info == null ? null : info.getAccessToken();
        });

        // CircuitBreaker 配置与 application.yml 的 d365CircuitBreaker 一致，
        // 但缩小窗口 / 缩短等待时间以加速测试（生产：window=20, minCalls=10, wait=30s）。
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(4)
                .minimumNumberOfCalls(4)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofMillis(300))
                .permittedNumberOfCallsInHalfOpenState(2)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(IntegrationException.class)
                .build();
        circuitBreaker = CircuitBreaker.of("d365CircuitBreaker", config);
    }

    /**
     * 通过 CircuitBreaker 装饰调用 D365 pushPurchaseReceipt，
     * 模拟 @CircuitBreaker 注解的 AOP 行为。
     */
    private String callPushPurchaseReceipt(PurchaseReceiptHeader header) {
        return circuitBreaker.executeSupplier(() -> d365Service.pushPurchaseReceipt(header));
    }

    /** 配置 mock：D365 token 请求失败 → 抛出 RestClientException → 包装为 IntegrationException。 */
    private void mockD365TokenFailure() {
        when(d365Properties.getTokenUrl()).thenReturn(TOKEN_URL);
        when(integrationRestTemplate.postForObject(
                eq(TOKEN_URL), any(), eq(TokenResponse.class)))
                .thenThrow(new RestClientException("D365 token endpoint down"));
    }

    /** 配置 integrationLogService.log 返回带 id 的日志记录（模拟 MyBatis-Plus insert 回填 id）。 */
    private void mockLogService() {
        when(integrationLogService.log(any(IntegrationLog.class)))
                .thenAnswer(invocation -> {
                    IntegrationLog log = invocation.getArgument(0);
                    log.setId(1L);
                    return log;
                });
    }

    @Test
    @DisplayName("测试1: 连续失败触发熔断器开启 (CLOSED → OPEN)")
    void consecutiveFailures_shouldOpenCircuit() {
        mockD365TokenFailure();
        when(d365Properties.getBaseUrl()).thenReturn(BASE_URL);
        mockLogService();

        PurchaseReceiptHeader header = new PurchaseReceiptHeader();
        header.setPurchaseOrderId("PO-001");

        // 调用 4 次（= minimumNumberOfCalls），全部失败（token 请求失败 → IntegrationException）
        // 失败率 100% >= 50%，触发熔断
        for (int i = 0; i < 4; i++) {
            assertThrows(IntegrationException.class,
                    () -> callPushPurchaseReceipt(header),
                    "第 " + (i + 1) + " 次调用应抛出 IntegrationException");
        }

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState(),
                "4 次失败后熔断器应处于 OPEN 状态");
    }

    @Test
    @DisplayName("测试2: 熔断器 OPEN 时调用被拒绝并触发 fallback")
    void openCircuit_shouldRejectCallAndTriggerFallback() {
        // 先触发熔断
        mockD365TokenFailure();
        when(d365Properties.getBaseUrl()).thenReturn(BASE_URL);
        mockLogService();

        PurchaseReceiptHeader header = new PurchaseReceiptHeader();
        header.setPurchaseOrderId("PO-002");
        for (int i = 0; i < 4; i++) {
            assertThrows(IntegrationException.class, () -> callPushPurchaseReceipt(header));
        }
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // 熔断 OPEN 时调用 → CallNotPermittedException
        // （生产环境由 @CircuitBreaker aspect 捕获并路由到 fallbackMethod）
        assertThrows(CallNotPermittedException.class,
                () -> callPushPurchaseReceipt(header),
                "熔断 OPEN 时应抛出 CallNotPermittedException");

        // 验证 fallback 方法本身抛出 IntegrationException（与生产环境行为一致）
        // fallback 签名：pushPurchaseReceiptFallback(PurchaseReceiptHeader, Throwable)
        IntegrationException fallbackEx = assertThrows(IntegrationException.class,
                () -> ReflectionTestUtils.invokeMethod(d365Service,
                        "pushPurchaseReceiptFallback", header,
                        new RuntimeException("circuit open")));
        assertTrue(fallbackEx.getMessage().contains("D365 采购收货推送服务暂不可用"),
                "fallback 异常消息应包含降级提示");
        assertEquals("d365", fallbackEx.getSystemName(),
                "fallback 异常应携带 systemName=d365");
    }

    @Test
    @DisplayName("测试3: 等待后熔断器自动转为半开 (OPEN → HALF_OPEN)")
    void afterWaitDuration_shouldTransitionToHalfOpen() {
        // 先触发熔断
        mockD365TokenFailure();
        when(d365Properties.getBaseUrl()).thenReturn(BASE_URL);
        mockLogService();

        PurchaseReceiptHeader header = new PurchaseReceiptHeader();
        header.setPurchaseOrderId("PO-003");
        for (int i = 0; i < 4; i++) {
            assertThrows(IntegrationException.class, () -> callPushPurchaseReceipt(header));
        }
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // 等待 wait-duration（300ms）后，automaticTransitionFromOpenToHalfOpenEnabled=true
        // 由后台 ScheduledExecutorService 自动转为 HALF_OPEN
        boolean halfOpen = awaitState(CircuitBreaker.State.HALF_OPEN, 3000);
        assertTrue(halfOpen, "等待 wait-duration 后熔断器应自动转为 HALF_OPEN");
    }

    @Test
    @DisplayName("测试4: 半开状态下成功调用后熔断器关闭 (HALF_OPEN → CLOSED)")
    void successInHalfOpen_shouldCloseCircuit() {
        // Phase 1: 触发熔断
        mockD365TokenFailure();
        when(d365Properties.getBaseUrl()).thenReturn(BASE_URL);
        mockLogService();

        PurchaseReceiptHeader header = new PurchaseReceiptHeader();
        header.setPurchaseOrderId("PO-004");
        for (int i = 0; i < 4; i++) {
            assertThrows(IntegrationException.class, () -> callPushPurchaseReceipt(header));
        }
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // Phase 2: 等待转半开
        assertTrue(awaitState(CircuitBreaker.State.HALF_OPEN, 3000),
                "应先转为 HALF_OPEN");

        // Phase 3: 切换 mock 为成功响应（D365 恢复）
        // 仅重置 RestTemplate mock，d365Properties / integrationLogService 的 stub 保持不变
        reset(integrationRestTemplate);
        TokenResponse token = new TokenResponse();
        token.setAccessToken("recovered-token");
        token.setExpiresIn(3600);
        when(integrationRestTemplate.postForObject(
                eq(TOKEN_URL), any(), eq(TokenResponse.class)))
                .thenReturn(token);
        when(integrationRestTemplate.postForEntity(
                eq(BASE_URL + "/purchase-receipts"), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        // 半开态下成功调用 permittedNumberOfCallsInHalfOpenState(2) 次 → 转为 CLOSED
        for (int i = 0; i < 2; i++) {
            String result = callPushPurchaseReceipt(header);
            assertEquals("OK", result, "D365 恢复后应返回正常响应");
        }
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState(),
                "半开态下成功调用后熔断器应转为 CLOSED");
    }

    /**
     * 轮询等待熔断器进入目标状态，超时返回 false。
     * 用于处理 automaticTransitionFromOpenToHalfOpenEnabled 的异步状态转换。
     */
    private boolean awaitState(CircuitBreaker.State target, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (circuitBreaker.getState() == target) {
                return true;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return circuitBreaker.getState() == target;
            }
        }
        return circuitBreaker.getState() == target;
    }
}
