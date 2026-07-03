-- =============================================================
-- V10__expand_milestone_type.sql
-- Expand the milestone type vocabulary from the legacy 5 types to the
-- 12-node PPDIOO milestone model and add the ppdioo_phase column.
-- Also extend the milestone status to include the BLOCKED state.
-- =============================================================

-- 1. Add the PPDIOO phase column next to milestone_type.
ALTER TABLE `pms_milestone`
    ADD COLUMN `ppdioo_phase` VARCHAR(32) DEFAULT NULL
        COMMENT 'PPDIOO phase (PREPARE, PLAN, DESIGN, IMPLEMENT, OPERATE)'
        AFTER `milestone_type`;

-- 2. Migrate legacy milestone_type values to the new 12-node model
--    and backfill the ppdioo_phase for the migrated rows.

-- Legacy INITIAL_ACCEPTANCE maps to SAT (Site Acceptance Test).
UPDATE `pms_milestone`
SET `milestone_type` = 'SAT',
    `ppdioo_phase` = 'IMPLEMENT'
WHERE `milestone_type` = 'INITIAL_ACCEPTANCE';

-- Legacy DEBUG maps to TESTING.
UPDATE `pms_milestone`
SET `milestone_type` = 'TESTING',
    `ppdioo_phase` = 'IMPLEMENT'
WHERE `milestone_type` = 'DEBUG';

-- Legacy INSTALL maps to INSTALLATION.
UPDATE `pms_milestone`
SET `milestone_type` = 'INSTALLATION',
    `ppdioo_phase` = 'IMPLEMENT'
WHERE `milestone_type` = 'INSTALL';

-- Backfill the ppdioo_phase for the already-existing canonical types.
UPDATE `pms_milestone`
SET `ppdioo_phase` = 'IMPLEMENT'
WHERE `milestone_type` = 'ARRIVAL'
  AND `ppdioo_phase` IS NULL;

UPDATE `pms_milestone`
SET `ppdioo_phase` = 'OPERATE'
WHERE `milestone_type` = 'FINAL_ACCEPTANCE'
  AND `ppdioo_phase` IS NULL;

-- Backfill any remaining rows that already use the new 12-node types.
UPDATE `pms_milestone` SET `ppdioo_phase` = 'PREPARE'    WHERE `milestone_type` = 'SITE_SURVEY'     AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'PLAN'       WHERE `milestone_type` = 'NETWORK_DESIGN' AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'PLAN'       WHERE `milestone_type` = 'PROCUREMENT'    AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'DESIGN'     WHERE `milestone_type` = 'STAGING'        AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'DESIGN'     WHERE `milestone_type` = 'FAT'            AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'IMPLEMENT'  WHERE `milestone_type` = 'INSTALLATION'  AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'IMPLEMENT'  WHERE `milestone_type` = 'TESTING'        AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'IMPLEMENT'  WHERE `milestone_type` = 'COMMISSIONING' AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'IMPLEMENT'  WHERE `milestone_type` = 'SAT'           AND `ppdioo_phase` IS NULL;
UPDATE `pms_milestone` SET `ppdioo_phase` = 'OPERATE'    WHERE `milestone_type` = 'UAT'           AND `ppdioo_phase` IS NULL;

-- 3. Expand the milestone status to include the BLOCKED state.
ALTER TABLE `pms_milestone`
    MODIFY COLUMN `status` VARCHAR(32) DEFAULT 'PENDING'
        COMMENT 'Status (PENDING, IN_PROGRESS, COMPLETED, OVERDUE, BLOCKED)';
