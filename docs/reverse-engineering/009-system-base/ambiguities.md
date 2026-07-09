# 009-system-base 域歧义清单(Ambiguities)

> 日期: 2026-07-09
> 歧义总数: 17
> 比对基线:
> - 正式 spec:`009-system-base:.specify/specs/009-system-base/spec.md`
> - 草稿:`009-system-base:docs/reverse-engineering/009-system-base/spec-draft.md`
> - 代码证据:草稿/spec 中标注的 `PMS-struts`、`core` 源码行号
>
> 三比对维度:
> 1. **代码 vs 文档**:代码证据与 spec 描述的术语/边界/实现细节不一致
> 2. **代码 vs 代码**:同一 spec 内不同位置引用的代码证据相互矛盾,或新老架构代码语义冲突
> 3. **spec 内部**:spec 自身规则未闭环、`[待澄清]` 标记待展开、跨域归属未定

---

## AMB-009-01: 系统变量加密算法术语不一致(ASE vs AES)

- **位置**:
  - 草稿 `US-050`、`NFR-4.2.2`、表 `t_sys_variable` 业务规则:`以 ASE 加密返回`
  - spec.md `SC-007`、表 `t_sys_variable` 业务规则:`以加密形式返回`(去掉算法名)
- **现象**:草稿多处写作 `ASE` 加密,spec.md 抽象化为"加密"但未澄清算法名。`ASE` 非业界标准对称加密算法名,疑似 `AES` 的拼写错误,亦可能是项目自定义缩写。
- **候选解释**:
  1. `ASE` 是 `AES` 的笔误,实际使用 AES(128/256)对称加密
  2. `ASE` 是项目自定义加密算法缩写(如 Aes/Sha/Encoder 组合)
  3. 仅是占位符,加密实现未定
- **影响面**:`SC-007`(加密/解密对业务透明)、新系统迁移时的加密互操作;若误读为 AES 而实际为自定义算法,迁移后将无法解密历史 `var` 字段
- **建议决策**:查 `SystemVariableController.java:60-66` 的加密工具类源码确认算法,统一术语为标准名(AES)或在 spec 中显式标注自定义算法全名

---

## AMB-009-02: 系统变量时效过滤条件边界不一致(`<=` vs `<`)

- **位置**:
  - `FR-2.7.1`(老架构 `fnd_sys_arg`):`effectiveFrom <= now() AND (effectiveTo IS NULL OR effectiveTo > now())`
  - `SC-024` / `NFR-4.6.1`:"查询均按 `effectiveFrom < now() AND (effectiveTo IS NULL OR effectiveTo > now())` 过滤"
  - 表 `fnd_basic_data`、`t_notify_template`:`effectiveFrom < now()`
- **现象**:老架构系统变量查询用 `effectiveFrom <= now()`,而 SC-024 笼统声明"均按 `< now()`",其余实体用 `< now()`。`effectiveFrom` 恰好等于 `now()` 的记录在老架构系统变量中可见,在其他实体中不可见。
- **候选解释**:
  1. 老架构故意用 `<=`(含生效时刻),新架构统一为 `<`,SC-024 描述不严谨
  2. 笔误,应统一为 `<` 或 `<=`
  3. 老架构 SQL 是历史遗留,新系统迁移时统一
- **影响面**:边界时刻(生效起始时间 = 当前时间)的数据可见性;同一批数据在不同子域的时效判定不一致;新系统迁移若统一规则可能改变老系统行为
- **建议决策**:明确统一为 `effectiveFrom <= now()`(更符合"生效起始"语义,含生效时刻)或 `< now()`,修正 SC-024 表述为"各实体按其既定规则",并标注老架构保留 `<=`

---

## AMB-009-03: 邮件失败重试边界表述歧义("超过" vs "达到")

