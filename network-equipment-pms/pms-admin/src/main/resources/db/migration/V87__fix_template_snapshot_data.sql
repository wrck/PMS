-- =============================================================
-- V87__fix_template_snapshot_data.sql
-- 修复 V82 预置模板 2-10 的非法 snapshot_json
--
-- 背景：V82 使用 JSON_OBJECT('phases', 5, 'tasks', 20, ...) 生成整数计数，
--       而 TemplateSnapshot DTO 期望 phases/tasks/deliverables 等为对象数组。
--       导致前端加载已发布模板时无法回显阶段/任务/交付件等详细配置。
--
-- 修复：用合法 JSON_ARRAY(JSON_OBJECT(...)) 替换模板 2-10 的 snapshot_json，
--       包含 PhaseDef/TaskDef/DeliverableDef/DependencyDef/ApprovalPlanDef/MilestoneDef
--       对象数组，字段名对齐 com.dp.plat.common.dto.TemplateSnapshot。
--
-- 幂等性：UPDATE 语句，重复执行结果一致；若 V82 未执行（全新库无模板 2-10），
--         UPDATE 影响 0 行，无副作用。
-- =============================================================

-- -------------------------------------------------------------
-- 模板 2：TPL-IMPL-STD-V2 网络设备实施标准模板 V2（5 阶段 / 20 任务 / 8 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','PREPARE','phaseName','准备阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','PLAN','phaseName','规划阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','DESIGN','phaseName','设计阶段','sortOrder',3,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','IMPLEMENT','phaseName','实施阶段','sortOrder',4,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','OPERATE','phaseName','运维阶段','sortOrder',5,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','项目启动会','taskType','OEM','phaseCode','PREPARE','plannedHours',8,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','需求调研','taskType','AGENT','phaseCode','PREPARE','plannedHours',24,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','现场勘察','taskType','AGENT','phaseCode','PREPARE','plannedHours',40,'priority','MEDIUM','sortOrder',3),
    JSON_OBJECT('taskName','总体方案设计','taskType','OEM','phaseCode','PLAN','plannedHours',80,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','网络拓扑设计','taskType','OEM','phaseCode','PLAN','plannedHours',40,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','IP 地址规划','taskType','OEM','phaseCode','PLAN','plannedHours',16,'priority','MEDIUM','sortOrder',6),
    JSON_OBJECT('taskName','设备选型清单','taskType','OEM','phaseCode','PLAN','plannedHours',24,'priority','MEDIUM','sortOrder',7),
    JSON_OBJECT('taskName','详细设计文档','taskType','OEM','phaseCode','DESIGN','plannedHours',120,'priority','HIGH','sortOrder',8),
    JSON_OBJECT('taskName','配置模板编写','taskType','OEM','phaseCode','DESIGN','plannedHours',32,'priority','MEDIUM','sortOrder',9),
    JSON_OBJECT('taskName','割接方案设计','taskType','OEM','phaseCode','DESIGN','plannedHours',24,'priority','HIGH','sortOrder',10),
    JSON_OBJECT('taskName','设备到货验收','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',16,'priority','HIGH','sortOrder',11),
    JSON_OBJECT('taskName','机架安装','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',40,'priority','HIGH','sortOrder',12),
    JSON_OBJECT('taskName','设备加电测试','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',24,'priority','HIGH','sortOrder',13),
    JSON_OBJECT('taskName','设备配置下发','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',32,'priority','HIGH','sortOrder',14),
    JSON_OBJECT('taskName','联调测试','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',48,'priority','HIGH','sortOrder',15),
    JSON_OBJECT('taskName','业务割接','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',16,'priority','CRITICAL','sortOrder',16),
    JSON_OBJECT('taskName','初验测试','taskType','OEM','phaseCode','OPERATE','plannedHours',24,'priority','HIGH','sortOrder',17),
    JSON_OBJECT('taskName','终验测试','taskType','OEM','phaseCode','OPERATE','plannedHours',16,'priority','HIGH','sortOrder',18),
    JSON_OBJECT('taskName','运维文档交付','taskType','OEM','phaseCode','OPERATE','plannedHours',16,'priority','MEDIUM','sortOrder',19),
    JSON_OBJECT('taskName','培训移交','taskType','OEM','phaseCode','OPERATE','plannedHours',8,'priority','MEDIUM','sortOrder',20)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','需求确认书','deliverableType','DOCUMENT','phaseCode','PREPARE','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','现场勘察报告','deliverableType','DOCUMENT','phaseCode','PREPARE','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','总体实施方案','deliverableType','DOCUMENT','phaseCode','PLAN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','设备选型清单','deliverableType','DOCUMENT','phaseCode','PLAN','mandatory',false,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','详细设计文档','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','割接方案','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','初验测试报告','deliverableType','DOCUMENT','phaseCode','OPERATE','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','终验报告','deliverableType','DOCUMENT','phaseCode','OPERATE','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','项目启动会','successorTaskName','需求调研','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','现场勘察','successorTaskName','总体方案设计','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','总体方案设计','successorTaskName','详细设计文档','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','设备到货验收','successorTaskName','机架安装','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','设备配置下发','successorTaskName','联调测试','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','联调测试','successorTaskName','业务割接','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','业务割接','successorTaskName','初验测试','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','PLAN','approverRoles',JSON_ARRAY('TECH_LEAD')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','DESIGN','approverRoles',JSON_ARRAY('PROJECT_MANAGER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','IMPLEMENT','approverRoles',JSON_ARRAY('PROJECT_MANAGER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','OPERATE','approverRoles',JSON_ARRAY('APPROVER'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','方案评审','milestoneType','NETWORK_DESIGN','phaseCode','PLAN','sortOrder',1),
    JSON_OBJECT('milestoneName','设备部署完成','milestoneType','INSTALLATION','phaseCode','IMPLEMENT','sortOrder',2),
    JSON_OBJECT('milestoneName','初验通过','milestoneType','SAT','phaseCode','OPERATE','sortOrder',3),
    JSON_OBJECT('milestoneName','终验通过','milestoneType','FINAL_ACCEPTANCE','phaseCode','OPERATE','sortOrder',4)
  )
) WHERE `id` = 2;

