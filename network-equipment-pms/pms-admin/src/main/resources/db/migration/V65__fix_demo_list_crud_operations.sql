-- =====================================================================
-- V65__fix_demo_list_crud_operations.sql
-- 修复演示模块列表 CRUD 不可用问题：
--   1. operations 中 type 字段改为 action（与前端 ListOperationConfig.action 对齐）
--   2. 补全 url/api/method 字段，使查看/编辑/删除/新建按钮有实际行为
--   3. toolbar 补全新建按钮（之前为空数组或 NULL）
--   4. searchApi 路径修正：/api/lowcode/dynamic/{code}/list → /api/lowcode/data/{code}
--      （DynamicEntityController 实际路径为 /api/lowcode/data/{entityCode}）
-- =====================================================================

-- 1. 员工列表
UPDATE pms_lowcode_list SET list_config = JSON_OBJECT(
    'title', '员工列表',
    'entityCode', 'demo_employee',
    'columns', JSON_ARRAY(
        JSON_OBJECT('prop', 'emp_no', 'label', '工号', 'width', 120, 'sortable', true, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'name', 'label', '姓名', 'width', 120, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'gender', 'label', '性别', 'width', 80, 'formatter', 'genderText', 'breakpoint', 'md'),
        JSON_OBJECT('prop', 'dept_id', 'label', '部门', 'width', 140, 'formatter', 'deptName', 'breakpoint', 'md'),
        JSON_OBJECT('prop', 'position', 'label', '职位', 'width', 120, 'breakpoint', 'lg'),
        JSON_OBJECT('prop', 'level', 'label', '职级', 'width', 80, 'breakpoint', 'lg'),
        JSON_OBJECT('prop', 'entry_date', 'label', '入职日期', 'width', 120, 'breakpoint', 'xl'),
        JSON_OBJECT('prop', 'status', 'label', '状态', 'width', 100, 'tag', true,
                    'tagTypes', JSON_OBJECT('LEFT','danger','REGULAR','success','PROBATION','info','ONBOARDING','warning'),
                    'breakpoint', 'md')
    ),
    'filters', JSON_ARRAY(
        JSON_OBJECT('prop', 'emp_no', 'label', '工号', 'type', 'input', 'span', 6, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'name', 'label', '姓名', 'type', 'input', 'span', 6, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'dept_id', 'label', '部门', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label','人力资源部','value',1001),
                        JSON_OBJECT('label','工程研发部','value',1002),
                        JSON_OBJECT('label','销售部','value',1003)
                    )),
        JSON_OBJECT('prop', 'status', 'label', '状态', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label','入职中','value','ONBOARDING'),
                        JSON_OBJECT('label','试用期','value','PROBATION'),
                        JSON_OBJECT('label','已转正','value','REGULAR'),
                        JSON_OBJECT('label','已离职','value','LEFT')
                    ))
    ),
    'toolbar', JSON_ARRAY(
        JSON_OBJECT('id','op_create','label','新建员工','action','create','type','primary','icon','Plus',
                    'url','/lowcode/form/form_demo_employee?mode=create',
                    'permission','lowcode:demo:employee:edit')
    ),
    'operations', JSON_ARRAY(
        JSON_OBJECT('id','op_view','label','查看','action','view','type','text','icon','View',
                    'url','/lowcode/form/form_demo_employee?mode=view&id={id}'),
        JSON_OBJECT('id','op_edit','label','编辑','action','edit','type','primary','icon','Edit',
                    'url','/lowcode/form/form_demo_employee?mode=edit&id={id}',
                    'permission','lowcode:demo:employee:edit'),
        JSON_OBJECT('id','op_delete','label','删除','action','delete','type','danger','icon','Delete',
                    'api','/api/lowcode/data/demo_employee/{id}','method','DELETE',
                    'confirm','确认删除该员工？',
                    'permission','lowcode:demo:employee:delete')
    ),
    'pagination', JSON_OBJECT('pageSize', 20, 'pageSizes', JSON_ARRAY(10,20,50,100), 'layout', 'total, sizes, prev, pager, next, jumper'),
    'searchApi', '/api/lowcode/data/demo_employee',
    'method', 'GET',
    'stripe', true, 'border', true, 'showSelection', true, 'showIndex', true, 'showPagination', true,
    'export', JSON_OBJECT('enabled', false)
)
WHERE code = 'list_demo_employee' AND deleted = 0;

