-- =============================================================
-- V24__init_permissions.sql
-- Initialize permission menu entries for all business modules and
-- bind them to the super admin role (role_id = 1).
--
-- <p>Menu id range used: 100..399. Existing menus in V1 use 1..12,
-- leaving ample room for future system menu additions.</p>
--
-- <p>Menu type convention:
-- <ul>
--   <li>M = directory (top-level grouping)</li>
--   <li>C = menu (page-level entry, has list perm)</li>
--   <li>F = button (operation perm: add/edit/remove/process/etc.)</li>
-- </ul>
-- </p>
-- =============================================================

-- =============================================================
-- 1. System module supplementary permissions
--    Existing V1 covered user/role/menu; here we add dept + cache + schedule.
-- =============================================================

-- Dept buttons (parent_id = 5 = Dept menu from V1)
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(100, 5, 'Dept Add',    'F', '', NULL, 'system:dept:add',    '#', 1, '0', 'admin', NOW()),
(101, 5, 'Dept Edit',   'F', '', NULL, 'system:dept:edit',   '#', 2, '0', 'admin', NOW()),
(102, 5, 'Dept Delete', 'F', '', NULL, 'system:dept:remove', '#', 3, '0', 'admin', NOW());

-- Cache management buttons
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(110, 1, 'Cache',        'C', '/system/cache',    'system/cache/index',    'system:cache:list',  'redis',    8, '0', 'admin', NOW()),
(111, 110, 'Cache Clear','F', '',                 NULL,                    'system:cache:clear', '#',        1, '0', 'admin', NOW());

-- Schedule monitor buttons
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(120, 1, 'Schedule',          'C', '/system/schedule', 'system/schedule/index', 'system:schedule:list',  'time',     9, '0', 'admin', NOW()),
(121, 120, 'Schedule Retry',  'F', '',                 NULL,                    'system:schedule:retry', '#',        1, '0', 'admin', NOW());

-- =============================================================
-- 2. Project module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(200, 0,   'Project',           'M', '/project',               NULL,                          '',                              'example',  2, '0', 'admin', NOW()),
(201, 200, 'Project List',      'C', '/project/list',          'project/list/index',          'project:project:list',          'list',     1, '0', 'admin', NOW()),
(202, 201, 'Project Add',       'F', '',                        NULL,                          'project:project:add',           '#',        1, '0', 'admin', NOW()),
(203, 201, 'Project Edit',      'F', '',                        NULL,                          'project:project:edit',          '#',        2, '0', 'admin', NOW()),
(204, 201, 'Project Delete',    'F', '',                        NULL,                          'project:project:remove',        '#',        3, '0', 'admin', NOW()),
(205, 201, 'Project Approve',   'F', '',                        NULL,                          'project:project:approve',       '#',        4, '0', 'admin', NOW()),
(210, 200, 'Milestone',         'C', '/project/milestone',     'project/milestone/index',     'project:milestone:list',        'flag',     2, '0', 'admin', NOW()),
(211, 210, 'Milestone Add',     'F', '',                        NULL,                          'project:milestone:add',         '#',        1, '0', 'admin', NOW()),
(212, 210, 'Milestone Edit',    'F', '',                        NULL,                          'project:milestone:edit',        '#',        2, '0', 'admin', NOW()),
(213, 210, 'Milestone Delete',  'F', '',                        NULL,                          'project:milestone:remove',      '#',        3, '0', 'admin', NOW()),
(214, 210, 'Milestone Import',  'F', '',                        NULL,                          'project:milestone:import',      '#',        4, '0', 'admin', NOW()),
(220, 200, 'FinalAcceptance',   'C', '/project/final-acceptance','project/final-acceptance/index','project:finalAcceptance:list','checkbox', 3, '0', 'admin', NOW()),
(221, 220, 'FinalAccept Apply', 'F', '',                        NULL,                          'project:finalAcceptance:apply', '#',        1, '0', 'admin', NOW()),
(222, 220, 'FinalAccept Approve','F','',                        NULL,                          'project:finalAcceptance:approve','#',       2, '0', 'admin', NOW()),
(230, 200, 'PunchList',         'C', '/project/punch-list',    'project/punch-list/index',    'project:punchList:list',        'bug',      4, '0', 'admin', NOW()),
(231, 230, 'PunchList Add',     'F', '',                        NULL,                          'project:punchList:add',         '#',        1, '0', 'admin', NOW()),
(232, 230, 'PunchList Edit',    'F', '',                        NULL,                          'project:punchList:edit',        '#',        2, '0', 'admin', NOW()),
(233, 230, 'PunchList Delete',  'F', '',                        NULL,                          'project:punchList:remove',      '#',        3, '0', 'admin', NOW()),
(234, 230, 'PunchList Resolve', 'F', '',                        NULL,                          'project:punchList:resolve',     '#',        4, '0', 'admin', NOW()),
(235, 230, 'PunchList Verify',  'F', '',                        NULL,                          'project:punchList:verify',      '#',        5, '0', 'admin', NOW()),
(240, 200, 'Deliverable',       'C', '/project/deliverable',   'project/deliverable/index',   'project:deliverable:list',      'documentation', 5, '0', 'admin', NOW()),
(241, 240, 'Deliverable Add',   'F', '',                        NULL,                          'project:deliverable:add',       '#',        1, '0', 'admin', NOW()),
(242, 240, 'Deliverable Edit',  'F', '',                        NULL,                          'project:deliverable:edit',      '#',        2, '0', 'admin', NOW()),
(243, 240, 'Deliverable Delete','F', '',                        NULL,                          'project:deliverable:remove',    '#',        3, '0', 'admin', NOW());

