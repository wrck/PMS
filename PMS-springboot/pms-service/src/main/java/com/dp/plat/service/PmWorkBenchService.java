package com.dp.plat.service;

import com.dp.plat.model.entity.*;
import java.util.List;
import java.util.Map;

/**
 * 工作台服务 - 提供工作台首页数据
 */
public interface PmWorkBenchService extends BaseService<PmsProject> {
    List<Map<String, Object>> getTodoTasks(String username);
    List<Map<String, Object>> getDailyTasks(String username);
    List<Map<String, Object>> getHistoryTasks(String username);
    int getUnreadNotificationCount(String username);
    List<SysNotification> getNotifications(String username);
}
