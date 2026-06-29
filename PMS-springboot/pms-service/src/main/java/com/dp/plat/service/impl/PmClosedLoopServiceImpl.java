package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmClosedLoopMapper;
import com.dp.plat.model.entity.PmClosedLoop;
import com.dp.plat.service.PmClosedLoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PmClosedLoopServiceImpl implements PmClosedLoopService {

    @Autowired
    private PmClosedLoopMapper closedLoopMapper;

    @Override
    public IPage<PmClosedLoop> queryClosedLoopPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState) {
        Page<PmClosedLoop> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmClosedLoop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, PmClosedLoop::getProjectId, projectId)
               .eq(applyState != null, PmClosedLoop::getApplyState, applyState)
               .orderByDesc(PmClosedLoop::getCreateTime);
        return closedLoopMapper.selectPage(page, wrapper);
    }

    @Override
    public PmClosedLoop getDetail(Long id) {
        PmClosedLoop cl = closedLoopMapper.selectById(id);
        if (cl == null) {
            throw new BusinessException("闭环记录不存在");
        }
        return cl;
    }

    @Override
    @Transactional
    public void apply(PmClosedLoop closedLoop) {
        closedLoop.setApplyState(0);
        closedLoop.setApplyTime(LocalDateTime.now());
        closedLoop.setCreateTime(LocalDateTime.now());
        closedLoopMapper.insert(closedLoop);
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved, String role) {
        PmClosedLoop cl = closedLoopMapper.selectById(id);
        if (cl == null) {
            throw new BusinessException("闭环记录不存在");
        }
        cl.setApplyState(approved ? 1 : 2);
        closedLoopMapper.updateById(cl);
    }
}
