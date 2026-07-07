package com.dp.plat.admin.controller;

import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.workflow.dto.CompleteTaskRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code WorkflowController} 集成测试（Task 12）。
 *
 * <p>覆盖工作流的待办 / 已办 / 流程定义列表 / 流程历史 / 完成任务 / 流程实例详情查询。
 * 工作流接口实际路径为 {@code /api/workflow/task/todo}、{@code /api/workflow/task/done}、
 * {@code /api/workflow/task/complete}、{@code /api/workflow/history/{processInstanceId}}
 * （非任务描述中的 {@code /todo}、{@code /done}、{@code /complete}、{@code /history}），
 * 已按源码 {@link com.dp.plat.workflow.controller.WorkflowController} 校正。</p>
 *
 * <p>注：完成任务与流程历史查询依赖 Flowable 引擎中已部署的流程定义与运行中的任务实例。
 * 本测试在无对应数据时验证接口的健壮性（返回统一 Result 而非 5xx）；端到端流程串联
 * （部署 BPMN → 启动实例 → 完成任务）建议在具备真实流程定义的环境中补充。</p>
 *
 * <p>继承自 {@link AbstractIntegrationTest}，无 Docker 环境自动跳过。</p>
 */
@Transactional
@WithMockUser(username = "admin", authorities = {
        "workflow:task:complete",
        "workflow:instance:start",
        "workflow:definition:deploy",
        "workflow:definition:remove",
        "workflow:task:withdraw",
        "workflow:task:transfer"
})
class WorkflowControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/workflow";

    @Test
    @DisplayName("GET /api/workflow/task/todo 查询待办任务列表返回 200")
    void todoTasks_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_URL + "/task/todo")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/workflow/task/done 查询已办任务列表返回 200")
    void doneTasks_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_URL + "/task/done")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/workflow/definition/list 分页查询流程定义列表返回 200")
    void listDefinitions_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_URL + "/definition/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/workflow/history/{processInstanceId} 查询流程历史返回 200")
    void getProcessHistory_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_URL + "/history/{processInstanceId}", "non-existent-instance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/workflow/instance/{processInstanceId} 查询流程实例详情返回 200")
    void getProcessInstance_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_URL + "/instance/{processInstanceId}", "non-existent-instance"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/workflow/task/complete 完成任务（无效 taskId 返回业务错误，HTTP 200）")
    void completeTask_shouldReturnBusinessErrorForInvalidTaskId() throws Exception {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskId("non-existent-task-id");
        request.setComment("集成测试完成");
        request.setVariables(Map.of("approved", true));

        mockMvc.perform(post(BASE_URL + "/task/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
    }
}
