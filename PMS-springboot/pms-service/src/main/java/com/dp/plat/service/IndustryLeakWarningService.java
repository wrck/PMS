package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.IndustryLeakWarning;
import java.util.List;

/**
 * 行业泄漏预警服务 - migrated from Struts
 */
public interface IndustryLeakWarningService {

    IPage<IndustryLeakWarning> queryPage(Integer pageNum, Integer pageSize);

    IndustryLeakWarning getById(Long id);

    void add(IndustryLeakWarning entity);

    void update(IndustryLeakWarning entity);

    void delete(Long id);

    List<IndustryLeakWarning> listAll();

}