-- =============================================================
-- V72__seed_demo_data_and_permissions.sql
-- Phase 8 / 8.1 — 演示数据 + 权限菜单 + 字典数据
--
-- 关联设计文档：§6.10（行 1649-1748）、§8.2 Phase 8 任务 8.1
-- 内容：
--   1. 权限菜单：项目管理相关 ~20 个权限码（project:template:*、project:phase:*、
--      project:task:*、project:baseline:*、project:deliverable:*、approval:center:*）
--      以及配套 sys_menu 菜单与超管绑定
--   2. 字典数据：项目状态、阶段状态、任务状态、交付件状态、审批状态、
--      模板状态、依赖类型、优先级、签核类型
--   3. 演示数据：1 个项目模板（已发布 v1.0.0）+ 1 个项目（从模板创建）
--      + 2 个阶段 + 3 个任务 + 2 个交付件 + 1 个交付件版本 + 1 个签名
--      + 1 条任务依赖 + 1 个基线快照 + 1 条审批 + 1 条审批历史 + 3 条字段权限
--
-- 兼容性：
--   - 旧业务表（pms_project/pms_impl_task/pms_deliverable 等 V2 表）使用
--     VARCHAR(64) create_by，故 create_by 取 'admin' 字符串。
--   - 新 Phase 1-7 表（V64~V71）使用 BIGINT create_by，取 admin 用户 ID = 1。
--   - 所有 INSERT 使用 INSERT IGNORE 或 NOT EXISTS 子查询保证可重复执行。
--   - 显式 ID 与 V61（pms_project 1~10）/ V24（sys_menu 1~100）保持不冲突：
--     pms_project 从 1001 起、pms_impl_task 从 8001 起、pms_deliverable 从 2001 起。
-- =============================================================

-- =============================================================
-- 1. 权限菜单
-- =============================================================

-- 1.1 注册权限码（sys_permission，~20 条）
INSERT IGNORE INTO `sys_permission` (`code`, `name`, `type`, `parent_id`, `sort`, `create_by`, `create_time`) VALUES
('project:template:list',     '项目模板-查询',   'menu',   0, 100, 'admin', NOW()),
('project:template:create',   '项目模板-新增',   'button', 0, 101, 'admin', NOW()),
('project:template:edit',     '项目模板-编辑',   'button', 0, 102, 'admin', NOW()),
('project:template:publish',  '项目模板-发布版本','button', 0, 103, 'admin', NOW()),
('project:template:delete',   '项目模板-删除',   'button', 0, 104, 'admin', NOW()),
('project:template:create-project','从模板创建项目','button', 0, 105, 'admin', NOW()),
('project:phase:list',        '项目阶段-查询',   'menu',   0, 110, 'admin', NOW()),
('project:phase:advance',     '项目阶段-推进',   'button', 0, 111, 'admin', NOW()),
('project:phase:rollback',    '项目阶段-回退',   'button', 0, 112, 'admin', NOW()),
('project:task:list',         '项目任务-查询',   'menu',   0, 120, 'admin', NOW()),
('project:task:create',       '项目任务-新增',   'button', 0, 121, 'admin', NOW()),
('project:task:edit',         '项目任务-编辑',   'button', 0, 122, 'admin', NOW()),
('project:task:delete',       '项目任务-删除',   'button', 0, 123, 'admin', NOW()),
('project:task:dependency',   '项目任务-依赖配置','button', 0, 124, 'admin', NOW()),
('project:task:submit-review','项目任务-提交评审','button', 0, 125, 'admin', NOW()),
('project:baseline:list',     '计划基线-查询',   'menu',   0, 130, 'admin', NOW()),
('project:baseline:create',   '计划基线-创建快照','button', 0, 131, 'admin', NOW()),
('project:baseline:change',   '计划基线-变更申请','button', 0, 132, 'admin', NOW()),
('project:deliverable:list',  '交付件-查询',     'menu',   0, 140, 'admin', NOW()),
('project:deliverable:revise','交付件-修订新版本','button', 0, 141, 'admin', NOW()),
('project:deliverable:publish','交付件-发布',    'button', 0, 142, 'admin', NOW()),
('approval:center:list',      '审批中心-查询',   'menu',   0, 150, 'admin', NOW()),
('approval:center:handle',    '审批中心-审批',   'button', 0, 151, 'admin', NOW()),
('approval:center:resubmit',  '审批中心-重新提交','button', 0, 152, 'admin', NOW()),
('approval:center:field-perm','审批中心-字段权限','button', 0, 153, 'admin', NOW());

