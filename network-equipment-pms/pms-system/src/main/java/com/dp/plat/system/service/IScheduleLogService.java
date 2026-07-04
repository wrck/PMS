package com.dp.plat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.system.entity.ScheduleLog;

/**
 * 定时任务日志服务接口。
 */
public interface IScheduleLogService {

    /**
     * 记录定时任务日志。
     *
     * @param scheduleLog 定时任务日志
     * @return 是否记录成功
     */
    boolean record(ScheduleLog scheduleLog);

    /**
     * 分页查询定时任务日志。
     *
     * @param page   当前页码
     * @param size   每页条数
     * @param filter 过滤条件
     * @return 分页结果
     */
    IPage<ScheduleLog> page(int page, int size, ScheduleLog filter);

    /**
     * 分页查询失败任务列表。
     *
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页结果
     */
    IPage<ScheduleLog> listFailed(int page, int size);
}
