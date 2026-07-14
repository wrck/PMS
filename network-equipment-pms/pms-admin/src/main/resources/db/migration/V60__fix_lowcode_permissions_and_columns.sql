-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- Persist LowCode module integration fixes:
--   1. Add deleted column to pms_lowcode_* tables that lack it
--   2. Add create_by/update_by columns to pms_lowcode_import_task
--   3. Add operate_time/update_time/create_by/update_by to pms_lowcode_config_audit_log
--   4. Register missing lowcode permissions in sys_menu
--   5. Bind new lowcode permissions to super admin role (role_id=1)
--
-- NOTE: Uses PREPARE/EXECUTE with INFORMATION_SCHEMA check to be
--       idempotent (safe for re-run when columns already exist).
--       MySQL 8.0.16 does not support ADD COLUMN IF NOT EXISTS,
--       and Flyway does not support DELIMITER syntax.
-- =============================================================

-- 1. Add deleted column to tables that lack it (idempotent)
-- pms_lowcode_datasource
SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_datasource' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_datasource ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_approval_chain' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_approval_chain ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_backup_record' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_backup_record ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_collaboration_session' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_component_meta' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_component_meta ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_config_audit_log' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_config_audit_log ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_config_template' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_config_template ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_ddl_backup' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_ddl_backup ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_ddl_execution_log' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_ddl_execution_log ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_edit_lock' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_edit_lock ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_gray_release' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_gray_release ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_import_task' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_import_task ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_microflow_version' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_microflow_version ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_process_sla_record' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_publish_record' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_publish_record ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_rule_test_case' AND column_name = 'deleted');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_rule_test_case ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT ''Logical delete 0=no 1=yes''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. Add create_by/update_by to pms_lowcode_import_task
SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_import_task' AND column_name = 'create_by');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_import_task ADD COLUMN create_by VARCHAR(64) DEFAULT ''''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_import_task' AND column_name = 'update_by');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_import_task ADD COLUMN update_by VARCHAR(64) DEFAULT ''''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.1 Add operate_time/update_time/create_by/update_by to pms_lowcode_config_audit_log
SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_config_audit_log' AND column_name = 'operate_time');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_config_audit_log ADD COLUMN operate_time DATETIME DEFAULT NULL', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_config_audit_log' AND column_name = 'update_time');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_config_audit_log ADD COLUMN update_time DATETIME DEFAULT NULL', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_config_audit_log' AND column_name = 'create_by');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_config_audit_log ADD COLUMN create_by VARCHAR(64) DEFAULT ''''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'pms_lowcode_config_audit_log' AND column_name = 'update_by');
SET @sql = IF(@c = 0, 'ALTER TABLE pms_lowcode_config_audit_log ADD COLUMN update_by VARCHAR(64) DEFAULT ''''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. Register lowcode permissions referenced by @PreAuthorize but missing in sys_menu
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

-- 4. Bind all lowcode permissions to super admin role (role_id=1)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id, create_by, create_time)
SELECT 1, id, 'admin', NOW() FROM sys_menu WHERE perms LIKE 'lowcode:%';
