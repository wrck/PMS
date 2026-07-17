-- V66__create_project_phase_member_config.sql
-- 项目阶段 + 项目成员 + 项目配置 + 系统默认配置初始化
-- 关联设计文档：§6.4

CREATE TABLE pms_project_phase (
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

CREATE TABLE pms_project_member (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    project_id      BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    user_name       VARCHAR(64)  NULL COMMENT '冗余',
    role            VARCHAR(32)  NOT NULL DEFAULT 'PROJECT_MEMBER' COMMENT 'PROJECT_MANAGER/PROJECT_MEMBER/APPROVER/VIEWER/CUSTOMER',
    join_date       DATE         NULL,
    leave_date      DATE         NULL,
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_user (project_id, user_id),
    KEY idx_user_role (user_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员';

CREATE TABLE pms_project_config (
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