- **位置**:
  - `FR-2.6.7` / `US-046`:查询条件 `failedCount < sys.mail.sendFailed.maxCount(默认 3)`
  - `SC-025`:"邮件发送失败次数**超过** `sys.mail.sendFailed.maxCount`(默认 3)后不再重试"
  - Edge Cases:"邮件 `failedCount` **达到** `sys.mail.sendFailed.maxCount`(默认 3)后,定时任务跳过"
- **现象**:查询条件 `failedCount < 3` 拾取意味着 `failedCount=0,1,2` 重试,`failedCount=3` 跳过。SC-025 说"超过 3"跳过(字面含义 4+ 才跳过),Edge Cases 说"达到 3"跳过。两者边界相差 1。
- **候选解释**:
  1. 以代码查询条件为准:`failedCount=3` 即跳过(达到即跳过),SC-025 表述不准确
  2. 查询条件应为 `<=`,SC-025 正确(代码有 off-by-one)
  3. `maxCount` 语义为"最大可重试次数",`failedCount` 达到此数后不再重试
- **影响面**:邮件最多重试次数(2 次还是 3 次);SC-025 作为可测量标准若被测试用例采用,可能与代码行为不符
- **建议决策**:以代码 `failedCount < maxCount` 为准,修正 SC-025 为"达到 `maxCount` 后不再重试",并明确"最多重试 maxCount 次(失败计数从 0 开始)"

---

## AMB-009-04: 受限 SQL 执行功能(FR-2.1.5)是否保留 [待澄清]

- **位置**:
  - `FR-2.1.5` 执行维护 SQL(受限):"SQL 通过字符串拼接到 `<![CDATA[ $executeSql$ ]]>` 执行" + "风险:SQL 注入风险极高 `[待澄清]` 该功能是否仍在生产使用"
  - `US-1` 验收场景 5:"`[待澄清]` 该受限 SQL 执行功能是否仍保留"
  - Edge Cases:"无 WHERE 的 UPDATE/DELETE"
- **现象**:基础数据维护界面允许管理员直接执行拼接 SQL(仅校验是否含 `where`/`insert` 关键字),存在极高 SQL 注入风险。spec 将其列为 FR 但标记 `[待澄清]` 是否保留。
- **候选解释**:
  1. 保留(管理员紧急运维需要,新系统加严校验)
  2. 废弃(新系统不再提供裸 SQL 执行入口)
  3. 保留但限定为只读 SELECT 或加白名单
- **影响面**:管理员运维能力;安全审计;若保留则与 `SC-006`(SQL 三层校验)的安全模型冲突——FR-2.1.5 的 SQL 仅关键字校验,未走三层校验
- **建议决策**:建议废弃裸 SQL 执行入口,或强制接入 `SC-006` 的三层校验(HTML 净化 + 注入正则 + 表白名单);在 spec 中明确决策并移除 `[待澄清]`

---

## AMB-009-05: 公开文件下载端点鉴权策略未定(SC-013)[待澄清]

- **位置**:
  - `SC-013`:"文件下载区分公开与私有端点,私有端点需登录;`[待澄清]` 公开下载端点的鉴权策略(是否完全无鉴权)"
  - `FR-2.5.7`:"公开下载端点的鉴权策略待确认(见 SC-013)"
  - `US-5` 验收场景 4:"直接返回文件流(公开下载端点鉴权策略见 SC-013)"
- **现象**:`/file/down/public/{id}`、`/file/zipdown/public/{fileIds}` 是否完全无鉴权(任何人凭 ID 即可下载)、是否需 Referer/IP 限制、是否需时效签名(`t_file.downloadKey` 字段存在但未在公开下载流程中引用)均未明确。
- **候选解释**:
  1. 完全无鉴权(凭文件 ID 即可下载)
  2. 需 `downloadKey` 校验(字段已存在但流程未描述)
  3. 需登录但允许跨域
