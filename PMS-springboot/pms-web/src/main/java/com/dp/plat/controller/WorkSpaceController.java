package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.WorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台控制器 - 迁移自老系统 WorkSpaceAction
 *
 * 源码: 380行, 8个Action方法
 * 迁移映射:
 *   execute()      -> GET /daily-tasks    (日常项目跟踪)
 *   notice()       -> GET /notifications  (系统通知)
 *   task()         -> GET /business-tasks (业务流程办理)
 *   dailyTask()    -> GET /daily-tasks    (同execute)
 *   hisselftask()  -> GET /history-tasks  (个人已办)
 *   probTask()     -> GET /prob-tasks     (技术公告任务)
 *   subcontractTask() -> GET /subcontract-tasks (分包任务)
 *   updateNotifyState() -> POST /notification/{id}/read
 *
 * 注意: 原系统基于tab切换,新系统改为独立REST端点
 */
@RestController
@RequestMapping("/api/workspace")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    // ==================== 仪表盘 ====================

    /**
     * 工作台仪表盘 - 汇总统计
     * 迁移自: WorkSpaceAction.prepare()中的统计数据
     */
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard(@RequestParam String username) {
        return R.ok(workSpaceService.getDashboardData(username));
    }

    // ==================== 日常项目跟踪 ====================

    /**
     * 日常项目跟踪任务列表
     * 迁移自: WorkSpaceAction.execute() -> workspaceService.queryPmTaskList()
     *
     * 业务规则:
     * - 工程管理部/管理员: 查看所有活跃项目中任务即将到期的项目
     * - 服务经理: 只看自己负责的项目
     * - 项目经理: 只看自己管理的项目
     * - 支持按项目名/办事处/经理筛选
     *
     * @param username    当前用户名
     * @param role        当前用户角色(用于权限过滤)
     * @param projectName 项目名筛选(可选)
     * @param officeCode  办事处编码筛选(可选)
     * @param page        页码(默认1)
     * @param size        每页条数(默认20)
     */
    @GetMapping("/daily-tasks")
    public R<Map<String, Object>> dailyTasks(
            @RequestParam String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String officeCode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Map<String, Object> params = new HashMap<>();
        params.put("projectName", projectName);
        params.put("officeCode", officeCode);
        params.put("page", page);
        params.put("size", size);

        // 角色过滤逻辑(迁移自WorkSpaceServiceImpl.queryPmTaskList)
        // TODO: 从SecurityContext获取角色,此处暂时通过参数传入
        if ("serviceManager".equals(role)) {
            params.put("serviceManager", username);
        } else if ("programManager".equals(role)) {
            params.put("programManager", username);
        }
        // engineerManager/admin: 不添加过滤条件,查看全部

        List<Map<String, Object>> list = workSpaceService.queryDailyTaskList(params);
        int total = params.containsKey("total") ? (int) params.get("total") : list.size();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    // ==================== 业务流程办理 ====================

    /**
     * 业务流程办理任务列表(合并多种待办)
     * 迁移自: WorkSpaceAction.task()
     *
     * 合并以下待办任务类型:
     * 1. 闭环流程待办 (CLProcess)
     * 2. 回访申请待办 (CallBack)
     * 3. 项目回退确认 (ProjectBack)
     * 4. 不予跟踪确认 (ProjectTrack)
     * 5. 项目督查任务 (ProjectSupervision) - 需工程管理部角色
     * 6. 售前流程待办 (Presales)
     *
     * @param procKey 流程类型过滤(为空则查询全部)
     */
    @GetMapping("/business-tasks")
    public R<List<Map<String, Object>>> businessTasks(
            @RequestParam String username,
            @RequestParam(required = false) String procKey) {
        List<Map<String, Object>> tasks = workSpaceService.queryBusinessTaskList(procKey, username);
        return R.ok(tasks);
    }

    // ==================== 系统通知 ====================

    /**
     * 项目通知列表
     * 迁移自: WorkSpaceAction.notice() -> workspaceService.queryNotifyList()
     *
     * 业务规则: 与日常项目跟踪相同的权限过滤
     */
    @GetMapping("/notifications")
    public R<Map<String, Object>> notifications(
            @RequestParam String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String officeCode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Map<String, Object> params = new HashMap<>();
        params.put("projectName", projectName);
        params.put("officeCode", officeCode);
        params.put("page", page);
        params.put("size", size);

        // 角色过滤(同日常项目跟踪)
        if ("serviceManager".equals(role)) {
            params.put("serviceManager", username);
        } else if ("programManager".equals(role)) {
            params.put("programManager", username);
        }

        List<Map<String, Object>> list = workSpaceService.queryNotifyList(params);
        int total = params.containsKey("total") ? (int) params.get("total") : list.size();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 系统通知列表(未读优先)
     * 迁移自: checkNotificationList
     */
    @GetMapping("/system-notifications")
    public R<List<Map<String, Object>>> systemNotifications(@RequestParam String username) {
        return R.ok(workSpaceService.querySystemNotifications(username));
    }

    // ==================== 个人已办 ====================

    /**
     * 个人已办任务列表
     * 迁移自: WorkSpaceAction.hisselftask() -> workspaceService.querySelfHistoryTaskList()
     *
     * 查询当前用户已办理的闭环+回访任务
     */
    @GetMapping("/history-tasks")
    public R<Map<String, Object>> historyTasks(
            @RequestParam String username,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String officeCode,
            @RequestParam(required = false) String projectCustomer,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Map<String, Object> params = new HashMap<>();
        params.put("assignee", username);
        params.put("projectName", projectName);
        params.put("officeCode", officeCode);
        params.put("projectCustomer", projectCustomer);
        params.put("page", page);
        params.put("size", size);

        List<Map<String, Object>> list = workSpaceService.querySelfHistoryTaskList(params);
        int total = params.containsKey("total") ? (int) params.get("total") : list.size();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    // ==================== 技术公告任务 ====================

    /**
     * 技术公告任务列表
     * 迁移自: WorkSpaceAction.probTask() -> workspaceService.queryProbTaskList()
     *
     * 业务规则:
     * - 技术公告管理员: 查看所有非关闭公告
     * - 技术支持人员: 查看状态4,5的公告
     * - 研发人员: 查看自己跟踪的状态6公告
     * - 普通用户: 查看个人任务+通告
     */
    @GetMapping("/prob-tasks")
    public R<List<Map<String, Object>>> probTasks(@RequestParam String username) {
        return R.ok(workSpaceService.queryProbTaskList(username));
    }

    // ==================== 分包任务 ====================

    /**
     * 分包任务列表
     * 迁移自: WorkSpaceAction.subcontractTask() -> workspaceService.querySubcontractTaskList()
     *
     * 业务规则(按角色分组查询):
     * - 工程管理部/领导: emRole + emlRole
     * - 回访人员: cbRole
     * - 区域主管: zrRole (检查利润部门)
     * - 服务经理: smRole + profitSmRole + parentSmRole (含被驳回项目)
     * - 财务人员: role_财务
     */
    @GetMapping("/subcontract-tasks")
    public R<List<Map<String, Object>>> subcontractTasks(
            @RequestParam String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String areaPower) {

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("role", role);
        params.put("areaPower", areaPower);

        return R.ok(workSpaceService.querySubcontractTaskList(params));
    }

    // ==================== 通知状态更新 ====================

    /**
     * 标记通知已读
     * 迁移自: WorkSpaceAction.updateNotifyState()
     *
     * 原始SQL: UPDATE pm_project_notification_state SET notifyState = 1, checkTime = now() WHERE id = #{id}
     */
    @PostMapping("/notification/{id}/read")
    public R<Void> markNotificationRead(@PathVariable int id) {
        workSpaceService.updateNotificationState(id);
        return R.ok();
    }
}
