# 项目维护与其他表结构

> 数据库：dppms_d365 (MySQL)  
> 模块：项目维护、监督、质保、通知、报表、工作流

---

## 1. pm_project_maintenance（项目维护表）

项目维护/巡检任务主表，记录售后维护任务信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL, FK | - | 项目ID，关联pm_project_header.projectId |
| projectCode | VARCHAR(50) | - | NULL | 项目编码 |
| projectName | VARCHAR(500) | - | NULL | 项目名称 |
| projectType | INT | - | NULL | 项目类型（10:售后 20:售前） |
| projectExecutionState | VARCHAR(50) | - | NULL | 项目实施状态 |
| contractNo | VARCHAR(200) | - | NULL | 合同号 |
| officeCode | VARCHAR(50) | - | NULL | 办事处编码 |
| compId | VARCHAR(50) | - | NULL | 所属公司编码 |
| type | VARCHAR(50) | - | NULL | 任务性质 |
| category | VARCHAR(100) | - | NULL | 任务分类 |
| subCategory | VARCHAR(100) | - | NULL | 任务小类 |
| processTime | DATETIME | - | NULL | 处理时间 |
| processDesc | TEXT | - | NULL | 事项描述 |
| processStep | TEXT | - | NULL | 解决进展 |
| remainProblem | TEXT | - | NULL | 遗留问题 |
| transitHour | FLOAT | - | NULL | 在途耗时（小时） |
| processHour | FLOAT | - | NULL | 处理耗时（小时） |
| itemModel | VARCHAR(200) | - | NULL | 产品型号 |
| softVersion | VARCHAR(200) | - | NULL | 在网版本 |
| enabledFeatures | TEXT | - | NULL | 启用功能 |
| customTos | VARCHAR(1000) | - | NULL | 自定义主送（邮件地址列表） |
| customCcs | VARCHAR(1000) | - | NULL | 自定义抄送（邮件地址列表） |
| hasReport | BOOLEAN | - | FALSE | 是否有巡检报告 |
| quesnaireId | INT | - | NULL | 问卷ID |
| deliverFileIds | VARCHAR(500) | - | NULL | 交付件文件ID列表 |
| warrantyStatus | VARCHAR(50) | - | NULL | 质保状态 |
| industryName | VARCHAR(200) | - | NULL | 行业名称 |
| userOffice | VARCHAR(50) | - | NULL | 用户办事处 |
| year | INT | - | NULL | 年度 |
| quarter | INT | - | NULL | 季度 |
| month | INT | - | NULL | 月份 |
| wsCount | INT | - | 0 | WS设备数量 |
| wafCount | INT | - | 0 | WAF设备数量 |
| wsYearCount | INT | - | 0 | WS年度设备数量 |
| wafYearCount | INT | - | 0 | WAF年度设备数量 |
| warrantyInfo | TEXT | - | NULL | 质保信息 |
| serviceInfo | TEXT | - | NULL | 服务信息 |
| remark | TEXT | - | NULL | 备注 |
| customInfo | JSON | - | NULL | 自定义扩展信息（JSON格式） |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(100) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |
| effectiveFrom | DATETIME | - | NULL | 生效开始时间 |
| effectiveTo | DATETIME | - | NULL | 生效结束时间 |

---

## 2. pm_project_maintenance_service_delivery（维护服务交付表）

项目维护服务交付明细表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| maintenanceId | INT | NOT NULL, FK | - | 维护ID，关联pm_project_maintenance.id |
| serviceType | VARCHAR(50) | - | NULL | 服务类型 |
| serviceContent | TEXT | - | NULL | 服务内容 |
| serviceResult | TEXT | - | NULL | 服务结果 |
| deliverFileIds | VARCHAR(500) | - | NULL | 交付件文件ID列表 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 3. pm_project_supervision（项目监督表）

项目监督/督办信息表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL, FK | - | 项目ID |
| projectCode | VARCHAR(50) | - | NULL | 项目编码 |
| projectName | VARCHAR(500) | - | NULL | 项目名称 |
| officeCode | VARCHAR(50) | - | NULL | 办事处编码 |
| type | VARCHAR(50) | - | NULL | 任务性质 |
| channel | VARCHAR(200) | - | NULL | 代理商/服务商 |
| processTime | DATETIME | - | NULL | 处理时间 |
| state | BOOLEAN | - | FALSE | 是否完成 |
| isDelete | BOOLEAN | - | FALSE | 是否删除 |
| quesnaireId | INT | - | NULL | 问卷ID |
| deliverFileIds | VARCHAR(500) | - | NULL | 交付件文件ID列表 |
| remark | TEXT | - | NULL | 备注 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(100) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |

