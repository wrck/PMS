package com.dp.plat.security.csrf;

public class CsrfValidateFailedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String message;
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public CsrfValidateFailedException(String message) {
		super();
		this.message = message;
	}
}
