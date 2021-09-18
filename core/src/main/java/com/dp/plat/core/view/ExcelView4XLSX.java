package com.dp.plat.core.view;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import com.dp.plat.core.util.MessageUtils;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;

/**
 * Excel导出视图,根据Controller中返回的Model获取属性名为list的数据导出到Excel,待完善
 * 
 * @author w02611
 *
 */
public class ExcelView4XLSX extends AbstractExcelView {

	private boolean needParseTitle = true; 
	private boolean hasHeaderTitle = false;
	private boolean oneSheetOnly = false;
	private boolean hasCreatedSheet = false;
	private int startRow = 1;
	
	public ExcelView4XLSX() {
		setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		setExtension(".xlsx");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// get data model which is passed by the Spring container
		String exportFileName = (String) model.get("exportFileName");
		if (StringUtils.isBlank(exportFileName)) {
			exportFileName = request.getParameter("exportFileName");
		}
		if (StringUtils.isNotBlank(exportFileName)) {
			exportFileName = this.encodeFileName(request, exportFileName);
			String disposition = response.getHeader("Content-Disposition");
			String attachment = "attachment;filename=" + exportFileName + this.getExtension();
			if (disposition == null) {
				disposition = attachment;
			} else if (!disposition.contains("attachment;filename=")) {
				disposition += ";" + attachment;
			}
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-Disposition", disposition);
		}
		if (this.getBeanName() != null) {
			needParseTitle = true; 
			hasHeaderTitle = false;
			oneSheetOnly = false;
			hasCreatedSheet = false;
			startRow = 1;
		}
		
		List<Object> list = (List<Object>) model.get("data");
		Map<String, String> colValue = null;
		if (model.containsKey("columns") && model.get("columns") instanceof String[]) {
			String[] columns = (String[]) model.get("columns");
			Map<String, String> allColValue = new LinkedHashMap<>(columns.length);
			colValue = new LinkedHashMap<>(columns.length);
			for (String column : columns) {
				String[] kv = StringUtils.split(column, "=");
				if (kv == null || kv.length == 0) {
					continue;
				} else if (kv.length == 2) {
					if (!kv[0].equals(kv[1])) {
						colValue.put(kv[0], kv[1]);
					}
					allColValue.put(kv[0], kv[1]);
				} else {
					colValue.put(column, null);
				}
			}
			if (colValue.isEmpty()) {
				colValue = allColValue;
			}
		}/* else if (model.containsKey("pageParam")) {
			PageParam pageParam = (PageParam) model.get("pageParam");
			List<DataTableColumn> columns = pageParam.getColumns();
			colValue = new LinkedHashMap<>(columns.size());
			for (DataTableColumn column : columns) {
				String k = column.getData();
				String v = column.getTitle();
				colValue.put(k, v);
			}
		}*/
//		List<List<Object>> lists = new ArrayList<>();
//		int total = list.size();
//		int count = (int) Math.ceil((double) total / 65530);
//		for (int i = 0; i < count; i++) {
//			lists.add(list.subList(i * 65530, (i + 1) * 65530 > total ? total : (i + 1) * 65530));
//		}
//		int i = 1;
//		for (List<Object> list2 : lists) {
			int i = 1;
			List<Object> list2 = list;
			// create a new Excel sheet
			Sheet sheet = null;
			if (hasCreatedSheet && oneSheetOnly) {
				sheet = workbook.getSheetAt(0);
			} else {
				sheet = workbook.createSheet("sheet" + (i++));
				sheet.setDefaultColumnWidth(30);
				hasCreatedSheet = true;
			}

			// create style for header cells
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setFontName("Arial");
			style.setFillForegroundColor(HSSFColorPredefined.BLUE.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			font.setBold(true);
			font.setColor(HSSFColorPredefined.WHITE.getIndex());
			style.setFont(font);

			//动态列名
			Map<String, String> dynamicColumns = (Map<String, String>) model.get("dynamicColumns");
			Map<String, Object> renderColumns = Collections.emptyMap();
			if ((dynamicColumns == null || dynamicColumns.isEmpty()) && (model.containsKey("pageParam") || model.containsKey("columns") && model.get("columns") instanceof List)) {
				List<DataTableColumn> columns = null;
				if (model.containsKey("pageParam")) {
					PageParam pageParam = (PageParam) model.get("pageParam");
					columns = pageParam.getColumns();
				} 
				if (columns == null && model.containsKey("columns") && model.get("columns") instanceof List) {
					columns = (List<DataTableColumn>) model.get("columns");
				}
				if (columns != null) {
					dynamicColumns = new LinkedHashMap<String, String>(columns.size());
					colValue = new LinkedHashMap<>(columns.size());
					renderColumns = new LinkedHashMap<>(columns.size());
					for (DataTableColumn column : columns) {
						if (!column.getVisible()) {
							continue;
						}
						String k = column.getData();
						String v = column.getTitle();
						dynamicColumns.put(k, v);
						colValue.put(k, v);
						if (StringUtils.isNotBlank(column.getRender())) {
							String render = column.getRender();
							ScriptEngineManager engineManager = new ScriptEngineManager();
							ScriptEngine jsEngine = engineManager.getEngineByName("js");
							jsEngine.eval("var render = " + render);
							Map<String, Object> options = new HashMap<String, Object>();
							Map<String, Object> settings = new HashMap<String, Object>();
							Map<String, Object> aoColumn = new HashMap<String, Object>();
							aoColumn.put("data", k);
							try {
								Field aliasField = ReflectionUtils.findField(column.getClass(), "alias");
								aliasField.setAccessible(true);
								Object alias = ReflectionUtils.getField(aliasField, column);
								aoColumn.put("alias", alias);
							} catch (Exception e) {
							}
							settings.put("aoColumns", Collections.singletonList(aoColumn));
							options.put("settings", settings);
							options.put("col", 0);
							jsEngine.put("renderJS", render);
							jsEngine.put("options", options);
							Invocable invocable = (Invocable) jsEngine;
							renderColumns.put(k, invocable);
						}
					}
				}
			}
			// create header row
			if (!hasHeaderTitle) {
				Row header = sheet.createRow(0);
				Integer colCount = 0;
				Class objClass = null;
				if (list2 != null && !list2.isEmpty()) {
					Object obj = list2.get(0);
					objClass = obj.getClass();
				} else if (model.containsKey("pageParam")) {
					PageParam pageParam = (PageParam) model.get("pageParam");
					if (pageParam.getModel() != null) {
						objClass = pageParam.getModel().getClass();
					}
				} else {
					return;
				}
				setHeader(objClass, header, style, colCount, colValue ,dynamicColumns);
				hasHeaderTitle = true;
			}
			// create data rows
			createRow(list2, sheet, colValue, dynamicColumns, renderColumns);
//		}
	}

	protected void setHeader(Class<?> clazz, Row header, CellStyle style, Integer colCount,
			Map<String, String> colValue, Map<String, String> dynamicColumns) throws ClassNotFoundException {
		if (this.needParseTitle) {
			setTitle(clazz, header, style, colCount, colValue ,dynamicColumns);
			needParseTitle = true;
		}
		if (colValue != null) {
			for (Entry<String, String> entry : colValue.entrySet()) {
				header.createCell(colCount).setCellValue(entry.getValue());
				header.getCell(colCount++).setCellStyle(style);
			}
		}
	}

	protected Integer setTitle(Class<?> clazz, Row header, CellStyle style, Integer colCount,
			Map<String, String> colValue, Map<String, String> dynamicColumns) throws ClassNotFoundException {
		// 获取父节点的title
		if (clazz.getGenericSuperclass() != null) {
			Class<?> supperClazz = clazz.getSuperclass();
			colCount = setTitle(supperClazz, header, style, colCount, colValue ,dynamicColumns);
		}
		
		// 获取当前类的title
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String fieldName = field.getName();
			if (colValue != null && !colValue.containsKey(fieldName)) {
				continue;
			}
			String fieldDescription = MessageUtils
					.getLocaleMessage("export." + clazz.getSimpleName() + "." + fieldName);
			if(StringUtils.isBlank(fieldDescription)) {
				//国际化资源文件里没有的时候从动态列里面取数
				if(dynamicColumns != null && !dynamicColumns.containsKey(fieldName)) {
					fieldDescription = dynamicColumns.get(fieldName);
				}
				fieldDescription = dynamicColumns.get(fieldName);
			}
			
			if (StringUtils.isNotBlank(fieldDescription)) {
				if (colValue == null) {
					header.createCell(colCount).setCellValue(fieldDescription);
					header.getCell(colCount++).setCellStyle(style);
				} else {
					colValue.put(fieldName, fieldDescription);
				}
			}
		}
		return colCount;
	}

