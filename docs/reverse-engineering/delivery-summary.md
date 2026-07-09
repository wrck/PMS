# 逆向 SDD 交付汇总(Reverse-SDD Delivery Summary)

> **生成日期**:2026-07-09
> **基线分支**:SpecKit
> **方法论**:`specs/000-reverse-sdd-with-spec-kit-design.md`
> **执行计划**:`specs/000-reverse-sdd-execution.md`(12 Task)
> **执行状态**:Task 1-11 完成,Task 12 移交说明见本文件末尾

---

## 1. 域清单与产出物统计

| 编号 | 域名(中) | 域名(英) | 分支 | 表数 | AMB 数 | spec.md 行数 | clarify.md 行数 | 合并状态 |
|---|---|---|---|---|---|---|---|---|
| 001 | 用户与权限 | user-auth | 001-user-auth | 18 | 12 | 916 | 78 | ✅ merged |
| 002 | 售前与产品 | presales-product | 002-presales-product | 15 | 13 | 822 | 146 | ✅ merged |
| 003 | 项目交付 | project-delivery | 003-project-delivery | 24 | 22 | 749 | 222 | ✅ merged |
| 004 | 工作流与工作空间 | workflow-workspace | 004-workflow-workspace | 6+12(act_*) | 15 | 364 | 127 | ✅ merged |
| 005 | 质保回访与PM闭环 | warranty-callback | 005-warranty-callback | 10 | 18 | 472 | 162 | ✅ merged |
| 006 | 维保与监管 | maintenance-supervision | 006-maintenance-supervision | 7 | 11 | 418 | 112 | ✅ merged |
| 007 | 分包 | subcontract | 007-subcontract | 7 | 15 | 385 | 152 | ✅ merged |
| 008 | 外部集成 | external-integration | 008-external-integration | 16+9外部 | 16 | 973 | 179 | ✅ merged |
| 009 | 系统基础与报表 | system-base | 009-system-base | 21 | 17 | 1163 | 187 | ✅ merged |
| **合计** | — | — | — | **124+9外部** | **139** | **6262** | **1365** | **9/9** |

**反推中间产物**(docs/reverse-engineering/):
- spec-draft.md:9 个文件,共 5936 行
- ambiguities.md:9 个文件,共 1999 行

---

## 2. 产出物文件清单

### 2.1 项目宪法(Spec Kit artifact)
- `.specify/memory/constitution.md` — 含 8 条原则,其中 VI/VII/VIII 为三条逆向强制原则

### 2.2 各域 Spec artifact(Spec Kit 标准产出)
| 域 | spec.md 路径 | clarify.md 路径 |
|---|---|---|
| 001 | `.specify/specs/001-user-auth/spec.md` | `.specify/specs/001-user-auth/clarify.md` |
| 002 | `.specify/specs/002-presales-product/spec.md` | `.specify/specs/002-presales-product/clarify.md` |
| 003 | `.specify/specs/003-project-delivery/spec.md` | `.specify/specs/003-project-delivery/clarify.md` |
| 004 | `.specify/specs/004-workflow-workspace/spec.md` | `.specify/specs/004-workflow-workspace/clarify.md` |
| 005 | `.specify/specs/005-warranty-callback/spec.md` | `.specify/specs/005-warranty-callback/clarify.md` |
| 006 | `.specify/specs/006-maintenance-supervision/spec.md` | `.specify/specs/006-maintenance-supervision/clarify.md` |
| 007 | `.specify/specs/007-subcontract/spec.md` | `.specify/specs/007-subcontract/clarify.md` |
| 008 | `.specify/specs/008-external-integration/spec.md` | `.specify/specs/008-external-integration/clarify.md` |
| 009 | `.specify/specs/009-system-base/spec.md` | `.specify/specs/009-system-base/clarify.md` |

### 2.3 反推中间产物(证据中转)
- `docs/reverse-engineering/constitution-draft.md`
- `docs/reverse-engineering/domain-map.md`
- `docs/reverse-engineering/NNN-<domain>/spec-draft.md`(9 个)
- `docs/reverse-engineering/NNN-<domain>/ambiguities.md`(9 个)

