package com.dp.plat.service;

import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.DeptDTO;
import com.dp.plat.model.entity.SysDepartment;

import java.util.List;

public interface SysDeptService extends BaseService<SysDepartment> {

    List<SysDepartment> queryDeptTree();

    void createDept(DeptDTO deptDTO);

    void updateDept(DeptDTO deptDTO);

    void deleteDept(Long id);

    void refreshDept();
}
