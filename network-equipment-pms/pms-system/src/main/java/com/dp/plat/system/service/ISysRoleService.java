package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysRole;

import java.util.List;

/**
 * Service for {@link SysRole}.
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * Get role by role code.
     */
    SysRole getByRoleCode(String roleCode);

    /**
     * Assign the given menu ids to the role, replacing any previous assignment.
     *
     * @param roleId  the role id
     * @param menuIds the menu ids to assign (may be empty to clear)
     */
    void assignMenus(Long roleId, List<Long> menuIds);
}
