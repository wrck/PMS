# Feature Specification: 009-system-base(系统基础与报表)

**Feature Branch**: `009-system-base`

**Created**: 2026-07-09

**Status**: Draft

**Source**: 逆向反推自 PMS-struts + core 代码

## User Scenarios & Testing *(mandatory)*

<!--
  本域覆盖系统基础服务:基础数据/字典维护、操作日志、合格证管理、报表统计与数据分析、
  文件上传下载、邮件通知与通知模板、系统变量、集群管理、数据导出与数据操作。
  优先级原则:P1=业务运行基石(字典/文件/邮件);P2=运营管控(日志/报表/系统变量/数据导出);P3=扩展能力(合格证/集群)。
  端点路径分两套:老架构(.action)与新架构 REST 路径(/system、/data、/export、/file、/cluster)。
-->

### User Story 1 - 基础数据/字典管理 (Priority: P1)

**As a** 管理员,**I want** 按数据类型分组浏览、新增、修改字典项,并在新后台执行统一字典 CRUD,**so that** 业务表单的下拉枚举值可随业务调整并平滑迁移至新架构。

- 证据:`BasicDataManageAction.java:29-90`、`sql-map-admin-config.xml:812-923`、`DictionaryController.java:34-89`、`DictionaryMapper.xml:20-263`
- 覆盖草稿 US-001~006、US-059

**Why this priority**:字典数据是所有业务表单下拉选项的来源,缺失将导致项目创建、报表筛选等核心流程不可用,属于 MVP 必备能力。

**Independent Test**:可独立验证——给定一个数据类型编码,执行列表查询后返回该类型下所有有效字典项;新增一条字典项后,列表中可见且编码唯一性校验通过。

**Acceptance Scenarios**:

1. **Given** 系统中存在数据类型 `REPORT_ASSIGNED_RATE` 且其下有 5 条有效字典项,**When** 管理员访问基础数据管理页并选择该类型,**Then** 返回 5 条按 `sortId` 升序排列的字典项,且仅包含 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())` 的记录。
2. **Given** 管理员在类型 `IMPL_WAY` 下新增编码为 `impl0` 的字典项,**When** 提交新增表单且同类型下无重复编码,**Then** 记录成功插入,基础数据缓存失效,列表页重定向并显示新记录。
3. **Given** 管理员在类型 `IMPL_WAY` 下新增编码为 `impl0` 的字典项,**When** 同类型下已存在编码 `impl0`,**Then** 编码唯一性校验返回匹配数 > 0,拒绝新增。
4. **Given** 管理员在新后台访问统一字典分页查询,**When** 输入 `dic_key` 模糊条件 `impl%` 并指定页码,**Then** 返回匹配的字典列表及总数,按 `id desc` 排序。
5. **Given** 管理员在维护界面提交不含 `where` 关键字的 UPDATE 语句,**When** 执行受限 SQL 功能,**Then** 系统拒绝执行并返回"执行的 SQL 没有 where 条件,不允许执行"。[暂定决策:新系统废弃裸 SQL 执行入口(FR-2.1.5),紧急运维改走 SC-006 三层校验受控通道;老系统保留并标记为弃用,迁移时移除]

---

### User Story 2 - 操作日志与同步日志 (Priority: P2)

**As a** 管理员/运维管理员,**I want** 分页查询系统操作日志与外部数据同步日志,导出日志为 Excel,浏览并下载服务器磁盘日志文件,**so that** 我能审计用户操作行为、监控同步执行情况并远程定位运行时异常。

- 证据:`OperateLogAction.java:48-197`、`sql-map-admin-config.xml:541-591`、`SysLogController.java:47-145`、`SysLogMapper.xml:154-221`、`SyncLogController.java:47-144`、`SyncLogMapper.xml:200-312`
- 覆盖草稿 US-007~014

**Why this priority**:操作审计与同步监控是运营管控的必要能力,但不阻塞核心业务流程,可在 MVP 之后交付。

**Independent Test**:可独立验证——给定系统中有 N 条操作日志,执行分页查询后返回对应页的日志记录;执行导出后生成可下载的 Excel 文件。

**Acceptance Scenarios**:

1. **Given** 系统中有 200 条操作日志,**When** 管理员访问日志管理页并指定 `pagesize=20, offset=0`,**Then** 返回前 20 条日志(含用户真实姓名),按指定字段排序。
2. **Given** 管理员点击导出全部日志,**When** 导出操作执行,**Then** 使用 Excel 模板生成文件,包含 username、realName、ip、time、info 五列,通过流式响应下载。
3. **Given** 外部数据同步任务执行完毕,**When** 管理员查询同步日志并指定 `isSuccess=false`,**Then** 返回所有失败的同步记录,含目标方法、表对象、异常堆栈。
4. **Given** 运维管理员访问磁盘日志浏览页,**When** 系统从 Web 根目录向上回溯查找 `logs` 目录,**Then** 返回日志目录及文件列表(名称、大小、最后修改时间),路径以 Base64 URL-safe 编码传输。
5. **Given** 运维管理员选择多个日志文件,**When** 点击打包下载,**Then** 系统返回包含所选文件的 ZIP 压缩包。

---

### User Story 3 - 合格证管理 (Priority: P3)

**As a** 服务工程师/管理员,**I want** 根据设备序列号查询 OQC 合格证信息,并通过 Excel 批量导入印章领用信息,**so that** 我能向客户出示合格证并确保查询能匹配到正确检验员。

- 证据:`CertificateAction.java:37-109`、`sql-map-certificate-config.xml:11-57`
- 覆盖草稿 US-015~017

**Why this priority**:合格证查询是面向特定业务场景的扩展能力,不影响项目交付主流程,优先级较低。

**Independent Test**:可独立验证——给定 `mes_seal_info` 中有检验员领用印章记录且 `mes_oqc_info` 中有对应检验记录,输入设备序列号后返回合格证号与生产日期。

**Acceptance Scenarios**:

1. **Given** 管理员拥有 `roleId=1`,**When** 访问合格证查询主页,**Then** `canUpload` 标志为 true,显示印章上传入口。
2. **Given** `mes_oqc_info` 中存在 barcode=`ABC123` 的检验记录且 `mes_seal_info` 中检验员在该检验时间窗口内领用了印章(`inspectTime` 在 `[takeTime, backTime]` 区间),**When** 服务工程师输入 barcode=`ABC123` 查询,**Then** 返回从 `info` 中正则提取的合格证号(`QC PASS` 开头),并根据 barcode 解析生成生产日期(解析规则见 FR-2.3.2)。
3. **Given** 输入 barcode 为空,**When** 提交查询,**Then** 返回错误信息"请输入设备序列号!"。
4. **Given** `mes_oqc_info` 中无匹配记录,**When** 查询 barcode=`NOTEXIST`,**Then** 返回错误信息"没有找到[NOTEXIST]对应的OQC检验信息!"。
5. **Given** 管理员上传包含 50 条印章信息的 Excel 文件,**When** 执行批量导入,**Then** 系统先清空 `mes_seal_info` 表,再批量插入 50 条记录。

---

### User Story 4 - 报表统计与数据分析 (Priority: P2)

**As a** 管理员/工程经理/项目管理员,**I want** 查看全国项目统计综述、各办事处指派率/跟踪率/闭环率/质量评分/实施方式占比的柱状图与趋势折线图,以及项目状态汇总报表,**so that** 我能掌握全国项目交付情况、识别异常办事处并精细掌握项目分布。

- 证据:`ReportAction.java:88-700`、`sql-map-report-config.xml:36-663`、`DataAnalysisAction.java:37-56`
- 覆盖草稿 US-018~028

**Why this priority**:报表统计是管理层决策支撑的核心能力,但依赖项目主数据先行就绪,属 P2 优先级。

**Independent Test**:可独立验证——给定项目主数据中存在各状态的项目记录,执行指派率查询后返回各办事处柱状图 JSON 数据;执行趋势查询后返回月度折线图数据。

**Acceptance Scenarios**:

1. **Given** 管理员访问报表首页,**When** 页面加载,**Then** 返回选项卡列表、办事处列表(含"全国")、全国统计综述及指派率/跟踪率/闭环比表格 HTML。
2. **Given** 非管理员/工程经理/工程经理组长/财务/项目管理员/回访员角色用户访问报表首页,**When** 系统判断角色,**Then** 重定向至项目状态汇总页且仅显示该报表 tab。
3. **Given** 各办事处有已指派项目经理的项目(`memberRole='30' AND fromFlag=1`),**When** AJAX 调用指派率查询,**Then** 返回 ECharts 柱状图 JSON,含各办事处分子/分母及聚合的"全国"行。
4. **Given** `pm_report_line_data` 中有 `dataTypeCode=assignedRate` 且 `effectiveTo IS NULL` 的月度数据,**When** 加载趋势折线图,**Then** 返回按 `settingTime` 排序的月度 `specificValue` 折线图 JSON。
5. **Given** 非管理员访问项目状态汇总报表,**When** 查询执行,**Then** 注入 `areaPowers` 区域权限限制,SQL 中通过办事处编码过滤,仅返回该用户有权查看的办事处数据。
6. **Given** 管理员访问回访数据统计分析页,**When** 按公司/办事处/项目阶段/服务类型/项目类型筛选,**Then** 返回各下拉列表及回访数据列表。

---

### User Story 5 - 文件管理 (Priority: P1)

**As a** 任意用户,**I want** 上传、下载、删除附件文件,并在富文本编辑器中上传图片,新架构下按文件类型配置上传策略并区分公开/私有下载,**so that** 业务单据可关联附件且不同场景的文件上传策略可配置。

- 证据:`UploadAction.java:49-159`、`sql-map-admin-config.xml:931-984`、`UploaderController.java:54-288`、`FileInfoMapper.xml:291-375`
- 覆盖草稿 US-029~039

**Why this priority**:文件上传下载是项目交付、合同管理、邮件附件等核心业务流程的依赖项,属于 MVP 必备能力。

**Independent Test**:可独立验证——给定用户上传一个文件,系统返回文件 ID;使用该 ID 查询文件列表可见;使用该 ID 下载可获取原文件;删除后查询不再可见。

**Acceptance Scenarios**:

1. **Given** 用户通过上传表单提交 2 个文件,**When** 上传完成,**Then** 文件存储至 `upload/file/{随机数}/` 目录,数据库插入 2 条文件记录,返回逗号分隔的文件 ID 列表。
2. **Given** 文件 ID 为 100 的文件存在,**When** 用户点击下载链接,**Then** 以流式响应返回原文件,文件名 ISO8859-1 编码,同时记录下载日志。
3. **Given** 用户在新架构下按文件类型编码 `avatar` 上传头像,**When** POST 至上传端点,**Then** 系统按 `t_file_type` 配置(大小限制、允许类型、重命名、压缩、缩略图、保存目录)处理上传,返回文件信息列表。
4. **Given** 用户通过公开下载链接 `/file/down/public/{id}` 访问文件,**When** 请求到达且 `downloadKey` 校验通过,**Then** 返回文件流(公开下载端点须校验 `downloadKey`,见 SC-013)。
5. **Given** 用户通过私有下载链接 `/file/down/private/{id}` 访问文件,**When** 用户未登录,**Then** 拒绝访问。
6. **Given** 用户在富文本编辑器中上传图片,**When** 上传完成,**Then** 文件按 MD5 命名去重存储,返回完整可访问 URL 列表。

---

### User Story 6 - 邮件通知与通知模板 (Priority: P1)

**As a** 管理员/系统,**I want** 创建、修改、作废、手动发送邮件,通过定时任务自动发送待发邮件,并维护可复用的通知模板,**so that** 邮件按计划自动送达且业务通知可模板化复用。

- 证据:`MailInfoController.java:43-125`、`MailerJob.java:22-53`、`MailInfoMapper.xml:222-494`、`NotifyTemplateController.java:40-138`、`NotifyTemplateMapper.xml:20-293`
- 覆盖草稿 US-040~048

**Why this priority**:邮件通知是项目状态变更、验收通知等业务流程的关键触达手段,属于 MVP 必备能力。

**Independent Test**:可独立验证——给定一封待发邮件(`sendFlag=false AND expectSendTime<now()`),定时任务执行后邮件发送状态更新为已发;按模板编码查询返回当前有效模板。

**Acceptance Scenarios**:

1. **Given** 管理员创建一封待发邮件(`expectSendTime` 设为未来时间),**When** 提交新增表单,**Then** 记录插入数据库,`sendFlag=false`,`createTime=now()`。
2. **Given** 邮件 `expectSendTime` 已过且 `sendFlag=false AND failedCount<3`,**When** 定时任务执行,**Then** 调用邮件发送工具发送,成功则 `sendFlag=true, sendTime=now()`,失败则 `failedCount+1` 并追加 `failedMessage`。
3. **Given** 邮件 `failedCount` 已达到系统变量 `sys.mail.sendFailed.maxCount`(默认 3),**When** 定时任务执行,**Then** 跳过该邮件不再重试。
4. **Given** 管理员作废一封待发邮件,**When** 执行作废操作,**Then** 仅清空 `expectSendTime`,不删除记录,邮件不再被定时任务拾取。
5. **Given** 通知模板 `templateCode=WELCOME` 存在且 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())`,**When** 业务模块按编码查询,**Then** 返回当前有效模板内容。
6. **Given** 管理员保存通知模板时 content 含 `<script>` 标签,**When** 提交保存,**Then** content 经 HTML 白名单净化后存储,`<script>` 标签被移除。

