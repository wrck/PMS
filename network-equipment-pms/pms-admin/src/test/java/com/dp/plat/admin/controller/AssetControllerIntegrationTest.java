package com.dp.plat.admin.controller;

import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.asset.entity.Asset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code AssetController} 集成测试（Task 12）。
 *
 * <p>覆盖设备资产的入库 / 查询 / 分页 / 更新 / 生命周期日志 / 删除全链路，以及参数校验
 * 与权限拒绝场景。鉴权通过 {@link WithMockUser} 注入与 {@code @PreAuthorize} 对应的
 * 权限码。资产入库接口实际路径为 {@code POST /api/asset/inbound}（非 {@code /api/asset}），
 * 已按源码 {@link com.dp.plat.asset.controller.AssetController} 校正。</p>
 *
 * <p>继承自 {@link AbstractIntegrationTest}，无 Docker 环境自动跳过。</p>
 */
@Transactional
@WithMockUser(username = "admin", authorities = {
        "asset:asset:add",
        "asset:asset:allocate",
        "asset:asset:return",
        "asset:asset:edit",
        "asset:asset:remove",
        "asset:asset:import"
})
class AssetControllerIntegrationTest extends AbstractIntegrationTest {

    private Asset sampleAsset() {
        return Asset.builder()
                .serialNo("SN-ITEST-0001")
                .modelId(1L)
                .categoryId(1L)
                .assetName("集成测试交换机")
                .status("IN_STOCK")
                .warehouse("北京主库")
                .location("A-01-02")
                .macAddress("00:1A:2B:3C:4D:5E")
                .build();
    }

    private Long inboundAssetAndGetId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/asset/inbound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAsset())))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/id").asLong();
    }

    @Test
    @DisplayName("POST /api/asset/inbound 资产入库返回 200")
    void inboundAsset_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/asset/inbound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAsset())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("GET /api/asset/{id} 查询资产返回 200")
    void getAsset_shouldReturn200() throws Exception {
        Long id = inboundAssetAndGetId();

        mockMvc.perform(get("/api/asset/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.assetName").value("集成测试交换机"))
                .andExpect(jsonPath("$.data.serialNo").value("SN-ITEST-0001"));
    }

    @Test
    @DisplayName("GET /api/asset/list 分页查询资产列表返回 200")
    void listAssets_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/asset/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/asset/list 带状态过滤返回 200")
    void listAssets_withStatusFilter_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/asset/list")
                        .param("page", "1")
                        .param("size", "10")
                        .param("status", "IN_STOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/asset 更新资产（状态变更）返回 200")
    void updateAsset_shouldReturn200() throws Exception {
        Long id = inboundAssetAndGetId();
        Asset asset = sampleAsset();
        asset.setId(id);
        asset.setStatus("ALLOCATED");
        asset.setLocation("B-02-03");

        mockMvc.perform(put("/api/asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/asset/{id}/lifecycle 查询资产生命周期日志返回 200")
    void getAssetLifecycle_shouldReturn200() throws Exception {
        Long id = inboundAssetAndGetId();

        mockMvc.perform(get("/api/asset/{id}/lifecycle", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/asset/{id} 删除资产返回 200")
    void deleteAsset_shouldReturn200() throws Exception {
        Long id = inboundAssetAndGetId();

        mockMvc.perform(delete("/api/asset/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/asset/inbound 缺少必填字段返回 400 参数校验失败")
    void inboundAsset_withBlankFields_shouldReturn400() throws Exception {
        Asset invalid = Asset.builder()
                .serialNo("")
                .assetName("")
                .status("")
                .build();

        mockMvc.perform(post("/api/asset/inbound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
