package com.dp.plat.lowcode.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.engine.trigger.LowCodeTrigger;
import com.dp.plat.lowcode.service.LowCodeTriggerService;
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
 * 低代码触发器 Controller。
 *
 * <p>提供触发器 CRUD 与手动执行接口。写操作需对应权限，并记录操作日志。</p>
 */
@Tag(name = "低代码触发器", description = "LowCode trigger APIs")
@RestController
@RequestMapping("/api/lowcode/trigger")
@RequiredArgsConstructor
public class LowCodeTriggerController {

    private final LowCodeTriggerService triggerService;

    @Operation(summary = "触发器列表")
    @GetMapping
    @PreAuthorize("hasAuthority('lowcode:trigger:list')")
    public Result<List<LowCodeTrigger>> list() {
        return Result.ok(triggerService.list());
    }

    @Operation(summary = "触发器详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('lowcode:trigger:list')")
    public Result<LowCodeTrigger> get(@PathVariable Long id) {
        return Result.ok(triggerService.getById(id));
    }

    @Operation(summary = "保存触发器")
    @PostMapping
    @PreAuthorize("hasAuthority('lowcode:trigger:edit')")
    @OperLog(title = "低代码触发器", businessType = 1)
    public Result<LowCodeTrigger> save(@RequestBody LowCodeTrigger trigger) {
        triggerService.saveOrUpdate(trigger);
        return Result.ok(trigger);
    }

    @Operation(summary = "删除触发器")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('lowcode:trigger:edit')")
    @OperLog(title = "低代码触发器", businessType = 3)
    public Result<Void> delete(@PathVariable Long id) {
        triggerService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "手动执行触发器")
    @PostMapping("/{code}/execute")
    @PreAuthorize("hasAuthority('lowcode:trigger:edit')")
    public Result<Map<String, Object>> execute(@PathVariable String code,
                                               @RequestBody(required = false) Map<String, Object> data) {
        return Result.ok(triggerService.executeTrigger(code, data == null ? Map.of() : data));
    }
}