---

### User Story 7 - 系统变量与缓存 (Priority: P2)

**As a** 管理员/业务模块,**I want** 查询和维护系统变量(含二次认证保护敏感值),手动刷新系统缓存,**so that** 系统行为可远程配置且配置变更立即生效。

- 证据:`sql-map-admin-config.xml:27-30,862-875`、`SystemVariableController.java:44-133`、`SystemVariableMapper.xml:20-25`
- 覆盖草稿 US-049~052

**Why this priority**:系统变量驱动报表维度配置、SQL 注入过滤规则、邮件重试上限等运行时行为,是运营管控的必要能力但不阻塞核心业务。

**Independent Test**:可独立验证——给定系统变量 `sys.mail.sendFailed.maxCount=3`,查询返回该值;非二次认证状态下返回加密值,二次认证后返回明文。

**Acceptance Scenarios**:

1. **Given** 系统变量 `sys.mail.sendFailed.maxCount` 值为 `3` 且会话非二次认证状态,**When** 管理员查询系统变量列表,**Then** 返回的 `var` 字段以加密形式呈现。
2. **Given** 管理员输入正确验证码,**When** 提交二次认证,**Then** 会话置 `isSC=true`,后续查询返回明文值。
3. **Given** 管理员输入错误验证码,**When** 提交二次认证,**Then** 移除会话 `isSC` 标志,后续查询仍返回加密值。
4. **Given** 管理员手动触发缓存刷新,**When** 更新 `sys.cache.latest.refreshTime` 为当前时间,**Then** 基础数据缓存与用户数据缓存失效,下次查询从数据库重新加载。
5. **Given** 业务模块按 `code` 查询老架构系统变量,**When** 查询执行,**Then** 返回 `effectiveFrom <= now() AND (effectiveTo IS NULL OR effectiveTo > now())` 的变量值。

---

### User Story 8 - 集群管理 (Priority: P3)

**As a** 管理员,**I want** 触发集群范围内刷新当前用户菜单、过滤链定义、系统变量,**so that** 配置变更在所有节点生效。

- 证据:`ClusterController.java:22-32`
- 覆盖草稿 US-053

**Why this priority**:集群刷新是集群部署环境下的运维能力,单节点环境下非必需,优先级较低。

**Independent Test**:可独立验证——给定管理员角色用户调用刷新端点,系统依次刷新菜单、过滤链、系统变量并返回成功。

**Acceptance Scenarios**:

1. **Given** 用户拥有管理员角色,**When** POST `/cluster/refreshCore`,**Then** 依次刷新当前用户菜单、过滤链定义、系统变量,返回"刷新成功!"。
2. **Given** 用户无管理员角色,**When** POST `/cluster/refreshCore`,**Then** 返回"没有权限访问该功能!",不执行任何刷新操作。

---

### User Story 9 - 数据导出与数据操作 (Priority: P2)

**As a** 任意用户/管理员,**I want** 选择导出对象与列后导出 Excel,配置并执行数据导入/导出操作(含 SQL 脚本校验、反射导入),**so that** 业务数据可离线分析且批量数据可配置化导入导出。

- 证据:`DataExportController.java:36-89`、`DataOperationController.java:76-784`、`DataOperationMapper.xml:30-200`
- 覆盖草稿 US-054~058

**Why this priority**:数据导出与导入是业务分析的必要工具,但依赖业务数据先行就绪,属 P2 优先级。

**Independent Test**:可独立验证——给定一个数据操作配置(type=0,SQL 脚本通过校验),执行导出后生成 Excel 文件;给定一个导入配置(type=1),上传 Excel 后反射调用配置方法返回导入结果。

**Acceptance Scenarios**:

1. **Given** 管理员配置一个 type=0(导出)的数据操作,SQL 脚本含非法表名,**When** 保存配置,**Then** SQL 校验失败(表白名单/黑名单不通过),拒绝保存。
2. **Given** 管理员配置一个 type=0 的数据操作且 SQL 通过校验,**When** 业务用户执行导出,**Then** 使用流式 Excel 写出,进度以百分比写入会话属性,前端可轮询查询。
3. **Given** 管理员配置一个 type=1(导入)的数据操作,**When** 业务用户上传 `.xlsx` 文件执行导入,**Then** 系统反射调用配置的 Service 方法,返回错误列表 JSON。
4. **Given** 业务用户上传非 `.xlsx` 文件执行导入,**When** 提交导入,**Then** 返回文件格式错误信息。
5. **Given** 非管理员用户的数据操作配置 `empPower` 不包含该用户 ID,**When** 用户尝试操作,**Then** 拒绝操作。
6. **Given** 业务用户输入 SQL 查询可导出列,**When** 提交查询,**Then** SQL 经校验后返回列名列表,供用户勾选导出字段。

---

### Edge Cases

- **无 WHERE 的 UPDATE/DELETE**:管理员在基础数据维护界面提交不含 `where` 关键字的 UPDATE/DELETE 语句时,系统拒绝执行并返回"执行的 SQL 没有 where 条件,不允许执行"。
- **文件上传异常**:老架构文件上传过程中抛出异常时,异常堆栈被打印但操作仍返回 SUCCESS,需关注文件是否实际存储成功。
- **邮件发送失败达到上限**:邮件 `failedCount` 达到 `sys.mail.sendFailed.maxCount`(默认 3)后,定时任务跳过该邮件不再重试,需人工介入。
- **合格证查询 barcode 为空**:用户未输入设备序列号即提交查询,系统返回"请输入设备序列号!"错误。
- **合格证查询无匹配**:输入的 barcode 在 `mes_oqc_info` 中无匹配记录,系统返回"没有找到[barcode]对应的OQC检验信息!"。
- **报表角色越权访问**:非授权角色访问报表首页,被重定向至项目状态汇总页且仅显示该报表 tab。
- **印章导入覆盖**:批量上传印章信息前先 `truncate` 清空 `mes_seal_info` 表,历史数据被清除。[暂定决策:新系统改用事务内 `DELETE` + INSERT,失败可回滚,避免数据丢失]
- **趋势数据历史版本**:同一 `dataTypeCode + officeCode + settingTime` 的历史数据通过 `effectiveTo` 失效,趋势查询仅返回 `effectiveTo IS NULL` 的当前有效记录。
- **系统变量二次认证超时**:二次认证状态存储在会话中,会话过期后需重新认证才能查看明文。
- **数据导出大数据量**:大数据量导出使用流式写出(窗口大小 100 行)避免内存溢出,异常时记录错误 ID 并抛出含错误 ID 的异常。
- **新老架构表并存**:基础数据、邮件、系统变量、操作日志存在新老两套表结构。[暂定决策:过渡期新老表分别服务不同模块,无双向同步;迁移时按子域分批合并到新表](见 SC-022)。

---

## Requirements *(mandatory)*

### Functional Requirements

> 按子域组织。每条含触发条件/输入/处理规则/输出/异常。证据标注文件:行号。

#### 2.1 基础数据/字典管理

**FR-2.1.1 浏览基础数据列表(老架构)**
- **触发**:用户访问基础数据管理页(BasicdataManage.action)
- **输入**:`basicData.basicDataTypeCode`(可选,数据类型编码)
- **处理规则**:
  1. 先查所有有效基础数据类型(`status=1 AND effectiveFrom<NOW() AND (effectiveTo IS NULL OR effectiveTo>NOW())`),按 `dataTypeCode` 返回(`sql-map-admin-config.xml:825-829`)。
  2. 若指定数据类型编码,联查 `fnd_basic_data` 与 `fnd_basic_data_type`,按 `dataTypeCode,sortId` 排序(`sql-map-admin-config.xml:812-820`)。
- **输出**:基础数据类型列表 + 指定类型下的字典项列表
- **证据**:`BasicDataManageAction.java:29-37`、`sql-map-admin-config.xml:812-829`

**FR-2.1.2 新增基础数据(老架构)**
- **触发**:用户提交新增表单(BasicdataInsert.action)
- **输入**:`basicData`(`dataTypeCode`、`basicDataId`、`basicDataName`、`sortId`、`createTime`、`createBy`、`effectiveFrom`、`effectiveTo`)
- **处理规则**:INSERT 到 `fnd_basic_data`(`sql-map-admin-config.xml:851-856`);触发基础数据缓存失效(`sql-map-admin-config.xml:766`)。
- **输出**:重定向回列表页,带 `basicData.basicDataTypeCode` 参数
- **证据**:`BasicDataManageAction.java:51-59`、`sql-map-admin-config.xml:851-856`

