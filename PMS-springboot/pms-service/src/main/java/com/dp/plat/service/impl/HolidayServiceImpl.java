package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.HolidayMapper;
import com.dp.plat.model.entity.Holiday;
import com.dp.plat.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 假期服务 - migrated from Struts
 */
@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayMapper holidayMapper;

    @Override
    public IPage<Holiday> queryPage(Integer pageNum, Integer pageSize) {
        Page<Holiday> page = new Page<>(pageNum, pageSize);
        return holidayMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public Holiday getById(Long id) {
        return holidayMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Holiday entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        holidayMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Holiday entity) {
        entity.setUpdateTime(LocalDateTime.now());
        holidayMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        holidayMapper.deleteById(id);
    }

    @Override
    public List<Holiday> listAll() {
        return holidayMapper.selectList(new LambdaQueryWrapper<>());
    }

}