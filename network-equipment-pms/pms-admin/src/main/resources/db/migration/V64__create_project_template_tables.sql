-- V64__create_project_template_tables.sql
-- 项目模板与版本管理（Story 1）
-- 关联设计文档：§6.2

CREATE TABLE pms_project_template (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    template_code   VARCHAR(64)  NOT NULL COMMENT '模板编码',
    template_name   VARCHAR(128) NOT NULL COMMENT '模板名称',
    category        VARCHAR(32)  NOT NULL DEFAULT 'IMPLEMENT' COMMENT '类别：IMPLEMENT/MAINTENANCE/CONSULTING',
    description     VARCHAR(500) NULL COMMENT '描述',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/DEPRECATED',
    create_by       BIGINT       NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code),
    KEY idx_status_category (status, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模板';

CREATE TABLE pms_project_template_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    template_id     BIGINT       NOT NULL COMMENT '模板ID',
    version         VARCHAR(32)  NOT NULL COMMENT '语义化版本 v1.0.0',
    snapshot_json   JSON         NOT NULL COMMENT '模板内容快照JSON（phases/tasks/milestones/deliverables/dependencies/approvalPlans/assigneeRules）',
    change_log      VARCHAR(500) NULL COMMENT '版本变更说明',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
    published_at    DATETIME     NULL COMMENT '发布时间',
    published_by    BIGINT       NULL COMMENT '发布人',
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version_lock    INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_version (template_id, version),
    KEY idx_template_status (template_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模板版本';
