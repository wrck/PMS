-- =============================================================
-- V5__init_integration_tables.sql
-- Initialize external system integration log table.
-- =============================================================

-- ----------------------------
-- pms_integration_log  External system integration log
-- ----------------------------
DROP TABLE IF EXISTS `pms_integration_log`;
CREATE TABLE `pms_integration_log` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `log_type`        VARCHAR(32)   NOT NULL COMMENT 'External system type: D365/FP/OA/SMS/EHR',
    `business_type`   VARCHAR(64)   NOT NULL COMMENT 'Business type: PURCHASE_RECEIPT/PURCHASE_ORDER/SETTLEMENT/INVOICE etc.',
    `business_id`     VARCHAR(64)   DEFAULT NULL COMMENT 'Related business record id',
    `request_url`     VARCHAR(500)  DEFAULT NULL COMMENT 'Request URL',
    `request_body`    TEXT          COMMENT 'Request body (JSON)',
    `response_status` VARCHAR(32)   DEFAULT 'PENDING' COMMENT 'Response status: SUCCESS/FAILED/PENDING',
    `response_body`   TEXT          COMMENT 'Response body (JSON)',
    `error_message`   VARCHAR(1000) DEFAULT NULL COMMENT 'Error message when failed',
    `retry_count`     INT           DEFAULT 0 COMMENT 'Current retry count',
    `max_retry`       INT           DEFAULT 3 COMMENT 'Max retry times',
    `next_retry_time` DATETIME      DEFAULT NULL COMMENT 'Next retry time',
    `create_by`       VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_log_type` (`log_type`),
    KEY `idx_business_type` (`business_type`),
    KEY `idx_business_id` (`business_id`),
    KEY `idx_response_status` (`response_status`),
    KEY `idx_next_retry_time` (`next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='External system integration log';
