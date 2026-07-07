package com.dp.plat.admin.controller;

import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.implementation.dto.SettlementCreateRequest;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code SettlementController} 集成测试（Task 12）。
 *
 * <p>覆盖结算单的创建 / 查询 / 分页 / 审批通过 / 驳回 / 导出全链路，以及参数校验场景。
 * 结算单接口实际路径为 {@code /api/impl/settlement}（非 {@code /api/settlement}），
 * 已按源码 {@link com.dp.plat.implementation.controller.SettlementController} 校正。
 * 此外，源码中不存在独立的「提交审批」端点——创建结算单时即置为 PENDING 状态（等同于
 * 已提交），故审批通过 / 驳回可直接作用于新建的结算单。</p>
 *
 * <p>继承自 {@link AbstractIntegrationTest}，无 Docker 环境自动跳过。</p>
 */
@Transactional
@WithMockUser(username = "admin", authorities = {
        "implementation:settlement:add",
        "implementation:settlement:approve",
        "implementation:settlement:export"
})
class SettlementControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/impl/settlement";

    private Settlement sampleSettlement() {
        return Settlement.builder()
                .taskId(1L)
                .agentId(1L)
                .projectId(1L)
                .settlementNo("JS-ITEST-0001")
                .totalAmount(new BigDecimal("10000"))
                .taxRate(new BigDecimal("13.00"))
                .build();
    }

    private SettlementDetail sampleDetail() {
        return SettlementDetail.builder()
                .itemName("集成测试实施项")
                .workQuantity(new BigDecimal("10"))
                .unit("人天")
                .unitPrice(new BigDecimal("1000"))
                .amount(new BigDecimal("10000"))
                .build();
    }

    private SettlementCreateRequest sampleRequest() {
        SettlementCreateRequest request = new SettlementCreateRequest();
        request.setSettlement(sampleSettlement());
        request.setDetails(List.of(sampleDetail()));
        return request;
    }

    private Long createSettlementAndGetId() throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/id").asLong();
    }

    @Test
    @DisplayName("POST /api/impl/settlement 创建结算单返回 200 且状态为 PENDING")
    void createSettlement_shouldReturn200AndPendingStatus() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    @DisplayName("GET /api/impl/settlement/{id} 查询结算单返回 200")
    void getSettlement_shouldReturn200() throws Exception {
        Long id = createSettlementAndGetId();

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.settlementNo").value("JS-ITEST-0001"));
    }

    @Test
    @DisplayName("GET /api/impl/settlement/list 分页查询结算单列表返回 200")
    void listSettlements_shouldReturn200() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/impl/settlement/{id}/approve 审批通过返回 200")
    void approveSettlement_shouldReturn200() throws Exception {
        Long id = createSettlementAndGetId();

        mockMvc.perform(post(BASE_URL + "/{id}/approve", id)
                        .param("opinion", "同意"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/impl/settlement/{id}/reject 驳回结算单返回 200")
    void rejectSettlement_shouldReturn200() throws Exception {
        Long id = createSettlementAndGetId();

        mockMvc.perform(post(BASE_URL + "/{id}/reject", id)
                        .param("opinion", "金额有误，请核对"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/impl/settlement/export 导出结算单列表返回 200")
    void exportSettlements_shouldReturn200() throws Exception {
        createSettlementAndGetId();

        mockMvc.perform(get(BASE_URL + "/export"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/impl/settlement 缺少必填字段返回 400 参数校验失败")
    void createSettlement_withBlankFields_shouldReturn400() throws Exception {
        SettlementCreateRequest invalid = new SettlementCreateRequest();
        Settlement settlement = Settlement.builder()
                .taskId(null)
                .agentId(null)
                .projectId(null)
                .settlementNo("")
                .build();
        invalid.setSettlement(settlement);
        invalid.setDetails(List.of());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
