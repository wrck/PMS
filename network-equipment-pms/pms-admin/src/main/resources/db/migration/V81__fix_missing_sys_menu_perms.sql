-- =============================================================
-- V81__fix_missing_sys_menu_perms.sql
--
-- 修复：后端 Controller @PreAuthorize("hasAuthority('xxx')") 使用的权限码
-- 未在 sys_menu.perms 中注册，导致所有用户（含超管）调用接口被拒。
--
-- 根因：
--   1. V77 在 sys_permission 表注册了 25 条权限码，但权限加载链
--      (SysMenuMapper.listAllPerms / listPermsByUserId) 只读 sys_menu.perms，
--      不读 sys_permission。V77 在 sys_menu 中只插入了 4 条 (project:task:list、
--      project:template:list、project:baseline:list、空字符串)。
--   2. V24 早期遗漏：file:attachment:download、project:deliverable:submit/review/
--      sign/publish/archive、project:task:move/complete/approve/edit、
--      project:subproject:manage、project:close、project:phase:advance、
--      project:baseline:save/change、workflow:approval:handle/config、
--      workflow:field:perm、system:help:*、system:feedback:*、system:audit:list、
--      system:config:* 等未注册到 sys_menu。
--   3. V31 通配符权限 lowcode:data:*:list/add/edit/delete 未覆盖 query 动作。
--
-- 影响（A 类严重）：
--   - 18 个 Controller、35 个权限码 — 所有用户调用接口均被拒（含超管，
--     因 hasAuthority() 是 Spring Security 原生精确匹配，无超管放行逻辑）。
--   - 1 个 Controller、1 个权限码（B 类中等）— lowcode:dynamic/{entityCode}/query
--     普通用户被拒，超管走 @ss.hasPermi 放行。
--
-- 修复策略：
--   - 把全部缺失权限码注册到 sys_menu（menu_type='F', visible='1' 隐藏不显示）
--   - 全部绑定到超管角色（role_id=1）
--   - 不修改 Controller 注解（保持 hasAuthority 表达式不变，与现有代码风格一致）
--   - 不删除 sys_permission 中已注册的元数据（保留作为权限文档）
--
-- 兼容性：
--   - 使用 INSERT IGNORE 保证可重复执行
--   - 不指定 id（让自增主键分配），避免与 V24/V77 已分配的 id 冲突
--   - parent_id=0 表示按钮级权限无父菜单（F 类型 + visible='1' 不显示在菜单树）
-- =============================================================

-- =============================================================
-- 1. 注册缺失的权限码到 sys_menu
-- =============================================================

-- 1.1 项目模板（ProjectTemplateController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '项目模板-新增',     'F', '', NULL, 'project:template:add',     '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '项目模板-发布版本', 'F', '', NULL, 'project:template:publish', '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '项目模板-使用',     'F', '', NULL, 'project:template:use',     '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.2 项目主流程（ProjectController / ProjectMemberController / ProjectPhaseController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '项目-子项目管理', 'F', '', NULL, 'project:subproject:manage', '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '项目-关闭/取消',   'F', '', NULL, 'project:close',             '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '项目阶段-推进',   'F', '', NULL, 'project:phase:advance',     '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.3 基线（BaselineController / TaskDependencyController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '基线-保存快照', 'F', '', NULL, 'project:baseline:save',   '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '基线-变更申请', 'F', '', NULL, 'project:baseline:change', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.4 交付件（DeliverableController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '交付件-提交', 'F', '', NULL, 'project:deliverable:submit',  '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '交付件-评审', 'F', '', NULL, 'project:deliverable:review',  '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '交付件-签署', 'F', '', NULL, 'project:deliverable:sign',    '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '交付件-发布', 'F', '', NULL, 'project:deliverable:publish', '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '交付件-归档', 'F', '', NULL, 'project:deliverable:archive', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.5 实施任务（ImplTaskController / TaskChecklistController / TaskCommentController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '任务-移动',     'F', '', NULL, 'project:task:move',     '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '任务-完成提交', 'F', '', NULL, 'project:task:complete', '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '任务-审批',     'F', '', NULL, 'project:task:approve',  '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '任务-编辑',     'F', '', NULL, 'project:task:edit',     '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.6 工作流审批（ApprovalCenterController / ApprovalFieldPermissionController / ProjectConfigController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '审批中心-处理',     'F', '', NULL, 'workflow:approval:handle', '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '审批-字段权限',     'F', '', NULL, 'workflow:field:perm',      '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '审批-配置',         'F', '', NULL, 'workflow:approval:config', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.7 系统管理 — 帮助内容（HelpContentController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '帮助-新增', 'F', '', NULL, 'system:help:create', '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '帮助-编辑', 'F', '', NULL, 'system:help:edit',   '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '帮助-删除', 'F', '', NULL, 'system:help:remove', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.8 系统管理 — 反馈（FeedbackController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '反馈-列表', 'F', '', NULL, 'system:feedback:list',  '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '反馈-回复', 'F', '', NULL, 'system:feedback:reply', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.9 系统管理 — 审计日志（AuditLogController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '审计-列表', 'F', '', NULL, 'system:audit:list', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.10 系统管理 — 参数配置（SysConfigController）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '配置-列表', 'F', '', NULL, 'system:config:list',   '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '配置-新增', 'F', '', NULL, 'system:config:add',    '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '配置-编辑', 'F', '', NULL, 'system:config:edit',   '#', 0, '1', '1', '0', 'admin', NOW()),
(0, '配置-删除', 'F', '', NULL, 'system:config:remove', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.11 文件（FileController — 下载缩略图）
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '附件-下载', 'F', '', NULL, 'file:attachment:download', '#', 0, '1', '1', '0', 'admin', NOW());

