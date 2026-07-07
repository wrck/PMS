package com.dp.plat.lowcode.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.dto.VersionDiffDTO;
import com.dp.plat.lowcode.entity.LowCodeConfigVersion;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.dp.plat.lowcode.version.EnvironmentPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
