package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysOperateLog;
import com.dp.plat.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 操作日志控制器 - 迁移自老系统 OperateLogAction
 */
@RestController
@RequestMapping("/api/system/operate-log")
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    /** 查询操作日志列表 */
    @GetMapping("/list")
    public R<IPage<SysOperateLog>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String operateType,
                                        @RequestParam(required = false) String operatePerson) {
        return R.ok(operateLogService.queryPage(pageNum, pageSize));
    }

    /** 导出操作日志Excel */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String operateType,
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate) {
        operateLogService.exportLog(response, operateType, startDate, endDate);
    }

    /** 手动触发Quartz任务 */
    @PostMapping("/sync-task")
    public R<Void> syncTask(@RequestParam String taskName) {
        operateLogService.triggerSyncTask(taskName);
        return R.ok();
    }
}