**FR-2.1.3 修改基础数据(老架构)**
- **触发**:用户提交修改表单(BasicdataUpdate.action)
- **输入**:`basicData.id`、`basicData.basicDataName`、`sortId`、`effectiveTo`、`basicDataTypeCode`
- **处理规则**:
  1. 若 `id!=0` 且 `basicDataId==null`,视为打开修改页,回查单条记录(`sql-map-admin-config.xml:830-834`)。
  2. 否则执行 UPDATE,仅更新 `basicDataName`、可选 `sortId`、`effectiveTo`(`sql-map-admin-config.xml:835-850`),并触发缓存刷新(`BasicDataManageAction.java:47`)。
- **输出**:重定向回列表页
- **证据**:`BasicDataManageAction.java:39-49`、`sql-map-admin-config.xml:830-850`

**FR-2.1.4 基础数据编码唯一性校验**
- **触发**:用户录入 `basicDataId` 时 AJAX 校验
- **输入**:`dataTypeCode`、`basicDataId`
- **处理规则**:统计 `fnd_basic_data` 中同 `dataTypeCode` 与 `basicDataId` 的记录数(`sql-map-admin-config.xml:857-861`)
- **输出**:`result`(int,匹配数)
- **证据**:`BasicDataManageAction.java:64-70`、`sql-map-admin-config.xml:857-861`

**FR-2.1.5 执行维护 SQL(受限)**
- **触发**:管理员在维护界面提交 `executeSql`
- **输入**:`executeSql`(SQL 字符串)
- **处理规则**:
  1. 转小写后判断是否含 `where` 或 `insert` 关键字(`BasicDataManageAction.java:75-83`)。
  2. 含 `where` → 执行并返回"执行更新或删除成功";含 `insert` → 执行并返回"执行插入成功";否则拒绝"执行的 SQL 没有 where 条件,不允许执行"。
  3. SQL 通过字符串拼接到 `<![CDATA[ $executeSql$ ]]>` 执行(`sql-map-admin-config.xml:919-923`)。
- **输出**:`msg`(执行结果文本)
- **异常**:异常信息拼接到 `msg`(`BasicDataManageAction.java:86-88`)
- **风险**:SQL 注入风险极高。[暂定决策:新系统废弃该裸 SQL 执行入口,紧急运维改走 SC-006 三层校验受控通道;老系统保留并标记为弃用,迁移时移除]
- **证据**:`BasicDataManageAction.java:72-90`、`sql-map-admin-config.xml:919-923`

**FR-2.1.6 新架构数据字典 CRUD**
- **触发**:用户访问 `/system/dictionary` 系列端点
- **输入**:`id`、`dicTypeId`、`dicTypeName`、`dicKey`、`dicValue`、`custominfo`、`sort`、`status`
- **处理规则**:标准 CRUD,新增/修改/删除按主键 `id` 操作;列表支持模糊查询(`dic_type_name`、`dic_key`、`dic_value`、`customInfo` 模糊匹配)、排序、分页(`DictionaryMapper.xml:146-196,199-245`)
- **输出**:字典列表/单条详情
- **证据**:`DictionaryController.java:34-89`、`DictionaryMapper.xml:20-263`

**FR-2.1.7 查询字典类型最大 ID**
- **触发**:新增字典类型时分配类型 ID
- **处理规则**:`SELECT max(dic_type_id) FROM t_dictionary`(`DictionaryMapper.xml:256-258`)
- **备注**:[暂定决策:新增字典类型与新增字典项为独立操作;`dic_type_id` 应改由数据库自增或唯一约束保证,避免 `max+1` 并发竞态]
- **证据**:`DictionaryMapper.xml:256-258`

#### 2.2 操作日志

**FR-2.2.1 查询操作日志(老架构)**
- **触发**:用户访问日志管理页(LogManage.action)
- **输入**:`displayParam`(`sort`、`order`、`offset`、`pagesize`)
- **处理规则**:联查 `tb_sys_log` 与 `user` 表(用户名关联)获取 `realName`;按 `sort/order` 排序,`offset,pagesize` 分页(`sql-map-admin-config.xml:567-584`)
- **输出**:日志列表
- **证据**:`OperateLogAction.java:48-52`、`sql-map-admin-config.xml:559-591`

**FR-2.2.2 写入操作日志(老架构)**
- **触发**:登录等关键操作
- **输入**:`username`、`ip`、`info`、`time`
- **处理规则**:INSERT 到 `tb_sys_log(USER_NAME, IP, INFO, TIME)`(`sql-map-admin-config.xml:541-546`)
- **证据**:`sql-map-admin-config.xml:541-546`

**FR-2.2.3 导出操作日志为 Excel**
- **触发**:用户点击导出(ExportLogAll.action)
- **输入**:`displayParam`(查询条件)
- **处理规则**:
  1. 读取模板 `template/日志.xlsx`(`OperateLogAction.java:64-69`)。
  2. 查询全部日志(`sql-map-admin-config.xml:559-565`),逐行写入 Excel 第 0-4 列(username、realName、ip、time、info)。
  3. 输出到 `upload/payment/日志.xlsx`(`OperateLogAction.java:101-113`),通过流响应下载。
- **输出**:Excel 文件流
- **异常**:`FileNotFoundException`/`IOException` 返回 ERROR 并写入 `errmsg`(`OperateLogAction.java:117-125`)
- **证据**:`OperateLogAction.java:63-129`

**FR-2.2.4 新架构系统日志查询**
- **触发**:用户访问 `/system/syslog/list`
- **输入**:`description`、`createBy`、`requestIp`、`type`(均模糊或精确)
- **处理规则**:按 `id desc` 默认排序,支持 `description`/`create_by`/`request_ip` 模糊查询、`type` 精确匹配(`SysLogMapper.xml:154-191`)
- **输出**:日志列表
- **证据**:`SysLogController.java:47-78`、`SysLogMapper.xml:154-191`

**FR-2.2.5 新架构同步日志查询**
- **触发**:用户访问 `/system/synclog/list`
- **输入**:`targetMethod`、`tableObject`、`dataFrom`、`dataTo`、`syncParams`、`syncStartTime`、`syncEndTime`、`isSuccess`、`dataCount`、`syncType`、`exception`
- **处理规则**:支持上述字段精确过滤 + 模糊搜索(`targetMethod`/`tableObject`/`dataFrom`/`dataTo` 模糊)(`SyncLogMapper.xml:200-262`)
- **输出**:同步日志列表
- **证据**:`SyncLogController.java:47-76`、`SyncLogMapper.xml:200-312`

#### 2.3 合格证管理

**FR-2.3.1 合格证查询主页**
- **触发**:用户访问合格证模块(certificate.action)
- **处理规则**:根据当前用户是否拥有 `roleId=1` 设置 `canUpload` 标志(`CertificateAction.java:39`)
- **输出**:`canUpload` 标志
- **证据**:`CertificateAction.java:37-41`

**FR-2.3.2 根据条码查询 OQC 合格证**
- **触发**:用户输入 `barcode` 提交查询
- **输入**:`barcode`(设备序列号)
- **处理规则**:
  1. 查询 `mes_oqc_info` 关联 `mes_seal_info`(检验员 = 印章 `user`,且 `inspectTime` 在 `takeTime` 与 `backTime` 之间或 `backTime IS NULL` 且 `inspectTime >= takeTime`),且 `info like 'QC PASS%'`(`sql-map-certificate-config.xml:11-30`)。
  2. 取首条结果的 `info`,正则提取数字作为 `oqcNo`(`CertificateAction.java:56-61`)。
  3. 根据 barcode 解析生成 `productionDate`([暂定决策:待源码 `CertificateAction.java:83-99` 确认位偏移;暂定取第 11-12 位解析为 16 进制月份],`CertificateAction.java:83-99`)。
- **输出**:`oqcNo`、`productionDate`
- **异常**:未找到 → `errmsg="没有找到[barcode]对应的OQC检验信息!"`(`CertificateAction.java:70`);barcode 为空 → `errmsg="请输入设备序列号!"`(`CertificateAction.java:72`)
- **证据**:`CertificateAction.java:48-75`、`sql-map-certificate-config.xml:11-30`

**FR-2.3.3 批量上传印章信息**
- **触发**:管理员上传 Excel 文件(uploadSealInfo.action)
- **输入**:Excel 文件(印章 ID、名称、info、描述、领用时间、归还时间、备注、上传人)
- **处理规则**:
  1. 解析 Excel 文件(`CertificateAction.java:103`)。
  2. 先清空 `mes_seal_info`(`sql-map-certificate-config.xml:55-57`)。
  3. 批量 INSERT(`sql-map-certificate-config.xml:49-54`)。
- **输出**:重定向回主页
- **异常**:解析异常 → `errmsg` + ERROR 页(`CertificateAction.java:104-107`)
- **事务策略**:[暂定决策:新系统改用事务内 `DELETE` + INSERT 替代非事务的 `truncate`,失败可回滚;导入前可选备份,避免印章数据丢失]
- **证据**:`CertificateAction.java:101-109`、`sql-map-certificate-config.xml:39-57`

#### 2.4 报表统计与数据分析

**FR-2.4.1 报表首页**
- **触发**:用户访问报表首页(report_show)
- **处理规则**:
  1. 查询选项卡(基础数据 `28`)、办事处列表(含"全国")(`ReportAction.java:116-123`)。
  2. 查询全国统计综述(`ReportAction.java:125`)。
  3. 查询指派率、跟踪率、闭环比表格数据并组装 HTML(`ReportAction.java:127-137`)。
- **输出**:选项卡、办事处列表、综述、各表格 HTML
- **权限**:非管理员/工程经理/工程经理组长/财务/项目管理员/回访员角色被重定向至项目状态汇总页(`ReportAction.java:98-106`)
- **证据**:`ReportAction.java:88-145`

**FR-2.4.2 项目指派率**
- **触发**:AJAX 调用 assignedRate
- **输入**:`queryParam`(`quarterStartTime` 等)
- **处理规则**:查询各办事处已指派项目经理项目数 / 总项目数,并聚合"全国"行(`ReportAction.java:294-307`);柱状图参数从基础数据类型 `REPORT_ASSIGNED_RATE` 取标题与属性(`ReportAction.java:296`)
- **输出**:柱状图 JSON
- **证据**:`ReportAction.java:291-313`、`sql-map-report-config.xml:36-53`

**FR-2.4.3 项目跟踪率**
- **触发**:AJAX 调用 traceRate
- **处理规则**:排除工程计划状态=40 的项目后计算跟踪率(`sql-map-report-config.xml:81-103`)
- **输出**:柱状图 JSON
- **证据**:`ReportAction.java:322-344`、`sql-map-report-config.xml:81-103`

