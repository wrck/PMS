-- V41: 低代码发布记录表

CREATE TABLE IF NOT EXISTS `pms_lowcode_publish_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `config_type`   VARCHAR(32)  NOT NULL,
    `config_id`     BIGINT       NOT NULL,
    `config_code`   VARCHAR(128) NULL,
    `version`       INT          NOT NULL COMMENT '发布的版本号',
    `status`        VARCHAR(16)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/SUBMITTED/APPROVED/REJECTED/PUBLISHED',
    `applicant_id`  BIGINT       NULL,
    `applicant`     VARCHAR(64)  NULL,
    `approver_id`   BIGINT       NULL,
    `approver`      VARCHAR(64)  NULL,
    `change_log`    VARCHAR(512) NULL,
    `reject_reason` VARCHAR(512) NULL,
    `submitted_at`  DATETIME     NULL,
    `approved_at`   DATETIME     NULL,
    `published_at`  DATETIME     NULL,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_config` (`config_type`, `config_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码发布记录';

INSERT IGNORE INTO `sys_permission` (`code`, `name`, `type`, `parent_id`, `sort`) VALUES
('lowcode:publish:list',     '发布列表',   'BUTTON', 0, 170),
('lowcode:publish:submit',   '提交发布',   'BUTTON', 0, 171),
('lowcode:publish:approve',  '审批发布',   'BUTTON', 0, 172),
('lowcode:publish:rollback', '回滚发布',   'BUTTON', 0, 173);
