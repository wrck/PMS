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
 * 通知模板实体。
 *
 * <p>subject 与 body 中使用 {@code ${var}} 形式的 Freemarker 占位符，
 * 由 {@code NotificationTemplateEngine} 在运行时根据 variables 定义渲染。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_notification_template")
public class NotificationTemplate {

    /** 主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 模板编码（唯一），如 TASK_ASSIGNED、WARRANTY_EXPIRE_30。 */
    private String templateCode;

    /** 通知标题模板。 */
    private String subject;

    /** 通知正文模板，含 {@code ${var}} 占位符。 */
    private String body;

    /** 变量定义（JSON 格式），描述模板可用变量。 */
    private String variables;

    /** 模板描述。 */
    private String description;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
