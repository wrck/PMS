package com.dp.plat.core.exception;
/**
 * 	文件上传异常
 * @author j01441
 *
 */
public class UploadException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public UploadException(String message) {
		super();
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
