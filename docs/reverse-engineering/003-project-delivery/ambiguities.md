# 003-project-delivery 域歧义清单(Ambiguities)

> 日期: 2026-07-09
> 歧义总数: 22
> 比对基准:spec.md(正式规格)vs spec-draft.md(代码逆向草稿)vs 代码证据引用
> 比对维度:① 代码 vs 文档 ② 代码 vs 代码(证据内部矛盾)③ spec 内部 [待澄清] 展开

---

## AMB-003-01: pm_project 与 pm_project_header 表名/对象关系不明

- **位置**:spec.md Assumptions、Edge Cases、3.1 pm_project_header;spec-draft 附录第 1 条;代码证据 sql-map-project-config.xml:3626/3996/4001/4007
- **现象**:部分失效操作的 UPDATE 语句使用表名 `pm_project`(如 `UPDATE pm_project SET effectiveTo = NOW()`),而数据契约主表定义为 `pm_project_header`。spec.md 在 Assumptions 中"假定二者指向同一项目主数据语义",但未在数据契约中正式声明二者关系(同义词/视图/别名)。
- **候选解释**:
  1. `pm_project` 是 `pm_project_header` 的数据库同义词(synonym)或视图,二者完全等价;
  2. `pm_project` 是历史遗留表名,DDL 重命名后旧语句未同步迁移;
  3. `pm_project` 是 `pm_project_header` 的一个受限视图(例如仅含未失效记录)。
- **影响面**:NFR-CON-02 软删除模式、FR-PROJ-07 项目清理、所有 effectiveTo 失效操作的实现层;若为视图且含过滤条件,可能导致失效操作无法触达全部历史记录。
- **建议决策**:在数据契约 3.1 增加显式声明"pm_project 为 pm_project_header 的同义词/视图,语义等价";若实际为历史遗留表名,要求实现层统一为 pm_project_header 并迁移旧语句;在实现前用 DDL 查询确认二者关系。

---

## AMB-003-02: pm_project_contract 缺少主键 id、projectId、effectiveTo 字段

- **位置**:spec.md 3.3 pm_project_contract;spec-draft 3.3 及附录第 2 条;代码证据 sql-map-project-config.xml:1261-1264/3360-3363/3017-3026
- **现象**:数据契约中 pm_project_contract 仅列出 contractNo、projectGroupCode、createTime、createBy 四个字段,标注 [待澄清] 主键 id 与 projectId 是否存在。同时该表缺少 effectiveFrom/effectiveTo 软删除字段,但 NFR-CON-02 声称"合同等均采用 effectiveTo 失效模式"。
- **候选解释**:
  1. 表实际含自增主键 id 与 projectId 字段,但 SELECT/INSERT 语句未显式列出(逆向反推盲区);
  2. 表无主键,仅以 (contractNo, projectGroupCode) 作复合键,projectId 通过 projectGroupCode 间接关联;
  3. 合同关系不支持软删除(物理删除),与 NFR-CON-02 描述不符。
- **影响面**:FR-CONTRACT-01/02 合同合并预检与合并、FR-PROJ-09 设备转移插入转移合同关系;合同软删除能力缺失将影响 SC-007"历史数据可追溯"达成。
- **建议决策**:查询 DDL 确认表结构;若存在 id/projectId/effectiveTo,补全字段表;若确实无软删除字段,在 NFR-CON-02 中明确豁免 pm_project_contract,或评估是否需要补加软删除支持以满足可追溯性。

---

## AMB-003-03: pm_project_instruction 是否含自增主键 id 不明

