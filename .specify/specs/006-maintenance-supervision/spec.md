# Feature Specification: 006-maintenance-supervision(维保与监管)

**Feature Branch**: `006-maintenance-supervision`

**Created**: 2026-07-09

**Status**: Draft

**Source**: 逆向反推自 PMS-struts maintenance/supervision 包

> 本文档为应用 clarify.md 决策后的更新版本,所有待澄清标记已清除。

## User Scenarios & Testing *(mandatory)*

<!--
  用户故事按价值优先级排序。每个故事可独立测试、独立交付。
  P1 = MVP 核心;P2 = 重要增强;P3 = 辅助/待澄清。
-->

### User Story 1 - 维保记录录入(Priority: P1)

作为服务经理或项目经理,我希望能够为售后/售前项目录入维保服务记录(含事项描述、处理进展、遗留问题、耗时、产品型号、在网版本等),以便沉淀项目维保执行过程并支撑日报与季报。

**Why this priority**: 维保记录是本域最核心的数据资产,日报、季报、状态联动均依赖其存在,无录入则其余功能无意义。

**Independent Test**: 以服务经理身份登录,为一个售后项目录入并提交一条维保记录(含问卷),提交后该记录在列表中可见、问卷分数已计算、项目实施状态已联动更新,即视为通过。

**Acceptance Scenarios**:

1. **Given** 服务经理已登录且对目标售后项目具备人员权限,**When** 其为该项目提交一条带问卷的维保记录(任务性质、任务分类、处理时间、事项描述均已填),**Then** 记录创建成功,问卷状态置为已完成并计算分数,项目实施状态按最新维保记录联动更新。
2. **Given** 一条已存在的维保记录由用户 A 创建,**When** 用户 B(非创建人)尝试编辑该记录,**Then** 系统拒绝编辑并重定向,仅创建人本人可编辑。
3. **Given** 用户在录入页选择"复制新增",**When** 其基于既有记录复制新建,**Then** 主键、交付件、自定义信息、问卷ID 被清空,其余字段带入新记录。
4. **Given** 录入的项目为非业务类(projectType=30),**When** 其提交维保记录,**Then** 该记录无需关联问卷即可创建。

---

### User Story 2 - 维保记录查询与导出(Priority: P1)

作为项目干系人(服务经理、项目经理、工程经理、区域负责人、回调专员、项目管理员/查看者),我希望按项目、办事处、任务分类、维保状态、时间区间等条件分页查询维保记录并导出,以便掌握维保执行情况。

**Why this priority**: 查询与导出是干系人消费维保数据的基本入口,与录入共同构成 MVP。

**Independent Test**: 以项目管理员身份按"办事处+处理时间区间"查询维保记录,列表返回正确分页结果并以无分页模式导出全量,即视为通过。

**Acceptance Scenarios**:

1. **Given** 当前用户为非管理员且仅对办事处 X 具备区域权限,**When** 其查询维保记录且不指定办事处,**Then** 返回结果仅含办事处 X 内且其具备人员权限的项目维保记录。
2. **Given** 用户指定处理时间区间为 2026-Q2,**When** 其查询维保记录,**Then** 返回结果的 year/quarter/month 维度与处理时间一致,过滤正确。
3. **Given** 用户在查询页选择导出且 pagesize=-1,**When** 其触发导出,**Then** 系统返回无分页全量结果,字段与列表一致。

---

### User Story 3 - 项目督查记录录入与删除(Priority: P1)

作为服务经理或项目经理,我希望为售后项目录入督查记录(含代理商/服务商、任务性质、问卷),并对未完成的督查记录进行软删除,以便记录督查过程并清理无效记录。

**Why this priority**: 督查是本域与维保并列的核心子域,录入与软删除构成督查记录生命周期的基础闭环。

**Independent Test**: 服务经理为售后项目录入一条督查记录(问卷为草稿),随后创建人或工程经理将其软删除,列表默认不再展示该记录,即视为通过。

**Acceptance Scenarios**:

1. **Given** 服务经理已登录且对目标售后项目具备权限,**When** 其录入督查记录并保存为草稿(未提交问卷),**Then** state=false,记录创建成功,项目编号/名称/办事处自动填充。
2. **Given** 一条督查草稿记录(state=false)由用户 A 创建,**When** 用户 A 或工程经理/工程经理负责人触发软删除,**Then** isDelete 置为 true,列表默认查询不再返回该记录。
3. **Given** 一条督查记录已完成(state=true),**When** 创建人尝试软删除,**Then** 系统拒绝删除(仅未完成记录可删)。
4. **Given** 非创建人且非工程经理角色的用户,**When** 其尝试软删除他人督查记录,**Then** 系统拒绝(无删除权限)。

