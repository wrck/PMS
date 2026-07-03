package com.dp.plat.project.deliverable.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;
import com.dp.plat.project.deliverable.service.IDeliverableChecklistService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Final acceptance deliverable checklist controller.
 */
@Tag(name = "终验交付物清单", description = "Deliverable checklist management APIs")
@RestController
@RequestMapping("/api/project/deliverable-checklist")
@RequiredArgsConstructor
public class DeliverableChecklistController {

    private final IDeliverableChecklistService deliverableChecklistService;

    @Operation(summary = "创建交付物清单项")
    @PostMapping
    public Result<DeliverableChecklist> create(@RequestBody DeliverableChecklist checklist) {
        return deliverableChecklistService.create(checklist);
    }

    @Operation(summary = "更新交付物清单项")
    @PutMapping
    public Result<?> update(@RequestBody DeliverableChecklist checklist) {
        return deliverableChecklistService.update(checklist);
    }

    @Operation(summary = "删除交付物清单项")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return deliverableChecklistService.delete(id);
    }

    @Operation(summary = "根据ID查询交付物清单项")
    @GetMapping("/{id}")
    public Result<DeliverableChecklist> getById(@PathVariable Long id) {
        return deliverableChecklistService.getById(id);
    }

    @Operation(summary = "根据项目ID查询交付物清单")
    @GetMapping("/project/{projectId}")
    public Result<List<DeliverableChecklist>> listByProject(@PathVariable Long projectId) {
        return deliverableChecklistService.listByProject(projectId);
    }

    @Operation(summary = "初始化项目标准交付物清单")
    @PostMapping("/project/{projectId}/init")
    public Result<List<DeliverableChecklist>> initChecklist(@PathVariable Long projectId) {
        return deliverableChecklistService.initChecklist(projectId);
    }
}
