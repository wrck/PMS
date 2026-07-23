package com.dp.plat.deliverable.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.deliverable.dto.MandatoryDeliverableValidationResult;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.entity.DeliverableReference;
import com.dp.plat.deliverable.entity.DeliverableSignature;
import com.dp.plat.deliverable.entity.DeliverableTypeTemplate;
import com.dp.plat.deliverable.entity.DeliverableVersion;
import com.dp.plat.deliverable.service.DeliverableService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 交付件全生命周期控制器 — 7 态状态机 + 版本/签名/引用管理。
 *
 * <p>关联设计文档：§5.6 交付件 API（Story 5，行 1024-1079）。
 *
 * <p>权限码：{@code project:deliverable:add}（新建/修订）、
 * {@code project:deliverable:submit}（提交）、{@code project:deliverable:review}（审核）、
 * {@code project:deliverable:sign}（签核）、{@code project:deliverable:publish}（发布）、
 * {@code project:deliverable:archive}（归档）。
 *
 * <p>注：设计文档原文标注 {@code @RequiresPermissions}（Shiro），但本项目未引入 Shiro
 * 依赖，统一采用 Spring Security {@code @PreAuthorize}（与 pms-baseline 模块一致），
 * 权限码保持不变。</p>
 */
@Tag(name = "交付件全生命周期管理", description = "Deliverable full lifecycle APIs (7-state machine)")
@RestController
@RequestMapping("/api/deliverable")
@RequiredArgsConstructor
public class DeliverableController {

    private final DeliverableService deliverableService;

    // ==================== CRUD ====================