-- -------------------------------------------------------------
-- 模板 3：TPL-DC-BUILD 数据中心建设模板（4 阶段 / 15 任务 / 6 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','PREPARE','phaseName','准备阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','DESIGN','phaseName','设计阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','BUILD','phaseName','建设阶段','sortOrder',3,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','ACCEPT','phaseName','验收阶段','sortOrder',4,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','项目立项','taskType','OEM','phaseCode','PREPARE','plannedHours',8,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','机房选址评估','taskType','AGENT','phaseCode','PREPARE','plannedHours',24,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','机房基础设施勘察','taskType','AGENT','phaseCode','PREPARE','plannedHours',40,'priority','MEDIUM','sortOrder',3),
    JSON_OBJECT('taskName','总体架构设计','taskType','OEM','phaseCode','DESIGN','plannedHours',80,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','网络架构设计','taskType','OEM','phaseCode','DESIGN','plannedHours',60,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','综合布线设计','taskType','OEM','phaseCode','DESIGN','plannedHours',32,'priority','MEDIUM','sortOrder',6),
    JSON_OBJECT('taskName','机柜部署','taskType','AGENT','phaseCode','BUILD','plannedHours',80,'priority','HIGH','sortOrder',7),
    JSON_OBJECT('taskName','网络设备安装','taskType','AGENT','phaseCode','BUILD','plannedHours',120,'priority','HIGH','sortOrder',8),
    JSON_OBJECT('taskName','电源系统部署','taskType','AGENT','phaseCode','BUILD','plannedHours',60,'priority','HIGH','sortOrder',9),
    JSON_OBJECT('taskName','空调系统部署','taskType','AGENT','phaseCode','BUILD','plannedHours',48,'priority','MEDIUM','sortOrder',10),
    JSON_OBJECT('taskName','设备配置','taskType','AGENT','phaseCode','BUILD','plannedHours',40,'priority','HIGH','sortOrder',11),
    JSON_OBJECT('taskName','系统联调','taskType','AGENT','phaseCode','BUILD','plannedHours',56,'priority','HIGH','sortOrder',12),
    JSON_OBJECT('taskName','初验测试','taskType','OEM','phaseCode','ACCEPT','plannedHours',32,'priority','HIGH','sortOrder',13),
    JSON_OBJECT('taskName','终验测试','taskType','OEM','phaseCode','ACCEPT','plannedHours',24,'priority','HIGH','sortOrder',14),
    JSON_OBJECT('taskName','运维移交','taskType','OEM','phaseCode','ACCEPT','plannedHours',16,'priority','MEDIUM','sortOrder',15)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','机房勘察报告','deliverableType','DOCUMENT','phaseCode','PREPARE','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','数据中心总体设计','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','网络架构设计文档','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','设备部署报告','deliverableType','DOCUMENT','phaseCode','BUILD','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','初验测试报告','deliverableType','DOCUMENT','phaseCode','ACCEPT','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','终验报告','deliverableType','DOCUMENT','phaseCode','ACCEPT','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','项目立项','successorTaskName','机房选址评估','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','机房基础设施勘察','successorTaskName','总体架构设计','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','机柜部署','successorTaskName','网络设备安装','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','设备配置','successorTaskName','系统联调','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','系统联调','successorTaskName','初验测试','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','DESIGN','approverRoles',JSON_ARRAY('TECH_LEAD')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','BUILD','approverRoles',JSON_ARRAY('PROJECT_MANAGER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','ACCEPT','approverRoles',JSON_ARRAY('APPROVER'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','设计评审','milestoneType','NETWORK_DESIGN','phaseCode','DESIGN','sortOrder',1),
    JSON_OBJECT('milestoneName','设备部署完成','milestoneType','INSTALLATION','phaseCode','BUILD','sortOrder',2),
    JSON_OBJECT('milestoneName','终验通过','milestoneType','FINAL_ACCEPTANCE','phaseCode','ACCEPT','sortOrder',3)
  )
) WHERE `id` = 3;

