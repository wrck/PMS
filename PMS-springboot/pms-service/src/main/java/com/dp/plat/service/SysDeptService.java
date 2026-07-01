package com.dp.plat.service;

import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.DeptDTO;
import com.dp.plat.model.entity.SysDepartment;

import java.util.List;
import java.util.Map;

public interface SysDeptService extends BaseService<SysDepartment> {

    List<SysDepartment> queryDeptTree();

    void createDept(DeptDTO deptDTO);

    void updateDept(DeptDTO deptDTO);

    void deleteDept(Long id);

    void refreshDept();

    /**
     * 查询部门列表（支持条件过滤）
     * 迁移自: DepartmentManageServiceImpl.queryDepartmentList()
     */
    List<SysDepartment> queryDepartmentList(SysDepartment condition);

    /**
     * 查询所有部门（支持条件过滤）
     * 迁移自: DepartmentManageServiceImpl.queryAllDepartments()
     */
    List<SysDepartment> queryAllDepartments(SysDepartment condition);

    /**
     * 查询参数部门（isparam=1）
     * 迁移自: DepartmentManageServiceImpl.queryDepartments()
     */
    List<SysDepartment> queryDepartments();

    /**
     * 查询部门Map(deptCode -> deptName)
     * 迁移自: DepartmentManageServiceImpl.queryDepartmentMap()
     */
    Map<String, String> queryDepartmentMap();

    /**
     * 根据办事处编码查询部门
     * 迁移自: DepartmentManageServiceImpl.queryDepartmentByDepartmentNum()
     */
    SysDepartment queryDepartmentByDepartmentNum(String officeCode);

    /**
     * 查询公司列表
     * 迁移自: DepartmentManageServiceImpl.queryCompanyList()
     */
    List<Map<String, Object>> queryCompanyList(Map<String, Object> condition);

    /**
     * 查询单个公司
     * 迁移自: DepartmentManageServiceImpl.queryCompanyOne()
     */
    Map<String, Object> queryCompanyOne(Map<String, Object> condition);
}
