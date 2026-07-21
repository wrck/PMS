-- V85__unify_deliverable_type.sql
-- 统一交付件类型枚举为 8 类标准终验类型 + OTHER
-- 旧类型映射：DOCUMENT/CONFIG/CODE/MODEL → OTHER，REPORT → TEST_REPORT

-- 1. 迁移旧类型数据
UPDATE pms_deliverable SET deliverable_type = 'OTHER'
WHERE deliverable_type IN ('DOCUMENT', 'CONFIG', 'CODE', 'MODEL');

UPDATE pms_deliverable SET deliverable_type = 'TEST_REPORT'
WHERE deliverable_type = 'REPORT';

-- 2. 更新字段注释
ALTER TABLE pms_deliverable
    MODIFY COLUMN deliverable_type VARCHAR(32) DEFAULT NULL
    COMMENT '交付件类型：AS_BUILT/TEST_REPORT/ACCEPTANCE_CERT/TRAINING_RECORD/OPERATION_MANUAL/ASSET_REGISTER/WARRANTY_CERT/SPARE_PARTS_LIST/OTHER';
