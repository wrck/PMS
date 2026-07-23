package com.dp.plat.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 通知中心控制器。
 */
@Tag(name = "通知中心", description = "站内通知查询、已读管理与多通道发送")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @Operation(summary = "分页查询当前用户通知")
    @GetMapping("/page")
    public Result<IPage<Notification>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String category,
                                            @RequestParam(required = false) String readStatus) {
        Notification filter = new Notification();
        filter.setUserId(SecurityUtils.getCurrentUserId());
        filter.setCategory(category);
        filter.setReadStatus(readStatus);
        return Result.ok(notificationService.list(page, size, filter));
    }

    @Operation(summary = "当前用户未读通知数")
    @GetMapping("/unread/count")
    public Result<Integer> unreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(userId == null ? 0 : notificationService.unreadCount(userId));
    }

    @Operation(summary = "标记单条通知为已读")
    @PutMapping("/{id}/read")
    @OperLog(title = "通知中心", businessType = 2)
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        return Result.ok(notificationService.markAsRead(id));
    }

    @Operation(summary = "当前用户通知全部已读")
    @PutMapping("/read/all")
    @OperLog(title = "通知中心", businessType = 2)
    public Result<Boolean> markAllRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.ok(false);
        }
        return Result.ok(notificationService.markAllRead(userId));
    }

    @Operation(summary = "管理员手动发送通知（调试用）")
    @PostMapping("/send")
    @PreAuthorize("@ss.hasPermission('notification:notification:send')")
    @OperLog(title = "通知中心", businessType = 1)
    public Result<Void> send(@Valid @RequestBody Notification notification,
                             @RequestParam(required = false) Set<String> channels) {
        if (channels == null || channels.isEmpty()) {
            channels = Set.of("IN_APP");
        }
        notificationService.multiChannelSend(notification, channels);
        return Result.ok();
    }
}
