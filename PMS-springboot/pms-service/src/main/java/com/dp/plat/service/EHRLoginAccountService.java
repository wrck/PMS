package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.EHRLoginAccount;
import java.util.List;

/**
 * EHR登录账号服务 - migrated from Struts
 */
public interface EHRLoginAccountService {

    IPage<EHRLoginAccount> queryPage(Integer pageNum, Integer pageSize);

    EHRLoginAccount getById(Long id);

    void add(EHRLoginAccount entity);

    void update(EHRLoginAccount entity);

    void delete(Long id);

    List<EHRLoginAccount> listAll();

}