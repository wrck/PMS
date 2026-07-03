-- =============================================================
-- V18__init_risk_issue.sql
-- Initialize the risk register and issue log tables used by the
-- governance three-books (risk register + issue log).
-- =============================================================

DROP TABLE IF EXISTS `pms_risk`;
CREATE TABLE `pms_risk` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `risk_no`            VARCHAR(32)  NOT NULL COMMENT 'Risk number (RISK-YYYY-XXXX)',
    `project_id`         BIGINT       NOT NULL COMMENT 'Project id',
    `description`        VARCHAR(2000) NOT NULL COMMENT 'Risk description',
    `category`           VARCHAR(32)  DEFAULT NULL COMMENT 'Risk category (TECHNICAL, EXTERNAL, ORGANIZATIONAL, PM)',
    `likelihood`         INT          DEFAULT NULL COMMENT 'Likelihood score (1-5)',
    `impact`             INT          DEFAULT NULL COMMENT 'Impact score (1-5)',
    `score`              INT          DEFAULT NULL COMMENT 'Risk score (likelihood * impact)',
    `priority`           VARCHAR(32)  DEFAULT NULL COMMENT 'Priority (LOW, MEDIUM, HIGH)',
    `mitigation`         VARCHAR(32)  DEFAULT NULL COMMENT 'Mitigation strategy (AVOID, MITIGATE, TRANSFER, ACCEPT)',
    `contingency_plan`   TEXT         DEFAULT NULL COMMENT 'Contingency plan',
    `owner_id`           BIGINT       DEFAULT NULL COMMENT 'Owner user id',
    `owner_name`         VARCHAR(64)  DEFAULT NULL COMMENT 'Owner user name',
    `status`             VARCHAR(32)  DEFAULT 'OPEN' COMMENT 'Status (OPEN, IN_PROGRESS, CLOSED, ESCALATED)',
    `review_date`        DATE         DEFAULT NULL COMMENT 'Next review date',
    `source_issue_id`    BIGINT       DEFAULT NULL COMMENT 'Source issue id (if converted from issue)',
    `identified_at`      DATETIME     DEFAULT NULL COMMENT 'Time identified',
    `closed_at`          DATETIME     DEFAULT NULL COMMENT 'Time closed',
    `create_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`        DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`        DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`            TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_risk_no` (`risk_no`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_status` (`status`),
    KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Risk register';

DROP TABLE IF EXISTS `pms_issue`;
CREATE TABLE `pms_issue` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `issue_no`            VARCHAR(32)  NOT NULL COMMENT 'Issue number (ISSUE-YYYY-XXXX)',
    `project_id`          BIGINT       NOT NULL COMMENT 'Project id',
    `description`         VARCHAR(2000) NOT NULL COMMENT 'Issue description',
    `raised_by`           BIGINT       DEFAULT NULL COMMENT 'User id who raised the issue',
    `raised_by_name`      VARCHAR(64)  DEFAULT NULL COMMENT 'User name who raised the issue',
    `assignee_id`         BIGINT       DEFAULT NULL COMMENT 'Assignee user id',
    `assignee_name`       VARCHAR(64)  DEFAULT NULL COMMENT 'Assignee user name',
    `priority`            VARCHAR(32)  DEFAULT 'MEDIUM' COMMENT 'Priority (LOW, MEDIUM, HIGH, CRITICAL)',
    `target_resolve_date` DATE         DEFAULT NULL COMMENT 'Target resolution date',
    `status`              VARCHAR(32)  DEFAULT 'OPEN' COMMENT 'Status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)',
    `source_risk_id`      BIGINT       DEFAULT NULL COMMENT 'Source risk id (if from risk)',
    `source_risk_no`      VARCHAR(32)  DEFAULT NULL COMMENT 'Source risk number',
    `source_change_id`    BIGINT       DEFAULT NULL COMMENT 'Source change request id (if from change)',
    `source_cr_no`        VARCHAR(32)  DEFAULT NULL COMMENT 'Source change request number',
    `resolved_at`         DATETIME     DEFAULT NULL COMMENT 'Resolution time',
    `closed_at`           DATETIME     DEFAULT NULL COMMENT 'Closure time',
    `resolution`          TEXT         DEFAULT NULL COMMENT 'Resolution description',
    `create_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`         DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`         DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`             TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_issue_no` (`issue_no`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_status` (`status`),
    KEY `idx_assignee_id` (`assignee_id`),
    KEY `idx_source_risk_id` (`source_risk_id`),
    KEY `idx_source_change_id` (`source_change_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Issue log';
