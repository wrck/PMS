package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.entity.SysRole;
import com.dp.plat.system.entity.SysRoleMenu;
import com.dp.plat.system.mapper.SysRoleMapper;
import com.dp.plat.system.mapper.SysRoleMenuMapper;
import com.dp.plat.system.security.UserAuthorityService;
import com.dp.plat.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link ISysRoleService}.
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final UserAuthorityService userAuthorityService;

    @Override
    @Cacheable(value = "sysRole", key = "#roleCode")
    public SysRole getByRoleCode(String roleCode) {
        return this.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysRole", allEntries = true)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) {
            userAuthorityService.evictAll();
            return;
        }
        String currentUser = SecurityUtils.getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();
        for (Long menuId : menuIds) {
            SysRoleMenu rm = SysRoleMenu.builder()
                    .roleId(roleId)
                    .menuId(menuId)
                    .build();
            rm.setCreateBy(currentUser);
            rm.setUpdateBy(currentUser);
            rm.setCreateTime(now);
            rm.setUpdateTime(now);
            rm.setDeleted(0);
            sysRoleMenuMapper.insert(rm);
        }
        // Permission change invalidates all cached authorities.
        userAuthorityService.evictAll();
    }
}
