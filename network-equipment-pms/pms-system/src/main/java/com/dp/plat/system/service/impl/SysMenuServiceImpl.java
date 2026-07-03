package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysMenu;
import com.dp.plat.system.mapper.SysMenuMapper;
import com.dp.plat.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link ISysMenuService}.
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> listMenusByUserId(Long userId) {
        // TODO: implement join with sys_role_menu and sys_user_role for permission-based filtering.
        return Collections.emptyList();
    }

    @Override
    public List<SysMenu> listChildren(Long parentId) {
        return this.list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, parentId)
                .orderByAsc(SysMenu::getOrderNum));
    }
}
