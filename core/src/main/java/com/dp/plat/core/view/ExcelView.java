package com.dp.plat.core.view;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import com.dp.plat.core.util.MessageUtils;


/**
 * Excel导出视图,根据Controller中返回的Model获取属性名为list的数据导出到Excel,待完善
 * 
 * @author w02611
 *
 */
public class ExcelView extends AbstractExcelView {
	
	public ExcelView() {
		setContentType("application/vnd.ms-excel");
		setExtension(".xls");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// get data model which is passed by the Spring container
		List<Object> list = (List<Object>) model.get("data");
		Map<String, String> colValue = null;
		if (model.containsKey("columns")) {
			String[] columns = (String[]) model.get("columns");
			colValue = new LinkedHashMap<>(columns.length);
			for (String column : columns) {
				colValue.put(column, null);
			}
		}
		List<List<Object>> lists = new ArrayList<>();
		int total = list.size();
		int count = (int) Math.ceil((double) total / 65530);
		for (int i = 0; i < count; i++) {
			lists.add(list.subList(i * 65530, (i + 1) * 65530 > total ? total : (i + 1) * 65530));
		}
		int i = 1;
		for (List<Object> list2 : lists) {
			// create a new Excel sheet
			HSSFSheet sheet = (HSSFSheet) workbook.createSheet("sheet" + (i++));
			sheet.setDefaultColumnWidth(30);

			// create style for header cells
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setFontName("Arial");
			style.setFillForegroundColor(HSSFColorPredefined.BLUE.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			font.setBold(true);
			font.setColor(HSSFColorPredefined.WHITE.getIndex());
			style.setFont(font);

			// create header row
			HSSFRow header = sheet.createRow(0);

			if (list2 != null && !list2.isEmpty()) {
				Object obj = list2.get(0);
				Integer colCount = 0;
				setHeader(obj.getClass(), header, style, colCount, colValue);
			} else {
				return;
			}
			// create data rows
			createRow(list2, sheet, colValue);
		}
	}

	private void setHeader(Class<?> clazz, HSSFRow header, CellStyle style, Integer colCount,
			Map<String, String> colValue) {
		setTitle(clazz, header, style, colCount, colValue);
		if (colValue != null) {
			for (Entry<String, String> entry : colValue.entrySet()) {
				header.createCell(colCount).setCellValue(entry.getValue());
				header.getCell(colCount++).setCellStyle(style);
			}
		}
	}

	private Integer setTitle(Class<?> clazz, HSSFRow header, CellStyle style, Integer colCount,
			Map<String, String> colValue) {
		// 获取父节点的title
		if (clazz.getGenericSuperclass() != null) {
			Class<?> supperClazz = clazz.getSuperclass();
			colCount = setTitle(supperClazz, header, style, colCount, colValue);
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

	private void createRow(List<Object> list, HSSFSheet sheet, Map<String, String> colValue) throws Exception {
		int rowCount = 1;
		for (Object obj : list) {
			HSSFRow aRow = sheet.createRow(rowCount++);
			int colCount = 0;
			setCellValue(obj, obj.getClass(), aRow, colCount, colValue);
			if (colValue != null) {
				for (Entry<String, String> entry : colValue.entrySet()) {
					aRow.createCell(colCount++).setCellValue(entry.getValue());
				}
			}

		}
	}

	private Integer setCellValue(Object obj, Class<?> clazz, HSSFRow aRow, Integer colCount,
			Map<String, String> colValue) throws Exception {
		if (clazz.getGenericSuperclass() != null) {
			Class<?> supperClazz = clazz.getSuperclass();
			colCount = setCellValue(obj, supperClazz, aRow, colCount, colValue);
		}
		Field[] fields = clazz.getDeclaredFields();
		for (int colIndex = 0; colIndex < fields.length; colIndex++) {
			Field field = fields[colIndex];
			field.setAccessible(true);
			String fieldName = field.getName();
			if (colValue != null && !colValue.containsKey(fieldName)) {
				continue;
			}
			String fieldDescription = MessageUtils
					.getLocaleMessage("export." + clazz.getSimpleName() + "." + fieldName);
			if (StringUtils.isBlank(fieldDescription)) {
				continue;
			}
			Object value = field.get(obj);
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
		}
		return colCount;
	}
}
