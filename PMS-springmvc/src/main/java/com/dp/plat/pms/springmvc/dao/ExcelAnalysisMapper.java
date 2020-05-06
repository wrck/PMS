package com.dp.plat.pms.springmvc.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.vo.PageParam;

public interface ExcelAnalysisMapper  {

	void createTempTable(@Param("tempTableName") String tempTableName, @Param("sourceTableName") String sourceTableName);

	void insertTempData(@Param("tempTableName") String tempTableName, @Param("list") List<?> list, @Param("columns") Collection<String> columns);

	List<?> selectTempData(PageParam<?> pageParam);

	long countTempData(PageParam<?> pageParam);

	void dropTempTable(@Param("tempTableName") String tempTableName);

	void doImportData(@Param("list") List<?> list, @Param("sourceTableName") String sourcetablename, @Param("columns") Collection<String> columns, @Param("params") Map<String, Object> params);

	void submitImportData(@Param("tempTableName")String tempTableName, @Param("sourceTableName") String sourcetablename, @Param("columns") Collection<String> columns, @Param("params") Map<String, Object> params);
}
