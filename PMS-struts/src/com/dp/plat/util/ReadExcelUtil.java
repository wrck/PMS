package com.dp.plat.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.dp.plat.util.parser.ExcelParser;
import com.dp.plat.util.parser.SheetSize;
import com.dp.plat.util.parser.StringCellParser;

/**
 * 读Excel数据方法
 * @author admin
 *
 */
public class ReadExcelUtil {
	/**
	 * 读取数据
	 * @param filename 文件名
	 * @param filepath 文件所在目录
	 * @param sheetindex 读取excel第几个sheet页
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	public static List<Object[]> parseExcelToList(String filename,String filepath ,int sheetindex,ServletContext context ,int columnIndex ,int rowIndex) throws Exception {
		String targetDirectory = context.getRealPath(filepath);
		
		List<Object[]> result = new ArrayList<Object[]>();
		ExcelParser parser;
		parser = new ExcelParser(targetDirectory + File.separator + filename);
		List<SheetSize> sizes = parser.parserExcel();
		// 从第二行开始读取，第一行是标题
		StringCellParser strCell = new StringCellParser();
		for (int j = rowIndex; j < sizes.get(sheetindex).getRowNum(); j++) {
			Object[] objs = new Object[columnIndex+1];
			objs[0] = j + 1;
			for(int index = 1; index <= columnIndex ;index ++){
				parser.parseCell(sheetindex, j, (short) (index-1), strCell);
				objs[index] = strCell.getCellValue();
				strCell.setCellValue("");
			}	
			result.add(objs);
		}
	
		return result;
	}
	
}
