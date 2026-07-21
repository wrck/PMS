-- Align deliverable lifecycle permissions with controller operations.
INSERT IGNORE INTO sys_menu
    (parent_id, menu_name, menu_type, path, component, perms, icon, order_num,
     visible, is_frame, is_cache, create_by, create_time)
VALUES
(240, 'Deliverable Upload', 'F', '', NULL, 'project:deliverable:upload', '#', 4, '1', '1', '0', 'admin', NOW()),
(240, 'Deliverable Revise', 'F', '', NULL, 'project:deliverable:revise', '#', 5, '1', '1', '0', 'admin', NOW());

-- Existing roles that can create deliverables inherit upload/revise capabilities.
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT DISTINCT rm.role_id, target.id, 'admin', NOW()
FROM sys_role_menu rm
JOIN sys_menu source ON source.id = rm.menu_id AND source.perms = 'project:deliverable:add'
JOIN sys_menu target ON target.perms IN ('project:deliverable:upload', 'project:deliverable:revise')
WHERE source.deleted = 0 AND target.deleted = 0;
