package com.dp.plat.project.punchlist.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.punchlist.entity.PunchList;
import com.dp.plat.project.punchlist.service.IPunchListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Punch list defect management controller.
 */
@Tag(name = "Punch List 管理", description = "Punch list defect management APIs")
@RestController
@RequestMapping("/api/project/punch-list")
@RequiredArgsConstructor
public class PunchListController {

    private final IPunchListService punchListService;

    @Operation(summary = "分页查询 Punch List 列表")
    @GetMapping("/list")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<PunchList>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PunchList> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PunchList>()
                .eq(projectId != null, PunchList::getProjectId, projectId)
                .eq(severity != null, PunchList::getSeverity, severity)
                .eq(status != null, PunchList::getStatus, status)
                .orderByDesc(PunchList::getId);
        return Result.ok(punchListService.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), wrapper));
    }

    @Operation(summary = "创建 Punch List 项")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('project:punchList:add')")
    @OperLog(title = "Punch List管理", businessType = 1)
    public Result<PunchList> create(@Valid @RequestBody PunchList punchList) {
        return punchListService.create(punchList);
    }

    @Operation(summary = "更新 Punch List 项")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('project:punchList:edit')")
    @OperLog(title = "Punch List管理", businessType = 2)
    public Result<?> update(@Valid @RequestBody PunchList punchList) {
        return punchListService.update(punchList);
    }

    @Operation(summary = "删除 Punch List 项")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('project:punchList:remove')")
    @OperLog(title = "Punch List管理", businessType = 3)
    public Result<?> delete(@PathVariable Long id) {
        return punchListService.delete(id);
    }

    @Operation(summary = "根据ID查询 Punch List 项")
    @GetMapping("/{id}")
    public Result<PunchList> getById(@PathVariable Long id) {
        return punchListService.getById(id);
    }

    @Operation(summary = "根据项目ID查询 Punch List 列表")
    @GetMapping("/project/{projectId}")
    public Result<List<PunchList>> listByProject(@PathVariable Long projectId) {
        return punchListService.listByProject(projectId);
    }

    @Operation(summary = "根据里程碑ID查询 Punch List 列表")
    @GetMapping("/milestone/{milestoneId}")
    public Result<List<PunchList>> listByMilestone(@PathVariable Long milestoneId) {
        return punchListService.listByMilestone(milestoneId);
    }

    @Operation(summary = "标记 Punch List 项为已解决")
    @PostMapping("/{id}/resolve")
    @PreAuthorize("@ss.hasPermission('project:punchList:resolve')")
    @OperLog(title = "Punch List管理", businessType = 2)
    public Result<PunchList> resolve(@PathVariable Long id) {
        return punchListService.resolve(id);
    }

    @Operation(summary = "验证 Punch List 项")
    @PostMapping("/{id}/verify")
    @PreAuthorize("@ss.hasPermission('project:punchList:verify')")
    @OperLog(title = "Punch List管理", businessType = 2)
    public Result<PunchList> verify(@PathVariable Long id) {
        return punchListService.verify(id);
    }

    @Operation(summary = "校验项目所有 Punch List 项是否已验证")
    @GetMapping("/project/{projectId}/all-verified")
    public Result<Boolean> isAllVerified(@PathVariable Long projectId) {
        return Result.ok(punchListService.isAllVerified(projectId));
    }
}
