# 009-system-base 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts + core 代码,日期 2026-07-09
> 域职责:基础数据(字典)维护、操作日志、合格证管理、报表统计与数据分析、文件上传下载、邮件通知与通知模板、系统变量、集群管理、数据导出
> 证据基线:
> - 老架构(Struts):`PMS-struts/config/struts-sys.xml`、`PMS-struts/config-ibaits/sql-map-admin-config.xml`、`PMS-struts/config-ibaits/sql-map-certificate-config.xml`、`PMS-struts/config-ibaits/sql-map-report-config.xml`、`PMS-struts/src/com/dp/plat/action/BasicDataManageAction.java`、`OperateLogAction.java`、`UploadAction.java`、`ReportAction.java`、`DataAnalysisAction.java`、`PMS-struts/src/com/dp/plat/plus/certificate/action/CertificateAction.java`
> - 新架构(过渡):`core/src/main/java/com/dp/plat/core/controller/admin/DictionaryController.java`、`SystemVariableController.java`、`NotifyTemplateController.java`、`SyncLogController.java`、`SysLogController.java`、`SubModalController.java`、`core/src/main/java/com/dp/plat/core/controller/DataExportController.java`、`DataOperationController.java`、`UploaderController.java`、`cluster/ClusterController.java`、`core/src/main/java/com/dp/plat/support/mail/controller/MailInfoController.java`、`core/src/main/java/com/dp/plat/core/schedule/MailerJob.java`
> - 表结构证据:`core/src/main/java/com/dp/plat/core/mapping/`(DictionaryMapper/SystemVariableMapper/NotifyTemplateMapper/SyncLogMapper/SysLogMapper/FileInfoMapper/DataOperationMapper/CompanyMapper)、`core/src/main/java/com/dp/plat/support/mail/mapping/MailInfoMapper.xml`

---

## 第1章 用户故事

> 从控制器/Action 端点反推。每个端点至少 1 个故事。证据标注文件:行号。
> 端点路径分两套:老架构 `.action`(Struts,namespace `/`、`/base`、`/module`、`ajax`)、新架构 REST 路径(过渡,前缀 `/system`、`/data`、`/export`、`/file`、`/cluster`)。

### US-001: 浏览基础数据字典列表
- **故事**: As a 管理员, I want 按基础数据类型分组浏览字典项列表, so that 我能掌握系统中所有下拉枚举值的当前配置。
- **证据**: struts-sys.xml:134-136 (BasicdataManage.action);BasicDataManageAction.java:29-37;sql-map-admin-config.xml:812-820 (query_basic_data_all)、825-829 (query_basic_data_type)

### US-002: 新增基础数据
- **故事**: As a 管理员, I want 在指定数据类型下新增字典项(含编码、名称、排序、生效时间), so that 业务表单的下拉选项可扩展。
- **证据**: struts-sys.xml:145-152 (BasicdataInsert.action);BasicDataManageAction.java:51-59;sql-map-admin-config.xml:851-856 (insert_basic_data)

### US-003: 修改基础数据
- **故事**: As a 管理员, I want 修改字典项的名称、排序、失效时间, so that 字典内容可随业务调整。
- **证据**: struts-sys.xml:137-144 (BasicdataUpdate.action);BasicDataManageAction.java:39-49;sql-map-admin-config.xml:835-850 (update_basic_data)

### US-004: 基础数据编码唯一性校验
- **故事**: As a 管理员, I want 在录入字典编码时实时校验同类型下编码是否重复, so that 避免数据冲突。
- **证据**: BasicDataManageAction.java:64-70 (findBasicDataId);sql-map-admin-config.xml:857-861 (find_basic_data_id)

### US-005: 执行基础数据维护 SQL(受限)
- **故事**: As a 管理员, I want 通过界面直接执行含 WHERE 子句的 UPDATE/DELETE 或 INSERT 语句, so that 紧急修正字典数据无需走代码部署流程。
- **证据**: BasicDataManageAction.java:72-90 (executeSql);sql-map-admin-config.xml:919-923 (execute_sql)
- **说明**: 仅允许包含 `where` 或 `insert` 关键字的 SQL;无 WHERE 的 UPDATE/DELETE 被拒绝(`BasicDataManageAction.java:75-83`)。这是明显的风险点 `[待澄清]` 是否仍保留。

### US-006: 新架构数据字典 CRUD
- **故事**: As a 管理员, I want 在新后台分页查询、新增、修改、删除统一字典(类型 ID、Key、Value、排序、状态), so that 新老架构字典可平滑迁移。
- **证据**: DictionaryController.java:34-89;DictionaryMapper.xml:20-25,40-106,107-145,146-196,199-245

### US-007: 查询操作日志
- **故事**: As a 管理员, I want 分页查询系统操作日志(用户、IP、动作、结果、信息、时间)并排序, so that 我能审计用户操作行为。
- **证据**: struts-sys.xml:923-927 (LogManage.action);OperateLogAction.java:48-52;sql-map-admin-config.xml:567-584 (select-Operation-Log)、586-591 (select-Operation-Log-Sum)

### US-008: 导出操作日志为 Excel
- **故事**: As a 管理员, I want 将全部操作日志导出为 Excel 文件, so that 我能离线分析或归档审计记录。
- **证据**: struts-sys.xml:963-971 (ExportLogAll.action);OperateLogAction.java:63-129;sql-map-admin-config.xml:559-565 (select-all-log)
- **说明**: 使用 `template/日志.xlsx` 作为模板,导出文件落地 `upload/payment/日志.xlsx` 后以流方式下载(`OperateLogAction.java:64-128,199-217`)。

### US-009: AJAX 手动触发后台同步任务
- **故事**: As a 管理员, I want 通过 AJAX 指定任务名手动触发后台 Quartz 同步任务, so that 不必等待定时调度即可执行数据同步。
- **证据**: struts-sys.xml:974-979 (syncTask.action);OperateLogAction.java:137-197
- **说明**: 任务名 `taskName` 依次尝试解析为 `CronTriggerFactoryBean` → `JobDetailFactoryBean` → 类全名,最终以同步方式 `job.execute()` 执行(`OperateLogAction.java:177-184`)。

### US-010: 新架构系统日志(操作日志)查询
- **故事**: As a 管理员, I want 在新后台分页查询系统操作日志(描述、方法、类型、请求 IP、异常、参数、操作人、时间), so that 新架构下的操作审计信息可追溯。
- **证据**: SysLogController.java:47-78;SysLogMapper.xml:154-191 (selectBySelective)、194-221 (countBySelective)

### US-011: 新架构同步日志查询
- **故事**: As a 管理员, I want 分页查询外部数据同步日志(目标方法、表对象、源/目标、参数、开始/结束时间、成功标志、数据量、异常), so that 我能监控各外部系统同步的执行情况。
- **证据**: SyncLogController.java:47-76;SyncLogMapper.xml:200-262 (selectBySelectivePageable)、265-312 (countBySelectivePageable)

### US-012: 新架构手动触发同步任务
- **故事**: As a 管理员, I want 在新后台按任务类型手动触发同步任务, so that 同步任务可在故障恢复后立即重跑。
- **证据**: SyncLogController.java:78-144 (syncData)
- **说明**: 与 US-009 等价,触发逻辑相同(`SyncLogController.java:79-143`),返回 `status` 与错误信息。

### US-013: 浏览磁盘日志文件
- **故事**: As a 运维管理员, I want 在 Web 界面浏览服务器磁盘上的日志目录及文件(名称、大小、最后修改时间), so that 我能远程定位运行时异常。
- **证据**: SysLogController.java:80-125 (logsDisk)
- **说明**: 默认从 Web 根目录与 `catalina.home` 向上回溯最多 10 层,过滤 `logs` 目录(`SysLogController.java:93-117`);路径以 Base64 URL-safe 编码传输。

### US-014: 下载服务器日志 ZIP
- **故事**: As a 运维管理员, I want 选择多个日志目录或文件打包下载为 ZIP, so that 我能离线分析完整日志。
- **证据**: SysLogController.java:127-145 (downloadLogs)

### US-015: 合格证查询主页
- **故事**: As a 服务工程师, I want 进入合格证查询页面并根据当前用户角色判断是否可上传印章信息, so that 我能扫描设备序列号查询对应的 OQC 合格证。
- **证据**: struts-sys.xml:484-487 (certificate.action);CertificateAction.java:37-41
- **说明**: `canUpload` 标志取决于用户是否拥有 `roleId=1`(`CertificateAction.java:39`)。

### US-016: 根据设备序列号查询 OQC 合格证
- **故事**: As a 服务工程师, I want 输入设备序列号(barcode)查询 OQC 检验员、合格证号与生产日期, so that 我能向客户出示合格证。
- **证据**: CertificateAction.java:48-75 (queryCertificate);sql-map-certificate-config.xml:11-30 (queryOQCInfo)
- **说明**: 从 `mes_oqc_info` 关联 `mes_seal_info`(检验员印章领取/归还时间窗口)提取 `info` 中以 "QC PASS" 开头的字符串,正则解析出合格证号;生产日期取 barcode 第 10-12 位以 16 进制解析月份(`CertificateAction.java:83-99`)。

### US-017: 批量上传印章信息
- **故事**: As a 管理员, I want 通过 Excel 批量导入印章领用信息(印章 ID、名称、检验员、领用时间、归还时间、备注), so that OQC 合格证查询能匹配到正确检验员。
- **证据**: struts-sys.xml:476-482 (uploadSealInfo.action);CertificateAction.java:101-109;sql-map-certificate-config.xml:39-57 (insertSealInfo、truncateSealInfo)
- **说明**: 导入前先清空 `mes_seal_info` 表(`sql-map-certificate-config.xml:55-57` truncateSealInfo)。

### US-018: 浏览报表首页(综述+各率柱状图)
- **故事**: As a 管理员/工程经理, I want 在报表首页查看全国项目统计综述、指派率、跟踪率、闭环比表格, so that 我能掌握全国项目交付情况。
- **证据**: struts-sys.xml:280-283 (report_*);ReportAction.java:113-145 (show)
- **说明**: 表格 HTML 在服务端组装(`ReportAction.java:128,131,137`)。

### US-019: 查询项目指派率(分办事处)
- **故事**: As a 管理员, I want 查询各办事处项目经理指派率柱状图数据, so that 我能识别指派率偏低的办事处。
- **证据**: struts-sys.xml:1007-1011 (assignedRate);ReportAction.java:291-313;sql-map-report-config.xml:36-53 (query_reportline_assigned_info)
- **说明**: 分子=已指派项目经理的项目数(`pm_project_member.memberRole='30' AND fromFlag=1`),分母=该项目状态(30/31/32)下的项目总数。

