-- V70__alter_project_for_subproject.sql
-- Project 表扩展：主子项目（物化路径）+ 模板关联 + 阶段关联
-- 关联设计文档：§6.3

ALTER TABLE pms_project
    ADD COLUMN parent_project_id  BIGINT       NULL COMMENT '父项目ID（NULL=顶层）',
    ADD COLUMN project_path       VARCHAR(500) NOT NULL DEFAULT '/' COMMENT '物化路径 /1/5/',
    ADD COLUMN depth              INT          NOT NULL DEFAULT 0 COMMENT '深度（0=顶层）',
    ADD COLUMN weight             DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '汇总权重',
    ADD COLUMN template_id        BIGINT       NULL COMMENT '来源模板ID',
    ADD COLUMN template_version   VARCHAR(32)  NULL COMMENT '模板版本快照',
    ADD COLUMN current_phase_id   BIGINT       NULL COMMENT '当前阶段ID',
    ADD COLUMN project_objective  VARCHAR(500) NULL COMMENT '项目目标',
    ADD COLUMN project_scope      VARCHAR(1000) NULL COMMENT '项目范围';

CREATE INDEX idx_project_path ON pms_project (project_path);
CREATE INDEX idx_parent_project_id ON pms_project (parent_project_id);
CREATE INDEX idx_template_id ON pms_project (template_id);

-- 回填存量项目路径（顶层项目路径为 /<id>/）
UPDATE pms_project
SET project_path = CONCAT('/', id, '/')
WHERE project_path = '/' OR project_path IS NULL;
