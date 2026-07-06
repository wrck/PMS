package com.dp.plat.integration;

import com.dp.plat.common.exception.IntegrationException;
import com.dp.plat.integration.config.OaProperties;
import com.dp.plat.integration.constant.IntegrationConstants;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.oa.OaTodoRequest;
import com.dp.plat.integration.model.oa.OaTokenResponse;
import com.dp.plat.integration.oauth.OAuthTokenCache;
import com.dp.plat.integration.service.IIntegrationLogService;
import com.dp.plat.integration.service.impl.OaIntegrationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 单元测试：验证 OA 集成的重试行为与异常处理（Task 19.5）。
 *
 * <p>测试覆盖：
 * <ol>
 *   <li>测试1：OA 调用成功（无重试）—— 首次调用即返回 200</li>
 *   <li>测试2：OA 调用失败后重试成功 —— 首次 token 获取失败，重试后成功</li>
 *   <li>测试3：OA 调用重试耗尽后抛 IntegrationException —— 所有尝试均失败</li>
 *   <li>测试4：OaIntegrationServiceImpl.retry() 调度重试 —— 验证日志状态更新</li>
 *   <li>测试5：OA retry 失败时不抛异常（调度安全）—— 异常被吞掉</li>
 * </ol>
 *
 * <p><b>测试策略</b>：{@code @Retry} 注解依赖 Spring AOP 代理生效，纯 Mockito
 * 单元测试中无法触发。因此本测试手动构造一个与 {@code application.yml} 中
 * {@code oaRetry} 配置语义一致的 {@link Retry} 实例，通过
 * {@link Retry#executeSupplier} 装饰 OA 服务调用（与 AOP aspect 内部行为一致），
 * 验证重试流程。底层 HTTP 调用通过 Mockito 模拟，无需真实 OA 服务。</p>
 *
 * <p><b>注意</b>：OaTaskListener 异常独立于主流程的验证（事务传播 REQUIRES_NEW）
 * 见 {@code pms-workflow} 模块的 {@code OaTaskListenerTest}。</p>
 *
 * <p><b>实现细节</b>：{@link OaProperties} 使用真实实例（非 Mock），避免 Java 25
 * 上 Mockito inline mock maker 的兼容性问题。其余依赖通过 Mockito @Mock 注入。</p>
 */
@ExtendWith(MockitoExtension.class)
class OaRetryTest {

    private static final String TOKEN_URL = "http://oa.test/oauth2/token";
    private static final String BASE_URL = "http://oa.test/api";
    private static final String PUSH_URL = BASE_URL + "/todo/push";

    private OaProperties oaProperties;

    @Mock
    private RestTemplate integrationRestTemplate;

    @Mock
    private IIntegrationLogService integrationLogService;

    @Mock
    private OAuthTokenCache oauthTokenCache;

    private OaIntegrationServiceImpl oaService;

    private Retry retry;

    @BeforeEach
    void setUp() {
        // 使用真实 OaProperties 实例（非 Mock），避免 Java 25 Mockito 兼容性问题
        oaProperties = new OaProperties();
        oaProperties.setBaseUrl(BASE_URL);
        oaProperties.setTokenUrl(TOKEN_URL);
        oaProperties.setClientId("test-client-id");
        oaProperties.setClientSecret("test-client-secret");

        ObjectMapper objectMapper = new ObjectMapper();
        oaService = new OaIntegrationServiceImpl(
                oaProperties, integrationRestTemplate, integrationLogService,
                objectMapper, oauthTokenCache);

        // OAuthTokenCache.getToken 委托给 supplier 执行实际的 token 请求，
        // 使 mock 的 integrationRestTemplate 仍能控制 token 获取的成功/失败。
        lenient().when(oauthTokenCache.getToken(eq("oa"), any())).thenAnswer(invocation -> {
            java.util.function.Supplier<OAuthTokenCache.TokenInfo> supplier = invocation.getArgument(1);
            OAuthTokenCache.TokenInfo info = supplier.get();
            return info == null ? null : info.getAccessToken();
        });

        // Resilience4j Retry 配置：3 次尝试，间隔 50ms（加速测试）
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(50))
                .retryExceptions(IntegrationException.class)
                .build();
        retry = Retry.of("oaRetry", config);
    }

    /** 配置 mock：OA token 请求成功，返回有效 token。 */
    private void mockOaTokenSuccess() {
        OaTokenResponse tokenResponse = new OaTokenResponse();
        tokenResponse.setAccessToken("valid-oa-token");
        tokenResponse.setTokenType("Bearer");
        tokenResponse.setExpiresIn(3600);
        when(integrationRestTemplate.postForObject(
                eq(TOKEN_URL), any(), eq(OaTokenResponse.class)))
                .thenReturn(tokenResponse);
    }

    /** 配置 mock：OA token 请求失败，抛出 RestClientException。 */
    private void mockOaTokenFailure() {
        when(integrationRestTemplate.postForObject(
                eq(TOKEN_URL), any(), eq(OaTokenResponse.class)))
                .thenThrow(new RestClientException("OA token endpoint down"));
    }

    /** 配置 mock：IntegrationLogService.log 返回带 id 的日志记录。 */
    private void mockLogService() {
        when(integrationLogService.log(any(IntegrationLog.class)))
                .thenAnswer(invocation -> {
                    IntegrationLog logRecord = invocation.getArgument(0);
                    logRecord.setId(1L);
                    return logRecord;
                });
    }

    /** 配置 mock：OA todo push 端点返回 200。 */
    private void mockPushTodoSuccess() {
        when(integrationRestTemplate.exchange(
                eq(PUSH_URL), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"code\":0}", HttpStatus.OK));
    }

    /**
     * 通过 Retry 装饰调用 OA pushTodo，模拟 @Retry 注解的 AOP 行为。
     */
    private boolean callPushTodo(OaTodoRequest request) {
        return retry.executeSupplier(() -> oaService.pushTodo(request));
    }

    @Test
    @DisplayName("测试1: OA 调用成功（无重试）—— 首次调用即返回 200")
    void pushTodo_success_noRetry() {
        mockOaTokenSuccess();
        mockLogService();
        mockPushTodoSuccess();

        OaTodoRequest request = OaTodoRequest.builder()
                .title("审批任务")
                .handlerUserId("user001")
                .businessKey("BK-001")
                .build();

        boolean result = callPushTodo(request);

        assertTrue(result, "OA 调用成功应返回 true");
        // 验证只调用了一次（无重试）
        verify(integrationRestTemplate, times(1)).exchange(
                eq(PUSH_URL), any(), any(), eq(String.class));
        // 验证 IntegrationLog 被标记为 SUCCESS
        verify(integrationLogService).markSuccess(eq(1L), any());
        // 验证 Retry 无重试（成功调用不触发重试计数）
        assertEquals(0, retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt(),
                "成功调用不应有重试");
    }

    @Test
    @DisplayName("测试2: OA 调用失败后重试成功 —— 首次 token 失败，重试后成功")
    void pushTodo_failThenRetry_success() {
        // 首次 token 请求失败 → 抛出 IntegrationException → Retry 重试
        // 第二次 token 请求成功 → pushTodo 成功
        OaTokenResponse tokenResponse = new OaTokenResponse();
        tokenResponse.setAccessToken("recovered-oa-token");
        tokenResponse.setTokenType("Bearer");
        tokenResponse.setExpiresIn(3600);

        when(integrationRestTemplate.postForObject(
                eq(TOKEN_URL), any(), eq(OaTokenResponse.class)))
                .thenThrow(new RestClientException("OA token endpoint transient failure"))
                .thenReturn(tokenResponse);

        mockLogService();
        mockPushTodoSuccess();

        OaTodoRequest request = OaTodoRequest.builder()
                .title("审批任务")
                .handlerUserId("user002")
                .businessKey("BK-002")
                .build();

        boolean result = callPushTodo(request);

        assertTrue(result, "重试后 OA 调用应成功");
        // 验证 token 端点被调用了 2 次（首次失败 + 重试成功）
        verify(integrationRestTemplate, times(2)).postForObject(
                eq(TOKEN_URL), any(), eq(OaTokenResponse.class));
        // 验证 IntegrationLog 被标记为 SUCCESS
        verify(integrationLogService).markSuccess(eq(1L), any());
    }

    @Test
    @DisplayName("测试3: OA 调用重试耗尽后抛 IntegrationException")
    void pushTodo_retryExhausted_throwsIntegrationException() {
        mockOaTokenFailure();
        mockLogService();

        OaTodoRequest request = OaTodoRequest.builder()
                .title("审批任务")
                .handlerUserId("user003")
                .businessKey("BK-003")
                .build();

        // 3 次尝试均失败（token 获取失败 → IntegrationException），重试耗尽后抛出
        IntegrationException ex = assertThrows(IntegrationException.class,
                () -> callPushTodo(request),
                "重试耗尽后应抛出 IntegrationException");

        assertEquals("oa", ex.getSystemName(),
                "异常应携带 systemName=oa");
        // 验证 token 端点被调用了 3 次（= maxAttempts）
        verify(integrationRestTemplate, times(3)).postForObject(
                eq(TOKEN_URL), any(), eq(OaTokenResponse.class));
        // 验证 IntegrationLog 被标记为 FAILED
        verify(integrationLogService, times(3)).markFailed(eq(1L), any());
    }

    @Test
    @DisplayName("测试4: OaIntegrationServiceImpl.retry() 调度重试验证日志状态更新")
    void retry_scheduledRetry_updatesLogStatus() {
        // 构造一个已存在的 FAILED 日志记录
        IntegrationLog failedLog = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_PUSH)
                .businessId("BK-004")
                .requestUrl(PUSH_URL)
                .requestBody("{\"title\":\"test\"}")
                .responseStatus(IntegrationConstants.STATUS_FAILED)
                .retryCount(1)
                .maxRetry(3)
                .build();
        failedLog.setId(42L);

        when(integrationLogService.getById(42L)).thenReturn(failedLog);
        mockOaTokenSuccess();
        // 重试时 push 端点返回成功
        when(integrationRestTemplate.exchange(
                eq(PUSH_URL), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        // 调用 retry 方法（不带 @Retry 注解，单次重试）
        IntegrationLog result = oaService.retry(42L);

        // 验证日志被标记为 SUCCESS
        verify(integrationLogService).markSuccess(eq(42L), eq("OK"));
        assertEquals(IntegrationConstants.LOG_TYPE_OA, result.getLogType(),
                "返回的日志 logType 应为 OA");
    }

    @Test
    @DisplayName("测试5: OA retry 失败时不抛异常（调度安全）—— 异常被吞掉")
    void retry_failure_doesNotThrow() {
        IntegrationLog failedLog = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_PUSH)
                .businessId("BK-005")
                .requestUrl(PUSH_URL)
                .requestBody("{\"title\":\"test\"}")
                .responseStatus(IntegrationConstants.STATUS_FAILED)
                .retryCount(2)
                .maxRetry(3)
                .build();
        failedLog.setId(99L);

        when(integrationLogService.getById(99L)).thenReturn(failedLog);
        mockOaTokenFailure();

        // retry 方法应吞掉异常，不向外抛出（确保调度任务不中断）
        assertDoesNotThrow(() -> oaService.retry(99L),
                "retry 方法应吞掉异常，不向调度器抛出");

        // 验证日志被标记为 FAILED
        verify(integrationLogService).markFailed(eq(99L), any());
    }
}
