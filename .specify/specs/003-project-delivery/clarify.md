# 003-project-delivery 域歧义澄清记录(Clarify)

> 日期: 2026-07-09
> 歧义总数: 22(AMB-003-01 ~ AMB-003-22)
> 决策原则:采纳 ambiguities.md 中各条目"建议决策"作为最终决策,进行正向固化。
> 字段说明:每条含【决策结论】【依据】【影响范围】【回滚提示】四字段。

---

## AMB-003-01: pm_project 与 pm_project_header 表名/对象关系不明

- **决策结论**:在数据契约 3.1 增加显式声明"pm_project 为 pm_project_header 的同义词/视图,语义等价";要求实现层统一使用 pm_project_header 表名,旧语句迁移至 pm_project_header;实现前用 DDL 查询确认二者关系。
- **依据**:Assumptions 已"假定二者指向同一项目主数据语义";显式声明可消除实现层歧义,统一表名有利于维护。
- **影响范围**:NFR-CON-02 软删除模式、FR-PROJ-07 项目清理、所有 effectiveTo 失效操作的实现层;3.1 pm_project_header 数据契约声明;Assumptions。
- **回滚提示**:若 DDL 确认 pm_project 为含过滤条件的受限视图(非全量等价),需重新评估失效操作是否触达全部历史记录,回滚为分离表名处理。

---

## AMB-003-02: pm_project_contract 缺少主键 id、projectId、effectiveTo 字段

- **决策结论**:补全 pm_project_contract 字段表,新增 id(自增主键)、projectId(关联 pm_project_header)、effectiveFrom、effectiveTo 软删除字段,使其符合 NFR-CON-02 软删除模式。
- **依据**:反馈语义与合同软删除可追溯性要求稳定唯一标识;NFR-CON-02 声称"合同等均采用 effectiveTo 失效模式",字段表应与之对齐。
- **影响范围**:3.3 pm_project_contract 数据契约;FR-CONTRACT-01/02 合同合并预检与合并;FR-PROJ-09 设备转移插入转移合同关系;SC-007 历史数据可追溯。
- **回滚提示**:若 DDL 确认表确实无软删除字段且业务不允许补加,需在 NFR-CON-02 中明确豁免 pm_project_contract,并接受合同关系不可追溯。

---

## AMB-003-03: pm_project_instruction 是否含自增主键 id 不明

- **决策结论**:确认表含自增 id 主键并在数据契约补全;FR-NOTIFY-02 明确"instructionId 参数对应 pm_project_instruction.id"。
- **依据**:反馈语义(dataType=1, instructionsId 指向原批示)要求原批示有稳定唯一标识,自增主键 id 是最合理实现;INSERT 未显式写入符合数据库自增行为。
- **影响范围**:3.14 pm_project_instruction 数据契约;FR-NOTIFY-02 保存批示(反馈时关联原批示);FR-NOTIFY-03 查询批示列表。
- **回滚提示**:若 DDL 确认表无自增 id,需重新设计反馈关联机制(如改用复合键或时间戳派生 ID),并同步修订 FR-NOTIFY-02。

---

## AMB-003-04: pm_project_spot_check_ignore_item 无主键 + truncate 覆盖式导入与软删除原则矛盾

- **决策结论**:明确该表为配置类豁免软删除,在 NFR-CON-02(SC-007)增加豁免清单;补加 createTime、createBy 字段以记录最近一次导入信息;保留 truncate 覆盖式导入语义。
- **依据**:该表为全局配置表,语义上无需保留历史;配置类豁免符合业界惯例;补加审计字段可记录最近导入人时,部分满足可追溯性。
- **影响范围**:3.19 pm_project_spot_check_ignore_item 数据契约;FR-INSPECT-03 验货忽略项导入;SC-007 软删除模式(增加豁免清单);Edge Cases。
- **回滚提示**:若业务要求忽略项变更全程可追溯,需改为按 itemCode upsert + 失效旧记录的软删除模式,补全 effectiveFrom/effectiveTo。

---

## AMB-003-05: ITR 工单表与 License 授权表字段未定义(SELECT * 查询)

