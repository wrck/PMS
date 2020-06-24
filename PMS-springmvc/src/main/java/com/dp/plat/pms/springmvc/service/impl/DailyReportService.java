package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.service.IDailyReportService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.dao.DailyReportMapper;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.entity.DailyReport;

/**
 *
 * Created by CodeGenerator
 */
@Service("dailyReportService")
public class DailyReportService extends AbstractBaseService<DailyReportMapper, DailyReport> implements IDailyReportService {
}
