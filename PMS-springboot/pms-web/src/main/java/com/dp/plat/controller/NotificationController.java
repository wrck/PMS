package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysNotification;
import com.dp.plat.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统通知控制器 - 迁移自老系统 WorkSpaceAction.notice()
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /** 查询通知列表 */
    @GetMapping("/list")
    public R<IPage<SysNotification>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String username) {
        return R.ok(notificationService.queryPage(pageNum, pageSize));
    }

    /** 获取未读通知数量 */
    @GetMapping("/unread-count")
    public R<Integer> unreadCount(@RequestParam String username) {
        return R.ok(notificationService.getUnreadCount(username));
    }

    /** 标记通知为已读 */
    @PutMapping("/{id}/read")
    public R<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return R.ok();
    }

    /** 标记所有通知为已读 */
    @PutMapping("/read-all")
    public R<Void> markAllAsRead(@RequestParam String username) {
        notificationService.markAllAsRead(username);
        return R.ok();
    }

    /** 删除通知 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return R.ok();
    }
}
