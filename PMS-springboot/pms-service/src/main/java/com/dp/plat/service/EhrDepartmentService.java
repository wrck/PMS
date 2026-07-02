package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.SysDepartment;
import java.util.List;

/**
 * EHR部门服务 - migrated from Struts
 */
public interface EhrDepartmentService {

    IPage<SysDepartment> queryPage(Integer pageNum, Integer pageSize);

    SysDepartment getById(Long id);

    void add(SysDepartment entity);

    void update(SysDepartment entity);

    void delete(Long id);

    List<SysDepartment> listAll();

}