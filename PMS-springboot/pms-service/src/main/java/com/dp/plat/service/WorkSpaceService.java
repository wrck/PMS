package com.dp.plat.service;

import java.util.List;
import java.util.Map;

public interface WorkSpaceService {

    /** 获取工作台仪表盘数据 */
    Map<String, Object> getDashboardData(String username);

    /** 获取待办任务列表 */
    List<Map<String, Object>> getPendingTasks(String username);

    /** 获取最近通知 */
    List<Map<String, Object>> getRecentNotifications(String username);

    /** 查询售前任务列表 */
    List<Map<String, Object>> queryPresalesTasks(String username);

    /** 查询分包任务列表 */
    List<Map<String, Object>> querySubcontractTasks(String username);

    /** 更新通知状态(标记已读) */
    void updateNotificationState(Long notificationId);
}
