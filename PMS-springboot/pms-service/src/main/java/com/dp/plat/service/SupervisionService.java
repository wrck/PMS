package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsSupervision;

import java.util.List;
import java.util.Map;

public interface SupervisionService {
    IPage<PmsSupervision> queryPage(Integer pageNum, Integer pageSize, Long projectId, String officeCode);

    PmsSupervision getDetail(Long id);

    void create(PmsSupervision supervision);

    void update(PmsSupervision supervision);

    void delete(Long id);

    /** 查询权限用户 - 迁移自 SupervisionAction.queryPowerUser() */
    List<Map<String, Object>> queryPowerUsers();
}