### US-020: 查询项目经理跟踪率
- **故事**: As a 管理员, I want 查询各办事处项目经理跟踪率(排除工程计划状态=40), so that 我能识别跟踪不到位的办事处。
- **证据**: struts-sys.xml:1028-1032 (traceRate);ReportAction.java:322-344;sql-map-report-config.xml:81-103 (query_reportline_trace_info)

### US-021: 查询季度闭环新增比
- **故事**: As a 管理员, I want 查询各办事处季度闭环项目数与新增项目数之比, so that 我能评估交付吞吐。
- **证据**: struts-sys.xml:1033-1037 (closeRate);ReportAction.java:349-386;sql-map-report-config.xml:381-397 (query_reportline_closed_info)
- **说明**: 闭环项目限定 `projectState=100`;新增项目限定 `createTime > 季度开始 AND projectState > 20`(`sql-map-report-config.xml:354-361,381-397`)。

### US-022: 查询企业网项目实施方式占比
- **故事**: As a 管理员, I want 查询各办事处企业网项目三种实施方式(原厂直服/原厂督导/代理商自服)占比柱状图, so that 我能识别交付模式分布。
- **证据**: struts-sys.xml:1038-1042 (implRate);ReportAction.java:392-457;sql-map-report-config.xml:399-465 (create_implway_tmp_table、query_reportline_impl_info)
- **说明**: 实施方式取 `column012`,若为空且 `columno12_readonly != -1` 则取 `columno12_readonly`;项目类别限定 `column004 LIKE '企业网%' OR '渠道与商业%'`(`sql-map-report-config.xml:399-406`)。

### US-023: 查询项目质量评分
- **故事**: As a 管理员, I want 查询各办事处季度闭环项目平均质量得分(项目评分+工程评分+评价评分)及项目数量, so that 我能评估交付质量。
- **证据**: struts-sys.xml:1043-1047 (quality);ReportAction.java:464-535;sql-map-report-config.xml:105-137 (create_tmp_table_for_quality)、150-181 (query_total_quality)、182-215 (query_office_quality)、252-290 (query_reportline_quality_info)
- **说明**: 通过临时表 `quality` 计算;支持按项目类别、实施方式筛选(`sql-map-report-config.xml:155-180`)。

### US-024: 加载趋势折线图
- **故事**: As a 管理员, I want 按办事处与数据类型加载历史趋势折线图(月度), so that 我能观察各指标随时间的变化。
- **证据**: struts-sys.xml:1012-1016 (loadLineData);ReportAction.java:160-197;sql-map-report-config.xml:467-489 (query_line_data)
- **说明**: 数据来源 `pm_report_line_data`,按 `dataTypeCode`、`officeCode`(默认 `total`)、`effectiveTo IS NULL` 过滤。

### US-025: 加载闭环数量趋势
- **故事**: As a 管理员, I want 加载闭环项目数量月度趋势折线图, so that 我能观察交付趋势。
- **证据**: struts-sys.xml:1017-1021 (loadLine_qualityData);ReportAction.java:202-230;sql-map-report-config.xml:490-512 (query_line_quality_data)

### US-026: 加载实施方式占比趋势
- **故事**: As a 管理员, I want 加载企业网项目三种实施方式占比月度趋势, so that 我能观察交付模式演变。
- **证据**: struts-sys.xml:1022-1026 (loadLine_implData);ReportAction.java:235-268;sql-map-report-config.xml:586-603 (query_line_implway_data、query_report_impl_settingTime)

### US-027: 项目状态汇总报表
- **故事**: As a 项目管理员, I want 按办事处汇总项目实施状态、流程状态、可闭环项目数等维度, so that 我能精细掌握各办事处项目分布。
- **证据**: ReportAction.java:537-700 (projectSummaryStatus);sql-map-report-config.xml:604-663 (queryProjectSummaryStatus)
- **说明**: 汇总维度通过系统变量 `pm.report.project.summary.status` 配置(`ReportAction.java:600`);非管理员被重定向至本报表且仅显示该报表 tab(`ReportAction.java:553-566`);支持区域权限 `areaPowers`(`sql-map-report-config.xml:620-622`)。

### US-028: 回访数据统计分析
- **故事**: As a 管理员, I want 按公司、办事处、项目阶段、服务类型、项目类型筛选回访数据, so that 我能分析客户回访情况。
- **证据**: struts-sys.xml:409-411 (DataAnalysis.action);DataAnalysisAction.java:37-56

### US-029: 上传文件
- **故事**: As a 任意用户, I want 上传一个或多个附件并自动随机分目录存储, so that 业务单据可关联附件。
- **证据**: struts-sys.xml:511-515 (upload.action);UploadAction.java:49-64;sql-map-admin-config.xml:931-936 (insert_file_info)
- **说明**: 物理路径 `upload/file/{随机数}/`,数据库记录 `fileName/filePath/fileType/uploadBy/uploadTime`(`UploadAction.java:54-56`、`sql-map-admin-config.xml:931-936`)。

### US-030: AJAX 上传文件返回 ID
- **故事**: As a 任意用户, I want AJAX 上传文件后获得文件 ID 列表, so that 表单提交时能携带附件关联。
- **证据**: struts-sys.xml:1342-1346 (upload.action);UploadAction.java:49-64

### US-031: 删除文件
- **故事**: As a 任意用户, I want 按文件 ID 删除附件记录, so that 误传附件可清理。
- **证据**: struts-sys.xml:992-996 (deleteFile.action);UploadAction.java:69-80;sql-map-admin-config.xml:980-984 (delete_file)

### US-032: 下载附件
- **故事**: As a 任意用户, I want 按文件 ID 下载附件原文件, so that 我能查看附件内容。
- **证据**: struts-sys.xml:422-429 (download.action);UploadAction.java:86-95;sql-map-admin-config.xml:937-941 (query_flie_info)
- **说明**: 以 `application/octet-stream` 流式响应,文件名 ISO8859-1 编码(struts-sys.xml:424-425)。

### US-033: 查询文件列表
- **故事**: As a 任意用户, I want 按文件 ID 列表批量查询文件信息(含上传人), so that 表单回显时展示附件详情。
- **证据**: struts-sys.xml:1348-1352 (queryFile.action);UploadAction.java:97-106;sql-map-admin-config.xml:954-960 (query_file_list)

### US-034: 富文本编辑器图片上传
- **故事**: As a 任意用户, I want 在富文本编辑器中上传图片并获得可访问 URL, so that 编辑内容能引用图片。
- **证据**: UploadAction.java:141-159 (uploadImage)
- **说明**: 文件按 MD5 命名去重,物理路径 `upload/file/images/`,返回完整 URL 列表(`UploadAction.java:143-154`)。

### US-035: 新架构文件上传(按文件类型)
- **故事**: As a 任意用户, I want 按文件类型编码上传文件并自动应用文件类型配置(大小限制、允许类型、重命名、压缩、缩略图、保存目录), so that 不同业务场景的文件上传策略可配置。
- **证据**: UploaderController.java:205-218 (baseUpload);FileInfoMapper.xml:325-329 (selectFileTypeByCode)、331-340 (insertFileInfo)
- **说明**: 文件类型配置来自 `t_file_type`(`FileInfoMapper.xml:325-329`)。

### US-036: 新架构公开/私有文件下载
- **故事**: As a 任意用户/已登录用户, I want 通过公开或私有下载链接获取文件(单文件或 ZIP 打包), so that 附件能在站内外分享或受限访问。
- **证据**: UploaderController.java:258-288 (publicDownload、zipPublicDownload、privateDownload)

### US-037: 头像上传
- **故事**: As a 用户, I want 上传自定义头像(支持裁剪、原始图、多尺寸), so that 个人形象可个性化展示。
- **证据**: UploaderController.java:54-150 (avatarUpload)
- **说明**: 文件名按 MD5 命名,保存到 `upload/avatar/`,支持 `__source`、`__avatar1..N`、`__initParams` 字段(`UploaderController.java:103-145`)。

### US-038: 富文本编辑器图片上传(Summernote)
- **故事**: As a 用户, I want 在 Summernote 富文本编辑器中上传图片, so that 编辑内容能引用图片。
- **证据**: UploaderController.java:152-196 (summernoteUpload)

### US-039: 新架构文件列表查询
- **故事**: As a 任意用户, I want 按文件 ID 列表与类型 ID 查询文件列表(名称、上传人、上传时间), so that 表单回显附件表格。
- **证据**: UploaderController.java:226-250 (baseUpList);FileInfoMapper.xml:355-368 (selectFileInfoByIdsAndType)

### US-040: 邮件列表查询
- **故事**: As a 管理员, I want 分页查询邮件发送记录(主题、收件人、抄送、密送、内容、附件、状态、失败次数), so that 我能监控邮件发送情况。
- **证据**: MailInfoController.java:43-56 (support/mail);MailInfoMapper.xml:222-299 (selectBySelectivePageable)、300-368 (countBySelectivePageable)

### US-041: 邮件详情查询
- **故事**: As a 管理员, I want 查看单条邮件完整内容, so that 我能核对发送配置。
- **证据**: MailInfoController.java:58-69

### US-042: 新增邮件
- **故事**: As a 管理员, I want 手动创建待发送邮件(含期望发送时间), so that 定时任务能按计划发送。
- **证据**: MailInfoController.java:71-81 (create);MailInfoMapper.xml:47-149 (insertSelective)

### US-043: 修改邮件
- **故事**: As a 管理员, I want 修改待发送邮件内容, so that 发送前可调整。
- **证据**: MailInfoController.java:83-87 (update)

### US-044: 作废邮件
- **故事**: As a 管理员, I want 作废待发送邮件(清空 `expectSendTime`), so that 邮件不再被定时任务拾取。
- **证据**: MailInfoController.java:89-103 (invalid)
- **说明**: 仅清空期望发送时间,不删除记录(`MailInfoController.java:92-94`)。

### US-045: 手动发送邮件
- **故事**: As a 管理员, I want 立即发送一封待发邮件, so that 紧急邮件无需等待定时调度。
- **证据**: MailInfoController.java:105-125 (send)
- **说明**: 调用邮件发送工具发送后,更新发送状态(`MailInfoController.java:107-117`)。

