package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsProjectWeeklyMapper;
import com.dp.plat.mapper.WeeklyContentMapper;
import com.dp.plat.mapper.WeeklyFeedbackMapper;
import com.dp.plat.model.entity.PmsProjectWeekly;
import com.dp.plat.model.entity.WeeklyContent;
import com.dp.plat.model.entity.WeeklyFeedback;
import com.dp.plat.service.WeeklyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeeklyServiceImpl implements WeeklyService {
    @Autowired
    private PmsProjectWeeklyMapper weeklyMapper;

    @Autowired
    private WeeklyContentMapper contentMapper;

    @Autowired
    private WeeklyFeedbackMapper feedbackMapper;

    @Override
    public List<PmsProjectWeekly> queryWeeklyList(Long projectId, Integer weeklyState) {
        LambdaQueryWrapper<PmsProjectWeekly> w = new LambdaQueryWrapper<>();
        w.eq(PmsProjectWeekly::getProjectId, projectId);
        if (weeklyState != null && weeklyState != -1) {
            w.eq(PmsProjectWeekly::getWeeklyState, weeklyState);
        }
        w.orderByDesc(PmsProjectWeekly::getCreateTime);
        List<PmsProjectWeekly> list = weeklyMapper.selectList(w);
        for (PmsProjectWeekly pw : list) {
            pw.setWeeklyStateName(pw.getWeeklyState() == 0 ? "草稿" : "已提交");
        }
        return list;
    }

    @Override
    public PmsProjectWeekly getWeeklyDetail(Long weeklyId) {
        PmsProjectWeekly w = weeklyMapper.selectById(weeklyId);
        if (w == null) {
            throw new BusinessException("周报不存在");
        }
        w.setWeeklyStateName(w.getWeeklyState() == 0 ? "草稿" : "已提交");
        return w;
    }

    @Override
    @Transactional
    public Long createWeekly(PmsProjectWeekly weekly) {
        weekly.setWeeklyState(0);
        weekly.setCreateTime(LocalDateTime.now());
        weeklyMapper.insert(weekly);
        return weekly.getId();
    }

    @Override
    @Transactional
    public void updateWeekly(PmsProjectWeekly weekly) {
        weekly.setUpdateTime(LocalDateTime.now());
        weeklyMapper.updateById(weekly);
    }

    @Override
    @Transactional
    public void submitWeekly(Long weeklyId) {
        PmsProjectWeekly w = weeklyMapper.selectById(weeklyId);
        if (w == null) {
            throw new BusinessException("周报不存在");
        }
        w.setWeeklyState(1);
        w.setUpdateTime(LocalDateTime.now());
        weeklyMapper.updateById(w);
    }

    @Override
    @Transactional
    public void deleteWeekly(Long weeklyId) {
        weeklyMapper.deleteById(weeklyId);
        contentMapper.delete(new LambdaQueryWrapper<WeeklyContent>()
                .eq(WeeklyContent::getWeeklyId, weeklyId));
    }

    @Override
    public List<WeeklyContent> queryWeeklyContents(Long weeklyId, Integer optionType) {
        LambdaQueryWrapper<WeeklyContent> w = new LambdaQueryWrapper<>();
        w.eq(WeeklyContent::getWeeklyId, weeklyId);
        if (optionType != null) {
            w.eq(WeeklyContent::getOptionType, optionType);
        }
        w.and(i -> i.le(WeeklyContent::getEffectiveFrom, LocalDateTime.now())
                .and(j -> j.isNull(WeeklyContent::getEffectiveTo)
                        .or()
                        .gt(WeeklyContent::getEffectiveTo, LocalDateTime.now())));
        return contentMapper.selectList(w);
    }

    @Override
    @Transactional
    public void saveWeeklyContents(Long weeklyId, List<WeeklyContent> contents) {
        // 旧内容设为失效
        contentMapper.update(null, new LambdaQueryWrapper<WeeklyContent>()
                .eq(WeeklyContent::getWeeklyId, weeklyId)
                .ne(WeeklyContent::getOptionType, 6)
                .set(WeeklyContent::getEffectiveTo, LocalDateTime.now()));
        // 插入新内容
        for (WeeklyContent c : contents) {
            c.setWeeklyId(weeklyId);
            c.setEffectiveFrom(LocalDateTime.now());
            c.setEffectiveTo(null);
            contentMapper.insert(c);
        }
    }

    @Override
    public List<WeeklyFeedback> queryFeedbacks(Long weeklyId) {
        return feedbackMapper.selectList(new LambdaQueryWrapper<WeeklyFeedback>()
                .eq(WeeklyFeedback::getWeeklyId, weeklyId)
                .orderByDesc(WeeklyFeedback::getFeedbackTime));
    }

    @Override
    @Transactional
    public void addFeedback(WeeklyFeedback feedback) {
        feedback.setFeedbackTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }
}
