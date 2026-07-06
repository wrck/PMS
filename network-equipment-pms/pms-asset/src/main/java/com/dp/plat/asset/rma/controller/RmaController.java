package com.dp.plat.asset.rma.controller;

import com.dp.plat.asset.rma.dto.RmaKpiDto;
import com.dp.plat.asset.rma.entity.Rma;
import com.dp.plat.asset.rma.service.IRmaService;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.file.entity.Attachment;
import com.dp.plat.file.service.IAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RMA return process controller.
 */
@Slf4j
@Tag(name = "RMA 退货返修", description = "RMA return merchandise authorization APIs")
@RestController
@RequestMapping("/api/asset/rma")
@RequiredArgsConstructor
public class RmaController {

    /** Attachment business type for RMA fault photos. */
    private static final String ATTACHMENT_BIZ_TYPE_RMA = "RMA";

    private final IRmaService rmaService;
    private final IAttachmentService attachmentService;

    @Operation(summary = "Register a new RMA ticket")
    @PostMapping
    @PreAuthorize("hasAuthority('asset:rma:add')")
    @OperLog(title = "RMA退货返修", businessType = 1)
    public Result<Rma> create(@Valid @RequestBody Rma rma) {
        rmaService.create(rma);
        return Result.ok(rma);
    }

    @Operation(summary = "Check warranty status for the RMA asset")
    @PostMapping("/{id}/check-warranty")
    @PreAuthorize("hasAuthority('asset:rma:process')")
    @OperLog(title = "RMA退货返修", businessType = 2)
    public Result<Boolean> checkWarranty(@PathVariable Long id) {
        return Result.ok(rmaService.checkWarranty(id));
    }

    @Operation(summary = "Issue the RMA (RMA_ISSUED)")
    @PostMapping("/{id}/issue")
    @PreAuthorize("hasAuthority('asset:rma:process')")
    @OperLog(title = "RMA退货返修", businessType = 2)
    public Result<Boolean> issueRma(@PathVariable Long id) {
        return Result.ok(rmaService.issueRma(id));
    }

    @Operation(summary = "Mark the RMA as returning (RETURNING)")
    @PostMapping("/{id}/returning")
    @PreAuthorize("hasAuthority('asset:rma:process')")
    @OperLog(title = "RMA退货返修", businessType = 2)
    public Result<Boolean> markReturning(@PathVariable Long id) {
        return Result.ok(rmaService.markReturning(id));
    }

    @Operation(summary = "Inspect the returned asset (INSPECTED) and update asset status")
    @PostMapping("/{id}/inspect")
    @PreAuthorize("hasAuthority('asset:rma:process')")
    @OperLog(title = "RMA退货返修", businessType = 2)
    public Result<Boolean> inspect(@PathVariable Long id,
                                   @RequestParam(required = false) String notes) {
        return Result.ok(rmaService.inspect(id, notes));
    }

    @Operation(summary = "Close the RMA ticket (CLOSED)")
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('asset:rma:close')")
    @OperLog(title = "RMA退货返修", businessType = 2)
    public Result<Boolean> close(@PathVariable Long id) {
        return Result.ok(rmaService.close(id));
    }

    @Operation(summary = "Get RMA by id")
    @GetMapping("/{id}")
    public Result<Rma> get(@PathVariable Long id) {
        return Result.ok(rmaService.getById(id));
    }

    @Operation(summary = "List RMA tickets for a project")
    @GetMapping("/by-project/{projectId}")
    public Result<List<Rma>> listByProject(@PathVariable Long projectId) {
        return Result.ok(rmaService.listByProject(projectId));
    }

    @Operation(summary = "List RMA tickets for an asset")
    @GetMapping("/by-asset/{assetId}")
    public Result<List<Rma>> listByAsset(@PathVariable Long assetId) {
        return Result.ok(rmaService.listByAsset(assetId));
    }

    @Operation(summary = "RMA KPIs (total, closed, MTTR, first-pass rate) for a date range")
    @GetMapping("/kpi")
    public Result<RmaKpiDto> kpi(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(rmaService.kpi(startDate, endDate));
    }

    @Operation(summary = "Upload fault photos for an RMA ticket")
    @PostMapping("/{id}/photos")
    @PreAuthorize("hasAuthority('asset:rma:process')")
    @OperLog(title = "RMA退货返修", businessType = 1)
    public Result<List<Attachment>> uploadPhotos(@PathVariable Long id,
                                                  @RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.ok(List.of());
        }
        List<Attachment> uploaded = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            uploaded.add(attachmentService.upload(file, ATTACHMENT_BIZ_TYPE_RMA, id));
        }
        return Result.ok(uploaded);
    }

    @Operation(summary = "List all fault photos for an RMA ticket")
    @GetMapping("/{id}/photos")
    public Result<List<Attachment>> listPhotos(@PathVariable Long id) {
        return Result.ok(attachmentService.listByBiz(ATTACHMENT_BIZ_TYPE_RMA, id));
    }

    @Operation(summary = "Delete a single RMA fault photo")
    @DeleteMapping("/photos/{attachmentId}")
    @PreAuthorize("hasAuthority('asset:rma:remove')")
    @OperLog(title = "RMA退货返修", businessType = 3)
    public Result<Boolean> deletePhoto(@PathVariable Long attachmentId) {
        return Result.ok(attachmentService.delete(attachmentId));
    }
}
