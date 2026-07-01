package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.SysDepartmentMapper;
import com.dp.plat.model.dto.DeptDTO;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.SysDeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门管理服务 - 迁移自老系统 DepartmentManageServiceImpl
 *
 * 对应老系统 fnd_department 表
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {

    @Autowired
    private SysDepartmentMapper deptMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<SysDepartment> queryDeptTree() {
        List<SysDepartment> allDepts = deptMapper.selectList(
                new LambdaQueryWrapper<SysDepartment>().orderByAsc(SysDepartment::getSort));
        return buildDeptTree(allDepts, 0L);
    }

    private List<SysDepartment> buildDeptTree(List<SysDepartment> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .peek(d -> d.setChildren(buildDeptTree(allDepts, d.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createDept(DeptDTO dto) {
        // 检查编码唯一
        Long count = deptMapper.selectCount(
                new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getDeptCode, dto.getDeptCode()));
        if (count > 0) {
            throw new BusinessException("部门编码已存在");
        }
        SysDepartment dept = new SysDepartment();
        BeanUtils.copyProperties(dto, dept);
        dept.setCreateTime(LocalDateTime.now());
        deptMapper.insert(dept);
    }

    @Override
    @Transactional
    public void updateDept(DeptDTO dto) {
        SysDepartment dept = deptMapper.selectById(dto.getId());
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        if (dto.getDeptName() != null) dept.setDeptName(dto.getDeptName());
        if (dto.getDeptCode() != null) dept.setDeptCode(dto.getDeptCode());
        if (dto.getParentId() != null) dept.setParentId(dto.getParentId());
        if (dto.getSort() != null) dept.setSort(dto.getSort());
        if (dto.getStatus() != null) dept.setStatus(dto.getStatus());
        deptMapper.updateById(dept);
    }

    @Override
    @Transactional
    public void deleteDept(Long id) {
        // 检查是否有子部门
        Long childCount = deptMapper.selectCount(
                new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子部门，无法删除");
        }
        deptMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void refreshDept() {
        // 迁移自: DepartmentManageServiceImpl.refreshDepartment()
        // 从外部系统(OA/EHR)同步部门数据
        // 老系统逻辑: 通过DepartmentManageDao查询外部数据源
        // 新系统通过定时任务或手动触发同步
        // 实际实现需要调用外部系统API或读取同步数据
    }

    // ===== 迁移自老系统 DepartmentManageServiceImpl =====

    @Override
    public List<SysDepartment> queryDepartmentList(SysDepartment condition) {
        // 迁移自: DepartmentManageServiceImpl.queryDepartmentList()
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (StringUtils.hasText(condition.getDeptName())) {
                wrapper.like(SysDepartment::getDeptName, condition.getDeptName());
            }
            if (StringUtils.hasText(condition.getDeptCode())) {
                wrapper.eq(SysDepartment::getDeptCode, condition.getDeptCode());
            }
            if (condition.getParentId() != null) {
                wrapper.eq(SysDepartment::getParentId, condition.getParentId());
            }
            if (condition.getStatus() != null) {
                wrapper.eq(SysDepartment::getStatus, condition.getStatus());
            }
        }
        wrapper.orderByAsc(SysDepartment::getSort);
        return deptMapper.selectList(wrapper);
    }

    @Override
    public List<SysDepartment> queryAllDepartments(SysDepartment condition) {
        // 迁移自: DepartmentManageServiceImpl.queryAllDepartments()
        return queryDepartmentList(condition);
    }

    @Override
    public List<SysDepartment> queryDepartments() {
        // 迁移自: DepartmentManageServiceImpl.queryDepartments()
        // 老系统设置 isparam=1 查询参数部门
        // 新系统通过状态字段过滤启用的部门
        SysDepartment condition = new SysDepartment();
        condition.setStatus(1);
        return queryAllDepartments(condition);
    }

    @Override
    public Map<String, String> queryDepartmentMap() {
        // 迁移自: DepartmentManageServiceImpl.queryDepartmentMap()
        List<SysDepartment> allDepts = deptMapper.selectList(
                new LambdaQueryWrapper<SysDepartment>()
                        .eq(SysDepartment::getStatus, 1)
                        .orderByAsc(SysDepartment::getSort));
        Map<String, String> map = new LinkedHashMap<>();
        for (SysDepartment dept : allDepts) {
            map.put(dept.getDeptCode(), dept.getDeptName());
        }
        return map;
    }

    @Override
    public SysDepartment queryDepartmentByDepartmentNum(String officeCode) {
        // 迁移自: DepartmentManageServiceImpl.queryDepartmentByDepartmentNum()
        if (!StringUtils.hasText(officeCode)) return null;
        return deptMapper.selectByDeptCode(officeCode);
    }

    @Override
    public List<Map<String, Object>> queryCompanyList(Map<String, Object> condition) {
        // 迁移自: DepartmentManageServiceImpl.queryCompanyList()
        // 老系统通过 Company bean 查询公司列表
        // 新系统使用原生SQL查询（公司表结构可能不同）
        StringBuilder sql = new StringBuilder("SELECT * FROM fnd_company WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (condition != null) {
            if (condition.containsKey("companyName")) {
                sql.append("AND company_name LIKE ? ");
                params.add("%" + condition.get("companyName") + "%");
            }
            if (condition.containsKey("companyCode")) {
                sql.append("AND company_code = ? ");
                params.add(condition.get("companyCode"));
            }
        }
        sql.append("ORDER BY company_name");
        try {
            return jdbcTemplate.queryForList(sql.toString(), params.toArray());
        } catch (Exception e) {
            // 如果公司表不存在，返回空列表
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> queryCompanyOne(Map<String, Object> condition) {
        // 迁移自: DepartmentManageServiceImpl.queryCompanyOne()
        List<Map<String, Object>> list = queryCompanyList(condition);
        return list.isEmpty() ? null : list.get(0);
    }
}
