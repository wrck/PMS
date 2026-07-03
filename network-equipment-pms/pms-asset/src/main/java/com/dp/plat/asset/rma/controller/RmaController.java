package com.dp.plat.asset.rma.controller;

import com.dp.plat.asset.rma.dto.RmaKpiDto;
import com.dp.plat.asset.rma.entity.Rma;
import com.dp.plat.asset.rma.service.IRmaService;
import com.dp.plat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * RMA return process controller.
 */
@Tag(name = "RMA 退货返修", description = "RMA return merchandise authorization APIs")
@RestController
@RequestMapping("/api/asset/rma")
@RequiredArgsConstructor
public class RmaController {

    private final IRmaService rmaService;

    @Operation(summary = "Register a new RMA ticket")
    @PostMapping
    public Result<Rma> create(@RequestBody Rma rma) {
        rmaService.create(rma);
        return Result.ok(rma);
    }

    @Operation(summary = "Check warranty status for the RMA asset")
    @PostMapping("/{id}/check-warranty")
    public Result<Boolean> checkWarranty(@PathVariable Long id) {
        return Result.ok(rmaService.checkWarranty(id));
    }

    @Operation(summary = "Issue the RMA (RMA_ISSUED)")
    @PostMapping("/{id}/issue")
    public Result<Boolean> issueRma(@PathVariable Long id) {
        return Result.ok(rmaService.issueRma(id));
    }

    @Operation(summary = "Mark the RMA as returning (RETURNING)")
    @PostMapping("/{id}/returning")
    public Result<Boolean> markReturning(@PathVariable Long id) {
        return Result.ok(rmaService.markReturning(id));
    }

    @Operation(summary = "Inspect the returned asset (INSPECTED) and update asset status")
    @PostMapping("/{id}/inspect")
    public Result<Boolean> inspect(@PathVariable Long id,
                                   @RequestParam(required = false) String notes) {
        return Result.ok(rmaService.inspect(id, notes));
    }

    @Operation(summary = "Close the RMA ticket (CLOSED)")
    @PostMapping("/{id}/close")
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
}
