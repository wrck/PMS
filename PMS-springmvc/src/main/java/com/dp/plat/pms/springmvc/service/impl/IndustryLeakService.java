package com.dp.plat.pms.springmvc.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.dao.IndustryLeakMapper;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.service.IExcelAnalysisService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryLeakService")
public class IndustryLeakService extends AbstractBaseService<IndustryLeakMapper, IndustryLeak> implements IIndustryLeakService, IExcelAnalysisService<IndustryLeak> {

	private final static String sourceTableName = "af_industry_leak";
	
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
	public void createTempTable(String tempTableName) {
		excelAnalysisMapper.createTempTable(tempTableName, sourceTableName);
	}

	@Override
	public void insertTempData(String tempTableName, List<Object> list, Collection<String> columns) {
		excelAnalysisMapper.insertTempData(tempTableName, list, columns);
	}

	@Override
	public List<?> selectTempData(PageParam<?> pageParam) {
		return excelAnalysisMapper.selectTempData(pageParam);
	}

	@Override
	public long countTempData(PageParam<?> pageParam) {
		return excelAnalysisMapper.countTempData(pageParam);
	}

	@Override
	public void dropTempTable(String tempTableName) {
		excelAnalysisMapper.dropTempTable(tempTableName);
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
