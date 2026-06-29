package com.dp.plat.prob.exception;

import com.dp.plat.exception.CustomRuntimeException;

public class NoMatchedSoftVersionStrategyExecption extends CustomRuntimeException {

    private static final long serialVersionUID = 132412162734602152L;

    /**
     * 
     */
    public NoMatchedSoftVersionStrategyExecption() {
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public NoMatchedSoftVersionStrategyExecption(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public NoMatchedSoftVersionStrategyExecption(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public NoMatchedSoftVersionStrategyExecption(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public NoMatchedSoftVersionStrategyExecption(Throwable cause) {
        super(cause);
    }
}
