-- =============================================================
-- V1__init_system_tables.sql
-- Initialize system management tables for network equipment PMS.
-- =============================================================

-- ----------------------------
-- sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT 'Parent department id',
    `dept_name`   VARCHAR(50)  NOT NULL COMMENT 'Department name',
    `order_num`   INT          DEFAULT 0 COMMENT 'Display order',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '0=normal 1=disabled',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System department';

-- ----------------------------
-- sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `username`    VARCHAR(50)  NOT NULL COMMENT 'Username',
    `password`    VARCHAR(100) DEFAULT '' COMMENT 'Password (BCrypt)',
    `real_name`   VARCHAR(50)  DEFAULT '' COMMENT 'Real name',
    `email`       VARCHAR(100) DEFAULT '' COMMENT 'Email',
    `phone`       VARCHAR(20)  DEFAULT '' COMMENT 'Phone',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '0=normal 1=disabled',
    `dept_id`     BIGINT       DEFAULT NULL COMMENT 'Department id',
    `company_id`  BIGINT       DEFAULT NULL COMMENT 'Company id',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System user';

-- ----------------------------
-- sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `role_name`   VARCHAR(30)  NOT NULL COMMENT 'Role name',
    `role_code`   VARCHAR(100) NOT NULL COMMENT 'Role code',
    `description` VARCHAR(255) DEFAULT '' COMMENT 'Description',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '0=normal 1=disabled',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System role';

-- ----------------------------
-- sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT 'Parent menu id',
    `menu_name`   VARCHAR(50)  NOT NULL COMMENT 'Menu name',
    `menu_type`   CHAR(1)      DEFAULT '' COMMENT 'M=directory C=menu F=button',
    `path`        VARCHAR(200) DEFAULT '' COMMENT 'Routing path',
    `component`   VARCHAR(255) DEFAULT NULL COMMENT 'Frontend component',
    `perms`       VARCHAR(100) DEFAULT '' COMMENT 'Permission identifier',
    `icon`        VARCHAR(100) DEFAULT '' COMMENT 'Menu icon',
    `order_num`   INT          DEFAULT 0 COMMENT 'Display order',
    `visible`     CHAR(1)      DEFAULT '0' COMMENT '0=visible 1=hidden',
    `is_frame`    CHAR(1)      DEFAULT '1' COMMENT '0=yes 1=no (external link)',
    `is_cache`    CHAR(1)      DEFAULT '0' COMMENT '0=yes 1=no (cache)',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '0=normal 1=disabled',
    `remark`      VARCHAR(500) DEFAULT '' COMMENT 'Remark',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System menu';

-- ----------------------------
-- sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `code`        VARCHAR(100) NOT NULL COMMENT 'Permission code',
    `name`        VARCHAR(100) NOT NULL COMMENT 'Permission name',
    `type`        VARCHAR(20)  DEFAULT 'menu' COMMENT 'Permission type: menu/button/api',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT 'Parent permission id',
    `sort`        INT          DEFAULT 0 COMMENT 'Display order',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System permission';

-- ----------------------------
-- sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `dict_name`   VARCHAR(100) DEFAULT '' COMMENT 'Dictionary name',
    `dict_type`   VARCHAR(100) NOT NULL COMMENT 'Dictionary type',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '0=normal 1=disabled',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System dictionary';

-- ----------------------------
-- sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `dict_id`     BIGINT       NOT NULL COMMENT 'Dictionary id',
    `item_text`   VARCHAR(100) NOT NULL COMMENT 'Item text',
    `item_value`  VARCHAR(100) NOT NULL COMMENT 'Item value',
    `sort_order`  INT          DEFAULT 0 COMMENT 'Sort order',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System dictionary item';

-- ----------------------------
-- sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `user_id`     BIGINT       NOT NULL COMMENT 'User id',
    `role_id`     BIGINT       NOT NULL COMMENT 'Role id',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User-role mapping';

