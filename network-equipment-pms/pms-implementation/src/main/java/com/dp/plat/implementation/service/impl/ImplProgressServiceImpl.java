package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.mapper.ImplProgressMapper;
import com.dp.plat.implementation.service.IImplProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IImplProgressService}.
 */
@Service
@RequiredArgsConstructor
public class ImplProgressServiceImpl extends ServiceImpl<ImplProgressMapper, ImplProgress> implements IImplProgressService {

    @Override
    public List<ImplProgress> listByTaskId(Long taskId) {
        return this.list(new LambdaQueryWrapper<ImplProgress>()
                .eq(ImplProgress::getTaskId, taskId)
                .orderByDesc(ImplProgress::getReportTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImplProgress create(ImplProgress progress) {
        if (progress.getReportTime() == null) {
            progress.setReportTime(LocalDateTime.now());
        }
        if (progress.getReportUserId() == null) {
            progress.setReportUserId(SecurityUtils.getCurrentUserId());
        }
        if (progress.getReportUserName() == null) {
            progress.setReportUserName(SecurityUtils.getCurrentUsername());
        }
        this.save(progress);
        return progress;
    }
}
