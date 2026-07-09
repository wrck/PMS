# 005-warranty-callback 域歧义清单(Ambiguities)
> 日期: 2026-07-09
> 歧义总数: 18
> 比对类型:A=代码 vs 文档;B=代码 vs 代码;C=spec 内部 [待澄清] 展开

---

## 一、spec 内部 [待澄清] 展开(C 类)

### AMB-005-01: 问卷模板三表表名为推断,未经 DML 确认
- **位置**: spec.md 3.7 / 3.8 / 3.9;spec-draft.md 3.7-3.9 证据列;数据契约注释"表名为推断,见 [待澄清]"
- **现象**: `pm_closed_loop_quesnaire` / `pm_closed_loop_quesnaire_line` / `pm_closed_loop_quesnaire_opt` 三表表名仅在 Bean(`PmClosedLoopQuesnaire*.java`)与 Action 中被引用,未在任何 sql-map 的 DML(insert/update/delete)中直接出现,spec 标注为推断名
- **候选解释**: (a) 表名确为推断,需查 DDL 脚本确认真实名;(b) 表名由 iBatis 别名或 resultMap 在其他配置文件中映射,未在已读 sql-map 中暴露
- **影响面**: 数据契约准确性、后续正向工程 DDL 生成、DATA-REUSE-01 复用判断
- **建议决策**: 检索 `PmClosedLoopQuesnaire*` 对应的 iBatis sql-map 配置或数据库 DDL 脚本,确认精确表名后回写 spec

### AMB-005-02: 两套回访体系并存关系与数据归并语义不明
- **位置**: spec.md FR-2.2 vs FR-2.3;3.2 `pm_cl_callback` vs 3.4 `pm_cl_evaluation_header`(evaluationType=3);Edge Cases
- **现象**: 独立回访工作流(CallBack.bpmn,`pm_cl_callback`)与 PM 闭环内嵌回访节点(PmClosedLoop.bpmn,evaluationType=3)两套回访数据并存,是否同时服役、是否需迁移、字段映射关系均未明确
- **候选解释**: (a) 两套并存服务于不同业务场景(独立回访申请 vs 闭环内回访);(b) 历史演进遗留,独立回访工作流逐步被闭环流程吸收替代
- **影响面**: 回访数据归并与统计口径、同一项目重复回访风险、维护成本
- **建议决策**: 明确两套体系的业务边界;若并存则定义数据归并策略,若废弃则标注 deprecation 并规划迁移

### AMB-005-03: 续保意向 renewalIntention 值 3 语义不明
- **位置**: spec.md 3.1 `renewalIntention`;FR-2.1.5;FR-2.1.11 `canEdit()`;Open Questions 第 3 项
- **现象**: 字段注释枚举为 0否/1有/2待定(-1 未填写仅查询用),但 `ProjectWarrantyCallbackVO.canEdit` 注释提及"续保状态为 3 可编辑"(当前实现恒返回 false 已禁用),值 3 未在任何枚举文档中出现
- **候选解释**: (a) 值 3 为历史遗留枚举(如"已续保"),现已废弃;(b) 值 3 由外部域写入,本域只读
- **影响面**: canEdit 逻辑(虽已禁用)、续保意向查询完整性、枚举文档准确性
- **建议决策**: 检索 `renewalIntention=3` 的写入路径;若废弃则在 spec 枚举中显式标注"3=已废弃",若仍合法则补充语义

### AMB-005-04: 闭环网关 evaluationResult=2/3/-2 无常量定义
- **位置**: spec.md FR-2.3.10;`PmClosedLoopConstant.java:95,100,105`(仅定义 AGREE=1/REJECT=-1/CANTCB=-3);Open Questions 第 4 项
- **现象**: BPMN 网关分支使用 evaluationResult=2(已通过回访)、=3(服务经理与项目经理一致且通过回访)、=-2(驳回任务办理→结束),但常量类仅定义 1/-1/-3,值 2/3/-2 无对应常量,且与 FR-2.3.7 枚举(1/-1/-3)不一致
- **候选解释**: (a) 2/3/-2 为流程引擎内部中间态,由网关表达式计算得出,非业务结果;(b) 常量定义缺失,应补充
- **影响面**: 流程流转正确性、网关条件可维护性、spec 枚举完整性
- **建议决策**: 结合 PmClosedLoop.bpmn 与服务实现确认 2/3/-2 的产生路径;补充常量定义,或在 spec 中明确标注为"流程内部网关态,非评测结果枚举"

