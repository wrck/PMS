package com.dp.plat.admin.controller;

import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.project.entity.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code ProjectController} 集成测试（Task 12）。
 *
 * <p>通过 Testcontainers 启动真实 MySQL 8 + Redis，由 Flyway 自动建表，对项目管理的
 * 创建/查询/列表/更新/删除/审批/仪表盘全链路进行端到端验证。鉴权由 {@link WithMockUser}
 * 注入权限码（与 {@code @PreAuthorize} 中的权限串严格对应），Servlet 过滤器链在
 * {@link AbstractIntegrationTest} 中已关闭，避免对数据库中真实用户/角色数据的依赖。</p>
 *
 * <p>注：本类继承自 {@link AbstractIntegrationTest}，其上的
 * {@code @EnabledIfSystemProperty(docker.available=true)} 会在无 Docker 的沙箱/CI
 * 环境自动跳过，保证编译与构建始终通过。需本地运行时添加
 * {@code -Ddocker.available=true} 启动 Docker 后执行。</p>
 */
@Transactional
@WithMockUser(username = "admin", authorities = {
        "project:project:add",
        "project:project:edit",
        "project:project:remove",
        "project:project:approve"
})
class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    private Project sampleProject() {
        return Project.builder()
                .projectCode("PMS-2024-ITEST")
                .projectName("集成测试项目")
                .projectType("NETWORK_DEVICE")
                .customerName("ACME")
                .contractNo("HT-2024-001")
                .contractAmount(new BigDecimal("100000"))
                .planStartDate(LocalDate.of(2024, 2, 1))
                .planEndDate(LocalDate.of(2024, 12, 31))
                .projectManagerId(1L)
                .projectManagerName("Alice")
                .build();
    }

    private Long createProjectAndGetId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject())))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/id").asLong();
    }

    @Test
    @DisplayName("POST /api/project 创建项目返回 200 且状态为 PENDING")
    void createProject_shouldReturn200AndPendingStatus() throws Exception {
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    @DisplayName("GET /api/project/{id} 查询已存在项目返回 200")
    void getProjectById_shouldReturn200() throws Exception {
        Long id = createProjectAndGetId();

        mockMvc.perform(get("/api/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.projectName").value("集成测试项目"))
                .andExpect(jsonPath("$.data.projectCode").value("PMS-2024-ITEST"));
    }

    @Test
    @DisplayName("GET /api/project/list 分页查询项目列表返回 200")
    void listProjects_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/project/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/project/list 带状态过滤返回 200")
    void listProjects_withStatusFilter_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/project/list")
                        .param("page", "1")
                        .param("size", "10")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/project 更新项目返回 200")
    void updateProject_shouldReturn200() throws Exception {
        Long id = createProjectAndGetId();
        Project project = sampleProject();
        project.setId(id);
        project.setProjectName("集成测试项目-已更新");
        project.setProjectCode("PMS-2024-ITEST-UPD");

        mockMvc.perform(put("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/project/{id} 删除项目返回 200")
    void deleteProject_shouldReturn200() throws Exception {
        Long id = createProjectAndGetId();

        mockMvc.perform(delete("/api/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/project/{id}/approve 审批立项项目返回 200")
    void approveProject_shouldReturn200() throws Exception {
        Long id = createProjectAndGetId();

        mockMvc.perform(post("/api/project/{id}/approve", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/project/dashboard 返回 200")
    void dashboard_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/project/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/project 缺少必填字段返回 400 参数校验失败")
    void createProject_withBlankFields_shouldReturn400() throws Exception {
        Project invalid = Project.builder()
                .projectCode("")
                .projectName("")
                .projectType("")
                .customerName("")
                .build();

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("POST /api/project 权限不足返回 403")
    @WithMockUser(username = "guest", authorities = {"project:project:view"})
    void createProject_withoutPermission_shouldReturn403() throws Exception {
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }
}
