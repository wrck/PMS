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
 * EHRLoginAccount entity - migrated from Struts
 */
@Data
@TableName("ehr_login_account")
public class EHRLoginAccount extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("employeeCode")
    private String employeeCode;

    @TableField("loginAccount")
    private String loginAccount;

    @TableField("loginPassword")
    private String loginPassword;

    @TableField("status")
    private Integer status;

}