-- 1.2 注册项目管理顶级菜单（若不存在）
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
SELECT '项目管理', 0, 'M', '/project', NULL, '', 'Folder', 20, '0', '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_name` = '项目管理' AND `parent_id` = 0 AND `deleted` = 0);

SET @pmParentId = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '项目管理' AND `parent_id` = 0 AND `deleted` = 0 ORDER BY `id` DESC LIMIT 1);

-- 1.3 注册项目管理子菜单（C 类型，对应前端 router/index.ts 的可见路由）
INSERT IGNORE INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`) VALUES
('项目列表',     @pmParentId, 'C', 'list',                 'project/list/index',         'project:task:list',         'Folder',     1, '0', '1', '0', 'admin', NOW()),
('主子项目树',   @pmParentId, 'C', 'tree',                 'project/tree/index',         '',                          'Share',      2, '0', '1', '0', 'admin', NOW()),
('交付看板',     @pmParentId, 'C', 'kanban',               'project/kanban/index',       '',                          'Grid',       3, '0', '1', '0', 'admin', NOW()),
('项目模板',     @pmParentId, 'C', 'template',             'project/template/index',     'project:template:list',     'Files',      4, '0', '1', '0', 'admin', NOW());

-- 1.4 注册计划基线 / 工作流菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
SELECT '计划基线', 0, 'M', '/baseline', NULL, '', 'Histogram', 30, '0', '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_name` = '计划基线' AND `parent_id` = 0 AND `deleted` = 0);

SET @blParentId = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '计划基线' AND `parent_id` = 0 AND `deleted` = 0 ORDER BY `id` DESC LIMIT 1);

INSERT IGNORE INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`) VALUES
('基线管理',     @blParentId, 'C', 'list', 'baseline/index', 'project:baseline:list', 'Histogram', 1, '0', '1', '0', 'admin', NOW());

-- 1.5 把项目管理相关菜单绑定到超管角色（role_id=1）
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`, `create_time`)
SELECT 1, m.`id`, 'admin', NOW()
FROM `sys_menu` m
WHERE m.`deleted` = 0
  AND (m.`path` IN ('list','tree','kanban','template') AND m.`parent_id` = @pmParentId
       OR m.`path` = 'list' AND m.`parent_id` = @blParentId
       OR m.`menu_name` IN ('项目管理','计划基线'));

-- =============================================================
-- 2. 字典数据
-- =============================================================