-- =============================================================
-- 3. Asset module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(300, 0,   'Asset',             'M', '/asset',                  NULL,                          '',                          'component', 3, '0', 'admin', NOW()),
(301, 300, 'Asset List',        'C', '/asset/list',             'asset/list/index',            'asset:asset:list',          'server',    1, '0', 'admin', NOW()),
(302, 301, 'Asset Inbound',     'F', '',                        NULL,                          'asset:asset:add',           '#',         1, '0', 'admin', NOW()),
(303, 301, 'Asset Allocate',    'F', '',                        NULL,                          'asset:asset:allocate',      '#',         2, '0', 'admin', NOW()),
(304, 301, 'Asset Return',      'F', '',                        NULL,                          'asset:asset:return',        '#',         3, '0', 'admin', NOW()),
(305, 301, 'Asset Edit',        'F', '',                        NULL,                          'asset:asset:edit',          '#',         4, '0', 'admin', NOW()),
(306, 301, 'Asset Delete',      'F', '',                        NULL,                          'asset:asset:remove',        '#',         5, '0', 'admin', NOW()),
(307, 301, 'Asset Import',      'F', '',                        NULL,                          'asset:asset:import',        '#',         6, '0', 'admin', NOW()),
(310, 300, 'AssetModel',        'C', '/asset/model',            'asset/model/index',           'asset:model:list',          'skill',     2, '0', 'admin', NOW()),
(311, 310, 'AssetModel Add',    'F', '',                        NULL,                          'asset:model:add',           '#',         1, '0', 'admin', NOW()),
(312, 310, 'AssetModel Edit',   'F', '',                        NULL,                          'asset:model:edit',          '#',         2, '0', 'admin', NOW()),
(313, 310, 'AssetModel Delete', 'F', '',                        NULL,                          'asset:model:remove',        '#',         3, '0', 'admin', NOW()),
(320, 300, 'AssetCategory',     'C', '/asset/category',         'asset/category/index',        'asset:category:list',       'tree',      3, '0', 'admin', NOW()),
(321, 320, 'Category Add',      'F', '',                        NULL,                          'asset:category:add',        '#',         1, '0', 'admin', NOW()),
(322, 320, 'Category Edit',     'F', '',                        NULL,                          'asset:category:edit',       '#',         2, '0', 'admin', NOW()),
(323, 320, 'Category Delete',   'F', '',                        NULL,                          'asset:category:remove',     '#',         3, '0', 'admin', NOW()),
(330, 300, 'AssetTransfer',     'C', '/asset/transfer',         'asset/transfer/index',        'asset:transfer:list',       'swap',      4, '0', 'admin', NOW()),
(331, 330, 'Transfer Apply',    'F', '',                        NULL,                          'asset:transfer:apply',      '#',         1, '0', 'admin', NOW()),
(332, 330, 'Transfer Approve',  'F', '',                        NULL,                          'asset:transfer:approve',    '#',         2, '0', 'admin', NOW()),
(340, 300, 'RMA',               'C', '/asset/rma',              'asset/rma/index',             'asset:rma:list',            'bug',       5, '0', 'admin', NOW()),
(341, 340, 'RMA Add',           'F', '',                        NULL,                          'asset:rma:add',             '#',         1, '0', 'admin', NOW()),
(342, 340, 'RMA Process',       'F', '',                        NULL,                          'asset:rma:process',         '#',         2, '0', 'admin', NOW()),
(343, 340, 'RMA Close',         'F', '',                        NULL,                          'asset:rma:close',           '#',         3, '0', 'admin', NOW()),
(344, 340, 'RMA Delete',        'F', '',                        NULL,                          'asset:rma:remove',          '#',         4, '0', 'admin', NOW()),
(350, 300, 'Warranty',          'C', '/asset/warranty',         'asset/warranty/index',        'asset:warranty:list',       'shield',    6, '0', 'admin', NOW()),
(351, 350, 'Warranty Add',      'F', '',                        NULL,                          'asset:warranty:add',        '#',         1, '0', 'admin', NOW()),
(352, 350, 'Warranty Edit',     'F', '',                        NULL,                          'asset:warranty:edit',       '#',         2, '0', 'admin', NOW()),
(353, 350, 'Warranty Delete',   'F', '',                        NULL,                          'asset:warranty:remove',     '#',         3, '0', 'admin', NOW());

