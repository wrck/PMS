package com.dp.plat.pms.springmvc.service;

import java.util.Map;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.pms.springmvc.vo.DailyReportVO;
import com.dp.plat.pms.springmvc.entity.DailyReport;

/**
 *
 * Created by CodeGenerator
 */
public interface IDailyReportService extends IAbstractBaseService<DailyReport> {

    PermissionResult checkPermission(DailyReportVO v, String... permissions);

    Map<String, Object> checkPermissionMap(DailyReportVO v, String... permissions);
}
