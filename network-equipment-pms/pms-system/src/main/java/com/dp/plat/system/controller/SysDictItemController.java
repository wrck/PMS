package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysDictItem;
import com.dp.plat.system.service.ISysDictItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
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
 * <p><b>@Deprecated</b>：字典数据管理已由 yudao 底座提供，替代接口为 {@code /admin-api/system/dict-data/*}（yudao DictDataController）。</p>
 *
 * <p>Dictionary item management controller.</p>
 */
@Deprecated
@Tag(name = "字典项管理（已弃用）", description = "Dictionary item management APIs")
@RestController
@RequestMapping("/api/system/dict/item")
@RequiredArgsConstructor
public class SysDictItemController {

    private final ISysDictItemService sysDictItemService;

    @Operation(summary = "List dict items by dict id")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public Result<List<SysDictItem>> list(@RequestParam("dictId") Long dictId) {
        return Result.ok(sysDictItemService.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, dictId)
                .orderByAsc(SysDictItem::getSortOrder)));
    }

    @Operation(summary = "Create dict item")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @OperLog(title = "字典管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody SysDictItem item) {
        return Result.ok(sysDictItemService.create(item));
    }

    @Operation(summary = "Update dict item")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @OperLog(title = "字典管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody SysDictItem item) {
        return Result.ok(sysDictItemService.update(item));
    }

    @Operation(summary = "Delete dict item")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperLog(title = "字典管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysDictItemService.deleteById(id));
    }
}
