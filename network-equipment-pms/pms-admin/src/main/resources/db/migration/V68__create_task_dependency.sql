-- V68__create_task_dependency.sql
-- 任务依赖表 + milestone 阶段关联字段补全
-- 关联设计文档：§2.2 TaskDependency（行 161-168）、§3.6 依赖与基线规则、§6.6（行 1435-1463）

-- ======================================================================
-- 1. 任务依赖表 pms_task_dependency
--    dependency_type：FS=完成-开始 / FF=完成-完成 / SS=开始-开始 / SF=开始-完成
--    lag_days：滞后天数（可负，表示提前）
-- ======================================================================
CREATE TABLE pms_task_dependency (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL COMMENT '项目ID',
    predecessor_task_id BIGINT       NOT NULL COMMENT '前置任务ID',
    successor_task_id   BIGINT       NOT NULL COMMENT '后续任务ID',
    dependency_type     VARCHAR(4)   NOT NULL DEFAULT 'FS' COMMENT 'FS/FF/SS/SF',
    lag_days            INT          NOT NULL DEFAULT 0 COMMENT '滞后天数（可负）',
    create_by           BIGINT       NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT       NULL,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pred_succ_type (predecessor_task_id, successor_task_id, dependency_type),
    KEY idx_successor_task_id (successor_task_id),
    KEY idx_predecessor_task_id (predecessor_task_id),
    KEY idx_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖';

-- ======================================================================
-- 2. pms_milestone 扩展
--    a) phase_id：关联项目阶段（设计 §6.6，milestone 已在 V2 创建、V10 扩展类型）
--    b) planned_date / actual_date / status：V2 已建立 plan_date / actual_date / status，
--       此处以幂等方式补齐同义字段（planned_date 别名），避免重复执行报错。
--       若列已存在则跳过。
-- ======================================================================
DROP PROCEDURE IF EXISTS pms_v68_alter_milestone;
DELIMITER $$
CREATE PROCEDURE pms_v68_alter_milestone()
BEGIN
    -- phase_id 关联阶段（新增字段）
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_milestone'
                     AND COLUMN_NAME = 'phase_id') THEN
        ALTER TABLE pms_milestone
            ADD COLUMN phase_id BIGINT NULL COMMENT '关联阶段ID' AFTER project_id;
        ALTER TABLE pms_milestone
            ADD INDEX idx_project_phase (project_id, phase_id);
    END IF;

    -- planned_date（别名，V2 已有 plan_date；此处按任务要求幂等补充）
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_milestone'
                     AND COLUMN_NAME = 'planned_date') THEN
        ALTER TABLE pms_milestone
            ADD COLUMN planned_date DATE NULL COMMENT '计划日期（与 plan_date 同义）';
    END IF;

    -- actual_date（V2 已存在，幂等保护）
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_milestone'
                     AND COLUMN_NAME = 'actual_date') THEN
        ALTER TABLE pms_milestone
            ADD COLUMN actual_date DATE NULL COMMENT '实际日期';
    END IF;

    -- status（V2/V10 已存在，幂等保护）
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_milestone'
                     AND COLUMN_NAME = 'status') THEN
        ALTER TABLE pms_milestone
            ADD COLUMN status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态 PENDING/IN_PROGRESS/COMPLETED/OVERDUE/BLOCKED';
    END IF;
END$$
DELIMITER ;

CALL pms_v68_alter_milestone();
DROP PROCEDURE IF EXISTS pms_v68_alter_milestone;
