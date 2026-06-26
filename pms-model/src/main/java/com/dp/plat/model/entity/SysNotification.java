package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统通知实体 - 对应老系统 fnd_notification 表
 */
@Data
@TableName("fnd_notification")
public class SysNotification extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 通知类型编码 */
    @TableField("templateCode")
    private String templateCode;

    /** 通知标题 */
    @TableField("title")
    private String title;

    /** 通知内容 */
    @TableField("content")
    private String content;

    /** 接收人 */
    @TableField("receiver")
    private String receiver;

    /** 关联项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 关联项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 是否已读: 0=未读, 1=已读 */
    @TableField("isRead")
    private Integer isRead;

    /** 创建人 */
    @TableField("createBy")
    private String createBy;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;
}
