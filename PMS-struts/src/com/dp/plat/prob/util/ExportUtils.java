/**
 * 
 */
package com.dp.plat.prob.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.alibaba.fastjson.JSON;
import com.dp.plat.util.StringEscUtil;

/**
 * 导出xlsx工具
 * 
 * @author w02611
 *
 */
public class ExportUtils {

	public static Workbook buildExcelDocument(List<?> list) throws Exception {
		// get data model which is passed by the Spring container
		Map<String, String> colValue = null;
		// HSSFWorkbook workbook = new HSSFWorkbook();
		XSSFWorkbook workbook = new XSSFWorkbook();
		// create a new Excel sheet
		Sheet sheet = workbook.createSheet("sheet1");
		sheet.setDefaultColumnWidth(30);

		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);

		// create header row
		Row header = sheet.createRow(0);

		if (list != null && !list.isEmpty()) {
			Object obj = list.get(0);
			Integer colCount = 0;
			setHeader(obj.getClass(), header, style, colCount, colValue);
		} else {
			return null;
		}
		// create data rows
		createRow(list, sheet, colValue);
		return workbook;
	}

	/**
	 * 从Excel读取数据
	 * @param <T>
	 * @param files
	 * @param fileNamesStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> readFromExcel(File[] files, String fileNamesStr, Class<T> clazz)
			throws Exception {
		return readFromExcel(files, fileNamesStr, clazz, Collections.emptyMap());
	}
	
	/**
	 * 从Excel读取数据
	 * @param <T>
	 * @param files
	 * @param fileNamesStr
	 * @param clazz
	 * @param titleKeyPrefix 本地资源文件标题前缀
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> readFromExcel(File[] files, String fileNamesStr, Class<T> clazz, String titleKeyPrefix)
            throws Exception {
	    Map<String, String> titleMap = new HashMap<String, String>();
	    ReflectionUtils.doWithFields(clazz, new FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                String text = StringEscUtil.getText(titleKeyPrefix + field.getName()); 
                titleMap.put(text, field.getName());
            }
	        
	    });
        return readFromExcel(files, fileNamesStr, clazz, titleMap);
    }
	
	/**
	 * 从Excel读取数据
	 * @param <T>
	 * @param files
	 * @param fileNamesStr
	 * @param clazz
	 * @param titleMap 标题和字段名称映射关系
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> readFromExcel(File[] files, String fileNamesStr, Class<T> clazz, Map<String, String> titleMap)
            throws Exception {
	    if (titleMap == null) {
	        titleMap = Collections.emptyMap();
	    }
        List<T> result = new ArrayList<>();
        String[] fileNames = fileNamesStr.split(",");
//      String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            String fileName = fileNames[index];
//          File file = new File(targetDirectory + File.separator + fileName);
            if (file.isFile()) {
                String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                if (".xls".equalsIgnoreCase(suffix) || ".xlsx".equalsIgnoreCase(suffix)) {
                    InputStream inputStream = new FileInputStream(file);
//                  if (POIFSFileSystem.hasPOIFSHeader(inputStream) || POIXMLDocument.hasOOXMLHeader(inputStream)) {
                        Workbook workbook = WorkbookFactory.create(inputStream);
                        int sheetNumber = workbook.getNumberOfSheets();
                        if (sheetNumber > 0) {
                            // // 获取该对象的class对象
                            // Class<?> clazz = object.getClass();
                            // 获得该类的所有属性
//                          Field[] fields = clazz.getDeclaredFields();

                            // 读取excel数据
                            // 获得指定的excel表
                            Sheet sheet = workbook.getSheetAt(0);
                            // 获取表格的总行数
                            int rowCount = sheet.getLastRowNum() + 1; // 需要加一
                            if (rowCount < 1) {
                                return result;
                            }
                            // 获取表头的列数
                            int columnCount = sheet.getRow(0).getLastCellNum();
                            // 读取表头信息,确定需要用的方法名---set方法
                            // 用于存储方法名
                            String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数
                            // 用于存储属性类型
                            String[] fieldTypes = new String[columnCount];
                            // 用于属性类型
                            Field[] fields = new Field[columnCount];
                            // 获得表头行对象
                            Row titleRow = sheet.getRow(0);
                            // 遍历
                            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列
                                String data = titleRow.getCell(columnIndex).toString(); // 某一列的内容
                                data = titleMap.getOrDefault(data, data);
                                String Udata = Character.toUpperCase(data.charAt(0)) + data.substring(1, data.length()); // 使其首字母大写
                                methodNames[columnIndex] = "set" + Udata;
                                Field field = ReflectionUtils.findField(clazz, data);
//                              Field field = clazz.getDeclaredField(data);
                                if (field != null) {
                                    fieldTypes[columnIndex] = field.getType().getName();
                                    fields[columnIndex] = field;
                                }
                                
                                /*for (int i = 0; i < fields.length; i++) { // 遍历属性数组
                                    if (data.equals(fields[i].getName())) { // 属性与表头相等
                                        fieldTypes[columnIndex] = fields[i].getType().getName(); // 将属性类型放到数组中
                                        break;
                                    }
                                }*/
                            }
                            // 逐行读取数据 从1开始 忽略表头
                            for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
                                // 获得行对象
                                Row row = sheet.getRow(rowIndex);
                                if (row != null) {
                                    T obj = null;
                                    // 实例化该泛型类的对象一个对象
                                    try {
                                        obj = clazz.newInstance();
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                    // 获得本行中各单元格中的数据
                                    boolean useJson = true;
                                    Map<String, Object> objMap = new HashMap<String, Object>();
                                    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                                        String data = "";
                                        try {
                                            Cell cell = row.getCell(columnIndex);
                                            cell.setCellType(CellType.STRING);
                                            data = StringUtils.trimToEmpty(cell.toString());
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                        
                                        // 使用JSON来进行转换
                                        if (useJson) {
                                            // 如果没有指定的字段，借助Map进行值拷贝
                                            Field field = fields[columnIndex];
                                            if (field != null) {
                                                objMap.put(field.getName(), data);
                                            }
                                            continue;
                                        }

                                        // 获取要调用方法的方法名
                                        String methodName = methodNames[columnIndex];
                                        Method method = null;
                                        try {
                                            String fieldType = fieldTypes[columnIndex];
                                            // 这部分可自己扩展
                                            if ("java.lang.String".equals(fieldType)) {
                                                method = clazz.getDeclaredMethod(methodName, String.class); // 设置要执行的方法--set方法参数为String
                                                method.invoke(obj, data); // 执行该方法
                                            } else if ("int".equals(fieldType)) {
                                                method = clazz.getDeclaredMethod(methodName, int.class); // 设置要执行的方法--set方法参数为int
                                                double data_double = Double.parseDouble(data);
                                                int data_int = (int) data_double;
                                                method.invoke(obj, data_int); // 执行该方法
//                                            } else if ("java.lang.Integer".equals(fieldType)) {
//                                                method = clazz.getDeclaredMethod(methodName, Integer.class); // 设置要执行的方法--set方法参数为int
//                                                double data_double = Double.parseDouble(data);
//                                                int data_int = (int) data_double;
//                                                method.invoke(obj, data_int); // 执行该方法
                                            } else {
                                                // 如果没有指定的字段，借助Map进行值拷贝
                                                Field field = fields[columnIndex];
                                                if (field != null) {
                                                    field.setAccessible(true);
                                                    Map<String, String> map = Collections.singletonMap(field.getName(), data);
                                                    Object tmp = JSON.parseObject(JSON.toJSONString(map), clazz);
                                                    field.set(obj, field.get(tmp));
                                                }
                                            }
                                        } catch (NoSuchMethodException e) {
                                            throw new NoSuchMethodException("指定的标题栏与实体不匹配");
                                        } catch (NullPointerException e) {
                                            throw new NullPointerException("文件标题栏字段不正确！");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    // 使用JSON来进行转换
                                    if (useJson) {
                                        obj = JSON.parseObject(JSON.toJSONString(objMap), clazz);
                                    }
                                    result.add(obj);
                                }
                            }
                        }
//                  }

                }
            }
        }
        return result;
    }

	public static void writeToResponse(Workbook workbook, HttpServletResponse response, String fileName)
			throws IOException {
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();

		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
	}

	private static void setHeader(Class<?> clazz, Row header, CellStyle style, Integer colCount,
			Map<String, String> colValue) {
		setTitle(clazz, header, style, colCount, colValue);
		if (colValue != null) {
			for (Entry<String, String> entry : colValue.entrySet()) {
				header.createCell(colCount).setCellValue(entry.getValue());
				header.getCell(colCount++).setCellStyle(style);
			}
		}
	}

	private static Integer setTitle(Class<?> clazz, Row header, CellStyle style, Integer colCount,
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
			String key = "export." + clazz.getSimpleName() + "." + fieldName;
			String fieldDescription = StringEscUtil.getText(key);
			if (StringUtils.isNotBlank(fieldDescription) && !key.equals(fieldDescription)) {
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

	private static void createRow(List<?> list, Sheet sheet, Map<String, String> colValue) throws Exception {
		int rowCount = 1;
		for (Object obj : list) {
			Row aRow = sheet.createRow(rowCount++);
			int colCount = 0;
			setCellValue(obj, obj.getClass(), aRow, colCount, colValue);
			if (colValue != null) {
				for (Entry<String, String> entry : colValue.entrySet()) {
					aRow.createCell(colCount++).setCellValue(entry.getValue());
				}
			}

		}
	}

	private static Integer setCellValue(Object obj, Class<?> clazz, Row aRow, Integer colCount,
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
			String key = "export." + clazz.getSimpleName() + "." + fieldName;
			String fieldDescription = StringEscUtil.getText(key);
			if (StringUtils.isBlank(fieldDescription) || key.equals(fieldDescription)) {
				continue;
			}
			Object value = field.get(obj);
			if (value != null) {
				String valueClasss = value.getClass().getSimpleName();
				if (valueClasss.equals("Date")) {
					value = new SimpleDateFormat("yyyy-MM-dd mm:HH:ss").format(value);
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
