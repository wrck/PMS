package com.dp.plat.pms.extend.d365.exception;

public class CustomRuntimeException extends RuntimeException {

    public CustomRuntimeException() {
        super();
    }

    public CustomRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CustomRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomRuntimeException(String message) {
        super(message);
    }

    public CustomRuntimeException(Throwable cause) {
        super(cause);
    }


}
