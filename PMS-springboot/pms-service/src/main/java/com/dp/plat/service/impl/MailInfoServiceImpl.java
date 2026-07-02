package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.MailContentMapper;
import com.dp.plat.model.entity.MailContent;
import com.dp.plat.service.MailInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件信息服务 - migrated from Struts
 */
@Service
public class MailInfoServiceImpl implements MailInfoService {

    @Autowired
    private MailContentMapper mailContentMapper;

    @Override
    public IPage<MailContent> queryPage(Integer pageNum, Integer pageSize) {
        Page<MailContent> page = new Page<>(pageNum, pageSize);
        return mailContentMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public MailContent getById(Long id) {
        return mailContentMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(MailContent entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        mailContentMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(MailContent entity) {
        entity.setUpdateTime(LocalDateTime.now());
        mailContentMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        mailContentMapper.deleteById(id);
    }

    @Override
    public List<MailContent> listAll() {
        return mailContentMapper.selectList(new LambdaQueryWrapper<>());
    }

}