-- =============================================================
-- 4. Implementation module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(400, 0,   'Implementation',    'M', '/implementation',         NULL,                              '',                                    'build',    4, '0', 'admin', NOW()),
(401, 400, 'ImplTask',          'C', '/impl/task',               'impl/task/index',                 'implementation:implTask:list',        'task',     1, '0', 'admin', NOW()),
(402, 401, 'ImplTask Add',      'F', '',                         NULL,                              'implementation:implTask:add',         '#',        1, '0', 'admin', NOW()),
(403, 401, 'ImplTask Edit',     'F', '',                         NULL,                              'implementation:implTask:edit',        '#',        2, '0', 'admin', NOW()),
(404, 401, 'ImplTask Confirm',  'F', '',                         NULL,                              'implementation:implTask:confirm',     '#',        3, '0', 'admin', NOW()),
(410, 400, 'Agent',             'C', '/impl/agent',              'impl/agent/index',                'implementation:agent:list',           'user',     2, '0', 'admin', NOW()),
(411, 410, 'Agent Add',         'F', '',                         NULL,                              'implementation:agent:add',            '#',        1, '0', 'admin', NOW()),
(412, 410, 'Agent Edit',        'F', '',                         NULL,                              'implementation:agent:edit',           '#',        2, '0', 'admin', NOW()),
(413, 410, 'Agent Delete',      'F', '',                         NULL,                              'implementation:agent:remove',         '#',        3, '0', 'admin', NOW()),
(420, 400, 'AgentScore',        'C', '/impl/agent-score',        'impl/agent-score/index',          'implementation:agentScore:list',      'star',     3, '0', 'admin', NOW()),
(421, 420, 'AgentScore Add',    'F', '',                         NULL,                              'implementation:agentScore:add',       '#',        1, '0', 'admin', NOW()),
(430, 400, 'ImplProgress',      'C', '/impl/progress',           'impl/progress/index',             'implementation:implProgress:list',    'time',     4, '0', 'admin', NOW()),
(431, 430, 'Progress Add',      'F', '',                         NULL,                              'implementation:implProgress:add',     '#',        1, '0', 'admin', NOW()),
(432, 430, 'Progress Edit',     'F', '',                         NULL,                              'implementation:implProgress:edit',    '#',        2, '0', 'admin', NOW()),
(433, 430, 'Progress Remove',   'F', '',                         NULL,                              'implementation:implProgress:remove',  '#',        3, '0', 'admin', NOW()),
(440, 400, 'Settlement',        'C', '/impl/settlement',         'impl/settlement/index',           'implementation:settlement:list',      'money',    5, '0', 'admin', NOW()),
(441, 440, 'Settlement Add',    'F', '',                         NULL,                              'implementation:settlement:add',       '#',        1, '0', 'admin', NOW()),
(442, 440, 'Settlement Approve','F', '',                         NULL,                              'implementation:settlement:approve',   '#',        2, '0', 'admin', NOW()),
(443, 440, 'Settlement Export', 'F', '',                         NULL,                              'implementation:settlement:export',    '#',        3, '0', 'admin', NOW());

