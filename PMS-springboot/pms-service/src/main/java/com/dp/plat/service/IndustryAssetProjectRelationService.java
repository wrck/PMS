package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.IndustryAssetProjectRelation;
import java.util.List;

/**
 * 行业资产项目关系服务 - migrated from Struts
 */
public interface IndustryAssetProjectRelationService {

    IPage<IndustryAssetProjectRelation> queryPage(Integer pageNum, Integer pageSize);

    IndustryAssetProjectRelation getById(Long id);

    void add(IndustryAssetProjectRelation entity);

    void update(IndustryAssetProjectRelation entity);

    void delete(Long id);

    List<IndustryAssetProjectRelation> listAll();

}