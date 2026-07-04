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
 * 登录日志实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_login_log")
public class LoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 登录时间 */
    private LocalDateTime loginTime;

    /** 登录 IP */
    private String loginIp;

    /** 登录地点 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录状态：SUCCESS/FAIL */
    private String status;

    /** 提示消息 */
    private String message;

    /** 用户 ID */
    private Long userId;
}