---

### User Story 4 - 维保日报邮件(Priority: P2)

作为维保记录创建人,我希望系统每日自动汇总我前一天创建的维保记录,并向项目服务经理、项目经理、销售、区域主任、办事处秘书及自定义主送/抄送人发送日报邮件,以便各方及时了解维保进展。

**Why this priority**: 日报是维保数据的高价值消费形式,但依赖录入先存在,且单次失败不应阻断核心录入,故列为 P2。

**Independent Test**: 触发日报调度后,前一日有维保记录的每个创建人均收到一封渲染正确的日报邮件,主送/抄送列表符合规则,邮箱格式不合规者被过滤,即视为通过。

**Acceptance Scenarios**:

1. **Given** 前一日用户 A 与用户 B 各创建若干维保记录,**When** 日报调度执行,**Then** 系统按创建人分组生成两封日报邮件,各自包含本人前一日记录。
2. **Given** 项目有效成员含角色 10/20/30(销售/服务经理/项目经理),**When** 日报邮件组装主送,**Then** 主送列表含这些有效成员邮箱 + 维保记录自定义主送人邮箱。
3. **Given** 某抄送邮箱不匹配 `^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@(dptech.com|dp.com)?$`,**When** 日报邮件组装抄送,**Then** 该邮箱被过滤,不发送到该地址。
4. **Given** 用户 A 的日报邮件发送失败,**When** 调度继续,**Then** 用户 B 的日报邮件仍正常发送(单个创建人失败不影响其他)。
5. **Given** 日报调度完成,**When** 数据固化步骤执行,**Then** 系统调用存储过程 `queryProjectMaintenanceInfo(1)` 进行每日数据固化(调用失败仅记录异常日志不阻断调度)。

---

### User Story 5 - 服务交付查询(Priority: P2)

作为服务经理或管理员,我希望按服务季度、服务类型查询项目服务交付情况(含已交付次数、季度是否完成),以便跟踪服务交付进度。

**Why this priority**: 服务交付查询支撑季度交付追踪,是维保状态数据的查询入口,价值较高但依赖维保状态临时表就绪。

**Independent Test**: 服务经理按当前季度与"策略调优服务"类型查询,返回各项目的已交付次数与季度完成情况,非管理员仅见其权限内项目,即视为通过。

**Acceptance Scenarios**:

1. **Given** 当前为 2026-Q3,**When** 服务经理不指定服务季度查询,**Then** 默认返回 2026-Q3 的服务交付结果。
2. **Given** 非管理员用户仅对办事处 X 具备区域权限,**When** 其查询服务交付,**Then** 结果仅含办事处 X 内项目。
3. **Given** 用户选择导出,**When** 其触发导出,**Then** 系统返回全量结果,字段与列表一致。

---

### User Story 6 - 项目实施状态联动(Priority: P2)

作为系统,我希望在新增或更新最新一条维保记录时自动更新项目实施状态,以便项目状态与维保进展保持一致。

**Why this priority**: 状态联动保证数据一致性,是维保录入的关键副作用,但属系统行为而非用户直接功能,列为 P2。

**Independent Test**: 为某售后项目新增一条维保记录(其 id 等于该项目最新维保 maxId 或 maxId=0),提交后项目头实施状态被更新为维保记录中的 projectExecutionState,即视为通过。

**Acceptance Scenarios**:

1. **Given** 售后项目 P 当前最新维保 maxId=0(无维保记录),**When** 用户新增一条维保记录(projectExecutionState=S2),**Then** 项目头实施状态被更新为 S2。
2. **Given** 售前项目(projectType=20),**When** 用户新增维保记录,**Then** 不触发项目实施状态联动(仅售后项目联动)。
3. **Given** 项目实施状态变更,**When** 联动规则引擎执行,**Then** 系统按缓存配置的 before/after 阶段规则与脚本执行,脚本出错抛出业务异常。

---

### User Story 7 - 维保服务季度交付提醒(Priority: P3)

作为服务经理,我希望系统按季度检查策略调优服务、高级维保服务等服务类型的交付完成情况,并在未完成或临近时发送提醒邮件,以便保障服务交付次数达标。