	protected void createRow(List<Object> list, Sheet sheet, Map<String, String> colValue, Map<String, String> dynamicColumns, Map<String, Object> renderColumns) throws Exception {
		int rowCount = startRow;
		for (Object obj : list) {
			Row aRow = sheet.createRow(rowCount++);
			int colCount = 0;
			setCellValue(obj, obj.getClass(), aRow, colCount, colValue, dynamicColumns, renderColumns);
			if (colValue != null) {
				for (Entry<String, String> entry : colValue.entrySet()) {
					aRow.createCell(colCount++).setCellValue(entry.getValue());
				}
			}
		}
	}
	
	protected Object getCellValue(Object obj, Class<?> clazz, Row aRow, Integer colCount,
			String col, Map<String, Object> renderColumns) throws Exception {
		return getCellValue(obj, clazz, aRow, colCount, col, Collections.emptyMap(), renderColumns);
	}

	protected Object getCellValue(Object obj, Class<?> clazz, Row aRow, Integer colCount,
			String col, Map<String, String> dynamicColumns, Map<String, Object> renderColumns) throws Exception {
		if (StringUtils.isBlank(col)) {
			return null;
		}
		Object cellValue = null;
		if (obj instanceof Map) {
			String column = col;
			List<String> ks = Arrays.asList(StringUtils.split(column, "."));
			Map<String, Object> map = (Map<String, Object>) obj;
			if (map.containsKey(column)) {
				cellValue = map.get(column);
			} else if (ks.size() > 1) {
				Object value = null;
				for (Iterator<String> iterator = ks.iterator(); iterator.hasNext();) {
					String fieldName = iterator.next();
					value = map.get(fieldName);
					if (value != null && iterator.hasNext()) {
						value = getCellValue(value, value.getClass(), aRow, colCount, iterator.next(), dynamicColumns, renderColumns);
					} else {
						break;
					}
				}
				cellValue = value;
			}
		} else {
			Field field = null;
			Object value = null;
			String column = col;
			List<String> columns = Arrays.asList(StringUtils.split(column, " "));
			for (Iterator<String> columnIterator = columns.iterator(); columnIterator.hasNext();) {
				String subColumn = (String) columnIterator.next();
				
//				List<String> ks = Arrays.asList(StringUtils.split(column, "."));
				List<String> ks = Arrays.asList(StringUtils.split(subColumn, "."));
				for (Iterator<String> iterator = ks.iterator(); iterator.hasNext();) {
					String fieldName = iterator.next();
					try {
						if (clazz.getGenericSuperclass() != null) {
							Class<?> supperClazz = clazz.getSuperclass();
							value = getCellValue(obj, supperClazz, aRow, colCount, fieldName, dynamicColumns, renderColumns);
						}
						try {
							if (value == null) {
								field = clazz.getDeclaredField(fieldName);
								field.setAccessible(true);
								value = field.get(obj);
							}
						} catch (NoSuchFieldException e) {}
						try {
							if (value == null) {
								PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
								Method getMethod = pd.getReadMethod();//获得get方法
								value = getMethod.invoke(obj);//执行get方法返回一个Object
							}
						} catch (IntrospectionException e) {}
						if (value != null && iterator.hasNext()) {
							value = getCellValue(value, value.getClass(), aRow, colCount, iterator.next(), dynamicColumns, renderColumns);
						} else {
							break;
						}
					} catch (NoSuchFieldException | NoSuchMethodException | IntrospectionException e) {
						break;
					}
				}
				if (value != null) {
					break;
				}
			}
			cellValue = value;
		}
		try {
			if (renderColumns.containsKey(col) && obj.getClass().equals(clazz)) {
				Object render = renderColumns.get(col);
				Invocable invocable = null;
				if (render instanceof String) {
					ScriptEngineManager engineManager = new ScriptEngineManager();
					ScriptEngine jsEngine = engineManager.getEngineByName("nashorn");
					jsEngine.eval("var render = " + (String) renderColumns.get(col));
					invocable = (Invocable) jsEngine;
				} else if (render instanceof Invocable) {
					invocable = (Invocable) render;
				}
				if (invocable != null) {
					Object options = ((ScriptEngine) invocable).get("options");
					Object renderValue = invocable.invokeFunction("render", cellValue, null, obj, options);
					if (renderValue != null) {
						cellValue = renderValue;
					}
				}
			}
		} catch (Exception e) {
			Object remove = renderColumns.remove(col);
//			System.err.println(((ScriptEngine) remove).get("renderJS"));
//			e.printStackTrace();
		}
		return cellValue;
	}
	
