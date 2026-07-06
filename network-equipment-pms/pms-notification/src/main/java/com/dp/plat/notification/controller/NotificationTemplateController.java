package com.dp.plat.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.notification.entity.NotificationTemplate;
import com.dp.plat.notification.service.INotificationTemplateService;
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

/**
 * 通知模板控制器。
 */
@Tag(name = "通知模板", description = "通知模板 CRUD 与按编码查询")
@RestController
@RequestMapping("/api/notification/template")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final INotificationTemplateService templateService;

    @Operation(summary = "分页查询通知模板")
    @GetMapping("/page")
    public Result<IPage<NotificationTemplate>> page(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return Result.ok(templateService.list(page, size));
    }

    @Operation(summary = "按 id 查询通知模板")
    @GetMapping("/{id}")
    public Result<NotificationTemplate> get(@PathVariable Long id) {
        return Result.ok(templateService.getById(id));
    }

    @Operation(summary = "按编码查询通知模板")
    @GetMapping("/code/{code}")
    public Result<NotificationTemplate> getByCode(@PathVariable String code) {
        return Result.ok(templateService.getByCode(code));
    }

    @Operation(summary = "新增通知模板")
    @PostMapping
    @PreAuthorize("hasAuthority('notification:template:add')")
    @OperLog(title = "通知模板", businessType = 1)
    public Result<NotificationTemplate> create(@Valid @RequestBody NotificationTemplate template) {
        templateService.save(template);
        return Result.ok(template);
    }

    @Operation(summary = "修改通知模板")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('notification:template:edit')")
    @OperLog(title = "通知模板", businessType = 2)
    public Result<NotificationTemplate> update(@PathVariable Long id, @Valid @RequestBody NotificationTemplate template) {
        template.setId(id);
        templateService.updateById(template);
        return Result.ok(template);
    }

    @Operation(summary = "删除通知模板")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('notification:template:remove')")
    @OperLog(title = "通知模板", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(templateService.removeById(id));
    }
}
