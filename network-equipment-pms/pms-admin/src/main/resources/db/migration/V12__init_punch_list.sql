-- =============================================================
-- V12__init_punch_list.sql
-- Initialize the punch list defect table used to track walkdown
-- defects discovered during project milestone acceptance.
-- =============================================================

DROP TABLE IF EXISTS `pms_punch_list`;
CREATE TABLE `pms_punch_list` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`      BIGINT       NOT NULL COMMENT 'Project id',
    `milestone_id`    BIGINT       DEFAULT NULL COMMENT 'Milestone id',
    `severity`        VARCHAR(32)  DEFAULT NULL COMMENT 'Severity (SAFETY, FUNCTIONAL, COSMETIC)',
    `title`           VARCHAR(200) NOT NULL COMMENT 'Defect title',
    `description`     VARCHAR(1000) DEFAULT NULL COMMENT 'Defect description',
    `walkdown_stage`  VARCHAR(32)  DEFAULT NULL COMMENT 'Walkdown stage (PRE_PUNCH, FORMAL)',
    `assignee_id`     BIGINT       DEFAULT NULL COMMENT 'Assignee user id',
    `assignee_name`   VARCHAR(64)  DEFAULT NULL COMMENT 'Assignee user name',
    `deadline`        DATE         DEFAULT NULL COMMENT 'Resolution deadline',
    `status`          VARCHAR(32)  DEFAULT 'OPEN' COMMENT 'Status (OPEN, RESOLVED, VERIFIED)',
    `resolved_at`     DATETIME     DEFAULT NULL COMMENT 'Resolution time',
    `verified_at`     DATETIME     DEFAULT NULL COMMENT 'Verification time',
    `verified_by`     BIGINT       DEFAULT NULL COMMENT 'Verifier user id',
    `verified_by_name` VARCHAR(64) DEFAULT NULL COMMENT 'Verifier user name',
    `attachment_ids`  VARCHAR(500) DEFAULT NULL COMMENT 'Comma-separated attachment ids (reserved)',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_milestone_id` (`milestone_id`),
    KEY `idx_assignee_id` (`assignee_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Punch list defect';