- **影响面**:文件泄露风险(枚举 ID 可遍历下载);与 `t_file.downloadKey` 字段(标注"用于鉴权")的关系未闭环;`SC-016`(下载日志可追溯)在无鉴权下无法定位真实下载者
- **建议决策**:查 `UploaderController.java:258-288` 的 `publicDownload` 方法实现确认;建议公开下载至少校验 `downloadKey` 或加时效签名,并在 spec 中补全鉴权流程

---

## AMB-009-06: 新老架构表是否同步保持一致(SC-022)[待澄清]

- **位置**:
  - `SC-022`:"`[待澄清]` 新老表是否通过同步任务保持一致,还是分别服务不同模块"
  - `NFR-4.5.1` 草稿备注:同上
  - Assumptions:"新老架构过渡期并存……新系统迁移时须制定合并策略"
  - 表对照:`fnd_basic_data` vs `t_dictionary`、`fnd_mails` vs `t_mails`、`fnd_sys_arg` vs `t_sys_variable`、`tb_sys_log` vs `t_sys_log`
- **现象**:四组新老表并存,字段命名(下划线 vs 驼峰)、字段集合、时间存储类型(`tb_sys_log.TIME` INT Unix 戳 vs `t_sys_log.create_date` VARCHAR 字符串)均不同。spec 未说明数据是否双向同步、由谁写入、读写路由规则。
- **候选解释**:
  1. 老表只读(历史数据),新写入走新表,无同步
  2. 双写(新老 Controller 各写各表),无同步,数据可能不一致
  3. 定时任务单向同步(老→新 或 新→老)
  4. 不同模块服务不同表(老模块用老表,新模块用新表)
- **影响面**:数据一致性;查询结果取决于走哪套表;`SC-014`(新架构操作日志)与老架构 `tb_sys_log` 是否合并;迁移策略与 `DATA-REUSE-01` 契约(C 级字段不得删减)的冲突
- **建议决策**:逐表确认写入路径与同步机制;在 spec 中为每组新老表补充"写入方/读取方/同步策略"三栏,并在 Assumptions 中明确过渡期数据一致性容忍度

---

## AMB-009-07: t_file_type 表 CRUD 操作位置不明 [待澄清]

- **位置**:
  - 表 `t_file_type` 备注:"表结构由 POJO `FileType.java` 反推;mapper 仅查询,CRUD 操作在其他 mapper `[待澄清]`"
  - `FR-2.5.6`:`selectFileTypeByCode`(`FileInfoMapper.xml:325-329`)仅查询
- **现象**:`t_file_type` 的增删改操作未定位到具体 mapper,表结构本身由 POJO 反推而非 SQL 证据。文件类型配置如何维护(谁新增文件类型、修改大小限制/允许扩展名)未在 FR 中体现。
- **候选解释**:
  1. CRUD 在未列出的 mapper(如 `FileTypeMapper.xml`),需补充证据
  2. 通过数据库直接维护,无应用层 CRUD
  3. 表实际不存在,`FileType.java` 是硬编码枚举
- **影响面**:`FR-2.5.6`(按文件类型上传)依赖可配置的文件类型;若无法维护则上传策略不可配置;数据契约中表存在性未证实
- **建议决策**:搜索 `FileType` 相关 mapper/DAO 确认 CRUD 位置;若为 DB 直维则在 spec 中标注"运维维护,无应用 CRUD";若表不存在则从数据契约移除

---

## AMB-009-08: t_company 业务归属与 001 域交叉 [待澄清]

- **位置**:
  - 表 `t_company` 备注:"业务归属本域(基础数据范畴),但公司管理与 001 组织管理有交叉 `[待澄清]`"
  - 表 `fnd_company` 备注:"老架构表;新架构表为 `t_company`"
  - 跨域引用表:`fnd_department` 等见 001 域