**FR-2.4.4 季度闭环新增比**
- **触发**:AJAX 调用 closeRate
- **处理规则**:闭环项目(`projectState=100`)/ 新增项目(`createTime>季度开始 AND projectState>20`),按办事处分组(`sql-map-report-config.xml:354-361,381-397`)
- **输出**:柱状图 JSON
- **证据**:`ReportAction.java:349-386`、`sql-map-report-config.xml:354-397`

**FR-2.4.5 企业网项目实施方式占比**
- **触发**:AJAX 调用 implRate
- **处理规则**:
  1. 创建临时表 `implway`,实施方式取 `column012` 或 `columno12_readonly`,限定 `column004 LIKE '企业网%' OR '渠道与商业%'`(`sql-map-report-config.xml:399-407`)。
  2. 按办事处与实施方式分组统计占比(`sql-map-report-config.xml:434-465`)。
- **输出**:多系列柱状图 JSON(原厂直服/原厂督导/代理商自服)
- **证据**:`ReportAction.java:392-457`、`sql-map-report-config.xml:399-465`

**FR-2.4.6 项目质量评分**
- **触发**:AJAX 调用 quality
- **处理规则**:
  1. 创建临时表 `quality`,关联客户评价(`pm_cl_evaluation_header` `evaluationType=3 AND status=1`)、问卷结果、项目评价,限定 `projectState=100`(`sql-map-report-config.xml:105-137`)。
  2. 查询全国与各办事处平均得分、项目数,支持按项目类别(`column011`)与实施方式(`column012`/`columno12_readonly`)筛选(`sql-map-report-config.xml:150-326`)。
  3. 同时输出闭环平均得分柱状图、闭环项目数量柱状图、质量表格 HTML(`ReportAction.java:514-530`)。
- **输出**:`data`(平均分图)、`dataJson`(项目数图)、`qualityTableHtml`(表格)
- **证据**:`ReportAction.java:464-535`、`sql-map-report-config.xml:105-326`

**FR-2.4.7 趋势折线图(各率)**
- **触发**:AJAX 调用 loadLineData
- **输入**:`officeCode`、`dataTypeCode`
- **处理规则**:从 `pm_report_line_data` 查询指定办事处与数据类型的 `specificValue` 与 `settingTime`,按月格式化、按时间排序,组装折线图,动态设置 Y 轴最大值(`ReportAction.java:160-197`)
- **输出**:折线图 JSON
- **证据**:`ReportAction.java:160-197`、`sql-map-report-config.xml:467-489`

**FR-2.4.8 闭环数量趋势折线图**
- **触发**:AJAX 调用 loadLine_qualityData
- **处理规则**:查询 `pm_report_line_data` 的 `totalValue` 按月聚合(`sql-map-report-config.xml:490-512`)
- **输出**:折线图 JSON
- **证据**:`ReportAction.java:202-230`、`sql-map-report-config.xml:490-512`

**FR-2.4.9 实施方式占比趋势折线图**
- **触发**:AJAX 调用 loadLine_implData
- **处理规则**:查询 `impl*` 前缀的 `dataTypeCode` 数据,按月分组,组装 3 条折线(原厂直服/原厂督导/代理商自服)(`ReportAction.java:235-268`、`sql-map-report-config.xml:586-603`)
- **输出**:多系列折线图 JSON(百分比)
- **证据**:`ReportAction.java:235-268`、`sql-map-report-config.xml:586-603`

**FR-2.4.10 项目状态汇总报表**
- **触发**:用户访问 report_projectSummaryStatus(或被重定向)
- **输入**:`dataJson`(查询条件 JSON)、`data`(值为 `info` 时返回明细)
- **处理规则**:
  1. 默认注入 `projectState=30,31,32`、`shipmentState=-1`(`ReportAction.java:549-551`)。
  2. 非管理员注入区域权限 `areaPowers`(`ReportAction.java:559`)。
  3. 调用 `queryProjectSummaryStatus` 联查 `pm_project_header` + `pm_project_state` + `fnd_department` + `pm_project_contract`(`sql-map-report-config.xml:604-663`)。
  4. 按系统变量 `pm.report.project.summary.status` 配置的维度(实施状态、流程状态、可闭环项目数等)汇总(`ReportAction.java:600-622`)。
  5. 服务端渲染带链接的 HTML 表格,`count>0` 的单元格可下钻查询(`ReportAction.java:670-679`)。
- **输出**:汇总表格 HTML 或明细列表
- **权限**:非管理员仅见本报表 tab 且仅见 `projectSummaryStatus` 维度(`ReportAction.java:560-566`)
- **证据**:`ReportAction.java:537-700`、`sql-map-report-config.xml:604-663`

**FR-2.4.11 报表趋势数据持久化**
- **触发**:定时任务批量保存趋势图数据
- **处理规则**:批量 INSERT `pm_report_line_data(dataTypeCode, officeCode, conditionValue, totalValue, specificValue, settingTime, createTime, effectiveFrom)`(`sql-map-report-config.xml:555-580`)
- **备注**:[暂定决策:待定位定时任务类后补充任务类名与调度周期;暂定为月度定时任务预计算所有指标并持久化,历史 `effectiveTo` 失效在持久化前执行]
- **证据**:`sql-map-report-config.xml:555-580`

**FR-2.4.12 回访数据统计分析**
- **触发**:用户访问 DataAnalysis.action
- **输入**:`dataQueryParam`(查询条件)
- **处理规则**:加载公司、办事处、项目阶段、服务类型、项目类型、选项卡基础数据;查询回访数据列表(`DataAnalysisAction.java:37-56`)
- **输出**:各下拉列表 + 回访数据列表
- **异常**:数据查询返回 -1 时返回 ERROR(`DataAnalysisAction.java:52-54`)
- **证据**:`DataAnalysisAction.java:37-56`

#### 2.5 文件管理

**FR-2.5.1 文件上传(老架构)**
- **触发**:用户提交上传表单(upload.action)
- **输入**:`upload`(File[])、`uploadFileName`、`uploadFileType`
- **处理规则**:
  1. 物理存储路径 `upload/file/{随机数}/`(`UploadAction.java:54`)。
  2. 上传后 INSERT `fnd_files(fileName, filePath, fileType, uploadBy, uploadTime)`,返回自增 ID(`sql-map-admin-config.xml:931-936`)。
- **输出**:`fileIds`(逗号分隔 ID)
- **异常**:异常打印堆栈但返回 SUCCESS(`UploadAction.java:60-63`)
- **证据**:`UploadAction.java:49-64`、`sql-map-admin-config.xml:931-936`

**FR-2.5.2 文件删除(老架构)**
- **触发**:AJAX 调用 deleteFile.action
- **输入**:`fileId`
- **处理规则**:DELETE FROM `fnd_files WHERE id=fileId`(`sql-map-admin-config.xml:980-984`)
- **输出**:`message`("删除成功!" / "删除失败!")
- **证据**:`UploadAction.java:69-80`、`sql-map-admin-config.xml:980-984`

**FR-2.5.3 文件下载(老架构)**
- **触发**:用户点击下载链接(download.action)
- **输入**:`fileId`
- **处理规则**:查询 `fnd_files` 获取 `fileName`/`filePath`(`sql-map-admin-config.xml:937-941`),以 `application/octet-stream;charset=ISO8859-1` 流式响应,文件名 ISO8859-1 编码
- **输出**:文件流
- **证据**:`UploadAction.java:86-95`、`struts-sys.xml:422-429`

**FR-2.5.4 文件列表查询(老架构)**
- **触发**:AJAX 调用 queryFile.action
- **输入**:`fileIds`(逗号分隔)
- **处理规则**:联查 `fnd_files` 与 `fnd_user_info` 获取上传人姓名(`sql-map-admin-config.xml:954-960`)
- **输出**:`fileList`(文件信息列表)、`fileIds`、`message`
- **证据**:`UploadAction.java:97-106`、`sql-map-admin-config.xml:954-960`

**FR-2.5.5 富文本图片上传(老架构)**
- **触发**:富文本编辑器上传图片
- **处理规则**:物理路径 `upload/file/images/`,文件按 MD5 命名去重,返回完整 URL 列表(分号分隔)(`UploadAction.java:141-159`)
- **输出**:`message`(URL 列表)
- **证据**:`UploadAction.java:141-159`

**FR-2.5.6 新架构文件上传(按文件类型)**
- **触发**:POST `/file/baseUpload/{fileType}`
- **输入**:`fileType`(文件类型编码)、HTTP 多部分请求
- **处理规则**:按文件类型编码查 `t_file_type` 获取大小/类型/重命名/压缩/缩略图/目录配置(`FileInfoMapper.xml:325-329`),按配置处理上传后 INSERT `t_file(typeId, name, path, ext, size, dataType, dataId, createBy)`(`FileInfoMapper.xml:331-340`)
- **输出**:`Result` 含文件信息列表
- **异常**:异常 → `Result(false)` + 错误信息(`UploaderController.java:211-217`)
- **证据**:`UploaderController.java:205-218`、`FileInfoMapper.xml:325-340`

**FR-2.5.7 新架构文件下载(公开/私有)**
- **触发**:GET `/file/down/public/{fileId}`、`/file/down/private/{fileId}`、`/file/zipdown/public/{fileIds}`
- **处理规则**:单文件直接响应流;多文件 ZIP 打包下载(`UploaderController.java:258-288`)
- **输出**:文件流或 ZIP 流
- **备注**:公开下载端点须校验 `downloadKey`(见 SC-013)
- **证据**:`UploaderController.java:258-288`

**FR-2.5.8 新架构文件列表查询**
- **触发**:GET `/file/baseUpload/list`
- **输入**:`fileInfo.typeId`、`fileIds`
- **处理规则**:按 ID 列表与类型 ID 查询 `t_file`(`FileInfoMapper.xml:355-368`),组装列定义(文件名/上传人/上传时间)
- **输出**:文件列表 + 列定义
- **证据**:`UploaderController.java:226-250`、`FileInfoMapper.xml:355-368`

**FR-2.5.9 头像上传**
- **触发**:POST `/file/avatarUpload`
- **输入**:`userId`、多部分请求(含 `__source`、`__avatar1..N`、`__initParams`)
- **处理规则**:文件名 MD5+.png,保存到 `upload/avatar/`,支持原始图与多尺寸(`UploaderController.java:78-145`)
- **输出**:`AvatarResult`(success、avatarUrls、sourceUrl、msg)
- **证据**:`UploaderController.java:54-150`

**FR-2.5.10 富文本图片上传(Summernote)**
- **触发**:POST `/file/summernoteUpload`
- **处理规则**:文件名 MD5+.png,保存到 `upload/summernote/`,返回完整 URL 列表(分号分隔)(`UploaderController.java:152-196`)
- **输出**:URL 字符串
- **证据**:`UploaderController.java:152-196`

**FR-2.5.11 下载日志记录**
- **触发**:文件下载时
- **处理规则**:INSERT `t_down_log(fileIds, ip, timeline, downloadTime, user)`(`FileInfoMapper.xml:370-375`)
- **证据**:`FileInfoMapper.xml:370-375`

