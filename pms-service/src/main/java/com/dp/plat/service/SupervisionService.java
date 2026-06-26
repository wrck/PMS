package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsSupervision;

public interface SupervisionService {
    IPage<PmsSupervision> queryPage(Integer pageNum, Integer pageSize, Long projectId, String officeCode);

    PmsSupervision getDetail(Long id);

    void create(PmsSupervision supervision);

    void update(PmsSupervision supervision);

    void delete(Long id);
}
