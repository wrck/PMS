# 006-maintenance-supervision 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts maintenance/supervision 包,日期 2026-07-09
> 域职责:维保计划与执行、维保报表、项目督查、借用维保额度

---

## 第1章 用户故事

### US-01 维保记录录入(服务经理/项目经理)
作为服务经理或项目经理,我希望能够为售后/售前项目录入维保服务记录(含事项描述、处理进展、遗留问题、耗时、产品型号、在网版本等),以便沉淀项目维保执行过程并支撑日报与季报。
- 证据:`MaintenanceAction.createProjectMaintenance`(`PMS-struts/src/com/dp/plat/maintenance/action/MaintenanceAction.java:218-430`);路由 `maintenance_*`(struts-sys.xml:490-493)。

### US-02 维保记录查询与导出(项目干系人)
作为项目干系人(服务经理、项目经理、工程经理、区域负责人、回调专员、项目管理员/查看者),我希望按项目、办事处、任务分类、维保状态、时间区间等条件查询维保记录并导出,以便掌握维保执行情况。
- 证据:`MaintenanceAction.execute/projectMaintenance`(`MaintenanceAction.java:104-216`);权限角色校验(`MaintenanceAction.java:144-151`)。

### US-03 维保日报邮件(维保记录创建人)
作为维保记录创建人,我希望系统每日自动汇总我前一天创建的维保记录,并向项目服务经理、项目经理、销售、区域主任、办事处秘书及自定义主送/抄送人发送日报邮件,以便各方及时了解维保进展。
- 证据:`MaintenanceDailyReportMailer.execute`(`PMS-struts/src/com/dp/plat/maintenance/quartz/MaintenanceDailyReportMailer.java:50-134`)。

### US-04 维保服务季度交付提醒(服务经理)
作为服务经理,我希望系统按季度检查策略调优服务、高级维保服务等服务类型的交付完成情况,并在未完成或临近时发送提醒邮件,以便保障服务交付次数达标。
- 证据:`MaintenanceServiceQuarterMailer.execute`(`PMS-struts/src/com/dp/plat/maintenance/quartz/MaintenanceServiceQuarterMailer.java:46-120`)。

### US-05 服务交付查询(服务经理/管理员)
作为服务经理或管理员,我希望按服务季度、服务类型查询项目服务交付情况(含已交付次数、季度是否完成),以便跟踪服务交付进度。
- 证据:`MaintenanceAction.serviceDelivery`(`MaintenanceAction.java:432-477`)。

### US-06 项目督查记录录入(服务经理/项目经理)
作为服务经理或项目经理,我希望为售后项目录入督查记录(含代理商/服务商、任务性质、问卷),以便记录督查过程并完成问卷提交。
- 证据:`SupervisionAction.createProjectSupervision`(`PMS-struts/src/com/dp/plat/supervision/action/SupervisionAction.java:157-234`);路由 `supervision_*`(struts-sys.xml:496-499)。

### US-07 项目督查记录删除(创建人/工程经理)
作为督查记录创建人或工程经理,我希望对未完成的督查记录进行软删除,以便清理无效记录。
- 证据:`SupervisionAction.deleteProjectSupervision`(`SupervisionAction.java:236-251`)。

### US-08 督查权限用户查询(管理员)
作为管理员,我希望查询具有服务经理和项目经理角色的用户列表,以便分配督查相关权限。
- 证据:`SupervisionAction.queryPowerUser`(`SupervisionAction.java:253-278`)。

### US-09 借用维保额度管理(管理员)
作为管理员,我希望维护借用维保额度(新增、查看、更新、删除),并在新增/更新时校验额度是否重复,以便管理跨项目的维保额度借用。
- 证据:路由 `LendMaintenance/AddLendMaintenance/SeeLendMaintenance/UpdateLendMaintenance/DeleteLendMaintenance/IsLendQuotaRepeat`(struts-sys.xml:928-962)。`[待澄清]` Action 实现类与数据表未见。

### US-10 项目实施状态联动(系统)
作为系统,我希望在新增或更新最新一条维保记录时自动更新项目实施状态,以便项目状态与维保进展保持一致。
- 证据:`MaintenanceAction.createProjectMaintenance` 状态更新段(`MaintenanceAction.java:391-398`);状态变更联动 `ProjectStateUpdateAspect`(`PMS-struts/src/com/dp/plat/maintenance/aop/ProjectStateUpdateAspect.java:89-130`)。