- **位置**:spec.md 3.14 pm_project_instruction;spec-draft 3.14 及附录第 3 条;代码证据 sql-map-project-config.xml:1248-1253
- **现象**:数据契约列出 projectId、instructionsInfo、instructionsTime、instructionsUser、dataType、instructionsId、createTime、createBy,标注 [待澄清] 是否含主键 id。但反馈语义(dataType=1, instructionsId 指向原批示)要求原批示必须有稳定唯一标识,否则 instructionsId 无法引用。
- **候选解释**:
  1. 表含自增主键 id,INSERT 语句未显式写入(数据库自增),instructionsId 引用该 id;
  2. 以 (projectId, instructionsTime, instructionsUser) 作复合键,instructionsId 引用某时间戳派生值;
  3. instructionsId 实际指向 instructionsTime 字段(用时间戳作 ID)。
- **影响面**:FR-NOTIFY-02 保存批示(反馈时关联原批示)、FR-NOTIFY-03 查询批示列表;若主键缺失,反馈关联不可靠,可能产生悬挂引用。
- **建议决策**:确认表含自增 id 主键并在数据契约补全;FR-NOTIFY-02 应明确"instructionId 参数对应 pm_project_instruction.id";若实际无 id,需重新设计反馈关联机制。

---

## AMB-003-04: pm_project_spot_check_ignore_item 无主键 + truncate 覆盖式导入与软删除原则矛盾

- **位置**:spec.md 3.19、FR-INSPECT-03、Edge Cases;spec-draft 3.19 及附录第 4 条;代码证据 sql-map-project-config.xml:4529-4538
- **现象**:数据契约仅含 itemCode、itemModel、itemName 三个字段,无主键、无 effectiveFrom/effectiveTo、无 createTime/createBy。FR-INSPECT-03 采用"truncate 后批量插入"覆盖式导入。但 NFR-CON-02 声明"100% 采用失效时间软删除模式,历史数据可追溯",二者直接冲突。
- **候选解释**:
  1. 该表为全局配置表,语义上无需保留历史,truncate 是设计意图(NFR-CON-02 应豁免);
  2. 应改为软删除模式,补加 effectiveFrom/effectiveTo 与审计字段;
  3. truncate 是历史实现,应改为按 itemCode upsert + 失效旧记录。
- **影响面**:SC-007 软删除模式可追溯性、FR-INSPECT-03 导入审计;忽略项变更无法追溯谁在何时修改了配置。
- **建议决策**:明确该表为配置类豁免软删除,在 NFR-CON-02 增加豁免清单;或改为软删除模式以满足审计要求。建议倾向前者(配置表豁免),但需补加 createTime/createBy 以记录最近一次导入信息。

---

## AMB-003-05: ITR 工单表与 License 授权表字段未定义(SELECT * 查询)

- **位置**:spec.md 3.22 pm_project_incident_table_from_itr、3.23 pm_project_license_info_from_license;spec-draft 3.22/3.23 及附录第 5 条;代码证据 sql-map-project-config.xml:6382-6390/6405
- **现象**:两张外部同步表均使用 `SELECT *` 查询,字段未在源码中显式定义。spec.md 仅"反推"关键字段(projectCode、contractNo、barCode),标注 [待澄清] 完整字段列表需查询同步任务定义(GainDataFromITR/GainDataFromLicense)。
- **候选解释**:
  1. 表字段由外部同步任务定义,本域不拥有,完整字段需查询 ITR/License 同步模块;
  2. SELECT * 是反模式,实际只使用少数字段,应在契约中显式列出;
  3. 表结构动态变化,无法固定契约。
- **影响面**:FR-NOTIFY-04 问题工单查询、FR-NOTIFY-05 License 授权查询;外部表字段变更将直接破坏本域查询,违反契约稳定性原则。
- **建议决策**:查询 GainDataFromITR/GainDataFromLicense 同步任务定义,补全字段表;将 SELECT * 改为显式字段列表,仅声明本域实际消费的字段为契约字段(分级 C),其余为外部内部字段。

---

## AMB-003-06: column001 字段语义冲突(办事处编码 vs 利润中心)

