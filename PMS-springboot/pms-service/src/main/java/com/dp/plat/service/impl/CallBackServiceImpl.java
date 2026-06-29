package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsCallBackMapper;
import com.dp.plat.model.entity.PmsCallBack;
import com.dp.plat.service.CallBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class CallBackServiceImpl implements CallBackService {
    @Autowired
    private PmsCallBackMapper callBackMapper;

    @Override
    public IPage<PmsCallBack> queryCallBackPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState) {
        Page<PmsCallBack> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsCallBack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, PmsCallBack::getProjectId, projectId)
               .eq(applyState != null, PmsCallBack::getApplyState, applyState)
               .orderByDesc(PmsCallBack::getCreateTime);
        return callBackMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsCallBack getCallBackDetail(Long id) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        return cb;
    }

    @Override
    @Transactional
    public void createCallBack(PmsCallBack callBack) {
        callBack.setApplyState(-1);
        callBack.setCreateTime(LocalDateTime.now());
        callBackMapper.insert(callBack);
    }

    @Override
    @Transactional
    public void startFlow(Long id) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        cb.setApplyState(0);
        cb.setApplyTime(LocalDateTime.now());
        callBackMapper.updateById(cb);
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        cb.setApplyState(approved ? 1 : 2);
        if (!approved) cb.setEndTime(LocalDateTime.now());
        callBackMapper.updateById(cb);
    }

    @Override
    @Transactional
    public void resubmit(Long id, PmsCallBack callBack) {
        PmsCallBack existing = callBackMapper.selectById(id);
        if (existing == null) throw new BusinessException("回访记录不存在");
        existing.setApplyState(0);
        existing.setApplyTime(LocalDateTime.now());
        callBackMapper.updateById(existing);
    }
}
