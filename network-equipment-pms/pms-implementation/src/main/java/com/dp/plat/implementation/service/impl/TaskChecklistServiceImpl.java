package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.TaskChecklist;
import com.dp.plat.implementation.mapper.TaskChecklistMapper;
import com.dp.plat.implementation.service.ITaskChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务检查项服务实现 — CRUD + 勾选/取消勾选。
 */
@Service
@RequiredArgsConstructor
public class TaskChecklistServiceImpl extends ServiceImpl<TaskChecklistMapper, TaskChecklist>
        implements ITaskChecklistService {

    @Override
    public List<TaskChecklist> listByTaskId(Long taskId) {
        return this.list(new LambdaQueryWrapper<TaskChecklist>()
                .eq(TaskChecklist::getTaskId, taskId)
                .orderByAsc(TaskChecklist::getSortOrder)
                .orderByAsc(TaskChecklist::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskChecklist create(TaskChecklist checklist) {
        if (checklist.getMandatory() == null) {
            checklist.setMandatory(false);
        }
        if (checklist.getChecked() == null) {
            checklist.setChecked(false);
        }
        if (checklist.getSortOrder() == null) {
            checklist.setSortOrder(0);
        }
        this.save(checklist);
        return checklist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskChecklist update(TaskChecklist checklist) {
        if (checklist.getId() == null) {
            throw new BusinessException("检查项ID不能为空");
        }
        TaskChecklist existing = this.getById(checklist.getId());
        if (existing == null) {
            throw new BusinessException("检查项不存在");
        }
        // 仅更新可编辑字段，保留勾选状态与勾选人信息（勾选走 toggleCheck）
        existing.setTitle(checklist.getTitle());
        existing.setDescription(checklist.getDescription());
        if (checklist.getMandatory() != null) {
            existing.setMandatory(checklist.getMandatory());
        }
        if (checklist.getSortOrder() != null) {
            existing.setSortOrder(checklist.getSortOrder());
        }
        this.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException("检查项不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskChecklist toggleCheck(Long id, boolean checked) {
        TaskChecklist existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("检查项不存在");
        }
        existing.setChecked(checked);
        if (checked) {
            existing.setCheckedBy(SecurityUtils.getCurrentUserId());
            existing.setCheckedAt(LocalDateTime.now());
        } else {
            existing.setCheckedBy(null);
            existing.setCheckedAt(null);
        }
        this.updateById(existing);
        return existing;
    }
}
