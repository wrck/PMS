-- =====================================================================
-- V64__fix_demo_employee_permissions.sql
-- 修复演示中心菜单与权限：
--   1. 父菜单"员工入职演示"绑定到 admin 角色（之前遗漏）
--   2. 显示入职任务、部门管理（之前被隐藏 visible=1）
--   3. 补全缺失的权限标识（查看/任务/部门的 CRUD）
--   4. 将低代码渲染菜单 menu_type 从 C 改为 L，使 LowCodePermissionController
--      的 findLowCodeMenuByPath（menu_type='L'）能匹配到这些菜单
--   5. 为入职任务、部门管理补建列表配置（使页面可渲染）
-- =====================================================================

-- 1. 父菜单绑定到 admin 角色
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT 1, id, 'admin', NOW() FROM sys_menu
  WHERE menu_name = '员工入职演示' AND parent_id = 0 AND deleted = 0;

-- 2. 显示入职任务、部门管理
UPDATE sys_menu SET visible = '0'
  WHERE id IN (963, 964) AND deleted = 0;

-- 3. 补全缺失的权限标识
-- 3.1 员工管理菜单补充"查看"权限（原先只有 list/edit/delete/export，缺 view）
--    将 962 的 perms 从 lowcode:demo:employee:list 改为含 view 的通配形式
--    实际上 list 权限即可代表查看列表权限，这里补一个 view 按钮权限
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES
('查看员工', 961, 9, NULL, NULL, 'lowcode:demo:employee:view', 'F', '0', '0', NOW());

-- 3.2 入职任务操作权限
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES
('新增任务',     961, 20, NULL, NULL, 'lowcode:demo:task:edit',     'F', '0', '0', NOW()),
('删除任务',     961, 21, NULL, NULL, 'lowcode:demo:task:delete',   'F', '0', '0', NOW()),
('查看任务',     961, 19, NULL, NULL, 'lowcode:demo:task:view',     'F', '0', '0', NOW());

-- 3.3 部门管理操作权限
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES
('查看部门',     961, 29, NULL, NULL, 'lowcode:demo:department:view',   'F', '0', '0', NOW()),
('新增部门',     961, 30, NULL, NULL, 'lowcode:demo:department:edit',   'F', '0', '0', NOW()),
('删除部门',     961, 31, NULL, NULL, 'lowcode:demo:department:delete', 'F', '0', '0', NOW());

-- 4. 将演示菜单中作为低代码渲染入口的 C 菜单改为 L 类型
--    LowCodePermissionController.findLowCodeMenuByPath 只查 menu_type='L'
--    962 员工管理 path=lowcode/list/list_demo_employee
--    963 入职任务 path=tasks（需要修正为低代码渲染路由）
--    964 部门管理 path=departments（需要修正为低代码渲染路由）
UPDATE sys_menu SET menu_type = 'L'
  WHERE id = 962 AND deleted = 0;

-- 5. 为入职任务、部门管理补建列表配置（使低代码渲染能加载到配置）
-- 5.1 入职任务列表配置
INSERT IGNORE INTO `pms_lowcode_list`
    (`code`, `name`, `description`, `list_config`, `version`, `status`, `biz_type`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES (
'list_demo_onboarding_task',
'入职任务列表',
'演示-入职任务列表（含搜索/状态筛选/分页）',
JSON_OBJECT(
    'title', '入职任务列表',
    'entityCode', 'demo_onboarding_task',
    'columns', JSON_ARRAY(
        JSON_OBJECT('prop', 'id',           'label', 'ID',     'width', 60,  'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'task_name',    'label', '任务名称','width', 180, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'task_type',    'label', '任务类型','width', 120, 'breakpoint', 'md',
                    'tag', true, 'tagTypes', JSON_OBJECT('EQUIPMENT','primary','TRAINING','success','ORIENTATION','warning')),
        JSON_OBJECT('prop', 'assignee',     'label', '负责人', 'width', 100, 'breakpoint', 'md'),
        JSON_OBJECT('prop', 'status',       'label', '状态',   'width', 100, 'breakpoint', 'md',
                    'tag', true, 'tagTypes', JSON_OBJECT('PENDING','info','IN_PROGRESS','warning','DONE','success')),
        JSON_OBJECT('prop', 'due_date',     'label', '截止日期','width', 120, 'breakpoint', 'lg'),
        JSON_OBJECT('prop', 'completed_at', 'label', '完成时间','width', 160, 'breakpoint', 'xl'),
        JSON_OBJECT('prop', 'remark',       'label', '备注',   'minWidth', 200, 'breakpoint', 'xl')
    ),
    'filters', JSON_ARRAY(
        JSON_OBJECT('prop', 'task_name', 'label', '任务名称', 'type', 'input',  'span', 6, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'status',    'label', '状态',     'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label', '待处理',   'value', 'PENDING'),
                        JSON_OBJECT('label', '进行中',   'value', 'IN_PROGRESS'),
                        JSON_OBJECT('label', '已完成',   'value', 'DONE')
                    )),
        JSON_OBJECT('prop', 'task_type', 'label', '任务类型', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label', '设备配置', 'value', 'EQUIPMENT'),
                        JSON_OBJECT('label', '培训',     'value', 'TRAINING'),
                        JSON_OBJECT('label', '入职引导', 'value', 'ORIENTATION')
                    ))
    ),
    'operations', JSON_ARRAY(
        JSON_OBJECT('type', 'view',   'label', '查看', 'icon', 'View'),
        JSON_OBJECT('type', 'edit',   'label', '编辑', 'icon', 'Edit',   'permission', 'lowcode:demo:task:edit'),
        JSON_OBJECT('type', 'delete', 'label', '删除', 'icon', 'Delete', 'permission', 'lowcode:demo:task:delete', 'confirm', '确认删除该任务？')
    ),
    'toolbar', JSON_ARRAY(
        JSON_OBJECT('type', 'create', 'label', '新建任务', 'icon', 'Plus', 'permission', 'lowcode:demo:task:edit')
    ),
    'pagination', JSON_OBJECT('pageSize', 10, 'pageSizes', JSON_ARRAY(10, 20, 50, 100), 'layout', 'total, sizes, prev, pager, next, jumper'),
    'searchApi', '/api/lowcode/dynamic/demo_onboarding_task/list',
    'stripe', true, 'border', true, 'showSelection', true, 'showIndex', true, 'showPagination', true
),
1, 'DRAFT', 'EMP_ONBOARDING', 'demo', NOW(), 'demo', NOW(), 0
);

