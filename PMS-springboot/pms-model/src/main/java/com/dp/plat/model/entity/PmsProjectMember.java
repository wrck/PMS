package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成员实体 - 对应老系统 pm_project_member 表
 */
@Data
@TableName("pm_project_member")
public class PmsProjectMember extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 项目类型 (10=售后 20=售前) */
    @TableField("projectType")
    private String projectType;

    /** 成员编码（工号） */
    @TableField("memberCode")
    private String memberCode;

    /** 成员姓名 */
    @TableField("memberName")
    private String memberName;

    /** 成员角色: 10=服务经理, 20=服务经理(备), 30=项目经理, 40=组员 */
    @TableField("memberRole")
    private String memberRole;

    /** 电话 */
    @TableField("phoneNum")
    private String phoneNum;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 生效开始时间 */
    @TableField("effectiveFrom")
    private LocalDateTime effectiveFrom;

    /** 生效结束时间 */
    @TableField("effectiveTo")
    private LocalDateTime effectiveTo;

    // ===== 非数据库字段 =====

    /** 角色名称 */
    @TableField(exist = false)
    private String memberRoleName;

    public String getMemberRoleName() {
        if (memberRole == null) return "";
        switch (memberRole) {
            case "10": return "服务经理";
            case "20": return "服务经理(备)";
            case "30": return "项目经理";
            case "40": return "项目组员";
            default: return "未知";
        }
    }
}