-- -------------------------------------------------------------
-- 模板 4：TPL-SEC-AUDIT 安全设备部署与等保整改模板（3 阶段 / 12 任务 / 5 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','ASSESS','phaseName','评估阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','DEPLOY','phaseName','部署阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','REMEDIATION','phaseName','整改阶段','sortOrder',3,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','等保现状评估','taskType','AGENT','phaseCode','ASSESS','plannedHours',40,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','安全漏洞扫描','taskType','AGENT','phaseCode','ASSESS','plannedHours',24,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','风险评估报告','taskType','OEM','phaseCode','ASSESS','plannedHours',32,'priority','HIGH','sortOrder',3),
    JSON_OBJECT('taskName','防火墙部署','taskType','AGENT','phaseCode','DEPLOY','plannedHours',48,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','IDS/IPS 部署','taskType','AGENT','phaseCode','DEPLOY','plannedHours',40,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','安全策略配置','taskType','AGENT','phaseCode','DEPLOY','plannedHours',32,'priority','HIGH','sortOrder',6),
    JSON_OBJECT('taskName','VPN 网关部署','taskType','AGENT','phaseCode','DEPLOY','plannedHours',24,'priority','MEDIUM','sortOrder',7),
    JSON_OBJECT('taskName','终端准入部署','taskType','AGENT','phaseCode','DEPLOY','plannedHours',40,'priority','MEDIUM','sortOrder',8),
    JSON_OBJECT('taskName','安全策略整改','taskType','AGENT','phaseCode','REMEDIATION','plannedHours',48,'priority','HIGH','sortOrder',9),
    JSON_OBJECT('taskName','日志审计整改','taskType','AGENT','phaseCode','REMEDIATION','plannedHours',32,'priority','MEDIUM','sortOrder',10),
    JSON_OBJECT('taskName','等保测评','taskType','OEM','phaseCode','REMEDIATION','plannedHours',40,'priority','HIGH','sortOrder',11),
    JSON_OBJECT('taskName','整改报告交付','taskType','OEM','phaseCode','REMEDIATION','plannedHours',16,'priority','MEDIUM','sortOrder',12)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','风险评估报告','deliverableType','DOCUMENT','phaseCode','ASSESS','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','安全设备部署方案','deliverableType','DOCUMENT','phaseCode','DEPLOY','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','安全策略配置文档','deliverableType','DOCUMENT','phaseCode','DEPLOY','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','等保测评报告','deliverableType','DOCUMENT','phaseCode','REMEDIATION','mandatory',true,'approverRole','APPROVER'),
    JSON_OBJECT('deliverableName','整改总结报告','deliverableType','DOCUMENT','phaseCode','REMEDIATION','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','等保现状评估','successorTaskName','安全漏洞扫描','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','风险评估报告','successorTaskName','防火墙部署','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','防火墙部署','successorTaskName','安全策略配置','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','安全策略整改','successorTaskName','等保测评','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','ASSESS','approverRoles',JSON_ARRAY('TECH_LEAD')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','DEPLOY','approverRoles',JSON_ARRAY('PROJECT_MANAGER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','REMEDIATION','approverRoles',JSON_ARRAY('APPROVER'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','评估完成','milestoneType','SITE_SURVEY','phaseCode','ASSESS','sortOrder',1),
    JSON_OBJECT('milestoneName','设备部署完成','milestoneType','INSTALLATION','phaseCode','DEPLOY','sortOrder',2),
    JSON_OBJECT('milestoneName','等保通过','milestoneType','SAT','phaseCode','REMEDIATION','sortOrder',3)
  )
) WHERE `id` = 4;

