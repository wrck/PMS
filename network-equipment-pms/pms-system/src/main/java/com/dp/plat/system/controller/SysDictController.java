package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysDict;
import com.dp.plat.system.entity.SysDictItem;
import com.dp.plat.system.service.ISysDictService;
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
 * Dictionary management controller.
 */
@Tag(name = "字典管理", description = "Dictionary management APIs")
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictService sysDictService;

    @Operation(summary = "Paginated dict query")
    @GetMapping("/page")
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
    public Result<SysDict> get(@PathVariable Long id) {
        return Result.ok(sysDictService.getById(id));
    }

    @Operation(summary = "Create dict")
    @PostMapping
    public Result<Boolean> add(@RequestBody SysDict dict) {
        return Result.ok(sysDictService.save(dict));
    }

    @Operation(summary = "Update dict")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysDict dict) {
        return Result.ok(sysDictService.updateById(dict));
    }

    @Operation(summary = "Delete dict")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysDictService.removeById(id));
    }

    @Operation(summary = "List dict items by dict type")
    @GetMapping("/items/{dictType}")
    public Result<List<SysDictItem>> listItems(@PathVariable String dictType) {
        return Result.ok(sysDictService.listItemsByDictType(dictType));
    }
}
