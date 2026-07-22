package com.dp.plat.admin.controller;

import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.service.IAssetService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.service.DeliverableService;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.service.IImplTaskService;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.service.IProjectPhaseService;
import com.dp.plat.project.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交付件引用实体聚合查询控制器。
 *
 * <p>pms-admin 依赖所有业务模块，在此聚合各模块的查询能力，
 * 为交付件「实体引用」类型提供统一的实体概要查询和列表选择接口。
 * 避免在 pms-deliverable 模块引入对 pms-implementation/pms-asset 等的依赖。</p>
 */
@Tag(name = "交付件引用实体查询", description = "Deliverable referenced entity lookup APIs")
@RestController
@RequestMapping("/api/deliverable/ref-entity")
@RequiredArgsConstructor
public class DeliverableRefEntityController {

    private final IImplTaskService implTaskService;
    private final IAssetService assetService;
    private final IProjectPhaseService projectPhaseService;
    private final IProjectService projectService;
    private final DeliverableService deliverableService;

    /**
     * 引用实体概要信息。
     *
     * @param refEntityType 引用实体类型（TASK/ASSET/PHASE/PROJECT/DELIVERABLE/REPORT）
     * @param refEntityId   引用实体ID
     * @return 实体概要（id/name/type/description 等关键字段）
     */
    @Operation(summary = "查询引用实体概要信息")
    @GetMapping("/{refEntityType}/{refEntityId}")
    public Result<Map<String, Object>> getEntitySummary(
            @PathVariable String refEntityType,
            @PathVariable Long refEntityId) {
        if (refEntityId == null || refEntityId <= 0) {
            throw new BusinessException("引用实体ID无效");
        }
        Map<String, Object> summary = lookupEntitySummary(refEntityType, refEntityId);
        return Result.ok(summary);
    }

    /**
     * 查询指定项目下可选的引用实体列表（用于实体选择器下拉）。
     *
     * @param refEntityType 引用实体类型
     * @param projectId     项目ID（可空，空时查全部）
     * @return 实体列表（每项含 id 和 name）
     */
    @Operation(summary = "查询可选引用实体列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listEntities(
            @RequestParam String refEntityType,
            @RequestParam(required = false) Long projectId) {
        if (refEntityType == null || refEntityType.isBlank()) {
            throw new BusinessException("引用实体类型不能为空");
        }
        List<Map<String, Object>> list = lookupEntityList(refEntityType, projectId);
        return Result.ok(list);
    }

    // ==================== 查询逻辑 ====================

    private Map<String, Object> lookupEntitySummary(String refEntityType, Long refEntityId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("refEntityType", refEntityType);
        summary.put("refEntityId", refEntityId);
        switch (refEntityType) {
            case "TASK": {
                ImplTask task = implTaskService.getById(refEntityId);
                if (task == null) throw new BusinessException("引用的任务不存在：id=" + refEntityId);
                summary.put("name", task.getTaskName());
                summary.put("projectId", task.getProjectId());
                summary.put("detailUrl", "/implementation/task/" + refEntityId);
                break;
            }
            case "ASSET": {
                Asset asset = assetService.getById(refEntityId);
                if (asset == null) throw new BusinessException("引用的资产不存在：id=" + refEntityId);
                summary.put("name", asset.getAssetName());
                summary.put("hostname", asset.getHostname());
                summary.put("detailUrl", "/asset/detail/" + refEntityId);
                break;
            }
            case "PHASE": {
                ProjectPhase phase = projectPhaseService.getById(refEntityId);
                if (phase == null) throw new BusinessException("引用的阶段不存在：id=" + refEntityId);
                summary.put("name", phase.getPhaseName());
                summary.put("projectId", phase.getProjectId());
                summary.put("detailUrl", "/project/phase/" + (phase.getProjectId() != null ? phase.getProjectId() : ""));
                break;
            }
            case "PROJECT": {
                Project project = projectService.getById(refEntityId);
                if (project == null) throw new BusinessException("引用的项目不存在：id=" + refEntityId);
                summary.put("name", project.getProjectName());
                summary.put("detailUrl", "/project/detail/" + refEntityId);
                break;
            }
            case "DELIVERABLE": {
                Deliverable deliverable = deliverableService.getById(refEntityId);
                if (deliverable == null) throw new BusinessException("引用的交付件不存在：id=" + refEntityId);
                summary.put("name", deliverable.getDeliverableName());
                summary.put("projectId", deliverable.getProjectId());
                summary.put("detailUrl", "/deliverable/detail/" + refEntityId);
                break;
            }
            case "REPORT":
                // Report 为统计聚合接口，无单实体，返回固定信息
                summary.put("name", "统计报告 #" + refEntityId);
                summary.put("detailUrl", "/report");
                break;
            default:
                throw new BusinessException("不支持的引用实体类型：" + refEntityType);
        }
        return summary;
    }

    private List<Map<String, Object>> lookupEntityList(String refEntityType, Long projectId) {
        List<Map<String, Object>> list = new ArrayList<>();
        switch (refEntityType) {
            case "TASK": {
                List<ImplTask> tasks = (projectId != null)
                        ? implTaskService.getByProjectId(projectId)
                        : implTaskService.list();
                if (tasks != null) {
                    for (ImplTask t : tasks) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", t.getId());
                        item.put("name", t.getTaskName());
                        list.add(item);
                    }
                }
                break;
            }
            case "ASSET": {
                List<Asset> assets = assetService.list();
                if (assets != null) {
                    for (Asset a : assets) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", a.getId());
                        item.put("name", a.getAssetName());
                        list.add(item);
                    }
                }
                break;
            }
            case "PHASE": {
                if (projectId != null) {
                    List<ProjectPhase> phases = projectPhaseService.listByProjectId(projectId);
                    if (phases != null) {
                        for (ProjectPhase p : phases) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", p.getId());
                            item.put("name", p.getPhaseName());
                            list.add(item);
                        }
                    }
                }
                break;
            }
            case "PROJECT": {
                List<Project> projects = projectService.list();
                if (projects != null) {
                    for (Project p : projects) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", p.getId());
                        item.put("name", p.getProjectName());
                        list.add(item);
                    }
                }
                break;
            }
            case "DELIVERABLE": {
                List<Deliverable> deliverables = deliverableService.list(projectId, null, null, null);
                if (deliverables != null) {
                    for (Deliverable d : deliverables) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", d.getId());
                        item.put("name", d.getDeliverableName());
                        list.add(item);
                    }
                }
                break;
            }
            case "REPORT":
                // Report 无可选实体列表
                break;
            default:
                throw new BusinessException("不支持的引用实体类型：" + refEntityType);
        }
        return list;
    }
}
