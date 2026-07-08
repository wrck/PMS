package com.dp.plat.lowcode.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.dto.VersionDiffDTO;
import com.dp.plat.lowcode.dto.VersionTreeNode;
import com.dp.plat.lowcode.entity.LowCodeConfigVersion;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.dp.plat.lowcode.version.EnvironmentPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 低代码配置版本管理 Controller。
 *
 * <p>提供版本历史查询、版本 Diff 对比、版本回滚、环境晋升、配置包导出等接口。
 * 写操作需对应权限，并记录操作日志。</p>
 */
@Tag(name = "低代码配置版本管理", description = "LowCode config versioning APIs")
@RestController
@RequestMapping("/api/lowcode/version")
@RequiredArgsConstructor
public class LowCodeConfigVersionController {

    private final LowCodeConfigVersionService configVersionService;
    private final EnvironmentPromotionService promotionService;

    @Operation(summary = "查询版本历史")
    @GetMapping("/history")
    @PreAuthorize("hasAuthority('lowcode:version:list')")
    public Result<List<LowCodeConfigVersion>> history(@RequestParam String configType,
                                                        @RequestParam Long configId) {
        return Result.ok(configVersionService.getVersionHistory(configType, configId));
    }

    @Operation(summary = "查询版本树（线性构建：v1 → v2 → v3 链式树）")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('lowcode:version:list')")
    public Result<List<VersionTreeNode>> tree(@RequestParam String configType,
                                                @RequestParam Long configId) {
        return Result.ok(configVersionService.getVersionTree(configType, configId));
    }

    @Operation(summary = "对比两个版本差异")
    @GetMapping("/diff")
    @PreAuthorize("hasAuthority('lowcode:version:diff')")
    public Result<VersionDiffDTO> diff(@RequestParam String configType,
                                        @RequestParam Long configId,
                                        @RequestParam Integer fromVersion,
                                        @RequestParam Integer toVersion) {
        return Result.ok(configVersionService.diff(configType, configId, fromVersion, toVersion));
    }

    @Operation(summary = "回滚到指定版本")
    @PostMapping("/rollback")
    @PreAuthorize("hasAuthority('lowcode:version:rollback')")
    @OperLog(title = "低代码配置版本", businessType = 2)
    public Result<LowCodeConfigVersion> rollback(@RequestParam String configType,
                                                   @RequestParam Long configId,
                                                   @RequestParam Integer targetVersion,
                                                   @RequestParam(required = false) String changeLog) {
        return Result.ok(configVersionService.rollback(configType, configId, targetVersion, changeLog));
    }

    @Operation(summary = "环境晋升")
    @PostMapping("/promote")
    @PreAuthorize("hasAuthority('lowcode:version:promote')")
    @OperLog(title = "低代码配置版本", businessType = 5)
    public Result<Void> promote(@RequestParam String targetEnvironment,
                                 @RequestBody List<String> configCodes) {
        promotionService.promote(targetEnvironment, configCodes);
        return Result.ok();
    }

    @Operation(summary = "导出配置包")
    @GetMapping("/export-package")
    @PreAuthorize("hasAuthority('lowcode:version:export')")
    @OperLog(title = "低代码配置版本", businessType = 4)
    public Result<String> exportPackage(@RequestParam List<String> configCodes) {
        return Result.ok(promotionService.exportPackageJson(configCodes));
    }

    @Operation(summary = "导出配置包（zip）")
    @PostMapping("/export-package")
    @PreAuthorize("hasAuthority('lowcode:version:export')")
    @OperLog(title = "低代码配置版本", businessType = 4)
    public ResponseEntity<byte[]> exportPackageZip(@RequestBody ExportPackageRequest req) {
        byte[] zip = promotionService.exportPackageZip(req.getConfigCodes(), req.getTargetEnvironment());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=lowcode-package.zip")
                .header("Content-Type", "application/zip")
                .body(zip);
    }

    @Operation(summary = "导入配置包")
    @PostMapping("/import-package")
    @PreAuthorize("hasAuthority('lowcode:version:import')")
    @OperLog(title = "低代码配置版本", businessType = 1)
    public Result<Void> importPackage(@RequestParam("file") MultipartFile file,
                                       @RequestParam(defaultValue = "false") boolean overwrite) {
        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            promotionService.importPackageWithConfirm(json, overwrite);
            return Result.ok();
        } catch (Exception e) {
            throw new RuntimeException("导入失败", e);
        }
    }

    /**
     * 导出配置包请求体
     */
    @Data
    @Schema(description = "导出配置包请求")
    public static class ExportPackageRequest {
        @Schema(description = "配置编码列表")
        private List<String> configCodes;
        @Schema(description = "目标环境")
        private String targetEnvironment;
    }
}