-- -------------------------------------------------------------
-- 模板 5：TPL-MAINT-ROUTINE 日常运维巡检模板（2 阶段 / 8 任务 / 3 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','INSPECT','phaseName','巡检阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','MAINTAIN','phaseName','维护阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','设备健康检查','taskType','AGENT','phaseCode','INSPECT','plannedHours',16,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','日志审计检查','taskType','AGENT','phaseCode','INSPECT','plannedHours',8,'priority','MEDIUM','sortOrder',2),
    JSON_OBJECT('taskName','性能基线采集','taskType','AGENT','phaseCode','INSPECT','plannedHours',16,'priority','MEDIUM','sortOrder',3),
    JSON_OBJECT('taskName','告警分析','taskType','AGENT','phaseCode','INSPECT','plannedHours',8,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','配置备份','taskType','AGENT','phaseCode','MAINTAIN','plannedHours',8,'priority','MEDIUM','sortOrder',5),
    JSON_OBJECT('taskName','固件升级','taskType','AGENT','phaseCode','MAINTAIN','plannedHours',24,'priority','HIGH','sortOrder',6),
    JSON_OBJECT('taskName','备件更换','taskType','AGENT','phaseCode','MAINTAIN','plannedHours',16,'priority','MEDIUM','sortOrder',7),
    JSON_OBJECT('taskName','维护报告','taskType','OEM','phaseCode','MAINTAIN','plannedHours',8,'priority','MEDIUM','sortOrder',8)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','巡检报告','deliverableType','DOCUMENT','phaseCode','INSPECT','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','性能基线数据','deliverableType','DATA','phaseCode','INSPECT','mandatory',false,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','维护总结报告','deliverableType','DOCUMENT','phaseCode','MAINTAIN','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','设备健康检查','successorTaskName','性能基线采集','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','配置备份','successorTaskName','固件升级','dependencyType','FS','lagDays',0)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','INSPECT','approverRoles',JSON_ARRAY('TECH_LEAD'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','巡检完成','milestoneType','TESTING','phaseCode','INSPECT','sortOrder',1),
    JSON_OBJECT('milestoneName','维护完成','milestoneType','COMMISSIONING','phaseCode','MAINTAIN','sortOrder',2)
  )
) WHERE `id` = 5;