---

## 第2章 功能需求

### 2.1 维保计划/执行

#### FR-2.1.1 维保记录列表查询
- 系统应支持按项目类型(售后10/售前20/非业务30/自定义40)、项目编号、项目名称、办事处、任务性质、任务分类、任务小类、维保状态、维保级别、增值服务、处理时间区间、创建时间区间等条件分页查询维保记录。
- 非管理员角色应叠加区域权限(areapower)与人员权限(userPower)过滤;服务经理需校验其作为服务经理的人员权限。
- 证据:`MaintenanceAction.projectMaintenance`(`MaintenanceAction.java:142-216`);查询入口 `selectProjectMaintenanceMapList`(sql-map-maintenance-config.xml:783)。

#### FR-2.1.2 维保记录录入/编辑
- 系统应支持为售后项目(projectType=10)、售前项目(projectType=20)、非业务类(projectType=30)、自定义(projectType=40)录入维保记录。
- 除非业务类外,录入时需关联维护问卷(projectMaintenance 类型问卷),问卷支持草稿与提交两种状态,提交时计算分数。
- 录入时应携带项目维保状态、维保级别、增值服务信息(查询自项目维保状态)。
- 编辑时校验是否本人操作(创建人一致),非本人应跳转重定向。
- 支持复制新增(isCopy):清空主键、交付件、自定义信息、问卷ID。
- 证据:`MaintenanceAction.createProjectMaintenance`(`MaintenanceAction.java:218-430`)。

#### FR-2.1.3 维保记录交付件上传
- 系统应支持为维保记录上传交付件,交付件类型按项目类型(projectType+1)归类。
- 支持按事件节点(eventKey,格式 dataTypeCode-basicDataId)查询可上传交付件清单。
- 支持查询交付件清单(commonUpload)与回单表单(returnForm)两种模式。
- 证据:`MaintenanceAction.createProjectMaintenance` 交付件段(`MaintenanceAction.java:400-426`);`MaintenanceAction.toUploadFile/uploadFileList`(`MaintenanceAction.java:479-563`)。

#### FR-2.1.4 维保记录权限控制
- 可访问角色:项目管理员、项目查看者、服务经理、项目经理、工程经理、工程经理负责人、区域负责人、回调专员、系统管理员;售前额外允许售前人员。
- 数据权限:非管理员按区域权限(areapower)与项目成员(服务经理/项目经理/项目经理B/团队成员)过滤。
- 证据:`MaintenanceAction.projectMaintenance`(`MaintenanceAction.java:144-196`);`createProjectMaintenance`(`MaintenanceAction.java:231-262`)。

### 2.2 维保报表

#### FR-2.2.1 维保日报邮件(定时调度)
- 系统应每日定时调度,汇总前一天各创建人的维保记录,按创建人分组生成日报邮件。
- 邮件主送:项目有效成员(角色10/20/30,即销售/服务经理/项目经理)+ 维保记录自定义主送人。
- 邮件抄送:区域主任(ROLE_AREA_LEADER,按办事处匹配)、办事处秘书(从 SSE 同步)、维保记录自定义抄送人、系统参数 `maintenance.mail` 配置的群组邮箱。
- 邮件正文使用通知模板 `maintenanceDailyReportTable`(表格行模板)与 `maintenanceDailyReportInfo`(整体模板)渲染。
- 邮箱需匹配正则 `^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@(dptech.com|dp.com)?$`,不匹配的过滤。
- 调度完成后调用存储过程 `queryProjectMaintenanceInfo(1)` 进行每日数据固化。
- 证据:`MaintenanceDailyReportMailer.execute/infoMaintenanceDailyReport`(`MaintenanceDailyReportMailer.java:50-331`);存储过程调用(`MaintenanceDailyReportMailer.java:108-133`)。

#### FR-2.2.2 办事处秘书同步(定时调度)
- 系统应定时从 SSE 数据源同步办事处秘书清单(查询 `querySSEDepartmentSectaryList`,先清空 `deleteSSEDepartmentSectary` 再插入),以便日报邮件抄送秘书。
- 证据:`MaintenanceDepartmentSectaryJob.execute`(`PMS-struts/src/com/dp/plat/maintenance/quartz/MaintenanceDepartmentSectaryJob.java:23-42`)。

