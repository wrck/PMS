-- =============================================================================
-- 低代码配置平台：为 data_field_relation 增加状态机与版本字段
-- 模块: PMS-springmvc
-- 数据库: MySQL 8.0+
-- 说明:
--   原表已存在 status (INT) 字段，并被 BaseController.findFieldList 运行时
--   渲染按 status=1 过滤使用，故不复用该字段。新增 configStatus (VARCHAR)
--   承载 draft / published / disabled 状态机；状态机方法在迁移状态时同步
--   旧版 status（published=1，其它=0），使 findFieldList 仍只返回已发布配置。
--   列名沿用项目 camelCase 命名约定（与 dataName / dataType / isSystemField 一致）。
-- =============================================================================

-- 1. 新增列
ALTER TABLE `data_field_relation`
  ADD COLUMN `configStatus` VARCHAR(20) DEFAULT 'published' COMMENT '配置状态: draft/published/disabled',
  ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号,每次发布递增',
  ADD COLUMN `templateId` VARCHAR(64) DEFAULT NULL COMMENT '模板ID,用于模板分组',
  ADD COLUMN `description` VARCHAR(255) DEFAULT NULL COMMENT '描述';

-- 2. 将既有记录标记为已发布（已发布的运行时配置仍由 status=1 标识，
--    此处将 configStatus 同步为 published，保持与运行时渲染一致）
UPDATE `data_field_relation`
SET `configStatus` = 'published'
WHERE `configStatus` IS NULL OR `configStatus` = '';

-- 可选：为已存在但未参与渲染（status != 1）的记录纠正为 disabled，使
-- configStatus 与旧版 status 语义保持一致。如不需要可注释掉。
UPDATE `data_field_relation`
SET `configStatus` = 'disabled'
WHERE `status` IS NOT NULL AND `status` <> 1
  AND (`configStatus` = 'published' OR `configStatus` IS NULL);

-- 3. 索引建议：按 dataName + dataType + configStatus 查询较频繁
-- CREATE INDEX idx_dfr_dataname_status ON `data_field_relation` (dataName, dataType, configStatus);
