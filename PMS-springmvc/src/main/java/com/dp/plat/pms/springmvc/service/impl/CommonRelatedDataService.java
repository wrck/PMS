package com.dp.plat.pms.springmvc.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.dao.CommonRelatedDataMapper;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.entity.CommonRelatedData;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("commonRelatedDataService")
public class CommonRelatedDataService extends AbstractBaseService<CommonRelatedDataMapper, CommonRelatedData> implements ICommonRelatedDataService {

    private static final String sourceTableName = "pm_common_related_data";

    @Autowired
    private ExcelAnalysisMapper excelAnalysisMapper;
    
    @Override
    public ExcelAnalysisMapper getExcelAnalysisDao() {
        return excelAnalysisMapper;
    }

    @Override
    public String getSourceTableName() {
        return sourceTableName;
    }
    
    @Override
    public Result doImportData(List<?> list, Collection<String> columns, Map<String, Object> params) {
        //      excelAnalysisMapper.doImportData(list, sourceTableName, columns, params);
        if (params != null) {
            CommonRelatedDataVO targetValue = (CommonRelatedDataVO) params.get("targetValue");
            if (targetValue != null && (targetValue.getType() != null || StringUtils.isNotBlank(targetValue.getType()))) {
                for (Iterator<?> iterator = list.iterator(); iterator.hasNext(); ) {
                    CommonRelatedData data = (CommonRelatedData) iterator.next();
                    data.setType(targetValue.getType());
                    if (data.getId() == null || data.getId() == 0) {
                        this.insertSelective(data);
                    } else {
                        this.updateByPrimaryKeySelective(data);
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
}
