package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PmsProjectMemberMapper;
import com.dp.plat.model.entity.PmsProjectMember;
import com.dp.plat.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目成员服务 - migrated from Struts
 */
@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    @Autowired
    private PmsProjectMemberMapper pmsProjectMemberMapper;

    @Override
    public IPage<PmsProjectMember> queryPage(Integer pageNum, Integer pageSize) {
        Page<PmsProjectMember> page = new Page<>(pageNum, pageSize);
        return pmsProjectMemberMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public PmsProjectMember getById(Long id) {
        return pmsProjectMemberMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(PmsProjectMember entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        pmsProjectMemberMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(PmsProjectMember entity) {
        entity.setUpdateTime(LocalDateTime.now());
        pmsProjectMemberMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        pmsProjectMemberMapper.deleteById(id);
    }

    @Override
    public List<PmsProjectMember> listAll() {
        return pmsProjectMemberMapper.selectList(new LambdaQueryWrapper<>());
    }

}