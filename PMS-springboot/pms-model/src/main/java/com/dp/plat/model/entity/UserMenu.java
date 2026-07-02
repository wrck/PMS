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
 * UserMenu entity - migrated from Struts
 */
@Data
@TableName("fnd_menus")
public class UserMenu extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("menuCode")
    private String menuCode;

    @TableField("menuName")
    private String menuName;

    @TableField("menuLevel")
    private Integer menuLevel;

    @TableField("superId")
    private Integer superId;

    @TableField("path")
    private String path;

    @TableField("effectiveFrom")
    private LocalDateTime effectiveFrom;

    @TableField("effectiveTo")
    private LocalDateTime effectiveTo;

}