-- 2. 入职任务列表
UPDATE pms_lowcode_list SET list_config = JSON_OBJECT(
    'title', '入职任务列表',
    'entityCode', 'demo_onboarding_task',
    'columns', JSON_ARRAY(
        JSON_OBJECT('prop', 'id', 'label', 'ID', 'width', 60, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'task_name', 'label', '任务名称', 'width', 180, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'task_type', 'label', '任务类型', 'width', 120, 'breakpoint', 'md',
                    'tag', true, 'tagTypes', JSON_OBJECT('EQUIPMENT','primary','TRAINING','success','ORIENTATION','warning')),
        JSON_OBJECT('prop', 'assignee', 'label', '负责人', 'width', 100, 'breakpoint', 'md'),
        JSON_OBJECT('prop', 'status', 'label', '状态', 'width', 100, 'breakpoint', 'md',
                    'tag', true, 'tagTypes', JSON_OBJECT('PENDING','info','IN_PROGRESS','warning','DONE','success')),
        JSON_OBJECT('prop', 'due_date', 'label', '截止日期', 'width', 120, 'breakpoint', 'lg'),
        JSON_OBJECT('prop', 'completed_at', 'label', '完成时间', 'width', 160, 'breakpoint', 'xl'),
        JSON_OBJECT('prop', 'remark', 'label', '备注', 'minWidth', 200, 'breakpoint', 'xl')
    ),
    'filters', JSON_ARRAY(
        JSON_OBJECT('prop', 'task_name', 'label', '任务名称', 'type', 'input', 'span', 6, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'status', 'label', '状态', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label','待处理','value','PENDING'),
                        JSON_OBJECT('label','进行中','value','IN_PROGRESS'),
                        JSON_OBJECT('label','已完成','value','DONE')
                    )),
        JSON_OBJECT('prop', 'task_type', 'label', '任务类型', 'type', 'select', 'span', 6, 'breakpoint', 'sm',
                    'options', JSON_ARRAY(
                        JSON_OBJECT('label','设备配置','value','EQUIPMENT'),
                        JSON_OBJECT('label','培训','value','TRAINING'),
                        JSON_OBJECT('label','入职引导','value','ORIENTATION')
                    ))
    ),
    'toolbar', JSON_ARRAY(
        JSON_OBJECT('id','op_create','label','新建任务','action','create','type','primary','icon','Plus',
                    'url','/lowcode/form/form_demo_onboarding_task?mode=create',
                    'permission','lowcode:demo:task:edit')
    ),
    'operations', JSON_ARRAY(
        JSON_OBJECT('id','op_view','label','查看','action','view','type','text','icon','View',
                    'url','/lowcode/form/form_demo_onboarding_task?mode=view&id={id}'),
        JSON_OBJECT('id','op_edit','label','编辑','action','edit','type','primary','icon','Edit',
                    'url','/lowcode/form/form_demo_onboarding_task?mode=edit&id={id}',
                    'permission','lowcode:demo:task:edit'),
        JSON_OBJECT('id','op_delete','label','删除','action','delete','type','danger','icon','Delete',
                    'api','/api/lowcode/data/demo_onboarding_task/{id}','method','DELETE',
                    'confirm','确认删除该任务？',
                    'permission','lowcode:demo:task:delete')
    ),
    'pagination', JSON_OBJECT('pageSize', 10, 'pageSizes', JSON_ARRAY(10,20,50,100), 'layout', 'total, sizes, prev, pager, next, jumper'),
    'searchApi', '/api/lowcode/data/demo_onboarding_task',
    'method', 'GET',
    'stripe', true, 'border', true, 'showSelection', true, 'showIndex', true, 'showPagination', true
)
WHERE code = 'list_demo_onboarding_task' AND deleted = 0;

-- 3. 部门列表
UPDATE pms_lowcode_list SET list_config = JSON_OBJECT(
    'title', '部门列表',
    'entityCode', 'demo_department',
    'columns', JSON_ARRAY(
        JSON_OBJECT('prop', 'dept_code', 'label', '部门编码', 'width', 120, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'dept_name', 'label', '部门名称', 'width', 180, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'description', 'label', '描述', 'minWidth', 200, 'breakpoint', 'md')
    ),
    'filters', JSON_ARRAY(
        JSON_OBJECT('prop', 'dept_name', 'label', '部门名称', 'type', 'input', 'span', 8, 'breakpoint', 'xs'),
        JSON_OBJECT('prop', 'dept_code', 'label', '部门编码', 'type', 'input', 'span', 8, 'breakpoint', 'sm')
    ),
    'toolbar', JSON_ARRAY(
        JSON_OBJECT('id','op_create','label','新建部门','action','create','type','primary','icon','Plus',
                    'url','/lowcode/form/form_demo_department?mode=create',
                    'permission','lowcode:demo:department:edit')
    ),
    'operations', JSON_ARRAY(
        JSON_OBJECT('id','op_view','label','查看','action','view','type','text','icon','View',
                    'url','/lowcode/form/form_demo_department?mode=view&id={id}'),
        JSON_OBJECT('id','op_edit','label','编辑','action','edit','type','primary','icon','Edit',
                    'url','/lowcode/form/form_demo_department?mode=edit&id={id}',
                    'permission','lowcode:demo:department:edit'),
        JSON_OBJECT('id','op_delete','label','删除','action','delete','type','danger','icon','Delete',
                    'api','/api/lowcode/data/demo_department/{id}','method','DELETE',
                    'confirm','确认删除该部门？',
                    'permission','lowcode:demo:department:delete')
    ),
    'pagination', JSON_OBJECT('pageSize', 10, 'pageSizes', JSON_ARRAY(10,20,50), 'layout', 'total, sizes, prev, pager, next, jumper'),
    'searchApi', '/api/lowcode/data/demo_department',
    'method', 'GET',
    'stripe', true, 'border', true, 'showSelection', false, 'showIndex', true, 'showPagination', true
)
WHERE code = 'list_demo_department' AND deleted = 0;

SELECT '演示列表 CRUD 配置修复完成' AS message;
