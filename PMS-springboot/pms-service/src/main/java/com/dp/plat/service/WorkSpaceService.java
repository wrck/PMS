package com.dp.plat.service;

import java.util.List;
import java.util.Map;

/**
 * 工作台服务接口 - 迁移自老系统 WorkSpaceService
 *
 * 对应老系统: WorkSpaceAction (8个Action方法)
 * 迁移策略: 将Struts的tab-based页面转为REST API端点
 */
public interface WorkSpaceService {

    // ==================== 仪表盘 ====================

    /** 获取工作台仪表盘数据 */
    Map<String, Object> getDashboardData(String username);

    // ==================== 日常项目跟踪 ====================

    /**
     * 日常项目跟踪任务列表
     * 迁移自: WorkSpaceAction.execute() -> workspaceService.queryPmTaskList()
     *
     * 业务逻辑:
     * 1. 根据当前用户角色过滤:
     *    - 工程管理部/管理员: 查看全部
     *    - 服务经理: 按serviceManager过滤
     *    - 项目经理: 按programManager过滤
     *    - 其他角色: 返回空列表
     * 2. 查询条件: 项目状态IN(30,31,32), 计划状态!=48, 任务日期<7天或为空
     * 3. 支持分页+项目名/办事处/经理筛选
     */
    List<Map<String, Object>> queryDailyTaskList(Map<String, Object> params);

    /** 日常项目跟踪任务计数 */
    int countDailyTaskList(Map<String, Object> params);

    // ==================== 业务流程办理 ====================

    /**
     * 业务流程办理任务列表(合并多种待办)
     * 迁移自: WorkSpaceAction.task()
     *
     * 合并以下待办任务:
     * 1. 闭环流程待办 (queryPmCLTaskList) - 依赖Activiti
     * 2. 回访申请待办 (queryCallBackTaskList) - 依赖Activiti
     * 3. 项目回退确认 (queryProjectBackTaskList) - 本地表
     * 4. 不予跟踪确认 (queryProjectTrackTaskList) - 本地表
     * 5. 项目督查任务 (queryProjectSupervisionTask) - 本地表
     * 6. 售前流程待办 (queryPresalesTaskList) - 依赖Activiti
     *
     * @param procKey 流程类型过滤(为空则查询全部)
     * @param username 当前用户名
     */
    List<Map<String, Object>> queryBusinessTaskList(String procKey, String username);

    // ==================== 系统通知 ====================

    /**
     * 系统通知列表
     * 迁移自: WorkSpaceAction.notice() -> workspaceService.queryNotifyList()
     *
     * 业务逻辑:
     * 1. 根据当前用户角色过滤(同日常项目跟踪)
     * 2. 支持分页+项目名/办事处/经理筛选
     */
    List<Map<String, Object>> queryNotifyList(Map<String, Object> params);

    /** 项目通知计数 */
    int countNotifyList(Map<String, Object> params);

    /** 查询用户系统通知(未读优先) */
    List<Map<String, Object>> querySystemNotifications(String username);

    // ==================== 个人已办 ====================

    /**
     * 个人已办任务列表
     * 迁移自: WorkSpaceAction.hisselftask() -> workspaceService.querySelfHistoryTaskList()
     *
     * 查询当前用户已办理的:
     * 1. 闭环流程已办任务
     * 2. 回访流程已办任务
     */
    List<Map<String, Object>> querySelfHistoryTaskList(Map<String, Object> params);

    /** 个人已办任务计数 */
    int countSelfHistoryTaskList(Map<String, Object> params);

    // ==================== 技术公告任务 ====================

    /**
     * 技术公告任务列表
     * 迁移自: WorkSpaceAction.probTask() -> workspaceService.queryProbTaskList()
     *
     * 业务逻辑:
     * 1. 技术公告管理员(isProbAdmin=1): 查看所有非关闭公告
     * 2. 技术支持人员(isProbAdmin=2): 查看状态4,5的公告
     * 3. 研发人员(isProbAdmin=3): 查看自己跟踪的状态6公告
     * 4. 普通用户(isProbAdmin=0): 查看个人任务+通告
     */
    List<Map<String, Object>> queryProbTaskList(String username);

    // ==================== 分包任务 ====================

    /**
     * 分包任务列表
     * 迁移自: WorkSpaceAction.subcontractTask() -> workspaceService.querySubcontractTaskList()
     *
     * 业务逻辑(按角色分组查询):
     * 1. 工程管理部/工程管理部领导: emRole + emlRole
     * 2. 回访人员: cbRole
     * 3. 区域主管: zrRole (检查利润部门)
     * 4. 服务经理: smRole + profitSmRole + parentSmRole (含被驳回项目)
     * 5. 财务人员: role_财务
     */
    List<Map<String, Object>> querySubcontractTaskList(Map<String, Object> params);

    // ==================== 待办任务(简化版) ====================

    /** 获取待办任务列表(简化版,不过滤角色) */
    List<Map<String, Object>> getPendingTasks(String username);

    /** 获取最近通知(简化版) */
    List<Map<String, Object>> getRecentNotifications(String username);

    /** 查询售前任务列表(简化版) */
    List<Map<String, Object>> queryPresalesTasks(String username);

    /** 查询分包任务列表(简化版) */
    List<Map<String, Object>> querySubcontractTasks(String username);

    // ==================== 通知状态更新 ====================

    /** 更新通知状态(标记已读) */
    void updateNotificationState(int notificationId);
}
