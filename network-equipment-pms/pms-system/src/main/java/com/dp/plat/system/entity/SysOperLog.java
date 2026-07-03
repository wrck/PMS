package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Operation log entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {

    private String title;

    private Integer businessType;

    private String method;

    private String requestMethod;

    private String operName;

    private String operUrl;

    private String operParam;

    private String jsonResult;

    /** 0=success, 1=failure. */
    private Integer status;

    private String errorMsg;

    private LocalDateTime operTime;
}
