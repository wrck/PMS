-- =====================================================================
-- V58__demo_employee_onboarding.sql
-- 低代码平台演示模块：员工入职管理系统 (Employee Onboarding System)
-- bizType = 'EMP_ONBOARDING'
--
-- 设计目标：
--   通过平台自身的配置能力（元数据 INSERT）实现一个完整业务场景，
--   覆盖 12 项核心能力中的 9 项：数据建模 / 表单 / 列表 / 微流 /
--   规则 / 连接器 / 触发器 / 流程编排（绑定） / 权限管理。
--   不编写任何自定义 Java/Vue 代码。
--
-- 业务流程：
--   HR 创建员工档案 → CRUD 触发器（AFTER CREATE）→ 微流执行：
--     1) 调用决策表规则（按职级判定试用期月数 P1=1/P2-P3=3/P4-P7=6）
--     2) 赋值 probation_end_date = entry_date + probation_months
--     3) 调用 HR 同步连接器（REST POST + API Key + 熔断 + 限流）
--     4) 微流结束
--   后续：员工列表查询、入职任务跟踪、流程审批
--
-- 配置清单（共 9 类 12 项）：
--   实体 ×3 (demo_department / demo_employee / demo_onboarding_task)
--   字段 ×27
--   关联 ×3
--   表单 ×1 (form_demo_employee)
--   列表 ×1 (list_demo_employee)
--   微流 ×1 (microflow_demo_onboarding)
--   规则 ×1 (rule_demo_probation_decision)
--   连接器 ×1 (connector_demo_hr_sync)
--   触发器 ×1 (trigger_demo_employee_after_create)
--   权限菜单 ×4
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0. 业务数据表 DDL（兜底）
--    严格来说，业务表 DDL 应由平台 DdlGenerator 根据 pms_lowcode_entity
--    配置自动生成。此处提供 CREATE TABLE IF NOT EXISTS 作为演示可立即
--    运行的兜底，与平台生成的 DDL 等价。
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `demo_department` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dept_code`   VARCHAR(64)  NOT NULL COMMENT '部门编码',
    `dept_name`   VARCHAR(128) NOT NULL COMMENT '部门名称',
    `parent_id`   BIGINT       DEFAULT NULL COMMENT '上级部门ID',
    `manager_id`  BIGINT       DEFAULT NULL COMMENT '负责人ID',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_demo_dept_code` (`dept_code`),
    KEY `idx_demo_dept_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演示-部门';

CREATE TABLE IF NOT EXISTS `demo_employee` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `emp_no`              VARCHAR(32)  NOT NULL COMMENT '工号',
    `name`                VARCHAR(64)  NOT NULL COMMENT '姓名',
    `gender`              VARCHAR(8)   DEFAULT NULL COMMENT '性别',
    `phone`               VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
    `email`               VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `dept_id`             BIGINT       DEFAULT NULL COMMENT '部门ID',
    `position`            VARCHAR(64)  DEFAULT NULL COMMENT '职位',
    `level`               VARCHAR(8)   DEFAULT NULL COMMENT '职级 P1-P7',
    `entry_date`          DATE         DEFAULT NULL COMMENT '入职日期',
    `status`              VARCHAR(16)  NOT NULL DEFAULT 'ONBOARDING' COMMENT 'ONBOARDING/PROBATION/REGULAR/LEFT',
    `probation_end_date`  DATE         DEFAULT NULL COMMENT '试用期结束日期',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_demo_emp_no` (`emp_no`),
    KEY `idx_demo_emp_dept` (`dept_id`),
    KEY `idx_demo_emp_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演示-员工';

CREATE TABLE IF NOT EXISTS `demo_onboarding_task` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `emp_id`       BIGINT       NOT NULL COMMENT '员工ID',
    `task_type`    VARCHAR(32)  DEFAULT NULL COMMENT '任务类型: EQUIPMENT/TRAINING/ORIENTATION',
    `task_name`    VARCHAR(128) NOT NULL COMMENT '任务名称',
    `assignee`     VARCHAR(64)  DEFAULT NULL COMMENT '负责人',
    `status`       VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/DONE',
    `due_date`     DATE         DEFAULT NULL COMMENT '截止日期',
    `completed_at` DATETIME     DEFAULT NULL COMMENT '完成时间',
    `remark`       VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_demo_task_emp` (`emp_id`),
    KEY `idx_demo_task_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演示-入职任务';

-- 演示初始部门数据
INSERT IGNORE INTO `demo_department` (`id`, `dept_code`, `dept_name`, `parent_id`, `manager_id`, `description`) VALUES
(1001, 'HR',      '人力资源部',   NULL, NULL, '负责人事与招聘'),
(1002, 'ENG',     '工程研发部',   NULL, NULL, '负责产品研发'),
(1003, 'SALES',   '销售部',       NULL, NULL, '负责市场与销售'),
(1004, 'FE',      '前端组',       1002, NULL, '工程研发部子部门'),
(1005, 'BE',      '后端组',       1002, NULL, '工程研发部子部门');

-- ---------------------------------------------------------------------
-- 1. 实体定义（3 个）
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_entity`
    (`code`, `name`, `table_name`, `description`, `biz_type`, `status`, `version`, `create_time`, `update_time`, `create_by`, `deleted`)
