-- V63__seed_leave_request_demo.sql
-- 请假流程演示案例：实体 + 表单 + 列表 + 规则 + 流程绑定 + 菜单

-- ============================================================
-- 1. 请假单实体
-- ============================================================
INSERT INTO pms_lowcode_entity (code, name, table_name, biz_type, status, version, description, create_by, update_by, deleted)
VALUES ('demo_leave_request', '请假单', 'demo_leave_request', 'LEAVE', 'PUBLISHED', 1,
        '演示-员工请假流程请假单实体', 'demo', 'demo', 0);

SET @entity_id = LAST_INSERT_ID();

-- 实体字段
INSERT INTO pms_lowcode_field (entity_id, name, label, field_type, length, scale, nullable, primary_key, indexed, unique_flag, default_value, sort_order, create_by, update_by, deleted)
VALUES
  (@entity_id, 'leave_no',    '请假编号', 'STRING',  64, NULL, 0, 0, 1, 1, NULL,       1, 'demo', 'demo', 0),
  (@entity_id, 'applicant',   '申请人',   'STRING',  64, NULL, 0, 0, 0, 0, NULL,       2, 'demo', 'demo', 0),
  (@entity_id, 'leave_type',  '请假类型', 'STRING',  32, NULL, 0, 0, 1, 0, 'PERSONAL', 3, 'demo', 'demo', 0),
  (@entity_id, 'start_date',  '开始日期', 'DATE',  NULL, NULL, 0, 0, 0, 0, NULL,       4, 'demo', 'demo', 0),
  (@entity_id, 'end_date',    '结束日期', 'DATE',  NULL, NULL, 0, 0, 0, 0, NULL,       5, 'demo', 'demo', 0),
  (@entity_id, 'days',        '请假天数', 'DECIMAL',   5,    1, 0, 0, 0, 0, NULL,       6, 'demo', 'demo', 0),
  (@entity_id, 'reason',      '请假事由', 'TEXT',   NULL, NULL, 1, 0, 0, 0, NULL,       7, 'demo', 'demo', 0),
  (@entity_id, 'status',      '状态',     'STRING',  32, NULL, 0, 0, 1, 0, 'DRAFT',    8, 'demo', 'demo', 0),
  (@entity_id, 'approver',    '审批人',   'STRING',  64, NULL, 1, 0, 0, 0, NULL,       9, 'demo', 'demo', 0),
  (@entity_id, 'approve_comment','审批意见','TEXT',  NULL, NULL, 1, 0, 0, 0, NULL,      10, 'demo', 'demo', 0);