### US-046: 定时发送待发邮件
- **故事**: As a 系统, I want 定时扫描 `sendFlag=false AND expectSendTime<now()` 的邮件并发送, so that 邮件按计划自动送达。
- **证据**: MailerJob.java:22-53;MailInfoMapper.xml:377-384 (queryUnSendMails)、415-430 (updateMailInfoWhenSendSuccess)、432-451 (updateMailInfoWhenSendFail)、453-494 (updateMailInfoWhenSend)
- **说明**: 失败次数超过系统变量 `sys.mail.sendFailed.maxCount`(默认 3)则跳过(`MailerJob.java:25-26`);成功/失败均通过 `ON DUPLICATE KEY UPDATE` 合并更新(`MailInfoMapper.xml:415-494`)。

### US-047: 通知模板 CRUD
- **故事**: As a 管理员, I want 维护通知模板(模板编码、主题、内容), so that 业务发送邮件/通知时复用模板。
- **证据**: NotifyTemplateController.java:40-138;NotifyTemplateMapper.xml:20-25,30-39,40-106,107-152,154-160 (selectByTemplateCode 含时间有效性过滤)、162-204、242-293
- **说明**: 内容经 Jsoup HTML 净化(relaxed 白名单 + 表格属性),非二次认证状态下转义、二次认证状态下反转义(`NotifyTemplateController.java:92-102,120-130`)。

