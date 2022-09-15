package com.dp.plat.core.exception;

/**
 * 运行异常
 * 
 * @author w2611
 */
public class CustomRuntimeException extends RuntimeException implements CustomExceptionInterface {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public CustomRuntimeException(String message) {
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
