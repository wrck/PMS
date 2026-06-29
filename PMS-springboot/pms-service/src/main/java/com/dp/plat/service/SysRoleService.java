package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.RoleDTO;
import com.dp.plat.model.entity.SysRole;

import java.util.List;

public interface SysRoleService extends BaseService<SysRole> {

    /** 分页查询角色 */
    IPage<SysRole> queryRolePage(Integer pageNum, Integer pageSize, String roleName);

    /** 查询所有角色 */
    List<SysRole> listAllRoles();

    /** 创建角色 */
    void addRole(RoleDTO roleDTO);

    /** 更新角色 */
    void updateRole(RoleDTO roleDTO);

    /** 删除角色 */
    void deleteRole(Long id);
}