- **位置**:spec.md 3.1 pm_project_header.column001、FR-SHIP-01、FR-MEMBER-04、FR-PROJ-09、Edge Cases;spec-draft FR-SHIP-01/FR-MEMBER-04
- **现象**:3.1 数据契约定义 column001 = 办事处编码(officeCode),关联部门基础数据。但 FR-SHIP-01、FR-MEMBER-04、FR-PROJ-09 多处描述"总代借货(salesType=14)时按 column001(profitCenter)利润中心过滤"。同一字段同时承担"办事处编码"与"利润中心"两种语义。
- **候选解释**:
  1. column001 在总代借货场景下被复用为利润中心(同一物理字段,业务语义随 salesType 切换);
  2. 办事处编码与利润中心存在一一对应关系,过滤时实际用的还是办事处编码,文档误标为 profitCenter;
  3. 存在另一个独立的 profitCenter 字段未在契约中列出。
- **影响面**:FR-SHIP-01 发货查询、FR-MEMBER-04 安装地址保存、FR-PROJ-09 设备转移、FR-SOFT-01 软件版本查询;若语义切换,实现层需按 salesType 分支处理,易引入 bug。
- **建议决策**:确认 column001 是否为利润中心来源;若是语义复用,在 3.1 字段说明中明确"salesType=14 时作为 profitCenter 使用";若存在独立 profitCenter 字段,补全数据契约。

---

## AMB-003-07: transferFlag 语义矛盾(0=转销,1=退货,!=1 排除)

- **位置**:spec.md 3.10 pm_project_shipment.transferFlag、FR-SHIP-01、Edge Cases、SC-021;spec-draft 3.10
- **现象**:3.10 定义 transferFlag(0=转销,1=退货,!=1 排除)。但 Edge Cases 与 SC-021 描述"查询发货设备默认排除已转出设备(转移标记≠1)"。若 transferFlag=0 表示转销(已转移),则"已转出"应为 transferFlag=0,但排除条件是 !=1,会把 0(转销)也排除——逻辑上转销设备应保留可查,只有退货设备需排除?语义混乱。
- **候选解释**:
  1. transferFlag=1 表示"已转出(退货)",!=1 排除即排除退货设备,转销(0)保留;
  2. transferFlag=0 表示"未转移",1=已转出(含转销+退货),!=1 排除所有已转出;
  3. 文档枚举错误,实际语义为 0=未转移,1=已转出。
- **影响面**:FR-SHIP-01 发货查询、FR-PROJ-09 设备转移、SC-021 历史设备保留;错误的排除逻辑会导致发货清单包含/遗漏错误设备。
- **建议决策**:重新核对代码中 transferFlag 的赋值与过滤逻辑,明确枚举:建议为 0=未转移(默认)、1=已转出(转销)、2=已转出(退货),排除条件改为"!=0"或按业务重新定义;同步修正 spec.md 3.10、Edge Cases、SC-021 描述。

---

## AMB-003-08: projectRefreshTime 字段在 NFR 引用但数据契约缺失

- **位置**:spec.md NFR-OBS-01、SC-019;spec-draft 4.5 NFR-OBS-01;代码证据 ProjectMemberAction.java:60/82/105、ProjectWeeklyAction.java:113/162
- **现象**:NFR-OBS-01 与 SC-019 描述"成员变更、安装地址保存、周报保存/提交等操作均更新 pm_project_header.projectRefreshTime"。但 3.1 pm_project_header 数据契约字段表中没有 projectRefreshTime 字段。
- **候选解释**:
  1. 字段实际存在,逆向反推时遗漏(INSERT/UPDATE 语句未显式覆盖该字段);
  2. projectRefreshTime 复用了 updateTime 字段;
  3. 字段在另一张关联表(如 pm_project_state)中。
- **影响面**:SC-019 项目活跃度可观测性、FR-MEMBER-01/02/04、FR-WEEKLY-03/04;字段缺失会导致活跃度统计无法实现。
- **建议决策**:在 3.1 pm_project_header 补充 projectRefreshTime(datetime,可空,项目最后刷新时间,默认 NOW())字段;确认其为独立字段而非复用 updateTime。