- **现象**:`t_company` 字段含 `adminID`(上级公司)、`compAccount`(账套)、`lawyer`(法人)等组织管理属性,与 001 域组织管理职责重叠。spec 声称"本域查询"但未明确维护方,且本域 FR 中无公司 CRUD 端点(仅 `queryCompanyList`/`queryCompanyOne` 查询)。
- **候选解释**:
  1. 本域仅查询,维护在 001 域(公司属组织主数据)
  2. 本域维护(基础数据范畴),001 域只读
  3. 老架构 `fnd_company` 在本域,新架构 `t_company` 在 001 域
- **影响面**:跨域表归属影响契约所有权与变更审批;`compAccount`/`lawyer` 等字段若由 001 域维护则本域 spec 不应纳入数据契约
- **建议决策**:与 001 域 spec 对账确认 `t_company` 维护方;若归属 001 域则从本域数据契约移除或标注"跨域引用表";若本域维护则补充公司 CRUD FR

---

## AMB-009-09: t_data_export 表存在于草稿但缺失于 spec 数据契约

- **位置**:
  - 草稿 `FR-2.9.1`:"查询 `t_data_export` 动态列配置"
  - spec.md `FR-2.9.1`:"查询动态列配置 + 静态列工具"(删除了 `t_data_export` 表名)
  - spec.md 数据契约(Key Entities):无 `t_data_export` 表
- **现象**:草稿明确引用 `t_data_export` 表存储动态导出列配置,spec.md 在 FR 中抹去表名但未将该表补入数据契约,也未说明动态列配置的数据来源。
- **候选解释**:
  1. `t_data_export` 表存在,spec.md 遗漏未补入数据契约
  2. 动态列配置来自 `DataExportMapper.xml` 查询的其他表(如 `t_dictionary`)
  3. 表名错误,实为 `t_data_operation` 或其他
- **影响面**:`FR-2.9.1`/`FR-2.9.2` 通用数据导出的数据存储不明;新系统迁移时动态列配置无从迁移;`DATA-REUSE-01` 契约不完整
- **建议决策**:查 `DataExportController.java:43-50` 与 `DataExportMapper.xml` 确认表名;若 `t_data_export` 存在则补入 spec 数据契约并补全字段分级

---

## AMB-009-10: 报表临时表并发冲突与 DROP 时机未明确

- **位置**:
  - `SC-003`:"查询完成后自动清理临时表(`DROP TABLE IF EXISTS`)"
  - `NFR-4.1.3` 草稿:"查询后 `DROP TABLE IF EXISTS`"(`sql-map-report-config.xml:105-142,399-407`)
  - `FR-2.4.5`:"创建临时表 `implway`";`FR-2.4.6`:"创建临时表 `quality`"
- **现象**:临时表名固定为 `implway`/`quality`(无会话/用户前缀),`DROP TABLE IF EXISTS` 在创建前还是查询后未明确。若两个用户并发执行质量报表查询,临时表名冲突;若"创建前 DROP + 查询后 DROP",中间窗口仍冲突。
- **候选解释**:
  1. 每次查询:先 `DROP IF EXISTS` → `CREATE` → 查询 → `DROP`(并发冲突)
  2. 使用 MySQL 会话级临时表(`CREATE TEMPORARY TABLE`,会话隔离)
  3. 加锁串行化报表查询
- **影响面**:并发报表查询数据串扰;集群环境下多节点同时建表冲突;`SC-003` 作为可测量标准未覆盖并发场景
- **建议决策**:查 `sql-map-report-config.xml:105-142` 确认是否 `CREATE TEMPORARY TABLE`;若为普通表则建议改为会话级临时表或加用户前缀,并在 SC-003 补充"并发查询互不干扰"

---

## AMB-009-11: 印章导入 truncate 与 INSERT 事务性未明确

- **位置**:
  - `FR-2.3.3`:"先清空 `mes_seal_info`(`truncate`)……批量 INSERT"
  - 表 `mes_seal_info` 业务规则:"导入前先 `truncate`"
  - Edge Cases:"印章导入覆盖……历史数据被清除,需确保导入数据完整"