VALUES
('demo_department',      '部门',   'demo_department',      '演示-部门实体',         'EMP_ONBOARDING', 'DRAFT', 1, NOW(), NOW(), 'demo', 0),
('demo_employee',        '员工',   'demo_employee',        '演示-员工实体',         'EMP_ONBOARDING', 'DRAFT', 1, NOW(), NOW(), 'demo', 0),
('demo_onboarding_task', '入职任务','demo_onboarding_task','演示-入职任务实体',     'EMP_ONBOARDING', 'DRAFT', 1, NOW(), NOW(), 'demo', 0);

-- 实体 ID 变量（供字段/关联引用）
SET @deptEntityId      = (SELECT id FROM pms_lowcode_entity WHERE code = 'demo_department'      ORDER BY id DESC LIMIT 1);
SET @empEntityId       = (SELECT id FROM pms_lowcode_entity WHERE code = 'demo_employee'        ORDER BY id DESC LIMIT 1);
SET @taskEntityId      = (SELECT id FROM pms_lowcode_entity WHERE code = 'demo_onboarding_task' ORDER BY id DESC LIMIT 1);

-- ---------------------------------------------------------------------
-- 2. 实体字段（27 个）
--    primary_key=1 仅 id 字段；indexed=1 用于查询频繁字段；unique_flag=1 用于编码字段
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_field`
    (`entity_id`, `name`, `label`, `field_type`, `length`, `scale`, `nullable`, `primary_key`, `indexed`, `unique_flag`, `default_value`, `sort_order`, `create_time`, `update_time`, `deleted`)
VALUES
-- demo_department 字段
(@deptEntityId, 'id',          '主键',     'LONG',    NULL, NULL, 0, 1, 1, 0, NULL, 1,  NOW(), NOW(), 0),
(@deptEntityId, 'dept_code',   '部门编码', 'STRING',  64,  NULL, 0, 0, 1, 1, NULL, 2,  NOW(), NOW(), 0),
(@deptEntityId, 'dept_name',   '部门名称', 'STRING',  128, NULL, 0, 0, 0, 0, NULL, 3,  NOW(), NOW(), 0),
(@deptEntityId, 'parent_id',   '上级部门', 'LONG',    NULL, NULL, 1, 0, 1, 0, NULL, 4,  NOW(), NOW(), 0),
(@deptEntityId, 'manager_id',  '负责人',   'LONG',    NULL, NULL, 1, 0, 0, 0, NULL, 5,  NOW(), NOW(), 0),
(@deptEntityId, 'description', '描述',     'STRING',  512, NULL, 1, 0, 0, 0, NULL, 6,  NOW(), NOW(), 0),
-- demo_employee 字段
(@empEntityId,  'id',                  '主键',         'LONG',    NULL, NULL, 0, 1, 1, 0, NULL,                  1, NOW(), NOW(), 0),
(@empEntityId,  'emp_no',              '工号',         'STRING',  32,   NULL, 0, 0, 1, 1, NULL,                  2, NOW(), NOW(), 0),
(@empEntityId,  'name',                '姓名',         'STRING',  64,   NULL, 0, 0, 0, 0, NULL,                  3, NOW(), NOW(), 0),
(@empEntityId,  'gender',              '性别',         'STRING',  8,    NULL, 1, 0, 0, 0, NULL,                  4, NOW(), NOW(), 0),
(@empEntityId,  'phone',               '手机号',       'STRING',  32,   NULL, 1, 0, 0, 0, NULL,                  5, NOW(), NOW(), 0),
(@empEntityId,  'email',               '邮箱',         'STRING',  128,  NULL, 1, 0, 0, 0, NULL,                  6, NOW(), NOW(), 0),
(@empEntityId,  'dept_id',             '部门',         'LONG',    NULL, NULL, 1, 0, 1, 0, NULL,                  7, NOW(), NOW(), 0),
(@empEntityId,  'position',            '职位',         'STRING',  64,   NULL, 1, 0, 0, 0, NULL,                  8, NOW(), NOW(), 0),
(@empEntityId,  'level',               '职级',         'STRING',  8,    NULL, 1, 0, 0, 0, NULL,                  9, NOW(), NOW(), 0),
(@empEntityId,  'entry_date',          '入职日期',     'DATE',    NULL, NULL, 1, 0, 0, 0, NULL,                 10, NOW(), NOW(), 0),
(@empEntityId,  'status',              '状态',         'STRING',  16,   NULL, 0, 0, 1, 0, 'ONBOARDING',         11, NOW(), NOW(), 0),
(@empEntityId,  'probation_end_date',  '试用期结束',   'DATE',    NULL, NULL, 1, 0, 0, 0, NULL,                 12, NOW(), NOW(), 0),
-- demo_onboarding_task 字段
(@taskEntityId, 'id',           '主键',     'LONG',    NULL, NULL, 0, 1, 1, 0, NULL,         1, NOW(), NOW(), 0),
(@taskEntityId, 'emp_id',       '员工',     'LONG',    NULL, NULL, 0, 0, 1, 0, NULL,         2, NOW(), NOW(), 0),
(@taskEntityId, 'task_type',    '任务类型', 'STRING',  32,   NULL, 1, 0, 0, 0, NULL,         3, NOW(), NOW(), 0),
(@taskEntityId, 'task_name',    '任务名称', 'STRING',  128,  NULL, 0, 0, 0, 0, NULL,         4, NOW(), NOW(), 0),
(@taskEntityId, 'assignee',     '负责人',   'STRING',  64,   NULL, 1, 0, 0, 0, NULL,         5, NOW(), NOW(), 0),
(@taskEntityId, 'status',       '状态',     'STRING',  16,   NULL, 0, 0, 1, 0, 'PENDING',    6, NOW(), NOW(), 0),
(@taskEntityId, 'due_date',     '截止日期', 'DATE',    NULL, NULL, 1, 0, 0, 0, NULL,         7, NOW(), NOW(), 0),
(@taskEntityId, 'completed_at', '完成时间', 'DATETIME',NULL, NULL, 1, 0, 0, 0, NULL,         8, NOW(), NOW(), 0),
(@taskEntityId, 'remark',       '备注',     'STRING',  512,  NULL, 1, 0, 0, 0, NULL,         9, NOW(), NOW(), 0);

-- ---------------------------------------------------------------------
-- 3. 实体关联关系（3 个）
--    员工 N:1 部门 / 入职任务 N:1 员工 / 部门自关联 N:1 父部门
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_relation`
    (`from_entity_id`, `to_entity_id`, `relation_type`, `from_field_name`, `to_field_name`, `reverse_name`, `junction_table`, `on_delete`, `on_update`, `create_time`, `update_time`, `deleted`)
