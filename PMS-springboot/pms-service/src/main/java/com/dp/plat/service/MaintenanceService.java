package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsMaintenance;

public interface MaintenanceService {
    IPage<PmsMaintenance> queryPage(Integer pageNum, Integer pageSize, Long projectId, String maintenanceType);

    PmsMaintenance getDetail(Long id);

    void create(PmsMaintenance maintenance);

    void update(PmsMaintenance maintenance);

    void delete(Long id);
}
