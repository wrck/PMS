-- =============================================================
-- V61__fix_lowcode_menus.sql
-- Fix lowcode menu registration: create parent menu, register
-- missing C menus, fix path mismatches, hide orphan menus,
-- and bind all lowcode permissions to super admin role.
--
-- Root cause: V29/V31/V32 assumed a '低代码管理' parent menu was
-- created, but it never was. V53/V54/V55/V56 registered menus with
-- paths that don't match actual frontend routes. 12 lowcode routes
-- have no DB menu entry at all.
-- =============================================================

-- 1. Create the '低代码管理' parent directory menu (if not exists)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, icon, is_frame, is_cache, create_by, create_time)
SELECT '低代码管理', 0, 50, 'lowcode', NULL, 'M', '0', '0', 'MagicStick', 1, 0, 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '低代码管理' AND parent_id = 0 AND deleted = 0);

SET @lcParentId = (SELECT id FROM sys_menu WHERE menu_name = '低代码管理' AND parent_id = 0 AND deleted = 0 ORDER BY id DESC LIMIT 1);

-- 2. Fix existing menus: update parent_id and path for menus that were orphaned
-- 924 实体设计器 (parent was NULL)
UPDATE sys_menu SET parent_id = @lcParentId, path = 'lowcode/entity-designer', order_num = 1
  WHERE menu_name = '实体设计器' AND parent_id IS NULL AND deleted = 0;
-- 925 版本历史 (parent was NULL)
UPDATE sys_menu SET parent_id = @lcParentId, path = 'lowcode/version-history', order_num = 13
  WHERE menu_name = '版本历史' AND parent_id IS NULL AND deleted = 0;

-- 3. Register missing C menus (matching frontend router non-hidden routes)
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, icon, is_frame, is_cache, create_by, create_time)
VALUES
('表单配置',     @lcParentId, 2,  'lowcode/form-list',         'lowcode/form-list/index',         'C', '0', '0', 'Document',    1, 0, 'admin', NOW()),
('列表配置',     @lcParentId, 3,  'lowcode/list-list',         'lowcode/list-list/index',         'C', '0', '0', 'List',        1, 0, 'admin', NOW()),
('标签页配置',   @lcParentId, 4,  'lowcode/tab-list',          'lowcode/tab-list/index',          'C', '0', '0', 'Files',       1, 0, 'admin', NOW()),
('关联页配置',   @lcParentId, 5,  'lowcode/related-page-list', 'lowcode/related-page-list/index', 'C', '0', '0', 'Share',       1, 0, 'admin', NOW()),
('微流设计器',   @lcParentId, 6,  'lowcode/microflow-designer','lowcode/microflow-designer/index','C', '0', '0', 'Share',       1, 0, 'admin', NOW()),
('规则设计器',   @lcParentId, 7,  'lowcode/rule-designer',     'lowcode/rule-designer/index',     'C', '0', '0', 'Filter',      1, 0, 'admin', NOW()),
('流程设计器',   @lcParentId, 8,  'lowcode/process-designer',  'lowcode/process-designer/index',  'C', '0', '0', 'Connection',  1, 0, 'admin', NOW()),
('触发器',       @lcParentId, 9,  'lowcode/trigger-list',      'lowcode/trigger-list/index',      'C', '0', '0', 'BellFilled',  1, 0, 'admin', NOW()),
('连接器配置',   @lcParentId, 10, 'lowcode/connector-designer','lowcode/connector-designer/index','C', '0', '0', 'Connection',  1, 0, 'admin', NOW()),
('发布中心',     @lcParentId, 11, 'lowcode/publish-center',    'lowcode/publish-center/index',    'C', '0', '0', 'Promotion',   1, 0, 'admin', NOW()),
('审批链配置',   @lcParentId, 12, 'lowcode/approval-chain',    'lowcode/approval-chain/index',    'C', '0', '0', 'SetUp',       1, 0, 'admin', NOW()),
('APM 看板',     @lcParentId, 14, 'lowcode/apm-dashboard',     'lowcode/apm-dashboard/index',     'C', '0', '0', 'TrendCharts', 1, 0, 'admin', NOW());

-- 4. Fix path mismatches: V53 template-market and V54 app-source
-- 944 模板市场: was M with path=template-market, fix to C with correct path
UPDATE sys_menu SET menu_type = 'C', parent_id = @lcParentId, path = 'lowcode/template-market',
                    component = 'lowcode/template-market/index', order_num = 15
  WHERE id = 944 AND deleted = 0;
-- Delete non-existent child menus of 944 (模板查询, 模板创建)
UPDATE sys_menu SET deleted = 1 WHERE parent_id = 944 AND menu_type = 'C' AND deleted = 0;

-- 951 应用源码: was M with path=app-source, fix to C with correct path
UPDATE sys_menu SET menu_name = '应用源码导出', menu_type = 'C', parent_id = @lcParentId, path = 'lowcode/app-source-export',
                    component = 'lowcode/app-source-export/index', order_num = 16
  WHERE id = 951 AND deleted = 0;
-- Delete non-existent child menus of 951
UPDATE sys_menu SET deleted = 1 WHERE parent_id = 951 AND menu_type = 'C' AND deleted = 0;

-- 5. Hide orphan menus that have no corresponding frontend route
-- 939 DDL 执行日志: route lowcode/ddl-log does not exist
UPDATE sys_menu SET visible = '1' WHERE id = 939 AND deleted = 0;
-- 940 组件市场: route component-market does not exist
UPDATE sys_menu SET visible = '1' WHERE id = 940 AND deleted = 0;
-- 955 配置审计: route config-audit does not exist
UPDATE sys_menu SET visible = '1', deleted = 1 WHERE id = 955 AND deleted = 0;
UPDATE sys_menu SET deleted = 1 WHERE parent_id = 955 AND deleted = 0;
-- 957 数据导入导出: route data-io does not exist
UPDATE sys_menu SET visible = '1', deleted = 1 WHERE id = 957 AND deleted = 0;
UPDATE sys_menu SET deleted = 1 WHERE parent_id = 957 AND deleted = 0;

-- 6. Fix demo menu paths (V58): point to render route with correct pageType/pageCode
-- 962 员工管理: was path=employees, component=lowcode/render/index
UPDATE sys_menu SET path = 'lowcode/list/list_demo_employee', component = 'lowcode/render/index'
  WHERE id = 962 AND deleted = 0;
-- 963 入职任务: no list config exists, hide it
UPDATE sys_menu SET visible = '1' WHERE id = 963 AND deleted = 0;
-- 964 部门管理: no list config exists, hide it
UPDATE sys_menu SET visible = '1' WHERE id = 964 AND deleted = 0;

-- 7. Re-parent all lowcode F permissions (parent_id=0) to the 低代码管理 parent
-- These were registered by V60 with parent_id=0 (top-level), which is wrong for F type
UPDATE sys_menu SET parent_id = @lcParentId
  WHERE menu_type = 'F' AND perms LIKE 'lowcode:%' AND parent_id = 0 AND deleted = 0;

-- 8. Bind all lowcode menus and permissions to super admin role (role_id=1)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT 1, id, 'admin', NOW() FROM sys_menu
  WHERE (perms LIKE 'lowcode:%' OR parent_id = @lcParentId OR id = @lcParentId)
    AND deleted = 0;
