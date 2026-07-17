package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目模板
 * 关联表：pms_project_template
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_template")
public class ProjectTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 模板编码 */
    private String templateCode;

    /** 模板名称 */
    private String templateName;

    /** 类别：IMPLEMENT / MAINTENANCE / CONSULTING */
    private String category;

    /** 描述 */
    private String description;

    /** 状态：DRAFT / PUBLISHED / DEPRECATED */
    private String status;
}
