package com.dp.plat.admin.excel;

import com.alibaba.excel.EasyExcel;
import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.asset.dto.AssetImportDTO;
import com.dp.plat.project.dto.MilestoneImportDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Excel 导入 / 导出集成测试（Task 13.3）。
 *
 * <p>覆盖资产、里程碑、结算三大业务的 Excel 导出与批量导入链路。端点路径以 Controller
 * 源码为准（任务描述中的 {@code /api/milestone/export}、{@code /api/settlement/export}
 * 与实际不符，已校正）：
 * <ul>
 *   <li>资产：{@code GET /api/asset/export}、{@code GET /api/asset/template}、{@code POST /api/asset/import}</li>
 *   <li>里程碑：{@code GET /api/project/milestone/template}、{@code POST /api/project/milestone/import}
 *       （里程碑控制器未提供 export 端点，故跳过导出场景）</li>
 *   <li>结算：{@code GET /api/impl/settlement/export}（结算控制器未提供 import/template 端点）</li>
 * </ul>
 * </p>
 *
 * <p>导入测试通过 EasyExcel 在内存中构造 {@code .xlsx} 字节流，包装为
 * {@link MockMultipartFile} 上传，避免依赖外部模板文件。</p>
 *
 * <p>继承自 {@link AbstractIntegrationTest}，无 Docker 环境自动跳过。</p>
 */
@Transactional
@WithMockUser(username = "1", authorities = {
        "asset:asset:import",
        "project:milestone:import",
        "implementation:settlement:export"
})
class ExcelIntegrationTest extends AbstractIntegrationTest {

    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /** 构造资产导入 Excel 字节流。 */
    private byte[] buildAssetImportExcel(List<AssetImportDTO> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EasyExcel.write(out, AssetImportDTO.class).sheet("资产导入").doWrite(rows);
        return out.toByteArray();
    }

    /** 构造里程碑导入 Excel 字节流。 */
    private byte[] buildMilestoneImportExcel(List<MilestoneImportDTO> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EasyExcel.write(out, MilestoneImportDTO.class).sheet("里程碑导入").doWrite(rows);
        return out.toByteArray();
    }

    @Test
    @DisplayName("GET /api/asset/export 导出资产 Excel 返回 200 与 xlsx 内容类型")
    void exportAssets_shouldReturnXlsx() throws Exception {
        mockMvc.perform(get("/api/asset/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(XLSX_CONTENT_TYPE))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("asset-list")));
    }

    @Test
    @DisplayName("GET /api/asset/template 下载资产导入模板返回 200 与 xlsx")
    void downloadAssetTemplate_shouldReturnXlsx() throws Exception {
        mockMvc.perform(get("/api/asset/template"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(XLSX_CONTENT_TYPE))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("asset-template")));
    }

    @Test
    @DisplayName("POST /api/asset/import 批量导入合法资产数据返回 200 且 successList 非空")
    void importAssets_validData_shouldReturnSuccessList() throws Exception {
        AssetImportDTO row = new AssetImportDTO();
        row.setAssetNo("IMP-ITEST-0001");
        row.setSerialNo("SN-ITEST-0001");
        row.setStatus("RECEIVED");
        byte[] excel = buildAssetImportExcel(List.of(row));
        MockMultipartFile file = new MockMultipartFile(
                "file", "asset-import.xlsx", XLSX_CONTENT_TYPE, excel);

        mockMvc.perform(multipart("/api/asset/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successList").isArray())
                .andExpect(jsonPath("$.data.successList[0].assetNo").value("IMP-ITEST-0001"));
    }

    @Test
    @DisplayName("POST /api/asset/import 批量导入含非法状态数据返回 200 且 errors 非空")
    void importAssets_invalidData_shouldReturnErrorReport() throws Exception {
        AssetImportDTO row = new AssetImportDTO();
        row.setAssetNo("IMP-ITEST-0002");
        row.setSerialNo("SN-ITEST-0002");
        row.setStatus("NOT_A_VALID_STATUS");
        byte[] excel = buildAssetImportExcel(List.of(row));
        MockMultipartFile file = new MockMultipartFile(
                "file", "asset-import-bad.xlsx", XLSX_CONTENT_TYPE, excel);

        mockMvc.perform(multipart("/api/asset/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors[0].errorMessage",
                        org.hamcrest.Matchers.containsString("状态不在合法枚举内")));
    }

    @Test
    @DisplayName("GET /api/project/milestone/template 下载里程碑导入模板返回 200 与 xlsx")
    void downloadMilestoneTemplate_shouldReturnXlsx() throws Exception {
        mockMvc.perform(get("/api/project/milestone/template"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(XLSX_CONTENT_TYPE))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("milestone-template")));
    }

    @Test
    @DisplayName("POST /api/project/milestone/import 批量导入里程碑（项目ID为空）返回 200 且 errors 非空")
    void importMilestones_invalidData_shouldReturnErrorReport() throws Exception {
        MilestoneImportDTO row = new MilestoneImportDTO();
        row.setProjectId("");
        row.setMilestoneType("KICKOFF");
        row.setMilestoneName("启动会");
        row.setPlanDate("2026-01-01");
        byte[] excel = buildMilestoneImportExcel(List.of(row));
        MockMultipartFile file = new MockMultipartFile(
                "file", "milestone-import.xlsx", XLSX_CONTENT_TYPE, excel);

        mockMvc.perform(multipart("/api/project/milestone/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors[0].errorMessage",
                        org.hamcrest.Matchers.containsString("项目ID不能为空")));
    }

    @Test
    @DisplayName("GET /api/impl/settlement/export 导出结算明细返回 200 与 xlsx")
    void exportSettlements_shouldReturnXlsx() throws Exception {
        mockMvc.perform(get("/api/impl/settlement/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(XLSX_CONTENT_TYPE))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("settlement-list")));
    }

    @Test
    @DisplayName("GET /api/asset/export 带过滤条件导出返回 200 与 xlsx")
    void exportAssets_withFilter_shouldReturnXlsx() throws Exception {
        mockMvc.perform(get("/api/asset/export")
                        .param("status", "RECEIVED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(XLSX_CONTENT_TYPE));
    }
}
