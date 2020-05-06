package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;

public interface IndustryAssetProjectRelationMapper extends AbstractBaseMapper<IndustryAssetProjectRelation> {

	void invalidAssetProjectRelation(IndustryAssetProjectRelation t);
}