-- =============================================================
-- 5. Governance module (three-books)
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(500, 0,   'Governance',        'M', '/governance',             NULL,                              '',                              'eye',      5, '0', 'admin', NOW()),
(501, 500, 'ChangeRequest',     'C', '/governance/change-request','governance/change-request/index','governance:changeRequest:list','edit',     1, '0', 'admin', NOW()),
(502, 501, 'Change Add',        'F', '',                         NULL,                              'governance:changeRequest:add',  '#',        1, '0', 'admin', NOW()),
(503, 501, 'Change Edit',       'F', '',                         NULL,                              'governance:changeRequest:edit', '#',        2, '0', 'admin', NOW()),
(504, 501, 'Change Delete',     'F', '',                         NULL,                              'governance:changeRequest:remove','#',       3, '0', 'admin', NOW()),
(505, 501, 'Change Process',    'F', '',                         NULL,                              'governance:changeRequest:process','#',      4, '0', 'admin', NOW()),
(510, 500, 'Risk',              'C', '/governance/risk',         'governance/risk/index',           'governance:risk:list',          'warning',  2, '0', 'admin', NOW()),
(511, 510, 'Risk Add',          'F', '',                         NULL,                              'governance:risk:add',           '#',        1, '0', 'admin', NOW()),
(512, 510, 'Risk Edit',         'F', '',                         NULL,                              'governance:risk:edit',          '#',        2, '0', 'admin', NOW()),
(513, 510, 'Risk Delete',       'F', '',                         NULL,                              'governance:risk:remove',        '#',        3, '0', 'admin', NOW()),
(514, 510, 'Risk Process',      'F', '',                         NULL,                              'governance:risk:process',       '#',        4, '0', 'admin', NOW()),
(520, 500, 'Issue',             'C', '/governance/issue',        'governance/issue/index',          'governance:issue:list',         'bug',      3, '0', 'admin', NOW()),
(521, 520, 'Issue Add',         'F', '',                         NULL,                              'governance:issue:add',          '#',        1, '0', 'admin', NOW()),
(522, 520, 'Issue Edit',        'F', '',                         NULL,                              'governance:issue:edit',         '#',        2, '0', 'admin', NOW()),
(523, 520, 'Issue Delete',      'F', '',                         NULL,                              'governance:issue:remove',       '#',        3, '0', 'admin', NOW()),
(524, 520, 'Issue Process',     'F', '',                         NULL,                              'governance:issue:process',      '#',        4, '0', 'admin', NOW());

-- =============================================================
-- 6. Integration module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(600, 0,   'Integration',       'M', '/integration',            NULL,                              '',                          'link',     6, '0', 'admin', NOW()),
(601, 600, 'IntegrationLog',    'C', '/integration/log',        'integration/log/index',           'integration:log:list',      'log',      1, '0', 'admin', NOW()),
(602, 601, 'Integration Retry', 'F', '',                         NULL,                              'integration:log:retry',     '#',        1, '0', 'admin', NOW()),
(610, 600, 'D365Integration',   'C', '/integration/d365',       'integration/d365/index',          'integration:d365:list',     'international', 2, '0', 'admin', NOW()),
(611, 610, 'D365 Push',         'F', '',                         NULL,                              'integration:d365:push',     '#',        1, '0', 'admin', NOW()),
(612, 610, 'D365 Sync',         'F', '',                         NULL,                              'integration:d365:sync',     '#',        2, '0', 'admin', NOW()),
(620, 600, 'FpIntegration',     'C', '/integration/fp',         'integration/fp/index',            'integration:fp:list',       'money',    3, '0', 'admin', NOW()),
(621, 620, 'FP Push',           'F', '',                         NULL,                              'integration:fp:push',       '#',        1, '0', 'admin', NOW()),
(622, 620, 'FP OCR',            'F', '',                         NULL,                              'integration:fp:ocr',        '#',        2, '0', 'admin', NOW()),
(630, 600, 'OaIntegration',     'C', '/integration/oa',         'integration/oa/index',            'integration:oa:list',       'message',  4, '0', 'admin', NOW()),
(631, 630, 'OA Push',           'F', '',                         NULL,                              'integration:oa:push',       '#',        1, '0', 'admin', NOW()),
(632, 630, 'OA Process',        'F', '',                         NULL,                              'integration:oa:process',    '#',        2, '0', 'admin', NOW());

