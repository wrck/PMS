package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.IndustryAssetLeakRelation;
import java.util.List;

/**
 * 行业资产泄漏关系服务 - migrated from Struts
 */
public interface IndustryAssetLeakRelationService {

    IPage<IndustryAssetLeakRelation> queryPage(Integer pageNum, Integer pageSize);

    IndustryAssetLeakRelation getById(Long id);

    void add(IndustryAssetLeakRelation entity);

    void update(IndustryAssetLeakRelation entity);

    void delete(Long id);

    List<IndustryAssetLeakRelation> listAll();

}