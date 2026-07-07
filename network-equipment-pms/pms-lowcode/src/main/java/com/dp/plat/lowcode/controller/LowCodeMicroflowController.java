package com.dp.plat.lowcode.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.entity.LowCodeMicroflow;
import com.dp.plat.lowcode.service.LowCodeMicroflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 低代码微流 Controller。
 *
 * <p>提供微流 CRUD 与执行接口。写操作需对应权限，并记录操作日志。</p>
 */
@Tag(name = "低代码微流", description = "LowCode microflow APIs")
@RestController
@RequestMapping("/api/lowcode/microflow")
@RequiredArgsConstructor
public class LowCodeMicroflowController {

    private final LowCodeMicroflowService microflowService;

    @Operation(summary = "微流列表")
    @GetMapping
    @PreAuthorize("hasAuthority('lowcode:microflow:list')")
    public Result<List<LowCodeMicroflow>> list() {
        return Result.ok(microflowService.list());
    }

    @Operation(summary = "微流详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('lowcode:microflow:list')")
    public Result<LowCodeMicroflow> get(@PathVariable Long id) {
        return Result.ok(microflowService.getById(id));
    }

    @Operation(summary = "保存微流")
    @PostMapping
    @PreAuthorize("hasAuthority('lowcode:microflow:edit')")
    @OperLog(title = "低代码微流", businessType = 1)
    public Result<LowCodeMicroflow> save(@RequestBody LowCodeMicroflow microflow) {
        microflowService.saveOrUpdate(microflow);
        return Result.ok(microflow);
    }

    @Operation(summary = "删除微流")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('lowcode:microflow:edit')")
    @OperLog(title = "低代码微流", businessType = 3)
    public Result<Void> delete(@PathVariable Long id) {
        microflowService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "执行微流")
    @PostMapping("/{code}/execute")
    @PreAuthorize("hasAuthority('lowcode:microflow:exec')")
    public Result<Map<String, Object>> execute(@PathVariable String code,
                                               @RequestBody(required = false) Map<String, Object> inputs) {
        return Result.ok(microflowService.execute(code, inputs == null ? Map.of() : inputs));
    }
}