VALUES
(@empEntityId,  @deptEntityId, 'MANY_TO_ONE', 'dept_id',    NULL, 'employees',    NULL, 'SET_NULL', 'CASCADE', NOW(), NOW(), 0),
(@taskEntityId, @empEntityId,  'MANY_TO_ONE', 'emp_id',     NULL, 'tasks',        NULL, 'CASCADE',  'CASCADE', NOW(), NOW(), 0),
(@deptEntityId, @deptEntityId, 'MANY_TO_ONE', 'parent_id',  NULL, 'subDepartments', NULL, 'SET_NULL', 'CASCADE', NOW(), NOW(), 0);

-- ---------------------------------------------------------------------
-- 4. 表单配置（1 个）— 员工档案表单
--    覆盖能力：表单设计、属性面板 schema、组件库应用（DictSelect）、字段校验
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_form`
    (`code`, `name`, `description`, `form_config`, `events`, `version`, `status`, `biz_type`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES (
'form_demo_employee',
'员工档案表单',
'演示-员工入职档案表单（含字段校验、字典下拉、部门选择）',
JSON_OBJECT(
    'title', '员工档案',
    'description', '新员工入职档案信息采集',
    'labelWidth', 110,
    'labelPosition', 'right',
    'size', 'default',
    'fields', JSON_ARRAY(
        JSON_OBJECT('id', 'f_emp_no', 'type', 'input', 'label', '工号', 'prop', 'emp_no',
                    'required', true, 'span', 12, 'placeholder', '请输入工号',
                    'props', JSON_OBJECT('maxlength', 32, 'showWordLimit', true)),
        JSON_OBJECT('id', 'f_name', 'type', 'input', 'label', '姓名', 'prop', 'name',
                    'required', true, 'span', 12, 'placeholder', '请输入姓名',
                    'props', JSON_OBJECT('maxlength', 64)),
        JSON_OBJECT('id', 'f_gender', 'type', 'select', 'label', '性别', 'prop', 'gender',
                    'required', false, 'span', 12, 'placeholder', '请选择性别',
                    'props', JSON_OBJECT('options', JSON_ARRAY(
                        JSON_OBJECT('label', '男', 'value', 'M'),
                        JSON_OBJECT('label', '女', 'value', 'F')
                    ), 'clearable', true)),
        JSON_OBJECT('id', 'f_phone', 'type', 'input', 'label', '手机号', 'prop', 'phone',
                    'required', true, 'span', 12, 'placeholder', '请输入手机号',
                    'rules', JSON_ARRAY(JSON_OBJECT('pattern', '^1[3-9]\\d{9}$', 'message', '请输入正确的手机号', 'trigger', 'blur'))),
        JSON_OBJECT('id', 'f_email', 'type', 'input', 'label', '邮箱', 'prop', 'email',
                    'required', false, 'span', 12, 'placeholder', '请输入邮箱',
                    'rules', JSON_ARRAY(JSON_OBJECT('pattern', '^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$', 'message', '邮箱格式不正确', 'trigger', 'blur'))),
        JSON_OBJECT('id', 'f_dept_id', 'type', 'select', 'label', '部门', 'prop', 'dept_id',
                    'required', true, 'span', 12, 'placeholder', '请选择部门',
                    'props', JSON_OBJECT('options', JSON_ARRAY(
                        JSON_OBJECT('label', '人力资源部', 'value', 1001),
                        JSON_OBJECT('label', '工程研发部', 'value', 1002),
                        JSON_OBJECT('label', '销售部',     'value', 1003),
                        JSON_OBJECT('label', '前端组',     'value', 1004),
                        JSON_OBJECT('label', '后端组',     'value', 1005)
                    ), 'filterable', true, 'clearable', true)),
        JSON_OBJECT('id', 'f_position', 'type', 'input', 'label', '职位', 'prop', 'position',
                    'required', false, 'span', 12, 'placeholder', '请输入职位',
                    'props', JSON_OBJECT('maxlength', 64)),
        JSON_OBJECT('id', 'f_level', 'type', 'select', 'label', '职级', 'prop', 'level',
                    'required', true, 'span', 12, 'placeholder', '请选择职级',
                    'props', JSON_OBJECT('options', JSON_ARRAY(
                        JSON_OBJECT('label', 'P1 实习',   'value', 'P1'),
                        JSON_OBJECT('label', 'P2 初级',   'value', 'P2'),
                        JSON_OBJECT('label', 'P3 中级',   'value', 'P3'),
                        JSON_OBJECT('label', 'P4 高级',   'value', 'P4'),
                        JSON_OBJECT('label', 'P5 资深',   'value', 'P5'),
                        JSON_OBJECT('label', 'P6 专家',   'value', 'P6'),
                        JSON_OBJECT('label', 'P7 总监',   'value', 'P7')
                    ), 'filterable', true),
                    'events', JSON_OBJECT('change', 'onLevelChange')),
        JSON_OBJECT('id', 'f_entry_date', 'type', 'date', 'label', '入职日期', 'prop', 'entry_date',
                    'required', true, 'span', 12,
                    'props', JSON_OBJECT('format', 'YYYY-MM-DD', 'valueFormat', 'YYYY-MM-DD')),
        JSON_OBJECT('id', 'f_status', 'type', 'select', 'label', '状态', 'prop', 'status',
                    'required', true, 'span', 12, 'defaultValue', 'ONBOARDING',
                    'props', JSON_OBJECT('options', JSON_ARRAY(
                        JSON_OBJECT('label', '入职中',   'value', 'ONBOARDING'),
                        JSON_OBJECT('label', '试用期',   'value', 'PROBATION'),
                        JSON_OBJECT('label', '已转正',   'value', 'REGULAR'),
                        JSON_OBJECT('label', '已离职',   'value', 'LEFT')
                    ), 'disabled', true)),
        JSON_OBJECT('id', 'f_probation_end', 'type', 'date', 'label', '试用期结束', 'prop', 'probation_end_date',
                    'required', false, 'span', 12, 'disabled', true,
                    'props', JSON_OBJECT('format', 'YYYY-MM-DD', 'valueFormat', 'YYYY-MM-DD'))
    ),
    'layout', JSON_OBJECT('type', 'grid', 'gutter', 16)
),
JSON_OBJECT(
    'onChange', JSON_OBJECT('type', 'MICROFLOW', 'code', 'microflow_demo_onboarding'),
    'onSubmit', JSON_OBJECT('type', 'PROCESS',   'code', 'demo_onboarding_approval')
),
1, 'DRAFT', 'EMP_ONBOARDING', 'demo', NOW(), 'demo', NOW(), 0
);

-- ---------------------------------------------------------------------
-- 5. 列表配置（1 个）— 员工列表
--    覆盖能力：列表设计、筛选条件、分页、操作列、响应式断点
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_list`
    (`code`, `name`, `description`, `list_config`, `version`, `status`, `biz_type`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES (
'list_demo_employee',
'员工列表',
'演示-员工列表（含搜索、筛选、分页、操作）',
JSON_OBJECT(
    'title', '员工列表',
    'entityCode', 'demo_employee',
    'columns', JSON_ARRAY(
        JSON_OBJECT('prop', 'emp_no',     'label', '工号',   'width', 120, 'sortable', true,  'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'name',       'label', '姓名',   'width', 120, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'gender',     'label', '性别',   'width', 80,  'breakpoint', 'md', 'formatter', 'genderText'),
        JSON_OBJECT('prop', 'dept_id',    'label', '部门',   'width', 140, 'breakpoint', 'md', 'formatter', 'deptName'),
        JSON_OBJECT('prop', 'position',   'label', '职位',   'width', 120, 'breakpoint', 'lg'),
        JSON_OBJECT('prop', 'level',      'label', '职级',   'width', 80,  'breakpoint', 'lg'),
        JSON_OBJECT('prop', 'entry_date', 'label', '入职日期','width', 120, 'breakpoint', 'xl'),
        JSON_OBJECT('prop', 'status',     'label', '状态',   'width', 100, 'breakpoint', 'md', 'tag', true, 'tagTypes', JSON_OBJECT('ONBOARDING','warning','PROBATION','info','REGULAR','success','LEFT','danger'))
    ),
    'filters', JSON_ARRAY(
        JSON_OBJECT('prop', 'emp_no',  'label', '工号', 'type', 'input',  'span', 6, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'name',    'label', '姓名', 'type', 'input',  'span', 6, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'dept_id', 'label', '部门', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label', '人力资源部', 'value', 1001),
                        JSON_OBJECT('label', '工程研发部', 'value', 1002),
                        JSON_OBJECT('label', '销售部',     'value', 1003)
                    )),
        JSON_OBJECT('prop', 'status',  'label', '状态', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label', '入职中', 'value', 'ONBOARDING'),
                        JSON_OBJECT('label', '试用期', 'value', 'PROBATION'),
                        JSON_OBJECT('label', '已转正', 'value', 'REGULAR'),
                        JSON_OBJECT('label', '已离职', 'value', 'LEFT')
                    ))
    ),
    'operations', JSON_ARRAY(
        JSON_OBJECT('type', 'view',   'label', '查看', 'icon', 'View'),
        JSON_OBJECT('type', 'edit',   'label', '编辑', 'icon', 'Edit',   'permission', 'lowcode:demo:employee:edit'),
        JSON_OBJECT('type', 'delete', 'label', '删除', 'icon', 'Delete', 'permission', 'lowcode:demo:employee:delete', 'confirm', '确认删除该员工？')
    ),
    'pagination', JSON_OBJECT('pageSize', 10, 'pageSizes', JSON_ARRAY(10, 20, 50, 100), 'layout', 'total, sizes, prev, pager, next, jumper'),
    'searchApi', '/api/lowcode/dynamic/demo_employee/list'
),
1, 'DRAFT', 'EMP_ONBOARDING', 'demo', NOW(), 'demo', NOW(), 0
);

