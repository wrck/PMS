-- =============================================================
-- V89__create_yudao_system_tables.sql
-- 创建 yudao system_* 核心 RBAC 表（DDL + 初始数据）
--
-- 背景：
--   PMS 项目要从自研 sys_* 表 RBAC 体系完全迁移到 yudao 的
--   system_* 表 RBAC 体系。本脚本根据 yudao Entity 类反向生成
--   yudao system_* 核心表的 DDL，作为迁移目标表结构。
--
-- 字段来源说明：
--   - BaseDO：create_time, update_time, creator(varchar64), updater(varchar64), deleted(bit1 @TableLogic)
--   - TenantBaseDO：继承 BaseDO + tenant_id(BIGINT)
--   - 多租户已禁用（yudao.tenant.enable: false），但 tenant_id 字段仍保留建表
--     （yudao Entity 里有该字段），只是不自动注入。
--
-- 表清单（12 张）：
--   1. system_users        AdminUserDO   extends TenantBaseDO
--   2. system_role         RoleDO        extends TenantBaseDO
--   3. system_menu         MenuDO        extends BaseDO @TenantIgnore（无 tenant_id）
--   4. system_user_role    UserRoleDO    extends BaseDO（无 tenant_id）
--   5. system_role_menu    RoleMenuDO    extends TenantBaseDO
--   6. system_dept         DeptDO        extends TenantBaseDO
--   7. system_post         PostDO        extends BaseDO（无 tenant_id，Entity 确认）
--   8. system_user_post    UserPostDO    extends BaseDO（无 tenant_id）
--   9. system_dict_type    DictTypeDO    extends BaseDO @TenantIgnore（无 tenant_id）
--  10. system_dict_data    DictDataDO    extends BaseDO @TenantIgnore（无 tenant_id）
--  11. system_tenant       TenantDO      extends BaseDO @TenantIgnore（无 tenant_id）
--  12. system_tenant_package TenantPackageDO extends BaseDO @TenantIgnore（无 tenant_id）
--
-- 幂等性：全部使用 CREATE TABLE IF NOT EXISTS / INSERT IGNORE
-- =============================================================

-- -------------------------------------------------------------
-- 1. system_users  用户表（AdminUserDO extends TenantBaseDO）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_users` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户账号',
    `password`    VARCHAR(100) NOT NULL DEFAULT '' COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `dept_id`     BIGINT       DEFAULT NULL COMMENT '部门ID',
    `post_ids`    VARCHAR(1024) DEFAULT NULL COMMENT '岗位编号数组（JSON）',
    `email`       VARCHAR(128) DEFAULT '' COMMENT '用户邮箱',
    `mobile`      VARCHAR(32)  DEFAULT '' COMMENT '手机号码',
    `sex`         TINYINT      DEFAULT 0 COMMENT '性别（0=未知 1=男 2=女）',
    `avatar`      VARCHAR(512) DEFAULT NULL COMMENT '用户头像',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '账号状态（0=开启 1=禁用）',
    `login_ip`    VARCHAR(50)  DEFAULT NULL COMMENT '最后登录IP',
    `login_date`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -------------------------------------------------------------
-- 2. system_role  角色表（RoleDO extends TenantBaseDO）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_role` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name`                 VARCHAR(64)  NOT NULL COMMENT '角色名称',
    `code`                 VARCHAR(64)  NOT NULL COMMENT '角色标识',
    `sort`                 INT          NOT NULL DEFAULT 0 COMMENT '角色排序',
    `status`               TINYINT      NOT NULL DEFAULT 0 COMMENT '角色状态（0=开启 1=禁用）',
    `type`                 TINYINT      NOT NULL DEFAULT 0 COMMENT '角色类型（1=系统内置 2=自定义）',
    `remark`               VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `data_scope`           TINYINT      DEFAULT 1 COMMENT '数据范围（1=全部 2=自定义部门 3=本部门 4=本部门及以下）',
    `data_scope_dept_ids`  VARCHAR(1024) DEFAULT NULL COMMENT '数据范围指定部门数组（JSON）',
    `tenant_id`            BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`              VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`              VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`              BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`code`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- -------------------------------------------------------------