#### 2.6 邮件通知与通知模板

**FR-2.6.1 邮件列表查询**
- **触发**:用户访问 `/system/mailInfo/list`
- **输入**:`MailInfo`(过滤条件)、分页参数
- **处理规则**:支持 `id`、`subject`、`isInner`、`sendTime`、`expectSendTime`、`sendFlag`、`createBy`、`createTime`、`content`、`tos`、`ccs`、`bccs`、`actualSendAddress`、`attachFiles`、`failedCount`、`failedMessage` 精确过滤 + 模糊搜索(`MailInfoMapper.xml:222-299`)
- **输出**:邮件列表
- **证据**:`MailInfoController.java:43-56`、`MailInfoMapper.xml:222-368`

**FR-2.6.2 邮件详情查询**
- **触发**:GET/POST `/system/mailInfo/{id}`
- **处理规则**:按主键查询 `t_mails`(`MailInfoMapper.xml:25-30`)
- **输出**:单条邮件
- **证据**:`MailInfoController.java:58-69`

**FR-2.6.3 新增邮件**
- **触发**:POST `/system/mailInfo/detail`
- **输入**:`MailInfo`(`subject`、`content`、`tos`、`ccs`、`bccs`、`attachFiles`、`expectSendTime`、`isInner` 等)
- **处理规则**:设置 `createTime=now()` 后 INSERT(`MailInfoController.java:77-80`;`MailInfoMapper.xml:47-149`)
- **输出**:重定向到详情页
- **证据**:`MailInfoController.java:76-81`

**FR-2.6.4 修改邮件**
- **触发**:PUT `/system/mailInfo/{id}`
- **处理规则**:按主键选择性 UPDATE(`MailInfoMapper.xml:150-200`)
- **证据**:`MailInfoController.java:83-87`

**FR-2.6.5 作废邮件**
- **触发**:POST `/system/mailInfo/invalid`
- **输入**:`id`
- **处理规则**:查询邮件 → 清空 `expectSendTime` → UPDATE(`MailInfoController.java:91-94`)
- **输出**:`status`(true/false)、`message`
- **异常**:异常 → `status=false` + 错误信息(`MailInfoController.java:95-102`)
- **证据**:`MailInfoController.java:89-103`

**FR-2.6.6 手动发送邮件**
- **触发**:POST `/system/mailInfo/send`
- **输入**:`id`
- **处理规则**:查询邮件 → 复制属性到邮件发送信息对象 → 调用邮件发送工具发送带附件邮件 → 更新发送状态(`MailInfoController.java:107-117`)
- **输出**:`status`(发送成功标志)、`message`
- **异常**:异常记录到异常处理器并返回错误 ID(`MailInfoController.java:120-123`)
- **证据**:`MailInfoController.java:105-125`

**FR-2.6.7 定时发送待发邮件**
- **触发**:定时任务(MailerJob)
- **处理规则**:
  1. 查询 `sendFlag=false AND expectSendTime<now() AND failedCount<sys.mail.sendFailed.maxCount(默认3)` 的邮件(`MailInfoMapper.xml:377-384`)
  2. 逐封调用邮件发送工具发送(`MailerJob.java:29-37`)
  3. 通过批量合并更新(`ON DUPLICATE KEY UPDATE`)发送结果:成功→`sendFlag=true, sendTime=now()`,失败→`failedCount+1, failedMessage` 追加(`MailInfoMapper.xml:415-494`)
- **输出**:无(异步更新数据库)
- **证据**:`MailerJob.java:22-53`、`MailInfoMapper.xml:377-494`

**FR-2.6.8 通知模板 CRUD**
- **触发**:用户访问 `/system/notifyTemplate` 系列端点
- **输入**:`templateCode`、`subject`、`content`、`effectiveFrom`、`effectiveTo`
- **处理规则**:标准 CRUD;列表支持模糊搜索(`templateCode`/`subject`/`content`)(`NotifyTemplateMapper.xml:167-293`);新增/修改时对 `content` 进行 HTML 白名单净化(relaxed 白名单 + 表格属性 + 相对链接保留),非二次认证状态净化、二次认证状态反转义(`NotifyTemplateController.java:92-102,120-130`)
- **输出**:模板列表/详情
- **证据**:`NotifyTemplateController.java:40-138`、`NotifyTemplateMapper.xml:20-293`

**FR-2.6.9 按编码查询有效通知模板**
- **触发**:业务模块按 `templateCode` 查询模板
- **处理规则**:`SELECT * FROM t_notify_template WHERE templateCode=? AND effectiveFrom<now() AND (effectiveTo IS NULL OR effectiveTo>now())`(`NotifyTemplateMapper.xml:154-160`)
- **输出**:单条模板
- **证据**:`NotifyTemplateMapper.xml:154-160`、`MailInfoMapper.xml:370-375`

#### 2.7 系统变量

**FR-2.7.1 查询系统变量(老架构)**
- **触发**:业务模块按 `code` 读取配置
- **处理规则**:`SELECT var FROM fnd_sys_arg WHERE code=? AND effectiveFrom<=now() AND (effectiveTo IS NULL OR effectiveTo>now())`(`sql-map-admin-config.xml:862-867`)
- **输出**:变量值字符串
- **证据**:`sql-map-admin-config.xml:862-875`

**FR-2.7.2 新架构系统变量 CRUD**
- **触发**:用户访问 `/system/sysVariable` 系列端点
- **输入**:`code`、`var`、`remark`、`effectiveFrom`、`effectiveTo`
- **处理规则**:标准 CRUD;列表查询时若会话非二次认证(`isSC!=true`),`var` 字段以加密返回(`SystemVariableController.java:60-66`);详情查询同理(`SystemVariableController.java:84-87`);更新时若非二次认证则先解密再保存(`SystemVariableController.java:108-113`)
- **输出**:系统变量列表/详情
- **证据**:`SystemVariableController.java:44-121`、`SystemVariableMapper.xml:20-25`

**FR-2.7.3 系统变量二次认证**
- **触发**:POST `/system/sysVariable/secondaryCertification`
- **输入**:`cert`(用户输入验证码)
- **处理规则**:校验 `cert` 与会话中的 `captcha` 是否相等(忽略大小写);相等则会话置 `isSC=true`,否则移除 `isSC`(`SystemVariableController.java:127-131`)
- **输出**:无(仅会话状态变更)
- **备注**:[暂定决策:`isSC` 二次认证会话标志全局共享,覆盖系统变量与通知模板;编辑通知模板前须先走系统变量二次认证]
- **证据**:`SystemVariableController.java:123-133`

**FR-2.7.4 手动缓存刷新**
- **触发**:管理员手动刷新
- **处理规则**:`UPDATE fnd_sys_arg SET var=now() WHERE code='sys.cache.latest.refreshTime'`(`sql-map-admin-config.xml:27-30`);触发基础数据缓存/用户数据缓存失效(`sql-map-admin-config.xml:22,768`)
- **证据**:`sql-map-admin-config.xml:27-30,768`

#### 2.8 集群管理

**FR-2.8.1 集群核心功能刷新**
- **触发**:POST `/cluster/refreshCore`
- **输入**:无
- **处理规则**:
  1. 校验当前用户是否拥有管理员角色,否则返回"没有权限访问该功能!"(`ClusterController.java:24-26`)
  2. 依次调用刷新当前用户菜单、刷新过滤链定义、刷新系统变量(`ClusterController.java:27-29`)
- **输出**:`Result`(success=true/"刷新成功!" 或 false/"没有权限...")
- **证据**:`ClusterController.java:22-32`

#### 2.9 数据导出与数据操作

**FR-2.9.1 通用数据导出列展示**
- **触发**:GET `/export/showExportColumns`
- **输入**:`objectName`、`objectKV`、`pageParamKV`、`fullServiceName`
- **处理规则**:查询动态列配置 + 静态列工具,按排序规则排序(`DataExportController.java:43-50`)
- **输出**:列定义页面
- **备注**:[暂定决策:动态列配置存储于 `t_data_export` 表(草稿 FR-2.9.1 引用),补入本域数据契约;字段分级待 `DataExportMapper.xml` 源码确认后补全]
- **证据**:`DataExportController.java:36-53`

**FR-2.9.2 通用数据导出执行**
- **触发**:POST `/export/dataExport`
- **输入**:`objectName`、`objectKV`、`pageParamKV`、`columns`、`fullServiceName`
- **处理规则**:
  1. 反射调用 `fullServiceName` 的 `export{ObjectName}` 方法获取数据(`DataExportController.java:71-75`)
  2. 失败时回退调用 `dataExportService.export{objectName}`(`DataExportController.java:76-79`)
  3. 含动态列(`DataExportController.java:86-87`)
- **输出**:导出视图(含数据与列)
- **证据**:`DataExportController.java:64-89`

**FR-2.9.3 数据操作配置 CRUD**
- **触发**:用户访问 `/data` 系列端点
- **输入**:`DataOperation`(`name`、`description`、`type`(0=导出/1=导入)、`clazz`、`method`、`parameterTypes`、`columns`、`empPower`、`depPower`、`state`、`effectiveFrom`、`effectiveTo`、`formHtml`、`script`)
- **处理规则**:标准 CRUD;`type=0` 时 SQL 经校验(SQL 注入正则 + 表白名单/黑名单 + HTML 净化)(`DataOperationController.java:165-173,219-227,712-764`);`formHtml` 经表单白名单净化(`DataOperationController.java:175,229`);权限校验:管理员豁免 `empPower` 校验可操作所有配置,非管理员须 `empPower` 包含当前用户 ID(`DataOperationController.java:766-784`)
- **输出**:配置列表/详情
- **证据**:`DataOperationController.java:76-242`、`DataOperationMapper.xml:30-200`

**FR-2.9.4 数据导入执行**
- **触发**:POST `/data/import/{operationName}`
- **输入**:`operationName`、`fileExcel`(MultipartFile)
- **处理规则**:
  1. 校验文件扩展名 `.xlsx`(`DataOperationController.java:288`)
  2. 按配置的 `clazz`、`method`、`parameterTypes` 反射调用 Service 方法,首参为 `MultipartFile`,可选第二参 `request.getParameterMap()`(`DataOperationController.java:294-313`)
  3. 返回错误列表 JSON(去除 stackTrace 等字段)(`DataOperationController.java:314-327`)
- **输出**:JSON `{errorMessage, progress}`
- **异常**:文件格式错误/为空 → `errorMessage`(`DataOperationController.java:341-346`)
- **证据**:`DataOperationController.java:280-349,555-619`

**FR-2.9.5 数据导出执行(SQL 脚本)**
- **触发**:POST `/data/operation/{id}` 且 `type=0`
- **输入**:`id`、`objectName`、`objectKV`、`pageParamKV`、`columns`
- **处理规则**:
  1. 读取 `script`,经 SQL 校验并填充用户参数(`DataOperationController.java:510-514,720-764`)
  2. 以流式 Excel 写出(窗口大小 100 行)(`DataOperationController.java:522-542`)
  3. 进度写入会话属性 `operationName`(`DataOperationController.java:489,535,543`)
