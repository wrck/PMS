package com.dp.plat.core.exception;

/**
 * 模板数据导入错误
 * 
 * @author x02561
 *
 */
public class ExcelImportException extends RuntimeException implements CustomExceptionInterface {

	private static final long serialVersionUID = 1L;

	private String progress;

	public ExcelImportException(String message) {
		super(message);
	}

	public ExcelImportException(String message, String progress) {
		super(message);
		this.progress = progress;
	}

	public String getProgress() {
		return progress;
	}

}
