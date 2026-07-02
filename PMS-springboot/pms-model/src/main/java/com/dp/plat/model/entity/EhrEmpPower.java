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
 * EhrEmpPower entity - migrated from Struts
 */
@Data
@TableName("ehr_emp_power")
public class EhrEmpPower extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("employeeCode")
    private String employeeCode;

    @TableField("powerCode")
    private String powerCode;

    @TableField("powerValue")
    private Integer powerValue;

}