package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.CommonRelatedData;
import java.util.List;

/**
 * 公共关联数据服务 - migrated from Struts
 */
public interface CommonRelatedDataService {

    IPage<CommonRelatedData> queryPage(Integer pageNum, Integer pageSize);

    CommonRelatedData getById(Long id);

    void add(CommonRelatedData entity);

    void update(CommonRelatedData entity);

    void delete(Long id);

    List<CommonRelatedData> listAll();

}