-- ----------------------------
-- sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `role_id`     BIGINT       NOT NULL COMMENT 'Role id',
    `menu_id`     BIGINT       NOT NULL COMMENT 'Menu id',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time` DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time` DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`     TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role-menu mapping';

-- ----------------------------
-- sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `title`          VARCHAR(50)  DEFAULT '' COMMENT 'Module title',
    `business_type`  INT          DEFAULT 0 COMMENT 'Business type',
    `method`         VARCHAR(200) DEFAULT '' COMMENT 'Method name',
    `request_method` VARCHAR(10)  DEFAULT '' COMMENT 'HTTP method',
    `oper_name`      VARCHAR(50)  DEFAULT '' COMMENT 'Operator name',
    `oper_url`       VARCHAR(255) DEFAULT '' COMMENT 'Request URL',
    `oper_param`     TEXT         COMMENT 'Request params',
    `json_result`    TEXT         COMMENT 'Response result',
    `status`         TINYINT      DEFAULT 0 COMMENT '0=success 1=failure',
    `error_msg`      VARCHAR(2000) DEFAULT '' COMMENT 'Error message',
    `oper_time`      DATETIME     DEFAULT NULL COMMENT 'Operation time',
    `create_by`      VARCHAR(64)  DEFAULT '' COMMENT 'Creator',
    `create_time`    DATETIME     DEFAULT NULL COMMENT 'Create time',
    `update_by`      VARCHAR(64)  DEFAULT '' COMMENT 'Updater',
    `update_time`    DATETIME     DEFAULT NULL COMMENT 'Update time',
    `deleted`        TINYINT      DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Operation log';

-- =============================================================
-- Default data
-- =============================================================

-- Default company root department
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `order_num`, `status`, `create_by`, `create_time`)
VALUES (1, 0, 'DPtech', 0, '0', 'admin', NOW());

-- Default admin user. Password is BCrypt-encoded "admin123".
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `status`, `dept_id`, `company_id`, `create_by`, `create_time`)
VALUES (1, 'admin', '$2b$10$UfXeez/a8cqc/QZhlEsqiO6Gmr9tGO3yo6XHhwvBJttiBtdThRqfa', 'Administrator', 'admin@dptech.com', '13800000000', '0', 1, 1, 'admin', NOW());

-- Default super admin role
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`, `create_by`, `create_time`)
VALUES (1, 'Super Administrator', 'admin', 'Super administrator with all permissions', '0', 'admin', NOW());

-- Bind admin user to super admin role
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_by`, `create_time`)
VALUES (1, 1, 1, 'admin', NOW());

-- Default menus for system management
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(1, 0, 'System',       'M', '/system',        NULL,                  '',                       'system',  1, '0', 'admin', NOW()),
(2, 1, 'User',         'C', '/system/user',   'system/user/index',   'system:user:list',      'user',    1, '0', 'admin', NOW()),
(3, 1, 'Role',         'C', '/system/role',   'system/role/index',   'system:role:list',      'peoples', 2, '0', 'admin', NOW()),
(4, 1, 'Menu',         'C', '/system/menu',   'system/menu/index',   'system:menu:list',      'tree-table', 3, '0', 'admin', NOW()),
(5, 1, 'Dept',         'C', '/system/dept',   'system/dept/index',   'system:dept:list',      'tree',    4, '0', 'admin', NOW()),
(6, 1, 'Dict',         'C', '/system/dict',   'system/dict/index',   'system:dict:list',      'dict',    5, '0', 'admin', NOW()),
(7, 2, 'User Add',     'F', '',               NULL,                  'system:user:add',       '#',       1, '0', 'admin', NOW()),
(8, 2, 'User Edit',    'F', '',               NULL,                  'system:user:edit',      '#',       2, '0', 'admin', NOW()),
(9, 2, 'User Delete',  'F', '',               NULL,                  'system:user:remove',    '#',       3, '0', 'admin', NOW()),
(10, 3, 'Role Add',    'F', '',               NULL,                  'system:role:add',       '#',       1, '0', 'admin', NOW()),
(11, 3, 'Role Edit',   'F', '',               NULL,                  'system:role:edit',      '#',       2, '0', 'admin', NOW()),
(12, 3, 'Role Delete', 'F', '',               NULL,                  'system:role:remove',    '#',       3, '0', 'admin', NOW());

-- Bind all menus to super admin role
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`, `create_time`)
SELECT 1, `id`, 'admin', NOW() FROM `sys_menu`;

-- Default dictionary: user status
INSERT INTO `sys_dict` (`id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`)
VALUES (1, 'User Status', 'sys_user_status', '0', 'admin', NOW());

INSERT INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(1, 'Normal',   '0', 1, 'admin', NOW()),
(1, 'Disabled', '1', 2, 'admin', NOW());