#### FR-2.2.3 维保服务季度交付提醒(定时调度)
- 系统应定时调度,按系统参数 `pm.project.maintenance.serviceDelivery.deliverFile` 配置的服务类型(策略调优服务、高级维保服务等)检查交付完成情况。
- 检查逻辑:查询项目维保状态临时表,对每个服务类型判断是否在服务期限内(enable),统计近一年已交付次数(count)与季度次数(quarterCount),与年服务次数(yearCount)比对后发送提醒邮件。
- 使用通知模板 `maintenanceServiceReportInfo`。
- 证据:`MaintenanceServiceQuarterMailer.execute`(`MaintenanceServiceQuarterMailer.java:46-120`)。

#### FR-2.2.4 服务交付查询/导出
- 系统应支持按服务季度(默认当前季度)、服务日期、服务类型查询项目服务交付情况,并支持导出。
- 非管理员叠加区域权限过滤。
- 查询使用维保状态临时表(temp_project_warranty_state)。
- 证据:`MaintenanceAction.serviceDelivery`(`MaintenanceAction.java:432-477`);查询 `selectProjectMaintenanceServiceDeliveryMapList`(sql-map-maintenance-config.xml:1950)。

### 2.3 项目督查

#### FR-2.3.1 督查记录列表查询
- 系统应支持按项目编号、项目名称、办事处、任务性质、代理商/服务商、处理时间区间、状态、创建人等条件分页查询督查记录(默认过滤未删除 isDelete=false)。
- 非管理员叠加区域权限与人员权限过滤。
- 支持无分页导出(pagesize=-1)。
- 证据:`SupervisionAction.projectSupervision`(`SupervisionAction.java:108-155`);查询 `selectProjectSupervisionMapList`(sql-map-project-config2.xml:5025)。

#### FR-2.3.2 督查记录录入/编辑
- 系统应支持为售后项目录入督查记录,需关联督查问卷(projectSupervision 类型问卷)。
- 问卷提交时设置 state=true(已完成),草稿不设置。
- 录入时自动填充项目编号、项目名称、办事处(取项目 column001)。
- 证据:`SupervisionAction.createProjectSupervision`(`SupervisionAction.java:157-234`)。

#### FR-2.3.3 督查记录软删除
- 系统应支持对未完成(state=false)的督查记录进行软删除(isDelete=true)。
- 删除权限:创建人或工程经理/工程经理负责人。
- 证据:`SupervisionAction.deleteProjectSupervision`(`SupervisionAction.java:236-251`)。

#### FR-2.3.4 督查权限用户查询
- 系统应支持查询具有服务经理(ROLE_SERVICEMANAGER)或项目经理(ROLE_PROGRAMMANAGER)角色的用户列表(去重),返回 JSON。
- 证据:`SupervisionAction.queryPowerUser`(`SupervisionAction.java:253-278`)。

#### FR-2.3.5 督查记录权限控制
- 可访问角色同 FR-2.1.4(售前督查不涉及,仅售后项目)。
- 数据权限:非管理员按区域权限与项目成员(服务经理/项目经理/项目经理B)过滤。
- 证据:`SupervisionAction.projectSupervision`(`SupervisionAction.java:110-147`)。

### 2.4 借用维保额度

#### FR-2.4.1 借用维保额度列表
- 系统应提供借用维保额度列表页面。
- 证据:路由 `LendMaintenance`→`/sys/lendmaintenance.jsp`(struts-sys.xml:928-932)。`[待澄清]`

#### FR-2.4.2 借用维保额度新增
- 系统应支持新增借用维保额度,新增成功后重定向回列表页。
- 证据:路由 `AddLendMaintenance` method=`addQuota`→`/sys/sub/addlendmaintenance.jsp`(struts-sys.xml:933-940)。`[待澄清]`

#### FR-2.4.3 借用维保额度重复校验
- 系统应支持异步校验额度是否重复,返回 JSON(message)。
- 证据:路由 `IsLendQuotaRepeat` method=`isQuotaRepeat`,JSON 输出 `message`(struts-sys.xml:941-945)。`[待澄清]`

#### FR-2.4.4 借用维保额度查看/更新
- 系统应支持查看额度详情(跳转查看页或更新页)与更新,更新成功后重定向回列表页。
- 证据:路由 `SeeLendMaintenance` method=`seeQuota`、`UpdateLendMaintenance` method=`updateQuota`(struts-sys.xml:946-957)。`[待澄清]`