**Why this priority**: 季度提醒依赖服务交付数据与系统参数配置,属辅助提醒功能,列为 P3。

**Independent Test**: 触发季度交付提醒调度后,对每个配置的服务类型,服务次数未达标的项目收到提醒邮件,即视为通过。

**Acceptance Scenarios**:

1. **Given** 系统参数配置了"策略调优服务"(yearCount=4),**When** 季度提醒调度执行,**Then** 对在服务期限内(enable)的项目,统计近一年已交付次数(count)与季度次数(quarterCount),与 yearCount 比对后发送提醒。
2. **Given** 某项目不在该服务类型的服务期限内,**When** 调度执行,**Then** 该项目对该服务类型不发提醒。

---

### User Story 8 - 督查权限用户查询(Priority: P3)

作为管理员,我希望查询具有服务经理和项目经理角色的用户列表,以便分配督查相关权限。

**Why this priority**: 该查询为督查权限分配的辅助能力,使用频率低,列为 P3。

**Independent Test**: 管理员触发权限用户查询,返回去重后的服务经理+项目经理用户列表(JSON),即视为通过。

**Acceptance Scenarios**:

1. **Given** 系统中存在同时具备服务经理与项目经理角色的用户,**When** 管理员查询权限用户,**Then** 返回去重后的用户列表(JSON 格式)。

---

### User Story 9 - 借用维保额度管理(Priority: P3)

作为管理员,我希望维护借用维保额度(新增、查看、更新、删除),并在新增/更新时校验额度是否重复,以便管理跨项目的维保额度借用。

**Why this priority**: 该子域实现类与数据表均未见,功能可用性存疑,列为 P3。

**Independent Test**: [暂定决策:实现类与数据表缺失,本子域不计入 MVP 验收,独立测试待源文件补全后补充]。

**Acceptance Scenarios**:

1. **Given** 管理员进入借用维保额度列表页,**When** 其查看列表,**Then** 系统展示已有借用维保额度记录。
2. **Given** 管理员新增一条借用维保额度,**When** 其提交且额度已存在,**Then** 系统异步校验返回"重复"提示(message)。
3. **Given** 管理员选择某额度更新,**When** 其提交更新,**Then** 更新成功后重定向回列表页。
4. **Given** 管理员选择某额度删除,**When** 其触发删除,**Then** 系统返回 JSON(message) 表示删除结果。

> 备注(AMB-006-01/02):借用维保额度子域 Action 实现类源文件与数据表(lend_maintenance/lend_quota/maintenance_daily_report)均缺失,运行时可用性存疑,MVP 范围内不计入验收。如必须支持,需向项目维护方索取 Action 源文件或确认是否已废弃;若确认废弃,应将 FR-2.4.x 全部标记为 Deprecated。

---

### User Story 10 - 办事处秘书同步(Priority: P3)

作为系统,我希望定时从 SSE 数据源同步办事处秘书清单,以便日报邮件抄送秘书。

**Why this priority**: 秘书同步为日报邮件的辅助数据源,可被日报调度前同步调用,独立价值有限,列为 P3。

**Independent Test**: 触发秘书同步后,本地秘书表被清空并重新写入 SSE 查询结果,即视为通过。

**Acceptance Scenarios**:

1. **Given** SSE 数据源含最新秘书清单,**When** 秘书同步调度执行,**Then** 本地秘书表先清空再批量插入,内容与 SSE 一致。

---

### Edge Cases

- 当维保记录的 processTime 为空或格式异常时,year/quarter/month 如何派生?(由 processTime 自动派生,异常时应拦截写入)
- 当日报邮件的所有主送邮箱均不匹配正则时,邮件如何处理?(应跳过发送并记录)
- 当项目维保状态查询失败时,维保记录的 wsCount/wafCount 直接取查询结果(不受启用条件控制,反映已发生事实);wsYearCount/wafYearCount 按 warrantyGradeEnable/wafServiceEnable 启用条件归零(未启用时为 0,反映配额)。
- 当状态联动规则脚本执行出错时,维保记录是否仍写入?(脚本出错抛业务异常,应回滚或标记)
- 当督查问卷未提交(state=false)即软删除后,问卷数据如何处置?(记录软删,问卷保留)
- 当服务交付查询的维保状态临时表未就绪时,查询如何表现?(应报错或返回空)
- 当借用维保额度子域实现缺失时,路由访问如何表现?(可能 404 或异常;该子域不计入 MVP 验收)

