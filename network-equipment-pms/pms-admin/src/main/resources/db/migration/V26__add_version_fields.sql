-- =============================================================
-- V26__add_version_fields.sql
-- 为 5 个核心实体表添加乐观锁版本号字段 version。
--
-- <p>配合 MyBatis-Plus {@code OptimisticLockerInnerInterceptor} +
-- 实体 {@code @Version} 注解实现乐观锁控制，防止并发更新覆盖。</p>
--
-- <p>注意 1（默认值）：version INT NOT NULL DEFAULT 0，
-- 保证历史数据更新时不会因 version IS NULL 而触发乐观锁冲突。
-- MyBatis-Plus 在 version 为 null 时不执行乐观锁逻辑，但显式赋 0
-- 更安全且便于排查。</p>
--
-- <p>注意 2（幂等性）：Flyway 每个版本仅执行一次，标准 ALTER TABLE ADD COLUMN
-- 即可。MySQL 8 不支持 ADD COLUMN IF NOT EXISTS 语法。</p>
--
-- <p>注意 3（版本号）：tasks.md 原计划 V25，但 V25 已用于外键约束，
-- 故乐观锁字段使用 V26。</p>
-- =============================================================

-- pms_project：项目主表，并发编辑风险高
ALTER TABLE `pms_project`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';

-- pms_asset：资产实例，分配/调拨/状态变更并发场景
ALTER TABLE `pms_asset`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';

-- pms_impl_task：实施任务，多方协作更新进度
ALTER TABLE `pms_impl_task`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';

-- pms_settlement：结算单，财务敏感数据需防并发覆盖
ALTER TABLE `pms_settlement`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';

-- pms_change_request：变更请求，CCB 审批流程需保证一致性
ALTER TABLE `pms_change_request`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';
