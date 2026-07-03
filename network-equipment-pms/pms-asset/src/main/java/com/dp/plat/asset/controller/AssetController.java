package com.dp.plat.asset.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.service.IAssetService;
import com.dp.plat.common.result.Result;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<Boolean> inbound(@RequestBody Asset asset) {
        return Result.ok(assetService.inbound(asset));
    }

    @Operation(summary = "Allocate asset to a project")
    @PostMapping("/{id}/allocate")
    public Result<Boolean> allocate(@PathVariable Long id, @RequestParam Long projectId) {
        return Result.ok(assetService.allocate(id, projectId));
    }

    @Operation(summary = "Return an allocated asset")
    @PostMapping("/{id}/return")
    public Result<Boolean> returnAsset(@PathVariable Long id) {
        return Result.ok(assetService.returnAsset(id));
    }

    @Operation(summary = "Get asset by id")
    @GetMapping("/{id}")
    public Result<Asset> get(@PathVariable Long id) {
        return Result.ok(assetService.getById(id));
    }

    @Operation(summary = "Paginated asset list with filters")
    @GetMapping("/list")
    public Result<IPage<Asset>> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     Asset filter) {
        return Result.ok(assetService.list(page, size, filter));
    }

    @Operation(summary = "Update asset")
    @PutMapping
    public Result<Boolean> update(@RequestBody Asset asset) {
        return Result.ok(assetService.updateById(asset));
    }

    @Operation(summary = "Delete asset")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(assetService.removeById(id));
    }

    @Operation(summary = "Get asset lifecycle log")
    @GetMapping("/{id}/lifecycle")
    public Result<List<AssetLifecycleLog>> lifecycle(@PathVariable Long id) {
        return Result.ok(assetService.getLifecycleLog(id));
    }

    @Operation(summary = "Return all assets allocated to a project")
    @PostMapping("/return-by-project/{projectId}")
    public Result<List<Asset>> returnByProject(@PathVariable Long projectId) {
        return Result.ok(assetService.returnByProject(projectId));
    }
}
