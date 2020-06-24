package com.dp.plat.pms.springmvc.service.impl;

import java.util.Collection;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import java.util.List;
import com.dp.plat.core.service.impl.AbstractBaseService;
import java.util.Map;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.dao.IndustryAssetMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.Iterator;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.core.vo.Result;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.ProjectAssetVO;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryAssetService")
public class IndustryAssetService extends AbstractBaseService<IndustryAssetMapper, IndustryAsset> implements IIndustryAssetService {

    private static final String sourceTableName = "af_industry_asset";

    @Autowired
    private ExcelAnalysisMapper excelAnalysisMapper;

    @Lazy
    @Autowired
    private IIndustryAssetProjectRelationService industryAssetProjectRelationService;
    
    @Lazy
    @Autowired
    private IProjectHeaderService projectHeaderService;

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
        //		excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
        if (params != null) {
            Object targetValue = params.get("targetValue");
            if (targetValue instanceof ProjectAssetVO) {
	            ProjectAssetVO projectAssetVO = (ProjectAssetVO) targetValue;
				if (projectAssetVO  != null && projectAssetVO.getProjectId() != null) {
					ProjectHeader project = projectHeaderService.selectByPrimaryKey(projectAssetVO.getProjectId());
	                for (Iterator<?> iterator = list.iterator(); iterator.hasNext(); ) {
	                    IndustryAsset asset = (IndustryAsset) iterator.next();
	                    if (StringUtils.isBlank(asset.getCustomerName())) {
	                    	asset.setCustomerName(project.getColumn003());
	        			}
	        			if (StringUtils.isBlank(asset.getIndustryCode())) {
	        				asset.setIndustryCode(project.getColumn007());
	        			}
	        			if (asset instanceof ProjectAssetVO) {
							ProjectAssetVO projectAsset = (ProjectAssetVO) asset;
							if (projectAsset.getAssetId() != null) {
								projectAsset.setId(projectAsset.getAssetId());
							}
						}
	                    if (asset.getId() == null || asset.getId() == 0) {
	                        this.insertSelective(asset);
	                    } else {
	                        this.updateByPrimaryKeySelective(asset);
	                    }
	                    IndustryAssetProjectRelation t = new IndustryAssetProjectRelation();
	                    t.setProjectId(projectAssetVO.getProjectId());
	                    t.setAssetId(asset.getId());
	                    industryAssetProjectRelationService.invalidAssetProjectRelation(t);
	                    industryAssetProjectRelationService.insertSelective(t);
	                }
	            } else {
	                excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
	            }
            } else {
                excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
            }
        } else {
            excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
        }
        //		excelAnalysisMapper.doImportData2(params);
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
}