---

## AMB-003-09: 物理删除/truncate 与 NFR-CON-02 "100% 软删除"原则多处冲突

- **位置**:spec.md FR-PROJ-07(modifyflag=1 物理删除)、FR-INSPECT-03(truncate)、FR-FILE-03(deleteFileById 物理删除)、NFR-CON-02/SC-007;spec-draft 对应 FR
- **现象**:NFR-CON-02 声明"交付件、项目主表、成员、合同等关键数据 100% 采用失效时间软删除模式,历史数据可追溯",SC-007 同。但 FR-PROJ-07 明确支持"物理删除"模式(modifyflag=1 → batchDeleteProject),FR-INSPECT-03 采用 truncate,FR-FILE-03 周报附件采用 deleteFileById 物理删除。多处与"100% 软删除"直接冲突。
- **候选解释**:
  1. NFR-CON-02 描述过于绝对,实际应豁免清理类/配置类/附件类操作;
  2. 物理删除是历史保留能力,应废弃,统一改为软删除;
  3. 不同数据类别有不同保留策略,NFR-CON-02 需细化分类。
- **影响面**:SC-007 软删除模式达成度、FR-PROJ-07 项目清理、FR-INSPECT-03 忽略项导入、FR-FILE-03 周报附件删除;若按 NFR 严格执行,多个 FR 需重新设计。
- **建议决策**:将 NFR-CON-02 修改为"关键业务数据(项目主表、成员、合同、交付件、相关方)采用软删除;清理类批量操作(项目清理)与配置类(忽略项)与临时附件(周报附件)允许物理删除",并明确豁免清单;或统一为软删除但提供清理作业定期物理清除已失效数据。

---

## AMB-003-10: projectState 状态 21(创建中)与 30(已创建)关系不明,状态机未包含 21

- **位置**:spec.md 3.1 pm_project_header.projectState、SC-006、Assumptions;spec-draft 3.1
- **现象**:3.1 projectState 枚举列出"30=已创建,31=指定服务经理,32=指定项目经理,34=填写渠道,20=已闭环,100=不予跟踪,21=创建中"。但 SC-006 状态机仅描述"30→31→32→34→20/100",未包含 21。21(创建中)与 30(已创建)语义重叠,关系不明。
- **候选解释**:
  1. 21 是创建流程中的中间态(参数为空时返回创建页阶段),提交后转为 30;
  2. 21 是历史废弃状态,已由 30 取代;
  3. 21 用于串货项目或特殊创建场景,与 30 并行存在。
- **影响面**:SC-006 状态机校验、FR-PROJ-02 项目创建、FR-PROJ-01 列表查询(状态扩展 30/31/32 未含 21);状态机实现可能拒绝 21 状态迁移。
- **建议决策**:明确 21 的用途;若是中间态,在状态机中补充"21→30"迁移;若是废弃态,从枚举中移除并标记 D(废弃);在 FR-PROJ-01 状态扩展逻辑中说明是否包含 21。

---

## AMB-003-11: taskTypeId 与 projectPlanState 两套编码体系关系不明

- **位置**:spec.md 3.7 pm_project_state.projectPlanState、3.8 pm_project_task.taskTypeId、FR-INSPECT-04;spec-draft 3.7/3.8、FR-INSPECT-04
- **现象**:3.7 projectPlanState 枚举(43=到货验收,44=安装,45=初验,46=终验,48=项目闭环);3.8 taskTypeId 枚举(30=到货验收,60=初验,61=终验)。两套编码都描述验货节点,但数值不同,且 taskTypeId 缺少"安装"对应值。FR-INSPECT-04 提到"到货验收/初验/终验"未提"安装",但 spec-draft 阈值描述包含 44(安装)=5 月。
- **候选解释**:
  1. projectPlanState 是项目级状态汇总,taskTypeId 是任务级类型,二者通过业务规则映射(30→43,60→45,61→46);
  2. 两套编码独立演进,存在历史遗留冗余;
  3. taskTypeId 仅覆盖验货类任务,安装(44)由其他任务类型承担,未列出。
