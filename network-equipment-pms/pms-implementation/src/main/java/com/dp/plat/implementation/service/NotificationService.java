package com.dp.plat.implementation.service;

/**
 * Notification service for sending in-app messages to users.
 *
 * <p>Currently a placeholder that only logs the notification; later backed by
 * a notification table / message queue.</p>
 */
public interface NotificationService {

    /**
     * Notify a single user with the given title and content.
     *
     * @param userId  target user id
     * @param title   notification title
     * @param content notification content
     */
    void notifyUser(Long userId, String title, String content);
}