-- ---------------------------------------------------------------------
-- 6. 微流定义（1 个）— 入职处理微流
--    覆盖能力：微流引擎、6 执行器（CALL_RULE + ASSIGN + CALL_CONNECTOR）、执行轨迹
--    DAG: START → CALL_RULE → ASSIGN → CALL_CONNECTOR → END
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_microflow`
    (`code`, `name`, `description`, `definition`, `status`, `version`, `biz_type`, `create_by`, `update_by`, `create_time`, `update_time`, `deleted`)
VALUES (
'microflow_demo_onboarding',
'入职处理微流',
'演示-员工创建后触发：判定试用期月数 → 计算试用期结束日期 → 调用 HR 系统同步',
JSON_OBJECT(
    'nodes', JSON_ARRAY(
        JSON_OBJECT('id', 'n_start',  'type', 'START',
                    'config', JSON_OBJECT('description', '入职流程开始')),
        JSON_OBJECT('id', 'n_rule',   'type', 'CALL_RULE',
                    'config', JSON_OBJECT('ruleCode', 'rule_demo_probation_decision', 'inputs', JSON_OBJECT('level', '${level}')),
                    'description', '按职级判定试用期月数'),
        JSON_OBJECT('id', 'n_assign', 'type', 'ASSIGN',
                    'config', JSON_OBJECT(
                        'expression', 'import java.time.LocalDate; LocalDate.of(${entry_date}.year, ${entry_date}.monthValue, ${entry_date}.dayOfMonth).plusMonths(${probation_months})',
                        'target', 'probation_end_date'
                    ),
                    'description', '计算试用期结束日期'),
        JSON_OBJECT('id', 'n_conn',   'type', 'CALL_CONNECTOR',
                    'config', JSON_OBJECT('connectorCode', 'connector_demo_hr_sync',
                                          'inputs', JSON_OBJECT('emp_no', '${emp_no}', 'name', '${name}', 'dept_id', '${dept_id}', 'entry_date', '${entry_date}')),
                    'description', '同步员工信息到 HR 系统'),
        JSON_OBJECT('id', 'n_end',    'type', 'END',
                    'config', JSON_OBJECT('description', '入职流程结束'))
    ),
    'edges', JSON_ARRAY(
        JSON_OBJECT('source', 'n_start',  'target', 'n_rule'),
        JSON_OBJECT('source', 'n_rule',   'target', 'n_assign'),
        JSON_OBJECT('source', 'n_assign', 'target', 'n_conn'),
        JSON_OBJECT('source', 'n_conn',   'target', 'n_end')
    )
),
'DRAFT', 1, 'EMP_ONBOARDING', 'demo', 'demo', NOW(), NOW(), 0
);

-- ---------------------------------------------------------------------
-- 7. 规则定义（1 个）— 试用期月数决策表
--    覆盖能力：规则引擎、决策表、Hit Policy=FIRST
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_rule`
    (`code`, `name`, `description`, `type`, `definition`, `status`, `version`, `biz_type`, `create_by`, `update_by`, `create_time`, `update_time`, `deleted`)
