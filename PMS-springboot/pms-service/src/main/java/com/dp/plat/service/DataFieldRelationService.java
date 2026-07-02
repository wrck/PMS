package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.DataFieldRelation;
import java.util.List;

/**
 * 数据字段关系服务 - migrated from Struts
 */
public interface DataFieldRelationService {

    IPage<DataFieldRelation> queryPage(Integer pageNum, Integer pageSize);

    DataFieldRelation getById(Long id);

    void add(DataFieldRelation entity);

    void update(DataFieldRelation entity);

    void delete(Long id);

    List<DataFieldRelation> listAll();

}