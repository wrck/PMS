package com.dp.plat.pms.springmvc.service.impl;

import org.springframework.stereotype.Service;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;

import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import com.dp.plat.pms.springmvc.dao.IndustryAssetProjectRelationMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryAssetProjectRelationService")
public class IndustryAssetProjectRelationService extends AbstractBaseService<IndustryAssetProjectRelationMapper, IndustryAssetProjectRelation> implements IIndustryAssetProjectRelationService {

	public void invalidAssetProjectRelation(IndustryAssetProjectRelation t) {
		dao.invalidAssetProjectRelation(t);
	}
}