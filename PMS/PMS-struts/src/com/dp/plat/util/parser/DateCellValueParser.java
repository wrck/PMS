package com.dp.plat.util.parser;

import java.util.Date;

/**
 * @author Administrator
 * 
 */
public class DateCellValueParser implements ICellValueParser {
	Date date;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dp.plat.util.parser.ICellValueParser#getCellValue()
	 */
	@Override
	public Date getCellValue() {
		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dp.plat.util.parser.ICellValueParser#setCellValue(java.lang.Object)
	 */
	@Override
	public void setCellValue(Object cellValue) {
		if (cellValue instanceof Date) {
			this.date = (Date) cellValue;
		}
	}
}
