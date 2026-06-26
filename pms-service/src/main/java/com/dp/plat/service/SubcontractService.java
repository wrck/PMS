package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsSubcontract;

public interface SubcontractService {
    IPage<PmsSubcontract> queryPage(Integer pageNum, Integer pageSize, String subcontractName, String officeCode, Integer state);
    PmsSubcontract getDetail(Long id);
    void create(PmsSubcontract subcontract);
    void update(PmsSubcontract subcontract);
    void delete(Long id);
}
