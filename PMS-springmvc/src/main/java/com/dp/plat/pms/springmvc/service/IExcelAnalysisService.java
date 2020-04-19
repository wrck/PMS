package com.dp.plat.pms.springmvc.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.excel.EasyExcel;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.util.UploadUtils;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper;
import com.dp.plat.pms.springmvc.excel.ExcelAnalysisEventListener;

public interface IExcelAnalysisService<T> extends IAbstractBaseService<T> {

	public ExcelAnalysisMapper getExcelAnalysisDao();
	
	public String getSourceTableName();

	public default Result importPreview(Map<String, Object> params, String execlPath) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		List<DataTableColumn> columns = (List<DataTableColumn>) params.getOrDefault("columns", null);
		Map<String, String> headRelationMapping = null;
		if (columns != null) {
			headRelationMapping = new HashMap<String, String>();
			for (DataTableColumn m : columns) {
				headRelationMapping.put(m.getTitle(), m.getData());
			}
		}
		HttpServletRequest httpRequest = HttpContext.getCurrentRequest();
		String fileName = UploadUtils.getWebDir(httpRequest) + execlPath;
		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
		// List<Object> list =
		// EasyExcel.read(fileName).head(ItemData.class).sheet().doReadSync();
		ExcelAnalysisEventListener<T> listener = new ExcelAnalysisEventListener<T>((T) params.get("targetValue"));
		listener.setHeadRelationMapping(headRelationMapping);
		listener.setSync(false);
		//ExcelAnalysisEventListener<T> listener = new ExcelAnalysisEventListener<T>(false, headRelationMapping);
		listener.setExcelAnalysisService(this);
		listener.setUseTempTable(true);
		EasyExcel.read(fileName, listener).autoTrim(true).sheet().doRead();
		List<?> list = null;
		if (listener.isUseTempTable()) {
			list = listener.getList();
		} else {
			list = listener.getMapList();
		}
		if (columns == null || columns.isEmpty()) {
			Map<Integer, String> headMap = listener.getHeadMap();
			columns = new ArrayList<DataTableColumn>();
			for (Entry<Integer, String> entry : headMap.entrySet()) {
				columns.add(new DataTableColumn(entry.getValue(), entry.getKey().toString()));
			}
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("data", list);
		resultData.put("tempTableName", listener.getTempTableName());
		// 根据excel 的字段对应关系得到对应的表列
		Map<Integer, String> fieldMap = listener.getFieldMap();
		if (fieldMap != null && !fieldMap.isEmpty()) {
			List<DataTableColumn> excelColumns = new ArrayList<>(fieldMap.size());
			for (Entry<Integer, String> field : fieldMap.entrySet()) {
				Integer idx = field.getKey();
				String fieldName = field.getValue();
				for (Iterator<DataTableColumn> iterator = columns.iterator(); iterator.hasNext();) {
					DataTableColumn dataTableColumn = (DataTableColumn) iterator.next();
					if (fieldName.equals(dataTableColumn.getData())) {
						excelColumns.add(dataTableColumn);
						iterator.remove();
						break;
					}
				}
			}
			columns = excelColumns;
			Collection<String> columnKeys = fieldMap.values();
			resultData.put("columnKeys", columnKeys);
//			// 根据excel字段情况,判断数据调整的类型
//			if (columnKeys.contains("id")) {
//				importType = AdjustType.EXCEL_REPORT_LINE.type();
//			} else if (columnKeys.contains("deferredId")) {
//				importType = AdjustType.EXCEL_DEFERRED.type();
//			} else if (columnKeys.contains("salesInId")) {
//				importType = AdjustType.EXCEL_SALESIN.type();
//			} else if (columnKeys.contains("contract")) {
//				importType = AdjustType.EXCEL_CONTRACT.type();
//			} else {
//				importType = AdjustType.EXCEL.type();
//			}
		}
		resultData.put("columns", columns);
//		resultData.put("adjustType", importType);
		return new Result(true, resultData);
	}

