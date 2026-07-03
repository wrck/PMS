-- =============================================================
-- V11__expand_asset_status.sql
-- Expand the asset status column to support the 9-state lifecycle
-- (ORDERED/IN_TRANSIT/RECEIVED/STAGED/INSTALLED/COMMISSIONED/
--  IN_PRODUCTION/RMA/DECOMMISSIONED) and migrate legacy values.
-- =============================================================

-- Widen / re-document the status column. Values are VARCHAR(32).
ALTER TABLE `pms_asset`
    MODIFY COLUMN `status` VARCHAR(32) DEFAULT 'RECEIVED'
    COMMENT 'Status (ORDERED/IN_TRANSIT/RECEIVED/STAGED/INSTALLED/COMMISSIONED/IN_PRODUCTION/RMA/DECOMMISSIONED)';

-- Migrate legacy status values to the new 9-state model.
UPDATE `pms_asset` SET `status` = 'RECEIVED'       WHERE `status` = 'IN_STOCK';
UPDATE `pms_asset` SET `status` = 'INSTALLED'      WHERE `status` = 'ALLOCATED';
UPDATE `pms_asset` SET `status` = 'RECEIVED'       WHERE `status` = 'RETURNED';
UPDATE `pms_asset` SET `status` = 'DECOMMISSIONED' WHERE `status` = 'SCRAP';