VALUES (
'rule_demo_probation_decision',
'试用期月数决策表',
'演示-按职级判定试用期月数：P1=1个月 / P2-P3=3个月 / P4-P7=6个月',
'DECISION_TABLE',
JSON_OBJECT(
    'hitPolicy', 'FIRST',
    'conditionColumns', JSON_ARRAY(
        JSON_OBJECT('field', 'level', 'operator', 'EQ', 'label', '职级')
    ),
    'actionColumns', JSON_ARRAY(
        JSON_OBJECT('field', 'probation_months', 'label', '试用期月数')
    ),
    'rows', JSON_ARRAY(
        JSON_OBJECT('conditions', JSON_ARRAY('P1'), 'actions', JSON_ARRAY(1)),
        JSON_OBJECT('conditions', JSON_ARRAY('P2'), 'actions', JSON_ARRAY(3)),
        JSON_OBJECT('conditions', JSON_ARRAY('P3'), 'actions', JSON_ARRAY(3)),
        JSON_OBJECT('conditions', JSON_ARRAY('P4'), 'actions', JSON_ARRAY(6)),
        JSON_OBJECT('conditions', JSON_ARRAY('P5'), 'actions', JSON_ARRAY(6)),
        JSON_OBJECT('conditions', JSON_ARRAY('P6'), 'actions', JSON_ARRAY(6)),
        JSON_OBJECT('conditions', JSON_ARRAY('P7'), 'actions', JSON_ARRAY(6))
    )
),
'DRAFT', 1, 'EMP_ONBOARDING', 'demo', 'demo', NOW(), NOW(), 0
);

