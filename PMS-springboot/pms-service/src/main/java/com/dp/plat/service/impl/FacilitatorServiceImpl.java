package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.FacilitatorMapper;
import com.dp.plat.model.entity.Facilitator;
import com.dp.plat.service.FacilitatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务商服务 - migrated from Struts
 */
@Service
public class FacilitatorServiceImpl implements FacilitatorService {

    @Autowired
    private FacilitatorMapper facilitatorMapper;

    @Override
    public IPage<Facilitator> queryPage(Integer pageNum, Integer pageSize) {
        Page<Facilitator> page = new Page<>(pageNum, pageSize);
        return facilitatorMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public Facilitator getById(Long id) {
        return facilitatorMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Facilitator entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        facilitatorMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Facilitator entity) {
        entity.setUpdateTime(LocalDateTime.now());
        facilitatorMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        facilitatorMapper.deleteById(id);
    }

    @Override
    public List<Facilitator> listAll() {
        return facilitatorMapper.selectList(new LambdaQueryWrapper<>());
    }

}