package com.dp.plat.pms.springmvc.excel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.converter.DateConverter;
import com.dp.plat.pms.springmvc.service.IExcelAnalysisService;

/**
 * @author W02611
 */
public class ExcelAnalysisEventListener<T> extends AnalysisEventListener {
	/**
	 * 批量插入数量，然后清理list ，方便内存回收
	 */
	private static final int BATCH_COUNT = 1000;

	private static final String TEMP_TABLE_PREFIX = "import_temp_";

	private Map<Integer, String> headMap;

	private Map<String, String> headRelationMapping;

	private Map<Integer, String> fieldMap;

	private Class<T> tClass;
	/**
	 * 是否异步
	 */
	private boolean sync = true;

	/**
	 * 是否使用临时表
	 */
	private boolean useTempTable;

	/**
	 * 临时表名
	 */
	private String tempTableName;

	/**
	 * 解析的数据，同步操作可以通过getList 获取数据
	 */
	private List<Object> list = new ArrayList<>();

	/**
	 * 没有传入表/字段对应关系，可以通过getMapList 获取数据,用于预览数据
	 */
	private List<Map> mapList = new ArrayList<>();

	private IExcelAnalysisService excelAnalysisService;

	private Map<String, Object> params;
	
	public ExcelAnalysisEventListener(T t) {
		tClass = (Class<T>) t.getClass();
	}

	public ExcelAnalysisEventListener(Map<String, Object> params, boolean isSync) {
		this(params, null, isSync);
	}

	public ExcelAnalysisEventListener(boolean isSync, Map<String, String> headRelationMapping) {
		this(null, headRelationMapping, isSync);
	}

	public ExcelAnalysisEventListener(Map<String, Object> params) {
		this(null, null, false);
	}

	public ExcelAnalysisEventListener(Map<String, Object> params, Map<String, String> headRelationMapping,
			boolean isSync) {
		super();
		this.params = params;
		this.headRelationMapping = headRelationMapping;
		this.sync = isSync;
	}

	@Override
	public void invoke(Object data, AnalysisContext context) {
		if (data instanceof Map && headRelationMapping != null) {
			Map<String, Object> beanMap = new HashMap<>();
//            for (Entry<Integer, String> head : headMap.entrySet()) {
//                Integer headIndex = head.getKey();
//                String headName = head.getValue();
//                if (headRelationMapping.containsKey(headName)) {
//                    String fieldName = headRelationMapping.get(headName);
//                    beanMap.put(fieldName, ((Map) data).get(headIndex));
//                }
//            }
			for (Entry<Integer, String> field : fieldMap.entrySet()) {
				Integer fieldIndex = field.getKey();
				String fieldName = field.getValue();
				beanMap.put(fieldName, ((Map) data).get(fieldIndex));
			}
			beanMap.put("id", beanMap.getOrDefault("id", 0));
			T reportData = null;
			try {
				reportData = getTClass().newInstance();
				ConvertUtils.register(new BigDecimalConverter(BigDecimal.ZERO), BigDecimal.class);
				ConvertUtils.register(new Converter() {
					@Override
					public Object convert(Class type, Object value) {
						Date date = null;
						if (value instanceof String) {
							DateConverter dateConverter = new DateConverter();
							date = dateConverter.convert(String.valueOf(value));
						}
						return date;
					}
				}, Date.class);
				BeanUtils.populate(reportData, beanMap);
			} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
			list.add(reportData);
			// 如果使用临时表，则插入临时表
			if (isUseTempTable() && list.size() >= BATCH_COUNT) {
				Collection<String> columns = new ArrayList<>(fieldMap.values());
				getService().insertTempImportData(tempTableName, list, columns);
				list.clear();
				// 如果不使用临时表，判断是否为异步操作，异步操作直接进行数据调整
			} else if (isSync() && list.size() >= BATCH_COUNT) {
				getService().doImportData(list, beanMap);
				list.clear();
			}
		} else if (data instanceof Map) {
			mapList.add((Map) data);
		}
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		if (isUseTempTable() && !list.isEmpty()) {
			Collection<String> columns = null;
			if (headRelationMapping != null) {
				columns = new ArrayList<>(fieldMap.values());
			}
			getService().insertTempImportData(tempTableName, list, columns);
			list.clear();
			mapList.clear();
		} else if (isSync() && !list.isEmpty()) {
			getService().doImportData(list, params);
			list.clear();
			mapList.clear();
		}
	}

	@Override
	public void invokeHead(Map headMap, AnalysisContext context) {
		super.invokeHead(headMap, context);
	}

	@Override
	public void invokeHeadMap(Map headMap, AnalysisContext context) {
		this.headMap = headMap;
		this.fieldMap = new HashMap<>(headMap.size() * 4 / 3 + 1);
		if (headRelationMapping != null) {
			for (Entry<Integer, String> head : this.headMap.entrySet()) {
				Integer headIndex = head.getKey();
				String headName = head.getValue();
				if (headRelationMapping.containsKey(headName)) {
					String fieldName = headRelationMapping.get(headName);
					fieldMap.put(headIndex, fieldName);
				} else if (headRelationMapping.containsValue(headName)) {
					fieldMap.put(headIndex, headName);
				}
			}
		} else {
			this.fieldMap = headMap;
		}
		// 如果使用临时表，则创建临时表
		if (useTempTable) {
			if (StringUtils.isBlank(tempTableName)) {
				tempTableName = TEMP_TABLE_PREFIX + System.nanoTime();
			}
			getService().createTempImportTable(tempTableName);
		}
	}

	@Override
	public void onException(Exception exception, AnalysisContext context) throws Exception {
		super.onException(exception, context);
		// 如果使用临时表，则删除临时表
		if (useTempTable) {
			getService().dropTempTable(tempTableName);
		}
	}

	@Override
	public boolean hasNext(AnalysisContext context) {
		return super.hasNext(context);
	}

	public IExcelAnalysisService getService() {
		if (excelAnalysisService == null) {
			excelAnalysisService = SpringContext.getBean(IExcelAnalysisService.class);
		}
		return excelAnalysisService;
	}

	public boolean isUseTempTable() {
		return useTempTable;
	}

	public void setUseTempTable(boolean useTempTable) {
		this.useTempTable = useTempTable;
	}

	public String getTempTableName() {
		return tempTableName;
	}

	public void setTempTableName(String tempTableName) {
		this.tempTableName = tempTableName;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	public Map<Integer, String> getHeadMap() {
		return headMap;
	}

	public void setHeadMap(Map<Integer, String> headMap) {
		this.headMap = headMap;
	}

	public Map<String, String> getHeadRelationMapping() {
		return headRelationMapping;
	}

	public void setHeadRelationMapping(Map<String, String> headRelationMapping) {
		this.headRelationMapping = headRelationMapping;
	}

	public Map<Integer, String> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(Map<Integer, String> fieldMap) {
		this.fieldMap = fieldMap;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public List<Map> getMapList() {
		return mapList;
	}

	public void setMapList(List<Map> mapList) {
		this.mapList = mapList;
	}

	public void setExcelAnalysisService(IExcelAnalysisService excelAnalysisService) {
		this.excelAnalysisService = excelAnalysisService;
	}

	public Class<T> getTClass() {
		if (tClass != null) {
			return tClass;
		} else {
			Type type = getClass().getGenericSuperclass();
	        Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
			return (Class<T>) trueType;
		}
		
	}
}
