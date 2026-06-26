package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsSubcontractMapper;
import com.dp.plat.model.entity.PmsSubcontract;
import com.dp.plat.service.SubcontractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Service
public class SubcontractServiceImpl implements SubcontractService {
    @Autowired
    private PmsSubcontractMapper subcontractMapper;

    @Override
    public IPage<PmsSubcontract> queryPage(Integer pageNum, Integer pageSize, String subcontractName, String officeCode, Integer state) {
        Page<PmsSubcontract> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsSubcontract> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(subcontractName), PmsSubcontract::getSubcontractName, subcontractName)
               .eq(StringUtils.hasText(officeCode), PmsSubcontract::getOfficeCode, officeCode)
               .eq(state != null, PmsSubcontract::getState, state)
               .orderByDesc(PmsSubcontract::getCreateTime);
        return subcontractMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsSubcontract getDetail(Long id) {
        PmsSubcontract sc = subcontractMapper.selectById(id);
        if (sc == null) throw new BusinessException("转包项目不存在");
        return sc;
    }

    @Override
    @Transactional
    public void create(PmsSubcontract subcontract) {
        subcontract.setCreateTime(LocalDateTime.now());
        subcontract.setState(1);
        subcontractMapper.insert(subcontract);
    }

    @Override
    @Transactional
    public void update(PmsSubcontract subcontract) {
        subcontractMapper.updateById(subcontract);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        subcontractMapper.deleteById(id);
    }
}
