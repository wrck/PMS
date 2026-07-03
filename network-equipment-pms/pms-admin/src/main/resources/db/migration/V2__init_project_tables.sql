-- =============================================================
-- V2__init_project_tables.sql
-- Initialize project delivery management tables for network equipment PMS.
-- =============================================================

-- ----------------------------
-- pms_project
-- ----------------------------
DROP TABLE IF EXISTS `pms_project`;
CREATE TABLE `pms_project` (
    `id`                    BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_code`          VARCHAR(64)   DEFAULT NULL COMMENT 'Project code (PMS-YYYY-XXXX)',
    `project_name`          VARCHAR(200)  NOT NULL COMMENT 'Project name',
    `project_type`          VARCHAR(32)   DEFAULT NULL COMMENT 'Project type (NETWORK_DEVICE, SECURITY, DATACENTER, etc.)',
    `status`                VARCHAR(32)   DEFAULT 'PENDING' COMMENT 'Status (PENDING, APPROVED, IN_PROGRESS, INITIAL_ACCEPTANCE, FINAL_ACCEPTANCE, COMPLETED, CLOSED, REJECTED)',
    `customer_name`         VARCHAR(200)  DEFAULT NULL COMMENT 'Customer name',
    `customer_contact`      VARCHAR(64)   DEFAULT NULL COMMENT 'Customer contact',
    `customer_phone`        VARCHAR(32)   DEFAULT NULL COMMENT 'Customer phone',
    `contract_no`           VARCHAR(64)   DEFAULT NULL COMMENT 'Contract number',
    `contract_amount`       DECIMAL(14,2) DEFAULT NULL COMMENT 'Contract amount',
    `plan_start_date`       DATE          DEFAULT NULL COMMENT 'Planned start date',
    `plan_end_date`         DATE          DEFAULT NULL COMMENT 'Planned end date',
    `actual_start_date`     DATE          DEFAULT NULL COMMENT 'Actual start date',
    `actual_end_date`       DATE          DEFAULT NULL COMMENT 'Actual end date',
    `project_manager_id`    BIGINT        DEFAULT NULL COMMENT 'Project manager user id',
    `project_manager_name`  VARCHAR(64)   DEFAULT NULL COMMENT 'Project manager name',
    `description`           TEXT          COMMENT 'Project description',
    `progress`              INT           DEFAULT 0 COMMENT 'Progress percentage 0-100',
    `priority`              VARCHAR(16)   DEFAULT 'NORMAL' COMMENT 'Priority (HIGH, NORMAL, LOW)',
    `create_by`             VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`           DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`             VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`           DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`               TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_code` (`project_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Project main table';

-- ----------------------------
-- pms_project_member
-- ----------------------------
DROP TABLE IF EXISTS `pms_project_member`;
CREATE TABLE `pms_project_member` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`  BIGINT      NOT NULL COMMENT 'Project id',
    `user_id`     BIGINT      NOT NULL COMMENT 'User id',
    `role_type`   VARCHAR(32) DEFAULT NULL COMMENT 'Role type (PM, ENGINEER, QA, OBSERVER)',
    `create_by`   VARCHAR(64) DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME    DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64) DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME    DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT     DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Project member';

-- ----------------------------
-- pms_milestone
-- ----------------------------
DROP TABLE IF EXISTS `pms_milestone`;
CREATE TABLE `pms_milestone` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`     BIGINT       NOT NULL COMMENT 'Project id',
    `milestone_name` VARCHAR(200) NOT NULL COMMENT 'Milestone name',
    `milestone_type` VARCHAR(32)  DEFAULT NULL COMMENT 'Milestone type (ARRIVAL, INSTALL, DEBUG, INITIAL_ACCEPTANCE, FINAL_ACCEPTANCE)',
    `plan_date`      DATE         NOT NULL COMMENT 'Planned date',
    `actual_date`    DATE         DEFAULT NULL COMMENT 'Actual date',
    `status`         VARCHAR(32)  DEFAULT 'PENDING' COMMENT 'Status (PENDING, IN_PROGRESS, COMPLETED, OVERDUE)',
    `description`    VARCHAR(500) DEFAULT NULL COMMENT 'Description',
    `sort_order`     INT          DEFAULT 0 COMMENT 'Sort order',
    `create_by`      VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`    DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`      VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`    DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`        TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Project milestone';

-- ----------------------------
-- pms_delivery_plan
-- ----------------------------
DROP TABLE IF EXISTS `pms_delivery_plan`;
CREATE TABLE `pms_delivery_plan` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`  BIGINT       NOT NULL COMMENT 'Project id',
    `plan_name`   VARCHAR(200) DEFAULT NULL COMMENT 'Plan name',
    `plan_content` TEXT        COMMENT 'Plan content',
    `plan_date`   DATE         DEFAULT NULL COMMENT 'Plan date',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Delivery plan';

-- ----------------------------
-- pms_final_acceptance
-- ----------------------------
DROP TABLE IF EXISTS `pms_final_acceptance`;
CREATE TABLE `pms_final_acceptance` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`         BIGINT       NOT NULL COMMENT 'Project id',
    `apply_time`         DATETIME     DEFAULT NULL COMMENT 'Apply time',
    `apply_user_id`      BIGINT       DEFAULT NULL COMMENT 'Apply user id',
    `apply_user_name`    VARCHAR(64)  DEFAULT NULL COMMENT 'Apply user name',
    `status`             VARCHAR(32)  DEFAULT 'PENDING' COMMENT 'Status (PENDING, APPROVED, REJECTED)',
    `acceptance_report`  TEXT         COMMENT 'Acceptance report',
    `acceptance_opinion` VARCHAR(500) DEFAULT NULL COMMENT 'Acceptance opinion',
    `accept_user_id`     BIGINT       DEFAULT NULL COMMENT 'Accept user id',
    `accept_user_name`   VARCHAR(64)  DEFAULT NULL COMMENT 'Accept user name',
    `accept_time`        DATETIME     DEFAULT NULL COMMENT 'Accept time',
    `create_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`        DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`        DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`            TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Final acceptance record';

-- ----------------------------
-- pms_deliverable
-- ----------------------------
DROP TABLE IF EXISTS `pms_deliverable`;
CREATE TABLE `pms_deliverable` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`      BIGINT       NOT NULL COMMENT 'Project id',
    `deliverable_name` VARCHAR(200) NOT NULL COMMENT 'Deliverable name',
    `deliverable_type` VARCHAR(32)  DEFAULT NULL COMMENT 'Deliverable type (DOCUMENT, CONFIG, REPORT, OTHER)',
    `file_path`       VARCHAR(500) DEFAULT NULL COMMENT 'File path',
    `status`          VARCHAR(32)  DEFAULT 'PENDING' COMMENT 'Status (PENDING, SUBMITTED, CONFIRMED)',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Project deliverable';
