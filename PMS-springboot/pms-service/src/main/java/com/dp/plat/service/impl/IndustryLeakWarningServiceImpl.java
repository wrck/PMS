package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.IndustryLeakWarningMapper;
import com.dp.plat.model.entity.IndustryLeakWarning;
import com.dp.plat.service.IndustryLeakWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业泄漏预警服务 - migrated from Struts
 */
@Service
public class IndustryLeakWarningServiceImpl implements IndustryLeakWarningService {

    @Autowired
    private IndustryLeakWarningMapper industryLeakWarningMapper;

    @Override
    public IPage<IndustryLeakWarning> queryPage(Integer pageNum, Integer pageSize) {
        Page<IndustryLeakWarning> page = new Page<>(pageNum, pageSize);
        return industryLeakWarningMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public IndustryLeakWarning getById(Long id) {
        return industryLeakWarningMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(IndustryLeakWarning entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        industryLeakWarningMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(IndustryLeakWarning entity) {
        entity.setUpdateTime(LocalDateTime.now());
        industryLeakWarningMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        industryLeakWarningMapper.deleteById(id);
    }

    @Override
    public List<IndustryLeakWarning> listAll() {
        return industryLeakWarningMapper.selectList(new LambdaQueryWrapper<>());
    }

}