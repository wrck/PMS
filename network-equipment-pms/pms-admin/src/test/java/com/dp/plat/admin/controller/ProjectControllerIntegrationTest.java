package com.dp.plat.admin.controller;

import com.dp.plat.admin.PmsApplication;
import com.dp.plat.project.entity.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@code ProjectController} exercising the full Spring
 * Boot web layer via {@link MockMvc}.
 *
 * <p>These tests require a running MySQL database and Redis instance (as
 * configured in {@code application.yml}). They are {@link Disabled} by default
 * to keep the build green in environments without those services. Remove the
 * {@code @Disabled} annotation (and ensure the infrastructure is available)
 * before running.</p>
 */
@SpringBootTest(classes = PmsApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Disabled("需要 MySQL 与 Redis 环境，CI 中暂不执行；本地具备环境时可移除 @Disabled")
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Project sampleProject() {
        return Project.builder()
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

    @Test
    @DisplayName("POST /api/project 创建项目返回 200 且状态为 PENDING")
    void createProject_shouldReturn200AndPendingStatus() throws Exception {
        Project project = sampleProject();

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.projectCode").exists());
    }

    @Test
    @DisplayName("GET /api/project/{id} 查询已存在项目返回 200")
    void getProjectById_shouldReturn200() throws Exception {
        // 先创建一个项目
        Project project = sampleProject();
        String response = mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).at("/data/id").asLong();

        mockMvc.perform(get("/api/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.projectName").value("集成测试项目"));
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
    @DisplayName("GET /api/project/dashboard 返回 200")
    void dashboard_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/project/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
