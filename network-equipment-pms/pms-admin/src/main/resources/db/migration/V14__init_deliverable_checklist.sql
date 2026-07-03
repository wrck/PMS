-- =============================================================
-- V14__init_deliverable_checklist.sql
-- Initialize the final acceptance deliverable checklist table used
-- to verify that all mandatory deliverables are uploaded before a
-- project applies for final acceptance.
-- =============================================================

DROP TABLE IF EXISTS `pms_deliverable_checklist`;
CREATE TABLE `pms_deliverable_checklist` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`       BIGINT       NOT NULL COMMENT 'Project id',
    `deliverable_type` VARCHAR(32)  DEFAULT NULL COMMENT 'Deliverable type (AS_BUILT, TEST_REPORT, ACCEPTANCE_CERT, TRAINING_RECORD, OPERATION_MANUAL, ASSET_REGISTER, WARRANTY_CERT, SPARE_PARTS_LIST)',
    `required`         TINYINT      DEFAULT 1 COMMENT 'Whether the deliverable is mandatory (0=no 1=yes)',
    `uploaded`         TINYINT      DEFAULT 0 COMMENT 'Whether the deliverable has been uploaded (0=no 1=yes)',
    `attachment_id`    BIGINT       DEFAULT NULL COMMENT 'Attachment id (reserved)',
    `checked_at`       DATETIME     DEFAULT NULL COMMENT 'Last checked time',
    `checked_by`       VARCHAR(64)  DEFAULT NULL COMMENT 'Last checked by user',
    `create_by`        VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`      DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`        VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`      DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`          TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Final acceptance deliverable checklist';
