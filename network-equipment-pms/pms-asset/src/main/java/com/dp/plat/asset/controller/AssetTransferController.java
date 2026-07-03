package com.dp.plat.asset.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.asset.entity.AssetTransfer;
import com.dp.plat.asset.service.IAssetTransferService;
import com.dp.plat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Equipment transfer management controller.
 */
@Tag(name = "设备调拨管理", description = "Equipment transfer management APIs")
@RestController
@RequestMapping("/api/asset/transfer")
@RequiredArgsConstructor
public class AssetTransferController {

    private final IAssetTransferService assetTransferService;

    @Operation(summary = "Apply for a transfer")
    @PostMapping("/apply")
    public Result<Boolean> apply(@RequestBody AssetTransfer transfer) {
        return Result.ok(assetTransferService.apply(transfer));
    }

    @Operation(summary = "Approve a transfer")
    @PostMapping("/{id}/approve")
    public Result<Boolean> approve(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        return Result.ok(assetTransferService.approve(id, opinion));
    }

    @Operation(summary = "Reject a transfer")
    @PostMapping("/{id}/reject")
    public Result<Boolean> reject(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        return Result.ok(assetTransferService.reject(id, opinion));
    }

    @Operation(summary = "Paginated transfer list with filters")
    @GetMapping("/list")
    public Result<IPage<AssetTransfer>> list(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             AssetTransfer filter) {
        return Result.ok(assetTransferService.list(page, size, filter));
    }
}