-- 3. system_menu  菜单表（MenuDO extends BaseDO @TenantIgnore，无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_menu` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
    `name`           VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    `permission`     VARCHAR(128) NOT NULL DEFAULT '' COMMENT '权限标识',
    `type`           TINYINT      NOT NULL DEFAULT 0 COMMENT '菜单类型（1=目录 2=菜单 3=按钮）',
    `sort`           INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `parent_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单ID（0=根节点）',
    `path`           VARCHAR(256) DEFAULT '' COMMENT '路由地址',
    `icon`           VARCHAR(128) DEFAULT '' COMMENT '菜单图标',
    `component`      VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    `component_name` VARCHAR(128) DEFAULT NULL COMMENT '组件名',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '菜单状态（0=开启 1=禁用）',
    `visible`        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否可见（1=可见 0=隐藏）',
    `keep_alive`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否缓存（1=是 0=否）',
    `always_show`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否总是显示（1=是 0=否）',
    `creator`        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- -------------------------------------------------------------
-- 4. system_user_role  用户角色关联表（UserRoleDO extends BaseDO，无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_user_role` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT      NOT NULL COMMENT '角色ID',
    `creator`     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- -------------------------------------------------------------
-- 5. system_role_menu  角色菜单关联表（RoleMenuDO extends TenantBaseDO）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_role_menu` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `role_id`     BIGINT      NOT NULL COMMENT '角色ID',
    `menu_id`     BIGINT      NOT NULL COMMENT '菜单ID',
    `tenant_id`   BIGINT      NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- -------------------------------------------------------------
-- 6. system_dept  部门表（DeptDO extends TenantBaseDO）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_dept` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `name`            VARCHAR(64)  NOT NULL COMMENT '部门名称',
    `parent_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '父部门ID（0=根节点）',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `leader_user_id`  BIGINT       DEFAULT NULL COMMENT '负责人用户ID',
    `phone`           VARCHAR(32)  DEFAULT NULL COMMENT '联系电话',
    `email`           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '部门状态（0=开启 1=禁用）',
    `tenant_id`       BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- -------------------------------------------------------------
-- 7. system_post  岗位表（PostDO extends BaseDO，无 tenant_id —— Entity 确认）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_post` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `code`        VARCHAR(64)  NOT NULL COMMENT '岗位编码',
    `name`        VARCHAR(64)  NOT NULL COMMENT '岗位名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '岗位排序',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '岗位状态（0=开启 1=禁用）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- -------------------------------------------------------------
-- 8. system_user_post  用户岗位关联表（UserPostDO extends BaseDO，无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_user_post` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `post_id`     BIGINT      NOT NULL COMMENT '岗位ID',
    `creator`     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- -------------------------------------------------------------
-- 9. system_dict_type  字典类型表（DictTypeDO extends BaseDO @TenantIgnore，无 tenant_id）
--    Entity 额外含 deleted_time 字段
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_dict_type` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `name`          VARCHAR(100) NOT NULL COMMENT '字典名称',
    `type`          VARCHAR(100) NOT NULL COMMENT '字典类型',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0=开启 1=禁用）',
    `remark`        VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted_time`  DATETIME     DEFAULT NULL COMMENT '删除时间',
    `creator`       VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- -------------------------------------------------------------
