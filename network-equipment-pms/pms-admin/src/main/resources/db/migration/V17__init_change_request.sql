-- =============================================================
-- V17__init_change_request.sql
-- Initialize the change request and baseline history tables used
-- by the governance three-books (change request book).
-- =============================================================

DROP TABLE IF EXISTS `pms_change_request`;
CREATE TABLE `pms_change_request` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `cr_no`               VARCHAR(32)  NOT NULL COMMENT 'Change request number (CR-YYYY-XXXX)',
    `project_id`          BIGINT       NOT NULL COMMENT 'Project id',
    `project_name`        VARCHAR(200) DEFAULT NULL COMMENT 'Project name (denormalized for display)',
    `title`               VARCHAR(200) NOT NULL COMMENT 'Change title',
    `description`         VARCHAR(2000) DEFAULT NULL COMMENT 'Change description',
    `requester_id`        BIGINT       DEFAULT NULL COMMENT 'Requester user id',
    `requester_name`      VARCHAR(64)  DEFAULT NULL COMMENT 'Requester user name',
    `request_date`        DATE         DEFAULT NULL COMMENT 'Request date',
    `impact_scope`        TEXT         DEFAULT NULL COMMENT 'Impact scope',
    `impact_schedule`     VARCHAR(1000) DEFAULT NULL COMMENT 'Impact on schedule',
    `impact_cost`         VARCHAR(1000) DEFAULT NULL COMMENT 'Impact on cost',
    `impact_quality`      VARCHAR(1000) DEFAULT NULL COMMENT 'Impact on quality',
    `priority`            VARCHAR(32)  DEFAULT 'MEDIUM' COMMENT 'Priority (LOW, MEDIUM, HIGH, CRITICAL)',
    `status`              VARCHAR(32)  DEFAULT 'SUBMITTED' COMMENT 'Status (SUBMITTED, UNDER_REVIEW, CCB_APPROVED, CCB_REJECTED, IMPLEMENTING, CLOSED)',
    `approver_id`         BIGINT       DEFAULT NULL COMMENT 'Approver user id',
    `approver_name`       VARCHAR(64)  DEFAULT NULL COMMENT 'Approver user name',
    `process_instance_id` VARCHAR(64)  DEFAULT NULL COMMENT 'Workflow process instance id',
    `baseline_updated`    TINYINT      DEFAULT 0 COMMENT 'Whether baseline updated (0=no 1=yes)',
    `approved_at`         DATETIME     DEFAULT NULL COMMENT 'Approval time',
    `closed_at`           DATETIME     DEFAULT NULL COMMENT 'Closure time',
    `create_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`         DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`         DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`             TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cr_no` (`cr_no`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Change request';

DROP TABLE IF EXISTS `pms_baseline_history`;
CREATE TABLE `pms_baseline_history` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`          BIGINT       NOT NULL COMMENT 'Project id',
    `change_request_id`   BIGINT       DEFAULT NULL COMMENT 'Change request id',
    `cr_no`               VARCHAR(32)  DEFAULT NULL COMMENT 'Change request number',
    `change_type`         VARCHAR(32)  DEFAULT NULL COMMENT 'Change type (SCHEDULE, COST, SCOPE)',
    `field_name`          VARCHAR(128) DEFAULT NULL COMMENT 'Field name that changed',
    `old_value`           VARCHAR(1000) DEFAULT NULL COMMENT 'Old value',
    `new_value`           VARCHAR(1000) DEFAULT NULL COMMENT 'New value',
    `description`         VARCHAR(1000) DEFAULT NULL COMMENT 'Change description',
    `changed_at`          DATETIME     DEFAULT NULL COMMENT 'Time the baseline was changed',
    `changed_by`          VARCHAR(64)  DEFAULT NULL COMMENT 'User who performed the change',
    `create_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`         DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`         DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`             TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_change_request_id` (`change_request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Baseline history';
