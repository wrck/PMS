package com.dp.plat.workflow.service;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.workflow.dto.CompleteTaskRequest;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.dto.StartProcessRequest;
import com.dp.plat.workflow.service.impl.WorkflowServiceImpl;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WorkflowServiceImpl}.
 *
 * <p>Mocks the Flowable engine services ({@link RepositoryService}, {@link RuntimeService},
 * {@link TaskService}, {@link HistoryService}) so the service can be exercised
 * without a running Flowable engine or database.</p>
 */
@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private RuntimeService runtimeService;

    @Mock
    private TaskService taskService;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private WorkflowServiceImpl workflowService;

    // ===== deployProcess =====

    @Test
    @DisplayName("deployProcess: 文件为空抛出业务异常")
    void deployProcess_nullFile_throws() {
        assertThrows(BusinessException.class, () -> workflowService.deployProcess(null));
        verify(repositoryService, never()).createDeployment();
    }

    @Test
    @DisplayName("deployProcess: 空文件抛出业务异常")
    void deployProcess_emptyFile_throws() {
        MultipartFile file = new MockMultipartFile("file", "process.bpmn20.xml",
                "text/xml", new byte[0]);
        assertThrows(BusinessException.class, () -> workflowService.deployProcess(file));
        verify(repositoryService, never()).createDeployment();
    }

    // ===== deleteDeployment =====

    @Test
    @DisplayName("deleteDeployment: 空ID抛出业务异常")
    void deleteDeployment_blankId_throws() {
        assertThrows(BusinessException.class, () -> workflowService.deleteDeployment(""));
        assertThrows(BusinessException.class, () -> workflowService.deleteDeployment(null));
        verify(repositoryService, never()).deleteDeployment(anyString(), eq(true));
    }

    @Test
    @DisplayName("deleteDeployment: 有效ID时级联删除部署")
    void deleteDeployment_validId_callsDelete() {
        Result<Void> result = workflowService.deleteDeployment("deploy-1");

        assertTrue(result.isSuccess());
        verify(repositoryService, times(1)).deleteDeployment("deploy-1", true);
    }

    // ===== startProcess =====

    @Test
    @DisplayName("startProcess: 请求为空抛出业务异常")
    void startProcess_nullRequest_throws() {
        assertThrows(BusinessException.class, () -> workflowService.startProcess(null));
        verify(runtimeService, never()).createProcessInstanceBuilder();
    }

    @Test
    @DisplayName("startProcess: 流程定义Key为空抛出业务异常")
    void startProcess_blankKey_throws() {
        StartProcessRequest request = new StartProcessRequest();
        request.setProcessDefinitionKey("");
        assertThrows(BusinessException.class, () -> workflowService.startProcess(request));
        verify(runtimeService, never()).createProcessInstanceBuilder();
    }

    @Test
    @DisplayName("startProcess: 有效请求时启动流程并返回实例DTO")
    void startProcess_validRequest_returnsInstance() {
        StartProcessRequest request = new StartProcessRequest();
        request.setProcessDefinitionKey("project-approval");
        request.setBusinessKey("BIZ-001");
        Map<String, Object> variables = new HashMap<>();
        variables.put("projectId", 1L);
        request.setVariables(variables);

        ProcessInstanceBuilder builder = org.mockito.Mockito.mock(ProcessInstanceBuilder.class);
        when(runtimeService.createProcessInstanceBuilder()).thenReturn(builder);
        when(builder.processDefinitionKey(anyString())).thenReturn(builder);
        when(builder.businessKey(anyString())).thenReturn(builder);
        when(builder.variables(any())).thenReturn(builder);

        ProcessInstance instance = org.mockito.Mockito.mock(ProcessInstance.class);
        when(instance.getId()).thenReturn("pi-1");
        when(instance.getProcessDefinitionKey()).thenReturn("project-approval");
        when(instance.getProcessDefinitionName()).thenReturn("项目立项审批");
        when(instance.getBusinessKey()).thenReturn("BIZ-001");
        when(instance.getStartUserId()).thenReturn("system");
        when(instance.getStartTime()).thenReturn(new java.util.Date());
        when(instance.isSuspended()).thenReturn(false);
        when(builder.start()).thenReturn(instance);

        Result<ProcessInstanceDTO> result = workflowService.startProcess(request);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("pi-1", result.getData().getId());
        assertEquals("project-approval", result.getData().getProcessDefinitionKey());
        assertEquals("BIZ-001", result.getData().getBusinessKey());
        verify(builder, times(1)).start();
    }

    // ===== completeTask =====

    @Test
    @DisplayName("completeTask: 请求为空抛出业务异常")
    void completeTask_nullRequest_throws() {
        assertThrows(BusinessException.class, () -> workflowService.completeTask(null));
        verify(taskService, never()).complete(anyString());
    }

    @Test
    @DisplayName("completeTask: 任务ID为空抛出业务异常")
    void completeTask_blankTaskId_throws() {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskId("");
        assertThrows(BusinessException.class, () -> workflowService.completeTask(request));
        verify(taskService, never()).complete(anyString());
    }

    @Test
    @DisplayName("completeTask: 任务不存在抛出业务异常")
    void completeTask_taskNotFound_throws() {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskId("task-999");

        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskId(anyString())).thenReturn(query);
        when(query.singleResult()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> workflowService.completeTask(request));
        assertTrue(ex.getMessage().contains("任务不存在"));
        verify(taskService, never()).complete(anyString());
    }

    @Test
    @DisplayName("completeTask: 带审批意见完成任务调用 addComment 与 complete")
    void completeTask_withComment_callsAddCommentAndComplete() {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskId("task-1");
        request.setComment("同意");

        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskId(anyString())).thenReturn(query);

        Task task = org.mockito.Mockito.mock(Task.class);
        when(task.getId()).thenReturn("task-1");
        when(task.getProcessInstanceId()).thenReturn("pi-1");
        when(query.singleResult()).thenReturn(task);

        Result<Void> result = workflowService.completeTask(request);

        assertTrue(result.isSuccess());
        verify(taskService, times(1)).addComment("task-1", "pi-1", "同意");
        verify(taskService, times(1)).complete("task-1");
    }

    @Test
    @DisplayName("completeTask: 带流程变量完成任务调用 complete(taskId, variables)")
    void completeTask_withVariables_callsCompleteWithVariables() {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskId("task-2");
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);
        request.setVariables(variables);

        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskId(anyString())).thenReturn(query);

        Task task = org.mockito.Mockito.mock(Task.class);
        when(task.getId()).thenReturn("task-2");
        when(task.getProcessInstanceId()).thenReturn("pi-2");
        when(query.singleResult()).thenReturn(task);

        Result<Void> result = workflowService.completeTask(request);

        assertTrue(result.isSuccess());
        verify(taskService, times(1)).complete("task-2", variables);
        verify(taskService, never()).complete("task-2");
    }

    // ===== getTodoTasks =====

    @Test
    @DisplayName("getTodoTasks: 返回当前用户的待办任务分页")
    void getTodoTasks_shouldReturnPaginatedTasks() {
        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskCandidateOrAssigned(anyString())).thenReturn(query);
        when(query.orderByTaskCreateTime()).thenReturn(query);
        when(query.desc()).thenReturn(query);
        when(query.listPage(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(query.count()).thenReturn(0L);

        Result<Map<String, Object>> result = workflowService.getTodoTasks(1, 10);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0L, result.getData().get("total"));
        assertEquals(1, result.getData().get("page"));
        assertEquals(10, result.getData().get("size"));
        verify(taskService, times(2)).createTaskQuery();
    }

    @Test
    @DisplayName("getTodoTasks: 非法分页参数回退为默认值")
    void getTodoTasks_invalidPaging_usesDefaults() {
        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskCandidateOrAssigned(anyString())).thenReturn(query);
        when(query.orderByTaskCreateTime()).thenReturn(query);
        when(query.desc()).thenReturn(query);
        when(query.listPage(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(query.count()).thenReturn(0L);

        Result<Map<String, Object>> result = workflowService.getTodoTasks(-1, -5);

        assertTrue(result.isSuccess());
        // page <=0 回退为 1，size <=0 回退为 10
        assertEquals(1, result.getData().get("page"));
        assertEquals(10, result.getData().get("size"));
    }

    // ===== listProcessDefinitions =====

    @Test
    @DisplayName("listProcessDefinitions: 返回最新活跃的流程定义分页")
    void listProcessDefinitions_shouldReturnPaginatedDefinitions() {
        ProcessDefinitionQuery query = org.mockito.Mockito.mock(ProcessDefinitionQuery.class);
        when(repositoryService.createProcessDefinitionQuery()).thenReturn(query);
        when(query.latestVersion()).thenReturn(query);
        when(query.active()).thenReturn(query);
        when(query.orderByProcessDefinitionKey()).thenReturn(query);
        when(query.asc()).thenReturn(query);
        when(query.count()).thenReturn(2L);
        when(query.listPage(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        Result<Map<String, Object>> result = workflowService.listProcessDefinitions(1, 10);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2L, result.getData().get("total"));
        assertEquals(1, result.getData().get("page"));
    }

    // ===== getProcessInstance =====

    @Test
    @DisplayName("getProcessInstance: 空ID抛出业务异常")
    void getProcessInstance_blankId_throws() {
        assertThrows(BusinessException.class, () -> workflowService.getProcessInstance(""));
        assertThrows(BusinessException.class, () -> workflowService.getProcessInstance(null));
    }

    // ===== getProcessHistory =====

    @Test
    @DisplayName("getProcessHistory: 空ID抛出业务异常")
    void getProcessHistory_blankId_throws() {
        assertThrows(BusinessException.class, () -> workflowService.getProcessHistory(""));
        assertThrows(BusinessException.class, () -> workflowService.getProcessHistory(null));
    }

    @Test
    @DisplayName("getProcessHistory: 有效ID时返回历史活动列表")
    void getProcessHistory_validId_returnsHistoryList() {
        org.flowable.engine.history.HistoricActivityInstance activity =
                org.mockito.Mockito.mock(org.flowable.engine.history.HistoricActivityInstance.class);
        when(activity.getActivityId()).thenReturn("act-1");
        when(activity.getActivityName()).thenReturn("提交申请");
        when(activity.getActivityType()).thenReturn("userTask");
        when(activity.getAssignee()).thenReturn("user1");
        when(activity.getTaskId()).thenReturn("task-1");
        when(activity.getStartTime()).thenReturn(new java.util.Date());
        when(activity.getEndTime()).thenReturn(new java.util.Date());
        when(activity.getDurationInMillis()).thenReturn(5000L);

        org.flowable.engine.history.HistoricActivityInstanceQuery historyQuery =
                org.mockito.Mockito.mock(org.flowable.engine.history.HistoricActivityInstanceQuery.class);
        when(historyService.createHistoricActivityInstanceQuery()).thenReturn(historyQuery);
        when(historyQuery.processInstanceId(anyString())).thenReturn(historyQuery);
        when(historyQuery.orderByHistoricActivityInstanceStartTime()).thenReturn(historyQuery);
        when(historyQuery.asc()).thenReturn(historyQuery);
        when(historyQuery.list()).thenReturn(List.of(activity));

        Result<java.util.List<Map<String, Object>>> result =
                workflowService.getProcessHistory("pi-1");

        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        Map<String, Object> item = result.getData().get(0);
        assertEquals("act-1", item.get("activityId"));
        assertEquals("提交申请", item.get("activityName"));
        assertEquals("userTask", item.get("activityType"));
    }

    // ===== transferTask =====

    @Test
    @DisplayName("transferTask: 空任务ID抛出业务异常")
    void transferTask_blankTaskId_throws() {
        assertThrows(BusinessException.class, () -> workflowService.transferTask("", "user1"));
        verify(taskService, never()).setAssignee(anyString(), anyString());
    }

    @Test
    @DisplayName("transferTask: 空目标用户抛出业务异常")
    void transferTask_blankTargetUser_throws() {
        assertThrows(BusinessException.class, () -> workflowService.transferTask("task-1", ""));
        verify(taskService, never()).setAssignee(anyString(), anyString());
    }

    @Test
    @DisplayName("transferTask: 任务不存在抛出业务异常")
    void transferTask_taskNotFound_throws() {
        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskId(anyString())).thenReturn(query);
        when(query.singleResult()).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> workflowService.transferTask("task-999", "user2"));
        verify(taskService, never()).setAssignee(anyString(), anyString());
    }

    @Test
    @DisplayName("transferTask: 有效任务转办给目标用户")
    void transferTask_validTask_setsAssignee() {
        TaskQuery query = org.mockito.Mockito.mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(query);
        when(query.taskId(anyString())).thenReturn(query);

        Task task = org.mockito.Mockito.mock(Task.class);
        when(query.singleResult()).thenReturn(task);

        Result<Void> result = workflowService.transferTask("task-1", "user2");

        assertTrue(result.isSuccess());
        verify(taskService, times(1)).setAssignee("task-1", "user2");
    }
}