-- -------------------------------------------------------------
-- 模板 6：TPL-CONSULT-AUDIT 网络架构咨询评估模板（3 阶段 / 10 任务 / 4 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','SURVEY','phaseName','调研阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','ANALYSIS','phaseName','分析阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','ADVISE','phaseName','建议阶段','sortOrder',3,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','网络现状调研','taskType','AGENT','phaseCode','SURVEY','plannedHours',32,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','业务需求访谈','taskType','AGENT','phaseCode','SURVEY','plannedHours',24,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','网络拓扑梳理','taskType','AGENT','phaseCode','SURVEY','plannedHours',16,'priority','MEDIUM','sortOrder',3),
    JSON_OBJECT('taskName','性能数据分析','taskType','OEM','phaseCode','ANALYSIS','plannedHours',32,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','安全风险评估','taskType','OEM','phaseCode','ANALYSIS','plannedHours',24,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','容量规划分析','taskType','OEM','phaseCode','ANALYSIS','plannedHours',24,'priority','MEDIUM','sortOrder',6),
    JSON_OBJECT('taskName','架构优化建议','taskType','OEM','phaseCode','ADVISE','plannedHours',40,'priority','HIGH','sortOrder',7),
    JSON_OBJECT('taskName','演进路线规划','taskType','OEM','phaseCode','ADVISE','plannedHours',24,'priority','MEDIUM','sortOrder',8),
    JSON_OBJECT('taskName','投资估算','taskType','OEM','phaseCode','ADVISE','plannedHours',16,'priority','MEDIUM','sortOrder',9),
    JSON_OBJECT('taskName','咨询报告交付','taskType','OEM','phaseCode','ADVISE','plannedHours',16,'priority','HIGH','sortOrder',10)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','网络现状调研报告','deliverableType','DOCUMENT','phaseCode','SURVEY','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','风险评估报告','deliverableType','DOCUMENT','phaseCode','ANALYSIS','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','架构优化建议书','deliverableType','DOCUMENT','phaseCode','ADVISE','mandatory',true,'approverRole','APPROVER'),
    JSON_OBJECT('deliverableName','演进路线规划','deliverableType','DOCUMENT','phaseCode','ADVISE','mandatory',false,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','网络现状调研','successorTaskName','性能数据分析','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','性能数据分析','successorTaskName','架构优化建议','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','架构优化建议','successorTaskName','咨询报告交付','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','ANALYSIS','approverRoles',JSON_ARRAY('TECH_LEAD')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','ADVISE','approverRoles',JSON_ARRAY('APPROVER'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','调研完成','milestoneType','SITE_SURVEY','phaseCode','SURVEY','sortOrder',1),
    JSON_OBJECT('milestoneName','分析完成','milestoneType','TESTING','phaseCode','ANALYSIS','sortOrder',2),
    JSON_OBJECT('milestoneName','报告交付','milestoneType','FINAL_ACCEPTANCE','phaseCode','ADVISE','sortOrder',3)
  )
) WHERE `id` = 6;