### AMB-005-05: pmClosedLoopResultType 41/42/50/51 注释存在但代码未使用
- **位置**: `PmClosedLoopAction.java:43` 字段注释;spec.md FR-2.3.3 / FR-2.3.5 / FR-2.3.19;Open Questions 第 5 项
- **现象**: 字段注释列出 30/40/41/42/50/51 六个值,但代码实际仅使用 0(流程图)/1(提交问卷)/2(完成节点)/30(回访表单)/40(闭环表单);41(回访保存草稿)/42(回访提交)/50(工程填写)/51(工程提交)从未在代码分支中出现
- **候选解释**: (a) 注释为规划/历史遗留,功能未实现或已重构;(b) 由前端传入但后端未分支处理
- **影响面**: 入口参数文档完整性、前后端契约、spec 取值表准确性
- **建议决策**: 确认 41/42/50/51 是否废弃;在 spec 中仅记录实际生效的 0/1/2/30/40 并标注注释残留

---

## 二、代码 vs 代码(B 类)

### AMB-005-06: "无法回访"结果值在两套流程中不一致(-2 vs -3)
- **位置**: spec.md FR-2.2.8(result=-2 无法回访)vs FR-2.3.7(evaluationResult=-3 无法回访);`PmClosedLoopConstant.java:105`(CL_EVALU_RESULT_CANTCB=-3)
- **现象**: 独立回访工作流(CallBack.bpmn)中"无法回访"= -2,PM 闭环流程(PmClosedLoop.bpmn)中"无法回访"= -3,同一业务语义在两套流程中取值不同
- **候选解释**: (a) 两套流程独立演进,枚举未统一;(b) -2 在独立流程中实为"驳回"而非"无法回访",spec 描述有误
- **影响面**: 跨流程结果统计、回访结果语义混淆、维护成本
- **建议决策**: 统一"无法回访"枚举值;若两套流程值域隔离则需在 spec 中显式分区标注,避免混用

### AMB-005-07: pmClosedLoopResultType=1/2 按角色重载,语义随节点变化
- **位置**: spec.md FR-2.3.3 vs FR-2.3.5;`PmClosedLoopAction.java:545,698`(=1),`:604`(=2)
- **现象**: =1 对回访人员意为"提交回访问卷并计分",对工程人员意为"提交闭环建议问卷并计分";=2 同理双义(基于已有问卷完成回访节点 vs 完成闭环节点)。同一参数值语义随当前节点角色变化
- **候选解释**: (a) 有意重载,靠当前流程节点上下文区分;(b) 应拆分为独立参数以提升可读性
- **影响面**: 接口契约可读性、测试覆盖难度、维护风险
- **建议决策**: 在 spec 中明确"=1/=2 语义依赖当前节点角色"的上下文依赖关系;建议长期拆分

### AMB-005-08: pmClosedLoopResultType=30/40 入口态与结果态复用
- **位置**: `PmClosedLoopAction.java:108,114`(入口取表单)vs `:601,742`(提交后置值跳看分页)
- **现象**: 30/40 既作为入口态(进入页面时获取回访/闭环表单)又作为提交后结果态(提交成功后置值并跳转看分页),同一值在流程不同阶段含义不同
- **候选解释**: (a) 状态机复用,靠流程位置与返回视图区分;(b) 应区分入口态与结果态
- **影响面**: 流程状态理解、前端跳转逻辑、调试可读性
- **建议决策**: 在 spec 中补充 30/40 的双态语义说明(入口取表单 / 提交后跳看分页)

### AMB-005-09: 问卷计分逻辑三处实现,存在分叉风险
- **位置**: spec-draft.md NFR-11;`CallBackAction.java:344`;`PmClosedLoopAction.java:847`;`util/QuestionnarieUtil.java:52`
- **现象**: 计分逻辑在独立回访(CallBackAction)与闭环流程(PmClosedLoopAction)各有一份实现,另有 `QuestionnarieUtil` 供维保回访复用,三处逻辑需保持一致但无强制约束
- **候选解释**: (a) 历史原因两处复制,后抽取 Util 但未完全收敛;(b) 两处逻辑存在细微差异(如答案串拼接顺序)
- **影响面**: SC-011 计分一致性(要求 100% 一致)、规则变更遗漏风险、维护成本
- **建议决策**: 统一收敛到 `QuestionnarieUtil`;在 spec 中标注双实现并增加跨流程一致性测试用例