### US-048: 按模板编码查询有效模板
- **故事**: As a 业务模块, I want 按模板编码查询当前有效的通知模板, so that 发送通知时获取最新模板内容。
- **证据**: NotifyTemplateMapper.xml:154-160 (selectByTemplateCode);MailInfoMapper.xml:370-375 (queryNotificationTemplate)
- **说明**: 有效条件 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())`(`NotifyTemplateMapper.xml:159`)。

### US-049: 系统变量 CRUD(老架构)
- **故事**: As a 管理员, I want 查询系统变量编码对应的值, so that 业务读配置驱动行为。
- **证据**: sql-map-admin-config.xml:862-867 (query_sys_arg)、869-875 (querySysArgList)
- **说明**: 有效条件 `effectiveFrom <= now() AND (effectiveTo IS NULL OR effectiveTo > now())`(`sql-map-admin-config.xml:864-866`)。

### US-050: 新架构系统变量 CRUD
- **故事**: As a 管理员, I want 在新后台分页查询、新增、修改、删除系统变量(编码、值、备注、生效时间), so that 系统行为可远程配置。
- **证据**: SystemVariableController.java:44-121;SystemVariableMapper.xml:20-25
- **说明**: 非二次认证场景下,值以 ASE 加密返回,更新时反向解密(`SystemVariableController.java:60-66,82-87,108-113`)。

### US-051: 系统变量二次认证
- **故事**: As a 管理员, I want 通过验证码二次认证后查看系统变量明文, so that 敏感配置(密码、密钥)受保护。
- **证据**: SystemVariableController.java:123-133 (secondaryCertification)
- **说明**: 校验会话验证码后置 `isSC=true`,后续读取不再加密(`SystemVariableController.java:127-131`)。

### US-052: 系统变量手动缓存刷新
- **故事**: As a 管理员, I want 手动触发系统缓存刷新(更新 `sys.cache.latest.refreshTime`), so that 配置变更立即生效。
- **证据**: sql-map-admin-config.xml:27-30 (refreshCacheData)、768 (basicCache flushOnExecute=refreshCacheData)

### US-053: 集群核心功能刷新
- **故事**: As a 管理员, I want 触发集群范围内刷新当前用户菜单、过滤链定义、系统变量, so that 配置变更在所有节点生效。
- **证据**: ClusterController.java:22-32 (refreshCore)
- **说明**: 仅 `ROLE_ADMIN` 可调用,否则返回无权限(`ClusterController.java:24-26`)。

### US-054: 通用数据导出(列选择)
- **故事**: As a 任意用户, I want 选择导出对象与列后导出 Excel, so that 业务数据可离线分析。
- **证据**: DataExportController.java:36-89;DataExportMapper.xml
- **说明**: 列定义从动态列查询 `queryDynamicColumn` + 静态列工具组合,排序依据 `queryDynamicColumnSort`(`DataExportController.java:45-50`)。

### US-055: 数据操作配置 CRUD(导入/导出脚本)
- **故事**: As a 管理员, I want 配置数据导入/导出操作(名称、类型、目标类、方法、参数类型、列、表单 HTML、SQL 脚本、权限), so that 业务用户可通过配置化界面执行导入导出。
- **证据**: DataOperationController.java:76-242;DataOperationMapper.xml:30-35,40-58
- **说明**: `type=0` 为导出(SQL 脚本),`type=1` 为导入(反射调用指定 Service 方法)(`DataOperationController.java:136,465-472`);权限通过 `empPower` 字段匹配当前用户 ID(`DataOperationController.java:766-784`)。

### US-056: 数据导入执行
- **故事**: As a 业务用户, I want 上传 Excel 文件执行配置好的导入操作, so that 批量数据可入库。
- **证据**: DataOperationController.java:249-268 (importForm)、280-349 (importOperation)、454-479 (operation)、555-619 (importOperation)
- **说明**: 仅允许 `.xlsx` 文件,通过反射调用配置的 Service 方法,返回错误列表 JSON(`DataOperationController.java:285-348`)。

### US-057: 数据导出执行(SQL 脚本)
- **故事**: As a 业务用户, I want 执行配置好的 SQL 导出操作并下载 Excel, so that 自定义查询结果可导出。
- **证据**: DataOperationController.java:358-393 (exportForm)、481-553 (exportOperation)、629-686 (exportPreview)
- **说明**: SQL 经 Jsoup 净化 + SQL 注入正则过滤 + 表白名单/黑名单校验,使用 SXSSF 流式 Excel 写出,进度写入会话(`DataOperationController.java:510-552,712-764`)。

### US-058: 数据导出列查询
- **故事**: As a 业务用户, I want 输入 SQL 后查询出可导出列, so that 我能勾选需要导出的字段。
- **证据**: DataOperationController.java:402-445 (queryExportColumns)

### US-059: 公司信息查询(老架构)
- **故事**: As a 业务模块, I want 按条件查询公司列表(名称、编码、状态、上级 ID), so that 项目可选择所属公司。
- **证据**: sql-map-admin-config.xml:1007-1018 (queryCompanyList)、1020-1032 (queryCompanyOne)

---

## 第2章 功能需求

> 按子域组织。每条含:触发条件/输入/处理规则/输出/异常。证据标注文件:行号。

### 2.1 基础数据/字典管理

#### FR-2.1.1 浏览基础数据列表(老架构)
- **触发**: 用户访问基础数据管理页(BasicdataManage.action)
- **输入**: `basicData.basicDataTypeCode`(可选,数据类型编码)
- **处理规则**:
  1. 先查所有有效基础数据类型(`status=1 AND effectiveFrom<NOW() AND (effectiveTo IS NULL OR effectiveTo>NOW())`),按 `dataTypeCode` 返回(`sql-map-admin-config.xml:825-829`)。
  2. 若指定数据类型编码,联查 `fnd_basic_data` 与 `fnd_basic_data_type`,按 `dataTypeCode,sortId` 排序(`sql-map-admin-config.xml:812-820`)。
- **输出**: 基础数据类型列表 + 指定类型下的字典项列表
- **异常**: 无
- **证据**: BasicDataManageAction.java:29-37;sql-map-admin-config.xml:812-829

#### FR-2.1.2 新增基础数据(老架构)
- **触发**: 用户提交新增表单(BasicdataInsert.action)
- **输入**: `basicData`(`dataTypeCode`、`basicDataId`、`basicDataName`、`sortId`、`createTime`、`createBy`、`effectiveFrom`、`effectiveTo`)
- **处理规则**: INSERT 到 `fnd_basic_data`(`sql-map-admin-config.xml:851-856`);触发 `basicCache` 失效(`sql-map-admin-config.xml:766`)。
- **输出**: 重定向回列表页,带 `basicData.basicDataTypeCode` 参数
- **异常**: 无显式异常,失败抛出由全局处理
- **证据**: BasicDataManageAction.java:51-59;sql-map-admin-config.xml:851-856

#### FR-2.1.3 修改基础数据(老架构)
- **触发**: 用户提交修改表单(BasicdataUpdate.action)
- **输入**: `basicData.id`、`basicData.basicDataName`、`sortId`、`effectiveTo`、`basicDataTypeCode`
- **处理规则**:
  1. 若 `id!=0` 且 `basicDataId==null`,视为打开修改页,回查单条记录(`sql-map-admin-config.xml:830-834`)。
  2. 否则执行 UPDATE,仅更新 `basicDataName`、可选 `sortId`、`effectiveTo`(`sql-map-admin-config.xml:835-850`),并触发缓存刷新(`BasicDataManageAction.java:47` `SystemContext.refresh()`)。
- **输出**: 重定向回列表页
- **证据**: BasicDataManageAction.java:39-49;sql-map-admin-config.xml:830-850

#### FR-2.1.4 基础数据编码唯一性校验
- **触发**: 用户录入 `basicDataId` 时 AJAX 校验
- **输入**: `dataTypeCode`、`basicDataId`
- **处理规则**: 统计 `fnd_basic_data` 中同 `dataTypeCode` 与 `basicDataId` 的记录数(`sql-map-admin-config.xml:857-861`)
- **输出**: `result`(int,匹配数)
- **证据**: BasicDataManageAction.java:64-70;sql-map-admin-config.xml:857-861

#### FR-2.1.5 执行维护 SQL(受限)
- **触发**: 管理员在维护界面提交 `executeSql`
- **输入**: `executeSql`(SQL 字符串)
- **处理规则**:
  1. 转小写后判断是否含 `where` 或 `insert` 关键字(`BasicDataManageAction.java:75-83`)。
  2. 含 `where` → 执行并返回"执行更新或删除成功";含 `insert` → 执行并返回"执行插入成功";否则拒绝"执行的 SQL 没有 where 条件,不允许执行"。
  3. SQL 通过字符串拼接到 `<![CDATA[ $executeSql$ ]]>` 执行(`sql-map-admin-config.xml:919-923`)。
- **输出**: `msg`(执行结果文本)
- **异常**: 异常信息拼接到 `msg`(`BasicDataManageAction.java:86-88`)
- **风险**: SQL 注入风险极高 `[待澄清]` 该功能是否仍在生产使用
- **证据**: BasicDataManageAction.java:72-90;sql-map-admin-config.xml:919-923

#### FR-2.1.6 新架构数据字典 CRUD
- **触发**: 用户访问 `/system/dictionary` 系列端点
- **输入**: `id`、`dicTypeId`、`dicTypeName`、`dicKey`、`dicValue`、`custominfo`、`sort`、`status`
- **处理规则**: 标准 CRUD,新增/修改/删除按主键 `id` 操作;列表支持模糊查询(`dic_type_name`、`dic_key`、`dic_value`、`customInfo` 模糊匹配)、排序、分页(`DictionaryMapper.xml:146-196,199-245`)
- **输出**: 字典列表/单条详情
- **异常**: 无显式
- **证据**: DictionaryController.java:34-89;DictionaryMapper.xml

#### FR-2.1.7 查询字典类型最大 ID
- **触发**: 新增字典类型时分配类型 ID
- **处理规则**: `SELECT max(dic_type_id) FROM t_dictionary`(`DictionaryMapper.xml:256-258`)
- **证据**: DictionaryMapper.xml:256-258

### 2.2 操作日志

#### FR-2.2.1 查询操作日志(老架构)
- **触发**: 用户访问日志管理页(LogManage.action)
- **输入**: `displayParam`(`sort`、`order`、`offset`、`pagesize`)
- **处理规则**: 联查 `tb_sys_log` 与 `user` 表(用户名关联)获取 `realName`;按 `sort/order` 排序,`offset,pagesize` 分页(`sql-map-admin-config.xml:567-584`)
- **输出**: 日志列表
- **证据**: OperateLogAction.java:48-52;sql-map-admin-config.xml:559-591

#### FR-2.2.2 写入操作日志(老架构)
- **触发**: 登录等关键操作
- **输入**: `username`、`ip`、`info`、`time`
- **处理规则**: INSERT 到 `tb_sys_log(USER_NAME, IP, INFO, TIME)`(`sql-map-admin-config.xml:541-546`)
- **证据**: sql-map-admin-config.xml:541-546

#### FR-2.2.3 导出操作日志为 Excel
- **触发**: 用户点击导出(ExportLogAll.action)
- **输入**: `displayParam`(查询条件)
- **处理规则**:
  1. 读取模板 `template/日志.xlsx`(`OperateLogAction.java:64-69`)。
  2. 查询全部日志(`sql-map-admin-config.xml:559-565`),逐行写入 Excel 第 0-4 列(username、realName、ip、time、info)。
  3. 输出到 `upload/payment/日志.xlsx`(`OperateLogAction.java:101-113`),通过流响应下载(struts-sys.xml:963-971)。
- **输出**: Excel 文件流
- **异常**: `FileNotFoundException`/`IOException` 返回 ERROR 并写入 `errmsg`(`OperateLogAction.java:117-125`)
- **证据**: OperateLogAction.java:63-129

#### FR-2.2.4 新架构系统日志查询
- **触发**: 用户访问 `/system/syslog/list`
- **输入**: `description`、`createBy`、`requestIp`、`type`(均模糊或精确)
- **处理规则**: 按 `id desc` 默认排序,支持 `description`/`create_by`/`request_ip` 模糊查询、`type` 精确匹配(`SysLogMapper.xml:154-191`)
- **输出**: 日志列表
- **证据**: SysLogController.java:47-78;SysLogMapper.xml:154-191

#### FR-2.2.5 新架构同步日志查询
- **触发**: 用户访问 `/system/synclog/list`
- **输入**: `targetMethod`、`tableObject`、`dataFrom`、`dataTo`、`syncParams`、`syncStartTime`、`syncEndTime`、`isSuccess`、`dataCount`、`syncType`、`exception`
- **处理规则**: 支持上述字段精确过滤 + 模糊搜索(`targetMethod`/`tableObject`/`dataFrom`/`dataTo` 模糊)(`SyncLogMapper.xml:200-262`)
- **输出**: 同步日志列表
- **证据**: SyncLogController.java:47-76;SyncLogMapper.xml:200-262

### 2.3 合格证管理

#### FR-2.3.1 合格证查询主页
- **触发**: 用户访问合格证模块(certificate.action)
- **处理规则**: 根据当前用户是否拥有 `roleId=1` 设置 `canUpload` 标志(`CertificateAction.java:39`)
- **输出**: `canUpload` 标志
- **证据**: CertificateAction.java:37-41

#### FR-2.3.2 根据条码查询 OQC 合格证
- **触发**: 用户输入 `barcode` 提交查询
- **输入**: `barcode`(设备序列号)
- **处理规则**:
  1. 查询 `mes_oqc_info` 关联 `mes_seal_info`(检验员 = 印章 `user`,且 `inspectTime` 在 `takeTime` 与 `backTime` 之间或 `backTime IS NULL` 且 `inspectTime >= takeTime`),且 `info like 'QC PASS%'`(`sql-map-certificate-config.xml:11-30`)。
  2. 取首条结果的 `info`,正则提取数字作为 `oqcNo`(`CertificateAction.java:56-61`)。
  3. 根据 barcode 第 10-12 位(16 进制月)生成 `productionDate`(`CertificateAction.java:83-99`)。
- **输出**: `oqcNo`、`productionDate`
- **异常**: 未找到 → `errmsg="没有找到[barcode]对应的OQC检验信息!"`(`CertificateAction.java:70`);barcode 为空 → `errmsg="请输入设备序列号!"`(`CertificateAction.java:72`)
- **证据**: CertificateAction.java:48-75;sql-map-certificate-config.xml:11-30

#### FR-2.3.3 批量上传印章信息
- **触发**: 管理员上传 Excel 文件(uploadSealInfo.action)
- **输入**: Excel 文件(印章 ID、名称、info、描述、领用时间、归还时间、备注、上传人)
- **处理规则**:
  1. 解析 Excel 文件(`CertificateAction.java:103`)。
  2. 先清空 `mes_seal_info`(`sql-map-certificate-config.xml:55-57` truncateSealInfo)。
  3. 批量 INSERT(`sql-map-certificate-config.xml:49-54` insertSealInfo)。
- **输出**: 重定向回主页
- **异常**: 解析异常 → `errmsg` + ERROR 页(`CertificateAction.java:104-107`)
- **证据**: CertificateAction.java:101-109;sql-map-certificate-config.xml:39-57

### 2.4 报表统计与数据分析

#### FR-2.4.1 报表首页
- **触发**: 用户访问报表首页(report_show)
- **处理规则**:
  1. 查询选项卡(基础数据 `28`)、办事处列表(含"全国")(`ReportAction.java:116-123`)。
  2. 查询全国统计综述(`ReportAction.java:125`)。
  3. 查询指派率、跟踪率、闭环比表格数据并组装 HTML(`ReportAction.java:127-137`)。
- **输出**: 选项卡、办事处列表、综述、各表格 HTML
- **权限**: 非管理员/工程经理/工程经理组长/财务/项目管理员/回访员角色被重定向至项目状态汇总页(`ReportAction.java:98-106`)
- **证据**: ReportAction.java:88-145

#### FR-2.4.2 项目指派率
- **触发**: AJAX 调用 assignedRate
- **输入**: `queryParam`(`quarterStartTime` 等)
- **处理规则**: 查询各办事处已指派项目经理项目数 / 总项目数,并聚合"全国"行(`ReportAction.java:294-307`);柱状图参数从基础数据类型 `REPORT_ASSIGNED_RATE` 取标题与属性(`ReportAction.java:296`)
- **输出**: ECharts 柱状图 JSON
- **证据**: ReportAction.java:291-313;sql-map-report-config.xml:36-53

#### FR-2.4.3 项目跟踪率
- **触发**: AJAX 调用 traceRate
- **处理规则**: 排除工程计划状态=40 的项目后计算跟踪率(`sql-map-report-config.xml:81-103`)
- **输出**: ECharts 柱状图 JSON
- **证据**: ReportAction.java:322-344;sql-map-report-config.xml:81-103

#### FR-2.4.4 季度闭环新增比
- **触发**: AJAX 调用 closeRate
- **处理规则**: 闭环项目(`projectState=100`)/ 新增项目(`createTime>季度开始 AND projectState>20`),按办事处分组(`sql-map-report-config.xml:354-361,381-397`)
- **输出**: ECharts 柱状图 JSON
- **证据**: ReportAction.java:349-386;sql-map-report-config.xml:354-397

#### FR-2.4.5 企业网项目实施方式占比
- **触发**: AJAX 调用 implRate
- **处理规则**:
  1. 创建临时表 `implway`,实施方式取 `column012` 或 `columno12_readonly`,限定 `column004 LIKE '企业网%' OR '渠道与商业%'`(`sql-map-report-config.xml:399-407`)。
  2. 按办事处与实施方式分组统计占比(`sql-map-report-config.xml:434-465`)。
- **输出**: ECharts 多系列柱状图 JSON(原厂直服/原厂督导/代理商自服)
- **证据**: ReportAction.java:392-457;sql-map-report-config.xml:399-465

#### FR-2.4.6 项目质量评分
- **触发**: AJAX 调用 quality
- **处理规则**:
  1. 创建临时表 `quality`,关联客户评价(`pm_cl_evaluation_header` `evaluationType=3 AND status=1`)、问卷结果、项目评价(`pm_cl_evaluation_header` 关联 `pm_cl_quesnaire_result_header` `quesnaireTemplateHeaderId=2`),限定 `projectState=100`(`sql-map-report-config.xml:105-137`)。
  2. 查询全国与各办事处平均得分、项目数,支持按项目类别(`column011`)与实施方式(`column012`/`columno12_readonly`)筛选(`sql-map-report-config.xml:150-326`)。
  3. 同时输出闭环平均得分柱状图、闭环项目数量柱状图、质量表格 HTML(`ReportAction.java:514-530`)。
- **输出**: `data`(平均分图)、`dataJson`(项目数图)、`qualityTableHtml`(表格)
- **证据**: ReportAction.java:464-535;sql-map-report-config.xml:105-326

#### FR-2.4.7 趋势折线图(各率)
- **触发**: AJAX 调用 loadLineData
- **输入**: `officeCode`、`dataTypeCode`
- **处理规则**: 从 `pm_report_line_data` 查询指定办事处与数据类型的 `specificValue` 与 `settingTime`,按月格式化、按时间排序,组装 ECharts 折线图,动态设置 Y 轴最大值(`ReportAction.java:160-197`)
- **输出**: ECharts 折线图 JSON
- **证据**: ReportAction.java:160-197;sql-map-report-config.xml:467-489

#### FR-2.4.8 闭环数量趋势折线图
- **触发**: AJAX 调用 loadLine_qualityData
- **处理规则**: 查询 `pm_report_line_data` 的 `totalValue` 按月聚合(`sql-map-report-config.xml:490-512`)
- **输出**: ECharts 折线图 JSON
- **证据**: ReportAction.java:202-230;sql-map-report-config.xml:490-512

#### FR-2.4.9 实施方式占比趋势折线图
- **触发**: AJAX 调用 loadLine_implData
- **处理规则**: 查询 `impl*` 前缀的 `dataTypeCode` 数据,按月分组,组装 3 条折线(原厂直服/原厂督导/代理商自服)(`ReportAction.java:235-268`、`sql-map-report-config.xml:586-603`)
- **输出**: ECharts 多系列折线图 JSON(百分比)
- **证据**: ReportAction.java:235-268;sql-map-report-config.xml:586-603

#### FR-2.4.10 项目状态汇总报表
- **触发**: 用户访问 report_projectSummaryStatus(或被重定向)
- **输入**: `dataJson`(查询条件 JSON)、`data`(值为 `info` 时返回明细)
- **处理规则**:
  1. 默认注入 `projectState=30,31,32`、`shipmentState=-1`(`ReportAction.java:549-551`)。
  2. 非管理员注入区域权限 `areaPowers`(`ReportAction.java:559`)。
  3. 调用 `queryProjectSummaryStatus` SQL 联查 `pm_project_header` + `pm_project_state` + `fnd_department` + `pm_project_contract`(`sql-map-report-config.xml:604-663`)。
  4. 按系统变量 `pm.report.project.summary.status` 配置的维度(实施状态、流程状态、可闭环项目数等)汇总(`ReportAction.java:600-622`)。
  5. 服务端渲染带链接的 HTML 表格,`count>0` 的单元格可下钻查询(`ReportAction.java:670-679`)。
- **输出**: 汇总表格 HTML 或明细列表
- **权限**: 非管理员仅见本报表 tab 且仅见 `projectSummaryStatus` 维度(`ReportAction.java:560-566`)
- **证据**: ReportAction.java:537-700;sql-map-report-config.xml:604-663

#### FR-2.4.11 报表趋势数据持久化
- **触发**: 定时任务批量保存趋势图数据
- **处理规则**: 批量 INSERT `pm_report_line_data(dataTypeCode, officeCode, conditionValue, totalValue, specificValue, settingTime, createTime, effectiveFrom)`(`sql-map-report-config.xml:555-580`)
- **证据**: sql-map-report-config.xml:555-580

#### FR-2.4.12 回访数据统计分析
- **触发**: 用户访问 DataAnalysis.action
- **输入**: `dataQueryParam`(查询条件)
- **处理规则**: 加载公司、办事处、项目阶段、服务类型、项目类型、选项卡基础数据;查询回访数据列表(`DataAnalysisAction.java:37-56`)
- **输出**: 各下拉列表 + 回访数据列表
- **异常**: 数据查询返回 -1 时返回 ERROR(`DataAnalysisAction.java:52-54`)
- **证据**: DataAnalysisAction.java:37-56

### 2.5 文件管理

#### FR-2.5.1 文件上传(老架构)
- **触发**: 用户提交上传表单(upload.action)
- **输入**: `upload`(File[])、`uploadFileName`、`uploadFileType`
- **处理规则**:
  1. 物理存储路径 `upload/file/{随机数}/`(`UploadAction.java:54`)。
  2. 上传后 INSERT `fnd_files(fileName, filePath, fileType, uploadBy, uploadTime)`,返回自增 ID(`sql-map-admin-config.xml:931-936`)。
- **输出**: `fileIds`(逗号分隔 ID)
- **异常**: 异常打印堆栈但返回 SUCCESS(`UploadAction.java:60-63`)
- **证据**: UploadAction.java:49-64;sql-map-admin-config.xml:931-936

#### FR-2.5.2 文件删除(老架构)
- **触发**: AJAX 调用 deleteFile.action
- **输入**: `fileId`
- **处理规则**: DELETE FROM `fnd_files WHERE id=fileId`(`sql-map-admin-config.xml:980-984`)
- **输出**: `message`("删除成功!" / "删除失败!")
- **证据**: UploadAction.java:69-80;sql-map-admin-config.xml:980-984

#### FR-2.5.3 文件下载(老架构)
- **触发**: 用户点击下载链接(download.action)
- **输入**: `fileId`
- **处理规则**: 查询 `fnd_files` 获取 `fileName`/`filePath`(`sql-map-admin-config.xml:937-941`),以 `application/octet-stream;charset=ISO8859-1` 流式响应,文件名 ISO8859-1 编码(struts-sys.xml:424-425)
- **输出**: 文件流
- **证据**: UploadAction.java:86-95;struts-sys.xml:422-429

#### FR-2.5.4 文件列表查询(老架构)
- **触发**: AJAX 调用 queryFile.action
- **输入**: `fileIds`(逗号分隔)
- **处理规则**: 联查 `fnd_files` 与 `fnd_user_info` 获取上传人姓名(`sql-map-admin-config.xml:954-960`)
- **输出**: `fileList`(文件信息列表)、`fileIds`、`message`
- **证据**: UploadAction.java:97-106;sql-map-admin-config.xml:954-960

#### FR-2.5.5 富文本图片上传(老架构)
- **触发**: 富文本编辑器上传图片
- **处理规则**: 物理路径 `upload/file/images/`,文件按 MD5 命名去重,返回完整 URL 列表(分号分隔)(`UploadAction.java:141-159`)
- **输出**: `message`(URL 列表)
- **证据**: UploadAction.java:141-159

#### FR-2.5.6 新架构文件上传(按文件类型)
- **触发**: POST `/file/baseUpload/{fileType}`
- **输入**: `fileType`(文件类型编码)、HTTP 多部分请求
- **处理规则**: 按文件类型编码查 `t_file_type` 获取大小/类型/重命名/压缩/缩略图/目录配置(`FileInfoMapper.xml:325-329`),按配置处理上传后 INSERT `t_file(typeId, name, path, ext, size, dataType, dataId, createBy)`(`FileInfoMapper.xml:331-340`)
- **输出**: `Result` 含文件信息列表
- **异常**: 异常 → `Result(false)` + 错误信息(`UploaderController.java:211-217`)
- **证据**: UploaderController.java:205-218;FileInfoMapper.xml:325-340

#### FR-2.5.7 新架构文件下载(公开/私有)
- **触发**: GET `/file/down/public/{fileId}`、`/file/down/private/{fileId}`、`/file/zipdown/public/{fileIds}`
- **处理规则**: 单文件直接响应流;多文件 ZIP 打包下载(`UploaderController.java:258-288`)
- **输出**: 文件流或 ZIP 流
- **证据**: UploaderController.java:258-288

#### FR-2.5.8 新架构文件列表查询
- **触发**: GET `/file/baseUpload/list`
- **输入**: `fileInfo.typeId`、`fileIds`
- **处理规则**: 按 ID 列表与类型 ID 查询 `t_file`(`FileInfoMapper.xml:355-368`),组装列定义(文件名/上传人/上传时间)
- **输出**: 文件列表 + 列定义
- **证据**: UploaderController.java:226-250;FileInfoMapper.xml:355-368

#### FR-2.5.9 头像上传
- **触发**: POST `/file/avatarUpload`
- **输入**: `userId`、多部分请求(含 `__source`、`__avatar1..N`、`__initParams`)
- **处理规则**: 文件名 MD5+.png,保存到 `upload/avatar/`,支持原始图与多尺寸(`UploaderController.java:78-145`)
- **输出**: `AvatarResult`(success、avatarUrls、sourceUrl、msg)
- **证据**: UploaderController.java:54-150

#### FR-2.5.10 Summernote 富文本图片上传
- **触发**: POST `/file/summernoteUpload`
- **处理规则**: 文件名 MD5+.png,保存到 `upload/summernote/`,返回完整 URL 列表(分号分隔)(`UploaderController.java:152-196`)
- **输出**: URL 字符串
- **证据**: UploaderController.java:152-196

#### FR-2.5.11 下载日志记录
- **触发**: 文件下载时
- **处理规则**: INSERT `t_down_log(fileIds, ip, timeline, downloadTime, user)`(`FileInfoMapper.xml:370-375`)
- **证据**: FileInfoMapper.xml:370-375

### 2.6 邮件通知与通知模板

#### FR-2.6.1 邮件列表查询
- **触发**: 用户访问 `/system/mailInfo/list`
- **输入**: `MailInfo`(过滤条件)、分页参数
- **处理规则**: 支持 `id`、`subject`、`isInner`、`sendTime`、`expectSendTime`、`sendFlag`、`createBy`、`createTime`、`content`、`tos`、`ccs`、`bccs`、`actualSendAddress`、`attachFiles`、`failedCount`、`failedMessage` 精确过滤 + 模糊搜索(`MailInfoMapper.xml:222-299`)
- **输出**: 邮件列表
- **证据**: MailInfoController.java:43-56;MailInfoMapper.xml:222-368

#### FR-2.6.2 邮件详情查询
- **触发**: GET/POST `/system/mailInfo/{id}`
- **处理规则**: 按主键查询 `t_mails`(`MailInfoMapper.xml:25-30`)
- **输出**: 单条邮件
- **证据**: MailInfoController.java:58-69

#### FR-2.6.3 新增邮件
- **触发**: POST `/system/mailInfo/detail`
- **输入**: `MailInfo`(`subject`、`content`、`tos`、`ccs`、`bccs`、`attachFiles`、`expectSendTime`、`isInner` 等)
- **处理规则**: 设置 `createTime=now()` 后 INSERT(`MailInfoController.java:77-80`;`MailInfoMapper.xml:47-149`)
- **输出**: 重定向到详情页
- **证据**: MailInfoController.java:76-81

#### FR-2.6.4 修改邮件
- **触发**: PUT `/system/mailInfo/{id}`
- **处理规则**: 按主键选择性 UPDATE(`MailInfoMapper.xml:150-200`)
- **证据**: MailInfoController.java:83-87

#### FR-2.6.5 作废邮件
- **触发**: POST `/system/mailInfo/invalid`
- **输入**: `id`
- **处理规则**: 查询邮件 → 清空 `expectSendTime` → UPDATE(`MailInfoController.java:91-94`)
- **输出**: `status`(true/false)、`message`
- **异常**: 异常 → `status=false` + 错误信息(`MailInfoController.java:95-102`)
- **证据**: MailInfoController.java:89-103

#### FR-2.6.6 手动发送邮件
- **触发**: POST `/system/mailInfo/send`
- **输入**: `id`
- **处理规则**: 查询邮件 → 复制属性到 `MailSenderInfo` → 调用 `MailUtil.sendMailWithAttachments` → 更新发送状态(`MailInfoController.java:107-117`)
- **输出**: `status`(发送成功标志)、`message`
- **异常**: 异常记录到 `ExceptionHandler` 并返回错误 ID(`MailInfoController.java:120-123`)
- **证据**: MailInfoController.java:105-125

#### FR-2.6.7 定时发送待发邮件
- **触发**: 定时任务(MailerJob)
- **处理规则**:
  1. 查询 `sendFlag=false AND expectSendTime<now() AND failedCount<sys.mail.sendFailed.maxCount(默认3)` 的邮件(`MailInfoMapper.xml:377-384`)
  2. 逐封调用 `MailUtil.sendMailWithAttachments` 发送(`MailerJob.java:29-37`)
  3. 通过 `ON DUPLICATE KEY UPDATE` 批量更新发送结果:成功→`sendFlag=true, sendTime=now()`,失败→`failedCount+1, failedMessage` 追加(`MailInfoMapper.xml:415-494`)
- **输出**: 无(异步更新数据库)
- **证据**: MailerJob.java:22-53;MailInfoMapper.xml:377-494

#### FR-2.6.8 通知模板 CRUD
- **触发**: 用户访问 `/system/notifyTemplate` 系列端点
- **输入**: `templateCode`、`subject`、`content`、`effectiveFrom`、`effectiveTo`
- **处理规则**: 标准 CRUD;列表支持模糊搜索(`templateCode`/`subject`/`content`)(`NotifyTemplateMapper.xml:167-293`);新增/修改时对 `content` 进行 Jsoup HTML 净化(relaxed 白名单 + 表格属性 + 相对链接保留),非二次认证状态净化、二次认证状态反转义(`NotifyTemplateController.java:92-102,120-130`)
- **输出**: 模板列表/详情
- **证据**: NotifyTemplateController.java:40-138;NotifyTemplateMapper.xml

#### FR-2.6.9 按编码查询有效通知模板
- **触发**: 业务模块按 `templateCode` 查询模板
- **处理规则**: `SELECT * FROM t_notify_template WHERE templateCode=? AND effectiveFrom<now() AND (effectiveTo IS NULL OR effectiveTo>now())`(`NotifyTemplateMapper.xml:154-160`)
- **输出**: 单条模板
- **证据**: NotifyTemplateMapper.xml:154-160;MailInfoMapper.xml:370-375

### 2.7 系统变量

#### FR-2.7.1 查询系统变量(老架构)
- **触发**: 业务模块按 `code` 读取配置
- **处理规则**: `SELECT var FROM fnd_sys_arg WHERE code=? AND effectiveFrom<=now() AND (effectiveTo IS NULL OR effectiveTo>now())`(`sql-map-admin-config.xml:862-867`)
- **输出**: 变量值字符串
- **证据**: sql-map-admin-config.xml:862-875

#### FR-2.7.2 新架构系统变量 CRUD
- **触发**: 用户访问 `/system/sysVariable` 系列端点
- **输入**: `code`、`var`、`remark`、`effectiveFrom`、`effectiveTo`
- **处理规则**: 标准 CRUD;列表查询时若会话非二次认证(`isSC!=true`),`var` 字段以 ASE 加密返回(`SystemVariableController.java:60-66`);详情查询同理(`SystemVariableController.java:84-87`);更新时若非二次认证则先解密再保存(`SystemVariableController.java:108-113`)
- **输出**: 系统变量列表/详情
- **证据**: SystemVariableController.java:44-121;SystemVariableMapper.xml

#### FR-2.7.3 系统变量二次认证
- **触发**: POST `/system/sysVariable/secondaryCertification`
- **输入**: `cert`(用户输入验证码)
- **处理规则**: 校验 `cert` 与会话中的 `captcha` 是否相等(忽略大小写);相等则会话置 `isSC=true`,否则移除 `isSC`(`SystemVariableController.java:127-131`)
- **输出**: 无(仅会话状态变更)
- **证据**: SystemVariableController.java:123-133

#### FR-2.7.4 手动缓存刷新
- **触发**: 管理员手动刷新
- **处理规则**: `UPDATE fnd_sys_arg SET var=now() WHERE code='sys.cache.latest.refreshTime'`(`sql-map-admin-config.xml:27-30`);触发 `basicCache`/`userCache` 失效(`sql-map-admin-config.xml:22,768`)
- **证据**: sql-map-admin-config.xml:27-30,768

### 2.8 集群管理

#### FR-2.8.1 集群核心功能刷新
- **触发**: POST `/cluster/refreshCore`
- **输入**: 无
- **处理规则**:
  1. 校验当前用户是否拥有 `ROLE_ADMIN`,否则返回"没有权限访问该功能!"(`ClusterController.java:24-26`)
  2. 依次调用 `updateActiveUserMenu(null)`(刷新当前用户菜单)、`updateFilterChainDefinitionMap()`(刷新过滤链定义)、`updateSystemVariables(null)`(刷新系统变量)(`ClusterController.java:27-29`)
- **输出**: `Result`(success=true/"刷新成功!" 或 false/"没有权限...")
- **证据**: ClusterController.java:22-32

### 2.9 数据导出与数据操作

#### FR-2.9.1 通用数据导出列展示
- **触发**: GET `/export/showExportColumns`
- **输入**: `objectName`、`objectKV`、`pageParamKV`、`fullServiceName`
- **处理规则**: 查询 `t_data_export` 动态列配置 + 静态列工具,按 `queryDynamicColumnSort` 排序(`DataExportController.java:43-50`)
- **输出**: 列定义页面
- **证据**: DataExportController.java:36-53

#### FR-2.9.2 通用数据导出执行
- **触发**: POST `/export/dataExport`
- **输入**: `objectName`、`objectKV`、`pageParamKV`、`columns`、`fullServiceName`
- **处理规则**:
  1. 反射调用 `fullServiceName` 的 `export{ObjectName}` 方法获取数据(`DataExportController.java:71-75`)
  2. 失败时回退调用 `dataExportService.export{objectName}`(`DataExportController.java:76-79`)
  3. 含动态列(`DataExportController.java:86-87`)
- **输出**: 导出视图(含数据与列)
- **证据**: DataExportController.java:64-89

#### FR-2.9.3 数据操作配置 CRUD
- **触发**: 用户访问 `/data` 系列端点
- **输入**: `DataOperation`(`name`、`description`、`type`(0=导出/1=导入)、`clazz`、`method`、`parameterTypes`、`columns`、`empPower`、`depPower`、`state`、`effectiveFrom`、`effectiveTo`、`formHtml`、`script`)
- **处理规则**: 标准 CRUD;`type=0` 时 SQL 经 `checkSql` 校验(SQL 注入正则 + 表白名单/黑名单 + Jsoup 净化)(`DataOperationController.java:165-173,219-227,712-764`);`formHtml` 经表单白名单净化(`DataOperationController.java:175,229`);权限校验 `empPower` 包含当前用户 ID(`DataOperationController.java:766-784`)
- **输出**: 配置列表/详情
- **证据**: DataOperationController.java:76-242;DataOperationMapper.xml

#### FR-2.9.4 数据导入执行
- **触发**: POST `/data/import/{operationName}`
- **输入**: `operationName`、`fileExcel`(MultipartFile)
- **处理规则**:
  1. 校验文件扩展名 `.xlsx`(`DataOperationController.java:288`)
  2. 按配置的 `clazz`、`method`、`parameterTypes` 反射调用 Service 方法,首参为 `MultipartFile`,可选第二参 `request.getParameterMap()`(`DataOperationController.java:294-313`)
  3. 返回错误列表 JSON(去除 stackTrace 等字段)(`DataOperationController.java:314-327`)
- **输出**: JSON `{errorMessage, progress}`
- **异常**: 文件格式错误/为空 → `errorMessage`(`DataOperationController.java:341-346`)
- **证据**: DataOperationController.java:280-349,555-619

#### FR-2.9.5 数据导出执行(SQL 脚本)
- **触发**: POST `/data/operation/{id}` 且 `type=0`
- **输入**: `id`、`objectName`、`objectKV`、`pageParamKV`、`columns`
- **处理规则**:
  1. 读取 `script`,经 `checkSql(sql, true)` 校验并填充用户参数(`DataOperationController.java:510-514,720-764`)
  2. 以 `rowAccessWindowSize=100` 使用 SXSSF 流式写出 Excel(`DataOperationController.java:522-542`)
  3. 进度写入会话属性 `operationName`(`DataExportController.java:489,535,543`)
- **输出**: Excel 流
- **异常**: 异常记录到 `ExceptionHandler` 并抛出含错误 ID 的 RuntimeException(`DataOperationController.java:545-547`)
- **证据**: DataOperationController.java:481-553

#### FR-2.9.6 数据导出预览
- **触发**: GET `/data/export/preview/{id}`
- **处理规则**: 与导出执行相同校验逻辑,但不写出 Excel,返回预览数据(`DataOperationController.java:629-686`)
- **输出**: 预览数据列表
- **证据**: DataOperationController.java:629-686

#### FR-2.9.7 数据导出列查询(SQL)
- **触发**: GET `/data/export/queryExportColumns`
- **输入**: `sql`
- **处理规则**: `checkSql` 校验后查询 SQL 元数据获取列名(`DataOperationController.java:430-441`)
- **输出**: 列名列表
- **证据**: DataOperationController.java:402-445

---

## 第3章 数据契约【最关键】

> 列出该域所有相关表。区分:
> - **本域专属表**:仅在本域业务中使用
> - **与 001 域共享表**:表结构在 `sql-map-admin-config.xml` 中,但业务归属本域(基础数据、文件、邮件、系统变量、操作日志)
> - **跨域引用表**:本域查询但不维护(如 `pm_project_header`、`fnd_department`、`fnd_user_info`)
>
> 字段分级:**C**=契约字段(对外稳定)、**I**=内部字段(实现细节)、**D**=废弃字段

### 表 fnd_basic_data(基础数据/字典项)【与 001 共享,业务归属本域】

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

- **证据**: sql-map-admin-config.xml:774-784(resultMap)、851-856(insert)、835-850(update)、857-861(唯一性校验)
- **业务规则**: 查询均按 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())` 过滤;缓存模型 `basicCache` 在 insert/update/refreshCacheData 时失效(`sql-map-admin-config.xml:763-771`)