-- 10. system_dict_data  字典数据表（DictDataDO extends BaseDO @TenantIgnore，无 tenant_id）
--     Entity 额外含 color_type 字段
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_dict_data` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典数据编号',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '字典排序',
    `label`       VARCHAR(100) NOT NULL DEFAULT '' COMMENT '字典标签',
    `value`       VARCHAR(100) NOT NULL DEFAULT '' COMMENT '字典值',
    `dict_type`   VARCHAR(100) NOT NULL DEFAULT '' COMMENT '字典类型',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0=开启 1=禁用）',
    `color_type`  VARCHAR(32)  DEFAULT NULL COMMENT '颜色类型（default/primary/success/info/warning/danger）',
    `css_class`   VARCHAR(128) DEFAULT NULL COMMENT 'css 样式',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- -------------------------------------------------------------
-- 11. system_tenant  租户表（TenantDO extends BaseDO @TenantIgnore，无 tenant_id）
--     注意：TenantDO Entity 字段含 websites(List<String>)。
--     为兼容下方初始数据 INSERT（使用了 expired_day、tenant_id），
--     额外保留 expired_day、tenant_id 列。
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_tenant` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '租户编号',
    `name`            VARCHAR(64)  NOT NULL COMMENT '租户名',
    `contact_user_id` BIGINT       DEFAULT NULL COMMENT '联系人的用户编号',
    `contact_name`    VARCHAR(64)  DEFAULT NULL COMMENT '联系人',
    `contact_mobile`  VARCHAR(32)  DEFAULT NULL COMMENT '联系手机',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '租户状态（0=开启 1=禁用）',
    `websites`        VARCHAR(1024) DEFAULT NULL COMMENT '绑定域名列表（JSON 数组）',
    `package_id`      BIGINT       DEFAULT 0 COMMENT '租户套餐编号（0=系统内置）',
    `expire_time`     DATETIME     DEFAULT NULL COMMENT '过期时间',
    `account_count`   INT          NOT NULL DEFAULT 0 COMMENT '账号数量',
    `expired_day`     INT          DEFAULT 0 COMMENT '过期天数',
    `tenant_id`       BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号（兼容初始数据）',
    `creator`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- -------------------------------------------------------------
-- 12. system_tenant_package  租户套餐表（TenantPackageDO extends BaseDO @TenantIgnore，无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_tenant_package` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '套餐编号',
    `name`        VARCHAR(64)  NOT NULL COMMENT '套餐名',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '套餐状态（0=开启 1=禁用）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `menu_ids`    VARCHAR(2048) DEFAULT NULL COMMENT '关联的菜单编号数组（JSON）',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_package_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户套餐表';

-- =============================================================
-- 初始数据
-- =============================================================

-- 默认租户
INSERT IGNORE INTO `system_tenant` (`id`, `name`, `contact_name`, `status`, `package_id`, `account_count`, `expired_day`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES (1, '芋道源码', '管理员', 0, 0, 999, 999, '1', NOW(), '1', NOW(), b'0', 1);

-- 默认超管角色（code 必须是 'super_admin' 才能命中 yudao RoleCodeEnum.SUPER_ADMIN）
INSERT IGNORE INTO `system_role` (`id`, `name`, `code`, `sort`, `status`, `type`, `remark`, `data_scope`, `data_scope_dept_ids`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES (1, '超级管理员', 'super_admin', 1, 0, 1, '内置超级管理员角色', 1, NULL, '1', NOW(), '1', NOW(), b'0', 1);

-- 默认 admin 用户（密码 admin123 的 BCrypt hash，用 $2a$ 前缀兼容 yudao BCryptPasswordEncoder）
INSERT IGNORE INTO `system_users` (`id`, `username`, `password`, `nickname`, `remark`, `dept_id`, `post_ids`, `email`, `mobile`, `sex`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES (1, 'admin', '$2a$10$mRMIYLDtRHlf6.9ipiqH1.Z.bh/R9dO9d8iOiVSXLJrLl7yMcoOla', '管理员', '内置管理员用户', NULL, NULL, 'admin@dptech.com', '13800000000', 0, NULL, 0, NULL, NULL, '1', NOW(), '1', NOW(), b'0', 1);

-- admin 用户绑定超管角色
INSERT IGNORE INTO `system_user_role` (`id`, `user_id`, `role_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 默认顶级部门
INSERT IGNORE INTO `system_dept` (`id`, `name`, `parent_id`, `sort`, `leader_user_id`, `phone`, `email`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES (1, 'DPtech', 0, 1, NULL, NULL, NULL, 0, '1', NOW(), '1', NOW(), b'0', 1);

-- =============================================================
-- 文件结束
-- =============================================================