### AMB-005-10: 邮件模板 ID 27 缺失,范围描述"20-28"具有误导性
- **位置**: spec-draft.md NFR-7("模板 ID 20-28");`PmClosedLoopConstant.java:170-205`(CL_MAIL_TEMPLATE_01..08)
- **现象**: 8 个邮件模板 ID 实际为 20,21,22,23,24,25,26,**28**(ID 27 缺失),spec-draft 描述"模板 ID 20-28"暗示连续 9 个,与实际 8 个不符
- **候选解释**: (a) ID 27 曾存在后被废弃;(b) ID 27 属于其他业务域,闭环域未使用
- **影响面**: 邮件模板配置完整性核验、文档准确性
- **建议决策**: 在 spec 中枚举实际 8 个 ID 并标注 27 缺失;排查 ID 27 归属

### AMB-005-11: 状态常量命名与语义不匹配(SUBMIT=生效)
- **位置**: `PmClosedLoopConstant.java:48`(CL_STATUS_SUBMIT=1 注释"生效状态");`:63`(CL_STATUS_SUBMITQUES=2);spec.md FR-2.3.8
- **现象**: 常量名 `SUBMIT`(提交)对应值 1 实际语义为"生效",而 `SUBMITQUES` 才是"提交问卷";spec FR-2.3.8 标注"1=生效(SUBMIT)"将矛盾命名固化。此外 FR-2.3.8 标题"问卷状态"但取值(含 2=提交问卷)与评测头 status(3.4)一致,而模板表 questionnaireStatus(3.7)无"2 提交问卷"态,FR-2.3.8 所指实体存疑
- **候选解释**: (a) 命名历史遗留,SUBMIT 实际表"提交后生效";(b) 命名错误;(c) FR-2.3.8 实为评测头 status 而非模板 questionnaireStatus
- **影响面**: 代码可读性、spec 状态枚举归属歧义
- **建议决策**: 在 spec 中以中文语义为准,标注常量名与语义的映射矛盾;明确 FR-2.3.8 归属实体(评测头 vs 模板)

### AMB-005-12: evaluationType=2"服务经理回访申请"触发时机不明
- **位置**: spec.md FR-2.3.6;`PmClosedLoopConstant.java:74`(CL_EVALU_TYPE_SM=2 注释"服务经理回访申请")
- **现象**: evaluationType=2 标注"服务经理回访申请",但 User Story 3 与 FR-2.3.2 描述服务经理节点为"审核"而非"回访申请",该类型的实际触发场景与服务经理审核节点的关系未文档化
- **候选解释**: (a) 服务经理审核即产生 type=2 评测头(命名"回访申请"指其触发后续回访);(b) type=2 为另一独立子流程节点
- **影响面**: 评测头类型语义、历史回访查询过滤条件
- **建议决策**: 结合服务实现确认 type=2 的触发条件并在 spec 补充;统一"审核"与"回访申请"的命名

---

## 三、代码 vs 文档(A 类)

### AMB-005-13: customInfo 为 null 时增量合并行为未定义
- **位置**: spec.md Edge Cases;3.1 `customInfo`;FR-2.1.10;Assumptions(JSON_MERGE_PATCH)
- **现象**: spec 假设 customInfo 使用 JSON_MERGE_PATCH 增量合并,但 customInfo 为 null 时(首次创建场景)的合并行为仅在 Edge Case 中以疑问句提及,未给出明确规则
- **候选解释**: (a) JSON_MERGE_PATCH 对 null 自动等价于整体写入;(b) 需代码特判 null 转 empty object 后合并
- **影响面**: SC-010 JSON 合并成功率、首次创建回访记录场景
- **建议决策**: 在 spec 中明确"customInfo 为 null 时增量合并等价于整体写入"的规则

### AMB-005-14: "取首份"生效模板的排序规则未定义
- **位置**: spec.md FR-2.1.4
- **现象**: 回访表单取"首份"生效的 `projectWarrantyCallback` 类型问卷模板,但"首份"的排序依据(id 升序 / 创建时间 / 生效时间)未指定
- **候选解释**: (a) 按 id 升序取首(数据库默认);(b) 按生效时间倒序取最新生效
- **影响面**: 多模板并存场景下取到的模板不确定,影响回访问卷一致性
- **建议决策**: 在 spec 中明确排序规则(建议按生效时间倒序取最新)