- **决策结论**:[暂定决策] 查询 GainDataFromITR/GainDataFromLicense 同步任务定义补全字段表;将 SELECT * 改为显式字段列表,仅声明本域实际消费的字段为契约字段(分级 C),其余为外部内部字段。在补全前,spec 中标注为"[暂定决策:字段待同步任务定义补全,本域消费 projectCode/contractNo/barCode 等关键字段]"。
- **依据**:SELECT * 违反契约稳定性原则;显式字段列表可隔离外部表变更对本域查询的破坏;完整字段需查询外部同步模块,本域不应承担其定义。
- **影响范围**:3.22 pm_project_incident_table_from_itr;3.23 pm_project_license_info_from_license;FR-NOTIFY-04 问题工单查询;FR-NOTIFY-05 License 授权查询;Assumptions。
- **回滚提示**:若同步任务定义无法获取,保持 SELECT * 但在契约中明确仅关键字段为稳定契约,其余字段变更不视为本域 breaking change。

---

## AMB-003-06: column001 字段语义冲突(办事处编码 vs 利润中心)

- **决策结论**:确认 column001 为语义复用字段,在 3.1 字段说明中明确"column001 = 办事处编码(officeCode);salesType=14(总代借货)时作为 profitCenter 利润中心使用";实现层按 salesType 分支处理。
- **依据**:同一物理字段承担两种语义是历史实现,确认复用关系并在契约显式声明可避免实现层误判;无需新增独立字段。
- **影响范围**:3.1 pm_project_header.column001;FR-SHIP-01 发货查询;FR-MEMBER-04 安装地址保存;FR-PROJ-09 设备转移;FR-SOFT-01 软件版本查询;Edge Cases。
- **回滚提示**:若代码确认存在独立 profitCenter 字段,需补全数据契约并移除 column001 的利润中心语义说明。

---

## AMB-003-07: transferFlag 语义矛盾(0=转销,1=退货,!=1 排除)

- **决策结论**:重新定义 transferFlag 枚举为 0=未转移(默认)、1=已转出(转销)、2=已转出(退货);排除条件改为"转移标记≠0"(即排除所有已转出设备);同步修正 3.10、Edge Cases、SC-021 描述。
- **依据**:原枚举"0=转销,1=退货,!=1 排除"逻辑矛盾(转销应保留可查却会被排除);三值枚举(未转移/转销/退货)与"默认排除已转出、历史查询包含转销退货"语义自洽。
- **影响范围**:3.10 pm_project_shipment.transferFlag;FR-SHIP-01 发货查询;FR-PROJ-09 设备转移;SC-021 历史设备保留;Edge Cases。
- **回滚提示**:若代码确认 transferFlag=1 表示"已转出(退货)"且 !=1 排除即排除退货设备(转销保留),则恢复原枚举,仅需修正文档表述歧义。

---

## AMB-003-08: projectRefreshTime 字段在 NFR 引用但数据契约缺失

- **决策结论**:在 3.1 pm_project_header 补充 projectRefreshTime(datetime,可空,项目最后刷新时间,默认 NOW())字段;确认其为独立字段而非复用 updateTime。
- **依据**:代码证据(ProjectMemberAction.java、ProjectWeeklyAction.java)明确更新 projectRefreshTime;字段缺失会导致 SC-019 活跃度统计无法实现;独立字段避免与 updateTime 语义混淆。
- **影响范围**:3.1 pm_project_header 数据契约;SC-019 项目最后刷新时间;FR-MEMBER-01/02/04;FR-WEEKLY-03/04;NFR-OBS-01。
- **回滚提示**:若 DDL 确认 projectRefreshTime 复用 updateTime,需在 3.1 说明"projectRefreshTime 语义复用 updateTime 字段",并移除独立字段声明。

---

## AMB-003-09: 物理删除/truncate 与 NFR-CON-02 "100% 软删除"原则多处冲突

- **决策结论**:将 SC-007(NFR-CON-02)修改为"关键业务数据(项目主表、成员、合同、交付件、相关方)采用软删除;清理类批量操作(项目清理 FR-PROJ-07)、配置类(忽略项 FR-INSPECT-03)、临时附件(周报附件 FR-FILE-03)允许物理删除",并明确豁免清单。
- **依据**:NFR-CON-02 原"100% 软删除"描述过于绝对,与多个 FR 直接冲突;分类豁免符合实际业务需求(清理/配置/临时附件无需历史追溯)。
- **影响范围**:SC-007 软删除模式;FR-PROJ-07 项目清理;FR-INSPECT-03 忽略项导入;FR-FILE-03 周报附件删除;NFR-CON-02。
- **回滚提示**:若业务要求所有数据全程可追溯,需将清理类/配置类/附件类统一改为软删除,并提供清理作业定期物理清除已失效数据。