- **输出**:Excel 流
- **异常**:异常记录到异常处理器并抛出含错误 ID 的异常(`DataOperationController.java:545-547`)
- **证据**:`DataOperationController.java:481-553`

**FR-2.9.6 数据导出预览**
- **触发**:GET `/data/export/preview/{id}`
- **处理规则**:与导出执行相同校验逻辑,但不写出 Excel,返回预览数据(`DataOperationController.java:629-686`)
- **输出**:预览数据列表
- **证据**:`DataOperationController.java:629-686`

**FR-2.9.7 数据导出列查询(SQL)**
- **触发**:GET `/data/export/queryExportColumns`
- **输入**:`sql`
- **处理规则**:SQL 校验后查询 SQL 元数据获取列名(`DataOperationController.java:430-441`)
- **输出**:列名列表
- **证据**:`DataOperationController.java:402-445`

### Key Entities *(include if feature involves data)*

> 以下为 DATA-REUSE-01 数据契约。表结构为契约,新系统 MUST 默认复用既有表与字段(C 级字段对外稳定,不得删减);类型按 MySQL 方言反推,新系统可替换为等价类型。
>
> 字段分级:**C**=契约字段(对外稳定)、**I**=内部字段(实现细节)、**D**=废弃字段
>
> 表分类:
> - **本域专属表**:仅在本域业务中使用
> - **与 001 域共享表**:表结构在 `sql-map-admin-config.xml` 中,但业务归属本域(基础数据、文件、邮件、系统变量、操作日志)
> - **跨域引用表**:本域查询但不维护

#### 数据契约

##### 表 fnd_basic_data(基础数据/字典项)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一标识 | C |
| dataTypeCode | VARCHAR | 否 | 数据类型编码,关联 `fnd_basic_data_type.dataTypeCode` | 必须存在于类型表 | C |
| basicDataId | VARCHAR | 否 | 字典项业务编码(同类型内唯一) | 同 `dataTypeCode` 下唯一(`sql-map-admin-config.xml:857-861`) | C |
| basicDataName | VARCHAR | 否 | 字典项显示名称 | — | C |
| basicDataAttri1 | VARCHAR | 是 | 自定义属性 1(用于父子层级、报表颜色等) | — | C |
| sortId | INT | 是 | 排序号,按升序展示 | — | C |
| createTime | DATETIME | 是 | 创建时间 | — | I |
| createBy | VARCHAR | 是 | 创建人用户名 | — | I |
| effectiveFrom | DATETIME | 是 | 生效起始时间 | `< now()` 时才被查询 | C |
| effectiveTo | DATETIME | 是 | 失效时间, NULL 表示永久有效 | `IS NULL OR > now()` 时才被查询 | C |

- **证据**:`sql-map-admin-config.xml:774-784`(resultMap)、`851-856`(insert)、`835-850`(update)、`857-861`(唯一性校验)
- **业务规则**:查询均按 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())` 过滤;基础数据缓存在 insert/update/refreshCacheData 时失效(`sql-map-admin-config.xml:763-771`)

##### 表 fnd_basic_data_type(基础数据类型)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| dataTypeCode | VARCHAR | 否 | 类型编码,主键 | 唯一 | C |
| dataTypeName | VARCHAR | 否 | 类型名称 | — | C |
| status | INT | 是 | 状态(1=有效) | `=1` 时才被查询(`sql-map-admin-config.xml:827`) | C |
| effectiveFrom | DATETIME | 是 | 生效起始时间 | — | C |
| effectiveTo | DATETIME | 是 | 失效时间 | — | C |

- **证据**:`sql-map-admin-config.xml:821-829`
- **业务规则**:仅 `status=1 AND effectiveFrom<NOW() AND (effectiveTo IS NULL OR effectiveTo>NOW())` 的类型被查询(`sql-map-admin-config.xml:825-829`)

##### 表 fnd_sys_arg(系统变量)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| code | VARCHAR | 否 | 变量编码,主键 | 唯一 | C |
| var | VARCHAR | 是 | 变量值(可为时间字符串如 `sys.cache.latest.refreshTime`) | — | C |
| effectiveFrom | DATETIME | 是 | 生效起始时间 | `<= now()` 时才被查询 | C |
| effectiveTo | DATETIME | 是 | 失效时间 | — | C |

- **证据**:`sql-map-admin-config.xml:27-30`(refreshCacheData)、`862-875`(query_sys_arg/querySysArgList)
- **特殊编码**:`sys.cache.latest.refreshTime`(最近一次手动清理缓存时间)、`pm.report.project.summary.status`(项目状态汇总维度配置)、`pm.report.project.summary.executionState.relation`(实施状态映射)、`sys.mail.sendFailed.maxCount`(邮件发送失败最大次数,默认 3)、`sys.sql.inject.filter`(SQL 注入过滤正则)、`sys.sql.table.whitelist.regex`/`sys.sql.table.blacklist.regex`(表白/黑名单正则)

##### 表 fnd_files(文件)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| fileName | VARCHAR | 否 | 文件名 | — | C |
| filePath | VARCHAR | 否 | 文件存储相对路径(含分隔符前后缀) | — | C |
| fileType | VARCHAR | 是 | 文件类型 | — | I |
| uploadBy | VARCHAR | 是 | 上传人用户名,关联 `fnd_user_info.username` | — | C |
| uploadTime | DATETIME | 是 | 上传时间 | — | I |

- **证据**:`sql-map-admin-config.xml:925-936`(insert)、`937-941`(query_flie_info)、`942-960`(query_file_list)、`980-984`(delete)
- **业务规则**:`filePath` 存储形如 `\upload\file\{randNum}\` 的相对路径,与 `fileName` 拼接后通过资源流读取

##### 表 fnd_mails(邮件)【老架构,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| mailSubject | VARCHAR | 否 | 邮件主题 | — | C |
| mailContent | LONGTEXT | 是 | 邮件内容 | — | C |
| mailTos | LONGTEXT | 是 | 收件人列表(分号分隔) | — | C |
| mailCcs | LONGTEXT | 是 | 抄送列表 | — | C |
| mailBcc | LONGTEXT | 是 | 密送列表 | — | C |
| mailAttachFiles | LONGTEXT | 是 | 附件文件名列表 | — | C |
| mailServerPort | VARCHAR | 是 | 邮件服务器端口 | — | I |
| mailServerHost | VARCHAR | 是 | 邮件服务器主机 | — | I |
| mailUsername | VARCHAR | 是 | 发件人账号 | — | I |
| mailPassword | VARCHAR | 是 | 发件人密码 | — | I |
| mailFromaddress | VARCHAR | 是 | 发件人地址 | — | C |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | DATETIME | 是 | 创建时间 | — | I |
| effectiveFrom | DATETIME | 是 | 生效时间 | — | I |
| mailExpectSendTime | DATETIME | 是 | 期望发送时间 | `sendFlag=0 AND < now()` 时被定时任务拾取 | C |
| mailSendTime | DATETIME | 是 | 实际发送时间 | 发送成功后置为 `now()` | I |
| sendFlag | INT | 是 | 发送标志(0=未发,1=已发) | — | C |

- **证据**:`sql-map-admin-config.xml:685-700`(insert)、`715-721`(query_sys_mails)、`723-727`(update_sys_mails_state)
- **备注**:老架构表;新架构使用 `t_mails`(见下表),字段以驼峰命名

##### 表 t_mails(邮件)【新架构,本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| subject | VARCHAR | 否 | 邮件主题 | — | C |
| sendTime | TIMESTAMP | 是 | 实际发送时间 | 发送成功后置为 `now()` | I |
| expectSendTime | TIMESTAMP | 是 | 期望发送时间 | 作废时置 NULL;`sendFlag=false AND < now() AND failedCount<max` 时被拾取 | C |
| sendFlag | BIT | 是 | 发送标志(false=未发,true=已发) | — | C |
| failedCount | INT | 是 | 失败次数 | 超过 `sys.mail.sendFailed.maxCount` 不再重试 | C |
| failedMessage | LONGTEXT | 是 | 失败原因(多次失败以逗号追加) | — | I |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| content | LONGTEXT | 是 | 邮件内容 | — | C |
| tos | LONGTEXT | 是 | 收件人列表 | — | C |
| ccs | LONGTEXT | 是 | 抄送列表 | — | C |
| bccs | LONGTEXT | 是 | 密送列表 | — | C |
| actualSendAddress | LONGTEXT | 是 | 实际发送地址 | — | I |
| attachFiles | LONGTEXT | 是 | 附件列表 | — | C |
| isInner | BIT | 是 | 是否内部邮件 | — | C |

- **证据**:`MailInfoMapper.xml:4-21`(resultMap)、`22-24`(列清单)、`35-46`(insert)、`47-149`(insertSelective)、`150-200`(updateSelective)、`386-494`(发送状态批量更新)
- **业务规则**:发送结果通过批量合并更新(`ON DUPLICATE KEY UPDATE`)避免并发冲突(`MailInfoMapper.xml:415-494`)

##### 表 tb_sys_log(系统操作日志)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| ID | INT | 否 | 主键,自增 | 唯一 | C |
| USER_NAME | VARCHAR | 是 | 操作用户名,关联 `fnd_user_info.username` | — | C |
| IP | VARCHAR | 是 | 操作 IP | — | C |
| ACTION | VARCHAR | 是 | 操作动作 | — | I |
| RESULT | VARCHAR | 是 | 操作结果 | — | I |
| INFO | VARCHAR | 是 | 操作详情 | — | C |
| TIME | INT | 是 | 操作时间(Unix 时间戳) | — | C |

- **证据**:`sql-map-admin-config.xml:541-546`(insert)、`547-557`(resultMap)、`559-591`(select)
- **备注**:老架构表;`TIME` 字段以整型存储 Unix 时间戳(`sql-map-admin-config.xml:554-555`)

##### 表 t_sys_log(系统操作日志)【新架构,本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| description | VARCHAR | 是 | 操作描述 | — | C |
| method | VARCHAR | 是 | 操作方法 | — | I |
| type | VARCHAR | 是 | 操作类型 | — | C |
| request_ip | VARCHAR | 是 | 请求 IP | — | C |
| exception_code | VARCHAR | 是 | 异常代码 | — | I |
| exception_detail | VARCHAR | 是 | 异常详情 | — | I |
| params | VARCHAR | 是 | 请求参数 | — | I |
| create_by | VARCHAR | 是 | 操作人 | — | C |
| create_date | VARCHAR | 是 | 操作时间(字符串,格式 `yyyy-MM-dd HH:mm:ss`) | — | I(新系统新增 TIMESTAMP 列为 C 级) |

- **证据**:`SysLogMapper.xml:4-15`(resultMap)、`16-19`(列清单)、`20-25`(select)、`30-39`(insert)、`40-106`(insertSelective)、`107-152`(update)、`154-221`(查询)

##### 表 t_sync_log(同步日志)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| targetMethod | CHAR | 是 | 目标方法名 | — | C |
| tableObject | CHAR | 是 | 同步表对象 | — | C |
| dataFrom | CHAR | 是 | 数据来源 | — | C |
| dataTo | CHAR | 是 | 数据目标 | — | C |
| syncParams | VARCHAR | 是 | 同步参数(JSON) | — | I |
| syncStartTime | TIMESTAMP | 是 | 同步开始时间 | — | C |
| syncEndTime | TIMESTAMP | 是 | 同步结束时间 | — | C |
| isSuccess | BIT | 是 | 是否成功 | — | C |
| dataCount | INT | 是 | 同步数据量 | — | C |
| syncType | SMALLINT | 是 | 同步类型 | — | I |
| exception | LONGTEXT | 是 | 异常堆栈 | — | I |

- **证据**:`SyncLogMapper.xml:4-19`(resultMap)、`20-26`(列清单)、`27-34`(select)、`39-49`(insert)、`51-129`(insertSelective)、`130-197`(update)、`200-312`(查询)

##### 表 t_dictionary(数据字典)【新架构,本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| dic_type_id | INT | 否 | 字典类型 ID(同类型字典共享) | 同 `dic_type_id` + `dic_key` 应唯一 | C |
| dic_type_name | VARCHAR | 是 | 字典类型名称 | — | C |
| dic_key | VARCHAR | 否 | 字典 Key | — | C |
| dic_value | VARCHAR | 否 | 字典 Value | — | C |
| customInfo | VARCHAR | 是 | 自定义属性 | — | I |
| sort | INT | 是 | 排序号 | — | C |
| status | INT | 是 | 状态(1=有效,0=无效) | — | C |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |

- **证据**:`DictionaryMapper.xml:4-15`(resultMap)、`16-19`(列清单)、`20-25`(select)、`30-39`(insert)、`40-106`(insertSelective)、`107-145`(update)、`146-196`(分页查询)、`199-245`(计数)、`246-263`(类型查询)
- **业务规则**:`selectMaxDicTypeId` 用于新增类型时分配 `dic_type_id`(`DictionaryMapper.xml:256-258`)

##### 表 t_sys_variable(系统变量)【新架构,本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| code | VARCHAR | 否 | 变量编码,主键 | 唯一 | C |
| id | INT | 是 | 自增 ID(冗余) | — | I |
| var | VARCHAR | 是 | 变量值(可能以 AES 加密存储) | — | C |
| remark | VARCHAR | 是 | 备注 | — | I |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |
| effectiveFrom | TIMESTAMP | 是 | 生效起始时间 | — | C |
| effectiveTo | TIMESTAMP | 是 | 失效时间 | — | C |

- **证据**:`SystemVariableMapper.xml:4-15`(resultMap)、`16-19`(列清单)、`20-25`(select)
- **业务规则**:`var` 字段在非二次认证场景下以 AES 加密返回(`SystemVariableController.java:60-66,82-87`;草稿 `ASE` 视为 `AES` 笔误)

##### 表 t_notify_template(通知模板)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| templateCode | VARCHAR | 否 | 模板编码(业务检索键) | 同编码可多条,按生效时间取当前有效 | C |
| subject | VARCHAR | 是 | 模板标题 | — | C |
| content | LONGTEXT | 是 | 模板内容(HTML,经白名单净化) | — | C |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |
| effectiveFrom | TIMESTAMP | 是 | 生效起始时间 | `< now()` 才被查询 | C |
| effectiveTo | TIMESTAMP | 是 | 失效时间 | `IS NULL OR > now()` 才被查询 | C |
| priority | INT | 是 | 优先级(查询时排序用) | — | I |

- **证据**:`NotifyTemplateMapper.xml:4-15`(resultMap)、`16-19`(列清单)、`20-25`(select)、`30-39`(insert)、`40-106`(insertSelective)、`107-152`(update)、`154-160`(按编码查有效模板)、`162-204`(按选择查询含 `order by priority`)
- **业务规则**:查询有效模板 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())`(`NotifyTemplateMapper.xml:159`)