#### FR-2.4.5 借用维保额度删除
- 系统应支持删除借用维保额度,返回 JSON(message)。
- 证据:路由 `DeleteLendMaintenance` method=`deleteQuota`,JSON 输出 `message`(struts-sys.xml:958-962)。`[待澄清]`

---

## 第3章 数据契约【最关键】

> 字段分级说明:
> - **C**(Create/Write):创建或更新时写入的字段
> - **I**(Input/Query):作为查询条件输入的字段
> - **D**(Display):查询结果展示或返回的字段
> - 一个字段可同时具备多种分级(如 C/I/D)

### 3.1 表 `pm_project_maintenance`(项目维保记录)

> 证据:`sql-map-maintenance-config.xml:5-50`(resultMap)、`51-80`(insert)、`414-555`(insertOrUpdate)、`558-562`(maxId)、`783`(mapList)、`1034-1106`(view 查询)、`1578-1581`(维保状态)。

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER | C/I/D | 主键,自增 |
| projectId | INTEGER | C/I/D | 项目头信息主键(售后=projectId,售前=presalesId,非业务/自定义=-1) |
| projectCode | VARCHAR | C/I/D | 项目编号 |
| projectName | VARCHAR | C/I/D | 项目名称 |
| projectType | INTEGER | C/I/D | 项目类型:10=售后,20=售前,30=非业务,40=自定义 |
| projectExecutionState | VARCHAR | C/I/D | 项目实施状态(联动更新项目头) |
| contractNo | VARCHAR | C/I/D | 合同号 |
| officeCode | VARCHAR | C/I/D | 办事处编码(取项目 column001) |
| compId | INTEGER | C/I/D | 所属公司 |
| type | VARCHAR | C/I/D | 任务性质(基础数据 maintenanceType) |
| category | VARCHAR | C/I/D | 任务分类(基础数据 maintenanceCategory) |
| subCategory | VARCHAR | C/I/D | 任务小类(基础数据 maintenanceSubCategory) |
| processTime | TIMESTAMP | C/I/D | 处理时间(自动派生 year/quarter/month) |
| processDesc | VARCHAR | C/I/D | 事项描述 |
| processStep | VARCHAR | C/I/D | 解决进展 |
| remainProblem | VARCHAR | C/I/D | 遗留问题 |
| transitHour | REAL | C/I/D | 在途耗时(h) |
| processHour | REAL | C/I/D | 处理耗时(h) |
| itemModel | VARCHAR | C/I/D | 产品型号 |
| softVersion | VARCHAR | C/I/D | 在网版本 |
| enabledFeatures | VARCHAR | C/I/D | 启用功能 |
| customTos | VARCHAR | C/I/D | 自定义主送(分号分隔邮箱,日报邮件使用) |
| customCcs | VARCHAR | C/I/D | 自定义抄送(分号分隔邮箱,日报邮件使用) |
| hasReport | BIT | C/I/D | 是否有巡检报告 |
| quesnaireId | INTEGER | C/I/D | 问卷结果ID(关联问卷结果头) |
| deliverFileIds | VARCHAR | C/I/D | 交付件文件ID列表(逗号分隔,fnd_files id) |
| remark | VARCHAR | C/I/D | 备注 |
| createTime | TIMESTAMP | C/I/D | 创建时间 |
| createBy | VARCHAR | C/I/D | 创建用户(日报按此分组) |
| updateTime | TIMESTAMP | C/I/D | 最新更新时间 |
| updateBy | VARCHAR | C/I/D | 最新更新用户 |
| warrantyStatus | VARCHAR | C/I/D | 维保状态 |
| industryName | VARCHAR | C/I/D | 行业 |
| userOffice | VARCHAR | C/I/D | 用户办事处 |
| year | INTEGER | C/D | 所属年度(processTime 派生) |
| quarter | INTEGER | C/D | 所属季度(processTime 派生,1-4) |
| month | INTEGER | C/D | 所属月份(processTime 派生,1-12) |
| wsCount | INTEGER | C/D | 当前维保服务次数(来自项目维保状态) |
| wafCount | INTEGER | C/D | 当前其他服务次数(来自项目维保状态) |
| wsYearCount | INTEGER | C/D | 维保服务年次数(来自项目维保状态) |
| wafYearCount | INTEGER | C/D | 其他服务年次数(来自项目维保状态) |
| warrantyInfo | VARCHAR | C/D | 维保信息(维保级别描述) |
| serviceInfo | VARCHAR | C/D | 其他服务信息(增值服务描述) |
| customInfo | VARCHAR | C/D | 自定义信息(继承自 CustomInfoEntity) |

