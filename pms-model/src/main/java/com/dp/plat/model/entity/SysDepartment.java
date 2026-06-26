package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体 - 对应老系统 fnd_department 表
 */
@Data
@TableName("fnd_department")
public class SysDepartment extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 部门名称 */
    @TableField("departmentName")
    private String deptName;

    /** 部门编码 */
    @TableField("departmentNum")
    private String deptCode;

    /** 父部门ID */
    @TableField("parentId")
    private Long parentId;

    /** 排序 */
    @TableField("sort")
    private Integer sort;

    /** 状态: 1=启用, 0=禁用 */
    @TableField("status")
    private Integer status;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;

    // ===== 非数据库字段 =====

    /** 子部门列表 */
    @TableField(exist = false)
    private java.util.List<SysDepartment> children;
}
