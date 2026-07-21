package com.dp.plat.project.deliverable.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;
import com.dp.plat.project.deliverable.service.IDeliverableChecklistService;
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
    @PreAuthorize("hasAuthority('project:deliverable:add')")
    @OperLog(title = "终验交付物清单", businessType = 1)
    public Result<DeliverableChecklist> create(@Valid @RequestBody DeliverableChecklist checklist) {
        return deliverableChecklistService.create(checklist);
    }

    @Operation(summary = "更新交付物清单项")
    @PutMapping
    @PreAuthorize("hasAuthority('project:deliverable:edit')")
    @OperLog(title = "终验交付物清单", businessType = 2)
    public Result<?> update(@Valid @RequestBody DeliverableChecklist checklist) {
        return deliverableChecklistService.update(checklist);
    }

    @Operation(summary = "删除交付物清单项")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:deliverable:remove')")
    @OperLog(title = "终验交付物清单", businessType = 3)
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
    @PreAuthorize("hasAuthority('project:deliverable:add')")
    @OperLog(title = "终验交付物清单", businessType = 1)
    public Result<List<DeliverableChecklist>> initChecklist(@PathVariable Long projectId) {
        return deliverableChecklistService.initChecklist(projectId);
    }

    /**
     * 标记指定清单项已上传附件。
     *
     * <p>专用端点，绕开 {@link #update(DeliverableChecklist)} 的 {@code @Valid}
     * 全字段校验，仅更新 attachmentId / uploaded / checkedAt 字段。</p>
     *
     * @param id      清单项 id
     * @param body    请求体，仅包含 {@code attachmentId}
     */
    @Operation(summary = "标记交付物已上传附件")
    @PutMapping("/{id}/mark-uploaded")
    @PreAuthorize("hasAuthority('project:deliverable:edit')")
    @OperLog(title = "终验交付物清单", businessType = 2)
    public Result<?> markUploaded(@PathVariable Long id,
                                   @RequestBody MarkUploadedRequest body) {
        return deliverableChecklistService.markUploaded(id, body.attachmentId());
    }

    /**
     * 取消指定清单项的上传标记。
     */
    @Operation(summary = "取消交付物上传标记")
    @PutMapping("/{id}/cancel-uploaded")
    @PreAuthorize("hasAuthority('project:deliverable:edit')")
    @OperLog(title = "终验交付物清单", businessType = 2)
    public Result<?> cancelUploaded(@PathVariable Long id) {
        return deliverableChecklistService.cancelUploaded(id);
    }

    /**
     * 标记已上传请求体。
     *
     * @param attachmentId 附件 id
     */
    public record MarkUploadedRequest(Long attachmentId) {
    }
}
