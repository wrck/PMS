-- V86__deliverable_nature_type_and_ref.sql
-- 交付件类型从"终验用途分类"重构为"交付件性质分类"（数据字典驱动）
-- 1. pms_deliverable 新增 template_inherited / ref_entity_type / ref_entity_id 字段
-- 2. 旧终验用途类型（AS_BUILT 等 8 类）迁移为性质分类 DOCUMENT
-- 3. 新增字典 pms_deliverable_type（交付件性质类型）和 pms_deliverable_ref_entity_type（引用实体类型）
--
-- NOTE: 使用 PREPARE/EXECUTE + INFORMATION_SCHEMA 实现幂等（参考 V60 写法），
--       不使用 DELIMITER/存储过程，确保 Flyway 完全兼容。

-- =============================================================
-- 1. 幂等新增字段（PREPARE/EXECUTE 方式，参考 V60）
-- =============================================================

-- 1.1 template_inherited：是否模板预设（0=否 1=是）
SET @c = (SELECT COUNT(*) FROM information_schema.COLUMNS
          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_deliverable' AND COLUMN_NAME = 'template_inherited');
SET @sql = IF(@c = 0,
    'ALTER TABLE pms_deliverable ADD COLUMN `template_inherited` TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''是否模板预设（0=否 1=是）'' AFTER `mandatory`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1.2 ref_entity_type：引用实体类型（见字典 pms_deliverable_ref_entity_type）
SET @c = (SELECT COUNT(*) FROM information_schema.COLUMNS
          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_deliverable' AND COLUMN_NAME = 'ref_entity_type');
SET @sql = IF(@c = 0,
    'ALTER TABLE pms_deliverable ADD COLUMN `ref_entity_type` VARCHAR(32) DEFAULT NULL COMMENT ''引用实体类型（见字典 pms_deliverable_ref_entity_type）'' AFTER `approver_role`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1.3 ref_entity_id：引用实体ID
SET @c = (SELECT COUNT(*) FROM information_schema.COLUMNS
          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_deliverable' AND COLUMN_NAME = 'ref_entity_id');
SET @sql = IF(@c = 0,
    'ALTER TABLE pms_deliverable ADD COLUMN `ref_entity_id` BIGINT DEFAULT NULL COMMENT ''引用实体ID'' AFTER `ref_entity_type`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =============================================================
-- 2. 旧终验用途类型迁移为性质分类
--    8 类终验资料（AS_BUILT/TEST_REPORT/ACCEPTANCE_CERT/TRAINING_RECORD/
--    OPERATION_MANUAL/ASSET_REGISTER/WARRANTY_CERT/SPARE_PARTS_LIST）本质均为文档
-- =============================================================
UPDATE pms_deliverable SET deliverable_type = 'DOCUMENT'
WHERE deliverable_type IN (
    'AS_BUILT', 'TEST_REPORT', 'ACCEPTANCE_CERT', 'TRAINING_RECORD',
    'OPERATION_MANUAL', 'ASSET_REGISTER', 'WARRANTY_CERT', 'SPARE_PARTS_LIST'
);

-- NULL 类型默认归为 OTHER
UPDATE pms_deliverable SET deliverable_type = 'OTHER'
WHERE deliverable_type IS NULL;

-- 同步迁移 pms_deliverable_checklist 旧类型（保留表，仅数据对齐）
UPDATE pms_deliverable_checklist SET deliverable_type = 'DOCUMENT'
WHERE deliverable_type IN (
    'AS_BUILT', 'TEST_REPORT', 'ACCEPTANCE_CERT', 'TRAINING_RECORD',
    'OPERATION_MANUAL', 'ASSET_REGISTER', 'WARRANTY_CERT', 'SPARE_PARTS_LIST'
);

-- 更新 deliverable_type 字段注释（不再硬编码枚举，改为引用字典）
ALTER TABLE pms_deliverable
    MODIFY COLUMN `deliverable_type` VARCHAR(32) DEFAULT 'OTHER'
    COMMENT '交付件性质类型（见字典 pms_deliverable_type）：DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER';

-- =============================================================
-- 3. 新增字典：pms_deliverable_type（交付件性质类型，7 项）
-- =============================================================
INSERT IGNORE INTO `sys_dict` (`id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
(110, '交付件性质类型', 'pms_deliverable_type', '0', 'admin', NOW());

INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(110, '文档',       'DOCUMENT',    1, 'admin', NOW()),
(110, '代码',       'CODE',        2, 'admin', NOW()),
(110, '实体引用',   'ENTITY_REF',  3, 'admin', NOW()),
(110, '模型',       'MODEL',       4, 'admin', NOW()),
(110, '配置',       'CONFIG',      5, 'admin', NOW()),
(110, '数据',       'DATA',        6, 'admin', NOW()),
(110, '其他',       'OTHER',       7, 'admin', NOW());

-- =============================================================
-- 4. 新增字典：pms_deliverable_ref_entity_type（交付件引用实体类型，6 项）
-- =============================================================
INSERT IGNORE INTO `sys_dict` (`id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
(111, '交付件引用实体类型', 'pms_deliverable_ref_entity_type', '0', 'admin', NOW());

INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(111, '任务',     'TASK',        1, 'admin', NOW()),
(111, '资产',     'ASSET',       2, 'admin', NOW()),
(111, '阶段',     'PHASE',       3, 'admin', NOW()),
(111, '项目',     'PROJECT',     4, 'admin', NOW()),
(111, '交付件',   'DELIVERABLE', 5, 'admin', NOW()),
(111, '报告',     'REPORT',      6, 'admin', NOW());
