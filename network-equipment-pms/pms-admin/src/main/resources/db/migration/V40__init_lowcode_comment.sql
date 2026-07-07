-- V40: 低代码配置评论表

CREATE TABLE IF NOT EXISTS `pms_lowcode_comment` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `config_type`  VARCHAR(32)  NOT NULL,
    `config_id`    BIGINT       NOT NULL,
    `user_id`      BIGINT       NOT NULL,
    `user_name`    VARCHAR(64)  NULL,
    `content`      TEXT         NOT NULL COMMENT '评论内容（支持 @提及）',
    `mentions`     VARCHAR(512) NULL COMMENT '@提及的用户 ID 列表（逗号分隔）',
    `parent_id`    BIGINT       NULL COMMENT '父评论 ID（用于回复）',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_config` (`config_type`, `config_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码配置评论';

INSERT IGNORE INTO `sys_permission` (`code`, `name`, `type`, `parent_id`, `sort`) VALUES
('lowcode:comment:list', '评论列表', 'BUTTON', 0, 160),
('lowcode:comment:add',  '添加评论', 'BUTTON', 0, 161),
('lowcode:comment:del',  '删除评论', 'BUTTON', 0, 162);
