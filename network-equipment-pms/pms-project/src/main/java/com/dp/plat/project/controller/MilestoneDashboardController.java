package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.MilestoneGroupDto;
import com.dp.plat.project.service.IMilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Milestone dashboard controller.
 *
 * <p>Exposes the PPDIOO-phase grouped milestone dashboard view, separated from the
 * CRUD controller to use the plural {@code /api/project/milestones} resource path.</p>
 */
@Tag(name = "里程碑看板", description = "Milestone dashboard APIs")
@RestController
@RequestMapping("/api/project/milestones")
@RequiredArgsConstructor
public class MilestoneDashboardController {

    private final IMilestoneService milestoneService;

    @Operation(summary = "按PPDIOO阶段分组的里程碑看板")
    @GetMapping("/dashboard")
    public Result<List<MilestoneGroupDto>> dashboard(@RequestParam Long projectId) {
        return milestoneService.dashboardByPpdiooPhase(projectId);
    }
}