-- 2.1 字典类型（9 类）
INSERT IGNORE INTO `sys_dict` (`id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
(101, '项目状态',     'pms_project_status',     '0', 'admin', NOW()),
(102, '阶段状态',     'pms_phase_status',       '0', 'admin', NOW()),
(103, '任务状态',     'pms_task_status',        '0', 'admin', NOW()),
(104, '交付件状态',   'pms_deliverable_status', '0', 'admin', NOW()),
(105, '审批状态',     'pms_approval_status',    '0', 'admin', NOW()),
(106, '模板状态',     'pms_template_status',    '0', 'admin', NOW()),
(107, '依赖类型',     'pms_dependency_type',    '0', 'admin', NOW()),
(108, '任务优先级',   'pms_task_priority',      '0', 'admin', NOW()),
(109, '签核类型',     'pms_signature_type',     '0', 'admin', NOW());

-- 2.2 字典项（项目状态 8 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(101, '待启动',     'PENDING',              1, 'admin', NOW()),
(101, '已审批',     'APPROVED',             2, 'admin', NOW()),
(101, '执行中',     'IN_PROGRESS',          3, 'admin', NOW()),
(101, '初验中',     'INITIAL_ACCEPTANCE',   4, 'admin', NOW()),
(101, '终验中',     'FINAL_ACCEPTANCE',     5, 'admin', NOW()),
(101, '已完成',     'COMPLETED',            6, 'admin', NOW()),
(101, '已关闭',     'CLOSED',               7, 'admin', NOW()),
(101, '已驳回',     'REJECTED',             8, 'admin', NOW());

-- 2.3 阶段状态（4 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(102, '未开始',     'NOT_STARTED', 1, 'admin', NOW()),
(102, '进行中',     'IN_PROGRESS', 2, 'admin', NOW()),
(102, '已完成',     'COMPLETED',   3, 'admin', NOW()),
(102, '已跳过',     'SKIPPED',     4, 'admin', NOW());

-- 2.4 任务状态（6 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(103, '待接单',     'PENDING',     1, 'admin', NOW()),
(103, '已接单',     'ACCEPTED',    2, 'admin', NOW()),
(103, '进行中',     'IN_PROGRESS', 3, 'admin', NOW()),
(103, '已完成',     'COMPLETED',   4, 'admin', NOW()),
(103, '已确认',     'CONFIRMED',   5, 'admin', NOW()),
(103, '已驳回',     'REJECTED',    6, 'admin', NOW());

-- 2.5 交付件状态（7 态）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(104, '草稿',       'DRAFT',      1, 'admin', NOW()),
(104, '已提交',     'SUBMITTED',  2, 'admin', NOW()),
(104, '已评审',     'REVIEWED',   3, 'admin', NOW()),
(104, '已签核',     'SIGNED',     4, 'admin', NOW()),
(104, '已发布',     'PUBLISHED',  5, 'admin', NOW()),
(104, '已引用',     'REFERENCED', 6, 'admin', NOW()),
(104, '已归档',     'ARCHIVED',   7, 'admin', NOW());

-- 2.6 审批状态（5 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(105, '待审批',     'PENDING',   1, 'admin', NOW()),
(105, '已通过',     'APPROVED',  2, 'admin', NOW()),
(105, '已驳回',     'REJECTED',  3, 'admin', NOW()),
(105, '已撤回',     'WITHDRAWN', 4, 'admin', NOW()),
(105, '已超时',     'TIMEOUT',   5, 'admin', NOW());

-- 2.7 模板状态（3 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(106, '草稿',       'DRAFT',     1, 'admin', NOW()),
(106, '已发布',     'PUBLISHED', 2, 'admin', NOW()),
(106, '已弃用',     'DEPRECATED',3, 'admin', NOW());

-- 2.8 依赖类型（4 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(107, '完成-开始',  'FS', 1, 'admin', NOW()),
(107, '完成-完成',  'FF', 2, 'admin', NOW()),
(107, '开始-开始',  'SS', 3, 'admin', NOW()),
(107, '开始-完成',  'SF', 4, 'admin', NOW());

-- 2.9 任务优先级（4 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(108, '低',         'LOW',      1, 'admin', NOW()),
(108, '中',         'MEDIUM',   2, 'admin', NOW()),
(108, '高',         'HIGH',     3, 'admin', NOW()),
(108, '紧急',       'CRITICAL', 4, 'admin', NOW());

-- 2.10 签核类型（3 项）
INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(109, '电子签名',   'ELECTRONIC', 1, 'admin', NOW()),
(109, '印章',       'STAMP',      2, 'admin', NOW()),
(109, '数字签名',   'DIGITAL',    3, 'admin', NOW());

-- =============================================================
-- 3. 演示数据
-- =============================================================

-- 3.1 项目模板（1 个，已发布 v1.0.0）+ 模板版本快照
INSERT IGNORE INTO `pms_project_template`
    (`id`, `template_code`, `template_name`, `category`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1, 'TPL-IMPL-STD', '标准网络设备实施模板', 'IMPLEMENT', '5 阶段标准实施流程（含阶段退出条件 / 默认任务 / 默认交付件）', 'PUBLISHED', 1, NOW(), 1, NOW(), 0, 1);

INSERT IGNORE INTO `pms_project_template_version`
    (`id`, `template_id`, `version`, `snapshot_json`, `change_log`, `status`, `published_at`, `published_by`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version_lock`)
