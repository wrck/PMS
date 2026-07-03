package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysMenu;
import com.dp.plat.system.mapper.SysMenuMapper;
import com.dp.plat.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link ISysMenuService}.
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> listMenusByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.listMenusByUserId(userId);
    }

    @Override
    public List<SysMenu> listChildren(Long parentId) {
        return this.list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, parentId)
                .orderByAsc(SysMenu::getOrderNum));
    }

    @Override
    public List<SysMenu> buildTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<SysMenu>> byParent = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            Long pid = menu.getParentId() == null ? 0L : menu.getParentId();
            byParent.computeIfAbsent(pid, k -> new ArrayList<>()).add(menu);
        }
        for (SysMenu menu : menus) {
            menu.setChildren(byParent.get(menu.getId()));
        }
        List<SysMenu> roots = byParent.getOrDefault(0L, new ArrayList<>());
        // Also include any menu whose parent id is not present in the input set
        // as a root, so the tree works even when given a partial list.
        List<SysMenu> result = new ArrayList<>(roots);
        for (SysMenu menu : menus) {
            Long pid = menu.getParentId() == null ? 0L : menu.getParentId();
            if (pid != 0L && !byParent.containsKey(pid)) {
                result.add(menu);
            }
        }
        result.sort(Comparator.comparingInt(m -> m.getOrderNum() == null ? 0 : m.getOrderNum()));
        return result;
    }
}
