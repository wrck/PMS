package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * UserLogin entity - migrated from Struts
 */
@Data
@TableName("fnd_user_login_record")
public class UserLogin extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("userId")
    private Integer userId;

    @TableField("username")
    private String username;

    @TableField("loginIp")
    private String loginIp;

    @TableField("loginTime")
    private LocalDateTime loginTime;

    @TableField("loginStatus")
    private String loginStatus;

}