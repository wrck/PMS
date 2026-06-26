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
    @Autowired private PmsProjectMapper projectMapper;
    @Autowired private PmsPresalesMapper presalesMapper;
    @Autowired private PmsCallBackMapper callBackMapper;
    @Autowired private PmClosedLoopMapper closedLoopMapper;
    @Autowired private SysNotificationMapper notificationMapper;

    @Override
    public Map<String, Object> getDashboardData(String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("projectTotal", projectMapper.selectCount(null));
        data.put("projectActive", projectMapper.selectCount(
                new LambdaQueryWrapper<PmsProject>().in(PmsProject::getProjectState, 30, 31, 32)));
        data.put("presalesPending", presalesMapper.selectCount(
                new LambdaQueryWrapper<PmsPresales>().eq(PmsPresales::getApplyState, 0)));
        data.put("callbackPending", callBackMapper.selectCount(
                new LambdaQueryWrapper<PmsCallBack>().eq(PmsCallBack::getApplyState, 0)));
        data.put("closedLoopPending", closedLoopMapper.selectCount(
                new LambdaQueryWrapper<PmClosedLoop>().eq(PmClosedLoop::getApplyState, 0)));
        data.put("unreadNotifications", notificationMapper.selectCount(
                new LambdaQueryWrapper<SysNotification>()
                        .eq(SysNotification::getReceiver, username).eq(SysNotification::getIsRead, 0)));
        return data;
    }

    @Override
    public List<Map<String, Object>> getPendingTasks(String username) {
        List<Map<String, Object>> tasks = new ArrayList<>();
        List<PmsPresales> presalesList = presalesMapper.selectList(
                new LambdaQueryWrapper<PmsPresales>().eq(PmsPresales::getApplyState, 0));
        for (PmsPresales p : presalesList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "presales"); task.put("id", p.getId());
            task.put("title", "售前审批: " + p.getProjectName());
            task.put("applyBy", p.getApplyBy()); task.put("applyTime", p.getApplyTime());
            tasks.add(task);
        }
        List<PmsCallBack> cbList = callBackMapper.selectList(
                new LambdaQueryWrapper<PmsCallBack>().eq(PmsCallBack::getApplyState, 0));
        for (PmsCallBack cb : cbList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "callback"); task.put("id", cb.getId());
            task.put("title", "回访审批: " + cb.getProjectName());
            task.put("applyBy", cb.getApplyBy()); task.put("applyTime", cb.getApplyTime());
            tasks.add(task);
        }
        List<PmClosedLoop> clList = closedLoopMapper.selectList(
                new LambdaQueryWrapper<PmClosedLoop>().eq(PmClosedLoop::getApplyState, 0));
        for (PmClosedLoop cl : clList) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "closedLoop"); task.put("id", cl.getId());
            task.put("title", "闭环审批: " + cl.getProjectName());
            task.put("applyBy", cl.getApplyBy()); task.put("applyTime", cl.getApplyTime());
            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public List<Map<String, Object>> getRecentNotifications(String username) {
        List<SysNotification> notifications = notificationMapper.selectList(
                new LambdaQueryWrapper<SysNotification>()
                        .eq(SysNotification::getReceiver, username)
                        .orderByDesc(SysNotification::getCreateTime).last("LIMIT 10"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysNotification n : notifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", n.getId()); item.put("title", n.getTitle());
            item.put("content", n.getContent()); item.put("isRead", n.getIsRead());
            item.put("createTime", n.getCreateTime());
            result.add(item);
        }
        return result;
    }
}