### 表 fnd_basic_data_type(基础数据类型)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| dataTypeCode | VARCHAR | 否 | 类型编码,主键 | 唯一 | C |
| dataTypeName | VARCHAR | 否 | 类型名称 | — | C |
| status | INT | 是 | 状态(1=有效) | `=1` 时才被查询(`sql-map-admin-config.xml:827`) | C |
| effectiveFrom | DATETIME | 是 | 生效起始时间 | — | C |
| effectiveTo | DATETIME | 是 | 失效时间 | — | C |

- **证据**: sql-map-admin-config.xml:821-829
- **业务规则**: 仅 `status=1 AND effectiveFrom<NOW() AND (effectiveTo IS NULL OR effectiveTo>NOW())` 的类型被查询(`sql-map-admin-config.xml:825-829`)

### 表 fnd_sys_arg(系统变量)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| code | VARCHAR | 否 | 变量编码,主键 | 唯一 | C |
| var | VARCHAR | 是 | 变量值(可为时间字符串如 `sys.cache.latest.refreshTime`) | — | C |
| effectiveFrom | DATETIME | 是 | 生效起始时间 | `<= now()` 时才被查询 | C |
| effectiveTo | DATETIME | 是 | 失效时间 | — | C |

- **证据**: sql-map-admin-config.xml:27-30(refreshCacheData)、862-875(query_sys_arg/querySysArgList)
- **特殊编码**: `sys.cache.latest.refreshTime`(最近一次手动清理缓存时间)、`pm.report.project.summary.status`(项目状态汇总维度配置)、`pm.report.project.summary.executionState.relation`(实施状态映射)、`sys.mail.sendFailed.maxCount`(邮件发送失败最大次数,默认 3)、`sys.sql.inject.filter`(SQL 注入过滤正则)、`sys.sql.table.whitelist.regex`/`sys.sql.table.blacklist.regex`(表白/黑名单正则)

