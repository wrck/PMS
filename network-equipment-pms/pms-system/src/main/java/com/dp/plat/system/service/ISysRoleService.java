package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysRole;

/**
 * Service for {@link SysRole}.
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * Get role by role code.
     */
    SysRole getByRoleCode(String roleCode);
}
