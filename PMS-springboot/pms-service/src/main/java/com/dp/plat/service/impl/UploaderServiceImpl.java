package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysFileInfoMapper;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 上传服务 - migrated from Struts
 */
@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    private SysFileInfoMapper sysFileInfoMapper;

    @Override
    public IPage<SysFileInfo> queryPage(Integer pageNum, Integer pageSize) {
        Page<SysFileInfo> page = new Page<>(pageNum, pageSize);
        return sysFileInfoMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public SysFileInfo getById(Long id) {
        return sysFileInfoMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(SysFileInfo entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysFileInfoMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(SysFileInfo entity) {
        entity.setUpdateTime(LocalDateTime.now());
        sysFileInfoMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysFileInfoMapper.deleteById(id);
    }

    @Override
    public List<SysFileInfo> listAll() {
        return sysFileInfoMapper.selectList(new LambdaQueryWrapper<>());
    }

}