-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--   2. 为 pms_lowcode_import_task 补齐 create_by/update_by 列
--   3. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   4. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- 1. 为缺少 deleted 列的表补齐
ALTER TABLE pms_lowcode_datasource        ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record     ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log  ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template   ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup        ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock         ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release      ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task       ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case    ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- 2. 为 pms_lowcode_import_task 补齐 create_by/update_by 列
ALTER TABLE pms_lowcode_import_task
  ADD COLUMN create_by VARCHAR(64) DEFAULT '' AFTER end_time,
  ADD COLUMN update_by VARCHAR(64) DEFAULT '' AFTER create_time;

-- 2.1 为 pms_lowcode_config_audit_log 补齐 operate_time/update_time/create_by/update_by 列
ALTER TABLE pms_lowcode_config_audit_log
  ADD COLUMN operate_time DATETIME DEFAULT NULL AFTER tenant_id,
  ADD COLUMN update_time DATETIME DEFAULT NULL AFTER create_time,
  ADD COLUMN create_by VARCHAR(64) DEFAULT '' AFTER update_time,
  ADD COLUMN update_by VARCHAR(64) DEFAULT '' AFTER create_by;

-- 3. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',        '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',        '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',             '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',             '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',             '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',             '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',        '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',       '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0, 'F', 'lowcode:connector:test',        '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab Join',        0, 'F', 'lowcode:collaboration:join',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab List',        0, 'F', 'lowcode:collaboration:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Add',            0, 'F', 'lowcode:tab:add',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Edit',           0, 'F', 'lowcode:tab:edit',             '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Remove',         0, 'F', 'lowcode:tab:remove',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Publish',        0, 'F', 'lowcode:tab:publish',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Archive',        0, 'F', 'lowcode:tab:archive',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Export',         0, 'F', 'lowcode:tab:export',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Tab Import',         0, 'F', 'lowcode:tab:import',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Publish Submit',     0, 'F', 'lowcode:publish:submit',       '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Publish Approve',    0, 'F', 'lowcode:publish:approve',      '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Publish Rollback',   0, 'F', 'lowcode:publish:rollback',     '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Publish List',       0, 'F', 'lowcode:publish:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC RelatedPage Add',    0, 'F', 'lowcode:relatedPage:add',      '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC RelatedPage Edit',   0, 'F', 'lowcode:relatedPage:edit',     '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC RelatedPage Rm',     0, 'F', 'lowcode:relatedPage:remove',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC RelatedPage Pub',    0, 'F', 'lowcode:relatedPage:publish',   '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC RelatedPage Arch',   0, 'F', 'lowcode:relatedPage:archive',   '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC RelatedPage Exp',    0, 'F', 'lowcode:relatedPage:export',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC DataSource List',    0, 'F', 'lowcode:datasource:list',      '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC DataSource Edit',    0, 'F', 'lowcode:datasource:edit',      '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Menu Create',        0, 'F', 'lowcode:menu:create',          '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',   '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Comment List',       0, 'F', 'lowcode:comment:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC GrayRelease List',   0, 'F', 'lowcode:gray-release:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW());

-- 4. 将所有 lowcode 权限绑定到超级管理员角色（role_id=1）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT 1, id, 'admin', NOW() FROM sys_menu WHERE perms LIKE 'lowcode:%';
