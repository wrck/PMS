package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysNotification;
import com.dp.plat.service.PmWorkBenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作台控制器 - 迁移自老系统 WorkSpaceAction
 */
@RestController
@RequestMapping("/api/workbench")
public class PmWorkBenchController {

    @Autowired
    private PmWorkBenchService workBenchService;

    /** 获取待办任务 */
    @GetMapping("/todo-tasks")
    public R<List<Map<String, Object>>> todoTasks(@RequestParam String username) {
        return R.ok(workBenchService.getTodoTasks(username));
    }

    /** 获取日常项目跟踪 */
    @GetMapping("/daily-tasks")
    public R<List<Map<String, Object>>> dailyTasks(@RequestParam String username) {
        return R.ok(workBenchService.getDailyTasks(username));
    }

    /** 获取已办理任务 */
    @GetMapping("/history-tasks")
    public R<List<Map<String, Object>>> historyTasks(@RequestParam String username) {
        return R.ok(workBenchService.getHistoryTasks(username));
    }

    /** 获取未读通知数量 */
    @GetMapping("/unread-count")
    public R<Integer> unreadCount(@RequestParam String username) {
        return R.ok(workBenchService.getUnreadNotificationCount(username));
    }

    /** 获取系统通知列表 */
    @GetMapping("/notifications")
    public R<List<SysNotification>> notifications(@RequestParam String username) {
        return R.ok(workBenchService.getNotifications(username));
    }
}
