-- =============================================================
-- V7__add_project_process_instance_id.sql
-- Add process_instance_id column to pms_project for linking the
-- project approval workflow instance.
-- =============================================================

ALTER TABLE `pms_project`
    ADD COLUMN `process_instance_id` VARCHAR(64) DEFAULT NULL COMMENT '流程实例ID' AFTER `priority`;
