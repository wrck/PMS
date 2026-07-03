-- =============================================================
-- V19__init_d365_sync_tables.sql
-- Initialize D365 sync tables (purchase receipt, invoice) and add
-- settlement invoice_no / payment_status columns used by the FP
-- payment callback and D365 invoice sync.
-- =============================================================

-- ----------------------------
-- d365_purchase_receipt  D365 purchase receipt
-- ----------------------------
DROP TABLE IF EXISTS `d365_purchase_receipt`;
CREATE TABLE `d365_purchase_receipt` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `receipt_no`      VARCHAR(64)   DEFAULT NULL COMMENT 'Local receipt number',
    `po_no`           VARCHAR(64)   DEFAULT NULL COMMENT 'Related purchase order number',
    `asset_id`        BIGINT        DEFAULT NULL COMMENT 'Related asset id',
    `sn`              VARCHAR(128)  DEFAULT NULL COMMENT 'Asset serial number on the receipt',
    `quantity`        DECIMAL(18,4) DEFAULT NULL COMMENT 'Received quantity',
    `received_date`   DATETIME      DEFAULT NULL COMMENT 'Date the goods were received',
    `push_status`     VARCHAR(32)   DEFAULT 'PENDING' COMMENT 'Push status: PENDING/PUSHED/FAILED',
    `pushed_at`       DATETIME      DEFAULT NULL COMMENT 'Timestamp of the last successful push to D365',
    `d365_receipt_id` VARCHAR(64)   DEFAULT NULL COMMENT 'Identifier returned by D365 after a successful push',
    `create_by`       VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_d365_receipt_no` (`receipt_no`),
    KEY `idx_d365_po_no` (`po_no`),
    KEY `idx_d365_receipt_push_status` (`push_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='D365 purchase receipt';

-- ----------------------------
-- d365_invoice  D365 invoice
-- ----------------------------
DROP TABLE IF EXISTS `d365_invoice`;
CREATE TABLE `d365_invoice` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `invoice_no`      VARCHAR(64)   DEFAULT NULL COMMENT 'Invoice number',
    `settlement_no`   VARCHAR(64)   DEFAULT NULL COMMENT 'Related settlement number',
    `amount`          DECIMAL(18,4) DEFAULT NULL COMMENT 'Invoice amount (excluding tax)',
    `tax_amount`      DECIMAL(18,4) DEFAULT NULL COMMENT 'Tax amount',
    `total_amount`    DECIMAL(18,4) DEFAULT NULL COMMENT 'Total amount (including tax)',
    `invoice_date`    DATETIME      DEFAULT NULL COMMENT 'Invoice date',
    `vendor_name`     VARCHAR(128)  DEFAULT NULL COMMENT 'Vendor name',
    `push_status`     VARCHAR(32)   DEFAULT 'PENDING' COMMENT 'Push status: PENDING/PUSHED/FAILED',
    `pushed_at`       DATETIME      DEFAULT NULL COMMENT 'Timestamp of the last successful push to D365',
    `d365_invoice_id` VARCHAR(64)   DEFAULT NULL COMMENT 'Identifier returned by D365 after a successful push',
    `ocr_status`      VARCHAR(32)   DEFAULT 'PENDING' COMMENT 'OCR status: PENDING/RECOGNIZED/FAILED',
    `create_by`       VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_d365_invoice_no` (`invoice_no`),
    KEY `idx_d365_invoice_settlement_no` (`settlement_no`),
    KEY `idx_d365_invoice_push_status` (`push_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='D365 invoice';

-- ----------------------------
-- Add invoice_no / payment_status to pms_settlement for the D365
-- invoice sync and FP payment callback. These columns are updated by
-- the integration module via the SettlementMapper (UpdateWrapper) and
-- are not mapped on the Settlement entity.
-- ----------------------------
ALTER TABLE `pms_settlement`
    ADD COLUMN `invoice_no`     VARCHAR(64) DEFAULT NULL COMMENT 'Invoice number synced from D365' AFTER `push_response`,
    ADD COLUMN `payment_status` VARCHAR(32) DEFAULT NULL COMMENT 'Payment status from FP callback' AFTER `invoice_no`;
