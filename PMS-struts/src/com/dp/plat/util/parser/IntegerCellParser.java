package com.dp.plat.util.parser;

/**
 * @author Administrator
 * 
 */
public class IntegerCellParser implements ICellValueParser {
	int value;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dp.plat.util.parser.ICellValueParser#getCellValue()
	 */
	@Override
	public Integer getCellValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dp.plat.util.parser.ICellValueParser#setCellValue(java.lang.Object)
	 */
	@Override
	public void setCellValue(Object cellValue) {
		if (cellValue != null) {
			try {
				this.value = Integer.parseInt(cellValue.toString());
			} catch (NumberFormatException e) {
				this.value = (int) Float.parseFloat(cellValue.toString());
			}
		}
	}
}
