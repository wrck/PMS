package com.dp.plat.workflow.listener;

import com.dp.plat.common.exception.IntegrationException;
import com.dp.plat.integration.model.oa.OaTodoRequest;
import com.dp.plat.integration.service.OaIntegrationService;
import org.flowable.task.service.delegate.DelegateTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 单元测试：验证 OaTaskListener 异常独立于主流程（Task 19.4 / 19.5）。
 *
 * <p>测试覆盖：
 * <ol>
 *   <li>测试1：任务 create 事件 → OA 推送成功，不抛异常</li>
 *   <li>测试2：任务 create 事件 → OA 推送失败（IntegrationException），异常被吞掉，不影响主流程</li>
 *   <li>测试3：任务 complete 事件 → OA 完成失败（IntegrationException），异常被吞掉</li>
 *   <li>测试4：未知事件 → 不调用 OA 服务，不抛异常</li>
 * </ol>
 *
 * <p><b>验证目标</b>：OaTaskListener.notify() 标注了
 * {@code @Transactional(propagation = REQUIRES_NEW)}，且方法内部 try-catch
 * 吞掉所有异常。本测试验证：无论 OA 服务是否抛出异常，notify() 方法都不向
 * 调用方（Flowable 引擎）传播异常，确保工作流主流程不受 OA 集成故障影响。</p>
 *
 * <p><b>注</b>：{@code @Transactional} 注解在纯 Mockito 单元测试中不生效（无 Spring
 * 上下文 / AOP 代理），但异常吞咽行为（try-catch）可直接验证。事务隔离行为
 * 需通过集成测试（Spring Context + 数据库）验证，本测试聚焦于异常传播逻辑。</p>
 */
@ExtendWith(MockitoExtension.class)
class OaTaskListenerTest {

    @Mock
    private OaIntegrationService oaIntegrationService;

    @Mock
    private DelegateTask delegateTask;

    private OaTaskListener oaTaskListener;

    @BeforeEach
    void setUp() {
        oaTaskListener = new OaTaskListener(oaIntegrationService);
    }

    /**
     * 配置 mock DelegateTask 的通用属性。
     *
     * <p>使用 {@code lenient()} 标注非所有测试都触发的 stub（如 getVariable /
     * getAssignee 在 complete 事件中不被调用），避免 Strict 模式下的
     * UnnecessaryStubbingException。</p>
     *
     * @param eventName 事件名称（"create" / "complete"）
     */
    private void mockDelegateTask(String eventName) {
        when(delegateTask.getEventName()).thenReturn(eventName);
        // 以下 stub 在不同测试中触发的代码路径不同，统一使用 lenient 避免
        // UnnecessaryStubbingException（Strict 模式下未使用的 stub 会报错）
        lenient().when(delegateTask.getId()).thenReturn("task-001");
        lenient().when(delegateTask.getName()).thenReturn("项目审批");
        lenient().when(delegateTask.getAssignee()).thenReturn("user001");
        lenient().when(delegateTask.getProcessInstanceId()).thenReturn("pi-001");
        lenient().when(delegateTask.getProcessDefinitionId()).thenReturn("project-approval:1:1");
        lenient().when(delegateTask.getVariable("businessKey")).thenReturn("BK-001");
        lenient().when(delegateTask.getVariable("processUrl")).thenReturn("http://pms/approval/1");
    }

    @Test
    @DisplayName("测试1: 任务 create 事件 → OA 推送成功，不抛异常")
    void onTaskCreated_oaSuccess_noException() {
        mockDelegateTask("create");
        when(oaIntegrationService.pushTodo(any(OaTodoRequest.class))).thenReturn(true);

        assertDoesNotThrow(() -> oaTaskListener.notify(delegateTask),
                "OA 推送成功时 notify 不应抛出异常");

        verify(oaIntegrationService).pushTodo(any(OaTodoRequest.class));
    }

    @Test
    @DisplayName("测试2: 任务 create 事件 → OA 推送失败（IntegrationException），异常被吞掉，不影响主流程")
    void onTaskCreated_oaFailure_exceptionSwallowed() {
        mockDelegateTask("create");
        // OA 推送抛出 IntegrationException（如熔断器 OPEN / token 获取失败）
        doThrow(new IntegrationException("oa", "OA 服务暂不可用，请稍后重试"))
                .when(oaIntegrationService).pushTodo(any(OaTodoRequest.class));

        // 关键验证：notify 方法不应抛出异常，异常被 try-catch 吞掉
        assertDoesNotThrow(() -> oaTaskListener.notify(delegateTask),
                "OA 推送失败时 notify 不应向 Flowable 引擎抛出异常");

        // 验证 OA 服务确实被调用了
        verify(oaIntegrationService).pushTodo(any(OaTodoRequest.class));
    }

    @Test
    @DisplayName("测试3: 任务 complete 事件 → OA 完成失败（IntegrationException），异常被吞掉")
    void onTaskCompleted_oaFailure_exceptionSwallowed() {
        mockDelegateTask("complete");
        doThrow(new IntegrationException("oa", "OA 完成待办服务暂不可用"))
                .when(oaIntegrationService).completeTodo("task-001");

        assertDoesNotThrow(() -> oaTaskListener.notify(delegateTask),
                "OA 完成失败时 notify 不应向 Flowable 引擎抛出异常");

        verify(oaIntegrationService).completeTodo("task-001");
    }

    @Test
    @DisplayName("测试4: 未知事件 → 不调用 OA 服务，不抛异常")
    void onUnknownEvent_noOaCall_noException() {
        mockDelegateTask("assignment");

        assertDoesNotThrow(() -> oaTaskListener.notify(delegateTask),
                "未知事件不应抛出异常");

        // 验证 OA 服务未被调用（assignment 事件不触发 OA 同步）
        verify(oaIntegrationService, never()).pushTodo(any(OaTodoRequest.class));
        verify(oaIntegrationService, never()).completeTodo(any());
    }
}