VALUES
(1, 1, 'v1.0.0',
    JSON_OBJECT(
        'phases', JSON_ARRAY(
            JSON_OBJECT('phaseCode','PREPARE','phaseName','准备阶段','sortOrder',1,
                'exitCriteria', JSON_OBJECT('requiredDeliverables', JSON_ARRAY(JSON_OBJECT('deliverableName','实施方案','requiredStatus','PUBLISHED')))),
            JSON_OBJECT('phaseCode','PLAN','phaseName','规划阶段','sortOrder',2,
                'exitCriteria', JSON_OBJECT('requiredDeliverables', JSON_ARRAY(JSON_OBJECT('deliverableName','规划报告','requiredStatus','PUBLISHED'))))
        ),
        'tasks', JSON_ARRAY(
            JSON_OBJECT('taskName','项目启动会','taskType','OEM','priority','HIGH'),
            JSON_OBJECT('taskName','需求确认','taskType','OEM','priority','HIGH'),
            JSON_OBJECT('taskName','现场勘查','taskType','AGENT','priority','MEDIUM')
        ),
        'deliverables', JSON_ARRAY(
            JSON_OBJECT('deliverableName','实施方案','deliverableType','DOCUMENT','mandatory',1,'approverRole','TECH_LEAD'),
            JSON_OBJECT('deliverableName','规划报告','deliverableType','REPORT','mandatory',1,'approverRole','PROJECT_MANAGER')
        ),
        'dependencies', JSON_ARRAY(
            JSON_OBJECT('predecessorTaskName','需求确认','successorTaskName','现场勘查','dependencyType','FS','lagDays',1)
        )
    ),
    '初始版本', 'PUBLISHED', NOW(), 1, 1, NOW(), 1, NOW(), 0, 0);

-- 3.2 项目（1 个，从模板创建；ID=1001 避免与 V61 的 1~10 冲突）
-- 注：pms_project.create_by 为 VARCHAR(64)，取 'admin'；其余 Phase 1-7 表为 BIGINT。
INSERT IGNORE INTO `pms_project`
    (`id`, `project_code`, `project_name`, `project_type`, `status`,
     `customer_name`, `customer_contact`, `customer_phone`, `contract_no`, `contract_amount`,
     `plan_start_date`, `plan_end_date`, `actual_start_date`, `actual_end_date`,
     `project_manager_id`, `project_manager_name`, `description`, `progress`, `priority`,
     `parent_project_id`, `project_path`, `depth`, `weight`,
     `template_id`, `template_version`, `current_phase_id`,
     `project_objective`, `project_scope`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1001, 'IMPL-2026-001', 'XX 省网络设备实施主项目（演示）', 'NETWORK_DEVICE', 'IN_PROGRESS',
     'XX 省电信', '客户接口人', '13900000001', 'HT-2026-001', 500000.00,
     '2026-07-01', '2026-12-31', '2026-07-01', NULL,
     1, 'Administrator', '从标准实施模板 TPL-IMPL-STD v1.0.0 创建的演示项目', 60, 'HIGH',
     NULL, '/1001/', 0, 1.00,
     1, 'v1.0.0', 5001,
     '完成全省网络设备升级', '全省 10 个地市分公司',
     'admin', NOW(), 'admin', NOW(), 0);

