package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.SysDepartmentMapper;
import com.dp.plat.model.dto.DeptDTO;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.SysDeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public void addDept(DeptDTO dto) {
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
}
