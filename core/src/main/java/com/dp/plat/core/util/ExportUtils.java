/**
 * 
 */
package com.dp.plat.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.dp.plat.core.view.ExcelView4XLSX;
import com.dp.plat.core.vo.PageParam;

/**
 * 导出xls工具，用来选择导出实体类的导出字段
 * 
 * @author w02611
 *
 */
public class ExportUtils extends ExcelView4XLSX{

	private static final String[] packagePaths = new String[] { "com.dp.plat.core.vo.", "com.dp.plat.core.pojo.",
			"com.dp.plat.core.param.","com.dp.plat.export.pojo." };
	
	public static Map<String, String> getExportColumns(String objectName) throws ClassNotFoundException {
//		Map<String, String> columns = new LinkedHashMap<>();
//		Class<?> clazz = getClass(objectName);
//		columns = getColumns(clazz, columns);
		return getExportColumns(objectName, null);
	}
	
	public static Map<String, String> getExportColumns(String objectName, Map<String, String> dynamicColumn) throws ClassNotFoundException {
		Map<String, String> columns = new LinkedHashMap<>();
		Class<?> clazz = getClass(objectName);
		columns = getColumns(clazz, columns ,dynamicColumn);
		return columns;
	}
	
	
	public static Map<String, String> getExportColumns(Class<?> clazz, Map<String, String> dynamicColumn) throws ClassNotFoundException {
		Map<String, String> columns = new LinkedHashMap<>();
		columns = getColumns(clazz, columns ,dynamicColumn);
		return columns;
	}
	

	public static PageParam<?> getPageParam(String objectName, String objectKV, String pageParamKV)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = getClass(objectName);
		PageParam<?> pageParam = new PageParam<>();
		String[] pageKV = null;
		if (StringUtils.isNotBlank(pageParamKV)) {
			pageKV = pageParamKV.split(";");
			for (String str : pageKV) {
				String[] kv = str.split("=");
				setField(pageParam.getClass(), pageParam, kv);
			}
		}
		

		Object object = clazz.newInstance();
		String[] objKV = null;
		if (StringUtils.isNotBlank(objectKV)) {
			objKV = objectKV.split(";");
			for (String str : objKV) {
				String[] kv = str.split("=");
				setField(clazz, object, kv);
			}
		}
		
		setField(pageParam.getClass(), pageParam, new Object[] { "model", object });
		return pageParam;
	}

	/**
	 * kv字符串分割成map
	 * @param data
	 * @param inOrder 
	 * @param separators
	 * @return
	 */
	public static Map<String, String> str2KVMap(String data, boolean inOrder, String... separators) {
		Map<String, String> map = new HashMap<>();;
		if (StringUtils.isNotBlank(data)) {
			String separatorL1 = ";";
			String separatorL2 = "=";
			if (separators.length == 2) {
				separatorL1 = StringUtils.isNotBlank(separators[0]) ? separators[0] : separatorL1;
				separatorL2 = StringUtils.isNotBlank(separators[1]) ? separators[1] : separatorL2;
			} else if (separators.length == 1){
				separatorL1 = StringUtils.isNotBlank(separators[0]) ? separators[0] : separatorL1;
			}
			String[] columns = StringUtils.split(data, separatorL1);
			if (inOrder) {
				map = new LinkedHashMap<>(columns.length);
			} else {
				map = new HashMap<>(columns.length);
			}
			for (String column : columns) {
				String[] kv = StringUtils.split(column, separatorL2);
				if (kv == null || kv.length == 0) {
					continue;
				} else if (kv.length == 2) {
					map.put(kv[0], kv[1]);
				} else {
					map.put(column, null);
				}
			}
		}
		return map;
	}
	
	public static Class<?> getClass(String objectName) throws ClassNotFoundException {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(objectName);
		} catch (Exception e) {
			for (String packagePath : packagePaths) {
				try {
					clazz = Class.forName(packagePath + objectName);
					break;
				} catch (Exception e1) {
//					e1.printStackTrace();
				}
			}
		}
		if (clazz == null) {
			throw new ClassNotFoundException(objectName);
		}
		return clazz;
	}

	private static void setField(Class<?> clazz, Object object, Object[] kv) {
		if(kv.length < 2) {
			return;
		}
		Object key = kv[0];
		Object value = kv[1];
		try {
			if (object instanceof Map) {
				((Map) object).put(String.valueOf(key), value);
				return;
			}
			Field field = clazz.getDeclaredField((String) key);
			field.setAccessible(true);
			Class<?> filedType = field.getType();
			if (filedType.equals(Integer.class) || filedType.equals(int.class)) {
				field.set(object, Integer.parseInt((String) value));
			} else if (filedType.equals(Long.class) || filedType.equals(long.class)) {
				field.set(object, Long.parseLong((String) value));
			} else if (filedType.equals(Boolean.class) || filedType.equals(boolean.class)) {
				field.set(object, Boolean.parseBoolean((String) value));
			} else {
				field.set(object, value);
			}
		} catch (NoSuchFieldException e) {
			if (clazz.getGenericSuperclass() != null) {
				Class<?> supperClazz = clazz.getSuperclass();
				setField(supperClazz, object, kv);
			} else {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, String> getColumns(Class<?> clazz, Map<String, String> columns, Map<String, String> dynamicColumn) {
		if (clazz.getGenericSuperclass() != null) {
			Class<?> supperClazz = clazz.getSuperclass();
			getColumns(supperClazz, columns ,dynamicColumn);
		}
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String fieldName = field.getName();
			String fieldDescription = MessageUtils
					.getLocaleMessage("export." + clazz.getSimpleName() + "." + fieldName);
			if (StringUtils.isBlank(fieldDescription)) {
				if(!(dynamicColumn == null ||dynamicColumn.size() == 0 || !dynamicColumn.containsKey(fieldName))) {
					fieldDescription = dynamicColumn.get(fieldName);
				}else {
					continue;
				}
			}
			
			columns.put(fieldName, fieldDescription);
		}
		return columns;
	}
	
	
	
	private int rowAccessWindowSize = 5000;
	
	public ExportUtils() {
		super();
	}
	
	public ExportUtils(int rowAccessWindowSize) {
		super();
		this.rowAccessWindowSize = rowAccessWindowSize;
	}

	public SXSSFWorkbook renderExcelDocument(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook workbook)  throws Exception {
		if (workbook == null) {
			workbook = new SXSSFWorkbook(rowAccessWindowSize);
			workbook.setCompressTempFiles(true);
		}
		
		buildExcelDocument(model, workbook, request, response);
		return workbook;
	}

	public void writeToResponse(HttpServletResponse response, SXSSFWorkbook workbook) throws IOException {
		ByteArrayOutputStream baos = createTemporaryOutputStream();
		if (workbook != null) {
			workbook.write(baos);
		}
		
		// Write content type and also length (determined via byte array).
		response.setContentType(getContentType());
		response.setContentLength(baos.size());

		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		baos.writeTo(out);
		out.flush();
	}
	
	public int getRowAccessWindowSize() {
		return rowAccessWindowSize;
	}

	public void setRowAccessWindowSize(int rowAccessWindowSize) {
		this.rowAccessWindowSize = rowAccessWindowSize;
	}



	

}
