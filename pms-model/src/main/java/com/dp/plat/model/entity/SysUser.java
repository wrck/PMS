package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体 - 对应老系统 fnd_user_info 表
 */
@Data
@TableName("fnd_user_info")
public class SysUser extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（MD5加密） */
    private String password;

    /** 邮箱 */
    private String email;

    /** 真实姓名 */
    @TableField("realName")
    private String realname;

    /** 状态: 1=启用, 0=禁用 */
    private Integer status;

    /** 部门编码 */
    @TableField("dpNo")
    private String deptCode;

    /** 角色ID列表（分号分隔，如 ;1;2;3;） */
    @TableField("roleIds")
    private String roleIds;

    /** 密码过期时间 */
    @TableField("pwdoverdue")
    private LocalDateTime pwdOverdue;

    /** 默认首页路径 */
    @TableField("defaultPage")
    private String defaultPage;

    /** 是否发送邮件: 1=是, 0=否 */
    @TableField("isemail")
    private Integer isEmail;

    /** 自定义信息 */
    @TableField("customInfo")
    private String customInfo;

    /** 创建人 */
    @TableField("createBy")
    private String createBy;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;

    /** 更新人 */
    @TableField("updateBy")
    private String updateBy;

    /** 更新时间 */
    @TableField("updateTime")
    private LocalDateTime updateTime;

    /** 生效开始时间 */
    @TableField("effectiveFrom")
    private LocalDateTime effectiveFrom;

    /** 生效结束时间 */
    @TableField("effectiveTo")
    private LocalDateTime effectiveTo;

    // ===== 非数据库字段 =====

    /** 部门名称（关联查询） */
    @TableField(exist = false)
    private String deptName;

    /** 角色名称（关联查询） */
    @TableField(exist = false)
    private String roleName;

    /** 区域权限 */
    @TableField(exist = false)
    private String areaPower;

    /**
     * 判断是否拥有指定角色
     */
    public boolean hasRole(int roleId) {
        return roleIds != null && roleIds.contains(";" + roleId + ";");
    }

    /**
     * 判断是否拥有任意一个指定角色
     */
    public boolean hasAnyRole(int... roleIds) {
        for (int roleId : roleIds) {
            if (hasRole(roleId)) return true;
        }
        return false;
    }
}
