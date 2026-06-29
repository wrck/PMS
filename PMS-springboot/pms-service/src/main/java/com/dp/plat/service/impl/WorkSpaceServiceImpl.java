package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.WorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

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

    /** 查询售前任务列表 */
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

    /** 查询分包任务列表 */
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

    /** 更新通知状态 */
    public void updateNotificationState(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }
}
