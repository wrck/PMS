package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.SysNotification;

import java.util.List;

public interface NotificationService {
    void sendNotification(String templateCode, Long projectId, String projectName,
                          String receiver, String title, String content, String createBy);
    IPage<SysNotification> queryMyNotifications(String username, Integer pageNum, Integer pageSize);
    void markAsRead(Long id);
    int countUnread(String username);
}
