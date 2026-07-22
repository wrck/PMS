package com.dp.plat.common.result;

import com.dp.plat.framework.common.exception.ErrorCode;
import com.dp.plat.framework.common.exception.ServiceException;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Unified API response wrapper.
 *
 * <p>历史遗留返回包装器，与 yudao {@code CommonResult} 共存。
 * 新代码推荐直接使用 {@link com.dp.plat.framework.common.pojo.CommonResult}。
 * 本类保留 {@code code=200} 成功码以保证存量接口契约不变；新增的
 * {@link #error(ErrorCode)} / {@link #error(ServiceException)} 桥接方法
 * 使其可与 yudao 异常体系互操作。
 *
 * @param <T> the type of the data payload
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS_CODE, ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS_CODE, ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ERROR_CODE, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

    // ========== yudao 体系桥接方法 ==========

    /**
     * 桥接 yudao {@link ErrorCode}：按其 code/msg 构造失败结果。
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    /**
     * 桥接 yudao {@link ErrorCode}：支持 {} 占位符参数。
     */
    public static <T> Result<T> error(ErrorCode errorCode, Object... params) {
        String msg = com.dp.plat.framework.common.exception.util.ServiceExceptionUtil
                .doFormat(errorCode.getCode(), errorCode.getMsg(), params);
        return new Result<>(errorCode.getCode(), msg, null);
    }

    /**
     * 桥接 yudao {@link ServiceException}：按其 code/message 构造失败结果。
     */
    public static <T> Result<T> error(ServiceException ex) {
        return new Result<>(ex.getCode(), ex.getMessage(), null);
    }
}

