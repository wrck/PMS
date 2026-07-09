-- 批次5-T10: 应用源码导出 — 权限菜单
-- 借鉴网易轻舟源码导出 — 无黑盒引擎
-- 无需建表（复用现有 bizType 字段作为应用分组键），仅注册权限菜单

-- 权限：应用源码导出父菜单（M 目录）+ 子权限（C 菜单 / F 按钮）
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('应用源码', 0, 55, 'app-source', NULL, NULL, 'M', '0', '0', NOW());
SET @parentId = (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '应用源码' AND parent_id = 0 ORDER BY id DESC LIMIT 1) t);
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time) VALUES
('应用列表',   @parentId, 1, 'apps',    'lowcode/app-source/list',    'lowcode:app-source:export',  'C', '0', '0', NOW()),
('预览清单',   @parentId, 2, 'manifest', NULL,                         'lowcode:app-source:export',  'F', '0', '0', NOW()),
('导出源码',   @parentId, 3, 'export',  NULL,                          'lowcode:app-source:export',  'F', '0', '0', NOW());