---

## AMB-003-10: projectState 状态 21(创建中)与 30(已创建)关系不明,状态机未包含 21

- **决策结论**:明确 21 为创建流程中的中间态(参数为空时返回创建页阶段),提交后转为 30;在 SC-006 状态机中补充"21→30"迁移;在 FR-PROJ-01 状态扩展逻辑中说明 21 不参与列表查询扩展(扩展仍为 30/31/32)。
- **依据**:21=创建中与 30=已创建语义连续,中间态解释最自洽;补充迁移确保状态机完整性;列表查询扩展不包含 21 避免未提交项目干扰。
- **影响范围**:3.1 pm_project_header.projectState;SC-006 项目状态机;FR-PROJ-02 项目创建;FR-PROJ-01 列表查询;Assumptions。
- **回滚提示**:若代码确认 21 为废弃状态,需从枚举中移除并标记 D(废弃),回滚状态机补充。

---

## AMB-003-11: taskTypeId 与 projectPlanState 两套编码体系关系不明

- **决策结论**:在数据契约中增加两套编码映射表(taskTypeId 30↔projectPlanState 43 到货验收、50↔44 安装、60↔45 初验、61↔46 终验);taskTypeId 补充 50=安装;统一 FR-INSPECT-04 与 Assumptions 的节点描述为四节点(到货验收/安装/初验/终验)。
- **依据**:两套编码均描述验货节点,映射表消除数值差异;taskTypeId 缺少安装对应值导致 FR-INSPECT-04 节点不一致,补充 50 对齐;Assumptions 阈值已含安装(44=5月),统一为四节点。
- **影响范围**:3.7 pm_project_state.projectPlanState;3.8 pm_project_task.taskTypeId;FR-INSPECT-04 验货超期计算;FR-PROJ-05 工程计划编辑;Assumptions 验货超期阈值。
- **回滚提示**:若代码确认 taskTypeId 不含安装(50),需在 FR-INSPECT-04 明确安装节点仅由 projectPlanState(44)承担,taskTypeId 不参与安装超期计算。

---

## AMB-003-12: FR-MEMBER-03 与 FR-PROJ-04 状态过滤范围不一致

- **决策结论**:确认为设计意图,在 FR-MEMBER-03 增加"状态 34 项目不参与批量变更,因已进入渠道填写阶段,批量变更可能影响已推进项目"说明;保持两处状态范围不一致(批量变更 30/31/32,指定项目经理 30/32/34)。
- **依据**:批量变更是运维清理操作,范围故意更窄(30/31/32,有服务经理但可能无项目经理),避免影响已填写渠道(34)的已推进项目;两处状态范围差异符合业务边界。
- **影响范围**:FR-MEMBER-03 批量变更服务/项目经理;FR-PROJ-04 项目修改分权。
- **回滚提示**:若业务确认两处状态范围应一致,需统一为 30/31/32/34(批量变更也覆盖 34)或保持 30/31/32(指定项目经理也限制在 30/31/32)。

---

## AMB-003-13: column012 实施方式枚举缺失值 2

- **决策结论**:确认 2 为废弃值,在 3.1 枚举中标注"2=废弃(原代理商直服,已合并)";在 FR-PROJ-04 中明确"column012Readonly=-1 时可改,可选值 0/1/3/4(2 已废弃不可选)"。
- **依据**:枚举跳号(0/1/3/4)暗示 2 被废弃;标注废弃值可避免前端展示异常,同时保留历史数据兼容性。
- **影响范围**:3.1 pm_project_header.column012;FR-PROJ-02 项目创建;FR-PROJ-04 项目修改。
- **回滚提示**:若业务确认 2 应启用(如"混合实施"),需补全 2 的定义并开放可选值。

---

## AMB-003-14: instructionId(参数)vs instructionsId(字段)命名不一致