### 表 fnd_files(文件)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| fileName | VARCHAR | 否 | 文件名 | — | C |
| filePath | VARCHAR | 否 | 文件存储相对路径(含分隔符前后缀) | — | C |
| fileType | VARCHAR | 是 | 文件类型 | — | I |
| uploadBy | VARCHAR | 是 | 上传人用户名,关联 `fnd_user_info.username` | — | C |
| uploadTime | DATETIME | 是 | 上传时间 | — | I |

- **证据**: sql-map-admin-config.xml:925-936(insert)、937-941(query_flie_info)、942-960(query_file_list)、980-984(delete)
- **业务规则**: `filePath` 存储形如 `\upload\file\{randNum}\` 的相对路径,与 `fileName` 拼接后通过 ServletContext.getResourceAsStream 读取

### 表 fnd_mails(邮件)【老架构,业务归属本域】

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

- **证据**: sql-map-admin-config.xml:685-700(insert)、715-721(query_sys_mails)、723-727(update_sys_mails_state)
- **备注**: 老架构表;新架构使用 `t_mails`(见下表),字段以驼峰命名

### 表 t_mails(邮件)【新架构,本域专属】

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

- **证据**: MailInfoMapper.xml:4-21(resultMap)、22-24(列清单)、35-46(insert)、47-149(insertSelective)、150-200(updateSelective)、386-494(发送状态批量更新)
- **业务规则**: 发送结果通过 `INSERT ... ON DUPLICATE KEY UPDATE` 合并更新,避免并发冲突(`MailInfoMapper.xml:415-494`)

### 表 tb_sys_log(系统操作日志)【与 001 共享,业务归属本域】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| ID | INT | 否 | 主键,自增 | 唯一 | C |
| USER_NAME | VARCHAR | 是 | 操作用户名,关联 `fnd_user_info.username` | — | C |
| IP | VARCHAR | 是 | 操作 IP | — | C |
| ACTION | VARCHAR | 是 | 操作动作 | — | I |
| RESULT | VARCHAR | 是 | 操作结果 | — | I |
| INFO | VARCHAR | 是 | 操作详情 | — | C |
| TIME | INT | 是 | 操作时间(Unix 时间戳) | — | C |

- **证据**: sql-map-admin-config.xml:541-546(insert)、547-557(resultMap)、559-591(select)
- **备注**: 老架构表;`TIME` 字段以整型存储 Unix 时间戳(`sql-map-admin-config.xml:554-555` jdbcType=int)

### 表 t_sys_log(系统操作日志)【新架构,本域专属】

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
| create_date | VARCHAR | 是 | 操作时间(字符串) | — | C |

- **证据**: SysLogMapper.xml:4-15(resultMap)、16-19(列清单)、20-25(select)、30-39(insert)、40-106(insertSelective)、107-152(update)、154-221(查询)

### 表 t_sync_log(同步日志)【本域专属】

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

- **证据**: SyncLogMapper.xml:4-19(resultMap)、20-26(列清单)、27-34(select)、39-49(insert)、51-129(insertSelective)、130-197(update)、200-312(查询)

### 表 t_dictionary(数据字典)【新架构,本域专属】

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

- **证据**: DictionaryMapper.xml:4-15(resultMap)、16-19(列清单)、20-25(select)、30-39(insert)、40-106(insertSelective)、107-145(update)、146-196(分页查询)、199-245(计数)、246-263(类型查询)
- **业务规则**: `selectMaxDicTypeId` 用于新增类型时分配 `dic_type_id`(`DictionaryMapper.xml:256-258`)

### 表 t_sys_variable(系统变量)【新架构,本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| code | VARCHAR | 否 | 变量编码,主键 | 唯一 | C |
| id | INT | 是 | 自增 ID(冗余) | — | I |
| var | VARCHAR | 是 | 变量值(可能加密存储) | — | C |
| remark | VARCHAR | 是 | 备注 | — | I |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |
| effectiveFrom | TIMESTAMP | 是 | 生效起始时间 | — | C |
| effectiveTo | TIMESTAMP | 是 | 失效时间 | — | C |

- **证据**: SystemVariableMapper.xml:4-15(resultMap)、16-19(列清单)、20-25(select)
- **业务规则**: `var` 字段在非二次认证场景下以 ASE 加密返回(`SystemVariableController.java:60-66,82-87`)

### 表 t_notify_template(通知模板)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键,自增 | 唯一 | C |
| templateCode | VARCHAR | 否 | 模板编码(业务检索键) | 同编码可多条,按生效时间取当前有效 | C |
| subject | VARCHAR | 是 | 模板标题 | — | C |
| content | LONGTEXT | 是 | 模板内容(HTML,经 Jsoup 净化) | — | C |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |
| effectiveFrom | TIMESTAMP | 是 | 生效起始时间 | `< now()` 才被查询 | C |
| effectiveTo | TIMESTAMP | 是 | 失效时间 | `IS NULL OR > now()` 才被查询 | C |
| priority | INT | 是 | 优先级(查询时排序用,POJO 未体现但 XML 引用) | — | I |

- **证据**: NotifyTemplateMapper.xml:4-15(resultMap)、16-19(列清单)、20-25(select)、30-39(insert)、40-106(insertSelective)、107-152(update)、154-160(按编码查有效模板)、162-204(按选择查询含 `order by priority`)
- **业务规则**: 查询有效模板 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())`(`NotifyTemplateMapper.xml:159`)

