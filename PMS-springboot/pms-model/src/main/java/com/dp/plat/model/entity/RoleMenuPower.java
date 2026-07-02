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
 * RoleMenuPower entity - migrated from Struts
 */
@Data
@TableName("fnd_role_menu_power")
public class RoleMenuPower extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("roleId")
    private Integer roleId;

    @TableField("menuCode")
    private String menuCode;

    @TableField("menuValue")
    private Integer menuValue;

}