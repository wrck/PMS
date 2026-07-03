package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysMenu;

import java.util.List;

/**
 * Service for {@link SysMenu}.
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * Get menus by user id.
     */
    List<SysMenu> listMenusByUserId(Long userId);

    /**
     * Get child menus by parent id.
     */
    List<SysMenu> listChildren(Long parentId);

    /**
     * Build a menu tree from a flat list of menus.
     *
     * @param menus flat list of menus
     * @return forest of root menus with children populated
     */
    List<SysMenu> buildTree(List<SysMenu> menus);
}