- **现象**:`truncate` 是 DDL 语句(MySQL 中非事务性,无法回滚),随后批量 INSERT。若 INSERT 失败(Excel 解析异常或部分行错误),`mes_seal_info` 已被清空,数据丢失且不可恢复。
- **候选解释**:
  1. `truncate` + INSERT 非事务,失败丢数据(当前行为)
  2. 应改用 `DELETE`(事务性)替代 `truncate`,失败可回滚
  3. 先 INSERT 到临时表,成功后再切换
- **影响面**:印章数据丢失将导致合格证查询(FR-2.3.2)全部失效;Edge Cases 已警示但未给出事务保障决策
- **建议决策**:建议新系统改用 `DELETE`(事务内)+ INSERT,或先备份再 truncate;在 spec 中明确事务边界与失败回滚策略,移除"需确保导入数据完整"的模糊表述

---

## AMB-009-12: 数据操作 empPower 权限对管理员是否豁免未明确

- **位置**:
  - `SC-009`:"数据操作配置含员工权限(`empPower`),**非管理员**仅能操作 `empPower` 包含自身用户 ID 的配置,越权拒绝率 100%"
  - `FR-2.9.3`:"权限校验 `empPower` 包含当前用户 ID(`DataOperationController.java:766-784`)"
  - 表 `t_data_operation`:`empPower` 业务不变量"匹配当前用户 ID 才允许操作"
- **现象**:SC-009 限定"非管理员",暗示管理员豁免 empPower 校验;但 FR-2.9.3 与表业务不变量未区分角色,笼统说"匹配当前用户 ID 才允许操作"。管理员是否可操作所有配置(含 empPower 不含自己 ID 的)未明确。
- **候选解释**:
  1. 管理员豁免,可操作所有数据操作配置
  2. 所有角色均受 empPower 限制,管理员也只能操作含自己 ID 的
  3. 管理员可配置 empPower 但执行仍受限
- **影响面**:管理员运维能力;权限模型一致性;SC-009 测试用例需明确管理员分支
- **建议决策**:查 `DataOperationController.java:766-784` 确认管理员分支;在 FR-2.9.3 与表业务不变量中补充分角色规则,统一与 SC-009 表述

---

## AMB-009-13: 通知模板复用系统变量二次认证会话标志(isSC)未说明

- **位置**:
  - `FR-2.6.8`:"非二次认证状态净化、二次认证状态反转义(`NotifyTemplateController.java:92-102,120-130`)"
  - `FR-2.7.3`:"系统变量二次认证……会话置 `isSC=true`"
- **现象**:通知模板的 content 净化/反转义行为依赖会话标志 `isSC`,而 `isSC` 由系统变量二次认证端点(`/system/sysVariable/secondaryCertification`)设置。spec 未说明通知模板与系统变量共享同一二次认证会话标志,即编辑通知模板前需先走系统变量二次认证。
- **候选解释**:
  1. `isSC` 全局共享,系统变量认证后通知模板亦生效(当前实现)
  2. 通知模板应有独立二次认证端点
  3. `isSC` 应按模块隔离
- **影响面**:通知模板编辑流程耦合系统变量认证;用户体验(编辑模板需先认证系统变量);会话过期后两模块均需重新认证;安全模型隐式依赖未文档化
- **建议决策**:确认 `NotifyTemplateController` 是否直接读 `session.isSC`;若是则在 spec 中明确"二次认证会话标志全局共享,覆盖系统变量与通知模板",并评估是否应拆分

---

## AMB-009-14: 合格证 productionDate 的 barcode 第 10-12 位月份映射不明

- **位置**:
  - `FR-2.3.2`:"根据 barcode 第 10-12 位(16 进制月)生成 `productionDate`(`CertificateAction.java:83-99`)"
  - `US-3` 验收场景 2:"根据 barcode 第 10-12 位(16 进制)生成生产日期"
