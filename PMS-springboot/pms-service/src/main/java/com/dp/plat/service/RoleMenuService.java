package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.RoleMenuPower;
import java.util.List;

/**
 * 角色菜单服务 - migrated from Struts
 */
public interface RoleMenuService {

    IPage<RoleMenuPower> queryPage(Integer pageNum, Integer pageSize);

    RoleMenuPower getById(Long id);

    void add(RoleMenuPower entity);

    void update(RoleMenuPower entity);

    void delete(Long id);

    List<RoleMenuPower> listAll();

}