## Requirements *(mandatory)*

### Functional Requirements

#### 维保计划/执行

- **FR-2.1.1**: 系统 MUST 支持按项目类型(售后10/售前20/非业务30/自定义40)、项目编号、项目名称、办事处、任务性质、任务分类、任务小类、维保状态、维保级别、增值服务、处理时间区间、创建时间区间等条件分页查询维保记录;非管理员角色 MUST 叠加区域权限与人员权限过滤,服务经理需校验其作为服务经理的人员权限。
  - 来源:`MaintenanceAction.projectMaintenance`(`MaintenanceAction.java:142-216`);查询入口 `selectProjectMaintenanceMapList`(数据映射配置 `sql-map-maintenance-config.xml:783`)。

- **FR-2.1.2**: 系统 MUST 支持为售后/售前/非业务/自定义项目录入维保记录;除非业务类外,录入时 MUST 关联维护问卷(projectMaintenance 类型),问卷支持草稿与提交两种状态,提交时计算分数;录入时 MUST 携带项目维保状态、维保级别、增值服务信息(查询自项目维保状态);编辑时 MUST 校验是否本人操作(创建人一致),非本人跳转重定向;系统 MUST 支持复制新增(清空主键、交付件、自定义信息、问卷ID)。
  - 来源:`MaintenanceAction.createProjectMaintenance`(`MaintenanceAction.java:218-430`)。

- **FR-2.1.3**: 系统 MUST 支持为维保记录上传交付件,交付件类型按项目类型(projectType+1)归类;MUST 支持按事件节点(eventKey,格式 dataTypeCode-basicDataId)查询可上传交付件清单;MUST 支持查询交付件清单(commonUpload)与回单表单(returnForm)两种模式。
  - 来源:`MaintenanceAction.createProjectMaintenance` 交付件段(`MaintenanceAction.java:400-426`);`MaintenanceAction.toUploadFile/uploadFileList`(`MaintenanceAction.java:479-563`)。

- **FR-2.1.4**: 维保记录可访问角色 MUST 包含:项目管理员、项目查看者、服务经理、项目经理、工程经理、工程经理负责人、区域负责人、回调专员、系统管理员;售前额外允许售前人员。非管理员数据权限 MUST 按区域权限与项目成员(服务经理/项目经理/项目经理B/团队成员)过滤。
  - 来源:`MaintenanceAction.projectMaintenance`(`MaintenanceAction.java:144-196`);`createProjectMaintenance`(`MaintenanceAction.java:231-262`)。

#### 维保报表

- **FR-2.2.1**: 系统 MUST 每日定时调度(release profile cron = `00 00 05 * * ?`,每日 05:00:00,来源 `PMS-struts/config/beans-quartz.xml:303-312`),汇总前一天各创建人的维保记录,按创建人分组生成日报邮件;邮件主送 MUST 含项目有效成员(角色10/20/30,即销售/服务经理/项目经理)+ 维保记录自定义主送人;邮件抄送 MUST 含区域主任(按办事处匹配)、办事处秘书(从 SSE 同步)、维保记录自定义抄送人、系统参数 `maintenance.mail` 配置的群组邮箱;邮件正文 MUST 使用通知模板 `maintenanceDailyReportTable`(表格行模板)与 `maintenanceDailyReportInfo`(整体模板)渲染;邮箱 MUST 匹配正则 `^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@(dptech.com|dp.com)?$`,不匹配的过滤;调度完成后 MUST 调用存储过程 `queryProjectMaintenanceInfo(1)` 进行每日数据固化(存储过程 DDL 未纳入代码库,需向 DBA 索取)。
  - 来源:`MaintenanceDailyReportMailer.execute/infoMaintenanceDailyReport`(`MaintenanceDailyReportMailer.java:50-331`);数据固化调用(`MaintenanceDailyReportMailer.java:108-133`)。

- **FR-2.2.2**: 系统 MUST 定时从 SSE 数据源同步办事处秘书清单(先清空再插入,无独立 cron,由日报调度 `MaintenanceDailyReportMailer.execute()` 内部调用,可由 `MaintenanceDepartmentSectaryJob.main` 手动触发),以便日报邮件抄送秘书。
  - 来源:`MaintenanceDepartmentSectaryJob.execute`(`MaintenanceDepartmentSectaryJob.java:23-42`)。

