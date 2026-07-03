-- =============================================================
-- V8__add_transfer_process_instance_id.sql
-- Add process_instance_id column to pms_asset_transfer for linking
-- the asset transfer approval workflow instance.
-- =============================================================

ALTER TABLE `pms_asset_transfer`
    ADD COLUMN `process_instance_id` VARCHAR(64) DEFAULT NULL COMMENT '流程实例ID' AFTER `approve_opinion`;
