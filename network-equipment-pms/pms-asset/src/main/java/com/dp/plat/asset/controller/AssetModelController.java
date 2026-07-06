package com.dp.plat.asset.controller;

import com.dp.plat.asset.entity.AssetModel;
import com.dp.plat.asset.service.IAssetModelService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Equipment model management controller.
 */
@Tag(name = "设备型号管理", description = "Equipment model management APIs")
@RestController
@RequestMapping("/api/asset/model")
@RequiredArgsConstructor
public class AssetModelController {

    private final IAssetModelService assetModelService;

    @Operation(summary = "List models with optional category filter")
    @GetMapping("/list")
    public Result<List<AssetModel>> list(@RequestParam(required = false) Long categoryId) {
        return Result.ok(assetModelService.listByCategoryId(categoryId));
    }

    @Operation(summary = "Create model")
    @PostMapping
    @PreAuthorize("hasAuthority('asset:model:add')")
    @OperLog(title = "设备型号管理", businessType = 1)
    public Result<Boolean> create(@Valid @RequestBody AssetModel model) {
        return Result.ok(assetModelService.create(model));
    }

    @Operation(summary = "Update model")
    @PutMapping
    @PreAuthorize("hasAuthority('asset:model:edit')")
    @OperLog(title = "设备型号管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody AssetModel model) {
        return Result.ok(assetModelService.update(model));
    }

    @Operation(summary = "Delete model")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:model:remove')")
    @OperLog(title = "设备型号管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(assetModelService.delete(id));
    }
}
