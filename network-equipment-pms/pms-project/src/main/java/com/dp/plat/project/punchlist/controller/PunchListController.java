package com.dp.plat.project.punchlist.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.punchlist.entity.PunchList;
import com.dp.plat.project.punchlist.service.IPunchListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "创建 Punch List 项")
    @PostMapping
    public Result<PunchList> create(@RequestBody PunchList punchList) {
        return punchListService.create(punchList);
    }

    @Operation(summary = "更新 Punch List 项")
    @PutMapping
    public Result<?> update(@RequestBody PunchList punchList) {
        return punchListService.update(punchList);
    }

    @Operation(summary = "删除 Punch List 项")
    @DeleteMapping("/{id}")
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
    public Result<PunchList> resolve(@PathVariable Long id) {
        return punchListService.resolve(id);
    }

    @Operation(summary = "验证 Punch List 项")
    @PostMapping("/{id}/verify")
    public Result<PunchList> verify(@PathVariable Long id) {
        return punchListService.verify(id);
    }

    @Operation(summary = "校验项目所有 Punch List 项是否已验证")
    @GetMapping("/project/{projectId}/all-verified")
    public Result<Boolean> isAllVerified(@PathVariable Long projectId) {
        return Result.ok(punchListService.isAllVerified(projectId));
    }
}
