package com.dp.plat.pms.springmvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.dao.FacilitatorMapper;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.service.IExcelAnalysisService;
import com.dp.plat.pms.springmvc.service.IFacilitatorService;

/**
 *
 * Created by CodeGenerator
 */
@Service("facilitatorService")
public class FacilitatorService extends AbstractBaseService<FacilitatorMapper, Facilitator> implements IFacilitatorService, IExcelAnalysisService<Facilitator> {

    private static final String sourceTableName = "pm_facilitator";

    @Autowired
    private ExcelAnalysisMapper excelAnalysisMapper;

    @Override
    public ExcelAnalysisMapper getExcelAnalysisDao() {
        return excelAnalysisMapper;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }
}
