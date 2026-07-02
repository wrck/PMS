package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.UserMenu;
import java.util.List;

/**
 * 菜单服务 - migrated from Struts
 */
public interface MenuService {

    IPage<UserMenu> queryPage(Integer pageNum, Integer pageSize);

    UserMenu getById(Long id);

    void add(UserMenu entity);

    void update(UserMenu entity);

    void delete(Long id);

    List<UserMenu> listAll();

}