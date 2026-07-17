package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目配置（多层级：项目级 > 模板级 > 系统默认）
 * 关联表：pms_project_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_config")
public class ProjectConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** NULL = 系统级默认 */
    private Long projectId;

    /** NULL = 非模板配置 */
    private Long templateId;

    private String configKey;
    private String configValue;
    private String description;
}
