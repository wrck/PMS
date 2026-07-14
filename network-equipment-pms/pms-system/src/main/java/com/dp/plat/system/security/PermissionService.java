package com.dp.plat.system.security;

import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.common.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 权限校验 Bean，SpEL 中以 {@code @ss} 引用。
 *
 * <p>供 {@code @PreAuthorize("@ss.hasPermi('xxx')")} 注解调用。超级管理员
 * （角色 code = {@link CommonConstants#SUPER_ADMIN_ROLE}）放行所有校验；
 * 其他用户必须持有对应的权限标识。</p>
 *
 * <p>支持通配符匹配：若用户权限含 {@code *}，用 {@link AntPathMatcher}
 * 以 {@code :} 为分隔符做通配符匹配。例如用户持有
 * {@code lowcode:data:*:list}，则可访问 {@code lowcode:data:customer:list}。</p>
 */
@Component("ss")
public class PermissionService {

    private static final AntPathMatcher MATCHER = new AntPathMatcher(":");

    /**
     * 当前用户为超级管理员，或持有指定权限标识时返回 {@code true}。
     * 权限标识支持通配符匹配（用户权限含 * 时触发）。
     */
    public boolean hasPermi(String perm) {
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String name = authority.getAuthority();
            // 超管直通
            if (CommonConstants.SUPER_ADMIN_ROLE.equals(name)) {
                return true;
            }
            // 精确匹配
            if (perm != null && perm.equals(name)) {
                return true;
            }
            // 通配符匹配（用户权限含 * 时）
            if (perm != null && name != null && name.indexOf('*') >= 0 && MATCHER.match(name, perm)) {
                return true;
            }
        }
        return false;
    }
}