### 表 t_file(文件)【新架构,本域专属】

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

- **证据**: FileInfoMapper.xml:4-17(resultMap)、18-21(列清单)、22-27(select)、32-44(insert)、45-105(insertSelective)、291-368(查询)、331-340(insertFileInfo)

### 表 t_file_type(文件类型)【本域专属】

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

- **证据**: FileInfoMapper.xml:325-329(selectFileTypeByCode);FileType.java
- **备注**: 表结构由 POJO `FileType.java` 反推;mapper 仅查询,CRUD 操作在其他 mapper `[待澄清]`

### 表 t_data_operation(数据操作配置)【本域专属】

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
| empPower | VARCHAR | 是 | 员工权限(用户 ID 列表) | 匹配当前用户 ID 才允许操作 | C |
| depPower | VARCHAR | 是 | 部门权限(预留) | — | I |
| state | BIT | 是 | 状态 | — | C |
| effectiveFrom | TIMESTAMP | 是 | 生效起始时间 | — | C |
| effectiveTo | TIMESTAMP | 是 | 失效时间 | — | C |
| createBy | VARCHAR | 是 | 创建人 | — | I |
| createTime | TIMESTAMP | 是 | 创建时间 | — | I |
| updateBy | VARCHAR | 是 | 更新人 | — | I |
| updateTime | TIMESTAMP | 是 | 更新时间 | — | I |
| formHtml | LONGTEXT | 是 | 表单 HTML(经 Jsoup 表单白名单净化) | — | C |
| script | LONGTEXT | 是 | SQL 脚本(导出用,经 SQL 校验) | — | C |

- **证据**: DataOperationMapper.xml:4-24(resultMap)、25-29(列清单)、30-35(select)、40-58(insert)、60-105(insertSelective)、107-160(update)、161-200(其他)
- **业务规则**: `type=0` 时 `script` 必须经 `checkSql` 校验通过(`DataOperationController.java:165-173`)

### 表 t_down_log(下载日志)【本域专属】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| fileIds | VARCHAR | 否 | 下载的文件 ID 列表 | — | C |
| ip | VARCHAR | 是 | 下载者 IP | — | C |
| timeline | INT | 是 | 下载时间(Unix 时间戳) | — | I |
| downloadTime | DATETIME | 是 | 下载时间 | — | C |
| user | VARCHAR | 是 | 下载人 | — | C |

- **证据**: FileInfoMapper.xml:370-375(insertdownlog)

### 表 mes_oqc_info(OQC 检验信息)【外部表,本域只读】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| barcode | VARCHAR | 否 | 设备序列号(查询键) | — | C |
| inspectUser | VARCHAR | 是 | 检验员(关联 `mes_seal_info.user`) | — | C |
| inspectTime | DATETIME | 是 | 检验时间(用于匹配印章领取窗口) | — | C |

- **证据**: sql-map-certificate-config.xml:11-30
- **备注**: 此表位于 MES 系统,本域只读查询,不维护

### 表 mes_seal_info(印章信息)【本域维护,外部表】

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

- **证据**: sql-map-certificate-config.xml:39-57(insertSealInfo、truncateSealInfo)
- **业务规则**: 导入前先 `truncate`(`sql-map-certificate-config.xml:55-57`);查询时 `info like 'QC PASS%'` 且 `inspectTime` 在 `[takeTime, backTime]` 区间内(`sql-map-certificate-config.xml:18-28`)

### 表 pm_report_line_data(报表趋势数据)【本域维护,跨域引用项目表】

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

