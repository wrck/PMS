package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysDept;
import com.dp.plat.system.service.ISysDeptService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Department management controller.
 */
@Tag(name = "部门管理", description = "Department management APIs")
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final ISysDeptService sysDeptService;

    @Operation(summary = "List all departments")
    @GetMapping("/list")
    public Result<List<SysDept>> list() {
        return Result.ok(sysDeptService.list(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getOrderNum)));
    }

    @Operation(summary = "Get department by id")
    @GetMapping("/{id}")
    public Result<SysDept> get(@PathVariable Long id) {
        return Result.ok(sysDeptService.getById(id));
    }

    @Operation(summary = "Create department")
    @PostMapping
    public Result<Boolean> add(@RequestBody SysDept dept) {
        return Result.ok(sysDeptService.save(dept));
    }

    @Operation(summary = "Update department")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysDept dept) {
        return Result.ok(sysDeptService.updateById(dept));
    }

    @Operation(summary = "Delete department")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysDeptService.removeById(id));
    }
}