-- 3.3 项目阶段（2 个：准备阶段 COMPLETED + 规划阶段 IN_PROGRESS）
INSERT IGNORE INTO `pms_project_phase`
    (`id`, `project_id`, `template_phase_id`, `phase_name`, `phase_code`, `sort_order`,
     `entry_criteria`, `exit_criteria`, `status`,
     `planned_start_date`, `planned_end_date`, `actual_start_date`, `actual_end_date`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(5001, 1001, NULL, '准备阶段', 'PREPARE', 1, NULL,
    JSON_OBJECT('requiredDeliverables', JSON_ARRAY(JSON_OBJECT('deliverableName','实施方案','requiredStatus','PUBLISHED')),
                'requiredTasks', JSON_ARRAY(JSON_OBJECT('phaseId',5001,'allCompleted',true))),
    'COMPLETED', '2026-07-01', '2026-07-15', '2026-07-01', '2026-07-15',
    1, NOW(), 1, NOW(), 0, 0),
(5002, 1001, NULL, '规划阶段', 'PLAN', 2, NULL,
    JSON_OBJECT('requiredDeliverables', JSON_ARRAY(JSON_OBJECT('deliverableName','规划报告','requiredStatus','PUBLISHED'))),
    'IN_PROGRESS', '2026-07-16', '2026-08-15', '2026-07-16', NULL,
    1, NOW(), 1, NOW(), 0, 0);

-- 3.4 项目成员（3 种角色）
INSERT IGNORE INTO `pms_project_member`
    (`id`, `project_id`, `user_id`, `user_name`, `role`, `join_date`, `leave_date`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(7001, 1001, 1,   'Administrator', 'PROJECT_MANAGER', '2026-07-01', NULL, 1, NOW(), 1, NOW(), 0, 0),
(7002, 1001, 2,   '张明',           'PROJECT_MEMBER',  '2026-07-01', NULL, 1, NOW(), 1, NOW(), 0, 0),
(7003, 1001, 200, '审批员A',        'APPROVER',        '2026-07-01', NULL, 1, NOW(), 1, NOW(), 0, 0);

-- 3.5 任务（3 个：1 个 COMPLETED 顶层 + 1 个 COMPLETED 子任务 + 1 个 IN_PROGRESS 子任务）
-- 注：pms_impl_task.create_by 为 VARCHAR(64)，取 'admin'；planned_hours 列在 V67 未新增，使用 actual_hours/remaining_hours 表达工时。
INSERT IGNORE INTO `pms_impl_task`
    (`id`, `project_id`, `milestone_id`, `task_name`, `task_type`,
     `agent_id`, `engineer_id`, `engineer_name`,
     `plan_start_date`, `plan_end_date`, `actual_start_date`, `actual_end_date`,
     `status`, `progress`, `work_description`,
     `parent_task_id`, `task_path`, `depth`, `priority`,
     `actual_hours`, `remaining_hours`, `phase_id`, `task_weight`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(8001, 1001, NULL, '项目启动会', 'OEM',
     NULL, 1, 'Administrator',
     '2026-07-01', '2026-07-02', '2026-07-01', '2026-07-02',
     'COMPLETED', 100, '项目启动会已完成，参会方：客户、PM、技术负责人',
     NULL, '/8001/', 0, 'HIGH',
     4.00, 0.00, 5001, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8002, 1001, NULL, '需求确认', 'OEM',
     NULL, 1, 'Administrator',
     '2026-07-03', '2026-07-07', '2026-07-03', '2026-07-07',
     'COMPLETED', 100, '与客户确认需求范围，已签字',
     8001, '/8001/8002/', 1, 'HIGH',
     16.00, 0.00, 5001, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8003, 1001, NULL, '现场勘查', 'AGENT',
     NULL, 2, '张明',
     '2026-07-08', '2026-07-12', '2026-07-08', NULL,
     'IN_PROGRESS', 60, '4 个地市已完成勘查，剩余 6 地市待安排',
     8001, '/8001/8003/', 1, 'MEDIUM',
     14.40, 9.60, 5001, 1.00,
     'admin', NOW(), 'admin', NOW(), 0);

-- 3.6 任务检查项（2 个，强制项 2 条，部分勾选）
INSERT IGNORE INTO `pms_task_checklist`
    (`id`, `task_id`, `title`, `description`, `mandatory`, `checked`, `checked_by`, `checked_at`, `sort_order`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(9001, 8003, '现场照片上传', '上传至少 3 张现场环境照片', 1, 1, 2, NOW(), 1, 1, NOW(), 1, NOW(), 0, 0),
(9002, 8003, '客户签字确认', '获取客户现场确认签字',     1, 0, NULL, NULL, 2, 1, NOW(), 1, NOW(), 0, 0);

-- 3.7 任务依赖（1 条 FS 依赖，演示循环检测的初始图）
INSERT IGNORE INTO `pms_task_dependency`
    (`id`, `project_id`, `predecessor_task_id`, `successor_task_id`, `dependency_type`, `lag_days`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1, 1001, 8002, 8003, 'FS', 1, 1, NOW(), 1, NOW(), 0, 0);

-- 3.8 交付件（2 个：实施方案已发布 + 现场勘查报告待评审）
-- 注：pms_deliverable.create_by 为 VARCHAR(64)，取 'admin'。
INSERT IGNORE INTO `pms_deliverable`
    (`id`, `project_id`, `deliverable_name`, `deliverable_type`, `file_path`, `status`,
     `phase_id`, `current_version`, `mandatory`, `approver_role`, `published_at`, `archived_at`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(2001, 1001, '实施方案',     'DOCUMENT', '/files/impl-plan-v1.docx',    'PUBLISHED',
     5001, 1, 1, 'TECH_LEAD',        '2026-07-15 10:00:00', NULL,
     'admin', NOW(), 'admin', NOW(), 0),
(2002, 1001, '现场勘查报告', 'REPORT',   '/files/site-survey-v1.docx',  'REVIEWED',
     5001, 1, 1, 'PROJECT_MANAGER',  NULL,                  NULL,
     'admin', NOW(), 'admin', NOW(), 0);

-- 3.9 交付件版本（实施方案 v1，已发布；演示版本不可变性）
INSERT IGNORE INTO `pms_deliverable_version`
    (`id`, `deliverable_id`, `version_no`, `file_path`, `file_checksum`, `uploaded_by`, `uploaded_at`,
     `change_log`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version_lock`)
VALUES
(12001, 2001, 1, '/files/impl-plan-v1.docx', 'sha256:abc123def456', 1, '2026-07-10 10:00:00',
     '初始版本', 'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0);

-- 3.10 交付件签名（实施方案 v1 由 TECH_LEAD 电子签核）
INSERT IGNORE INTO `pms_deliverable_signature`
    (`id`, `deliverable_id`, `version_no`, `signer_id`, `signer_name`, `signer_role`,
     `signature_type`, `signature_data`, `signed_at`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(30001, 2001, 1, 200, '审批员A', 'TECH_LEAD',
     'ELECTRONIC', 'cert-fingerprint:7E2A9F', '2026-07-14 15:00:00',
     'admin', NOW(), 'admin', NOW(), 0);

-- 3.11 基线快照（1 个 APPROVED，含 3 个任务计划，演示基线偏差分析）
INSERT IGNORE INTO `pms_baseline_snapshot`
    (`id`, `project_id`, `baseline_name`, `status`, `snapshot_json`, `change_reason`,
     `approval_record_id`, `approved_at`, `approved_by`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(7001, 1001, '初始基线', 'APPROVED',
    JSON_ARRAY(
        JSON_OBJECT('taskId',8001,'taskName','项目启动会','plannedStart','2026-07-01','plannedEnd','2026-07-02','duration',2,'plannedHours',4),
        JSON_OBJECT('taskId',8002,'taskName','需求确认',  'plannedStart','2026-07-03','plannedEnd','2026-07-07','duration',5,'plannedHours',16),
        JSON_OBJECT('taskId',8003,'taskName','现场勘查',  'plannedStart','2026-07-08','plannedEnd','2026-07-12','duration',5,'plannedHours',24)
    ),
    '项目启动基线', NULL, '2026-07-01 10:00:00', 1,
    1, NOW(), 1, NOW(), 0, 0);

-- 3.12 统一审批记录（1 个 PENDING，演示字段脱敏）
INSERT IGNORE INTO `pms_approval_record`
    (`id`, `approval_type`, `business_id`, `business_code`, `project_id`, `process_instance_id`,
     `title`, `submitter_id`, `submitter_name`,
     `current_node_id`, `current_node_name`, `status`, `round`,
     `submitted_at`, `completed_at`, `timeout_at`, `escalated`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(9001, 'DELIVERABLE', 2002, 'DLV-2026-001', 1001, NULL,
     '交付件审批：现场勘查报告', 2, '张明',
     'node-pm-review', '项目经理审核', 'PENDING', 1,
     '2026-07-12 10:00:00', NULL, '2026-07-14 10:00:00', 0,
     1, NOW(), 1, NOW(), 0, 0);

-- 3.13 审批历史（1 条 SUBMIT，演示历史保留）
INSERT IGNORE INTO `pms_approval_history`
    (`id`, `record_id`, `round`, `node_name`, `operator_id`, `operator_name`,
     `action`, `opinion`, `operated_at`)
VALUES
(1, 9001, 1, '提交人', 2, '张明', 'SUBMIT', '请审核现场勘查报告', '2026-07-12 10:00:00');

-- 3.14 审批节点（1 个 PENDING 节点，绑定字段权限）
INSERT IGNORE INTO `pms_approval_node`
    (`id`, `record_id`, `node_name`, `node_order`, `approver_id`, `approver_role`,
     `status`, `approver_actual_id`, `opinion`, `operated_at`, `timeout_at`)
VALUES
(1, 9001, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'PENDING', NULL, NULL, NULL, '2026-07-14 10:00:00');

-- 3.15 审批敏感字段权限（3 条规则：金额脱敏 + 电话脱敏 + 文件路径隐藏）
INSERT IGNORE INTO `pms_approval_field_permission`
    (`id`, `approval_node_id`, `entity_type`, `field_name`, `permission`, `mask_pattern`, `custom_pattern`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1, 1, 'Deliverable', 'contractAmount', 'MASKED', 'amount-mask', NULL, 1, NOW(), 1, NOW(), 0, 0),
(2, 1, 'Deliverable', 'customerContact','MASKED', 'phone-mask',  NULL, 1, NOW(), 1, NOW(), 0, 0),
(3, 1, 'Deliverable', 'filePath',       'HIDDEN', NULL,          NULL, 1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 4. 数据完整性自检注释（不执行 SQL，仅作记录）
-- =============================================================
-- 预期数据条数（V72 执行后）：
--   sys_permission 新增 25 条
--   sys_menu 新增 7 条（1 顶级 + 4 子菜单 + 1 基线顶级 + 1 基线子菜单）
--   sys_role_menu 新增 ≤ 7 条
--   sys_dict 新增 9 条
--   sys_dict_item 新增 8+4+6+7+5+3+4+4+3 = 44 条
--   pms_project_template 1 条（id=1，PUBLISHED）
--   pms_project_template_version 1 条（id=1，v1.0.0，PUBLISHED）
--   pms_project 1 条（id=1001，IN_PROGRESS，从模板创建）
--   pms_project_phase 2 条（5001 COMPLETED, 5002 IN_PROGRESS）
--   pms_project_member 3 条（PM/MEMBER/APPROVER 三种角色）
--   pms_impl_task 3 条（8001/8002/8003，含父子层级 + 物化路径）
--   pms_task_checklist 2 条（强制项，部分勾选）
--   pms_task_dependency 1 条（FS 依赖）
--   pms_deliverable 2 条（2001 PUBLISHED, 2002 REVIEWED）
--   pms_deliverable_version 1 条（v1，PUBLISHED）
--   pms_deliverable_signature 1 条（电子签核）
--   pms_baseline_snapshot 1 条（APPROVED，含 3 任务计划）
--   pms_approval_record 1 条（PENDING）
--   pms_approval_history 1 条（SUBMIT）
--   pms_approval_node 1 条（PENDING）
--   pms_approval_field_permission 3 条（MASKED/MASKED/HIDDEN）
-- =============================================================
-- 文件结束
