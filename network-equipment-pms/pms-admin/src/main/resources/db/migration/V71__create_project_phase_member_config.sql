-- V71__create_project_phase_member_config.sql
-- 项目阶段 + 项目成员 + 项目配置 + 系统默认配置初始化
-- 关联设计文档：§6.4

CREATE TABLE IF NOT EXISTS pms_project_phase (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL COMMENT '项目ID',
    template_phase_id   BIGINT       NULL COMMENT '模板阶段ID（追溯）',
    phase_name          VARCHAR(64)  NOT NULL,
    phase_code          VARCHAR(32)  NOT NULL COMMENT 'PREPARE/PLAN/DESIGN/IMPLEMENT/OPERATE 或自定义',
    sort_order          INT          NOT NULL DEFAULT 0,
    entry_criteria      JSON         NULL COMMENT '进入条件（结构化 JSON）',
    exit_criteria       JSON         NULL COMMENT '退出条件 JSON：{requiredDeliverables,requiredTasks,requiredMilestones,requiredApprovals}',
    status              VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED' COMMENT 'NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED',
    planned_start_date  DATE         NULL,
    planned_end_date    DATE         NULL,
    actual_start_date   DATE         NULL,
    actual_end_date     DATE         NULL,
    create_by           BIGINT       NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT       NULL,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_phase_code (project_id, phase_code),
    KEY idx_project_sort (project_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目阶段';

-- pms_project_member 已在 V2 建表并由 V61 写入演示数据。这里必须做增量升级，
-- 不能重新 CREATE TABLE，否则所有从 V2 顺序升级的数据库都会在此失败。
ALTER TABLE pms_project_member
    ADD COLUMN user_name VARCHAR(64) NULL COMMENT '冗余' AFTER user_id,
    ADD COLUMN role VARCHAR(32) NOT NULL DEFAULT 'PROJECT_MEMBER'
        COMMENT 'PROJECT_MANAGER/PROJECT_MEMBER/APPROVER/VIEWER/CUSTOMER' AFTER role_type,
    ADD COLUMN join_date DATE NULL AFTER role,
    ADD COLUMN leave_date DATE NULL AFTER join_date,
    ADD COLUMN version INT NOT NULL DEFAULT 0 AFTER deleted,
    ADD UNIQUE KEY uk_project_user (project_id, user_id),
    ADD KEY idx_user_role (user_id, role);

-- 将旧角色值映射到新模型；保留 role_type 以兼容已有报表和历史 SQL。
UPDATE pms_project_member
SET role = CASE role_type
    WHEN 'PM' THEN 'PROJECT_MANAGER'
    WHEN 'ENGINEER' THEN 'PROJECT_MEMBER'
    WHEN 'QA' THEN 'APPROVER'
    WHEN 'OBSERVER' THEN 'VIEWER'
    ELSE 'PROJECT_MEMBER'
END;

CREATE TABLE IF NOT EXISTS pms_project_config (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    project_id      BIGINT       NULL COMMENT 'NULL=系统级默认',
    template_id     BIGINT       NULL COMMENT 'NULL=非模板配置',
    config_key      VARCHAR(100) NOT NULL,
    config_value    VARCHAR(500) NOT NULL,
    description     VARCHAR(255) NULL,
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_proj_tpl_key (project_id, template_id, config_key),
    KEY idx_template_key (template_id, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目配置';

-- 初始化系统默认配置（9 条）
INSERT INTO pms_project_config (project_id, template_id, config_key, config_value, description) VALUES
(NULL, NULL, 'baseline.variance.days.threshold',     '5',     '基线偏差天数阈值'),
(NULL, NULL, 'baseline.variance.percent.threshold',  '10',    '基线偏差百分比阈值'),
(NULL, NULL, 'approval.timeout.hours',               '48',    '审批超时小时数'),
(NULL, NULL, 'approval.escalate.hours',              '24',    '审批升级小时数'),
(NULL, NULL, 'approval.reminder.hours',              '12',    '审批提醒小时数'),
(NULL, NULL, 'approval.timeout.action',              'ESCALATE', '超时动作：ESCALATE/AUTO_APPROVE/AUTO_REJECT'),
(NULL, NULL, 'task.rollup.weight.field',             'PLANNED_HOURS', '任务汇总权重字段'),
(NULL, NULL, 'phase.exit.check.approval',            'true',  '阶段退出是否强制审批'),
(NULL, NULL, 'approval.max.rounds',                  '5',     '审批最大轮次');