##### 表 t_file(文件)【新架构,本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| typeId | INT | 是 | 文件类型 ID,关联 `t_file_type.id` | — | C |
| name | VARCHAR | 否 | 文件名 | — | C |
| path | VARCHAR | 否 | 文件存储路径 | — | C |
| ext | VARCHAR | 是 | 文件扩展名 | — | I |
| size | BIGINT | 是 | 文件大小(字节) | — | I |
| createTime | TIMESTAMP | 是 | 上传时间 | — | I |
| createBy | VARCHAR | 是 | 上传人 | — | C |
| downloadKey | VARCHAR | 是 | 下载密钥(用于鉴权) | — | I |
| dataType | VARCHAR | 是 | 关联数据类型(业务类型) | — | C |
| dataId | INT | 是 | 关联数据 ID | — | C |
| customInfo | JSON | 是 | 自定义信息(JSON) | — | I |

- **证据**:`FileInfoMapper.xml:4-17`(resultMap)、`18-21`(列清单)、`22-27`(select)、`32-44`(insert)、`45-105`(insertSelective)、`291-368`(查询)、`331-340`(insertFileInfo)

##### 表 t_file_type(文件类型)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键 | 唯一 | C |
| name | VARCHAR | 是 | 类型名称 | — | C |
| code | VARCHAR | 否 | 类型编码(业务检索键) | 唯一 | C |
| limitSize | INT | 是 | 大小限制(字节) | — | C |
| allowType | VARCHAR | 是 | 允许的扩展名列表 | — | C |
| rename | BIT | 是 | 是否重命名 | — | I |
| cut | BIT | 是 | 是否压缩 | — | I |
| thumbnail | BIT | 是 | 是否生成缩略图 | — | I |
| dir | VARCHAR | 是 | 保存相对路径 | — | C |
| uploadUrl | VARCHAR | 是 | 上传 URL(预留) | — | I |

- **证据**:`FileInfoMapper.xml:325-329`(selectFileTypeByCode)、`FileType.java`
- **备注**:表结构由 POJO 反推;mapper 仅查询。[暂定决策:t_file_type 通过数据库直接维护(DBA/运维操作),无应用层 CRUD 端点]

##### 表 t_data_operation(数据操作配置)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| name | VARCHAR | 否 | 操作名称(业务检索键) | — | C |
| description | VARCHAR | 是 | 描述 | — | I |
| type | INT | 否 | 类型(0=导出/SQL,1=导入/反射) | — | C |
| clazz | VARCHAR | 是 | 目标 Service 类全名(导入用) | — | I |
| method | VARCHAR | 是 | 目标方法名(导入用) | — | I |
| parameterTypes | VARCHAR | 是 | 方法参数类型列表(空格分隔) | — | I |
| columns | VARCHAR | 是 | 导出列定义(`key=title;...`) | — | C |
| empPower | VARCHAR | 是 | 员工权限(用户 ID 列表) | 非管理员须匹配当前用户 ID;管理员豁免 | C |
| depPower | VARCHAR | 是 | 部门权限(预留) | — | I |
| state | BIT | 是 | 状态 | — | C |
| effectiveFrom | TIMESTAMP | 是 | 生效起始时间 | — | C |
| effectiveTo | TIMESTAMP | 是 | 失效时间 | — | C |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |
| formHtml | LONGTEXT | 是 | 表单 HTML(经表单白名单净化) | — | C |
| script | LONGTEXT | 是 | SQL 脚本(导出用,经 SQL 校验) | — | C |

- **证据**:`DataOperationMapper.xml:4-24`(resultMap)、`25-29`(列清单)、`30-35`(select)、`40-58`(insert)、`60-105`(insertSelective)、`107-160`(update)、`161-200`(其他)
- **业务规则**:`type=0` 时 `script` 必须经 SQL 校验通过(`DataOperationController.java:165-173`)

##### 表 t_down_log(下载日志)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| fileIds | VARCHAR | 否 | 下载的文件 ID 列表 | — | C |
| ip | VARCHAR | 是 | 下载者 IP | — | C |
| timeline | INT | 是 | 下载时间(Unix 时间戳) | — | I |
| downloadTime | DATETIME | 是 | 下载时间 | — | C |
| user | VARCHAR | 是 | 下载人 | — | C |

- **证据**:`FileInfoMapper.xml:370-375`(insertdownlog)

##### 表 mes_oqc_info(OQC 检验信息)【外部表,本域只读】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| barcode | VARCHAR | 否 | 设备序列号(查询键) | — | C |
| inspectUser | VARCHAR | 是 | 检验员(关联 `mes_seal_info.user`) | — | C |
| inspectTime | DATETIME | 是 | 检验时间(用于匹配印章领取窗口) | — | C |

- **证据**:`sql-map-certificate-config.xml:11-30`
- **备注**:此表位于 MES 系统,本域只读查询,不维护

##### 表 mes_seal_info(印章信息)【本域维护,外部表】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | VARCHAR | 否 | 印章 ID | — | C |
| name | VARCHAR | 是 | 印章名称 | — | C |
| info | VARCHAR | 是 | 印章信息(以 "QC PASS" 开头,含合格证号) | — | C |
| description | VARCHAR | 是 | 描述 | — | I |
| user | VARCHAR | 是 | 领用检验员(关联 `mes_oqc_info.inspectUser`) | — | C |
| takeTime | DATETIME | 是 | 领用时间 | — | C |
| backTime | DATETIME | 是 | 归还时间(NULL 表示未归还) | — | C |
| remark | VARCHAR | 是 | 备注 | — | I |
| uploadBy | VARCHAR | 是 | 上传人 | — | I |
| (createTime) | DATETIME | 是 | 创建时间(INSERT 时 `now()`) | — | I |

- **证据**:`sql-map-certificate-config.xml:39-57`(insertSealInfo、truncateSealInfo)
- **业务规则**:导入前先 `truncate`(`sql-map-certificate-config.xml:55-57`);查询时 `info like 'QC PASS%'` 且 `inspectTime` 在 `[takeTime, backTime]` 区间内(`sql-map-certificate-config.xml:18-28`)

##### 表 pm_report_line_data(报表趋势数据)【本域维护,跨域引用项目表】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| dataTypeCode | VARCHAR | 否 | 数据类型编码(如 `impl0`/`impl1`/`impl3`、各率编码) | — | C |
| officeCode | VARCHAR | 否 | 办事处编码(`total` 表示全国) | — | C |
| conditionValue | DECIMAL | 是 | 分子值 | — | I |
| totalValue | DECIMAL | 是 | 分母值 | — | I |
| specificValue | DECIMAL | 是 | 计算后的百分比/数值 | — | C |
| settingTime | DATETIME | 是 | 数据发生时间(按月聚合) | — | C |
| createTime | DATETIME | 是 | 创建时间 | — | I |
| effectiveFrom | DATETIME | 是 | 生效起始时间 | — | I |
| effectiveTo | DATETIME | 是 | 失效时间(NULL 表示当前有效) | `IS NULL` 时被趋势查询 | C |

