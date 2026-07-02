package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.EhrEmpPower;
import java.util.List;

/**
 * EHR员工权限服务 - migrated from Struts
 */
public interface EhrEmpPowerService {

    IPage<EhrEmpPower> queryPage(Integer pageNum, Integer pageSize);

    EhrEmpPower getById(Long id);

    void add(EhrEmpPower entity);

    void update(EhrEmpPower entity);

    void delete(Long id);

    List<EhrEmpPower> listAll();

}