- **现象**:barcode 第 10-12 位为 3 个字符,但 16 进制表示月份(1-12,即 1-C)仅需 1-2 字符。3 字符如何映射到月份未说明(取首字符?取前两位?含年份信息?)。
- **候选解释**:
  1. 取第 10-12 位中的子区间(如第 11-12 位)解析为 16 进制月
  2. 3 字符为"年+月"编码(如 `6C` = 2026 年 12 月)
  3. 第 10-12 位含其他信息,月份另取
- **影响面**:`FR-2.3.2` 生产日期计算正确性;合格证展示准确性;若解析逻辑错误将向客户出示错误日期
- **建议决策**:查 `CertificateAction.java:83-99` 源码确认位偏移与解析逻辑;在 spec 中补充完整解析规则(起始位、长度、进制、年月组合方式)

---

## AMB-009-15: t_dictionary 新增类型与新增字典项操作分离不清

- **位置**:
  - `FR-2.1.6` 新架构数据字典 CRUD:输入 `dicTypeId`、`dicTypeName`、`dicKey`、`dicValue` 等
  - `FR-2.1.7` 查询字典类型最大 ID:`SELECT max(dic_type_id) FROM t_dictionary`,用于"新增类型时分配 `dic_type_id`"
  - 表 `t_dictionary`:`dic_type_id` "同类型字典共享",业务不变量"同 `dic_type_id` + `dic_key` 应唯一"
- **现象**:spec 描述了字典项 CRUD(FR-2.1.6)与类型 ID 分配(FR-2.1.7),但未单独定义"新增字典类型"端点。`dic_type_id` 由 `max+1` 分配暗示有独立的新增类型操作,但 FR-2.1.6 的 CRUD 输入已含 `dicTypeName`,疑似新增字典项时顺带建类型。两者操作边界模糊。
- **候选解释**:
  1. 新增类型与新增字典项同一端点,首次传入新 `dicTypeName` 时自动分配 `dic_type_id`
  2. 有独立的新增类型端点未在 spec 体现
  3. 类型通过 `dic_type_id` 手工指定,`max+1` 仅建议值
- **影响面**:`dic_type_id` 唯一性保证(并发新增类型时 `max+1` 竞态);类型与字典项的数据模型一致性;`selectMaxDicTypeId` 在分布式/并发下不安全
- **建议决策**:查 `DictionaryController.java:34-89` 确认新增类型流程;建议改为数据库自增或唯一约束保证 `dic_type_id`,在 spec 中明确"新增类型"与"新增字典项"为独立操作或合并操作

---

## AMB-009-16: 报表趋势数据持久化定时任务来源与调度未定义

- **位置**:
  - `FR-2.4.11` 报表趋势数据持久化:"定时任务批量保存趋势图数据",批量 INSERT `pm_report_line_data`
  - 表 `pm_report_line_data` 业务规则:"同一 `dataTypeCode + officeCode + settingTime` 历史数据通过 `effectiveTo` 失效"
  - Assumptions:"定时任务调度可用"
- **现象**:spec 声明"定时任务批量保存趋势数据",但未说明:① 哪个定时任务类(无证据行号);② 调度周期(月度?每日?);③ 数据来源(从 `FR-2.4.2~2.4.9` 的实时查询结果计算 `specificValue` 并持久化?还是独立重算?);④ 历史 `effectiveTo` 失效操作何时执行。
- **候选解释**:
  1. 报表查询时顺带持久化当前快照,定时任务仅失效历史
  2. 独立定时任务(如每月 1 日)预计算所有指标并持久化
  3. 由 `MailerJob` 同类调度框架的另一个 Job 处理,证据未列出