	protected Integer setCellValue(Object obj, Class<?> clazz, Row aRow, Integer colCount,
			Map<String, String> colValue, Map<String, String> dynamicColumns, Map<String, Object> renderColumns) throws Exception {
		for (String fieldName : colValue.keySet()) {
			Object value = getCellValue(obj, clazz, aRow, colCount, fieldName, dynamicColumns, renderColumns);
			if (!(obj instanceof Map)) {
				String fieldDescription = MessageUtils
						.getLocaleMessage("export." + clazz.getSimpleName() + "." + fieldName);
				if (StringUtils.isBlank(fieldDescription)) {
					if (dynamicColumns == null || !dynamicColumns.containsKey(fieldName)) {
						continue;
					}
				}
			}
			colCount = setCellValue(fieldName, value, colValue, aRow, colCount);
		}
		
//		if (obj instanceof Map) {
//			for (Entry<String, Object> entry : ((Map<String, Object>) obj).entrySet()) {
//				String fieldName = entry.getKey();
//				Object value = entry.getValue();
//				if (colValue != null && !colValue.containsKey(fieldName)) {
//					continue;
//				}
//				setCellValue(fieldName, value, colValue, aRow, colCount);
//			}
//		} else {
//			if (clazz.getGenericSuperclass() != null) {
//				Class<?> supperClazz = clazz.getSuperclass();
//				colCount = setCellValue(obj, supperClazz, aRow, colCount, colValue ,dynamicColumns);
//			}
//			Field[] fields = clazz.getDeclaredFields();
//			for (int colIndex = 0; colIndex < fields.length; colIndex++) {
//				Field field = fields[colIndex];
//				field.setAccessible(true);
//				String fieldName = field.getName();
//				if (colValue != null && !colValue.containsKey(fieldName)) {
//					continue;
//				}
//				String fieldDescription = MessageUtils
//						.getLocaleMessage("export." + clazz.getSimpleName() + "." + fieldName);
//				if (StringUtils.isBlank(fieldDescription)) {
//					if(dynamicColumns == null || !dynamicColumns.containsKey(fieldName)) {
//						continue;
//					}
//				}
//				Object value = field.get(obj);
//				colCount = setCellValue(fieldName, value, colValue, aRow, colCount);
//			}
//		}
		return colCount;
	}

	protected Integer setCellValue(String fieldName, Object value, Map<String, String> colValue, Row aRow, Integer colCount) {
		if (value != null) {
			String valueClasss = value.getClass().getSimpleName();
			if (valueClasss.equals("Date")) {
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
			}
			if (colValue == null) {
				aRow.createCell(colCount++).setCellValue(String.valueOf(value));
			} else {
				colValue.put(fieldName, String.valueOf(value));
			}
		} else {
			if (colValue == null) {
				aRow.createCell(colCount++).setCellValue("");
			} else {
				colValue.put(fieldName, "");
			}
		}
		return colCount;
	}

	public boolean isNeedParseTitle() {
		return needParseTitle;
	}

	public void setNeedParseTitle(boolean needParseTitle) {
		this.needParseTitle = needParseTitle;
	}

	public boolean isOneSheetOnly() {
		return oneSheetOnly;
	}

	public void setOneSheetOnly(boolean oneSheetOnly) {
		this.oneSheetOnly = oneSheetOnly;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	
	
}