- **决策结论**:统一命名,字段名与参数名均为 instructionsId(与表字段一致);在 FR-NOTIFY-02 明确"instructionId 前端参数映射到 instructionsId 数据库字段"。
- **依据**:命名一致可避免字段映射错误;以数据库字段名 instructionsId 为准,前端参数通过映射文档说明。
- **影响范围**:3.14 pm_project_instruction.instructionsId;FR-NOTIFY-02 保存批示;前后端接口契约。
- **回滚提示**:若前端契约已固定为 instructionId 无法变更,保持参数名 instructionId,在 FR-NOTIFY-02 显式声明映射关系即可。

---

## AMB-003-15: FR-PROJ-04 "工程管理部未指定服务经理→失效项目"语义疑义

- **决策结论**:核对确认为"撤销立项"能力,在 FR-PROJ-04 中明确"工程管理部未指定服务经理视为撤销立项,设置 effectiveTo=NOW 失效项目"。
- **依据**:invalidProject 操作设置 effectiveTo=NOW 等同软删除,语义为撤销立项而非普通修改;显式声明可消除"修改导致删除"的语义反常。
- **影响范围**:FR-PROJ-04 项目修改分权;NFR-CON-02 软删除;SC-007。
- **回滚提示**:若代码确认 invalidProject 实际为状态回退(如回到 21 创建中)而非 effectiveTo 失效,需修正 FR-PROJ-04 描述为状态回退语义。

---

## AMB-003-16: 闭环条件"安装数量=发货数量"中"安装数量"来源未明确

- **决策结论**:明确"安装数量 = pm_project_shipment 中 installAddress 非空的记录数(已安装设备数)";在 SC-008 补充说明计算口径。
- **依据**:pm_project_shipment.installAddress 非空表示设备已安装,计数即为安装数量;与发货数量(deliverQuantity 或 shipment 记录数)对比可校验闭环条件;无需新增字段。
- **影响范围**:SC-008 闭环条件校验;FR-PROJ-04 闭环申请;Edge Cases 闭环条件不满足。
- **回滚提示**:若代码确认安装数量来自 pm_project_product_line 的某字段或外部系统同步,需补全数据契约字段并修订 SC-008 计算口径。

---

## AMB-003-17: 验货超期阈值 FR-INSPECT-04 未包含"安装(44)"节点,但 Assumptions 提到

- **决策结论**:统一 FR-INSPECT-04 与 Assumptions 的节点列表为四节点(到货验收/安装/初验/终验);在 FR-INSPECT-04 补充"安装"节点参与超期计算与提醒。
- **依据**:Assumptions 阈值描述(43_after=2月,44=5月,45=9月,46=-1)已含安装;定时任务实际扫描四节点;FR 遗漏安装会导致安装超期项目不发送提醒。
- **影响范围**:FR-INSPECT-04 验货状态定时提醒;SC-016 验货超期提醒;Assumptions 验货超期阈值。
- **回滚提示**:若代码确认安装节点不发送提醒(仅用于计算),需修正 Assumptions 阈值描述,移除安装(44)或标注"仅计算不提醒"。

---

## AMB-003-18: customInfo JSON 字段在主表与软版本表语义不同

- **决策结论**:明确 3.17 pm_project_soft_version.customInfo 为设备级自定义信息,每次更新整条覆盖(非增量合并);在 NFR-CON-06(SC-011)中明确"仅 pm_project_header.customInfo 增量合并(JSON_MERGE_PATCH),pm_project_soft_version.customInfo 整条覆盖"。
- **依据**:软版本表 customInfo 是设备级信息,每次版本变更整体覆盖符合版本快照语义;主表 customInfo 是项目级聚合信息,增量合并避免历史字段丢失;两表语义不同应区分策略。
- **影响范围**:3.1 pm_project_header.customInfo;3.17 pm_project_soft_version.customInfo;NFR-CON-06(SC-011);FR-SOFT-02 更新软件版本。
- **回滚提示**:若代码确认软版本表 customInfo 也增量合并,需补充说明并统一两表更新策略。

---

## AMB-003-19: 安全标识 validateFlag = MD5("success") 硬编码疑义

