package com.dp.plat.system.security;

import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.common.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * 权限校验 Bean，SpEL 中以 {@code @ss} 引用。
 *
 * <p>供 {@code @PreAuthorize("@ss.hasPermi('xxx')")} 注解调用。超级管理员
 * （角色 code = {@link CommonConstants#SUPER_ADMIN_ROLE}）放行所有校验；
 * 其他用户必须持有对应的权限标识。</p>
 */
@Component("ss")
public class PermissionService {

    /**
     * 当前用户为超级管理员，或持有指定权限标识时返回 {@code true}。
     */
    public boolean hasPermi(String perm) {
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String name = authority.getAuthority();
            if (CommonConstants.SUPER_ADMIN_ROLE.equals(name)) {
                return true;
            }
            if (perm != null && perm.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
