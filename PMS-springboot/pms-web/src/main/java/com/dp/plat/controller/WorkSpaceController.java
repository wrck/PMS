package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.WorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspace")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /** 工作台仪表盘 */
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard(@RequestParam String username) {
        return R.ok(workSpaceService.getDashboardData(username));
    }

    /** 待办任务 */
    @GetMapping("/pending-tasks")
    public R<List<Map<String, Object>>> pendingTasks(@RequestParam String username) {
        return R.ok(workSpaceService.getPendingTasks(username));
    }

    /** 最近通知 */
    @GetMapping("/notifications")
    public R<List<Map<String, Object>>> notifications(@RequestParam String username) {
        return R.ok(workSpaceService.getRecentNotifications(username));
    }

    /** 售前任务列表 */
    @GetMapping("/presales-tasks")
    public R<List<Map<String, Object>>> presalesTasks(@RequestParam String username) {
        return R.ok(workSpaceService.queryPresalesTasks(username));
    }

    /** 分包任务列表 */
    @GetMapping("/subcontract-tasks")
    public R<List<Map<String, Object>>> subcontractTasks(@RequestParam String username) {
        return R.ok(workSpaceService.querySubcontractTasks(username));
    }

    /** 标记通知已读 */
    @PostMapping("/notification/{id}/read")
    public R<Void> markNotificationRead(@PathVariable Long id) {
        workSpaceService.updateNotificationState(id);
        return R.ok();
    }
}
