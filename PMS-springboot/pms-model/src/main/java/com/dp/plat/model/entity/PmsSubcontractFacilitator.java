package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 服务商实体 - 对应 pm_subcontract_facilitator 表
 */
@Data
@TableName("pm_subcontract_facilitator")
public class PmsSubcontractFacilitator extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 服务商名称 */
    @TableField("name")
    private String name;

    /** 服务商编码 */
    @TableField("code")
    private String code;

    /** 账号 */
    @TableField("account")
    private String account;

    /** 银行信息 */
    @TableField("bankInfo")
    private String bankInfo;

    /** 银行账号 */
    @TableField("bankAccount")
    private String bankAccount;

    /** 收款人 */
    @TableField("receiver")
    private String receiver;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 状态 */
    @TableField("state")
    private Integer state;
}
