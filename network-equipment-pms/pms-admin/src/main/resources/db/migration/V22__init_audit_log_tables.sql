-- =============================================================
-- V22__init_audit_log_tables.sql
-- Initialize audit log tables: login log, exception log and
-- schedule log, used by the audit log module.
-- =============================================================

-- ----------------------------
-- sys_login_log  登录日志
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `username`       VARCHAR(100) DEFAULT NULL COMMENT 'Login username',
    `login_time`     DATETIME     DEFAULT NULL COMMENT 'Login time',
    `login_ip`       VARCHAR(50)  DEFAULT NULL COMMENT 'Login IP',
    `login_location` VARCHAR(200) DEFAULT NULL COMMENT 'Login location',
    `browser`        VARCHAR(100) DEFAULT NULL COMMENT 'Browser type',
    `os`             VARCHAR(100) DEFAULT NULL COMMENT 'Operating system',
    `status`         VARCHAR(20)  DEFAULT NULL COMMENT 'Login status: SUCCESS/FAIL',
    `message`        VARCHAR(500) DEFAULT NULL COMMENT 'Prompt message',
    `user_id`        BIGINT       DEFAULT NULL COMMENT 'User id',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Login log';

-- ----------------------------
-- sys_exception_log  异常日志
-- ----------------------------
DROP TABLE IF EXISTS `sys_exception_log`;
CREATE TABLE `sys_exception_log` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `user_id`          BIGINT       DEFAULT NULL COMMENT 'User id',
    `username`         VARCHAR(100) DEFAULT NULL COMMENT 'Username',
    `request_uri`      VARCHAR(500) DEFAULT NULL COMMENT 'Request URI',
    `request_method`   VARCHAR(10)  DEFAULT NULL COMMENT 'HTTP method: GET/POST etc.',
    `request_params`   TEXT         COMMENT 'Request params',
    `exception_type`   VARCHAR(255) DEFAULT NULL COMMENT 'Exception type',
    `exception_message` TEXT        COMMENT 'Exception message',
    `stack_trace`      LONGTEXT     COMMENT 'Full stack trace',
    `request_ip`       VARCHAR(50)  DEFAULT NULL COMMENT 'Request IP',
    `occur_time`       DATETIME     DEFAULT NULL COMMENT 'Occur time',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_occur_time` (`occur_time`),
    KEY `idx_uri` (`request_uri`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Exception log';

-- ----------------------------
-- sys_schedule_log  定时任务日志
-- ----------------------------
DROP TABLE IF EXISTS `sys_schedule_log`;
CREATE TABLE `sys_schedule_log` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `task_name`       VARCHAR(200) DEFAULT NULL COMMENT 'Task name',
    `task_group`      VARCHAR(100) DEFAULT NULL COMMENT 'Task group',
    `cron_expression` VARCHAR(100) DEFAULT NULL COMMENT 'Cron expression',
    `start_time`      DATETIME     DEFAULT NULL COMMENT 'Start time',
    `end_time`        DATETIME     DEFAULT NULL COMMENT 'End time',
    `cost_ms`         BIGINT       DEFAULT NULL COMMENT 'Cost in milliseconds',
    `status`          VARCHAR(20)  DEFAULT NULL COMMENT 'Status: SUCCESS/FAIL',
    `error_message`   TEXT         COMMENT 'Error message',
    `trigger_type`    VARCHAR(20)  DEFAULT NULL COMMENT 'Trigger type: AUTO/MANUAL',
    PRIMARY KEY (`id`),
    KEY `idx_task` (`task_name`),
    KEY `idx_status_time` (`status`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Schedule log';
