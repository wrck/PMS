package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.IndustryLeak;
import java.util.List;

/**
 * 行业泄漏服务 - migrated from Struts
 */
public interface IndustryLeakService {

    IPage<IndustryLeak> queryPage(Integer pageNum, Integer pageSize);

    IndustryLeak getById(Long id);

    void add(IndustryLeak entity);

    void update(IndustryLeak entity);

    void delete(Long id);

    List<IndustryLeak> listAll();

}