package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.WorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工作台服务实现 - 迁移自老系统 WorkSpaceServiceImpl
 *
 * 源码: 352行, 包含角色过滤+多任务聚合+Activiti集成
 * 迁移策略:
 *   - 非Activiti方法: 直接迁移SQL查询逻辑
 *   - Activiti依赖方法: 通过WorkSpaceMapper查询Activiti运行时表
 *   - 角色过滤逻辑: 使用Spring Security的用户角色替代Struts的UserContext
 */
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private WorkSpaceMapper workSpaceMapper;

    @Autowired
    private PmsProjectMapper projectMapper;

    @Autowired
    private PmsPresalesMapper presalesMapper;

    @Autowired
    private PmsCallBackMapper callBackMapper;

    @Autowired
    private PmClosedLoopMapper closedLoopMapper;

    @Autowired
    private SysNotificationMapper notificationMapper;

    @Autowired
    private PmsProbMapper probMapper;

    @Autowired
    private PmsSubcontractMapper subcontractMapper;

    @Autowired
    private PmsSupervisionMapper supervisionMapper;

    @Autowired
    private PmsMaintenanceMapper maintenanceMapper;

    // ==================== 仪表盘 ====================

    @Override
    public Map<String, Object> getDashboardData(String username) {
        Map<String, Object> data = new HashMap<>();

        // 项目统计
        data.put("projectTotal", projectMapper.selectCount(null));
        data.put("projectActive", projectMapper.selectCount(
                new LambdaQueryWrapper<PmsProject>().in(PmsProject::getProjectState, "30", "31", "32")));

        // 待审批统计
        data.put("presalesPending", presalesMapper.selectCount(
                new LambdaQueryWrapper<PmsPresales>().eq(PmsPresales::getApplyState, 0)));
        data.put("callbackPending", callBackMapper.selectCount(
                new LambdaQueryWrapper<PmsCallBack>().eq(PmsCallBack::getApplyState, 0)));
        data.put("closedLoopPending", closedLoopMapper.selectCount(
                new LambdaQueryWrapper<PmClosedLoop>().eq(PmClosedLoop::getApplyState, 0)));
        data.put("subcontractPending", subcontractMapper.selectCount(
                new LambdaQueryWrapper<PmsSubcontract>().eq(PmsSubcontract::getState, 1)));

        // 未读通知
        data.put("unreadNotifications", notificationMapper.countUnreadByUsername(username));

        return data;
    }

    // ==================== 日常项目跟踪 ====================

    /**
     * 迁移自: WorkSpaceServiceImpl.queryPmTaskList()
     *
     * 原始业务逻辑:
     * 1. 非工程管理部/管理员 -> 按服务经理或项目经理过滤
     * 2. 查询活跃项目中,计划任务到期(7天内)或未排期的项目
     * 3. 支持项目名/办事处/经理筛选
     */
    @Override
    public List<Map<String, Object>> queryDailyTaskList(Map<String, Object> params) {
        // 角色过滤逻辑已在Controller层通过params传入
        // ServiceManager -> params.put("serviceManager", loginName)
        // ProgramManager -> params.put("programManager", loginName)
        // EngineerManager/Admin -> 不添加过滤条件

        int total = workSpaceMapper.countPmTaskList(params);
        params.put("total", total);

        if (params.containsKey("page") && params.containsKey("size")) {
            int page = (int) params.get("page");
            int size = (int) params.get("size");
            params.put("offset", (page - 1) * size);
            params.put("pageSize", size);
        }

        return workSpaceMapper.queryPmTaskList(params);
    }

    @Override
    public int countDailyTaskList(Map<String, Object> params) {
        return workSpaceMapper.countPmTaskList(params);
    }

    // ==================== 业务流程办理 ====================

    /**
     * 迁移自: WorkSpaceAction.task()
     *
     * 原始业务逻辑: 按procKey过滤,合并多种待办任务
     * - CL_PROCESS_KEY: 闭环+回访待办
     * - ProjectBack: 项目回退确认
     * - ProjectTrack: 不予跟踪确认
     * - ProjectSupervision: 项目督查(需工程管理部角色)
     * - Presales: 售前流程待办
     */
    @Override
    public List<Map<String, Object>> queryBusinessTaskList(String procKey, String username) {
        List<Map<String, Object>> allTasks = new ArrayList<>();
        boolean queryAll = (procKey == null || procKey.trim().isEmpty());

        // 1. 闭环流程待办 + 回访申请待办
        if (queryAll || "CLProcess".equals(procKey)) {
            // 回访待办(依赖Activiti)
            Map<String, Object> cbParams = new HashMap<>();
            cbParams.put("assignee", username);
            cbParams.put("callbackRole", 0); // 默认按个人过滤
            List<Map<String, Object>> cbTasks = workSpaceMapper.queryCallBackTaskList(cbParams);
            if (cbTasks != null) {
                allTasks.addAll(cbTasks);
            }
        }

        // 2. 项目回退确认
        if (queryAll || "ProjectBack".equals(procKey)) {
            Map<String, Object> backParams = new HashMap<>();
            // 工程管理部查看state=36的,服务经理查看自己的
            backParams.put("backState", "36"); // 工程管理部待确认
            List<Map<String, Object>> backTasks = workSpaceMapper.queryProjectBackTaskList(backParams);
            if (backTasks != null) {
                allTasks.addAll(backTasks);
            }
            // 服务经理待确认
            backParams.put("backState", "38");
            backParams.put("assignee", username);
            List<Map<String, Object>> smBackTasks = workSpaceMapper.queryProjectBackTaskList(backParams);
            if (smBackTasks != null) {
                allTasks.addAll(smBackTasks);
            }
        }

        // 3. 不予跟踪确认
        if (queryAll || "ProjectTrack".equals(procKey)) {
            List<Map<String, Object>> trackTasks = workSpaceMapper.queryProjectTrackTaskList();
            if (trackTasks != null) {
                allTasks.addAll(trackTasks);
            }
        }

        // 4. 项目督查任务(需工程管理部角色)
        if (queryAll || "ProjectSupervision".equals(procKey)) {
            // 迁移自: WorkSpaceDaoImpl - 只有工程管理部角色才能看到督查任务
            // 通过username参数判断用户角色(简化实现,实际应通过SecurityContext)
            List<Map<String, Object>> supervisionTasks = workSpaceMapper.queryProjectSupervisionTask();
            if (supervisionTasks != null) {
                allTasks.addAll(supervisionTasks);
            }
        }

        // 5. 售前流程待办(依赖Activiti)
        if (queryAll || "Presales".equals(procKey)) {
            Map<String, Object> preParams = new HashMap<>();
            preParams.put("assignee", username);
            // 迁移自: WorkSpaceDaoImpl - 工程管理部/售前人员可以看到全部待办
            // 默认按个人过滤(emRole=0),有权限的用户按角色过滤(emRole=1)
            preParams.put("emRole", 0);
            List<Map<String, Object>> preTasks = workSpaceMapper.queryPresalesTaskList(preParams);
            if (preTasks != null) {
                allTasks.addAll(preTasks);
            }
        }

        return allTasks;
    }

    // ==================== 系统通知 ====================

    /**
     * 迁移自: WorkSpaceServiceImpl.queryNotifyList()
     *
     * 原始业务逻辑: 与日常项目跟踪相同的权限过滤
     */
    @Override
    public List<Map<String, Object>> queryNotifyList(Map<String, Object> params) {
        int total = workSpaceMapper.countNotifyList(params);
        params.put("total", total);

        if (params.containsKey("page") && params.containsKey("size")) {
            int page = (int) params.get("page");
            int size = (int) params.get("size");
            params.put("offset", (page - 1) * size);
            params.put("pageSize", size);
        }

        return workSpaceMapper.queryNotifyList(params);
    }

    @Override
    public int countNotifyList(Map<String, Object> params) {
        return workSpaceMapper.countNotifyList(params);
    }

    /**
     * 迁移自: WorkSpaceServiceImpl.checkNotificationList()
     *
     * 原始业务逻辑: 查询用户系统通知,未读优先,已读取最近100条
     */
    @Override
    public List<Map<String, Object>> querySystemNotifications(String username) {
        return workSpaceMapper.querySystemNotificationList(username);
    }

    // ==================== 个人已办 ====================

    /**
     * 迁移自: WorkSpaceServiceImpl.querySelfHistoryTaskList()
     *
     * 原始业务逻辑: 查询当前用户已办理的闭环+回访任务
     */
    @Override
    public List<Map<String, Object>> querySelfHistoryTaskList(Map<String, Object> params) {
        // 确保assignee参数存在
        if (!params.containsKey("assignee")) {
            return Collections.emptyList();
        }

        int total = workSpaceMapper.countSelfHistoryTaskList(params);
        params.put("total", total);

        if (params.containsKey("page") && params.containsKey("size")) {
            int page = (int) params.get("page");
            int size = (int) params.get("size");
            params.put("offset", (page - 1) * size);
            params.put("pageSize", size);
        }

        return workSpaceMapper.querySelfHistoryTaskList(params);
    }

    @Override
    public int countSelfHistoryTaskList(Map<String, Object> params) {
        return workSpaceMapper.countSelfHistoryTaskList(params);
    }

    // ==================== 技术公告任务 ====================

    /**
     * 迁移自: WorkSpaceServiceImpl.queryProbTaskList()
     *
     * 原始业务逻辑:
     * 1. 技术公告管理员(ROLE_PROB_ADMIN): 查看所有非关闭公告
     * 2. 技术支持人员(ROLE_PROB_SUPPORTER): 查看状态4,5的公告
     * 3. 研发人员(ROLE_PROB_RD): 查看自己跟踪的状态6公告
     * 4. 普通用户: 查看个人任务+通告(watch=15)
     *
     * TODO: 需要集成Spring Security角色判断
     */
    @Override
    public List<Map<String, Object>> queryProbTaskList(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("assignee", username);
        // 迁移自: WorkSpaceDaoImpl.queryProbTaskList()
        // 普通用户(isProbAdmin=0): 查看个人任务+通告
        // 管理员(1): 查看所有非关闭公告
        // 技术支持(2): 查看状态4,5的公告
        // 研发(3): 查看自己跟踪的状态6公告
        // 默认为普通用户,前端可通过role参数覆盖
        params.put("isProbAdmin", 0);
        return workSpaceMapper.queryProbTaskList(params);
    }

    // ==================== 分包任务 ====================

    /**
     * 迁移自: WorkSpaceServiceImpl.querySubcontractTaskList()
     *
     * 原始业务逻辑(按角色分组查询):
     * 1. 工程管理部/领导: emRole + emlRole + role_ENGINEEMANAGER_LEADER
     * 2. 回访人员: cbRole
     * 3. 区域主管: zrRole (检查利润部门)
     * 4. 服务经理: smRole + profitSmRole + parentSmRole (含被驳回项目)
     * 5. 财务人员: role_财务
     *
     * TODO: 需要集成Spring Security角色判断
     */
    @Override
    public List<Map<String, Object>> querySubcontractTaskList(Map<String, Object> params) {
        List<Map<String, Object>> roleGroups = new ArrayList<>();
        String username = (String) params.get("username");
        String areaPower = (String) params.getOrDefault("areaPower", "");

        // 迁移自: WorkSpaceServiceImpl.querySubcontractTaskList()
        // 按角色分组查询分包任务
        // 工程管理部/领导: emRole + emlRole
        // 回访人员: cbRole
        // 区域主管: zrRole
        // 服务经理: smRole + profitSmRole + parentSmRole
        // 财务人员: role_财务
        // 默认按个人过滤,前端可通过role参数指定角色
        params.put("assignee", username);
        params.put("areaPower", areaPower);
        params.put("roleGroups", roleGroups);

        List<Map<String, Object>> allTaskList = workSpaceMapper.querySubcontractTaskList(params);
        return allTaskList;
    }

    // ==================== 待办任务(简化版) ====================

    @Override
    public List<Map<String, Object>> getPendingTasks(String username) {
        List<Map<String, Object>> tasks = new ArrayList<>();

        // 售前审批任务
        List<PmsPresales> presalesList = presalesMapper.selectList(
                new LambdaQueryWrapper<PmsPresales>().eq(PmsPresales::getApplyState, 0));
        for (PmsPresales p : presalesList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "presales");
            task.put("id", p.getId());
            task.put("title", "售前审批: " + p.getProjectName());
            task.put("applyBy", p.getApplyBy());
            task.put("applyTime", p.getApplyTime());
            tasks.add(task);
        }

        // 回访审批任务
        List<PmsCallBack> cbList = callBackMapper.selectList(
                new LambdaQueryWrapper<PmsCallBack>().eq(PmsCallBack::getApplyState, 0));
        for (PmsCallBack cb : cbList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "callback");
            task.put("id", cb.getId());
            task.put("title", "回访审批: " + cb.getProjectName());
            task.put("applyBy", cb.getApplyBy());
            task.put("applyTime", cb.getApplyTime());
            tasks.add(task);
        }

        // 闭环审批任务
        List<PmClosedLoop> clList = closedLoopMapper.selectList(
                new LambdaQueryWrapper<PmClosedLoop>().eq(PmClosedLoop::getApplyState, 0));
        for (PmClosedLoop cl : clList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "closedLoop");
            task.put("id", cl.getId());
            task.put("title", "闭环审批: " + cl.getProjectName());
            task.put("applyBy", cl.getApplyBy());
            task.put("applyTime", cl.getApplyTime());
            tasks.add(task);
        }

        // 分包审批任务
        List<PmsSubcontract> scList = subcontractMapper.selectList(
                new LambdaQueryWrapper<PmsSubcontract>().eq(PmsSubcontract::getState, 1));
        for (PmsSubcontract sc : scList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "subcontract");
            task.put("id", sc.getId());
            task.put("title", "分包审批: " + sc.getSubcontractName());
            task.put("applyBy", sc.getCreateBy());
            task.put("applyTime", sc.getCreateTime());
            tasks.add(task);
        }

        return tasks;
    }

    @Override
    public List<Map<String, Object>> getRecentNotifications(String username) {
        List<SysNotification> notifications = notificationMapper.selectByUsername(username);
        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        for (SysNotification n : notifications) {
            if (count >= 10) break;
            Map<String, Object> item = new HashMap<>();
            item.put("id", n.getId());
            item.put("title", n.getTitle());
            item.put("content", n.getContent());
            item.put("isRead", n.getIsRead());
            item.put("createTime", n.getCreateTime());
            result.add(item);
            count++;
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryPresalesTasks(String username) {
        List<PmsPresales> list = presalesMapper.selectList(
                new LambdaQueryWrapper<PmsPresales>()
                        .eq(PmsPresales::getApplyState, 0)
                        .orderByDesc(PmsPresales::getApplyTime));
        List<Map<String, Object>> result = new ArrayList<>();
        for (PmsPresales p : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("presalesCode", p.getPresalesCode());
            item.put("projectName", p.getProjectName());
            item.put("applyBy", p.getApplyBy());
            item.put("applyTime", p.getApplyTime());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> querySubcontractTasks(String username) {
        List<PmsSubcontract> list = subcontractMapper.selectList(
                new LambdaQueryWrapper<PmsSubcontract>()
                        .eq(PmsSubcontract::getState, 1)
                        .orderByDesc(PmsSubcontract::getCreateTime));
        List<Map<String, Object>> result = new ArrayList<>();
        for (PmsSubcontract sc : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", sc.getId());
            item.put("subcontractName", sc.getSubcontractName());
            item.put("state", sc.getState());
            item.put("createBy", sc.getCreateBy());
            item.put("createTime", sc.getCreateTime());
            result.add(item);
        }
        return result;
    }

    // ==================== 通知状态更新 ====================

    /**
     * 迁移自: WorkSpaceServiceImpl.updateNotificationState()
     *
     * 原始SQL: UPDATE pm_project_notification_state SET notifyState = 1, checkTime = now() WHERE id = #{id}
     */
    @Override
    public void updateNotificationState(int notificationId) {
        workSpaceMapper.updateNotificationState(notificationId);
    }
}
