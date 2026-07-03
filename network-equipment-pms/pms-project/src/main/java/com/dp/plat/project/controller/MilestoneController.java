package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.service.IMilestoneService;
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

/**
 * Milestone management controller.
 */
@Tag(name = "里程碑管理", description = "Project milestone management APIs")
@RestController
@RequestMapping("/api/project/milestone")
@RequiredArgsConstructor
public class MilestoneController {

    private final IMilestoneService milestoneService;

    @Operation(summary = "创建里程碑")
    @PostMapping
    public Result<Milestone> create(@RequestBody Milestone milestone) {
        return milestoneService.createMilestone(milestone);
    }

    @Operation(summary = "更新里程碑")
    @PutMapping
    public Result<?> update(@RequestBody Milestone milestone) {
        return milestoneService.updateMilestone(milestone);
    }

    @Operation(summary = "删除里程碑")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return milestoneService.deleteMilestone(id);
    }

    @Operation(summary = "根据项目ID查询里程碑列表")
    @GetMapping("/project/{projectId}")
    public Result<?> listByProjectId(@PathVariable Long projectId) {
        return milestoneService.listByProjectId(projectId);
    }

    @Operation(summary = "更新里程碑进度")
    @PostMapping("/{id}/progress")
    public Result<Milestone> updateProgress(@PathVariable Long id,
                                            @RequestParam(required = false) String actualDate,
                                            @RequestParam(required = false) String description) {
        return milestoneService.updateProgress(id, actualDate, description);
    }
}
