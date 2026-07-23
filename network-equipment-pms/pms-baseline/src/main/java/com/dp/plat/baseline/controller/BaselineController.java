package com.dp.plat.baseline.controller;

import com.dp.plat.baseline.dto.BaselineDiffResult;
import com.dp.plat.baseline.entity.BaselineSnapshot;
import com.dp.plat.baseline.service.BaselineService;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 计划基线管理控制器 — 列表、保存、申请变更、偏差分析。
 *
 * <p>关联设计文档：§5.5 依赖与基线 API（Story 4）。</p>
 *
 * <p>权限码：{@code project:baseline:save}（保存基线）、
 * {@code project:baseline:change}（申请变更）。列表与偏差分析为只读，不鉴权。
 * 注：设计文档原文标注 {@code @RequiresPermissions}（Shiro），但本项目未引入 Shiro
 * 依赖，统一采用 Spring Security {@code @PreAuthorize}（与 pms-implementation 模块一致），
 * 权限码保持不变。</p>
 */
@Tag(name = "计划基线管理", description = "Baseline snapshot & variance APIs")
@RestController
@RequestMapping("/api/baseline")
@RequiredArgsConstructor
public class BaselineController {

    private final BaselineService baselineService;

    @Operation(summary = "查询项目基线列表")
    @GetMapping("/list")
    public Result<List<BaselineSnapshot>> list(@RequestParam Long projectId) {
        return Result.ok(baselineService.listByProject(projectId));
    }

    @Operation(summary = "保存基线（快照项目全部任务）")
    @PostMapping("/save")
    @PreAuthorize("@ss.hasPermission('project:baseline:save')")
    @OperLog(title = "计划基线", businessType = 1)
    public Result<BaselineSnapshot> save(@RequestParam Long projectId,
                                         @RequestParam(required = false) String baselineName) {
        return Result.ok(baselineService.saveBaseline(projectId, baselineName));
    }

    @Operation(summary = "申请基线变更（双阈值 OR 触发审批）")
    @PostMapping("/{id}/request-change")
    @PreAuthorize("@ss.hasPermission('project:baseline:change')")
    @OperLog(title = "计划基线-申请变更", businessType = 2)
    public Result<BaselineDiffResult> requestChange(@PathVariable Long id,
                                                    @RequestParam(required = false) String changeReason) {
        return Result.ok(baselineService.requestBaselineChange(id, changeReason));
    }

    @Operation(summary = "基线偏差分析（逐任务对比当前计划与基线快照）")
    @GetMapping("/diff")
    public Result<BaselineDiffResult> diff(@RequestParam Long baselineId) {
        return Result.ok(baselineService.compareWithBaseline(baselineId));
    }
}