- **证据**: sql-map-report-config.xml:467-512(查询)、555-580(批量插入)、586-603(impl 趋势查询)
- **业务规则**: 趋势查询 `effectiveTo IS NULL ORDER BY settingTime`;同一 `dataTypeCode + officeCode + settingTime` 历史数据通过 `effectiveTo` 失效

### 表 fnd_company(公司)【与 001 共享,本域查询】

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INT | 否 | 主键 | 唯一 | C |
| name | VARCHAR | 是 | 公司名称 | — | C |
| code | VARCHAR | 是 | 公司编码 | — | C |
| status | INT | 是 | 状态 | — | C |
| pid | INT | 是 | 上级公司 ID | — | C |

- **证据**: sql-map-admin-config.xml:1007-1032(queryCompanyList、queryCompanyOne)
- **备注**: 老架构表;新架构表为 `t_company`(见 CompanyMapper.xml,字段更丰富含账套/法人/地址等)

### 表 t_company(公司)【新架构,本域查询】

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

- **证据**: CompanyMapper.xml:4-28(resultMap)、29-33(列清单)
- **备注**: 业务归属本域(基础数据范畴),但公司管理与 001 组织管理有交叉 `[待澄清]`

### 跨域引用表(本域查询不维护)

- `pm_project_header`、`pm_project_member`、`pm_project_state`、`pm_project_contract`、`pm_project_group_relationship`:项目主数据,见 004 域
- `pm_cl_evaluation_header`、`pm_cl_quesnaire_result_header`、`pm_cl_quesnaire_result_line`、`pm_cl_quesnaire_template_line`:客户评价数据,见回调/客户评价域
- `fnd_department`、`fnd_user_info`、`fnd_user_power`、`fnd_roles`、`fnd_menus`、`fnd_user_menus`、`fnd_role_menus`:组织与权限数据,见 001 域

---

## 第4章 非功能需求

### 4.1 性能

#### NFR-4.1.1 基础数据缓存
- **要求**: 基础数据查询启用 `basicCache`(LRU,size=50,readOnly,1 小时刷新);insert/update/手动刷新缓存时失效(`sql-map-admin-config.xml:763-771`)
- **证据**: sql-map-admin-config.xml:763-771

#### NFR-4.1.2 用户数据缓存
- **要求**: 用户/角色/菜单查询启用 `userCache`(LRU,size=50,1 小时刷新);相关 insert/update 时失效(`sql-map-admin-config.xml:4-25`)
- **证据**: sql-map-admin-config.xml:4-25

#### NFR-4.1.3 报表临时表
- **要求**: 质量报表(`quality`)与实施方式报表(`implway`)使用临时表先聚合再查询,降低主表压力;查询后 `DROP TABLE IF EXISTS`(`sql-map-report-config.xml:105-142,399-407`)
- **证据**: sql-map-report-config.xml:105-142,138-142

#### NFR-4.1.4 数据导出流式 Excel
- **要求**: 数据导出使用 SXSSF 流式写出(`rowAccessWindowSize=100`),避免大数据量 OOM;进度实时写入会话(`DataOperationController.java:522-542`)
- **证据**: DataOperationController.java:522-552

#### NFR-4.1.5 邮件批量合并更新
- **要求**: 邮件发送结果通过 `INSERT ... ON DUPLICATE KEY UPDATE` 批量合并,减少数据库往返(`MailInfoMapper.xml:415-494`)
- **证据**: MailInfoMapper.xml:415-494

### 4.2 安全

#### NFR-4.2.1 SQL 注入防护
- **要求**: 数据导出/数据操作中的 SQL 经三层校验:① Jsoup 净化;② 系统变量 `sys.sql.inject.filter` 正则匹配(命中则重定向非法页面);③ 系统变量 `sys.sql.table.whitelist.regex` 白名单与 `sys.sql.table.blacklist.regex` 黑名单校验(`DataOperationController.java:712-764`)
- **证据**: DataOperationController.java:712-764

#### NFR-4.2.2 系统变量敏感值加密
- **要求**: 系统变量 `var` 字段在非二次认证场景下以 ASE 加密返回,防止敏感配置(密码、密钥)泄露(`SystemVariableController.java:60-66,82-87,108-113`)
- **证据**: SystemVariableController.java:60-113

#### NFR-4.2.3 通知模板 HTML 净化
- **要求**: 通知模板 `content` 经 Jsoup `relaxed` 白名单净化(允许表格属性、相对链接),防止 XSS(`NotifyTemplateController.java:92-102,120-130`)
- **证据**: NotifyTemplateController.java:92-102,120-130

#### NFR-4.2.4 数据操作权限控制
- **要求**: 数据操作配置含 `empPower`(员工权限列表),非管理员仅能操作 `empPower` 包含自身用户 ID 的配置(`DataOperationController.java:766-784`)
- **证据**: DataOperationController.java:766-784

#### NFR-4.2.5 集群刷新管理员限制
- **要求**: 集群核心功能刷新仅 `ROLE_ADMIN` 可调用(`ClusterController.java:24-26`)
- **证据**: ClusterController.java:24-26

#### NFR-4.2.6 报表区域权限
- **要求**: 项目状态汇总报表对非管理员注入 `areaPowers` 区域权限限制(`ReportAction.java:559`);SQL 中通过 `ph.column001 in ('', ...)` 过滤(`sql-map-report-config.xml:620-622`)
- **证据**: ReportAction.java:553-566;sql-map-report-config.xml:620-622

#### NFR-4.2.7 报表角色重定向
- **要求**: 非管理员/工程经理/工程经理组长/财务/项目管理员/回访员角色访问报表首页被重定向至项目状态汇总页(`ReportAction.java:98-106`)
- **证据**: ReportAction.java:88-106

#### NFR-4.2.8 文件下载公开/私有分离
- **要求**: 文件下载区分公开(`/file/down/public/{id}`、`/file/zipdown/public/{fileIds}`)与私有(`/file/down/private/{id}`)两类端点,后者需登录(`UploaderController.java:258-288`)
- **证据**: UploaderController.java:258-288
- **备注**: `[待澄清]` 公开下载端点的鉴权策略(是否完全无鉴权)

### 4.3 可观测性

#### NFR-4.3.1 操作日志记录
- **要求**: 新架构关键操作(查看/新增/修改/删除字典)通过 `@SystemControllerLog` 注解记录到 `t_sys_log`(`DictionaryController.java:45,72,79,86`)
- **证据**: DictionaryController.java:45,72,79,86;SysLogMapper.xml:30-106

#### NFR-4.3.2 同步日志记录
- **要求**: 外部数据同步任务记录到 `t_sync_log`(目标方法、表对象、起止时间、成功标志、数据量、异常),便于排查同步问题(`SyncLogMapper.xml:39-49`)
- **证据**: SyncLogMapper.xml:39-49

#### NFR-4.3.3 下载日志记录
- **要求**: 文件下载记录到 `t_down_log`(文件 ID、IP、时间、用户)(`FileInfoMapper.xml:370-375`)
- **证据**: FileInfoMapper.xml:370-375

#### NFR-4.3.4 异常处理与错误 ID
- **要求**: 关键操作异常通过 `ExceptionHandler.insertException(e)` 记录并返回错误 ID,便于用户反馈(`MailInfoController.java:120-122`、`DataOperationController.java:545-547`)
- **证据**: MailInfoController.java:120-122;DataOperationController.java:545-547

#### NFR-4.3.5 数据导出进度反馈
- **要求**: 数据导出进度以百分比形式写入会话属性 `operationName`,前端可轮询查询(`DataOperationController.java:489,535,543`)
- **证据**: DataOperationController.java:489-543

### 4.4 可用性

#### NFR-4.4.1 缓存手动刷新
- **要求**: 提供手动刷新基础数据/用户数据缓存能力(更新 `sys.cache.latest.refreshTime` 触发 `basicCache`/`userCache` 失效)(`sql-map-admin-config.xml:27-30,768`)
- **证据**: sql-map-admin-config.xml:27-30,768

#### NFR-4.4.2 集群缓存刷新
- **要求**: 集群环境下提供 `/cluster/refreshCore` 端点刷新当前用户菜单、过滤链、系统变量(`ClusterController.java:22-32`)
- **证据**: ClusterController.java:22-32

#### NFR-4.4.3 同步任务手动触发
- **要求**: 支持通过 AJAX 指定任务名手动触发 Quartz 同步任务,不必等待定时调度(`OperateLogAction.java:137-197`、`SyncLogController.java:78-144`)
- **证据**: OperateLogAction.java:137-197;SyncLogController.java:78-144

### 4.5 兼容性

#### NFR-4.5.1 新老架构并存
- **要求**: 老架构(Struts + iBATIS + `fnd_*`/`tb_sys_log` 表)与新架构(Spring MVC + MyBatis + `t_*` 表)并存,部分域(基础数据、邮件、系统变量、操作日志)存在两套表结构(`fnd_basic_data` vs `t_dictionary`、`fnd_mails` vs `t_mails`、`fnd_sys_arg` vs `t_sys_variable`、`tb_sys_log` vs `t_sys_log`)
- **证据**: sql-map-admin-config.xml vs core/mapping/*.xml
- **备注**: `[待澄清]` 新老表是否通过同步任务保持一致,还是分别服务不同模块

#### NFR-4.5.2 数据库类型适配
- **要求**: SQL 校验时根据当前数据源类型(`RoutingDataSource`)适配 SQL 解析(`DataOperationController.java:748,791-794`)
- **证据**: DataOperationController.java:748,791-794

### 4.6 数据完整性

#### NFR-4.6.1 基础数据时效性
- **要求**: 基础数据、系统变量、通知模板均通过 `effectiveFrom`/`effectiveTo` 控制时效,查询均按 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())` 过滤
- **证据**: sql-map-admin-config.xml:787-789,827,864-866;NotifyTemplateMapper.xml:159;SystemVariableMapper.xml

#### NFR-4.6.2 邮件失败重试上限
- **要求**: 邮件发送失败次数超过 `sys.mail.sendFailed.maxCount`(默认 3)后不再重试,避免无限重试(`MailerJob.java:25-26`;`MailInfoMapper.xml:377-384`)
- **证据**: MailerJob.java:25-26;MailInfoMapper.xml:377-384

#### NFR-4.6.3 基础数据编码唯一性
- **要求**: 同一 `dataTypeCode` 下 `basicDataId` 唯一,通过 `find_basic_data_id` 校验(`sql-map-admin-config.xml:857-861`)
- **证据**: sql-map-admin-config.xml:857-861