- **影响面**:趋势折线图(`FR-2.4.7~2.4.9`)数据准确性依赖此持久化;若任务未运行则趋势图无数据;`effectiveTo` 失效逻辑缺失会导致趋势查询返回多条历史记录
- **建议决策**:搜索 `pm_report_line_data` 的写入方(批量 INSERT 调用方)定位定时任务类;在 spec 中补充任务类名、调度周期、数据来源、历史失效触发时机

---

## AMB-009-17: t_sys_log.create_date 为 VARCHAR 类型与时间查询适配未说明

- **位置**:
  - 表 `t_sys_log`:`create_date | VARCHAR | 是 | 操作时间(字符串)` C 级契约字段
  - 对比 `tb_sys_log.TIME`(INT Unix 时间戳)、`t_sync_log.syncStartTime`(TIMESTAMP)、`t_dictionary.createTime`(TIMESTAMP)
  - `FR-2.2.4`:新架构系统日志查询"按 `id desc` 默认排序",未提及时间范围查询
- **现象**:`t_sys_log.create_date` 以 VARCHAR 存储操作时间(字符串),与域内其他时间字段(TIMESTAMP/DATETIME/INT)不一致。作为 C 级契约字段,新系统迁移时是否保留 VARCHAR、时间范围查询(如"近 7 天日志")如何适配未说明。
- **候选解释**:
  1. 保留 VARCHAR(字符串格式如 `yyyy-MM-dd HH:mm:ss`),范围查询用字符串比较
  2. 迁移为 TIMESTAMP,牺牲老数据兼容
  3. 历史遗留,新系统新增 TIMESTAMP 列并行
- **影响面**:时间范围查询性能(无法走时间索引,需全表扫描字符串比较);`SC-014`(操作日志覆盖率)统计;新老架构日志合并查询(与 `tb_sys_log.TIME` INT 戳适配)
- **建议决策**:确认 `create_date` 实际存储格式;建议新系统迁移为 TIMESTAMP 并在 spec 中标注"原 VARCHAR 字段保留为 I 级,新增 TIMESTAMP 列为 C 级",或明确 VARCHAR 的字符串格式约定

---

## 汇总

| ID | 类别 | 严重度 | 状态 |
|---|---|---|---|
| AMB-009-01 | 代码 vs 文档(术语) | 中 | 待确认算法 |
| AMB-009-02 | 代码 vs 代码(边界) | 中 | 待统一规则 |
| AMB-009-03 | spec 内部(表述) | 中 | 待修正 SC-025 |
| AMB-009-04 | [待澄清] 展开 | 高 | 待决策保留/废弃 |
| AMB-009-05 | [待澄清] 展开 | 高 | 待定鉴权策略 |
| AMB-009-06 | [待澄清] 展开 | 高 | 待定同步策略 |
| AMB-009-07 | [待澄清] 展开 | 中 | 待定位 CRUD |
| AMB-009-08 | [待澄清] 展开 + 跨域 | 中 | 待与 001 域对账 |
| AMB-009-09 | 代码 vs 文档(表遗漏) | 中 | 待补数据契约 |
| AMB-009-10 | spec 内部(并发) | 高 | 待定临时表策略 |
| AMB-009-11 | spec 内部(事务) | 高 | 待定回滚策略 |
| AMB-009-12 | spec 内部(权限) | 中 | 待定管理员分支 |
| AMB-009-13 | spec 内部(耦合) | 中 | 待定会话隔离 |
| AMB-009-14 | 代码 vs 文档(解析) | 中 | 待补解析规则 |
| AMB-009-15 | spec 内部(操作边界) | 中 | 待定类型/项分离 |
| AMB-009-16 | spec 内部(调度缺失) | 高 | 待定位任务类 |
| AMB-009-17 | 代码 vs 代码(类型) | 中 | 待定迁移策略 |

**高严重度(阻塞迁移或存在数据/安全风险)**:AMB-009-04、05、06、10、11、16
**中严重度(影响一致性或需澄清)**:其余 11 条
