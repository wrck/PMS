package com.dp.plat.asset.controller;

import com.dp.plat.asset.entity.AssetCategory;
import com.dp.plat.asset.service.IAssetCategoryService;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
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
 * Equipment category management controller.
 */
@Tag(name = "设备分类管理", description = "Equipment category management APIs")
@RestController
@RequestMapping("/api/asset/category")
@RequiredArgsConstructor
public class AssetCategoryController {

    private final IAssetCategoryService assetCategoryService;

    @Operation(summary = "Get category tree")
    @GetMapping("/tree")
    public Result<List<AssetCategory>> tree() {
        return Result.ok(assetCategoryService.getTree());
    }

    @Operation(summary = "Create category")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('asset:category:add')")
    @OperLog(title = "设备分类管理", businessType = 1)
    public Result<Boolean> create(@Valid @RequestBody AssetCategory category) {
        return Result.ok(assetCategoryService.create(category));
    }

    @Operation(summary = "Update category")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('asset:category:edit')")
    @OperLog(title = "设备分类管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody AssetCategory category) {
        return Result.ok(assetCategoryService.update(category));
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('asset:category:remove')")
    @OperLog(title = "设备分类管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(assetCategoryService.delete(id));
    }
}
