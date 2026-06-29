package com.dp.plat.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已禁用"),
    USER_ALREADY_EXISTS(1004, "用户已存在"),
    OLD_PASSWORD_ERROR(1005, "旧密码错误"),

    // 角色相关 2xxx
    ROLE_NOT_FOUND(2001, "角色不存在"),
    ROLE_ALREADY_EXISTS(2002, "角色已存在"),

    // 部门相关 3xxx
    DEPT_NOT_FOUND(3001, "部门不存在"),
    DEPT_HAS_CHILDREN(3002, "存在子部门，无法删除"),
    DEPT_HAS_USERS(3003, "部门下存在用户，无法删除"),

    // 项目相关 4xxx
    PROJECT_NOT_FOUND(4001, "项目不存在"),
    PROJECT_CODE_EXISTS(4002, "项目编号已存在");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
