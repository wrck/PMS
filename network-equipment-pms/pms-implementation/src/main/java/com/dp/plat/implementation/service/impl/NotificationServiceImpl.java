package com.dp.plat.implementation.service.impl;

import com.dp.plat.implementation.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Logging-only implementation of {@link NotificationService}.
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notifyUser(Long userId, String title, String content) {
        log.info("通知用户 {}：{} - {}", userId, title, content);
    }
}
