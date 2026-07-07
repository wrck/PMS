-- V37: 低代码连接器表

CREATE TABLE IF NOT EXISTS `pms_lowcode_connector` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `code`        VARCHAR(64)  NOT NULL,
    `name`        VARCHAR(128) NOT NULL,
    `description` VARCHAR(512) NULL,
    `type`        VARCHAR(16)  NOT NULL COMMENT 'REST/DB',
    `config`      LONGTEXT     NOT NULL COMMENT '配置 JSON: REST={url,method,auth,...}; DB={url,username,password,...}',
    `status`      VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    `version`     INT          NOT NULL DEFAULT 1,
    `biz_type`    VARCHAR(64)  NULL,
    `create_by`   VARCHAR(64)  NULL,
    `update_by`   VARCHAR(64)  NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码连接器';

INSERT IGNORE INTO `sys_permission` (`code`, `name`, `type`, `parent_id`, `sort`) VALUES
('lowcode:connector:list',  '连接器列表', 'BUTTON', 0, 140),
('lowcode:connector:edit',  '连接器编辑', 'BUTTON', 0, 141),
('lowcode:connector:test',  '连接器测试', 'BUTTON', 0, 142);
