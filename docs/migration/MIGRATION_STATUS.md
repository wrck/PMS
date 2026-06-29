# PMS 迁移完成度报告

> 更新时间：2026-06-29

## 总体统计

| 指标 | 数量 |
|------|------|
| Entity | 44个 |
| Mapper | 45个 |
| Service接口 | 22个 |
| ServiceImpl | 22个 |
| Controller | 22个 |
| DTO | 9个 |
| VO | 9个 |
| **API端点** | **184个** |

## 模块完成度

| 模块 | Controller | 端点数 | 完成度 | 说明 |
|------|-----------|--------|--------|------|
| 项目管理 | PmsProjectController | 44 | ✅ 90% | 基础CRUD+成员+合同+发货+软件版本+维护+批示+拆分合并 |
| 技术公告 | ProbController | 17 | ✅ 85% | CRUD+软件版本+恢复任务+产品+阅读日志+审核+统计 |
| 售前项目 | PmsPresalesController | 20 | ✅ 85% | CRUD+多级审批+产品+任务+交付件+导出 |
| 分包管理 | SubcontractController | 23 | ✅ 80% | CRUD+设备行+交付件+付款+服务商+流程+回访 |
| 闭环管理 | PmClosedLoopController | 4 | ⚠️ 60% | 基础CRUD+审批，复杂多级审批待补 |
| 回访管理 | CallBackController | 6 | ⚠️ 70% | CRUD+流程+审批+重新提交 |
| 质保回调 | WarrantyCallbackController | 7 | ✅ 75% | CRUD+项目查询+客户查询 |
| 运维管理 | MaintenanceController | 5 | ⚠️ 60% | 基础CRUD |
| 监理管理 | SupervisionController | 5 | ⚠️ 60% | 基础CRUD |
| 证书管理 | CertificateController | 4 | ⚠️ 60% | 基础CRUD |
| 周报管理 | WeeklyController | 10 | ✅ 90% | CRUD+提交+内容+反馈 |
| 任务管理 | ProjectTaskController | 3 | ✅ 80% | CRUD |
| 交付管理 | ProjectDeliverController | 4 | ✅ 80% | CRUD |
| 文件管理 | FileController | 3 | ✅ 80% | 上传+删除+查询 |
| 认证登录 | AuthController | 3 | ✅ 90% | 登录+登出+用户信息 |
| 用户管理 | SysUserController | 5 | ✅ 90% | CRUD+密码重置 |
| 角色管理 | SysRoleController | 4 | ✅ 90% | CRUD |
| 部门管理 | SysDeptController | 5 | ✅ 85% | CRUD+刷新 |
| 基础数据 | BasicDataController | 4 | ✅ 85% | CRUD |
| 操作日志 | OperateLogController | 2 | ⚠️ 70% | 列表+导出 |
| 通知管理 | NotificationController | 3 | ✅ 80% | 列表+未读数+已读 |
| 工作台 | WorkSpaceController | 3 | ⚠️ 50% | 骨架 |

## 未迁移模块

| 模块 | 说明 | 优先级 |
|------|------|--------|
| 报表统计 | ReportAction (11个方法) | P2 |
| 工作流引擎 | WorkFlowAction (15个方法) | P3 |
| 数据分析 | DataAnalysisAction | P3 |
| 缓存管理 | ClusterAction | P3 |
| 定时任务 | 42个Job类 | P3 |
| 数据同步 | 20+个同步Job | P3 |
| 邮件服务 | SendMailService + 11个邮件Job | P3 |
| 回访问卷 | PmClosedLoopQuesnaireAction (13个方法) | P2 |

## 新增Entity清单

### P0 第一批
- PmsInstruction (项目批示)
- PmsProjectContract (项目合同)
- PmsProjectGroupRelationship (项目组关系)
- PmsProjectProductLine (项目产品线)

### P0 第二批
- PmsProbSoftVersion (技术公告软件版本)
- PmsProbRestore (技术公告恢复任务)
- PmsProbProduct (技术公告产品)
- PmsProbReadLog (技术公告阅读日志)

### P0 第三批
- PmsPresalesProduct (售前产品)
- PmsPresalesTask (售前任务)
- PmsPresalesComment (售前审批意见)

### P1 第四批
- PmsSubcontractLine (分包设备行)
- PmsSubcontractDeliver (分包交付件)
- PmsSubcontractPayment (分包付款)
- PmsSubcontractFacilitator (服务商)

### P1 第六批
- PmsWarrantyCallback (质保回访)
