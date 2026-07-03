-- =============================================================
-- V15__init_warranty.sql
-- Initialize the warranty table and link it to assets.
-- =============================================================

DROP TABLE IF EXISTS `pms_warranty`;
CREATE TABLE `pms_warranty` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `asset_id`        BIGINT       NOT NULL COMMENT 'Asset id',
    `start_date`      DATE         DEFAULT NULL COMMENT 'Warranty start date',
    `end_date`        DATE         DEFAULT NULL COMMENT 'Warranty end date',
    `duration_months` INT          DEFAULT NULL COMMENT 'Warranty duration in months',
    `sla_level`       VARCHAR(32)  DEFAULT NULL COMMENT 'SLA level (BASIC/PREMIUM/PLATINUM)',
    `contract_no`     VARCHAR(100) DEFAULT NULL COMMENT 'Warranty contract number',
    `project_id`      BIGINT       DEFAULT NULL COMMENT 'Project id',
    `notes`           VARCHAR(500) DEFAULT NULL COMMENT 'Notes',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_end_date` (`end_date`),
    KEY `idx_contract_no` (`contract_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment warranty';

-- Link warranty records back to the asset for quick lookup.
ALTER TABLE `pms_asset`
    ADD COLUMN `warranty_id` BIGINT DEFAULT NULL COMMENT 'Warranty record id' AFTER `remarks`;
