package com.dp.plat.util.parser;

/**
 * @author Administrator
 * 
 */
public class FloatCellParser implements ICellValueParser {
	float value;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dp.plat.util.parser.ICellValueParser#getCellValue()
	 */
	@Override
	public Float getCellValue() {
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
		Object obj = cellValue;
		if (cellValue != null) {
			if(obj.toString().indexOf(" ") != -1){
				this.value = 0;
			}else{
				this.value = Float.parseFloat(cellValue.toString());
			}
		}
		/*if (cellValue != null) {
			this.value = Float.parseFloat(cellValue.toString());
		}*/
	}
}
