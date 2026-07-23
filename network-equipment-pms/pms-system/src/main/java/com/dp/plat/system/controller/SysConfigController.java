package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysConfig;
import com.dp.plat.system.service.ISysConfigService;
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

/**
 * System parameter configuration controller.
 */
@Tag(name = "参数配置", description = "System parameter configuration APIs")
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService sysConfigService;

    @Operation(summary = "Paginated config query")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:config:list')")
    public Result<Page<SysConfig>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String configName) {
        return Result.ok(sysConfigService.selectPage(pageNum, pageSize, configName));
    }

    @Operation(summary = "Get config by id")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:config:list')")
    public Result<SysConfig> get(@PathVariable Long id) {
        return Result.ok(sysConfigService.getById(id));
    }

    @Operation(summary = "Get config by config key")
    @GetMapping("/key/{configKey}")
    public Result<SysConfig> getByKey(@PathVariable String configKey) {
        return Result.ok(sysConfigService.getByConfigKey(configKey));
    }

    @Operation(summary = "Create config")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:config:add')")
    @OperLog(title = "参数配置", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody SysConfig config) {
        return Result.ok(sysConfigService.create(config));
    }

    @Operation(summary = "Update config")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:config:edit')")
    @OperLog(title = "参数配置", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody SysConfig config) {
        return Result.ok(sysConfigService.update(config));
    }

    @Operation(summary = "Delete config")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:config:remove')")
    @OperLog(title = "参数配置", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysConfigService.deleteById(id));
    }
}