### 3.2 表 `pm_project_supervision`(项目督查记录)

> 证据:`sql-map-project-config2.xml:4778-4796`(resultMap)、`4797-4811`(insert)、`4812-4853`(insertSelective)、`4854-4910`(list)、`4962-5023`(insertOrUpdate)、`5025-5060`(mapList)。

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER | C/I/D | 主键,自增 |
| projectId | INTEGER | C/I/D | 项目头信息主键(仅售后项目) |
| projectCode | VARCHAR | C/I/D | 项目编号 |
| projectName | VARCHAR | C/I/D | 项目名称 |
| officeCode | VARCHAR | C/I/D | 办事处编码(取项目 column001) |
| type | VARCHAR | C/I/D | 任务性质(基础数据 supervisionType) |
| channel | VARCHAR | C/I/D | 代理商/服务商 |
| processTime | TIMESTAMP | C/I/D | 处理时间 |
| state | BIT | C/I/D | 是否完成(问卷提交时置 true) |
| isDelete | BIT | C/I/D | 是否删除(软删除标记,查询默认 false) |
| quesnaireId | INTEGER | C/I/D | 问卷结果ID(关联问卷结果头) |
| deliverFileIds | VARCHAR | C/I/D | 交付件文件ID列表(逗号分隔) |
| remark | VARCHAR | C/I/D | 备注 |
| createTime | TIMESTAMP | C/I/D | 创建时间 |
| createBy | VARCHAR | C/I/D | 创建用户 |
| updateTime | TIMESTAMP | C/I/D | 最新更新时间 |
| updateBy | VARCHAR | C/D | 最新更新用户(insertOrUpdate 不回写 updateBy) |

### 3.3 视图 `pm_project_maintenance_view`(维保记录视图,用于日报查询)

> 证据:`sql-map-maintenance-config.xml:1034-1035`(`select * from pm_project_maintenance_view m`)。
> 字段与 `pm_project_maintenance` 基本一致,用于日报列表查询与导出。`[待澄清]` 视图定义(DDL)未在配置中找到,可能由存储过程 `queryProjectMaintenanceInfo` 维护。

### 3.4 临时表 `temp_project_warranty_state`(项目维保状态临时表)

> 证据:`MaintenanceServiceQuarterMailer.execute`(`MaintenanceServiceQuarterMailer.java:76` `createTempProjectWarrantyStateTable`);`selectProjectMaintenanceServiceDeliveryMapList`(sql-map-maintenance-config.xml:1962 `from temp_project_warranty_state wcs`)。
> 由定时调度创建,承载项目维保状态(保内/部分保外/保外、维保级别、增值服务起止时间、年服务次数等),用于服务交付查询与季度提醒。`[待澄清]` 字段明细需查看 `createTempProjectWarrantyStateTable` 语句。

### 3.5 临时表 `temp_maintenance_contract_no`(维保合同号临时表)

> 证据:`MaintenanceDailyReportMailer.execute`(`MaintenanceDailyReportMailer.java:81` `createTempMaintenanceContractNoTable`、`103` `deleteTempMaintenanceContractNoTable`)。
> 日报调度过程中临时创建,辅助日报数据查询,调度结束删除。

### 3.6 表/视图 `pm_project_soleagent_lend_from_sms`(代理商借用,从 SMS 同步)

> 证据:`sql-map-refresh-data-sms-config.xml:6-32`。
> 该表为代理商借用数据从 SMS 同步的落地表,**与"借用维保额度"功能是否相关待澄清**。字段包括 soleAgentLendId、orderExecNumber、orderExecNumberShort、orderCodes 等。

### 3.7 借用维保额度相关表 `[待澄清]`

> 证据:struts-sys.xml:928-962 存在 `LendMaintenanceAction` 路由(addQuota/isQuotaRepeat/seeQuota/updateQuota/deleteQuota),但:
> - 未找到 `LendMaintenanceAction` 类源文件;
> - 未找到 `lend_maintenance`/`lend_quota` 表的 SQL 映射;
> - domain-map.md 提及的 `lend_maintenance`、`lend_quota`、`maintenance_daily_report` 表在 SQL-map 中均未命中。
> 推测:该功能可能已废弃、未实现,或由别处 Bean 别名实现。表结构无法反推,需人工澄清。

