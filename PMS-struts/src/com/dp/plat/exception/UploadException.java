/**
 * 
 */
package com.dp.plat.exception;

/**
 * 文件上传异常
 * @author w02611
 *
 */
public class UploadException extends CustomRuntimeException {

	private static final long serialVersionUID = -7682210354187194940L;

	/**
	 * 
	 */
	public UploadException() {
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UploadException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UploadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UploadException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UploadException(Throwable cause) {
		super(cause);
	}

	
}
