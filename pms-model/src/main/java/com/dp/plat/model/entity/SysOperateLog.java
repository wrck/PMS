package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体 - 对应老系统 fnd_operate_log 表
 */
@Data
@TableName("fnd_operate_log")
public class SysOperateLog extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    @TableField("username")
    private String username;

    /** 真实姓名 */
    @TableField("realname")
    private String realname;

    /** IP地址 */
    @TableField("ip")
    private String ip;

    /** 操作内容 */
    @TableField("operation")
    private String operation;

    /** 模块 */
    @TableField("module")
    private String module;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;
}