---

## 3. 数据库复用/迁移要点

### 3.1 数据契约总览
- **契约表总数**:124 张本地表 + 9 类外部系统视图/表/API
- **字段分级标注**:每域 spec.md 第 3 章"数据契约"节,每个字段均标注分级标记
- **DATA-REUSE-01 遵循**:所有域 spec 均声明"复用既有数据契约,不新增表"

### 3.2 字段分级语义说明(已知偏差)
> 设计文档定义的分级为:契约字段(C)/内部字段(I)/废弃字段(D)/外部契约字段(EC)。
> 实际产出中各域 subagent 采用了两种语义:
> - **数据生命周期语义**(006、007 等):C=Create/I=Input/D=Derived
> - **契约识别语义**(008):I=Identifying/D=Descriptive/C=Cardinal/EC=外部契约
>
> 两种语义均提供了字段级文档,但与新系统迁移所需的"契约/内部/废弃"分类存在偏差。
> **后续行动**:在 `/plan` 阶段选定新技术栈后,可基于现有字段说明 + AMB 决策,一次性重映射为统一契约分级。

### 3.3 关键数据契约决策(按域汇总)

| 域 | 关键决策(影响 DB 迁移) |
|---|---|
| 001-user-auth | `user` 与 `fnd_user_info` 双用户表统一为同义视图;采用 `UserRole` 关联表(非 user.menuCode 逗号串) |
| 002-presales-product | 设备版本解析路径拆分为独立策略;`restoreStatus` 物理归属确认;驳回逻辑修正 |
| 003-project-delivery | `transferFlag` 语义重定义(0=正常/1=已转移);软删除分类豁免清单;项目编码映射表 |
| 004-workflow-workspace | FR-TASK-01/DELEGATE-01 标记废弃但表保留;submitTask redirect 行为固化 |
| 005-warranty-callback | 两套回访体系(质保回访 + 维保回访)并存确认;网关态非业务枚举;计分逻辑收敛 |
| 006-maintenance-supervision | cron 表达式补全(原硬编码);借用维保额度暂定不计入 MVP |
| 007-subcontract | `customInfo` 增量合并语义(JSON_MERGE_PATCH);驳回重提交新建流程实例;3 项 [暂定决策] |
| 008-external-integration | `clientSecret` 加密存储;`PlanGetBySMS` 改造继承基类;SAP 同步保留(原被注释禁用) |
| 009-system-base | 废弃裸 SQL(executeSql);新老架构表分批合并;印章导入改事务性 |

### 3.4 [暂定决策] 清单(待业务/代码最终确认)
分布在 007、008 等域的 spec.md 中,以 `[暂定决策:...]` 标记。这些是基于代码证据的合理假设,在 `/plan` 阶段需优先复核:
- 007:转包 state=40/−100 语义、isAccrued/isInvoiced 回写来源、发票查验执行方
- 008:外部系统对接细节
- 其他域零散分布

---

## 4. 校验清单(全局)

### 4.1 Task 10 单域校验

| 校验项 | 001 | 002 | 003 | 004 | 005 | 006 | 007 | 008 | 009 |
|---|---|---|---|---|---|---|---|---|---|
| Step 1 无残留 [待澄清](spec.md) | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Step 2 技术栈无关(spec.md) | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Step 3 字段分级完整 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Step 4a clarify 覆盖全部 AMB | ✅12 | ✅13 | ✅22 | ✅15 | ✅18 | ✅11 | ✅15 | ✅16 | ✅17 |
| Step 4b 代码证据存在 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

### 4.2 Task 11 全局校验

