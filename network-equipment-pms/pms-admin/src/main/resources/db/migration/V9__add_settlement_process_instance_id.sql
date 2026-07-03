-- =============================================================
-- V9__add_settlement_process_instance_id.sql
-- Add process_instance_id column to pms_settlement for the
-- settlement approval workflow linkage.
-- =============================================================

ALTER TABLE `pms_settlement`
    ADD COLUMN `process_instance_id` VARCHAR(64) DEFAULT NULL COMMENT '流程实例ID' AFTER `push_response`;
