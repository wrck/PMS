package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.TaskChecklist;

import java.util.List;

/**
 * 任务检查项服务 — CRUD + 勾选/取消勾选。
 * 关联设计文档：§2.2、§5.4 检查项 API。
 */
public interface ITaskChecklistService extends IService<TaskChecklist> {

    /**
     * 查询指定任务的全部检查项（按 sortOrder 升序）。
     */
    List<TaskChecklist> listByTaskId(Long taskId);

    /**
     * 新增检查项。
     */
    TaskChecklist create(TaskChecklist checklist);

    /**
     * 更新检查项（标题、描述、强制标记、排序）。
     */
    TaskChecklist update(TaskChecklist checklist);

    /**
     * 逻辑删除检查项。
     */
    void delete(Long id);

    /**
     * 勾选/取消勾选检查项，记录 checkedBy 与 checkedAt。
     *
     * @param id      检查项ID
     * @param checked 目标勾选状态（true=勾选，false=取消）
     * @return 更新后的检查项
     */
    TaskChecklist toggleCheck(Long id, boolean checked);
}