- **FR-2.2.3**: 系统 MUST 定时调度(release profile cron = `00 00 06 1 2/3 ?`,每季度中间月初 06:00,即 2/5/8/11 月 1 号,来源 `PMS-struts/config/beans-quartz.xml:325-334`),按系统参数 `pm.project.maintenance.serviceDelivery.deliverFile` 配置的服务类型(策略调优服务、高级维保服务等)检查交付完成情况;检查逻辑 MUST 查询项目维保状态临时表,对每个服务类型判断是否在服务期限内(enable),统计近一年已交付次数(count)与季度次数(quarterCount),与年服务次数(yearCount)比对后发送提醒邮件;MUST 使用通知模板 `maintenanceServiceReportInfo`。
  - 来源:`MaintenanceServiceQuarterMailer.execute`(`MaintenanceServiceQuarterMailer.java:46-120`)。

- **FR-2.2.4**: 系统 MUST 支持按服务季度(默认当前季度)、服务日期、服务类型查询项目服务交付情况,并支持导出;非管理员 MUST 叠加区域权限过滤;查询 MUST 使用维保状态临时表(temp_project_warranty_state)。
  - 来源:`MaintenanceAction.serviceDelivery`(`MaintenanceAction.java:432-477`);查询 `selectProjectMaintenanceServiceDeliveryMapList`(数据映射配置 `sql-map-maintenance-config.xml:1950`)。

#### 项目督查

- **FR-2.3.1**: 系统 MUST 支持按项目编号、项目名称、办事处、任务性质、代理商/服务商、处理时间区间、状态、创建人等条件分页查询督查记录(默认过滤未删除 isDelete=false);非管理员 MUST 叠加区域权限与人员权限过滤;MUST 支持无分页导出(pagesize=-1)。
  - 来源:`SupervisionAction.projectSupervision`(`SupervisionAction.java:108-155`);查询 `selectProjectSupervisionMapList`(数据映射配置 `sql-map-project-config2.xml:5025`,权威路径;`sql-map-project-config.xml:5555+` 同名映射为冗余,见 AMB-006-03)。

- **FR-2.3.2**: 系统 MUST 支持为售后项目(projectType=10)录入督查记录,其他项目类型 MUST 拒绝;需关联督查问卷(projectSupervision 类型问卷);问卷提交时 MUST 设置 state=true(已完成),草稿不设置;录入时 MUST 自动填充项目编号、项目名称、办事处(取项目 column001)。
  - 来源:`SupervisionAction.createProjectSupervision`(`SupervisionAction.java:157-234`)。

- **FR-2.3.3**: 系统 MUST 支持对未完成(state=false)的督查记录进行软删除(isDelete=true);删除权限 MUST 限定为创建人或工程经理/工程经理负责人。
  - 来源:`SupervisionAction.deleteProjectSupervision`(`SupervisionAction.java:236-251`)。

- **FR-2.3.4**: 系统 MUST 支持查询具有服务经理或项目经理角色的用户列表(去重),返回 JSON。
  - 来源:`SupervisionAction.queryPowerUser`(`SupervisionAction.java:253-278`)。

- **FR-2.3.5**: 督查记录可访问角色同 FR-2.1.4(售前督查不涉及,仅售后项目 projectType=10);数据权限 MUST 按区域权限与项目成员(服务经理/项目经理/项目经理B)过滤。实现层提示:`SupervisionAction.createProjectSupervision` 入口当前仅校验 `projectId==0`,建议后端补 `projectType` 校验以确保售前项目被拒绝(见 AMB-006-11)。
  - 来源:`SupervisionAction.projectSupervision`(`SupervisionAction.java:110-147`)。

#### 借用维保额度

> 子域状态(AMB-006-01/02):实现类源文件与数据表(lend_maintenance/lend_quota/maintenance_daily_report)均缺失,运行时可用性存疑,MVP 范围内不计入验收。以下 FR 保留以记录路由配置事实,但标注为 [暂定决策:不计入 MVP 验收]。

- **FR-2.4.1**: 系统 MUST 提供借用维保额度列表页面。[暂定决策:实现缺失,运行时可用性存疑,MVP 范围内不计入验收]
  - 来源:路由 `LendMaintenance`→`/sys/lendmaintenance.jsp`(路由配置 `struts-sys.xml:928-932`)。

- **FR-2.4.2**: 系统 MUST 支持新增借用维保额度,新增成功后重定向回列表页。[暂定决策:实现缺失,运行时可用性存疑,MVP 范围内不计入验收]
  - 来源:路由 `AddLendMaintenance` method=`addQuota`→`/sys/sub/addlendmaintenance.jsp`(路由配置 `struts-sys.xml:933-940`)。