---

## 第4章 非功能需求

### 4.1 定时调度

#### NFR-4.1.1 维保日报调度
- 调度名称:项目维护日报邮件发送。
- 触发:每日定时(具体 cron 配置 `[待澄清]`,代码中按"前一天"汇总)。
- 上线约束:2019-07-04 起开始发送(reportTime 早于该日期不发送)。
- 执行步骤:
  1. 先同步办事处秘书(调用 NFR-4.1.2);
  2. 查询前一天有维保记录的创建人列表(`selectDailyMaintenanceUsers`);
  3. 创建维保合同号临时表、维保状态临时表;
  4. 按创建人循环查询其前一天维保记录(`selectDailyMaintenanceMapList`),渲染邮件并发送;
  5. 删除临时表;
  6. 调用存储过程 `queryProjectMaintenanceInfo(1)` 固化每日数据。
- 容错:单个创建人邮件发送失败不影响其他创建人(try-catch 包裹)。
- 证据:`MaintenanceDailyReportMailer.execute`(`MaintenanceDailyReportMailer.java:50-134`)。

#### NFR-4.1.2 办事处秘书同步调度
- 调度名称:SSE 部门秘书同步。
- 触发:由 NFR-4.1.1 日报调度前同步调用,也可独立调度(`main` 入口)。
- 执行步骤:从 SSE 数据源查询秘书清单 → 清空本地秘书表 → 批量插入。
- 证据:`MaintenanceDepartmentSectaryJob.execute`(`MaintenanceDepartmentSectaryJob.java:23-42`)。

#### NFR-4.1.3 维保服务季度交付提醒调度
- 调度名称:项目维护服务交付邮件提醒(策略调优服务、高级维保服务)。
- 触发:定时调度(具体 cron `[待澄清]`)。
- 执行步骤:
  1. 读取系统参数 `pm.project.maintenance.serviceDelivery.deliverFile` 获取服务类型配置(交付件名、服务名、年次数、服务编码、交付ID);
  2. 设置 group_contract 最大长度(1024000)、创建项目维保状态临时表;
  3. 按服务类型循环查询项目服务交付列表(`selectProjectMaintenanceServiceDeliveryMapList`);
  4. 对每个项目判断服务期限(enable)、统计近一年已交付次数与季度次数(`queryProjectMaintenanceDeliverCount`);
  5. 渲染邮件 `maintenanceServiceReportInfo` 并发送。
- 证据:`MaintenanceServiceQuarterMailer.execute`(`MaintenanceServiceQuarterMailer.java:46-120`)。

### 4.2 状态变更联动

#### NFR-4.2.1 项目实施状态联动(维保→项目头)
- 触发点:维保记录新增或最新记录更新时(MaintenanceAction.createProjectMaintenance)。
- 条件:售后项目(projectType=10)且当前维保记录 id 等于该项目最新维保 maxId(`selectSingleProjectMaintenanceMaxId`)或 maxId=0。
- 动作:调用项目服务 `updateProjectExecutionState`,将维保记录中的 projectExecutionState 同步到项目头。
- 证据:`MaintenanceAction.createProjectMaintenance`(`MaintenanceAction.java:391-398`);`selectSingleProjectMaintenanceMaxId`(sql-map-maintenance-config.xml:558-562)。

#### NFR-4.2.2 项目状态更新规则引擎(状态变更联动)
- 联动点:项目服务 `insertOrUpdateProjectState` 与 `updateProjectExecutionState` 方法。
- 规则来源:系统上下文缓存配置 `pm.project.state.update.rules.config`(JSON)。
- 规则结构:分 before/after 两阶段,每阶段含 enable 开关、condition(条件表达式)、scripts(脚本列表)。
- 执行:基于表达式引擎执行 condition 判断是否启用,再执行 script 脚本;脚本执行出错抛出业务异常。
- 配置驱动:规则、脚本、调试开关均由缓存配置控制,无需改代码即可调整联动逻辑。
- 证据:`ProjectStateUpdateAspect.updateProjectStateAdvice`(`ProjectStateUpdateAspect.java:103-130`);规则执行 `updateProjectStateByRule`(`ProjectStateUpdateAspect.java:181-208`);脚本执行 `execScripts`(`ProjectStateUpdateAspect.java:311-370`)。

