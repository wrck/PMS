package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.Employee;
import java.util.List;

/**
 * 员工服务 - migrated from Struts
 */
public interface EmployeeService {

    IPage<Employee> queryPage(Integer pageNum, Integer pageSize);

    Employee getById(Long id);

    void add(Employee entity);

    void update(Employee entity);

    void delete(Long id);

    List<Employee> listAll();

}