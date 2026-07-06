package com.dp.plat.project.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.excel.ExcelImportResult;
import com.dp.plat.common.excel.ExcelUtils;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.MilestoneImportDTO;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.service.IMilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;

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
    @PreAuthorize("hasAuthority('project:milestone:add')")
    @OperLog(title = "里程碑管理", businessType = 1)
    public Result<Milestone> create(@Valid @RequestBody Milestone milestone) {
        return milestoneService.createMilestone(milestone);
    }

    @Operation(summary = "更新里程碑")
    @PutMapping
    @PreAuthorize("hasAuthority('project:milestone:edit')")
    @OperLog(title = "里程碑管理", businessType = 2)
    public Result<?> update(@Valid @RequestBody Milestone milestone) {
        return milestoneService.updateMilestone(milestone);
    }

    @Operation(summary = "删除里程碑")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:milestone:remove')")
    @OperLog(title = "里程碑管理", businessType = 3)
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
    @PreAuthorize("hasAuthority('project:milestone:edit')")
    @OperLog(title = "里程碑管理", businessType = 2)
    public Result<Milestone> updateProgress(@PathVariable Long id,
                                            @RequestParam(required = false) String actualDate,
                                            @RequestParam(required = false) String description) {
        return milestoneService.updateProgress(id, actualDate, description);
    }

    @Operation(summary = "Download milestone import template")
    @GetMapping("/template")
    public void template(HttpServletResponse response) {
        ExcelUtils.exportTemplate(response, "milestone-template", "里程碑导入模板", MilestoneImportDTO.class);
    }

    @Operation(summary = "Batch import milestones from Excel")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('project:milestone:import')")
    @OperLog(title = "里程碑管理", businessType = 5)
    public Result<ExcelImportResult<MilestoneImportDTO>> importExcel(@RequestParam("file") MultipartFile file) {
        return Result.ok(milestoneService.batchImport(file));
    }
}