- **证据**:`sql-map-report-config.xml:467-512`(查询)、`555-580`(批量插入)、`586-603`(impl 趋势查询)
- **业务规则**:趋势查询 `effectiveTo IS NULL ORDER BY settingTime`;同一 `dataTypeCode + officeCode + settingTime` 历史数据通过 `effectiveTo` 失效

##### 表 fnd_company(公司)【与 001 共享,本域查询】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键 | 唯一 | C |
| name | VARCHAR | 是 | 公司名称 | — | C |
| code | VARCHAR | 是 | 公司编码 | — | C |
| status | INT | 是 | 状态 | — | C |
| pid | INT | 是 | 上级公司 ID | — | C |

- **证据**:`sql-map-admin-config.xml:1007-1032`(queryCompanyList、queryCompanyOne)
- **备注**:老架构表;新架构表为 `t_company`(见下表,字段更丰富含账套/法人/地址等)

##### 表 t_company(公司)【新架构,本域查询】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键 | 唯一 | C |
| compCode | VARCHAR | 否 | 公司编号 | — | C |
| compName | VARCHAR | 否 | 公司名称 | — | C |
| compAbbr | VARCHAR | 是 | 公司简称 | — | C |
| compAccount | VARCHAR | 是 | 公司账套 | — | C |
| adminID | INT | 是 | 上级公司 ID | — | C |
| compGrade | INT | 是 | 公司级别 | — | I |
| lawyer | VARCHAR | 是 | 法人 | — | I |
| address | VARCHAR | 是 | 地址 | — | I |
| regAddress | VARCHAR | 是 | 注册地址 | — | I |
| tel | VARCHAR | 是 | 电话 | — | I |
| fax | VARCHAR | 是 | 传真 | — | I |
| postCode | VARCHAR | 是 | 邮编 | — | I |
| webSite | VARCHAR | 是 | 网站 | — | I |
| state | BIT | 是 | 失效状态 | — | C |
| effectiveFrom | TIMESTAMP | 是 | 成立时间 | — | C |
| effectiveTo | TIMESTAMP | 是 | 结束时间 | — | C |
| disabledTime | TIMESTAMP | 是 | 失效时间 | — | I |
| remark | VARCHAR | 是 | 备注 | — | I |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |

- **证据**:`CompanyMapper.xml:4-28`(resultMap)、`29-33`(列清单)
- **备注**:[暂定决策:t_company 归属 001 域组织管理(维护方在 001),本域仅查询,按"跨域引用表"处理]

##### 表 t_data_export(数据导出列配置)【本域专属,待源码补全】

[暂定决策:动态导出列配置存储于此表(草稿 FR-2.9.1 引用),补入本域数据契约;字段分级待 `DataExportMapper.xml` 源码确认后补全]

##### 跨域引用表(本域查询不维护)

- `pm_project_header`、`pm_project_member`、`pm_project_state`、`pm_project_contract`、`pm_project_group_relationship`:项目主数据,见 004 域
- `pm_cl_evaluation_header`、`pm_cl_quesnaire_result_header`、`pm_cl_quesnaire_result_line`、`pm_cl_quesnaire_template_line`:客户评价数据,见回调/客户评价域
- `fnd_department`、`fnd_user_info`、`fnd_user_power`、`fnd_roles`、`fnd_menus`、`fnd_user_menus`、`fnd_role_menus`:组织与权限数据,见 001 域

## Success Criteria *(mandatory)*

> 以下由草稿第4章非功能需求(NFR)转化为可测量标准。技术选型记录在 plan.md 中,此处仅描述系统须具备的质量属性与可度量阈值。

### Measurable Outcomes

**性能**

- **SC-001**:基础数据查询在缓存命中时响应时间 < 100ms,缓存未命中时 < 500ms;缓存容量 50 条,1 小时自动刷新,insert/update/手动刷新时失效。(源自 NFR-4.1.1)
- **SC-002**:用户/角色/菜单查询在缓存命中时响应时间 < 100ms;缓存容量 50 条,1 小时自动刷新,相关 insert/update 时失效。(源自 NFR-4.1.2)
- **SC-003**:质量报表与实施方式报表通过临时表聚合后查询,查询完成后自动清理临时表(`DROP TABLE IF EXISTS`),避免主表查询压力。[暂定决策:新系统改用 MySQL 会话级临时表(`CREATE TEMPORARY TABLE`)隔离并发查询,并发查询互不干扰]。(源自 NFR-4.1.3)
- **SC-004**:数据导出 10 万行数据时内存峰值 < 512MB(通过流式 Excel 写出,窗口大小 100 行),进度以百分比实时写入会话供前端轮询。(源自 NFR-4.1.4)
- **SC-005**:邮件发送结果通过批量合并更新减少数据库往返,N 封邮件的发送结果更新在 1 次批量操作内完成。(源自 NFR-4.1.5)

**安全**

- **SC-006**:数据导出/数据操作中的 SQL 经三层校验(HTML 净化 → SQL 注入正则匹配 → 表白名单/黑名单校验),命中注入规则时重定向至非法提示页面,通过率 100%。(源自 NFR-4.2.1)
- **SC-007**:系统变量敏感值在非二次认证场景下以加密形式返回,二次认证(验证码校验通过)后返回明文;加密/解密对业务透明。[暂定决策:统一采用 AES 对称加密(草稿 `ASE` 视为 `AES` 笔误),新系统迁移时以 AES 实现]。(源自 NFR-4.2.2)
- **SC-008**:通知模板内容经 HTML 白名单净化(relaxed 白名单 + 表格属性 + 相对链接),`<script>` 等危险标签移除率 100%。(源自 NFR-4.2.3)
- **SC-009**:数据操作配置含员工权限(`empPower`),非管理员仅能操作 `empPower` 包含自身用户 ID 的配置,越权拒绝率 100%。(源自 NFR-4.2.4)
- **SC-010**:集群核心功能刷新仅管理员角色可调用,非管理员调用返回"没有权限"且不执行任何刷新。(源自 NFR-4.2.5)
- **SC-011**:项目状态汇总报表对非管理员注入区域权限限制,SQL 中通过办事处编码过滤,非管理员仅可见授权办事处数据。(源自 NFR-4.2.6)
- **SC-012**:非授权角色(非管理员/工程经理/工程经理组长/财务/项目管理员/回访员)访问报表首页被重定向至项目状态汇总页。(源自 NFR-4.2.7)
- **SC-013**:文件下载区分公开与私有端点,私有端点需登录;[暂定决策:公开下载端点须校验 `t_file.downloadKey`(无 downloadKey 或不匹配则拒绝),防止 ID 枚举遍历]。(源自 NFR-4.2.8)

**可观测性**

- **SC-014**:新架构关键操作(查看/新增/修改/删除字典)通过操作日志注解自动记录到操作日志表,覆盖率 100%。(源自 NFR-4.3.1)
- **SC-015**:外部数据同步任务记录到同步日志(目标方法、表对象、起止时间、成功标志、数据量、异常),可追溯率 100%。(源自 NFR-4.3.2)
- **SC-016**:文件下载记录到下载日志(文件 ID、IP、时间、用户),可追溯率 100%。(源自 NFR-4.3.3)
- **SC-017**:关键操作异常通过异常处理器记录并返回错误 ID,用户可凭错误 ID反馈,错误 ID 生成率 100%。(源自 NFR-4.3.4)
- **SC-018**:数据导出进度以百分比形式写入会话属性,前端轮询可获取实时进度,进度更新延迟 < 1 秒。(源自 NFR-4.3.5)

**可用性**

- **SC-019**:提供手动刷新基础数据/用户数据缓存能力,刷新后缓存立即失效,下次查询从数据库重新加载。(源自 NFR-4.4.1)
- **SC-020**:集群环境下提供核心功能刷新端点,刷新当前用户菜单、过滤链、系统变量,刷新成功率 100%。(源自 NFR-4.4.2)
- **SC-021**:支持通过 AJAX 指定任务名手动触发定时同步任务,不必等待定时调度,触发后任务立即执行。(源自 NFR-4.4.3)

**兼容性**

- **SC-022**:老架构(`fnd_*`/`tb_sys_log` 表)与新架构(`t_*` 表)并存,基础数据、邮件、系统变量、操作日志存在两套表结构可分别服务;[暂定决策:过渡期新老表分别服务不同模块(老模块读写老表、新模块读写新表),无双向同步;迁移时按子域分批合并到新表,合并前老表只读保留]。(源自 NFR-4.5.1)
- **SC-023**:SQL 校验时根据当前数据源类型适配 SQL 解析,支持多数据源环境下统一校验逻辑。(源自 NFR-4.5.2)

**数据完整性**

- **SC-024**:基础数据、系统变量、通知模板均通过 `effectiveFrom`/`effectiveTo` 控制时效,各实体按其既定规则过滤(新架构统一 `effectiveFrom < now()`;老架构系统变量保留 `effectiveFrom <= now()` 含生效时刻),`(effectiveTo IS NULL OR effectiveTo > now())`,过期数据不可见率 100%。(源自 NFR-4.6.1)
- **SC-025**:邮件发送失败次数达到 `sys.mail.sendFailed.maxCount`(默认 3)后不再重试(以代码 `failedCount < maxCount` 为准,失败计数从 0 开始,最多重试 maxCount 次),无限重试发生率 0%。(源自 NFR-4.6.2)
- **SC-026**:同一 `dataTypeCode` 下 `basicDataId` 唯一,通过唯一性校验保证,重复编码写入率 0%。(源自 NFR-4.6.3)

## Assumptions

- **认证系统复用**:现有用户认证与角色权限体系(001 域)将被复用,本域不重新实现登录/权限分配。
- **新老架构过渡期并存**:老架构与新架构在过渡期内并存,部分子域(基础数据、邮件、系统变量、操作日志)存在两套表结构,新系统迁移时须制定合并策略。
- **外部 MES 系统可用**:OQC 合格证查询依赖外部 MES 系统的 `mes_oqc_info` 表,假定该表由 MES 系统维护且可读。
- **邮件服务器可用**:邮件发送依赖已配置的邮件服务器(SMTP),假定服务器地址、端口、账号、密码已在系统变量或配置中设置。
- **文件存储为本地文件系统**:当前文件存储使用本地文件系统(`upload/` 目录),新系统迁移时须评估是否切换为对象存储。
- **定时任务调度可用**:邮件定时发送、报表趋势数据持久化、外部数据同步依赖定时任务调度框架,假定调度框架正常运行。
- **项目主数据先行就绪**:报表统计依赖 004 域项目主数据(`pm_project_header`、`pm_project_state` 等)和客户评价数据先行同步到位。
- **区域权限数据准确**:报表区域权限(`areaPowers`)依赖 001 域的组织/部门数据,假定权限映射关系准确维护。
