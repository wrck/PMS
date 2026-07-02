package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.DailyReportMapper;
import com.dp.plat.model.entity.DailyReport;
import com.dp.plat.service.DailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日报服务 - migrated from Struts
 */
@Service
public class DailyReportServiceImpl implements DailyReportService {

    @Autowired
    private DailyReportMapper dailyReportMapper;

    @Override
    public IPage<DailyReport> queryPage(Integer pageNum, Integer pageSize) {
        Page<DailyReport> page = new Page<>(pageNum, pageSize);
        return dailyReportMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public DailyReport getById(Long id) {
        return dailyReportMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(DailyReport entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        dailyReportMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(DailyReport entity) {
        entity.setUpdateTime(LocalDateTime.now());
        dailyReportMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        dailyReportMapper.deleteById(id);
    }

    @Override
    public List<DailyReport> listAll() {
        return dailyReportMapper.selectList(new LambdaQueryWrapper<>());
    }

}