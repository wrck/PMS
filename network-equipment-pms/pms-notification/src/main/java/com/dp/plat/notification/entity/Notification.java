package com.dp.plat.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 站内通知实体。
 *
 * <p>记录每一条通知及其投递通道，支持站内信、WebSocket 推送、邮件、OA 多通道。
 * 不继承 {@code BaseEntity}：通知表使用独立的 {@code created_at} / {@code created_by}
 * 审计字段，且不参与逻辑删除（通知一经产生即留存审计）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_notification")
public class Notification {

    /** 主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收人用户 id。 */
    private Long userId;

    /** 通知标题。 */
    private String title;

    /** 通知正文。 */
    private String content;

    /** 业务分类：MILESTONE/TASK/APPROVAL/PUNCH_LIST/WARRANTY/RMA/SETTLEMENT。 */
    private String category;

    /** 业务类型（如 TASK_ASSIGNED、WARRANTY_EXPIRE_30 等），与模板编码对应。 */
    private String bizType;

    /** 关联业务记录 id。 */
    private Long bizId;

    /** 已读状态：UNREAD/READ。 */
    private String readStatus;

    /** 投递通道：IN_APP/WS/EMAIL/OA。 */
    private String channel;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 创建人用户 id。 */
    private Long createdBy;
}
