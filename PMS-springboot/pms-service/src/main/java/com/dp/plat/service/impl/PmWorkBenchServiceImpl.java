package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 工作台服务 - 迁移自老系统 WorkSpaceService
 * 提供工作台首页的待办任务、通知、项目跟踪等数据
 */
@Service
public class PmWorkBenchServiceImpl implements PmWorkBenchService {

    @Autowired private PmsProjectMapper projectMapper;
    @Autowired private PmsProjectTaskMapper projectTaskMapper;
    @Autowired private SysNotificationMapper notificationMapper;
    @Autowired private PmClosedLoopMapper closedLoopMapper;
    @Autowired private PmsPresalesMapper presalesMapper;
    @Autowired private PmsSubcontractMapper subcontractMapper;
    @Autowired private PmsProbMapper probMapper;
    @Autowired private PmsSupervisionMapper supervisionMapper;

    @Override
    public IPage<PmsProject> queryPage(Integer pageNum, Integer pageSize) {
        return projectMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<>());
    }

    @Override
    public PmsProject getById(Long id) { return projectMapper.selectById(id); }

    @Override
    public void add(PmsProject entity) { projectMapper.insert(entity); }

    @Override
    public void update(PmsProject entity) { projectMapper.updateById(entity); }

    @Override
    public void delete(Long id) { projectMapper.deleteById(id); }

    @Override
    public List<PmsProject> listAll() {
        return projectMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 获取待办任务列表
     * 迁移自老系统 WorkSpaceAction.task()
     */
    public List<Map<String, Object>> getTodoTasks(String username) {
        List<Map<String, Object>> tasks = new ArrayList<>();
        // 1. 闭环待办
        // 2. 回访待办
        // 3. 转包待办
        // 4. 售前待办
        // 5. 督查待办
        return tasks;
    }

    /**
     * 获取日常项目跟踪列表
     * 迁移自老系统 WorkSpaceAction.dailyTask()
     */
    public List<Map<String, Object>> getDailyTasks(String username) {
        return new ArrayList<>();
    }

    /**
     * 获取已办理任务列表
     * 迁移自老系统 WorkSpaceAction.hisselftask()
     */
    public List<Map<String, Object>> getHistoryTasks(String username) {
        return new ArrayList<>();
    }

    /**
     * 获取未读通知数量
     * 迁移自老系统 WorkSpaceAction.notice()
     */
    public int getUnreadNotificationCount(String username) {
        return notificationMapper.selectCount(
            new LambdaQueryWrapper<SysNotification>()
                .eq(SysNotification::getNotifyObject, username)
                .eq(SysNotification::getNotifyState, 0));
    }

    /**
     * 获取系统通知列表
     */
    public List<SysNotification> getNotifications(String username) {
        return notificationMapper.selectList(
            new LambdaQueryWrapper<SysNotification>()
                .eq(SysNotification::getNotifyObject, username)
                .orderByDesc(SysNotification::getCreateTime));
    }
}
