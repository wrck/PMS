package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsMaintenanceMapper;
import com.dp.plat.model.entity.PmsMaintenance;
import com.dp.plat.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {
    @Autowired
    private PmsMaintenanceMapper mapper;

    @Override
    public IPage<PmsMaintenance> queryPage(Integer pageNum, Integer pageSize, Long projectId, String maintenanceType) {
        Page<PmsMaintenance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsMaintenance> w = new LambdaQueryWrapper<>();
        w.eq(projectId != null, PmsMaintenance::getProjectId, projectId)
         .eq(StringUtils.hasText(maintenanceType), PmsMaintenance::getMaintenanceType, maintenanceType)
         .orderByDesc(PmsMaintenance::getCreateTime);
        return mapper.selectPage(page, w);
    }

    @Override
    public PmsMaintenance getDetail(Long id) {
        PmsMaintenance m = mapper.selectById(id);
        if (m == null) throw new BusinessException("维保记录不存在");
        return m;
    }

    @Override
    @Transactional
    public void create(PmsMaintenance m) {
        m.setCreateTime(LocalDateTime.now());
        mapper.insert(m);
    }

    @Override
    @Transactional
    public void update(PmsMaintenance m) {
        mapper.updateById(m);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