-- 5.2 部门管理列表配置
INSERT IGNORE INTO `pms_lowcode_list`
    (`code`, `name`, `description`, `list_config`, `version`, `status`, `biz_type`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES (
'list_demo_department',
'部门列表',
'演示-部门列表（含搜索/分页）',
JSON_OBJECT(
    'title', '部门列表',
    'entityCode', 'demo_department',
    'columns', JSON_ARRAY(
        JSON_OBJECT('prop', 'dept_code',   'label', '部门编码', 'width', 120, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'dept_name',   'label', '部门名称', 'width', 180, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'description', 'label', '描述',     'minWidth', 200, 'breakpoint', 'md')
    ),
    'filters', JSON_ARRAY(
        JSON_OBJECT('prop', 'dept_name', 'label', '部门名称', 'type', 'input', 'span', 8, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'dept_code', 'label', '部门编码', 'type', 'input', 'span', 8, 'breakpoint', 'sm')
    ),
    'operations', JSON_ARRAY(
        JSON_OBJECT('type', 'view',   'label', '查看', 'icon', 'View'),
        JSON_OBJECT('type', 'edit',   'label', '编辑', 'icon', 'Edit',   'permission', 'lowcode:demo:department:edit'),
        JSON_OBJECT('type', 'delete', 'label', '删除', 'icon', 'Delete', 'permission', 'lowcode:demo:department:delete', 'confirm', '确认删除该部门？')
    ),
    'toolbar', JSON_ARRAY(
        JSON_OBJECT('type', 'create', 'label', '新建部门', 'icon', 'Plus', 'permission', 'lowcode:demo:department:edit')
    ),
    'pagination', JSON_OBJECT('pageSize', 10, 'pageSizes', JSON_ARRAY(10, 20, 50), 'layout', 'total, sizes, prev, pager, next, jumper'),
    'searchApi', '/api/lowcode/dynamic/demo_department/list',
    'stripe', true, 'border', true, 'showSelection', false, 'showIndex', true, 'showPagination', true
),
1, 'DRAFT', 'EMP_ONBOARDING', 'demo', NOW(), 'demo', NOW(), 0
);

-- 6. 修正入职任务、部门管理的菜单 path 和 component，指向低代码渲染路由
UPDATE sys_menu SET
    path = 'lowcode/list/list_demo_onboarding_task',
    component = 'lowcode/render/index',
    menu_type = 'L'
  WHERE id = 963 AND deleted = 0;

UPDATE sys_menu SET
    path = 'lowcode/list/list_demo_department',
    component = 'lowcode/render/index',
    menu_type = 'L'
  WHERE id = 964 AND deleted = 0;

-- 7. 将所有新增的演示权限菜单绑定到 admin 角色
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT 1, id, 'admin', NOW() FROM sys_menu
  WHERE parent_id = 961 AND deleted = 0
    AND id NOT IN (SELECT menu_id FROM sys_role_menu WHERE role_id = 1);

-- 8. 清理测试垃圾触发器（config 为空、target_code 指向不存在的微流，会阻断所有实体创建）
UPDATE pms_lowcode_trigger SET deleted = 1
  WHERE code = 'test_trig_001' AND target_code = 'test' AND deleted = 0;

-- 9. 完成提示
SELECT '演示中心菜单与权限修复完成' AS message,
       CONCAT('已修复: 父菜单绑定admin / 显示入职任务和部门管理 / 补全权限 / menu_type=L / 新建2个列表配置 / 清理测试触发器') AS detail;
