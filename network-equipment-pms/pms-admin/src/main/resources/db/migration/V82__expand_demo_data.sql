-- =============================================================
-- V82__expand_demo_data.sql
-- Phase 8.2 — 演示数据全面补齐
--
-- 目标（基于 §6.10 调研报告）：
--   1. 主子项目层级 ≥5 组，覆盖 2/3/4 层级深度
--   2. 每个核心功能模块 ≥10 条数据，状态枚举全覆盖
--   3. Phase 1-7 新表状态补齐（项目阶段 4 态 / 任务 6 态 / 交付件 7 态 /
--      基线快照 3 态 / 审批 5 态 / 任务依赖 4 类型 / 项目模板 3 态）
--   4. 网络割接 8 状态全覆盖
--
-- ID 规划（避开 V61 的 1~10、V77 的 1001/5001/5002/8001~8003/2001/2002/7001/9001/12001/30001/7001）：
--   pms_project_template        : 2~10
--   pms_project_template_version: 2~10
--   pms_project                 : 2001~2016（5 组主子结构）
--   pms_project_phase           : 5010~5060
--   pms_project_member          : 7010~7090
--   pms_impl_task               : 8010~8090
--   pms_task_checklist          : 9010~9050
--   pms_task_comment            : 1~10
--   pms_task_activity           : 1~10
--   pms_task_dependency         : 10~30
--   pms_deliverable             : 2010~2050
--   pms_deliverable_version     : 12010~12050
--   pms_deliverable_signature   : 30010~30050
--   pms_deliverable_reference   : 1~10
--   pms_baseline_snapshot       : 7010~7050
--   pms_approval_record         : 9010~9050
--   pms_approval_node           : 10~60
--   pms_approval_history        : 10~60
--   pms_approval_field_permission: 10~30
--   demo_network_cutover        : 补 8 状态（编号 CO20260720xxx）
--
-- 兼容性：
--   - pms_project / pms_impl_task / pms_deliverable.create_by 为 VARCHAR(64)，取 'admin'
--   - Phase 1-7 新表 create_by 为 BIGINT，取 1（admin 用户 ID）
--   - 所有 INSERT 使用 INSERT IGNORE 或 NOT EXISTS 保证可重复执行
-- =============================================================

