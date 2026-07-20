-- Complete the network-cutover demo with composable tab and related-page views.
-- The referenced form/list are published by V68, so these views can be used
-- directly from the runtime renderer after this migration is applied.

INSERT IGNORE INTO pms_lowcode_related_page
  (code, name, description, related_config, version, status, biz_type,
   create_by, create_time, update_by, update_time, deleted)
VALUES
  ('related_demo_network_cutover',
   '网络割接关联视图',
   '组合展示割接申请详情与割接台账，演示关联页的模块化页面编排能力',
   JSON_OBJECT(
     'title', '网络割接关联视图',
     'description', '申请详情与历史台账联动视图',
     'mainEntity', 'demo_network_cutover',
     'layout', 'grid',
     'gutter', 16,
     'sections', JSON_ARRAY(
       JSON_OBJECT(
         'id', 'cutover_detail',
         'title', '割接申请详情',
         'type', 'form',
         'pageCode', 'form_demo_network_cutover',
         'span', 24,
         'order', 100,
         'props', JSON_OBJECT()
       ),
       JSON_OBJECT(
         'id', 'cutover_ledger',
         'title', '割接历史台账',
         'type', 'list',
         'pageCode', 'list_demo_network_cutover',
         'span', 24,
         'order', 200,
         'props', JSON_OBJECT()
       )
     )
   ),
   1, 'PUBLISHED', 'NETWORK_CUTOVER',
   'demo', NOW(), 'demo', NOW(), 0);

INSERT IGNORE INTO pms_lowcode_tab
  (code, name, description, tab_config, version, status, biz_type,
   create_by, create_time, update_by, update_time, deleted)
VALUES
  ('tab_demo_network_cutover',
   '网络割接工作台',
   '通过标签页组合割接申请、割接台账和关联视图',
   JSON_OBJECT(
     'title', '网络割接工作台',
     'description', '网络割接全流程配置工作台',
     'type', 'border-card',
     'tabPosition', 'top',
     'closable', FALSE,
     'addable', FALSE,
     'editable', FALSE,
     'tabs', JSON_ARRAY(
       JSON_OBJECT(
         'id', 'cutover_application',
         'name', 'application',
         'title', '割接申请',
         'pageType', 'form',
         'pageCode', 'form_demo_network_cutover',
         'lazy', FALSE,
         'props', JSON_OBJECT()
       ),
       JSON_OBJECT(
         'id', 'cutover_ledger',
         'name', 'ledger',
         'title', '割接台账',
         'pageType', 'list',
         'pageCode', 'list_demo_network_cutover',
         'lazy', TRUE,
         'props', JSON_OBJECT()
       ),
       JSON_OBJECT(
         'id', 'cutover_related',
         'name', 'related',
         'title', '关联视图',
         'pageType', 'related-page',
         'pageCode', 'related_demo_network_cutover',
         'lazy', TRUE,
         'props', JSON_OBJECT()
       )
     )
   ),
   1, 'PUBLISHED', 'NETWORK_CUTOVER',
   'demo', NOW(), 'demo', NOW(), 0);

SET @cutover_menu_id = (
  SELECT id
  FROM sys_menu
  WHERE menu_name = '网络割接演示' AND deleted = 0
  ORDER BY id DESC
  LIMIT 1
);

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, menu_type, visible, status,
   perms, icon, create_by, create_time, update_by, update_time, deleted)
SELECT
  '网络割接工作台', @cutover_menu_id, 3,
  '/lowcode/tab/tab_demo_network_cutover', '', 'L', '1', '1',
  'lowcode:page:tab:tab_demo_network_cutover', 'Grid',
  'demo', NOW(), 'demo', NOW(), 0
WHERE @cutover_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE perms = 'lowcode:page:tab:tab_demo_network_cutover' AND deleted = 0
  );

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, menu_type, visible, status,
   perms, icon, create_by, create_time, update_by, update_time, deleted)
SELECT
  '网络割接关联视图', @cutover_menu_id, 4,
  '/lowcode/related-page/related_demo_network_cutover', '', 'L', '1', '1',
  'lowcode:page:related-page:related_demo_network_cutover', 'Share',
  'demo', NOW(), 'demo', NOW(), 0
WHERE @cutover_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE perms = 'lowcode:page:related-page:related_demo_network_cutover' AND deleted = 0
  );

SET @admin_role_id = (
  SELECT id FROM sys_role
  WHERE role_code = 'admin' AND deleted = 0
  LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id, create_by, create_time, update_by, update_time, deleted)
SELECT @admin_role_id, menu.id, 'demo', NOW(), 'demo', NOW(), 0
FROM sys_menu menu
WHERE @admin_role_id IS NOT NULL
  AND menu.perms IN (
    'lowcode:page:tab:tab_demo_network_cutover',
    'lowcode:page:related-page:related_demo_network_cutover'
  )
  AND menu.deleted = 0
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu role_menu
    WHERE role_menu.role_id = @admin_role_id
      AND role_menu.menu_id = menu.id
      AND role_menu.deleted = 0
  );