- **决策结论**:明确 validateFlag 为防误操作确认标记(非安全防护),在 NFR-SEC-03(SC-003)中改为"防误操作确认标记,语义为用户已确认 success";不升级为 token/会话校验(保持现状)。
- **依据**:MD5("success") 是固定值,作为安全标识无实际安全意义,但作为防误操作二次确认标记语义合理;升级为 token/会话校验超出当前逆向范围,保持现状避免过度设计。
- **影响范围**:NFR-SEC-03(SC-003)安全标识校验;所有项目操作的安全防护描述。
- **回滚提示**:若业务要求真实安全防护,需升级 validateFlag 为基于会话/权限的动态 token 校验,并修订 SC-003 的"0 发生"承诺。

---

## AMB-003-20: mergeBranchMark 合并/拆分标记值未定义

- **决策结论**:明确 mergeBranchMark 枚举值为字符串"MERGE=合并"、"BRANCH=拆分";在 FR-CONTRACT-02 合同合并写入"MERGE",FR-CONTRACT-03 项目拆分写入"BRANCH"。
- **依据**:字符串枚举语义清晰,易于阅读与追溯;数值枚举(0/1)语义不直观;FR-CONTRACT-02/03 写入值明确可区分合并/拆分历史。
- **影响范围**:3.5 pm_project_group_relationship.mergeBranchMark;FR-CONTRACT-02 合同合并;FR-CONTRACT-03 项目拆分。
- **回滚提示**:若代码确认 mergeBranchMark 为数值(0/1)或派生值(项目编码),需修订枚举为实际取值。

---

## AMB-003-21: pm_project_state 无主键,每项目状态记录数不明

- **决策结论**:确认每项目仅一条状态记录,声明 projectId 为唯一逻辑键;projectPlanState 为汇总状态字段(取最新节点),非每节点一条记录。
- **依据**:projectPlanState 枚举(43/44/45/46/48)表示项目当前所处计划阶段,每项目仅一个当前阶段,汇总为一条状态记录最合理;projectId 唯一键简化查询与更新逻辑。
- **影响范围**:3.7 pm_project_state 数据契约;FR-PROJ-04 项目状态查询;FR-MEMBER-05 更新实施状态;闭环条件计算。
- **回滚提示**:若 DDL 确认每项目多条记录(每个 projectPlanState 一条),需补全复合主键 (projectId, projectPlanState) 与 effectiveFrom/effectiveTo 软删除字段。

---

## AMB-003-22: projectCode -后缀规则与 chContractNo -C 后缀关系不明

- **决策结论**:明确 projectCode 后缀规则为"按 "-" 分割取首段为基础项目编码,后缀标识派生关系;-C 表示总代借货转移派生(chContractNo 同源),-B 表示项目拆分派生";去后缀逻辑为"按 "-" 分割取首段"。
- **依据**:chContractNo 的 -C 后缀用于总代借货转移,projectCode 的 -后缀用于项目组内区分;二者同源(-C 转移),拆分场景补充 -B;去后缀逻辑统一为按 "-" 分割取首段,支持 FR-NOTIFY-04/05 查询。
- **影响范围**:3.1 pm_project_header.projectCode;3.10 pm_project_shipment.chContractNo;FR-PROJ-09 设备转移;FR-NOTIFY-04 问题工单查询;FR-NOTIFY-05 License 授权查询;Edge Cases。
- **回滚提示**:若代码确认 -C 是 projectCode 唯一后缀(无 -B 等),需修订规则为"后缀 = -C,仅用于总代借货转移场景",并简化去后缀逻辑。

---

## 附录:决策统计

| 决策类型 | 数量 | 编号 |
|---|---|---|
| 补全字段/主键 | 6 | AMB-003-02/03/05/08/16/21 |
| 明确枚举/编码 | 6 | AMB-003-07/11/13/20/22/10 |
| 明确语义/命名 | 4 | AMB-003-01/06/14/18 |
| 调整原则/豁免 | 2 | AMB-003-04/09 |
| 明确业务规则 | 3 | AMB-003-12/15/17 |
| 安全定位调整 | 1 | AMB-003-19 |

| 暂定决策(需实现层 DDL/代码确认) | 数量 | 编号 |
|---|---|---|
| 标注 [暂定决策] | 1 | AMB-003-05 |
| 含回滚条件 | 22 | 全部(均提供回滚提示) |
