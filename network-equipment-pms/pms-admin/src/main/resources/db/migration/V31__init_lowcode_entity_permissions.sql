-- V31: 低代码数据建模与版本控制权限初始化
-- 注意：V29__init_lowcode_entity_tables.sql 已注册"实体设计器"菜单（order_num=2，perms=lowcode:entity:list）。
-- 本脚本仅补充"版本历史"菜单 + 全部按钮权限 + 动态数据权限模板 + 管理员角色授权，避免重复插入。

-- 版本历史页面菜单（实体设计器菜单已在 V29 注册，order_num=2，此处版本历史用 order_num=3 保持顺序）
INSERT INTO sys_menu (menu_name, parent_id, path, component, menu_type, perms, icon, order_num, visible, status, create_time, create_by) VALUES
('版本历史', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name='低代码管理' LIMIT 1) t), 'version-history', 'lowcode/version-history/index', 'C', 'lowcode:version:list', 'Timer', 3, '0', '0', NOW(), 'system');

-- 实体管理按钮权限（父菜单 = 实体设计器 lowcode:entity:list，已在 V29 注册）
INSERT INTO sys_menu (menu_name, parent_id, menu_type, perms, order_num, visible, status, create_time, create_by) VALUES
('实体查询', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:entity:query', 1, '0', '0', NOW(), 'system'),
('实体新增', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:entity:add', 2, '0', '0', NOW(), 'system'),
('DDL生成', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:entity:ddl', 3, '0', '0', NOW(), 'system'),
('实体发布', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:entity:publish', 4, '0', '0', NOW(), 'system'),
('实体删除', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:entity:delete', 5, '0', '0', NOW(), 'system');

-- 版本管理按钮权限（父菜单 = 版本历史 lowcode:version:list，本脚本上方注册）
INSERT INTO sys_menu (menu_name, parent_id, menu_type, perms, order_num, visible, status, create_time, create_by) VALUES
('版本对比', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:version:list' LIMIT 1) t), 'F', 'lowcode:version:diff', 1, '0', '0', NOW(), 'system'),
('版本回滚', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:version:list' LIMIT 1) t), 'F', 'lowcode:version:rollback', 2, '0', '0', NOW(), 'system'),
('环境晋升', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:version:list' LIMIT 1) t), 'F', 'lowcode:version:promote', 3, '0', '0', NOW(), 'system'),
('配置包导出', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:version:list' LIMIT 1) t), 'F', 'lowcode:version:export', 4, '0', '0', NOW(), 'system');

-- 动态数据权限模板（实际权限为 lowcode:data:{entityCode}:list 等，运行时动态校验，* 为通配符占位）
INSERT INTO sys_menu (menu_name, parent_id, menu_type, perms, order_num, visible, status, create_time, create_by) VALUES
('动态数据查询', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:data:*:list', 10, '0', '0', NOW(), 'system'),
('动态数据新增', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:data:*:add', 11, '0', '0', NOW(), 'system'),
('动态数据编辑', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:data:*:edit', 12, '0', '0', NOW(), 'system'),
('动态数据删除', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms='lowcode:entity:list' LIMIT 1) t), 'F', 'lowcode:data:*:delete', 13, '0', '0', NOW(), 'system');

-- 管理员角色授权（role_id=1 关联所有低代码相关权限）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu
WHERE perms LIKE 'lowcode:entity%' OR perms LIKE 'lowcode:version%' OR perms LIKE 'lowcode:data%'
ON DUPLICATE KEY UPDATE role_id = 1;