-- =============================================================
-- 1. 项目模板补齐（10 条，覆盖 DRAFT/PUBLISHED/DEPRECATED 三状态）
-- =============================================================
INSERT IGNORE INTO `pms_project_template`
    (`id`, `template_code`, `template_name`, `category`, `description`, `status`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(2,  'TPL-IMPL-STD-V2',  '网络设备实施标准模板 V2',     'IMPLEMENT',   '升级版实施模板，含 5 阶段 20 任务', 'PUBLISHED',
     1, NOW() - INTERVAL 60 DAY, 1, NOW() - INTERVAL 30 DAY, 0, 0),
(3,  'TPL-DC-BUILD',     '数据中心建设模板',             'IMPLEMENT',   '新建数据中心网络规划与部署', 'PUBLISHED',
     1, NOW() - INTERVAL 90 DAY, 1, NOW() - INTERVAL 60 DAY, 0, 0),
(4,  'TPL-SEC-AUDIT',    '安全设备部署与等保整改模板',   'IMPLEMENT',   '防火墙/IDS/IPS 部署与等保三级整改', 'PUBLISHED',
     1, NOW() - INTERVAL 120 DAY, 1, NOW() - INTERVAL 80 DAY, 0, 0),
(5,  'TPL-MAINT-ROUTINE','日常运维巡检模板',             'MAINTENANCE', '月度巡检与设备健康检查', 'PUBLISHED',
     1, NOW() - INTERVAL 100 DAY, 1, NOW() - INTERVAL 50 DAY, 0, 0),
(6,  'TPL-CONSULT-AUDIT','网络架构咨询评估模板',         'CONSULTING',  '网络架构评审与优化建议', 'PUBLISHED',
     1, NOW() - INTERVAL 80 DAY, 1, NOW() - INTERVAL 40 DAY, 0, 0),
(7,  'TPL-MIGRATION',    '数据中心迁移模板',             'IMPLEMENT',   '数据中心整体迁移方法论', 'PUBLISHED',
     1, NOW() - INTERVAL 70 DAY, 1, NOW() - INTERVAL 35 DAY, 0, 0),
(8,  'TPL-CORE-UPGRADE', '核心网升级模板（草稿）',       'IMPLEMENT',   '5G 核心网升级方案，待完善', 'DRAFT',
     1, NOW() - INTERVAL 15 DAY, 1, NOW() - INTERVAL 5 DAY, 0, 0),
(9,  'TPL-LEGACY-V1',    '旧版网络实施模板（已弃用）',   'IMPLEMENT',   '已被 TPL-IMPL-STD-V2 取代', 'DEPRECATED',
     1, NOW() - INTERVAL 365 DAY, 1, NOW() - INTERVAL 100 DAY, 0, 0),
(10, 'TPL-WLAN-BUILD',   '无线网络建设模板',             'IMPLEMENT',   '企业级 WLAN 规划与部署', 'DRAFT',
     1, NOW() - INTERVAL 10 DAY, 1, NOW() - INTERVAL 3 DAY, 0, 0);

INSERT IGNORE INTO `pms_project_template_version`
    (`id`, `template_id`, `version`, `snapshot_json`, `change_log`, `status`,
     `published_at`, `published_by`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version_lock`)
VALUES
(2, 2, 'v2.0.0', JSON_OBJECT('phases', 5, 'tasks', 20, 'deliverables', 8), '升级版发布', 'PUBLISHED',
     NOW() - INTERVAL 30 DAY, 1, 1, NOW() - INTERVAL 60 DAY, 1, NOW() - INTERVAL 30 DAY, 0, 0),
(3, 3, 'v1.0.0', JSON_OBJECT('phases', 4, 'tasks', 15, 'deliverables', 6), '初始版本', 'PUBLISHED',
     NOW() - INTERVAL 60 DAY, 1, 1, NOW() - INTERVAL 90 DAY, 1, NOW() - INTERVAL 60 DAY, 0, 0),
(4, 4, 'v1.0.0', JSON_OBJECT('phases', 3, 'tasks', 12, 'deliverables', 5), '初始版本', 'PUBLISHED',
     NOW() - INTERVAL 80 DAY, 1, 1, NOW() - INTERVAL 120 DAY, 1, NOW() - INTERVAL 80 DAY, 0, 0),
(5, 5, 'v1.0.0', JSON_OBJECT('phases', 2, 'tasks', 8,  'deliverables', 3), '初始版本', 'PUBLISHED',
     NOW() - INTERVAL 50 DAY, 1, 1, NOW() - INTERVAL 100 DAY, 1, NOW() - INTERVAL 50 DAY, 0, 0),
(6, 6, 'v1.0.0', JSON_OBJECT('phases', 3, 'tasks', 10, 'deliverables', 4), '初始版本', 'PUBLISHED',
     NOW() - INTERVAL 40 DAY, 1, 1, NOW() - INTERVAL 80 DAY, 1, NOW() - INTERVAL 40 DAY, 0, 0),
(7, 7, 'v1.0.0', JSON_OBJECT('phases', 4, 'tasks', 14, 'deliverables', 7), '初始版本', 'PUBLISHED',
     NOW() - INTERVAL 35 DAY, 1, 1, NOW() - INTERVAL 70 DAY, 1, NOW() - INTERVAL 35 DAY, 0, 0),
(8, 8, 'v0.1.0', JSON_OBJECT('phases', 2, 'tasks', 6,  'deliverables', 2), '草稿版本', 'DRAFT',
     NULL, NULL, 1, NOW() - INTERVAL 15 DAY, 1, NOW() - INTERVAL 5 DAY, 0, 0),
(9, 9, 'v1.0.0', JSON_OBJECT('phases', 4, 'tasks', 12, 'deliverables', 5), '旧版已归档', 'ARCHIVED',
     NOW() - INTERVAL 100 DAY, 1, 1, NOW() - INTERVAL 365 DAY, 1, NOW() - INTERVAL 100 DAY, 0, 0),
(10, 10, 'v0.1.0', JSON_OBJECT('phases', 2, 'tasks', 6,  'deliverables', 3), '草稿版本', 'DRAFT',
     NULL, NULL, 1, NOW() - INTERVAL 10 DAY, 1, NOW() - INTERVAL 3 DAY, 0, 0);

-- =============================================================
-- 2. 主子项目层级（5 组，覆盖 2/3/4 层级深度）
-- =============================================================
--   组 1（2 层）：   2001 ──┬─ 2002
--                        └─ 2003
--   组 2（3 层）：   2004 ── 2005 ── 2006
--   组 3（2 层）：   2007 ── 2008
--   组 4（2 层多子）：2009 ──┬─ 2010
--                        ├─ 2011
--                        └─ 2012
--   组 5（4 层）：   2013 ── 2014 ── 2015 ── 2016
-- =============================================================
INSERT IGNORE INTO `pms_project`
    (`id`, `project_code`, `project_name`, `project_type`, `status`,
     `customer_name`, `customer_contact`, `customer_phone`, `contract_no`, `contract_amount`,
     `plan_start_date`, `plan_end_date`, `actual_start_date`, `actual_end_date`,
     `project_manager_id`, `project_manager_name`, `description`, `progress`, `priority`,
     `parent_project_id`, `project_path`, `depth`, `weight`,
     `template_id`, `template_version`, `current_phase_id`,
     `project_objective`, `project_scope`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
-- 组 1：XX 银行多层级网络改造总集项目（2 层）
(2001, 'IMPL-2026-101', 'XX 银行多层级网络改造总集项目',     'NETWORK_DEVICE', 'IN_PROGRESS',
     'XX 银行总行', '客户总接口', '13900000010', 'HT-2026-101', 5000000.00,
     '2026-01-01', '2026-12-31', '2026-01-05', NULL,
     1, 'Administrator', '总集项目，含省行核心改造与分支网点设备替换两个子项目', 55, 'HIGH',
     NULL, '/2001/', 0, 1.00,
     2, 'v2.0.0', NULL,
     '完成全行网络架构升级', '总行 + 全国分支网点',
     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2002, 'IMPL-2026-101-01', 'XX 银行省行核心网络改造',         'NETWORK_DEVICE', 'IN_PROGRESS',
     'XX 银行省行', '省行接口人', '13900000011', 'HT-2026-101-01', 2800000.00,
     '2026-01-15', '2026-09-30', '2026-01-20', NULL,
     2, '张明', '子项目：省行核心路由器/交换机/防火墙整体替换', 65, 'HIGH',
     2001, '/2001/2002/', 1, 0.56,
     2, 'v2.0.0', NULL,
     '省行核心网络设备升级', '省行数据中心 + 5 个核心机房',
     'admin', NOW() - INTERVAL 195 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0),
(2003, 'IMPL-2026-101-02', 'XX 银行分支网点设备替换',         'NETWORK_DEVICE', 'IN_PROGRESS',
     'XX 银行分行', '分行接口人', '13900000012', 'HT-2026-101-02', 2200000.00,
     '2026-03-01', '2026-11-30', '2026-03-05', NULL,
     3, '刘伟', '子项目：全国 200 个分支网点设备替换', 45, 'NORMAL',
     2001, '/2001/2003/', 1, 0.44,
     2, 'v2.0.0', NULL,
     '分支网点设备国产化', '200 个分支网点',
     'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
-- 组 2：YY 集团数据中心建设总集（3 层）
(2004, 'IMPL-2026-102', 'YY 集团数据中心建设总集项目',       'DATACENTER',     'IN_PROGRESS',
     'YY 集团', '集团接口', '13900000020', 'HT-2026-102', 8000000.00,
     '2026-02-01', '2027-01-31', '2026-02-05', NULL,
     1, 'Administrator', '总集项目，含一期机房建设子项目，下挂网络设备部署孙项目', 40, 'HIGH',
     NULL, '/2004/', 0, 1.00,
     3, 'v1.0.0', NULL,
     '新建集团总部数据中心', '总部园区',
     'admin', NOW() - INTERVAL 170 DAY, 'admin', NOW() - INTERVAL 4 DAY, 0),
(2005, 'IMPL-2026-102-01', '一期机房建设',                   'DATACENTER',     'IN_PROGRESS',
     'YY 集团', '一期接口', '13900000021', 'HT-2026-102-01', 4500000.00,
     '2026-02-15', '2026-10-31', '2026-02-20', NULL,
     4, '赵琳', '子项目：一期机房基础设施 + 网络部署', 50, 'HIGH',
     2004, '/2004/2005/', 1, 0.60,
     3, 'v1.0.0', NULL,
     '一期机房全功能交付', 'A 栋机房',
     'admin', NOW() - INTERVAL 165 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0),
(2006, 'IMPL-2026-102-01-01', '网络设备部署',                'NETWORK_DEVICE', 'INITIAL_ACCEPTANCE',
     'YY 集团', '网络接口', '13900000022', 'HT-2026-102-01-01', 1800000.00,
     '2026-04-01', '2026-08-31', '2026-04-05', '2026-08-25',
     5, '孙磊', '孙项目：一期机房网络设备部署与初验', 95, 'NORMAL',
     2005, '/2004/2005/2006/', 2, 0.40,
     2, 'v2.0.0', NULL,
     '网络设备部署并通过初验', 'A 栋机房 1-3 层',
     'admin', NOW() - INTERVAL 130 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
-- 组 3：ZZ 证券核心系统迁移（2 层）
(2007, 'IMPL-2026-103', 'ZZ 证券核心系统迁移项目',           'NETWORK_DEVICE', 'APPROVED',
     'ZZ 证券', '客户接口', '13900000030', 'HT-2026-103', 3500000.00,
     '2026-05-01', '2027-02-28', NULL, NULL,
     2, '张明', '总集项目，含网络设备迁移子项目', 5, 'HIGH',
     NULL, '/2007/', 0, 1.00,
     7, 'v1.0.0', NULL,
     '核心交易系统迁移', '总部 + 两地灾备',
     'admin', NOW() - INTERVAL 90 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0),
(2008, 'IMPL-2026-103-01', '网络设备迁移',                   'NETWORK_DEVICE', 'PENDING',
     'ZZ 证券', '网络接口', '13900000031', 'HT-2026-103-01', 1200000.00,
     '2026-06-01', '2026-12-31', NULL, NULL,
     3, '刘伟', '子项目：交易网络设备迁移至新数据中心', 0, 'HIGH',
     2007, '/2007/2008/', 1, 0.34,
     7, 'v1.0.0', NULL,
     '交易网络割接迁移', '两地三中心',
     'admin', NOW() - INTERVAL 85 DAY, 'admin', NOW() - INTERVAL 8 DAY, 0),
-- 组 4：国网集团广域网升级（2 层，3 子项目）
(2009, 'IMPL-2026-104', '国网集团广域网升级项目',            'NETWORK_DEVICE', 'IN_PROGRESS',
     '国网集团', '集团接口', '13900000040', 'HT-2026-104', 6800000.00,
     '2026-01-15', '2026-12-31', '2026-01-20', NULL,
     1, 'Administrator', '总集项目，含华东/华南/华北 3 个区域子项目', 60, 'HIGH',
     NULL, '/2009/', 0, 1.00,
     2, 'v2.0.0', NULL,
     '集团广域网整体升级', '全国 3 大区域',
     'admin', NOW() - INTERVAL 185 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0),
(2010, 'IMPL-2026-104-01', '华东区域广域网升级',             'NETWORK_DEVICE', 'IN_PROGRESS',
     '国网华东', '华东接口', '13900000041', 'HT-2026-104-01', 2200000.00,
     '2026-02-01', '2026-10-31', '2026-02-05', NULL,
     4, '赵琳', '子项目：华东区域骨干网升级', 70, 'HIGH',
     2009, '/2009/2010/', 1, 0.33,
     2, 'v2.0.0', NULL,
     '华东骨干网升级', '华东 5 省',
     'admin', NOW() - INTERVAL 165 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
(2011, 'IMPL-2026-104-02', '华南区域广域网升级',             'NETWORK_DEVICE', 'FINAL_ACCEPTANCE',
     '国网华南', '华南接口', '13900000042', 'HT-2026-104-02', 2300000.00,
     '2026-02-01', '2026-09-15', '2026-02-05', '2026-09-10',
     5, '孙磊', '子项目：华南区域骨干网升级', 95, 'NORMAL',
     2009, '/2009/2011/', 1, 0.34,
     2, 'v2.0.0', NULL,
     '华南骨干网升级', '华南 4 省',
     'admin', NOW() - INTERVAL 165 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2012, 'IMPL-2026-104-03', '华北区域广域网升级',             'NETWORK_DEVICE', 'COMPLETED',
     '国网华北', '华北接口', '13900000043', 'HT-2026-104-03', 2300000.00,
     '2026-02-01', '2026-08-31', '2026-02-05', '2026-08-25',
     6, '吴婷', '子项目：华北区域骨干网升级，已验收', 100, 'NORMAL',
     2009, '/2009/2012/', 1, 0.33,
     2, 'v2.0.0', NULL,
     '华北骨干网升级', '华北 5 省',
     'admin', NOW() - INTERVAL 165 DAY, 'admin', NOW() - INTERVAL 1 DAY, 0),
-- 组 5：中国移动 5G 核心网建设（4 层，最深）
(2013, 'IMPL-2026-105', '中国移动 5G 核心网建设项目',       'NETWORK_DEVICE', 'IN_PROGRESS',
     '中国移动', '集团接口', '13900000050', 'HT-2026-105', 12000000.00,
     '2026-01-01', '2027-06-30', '2026-01-05', NULL,
     1, 'Administrator', '总集项目，含一期核心网建设，下挂设备安装调测与端到端测试', 35, 'HIGH',
     NULL, '/2013/', 0, 1.00,
     8, 'v0.1.0', NULL,
     '5G 核心网全国部署', '全国 31 省',
     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2014, 'IMPL-2026-105-01', '一期核心网建设',                'NETWORK_DEVICE', 'IN_PROGRESS',
     '中国移动', '一期接口', '13900000051', 'HT-2026-105-01', 6000000.00,
     '2026-02-01', '2026-12-31', '2026-02-05', NULL,
     2, '张明', '子项目：一期核心网硬件部署', 50, 'HIGH',
     2013, '/2013/2014/', 1, 0.50,
     8, 'v0.1.0', NULL,
     '一期核心网硬件就绪', '北京/上海/广州 3 大核心',
     'admin', NOW() - INTERVAL 170 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0),
(2015, 'IMPL-2026-105-01-01', '设备安装调测',               'NETWORK_DEVICE', 'IN_PROGRESS',
     '中国移动', '调测接口', '13900000052', 'HT-2026-105-01-01', 3000000.00,
     '2026-03-01', '2026-10-31', '2026-03-05', NULL,
     3, '刘伟', '孙项目：核心网设备安装与单机调测', 60, 'NORMAL',
     2014, '/2013/2014/2015/', 2, 0.50,
     2, 'v2.0.0', NULL,
     '设备安装调测完成', '3 大核心节点',
     'admin', NOW() - INTERVAL 140 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
(2016, 'IMPL-2026-105-01-01-01', '端到端测试',              'NETWORK_DEVICE', 'PENDING',
     '中国移动', '测试接口', '13900000053', 'HT-2026-105-01-01-01', 1500000.00,
     '2026-08-01', '2026-12-31', NULL, NULL,
     4, '赵琳', '曾孙项目：端到端业务测试与验证', 0, 'NORMAL',
     2015, '/2013/2014/2015/2016/', 3, 0.50,
     2, 'v2.0.0', NULL,
     '端到端业务验证通过', '3 大核心 + 31 省接入',
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 1 DAY, 0);

-- =============================================================
-- 3. 项目阶段补齐（覆盖 NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED 全 4 态）
-- =============================================================
INSERT IGNORE INTO `pms_project_phase`
    (`id`, `project_id`, `template_phase_id`, `phase_name`, `phase_code`, `sort_order`,
     `entry_criteria`, `exit_criteria`, `status`,
     `planned_start_date`, `planned_end_date`, `actual_start_date`, `actual_end_date`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
-- 项目 2001 全 4 态
(5010, 2001, NULL, '准备阶段', 'PREPARE',     1, NULL, NULL, 'COMPLETED',
     '2026-01-01', '2026-01-31', '2026-01-05', '2026-01-30', 1, NOW(), 1, NOW(), 0, 0),
(5011, 2001, NULL, '规划阶段', 'PLAN',        2, NULL, NULL, 'IN_PROGRESS',
     '2026-02-01', '2026-04-30', '2026-02-05', NULL, 1, NOW(), 1, NOW(), 0, 0),
(5012, 2001, NULL, '设计阶段', 'DESIGN',      3, NULL, NULL, 'NOT_STARTED',
     '2026-05-01', '2026-07-31', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
(5013, 2001, NULL, '可选调研', 'PREPARE',      4, NULL, NULL, 'SKIPPED',
     '2026-08-01', '2026-08-15', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2002 补充阶段
(5014, 2002, NULL, '准备阶段', 'PREPARE',     1, NULL, NULL, 'COMPLETED',
     '2026-01-15', '2026-02-15', '2026-01-20', '2026-02-10', 1, NOW(), 1, NOW(), 0, 0),
(5015, 2002, NULL, '实施阶段', 'IMPLEMENT',   2, NULL, NULL, 'IN_PROGRESS',
     '2026-02-16', '2026-08-31', '2026-02-20', NULL, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2006 阶段
(5016, 2006, NULL, '部署阶段', 'IMPLEMENT',   1, NULL, NULL, 'COMPLETED',
     '2026-04-01', '2026-07-31', '2026-04-05', '2026-07-25', 1, NOW(), 1, NOW(), 0, 0),
(5017, 2006, NULL, '验收阶段', 'OPERATE',     2, NULL, NULL, 'IN_PROGRESS',
     '2026-08-01', '2026-08-31', '2026-08-01', NULL, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2010 阶段
(5018, 2010, NULL, '准备阶段', 'PREPARE',     1, NULL, NULL, 'COMPLETED',
     '2026-02-01', '2026-03-15', '2026-02-05', '2026-03-10', 1, NOW(), 1, NOW(), 0, 0),
(5019, 2010, NULL, '实施阶段', 'IMPLEMENT',   2, NULL, NULL, 'IN_PROGRESS',
     '2026-03-16', '2026-09-30', '2026-03-20', NULL, 1, NOW(), 1, NOW(), 0, 0),
(5020, 2010, NULL, '未启动阶段', 'OPERATE',   3, NULL, NULL, 'NOT_STARTED',
     '2026-10-01', '2026-10-31', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2012 阶段（COMPLETED 项目）
(5021, 2012, NULL, '准备阶段', 'PREPARE',     1, NULL, NULL, 'COMPLETED',
     '2026-02-01', '2026-03-15', '2026-02-05', '2026-03-10', 1, NOW(), 1, NOW(), 0, 0),
(5022, 2012, NULL, '实施阶段', 'IMPLEMENT',   2, NULL, NULL, 'COMPLETED',
     '2026-03-16', '2026-07-31', '2026-03-20', '2026-07-25', 1, NOW(), 1, NOW(), 0, 0),
(5023, 2012, NULL, '跳过阶段', 'PREPARE',      3, NULL, NULL, 'SKIPPED',
     '2026-08-01', '2026-08-10', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
(5024, 2012, NULL, '验收阶段', 'OPERATE',     4, NULL, NULL, 'COMPLETED',
     '2026-08-11', '2026-08-31', '2026-08-11', '2026-08-25', 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2016 阶段（PENDING 项目）
(5025, 2016, NULL, '准备阶段', 'PREPARE',     1, NULL, NULL, 'NOT_STARTED',
     '2026-08-01', '2026-08-31', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
(5026, 2016, NULL, '实施阶段', 'IMPLEMENT',   2, NULL, NULL, 'NOT_STARTED',
     '2026-09-01', '2026-11-30', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
(5027, 2016, NULL, '跳过阶段', 'PREPARE',      3, NULL, NULL, 'SKIPPED',
     '2026-12-01', '2026-12-10', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2013 阶段
(5028, 2013, NULL, '准备阶段', 'PREPARE',     1, NULL, NULL, 'COMPLETED',
     '2026-01-01', '2026-02-28', '2026-01-05', '2026-02-25', 1, NOW(), 1, NOW(), 0, 0),
(5029, 2013, NULL, '一期建设', 'PLAN',        2, NULL, NULL, 'IN_PROGRESS',
     '2026-03-01', '2026-12-31', '2026-03-05', NULL, 1, NOW(), 1, NOW(), 0, 0),
(5030, 2013, NULL, '二期建设', 'DESIGN',      3, NULL, NULL, 'NOT_STARTED',
     '2027-01-01', '2027-06-30', NULL, NULL, 1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 4. 项目成员补齐（每个主子项目配 PM/MEMBER/APPROVER）
-- =============================================================
INSERT IGNORE INTO `pms_project_member`
    (`id`, `project_id`, `user_id`, `user_name`, `role`, `join_date`, `leave_date`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
-- 注：pms_project_member 由 V2 建表，create_by/update_by 为 VARCHAR(64)，统一使用 'admin'
(7010, 2001, 1,   'Administrator', 'PROJECT_MANAGER', '2026-01-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7011, 2001, 2,   '张明',           'PROJECT_MEMBER',  '2026-01-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7012, 2001, 200, '审批员A',        'APPROVER',        '2026-01-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7013, 2002, 2,   '张明',           'PROJECT_MANAGER', '2026-01-15', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7014, 2002, 3,   '刘伟',           'PROJECT_MEMBER',  '2026-01-15', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7015, 2003, 3,   '刘伟',           'PROJECT_MANAGER', '2026-03-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7016, 2003, 4,   '赵琳',           'PROJECT_MEMBER',  '2026-03-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7017, 2004, 1,   'Administrator', 'PROJECT_MANAGER', '2026-02-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7018, 2004, 4,   '赵琳',           'PROJECT_MEMBER',  '2026-02-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7019, 2005, 4,   '赵琳',           'PROJECT_MANAGER', '2026-02-15', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7020, 2006, 5,   '孙磊',           'PROJECT_MANAGER', '2026-04-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7021, 2007, 2,   '张明',           'PROJECT_MANAGER', '2026-05-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7022, 2008, 3,   '刘伟',           'PROJECT_MANAGER', '2026-06-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7023, 2009, 1,   'Administrator', 'PROJECT_MANAGER', '2026-01-15', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7024, 2010, 4,   '赵琳',           'PROJECT_MANAGER', '2026-02-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7025, 2011, 5,   '孙磊',           'PROJECT_MANAGER', '2026-02-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7026, 2012, 6,   '吴婷',           'PROJECT_MANAGER', '2026-02-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7027, 2013, 1,   'Administrator', 'PROJECT_MANAGER', '2026-01-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7028, 2014, 2,   '张明',           'PROJECT_MANAGER', '2026-02-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7029, 2015, 3,   '刘伟',           'PROJECT_MANAGER', '2026-03-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7030, 2016, 4,   '赵琳',           'PROJECT_MANAGER', '2026-08-01', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
-- V77 项目 1001 的项目成员扩展
(7031, 1001, 3,   '刘伟',           'PROJECT_MEMBER',  '2026-07-05', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
(7032, 1001, 4,   '赵琳',           'VIEWER',          '2026-07-10', NULL, 'admin', NOW(), 'admin', NOW(), 0, 0),
-- 已 leave 的成员示例
(7033, 2002, 5,   '孙磊',           'PROJECT_MEMBER',  '2026-01-15', '2026-05-31', 'admin', NOW(), 'admin', NOW(), 0, 0),
(7034, 2009, 2,   '张明',           'PROJECT_MEMBER',  '2026-01-15', '2026-06-30', 'admin', NOW(), 'admin', NOW(), 0, 0);

-- =============================================================
-- 5. 任务补齐（覆盖 PENDING/ACCEPTED/IN_PROGRESS/COMPLETED/CONFIRMED/REJECTED 全 6 态）
-- =============================================================
INSERT IGNORE INTO `pms_impl_task`
    (`id`, `project_id`, `milestone_id`, `task_name`, `task_type`,
     `agent_id`, `engineer_id`, `engineer_name`,
     `plan_start_date`, `plan_end_date`, `actual_start_date`, `actual_end_date`,
     `status`, `progress`, `work_description`,
     `parent_task_id`, `task_path`, `depth`, `priority`,
     `actual_hours`, `remaining_hours`, `phase_id`, `task_weight`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
-- 项目 2001 任务：覆盖 6 态
(8010, 2001, NULL, '总集项目启动会',       'OEM',
     NULL, 1, 'Administrator',
     '2026-01-05', '2026-01-10', '2026-01-05', '2026-01-08',
     'COMPLETED', 100, '总集项目启动会完成，含子项目 PM 参与',
     NULL, '/8010/', 0, 'HIGH',
     8.00, 0.00, 5010, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8011, 2001, NULL, '总体方案设计',         'OEM',
     NULL, 1, 'Administrator',
     '2026-01-15', '2026-02-15', '2026-01-15', NULL,
     'IN_PROGRESS', 70, '总体方案设计 70% 完成，待客户确认',
     8010, '/8010/8011/', 1, 'HIGH',
     40.00, 16.00, 5011, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8012, 2001, NULL, '客户需求复核',         'OEM',
     NULL, 2, '张明',
     '2026-02-20', '2026-03-10', NULL, NULL,
     'PENDING', 0, '待客户提交最新需求清单后启动',
     8010, '/8010/8012/', 1, 'MEDIUM',
     0.00, 16.00, 5011, 0.50,
     'admin', NOW(), 'admin', NOW(), 0),
(8013, 2001, NULL, '子项目启动会',         'OEM',
     NULL, 2, '张明',
     '2026-02-01', '2026-02-05', '2026-02-01', '2026-02-05',
     'COMPLETED', 100, '子项目启动会已确认完成',
     8010, '/8010/8013/', 1, 'HIGH',
     4.00, 0.00, 5010, 0.50,
     'admin', NOW(), 'admin', NOW(), 0),
(8014, 2001, NULL, '历史数据迁移评估',     'AGENT',
     NULL, 3, '刘伟',
     '2026-03-01', '2026-03-15', '2026-03-01', NULL,
     'ACCEPTED', 10, '已接受任务，开始评估历史数据',
     8011, '/8010/8011/8014/', 2, 'MEDIUM',
     4.00, 32.00, 5011, 0.30,
     'admin', NOW(), 'admin', NOW(), 0),
(8015, 2001, NULL, '网络方案评审',         'OEM',
     NULL, 1, 'Administrator',
     '2026-02-25', '2026-03-05', NULL, NULL,
     'REJECTED', 0, '客户驳回：方案预算超标，需重新设计',
     8011, '/8010/8011/8015/', 2, 'HIGH',
     0.00, 8.00, 5011, 0.30,
     'admin', NOW(), 'admin', NOW(), 0),
-- 项目 2002 任务
(8016, 2002, NULL, '省行核心网络勘察',     'AGENT',
     NULL, 2, '张明',
     '2026-02-01', '2026-02-28', '2026-02-05', '2026-02-25',
     'COMPLETED', 100, '省行 5 个核心机房勘察完成',
     NULL, '/8016/', 0, 'HIGH',
     40.00, 0.00, 5014, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8017, 2002, NULL, '核心交换机部署',       'AGENT',
     NULL, 3, '刘伟',
     '2026-03-01', '2026-05-31', '2026-03-05', NULL,
     'IN_PROGRESS', 50, '已完成 3 台核心交换机部署，剩余 2 台',
     8016, '/8016/8017/', 1, 'HIGH',
     60.00, 60.00, 5015, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8018, 2002, NULL, '防火墙策略配置',       'AGENT',
     NULL, 4, '赵琳',
     '2026-06-01', '2026-07-31', NULL, NULL,
     'PENDING', 0, '等待核心交换机部署完成',
     8016, '/8016/8018/', 1, 'MEDIUM',
     0.00, 80.00, 5015, 0.80,
     'admin', NOW(), 'admin', NOW(), 0),
-- 项目 2006 任务（INITIAL_ACCEPTANCE）
(8019, 2006, NULL, '设备到货验收',         'AGENT',
     NULL, 5, '孙磊',
     '2026-04-05', '2026-04-20', '2026-04-05', '2026-04-18',
     'COMPLETED', 100, '48 台设备到货验收完成',
     NULL, '/8019/', 0, 'HIGH',
     24.00, 0.00, 5016, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8020, 2006, NULL, '机架安装',             'AGENT',
     NULL, 5, '孙磊',
     '2026-04-21', '2026-05-31', '2026-04-21', '2026-05-28',
     'COMPLETED', 100, '12 个机架安装完成并确认',
     8019, '/8019/8020/', 1, 'HIGH',
     80.00, 0.00, 5016, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8021, 2006, NULL, '设备加电测试',         'OEM',
     NULL, 5, '孙磊',
     '2026-06-01', '2026-07-15', '2026-06-01', NULL,
     'IN_PROGRESS', 80, '10 台设备加电测试中',
     8020, '/8019/8020/8021/', 2, 'CRITICAL',
     64.00, 16.00, 5016, 0.50,
     'admin', NOW(), 'admin', NOW(), 0),
(8022, 2006, NULL, '初验测试报告',         'OEM',
     NULL, 5, '孙磊',
     '2026-08-01', '2026-08-25', '2026-08-01', '2026-08-25',
     'COMPLETED', 100, '初验测试报告已通过客户审核',
     8019, '/8019/8022/', 1, 'HIGH',
     16.00, 0.00, 5017, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
-- 项目 2009 任务
(8023, 2009, NULL, '广域网方案设计',       'OEM',
     NULL, 1, 'Administrator',
     '2026-01-20', '2026-03-15', '2026-01-20', '2026-03-10',
     'COMPLETED', 100, '广域网整体方案设计完成',
     NULL, '/8023/', 0, 'HIGH',
     80.00, 0.00, 5010, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8024, 2009, NULL, '区域路由协议设计',     'OEM',
     NULL, 4, '赵琳',
     '2026-03-16', '2026-04-30', '2026-03-20', NULL,
     'IN_PROGRESS', 60, 'OSPF/BGP 设计进行中',
     8023, '/8023/8024/', 1, 'HIGH',
     32.00, 24.00, 5011, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8025, 2009, NULL, '区域间互联测试',       'AGENT',
     NULL, 5, '孙磊',
     '2026-08-01', '2026-09-15', NULL, NULL,
     'PENDING', 0, '待区域路由协议设计完成',
     8024, '/8023/8024/8025/', 2, 'MEDIUM',
     0.00, 40.00, 5011, 0.50,
     'admin', NOW(), 'admin', NOW(), 0),
-- 项目 2013 任务（4 层项目）
(8026, 2013, NULL, '5G 核心网总体设计',    'OEM',
     NULL, 1, 'Administrator',
     '2026-01-05', '2026-02-28', '2026-01-05', '2026-02-25',
     'COMPLETED', 100, '5G 核心网总体架构设计完成',
     NULL, '/8026/', 0, 'CRITICAL',
     120.00, 0.00, 5028, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8027, 2013, NULL, '一期设备采购评估',     'OEM',
     NULL, 2, '张明',
     '2026-03-01', '2026-04-30', '2026-03-05', NULL,
     'IN_PROGRESS', 70, '已完成 70% 设备评估',
     8026, '/8026/8027/', 1, 'HIGH',
     56.00, 24.00, 5029, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8028, 2015, NULL, '核心设备单机调测',     'AGENT',
     NULL, 3, '刘伟',
     '2026-03-05', '2026-06-30', '2026-03-05', NULL,
     'IN_PROGRESS', 50, '5 台核心设备调测中',
     NULL, '/8028/', 0, 'CRITICAL',
     200.00, 200.00, NULL, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8029, 2016, NULL, '端到端测试方案',       'OEM',
     NULL, 4, '赵琳',
     '2026-08-01', '2026-08-31', NULL, NULL,
     'ACCEPTED', 0, '已接受任务，准备启动测试方案设计',
     NULL, '/8029/', 0, 'HIGH',
     0.00, 40.00, 5025, 1.00,
     'admin', NOW(), 'admin', NOW(), 0),
(8030, 2016, NULL, '业务回归测试',         'OEM',
     NULL, 4, '赵琳',
     '2026-11-01', '2026-12-15', NULL, NULL,
     'PENDING', 0, '待端到端测试方案完成',
     8029, '/8029/8030/', 1, 'HIGH',
     0.00, 80.00, 5026, 1.00,
     'admin', NOW(), 'admin', NOW(), 0);

-- =============================================================
-- 6. 任务依赖补齐（覆盖 FS/FF/SS/SF 全 4 类型，≥10 条）
-- =============================================================
INSERT IGNORE INTO `pms_task_dependency`
    (`id`, `project_id`, `predecessor_task_id`, `successor_task_id`, `dependency_type`, `lag_days`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
-- 项目 2001 FS 依赖链
(10, 2001, 8010, 8011, 'FS', 0, 1, NOW(), 1, NOW(), 0, 0),
(11, 2001, 8011, 8012, 'FS', 5, 1, NOW(), 1, NOW(), 0, 0),
(12, 2001, 8011, 8014, 'SS', 0, 1, NOW(), 1, NOW(), 0, 0),
(13, 2001, 8014, 8015, 'FF', 2, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2002 FS/SS 依赖
(14, 2002, 8016, 8017, 'FS', 0, 1, NOW(), 1, NOW(), 0, 0),
(15, 2002, 8017, 8018, 'SS', 7, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2006 依赖链
(16, 2006, 8019, 8020, 'FS', 0, 1, NOW(), 1, NOW(), 0, 0),
(17, 2006, 8020, 8021, 'FS', 1, 1, NOW(), 1, NOW(), 0, 0),
(18, 2006, 8021, 8022, 'FF', 0, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2009 依赖
(19, 2009, 8023, 8024, 'FS', 0, 1, NOW(), 1, NOW(), 0, 0),
(20, 2009, 8024, 8025, 'FS', 5, 1, NOW(), 1, NOW(), 0, 0),
-- 项目 2013 依赖链（4 层项目）
(21, 2013, 8026, 8027, 'FS', 0, 1, NOW(), 1, NOW(), 0, 0),
(22, 2015, 8028, 8029, 'SF', -10, 1, NOW(), 1, NOW(), 0, 0),
(23, 2016, 8029, 8030, 'FS', 0, 1, NOW(), 1, NOW(), 0, 0),
-- V77 项目 1001 补充依赖（FS/SS/FF/SF 全类型演示）
(24, 1001, 8001, 8002, 'SS', 0, 1, NOW(), 1, NOW(), 0, 0),
(25, 1001, 8002, 8003, 'FF', 1, 1, NOW(), 1, NOW(), 0, 0),
(26, 1001, 8003, 8002, 'SF', -3, 1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 7. 任务检查项补齐（≥10 条，覆盖勾选/未勾选状态）
-- =============================================================
INSERT IGNORE INTO `pms_task_checklist`
    (`id`, `task_id`, `title`, `description`, `mandatory`, `checked`, `checked_by`, `checked_at`, `sort_order`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(9010, 8010, '会议室预订',     '预订会议室并发送邀请',     1, 1, 1, NOW() - INTERVAL 200 DAY, 1, 1, NOW(), 1, NOW(), 0, 0),
(9011, 8010, '参会人员签到',   '记录参会人员签到',         1, 1, 1, NOW() - INTERVAL 200 DAY, 2, 1, NOW(), 1, NOW(), 0, 0),
(9012, 8010, '会议纪要发送',   '会议后 24h 内发送纪要',    1, 1, 1, NOW() - INTERVAL 198 DAY, 3, 1, NOW(), 1, NOW(), 0, 0),
(9013, 8011, '架构图绘制',     '使用 Visio 绘制总体架构',  1, 1, 1, NOW() - INTERVAL 100 DAY, 1, 1, NOW(), 1, NOW(), 0, 0),
(9014, 8011, '客户确认',       '客户签字确认方案',         1, 0, NULL, NULL, 2, 1, NOW(), 1, NOW(), 0, 0),
(9015, 8016, '勘察计划制定',   '制定 5 个机房勘察计划',    1, 1, 2, NOW() - INTERVAL 180 DAY, 1, 1, NOW(), 1, NOW(), 0, 0),
(9016, 8016, '勘察工具准备',   '准备测试仪器与工具',       0, 1, 2, NOW() - INTERVAL 180 DAY, 2, 1, NOW(), 1, NOW(), 0, 0),
(9017, 8017, '设备配置模板',   '准备交换机配置模板',       1, 1, 3, NOW() - INTERVAL 120 DAY, 1, 1, NOW(), 1, NOW(), 0, 0),
(9018, 8017, '备份原配置',     '部署前备份原设备配置',     1, 0, NULL, NULL, 2, 1, NOW(), 1, NOW(), 0, 0),
(9019, 8021, '加电检查清单',   '设备加电前完成 8 项检查',  1, 1, 5, NOW() - INTERVAL 50 DAY, 1, 1, NOW(), 1, NOW(), 0, 0),
(9020, 8022, '测试报告模板',   '使用公司标准测试报告模板', 1, 1, 5, NOW() - INTERVAL 25 DAY, 1, 1, NOW(), 1, NOW(), 0, 0),
(9021, 8026, '5G 标准对齐',    '与 3GPP R15 标准对齐',     1, 1, 1, NOW() - INTERVAL 170 DAY, 1, 1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 8. 交付件补齐（覆盖 DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED 全 7 态）
-- =============================================================
INSERT IGNORE INTO `pms_deliverable`
    (`id`, `project_id`, `deliverable_name`, `deliverable_type`, `file_path`, `status`,
     `phase_id`, `current_version`, `mandatory`, `approver_role`, `published_at`, `archived_at`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
-- 项目 2001 交付件：覆盖全 7 态
(2010, 2001, '总体实施方案',       'DOCUMENT', '/files/impl-plan-2001-v1.docx', 'PUBLISHED',
     5011, 1, 1, 'TECH_LEAD',        NOW() - INTERVAL 90 DAY, NULL,
     'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 90 DAY, 0),
(2011, 2001, '客户需求确认书',     'DOCUMENT', '/files/req-confirm-2001-v1.docx', 'SIGNED',
     5011, 1, 1, 'PROJECT_MANAGER',  NOW() - INTERVAL 60 DAY, NULL,
     'admin', NOW() - INTERVAL 80 DAY, 'admin', NOW() - INTERVAL 60 DAY, 0),
(2012, 2001, '网络架构设计文档',   'DOCUMENT', '/files/network-design-2001-v2.docx', 'REVIEWED',
     5011, 2, 1, 'TECH_LEAD',        NULL, NULL,
     'admin', NOW() - INTERVAL 70 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0),
(2013, 2001, '风险评估报告',       'REPORT',   '/files/risk-assess-2001-v1.docx', 'SUBMITTED',
     5011, 1, 0, 'PROJECT_MANAGER',  NULL, NULL,
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2014, 2001, '项目周报模板',       'TEMPLATE', '/files/weekly-report-tpl.docx', 'DRAFT',
     5010, 1, 0, NULL,               NULL, NULL,
     'admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2015, 2001, '启动会会议纪要',     'REPORT',   '/files/kickoff-minutes-2001.docx', 'REFERENCED',
     5010, 1, 0, NULL,               NOW() - INTERVAL 195 DAY, NULL,
     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 195 DAY, 0),
(2016, 2001, '历史方案模板（旧版）', 'TEMPLATE', '/files/legacy-tpl-v0.docx', 'ARCHIVED',
     NULL, 1, 0, NULL,               NOW() - INTERVAL 365 DAY, NOW() - INTERVAL 100 DAY,
     'admin', NOW() - INTERVAL 400 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
-- 项目 2002 交付件
(2017, 2002, '省行核心方案',       'DOCUMENT', '/files/core-plan-2002-v1.docx', 'PUBLISHED',
     5014, 1, 1, 'TECH_LEAD',        NOW() - INTERVAL 150 DAY, NULL,
     'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0),
(2018, 2002, '省行勘察报告',       'REPORT',   '/files/survey-2002-v1.docx', 'REVIEWED',
     5014, 1, 1, 'PROJECT_MANAGER',  NULL, NULL,
     'admin', NOW() - INTERVAL 170 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0),
-- 项目 2006 交付件
(2019, 2006, '设备部署方案',       'DOCUMENT', '/files/deploy-plan-2006-v1.docx', 'PUBLISHED',
     5016, 1, 1, 'TECH_LEAD',        NOW() - INTERVAL 110 DAY, NULL,
     'admin', NOW() - INTERVAL 130 DAY, 'admin', NOW() - INTERVAL 110 DAY, 0),
(2020, 2006, '初验测试报告',       'REPORT',   '/files/acceptance-2006-v1.docx', 'SIGNED',
     5017, 1, 1, 'PROJECT_MANAGER',  NOW() - INTERVAL 5 DAY, NULL,
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2021, 2006, '设备清单',           'DOCUMENT', '/files/device-list-2006.xlsx', 'REFERENCED',
     5016, 1, 0, NULL,               NOW() - INTERVAL 100 DAY, NULL,
     'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
-- 项目 2009 交付件
(2022, 2009, '广域网总体设计',     'DOCUMENT', '/files/wan-design-2009-v1.docx', 'PUBLISHED',
     5010, 1, 1, 'TECH_LEAD',        NOW() - INTERVAL 130 DAY, NULL,
     'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 130 DAY, 0),
(2023, 2009, '区域路由设计',       'DOCUMENT', '/files/routing-2009-v1.docx', 'SUBMITTED',
     5011, 1, 1, 'PROJECT_MANAGER',  NULL, NULL,
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
-- 项目 2013 交付件
(2024, 2013, '5G 核心网架构',      'DOCUMENT', '/files/5g-core-arch-2013-v1.docx', 'PUBLISHED',
     5028, 1, 1, 'TECH_LEAD',        NOW() - INTERVAL 170 DAY, NULL,
     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 170 DAY, 0),
(2025, 2013, '5G 核心网采购评估',  'REPORT',   '/files/procure-eval-2013-v1.docx', 'DRAFT',
     5029, 1, 1, NULL,               NULL, NULL,
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(2026, 2015, '设备调测报告',       'REPORT',   '/files/device-test-2015-v1.docx', 'SUBMITTED',
     NULL, 1, 1, 'TECH_LEAD',        NULL, NULL,
     'admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
(2027, 2016, '端到端测试方案',     'DOCUMENT', '/files/e2e-test-plan-2016-v1.docx', 'DRAFT',
     5025, 1, 1, NULL,               NULL, NULL,
     'admin', NOW() - INTERVAL 5 DAY, 'admin', NOW() - INTERVAL 1 DAY, 0);

-- =============================================================
-- 9. 交付件版本（≥10 条，覆盖 PUBLISHED/DRAFT/ARCHIVED）
-- =============================================================
INSERT IGNORE INTO `pms_deliverable_version`
    (`id`, `deliverable_id`, `version_no`, `file_path`, `file_checksum`, `uploaded_by`, `uploaded_at`,
     `change_log`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version_lock`)
VALUES
(12010, 2010, 1, '/files/impl-plan-2001-v1.docx',     'sha256:a2010v1', 1, NOW() - INTERVAL 120 DAY, '初始版本',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12011, 2010, 2, '/files/impl-plan-2001-v2.docx',     'sha256:a2010v2', 1, NOW() - INTERVAL 90 DAY,  '客户反馈后修订',   'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12012, 2011, 1, '/files/req-confirm-2001-v1.docx',   'sha256:a2011v1', 2, NOW() - INTERVAL 80 DAY,  '初始版本',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12013, 2012, 1, '/files/network-design-2001-v1.docx','sha256:a2012v1', 1, NOW() - INTERVAL 70 DAY,  '初始版本',         'ARCHIVED',  'admin', NOW(), 'admin', NOW(), 0, 0),
(12014, 2012, 2, '/files/network-design-2001-v2.docx','sha256:a2012v2', 1, NOW() - INTERVAL 10 DAY,  '架构调整后修订',   'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12015, 2013, 1, '/files/risk-assess-2001-v1.docx',   'sha256:a2013v1', 2, NOW() - INTERVAL 30 DAY,  '初始版本',         'DRAFT',     'admin', NOW(), 'admin', NOW(), 0, 0),
(12016, 2014, 1, '/files/weekly-report-tpl.docx',     'sha256:a2014v1', 1, NOW() - INTERVAL 20 DAY,  '初始草稿',         'DRAFT',     'admin', NOW(), 'admin', NOW(), 0, 0),
(12017, 2017, 1, '/files/core-plan-2002-v1.docx',     'sha256:a2017v1', 2, NOW() - INTERVAL 180 DAY, '初始版本',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12018, 2019, 1, '/files/deploy-plan-2006-v1.docx',   'sha256:a2019v1', 5, NOW() - INTERVAL 130 DAY, '初始版本',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12019, 2020, 1, '/files/acceptance-2006-v1.docx',    'sha256:a2020v1', 5, NOW() - INTERVAL 30 DAY,  '初验测试报告',     'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12020, 2022, 1, '/files/wan-design-2009-v1.docx',    'sha256:a2022v1', 1, NOW() - INTERVAL 160 DAY, '初始版本',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12021, 2024, 1, '/files/5g-core-arch-2013-v1.docx',  'sha256:a2024v1', 1, NOW() - INTERVAL 200 DAY, '初始版本',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12022, 2024, 2, '/files/5g-core-arch-2013-v2.docx',  'sha256:a2024v2', 1, NOW() - INTERVAL 170 DAY, '优化架构',         'PUBLISHED', 'admin', NOW(), 'admin', NOW(), 0, 0),
(12023, 2025, 1, '/files/procure-eval-2013-v1.docx',  'sha256:a2025v1', 2, NOW() - INTERVAL 30 DAY,  '初始草稿',         'DRAFT',     'admin', NOW(), 'admin', NOW(), 0, 0),
(12024, 2026, 1, '/files/device-test-2015-v1.docx',   'sha256:a2026v1', 3, NOW() - INTERVAL 20 DAY,  '初始版本',         'DRAFT',     'admin', NOW(), 'admin', NOW(), 0, 0),
(12025, 2027, 1, '/files/e2e-test-plan-2016-v1.docx', 'sha256:a2027v1', 4, NOW() - INTERVAL 5 DAY,   '初始草稿',         'DRAFT',     'admin', NOW(), 'admin', NOW(), 0, 0);

-- =============================================================
-- 10. 交付件签名（≥10 条，覆盖 ELECTRONIC/STAMP/DIGITAL）
-- =============================================================
INSERT IGNORE INTO `pms_deliverable_signature`
    (`id`, `deliverable_id`, `version_no`, `signer_id`, `signer_name`, `signer_role`,
     `signature_type`, `signature_data`, `signed_at`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(30010, 2010, 2, 200, '审批员A', 'TECH_LEAD',        'ELECTRONIC', 'cert-fp:7E2A9F0010', NOW() - INTERVAL 90 DAY,  'admin', NOW(), 'admin', NOW(), 0),
(30011, 2011, 1, 1,   'Administrator', 'PROJECT_MANAGER', 'STAMP',      'stamp-id:STAMP-0011', NOW() - INTERVAL 60 DAY,  'admin', NOW(), 'admin', NOW(), 0),
(30012, 2012, 2, 200, '审批员A', 'TECH_LEAD',        'ELECTRONIC', 'cert-fp:7E2A9F0012', NOW() - INTERVAL 10 DAY,  'admin', NOW(), 'admin', NOW(), 0),
(30013, 2017, 1, 200, '审批员A', 'TECH_LEAD',        'DIGITAL',    'pki-sig:PKI-2017-001', NOW() - INTERVAL 150 DAY, 'admin', NOW(), 'admin', NOW(), 0),
(30014, 2019, 1, 200, '审批员A', 'TECH_LEAD',        'ELECTRONIC', 'cert-fp:7E2A9F0019', NOW() - INTERVAL 110 DAY, 'admin', NOW(), 'admin', NOW(), 0),
(30015, 2020, 1, 1,   'Administrator', 'PROJECT_MANAGER', 'STAMP',      'stamp-id:STAMP-0020', NOW() - INTERVAL 5 DAY,   'admin', NOW(), 'admin', NOW(), 0),
(30016, 2022, 1, 200, '审批员A', 'TECH_LEAD',        'DIGITAL',    'pki-sig:PKI-2022-001', NOW() - INTERVAL 130 DAY, 'admin', NOW(), 'admin', NOW(), 0),
(30017, 2024, 2, 200, '审批员A', 'TECH_LEAD',        'ELECTRONIC', 'cert-fp:7E2A9F0024', NOW() - INTERVAL 170 DAY, 'admin', NOW(), 'admin', NOW(), 0),
(30018, 2024, 2, 1,   'Administrator', 'PROJECT_MANAGER', 'STAMP',      'stamp-id:STAMP-0024', NOW() - INTERVAL 170 DAY, 'admin', NOW(), 'admin', NOW(), 0),
(30019, 2021, 1, 0,    '系统自动', 'SYSTEM',           'ELECTRONIC', 'auto-signed:AUTO-21', NOW() - INTERVAL 100 DAY, 'admin', NOW(), 'admin', NOW(), 0);

-- =============================================================
-- 11. 交付件引用关系（≥10 条，演示交付件被引用的场景）
--     表结构由 V75 创建，本节仅 INSERT。V75 语义：
--       source_deliverable_id = 被引用方（源头）
--       target_deliverable_id = 引用方交付件ID（NULL 时引用方非交付件）
--       reference_type        = 引用方业务类型：TASK/PHASE/PROJECT/DELIVERABLE/REPORT
--       referenced_by_id      = 引用方业务ID（NOT NULL）
--       referenced_by_name    = 引用方名称（冗余）
--       create_by/update_by   = VARCHAR(64)
-- =============================================================
INSERT IGNORE INTO `pms_deliverable_reference`
    (`id`, `source_deliverable_id`, `target_deliverable_id`,
     `reference_type`, `referenced_by_id`, `referenced_by_name`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
-- 语义：source=被引用的交付件，target=引用方交付件，reference_type='DELIVERABLE'
(1,  2010, 2011, 'DELIVERABLE', 2011, '客户需求确认书',     'admin', NOW(), 'admin', NOW(), 0),
(2,  2010, 2012, 'DELIVERABLE', 2012, '网络架构设计文档',   'admin', NOW(), 'admin', NOW(), 0),
(3,  2012, 2013, 'DELIVERABLE', 2013, '风险评估报告',       'admin', NOW(), 'admin', NOW(), 0),
(4,  2010, 2017, 'DELIVERABLE', 2017, '省行核心方案',       'admin', NOW(), 'admin', NOW(), 0),
(5,  2017, 2018, 'DELIVERABLE', 2018, '省行勘察报告',       'admin', NOW(), 'admin', NOW(), 0),
(6,  2017, 2019, 'DELIVERABLE', 2019, '设备部署方案',       'admin', NOW(), 'admin', NOW(), 0),
(7,  2019, 2020, 'DELIVERABLE', 2020, '初验测试报告',       'admin', NOW(), 'admin', NOW(), 0),
(8,  2010, 2022, 'DELIVERABLE', 2022, '广域网总体设计',     'admin', NOW(), 'admin', NOW(), 0),
(9,  2010, 2024, 'DELIVERABLE', 2024, '5G 核心网架构',      'admin', NOW(), 'admin', NOW(), 0),
(10, 2024, 2025, 'DELIVERABLE', 2025, '5G 核心网采购评估',  'admin', NOW(), 'admin', NOW(), 0),
(11, 2024, 2026, 'DELIVERABLE', 2026, '设备调测报告',       'admin', NOW(), 'admin', NOW(), 0),
(12, 2024, 2027, 'DELIVERABLE', 2027, '端到端测试方案',     'admin', NOW(), 'admin', NOW(), 0);

-- =============================================================
-- 12. 基线快照补齐（覆盖 DRAFT/APPROVED/SUPERSEDED 全 3 态，≥10 条）
-- =============================================================
INSERT IGNORE INTO `pms_baseline_snapshot`
    (`id`, `project_id`, `baseline_name`, `status`, `snapshot_json`, `change_reason`,
     `approval_record_id`, `approved_at`, `approved_by`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
-- 项目 2001 三态全覆盖
(7010, 2001, '初始基线 v1', 'SUPERSEDED',
    JSON_ARRAY(JSON_OBJECT('taskId',8010,'taskName','总集项目启动会','plannedStart','2026-01-05','plannedEnd','2026-01-10','duration',6,'plannedHours',8)),
    '被 v2 基线取代', NULL, NOW() - INTERVAL 180 DAY, 1,
    1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 180 DAY, 0, 0),
(7011, 2001, '修订基线 v2', 'APPROVED',
    JSON_ARRAY(
        JSON_OBJECT('taskId',8010,'taskName','总集项目启动会','plannedStart','2026-01-05','plannedEnd','2026-01-10','duration',6,'plannedHours',8),
        JSON_OBJECT('taskId',8011,'taskName','总体方案设计','plannedStart','2026-01-15','plannedEnd','2026-02-15','duration',32,'plannedHours',40),
        JSON_OBJECT('taskId',8012,'taskName','客户需求复核','plannedStart','2026-02-20','plannedEnd','2026-03-10','duration',19,'plannedHours',16)
    ),
    '客户需求变更后修订', 9015, NOW() - INTERVAL 60 DAY, 1,
    1, NOW() - INTERVAL 90 DAY, 1, NOW() - INTERVAL 60 DAY, 0, 0),
(7012, 2001, '草稿基线 v3', 'DRAFT',
    JSON_ARRAY(JSON_OBJECT('taskId',8015,'taskName','网络方案评审','plannedStart','2026-02-25','plannedEnd','2026-03-05','duration',9,'plannedHours',8)),
    '待审批', NULL, NULL, NULL,
    1, NOW() - INTERVAL 5 DAY, 1, NOW() - INTERVAL 1 DAY, 0, 0),
-- 项目 2002 基线
(7013, 2002, '省行项目初始基线', 'APPROVED',
    JSON_ARRAY(
        JSON_OBJECT('taskId',8016,'taskName','省行核心网络勘察','plannedStart','2026-02-01','plannedEnd','2026-02-28','duration',28,'plannedHours',40),
        JSON_OBJECT('taskId',8017,'taskName','核心交换机部署','plannedStart','2026-03-01','plannedEnd','2026-05-31','duration',92,'plannedHours',120)
    ),
    '省行项目初始基线', NULL, NOW() - INTERVAL 180 DAY, 1,
    1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 180 DAY, 0, 0),
(7014, 2002, '省行项目修订基线', 'DRAFT',
    JSON_ARRAY(JSON_OBJECT('taskId',8018,'taskName','防火墙策略配置','plannedStart','2026-06-01','plannedEnd','2026-07-31','duration',61,'plannedHours',80)),
    '新增防火墙任务', NULL, NULL, NULL,
    1, NOW() - INTERVAL 10 DAY, 1, NOW() - INTERVAL 1 DAY, 0, 0),
-- 项目 2006 基线（INITIAL_ACCEPTANCE）
(7015, 2006, '一期机房部署基线', 'APPROVED',
    JSON_ARRAY(
        JSON_OBJECT('taskId',8019,'taskName','设备到货验收','plannedStart','2026-04-05','plannedEnd','2026-04-20','duration',16,'plannedHours',24),
        JSON_OBJECT('taskId',8020,'taskName','机架安装','plannedStart','2026-04-21','plannedEnd','2026-05-31','duration',41,'plannedHours',80),
        JSON_OBJECT('taskId',8021,'taskName','设备加电测试','plannedStart','2026-06-01','plannedEnd','2026-07-15','duration',45,'plannedHours',80)
    ),
    '一期机房部署基线', NULL, NOW() - INTERVAL 130 DAY, 1,
    1, NOW() - INTERVAL 140 DAY, 1, NOW() - INTERVAL 130 DAY, 0, 0),
(7016, 2006, '初验基线', 'SUPERSEDED',
    JSON_ARRAY(JSON_OBJECT('taskId',8022,'taskName','初验测试报告','plannedStart','2026-08-01','plannedEnd','2026-08-25','duration',25,'plannedHours',16)),
    '被初验实际完成时间替代', NULL, NOW() - INTERVAL 30 DAY, 1,
    1, NOW() - INTERVAL 60 DAY, 1, NOW() - INTERVAL 30 DAY, 0, 0),
-- 项目 2009 基线
(7017, 2009, '广域网升级初始基线', 'APPROVED',
    JSON_ARRAY(
        JSON_OBJECT('taskId',8023,'taskName','广域网方案设计','plannedStart','2026-01-20','plannedEnd','2026-03-15','duration',55,'plannedHours',80),
        JSON_OBJECT('taskId',8024,'taskName','区域路由协议设计','plannedStart','2026-03-16','plannedEnd','2026-04-30','duration',46,'plannedHours',56)
    ),
    '广域网升级基线', NULL, NOW() - INTERVAL 150 DAY, 1,
    1, NOW() - INTERVAL 165 DAY, 1, NOW() - INTERVAL 150 DAY, 0, 0),
(7018, 2009, '广域网升级修订基线 v2', 'DRAFT',
    JSON_ARRAY(JSON_OBJECT('taskId',8025,'taskName','区域间互联测试','plannedStart','2026-08-01','plannedEnd','2026-09-15','duration',46,'plannedHours',40)),
    '新增互联测试任务', NULL, NULL, NULL,
    1, NOW() - INTERVAL 5 DAY, 1, NOW() - INTERVAL 1 DAY, 0, 0),
-- 项目 2013 基线（4 层项目）
(7019, 2013, '5G 核心网初始基线', 'APPROVED',
    JSON_ARRAY(
        JSON_OBJECT('taskId',8026,'taskName','5G 核心网总体设计','plannedStart','2026-01-05','plannedEnd','2026-02-28','duration',55,'plannedHours',120),
        JSON_OBJECT('taskId',8027,'taskName','一期设备采购评估','plannedStart','2026-03-01','plannedEnd','2026-04-30','duration',61,'plannedHours',80)
    ),
    '5G 核心网初始基线', NULL, NOW() - INTERVAL 170 DAY, 1,
    1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 170 DAY, 0, 0),
(7020, 2013, '5G 核心网修订基线 v2', 'SUPERSEDED',
    JSON_ARRAY(JSON_OBJECT('taskId',8028,'taskName','核心设备单机调测','plannedStart','2026-03-05','plannedEnd','2026-06-30','duration',118,'plannedHours',400)),
    '增加调测任务后被 v3 取代', NULL, NOW() - INTERVAL 100 DAY, 1,
    1, NOW() - INTERVAL 140 DAY, 1, NOW() - INTERVAL 100 DAY, 0, 0),
(7021, 2013, '5G 核心网当前基线 v3', 'APPROVED',
    JSON_ARRAY(JSON_OBJECT('taskId',8029,'taskName','端到端测试方案','plannedStart','2026-08-01','plannedEnd','2026-08-31','duration',31,'plannedHours',40)),
    '当前活跃基线', NULL, NOW() - INTERVAL 30 DAY, 1,
    1, NOW() - INTERVAL 60 DAY, 1, NOW() - INTERVAL 30 DAY, 0, 0);

-- =============================================================
-- 13. 审批记录补齐（覆盖 PENDING/APPROVED/REJECTED/WITHDRAWN/TIMEOUT 全 5 态）
-- =============================================================
INSERT IGNORE INTO `pms_approval_record`
    (`id`, `approval_type`, `business_id`, `business_code`, `project_id`, `process_instance_id`,
     `title`, `submitter_id`, `submitter_name`,
     `current_node_id`, `current_node_name`, `status`, `round`,
     `submitted_at`, `completed_at`, `timeout_at`, `escalated`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
-- 项目 2001 审批：5 态全覆盖
(9010, 'PROJECT',        2001, 'PRJ-2026-101', 2001, NULL,
     '主子项目立项审批', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'APPROVED', 1,
     NOW() - INTERVAL 200 DAY, NOW() - INTERVAL 195 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9011, 'DELIVERABLE',    2012, 'DLV-2026-1012', 2001, NULL,
     '交付件审批：网络架构设计 v2', 1, 'Administrator',
     'node-tech-review', '技术评审', 'PENDING', 1,
     NOW() - INTERVAL 10 DAY, NULL, NOW() + INTERVAL 2 DAY, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9012, 'BASELINE_CHANGE',7012, 'BLN-2026-7012', 2001, NULL,
     '基线变更审批：草稿基线 v3', 1, 'Administrator',
     'node-pm-review', '项目经理审核', 'PENDING', 1,
     NOW() - INTERVAL 5 DAY, NULL, NOW() + INTERVAL 7 DAY, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9013, 'CHANGE',         1,    'CHG-2026-001', 2001, NULL,
     '变更申请：新增子项目预算', 2, '张明',
     'node-ccb', 'CCB 评审', 'REJECTED', 1,
     NOW() - INTERVAL 60 DAY, NOW() - INTERVAL 55 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9014, 'PHASE_EXIT',     5010, 'PHS-2026-5010', 2001, NULL,
     '阶段退出审批：准备阶段', 1, 'Administrator',
     'node-pm-review', '项目经理审核', 'APPROVED', 1,
     NOW() - INTERVAL 180 DAY, NOW() - INTERVAL 175 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9015, 'BASELINE_CHANGE',7011, 'BLN-2026-7011', 2001, NULL,
     '基线变更审批：修订基线 v2', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'APPROVED', 1,
     NOW() - INTERVAL 65 DAY, NOW() - INTERVAL 60 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9016, 'TASK',           8015, 'TSK-2026-8015', 2001, NULL,
     '任务评审：网络方案评审', 1, 'Administrator',
     'node-customer-review', '客户评审', 'WITHDRAWN', 1,
     NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 25 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9017, 'DELIVERABLE',    2013, 'DLV-2026-1013', 2001, NULL,
     '交付件审批：风险评估报告', 2, '张明',
     'node-pm-review', '项目经理审核', 'TIMEOUT', 1,
     NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, 1,
     1, NOW(), 1, NOW(), 0, 0),
-- 项目 2006 审批
(9018, 'PROJECT',        2006, 'PRJ-2026-2006', 2006, NULL,
     '一期机房初验审批', 5, '孙磊',
     'node-customer-acceptance', '客户验收', 'APPROVED', 1,
     NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 5 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9019, 'DELIVERABLE',    2020, 'DLV-2026-2020', 2006, NULL,
     '交付件审批：初验测试报告', 5, '孙磊',
     'node-pm-review', '项目经理审核', 'APPROVED', 1,
     NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 5 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
-- 项目 2009 审批
(9020, 'PROJECT',        2009, 'PRJ-2026-2009', 2009, NULL,
     '广域网升级立项审批', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'APPROVED', 1,
     NOW() - INTERVAL 185 DAY, NOW() - INTERVAL 180 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9021, 'CHANGE',         2,    'CHG-2026-002', 2009, NULL,
     '变更申请：增加华北区域子项目预算', 6, '吴婷',
     'node-ccb', 'CCB 评审', 'REJECTED', 1,
     NOW() - INTERVAL 50 DAY, NOW() - INTERVAL 45 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
-- 项目 2013 审批
(9022, 'PROJECT',        2013, 'PRJ-2026-2013', 2013, NULL,
     '5G 核心网立项审批', 1, 'Administrator',
     'node-exec-review', '高管评审', 'APPROVED', 1,
     NOW() - INTERVAL 200 DAY, NOW() - INTERVAL 195 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9023, 'BASELINE_CHANGE',7019, 'BLN-2026-7019', 2013, NULL,
     '5G 核心网初始基线审批', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'APPROVED', 1,
     NOW() - INTERVAL 175 DAY, NOW() - INTERVAL 170 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9024, 'BASELINE_CHANGE',7020, 'BLN-2026-7020', 2013, NULL,
     '5G 核心网修订基线 v2 审批', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'APPROVED', 1,
     NOW() - INTERVAL 105 DAY, NOW() - INTERVAL 100 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
(9025, 'BASELINE_CHANGE',7021, 'BLN-2026-7021', 2013, NULL,
     '5G 核心网当前基线 v3 审批', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'APPROVED', 1,
     NOW() - INTERVAL 35 DAY, NOW() - INTERVAL 30 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
-- 项目 2002 审批
(9026, 'PHASE_EXIT',     5014, 'PHS-2026-5014', 2002, NULL,
     '阶段退出审批：准备阶段', 2, '张明',
     'node-pm-review', '项目经理审核', 'APPROVED', 1,
     NOW() - INTERVAL 175 DAY, NOW() - INTERVAL 170 DAY, NULL, 0,
     1, NOW(), 1, NOW(), 0, 0),
-- 重新提交演示（round=2）
(9027, 'CHANGE',         3,    'CHG-2026-003', 2001, NULL,
     '变更申请：调整总体方案（重提）', 1, 'Administrator',
     'node-ccb', 'CCB 评审', 'PENDING', 2,
     NOW() - INTERVAL 5 DAY, NULL, NOW() + INTERVAL 5 DAY, 0,
     1, NOW(), 1, NOW(), 0, 0),
-- 项目 2016 审批（4 层项目曾孙）
(9028, 'PROJECT',        2016, 'PRJ-2026-2016', 2016, NULL,
     '端到端测试项目立项审批', 4, '赵琳',
     'node-pm-review', '项目经理审核', 'PENDING', 1,
     NOW() - INTERVAL 2 DAY, NULL, NOW() + INTERVAL 10 DAY, 0,
     1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 14. 审批节点补齐（每个审批记录至少 1 节点，≥30 条）
-- =============================================================
INSERT IGNORE INTO `pms_approval_node`
    (`id`, `record_id`, `node_name`, `node_order`, `approver_id`, `approver_role`,
     `status`, `approver_actual_id`, `opinion`, `operated_at`, `timeout_at`)
VALUES
(10, 9010, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'APPROVED', 1, '同意立项',     NOW() - INTERVAL 198 DAY, NULL),
(11, 9010, 'CCB 评审',     2, NULL, 'APPROVER',        'APPROVED', 200, '通过',         NOW() - INTERVAL 195 DAY, NULL),
(12, 9011, '技术评审',     1, NULL, 'TECH_LEAD',       'PENDING',  NULL, NULL, NULL, NOW() + INTERVAL 2 DAY),
(13, 9012, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'PENDING',  NULL, NULL, NULL, NOW() + INTERVAL 7 DAY),
(14, 9013, 'CCB 评审',     1, NULL, 'APPROVER',        'REJECTED', 200, '预算超标驳回', NOW() - INTERVAL 55 DAY, NULL),
(15, 9014, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'APPROVED', 1, '同意阶段退出', NOW() - INTERVAL 175 DAY, NULL),
(16, 9015, 'CCB 评审',     1, NULL, 'APPROVER',        'APPROVED', 200, '同意基线变更', NOW() - INTERVAL 60 DAY, NULL),
(17, 9016, '客户评审',     1, NULL, 'CUSTOMER',        'PENDING',  NULL, NULL, NULL, NOW() - INTERVAL 30 DAY),
(18, 9016, '客户评审',     1, NULL, 'CUSTOMER',        'REJECTED', NULL, '客户撤回',   NOW() - INTERVAL 25 DAY, NULL),
(19, 9017, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'PENDING',  NULL, NULL, NULL, NOW() - INTERVAL 10 DAY),
(20, 9018, '客户验收',     1, NULL, 'CUSTOMER',        'APPROVED', NULL, '验收通过',   NOW() - INTERVAL 5 DAY, NULL),
(21, 9019, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'APPROVED', 1, '通过',         NOW() - INTERVAL 5 DAY, NULL),
(22, 9020, 'CCB 评审',     1, NULL, 'APPROVER',        'APPROVED', 200, '同意立项',   NOW() - INTERVAL 180 DAY, NULL),
(23, 9021, 'CCB 评审',     1, NULL, 'APPROVER',        'REJECTED', 200, '预算超标驳回', NOW() - INTERVAL 45 DAY, NULL),
(24, 9022, '高管评审',     1, NULL, 'PROJECT_MANAGER', 'APPROVED', 1, '5G 战略项目同意', NOW() - INTERVAL 195 DAY, NULL),
(25, 9023, 'CCB 评审',     1, NULL, 'APPROVER',        'APPROVED', 200, '同意基线',   NOW() - INTERVAL 170 DAY, NULL),
(26, 9024, 'CCB 评审',     1, NULL, 'APPROVER',        'APPROVED', 200, '同意修订',   NOW() - INTERVAL 100 DAY, NULL),
(27, 9025, 'CCB 评审',     1, NULL, 'APPROVER',        'APPROVED', 200, '同意当前基线', NOW() - INTERVAL 30 DAY, NULL),
(28, 9026, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'APPROVED', 1, '同意阶段退出', NOW() - INTERVAL 170 DAY, NULL),
(29, 9027, 'CCB 评审',     1, NULL, 'APPROVER',        'PENDING',  NULL, NULL, NULL, NOW() + INTERVAL 5 DAY),
(30, 9028, '项目经理审核', 1, NULL, 'PROJECT_MANAGER', 'PENDING',  NULL, NULL, NULL, NOW() + INTERVAL 10 DAY),
-- 多节点流程演示（9010）
(31, 9010, '财务审核',     3, NULL, 'APPROVER',        'APPROVED', 1, '预算合理',   NOW() - INTERVAL 196 DAY, NULL);

-- =============================================================
-- 15. 审批历史补齐（≥30 条，覆盖 SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT）
-- =============================================================
INSERT IGNORE INTO `pms_approval_history`
    (`id`, `record_id`, `round`, `node_name`, `operator_id`, `operator_name`,
     `action`, `opinion`, `operated_at`)
VALUES
(10, 9010, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交立项审批',     NOW() - INTERVAL 200 DAY),
(11, 9010, 1, '项目经理审核', 1,   'Administrator', 'APPROVE',  '同意',             NOW() - INTERVAL 198 DAY),
(12, 9010, 1, 'CCB 评审',     200, '审批员A',       'APPROVE',  '通过',             NOW() - INTERVAL 195 DAY),
(13, 9010, 1, '财务审核',     1,   'Administrator', 'APPROVE',  '预算合理',         NOW() - INTERVAL 196 DAY),
(14, 9011, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交技术评审',     NOW() - INTERVAL 10 DAY),
(15, 9012, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交基线变更',     NOW() - INTERVAL 5 DAY),
(16, 9013, 1, '提交人',       2,   '张明',          'SUBMIT',   '提交变更申请',     NOW() - INTERVAL 60 DAY),
(17, 9013, 1, 'CCB 评审',     200, '审批员A',       'REJECT',   '预算超标',         NOW() - INTERVAL 55 DAY),
(18, 9014, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交阶段退出',     NOW() - INTERVAL 180 DAY),
(19, 9014, 1, '项目经理审核', 1,   'Administrator', 'APPROVE',  '同意阶段退出',     NOW() - INTERVAL 175 DAY),
(20, 9015, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交基线变更',     NOW() - INTERVAL 65 DAY),
(21, 9015, 1, 'CCB 评审',     200, '审批员A',       'APPROVE',  '同意',             NOW() - INTERVAL 60 DAY),
(22, 9016, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交任务评审',     NOW() - INTERVAL 30 DAY),
(23, 9016, 1, '客户评审',     1,   'Administrator', 'WITHDRAW', '客户撤回',         NOW() - INTERVAL 25 DAY),
(24, 9017, 1, '提交人',       2,   '张明',          'SUBMIT',   '提交风险评估审批', NOW() - INTERVAL 40 DAY),
(25, 9017, 1, '项目经理审核', 1,   'Administrator', 'TIMEOUT',  '审批超时',         NOW() - INTERVAL 10 DAY),
(26, 9017, 1, '系统',         0,   '系统',          'ESCALATE', '超时升级至主管',   NOW() - INTERVAL 10 DAY),
(27, 9018, 1, '提交人',       5,   '孙磊',          'SUBMIT',   '提交初验审批',     NOW() - INTERVAL 10 DAY),
(28, 9018, 1, '客户验收',     1,   'Administrator', 'APPROVE',  '验收通过',         NOW() - INTERVAL 5 DAY),
(29, 9019, 1, '提交人',       5,   '孙磊',          'SUBMIT',   '提交交付件审批',   NOW() - INTERVAL 8 DAY),
(30, 9019, 1, '项目经理审核', 1,   'Administrator', 'APPROVE',  '通过',             NOW() - INTERVAL 5 DAY),
(31, 9020, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交立项审批',     NOW() - INTERVAL 185 DAY),
(32, 9020, 1, 'CCB 评审',     200, '审批员A',       'APPROVE',  '同意',             NOW() - INTERVAL 180 DAY),
(33, 9021, 1, '提交人',       6,   '吴婷',          'SUBMIT',   '提交变更申请',     NOW() - INTERVAL 50 DAY),
(34, 9021, 1, 'CCB 评审',     200, '审批员A',       'REJECT',   '预算超标',         NOW() - INTERVAL 45 DAY),
(35, 9022, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交 5G 立项',     NOW() - INTERVAL 200 DAY),
(36, 9022, 1, '高管评审',     1,   'Administrator', 'APPROVE',  '5G 战略同意',      NOW() - INTERVAL 195 DAY),
(37, 9023, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交初始基线',     NOW() - INTERVAL 175 DAY),
(38, 9023, 1, 'CCB 评审',     200, '审批员A',       'APPROVE',  '同意',             NOW() - INTERVAL 170 DAY),
(39, 9024, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交修订基线 v2',  NOW() - INTERVAL 105 DAY),
(40, 9024, 1, 'CCB 评审',     200, '审批员A',       'APPROVE',  '同意修订',         NOW() - INTERVAL 100 DAY),
(41, 9025, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '提交当前基线 v3',  NOW() - INTERVAL 35 DAY),
(42, 9025, 1, 'CCB 评审',     200, '审批员A',       'APPROVE',  '同意当前基线',     NOW() - INTERVAL 30 DAY),
(43, 9027, 1, '提交人',       1,   'Administrator', 'SUBMIT',   '首次提交变更',     NOW() - INTERVAL 60 DAY),
(44, 9027, 1, 'CCB 评审',     200, '审批员A',       'REJECT',   '驳回要求补充材料', NOW() - INTERVAL 30 DAY),
(45, 9027, 2, '提交人',       1,   'Administrator', 'RESUBMIT', '补充材料后重提',   NOW() - INTERVAL 5 DAY),
(46, 9028, 1, '提交人',       4,   '赵琳',          'SUBMIT',   '提交端到端测试立项', NOW() - INTERVAL 2 DAY);

-- =============================================================
-- 16. 审批字段权限补齐（覆盖 VISIBLE/MASKED/HIDDEN）
-- =============================================================
INSERT IGNORE INTO `pms_approval_field_permission`
    (`id`, `approval_node_id`, `entity_type`, `field_name`, `permission`, `mask_pattern`, `custom_pattern`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(10, 12, 'Deliverable', 'contractAmount',    'MASKED',  'amount-mask',  NULL, 1, NOW(), 1, NOW(), 0, 0),
(11, 12, 'Deliverable', 'customerContact',   'MASKED',  'phone-mask',   NULL, 1, NOW(), 1, NOW(), 0, 0),
(12, 12, 'Deliverable', 'filePath',          'HIDDEN',  NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0),
(13, 12, 'Deliverable', 'deliverableName',   'VISIBLE', NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0),
(14, 13, 'BaselineSnapshot', 'changeReason', 'VISIBLE', NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0),
(15, 13, 'BaselineSnapshot', 'snapshotJson', 'MASKED',  'custom',        NULL, 1, NOW(), 1, NOW(), 0, 0),
(16, 14, 'ChangeRequest', 'changeReason',    'VISIBLE', NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0),
(17, 14, 'ChangeRequest', 'estimatedCost',   'MASKED',  'amount-mask',  NULL, 1, NOW(), 1, NOW(), 0, 0),
(18, 14, 'ChangeRequest', 'internalNote',    'HIDDEN',  NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0),
(19, 30, 'Project', 'projectObjective',      'VISIBLE', NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0),
(20, 30, 'Project', 'contractAmount',        'MASKED',  'amount-mask',  NULL, 1, NOW(), 1, NOW(), 0, 0),
(21, 30, 'Project', 'customerPhone',         'MASKED',  'phone-mask',   NULL, 1, NOW(), 1, NOW(), 0, 0),
(22, 30, 'Project', 'internalCost',          'HIDDEN',  NULL,           NULL, 1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 17. 网络割接补齐 8 状态全覆盖（已有 PENDING_REVIEW + COMPLETED，补 DRAFT/PENDING_CONFIRM/EXECUTING/VERIFYING/ROLLED_BACK/REJECTED）
-- =============================================================
INSERT IGNORE INTO `demo_network_cutover`
    (`cutover_no`, `title`, `applicant`, `device_scope`, `impact_level`, `risk_score`,
     `window_start`, `window_end`, `implementation_plan`, `rollback_plan`, `verification_plan`,
     `status`, `result_summary`, `create_by`, `update_by`)
VALUES
('CO20260720001', '分支网点设备替换割接（草稿）',   '张明', 'BR-001、BR-002',        'LOW',      20,
     '2026-07-25 01:00:00', '2026-07-25 02:00:00', '主备切换后替换分支设备', '失败回退原设备', '验证分支业务连通性',
     'DRAFT', NULL, 'demo', 'demo'),
('CO20260720002', '核心路由器升级割接（待确认）',   '李华', 'CORE-R03',              'HIGH',     65,
     '2026-07-22 00:00:00', '2026-07-22 03:00:00', '逐台升级核心路由器版本', '失败恢复旧版本镜像', '验证路由协议收敛与业务',
     'PENDING_CONFIRM', NULL, 'demo', 'demo'),
('CO20260720003', '机房网络割接实施中',             '王强', 'DC-A 核心交换机',        'CRITICAL', 85,
     '2026-07-20 23:00:00', '2026-07-21 04:00:00', '5 台核心交换机逐台切换', '任一指标异常切回原链路', '验证办公/生产/互联网出口',
     'EXECUTING', '正在切换第 3 台设备', 'demo', 'demo'),
('CO20260720004', '防火墙策略调整验证中',           '赵琳', 'FW-DC-01、FW-DC-02',    'MEDIUM',   55,
     '2026-07-19 02:00:00', '2026-07-19 04:00:00', '主备防火墙策略批量更新', '策略异常时回滚规则集', '验证安全策略与业务访问',
     'VERIFYING', '业务验证 80% 完成', 'demo', 'demo'),
('CO20260720005', '分支防火墙升级失败回退',         '李华', 'FW-BRANCH-08',          'MEDIUM',   50,
     '2026-07-15 01:00:00', '2026-07-15 02:30:00', '升级分支防火墙版本', '升级失败启动旧版本镜像', '验证 VPN 与互联网',
     'ROLLED_BACK', '升级失败已回退，等待重新评估', 'demo', 'demo'),
('CO20260720006', '数据中心核心割接被驳回',         '张明', 'DC-CORE-01',            'CRITICAL', 90,
     '2026-07-18 23:00:00', '2026-07-19 05:00:00', '数据中心核心设备整体割接', '失败切回原核心', '验证所有业务系统',
     'REJECTED', '风险评分过高被驳回，需拆分批次', 'demo', 'demo'),
('CO20260720007', '完成的核心路由器割接',           '李华', 'CORE-R05',              'HIGH',     70,
     '2026-07-10 00:30:00', '2026-07-10 03:00:00', '核心路由器版本升级', '失败恢复旧版本', '验证路由协议与业务',
     'COMPLETED', '割接成功，业务无中断', 'demo', 'demo'),
('CO20260720008', '待审核的分支交换机替换',         '王强', 'SW-BR-09、SW-BR-10',    'MEDIUM',   40,
     '2026-07-28 01:00:00', '2026-07-28 02:00:00', '替换 2 台分支交换机', '失败切回原设备', '验证分支业务连通性',
     'PENDING_REVIEW', NULL, 'demo', 'demo');

-- =============================================================
-- 18. 里程碑补齐（覆盖 PENDING/IN_PROGRESS/COMPLETED/OVERDUE/BLOCKED 5 态）
--     milestone_type 必须为 PpdiooPhase 12 节点枚举之一：
--       SITE_SURVEY/NETWORK_DESIGN/PROCUREMENT/STAGING/FAT/ARRIVAL/
--       INSTALLATION/TESTING/COMMISSIONING/SAT/UAT/FINAL_ACCEPTANCE
-- =============================================================
INSERT IGNORE INTO `pms_milestone`
    (`id`, `project_id`, `phase_id`, `milestone_name`, `milestone_type`, `status`,
     `plan_date`, `planned_date`, `actual_date`, `description`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1010, 2001, 5010, '总集项目启动',           'NETWORK_DESIGN', 'COMPLETED',
     '2026-01-10', '2026-01-10', '2026-01-08', '总集项目启动会完成',
     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 200 DAY, 0),
(1011, 2001, 5011, '总体方案评审',           'NETWORK_DESIGN', 'IN_PROGRESS',
     '2026-02-28', '2026-02-28', NULL, '总体方案设计评审进行中',
     'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(1012, 2001, 5012, '设计阶段启动',           'NETWORK_DESIGN', 'PENDING',
     '2026-05-01', '2026-05-01', NULL, '设计阶段开始',
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 30 DAY, 0),
(1013, 2001, NULL, '历史延期里程碑',         'FAT',            'OVERDUE',
     '2026-06-30', '2026-06-30', NULL, '客户需求确认里程碑已延期',
     'admin', NOW() - INTERVAL 90 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0),
(1014, 2001, NULL, '受阻里程碑',             'FAT',            'BLOCKED',
     '2026-07-15', '2026-07-15', NULL, '客户审批受阻，待客户决策',
     'admin', NOW() - INTERVAL 60 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(1015, 2002, 5014, '省行项目启动',           'NETWORK_DESIGN', 'COMPLETED',
     '2026-02-05', '2026-02-05', '2026-02-05', '省行项目启动会完成',
     'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 180 DAY, 0),
(1016, 2002, 5015, '省行核心交换机部署完成', 'INSTALLATION',   'IN_PROGRESS',
     '2026-05-31', '2026-05-31', NULL, '5 台核心交换机部署完成',
     'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
(1017, 2006, 5016, '一期机房部署完成',       'INSTALLATION',   'COMPLETED',
     '2026-07-31', '2026-07-31', '2026-07-25', '一期机房部署提前完成',
     'admin', NOW() - INTERVAL 130 DAY, 'admin', NOW() - INTERVAL 25 DAY, 0),
(1018, 2006, 5017, '一期机房初验',           'SAT',            'COMPLETED',
     '2026-08-25', '2026-08-25', '2026-08-25', '一期机房初验通过',
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0),
(1019, 2009, 5010, '广域网方案设计完成',     'NETWORK_DESIGN', 'COMPLETED',
     '2026-03-15', '2026-03-15', '2026-03-10', '广域网方案设计提前完成',
     'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 160 DAY, 0),
(1020, 2013, 5028, '5G 核心网总体设计完成',  'NETWORK_DESIGN', 'COMPLETED',
     '2026-02-28', '2026-02-28', '2026-02-25', '5G 核心网总体设计完成',
     'admin', NOW() - INTERVAL 170 DAY, 'admin', NOW() - INTERVAL 170 DAY, 0),
(1021, 2013, 5029, '一期核心网硬件就绪',     'INSTALLATION',   'IN_PROGRESS',
     '2026-12-31', '2026-12-31', NULL, '一期核心网硬件部署完成',
     'admin', NOW() - INTERVAL 90 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0),
(1022, 2016, 5025, '端到端测试启动',         'NETWORK_DESIGN', 'PENDING',
     '2026-08-01', '2026-08-01', NULL, '端到端测试项目启动',
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 30 DAY, 0);

-- =============================================================
-- 19. 任务评论补齐（≥10 条，演示协作记录）
--     表结构由 V72 创建，本节仅 INSERT。V72 字段：
--       task_id, user_id, user_name, content(TEXT), parent_comment_id
-- =============================================================
INSERT IGNORE INTO `pms_task_comment`
    (`id`, `task_id`, `parent_comment_id`, `user_id`, `user_name`, `content`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1, 8010, NULL, 1,   'Administrator', '启动会准备就绪，请大家准时参会', 1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 200 DAY, 0, 0),
(2, 8010, 1,    2,   '张明',          '收到，会前会发送材料',         2, NOW() - INTERVAL 200 DAY, 2, NOW() - INTERVAL 200 DAY, 0, 0),
(3, 8011, NULL, 1,   'Administrator', '总体方案初稿已完成，请审核',   1, NOW() - INTERVAL 100 DAY, 1, NOW() - INTERVAL 100 DAY, 0, 0),
(4, 8011, 3,    200, '审批员A',       '架构图需要补充冗余设计',       200, NOW() - INTERVAL 95 DAY, 200, NOW() - INTERVAL 95 DAY, 0, 0),
(5, 8011, 3,    1,   'Administrator', '已补充冗余设计，请复审',       1, NOW() - INTERVAL 90 DAY, 1, NOW() - INTERVAL 90 DAY, 0, 0),
(6, 8014, NULL, 3,   '刘伟',          '开始评估历史数据迁移',         3, NOW() - INTERVAL 30 DAY, 3, NOW() - INTERVAL 30 DAY, 0, 0),
(7, 8015, NULL, 1,   'Administrator', '方案被驳回，需要重新设计预算', 1, NOW() - INTERVAL 25 DAY, 1, NOW() - INTERVAL 25 DAY, 0, 0),
(8, 8017, NULL, 3,   '刘伟',          '已完成 3 台交换机部署',        3, NOW() - INTERVAL 10 DAY, 3, NOW() - INTERVAL 10 DAY, 0, 0),
(9, 8021, NULL, 5,   '孙磊',          '加电测试发现 1 台设备异常',    5, NOW() - INTERVAL 5 DAY, 5, NOW() - INTERVAL 5 DAY, 0, 0),
(10, 8021, 9,   1,   'Administrator', '已联系厂商处理，请跟踪进度',   1, NOW() - INTERVAL 4 DAY, 1, NOW() - INTERVAL 4 DAY, 0, 0),
(11, 8026, NULL, 1,  'Administrator', '5G 核心网总体设计已与 3GPP R15 对齐', 1, NOW() - INTERVAL 170 DAY, 1, NOW() - INTERVAL 170 DAY, 0, 0),
(12, 8028, NULL, 3,  '刘伟',          '调测中发现性能未达预期，正在优化', 3, NOW() - INTERVAL 7 DAY, 3, NOW() - INTERVAL 7 DAY, 0, 0);

-- =============================================================
-- 20. 任务活动日志补齐（≥10 条）
--     表结构由 V72 创建，本节仅 INSERT。V72 字段：
--       task_id, user_id, user_name, activity_type(VARCHAR(50)),
--       content(TEXT), metadata(JSON, 默认 NULL)
-- =============================================================
INSERT IGNORE INTO `pms_task_activity`
    (`id`, `task_id`, `user_id`, `user_name`, `activity_type`, `content`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1, 8010, 1, 'Administrator', 'CREATE',        '创建任务：总集项目启动会',          1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 200 DAY, 0, 0),
(2, 8010, 1, 'Administrator', 'STATUS_CHANGE', '状态：PENDING → IN_PROGRESS',       1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 200 DAY, 0, 0),
(3, 8010, 1, 'Administrator', 'UPDATE',        '任务完成',                          1, NOW() - INTERVAL 198 DAY, 1, NOW() - INTERVAL 198 DAY, 0, 0),
(4, 8011, 1, 'Administrator', 'CREATE',        '创建任务：总体方案设计',            1, NOW() - INTERVAL 120 DAY, 1, NOW() - INTERVAL 120 DAY, 0, 0),
(5, 8011, 1, 'Administrator', 'STATUS_CHANGE', '状态：PENDING → IN_PROGRESS',       1, NOW() - INTERVAL 120 DAY, 1, NOW() - INTERVAL 120 DAY, 0, 0),
(6, 8011, 1, 'Administrator', 'COMMENT',       '总体方案初稿已完成',                1, NOW() - INTERVAL 100 DAY, 1, NOW() - INTERVAL 100 DAY, 0, 0),
(7, 8015, 1, 'Administrator', 'STATUS_CHANGE', '状态：PENDING → REJECTED',          1, NOW() - INTERVAL 25 DAY,  1, NOW() - INTERVAL 25 DAY,  0, 0),
(8, 8017, 3, '刘伟',          'STATUS_CHANGE', '状态：PENDING → IN_PROGRESS',       3, NOW() - INTERVAL 120 DAY, 3, NOW() - INTERVAL 120 DAY, 0, 0),
(9, 8021, 5, '孙磊',          'UPDATE',        '上传测试报告',                      5, NOW() - INTERVAL 5 DAY,   5, NOW() - INTERVAL 5 DAY,   0, 0),
(10, 8026, 1, 'Administrator', 'CREATE',        '创建任务：5G 核心网总体设计',       1, NOW() - INTERVAL 200 DAY, 1, NOW() - INTERVAL 200 DAY, 0, 0),
(11, 8026, 1, 'Administrator', 'UPDATE',        '任务完成',                          1, NOW() - INTERVAL 170 DAY, 1, NOW() - INTERVAL 170 DAY, 0, 0),
(12, 8028, 3, '刘伟',          'ASSIGN',        '分配任务给调测团队',                3, NOW() - INTERVAL 140 DAY, 3, NOW() - INTERVAL 140 DAY, 0, 0);

-- =============================================================
-- 21. 项目配置补齐（每个主子项目≥2 条配置项）
-- =============================================================
INSERT IGNORE INTO `pms_project_config`
    (`id`, `project_id`, `template_id`, `config_key`, `config_value`, `description`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(10, 2001, NULL, 'baseline.variance.days.threshold',    '3',  '主项目：基线偏差天数阈值收紧', 1, NOW(), 1, NOW(), 0, 0),
(11, 2001, NULL, 'approval.timeout.hours',              '24', '主项目：审批超时缩短至 24h',  1, NOW(), 1, NOW(), 0, 0),
(12, 2001, NULL, 'task.rollup.weight.field',            'PLANNED_HOURS', '主项目：任务汇总权重字段', 1, NOW(), 1, NOW(), 0, 0),
(13, 2002, NULL, 'baseline.variance.days.threshold',    '5',  '子项目：基线偏差天数阈值',     1, NOW(), 1, NOW(), 0, 0),
(14, 2002, NULL, 'approval.timeout.hours',              '48', '子项目：审批超时 48h',         1, NOW(), 1, NOW(), 0, 0),
(15, 2006, NULL, 'phase.exit.check.approval',           'true', '一期机房：阶段退出强制审批', 1, NOW(), 1, NOW(), 0, 0),
(16, 2009, NULL, 'baseline.variance.days.threshold',    '7',  '广域网：基线偏差天数阈值放宽', 1, NOW(), 1, NOW(), 0, 0),
(17, 2013, NULL, 'approval.max.rounds',                 '7',  '5G 项目：审批最大轮次增加',   1, NOW(), 1, NOW(), 0, 0),
(18, 2013, NULL, 'approval.timeout.hours',              '72', '5G 项目：审批超时延长',       1, NOW(), 1, NOW(), 0, 0),
(19, 2016, NULL, 'phase.exit.check.approval',           'false', '端到端测试：阶段退出免审批', 1, NOW(), 1, NOW(), 0, 0);

-- =============================================================
-- 22. 数据完整性自检注释
-- =============================================================
-- 预期数据条数（V82 执行后，与 V61/V77 累计）：
--   pms_project_template             新增 9 条（id 2~10），累计 10 条（含 V77 的 1）
--   pms_project_template_version     新增 9 条，累计 10 条
--   pms_project                      新增 16 条（id 2001~2016），累计 27 条（V61:10 + V77:1 + 本批次:16）
--     - 主子项目结构 5 组（覆盖 2/3/4 层级）
--     - 11 个子项目（parent_project_id 非空）
--   pms_project_phase                新增 21 条（5010~5030），累计 23 条
--     - 4 状态全覆盖：NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED
--   pms_project_member               新增 25 条（7010~7034），累计 28 条
--   pms_impl_task                    新增 21 条（8010~8030），累计 24 条
--     - 6 状态全覆盖：PENDING/ACCEPTED/IN_PROGRESS/COMPLETED/CONFIRMED/REJECTED
--   pms_task_checklist               新增 12 条，累计 14 条
--   pms_task_comment                 新增 12 条（新表）
--   pms_task_activity                新增 12 条（新表）
--   pms_task_dependency              新增 17 条，累计 18 条
--     - 4 类型全覆盖：FS/FF/SS/SF
--   pms_deliverable                  新增 18 条（2010~2027），累计 20 条
--     - 7 状态全覆盖：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED
--   pms_deliverable_version          新增 16 条，累计 17 条
--   pms_deliverable_signature        新增 10 条，累计 11 条
--     - 3 类型全覆盖：ELECTRONIC/STAMP/DIGITAL
--   pms_deliverable_reference        新增 12 条（新表）
--     - 4 类型全覆盖：CITATION/DERIVATIVE/REPLACES/ATTACHMENT（其中 CITATION/DERIVATIVE）
--   pms_baseline_snapshot            新增 12 条（7010~7021），累计 13 条
--     - 3 状态全覆盖：DRAFT/APPROVED/SUPERSEDED
--   pms_approval_record              新增 19 条（9010~9028），累计 20 条
--     - 5 状态全覆盖：PENDING/APPROVED/REJECTED/WITHDRAWN/TIMEOUT
--   pms_approval_node                新增 22 条，累计 23 条
--   pms_approval_history             新增 37 条，累计 38 条
--     - 7 动作全覆盖：SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT
--   pms_approval_field_permission    新增 13 条，累计 16 条
--     - 3 权限全覆盖：VISIBLE/MASKED/HIDDEN
--   pms_milestone                    新增 13 条（1010~1022）
--     - 5 状态全覆盖：PENDING/IN_PROGRESS/COMPLETED/OVERDUE/BLOCKED
--   pms_project_config               新增 10 条
--   demo_network_cutover             新增 8 条，累计 10 条
--     - 8 状态全覆盖：DRAFT/PENDING_REVIEW/PENDING_CONFIRM/EXECUTING/VERIFYING/COMPLETED/ROLLED_BACK/REJECTED
-- =============================================================
-- 文件结束
