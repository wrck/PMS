package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.UserLogin;
import java.util.List;

/**
 * 用户登录记录服务 - migrated from Struts
 */
public interface UserLoginRecordService {

    IPage<UserLogin> queryPage(Integer pageNum, Integer pageSize);

    UserLogin getById(Long id);

    void add(UserLogin entity);

    void update(UserLogin entity);

    void delete(Long id);

    List<UserLogin> listAll();

}