-- -------------------------------------------------------------
-- 模板 7：TPL-MIGRATION 数据中心迁移模板（4 阶段 / 14 任务 / 7 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','PLAN','phaseName','规划阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','PREPARE','phaseName','准备阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','MIGRATE','phaseName','迁移阶段','sortOrder',3,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','VERIFY','phaseName','验证阶段','sortOrder',4,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','迁移需求分析','taskType','AGENT','phaseCode','PLAN','plannedHours',24,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','迁移方案设计','taskType','OEM','phaseCode','PLAN','plannedHours',80,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','回退方案设计','taskType','OEM','phaseCode','PLAN','plannedHours',32,'priority','HIGH','sortOrder',3),
    JSON_OBJECT('taskName','新机房准备','taskType','AGENT','phaseCode','PREPARE','plannedHours',48,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','设备采购','taskType','OEM','phaseCode','PREPARE','plannedHours',16,'priority','MEDIUM','sortOrder',5),
    JSON_OBJECT('taskName','新设备部署','taskType','AGENT','phaseCode','PREPARE','plannedHours',80,'priority','HIGH','sortOrder',6),
    JSON_OBJECT('taskName','数据备份','taskType','AGENT','phaseCode','PREPARE','plannedHours',24,'priority','HIGH','sortOrder',7),
    JSON_OBJECT('taskName','业务割接','taskType','AGENT','phaseCode','MIGRATE','plannedHours',16,'priority','CRITICAL','sortOrder',8),
    JSON_OBJECT('taskName','设备迁移','taskType','AGENT','phaseCode','MIGRATE','plannedHours',48,'priority','HIGH','sortOrder',9),
    JSON_OBJECT('taskName','数据迁移','taskType','AGENT','phaseCode','MIGRATE','plannedHours',40,'priority','HIGH','sortOrder',10),
    JSON_OBJECT('taskName','应用切换','taskType','AGENT','phaseCode','MIGRATE','plannedHours',24,'priority','CRITICAL','sortOrder',11),
    JSON_OBJECT('taskName','业务验证','taskType','AGENT','phaseCode','VERIFY','plannedHours',32,'priority','HIGH','sortOrder',12),
    JSON_OBJECT('taskName','性能测试','taskType','OEM','phaseCode','VERIFY','plannedHours',24,'priority','HIGH','sortOrder',13),
    JSON_OBJECT('taskName','迁移验收','taskType','OEM','phaseCode','VERIFY','plannedHours',16,'priority','HIGH','sortOrder',14)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','迁移方案设计文档','deliverableType','DOCUMENT','phaseCode','PLAN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','回退方案','deliverableType','DOCUMENT','phaseCode','PLAN','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','新机房部署报告','deliverableType','DOCUMENT','phaseCode','PREPARE','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','数据备份清单','deliverableType','DOCUMENT','phaseCode','PREPARE','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','割接记录','deliverableType','DOCUMENT','phaseCode','MIGRATE','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','业务验证报告','deliverableType','DOCUMENT','phaseCode','VERIFY','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','迁移验收报告','deliverableType','DOCUMENT','phaseCode','VERIFY','mandatory',true,'approverRole','APPROVER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','迁移需求分析','successorTaskName','迁移方案设计','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','新机房准备','successorTaskName','新设备部署','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','数据备份','successorTaskName','业务割接','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','设备迁移','successorTaskName','应用切换','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','应用切换','successorTaskName','业务验证','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','PLAN','approverRoles',JSON_ARRAY('TECH_LEAD')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','PREPARE','approverRoles',JSON_ARRAY('PROJECT_MANAGER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','MIGRATE','approverRoles',JSON_ARRAY('APPROVER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','VERIFY','approverRoles',JSON_ARRAY('APPROVER'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','方案评审','milestoneType','NETWORK_DESIGN','phaseCode','PLAN','sortOrder',1),
    JSON_OBJECT('milestoneName','新机房就绪','milestoneType','INSTALLATION','phaseCode','PREPARE','sortOrder',2),
    JSON_OBJECT('milestoneName','割接完成','milestoneType','COMMISSIONING','phaseCode','MIGRATE','sortOrder',3),
    JSON_OBJECT('milestoneName','迁移验收','milestoneType','FINAL_ACCEPTANCE','phaseCode','VERIFY','sortOrder',4)
  )
) WHERE `id` = 7;

-- -------------------------------------------------------------
-- 模板 8：TPL-CORE-UPGRADE 核心网升级模板（草稿）（2 阶段 / 6 任务 / 2 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','DESIGN','phaseName','设计阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','UPGRADE','phaseName','升级阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','核心网现状评估','taskType','AGENT','phaseCode','DESIGN','plannedHours',40,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','升级方案设计','taskType','OEM','phaseCode','DESIGN','plannedHours',80,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','兼容性测试','taskType','AGENT','phaseCode','DESIGN','plannedHours',32,'priority','HIGH','sortOrder',3),
    JSON_OBJECT('taskName','核心网设备升级','taskType','AGENT','phaseCode','UPGRADE','plannedHours',48,'priority','CRITICAL','sortOrder',4),
    JSON_OBJECT('taskName','业务回归测试','taskType','AGENT','phaseCode','UPGRADE','plannedHours',32,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','升级验收','taskType','OEM','phaseCode','UPGRADE','plannedHours',16,'priority','HIGH','sortOrder',6)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','核心网升级方案','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','升级验收报告','deliverableType','DOCUMENT','phaseCode','UPGRADE','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','核心网现状评估','successorTaskName','升级方案设计','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','兼容性测试','successorTaskName','核心网设备升级','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','核心网设备升级','successorTaskName','业务回归测试','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','DESIGN','approverRoles',JSON_ARRAY('TECH_LEAD'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','方案评审','milestoneType','NETWORK_DESIGN','phaseCode','DESIGN','sortOrder',1),
    JSON_OBJECT('milestoneName','升级完成','milestoneType','FINAL_ACCEPTANCE','phaseCode','UPGRADE','sortOrder',2)
  )
) WHERE `id` = 8;

-- -------------------------------------------------------------
-- 模板 9：TPL-LEGACY-V1 旧版网络实施模板（已弃用，ARCHIVED）（4 阶段 / 12 任务 / 5 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','PREPARE','phaseName','准备阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','DESIGN','phaseName','设计阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','IMPLEMENT','phaseName','实施阶段','sortOrder',3,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','ACCEPT','phaseName','验收阶段','sortOrder',4,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','项目启动','taskType','OEM','phaseCode','PREPARE','plannedHours',8,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','需求收集','taskType','AGENT','phaseCode','PREPARE','plannedHours',24,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','方案设计','taskType','OEM','phaseCode','DESIGN','plannedHours',60,'priority','HIGH','sortOrder',3),
    JSON_OBJECT('taskName','设备清单','taskType','OEM','phaseCode','DESIGN','plannedHours',16,'priority','MEDIUM','sortOrder',4),
    JSON_OBJECT('taskName','设备部署','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',80,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','设备配置','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',40,'priority','HIGH','sortOrder',6),
    JSON_OBJECT('taskName','联调测试','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',40,'priority','HIGH','sortOrder',7),
    JSON_OBJECT('taskName','业务割接','taskType','AGENT','phaseCode','IMPLEMENT','plannedHours',16,'priority','CRITICAL','sortOrder',8),
    JSON_OBJECT('taskName','初验','taskType','OEM','phaseCode','ACCEPT','plannedHours',16,'priority','HIGH','sortOrder',9),
    JSON_OBJECT('taskName','终验','taskType','OEM','phaseCode','ACCEPT','plannedHours',16,'priority','HIGH','sortOrder',10),
    JSON_OBJECT('taskName','文档移交','taskType','OEM','phaseCode','ACCEPT','plannedHours',8,'priority','MEDIUM','sortOrder',11),
    JSON_OBJECT('taskName','培训','taskType','OEM','phaseCode','ACCEPT','plannedHours',8,'priority','LOW','sortOrder',12)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','实施方案','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','设备清单','deliverableType','DOCUMENT','phaseCode','DESIGN','mandatory',false,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','部署报告','deliverableType','DOCUMENT','phaseCode','IMPLEMENT','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','初验报告','deliverableType','DOCUMENT','phaseCode','ACCEPT','mandatory',true,'approverRole','PROJECT_MANAGER'),
    JSON_OBJECT('deliverableName','终验报告','deliverableType','DOCUMENT','phaseCode','ACCEPT','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','项目启动','successorTaskName','需求收集','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','方案设计','successorTaskName','设备部署','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','联调测试','successorTaskName','业务割接','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','业务割接','successorTaskName','初验','dependencyType','FS','lagDays',1)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','DESIGN','approverRoles',JSON_ARRAY('TECH_LEAD')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','IMPLEMENT','approverRoles',JSON_ARRAY('PROJECT_MANAGER')),
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','ACCEPT','approverRoles',JSON_ARRAY('APPROVER'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','方案评审','milestoneType','NETWORK_DESIGN','phaseCode','DESIGN','sortOrder',1),
    JSON_OBJECT('milestoneName','部署完成','milestoneType','INSTALLATION','phaseCode','IMPLEMENT','sortOrder',2),
    JSON_OBJECT('milestoneName','终验通过','milestoneType','FINAL_ACCEPTANCE','phaseCode','ACCEPT','sortOrder',3)
  )
) WHERE `id` = 9;

