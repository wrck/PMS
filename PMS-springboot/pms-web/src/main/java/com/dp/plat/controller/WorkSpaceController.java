package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.WorkSpaceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspace")
public class WorkSpaceController {
    @Autowired private WorkSpaceService workSpaceService;

    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        return R.ok(workSpaceService.getDashboardData(username));
    }

    @GetMapping("/pending-tasks")
    public R<List<Map<String, Object>>> pendingTasks(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        return R.ok(workSpaceService.getPendingTasks(username));
    }

    @GetMapping("/notifications")
    public R<List<Map<String, Object>>> notifications(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        return R.ok(workSpaceService.getRecentNotifications(username));
    }
}
