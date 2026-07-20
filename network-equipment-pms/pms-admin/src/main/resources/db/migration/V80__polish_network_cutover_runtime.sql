-- Make every action in the network-cutover runtime demo directly usable.

UPDATE pms_lowcode_form
SET form_config = JSON_SET(
      form_config,
      '$.fields[11].props',
      JSON_OBJECT(
        'options', JSON_ARRAY(
          JSON_OBJECT('label', '草稿', 'value', 'DRAFT'),
          JSON_OBJECT('label', '待风险审核', 'value', 'PENDING_REVIEW'),
          JSON_OBJECT('label', '待割接确认', 'value', 'PENDING_CONFIRM'),
          JSON_OBJECT('label', '实施中', 'value', 'EXECUTING'),
          JSON_OBJECT('label', '业务验证', 'value', 'VERIFYING'),
          JSON_OBJECT('label', '已完成', 'value', 'COMPLETED'),
          JSON_OBJECT('label', '已回退', 'value', 'ROLLED_BACK'),
          JSON_OBJECT('label', '已驳回', 'value', 'REJECTED')
        )
      )
    ),
    update_by = 'demo',
    update_time = NOW()
WHERE code = 'form_demo_network_cutover' AND deleted = 0;

UPDATE pms_lowcode_list
SET list_config = JSON_SET(
      list_config,
      '$.filters[1].options',
      JSON_ARRAY(
        JSON_OBJECT('label', '草稿', 'value', 'DRAFT'),
        JSON_OBJECT('label', '待风险审核', 'value', 'PENDING_REVIEW'),
        JSON_OBJECT('label', '待割接确认', 'value', 'PENDING_CONFIRM'),
        JSON_OBJECT('label', '实施中', 'value', 'EXECUTING'),
        JSON_OBJECT('label', '业务验证', 'value', 'VERIFYING'),
        JSON_OBJECT('label', '已完成', 'value', 'COMPLETED'),
        JSON_OBJECT('label', '已回退', 'value', 'ROLLED_BACK'),
        JSON_OBJECT('label', '已驳回', 'value', 'REJECTED')
      ),
      '$.toolbar',
      JSON_ARRAY(
        JSON_OBJECT(
          'id', 'create_cutover',
          'label', '新建割接申请',
          'action', 'create',
          'type', 'primary'
        )
      )
    ),
    update_by = 'demo',
    update_time = NOW()
WHERE code = 'list_demo_network_cutover' AND deleted = 0;

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
  '割接数据-详情', @cutover_menu_id, 14, '', '', 'F', '1', '1',
  'lowcode:data:demo_network_cutover:query', '',
  'demo', NOW(), 'demo', NOW(), 0
WHERE @cutover_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE perms = 'lowcode:data:demo_network_cutover:query' AND deleted = 0
  );

SET @admin_role_id = (
  SELECT id FROM sys_role
  WHERE role_code = 'admin' AND deleted = 0
  LIMIT 1
);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time, update_by, update_time, deleted)
SELECT @admin_role_id, id, 'demo', NOW(), 'demo', NOW(), 0
FROM sys_menu
WHERE perms = 'lowcode:data:demo_network_cutover:query'
  AND deleted = 0
  AND @admin_role_id IS NOT NULL;
