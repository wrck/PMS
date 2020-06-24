package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.pms.springmvc.vo.DailyReportVO;
import com.dp.plat.pms.springmvc.entity.DailyReport;

public interface DailyReportMapper extends AbstractBaseMapper<DailyReport> {

    int updateByPrimaryKeyWithBLOBs(DailyReport record);

    Map<String, Object> checkPermission(@Param("model") DailyReportVO v, @Param("user") Principal currentPrincipal);

    Map<String, Object> checkPermission(@Param("model") DailyReportVO v, @Param("permissionTypes") String permissionTypes, @Param("user") Principal currentPrincipal);
}
