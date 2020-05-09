package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.dao.IndustryAssetLeakRelationMapper;
import com.dp.plat.pms.springmvc.entity.IndustryAssetLeakRelation;
import com.dp.plat.pms.springmvc.service.IIndustryAssetLeakRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryAssetLeakRelationService")
public class IndustryAssetLeakRelationService
		extends AbstractBaseService<IndustryAssetLeakRelationMapper, IndustryAssetLeakRelation>
		implements IIndustryAssetLeakRelationService {

	@Autowired
	@Lazy
	private IIndustryLeakService industryLeakService;

	@Override
	@Transactional
	public void insertProjectAssetLeakSelective(IndustryLeakVO v) {
		if (v == null) {
			return;
		}
		industryLeakService.insertSelective(v);
		if (v.getProjectId() != null) {
			IndustryAssetLeakRelation t = new IndustryAssetLeakRelation();
			t.setAssetId(v.getAssetId());
			t.setLeakId(v.getId());
			t.setProjectId(v.getProjectId());
			this.insertSelective(t);
		}
	}

	@Override
	public void invalidProjectAssetLeakRelation(IndustryAssetLeakRelation t) {
		dao.invalidProjectAssetLeakRelation(t);
	}

	@Override
	public long countProjectAssetLeakBySelectivePageable(PageParam<Object> pageParam) {
		return dao.countProjectAssetLeakBySelectivePageable(pageParam);
	}

	@Override
	public List<Object> selectProjectAssetLeakBySelectivePageable(PageParam<Object> pageParam) {
		return dao.selectProjectAssetLeakBySelectivePageable(pageParam);
	}
}