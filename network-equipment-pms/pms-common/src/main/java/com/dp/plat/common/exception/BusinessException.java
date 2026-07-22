package com.dp.plat.common.exception;

import com.dp.plat.common.result.ResultCode;
import com.dp.plat.framework.common.exception.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * Custom business exception with code and message.
 *
 * <p>历史遗留异常，与 yudao {@link com.dp.plat.framework.common.exception.ServiceException} 共存。
 * 新代码推荐直接使用 {@code ServiceException}。新增 {@link #BusinessException(ErrorCode)}
 * 构造方法以支持 yudao {@link ErrorCode} 互操作。
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 桥接 yudao {@link ErrorCode}：按其 code/msg 构造业务异常。
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }
}

