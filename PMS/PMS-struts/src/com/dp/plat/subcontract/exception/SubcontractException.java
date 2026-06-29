/**
 * 
 */
package com.dp.plat.subcontract.exception;

import com.dp.plat.exception.CustomRuntimeException;

/**
 * 项目转包自定义异常
 * @author w02611
 *
 */
public class SubcontractException extends CustomRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5107226661974984387L;

	/**
	 * 
	 */
	public SubcontractException() {
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SubcontractException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SubcontractException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SubcontractException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SubcontractException(Throwable cause) {
		super(cause);
	}

	
}
