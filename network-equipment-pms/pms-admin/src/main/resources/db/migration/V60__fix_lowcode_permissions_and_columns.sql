-- =============================================================
-- V60__-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyB-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
---- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINY-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_low-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE p-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted T-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_low-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2.-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0',-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#',-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, '-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#',-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0',-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0,-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0, 'F', 'lowcode:connector:test',         '#', 0, '1', '1', '0', '0', 'admin-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0, 'F', 'lowcode:connector:test',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab Join',        0, 'F', 'lowcode:-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0, 'F', 'lowcode:connector:test',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab Join',        0, 'F', 'lowcode:collaboration:join',     '#', 0, '1', '1', '0-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0, 'F', 'lowcode:connector:test',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab Join',        0, 'F', 'lowcode:collaboration:join',     '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab List',        0, '-- =============================================================
-- V60__fix_lowcode_permissions_and_columns.sql
-- 持久化 LowCode 模块联调修复：
--   1. 为缺少 deleted 列的 pms_lowcode_* 表补齐 deleted 列
--      （MyBatis-Plus 逻辑删除要求所有实体表都有 deleted 列）
--   2. 注册 @PreAuthorize 注解引用但 sys_menu 中缺失的 lowcode 权限
--   3. 将新增 lowcode 权限绑定到超级管理员角色（role_id=1）
-- =============================================================

-- ----------------------------
-- 1. 补齐 deleted 列
-- ----------------------------
ALTER TABLE pms_lowcode_datasource         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_approval_chain     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_backup_record      ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_collaboration_session ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_component_meta     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_audit_log   ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_config_template    ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_backup         ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_ddl_execution_log  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_edit_lock          ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_gray_release       ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_import_task        ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_microflow_version  ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_process_sla_record ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_publish_record     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';
ALTER TABLE pms_lowcode_rule_test_case     ADD COLUMN IF NOT EXISTS deleted TINYINT DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes';

-- ----------------------------
-- 2. 注册 @PreAuthorize 注解引用但缺失的 lowcode 权限
--    （parent_id=0, menu_type=F, visible=1 即不在菜单树显示的纯权限项）
-- ----------------------------
INSERT IGNORE INTO sys_menu (menu_name, parent_id, menu_type, perms, icon, order_num, visible, is_frame, is_cache, status, create_by, create_time)
VALUES
('LC Microflow List',     0, 'F', 'lowcode:microflow:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Microflow Exec',     0, 'F', 'lowcode:microflow:exec',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Trigger List',       0, 'F', 'lowcode:trigger:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC ApprovalChain List', 0, 'F', 'lowcode:approval-chain:list',    '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule List',          0, 'F', 'lowcode:rule:list',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Edit',          0, 'F', 'lowcode:rule:edit',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Exec',          0, 'F', 'lowcode:rule:exec',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Execute',       0, 'F', 'lowcode:rule:execute',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Rule Test',          0, 'F', 'lowcode:rule:test',              '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process List',       0, 'F', 'lowcode:process:list',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Process Edit',       0, 'F', 'lowcode:process:edit',           '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector List',     0, 'F', 'lowcode:connector:list',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Edit',     0, 'F', 'lowcode:connector:edit',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Connector Test',     0, 'F', 'lowcode:connector:test',         '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab Join',        0, 'F', 'lowcode:collaboration:join',     '#', 0, '1', '1', '0', '0', 'admin', NOW()),
('LC Collab List',        0, 'F', 'lowcode:collaboration:list',     '#', 0, '1',