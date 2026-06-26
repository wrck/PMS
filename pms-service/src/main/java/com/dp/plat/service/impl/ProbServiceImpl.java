package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsProbMapper;
import com.dp.plat.model.entity.PmsProb;
import com.dp.plat.service.ProbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ProbServiceImpl implements ProbService {
    @Autowired
    private PmsProbMapper probMapper;

    @Override
    public IPage<PmsProb> queryPage(Integer pageNum, Integer pageSize, String probTitle, Integer probState, Integer probType) {
        Page<PmsProb> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProb> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(probTitle), PmsProb::getProbTitle, probTitle)
               .eq(probState != null, PmsProb::getProbState, probState)
               .eq(probType != null, PmsProb::getProbType, probType)
               .orderByDesc(PmsProb::getCreateTime);
        return probMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsProb getDetail(Long id) {
        PmsProb prob = probMapper.selectById(id);
        if (prob == null) throw new BusinessException("技术公告不存在");
        return prob;
    }

    @Override
    @Transactional
    public void create(PmsProb prob) {
        prob.setProbCode("SP." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        prob.setProbState(1);
        prob.setCreateTime(LocalDateTime.now());
        probMapper.insert(prob);
    }

    @Override
    @Transactional
    public void update(PmsProb prob) {
        prob.setUpdateTime(LocalDateTime.now());
        probMapper.updateById(prob);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        probMapper.deleteById(id);
    }
}
