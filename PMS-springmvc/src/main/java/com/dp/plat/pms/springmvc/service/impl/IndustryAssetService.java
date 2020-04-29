package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.dao.IndustryAssetMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryAssetService")
public class IndustryAssetService extends AbstractBaseService<IndustryAssetMapper, IndustryAsset> implements IIndustryAssetService {

	private final static String sourceTableName = "af_industry_asset";
	
	@Autowired
	private ExcelAnalysisMapper excelAnalysisMapper;
	
	@Override
	public ExcelAnalysisMapper getExcelAnalysisDao() {
		return excelAnalysisMapper;
	}
	
	public String getSourceTableName() {
		return sourceTableName;
	}

	@Override
	public Result doImportData(List<?> list, Map<String, Object> params) {
		excelAnalysisMapper.doImportData(list, params);
		return new Result(true);
	}
	
	@Override
	public Result submitImportData(Map<String, Object> params, String tempTableName, Collection<String> columns) {
		excelAnalysisMapper.submitImportData(tempTableName, sourceTableName, columns);
		return new Result(true);
	}
}
