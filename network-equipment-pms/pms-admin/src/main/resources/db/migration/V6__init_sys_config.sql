-- =============================================================
-- V6__init_sys_config.sql
-- Initialize system parameter configuration table.
-- =============================================================

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `config_name`  VARCHAR(100) DEFAULT '' COMMENT 'Config name',
    `config_key`   VARCHAR(100) NOT NULL COMMENT 'Config key',
    `config_value` VARCHAR(500) DEFAULT '' COMMENT 'Config value',
    `config_type`  CHAR(1)      DEFAULT '1' COMMENT '0=system built-in 1=user-defined',
    `remark`       VARCHAR(255) DEFAULT '' COMMENT 'Remark',
    `create_by`    VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`  DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`    VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`  DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`      TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System parameter configuration';

-- Default configurations
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `remark`, `create_by`, `create_time`) VALUES
('System Name', 'sys.name', 'Network Equipment PMS', '0', 'System display name', 'admin', NOW()),
('System Version', 'sys.version', '1.0.0', '0', 'System version', 'admin', NOW()),
('Default Password', 'sys.user.initPassword', 'admin123', '0', 'Initial password for new users', 'admin', NOW()),
('Self Registration', 'sys.account.registerUser', 'false', '0', 'Whether self registration is enabled', 'admin', NOW());
