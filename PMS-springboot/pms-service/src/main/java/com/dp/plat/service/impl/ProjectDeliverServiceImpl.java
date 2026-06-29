package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.PmsProjectDeliverMapper;
import com.dp.plat.model.entity.PmsProjectDeliver;
import com.dp.plat.service.ProjectDeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectDeliverServiceImpl implements ProjectDeliverService {

    @Autowired
    private PmsProjectDeliverMapper deliverMapper;

    @Override
    public List<PmsProjectDeliver> queryDeliversByProject(Long projectId) {
        return deliverMapper.selectList(new LambdaQueryWrapper<PmsProjectDeliver>()
                .eq(PmsProjectDeliver::getProjectId, projectId)
                .orderByDesc(PmsProjectDeliver::getCreateTime));
    }

    @Override
    @Transactional
    public void addDeliver(PmsProjectDeliver deliver) {
        deliver.setCreateTime(LocalDateTime.now());
        deliverMapper.insert(deliver);
    }

    @Override
    @Transactional
    public void updateDeliver(PmsProjectDeliver deliver) {
        deliverMapper.updateById(deliver);
    }

    @Override
    @Transactional
    public void deleteDeliver(Long id) {
        deliverMapper.deleteById(id);
    }
}
