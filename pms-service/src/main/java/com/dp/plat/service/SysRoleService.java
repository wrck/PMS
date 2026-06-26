package com.dp.plat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.RoleDTO;
import com.dp.plat.model.entity.SysRole;

import java.util.List;

public interface SysRoleService extends BaseService<SysRole> {

    Page<SysRole> listRoles(int pageNum, int pageSize, String roleName);

    List<SysRole> listAllRoles();

    void createRole(RoleDTO roleDTO);

    void updateRole(RoleDTO roleDTO);

    void deleteRole(Long id);
}