- **影响面**:FR-INSPECT-04 验货超期计算(阈值 43_after=2月、44=5月、45=9月、46=-1)、FR-PROJ-05 工程计划编辑;编码映射错误会导致超期提醒发错节点。
- **建议决策**:在数据契约中增加两套编码的映射表(如 30↔43、60↔45、61↔46);明确 taskTypeId 是否需要"安装"对应值(如 50);统一 FR-INSPECT-04 与 Assumptions 的节点描述(是否包含安装)。

---

## AMB-003-12: FR-MEMBER-03 与 FR-PROJ-04 状态过滤范围不一致

- **位置**:spec.md FR-MEMBER-03、FR-PROJ-04;spec-draft FR-MEMBER-03(ProjectUtils.java:47-58)、FR-PROJ-04
- **现象**:FR-MEMBER-03 批量变更"查询该部门下状态 30/31/32 且指定旧成员的项目"。FR-PROJ-04 中"服务经理在状态 30/32/34 时指定项目经理""项目经理在状态 30/32/34 时更新渠道"。批量变更查 30/31/32,但指定项目经理操作在 30/32/34。状态 31 不在指定项目经理范围,状态 34 不在批量变更范围。
- **候选解释**:
  1. 批量变更范围(30/31/32)是"有服务经理但可能无项目经理"的项目,34 已填写渠道不再批量变更;
  2. 文档描述有误,两处状态范围应一致;
  3. 批量变更是运维清理操作,范围故意更窄,避免影响已推进项目。
- **影响面**:FR-MEMBER-03 批量变更成员、FR-PROJ-04 项目修改分权;状态 34 项目无法批量变更成员,状态 31 项目无法指定项目经理,逻辑边界不清。
- **建议决策**:明确两处状态范围的业务原因;若为设计意图,在 FR-MEMBER-03 增加"状态 34 项目不参与批量变更因已进入渠道填写阶段"说明;若为文档错误,统一状态范围。

---

## AMB-003-13: column012 实施方式枚举缺失值 2

- **位置**:spec.md 3.1 pm_project_header.column012;spec-draft 3.1
- **现象**:3.1 column012 实施方式枚举"0=原厂直服,1=代理商自服,3=代理商集成,4=原厂集成",缺失 2。枚举出现跳号,2 的语义不明。
- **候选解释**:
  1. 2 是废弃值(如"代理商直服"已合并到其他选项);
  2. 文档遗漏,2 应有定义(如"原厂自服"或"混合实施");
  3. 2 是预留值,未启用。
- **影响面**:FR-PROJ-02 项目创建、FR-PROJ-04 项目修改(实施方式更新);若 2 仍可能出现在数据中,前端展示与校验会异常。
- **建议决策**:确认 2 的历史含义;若是废弃值,在枚举中标注"2=废弃";若应启用,补全定义;在 FR-PROJ-04 中明确"column012Readonly=-1 时可改,可选值 0/1/3/4"。

---

## AMB-003-14: instructionId(参数)vs instructionsId(字段)命名不一致

- **位置**:spec.md FR-NOTIFY-02、3.14 pm_project_instruction;spec-draft FR-NOTIFY-02、3.14
- **现象**:FR-NOTIFY-02 输入参数描述"可指定原批示 ID 表示反馈",草稿明确输入参数为 instructionId(>0 表示对已有批示的反馈)。但 3.14 数据契约字段名为 instructionsId(复数 s)。参数名与字段名命名不一致。
- **候选解释**:
  1. 参数名 instructionId 是前端传入名,字段名 instructionsId 是数据库列名,二者通过映射转换;
  2. 文档笔误,二者应一致;
  3. instructionId 是逻辑参数,instructionsId 是物理字段,语义相同。
