-- =============================================================
-- V25__add_foreign_keys.sql
-- 为核心业务表添加外键约束，保证引用完整性。
--
-- <p>外键策略：
-- <ul>
--   <li>ON DELETE RESTRICT —— 禁止删除被引用的项目/资产，避免孤立业务数据</li>
--   <li>ON UPDATE CASCADE —— 主键变更时级联更新外键（虽 BIGINT 主键极少变更）</li>
-- </ul>
-- </p>
--
-- <p>注意 1（版本号）：tasks.md 原计划使用 V24，但 V24 已被 Task 1
-- （权限初始化）占用，故外键约束改用 V25，乐观锁字段使用 V26。</p>
--
-- <p>注意 2（孤儿数据）：在添加外键前先清洗历史脏数据。
-- 对于允许为空的 project_id 列，将不存在引用置为 NULL；
-- 对于 NOT NULL 的 project_id 列，删除引用不存在项目的行（仅当确实存在孤儿数据时）。
-- 这保证 ALTER TABLE ADD CONSTRAINT 不会因数据完整性失败。</p>
--
-- <p>注意 3（幂等性）：Flyway 每个版本仅执行一次，标准 ALTER TABLE ADD CONSTRAINT
-- 即可。MySQL 8 不支持 ADD CONSTRAINT IF NOT EXISTS 语法。</p>
-- =============================================================

-- =============================================================
-- 1. 清洗孤儿数据（仅在存在脏数据时生效，正常库为 0 行）
-- =============================================================

-- 可空 project_id 列：置 NULL
UPDATE `pms_asset`         SET `project_id` = NULL WHERE `project_id` IS NOT NULL AND `project_id` NOT IN (SELECT `id` FROM `pms_project`);
UPDATE `pms_settlement`    SET `project_id` = NULL WHERE `project_id` IS NOT NULL AND `project_id` NOT IN (SELECT `id` FROM `pms_project`);
UPDATE `pms_rma`           SET `project_id` = NULL WHERE `project_id` IS NOT NULL AND `project_id` NOT IN (SELECT `id` FROM `pms_project`);

-- NOT NULL project_id 列：删除孤儿行（项目被硬删的极端场景）
DELETE FROM `pms_milestone`      WHERE `project_id` NOT IN (SELECT `id` FROM `pms_project`);
DELETE FROM `pms_impl_task`      WHERE `project_id` NOT IN (SELECT `id` FROM `pms_project`);
DELETE FROM `pms_punch_list`     WHERE `project_id` NOT IN (SELECT `id` FROM `pms_project`);
DELETE FROM `pms_change_request` WHERE `project_id` NOT IN (SELECT `id` FROM `pms_project`);
DELETE FROM `pms_risk`           WHERE `project_id` NOT IN (SELECT `id` FROM `pms_project`);
DELETE FROM `pms_issue`          WHERE `project_id` NOT IN (SELECT `id` FROM `pms_project`);

-- pms_warranty.asset_id 关联 pms_asset：删除资产不存在的保修记录
DELETE FROM `pms_warranty` WHERE `asset_id` NOT IN (SELECT `id` FROM `pms_asset`);

-- =============================================================
-- 2. 添加外键约束（project_id → pms_project.id）
-- =============================================================

-- pms_asset.project_id（可空）
ALTER TABLE `pms_asset`
    ADD CONSTRAINT `fk_asset_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_impl_task.project_id
ALTER TABLE `pms_impl_task`
    ADD CONSTRAINT `fk_impl_task_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_settlement.project_id（可空）
ALTER TABLE `pms_settlement`
    ADD CONSTRAINT `fk_settlement_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_milestone.project_id
ALTER TABLE `pms_milestone`
    ADD CONSTRAINT `fk_milestone_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_punch_list.project_id
ALTER TABLE `pms_punch_list`
    ADD CONSTRAINT `fk_punch_list_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_rma.project_id（可空，资产登记时所属项目快照）
ALTER TABLE `pms_rma`
    ADD CONSTRAINT `fk_rma_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_change_request.project_id
ALTER TABLE `pms_change_request`
    ADD CONSTRAINT `fk_change_request_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_risk.project_id
ALTER TABLE `pms_risk`
    ADD CONSTRAINT `fk_risk_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- pms_issue.project_id
ALTER TABLE `pms_issue`
    ADD CONSTRAINT `fk_issue_project`
    FOREIGN KEY (`project_id`) REFERENCES `pms_project`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- =============================================================
-- 3. pms_warranty.asset_id → pms_asset.id
--    保修记录关联到具体资产（非项目）
-- =============================================================
ALTER TABLE `pms_warranty`
    ADD CONSTRAINT `fk_warranty_asset`
    FOREIGN KEY (`asset_id`) REFERENCES `pms_asset`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE;