### AMB-005-15: questionStatus 取值未文档化
- **位置**: spec.md 3.8 `pm_closed_loop_quesnaire_line.questionStatus`
- **现象**: 题目状态 `questionStatus` 字段标注为 INTEGER 但无取值枚举文档,与模板头 questionnaireStatus(1/-1/-2)的关系不明
- **候选解释**: (a) 复用 CL_STATUS_* 枚举(1生效/-1草稿/-2失效);(b) 独立枚举(如启用/停用)
- **影响面**: 题目生命周期管理、查询过滤条件
- **建议决策**: 补充 questionStatus 取值枚举,或标注"复用模板头状态枚举"

### AMB-005-16: quesnaireState 在 pm_cl_callback_quesnaire 仅"1已提交",与 pm_project_warranty_callback 不一致
- **位置**: spec.md 3.3 `quesnaireState`(仅 1已提交)vs 3.1 `quesnaireState`(-1草稿/1已提交)
- **现象**: 3.3 回访任务问卷关联表的 quesnaireState 仅标注"1已提交",无草稿态;3.1 维保回访记录的 quesnaireState 含 -1草稿/1已提交。两表同一字段名取值域不一致
- **候选解释**: (a) 关联表仅记录已提交问卷,草稿态不建立关联;(b) 文档遗漏草稿态
- **影响面**: 草稿问卷是否写入关联表、查询完整性、跨表状态对齐
- **建议决策**: 确认草稿态是否写入 `pm_cl_callback_quesnaire`;在 spec 中标注取值域差异原因

### AMB-005-17: FR-2.2.5"每次保存重新生成"与 FR-2.2.6"唯一确定"存在张力
- **位置**: spec.md FR-2.2.5(每次保存/提交都重新生成一份问卷结果)vs FR-2.2.6(callBackId+taskId 唯一确定一份 CallBackQuesnaire)
- **现象**: FR-2.2.5 表述每次保存都重新生成新问卷结果数据,FR-2.2.6 表述 callBackId+taskId 唯一确定一份,二者在"同一任务重复保存"场景下的行为(新建 vs 覆盖)存在张力
- **候选解释**: (a) 每次保存按 callBackId+taskId 做 upsert(覆盖旧结果);(b) 每次保存插入新版本(历史保留)
- **影响面**: 问卷结果历史保留策略、存储增长、重复保存的数据一致性
- **建议决策**: 明确重复保存为 upsert(覆盖)还是版本化新增;在 FR-2.2.5/2.2.6 中消解表述冲突

### AMB-005-18: canEdit"接听状态以-开头"取值来源不明
- **位置**: spec.md FR-2.1.11
- **现象**: canEdit 原意含"接听状态以 - 开头时可编辑",但接听状态取自基础数据 `projectWarrantyCallback_phoneAnswerState`,哪些枚举值以"-"开头未文档化,且该逻辑已被注释禁用(恒返回 false)
- **候选解释**: (a) 基础数据中存在以"-"前缀标记的特殊状态(如"-无人接听");(b) 描述有误,"-"为分隔符而非前缀
- **影响面**: canEdit 逻辑(虽已禁用)、接听状态枚举完整性、未来恢复编辑功能的可行性
- **建议决策**: 确认接听状态基础数据是否含"-"前缀值;在 spec 中标注该逻辑已禁用且取值来源待确认

---

## 汇总统计

| 比对类型 | 数量 | 编号 |
|---------|------|------|
| C=spec 内部 [待澄清] 展开 | 5 | AMB-005-01 ~ 05 |
| B=代码 vs 代码 | 7 | AMB-005-06 ~ 12 |
| A=代码 vs 文档 | 6 | AMB-005-13 ~ 18 |
| **合计** | **18** | |

### 关键歧义(影响面最大)
1. **AMB-005-02**(两套回访体系并存):涉及数据归并与统计口径,影响整个回访域的数据一致性基础
2. **AMB-005-04**(网关 evaluationResult=2/3/-2 无常量):直接影响 PM 闭环流程流转正确性,网关条件无法维护
3. **AMB-005-09**(计分逻辑三处实现):直接威胁 SC-011(计分一致性 100%)的成功标准,规则变更易遗漏
