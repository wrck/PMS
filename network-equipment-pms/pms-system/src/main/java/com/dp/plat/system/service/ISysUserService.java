package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysUser;

/**
 * Service for {@link SysUser}.
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * Get user by username.
     */
    SysUser getByUsername(String username);
}
