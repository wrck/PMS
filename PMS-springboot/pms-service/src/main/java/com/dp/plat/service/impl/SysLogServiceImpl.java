package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysOperateLogMapper;
import com.dp.plat.model.entity.SysOperateLog;
import com.dp.plat.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志服务 - migrated from Struts
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysOperateLogMapper sysOperateLogMapper;

    @Override
    public IPage<SysOperateLog> queryPage(Integer pageNum, Integer pageSize) {
        Page<SysOperateLog> page = new Page<>(pageNum, pageSize);
        return sysOperateLogMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public SysOperateLog getById(Long id) {
        return sysOperateLogMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(SysOperateLog entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysOperateLogMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(SysOperateLog entity) {
        entity.setUpdateTime(LocalDateTime.now());
        sysOperateLogMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysOperateLogMapper.deleteById(id);
    }

    @Override
    public List<SysOperateLog> listAll() {
        return sysOperateLogMapper.selectList(new LambdaQueryWrapper<>());
    }

}