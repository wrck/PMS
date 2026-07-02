package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.IndustryLeakMapper;
import com.dp.plat.model.entity.IndustryLeak;
import com.dp.plat.service.IndustryLeakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业泄漏服务 - migrated from Struts
 */
@Service
public class IndustryLeakServiceImpl implements IndustryLeakService {

    @Autowired
    private IndustryLeakMapper industryLeakMapper;

    @Override
    public IPage<IndustryLeak> queryPage(Integer pageNum, Integer pageSize) {
        Page<IndustryLeak> page = new Page<>(pageNum, pageSize);
        return industryLeakMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public IndustryLeak getById(Long id) {
        return industryLeakMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(IndustryLeak entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        industryLeakMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(IndustryLeak entity) {
        entity.setUpdateTime(LocalDateTime.now());
        industryLeakMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        industryLeakMapper.deleteById(id);
    }

    @Override
    public List<IndustryLeak> listAll() {
        return industryLeakMapper.selectList(new LambdaQueryWrapper<>());
    }

}