- **影响面**:FR-NOTIFY-02 保存批示(反馈关联)、前后端接口契约;命名不一致易导致字段映射错误。
- **建议决策**:统一命名;建议字段名与参数名均为 instructionsId(与表字段一致),或在 FR-NOTIFY-02 明确"instructionId 参数映射到 instructionsId 字段"。

---

## AMB-003-15: FR-PROJ-04 "工程管理部未指定服务经理→失效项目"语义疑义

- **位置**:spec.md FR-PROJ-04;spec-draft FR-PROJ-04(ProjectAction.java:791-792)
- **现象**:FR-PROJ-04 描述"工程管理部且未指定服务经理时失效项目(invalidProject)"。失效项目(effectiveTo=NOW)等同于软删除项目,但此操作发生在"项目修改"流程中。修改项目却导致项目被失效删除,语义反常。
- **候选解释**:
  1. 这是"撤销立项"能力,工程管理部发现未指定服务经理时取消项目;
  2. "失效项目"实际是"失效本次修改"而非删除项目,文档表述错误;
  3. 是某种状态回退(如回到 21 创建中),而非 effectiveTo 失效。
- **影响面**:FR-PROJ-04 项目修改分权、NFR-CON-02 软删除;误判会导致项目被意外删除。
- **建议决策**:核对 invalidProject 操作的实际语义;若是撤销立项,在 FR-PROJ-04 中明确"工程管理部未指定服务经理视为撤销立项,设置 effectiveTo 失效";若是其他操作,修正描述。

---

## AMB-003-16: 闭环条件"安装数量=发货数量"中"安装数量"来源未明确

- **位置**:spec.md FR-PROJ-04、SC-008、Edge Cases;spec-draft FR-PROJ-04(ProjectAction.java:732-779)
- **现象**:SC-008 闭环条件"安装数量=发货数量"。但"安装数量"的字段来源未在数据契约中定义。pm_project_shipment 有 installAddress 字段,pm_project_state 有 shipmentState,pm_project_product_line 有 deliverQuantity/openQuantity,均非"安装数量"。
- **候选解释**:
  1. 安装数量 = pm_project_shipment 中 installAddress 非空的记录数(已安装设备数);
  2. 安装数量 = pm_project_product_line 的某字段(未列出);
  3. 安装数量 = 发货数量 - 未安装数量,来自外部系统同步。
- **影响面**:SC-008 闭环条件校验、FR-PROJ-04 闭环申请;来源不明导致闭环判断逻辑无法实现。
- **建议决策**:明确"安装数量"的计算口径;若是 pm_project_shipment.installAddress 非空计数,在 SC-008 补充说明;若来自新字段,补全数据契约。

---

## AMB-003-17: 验货超期阈值 FR-INSPECT-04 未包含"安装(44)"节点,但 Assumptions 提到

- **位置**:spec.md FR-INSPECT-04、Assumptions;spec-draft FR-INSPECT-04(ProjectInspectionMailer.java:60-65/568-617)
- **现象**:FR-INSPECT-04 描述"查询所有需验货项目(到货验收/初验/终验的计划与实际时间)",仅含三节点。但 Assumptions 验货超期阈值列出"到货验收后 2 月、安装 5 月、初验 9 月、终验按初验后超期计算",包含"安装(44)"。spec-draft 阈值描述"43_after=2月,44=5月,45=9月,46=-1"也含安装。FR 与 Assumptions 节点不一致。
- **候选解释**:
  1. FR-INSPECT-04 文档遗漏"安装"节点,实际定时任务扫描四节点;
  2. Assumptions 阈值描述冗余,安装节点不发送提醒,仅用于计算;
  3. 安装节点超期提醒仅在特定条件下触发。
- **影响面**:FR-INSPECT-04 验货状态定时提醒、SC-016 验货超期提醒;节点遗漏会导致安装超期项目不发送提醒。
- **建议决策**:统一 FR-INSPECT-04 与 Assumptions 的节点列表;若安装节点参与超期计算,在 FR-INSPECT-04 补充"到货验收/安装/初验/终验"四节点;若仅三节点,修正 Assumptions 阈值描述。

