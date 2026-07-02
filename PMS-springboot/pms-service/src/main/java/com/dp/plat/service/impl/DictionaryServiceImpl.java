package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysBasicDataMapper;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典服务 - migrated from Struts
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private SysBasicDataMapper sysBasicDataMapper;

    @Override
    public IPage<SysBasicData> queryPage(Integer pageNum, Integer pageSize) {
        Page<SysBasicData> page = new Page<>(pageNum, pageSize);
        return sysBasicDataMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public SysBasicData getById(Long id) {
        return sysBasicDataMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(SysBasicData entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysBasicDataMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(SysBasicData entity) {
        entity.setUpdateTime(LocalDateTime.now());
        sysBasicDataMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysBasicDataMapper.deleteById(id);
    }

    @Override
    public List<SysBasicData> listAll() {
        return sysBasicDataMapper.selectList(new LambdaQueryWrapper<>());
    }

}