	/**
	 * 根据上传的excel文件，进行导入，用于完整报表行的情况
	 */
	@Transactional
	public default Result importSubmit(Map<String, Object> params, String execlPath) {
		List<DataTableColumn> columns = (List<DataTableColumn>) params.get("columns");
		Map<String, String> headRelationMapping = new HashMap<String, String>();
		if (columns != null) {
			for (DataTableColumn m : columns) {
				headRelationMapping.put(m.getTitle(), m.getData());
			}
		}
		HttpServletRequest httpRequest = HttpContext.getCurrentRequest();
		String fileName = UploadUtils.getWebDir(httpRequest) + execlPath;
		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
		// List<Object> list =
		// EasyExcel.read(fileName).head(ItemData.class).sheet().doReadSync();
		ExcelAnalysisEventListener listener = new ExcelAnalysisEventListener(true, headRelationMapping);
		EasyExcel.read(fileName, listener).autoCloseStream(true).sheet().doRead();
		return new Result(true);
	}

	public default void createTempImportTable(String tempTableName) {
		this.createTempTable(tempTableName);
	}

	public default void createTempImportTable(String tempTableName, String sourceTableName) {
		this.createTempTable(tempTableName, sourceTableName);
	}

	public default void createTempTable(String tempTableName) {
		getExcelAnalysisDao().createTempTable(tempTableName, getSourceTableName());
	}

	public default void createTempTable(String tempTableName, String sourceTableName) {
		getExcelAnalysisDao().createTempTable(tempTableName, sourceTableName);
	}

	public default void insertTempImportData(String tempTableName, Object vo, Collection<String> columns) {
		List<Object> list = new ArrayList<>();
		list.add(vo);
		this.insertTempData(tempTableName, list, columns);
	}

	public default Result submitTempTable(Map<String, Object> params, String tempTableName,
			Collection<String> columns) {
		Result result = new Result(true);
		result = this.submitImportData(params, tempTableName, columns);
		this.dropTempTable(tempTableName);
		return result;
	}
//	public default Result submitTempTable(Map<String, Object> params, String tempTableName,
//			Collection<String> columns) {
//		Result result = new Result(true);
//		// 根据导入的调整数据，创建报告数据临时表
//		this.createTempImportTable(tempTableName + "_data");
//		// 查询发生变更的报告行IDs
////        List<Integer> reportLineIds = this.selectChangedReportDataId(reportId, tempTableName);
////        if (!reportLineIds.isEmpty()) {
////            ReportDataVO model = new ReportDataVO(reportId);
////            model.setReportLineIds(reportLineIds);
//		PageParam<Object> pageParam = new PageParam<>();
//		pageParam.setPageSize(-1);
////            pageParam.setModel(model);
//		List<?> list = this.selectTempImportData(tempTableName + "_data", pageParam);
//		result = this.doImportData(list, params);
////        }
//		this.dropTempTable(tempTableName);
////		this.dropTempTable(tempTableName + "_data");
//		return result;
//	}

	public default Result submitImportData(Map<String, Object> params, String tempTableName, Collection<String> columns) {
		getExcelAnalysisDao().submitImportData(tempTableName, getSourceTableName(), columns);
		return new Result(true);
	}

	public default void insertTempImportData(String tempTableName, List<Object> list, Collection<String> columns) {
		this.insertTempData(tempTableName, list, columns);
	}

	public default void insertTempData(String tempTableName, List<Object> list, Collection<String> columns) {
		getExcelAnalysisDao().insertTempData(tempTableName, list, columns);
	}

	public default List<?> selectTempImportData(String tempTableName, PageParam<?> pageParam) {
		pageParam.setCustomField(tempTableName);
		long total = this.countTempData(pageParam);
		pageParam.setTotal(total);
		return this.selectTempData(pageParam);
	}

	public default List<?> selectTempData(PageParam<?> pageParam) {
		return getExcelAnalysisDao().selectTempData(pageParam);
	}

	public default long countTempData(PageParam<?> pageParam) {
		return getExcelAnalysisDao().countTempData(pageParam);
	}

	public default void dropTempTable(String tempTableName) {
		getExcelAnalysisDao().dropTempTable(tempTableName);
	}

	public default Result doImportData(List<?> list, Map<String, Object> params) {
		getExcelAnalysisDao().doImportData(list, params);
		return new Result(true);
	}

}
