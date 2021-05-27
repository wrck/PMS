package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import java.util.Collection;
import java.util.List;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.core.vo.PageParam;

public interface IndustryLeakMapper extends AbstractBaseMapper<IndustryLeak> {

    int updateByPrimaryKeyWithBLOBs(IndustryLeak record);

    void createTempTable(String tempTableName);

    void insertTempData(String tempTableName, List<Object> list, Collection<String> columns);

    List<?> selectTempData(PageParam<?> pageParam);

    long countTempData(PageParam<?> pageParam);

    void dropTempTable(String tempTableName);
}
