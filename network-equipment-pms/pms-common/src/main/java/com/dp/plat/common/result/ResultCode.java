package com.dp.plat.common.result;

import lombok.Getter;

/**
 * Common result codes enum.
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    UNAUTHORIZED(401, "未认证或认证已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数校验错误"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "数据冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    // 集成服务（D365/FP/OA）调用失败 / 熔断降级，统一返回 503，区分于内部 5xx
    INTEGRATION_FAILURE(503, "集成服务暂不可用"),
    BUSINESS_ERROR(1001, "业务异常"),
    TOKEN_INVALID(1002, "Token 无效"),
    TOKEN_EXPIRED(1003, "Token 已过期"),
    ACCOUNT_LOCKED(1004, "账号已被锁定"),
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1006, "用户名或密码错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
