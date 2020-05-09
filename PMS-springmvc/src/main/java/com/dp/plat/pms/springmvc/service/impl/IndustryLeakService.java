package com.dp.plat.pms.springmvc.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.dao.IndustryLeakMapper;
import com.dp.plat.pms.springmvc.entity.IndustryAssetLeakRelation;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.service.IExcelAnalysisService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetLeakRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.vo.ProjectAssetLeakVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryLeakService")
public class IndustryLeakService extends AbstractBaseService<IndustryLeakMapper, IndustryLeak> implements IIndustryLeakService, IExcelAnalysisService<IndustryLeak> {

	private final static String sourceTableName = "af_industry_leak";
	
	@Autowired
	private ExcelAnalysisMapper excelAnalysisMapper;
	
	@Autowired
	@Lazy
	private IIndustryAssetLeakRelationService industryAssetLeakRelationService;
	
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
	public Result doImportData(List<?> list, Collection<String> columns, Map<String, Object> params) {
//		excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
		if (params != null) {
			ProjectAssetLeakVO assetLeak = (ProjectAssetLeakVO) params.get("targetValue");
			if (assetLeak != null &&( assetLeak.getAssetId() != null || StringUtils.isNotBlank(assetLeak.getAssetIds()))) {
				Set<String> assetIds = Sets.newHashSet(StringUtils.split(StringUtils.trimToEmpty(assetLeak.getAssetIds()), ","));
				if (assetLeak.getAssetId() != null) {
					assetIds.add(assetLeak.getAssetId().toString());
				}
				for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
					IndustryLeak leak = (IndustryLeak) iterator.next();
					if (leak.getId() == null || leak.getId() == 0) {
						this.insertSelective(leak);
					} else {
						this.updateByPrimaryKeySelective(leak);
					}
					
					for (String assetId : assetIds) {
						IndustryAssetLeakRelation t = new IndustryAssetLeakRelation();
						t.setProjectId(assetLeak.getProjectId());
						t.setAssetId(Integer.valueOf(assetId));
						t.setLeakId(leak.getId());
						industryAssetLeakRelationService.invalidProjectAssetLeakRelation(t);
						industryAssetLeakRelationService.insertSelective(t);
					}
				}
			} else {
				excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
			}
		} else {
			excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
		}
		return new Result(true);
	}

	@Override
	public Result submitImportData(Map<String, Object> params, String tempTableName, Collection<String> columns) {
		excelAnalysisMapper.submitImportData(tempTableName, sourceTableName, columns, params);
		return new Result(true);
	}
}