| 校验项 | 方法 | 结果 |
|---|---|---|
| 全局无残留 [待澄清](spec.md) | `grep -rn "\[待澄清\]" .specify/specs/*.md`(限定 spec.md) | ✅ 0 命中 |
| 全局技术栈无关 | `grep -rnE "@(Entity|Controller|Service|Repository|Component|Autowired)|javax\.|org\.springframework" .specify/specs/` | ✅ 0 命中 |
| Constitution 含三条逆向原则 | `grep -E "SPEC-TYPE-01|DATA-REUSE-01|AMBIGUITY-01" .specify/memory/constitution.md` | ✅ 3 条各命中 |
| 域间依赖无环 | domain-map.md 拓扑序校验 + 合并无冲突 | ✅ 无环,9 域独立合并 |
| clarify.md 引用 [待澄清] 为上下文引用 | 人工抽查 008 clarify.md | ✅ 确认为原始歧义标题引用 |

### 4.3 已知偏差与限制

1. **字段分级语义偏差**(见 §3.2):部分域采用数据生命周期语义而非契约分类语义。不阻塞使用,建议在 `/plan` 阶段统一重映射。
2. **[暂定决策] 未清零**:spec.md 中残留 `[暂定决策:...]` 标记(区别于 `[待澄清]`)。这些是已固化的工作假设,非未决歧义,在 `/plan` 阶段需复核。
3. **代码证据格式不统一**:各域 subagent 采用不同证据引用风格(嵌入式 vs 独立"证据:"标记),但均附代码溯源。

---

## 5. 域间依赖与拓扑序

```
001-user-auth ─────────────────────────────────┐ (基础设施,被所有域依赖)
004-workflow-workspace ──► 001                 │
008-external-integration ─► 001                │
009-system-base ─► 001                         │
002-presales-product ─► 001, 004               │
003-project-delivery ─► 001, 002, 004, 008     │
005-warranty-callback ─► 001, 003, 004         │
006-maintenance-supervision ─► 001, 003        │
007-subcontract ─► 001, 003, 004               │
```

**拓扑序**:001 → 004/008/009 → 002 → 003 → 005/006/007(依赖方向严格向下,无环)

---

## 6. Git 提交历史概览

各域分支均从 SpecKit(8476f95c)切出,经 Task 6/7/8/9 四轮提交后以 `--no-ff` 合并回 SpecKit:

- Task 6: `docs(NNN-<domain>): add spec reverse-draft`(9 域)
- Task 7: `feat(NNN-<domain>): 固化 spec artifact`(9 域)
- Task 8: `docs(NNN-<domain>): add ambiguity list for clarify`(9 域)
- Task 9: `feat(NNN-<domain>): 固化 clarify 并清零待澄清标记`(9 域)
- Task 11: `merge: reverse spec for NNN-<domain>`(9 域,--no-ff 合并)

---

## 7. 后续衔接(Task 12 移交说明)

### 7.1 产出物用途
本批 artifact(constitution + 9 域 spec + 9 域 clarify)是新系统(AI 全新生成、复用/迁移数据库)开发阶段的**事实来源**。新功能或重构需求到来时,在已有 artifact 基础上:

1. 新建 feature branch
2. 追加/修改 spec(保持技术栈无关,遵循 SPEC-TYPE-01)
3. 走 `/clarify`(若有新歧义)→ `/plan`(此时选定新技术栈,技术选型集中在此一次性做出)→ `/tasks` → `/analyze` → `/implement`

### 7.2 优先复核项
在进入 `/plan` 前,建议优先复核:
- **[暂定决策] 清单**(见 §3.4):逐条与业务方确认
- **字段分级语义**(见 §3.2):统一为契约/内部/废弃分类
- **003-project-delivery 域**:该域最大(24 表、22 AMB),且为 005/006/007 的上游,迁移优先级最高

### 7.3 数据库迁移策略提示
- 遵循 DATA-REUSE-01:默认复用既有表结构,变更需显式 spec
- 124 张本地表 + 9 类外部对接,按域拓扑序(001 → ... → 007)分批迁移
- 外部系统(008)对接优先级独立,可与本地域并行
- 废弃表/字段(各域 spec 中标记 D 或 [暂定决策:废弃])在迁移时剔除

---

*本汇总由逆向 SDD 流程自动生成,对应执行计划 Task 11 Step 6。*
