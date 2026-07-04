package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 异常日志实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_exception_log")
public class ExceptionLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID */
    private Long userId;

    /** 用户账号 */
    private String username;

    /** 请求 URI */
    private String requestUri;

    /** 请求方法：GET/POST 等 */
    private String requestMethod;

    /** 请求参数（TEXT） */
    private String requestParams;

    /** 异常类型 */
    private String exceptionType;

    /** 异常消息（TEXT） */
    private String exceptionMessage;

    /** 完整堆栈信息（LONGTEXT） */
    private String stackTrace;

    /** 请求 IP */
    private String requestIp;

    /** 发生时间 */
    private LocalDateTime occurTime;
}
