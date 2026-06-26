package com.dp.plat.service;

import java.util.List;
import java.util.Map;

public interface WorkSpaceService {
    Map<String, Object> getDashboardData(String username);
    List<Map<String, Object>> getPendingTasks(String username);
    List<Map<String, Object>> getRecentNotifications(String username);
}