---

## AMB-003-18: customInfo JSON 字段在主表与软版本表语义不同

- **位置**:spec.md 3.1 pm_project_header.customInfo、3.17 pm_project_soft_version.customInfo、NFR-CON-06;spec-draft 3.1/3.17
- **现象**:3.1 customInfo(自定义信息:服务经理/项目经理/项目经理B/销售编码等)业务不变量"增量合并更新",NFR-CON-06 明确"JSON_MERGE_PATCH 增量更新"。3.17 customInfo(自定义信息)无合并说明,分级 I(内部字段)。同名字段在不同表中语义与更新策略不同。
- **候选解释**:
  1. 软版本表 customInfo 是设备级自定义信息,每次更新整条覆盖(非增量合并);
  2. 软版本表 customInfo 也应增量合并,文档遗漏;
  3. 软版本表 customInfo 是占位字段,实际未使用。
- **影响面**:FR-SOFT-02 更新软件版本、NFR-CON-06 customInfo 合并;若误用增量合并,可能丢失软版本自定义信息。
- **建议决策**:明确 3.17 customInfo 的更新策略;若是整条覆盖,在 NFR-CON-06 中明确"仅 pm_project_header.customInfo 增量合并,pm_project_soft_version.customInfo 整条覆盖";若也增量合并,补充说明。

---

## AMB-003-19: 安全标识 validateFlag = MD5("success") 硬编码疑义

- **位置**:spec.md NFR-SEC-03、SC-003;spec-draft 4.1 NFR-SEC-03(Project.java:149-150/498-503)
- **现象**:NFR-SEC-03 描述"项目操作需校验 validateFlag(MD5('success'))"。MD5("success") 是固定值(如某 32 位 hex),作为安全标识等同硬编码,无实际安全意义。SC-003 要求"未通过校验的操作 0 发生",但校验值固定可被轻易绕过。
- **候选解释**:
  1. validateFlag 是防误操作的二次确认标记,非安全防护(语义为"用户已确认 success");
  2. 实际校验逻辑更复杂,MD5("success") 只是其中一环;
  3. 是历史遗留的弱安全实现,应升级为 token/会话校验。
- **影响面**:SC-003 安全标识校验、所有项目操作的安全防护;若为弱安全,SC-003 的"0 发生"承诺不可信。
- **建议决策**:明确 validateFlag 的安全定位;若是防误操作确认,在 NFR-SEC-03 中改为"防误操作确认标记"而非"安全标识";若需真实安全,升级为基于会话/权限的校验。

---

## AMB-003-20: mergeBranchMark 合并/拆分标记值未定义

- **位置**:spec.md 3.5 pm_project_group_relationship.mergeBranchMark、FR-CONTRACT-02、FR-CONTRACT-03;spec-draft 3.5
- **现象**:3.5 mergeBranchMark(合并/拆分标记)分级 I,但枚举值未定义。FR-CONTRACT-02 合同合并与 FR-CONTRACT-03 项目拆分都会写入该标记,但具体取值(合并=?拆分=?)未说明。
- **候选解释**:
  1. mergeBranchMark = "merge"/"branch" 字符串;
  2. mergeBranchMark = 0/1 数值(0=合并,1=拆分);
  3. mergeBranchMark = 项目编码或合同号派生值,非固定枚举。
- **影响面**:FR-CONTRACT-02 合同合并、FR-CONTRACT-03 项目拆分;标记值不明导致合并/拆分历史无法区分,影响数据追溯。
- **建议决策**:明确 mergeBranchMark 枚举值;建议"MERGE=合并,BRANCH=拆分"或"0=合并,1=拆分",并在 FR-CONTRACT-02/03 中说明写入值。

---

## AMB-003-21: pm_project_state 无主键,每项目状态记录数不明

