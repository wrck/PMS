package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * System menu entity.
 * menuType: M=directory, C=menu, F=button.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private Long parentId;

    private String menuName;

    /** M=directory, C=menu, F=button. */
    private String menuType;

    private String path;

    private String component;

    private String perms;

    private String icon;

    private Integer orderNum;

    /** 0=visible, 1=hidden. */
    private String visible;
}
