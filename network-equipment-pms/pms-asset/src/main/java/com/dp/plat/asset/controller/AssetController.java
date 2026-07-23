package com.dp.plat.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.asset.dto.AssetExportDTO;
import com.dp.plat.asset.dto.AssetImportDTO;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.service.IAssetService;
import com.dp.plat.common.annotation.Idempotent;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.excel.ExcelImportResult;
import com.dp.plat.common.excel.ExcelUtils;
import com.dp.plat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
 * Equipment asset management controller.
 */
@Tag(name = "设备资产管理", description = "Equipment asset management APIs")
@RestController
@RequestMapping("/api/asset")
@RequiredArgsConstructor
public class AssetController {

    private final IAssetService assetService;

    @Operation(summary = "Inbound a new asset")
    @PostMapping("/inbound")
    @PreAuthorize("@ss.hasPermission('asset:asset:add')")
    @OperLog(title = "设备资产管理", businessType = 1)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 20, refillTokens = 20, refillPeriodSeconds = 60)
    @Idempotent
    public Result<Boolean> inbound(@Valid @RequestBody Asset asset) {
        return Result.ok(assetService.inbound(asset));
    }

    @Operation(summary = "Allocate asset to a project")
    @PostMapping("/{id}/allocate")
    @PreAuthorize("@ss.hasPermission('asset:asset:allocate')")
    @OperLog(title = "设备资产管理", businessType = 2)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 20, refillTokens = 20, refillPeriodSeconds = 60)
    public Result<Boolean> allocate(@PathVariable Long id, @RequestParam Long projectId) {
        return Result.ok(assetService.allocate(id, projectId));
    }

    @Operation(summary = "Return an allocated asset")
    @PostMapping("/{id}/return")
    @PreAuthorize("@ss.hasPermission('asset:asset:return')")
    @OperLog(title = "设备资产管理", businessType = 2)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 20, refillTokens = 20, refillPeriodSeconds = 60)
    public Result<Boolean> returnAsset(@PathVariable Long id) {
        return Result.ok(assetService.returnAsset(id));
    }

    @Operation(summary = "Get asset by id")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('asset:asset:list')")
    public Result<Asset> get(@PathVariable Long id) {
        return Result.ok(assetService.getById(id));
    }

    @Operation(summary = "Paginated asset list with filters")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('asset:asset:list')")
    public Result<IPage<Asset>> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     Asset filter) {
        return Result.ok(assetService.list(page, size, filter));
    }

    @Operation(summary = "Update asset")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('asset:asset:edit')")
    @OperLog(title = "设备资产管理", businessType = 2)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 30, refillTokens = 30, refillPeriodSeconds = 60)
    public Result<Boolean> update(@Valid @RequestBody Asset asset) {
        return Result.ok(assetService.updateById(asset));
    }

    @Operation(summary = "Delete asset")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('asset:asset:remove')")
    @OperLog(title = "设备资产管理", businessType = 3)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(assetService.removeById(id));
    }

    @Operation(summary = "Get asset lifecycle log")
    @GetMapping("/{id}/lifecycle")
    @PreAuthorize("@ss.hasPermission('asset:asset:list')")
    public Result<List<AssetLifecycleLog>> lifecycle(@PathVariable Long id) {
        return Result.ok(assetService.getLifecycleLog(id));
    }

    @Operation(summary = "Return all assets allocated to a project")
    @PostMapping("/return-by-project/{projectId}")
    @PreAuthorize("@ss.hasPermission('asset:asset:return')")
    @OperLog(title = "设备资产管理", businessType = 2)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    public Result<List<Asset>> returnByProject(@PathVariable Long projectId) {
        return Result.ok(assetService.returnByProject(projectId));
    }

    @Operation(summary = "Download asset import template")
    @GetMapping("/template")
    @PreAuthorize("@ss.hasPermission('asset:asset:list')")
    public void template(HttpServletResponse response) {
        ExcelUtils.exportTemplate(response, "asset-template", "资产导入模板", AssetImportDTO.class);
    }

    @Operation(summary = "Export asset list to Excel")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('asset:asset:export')")
    public void export(HttpServletResponse response, Asset filter) {
        List<Asset> rows = filter == null
                ? assetService.list(new LambdaQueryWrapper<Asset>().orderByDesc(Asset::getId))
                : assetService.list(buildExportWrapper(filter));
        List<AssetExportDTO> data = rows.stream().map(a -> {
            AssetExportDTO dto = new AssetExportDTO();
            BeanUtils.copyProperties(a, dto);
            return dto;
        }).toList();
        ExcelUtils.export(response, "asset-list", "资产清单", AssetExportDTO.class, data);
    }

    @Operation(summary = "Batch import assets from Excel")
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermission('asset:asset:import')")
    @OperLog(title = "设备资产管理", businessType = 5)
    @RateLimit(key = "T(com.dp.plat.common.util.SecurityUtils).getCurrentUserId()", capacity = 5, refillTokens = 5, refillPeriodSeconds = 60)
    public Result<ExcelImportResult<AssetImportDTO>> importExcel(@RequestParam("file") MultipartFile file) {
        return Result.ok(assetService.batchImport(file));
    }

    /**
     * Build a {@link LambdaQueryWrapper} mirroring the list filter for export.
     *
     * @param filter filter bean
     * @return wrapper with the supported conditions applied
     */
    private LambdaQueryWrapper<Asset> buildExportWrapper(Asset filter) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        if (filter.getSerialNo() != null) {
            wrapper.like(Asset::getSerialNo, filter.getSerialNo());
        }
        if (filter.getAssetName() != null) {
            wrapper.like(Asset::getAssetName, filter.getAssetName());
        }
        if (filter.getStatus() != null) {
            wrapper.eq(Asset::getStatus, filter.getStatus());
        }
        if (filter.getCategoryId() != null) {
            wrapper.eq(Asset::getCategoryId, filter.getCategoryId());
        }
        if (filter.getModelId() != null) {
            wrapper.eq(Asset::getModelId, filter.getModelId());
        }
        if (filter.getProjectId() != null) {
            wrapper.eq(Asset::getProjectId, filter.getProjectId());
        }
        wrapper.orderByDesc(Asset::getId);
        return wrapper;
    }
}
