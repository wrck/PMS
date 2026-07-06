package com.dp.plat.asset.warranty.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.asset.warranty.entity.Warranty;
import com.dp.plat.asset.warranty.service.IWarrantyService;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Warranty management controller.
 */
@Tag(name = "质保管理", description = "Warranty management APIs")
@RestController
@RequestMapping("/api/asset/warranty")
@RequiredArgsConstructor
public class WarrantyController {

    private final IWarrantyService warrantyService;

    @Operation(summary = "Paginated warranty query")
    @GetMapping("/list")
    public Result<Page<Warranty>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       Warranty filters) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Warranty> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Warranty>()
                .eq(filters != null && filters.getAssetId() != null, Warranty::getAssetId, filters == null ? null : filters.getAssetId())
                .eq(filters != null && filters.getProjectId() != null, Warranty::getProjectId, filters == null ? null : filters.getProjectId())
                .eq(filters != null && filters.getContractNo() != null, Warranty::getContractNo, filters == null ? null : filters.getContractNo())
                .orderByDesc(Warranty::getId);
        return Result.ok(warrantyService.page(new Page<>(page, size), wrapper));
    }

    @Operation(summary = "Get warranty by id")
    @GetMapping("/{id}")
    public Result<Warranty> get(@PathVariable Long id) {
        return Result.ok(warrantyService.getById(id));
    }

    @Operation(summary = "Create warranty")
    @PostMapping
    @PreAuthorize("hasAuthority('asset:warranty:add')")
    @OperLog(title = "质保管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody Warranty warranty) {
        return Result.ok(warrantyService.save(warranty));
    }

    @Operation(summary = "Update warranty")
    @PutMapping
    @PreAuthorize("hasAuthority('asset:warranty:edit')")
    @OperLog(title = "质保管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody Warranty warranty) {
        return Result.ok(warrantyService.updateById(warranty));
    }

    @Operation(summary = "Delete warranty")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:warranty:remove')")
    @OperLog(title = "质保管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(warrantyService.removeById(id));
    }

    @Operation(summary = "List warranties for an asset")
    @GetMapping("/by-asset/{assetId}")
    public Result<List<Warranty>> listByAsset(@PathVariable Long assetId) {
        return Result.ok(warrantyService.listByAsset(assetId));
    }

    @Operation(summary = "List warranties for a project")
    @GetMapping("/by-project/{projectId}")
    public Result<List<Warranty>> listByProject(@PathVariable Long projectId) {
        return Result.ok(warrantyService.listByProject(projectId));
    }

    @Operation(summary = "List warranties expiring within the given days")
    @GetMapping("/expiring-soon")
    public Result<List<Warranty>> listExpiringSoon(@RequestParam(defaultValue = "30") int days) {
        return Result.ok(warrantyService.listExpiringSoon(days));
    }

    @Operation(summary = "Check whether an asset is in warranty on a given date")
    @GetMapping("/in-warranty/{assetId}")
    public Result<Boolean> isInWarranty(@PathVariable Long assetId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.ok(warrantyService.isInWarranty(assetId, date));
    }

    @Operation(summary = "Initialize warranty records for all assets of a project")
    @PostMapping("/init-for-project")
    @PreAuthorize("hasAuthority('asset:warranty:add')")
    @OperLog(title = "质保管理", businessType = 1)
    public Result<Boolean> initForProject(@RequestParam Long projectId,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finalAcceptanceDate,
                                          @RequestParam(required = false) Integer durationMonths) {
        warrantyService.initWarrantyForProject(projectId, finalAcceptanceDate, durationMonths);
        return Result.ok(true);
    }
}
