package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.SysRoleMapper;
import com.dp.plat.model.dto.RoleDTO;
import com.dp.plat.model.entity.SysRole;
import com.dp.plat.service.SysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理服务 - 迁移自老系统 RoleManageServiceImpl
 * 
 * 对应老系统 fnd_roles 表
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public IPage<SysRole> queryRolePage(Integer pageNum, Integer pageSize, String roleName) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
               .orderByDesc(SysRole::getCreateTime);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void addRole(RoleDTO dto) {
        // 检查角色编码唯一
        if (StringUtils.hasText(dto.getRoleCode())) {
            Long count = roleMapper.selectCount(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, dto.getRoleCode()));
            if (count > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO dto) {
        SysRole role = roleMapper.selectById(dto.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (StringUtils.hasText(dto.getRoleName())) role.setRoleName(dto.getRoleName());
        if (StringUtils.hasText(dto.getRoleCode())) role.setRoleCode(dto.getRoleCode());
        if (dto.getStatus() != null) role.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getMenuIds())) role.setMenuIds(dto.getMenuIds());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public List<SysRole> listAllRoles() {
        return roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getRoleName));
    }
}