-- =============================================================
-- 7. Notification module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(700, 0,   'Notification',      'M', '/notification',           NULL,                              '',                              'message',   7, '0', 'admin', NOW()),
(701, 700, 'Notification List', 'C', '/notification/list',      'notification/list/index',         'notification:notification:list','bell',     1, '0', 'admin', NOW()),
(702, 701, 'Notification Send', 'F', '',                         NULL,                              'notification:notification:send', '#',        1, '0', 'admin', NOW()),
(710, 700, 'NotificationTpl',   'C', '/notification/template',  'notification/template/index',     'notification:template:list',    'form',     2, '0', 'admin', NOW()),
(711, 710, 'Tpl Add',           'F', '',                         NULL,                              'notification:template:add',     '#',        1, '0', 'admin', NOW()),
(712, 710, 'Tpl Edit',          'F', '',                         NULL,                              'notification:template:edit',    '#',        2, '0', 'admin', NOW()),
(713, 710, 'Tpl Delete',        'F', '',                         NULL,                              'notification:template:remove',  '#',        3, '0', 'admin', NOW());

-- =============================================================
-- 8. File module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(800, 0,   'File',              'M', '/file',                   NULL,                          '',                          'upload',   8, '0', 'admin', NOW()),
(801, 800, 'Attachment',        'C', '/file/attachment',        'file/attachment/index',       'file:attachment:list',      'file',     1, '0', 'admin', NOW()),
(802, 801, 'Attachment Upload', 'F', '',                         NULL,                          'file:attachment:upload',    '#',        1, '0', 'admin', NOW()),
(803, 801, 'Attachment Delete', 'F', '',                         NULL,                          'file:attachment:remove',    '#',        2, '0', 'admin', NOW());

-- =============================================================
-- 9. Workflow module
-- =============================================================
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
(900, 0,   'Workflow',          'M', '/workflow',               NULL,                          '',                          'cascade',  9, '0', 'admin', NOW()),
(901, 900, 'ProcessDefinition', 'C', '/workflow/definition',    'workflow/definition/index',   'workflow:definition:list',  'tree',     1, '0', 'admin', NOW()),
(902, 901, 'Definition Deploy', 'F', '',                         NULL,                          'workflow:definition:deploy', '#',       1, '0', 'admin', NOW()),
(903, 901, 'Definition Remove', 'F', '',                         NULL,                          'workflow:definition:remove', '#',       2, '0', 'admin', NOW()),
(910, 900, 'ProcessInstance',   'C', '/workflow/instance',      'workflow/instance/index',     'workflow:instance:list',    'tool',     2, '0', 'admin', NOW()),
(911, 910, 'Instance Start',    'F', '',                         NULL,                          'workflow:instance:start',   '#',        1, '0', 'admin', NOW()),
(920, 900, 'Task',              'C', '/workflow/task',          'workflow/task/index',         'workflow:task:list',        'checkbox', 3, '0', 'admin', NOW()),
(921, 920, 'Task Complete',     'F', '',                         NULL,                          'workflow:task:complete',    '#',        1, '0', 'admin', NOW()),
(922, 920, 'Task Withdraw',     'F', '',                         NULL,                          'workflow:task:withdraw',    '#',        2, '0', 'admin', NOW()),
(923, 920, 'Task Transfer',     'F', '',                         NULL,                          'workflow:task:transfer',    '#',        3, '0', 'admin', NOW());

-- =============================================================
-- 10. Bind all new menus (id >= 100) to super admin role (role_id = 1)
-- =============================================================
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`, `create_time`)
SELECT 1, `id`, 'admin', NOW() FROM `sys_menu`
WHERE `id` >= 100 AND `deleted` = 0;
