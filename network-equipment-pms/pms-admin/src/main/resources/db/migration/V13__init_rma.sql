-- =============================================================
-- V13__init_rma.sql
-- Initialize the RMA (Return Merchandise Authorization) table
-- tracking the 6-step closed loop:
-- REGISTERED → WARRANTY_CHECKED → RMA_ISSUED → RETURNING → INSPECTED → CLOSED.
-- =============================================================

DROP TABLE IF EXISTS `pms_rma`;
CREATE TABLE `pms_rma` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `rma_no`              VARCHAR(64)  NOT NULL COMMENT 'RMA number, format RMA-YYYY-XXXX',
    `asset_id`            BIGINT       NOT NULL COMMENT 'Asset id',
    `sn`                  VARCHAR(100) DEFAULT NULL COMMENT 'Asset serial number snapshot',
    `fault_description`   VARCHAR(1000) DEFAULT NULL COMMENT 'Fault description',
    `fault_photos`        VARCHAR(500) DEFAULT NULL COMMENT 'Comma-separated attachment ids (reserved)',
    `ticket_status`       VARCHAR(32)  DEFAULT 'REGISTERED' COMMENT 'REGISTERED/WARRANTY_CHECKED/RMA_ISSUED/RETURNING/INSPECTED/CLOSED',
    `warranty_status`     VARCHAR(32)  DEFAULT NULL COMMENT 'IN_WARRANTY/OUT_OF_WARRANTY',
    `project_id`          BIGINT       DEFAULT NULL COMMENT 'Asset current project at registration',
    `registered_at`       DATETIME     DEFAULT NULL COMMENT 'Registration time',
    `warranty_checked_at` DATETIME     DEFAULT NULL COMMENT 'Warranty check time',
    `rma_issued_at`       DATETIME     DEFAULT NULL COMMENT 'RMA issue time',
    `returning_at`        DATETIME     DEFAULT NULL COMMENT 'Return shipping time',
    `inspected_at`        DATETIME     DEFAULT NULL COMMENT 'Inspection time',
    `closed_at`           DATETIME     DEFAULT NULL COMMENT 'Close time',
    `register_user_id`    BIGINT       DEFAULT NULL COMMENT 'Registering user id',
    `register_user_name`  VARCHAR(64)  DEFAULT NULL COMMENT 'Registering user name',
    `resolution`          VARCHAR(1000) DEFAULT NULL COMMENT 'Repair result description',
    `inspector_notes`     VARCHAR(1000) DEFAULT NULL COMMENT 'Inspector notes',
    `create_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`         DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`           VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`         DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`             TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rma_no` (`rma_no`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_ticket_status` (`ticket_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RMA return merchandise authorization';