-- -------------------------------------------------------------
-- 模板 10：TPL-WLAN-BUILD 无线网络建设模板（草稿）（2 阶段 / 6 任务 / 3 交付件）
-- -------------------------------------------------------------
UPDATE `pms_project_template_version` SET `snapshot_json` = JSON_OBJECT(
  'phases', JSON_ARRAY(
    JSON_OBJECT('phaseCode','SURVEY','phaseName','勘测阶段','sortOrder',1,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',false,'requireApproval',false),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY())),
    JSON_OBJECT('phaseCode','DEPLOY','phaseName','部署阶段','sortOrder',2,
      'entryCriteria',JSON_OBJECT('requirePreviousPhaseComplete',true,'requireApproval',true),
      'exitCriteria',JSON_OBJECT('requiredDeliverables',JSON_ARRAY(),'requiredTasks',JSON_ARRAY(),'requiredMilestones',JSON_ARRAY(),'requiredApprovals',JSON_ARRAY()))
  ),
  'tasks', JSON_ARRAY(
    JSON_OBJECT('taskName','无线覆盖勘测','taskType','AGENT','phaseCode','SURVEY','plannedHours',40,'priority','HIGH','sortOrder',1),
    JSON_OBJECT('taskName','AP 点位规划','taskType','OEM','phaseCode','SURVEY','plannedHours',24,'priority','HIGH','sortOrder',2),
    JSON_OBJECT('taskName','无线方案设计','taskType','OEM','phaseCode','SURVEY','plannedHours',32,'priority','HIGH','sortOrder',3),
    JSON_OBJECT('taskName','AP 安装部署','taskType','AGENT','phaseCode','DEPLOY','plannedHours',60,'priority','HIGH','sortOrder',4),
    JSON_OBJECT('taskName','AC 配置','taskType','AGENT','phaseCode','DEPLOY','plannedHours',24,'priority','HIGH','sortOrder',5),
    JSON_OBJECT('taskName','覆盖优化测试','taskType','AGENT','phaseCode','DEPLOY','plannedHours',32,'priority','HIGH','sortOrder',6)
  ),
  'deliverables', JSON_ARRAY(
    JSON_OBJECT('deliverableName','无线勘测报告','deliverableType','DOCUMENT','phaseCode','SURVEY','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','无线方案设计','deliverableType','DOCUMENT','phaseCode','SURVEY','mandatory',true,'approverRole','TECH_LEAD'),
    JSON_OBJECT('deliverableName','覆盖优化报告','deliverableType','DOCUMENT','phaseCode','DEPLOY','mandatory',true,'approverRole','PROJECT_MANAGER')
  ),
  'dependencies', JSON_ARRAY(
    JSON_OBJECT('predecessorTaskName','无线覆盖勘测','successorTaskName','AP 点位规划','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','无线方案设计','successorTaskName','AP 安装部署','dependencyType','FS','lagDays',0),
    JSON_OBJECT('predecessorTaskName','AP 安装部署','successorTaskName','覆盖优化测试','dependencyType','FS','lagDays',0)
  ),
  'approvalPlans', JSON_ARRAY(
    JSON_OBJECT('approvalType','PHASE_EXIT','triggerPhaseCode','SURVEY','approverRoles',JSON_ARRAY('TECH_LEAD'))
  ),
  'milestones', JSON_ARRAY(
    JSON_OBJECT('milestoneName','勘测完成','milestoneType','SITE_SURVEY','phaseCode','SURVEY','sortOrder',1),
    JSON_OBJECT('milestoneName','部署完成','milestoneType','INSTALLATION','phaseCode','DEPLOY','sortOrder',2)
  )
) WHERE `id` = 10;

-- =============================================================
-- 修复说明：
--   模板 2-10 的 snapshot_json 已从非法整数计数（JSON_OBJECT('phases',5,...)）
--   替换为合法对象数组（JSON_OBJECT('phases',JSON_ARRAY(JSON_OBJECT(...)),...)）。
--   字段名对齐 com.dp.plat.common.dto.TemplateSnapshot：
--     PhaseDef:        phaseCode/phaseName/sortOrder/entryCriteria/exitCriteria
--     TaskDef:         taskName/taskType/parentTaskName/phaseCode/plannedHours/priority/sortOrder
--     DeliverableDef:  deliverableName/deliverableType/phaseCode/mandatory/approverRole
--     DependencyDef:   predecessorTaskName/successorTaskName/dependencyType/lagDays
--     ApprovalPlanDef: approvalType/triggerPhaseCode/approverRoles
--     MilestoneDef:    milestoneName/milestoneType/phaseCode/sortOrder
--   milestoneType 取值对齐 PpdiooPhase 12 节点枚举。
--   交付件类型引用字典 pms_deliverable_type（V86 定义）。
-- =============================================================
