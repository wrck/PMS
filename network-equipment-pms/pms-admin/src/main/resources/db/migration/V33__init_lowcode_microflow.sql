-- V33: 低代码微流表 + 微流版本表 + form.events 字段扩展

CREATE TABLE IF NOT EXISTS `pms_lowcode_microflow` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        VARCHAR(64)  NOT NULL                COMMENT '微流编码（唯一）',
    `name`        VARCHAR(128) NOT NULL                COMMENT '微流名称',
    `description` VARCHAR(512) NULL                    COMMENT '描述',
    `definition`  LONGTEXT     NULL                    COMMENT '微流定义 JSON（节点 + 边）',
    `status`      VARCHAR(16)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED/ARCHIVED',
    `version`     INT          NOT NULL DEFAULT 1      COMMENT '版本号',
    `biz_type`    VARCHAR(64)  NULL                    COMMENT '业务类型',
    `create_by`   VARCHAR(64)  NULL,
    `update_by`   VARCHAR(64)  NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码微流';

CREATE TABLE IF NOT EXISTS `pms_lowcode_microflow_version` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `microflow_id`  BIGINT       NOT NULL,
    `version`       INT          NOT NULL,
    `definition`    LONGTEXT     NOT NULL,
    `change_log`    VARCHAR(512) NULL,
    `status`        VARCHAR(16)  NOT NULL DEFAULT 'PUBLISHED',
    `create_by`     VARCHAR(64)  NULL,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_microflow_id` (`microflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码微流版本';

-- 扩展 pms_lowcode_form 表，新增 events 字段
ALTER TABLE `pms_lowcode_form` ADD COLUMN `events` LONGTEXT NULL COMMENT '事件绑定 JSON: {onLoad:{type,code}, onChange:{...}, onSubmit:{...}}' AFTER `form_config`;

-- 权限初始化
INSERT IGNORE INTO `sys_permission` (`code`, `name`, `type`, `parent_id`, `sort`) VALUES
('lowcode:microflow:list',  '微流列表', 'BUTTON', 0, 100),
('lowcode:microflow:edit',  '微流编辑', 'BUTTON', 0, 101),
('lowcode:microflow:exec',  '微流执行', 'BUTTON', 0, 102),
('lowcode:rule:list',       '规则列表', 'BUTTON', 0, 110),
('lowcode:rule:edit',       '规则编辑', 'BUTTON', 0, 111),
('lowcode:rule:exec',       '规则执行', 'BUTTON', 0, 112);