-- 物理表 DDL
CREATE TABLE IF NOT EXISTS `demo_leave_request` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `leave_no` VARCHAR(64) NOT NULL COMMENT '请假编号',
  `applicant` VARCHAR(64) NOT NULL COMMENT '申请人',
  `leave_type` VARCHAR(32) NOT NULL DEFAULT 'PERSONAL' COMMENT '请假类型(SICK/PERSONAL/ANNUAL/MATERNITY)',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `days` DECIMAL(5,1) NOT NULL COMMENT '请假天数',
  `reason` TEXT COMMENT '请假事由',
  `status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT/PENDING/APPROVED/REJECTED)',
  `approver` VARCHAR(64) COMMENT '审批人',
  `approve_comment` TEXT COMMENT '审批意见',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_leave_no` (`leave_no`),
  KEY `idx_leave_type` (`leave_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假单';

-- 插入演示数据
INSERT INTO demo_leave_request (leave_no, applicant, leave_type, start_date, end_date, days, reason, status, approver, approve_comment, create_by, update_by)
VALUES
  ('LV20260701001', '张三', 'PERSONAL', '2026-07-10', '2026-07-11', 2.0, '家中有事需要处理', 'APPROVED', '李经理', '同意', 'demo', 'demo'),
  ('LV20260702001', '王五', 'SICK', '2026-07-15', '2026-07-16', 2.0, '身体不适需要休息', 'PENDING', '李经理', NULL, 'demo', 'demo'),
  ('LV20260703001', '赵六', 'ANNUAL', '2026-07-20', '2026-07-25', 5.0, '年假出游', 'DRAFT', NULL, NULL, 'demo', 'demo');

-- ============================================================
-- 2. 请假单表单
-- ============================================================
INSERT INTO pms_lowcode_form (code, name, description, form_config, events, status, version, biz_type, create_by, update_by, deleted)
VALUES ('form_demo_leave_request', '请假单表单', '演示-员工请假流程请假单表单',
 JSON_OBJECT(
   'title', '请假申请',
   'layout', 'vertical',
   'fields', JSON_ARRAY(
     JSON_OBJECT('prop','leave_no','label','请假编号','type','input','required',true,'placeholder','自动生成','disabled',true,'span',12),
     JSON_OBJECT('prop','applicant','label','申请人','type','input','required',true,'placeholder','请输入申请人','span',12),
     JSON_OBJECT('prop','leave_type','label','请假类型','type','select','required',true,'span',12,
       'options',JSON_ARRAY(
         JSON_OBJECT('label','事假','value','PERSONAL'),
         JSON_OBJECT('label','病假','value','SICK'),
         JSON_OBJECT('label','年假','value','ANNUAL'),
         JSON_OBJECT('label','产假','value','MATERNIAL')
       )),
     JSON_OBJECT('prop','start_date','label','开始日期','type','date','required',true,'span',12),
     JSON_OBJECT('prop','end_date','label','结束日期','type','date','required',true,'span',12),
     JSON_OBJECT('prop','days','label','请假天数','type','number','required',true,'span',12,'min',0.5,'step',0.5),
     JSON_OBJECT('prop','reason','label','请假事由','type','textarea','required',false,'span',24,'rows',3),
     JSON_OBJECT('prop','status','label','状态','type','select','required',false,'span',12,'hidden',true,
       'options',JSON_ARRAY(
         JSON_OBJECT('label','草稿','value','DRAFT'),
         JSON_OBJECT('label','待审批','value','PENDING'),
         JSON_OBJECT('label','已通过','value','APPROVED'),
         JSON_OBJECT('label','已驳回','value','REJECTED')
       )),
     JSON_OBJECT('prop','approver','label','审批人','type','input','required',false,'span',12,'hidden',true),
     JSON_OBJECT('prop','approve_comment','label','审批意见','type','textarea','required',false,'span',24,'hidden',true)
   )
 ),
 JSON_OBJECT('onSubmit', JSON_OBJECT('type','PROCESS','code','demo_leave_approval')),
 'PUBLISHED', 1, 'LEAVE', 'demo', 'demo', 0);

-- ============================================================
-- 3. 请假单列表
-- ============================================================
INSERT INTO pms_lowcode_list (code, name, description, list_config, status, version, biz_type, create_by, update_by, deleted)
VALUES ('list_demo_leave_request', '请假单列表', '演示-员工请假流程请假单列表',
 JSON_OBJECT(
   'entityCode', 'demo_leave_request',
   'searchApi', '/api/lowcode/data/demo_leave_request',
   'columns', JSON_ARRAY(
     JSON_OBJECT('prop','leave_no','label','请假编号','width',140,'sortable',true),
     JSON_OBJECT('prop','applicant','label','申请人','width',100),
     JSON_OBJECT('prop','leave_type','label','请假类型','width',100,
       'formatter',JSON_OBJECT('type','dict','items',JSON_ARRAY(
         JSON_OBJECT('label','事假','value','PERSONAL'),
         JSON_OBJECT('label','病假','value','SICK'),
         JSON_OBJECT('label','年假','value','ANNUAL'),
         JSON_OBJECT('label','产假','value','MATERNIAL')
       ))),
     JSON_OBJECT('prop','start_date','label','开始日期','width',120,'sortable',true),
     JSON_OBJECT('prop','end_date','label','结束日期','width',120,'sortable',true),
     JSON_OBJECT('prop','days','label','天数','width',80,'sortable',true),
     JSON_OBJECT('prop','status','label','状态','width',100,
       'formatter',JSON_OBJECT('type','dict','items',JSON_ARRAY(
         JSON_OBJECT('label','草稿','value','DRAFT','tagType','info'),
         JSON_OBJECT('label','待审批','value','PENDING','tagType','warning'),
         JSON_OBJECT('label','已通过','value','APPROVED','tagType','success'),
         JSON_OBJECT('label','已驳回','value','REJECTED','tagType','danger')
       ))),
     JSON_OBJECT('prop','approver','label','审批人','width',100)
   ),
   'filters', JSON_ARRAY(
     JSON_OBJECT('prop','leave_type','label','请假类型','type','select',
       'options',JSON_ARRAY(
         JSON_OBJECT('label','事假','value','PERSONAL'),
         JSON_OBJECT('label','病假','value','SICK'),
         JSON_OBJECT('label','年假','value','ANNUAL')
       )),
     JSON_OBJECT('prop','status','label','状态','type','select',
       'options',JSON_ARRAY(
         JSON_OBJECT('label','草稿','value','DRAFT'),
         JSON_OBJECT('label','待审批','value','PENDING'),
         JSON_OBJECT('label','已通过','value','APPROVED'),
         JSON_OBJECT('label','已驳回','value','REJECTED')
       ))
   ),
   'operations', JSON_ARRAY(
     JSON_OBJECT('label','查看','type','view','action','view'),
     JSON_OBJECT('label','编辑','type','primary','action','edit'),
     JSON_OBJECT('label','删除','type','danger','action','delete')
   ),
   'pagination', JSON_OBJECT('pageSize',20,'pageSizes',JSON_ARRAY(10,20,50)),
   'showSelection', false,
   'showIndex', true
 ),
 'PUBLISHED', 1, 'LEAVE', 'demo', 'demo', 0);

-- ============================================================
-- 4. 请假天数审批路由规则（决策表）
-- ============================================================
INSERT INTO pms_lowcode_rule (code, name, description, type, definition, status, version, biz_type, create_by, update_by, deleted)
VALUES ('rule_demo_leave_approver_router', '请假审批路由决策表', '演示-按请假天数决定审批层级',
 'DECISION_TABLE',
 JSON_OBJECT(
   'hitPolicy', 'FIRST',
   'conditionColumns', JSON_ARRAY(
     JSON_OBJECT('field', 'days', 'operator', 'LE', 'label', '请假天数≤3')
   ),
   'actionColumns', JSON_ARRAY(
     JSON_OBJECT('field', 'approver_level', 'label', '审批层级'),
     JSON_OBJECT('field', 'description', 'label', '说明')
   ),
   'rows', JSON_ARRAY(
     JSON_OBJECT('conditions', JSON_ARRAY(JSON_OBJECT('value', 3)), 'actions', JSON_ARRAY(JSON_OBJECT('value', 'DIRECT_MANAGER'), JSON_OBJECT('value', '直属主管审批'))),
     JSON_OBJECT('conditions', JSON_ARRAY(JSON_OBJECT('value', 5)), 'actions', JSON_ARRAY(JSON_OBJECT('value', 'DEPT_MANAGER'), JSON_OBJECT('value', '部门经理审批'))),
     JSON_OBJECT('conditions', JSON_ARRAY(JSON_OBJECT('value', 999)), 'actions', JSON_ARRAY(JSON_OBJECT('value', 'HR_DIRECTOR'), JSON_OBJECT('value', 'HR总监审批')))
   )
 ),
 'PUBLISHED', 1, 'LEAVE', 'demo', 'demo', 0);

-- ============================================================
-- 5. 流程绑定
-- ============================================================
INSERT INTO pms_lowcode_process_binding (process_definition_key, process_definition_name, node_form_bindings, task_callbacks, status, create_by, update_by, deleted)
VALUES ('demo_leave_approval', '请假审批流程',
 JSON_ARRAY(
   JSON_OBJECT('nodeId', 'apply', 'formCode', 'form_demo_leave_request'),
   JSON_OBJECT('nodeId', 'manager_review', 'formCode', 'form_demo_leave_request'),
   JSON_OBJECT('nodeId', 'hr_review', 'formCode', 'form_demo_leave_request')
 ),
 NULL,
 'ACTIVE', 'demo', 'demo', 0);

-- ============================================================
-- 6. 低代码页面菜单
-- ============================================================
-- 请假演示父菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, update_by, deleted)
SELECT '请假流程演示', parent_id, 60, 'leave-demo', NULL, 'M', '1', '1', '', 'Memo',
       'demo', 'demo', 0
FROM sys_menu WHERE menu_name = '低代码管理' AND deleted = 0 LIMIT 1;

SET @leave_menu_id = LAST_INSERT_ID();

-- 请假单列表页面菜单（L类型 - 低代码页面）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, update_by, deleted)
VALUES ('请假单管理', @leave_menu_id, 1, '/lowcode/list/list_demo_leave_request', '', 'L', '1', '1', 'lowcode:page:list:list_demo_leave_request', 'List',
        'demo', 'demo', 0);

-- 请假单表单页面菜单（L类型）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, update_by, deleted)
VALUES ('请假申请', @leave_menu_id, 2, '/lowcode/form/form_demo_leave_request', '', 'L', '1', '1', 'lowcode:page:form:form_demo_leave_request', 'EditPen',
        'demo', 'demo', 0);

-- 动态数据权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, update_by, deleted)
VALUES ('请假单数据-查看', @leave_menu_id, 10, '', '', 'F', '1', '1', 'lowcode:data:demo_leave_request:list', '',
        'demo', 'demo', 0),
       ('请假单数据-新增', @leave_menu_id, 11, '', '', 'F', '1', '1', 'lowcode:data:demo_leave_request:add', '',
        'demo', 'demo', 0),
       ('请假单数据-编辑', @leave_menu_id, 12, '', '', 'F', '1', '1', 'lowcode:data:demo_leave_request:edit', '',
        'demo', 'demo', 0),
       ('请假单数据-删除', @leave_menu_id, 13, '', '', 'F', '1', '1', 'lowcode:data:demo_leave_request:delete', '',
        'demo', 'demo', 0);

-- 将请假演示菜单绑定到超管角色
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'admin' AND deleted = 0 LIMIT 1);
INSERT INTO sys_role_menu (role_id, menu_id, create_by, update_by, deleted)
SELECT @admin_role_id, id, 'demo', 'demo', 0 FROM sys_menu WHERE parent_id = @leave_menu_id AND deleted = 0;
-- 同时绑定父菜单
INSERT INTO sys_role_menu (role_id, menu_id, create_by, update_by, deleted)
VALUES (@admin_role_id, @leave_menu_id, 'demo', 'demo', 0);
