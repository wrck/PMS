package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsSupervisionMapper;
import com.dp.plat.model.entity.PmsSupervision;
import com.dp.plat.service.SupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Service
public class SupervisionServiceImpl implements SupervisionService {
    @Autowired
    private PmsSupervisionMapper mapper;

    @Override
    public IPage<PmsSupervision> queryPage(Integer pageNum, Integer pageSize, Long projectId, String officeCode) {
        Page<PmsSupervision> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsSupervision> w = new LambdaQueryWrapper<>();
        w.eq(projectId != null, PmsSupervision::getProjectId, projectId)
         .eq(StringUtils.hasText(officeCode), PmsSupervision::getOfficeCode, officeCode)
         .orderByDesc(PmsSupervision::getCreateTime);
        return mapper.selectPage(page, w);
    }

    @Override
    public PmsSupervision getDetail(Long id) {
        PmsSupervision s = mapper.selectById(id);
        if (s == null) throw new BusinessException("督查记录不存在");
        return s;
    }

    @Override
    @Transactional
    public void create(PmsSupervision s) {
        s.setCreateTime(LocalDateTime.now());
        mapper.insert(s);
    }

    @Override
    @Transactional
    public void update(PmsSupervision s) {
        mapper.updateById(s);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
