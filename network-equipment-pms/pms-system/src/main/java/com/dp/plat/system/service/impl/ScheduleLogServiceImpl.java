package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.system.entity.ScheduleLog;
import com.dp.plat.system.mapper.ScheduleLogMapper;
import com.dp.plat.system.service.IScheduleLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 定时任务日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class ScheduleLogServiceImpl implements IScheduleLogService {

    private static final String STATUS_FAIL = "FAIL";

    private final ScheduleLogMapper scheduleLogMapper;

    @Override
    public boolean record(ScheduleLog scheduleLog) {
        return scheduleLogMapper.insert(scheduleLog) > 0;
    }

    @Override
    public IPage<ScheduleLog> page(int page, int size, ScheduleLog filter) {
        Page<ScheduleLog> p = new Page<>(page, size);
        LambdaQueryWrapper<ScheduleLog> wrapper = new LambdaQueryWrapper<>();
        if (filter != null) {
            wrapper.like(StringUtils.hasText(filter.getTaskName()), ScheduleLog::getTaskName, filter.getTaskName());
            wrapper.eq(StringUtils.hasText(filter.getStatus()), ScheduleLog::getStatus, filter.getStatus());
            wrapper.eq(StringUtils.hasText(filter.getTaskGroup()), ScheduleLog::getTaskGroup, filter.getTaskGroup());
        }
        wrapper.orderByDesc(ScheduleLog::getStartTime);
        return scheduleLogMapper.selectPage(p, wrapper);
    }

    @Override
    public IPage<ScheduleLog> listFailed(int page, int size) {
        Page<ScheduleLog> p = new Page<>(page, size);
        LambdaQueryWrapper<ScheduleLog> wrapper = new LambdaQueryWrapper<ScheduleLog>()
                .eq(ScheduleLog::getStatus, STATUS_FAIL)
                .orderByDesc(ScheduleLog::getStartTime);
        return scheduleLogMapper.selectPage(p, wrapper);
    }
}
