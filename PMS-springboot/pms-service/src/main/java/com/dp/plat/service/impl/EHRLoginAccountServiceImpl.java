package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.EHRLoginAccountMapper;
import com.dp.plat.model.entity.EHRLoginAccount;
import com.dp.plat.service.EHRLoginAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EHR登录账号服务 - migrated from Struts
 */
@Service
public class EHRLoginAccountServiceImpl implements EHRLoginAccountService {

    @Autowired
    private EHRLoginAccountMapper eHRLoginAccountMapper;

    @Override
    public IPage<EHRLoginAccount> queryPage(Integer pageNum, Integer pageSize) {
        Page<EHRLoginAccount> page = new Page<>(pageNum, pageSize);
        return eHRLoginAccountMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public EHRLoginAccount getById(Long id) {
        return eHRLoginAccountMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(EHRLoginAccount entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        eHRLoginAccountMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(EHRLoginAccount entity) {
        entity.setUpdateTime(LocalDateTime.now());
        eHRLoginAccountMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        eHRLoginAccountMapper.deleteById(id);
    }

    @Override
    public List<EHRLoginAccount> listAll() {
        return eHRLoginAccountMapper.selectList(new LambdaQueryWrapper<>());
    }

}