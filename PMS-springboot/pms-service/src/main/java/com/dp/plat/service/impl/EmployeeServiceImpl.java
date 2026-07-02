package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.EmployeeMapper;
import com.dp.plat.model.entity.Employee;
import com.dp.plat.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工服务 - migrated from Struts
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public IPage<Employee> queryPage(Integer pageNum, Integer pageSize) {
        Page<Employee> page = new Page<>(pageNum, pageSize);
        return employeeMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public Employee getById(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Employee entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        employeeMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Employee entity) {
        entity.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeMapper.deleteById(id);
    }

    @Override
    public List<Employee> listAll() {
        return employeeMapper.selectList(new LambdaQueryWrapper<>());
    }

}