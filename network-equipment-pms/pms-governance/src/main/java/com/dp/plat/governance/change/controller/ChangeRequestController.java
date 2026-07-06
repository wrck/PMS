package com.dp.plat.governance.change.controller;

import com.dp.plat.common.annotation.Idempotent;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.service.IChangeRequestService;
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
 * Change request controller (governance three-books: change request book).
 */
@Tag(name = "变更请求管理", description = "Change request governance APIs")
@RestController
@RequestMapping("/api/governance/change-request")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final IChangeRequestService changeRequestService;

    @Operation(summary = "创建变更请求")
    @PostMapping
    @PreAuthorize("hasAuthority('governance:changeRequest:add')")
    @OperLog(title = "变更请求管理", businessType = 1)
    @Idempotent
    public Result<ChangeRequest> create(@Valid @RequestBody ChangeRequest changeRequest) {
        return changeRequestService.create(changeRequest);
    }

    @Operation(summary = "更新变更请求")
    @PutMapping
    @PreAuthorize("hasAuthority('governance:changeRequest:edit')")
    @OperLog(title = "变更请求管理", businessType = 2)
    public Result<?> update(@Valid @RequestBody ChangeRequest changeRequest) {
        return changeRequestService.update(changeRequest);
    }

    @Operation(summary = "删除变更请求")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('governance:changeRequest:remove')")
    @OperLog(title = "变更请求管理", businessType = 3)
    public Result<?> delete(@PathVariable Long id) {
        return changeRequestService.delete(id);
    }

    @Operation(summary = "查询全部变更请求")
    @GetMapping
    public Result<List<ChangeRequest>> list() {
        return changeRequestService.listAll();
    }

    @Operation(summary = "根据ID查询变更请求")
    @GetMapping("/{id}")
    public Result<ChangeRequest> getById(@PathVariable Long id) {
        return changeRequestService.getById(id);
    }

    @Operation(summary = "根据项目ID查询变更请求列表")
    @GetMapping("/project/{projectId}")
    public Result<List<ChangeRequest>> listByProject(@PathVariable Long projectId) {
        return changeRequestService.listByProject(projectId);
    }

    @Operation(summary = "提交变更请求进行CCB审批")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('governance:changeRequest:process')")
    @OperLog(title = "变更请求管理", businessType = 2)
    public Result<ChangeRequest> submit(@PathVariable Long id) {
        return changeRequestService.submit(id);
    }

    @Operation(summary = "CCB审批通过变更请求")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('governance:changeRequest:process')")
    @OperLog(title = "变更请求管理", businessType = 2)
    public Result<ChangeRequest> approve(@PathVariable Long id,
                                         @RequestParam(required = false) String approverName) {
        return changeRequestService.approve(id, approverName);
    }

    @Operation(summary = "CCB驳回变更请求")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('governance:changeRequest:process')")
    @OperLog(title = "变更请求管理", businessType = 2)
    public Result<ChangeRequest> reject(@PathVariable Long id,
                                        @RequestParam(required = false) String reason) {
        return changeRequestService.reject(id, reason);
    }

    @Operation(summary = "开始实施已批准的变更请求")
    @PostMapping("/{id}/implement")
    @PreAuthorize("hasAuthority('governance:changeRequest:process')")
    @OperLog(title = "变更请求管理", businessType = 2)
    public Result<ChangeRequest> implement(@PathVariable Long id) {
        return changeRequestService.implement(id);
    }

    @Operation(summary = "关闭变更请求")
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('governance:changeRequest:process')")
    @OperLog(title = "变更请求管理", businessType = 2)
    public Result<ChangeRequest> close(@PathVariable Long id) {
        return changeRequestService.close(id);
    }
}
