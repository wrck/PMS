# PMS 迁移比对总报告 — Action→Controller 层

> 比对时间：2026-06-30
> 比对范围：24个 Struts Action → SpringBoot Controller/Service
> 比对方法：逐方法业务逻辑比对

---

## 总体统计

| 指标 | 数值 |
|------|------|
| 总业务方法数 | 302 |
| ✅ 完全迁移 | 72 (23.8%) |
| ⚠️ 部分迁移 | 130 (43.1%) |
| ❌ 未迁移 | 100 (33.1%) |
| **整体迁移完整度** | **~45%** |

---

## 分组详情

| 子代理 | 模块 | 方法数 | ✅ | ⚠️ | ❌ | 迁移率 |
|--------|------|--------|-----|-----|-----|--------|
| Group1 | BaseAction, BasicData, Cluster, DataAnalysis, Department, Login, OperateLog | 31 | 16 | 9 | 6 | 66% |
| Group2 | CallBackAction | 12 | 0 | 8 | 4 | 33% |
| Group3 | PmClosedLoop + PmClosedLoopQuesnaire | 29 | 0 | 1 | 28 | 2% |
| Group4 | ProjectAction | 52 | 20 | 23 | 9 | 61% |
| Group5 | Presales, Report, Subcontract | 78 | 10 | 36 | 32 | 29% |
| Group6 | RoleManage, Upload, UserManage, WorkFlow, WorkSpace, Maintenance, Certificate, ProbManage, Supervision, WarrantyCallback | 100 | 26 | 53 | 21 | 53% |

---

## 🔴 高风险未迁移项（按严重程度排序）

### 1. 工作流引擎（Activiti）完全缺失
- **影响范围**: WorkFlowAction(12方法)、PmClosedLoopAction(工作流路由)、SubcontractAction(savePayment)、CallBackAction(审批流程)
- **现状**: 所有工作流方法在SpringBoot中无对应实现，仅有简单数据库状态更新
- **影响**: 审批流程、任务委派、流程图查看、历史任务全部不可用

### 2. 问卷/评分系统完全未迁移
- **影响范围**: CallBackAction(quesMark/getQuesTypeScore)、PmClosedLoopQuesnaireAction(13方法全部空桩)、Maintenance/Supervision/WarrantyCallback的QuestionnarieUtil
- **现状**: 问卷创建、编辑、评分算法、通过/驳回判定均缺失
- **影响**: 回访问卷、闭环问卷功能完全不可用

### 3. 邮件通知系统全部注释/缺失
- **影响范围**: ProjectAction(15处邮件)、ProbManageAction(公告发布)、UserManageAction(用户开通)、MaintenanceAction(季度提醒)
- **现状**: MailUtil/NotificationTemplateUtil存在但未集成，所有邮件发送点均注释
- **影响**: 项目通知、任务分配提醒、超期预警全部不工作

### 4. 复杂权限系统大幅简化
- **影响范围**: 几乎所有Action
- **现状**: 老系统8-10种角色+办事处+团队成员+areapower数据权限，新系统多为简单CRUD权限
- **影响**: 数据隔离失效，用户可能看到非权限范围数据

### 5. 项目状态流转引擎未迁移
- **影响范围**: ProjectAction.updateProject()、updateprojectisback()
- **现状**: 30+种状态组合的流转规则引擎在SpringBoot中简化为简单状态更新
- **影响**: 项目生命周期管理逻辑不完整

### 6. 报表图表渲染未迁移
- **影响范围**: ReportAction(ECharts JSON构建)、DataAnalysisAction
- **现状**: 所有ECharts图表数据构建逻辑缺失
- **影响**: 报表页面无法正确渲染图表

---

## 🟡 中风险项

| 问题 | 影响模块 | 说明 |
|------|----------|------|
| 文件上传/下载不完整 | Upload, Project, Prob | downloadFile/uploadImage/queryFile未迁移 |
| N+1查询问题 | CallBack, Presales | 选项逐个查询改为批量查询但实现不完整 |
| 数据导入/导出缺失 | Prob(产品/组件导入)、Project(批量创建) | Excel导入逻辑未迁移 |
| 发票识别逻辑缺失 | Subcontract | 付款发票识别/去重/金额计算未迁移 |
| 统计分析简化 | Report, Prob | 多维度动态统计简化为简单分组 |
| OA/D365数据同步 | Presales(syncOaData) | 外部系统集成未迁移 |

---

## 详细报告索引

| 文件 | 内容 |
|------|------|
| COMPARE_GROUP1.md | BaseAction, BasicDataManage, Cluster, DataAnalysis, Department, Login, OperateLog |
| COMPARE_GROUP2.md | CallBackAction |
| COMPARE_GROUP3.md | PmClosedLoopAction, PmClosedLoopQuesnaireAction |
| COMPARE_GROUP4.md | ProjectAction |
| COMPARE_GROUP5.md | PresalesAction, ReportAction, SubcontractAction |
| COMPARE_GROUP6.md | RoleManage, Upload, UserManage, WorkFlow, WorkSpace, Maintenance, Certificate, ProbManage, Supervision, WarrantyCallback |

---

## 待完成比对

- [ ] **Service 层**: Struts Service/ServiceImpl → SpringBoot Service/ServiceImpl (14,232行)
- [ ] **Dao 层**: Struts Dao/DaoImpl + iBATIS XML → SpringBoot Mapper + MyBatis XML (4,310行 + 29,319行SQL)