### 4.3 权限与安全

#### NFR-4.3.1 角色矩阵
- 维保/督查访问角色:ROLE_ADMIN、ROLE_ENGINEEMANAGER、ROLE_ENGINEEMANAGER_LEADER、ROLE_CALLBACKPER、ROLE_PROJECT_ADMIN、ROLE_PROJECT_VIEWER、ROLE_SERVICEMANAGER、ROLE_PROGRAMMANAGER、ROLE_AREA_LEADER;售前维保额外允许 ROLE_PRESALES_STAFF。
- 数据权限:非管理员按区域权限(areapower,办事处集合)与人员权限(userPower,项目成员)双重过滤;服务经理需校验其作为服务经理的人员权限(checkServicePower)。
- 维保编辑:仅创建人本人可编辑(创建人一致校验)。
- 督查删除:仅创建人或工程经理/工程经理负责人可软删除,且仅未完成(state=false)记录可删。
- 证据:`MaintenanceAction.java:144-151、231-262、343-346`;`SupervisionAction.java:110-147、164-178、241-243`。

### 4.4 数据派生与一致性

#### NFR-4.4.1 维保时间维度派生
- 维保记录的 year/quarter/month 由 processTime 自动派生(写入时由 VO 的 setProcessTime 计算)。
- 证据:`ProjectMaintenanceVO.setProcessTime`(`PMS-struts/src/com/dp/plat/maintenance/vo/ProjectMaintenanceVO.java:359-372`)。

#### NFR-4.4.2 维保次数同步
- 维保记录的 wsCount/wafCount/wsYearCount/wafYearCount/warrantyInfo/serviceInfo 由项目维保状态(queryProjectWarrantyState)初始化,根据维保级别启用(warrantyGradeEnable)与增值服务启用(wafServiceEnable)条件赋值。
- 证据:`ProjectMaintenanceVO.initWarrantyExtParams`(`ProjectMaintenanceVO.java:374-391`);`MaintenanceAction.createProjectMaintenance`(`MaintenanceAction.java:336-339、382-386`)。

### 4.5 通知模板

#### NFR-4.5.1 通知模板清单
- `maintenanceDailyReportTable`:维保日报表格行模板(渲染每条维保记录)。
- `maintenanceDailyReportInfo`:维保日报整体模板(渲染邮件正文)。
- `maintenanceServiceReportInfo`:维保服务交付提醒模板。
- 模板存储:通知模板表(NotificationTemplate),通过 `queryNotificationTemplate` 按 code 查询。
- 证据:`MaintenanceDailyReportMailer.java:44-45、184-188`;`MaintenanceServiceQuarterMailer.java:41`。

---

## 附录:关键歧义点 `[待澄清]`

1. **LendMaintenanceAction 实现缺失**:路由配置(struts-sys.xml:928-962)引用 `LendMaintenanceAction` 类(addQuota/isQuotaRepeat/seeQuota/updateQuota/deleteQuota 方法),但全代码库未找到该类源文件,也未找到对应组件定义,功能是否在运行时可用存疑。
2. **借用维保额度表结构缺失**:domain-map.md 列出的 `lend_maintenance`、`lend_quota`、`maintenance_daily_report` 三张表在 SQL-map 配置中均未命中,无法反推字段契约。`pm_project_soleagent_lend_from_sms` 为代理商借用数据落地表,与"借用维保额度"是否同一概念需澄清。
3. **supervision SQL-map 双份并存**:`pm_project_supervision` 的 resultMap 与 CRUD 同时存在于 `sql-map-project-config.xml`(5555+)与 `sql-map-project-config2.xml`(4778+),两者是否完全一致、运行时加载哪份需澄清。
4. **存储过程 `queryProjectMaintenanceInfo(1)` 定义缺失**:日报调度调用该存储过程做"每日数据固化",但存储过程 DDL 未在配置中找到,固化逻辑与目标表不明。
5. **定时调度 cron 配置缺失**:三个定时任务(MaintenanceDailyReportMailer、MaintenanceServiceQuarterMailer、MaintenanceDepartmentSectaryJob)的 cron 表达式配置未在代码中找到,实际调度周期需查调度配置文件 `[待澄清]`。
