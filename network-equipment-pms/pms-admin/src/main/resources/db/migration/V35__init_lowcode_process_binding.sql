-- V35: 低代码流程绑定表（绑定流程节点 → 低代码表单）

CREATE TABLE IF NOT EXISTS `pms_lowcode_process_binding` (
    `id`                       BIGINT       NOT NULL AUTO_INCREMENT,
    `process_definition_key`   VARCHAR(128) NOT NULL COMMENT '流程定义 key',
    `process_definition_name`  VARCHAR(256) NULL,
    `node_form_bindings`       LONGTEXT     NOT NULL COMMENT '节点 → 表单绑定 JSON: [{nodeId, formCode, microflowCode}]',
    `status`                   VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    `create_by`                VARCHAR(64)  NULL,
    `update_by`                VARCHAR(64)  NULL,
    `create_time`              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`                  TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_process_key` (`process_definition_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码流程绑定';

INSERT IGNORE INTO `sys_permission` (`code`, `name`, `type`, `parent_id`, `sort`) VALUES
('lowcode:process:list',  '流程列表', 'BUTTON', 0, 120),
('lowcode:process:edit',  '流程编辑', 'BUTTON', 0, 121),
('lowcode:process:deploy','流程部署', 'BUTTON', 0, 122);
