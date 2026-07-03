package com.dp.plat.workflow.listener;

import com.dp.plat.integration.model.oa.OaTodoRequest;
import com.dp.plat.integration.service.OaIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * Flowable {@link TaskListener} that mirrors user-task lifecycle events to
 * the 致远 OA todo system.
 *
 * <p>On task {@code create} a todo is pushed to OA for the task assignee; on
 * task {@code complete} the corresponding OA todo is closed. The listener is
 * registered in the BPMN via {@code delegateExpression="${oaTaskListener}"}.
 * Any OA call failure is swallowed so it never breaks the workflow
 * transaction.</p>
 */
@Slf4j
@Component("oaTaskListener")
@RequiredArgsConstructor
public class OaTaskListener implements TaskListener {

    private final OaIntegrationService oaIntegrationService;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        try {
            if ("create".equals(eventName)) {
                OaTodoRequest request = OaTodoRequest.builder()
                        .title(delegateTask.getName())
                        .content("待办任务：" + delegateTask.getName())
                        .handlerUserId(delegateTask.getAssignee())
                        .processInstanceId(delegateTask.getProcessInstanceId())
                        .build();
                oaIntegrationService.pushTodo(request);
            } else if ("complete".equals(eventName)) {
                oaIntegrationService.completeTodo(delegateTask.getId());
            }
        } catch (Exception e) {
            log.warn("OA task listener '{}' failed for task {}: {}",
                    eventName, delegateTask.getId(), e.getMessage());
        }
    }
}
