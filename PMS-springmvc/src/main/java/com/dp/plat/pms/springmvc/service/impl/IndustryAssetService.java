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
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.core.vo.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
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
            ProjectAssetVO projectAssetVO = (ProjectAssetVO) params.get("targetValue");
            if (projectAssetVO != null && projectAssetVO.getProjectId() != null) {
                for (Iterator<?> iterator = list.iterator(); iterator.hasNext(); ) {
                    IndustryAsset asset = (IndustryAsset) iterator.next();
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
