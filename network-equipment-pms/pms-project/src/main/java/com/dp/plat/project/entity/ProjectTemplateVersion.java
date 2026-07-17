package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.handler.JsonTypeHandlers;
import com.dp.plat.common.dto.TemplateSnapshot;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 项目模板版本
 * 关联表：pms_project_template_version
 *
 * <p>注意：autoResultMap = true 必须开启，否则 @TableField(typeHandler=...) 在 BaseMapper 方法中不生效。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_project_template_version", autoResultMap = true)
public class ProjectTemplateVersion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 语义化版本 v1.0.0 */
    private String version;

    /** 模板内容快照（JSON） */
    @TableField(typeHandler = JsonTypeHandlers.TemplateSnapshotHandler.class)
    private TemplateSnapshot snapshotJson;

    /** 版本变更说明 */
    private String changeLog;

    /** 状态：DRAFT / PUBLISHED / ARCHIVED */
    private String status;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 发布人 */
    private Long publishedBy;
}
