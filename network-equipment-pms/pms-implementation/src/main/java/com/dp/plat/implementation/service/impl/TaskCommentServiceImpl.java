package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.TaskComment;
import com.dp.plat.implementation.mapper.TaskCommentMapper;
import com.dp.plat.implementation.service.ITaskCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务评论服务实现。
 */
@Service
@RequiredArgsConstructor
public class TaskCommentServiceImpl extends ServiceImpl<TaskCommentMapper, TaskComment>
        implements ITaskCommentService {

    @Override
    public List<TaskComment> listByTaskId(Long taskId) {
        return this.list(new LambdaQueryWrapper<TaskComment>()
                .eq(TaskComment::getTaskId, taskId)
                .orderByAsc(TaskComment::getCreateTime)
                .orderByAsc(TaskComment::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskComment create(TaskComment comment) {
        comment.setUserId(SecurityUtils.getCurrentUserId());
        comment.setUserName(SecurityUtils.getCurrentUsername());
        this.save(comment);
        return comment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException("评论不存在");
        }
        this.removeById(id);
    }
}
