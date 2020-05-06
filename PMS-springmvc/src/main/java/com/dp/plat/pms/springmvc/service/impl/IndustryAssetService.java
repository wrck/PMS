package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.ProjectAssetVO;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.dao.IndustryAssetMapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryAssetService")
public class IndustryAssetService extends AbstractBaseService<IndustryAssetMapper, IndustryAsset> implements IIndustryAssetService {

	private final static String sourceTableName = "af_industry_asset";
	
	@Autowired
	private ExcelAnalysisMapper excelAnalysisMapper;
	
	@Autowired
	private IndustryAssetProjectRelationService industryAssetProjectRelationService;
	
	@Override
	public ExcelAnalysisMapper getExcelAnalysisDao() {
		return excelAnalysisMapper;
	}
	
	public String getSourceTableName() {
		return sourceTableName;
	}
	

	@Override
	@Transactional
	public Result doImportData(List<?> list, Collection<String> columns, Map<String, Object> params) {
		excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
		if (params != null) {
			ProjectAssetVO projectAssetVO = (ProjectAssetVO) params.get("targetValue");
			if (projectAssetVO != null && projectAssetVO.getProjectId() != null) {
				for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
					IndustryAsset asset = (IndustryAsset) iterator.next();
					IndustryAssetProjectRelation t = new IndustryAssetProjectRelation();
					t.setProjectId(projectAssetVO.getProjectId());
					t.setAssetId(asset.getId());
					industryAssetProjectRelationService.invalidAssetProjectRelation(t);
					industryAssetProjectRelationService.insertSelective(t);
				}
			}
		}
		return new Result(true);
	}
	
	@Override
	public Result submitImportData(Map<String, Object> params, String tempTableName, Collection<String> columns) {
		excelAnalysisMapper.submitImportData(tempTableName, sourceTableName, columns, params);
		return new Result(true);
	}

	@Override
	public List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam) {
		return dao.selectProjectAssetBySelectivePageable(pageParam);
	}

	@Override
	public long countProjectAssetBySelectivePageable(PageParam<Object> pageParam) {
		return dao.countProjectAssetBySelectivePageable(pageParam);
	}

	@Override
	@Transactional
	public void insertProjectAssetSelective(IndustryAssetVO v) {
		if (v == null) {
			return;
		}
		this.insertSelective(v);
		
		if (((ProjectAssetVO)v).getProjectId() != null) {
			IndustryAssetProjectRelation t = new IndustryAssetProjectRelation();
			t.setAssetId(v.getId());
			t.setProjectId(((ProjectAssetVO)v).getProjectId());
			industryAssetProjectRelationService.insertSelective(t);
		}
	}
	
}