---

## 4. pm_project_warranty_callback（项目质保回访表）

项目质保回访记录表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL, FK | - | 项目ID |
| warrantyStatus | VARCHAR(50) | - | NULL | 质保状态 |
| warrantyGrade | VARCHAR(50) | - | NULL | 质保等级 |
| wafService | VARCHAR(50) | - | NULL | WAF服务标志 |
| callbackBy | VARCHAR(100) | - | NULL | 回访人 |
| callbackTime | DATETIME | - | NULL | 回访时间 |
| callbackResult | TEXT | - | NULL | 回访结果 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 5. pm_notification_template（通知模板表）

系统通知/邮件模板表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| templateCode | VARCHAR(100) | NOT NULL, UNIQUE | - | 模板编码 |
| templateName | VARCHAR(200) | NOT NULL | - | 模板名称 |
| templateSubject | VARCHAR(500) | - | NULL | 邮件主题模板 |
| templateContent | LONGTEXT | - | NULL | 邮件内容模板（支持变量替换） |
| templateType | VARCHAR(50) | - | NULL | 模板类型 |
| status | INT | - | 1 | 状态（1:启用 0:禁用） |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(100) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |

**常用模板编码：**
- 项目创建通知
- 成员变更通知
- 技术公告发布通知
- 转包服务商通知
- 质保到期提醒

---

## 6. pm_report_line_data（报表行数据表）

报表数据行存储表，用于动态报表数据存储。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| reportCode | VARCHAR(100) | NOT NULL | - | 报表编码 |
| lineData | JSON | - | NULL | 行数据（JSON格式） |
| lineType | VARCHAR(50) | - | NULL | 行类型 |
| sortId | INT | - | 0 | 排序序号 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 7. dp_act_unify_task（统一工作流任务表）

Activiti工作流统一任务视图表，整合各业务流程的任务信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| taskId | VARCHAR(100) | NOT NULL | - | Activiti任务ID |
| originTaskId | VARCHAR(100) | - | NULL | 原始任务ID |
| procInstId | VARCHAR(100) | - | NULL | 流程实例ID |
| processKey | VARCHAR(100) | - | NULL | 流程定义Key |
| taskKey | VARCHAR(100) | - | NULL | 任务定义Key |
| taskName | VARCHAR(200) | - | NULL | 任务名称 |
| eventType | VARCHAR(50) | - | NULL | 事件类型 |
| title | VARCHAR(500) | - | NULL | 任务标题 |
| assignee | VARCHAR(100) | - | NULL | 当前办理人 |
| formUrl | VARCHAR(500) | - | NULL | 表单URL |
| beginTime | DATETIME | - | NULL | 任务开始时间 |
| endTime | DATETIME | - | NULL | 任务结束时间 |
| dueTime | DATETIME | - | NULL | 截止时间 |
| state | INT | - | 0 | 任务状态（0:进行中 1:已完成 2:已终止） |
| subState | INT | - | NULL | 子状态 |
| success | INT | - | NULL | 是否成功完成（0:否 1:是） |
| message | TEXT | - | NULL | 消息/备注 |
| latest | INT | - | 1 | 是否最新记录（1:是 0:否） |
| pushSender | VARCHAR(100) | - | NULL | 推送发送者 |
| pushData | TEXT | - | NULL | 推送数据 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

**常用 processKey：**

| processKey | 含义 |
|-----------|------|
| PmClosedLoop | 项目闭环流程 |
| SubcontractInspection | 转包验收流程 |
| PresalesProject | 售前项目流程 |

---

## 表间关系概览

```
pm_project_header (1) ──→ (N) pm_project_maintenance              通过 projectId
pm_project_header (1) ──→ (N) pm_project_supervision              通过 projectId
pm_project_header (1) ──→ (N) pm_project_warranty_callback        通过 projectId

pm_project_maintenance (1) ──→ (N) pm_project_maintenance_service_delivery  通过 maintenanceId

dp_act_unify_task ──→ 各业务表  通过 procInstId 关联流程实例
                   ──→ pm_project_header  通过业务Key解析
```
