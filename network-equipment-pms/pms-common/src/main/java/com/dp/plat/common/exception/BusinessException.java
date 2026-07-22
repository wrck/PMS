package com.dp.plat.common.exception;

import com.dp.plat.common.result.ResultCode;

/**
 * 业务异常（兼容层，委托 yudao ServiceException 语义）。
 * <p>yudao {@code ServiceException} 是 {@code final} 类不可继承，
 * 因此本异常直接继承 {@code RuntimeException} 并保留 {@code code} 字段。</p>
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 1001;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 1001;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