-- 1.12 低代码动态数据查询（DynamicEntityController — B 类通配符补齐）
--      V31 注册了 lowcode:data:*:list/add/edit/delete，未覆盖 query 动作
INSERT IGNORE INTO `sys_menu`
    (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`, `visible`, `is_frame`, `is_cache`, `create_by`, `create_time`)
VALUES
(0, '动态数据-查询', 'F', '', NULL, 'lowcode:data:*:query', '#', 0, '1', '1', '0', 'admin', NOW());

-- =============================================================
-- 2. 把新增权限绑定到超管角色（role_id=1）
--    超管加载链：UserAuthorityService.doLoad() → sys_menu.listAllPerms()
--    → 全部 perms 作为 SimpleGrantedAuthority 注入 SecurityContext
-- =============================================================
SET @adminRoleId = 1;

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`, `create_time`)
SELECT @adminRoleId, m.`id`, 'admin', NOW()
FROM `sys_menu` m
WHERE m.`deleted` = 0
  AND m.`perms` IN (
    -- 1.1 项目模板
    'project:template:add',
    'project:template:publish',
    'project:template:use',
    -- 1.2 项目主流程
    'project:subproject:manage',
    'project:close',
    'project:phase:advance',
    -- 1.3 基线
    'project:baseline:save',
    'project:baseline:change',
    -- 1.4 交付件
    'project:deliverable:submit',
    'project:deliverable:review',
    'project:deliverable:sign',
    'project:deliverable:publish',
    'project:deliverable:archive',
    -- 1.5 实施任务
    'project:task:move',
    'project:task:complete',
    'project:task:approve',
    'project:task:edit',
    -- 1.6 工作流审批
    'workflow:approval:handle',
    'workflow:field:perm',
    'workflow:approval:config',
    -- 1.7 帮助内容
    'system:help:create',
    'system:help:edit',
    'system:help:remove',
    -- 1.8 反馈
    'system:feedback:list',
    'system:feedback:reply',
    -- 1.9 审计
    'system:audit:list',
    -- 1.10 参数配置
    'system:config:list',
    'system:config:add',
    'system:config:edit',
    'system:config:remove',
    -- 1.11 文件
    'file:attachment:download',
    -- 1.12 低代码动态数据
    'lowcode:data:*:query'
  );

-- =============================================================
-- 3. 自检说明（不执行，仅记录）
-- =============================================================
-- 预期新增 sys_menu 行数：3 + 3 + 2 + 5 + 4 + 3 + 3 + 2 + 1 + 4 + 1 + 1 = 32
-- 预期新增 sys_role_menu 行数：≤ 32（受 INSERT IGNORE 影响，已存在的会跳过）
-- 修复后影响范围：
--   - 项目模板保存（POST /api/project/template）— 立即解除「无权限访问」错误
--   - 项目模板发布版本、从模板创建项目 — 解除拒绝
--   - 项目子项目管理、关闭、取消 — 解除拒绝
--   - 阶段推进、基线保存/变更 — 解除拒绝
--   - 交付件提交/评审/签署/发布/归档 — 解除拒绝
--   - 任务移动/完成/审批/编辑（含检查项与评论）— 解除拒绝
--   - 审批中心处理、字段权限管理、审批配置 — 解除拒绝
--   - 帮助内容管理、反馈管理、审计日志查询、参数配置 — 解除拒绝
--   - 附件下载（含缩略图）— 解除拒绝
--   - 低代码动态数据查询（lowcode:dynamic/{entityCode}/query）— 解除普通用户拒绝
-- =============================================================
-- 文件结束
