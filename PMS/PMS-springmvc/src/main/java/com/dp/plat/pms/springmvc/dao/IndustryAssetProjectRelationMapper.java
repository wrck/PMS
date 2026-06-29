package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import java.util.List;
import com.dp.plat.core.vo.PageParam;

public interface IndustryAssetProjectRelationMapper extends AbstractBaseMapper<IndustryAssetProjectRelation> {

    void invalidAssetProjectRelation(IndustryAssetProjectRelation t);

    List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam);

    long countProjectAssetBySelectivePageable(PageParam<Object> pageParam);

}
