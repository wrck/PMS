-- =============================================================
-- V16__expand_asset_task_agent_fields.sql
-- Add serialization / deployment fields to pms_asset, service
-- execution fields to pms_impl_task, and certification fields to
-- pms_agent.
-- =============================================================

-- ----------------------------
-- pms_asset: serialization / deployment fields
-- ----------------------------
ALTER TABLE `pms_asset`
    ADD COLUMN `mac_address`          VARCHAR(64)  DEFAULT NULL COMMENT 'MAC address' AFTER `warranty_id`,
    ADD COLUMN `management_ip`        VARCHAR(64)  DEFAULT NULL COMMENT 'Management IP address' AFTER `mac_address`,
    ADD COLUMN `hostname`             VARCHAR(200) DEFAULT NULL COMMENT 'Hostname' AFTER `management_ip`,
    ADD COLUMN `data_center`          VARCHAR(200) DEFAULT NULL COMMENT 'Data center' AFTER `hostname`,
    ADD COLUMN `rack`                 VARCHAR(100) DEFAULT NULL COMMENT 'Rack identifier' AFTER `data_center`,
    ADD COLUMN `start_u`              INT          DEFAULT NULL COMMENT 'Rack start U position' AFTER `rack`,
    ADD COLUMN `end_u`                INT          DEFAULT NULL COMMENT 'Rack end U position' AFTER `start_u`,
    ADD COLUMN `imei`                 VARCHAR(32)  DEFAULT NULL COMMENT 'IMEI (mobile equipment)' AFTER `end_u`,
    ADD COLUMN `po_no`                VARCHAR(100) DEFAULT NULL COMMENT 'Purchase order number' AFTER `imei`,
    ADD COLUMN `invoice_no`           VARCHAR(100) DEFAULT NULL COMMENT 'Invoice number' AFTER `po_no`,
    ADD COLUMN `warranty_contract_no` VARCHAR(100) DEFAULT NULL COMMENT 'Warranty contract number' AFTER `invoice_no`;

-- ----------------------------
-- pms_impl_task: service execution fields
-- ----------------------------
ALTER TABLE `pms_impl_task`
    ADD COLUMN `customer_contact`      VARCHAR(100) DEFAULT NULL COMMENT 'Customer contact' AFTER `accept_time`,
    ADD COLUMN `service_address`       VARCHAR(500) DEFAULT NULL COMMENT 'Service address' AFTER `customer_contact`,
    ADD COLUMN `service_type`          VARCHAR(32)  DEFAULT NULL COMMENT 'Service type (SITE_SURVEY/INSTALL/DEBUG/MAINTENANCE)' AFTER `service_address`,
    ADD COLUMN `sop_steps`             TEXT         COMMENT 'Standard operating procedure steps' AFTER `service_type`,
    ADD COLUMN `material_list`         TEXT         COMMENT 'Required materials list' AFTER `sop_steps`,
    ADD COLUMN `planned_hours`         INT          DEFAULT NULL COMMENT 'Planned work hours' AFTER `material_list`,
    ADD COLUMN `skill_level`           VARCHAR(32)  DEFAULT NULL COMMENT 'Required skill level (JUNIOR/SENIOR/EXPERT)' AFTER `planned_hours`,
    ADD COLUMN `safety_ppe`            VARCHAR(32)  DEFAULT NULL COMMENT 'Safety requirements (PPE/LOTO/PERMIT)' AFTER `skill_level`,
    ADD COLUMN `evidence_checkpoints`  TEXT         COMMENT 'Evidence checkpoint definition' AFTER `safety_ppe`,
    ADD COLUMN `sign_off_required`     TINYINT      DEFAULT 1 COMMENT 'Whether a formal sign-off is required (0=no 1=yes)' AFTER `evidence_checkpoints`;

-- ----------------------------
-- pms_agent: certification fields
-- ----------------------------
ALTER TABLE `pms_agent`
    ADD COLUMN `cert_level`        VARCHAR(32) DEFAULT NULL COMMENT 'Certification tier (SELECT/PREMIER/SILVER/GOLD)' AFTER `overall_score`,
    ADD COLUMN `ccie_count`        INT         DEFAULT 0 COMMENT 'Number of CCIE-certified engineers' AFTER `cert_level`,
    ADD COLUMN `specializations`   TEXT        COMMENT 'Specializations (JSON array as text)' AFTER `ccie_count`,
    ADD COLUMN `cert_expiry_date`  DATE        DEFAULT NULL COMMENT 'Certification expiry date' AFTER `specializations`;
