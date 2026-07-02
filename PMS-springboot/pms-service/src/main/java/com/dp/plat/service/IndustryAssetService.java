package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.IndustryAsset;
import java.util.List;

/**
 * 行业资产服务 - migrated from Struts
 */
public interface IndustryAssetService {

    IPage<IndustryAsset> queryPage(Integer pageNum, Integer pageSize);

    IndustryAsset getById(Long id);

    void add(IndustryAsset entity);

    void update(IndustryAsset entity);

    void delete(Long id);

    List<IndustryAsset> listAll();

}