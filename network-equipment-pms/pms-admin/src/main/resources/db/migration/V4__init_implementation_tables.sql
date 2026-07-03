-- =============================================================
-- V4__init_implementation_tables.sql
-- Initialize implementation management tables (OEM + agent).
-- =============================================================

-- ----------------------------
-- pms_impl_task  Implementation task
-- ----------------------------
DROP TABLE IF EXISTS `pms_impl_task`;
CREATE TABLE `pms_impl_task` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `project_id`         BIGINT       NOT NULL COMMENT 'Project id',
    `milestone_id`       BIGINT       DEFAULT NULL COMMENT 'Related milestone id',
    `task_name`          VARCHAR(200) NOT NULL COMMENT 'Task name',
    `task_type`          VARCHAR(32)  NOT NULL COMMENT 'OEM=原厂实施 AGENT=代理商实施',
    `agent_id`           BIGINT       DEFAULT NULL COMMENT 'Agent id (for AGENT type)',
    `engineer_id`        BIGINT       DEFAULT NULL COMMENT 'OEM engineer user id (for OEM type)',
    `engineer_name`      VARCHAR(64)  DEFAULT NULL COMMENT 'Engineer name',
    `plan_start_date`    DATE         DEFAULT NULL COMMENT 'Plan start date',
    `plan_end_date`      DATE         DEFAULT NULL COMMENT 'Plan end date',
    `actual_start_date`  DATE         DEFAULT NULL COMMENT 'Actual start date',
    `actual_end_date`    DATE         DEFAULT NULL COMMENT 'Actual end date',
    `status`             VARCHAR(32)  DEFAULT 'PENDING' COMMENT 'PENDING=待接单 ACCEPTED=已接单 IN_PROGRESS=进行中 COMPLETED=已完成 CONFIRMED=已确认 REJECTED=已驳回',
    `progress`           INT          DEFAULT 0 COMMENT 'Progress percent 0-100',
    `work_description`   TEXT         COMMENT 'Work description',
    `accept_opinion`     VARCHAR(500) DEFAULT NULL COMMENT 'Accept/confirm opinion',
    `accept_user_id`     BIGINT       DEFAULT NULL COMMENT 'Accept user id',
    `accept_user_name`   VARCHAR(64)  DEFAULT NULL COMMENT 'Accept user name',
    `accept_time`        DATETIME     DEFAULT NULL COMMENT 'Accept time',
    `create_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`        DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`          VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`        DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`            TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_agent_id` (`agent_id`),
    KEY `idx_engineer_id` (`engineer_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Implementation task';

-- ----------------------------
-- pms_impl_progress  Implementation progress log
-- ----------------------------
DROP TABLE IF EXISTS `pms_impl_progress`;
CREATE TABLE `pms_impl_progress` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `task_id`           BIGINT       NOT NULL COMMENT 'Implementation task id',
    `progress_percent`  INT          DEFAULT NULL COMMENT 'Progress percent 0-100',
    `work_log`          TEXT         COMMENT 'Work log description',
    `photo_urls`        VARCHAR(1000) DEFAULT NULL COMMENT 'Photo urls (comma-separated)',
    `report_user_id`    BIGINT       DEFAULT NULL COMMENT 'Report user id',
    `report_user_name`  VARCHAR(64)  DEFAULT NULL COMMENT 'Report user name',
    `report_time`       DATETIME     DEFAULT NULL COMMENT 'Report time',
    `create_by`         VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`       DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`         VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`       DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`           TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Implementation progress log';

-- ----------------------------
-- pms_agent  Agent / partner company
-- ----------------------------
DROP TABLE IF EXISTS `pms_agent`;
CREATE TABLE `pms_agent` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `agent_name`      VARCHAR(200) NOT NULL COMMENT 'Agent name',
    `agent_code`      VARCHAR(64)  DEFAULT NULL COMMENT 'Agent code',
    `contact_person`  VARCHAR(64)  DEFAULT NULL COMMENT 'Contact person',
    `contact_phone`   VARCHAR(32)  DEFAULT NULL COMMENT 'Contact phone',
    `contact_email`   VARCHAR(100) DEFAULT NULL COMMENT 'Contact email',
    `address`         VARCHAR(500) DEFAULT NULL COMMENT 'Address',
    `qualification`   VARCHAR(500) DEFAULT NULL COMMENT 'Qualification',
    `status`          TINYINT      DEFAULT 1 COMMENT '1=enabled 0=disabled',
    `overall_score`   DECIMAL(3,1) DEFAULT 0.0 COMMENT 'Overall score 0-10',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`     DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`     DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`         TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent / partner company';

-- ----------------------------
-- pms_agent_score  Agent quality evaluation
-- ----------------------------
DROP TABLE IF EXISTS `pms_agent_score`;
CREATE TABLE `pms_agent_score` (
    `id`                            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `agent_id`                      BIGINT       NOT NULL COMMENT 'Agent id',
    `task_id`                       BIGINT       DEFAULT NULL COMMENT 'Task id',
    `response_speed_score`          INT          DEFAULT NULL COMMENT 'Response speed score 0-10',
    `construction_quality_score`    INT          DEFAULT NULL COMMENT 'Construction quality score 0-10',
    `document_completeness_score`   INT          DEFAULT NULL COMMENT 'Document completeness score 0-10',
    `overall_score`                 DECIMAL(3,1) DEFAULT NULL COMMENT 'Overall score of this evaluation',
    `comment`                       VARCHAR(500) DEFAULT NULL COMMENT 'Comment',
    `evaluator_id`                  BIGINT       DEFAULT NULL COMMENT 'Evaluator user id',
    `evaluator_name`                VARCHAR(64)  DEFAULT NULL COMMENT 'Evaluator name',
    `evaluate_time`                 DATETIME     DEFAULT NULL COMMENT 'Evaluate time',
    `create_by`                     VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`                   DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`                     VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`                   DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`                       TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent quality evaluation';

-- ----------------------------
-- pms_settlement  Agent settlement
-- ----------------------------
DROP TABLE IF EXISTS `pms_settlement`;
CREATE TABLE `pms_settlement` (
    `id`                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `task_id`            BIGINT        NOT NULL COMMENT 'Task id',
    `agent_id`           BIGINT        NOT NULL COMMENT 'Agent id',
    `project_id`         BIGINT        DEFAULT NULL COMMENT 'Project id',
    `settlement_no`      VARCHAR(64)   DEFAULT NULL COMMENT 'Settlement no',
    `total_amount`       DECIMAL(14,2) DEFAULT NULL COMMENT 'Total amount (without tax)',
    `tax_rate`           DECIMAL(5,2)  DEFAULT 13.00 COMMENT 'Tax rate (%)',
    `tax_amount`         DECIMAL(14,2) DEFAULT NULL COMMENT 'Tax amount',
    `total_with_tax`     DECIMAL(14,2) DEFAULT NULL COMMENT 'Total amount with tax',
    `status`             VARCHAR(32)   DEFAULT 'PENDING' COMMENT 'PENDING APPROVED REJECTED PUSHED',
    `apply_user_id`      BIGINT        DEFAULT NULL COMMENT 'Apply user id',
    `apply_user_name`    VARCHAR(64)   DEFAULT NULL COMMENT 'Apply user name',
    `apply_time`         DATETIME      DEFAULT NULL COMMENT 'Apply time',
    `approve_user_id`    BIGINT        DEFAULT NULL COMMENT 'Approve user id',
    `approve_user_name`  VARCHAR(64)   DEFAULT NULL COMMENT 'Approve user name',
    `approve_time`       DATETIME      DEFAULT NULL COMMENT 'Approve time',
    `approve_opinion`    VARCHAR(500)  DEFAULT NULL COMMENT 'Approve opinion',
    `push_status`        VARCHAR(32)   DEFAULT NULL COMMENT 'Push status NULL SUCCESS FAILED',
    `push_time`          DATETIME      DEFAULT NULL COMMENT 'Push time',
    `push_response`      VARCHAR(1000) DEFAULT NULL COMMENT 'Push response',
    `create_by`          VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`        DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`          VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`        DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`            TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_settlement_no` (`settlement_no`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_agent_id` (`agent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent settlement';

-- ----------------------------
-- pms_settlement_detail  Settlement line items
-- ----------------------------
DROP TABLE IF EXISTS `pms_settlement_detail`;
CREATE TABLE `pms_settlement_detail` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `settlement_id`  BIGINT        NOT NULL COMMENT 'Settlement id',
    `item_name`      VARCHAR(200)  DEFAULT NULL COMMENT 'Item name',
    `work_quantity`  DECIMAL(10,2) DEFAULT NULL COMMENT 'Work quantity',
    `unit`           VARCHAR(32)   DEFAULT NULL COMMENT 'Unit',
    `unit_price`     DECIMAL(14,2) DEFAULT NULL COMMENT 'Unit price',
    `amount`         DECIMAL(14,2) DEFAULT NULL COMMENT 'Amount',
    `remarks`        VARCHAR(500)  DEFAULT NULL COMMENT 'Remarks',
    `create_by`      VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`    DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`      VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`    DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`        TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_settlement_id` (`settlement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Settlement line item';
