package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysNotificationMapper;
import com.dp.plat.model.entity.SysNotification;
import com.dp.plat.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 通知服务 - 迁移自老系统 NotificationService
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SysNotificationMapper notificationMapper;

    @Override
    @Transactional
    public void sendNotification(String templateCode, Long projectId, String projectName,
                                  String receiver, String title, String content, String createBy) {
        SysNotification notification = new SysNotification();
        notification.setTemplateCode(templateCode);
        notification.setProjectId(projectId);
        notification.setProjectName(projectName);
        notification.setReceiver(receiver);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateBy(createBy);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public IPage<SysNotification> queryMyNotifications(String username, Integer pageNum, Integer pageSize) {
        Page<SysNotification> page = new Page<>(pageNum, pageSize);
        return notificationMapper.selectPage(page,
                new LambdaQueryWrapper<SysNotification>()
                        .eq(SysNotification::getReceiver, username)
                        .orderByDesc(SysNotification::getCreateTime));
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        SysNotification notification = notificationMapper.selectById(id);
        if (notification != null) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public int countUnread(String username) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<SysNotification>()
                        .eq(SysNotification::getReceiver, username)
                        .eq(SysNotification::getIsRead, 0)).intValue();
    }
}
