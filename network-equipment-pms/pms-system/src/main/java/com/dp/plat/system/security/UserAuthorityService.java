package com.dp.plat.system.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.system.entity.SysRole;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.entity.SysUserRole;
import com.dp.plat.system.mapper.SysMenuMapper;
import com.dp.plat.system.mapper.SysRoleMapper;
import com.dp.plat.system.mapper.SysUserMapper;
import com.dp.plat.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Loads user authorities (role-based permissions) from the database with a
 * simple in-memory TTL cache to avoid hitting the DB on every request.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthorityService {

    /** Cache TTL in milliseconds (5 minutes). */
    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    private final SysUserMapper sysUserMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    private final Map<String, CachedAuthorities> cache = new ConcurrentHashMap<>();

    /**
     * Load the granted authorities for the given username, using a TTL cache.
     *
     * @param username the username to resolve
     * @return a collection of granted authorities (never {@code null})
     */
    public Collection<GrantedAuthority> loadAuthorities(String username) {
        if (username == null || username.isBlank()) {
            return Collections.emptyList();
        }
        CachedAuthorities cached = cache.get(username);
        long now = System.currentTimeMillis();
        if (cached != null && now < cached.expiresAt()) {
            return cached.authorities();
        }
        Collection<GrantedAuthority> authorities = doLoad(username);
        cache.put(username, new CachedAuthorities(authorities, now + CACHE_TTL_MS));
        return authorities;
    }

    /**
     * Evict the cached authorities for the given username (e.g. after role changes).
     */
    public void evict(String username) {
        if (username != null) {
            cache.remove(username);
        }
    }

    /**
     * Evict all cached authorities (e.g. after bulk permission changes).
     */
    public void evictAll() {
        cache.clear();
    }

    private Collection<GrantedAuthority> doLoad(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            return Collections.emptyList();
        }
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> userRoleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toSet());
        // Detect whether the user is bound to the super admin role.
        SysRole adminRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, CommonConstants.SUPER_ADMIN_ROLE));
        boolean isAdmin = adminRole != null && userRoleIds.contains(adminRole.getId());

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority(CommonConstants.SUPER_ADMIN_ROLE));
        }
        // Always load concrete perms so @PreAuthorize("hasAuthority('xxx')") works.
        List<String> perms = sysMenuMapper.listPermsByUserId(user.getId());
        for (String perm : perms) {
            authorities.add(new SimpleGrantedAuthority(perm));
        }
        return authorities;
    }

    private record CachedAuthorities(Collection<GrantedAuthority> authorities, long expiresAt) {
    }
}
