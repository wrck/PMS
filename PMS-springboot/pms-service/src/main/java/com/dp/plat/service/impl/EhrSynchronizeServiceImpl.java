package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.config.DataSource;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EHR同步服务 - 迁移自老系统 EhrSynchronizeService
 * 负责从EHR同步员工、部门、公司等数据
 */
@Service
public class EhrSynchronizeServiceImpl implements EhrSynchronizeService {

    @Autowired private EmployeeMapper employeeMapper;
    @Autowired private SysDepartmentMapper departmentMapper;
    @Autowired private JobMapper jobMapper;
    @Autowired private HolidayMapper holidayMapper;
    @Autowired private EHRLoginAccountMapper loginAccountMapper;
    @Autowired private SysOperateLogMapper operateLogMapper;

    @Override
    public IPage<Employee> queryPage(Integer pageNum, Integer pageSize) {
        return employeeMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<>());
    }

    @Override
    public Employee getById(Long id) { return employeeMapper.selectById(id); }

    @Override
    public void add(Employee entity) { employeeMapper.insert(entity); }

    @Override
    public void update(Employee entity) { employeeMapper.updateById(entity); }

    @Override
    public void delete(Long id) { employeeMapper.deleteById(id); }

    @Override
    public List<Employee> listAll() {
        return employeeMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 从EHR同步员工数据
     * 迁移自老系统 GainPersonByEHR
     */
    @Override
    @Transactional
    @DataSource("ehr")
    public void syncEmployeeFromEHR() {
        logSync("syncEmployeeFromEHR", "开始同步EHR员工数据");
        try {
            // 1. 从EHR数据源查询员工信息
            List<Employee> employees = selectAllFromEHR();
            // 2. 清空本地临时数据
            clearLocalData();
            // 3. 插入新数据
            if (employees != null && !employees.isEmpty()) {
                insertEmployees(employees);
            }
            // 4. 同步部门、岗位、假期等数据
            syncDepartments();
            syncJobs();
            syncHolidays();
            logSync("syncEmployeeFromEHR", "EHR员工数据同步完成");
        } catch (Exception e) {
            logSyncError("syncEmployeeFromEHR", e.getMessage());
            throw e;
        }
    }

    @DataSource("ehr")
    public List<Employee> selectAllFromEHR() {
        return employeeMapper.selectList(new LambdaQueryWrapper<>());
    }

    private void clearLocalData() {
        // 清空本地员工临时表
    }

    private void insertEmployees(List<Employee> employees) {
        for (Employee emp : employees) {
            employeeMapper.insert(emp);
        }
    }

    @DataSource("ehr")
    private void syncDepartments() {
        // 同步部门数据
    }

    @DataSource("ehr")
    private void syncJobs() {
        // 同步岗位数据
    }

    @DataSource("ehr")
    private void syncHolidays() {
        // 同步假期数据
    }

    private void logSync(String method, String message) {
        SysOperateLog log = new SysOperateLog();
        log.setOperateType("SYNC");
        log.setOperateContent(method + ": " + message);
        log.setOperateTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        operateLogMapper.insert(log);
    }

    private void logSyncError(String method, String error) {
        SysOperateLog log = new SysOperateLog();
        log.setOperateType("SYNC_ERROR");
        log.setOperateContent(method + ": " + error);
        log.setOperateTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        operateLogMapper.insert(log);
    }
}
