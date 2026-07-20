-- Complete low-code designer permissions.
-- Earlier migrations registered only part of the permissions referenced by
-- @PreAuthorize, and V62 soft-deleted template child permissions together with
-- obsolete route menus. This made legitimate designer entries disappear and
-- caused edit/execute APIs to return 403 even for the super administrator.

SET @lcParentId = (
    SELECT id
    FROM sys_menu
    WHERE menu_name = '低代码管理' AND parent_id = 0 AND deleted = 0
    ORDER BY id DESC
    LIMIT 1
);

INSERT INTO sys_menu
    (menu_name, parent_id, menu_type, path, component, perms, icon,
     order_num, visible, status, is_frame, is_cache, create_by, create_time)
VALUES
('LC Form List',          @lcParentId, 'F', '', NULL, 'lowcode:form:list',           '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Add',           @lcParentId, 'F', '', NULL, 'lowcode:form:add',            '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Edit',          @lcParentId, 'F', '', NULL, 'lowcode:form:edit',           '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Remove',        @lcParentId, 'F', '', NULL, 'lowcode:form:remove',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Publish',       @lcParentId, 'F', '', NULL, 'lowcode:form:publish',        '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Archive',       @lcParentId, 'F', '', NULL, 'lowcode:form:archive',        '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Export',        @lcParentId, 'F', '', NULL, 'lowcode:form:export',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Form Import',        @lcParentId, 'F', '', NULL, 'lowcode:form:import',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List List',          @lcParentId, 'F', '', NULL, 'lowcode:list:list',           '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Add',           @lcParentId, 'F', '', NULL, 'lowcode:list:add',            '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Edit',          @lcParentId, 'F', '', NULL, 'lowcode:list:edit',           '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Remove',        @lcParentId, 'F', '', NULL, 'lowcode:list:remove',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Publish',       @lcParentId, 'F', '', NULL, 'lowcode:list:publish',        '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Archive',       @lcParentId, 'F', '', NULL, 'lowcode:list:archive',        '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Export',        @lcParentId, 'F', '', NULL, 'lowcode:list:export',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC List Import',        @lcParentId, 'F', '', NULL, 'lowcode:list:import',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Entity Edit',        @lcParentId, 'F', '', NULL, 'lowcode:entity:edit',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Microflow Edit',     @lcParentId, 'F', '', NULL, 'lowcode:microflow:edit',      '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Trigger Edit',       @lcParentId, 'F', '', NULL, 'lowcode:trigger:edit',        '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC ApprovalChain Edit', @lcParentId, 'F', '', NULL, 'lowcode:approval-chain:edit', '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC RelatedPage Import', @lcParentId, 'F', '', NULL, 'lowcode:relatedPage:import',  '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC GrayRelease Edit',   @lcParentId, 'F', '', NULL, 'lowcode:gray-release:edit',   '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Comment Add',        @lcParentId, 'F', '', NULL, 'lowcode:comment:add',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Comment Delete',     @lcParentId, 'F', '', NULL, 'lowcode:comment:del',         '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC EditLock Acquire',   @lcParentId, 'F', '', NULL, 'lowcode:editlock:acquire',    '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC EditLock Release',   @lcParentId, 'F', '', NULL, 'lowcode:editlock:release',    '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Version Branch',     @lcParentId, 'F', '', NULL, 'lowcode:version:branch',      '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Version Import',     @lcParentId, 'F', '', NULL, 'lowcode:version:import',      '#', 0, '1', '0', '1', '0', 'admin', NOW()),
('LC Version Tag',        @lcParentId, 'F', '', NULL, 'lowcode:version:tag',         '#', 0, '1', '0', '1', '0', 'admin', NOW());

-- V62 removed these route children. Their permission records are still needed,
-- so restore them as hidden button permissions rather than route menus.
UPDATE sys_menu
SET parent_id = @lcParentId,
    menu_type = 'F',
    path = '',
    component = NULL,
    visible = '1',
    status = '0',
    deleted = 0
WHERE perms IN ('lowcode:template:list', 'lowcode:template:create');

-- Ensure the super administrator receives every active low-code capability.
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT 1, id, 'admin', NOW()
FROM sys_menu
WHERE perms LIKE 'lowcode:%' AND deleted = 0;
