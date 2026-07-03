-- =============================================================
-- V3__init_asset_tables.sql
-- Initialize equipment asset management tables for network equipment PMS.
-- Covers equipment catalog (category, model) and asset lifecycle
-- (asset, allocation, transfer, lifecycle log).
-- =============================================================

-- ----------------------------
-- pms_asset_category
-- Equipment category tree.
-- ----------------------------
DROP TABLE IF EXISTS `pms_asset_category`;
CREATE TABLE `pms_asset_category` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `parent_id`     BIGINT       DEFAULT 0 COMMENT 'Parent category id, 0=root',
    `category_name` VARCHAR(100) NOT NULL COMMENT 'Category name',
    `category_code` VARCHAR(64)  DEFAULT NULL COMMENT 'Category code',
    `sort_order`    INT          DEFAULT 0 COMMENT 'Display order',
    `status`        TINYINT      DEFAULT 1 COMMENT '1=active 0=disabled',
    `create_by`     VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`   DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`     VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`   DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`       TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment category';

-- ----------------------------
-- pms_asset_model
-- Equipment model belonging to a category.
-- ----------------------------
DROP TABLE IF EXISTS `pms_asset_model`;
CREATE TABLE `pms_asset_model` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `category_id`    BIGINT        NOT NULL COMMENT 'Category id',
    `model_name`     VARCHAR(200)  NOT NULL COMMENT 'Model name',
    `model_code`     VARCHAR(64)   DEFAULT NULL COMMENT 'Model code',
    `brand`          VARCHAR(100)  DEFAULT NULL COMMENT 'Brand',
    `spec_params`    TEXT          COMMENT 'JSON string of specifications',
    `standard_price` DECIMAL(14,2) DEFAULT NULL COMMENT 'Standard price',
    `unit`           VARCHAR(32)   DEFAULT NULL COMMENT 'Unit (台/套/个)',
    `status`         TINYINT       DEFAULT 1 COMMENT '1=active 0=disabled',
    `create_by`      VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`    DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`      VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`    DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`        TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment model';

