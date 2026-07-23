package com.dp.plat.project.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.FinalAcceptance;
import com.dp.plat.project.service.IFinalAcceptanceService;
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

/**
 * Final acceptance management controller.
 */
@Tag(name = "终验管理", description = "Final acceptance management APIs")
@RestController
@RequestMapping("/api/project/acceptance")
@RequiredArgsConstructor
public class FinalAcceptanceController {

    private final IFinalAcceptanceService finalAcceptanceService;

    @Operation(summary = "申请终验")
    @PostMapping("/apply")
    @PreAuthorize("@ss.hasPermission('project:finalAcceptance:apply')")
    @OperLog(title = "终验管理", businessType = 1)
    public Result<FinalAcceptance> apply(@RequestParam Long projectId,
                                         @RequestParam(required = false) String report) {
        return finalAcceptanceService.apply(projectId, report);
    }

    @Operation(summary = "审批通过终验")
    @PostMapping("/{id}/approve")
    @PreAuthorize("@ss.hasPermission('project:finalAcceptance:approve')")
    @OperLog(title = "终验管理", businessType = 2)
    public Result<FinalAcceptance> approve(@PathVariable Long id,
                                           @RequestParam(required = false) String opinion) {
        return finalAcceptanceService.approve(id, opinion);
    }

    @Operation(summary = "驳回终验申请")
    @PostMapping("/{id}/reject")
    @PreAuthorize("@ss.hasPermission('project:finalAcceptance:approve')")
    @OperLog(title = "终验管理", businessType = 2)
    public Result<FinalAcceptance> reject(@PathVariable Long id,
                                          @RequestParam(required = false) String opinion) {
        return finalAcceptanceService.reject(id, opinion);
    }

    @Operation(summary = "根据项目ID查询终验记录")
    @GetMapping("/{projectId}")
    public Result<FinalAcceptance> getByProjectId(@PathVariable Long projectId) {
        return finalAcceptanceService.getByProjectId(projectId);
    }
}
