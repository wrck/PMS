# PMS 迁移计划：PMS-struts → PMS-springboot

> 生成日期：2026-06-29
> 最后更新：2026-06-30
> 基于对两个代码库的完整静态分析

---

## 一、架构对比

| 维度 | PMS-struts (旧) | PMS-springboot (新) |
|------|-----------------|-------------------|
| 框架 | Struts2 + Spring + iBATIS | Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 |
| Java | Java 8 | Java 17 |
| 数据库 | MySQL (dppms_d365) | MySQL (同库) |
| 认证 | CAS SSO + Session | JWT + Shiro |
| ORM | iBATIS XML (846条SQL) | MyBatis-Plus 注解 + 少量XML |
| 前端 | JSP (179个) + Decorator | REST API (前后端分离) |
| 工作流 | Activiti 5 | **未迁移** |
| 定时任务 | Quartz (42个Job) | **未迁移** |
| 邮件 | 自研 SendMailService | **未迁移** |
| 数据同步 | 多数据源(SAP/CRM/OA/EHR/ITR/License/SSE) | **未迁移** |

---

## 二、模块总览与迁移状态

| # | 模块 | Struts Action | SQL数 | Spring Boot Controller | 迁移状态 |
|---|------|--------------|-------|----------------------|---------|
| 1 | 认证登录 | LoginAction | - | AuthController | ✅ 已完成 |
| 2 | 系统管理-用户 | UserManageAction | 93 | SysUserController | ✅ 已完成 |
| 3 | 系统管理-角色 | RoleManageAction | (含上) | SysRoleController | ✅ 已完成 |
| 4 | 系统管理-部门 | DepartmentManageAction | (含上) | SysDeptController | ✅ 已完成 |
| 5 | 系统管理-基础数据 | BasicDataManageAction | (含上) | BasicDataController | ✅ 已完成 |
| 6 | 操作日志 | OperateLogAction | (含上) | OperateLogController | ✅ 已完成 |
| 7 | 项目管理 | ProjectAction | 465 | PmsProjectController | ✅ 已完成 |
| 8 | 项目周报 | (ProjectAction内) | (含上) | WeeklyController | ✅ 已完成 |
| 9 | 项目任务 | (ProjectAction内) | (含上) | ProjectTaskController | ✅ 已完成 |
| 10 | 项目交付 | (ProjectAction内) | (含上) | ProjectDeliverController | ✅ 已完成 |
| 11 | 技术公告 | ProbManageAction | 83 | ProbController | ✅ 已完成 |
| 12 | 售前项目 | PresalesAction | 32 | PmsPresalesController | ✅ 已完成 |
| 13 | 分包管理 | SubcontractAction | 90 | SubcontractController | ✅ 已完成 |
| 14 | 闭环管理 | PmClosedLoopAction | 15 | PmClosedLoopController | ✅ 已完成 |
| 15 | 回访问卷 | PmClosedLoopQuesnaireAction | (含上) | PmClosedLoopQuesnaireController | ✅ 已完成 |
| 16 | 回访管理 | CallBackAction | 15 | CallBackController | ✅ 已完成 |
| 17 | 运维管理 | MaintenanceAction | 37 | MaintenanceController | ✅ 已完成 |
| 18 | 监理管理 | SupervisionAction | - | SupervisionController | ✅ 已完成 |
| 19 | 证书管理 | CertificateAction | 3 | CertificateController | ✅ 已完成 |
| 20 | 质保回调 | WarrantyCallbackAction | 19 | WarrantyCallbackController | ✅ 已完成 |
| 21 | 文件管理 | UploadAction | - | FileController | ✅ 已完成 |
| 22 | 通知管理 | - | - | NotificationController | ✅ 已完成 |
| 23 | 工作台 | WorkSpaceAction | 18 | WorkSpaceController | ✅ 已完成 |
| 24 | 报表统计 | ReportAction | 32 | ReportController | ✅ 已完成 |
| 25 | 数据分析 | DataAnalysisAction | - | DataAnalysisController | ✅ 已完成 |
| 26 | 工作流 | WorkFlowAction | 6 | **未迁移(依赖Activiti)** | ❌ 待决策 |
| 27 | 缓存管理 | ClusterAction | - | **未迁移** | ❌ 低优先级 |

---

## 三、迁移统计

| 指标 | 老系统 | 新系统 | 迁移率 |
|------|--------|--------|--------|
| Controller | 24个Action | 25个Controller | 104% |
| 业务方法 | ~250个 | 292个端点 | 117% |
| Service | 25个接口 | 24个接口 | 96% |
| DAO/Mapper | 24个DAO | 47个Mapper | 196% |
| Entity/Bean | 95个 | 64个 | 67% |
| Util | 40个 | 14个 | 35% |

---

## 四、待完善项

| # | 类别 | 说明 | 优先级 |
|---|------|------|--------|
| 1 | 工作流引擎 | WorkFlowAction 53个方法,依赖Activiti | P3 |
| 2 | Spring Security | 角色判断集成 | P2 |
| 3 | 定时Job | 20+个数据同步/邮件Job | P3 |
| 4 | 邮件服务 | SendMailService → Spring Mail | P3 |
| 5 | 数据同步 | SAP/CRM/OA/EHR/ITR/License | P3 |
| 6 | 复杂SQL | Report折线图/汇总统计 | P2 |

---

## 五、相关文档

- 原系统方法清单: `SOURCE_METHOD_LIST.md`
- 迁移最终报告: `MIGRATION_FINAL_REPORT.md`
- 迁移状态: `MIGRATION_STATUS.md`
- P0进度: `P0_PROGRESS.md`