-- ----------------------------
-- pms_asset
-- Equipment asset instance.
-- status: IN_STOCK=在库, ALLOCATED=已分配, IN_TRANSIT=调拨中, SCRAPPED=已报废
-- ----------------------------
DROP TABLE IF EXISTS `pms_asset`;
CREATE TABLE `pms_asset` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `serial_no`     VARCHAR(100) NOT NULL COMMENT 'Asset serial number',
    `model_id`      BIGINT       NOT NULL COMMENT 'Model id',
    `category_id`   BIGINT       NOT NULL COMMENT 'Category id',
    `asset_name`    VARCHAR(200) NOT NULL COMMENT 'Asset name',
    `status`        VARCHAR(32)  DEFAULT 'IN_STOCK' COMMENT 'IN_STOCK/ALLOCATED/IN_TRANSIT/SCRAPPED',
    `warehouse`     VARCHAR(100) DEFAULT NULL COMMENT 'Warehouse',
    `location`      VARCHAR(200) DEFAULT NULL COMMENT 'Storage location',
    `project_id`    BIGINT       DEFAULT NULL COMMENT 'Current allocated project id',
    `inbound_time`  DATETIME     DEFAULT NULL COMMENT 'Inbound time',
    `outbound_time` DATETIME     DEFAULT NULL COMMENT 'Outbound time',
    `remarks`       VARCHAR(500) DEFAULT NULL COMMENT 'Remarks',
    `create_by`     VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`   DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`     VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`   DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`       TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_serial_no` (`serial_no`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_serial_no` (`serial_no`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment asset instance';

-- ----------------------------
-- pms_asset_allocation
-- Equipment allocation to a project.
-- status: ACTIVE, RETURNED
-- ----------------------------
DROP TABLE IF EXISTS `pms_asset_allocation`;
CREATE TABLE `pms_asset_allocation` (
    `id`                  BIGINT      NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `asset_id`            BIGINT      NOT NULL COMMENT 'Asset id',
    `project_id`          BIGINT      NOT NULL COMMENT 'Project id',
    `model_id`            BIGINT      DEFAULT NULL COMMENT 'Model id',
    `quantity`            INT         DEFAULT 1 COMMENT 'Quantity',
    `allocate_time`       DATETIME    DEFAULT NULL COMMENT 'Allocate time',
    `allocate_user_id`    BIGINT      DEFAULT NULL COMMENT 'Allocate user id',
    `allocate_user_name`  VARCHAR(64) DEFAULT NULL COMMENT 'Allocate user name',
    `status`              VARCHAR(32) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/RETURNED',
    `return_time`         DATETIME    DEFAULT NULL COMMENT 'Return time',
    `create_by`           VARCHAR(64) DEFAULT '' COMMENT 'Creator',
    `create_time`         DATETIME    DEFAULT NULL COMMENT 'Create time',
    `update_by`           VARCHAR(64) DEFAULT '' COMMENT 'Updater',
    `update_time`         DATETIME    DEFAULT NULL COMMENT 'Update time',
    `deleted`             TINYINT     DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment allocation';

-- ----------------------------
-- pms_asset_transfer
-- Equipment transfer between projects.
-- status: PENDING, APPROVED, REJECTED
-- ----------------------------
DROP TABLE IF EXISTS `pms_asset_transfer`;
CREATE TABLE `pms_asset_transfer` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `asset_id`           BIGINT       NOT NULL COMMENT 'Asset id',
    `from_project_id`    BIGINT       DEFAULT NULL COMMENT 'Source project id',
    `to_project_id`      BIGINT       DEFAULT NULL COMMENT 'Target project id',
    `transfer_reason`    VARCHAR(500) DEFAULT NULL COMMENT 'Transfer reason',
    `status`             VARCHAR(32)  DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    `apply_user_id`      BIGINT       DEFAULT NULL COMMENT 'Apply user id',
    `apply_user_name`    VARCHAR(64)  DEFAULT NULL COMMENT 'Apply user name',
    `apply_time`         DATETIME     DEFAULT NULL COMMENT 'Apply time',
    `approve_user_id`    BIGINT       DEFAULT NULL COMMENT 'Approve user id',
    `approve_user_name`  VARCHAR(64)  DEFAULT NULL COMMENT 'Approve user name',
    `approve_time`       DATETIME     DEFAULT NULL COMMENT 'Approve time',
    `approve_opinion`    VARCHAR(500) DEFAULT NULL COMMENT 'Approve opinion',
    `create_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`        DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`        DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`            TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_from_project_id` (`from_project_id`),
    KEY `idx_to_project_id` (`to_project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment transfer';

-- ----------------------------
-- pms_asset_lifecycle_log
-- Equipment lifecycle log.
-- action_type: INBOUND, ALLOCATE, TRANSFER, RETURN, SCRAP
-- ----------------------------
DROP TABLE IF EXISTS `pms_asset_lifecycle_log`;
CREATE TABLE `pms_asset_lifecycle_log` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `asset_id`         BIGINT       NOT NULL COMMENT 'Asset id',
    `action_type`      VARCHAR(32)  DEFAULT NULL COMMENT 'INBOUND/ALLOCATE/TRANSFER/RETURN/SCRAP',
    `from_project_id`  BIGINT       DEFAULT NULL COMMENT 'From project id',
    `to_project_id`    BIGINT       DEFAULT NULL COMMENT 'To project id',
    `operator_id`      BIGINT       DEFAULT NULL COMMENT 'Operator id',
    `operator_name`    VARCHAR(64)  DEFAULT NULL COMMENT 'Operator name',
    `action_time`      DATETIME     DEFAULT NULL COMMENT 'Action time',
    `remarks`          VARCHAR(500) DEFAULT NULL COMMENT 'Remarks',
    `create_by`        VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`      DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`        VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`      DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`          TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Equipment lifecycle log';
