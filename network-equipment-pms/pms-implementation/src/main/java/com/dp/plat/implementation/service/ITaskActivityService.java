package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.TaskActivity;

import java.util.List;

/**
 * 任务活动记录服务 — 列表查询 + 活动记录（追加型）。
 */
public interface ITaskActivityService extends IService<TaskActivity> {

    /**
     * 查询任务活动记录（按时间倒序）。
     */
    List<TaskActivity> listByTaskId(Long taskId);

    /**
     * 记录一条任务活动，自动填充当前操作人。
     *
     * @param taskId       任务ID
     * @param activityType 活动类型（CREATE/UPDATE/STATUS_CHANGE/SUBMIT_REVIEW/...）
     * @param content      活动描述
     * @param metadata     附加元数据（JSON 字符串，可空）
     */
    void record(Long taskId, String activityType, String content, String metadata);
}