- **FR-2.4.3**: 系统 MUST 支持异步校验额度是否重复,返回 JSON(message)。[暂定决策:实现缺失,运行时可用性存疑,MVP 范围内不计入验收]
  - 来源:路由 `IsLendQuotaRepeat` method=`isQuotaRepeat`,JSON 输出 `message`(路由配置 `struts-sys.xml:941-945`)。

- **FR-2.4.4**: 系统 MUST 支持查看额度详情(跳转查看页或更新页)与更新,更新成功后重定向回列表页。[暂定决策:实现缺失,运行时可用性存疑,MVP 范围内不计入验收]
  - 来源:路由 `SeeLendMaintenance` method=`seeQuota`、`UpdateLendMaintenance` method=`updateQuota`(路由配置 `struts-sys.xml:946-957`)。

- **FR-2.4.5**: 系统 MUST 支持删除借用维保额度,返回 JSON(message)。[暂定决策:实现缺失,运行时可用性存疑,MVP 范围内不计入验收]
  - 来源:路由 `DeleteLendMaintenance` method=`deleteQuota`,JSON 输出 `message`(路由配置 `struts-sys.xml:958-962`)。

### Key Entities *(include if feature involves data)*

#### 数据契约

> 字段分级说明:
> - **C**(Create/Write):创建或更新时写入的字段
> - **I**(Input/Query):作为查询条件输入的字段
> - **D**(Display):查询结果展示或返回的字段
> - 一个字段可同时具备多种分级(如 C/I/D)

**实体 `pm_project_maintenance`(项目维保记录)**

> 来源:数据映射配置 `sql-map-maintenance-config.xml:5-50`(结果映射)、`51-80`(写入)、`414-555`(写入或更新)、`558-562`(maxId)、`783`(列表查询)、`1034-1106`(视图查询)、`1578-1581`(维保状态)。

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
| wsCount | INTEGER | C/D | 当前维保服务次数(直接取维保状态查询结果,不受启用条件控制,反映已发生事实) |
| wafCount | INTEGER | C/D | 当前其他服务次数(直接取维保状态查询结果,不受启用条件控制,反映已发生事实) |
| wsYearCount | INTEGER | C/D | 维保服务年次数(受 warrantyGradeEnable 控制,未启用时为 0,反映配额) |
| wafYearCount | INTEGER | C/D | 其他服务年次数(受 wafServiceEnable 控制,未启用时为 0,反映配额) |
| warrantyInfo | VARCHAR | C/D | 维保信息(维保级别描述) |
| serviceInfo | VARCHAR | C/D | 其他服务信息(增值服务描述) |
| customInfo | VARCHAR | C/D | 自定义信息(继承自 CustomInfoEntity) |

**实体 `pm_project_supervision`(项目督查记录)**

> 来源:数据映射配置 `sql-map-project-config2.xml:4778-4796`(结果映射)、`4797-4811`(写入)、`4812-4853`(选择性写入)、`4854-4910`(列表)、`4962-5023`(写入或更新)、`5025-5060`(列表查询)。权威路径为 `sql-map-project-config2.xml`;`sql-map-project-config.xml:5555+` 同名映射为冗余(见 AMB-006-03)。

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
| updateBy | VARCHAR | C/D | 最新更新用户(写入或更新时不回写 updateBy) |

**实体 `pm_project_maintenance_view`(维保记录视图,用于日报查询)**

> 来源:数据映射配置 `sql-map-maintenance-config.xml:1034-1035`(`select * from pm_project_maintenance_view m`)。
> 字段与 `pm_project_maintenance` 基本一致,用于日报列表查询与导出。视图 DDL 由 DBA 维护,不在代码库;字段契约以 `select * from pm_project_maintenance_view m` 输出为准,与 `pm_project_maintenance` 基本一致(见 AMB-006-10)。

**实体 `temp_project_warranty_state`(项目维保状态临时表)**

> 来源:`MaintenanceServiceQuarterMailer.execute`(`MaintenanceServiceQuarterMailer.java:76` 创建临时表);`selectProjectMaintenanceServiceDeliveryMapList`(数据映射配置 `sql-map-maintenance-config.xml:1962` 引用)。
> 由定时调度创建,承载项目维保状态(保内/部分保外/保外、维保级别、增值服务起止时间、年服务次数等),用于服务交付查询与季度提醒。该表为动态派生表,无静态字段契约;字段集合以 `selectProjectWarrantyState` 子查询输出列为准,DDL 仅定义 projectId/warrantyStatus/warrantyGrade/wafService 四个索引列(见 AMB-006-06)。

