package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysDict;
import com.dp.plat.system.entity.SysDictItem;
import com.dp.plat.system.service.ISysDictService;
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
 * <p><b>@Deprecated</b>：字典类型管理已由 yudao 底座提供，替代接口为 {@code /admin-api/system/dict-type/*}（yudao DictTypeController）。</p>
 *
 * <p>Dictionary management controller.</p>
 */
@Deprecated
@Tag(name = "字典管理（已弃用）", description = "Dictionary management APIs")
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictService sysDictService;

    @Operation(summary = "Paginated dict query")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public Result<Page<SysDict>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String dictName) {
        Page<SysDict> page = sysDictService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysDict>().like(dictName != null, SysDict::getDictName, dictName));
        return Result.ok(page);
    }

    @Operation(summary = "Get dict by id")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public Result<SysDict> get(@PathVariable Long id) {
        return Result.ok(sysDictService.getById(id));
    }

    @Operation(summary = "Create dict")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @OperLog(title = "字典管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody SysDict dict) {
        return Result.ok(sysDictService.save(dict));
    }

    @Operation(summary = "Update dict")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @OperLog(title = "字典管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody SysDict dict) {
        return Result.ok(sysDictService.updateById(dict));
    }

    @Operation(summary = "Delete dict")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperLog(title = "字典管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysDictService.removeById(id));
    }

    @Operation(summary = "List dict items by dict type")
    @GetMapping("/items/{dictType}")
    public Result<List<SysDictItem>> listItems(@PathVariable String dictType) {
        return Result.ok(sysDictService.listItemsByDictType(dictType));
    }
}
