package com.dp.plat.admin;

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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 幂等性集成测试：通过 {@link MockMvc} 发送两个相同 {@code X-Idempotent-Key} 的
 * POST 请求，验证 {@code @Idempotent} 注解的 REJECT 策略生效（第二个返回 409）。
 *
 * <p>测试链路：前端 X-Idempotent-Key 头 → {@code IdempotentKeyInterceptor} 透传 →
 * {@code IdempotentAspect} Redis SETNX → 重复请求抛出 {@code BusinessException}（code=409）→
 * {@code GlobalExceptionHandler} 返回 409 响应。</p>
 *
 * <p>依赖：MySQL（项目数据表）+ Redis（幂等键存储）。CI 无环境时通过
 * {@link Disabled} 跳过，本地具备环境时移除 {@code @Disabled} 即可执行。</p>
 */
@SpringBootTest(classes = PmsApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@Disabled("需要 MySQL 与 Redis 环境，CI 中暂不执行；本地具备环境时可移除 @Disabled")
class IdempotentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Project sampleProject() {
        return Project.builder()
                .projectName("幂等测试项目")
                .projectType("NETWORK_DEVICE")
                .customerName("ACME")
                .contractNo("HT-2024-IDEMPOTENT")
                .contractAmount(new BigDecimal("100000"))
                .planStartDate(LocalDate.of(2024, 2, 1))
                .planEndDate(LocalDate.of(2024, 12, 31))
                .projectManagerId(1L)
                .projectManagerName("Alice")
                .build();
    }

    @Test
    @DisplayName("相同 X-Idempotent-Key 的两个 POST 请求：第一个成功，第二个返回 409")
    void duplicateRequestWithSameKey_shouldReturn409() throws Exception {
        Project project = sampleProject();
        String json = objectMapper.writeValueAsString(project);
        String idempotentKey = UUID.randomUUID().toString();

        // 第一次请求：成功创建项目
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Idempotent-Key", idempotentKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 第二次请求（相同 X-Idempotent-Key）：被幂等切面拦截，返回 409
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Idempotent-Key", idempotentKey))
                .andExpect(status().isOk()) // GlobalExceptionHandler 不设置 HTTP 状态码，业务码 409
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("请勿重复提交"));
    }

    @Test
    @DisplayName("不同 X-Idempotent-Key 的两个 POST 请求：均成功")
    void differentKeys_shouldBothSucceed() throws Exception {
        Project project = sampleProject();
        String json = objectMapper.writeValueAsString(project);

        // 第一次请求（key A）：成功
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Idempotent-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 第二次请求（key B）：同样成功（不同幂等键，互不影响）
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Idempotent-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("未携带 X-Idempotent-Key 头：跳过幂等校验，请求正常处理")
    void requestWithoutKey_shouldSkipIdempotency() throws Exception {
        Project project = sampleProject();
        String json = objectMapper.writeValueAsString(project);

        // 未携带 X-Idempotent-Key：跳过幂等保护，直接执行
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
