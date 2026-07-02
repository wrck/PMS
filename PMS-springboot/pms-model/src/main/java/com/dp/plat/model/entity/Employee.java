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
 * Employee entity - migrated from Struts
 */
@Data
@TableName("ehr_employee")
public class Employee extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("employeeCode")
    private String employeeCode;

    @TableField("employeeName")
    private String employeeName;

    @TableField("departmentCode")
    private String departmentCode;

    @TableField("jobCode")
    private String jobCode;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("status")
    private Integer status;

}