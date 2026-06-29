package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysNotification;
import com.dp.plat.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/list")
    public R<IPage<SysNotification>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        return R.ok(notificationService.queryMyNotifications(username, pageNum, pageSize));
    }

    @GetMapping("/unread-count")
    public R<Integer> unreadCount(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        return R.ok(notificationService.countUnread(username));
    }

    @PostMapping("/{id}/read")
    public R<Void> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return R.ok();
    }
}