-- ---------------------------------------------------------------------
-- 8. 连接器配置（1 个）— HR 系统同步 REST 连接器
--    覆盖能力：连接器、OAuth2/API_KEY 认证、熔断、限流、重试、响应映射
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_connector`
    (`code`, `name`, `description`, `type`, `config`, `status`, `version`, `biz_type`, `create_by`, `update_by`, `create_time`, `update_time`, `deleted`)
VALUES (
'connector_demo_hr_sync',
'HR系统同步连接器',
'演示-将员工信息同步到外部 HR 系统（REST POST + API Key + 熔断 + 限流）',
'REST',
JSON_OBJECT(
    'url', 'https://hr.example.com/api/employees/sync',
    'method', 'POST',
    'headers', JSON_OBJECT('Content-Type', 'application/json'),
    'auth', JSON_OBJECT('type', 'API_KEY', 'headerName', 'X-API-Key', 'apiKey', '${HR_API_KEY}'),
    'body', JSON_OBJECT(
        'emp_no', '${emp_no}',
        'name', '${name}',
        'dept_id', '${dept_id}',
        'entry_date', '${entry_date}'
    ),
    'retry', JSON_OBJECT('maxAttempts', 3, 'waitMillis', 500),
    'circuitBreaker', JSON_OBJECT('failureRateThreshold', 50, 'waitDurationMillis', 60000, 'slidingWindowSize', 100),
    'rateLimiter', JSON_OBJECT('limitForPeriod', 10, 'limitRefreshPeriodMillis', 1000),
    'timeoutMillis', 10000,
    'responseMapping', JSON_OBJECT('dataPath', '$.data', 'fieldMappings', JSON_ARRAY(
        JSON_OBJECT('from', 'sync_status', 'to', 'syncStatus'),
        JSON_OBJECT('from', 'hr_employee_id', 'to', 'hrEmployeeId')
    ))
),
'ACTIVE', 1, 'EMP_ONBOARDING', 'demo', 'demo', NOW(), NOW(), 0
);

-- ---------------------------------------------------------------------
-- 9. 触发器（1 个）— 员工创建后触发入职微流
--    覆盖能力：触发器、CRUD 钩子、AFTER CREATE
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `pms_lowcode_trigger`
    (`code`, `name`, `type`, `config`, `target_type`, `target_code`, `status`, `create_by`, `update_by`, `create_time`, `update_time`, `deleted`)
VALUES (
'trigger_demo_employee_after_create',
'员工创建后触发入职微流',
'CRUD',
JSON_OBJECT('entityCode', 'demo_employee', 'operations', JSON_ARRAY('CREATE'), 'timing', JSON_ARRAY('AFTER')),
'MICROFLOW',
'microflow_demo_onboarding',
'ACTIVE',
'demo', 'demo', NOW(), NOW(), 0
);

-- ---------------------------------------------------------------------
-- 10. 权限菜单 — 员工入职演示模块
-- ---------------------------------------------------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('员工入职演示', 0, 70, 'demo-onboarding', NULL, NULL, 'M', '0', '0', NOW());
SET @demoParentId = (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '员工入职演示' AND parent_id = 0 ORDER BY id DESC LIMIT 1) t);
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time) VALUES
('员工管理', @demoParentId, 1, 'employees', 'lowcode/render/index', 'lowcode:demo:employee:list',   'C', '0', '0', NOW()),
('入职任务', @demoParentId, 2, 'tasks',     'lowcode/render/index', 'lowcode:demo:task:list',       'C', '0', '0', NOW()),
('部门管理', @demoParentId, 3, 'departments','lowcode/render/index','lowcode:demo:department:list', 'C', '0', '0', NOW());

-- 操作按钮权限（F 类型）
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time) VALUES
('新增员工',     @demoParentId, 10, NULL, NULL, 'lowcode:demo:employee:edit',     'F', '0', '0', NOW()),
('删除员工',     @demoParentId, 11, NULL, NULL, 'lowcode:demo:employee:delete',   'F', '0', '0', NOW()),
('导出员工',     @demoParentId, 12, NULL, NULL, 'lowcode:demo:employee:export',   'F', '0', '0', NOW());

-- ---------------------------------------------------------------------
-- 完成提示
-- ---------------------------------------------------------------------
SELECT '演示模块 [员工入职管理系统 EMP_ONBOARDING] 已就绪' AS message,
       '12 项配置已注入：3 实体 + 27 字段 + 3 关联 + 1 表单 + 1 列表 + 1 微流 + 1 规则 + 1 连接器 + 1 触发器 + 6 权限菜单' AS detail;
