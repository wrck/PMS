package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.TaskComment;

import java.util.List;

/**
 * 任务评论服务 — 列表 / 新增 / 删除（支持二级回复）。
 */
public interface ITaskCommentService extends IService<TaskComment> {

    /**
     * 查询任务评论列表（按时间正序）。
     */
    List<TaskComment> listByTaskId(Long taskId);

    /**
     * 新增评论，自动填充当前用户ID与姓名。
     */
    TaskComment create(TaskComment comment);

    /**
     * 逻辑删除评论。
     */
    void delete(Long id);
}
