package com.dp.plat.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工作台Mapper - 迁移自老系统 WorkSpaceDao + iBATIS XML
 *
 * 对应SQL: sql-map-work-config.xml, sql-map-subcontract-config.xml
 */
@Mapper
public interface WorkSpaceMapper {

    // ==================== 日常项目跟踪 ====================

    /** 日常项目跟踪任务列表 (query_pm_task_list) */
    List<Map<String, Object>> queryPmTaskList(@Param("params") Map<String, Object> params);

    /** 日常项目跟踪任务计数 (query_pm_task_count) */
    int countPmTaskList(@Param("params") Map<String, Object> params);

    // ==================== 项目回退/不予跟踪 ====================

    /** 项目回退确认任务 (query_project_back_task_list) */
    List<Map<String, Object>> queryProjectBackTaskList(@Param("params") Map<String, Object> params);

    /** 不予跟踪确认任务 (query_project_track_task_list) */
    List<Map<String, Object>> queryProjectTrackTaskList();

    // ==================== 回访待办 ====================

    /** 回访申请待办任务 (query_call_back_task) */
    List<Map<String, Object>> queryCallBackTaskList(@Param("params") Map<String, Object> params);

    // ==================== 售前待办 ====================

    /** 售前流程待办任务 (query_presales_task) */
    List<Map<String, Object>> queryPresalesTaskList(@Param("params") Map<String, Object> params);

    // ==================== 项目督查 ====================

    /** 项目督查任务 (queryProjectSupervisionTask) */
    List<Map<String, Object>> queryProjectSupervisionTask();

    // ==================== 个人已办 ====================

    /** 个人已办任务列表 (querySelfHistoryTaskList) */
    List<Map<String, Object>> querySelfHistoryTaskList(@Param("params") Map<String, Object> params);

    /** 个人已办任务计数 (countSelfHistoryTaskList) */
    int countSelfHistoryTaskList(@Param("params") Map<String, Object> params);

    // ==================== 技术公告任务 ====================

    /** 技术公告任务列表 (query_probTask_list) */
    List<Map<String, Object>> queryProbTaskList(@Param("params") Map<String, Object> params);

    // ==================== 分包任务 ====================

    /** 分包任务列表 (querySubcontractTaskList) */
    List<Map<String, Object>> querySubcontractTaskList(@Param("params") Map<String, Object> params);

    // ==================== 通知 ====================

    /** 项目通知列表 (query_notify_list) */
    List<Map<String, Object>> queryNotifyList(@Param("params") Map<String, Object> params);

    /** 项目通知计数 (query_notify_count) */
    int countNotifyList(@Param("params") Map<String, Object> params);

    /** 系统通知列表 (check_notification_list) */
    List<Map<String, Object>> querySystemNotificationList(@Param("username") String username);

    /** 更新系统通知状态 (update_notification_state) */
    void updateNotificationState(@Param("id") int id);
}