    @Operation(summary = "查询交付件列表（按项目/阶段/状态/来源过滤）")
    @GetMapping("/list")
    public Result<List<Deliverable>> list(@RequestParam(required = false) Long projectId,
                                          @RequestParam(required = false) Long phaseId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) Boolean templateInherited) {
        return Result.ok(deliverableService.list(projectId, phaseId, status, templateInherited));
    }

    @Operation(summary = "查询交付件详情")
    @GetMapping("/{id}")
    public Result<Deliverable> get(@PathVariable Long id) {
        return Result.ok(deliverableService.getById(id));
    }

    @Operation(summary = "新建交付件（默认 DRAFT，若提供 filePath 则同步创建 v1 版本）")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('project:deliverable:add')")
    @OperLog(title = "交付件", businessType = 1)
    public Result<Deliverable> create(@Valid @RequestBody Deliverable deliverable) {
        return Result.ok(deliverableService.create(deliverable));
    }

    @Operation(summary = "上传交付件初始文件并创建 v1 版本")
    @PostMapping(value = "/{id}/upload", consumes = "multipart/form-data")
    @PreAuthorize("@ss.hasPermission('project:deliverable:upload')")
    @OperLog(title = "交付件-上传初始版本", businessType = 1)
    public Result<DeliverableVersion> upload(@PathVariable Long id,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam(required = false) String changeLog) {
        return Result.ok(deliverableService.uploadInitialVersion(id, file, changeLog));
    }

    @Operation(summary = "更新交付件基础信息（不允许直接修改 status，请走状态流转接口）")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('project:deliverable:edit')")
    @OperLog(title = "交付件", businessType = 2)
    public Result<Deliverable> update(@PathVariable Long id, @Valid @RequestBody Deliverable deliverable) {
        return Result.ok(deliverableService.updateBaseInfo(id, deliverable));
    }

    @Operation(summary = "删除交付件（仅 DRAFT 状态建议删除）")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('project:deliverable:remove')")
    @OperLog(title = "交付件", businessType = 3)
    public Result<Void> delete(@PathVariable Long id) {
        deliverableService.removeById(id);
        return Result.ok();
    }

    // ==================== 7 态状态机 ====================

    @Operation(summary = "提交：DRAFT → SUBMITTED")
    @PostMapping("/{id}/submit")
    @PreAuthorize("@ss.hasPermission('project:deliverable:submit')")
    @OperLog(title = "交付件-提交", businessType = 2)
    public Result<Deliverable> submit(@PathVariable Long id) {
        return Result.ok(deliverableService.submit(id));
    }

    @Operation(summary = "审核：SUBMITTED → REVIEWED（通过）/ DRAFT（退回）")
    @PostMapping("/{id}/review")
    @PreAuthorize("@ss.hasPermission('project:deliverable:review')")
    @OperLog(title = "交付件-审核", businessType = 2)
    public Result<Deliverable> review(@PathVariable Long id, @RequestParam boolean passed) {
        return Result.ok(deliverableService.review(id, passed));
    }

    @Operation(summary = "签核：REVIEWED → SIGNED")
    @PostMapping("/{id}/sign")
    @PreAuthorize("@ss.hasPermission('project:deliverable:sign')")
    @OperLog(title = "交付件-签核", businessType = 2)
    public Result<Deliverable> sign(@PathVariable Long id) {
        return Result.ok(deliverableService.sign(id));
    }

    @Operation(summary = "发布：SIGNED → PUBLISHED（版本固化，写入 publishedAt）")
    @PostMapping("/{id}/publish")
    @PreAuthorize("@ss.hasPermission('project:deliverable:publish')")
    @OperLog(title = "交付件-发布", businessType = 2)
    public Result<Deliverable> publish(@PathVariable Long id) {
        return Result.ok(deliverableService.publish(id));
    }

    @Operation(summary = "归档：REFERENCED → ARCHIVED（写入 archivedAt）")
    @PostMapping("/{id}/archive")
    @PreAuthorize("@ss.hasPermission('project:deliverable:archive')")
    @OperLog(title = "交付件-归档", businessType = 2)
    public Result<Deliverable> archive(@PathVariable Long id) {
        return Result.ok(deliverableService.archive(id));
    }

    // ==================== 版本管理 ====================

    @Operation(summary = "查询版本历史（按版本号倒序）")
    @GetMapping("/{id}/versions")
    public Result<List<DeliverableVersion>> listVersions(@PathVariable Long id) {
        return Result.ok(deliverableService.listVersions(id));
    }

    @Operation(summary = "修订：新建版本不覆盖旧版本（Story 5 验收 1）")
    @PostMapping("/{id}/revise")
    @PreAuthorize("@ss.hasPermission('project:deliverable:revise')")
    @OperLog(title = "交付件-修订", businessType = 1)
    public Result<DeliverableVersion> revise(@PathVariable Long id,
                                             @RequestParam String filePath,
                                             @RequestParam(required = false) String changeLog,
                                             @RequestParam(required = false) Long uploadedBy) {
        return Result.ok(deliverableService.revise(id, filePath, changeLog, uploadedBy));
    }

    @Operation(summary = "查询指定版本记录")
    @GetMapping("/{id}/versions/{versionNo}")
    public Result<DeliverableVersion> getVersion(@PathVariable Long id,
                                                  @PathVariable Integer versionNo) {
        return Result.ok(deliverableService.getVersion(id, versionNo));
    }

    // ==================== 签名管理 ====================

    @Operation(summary = "查询交付件签名记录（按签核时间倒序）")
    @GetMapping("/{id}/signatures")
    public Result<List<DeliverableSignature>> listSignatures(@PathVariable Long id) {
        return Result.ok(deliverableService.listSignatures(id));
    }

    @Operation(summary = "新增签名记录（REVIEWED → SIGNED 阶段的签核动作）")
    @PostMapping("/{id}/signatures")
    @PreAuthorize("@ss.hasPermission('project:deliverable:sign')")
    @OperLog(title = "交付件-签名", businessType = 1)
    public Result<DeliverableSignature> addSignature(@PathVariable Long id,
                                                      @RequestBody DeliverableSignature signature) {
        if (signature == null) {
            signature = DeliverableSignature.builder().build();
        }
        signature.setDeliverableId(id);
        return Result.ok(deliverableService.addSignature(signature));
    }

    // ==================== 引用管理 ====================

    @Operation(summary = "查询交付件被引用记录（按创建时间倒序）")
    @GetMapping("/{id}/references")
    public Result<List<DeliverableReference>> listReferences(@PathVariable Long id) {
        return Result.ok(deliverableService.listReferences(id));
    }

    @Operation(summary = "新增引用关系（PUBLISHED → REFERENCED 流转）")
    @PostMapping("/{id}/references")
    @PreAuthorize("@ss.hasPermission('project:deliverable:publish')")
    @OperLog(title = "交付件-引用", businessType = 1)
    public Result<DeliverableReference> addReference(@PathVariable Long id,
                                                     @RequestBody DeliverableReference reference) {
        if (reference == null) {
            reference = DeliverableReference.builder().build();
        }
        reference.setSourceDeliverableId(id);
        return Result.ok(deliverableService.addReference(reference));
    }

    // ==================== 阶段退出校验 ====================

    @Operation(summary = "阶段必需交付件校验（Story 5 验收 2，供 advancePhase 调用）")
    @GetMapping("/phase/{phaseId}/validate")
    public Result<MandatoryDeliverableValidationResult> validateMandatoryDeliverables(
            @PathVariable Long phaseId) {
        return Result.ok(deliverableService.validateMandatoryDeliverables(phaseId));
    }

    // ==================== 类型默认内容块模板 ====================

    @Operation(summary = "查询所有交付件类型默认内容块模板（按 deliverableType 升序）")
    @GetMapping("/type-templates")
    public Result<List<DeliverableTypeTemplate>> listTypeTemplates() {
        return Result.ok(deliverableService.listTypeTemplates());
    }

    @Operation(summary = "查询指定交付件类型的默认内容块模板")
    @GetMapping("/type-templates/{deliverableType}")
    public Result<DeliverableTypeTemplate> getTypeTemplate(@PathVariable String deliverableType) {
        return Result.ok(deliverableService.getTypeTemplate(deliverableType));
    }
}