**实体 `temp_maintenance_contractNo`(维保合同号临时表)**

> 来源:`MaintenanceDailyReportMailer.execute`(`MaintenanceDailyReportMailer.java:81` 创建、`103` 删除);DDL 见 `sql-map-maintenance-config.xml:1467-1484`。
> 日报调度过程中临时创建,辅助日报数据查询,调度结束删除。表名采用驼峰命名(`No` 大写无下划线)与代码 DDL 对齐(见 AMB-006-05)。

**实体 `pm_project_soleagent_lend_from_sms`(代理商借用,从 SMS 同步)**

> 来源:数据映射配置 `sql-map-refresh-data-sms-config.xml:6-32`。
> 该表为代理商借用数据从 SMS 同步的落地表,与"借用维保额度"功能不相关(借用维保额度子域因实现与数据层缺失不计入 MVP,见 AMB-006-01/02)。字段包括 soleAgentLendId、orderExecNumber、orderExecNumberShort、orderCodes 等。

**实体 借用维保额度相关表 [暂定决策:数据层缺失,已移除实体定义;借用维保额度子域不计入 MVP 验收]**

> 来源:路由配置 `struts-sys.xml:928-962` 存在借用维保额度相关路由(addQuota/isQuotaRepeat/seeQuota/updateQuota/deleteQuota),但:
> - 未找到对应 Action 实现类源文件;
> - 未找到 `lend_maintenance`/`lend_quota` 表的数据映射;
> - 域映射文档提及的 `lend_maintenance`、`lend_quota`、`maintenance_daily_report` 表在数据映射配置中均未命中。
> 决策(AMB-006-01/02):该功能实现缺失、数据层缺失,运行时可用性存疑,MVP 范围内不计入验收。如必须支持,需向项目维护方索取 Action 源文件或确认是否已废弃;若 DBA 确认表真实存在,需补全 SQL-map 与字段契约并恢复实体定义。

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 维保记录录入后,问卷提交态记录的问卷分数 MUST 100% 计算成功(草稿态不计分)。
- **SC-002**: 非管理员用户的维保/督查列表查询结果 MUST 100% 不含其无权限的项目记录(区域权限+人员权限双重过滤零越权)。
- **SC-003**: 维保日报调度执行后,前一日有维保记录的每个创建人 MUST 各收到且仅收到一封日报邮件(100% 按创建人分组覆盖)。
- **SC-004**: 日报邮件主送/抄送列表中不匹配邮箱正则的地址 MUST 100% 被过滤(零违规邮箱外发)。
- **SC-005**: 单个创建人日报邮件发送失败 MUST 不影响其他创建人(其余创建人邮件发送成功率应与无失败场景一致)。
- **SC-006**: 售后项目新增/更新最新维保记录后,项目头实施状态 MUST 在事务内同步更新(一致率 100%)。
- **SC-007**: 督查软删除 MUST 仅对未完成(state=false)记录生效,已完成记录软删请求 MUST 100% 被拒绝。
- **SC-008**: 维保记录编辑 MUST 仅创建人本人可操作,非创建人编辑请求 MUST 100% 被重定向拒绝。
- **SC-009**: 维保时间维度(year/quarter/month)MUST 100% 由 processTime 自动派生,无需人工填写且与处理时间一致。
- **SC-010**: 季度交付提醒调度执行后,在服务期限内且服务次数未达标的项目 MUST 100% 收到提醒邮件。
- **SC-011**: 日报调度完成后 MUST 触发存储过程 `queryProjectMaintenanceInfo(1)` 调用进行每日数据固化;调用失败 MUST 记录异常日志但不阻断调度(失败重试策略待 DBA/运维澄清;见 AMB-006-08)。
- **SC-012**: 日报调度单次执行总时长 SHOULD 在 30 分钟内完成(单创建人邮件发送失败不阻断)。

## Assumptions

