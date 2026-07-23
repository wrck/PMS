-- =============================================================
-- V90__migrate_pms_sys_to_yudao_system.sql
-- 将 PMS 自研 sys_* 表数据迁移到 yudao system_* 表
--
-- 依赖：V89__create_yudao_system_tables.sql（已创建 system_* 表及初始数据）
--
-- 迁移映射总览：
--   sys_user      → system_users      real_name→nickname, phone→mobile, 丢弃 company_id
--   sys_role      → system_role       role_name→name, role_code→code, description→remark
--   sys_menu      → system_menu       menu_name→name, perms→permission
--   sys_user_role → system_user_role
--   sys_role_menu → system_role_menu  menu_id 同步 +999 偏移
--   sys_dept      → system_dept       dept_name→name, order_num→sort
--
-- ID 偏移策略：
--   - sys_menu 的 id 统一 +999，避开 yudao 内置菜单 id 1~999
--   - sys_menu 的 parent_id 同步 +999（根节点 parent_id=0 保持不变），维持菜单树结构
--   - sys_role_menu 的 menu_id 同步 +999，与迁移后的 system_menu.id 对齐
--   - sys_user / sys_role / sys_dept 的 id 保持原值（V89 已创建 id=1 的初始数据，迁移时跳过 id=1）
--
-- 类型转换说明（依据 yudao Entity 确认）：
--   - deleted: PMS TINYINT(0/1) → yudao BIT(1)，用 IF(deleted=0, b'0', b'1')
--   - menu_type: PMS CHAR(M/C/F/L) → yudao Integer(1/2/3)，按 MenuTypeEnum 映射
--       M=目录→1(DIR), C=菜单→2(MENU), L=低代码菜单→2(MENU), F=按钮→3(BUTTON)
--   - visible: PMS CHAR('0'=可见/'1'=隐藏) → yudao Boolean(1=可见/0=隐藏)，语义取反
--       用 IF(visible='0', 1, 0)
--   - status: PMS CHAR('0'/'1') → yudao TINYINT(0/1)，值一致，MySQL 隐式转换
--   - sys_dept 无 leader/phone/email 列（V1 建表确认），对应字段填 NULL
--
-- 幂等性：全部使用 INSERT IGNORE，重复执行不会报错
-- =============================================================

-- -------------------------------------------------------------
-- 1. sys_user → system_users
--    real_name → nickname, phone → mobile, 丢弃 company_id
--    跳过 id=1（V89 已创建 admin 用户）
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_users` (`id`, `username`, `password`, `nickname`, `remark`, `dept_id`, `post_ids`, `email`, `mobile`, `sex`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, `username`, `password`, `real_name`, NULL, `dept_id`, NULL, `email`, `phone`, 0, NULL, `status`, NULL, NULL, `create_by`, `create_time`, `update_by`, `update_time`, IF(`deleted`=0, b'0', b'1'), 1
FROM `sys_user` WHERE `id` > 1;

-- -------------------------------------------------------------
-- 2. sys_role → system_role
--    role_name → name, role_code → code, description → remark
--    跳过 id=1（V89 已创建 super_admin 角色）
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_role` (`id`, `name`, `code`, `sort`, `status`, `type`, `remark`, `data_scope`, `data_scope_dept_ids`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, `role_name`, `role_code`, 1, `status`, 1, `description`, 1, NULL, `create_by`, `create_time`, `update_by`, `update_time`, IF(`deleted`=0, b'0', b'1'), 1
FROM `sys_role` WHERE `id` > 1;

-- -------------------------------------------------------------
-- 3. sys_menu → system_menu
--    menu_name → name, perms → permission
--    id 偏移 +999（避开 yudao 内置菜单 id 1~999）
--    parent_id 同步 +999（根节点 0 保持不变），维持菜单树结构
--    menu_type 按 MenuTypeEnum 映射为整数：M→1, C→2, L→2, F→3
--    visible 语义取反：PMS '0'=可见 → yudao 1=可见
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT
    `id` + 999,
    `menu_name`,
    `perms`,
    CASE `menu_type`
        WHEN 'M' THEN 1   -- 目录 → DIR
        WHEN 'C' THEN 2   -- 菜单 → MENU
        WHEN 'L' THEN 2   -- 低代码菜单 → MENU
        WHEN 'F' THEN 3   -- 按钮 → BUTTON
        ELSE 2            -- 默认 → MENU
    END,
    `order_num`,
    IF(`parent_id` = 0, 0, `parent_id` + 999),
    `path`,
    `icon`,
    `component`,
    NULL,
    `status`,
    IF(`visible` = '0', 1, 0),
    0,
    0,
    `create_by`,
    `create_time`,
    `update_by`,
    `update_time`,
    IF(`deleted`=0, b'0', b'1')
FROM `sys_menu`;

-- -------------------------------------------------------------
-- 4. sys_user_role → system_user_role
--    跳过 user_id=1（V89 已创建 admin↔super_admin 绑定）
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_user_role` (`user_id`, `role_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT `user_id`, `role_id`, `create_by`, `create_time`, `update_by`, `update_time`, IF(`deleted`=0, b'0', b'1')
FROM `sys_user_role` WHERE `user_id` > 1;

-- -------------------------------------------------------------
-- 5. sys_role_menu → system_role_menu
--    menu_id 同步 +999 偏移，与迁移后的 system_menu.id 对齐
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `role_id`, `menu_id` + 999, `create_by`, `create_time`, `update_by`, `update_time`, IF(`deleted`=0, b'0', b'1'), 1
FROM `sys_role_menu`;

-- -------------------------------------------------------------
-- 6. sys_dept → system_dept
--    dept_name → name, order_num → sort
--    跳过 id=1（V89 已创建 DPtech 顶级部门）
--    注意：sys_dept 无 leader/phone/email 列，对应字段填 NULL
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_dept` (`id`, `name`, `parent_id`, `sort`, `leader_user_id`, `phone`, `email`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, `dept_name`, `parent_id`, `order_num`, NULL, NULL, NULL, `status`, `create_by`, `create_time`, `update_by`, `update_time`, IF(`deleted`=0, b'0', b'1'), 1
FROM `sys_dept` WHERE `id` > 1;

-- -------------------------------------------------------------
-- 7. 把所有从 PMS 迁移过来的菜单（id >= 1000）绑定到超管角色（role_id=1）
--    确保超级管理员拥有全部 PMS 菜单权限
--    INSERT IGNORE 自动去重（与步骤 5 中 role_id=1 的记录重叠时跳过）
-- -------------------------------------------------------------
INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, `id`, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` WHERE `id` >= 1000;

-- =============================================================
-- 文件结束
-- =============================================================
