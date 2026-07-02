package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.SysUser;
import java.util.List;

/**
 * 用户信息服务 - migrated from Struts
 */
public interface UserInfoService {

    IPage<SysUser> queryPage(Integer pageNum, Integer pageSize);

    SysUser getById(Long id);

    void add(SysUser entity);

    void update(SysUser entity);

    void delete(Long id);

    List<SysUser> listAll();

}