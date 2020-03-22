package com.dp.plat.util.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelParser
{
	private static final int DATECELL = 3;
	private static final int STRINGCELL = 1;
	private static final int DEFALTCELL = 0;
	private FileInputStream in;
	private Workbook workbook;

	/**
	 * 构造完成后，主动调用loadExcel和parserExcel方法来完成解析，需要指定CellValueParser数组，
	 * 对应于表格中的某一个行的解析器
	 */
	public ExcelParser()
	{
	}

	/**
	 * 传入一个文件类型的输入流，然后进行后续的parserExcel操作，可以指定CellValueParser数组用于解析对应的结果
	 * 
	 * @param input
	 */
	public ExcelParser(FileInputStream input)
	{
		this.in = input;
	}

	/**
	 * 传入需要解析的文件名，由本类内部进行Excel的装载，然后调用parserExcel进行解析，
	 * 可以指定CellValueParser数组用于解析对应的结果
	 * 
	 * @param filepath
	 *            文件完整路径
	 * @throws InvalidFormatException
	 *             当Excel格式不正确时抛出
	 * @throws IOException
	 *             当Excel文件出现IO错误时抛出，例如无法访问，文件不存在等等
	 */
	public ExcelParser(String filepath) throws InvalidFormatException,
			IOException
	{
		this.loadExcel(filepath);
	}

	/**
	 * 获取单元格的具体值，按照单元格的不同类型进行获取
	 * 
	 * @param row
	 *            实际的一行表格数据实体
	 * @param k
	 *            单元格序号
	 */
	private Object getCellValue(Row row, short k, int cellType)
	{
		Cell cell = row.getCell(k);
		if (cell != null)
		{
			if (cellType == STRINGCELL)
			{
				try
				{
					return cell.getRichStringCellValue().getString();
				}
				catch (Exception e)
				{
					return cell.getNumericCellValue();
				}
			}
			CellType type = cell.getCellType();
			switch (type)
			{
			case ERROR:
				System.out.println("Error data -- " + k);
			case BLANK:
				return null;
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case NUMERIC:
				if (cellType == DATECELL)
				{
					try
					{
						if (DateUtil.isValidExcelDate(cell
								.getNumericCellValue()))
						{
							return cell.getDateCellValue();
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					return cell.getNumericCellValue();
				}
				break;
			case STRING:
				return cell.getRichStringCellValue().getString();
			case FORMULA:
				return cell.getCachedFormulaResultType();
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * 通过传入的文件输入流进行Excel的解析
	 * 
	 * @param in
	 *            需要解析的Excel文件输入流
	 * @throws InvalidFormatException
	 *             当Excel格式不正确时抛出
	 * @throws IOException
	 *             当Excel文件出现IO错误时抛出，例如无法访问，文件不存在等等
	 */
	public void loadExcel(FileInputStream in) throws InvalidFormatException,
			IOException
	{
		this.in = in;
	}

	/**
	 * 通过传入的文件路径进行Excel的解析
	 * 
	 * @param filePath
	 *            需要解析的Excel的全路径
	 * @throws InvalidFormatException
	 *             当Excel格式不正确时抛出
	 * @throws IOException
	 *             当Excel文件出现IO错误时抛出，例如无法访问，文件不存在等等
	 */
	public void loadExcel(String filePath) throws InvalidFormatException,
			IOException
	{
		in = new FileInputStream(filePath);
	}

	/**
	 * 解析Excel，通过预先设置好的文件流或者文件参数进行解析
	 * 
	 * @throws InvalidFormatException
	 *             当Excel格式不正确时抛出
	 * @throws IOException
	 *             当Excel文件出现IO错误时抛出，例如无法访问，文件不存在等等
	 */
	public List<SheetSize> parserExcel() throws InvalidFormatException,
			IOException
	{
		workbook = WorkbookFactory.create(in);
		List<SheetSize> sizes = new ArrayList<SheetSize>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++)
		{
			Sheet sheet = workbook.getSheetAt(i);
			SheetSize sheetSize = new SheetSize();
			sheetSize.setRowMin(sheet.getFirstRowNum());
			sheetSize.setRowMax(sheet.getLastRowNum());
			sizes.add(sheetSize);
		}
		return sizes;
	}
	/**
	 * 根据名称返回sheet页
	 * @param sheetName
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public XSSFSheet parserExcel(String sheetName) throws InvalidFormatException, IOException{
		
		workbook = WorkbookFactory.create(in);
		
		return (XSSFSheet) workbook.getSheet(sheetName);
	} 
	/**
	 * 获取sheet页的数据行数
	 * @param sheet
	 * @return
	 */
	public int size(XSSFSheet sheet){
		int frn = sheet.getFirstRowNum();
		int lrn = sheet.getLastRowNum();
		
		return lrn - frn +1;
	}
	
	/**
	 * @param sheetIndex
	 * @param rowIndex
	 * @param columnIndex
	 * @param parser
	 * @param isDate
	 */
	public void parseCell(int sheetIndex, int rowIndex, short columnIndex,
			ICellValueParser parser, boolean isDate)
	{
		if (sheetIndex < workbook.getNumberOfSheets())
		{
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			int firstRow = sheet.getFirstRowNum();
			int lastRow = sheet.getLastRowNum();
			if (rowIndex >= firstRow && rowIndex <= lastRow)
			{
				Row row = sheet.getRow(rowIndex);
				short firstCell = row.getFirstCellNum();
				short lastCell = row.getLastCellNum();
				if (columnIndex >= firstCell && columnIndex <= lastCell){
//					if (parser instanceof ICellValueParser){
						int type = DEFALTCELL;
						if (parser instanceof StringCellParser)
						{
							type = STRINGCELL;
						}
						if (isDate)
						{
							type = DATECELL;
						}
						parser
								.setCellValue(getCellValue(row, columnIndex,
										type));
//					}else{
//						System.out.println(getCellValue(row, columnIndex,
//								isDate ? DATECELL : DEFALTCELL));
//					}
				}
			}
		}
	}

	public void parseCell(int sheetIndex, int rowIndex, short columnIndex,
			ICellValueParser parser)
	{
		this.parseCell(sheetIndex, rowIndex, columnIndex, parser, false);
	}
}
