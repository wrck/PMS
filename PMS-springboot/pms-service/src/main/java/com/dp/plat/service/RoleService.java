package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.SysRole;
import java.util.List;

/**
 * 角色服务 - migrated from Struts
 */
public interface RoleService {

    IPage<SysRole> queryPage(Integer pageNum, Integer pageSize);

    SysRole getById(Long id);

    void add(SysRole entity);

    void update(SysRole entity);

    void delete(Long id);

    List<SysRole> listAll();

}