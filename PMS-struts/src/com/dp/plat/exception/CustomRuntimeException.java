/**
 * 
 */
package com.dp.plat.exception;

/**
 * 文件上传异常
 * @author w02611
 *
 */
public class CustomRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -7682210354187194940L;

	/**
	 * 
	 */
	public CustomRuntimeException() {
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CustomRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CustomRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CustomRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CustomRuntimeException(Throwable cause) {
		super(cause);
	}

	
}