- **位置**:spec.md 3.7 pm_project_state;spec-draft 3.7
- **现象**:3.7 pm_project_state 字段表无主键 id,仅以 projectId 作为项目关联。但一个项目可能有多个 projectPlanState(到货验收/安装/初验/终验/闭环)记录,还是每个项目只有一条汇总状态记录?数据契约未明确。
- **候选解释**:
  1. 每项目仅一条状态记录,projectId 为唯一键(逻辑主键);
  2. 每项目多条记录,每个 projectPlanState 一条,复合键 (projectId, projectPlanState);
  3. 状态记录随时间累积,需 effectiveFrom/effectiveTo 软删除(但表中未列出)。
- **影响面**:FR-PROJ-04 项目状态查询、FR-MEMBER-05 更新实施状态、闭环条件计算;记录数不明导致状态查询与更新逻辑无法确定。
- **建议决策**:确认表结构与查询逻辑;若每项目一条,声明 projectId 为唯一逻辑键;若多条,补全复合主键与 effectiveFrom/effectiveTo 软删除字段。

---

## AMB-003-22: projectCode -后缀规则与 chContractNo -C 后缀关系不明

- **位置**:spec.md 3.1 pm_project_header.projectCode、3.10 pm_project_shipment.chContractNo、Edge Cases;spec-draft 3.1/3.10
- **现象**:3.1 projectCode(可含 -后缀,后缀用于区分)业务不变量"项目组内可重复,全串唯一"。3.10 chContractNo(串货合同号,可能含 -C 后缀)。Edge Cases 提到"总代借货转移时合同号加 -C 后缀"。projectCode 的 -后缀与 chContractNo 的 -C 后缀是否同一规则?-后缀的完整取值集合未定义。
- **候选解释**:
  1. projectCode -后缀即 -C(总代借货转移专用),二者同源;
  2. projectCode 支持多种 -后缀(如 -C 串货、-B 拆分等),-C 是其中之一;
  3. projectCode 后缀是项目组内区分用,chContractNo 后缀是转移合同关系用,二者独立。
- **影响面**:FR-PROJ-09 设备转移、FR-NOTIFY-04 问题工单查询(去 -后缀按项目编号查询)、FR-NOTIFY-05 License 授权查询(构造合同号列表含 projectCode);后缀规则不明导致去后缀逻辑与合同号构造可能出错。
- **建议决策**:明确 projectCode 后缀的完整规则与取值集合;若 -C 是唯一后缀,在 3.1 说明"后缀 = -C,仅用于总代借货转移场景";若多种,列出枚举;明确去后缀的逻辑(如按 "-" 分割取首段)。

---

## 附录:歧义分类统计

| 类别 | 数量 | 编号 |
|---|---|---|
| 表结构/主键缺失 | 5 | AMB-003-01/02/03/04/21 |
| 字段语义冲突/缺失 | 5 | AMB-003-06/08/13/16/18 |
| 枚举/编码体系不明 | 4 | AMB-003-07/10/11/20 |
| 状态/流程逻辑不一致 | 2 | AMB-003-12/15 |
| 软删除原则冲突 | 1 | AMB-003-09 |
| 外部契约未定义 | 1 | AMB-003-05 |
| 命名不一致 | 1 | AMB-003-14 |
| 安全设计疑义 | 1 | AMB-003-19 |
| 业务规则不完整 | 2 | AMB-003-17/22 |

## 附录:三比对来源映射

| 比对维度 | 涉及歧义 |
|---|---|
| 代码 vs 文档(spec-draft 证据与 spec.md 表述差异) | AMB-003-08/17 |
| 代码 vs 代码(证据引用内部矛盾) | AMB-003-06/07/11/12 |
| spec 内部 [待澄清] 展开 | AMB-003-01/02/03/04/05 |
| spec 内部 FR/NFR/SC 互相矛盾 | AMB-003-09/10/13/15/18/19 |
| spec 内部字段表不完整 | AMB-003-14/16/20/21/22 |
