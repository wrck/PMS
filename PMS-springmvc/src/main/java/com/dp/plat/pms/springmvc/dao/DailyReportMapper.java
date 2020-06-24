package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.DailyReport;

public interface DailyReportMapper extends AbstractBaseMapper<DailyReport> {

    int updateByPrimaryKeyWithBLOBs(DailyReport record);
}
