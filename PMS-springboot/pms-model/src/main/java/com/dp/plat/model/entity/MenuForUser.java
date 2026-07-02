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
 * MenuForUser entity - migrated from Struts
 */
@Data
@TableName("fnd_user_menus")
public class MenuForUser extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("fndUserId")
    private Integer fndUserId;

    @TableField("menuCode")
    private String menuCode;

    @TableField("menuValue")
    private Integer menuValue;

    @TableField("effectiveFrom")
    private LocalDateTime effectiveFrom;

    @TableField("effectiveTo")
    private LocalDateTime effectiveTo;

}