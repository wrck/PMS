package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.ExceptionLog;
import com.dp.plat.system.entity.LoginLog;
import com.dp.plat.system.entity.ScheduleLog;
import com.dp.plat.system.service.IExceptionLogService;
import com.dp.plat.system.service.ILoginLogService;
import com.dp.plat.system.service.IScheduleLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志控制器：登录日志、异常日志、定时任务日志查询。
 */
@Tag(name = "审计日志", description = "Audit log APIs")
@RestController
@RequestMapping("/api/system/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final ILoginLogService loginLogService;
    private final IExceptionLogService exceptionLogService;
    private final IScheduleLogService scheduleLogService;

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/login/page")
    public Result<IPage<LoginLog>> loginPage(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String username,
                                             @RequestParam(required = false) String status) {
        LoginLog filter = new LoginLog();
        filter.setUsername(username);
        filter.setStatus(status);
        return Result.ok(loginLogService.page(page, size, filter));
    }

    @Operation(summary = "分页查询异常日志")
    @GetMapping("/exception/page")
    public Result<IPage<ExceptionLog>> exceptionPage(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) String username,
                                                     @RequestParam(required = false) String requestUri) {
        ExceptionLog filter = new ExceptionLog();
        filter.setUsername(username);
        filter.setRequestUri(requestUri);
        return Result.ok(exceptionLogService.page(page, size, filter));
    }

    @Operation(summary = "分页查询定时任务日志")
    @GetMapping("/schedule/page")
    public Result<IPage<ScheduleLog>> schedulePage(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String taskName,
                                                   @RequestParam(required = false) String status) {
        ScheduleLog filter = new ScheduleLog();
        filter.setTaskName(taskName);
        filter.setStatus(status);
        return Result.ok(scheduleLogService.page(page, size, filter));
    }

    @Operation(summary = "查询失败的定时任务列表")
    @GetMapping("/schedule/failed")
    public Result<IPage<ScheduleLog>> scheduleFailed(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return Result.ok(scheduleLogService.listFailed(page, size));
    }
}
