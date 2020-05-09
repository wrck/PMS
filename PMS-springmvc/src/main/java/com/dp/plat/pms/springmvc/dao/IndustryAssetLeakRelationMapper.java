package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.IndustryAssetLeakRelation;

public interface IndustryAssetLeakRelationMapper extends AbstractBaseMapper<IndustryAssetLeakRelation> {

    long countProjectAssetLeakBySelectivePageable(PageParam<Object> pageParam);

    List<Object> selectProjectAssetLeakBySelectivePageable(PageParam<Object> pageParam);
    
    void invalidProjectAssetLeakRelation(IndustryAssetLeakRelation t);
}