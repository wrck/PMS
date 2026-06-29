package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.dao.IndustryAssetLeakRelationMapper;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryAssetLeakRelation;
import com.dp.plat.pms.springmvc.service.IIndustryAssetLeakRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
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
	
	@Autowired
	@Lazy
	private IIndustryAssetService industryAssetService;

	@Override
	@Transactional
	public void insertProjectAssetLeakSelective(IndustryLeakVO v) {
		if (v == null) {
			return;
		}
		// 如果漏洞已经添加过，则不再继续添加
		if (v.getId() == null || v.getId() == 0) {
			// 如果漏洞的所属行业为空， 则查询资产的所属行业进行填充
			if (StringUtils.isBlank(v.getIndustryCode())) {
				IndustryAsset asset = industryAssetService.selectByPrimaryKey(v.getAssetId());
				if (asset != null && StringUtils.isNotBlank(asset.getIndustryCode())) {
					v.setIndustryCode(asset.getIndustryCode());
				}
			}
			industryLeakService.insertSelective(v);
		}
		// 如果是项目管理，添加资产。漏洞关联关系
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