-- V69__create_baseline_snapshot.sql
-- 计划基线快照表
-- 关联设计文档：§2.2 BaselineSnapshot（行 170-182）、§3.6 计划基线偏差分析、§6.7（行 1465-1492）

-- ======================================================================
-- 1. 基线快照表 pms_baseline_snapshot
--    status：DRAFT（草稿）/ APPROVED（已审批，单一活跃）/ SUPERSEDED（已被新基线取代）
--    snapshot_json：项目全部任务计划快照
--      [{taskId, taskName, plannedStart, plannedEnd, duration, plannedHours, ...}]
-- ======================================================================
CREATE TABLE pms_baseline_snapshot (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL COMMENT '项目ID',
    baseline_name       VARCHAR(128) NOT NULL COMMENT '基线名称',
    status              VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/APPROVED/SUPERSEDED',
    snapshot_json       JSON         NOT NULL COMMENT '快照JSON：任务计划列表',
    change_reason       VARCHAR(500) NULL COMMENT '变更原因（关联审批）',
    approval_record_id  BIGINT       NULL COMMENT '关联审批记录ID',
    approved_at         DATETIME     NULL COMMENT '审批时间',
    approved_by         BIGINT       NULL COMMENT '审批人ID',
    create_by           BIGINT       NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT       NULL,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_project_id_status (project_id, status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计划基线快照';

-- ======================================================================
-- 2. pms_baseline_history 关联基线快照（V17 已建表，幂等补充 baseline_snapshot_id）
-- ======================================================================
DROP PROCEDURE IF EXISTS pms_v69_alter_baseline_history;
DELIMITER $$
CREATE PROCEDURE pms_v69_alter_baseline_history()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_baseline_history'
                     AND COLUMN_NAME = 'baseline_snapshot_id') THEN
        ALTER TABLE pms_baseline_history
            ADD COLUMN baseline_snapshot_id BIGINT NULL COMMENT '关联基线快照ID' AFTER project_id;
    END IF;
END$$
DELIMITER ;

CALL pms_v69_alter_baseline_history();
DROP PROCEDURE IF EXISTS pms_v69_alter_baseline_history;