- 用户具备稳定的网络连接,可通过浏览器访问维保/督查相关页面。
- 项目头信息、项目成员、区域权限、人员权限等基础数据已由项目主数据域维护就绪。
- 问卷子系统已提供 projectMaintenance 与 projectSupervision 两种类型问卷的录入与分数计算能力。
- 通知模板表已预置 `maintenanceDailyReportTable`、`maintenanceDailyReportInfo`、`maintenanceServiceReportInfo` 三个模板。
- SSE 数据源可用,可查询办事处秘书清单。
- SMS 数据源可用,可同步代理商借用数据落地表。
- 系统参数 `maintenance.mail`(群组邮箱)与 `pm.project.maintenance.serviceDelivery.deliverFile`(服务类型配置)已由管理员配置。
- 项目实施状态联动规则缓存配置 `pm.project.state.update.rules.config`(JSON,含 before/after 阶段、condition、scripts)已就绪。
- 维保日报调度自 2019-07-04 起开始发送(reportTime 早于该日期不发送)。
- 维保日报调度的具体 cron 配置: release profile cron = `00 00 05 * * ?`(每日 05:00:00),来源 `PMS-struts/config/beans-quartz.xml:303-312`(`MaintenanceDailyReportMailerTrigger`)。
- 维保服务季度交付提醒调度的具体 cron 配置: release profile cron = `00 00 06 1 2/3 ?`(每季度中间月初 06:00,即 2/5/8/11 月 1 号),来源 `beans-quartz.xml:325-334`(`MaintenanceServiceQuarterMailerTrigger`)。
- 办事处秘书同步无独立 cron,由日报调度 `MaintenanceDailyReportMailer.execute()` 内部调用,可由 `MaintenanceDepartmentSectaryJob.main` 手动触发。需核对四份 profile(dev/test/release/yfpms)的 `beans-quartz.xml` cron 是否一致(见 AMB-006-04)。
- 借用维保额度子域因实现类源文件与数据表(lend_maintenance/lend_quota/maintenance_daily_report)均缺失,运行时可用性存疑;在 MVP 范围内不计入验收。如必须支持,需向项目维护方索取 Action 源文件或确认是否已废弃(见 AMB-006-01/02)。

## 附录:关键歧义点(已澄清,详见 clarify.md)

> 所有歧义点已通过 `.specify/specs/006-maintenance-supervision/clarify.md` 正向固化决策。以下为原歧义点的决策结论摘要。

1. **借用维保额度 Action 实现缺失**(AMB-006-01):路由配置(`struts-sys.xml:928-962`)引用借用维保额度相关 Action 类,但全代码库未找到该类源文件与 Spring Bean 定义。决策:在 spec 中标记为"实现缺失,运行时可用性存疑,MVP 范围内不计入验收";FR-2.4.x 保留路由事实但标注 [暂定决策:不计入 MVP 验收]。
2. **借用维保额度表结构缺失**(AMB-006-02):域映射文档列出的 `lend_maintenance`、`lend_quota`、`maintenance_daily_report` 三张表在数据映射配置中均未命中。决策:移除"借用维保额度相关表"实体定义,标注"借用维保额度子域因数据层缺失不计入 MVP 验收";`pm_project_soleagent_lend_from_sms` 不与"借用维保额度"混同。
3. **督查数据映射双份并存**(AMB-006-03):`pm_project_supervision` 的结果映射与增删改查同时存在于 `sql-map-project-config.xml`(5555+)与 `sql-map-project-config2.xml`(4778+)。决策:以 `sql-map-project-config2.xml` 为权威路径,`sql-map-project-config.xml` 同名映射标注为冗余;运行时加载顺序由 `sql-map-config.xml` 决定。
4. **数据固化流程定义缺失**(AMB-006-07):日报调度调用"数据固化流程"做每日数据固化,定义未在配置中找到。决策:明确为调用存储过程 `queryProjectMaintenanceInfo(1)`(来源 `MaintenanceDailyReportMailer.java:113`);存储过程 DDL 未纳入代码库,需向 DBA 索取。
5. **定时调度 cron 配置**(AMB-006-04):三个定时任务的 cron 表达式已在 `PMS-struts/config/beans-quartz.xml` 找到:维保日报 `00 00 05 * * ?`(每日 05:00)、维保服务季度交付提醒 `00 00 06 1 2/3 ?`(每季度中间月初 06:00,即 2/5/8/11 月 1 号)、办事处秘书同步无独立 cron(由日报调度内部调用 + main 手动触发)。以 release profile 为准,需核对四份 profile 是否一致。

---

> 本文档由 clarify.md 决策应用生成;原 spec.md 中的 13 处待澄清标记已全部清除,替换为对应决策结论或"暂定决策"标注。
