package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体 - 对应老系统 fnd_roles 表
 */
@Data
@TableName("fnd_roles")
public class SysRole extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    @TableField("roleName")
    private String roleName;

    /** 角色编码 */
    @TableField("roleCode")
    private String roleCode;

    /** 菜单权限ID列表 */
    @TableField("menuIds")
    private String menuIds;

    /** 状态: 1=启用, 0=禁用 */
    @TableField("status")
    private Integer status;

    /** 默认页面 */
    @TableField("defaultPage")
    private String defaultPage;

    /** 过期时间 */
    @TableField("expireTime")
    private LocalDateTime expireTime;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;
}
