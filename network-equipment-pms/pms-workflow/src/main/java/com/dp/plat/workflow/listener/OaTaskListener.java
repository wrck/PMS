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
 *
 * <p>OA integration is best-effort and intentionally independent of the main
 * workflow transaction: every OA call is wrapped in a try-catch and failures
 * are logged but never re-thrown, so a transient OA outage never rolls back
 * or breaks the Flowable task lifecycle. (An equivalent
 * {@code @Transactional(propagation=REQUIRES_NEW)} would also work, but since
 * the OA service already swallows internal errors and returns a boolean, a
 * plain try-catch is sufficient here.)</p>
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
                        .businessKey(resolveBusinessKey(delegateTask))
                        .processUrl(resolveProcessUrl(delegateTask))
                        .businessType(delegateTask.getProcessDefinitionId())
                        .build();
                oaIntegrationService.pushTodo(request);
            } else if ("complete".equals(eventName)) {
                // Use the Flowable task id as the OA business key.
                oaIntegrationService.completeTodo(delegateTask.getId());
            }
        } catch (Exception e) {
            // Swallow: OA integration must not affect the workflow transaction.
            log.warn("OA task listener '{}' failed for task {} (workflow unaffected): {}",
                    eventName, delegateTask.getId(), e.getMessage());
        }
    }

    /**
     * Resolve the business key from the process variables if available.
     */
    private String resolveBusinessKey(DelegateTask delegateTask) {
        try {
            Object value = delegateTask.getVariable("businessKey");
            return value == null ? delegateTask.getId() : value.toString();
        } catch (Exception e) {
            return delegateTask.getId();
        }
    }

    /**
     * Resolve a process detail URL from process variables if available.
     */
    private String resolveProcessUrl(DelegateTask delegateTask) {
        try {
            Object value = delegateTask.getVariable("processUrl");
            return value == null ? null : value.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
