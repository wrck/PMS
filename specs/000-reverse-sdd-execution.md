# 逆向 SDD 执行实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 对既有 Java 系统执行逆向 Spec-Driven Development,产出 constitution + spec + clarify 三类 artifact,作为新系统(AI 全新生成、复用/迁移数据库)开发阶段的事实来源。

**Architecture:** 反向抽取(AI 从源码+零散文档反推草稿)+ 正向固化(Spec Kit `/speckit-*` 命令链落盘)。按业务域分域产出,每域独立 git feature branch,互不阻塞。spec 强制技术栈无关。

**Tech Stack:** Spec Kit(specify CLI)、Git、TRAE AI Agent(执行反推与 `/speckit-*` 命令)。

**配套 spec:** `000-reverse-sdd-with-spec-kit-design.md`

**前置假设:** 老 Java 系统源码已就位于 `E:\AICoding\workspaces2\PMS`(路径可替换;若不同,后续所有 `<LEGACY>` 处替换为实际绝对路径)。老系统含完整源码 + 零散过时文档。

**关于"测试":** 本计划产出是文档 artifact 而非代码,故用**校验检查**(grep/清单核对)替代单元测试。每个任务以"校验通过 → commit"收尾,保持频繁提交。

---

## 文件结构

执行过程中创建/修改的产物(均在老仓库内,由 `specify init` 生成骨架):

- `<LEGACY>/.spec/` — Spec Kit 工作目录,存放 constitution 模板与配置
- `<LEGACY>/.spec/constitution.md` — 项目宪法(Task 3 产出)
- `<LEGACY>/.spec/specs/<NNN>-<domain>/spec.md` — 各域规格(Task 7 产出,逐域)
- `<LEGACY>/.spec/specs/<NNN>-<domain>/clarify.md` — 各域澄清记录(Task 9 产出,逐域)
- `<LEGACY>/docs/reverse-engineering/` — 反推草稿与证据中转目录(人工/AI 中间产物)
  - `constitution-draft.md`
  - `domain-map.md`
  - `<NNN>-<domain>/spec-draft.md`
  - `<NNN>-<domain>/ambiguities.md`

Git 分支策略:`main` 为基线;每个业务域建 `NNN-<domain>` 分支,域内 artifact 在该分支提交。

---

## Task 1: 环境准备 — 安装 specify 并初始化老仓库

**Files:**
- Modify: `<LEGACY>/.spec/`(由 `specify init` 生成)
- Modify: `<LEGACY>/.gitignore`(若不存在则创建)

- [ ] **Step 1: 确认老系统代码就位**

Run: `ls <LEGACY>`
Expected: 列出老 Java 仓库根文件(`pom.xml` 或 `build.gradle`、`src/`、零散文档)。若目录不存在或为空,停止并要求用户提供老系统代码路径。

- [ ] **Step 2: 安装 specify CLI**

Run: `pipx install git+https://github.com/github/spec-kit.git`
Expected: 安装成功,无报错。

- [ ] **Step 3: 验证安装**

Run: `specify --version`
Expected: 打印版本号。

- [ ] **Step 4: 确认老仓库已是 git 仓库(若否则初始化)**

Run: `cd <LEGACY> && git rev-parse --is-inside-work-tree`
Expected: `true`。若失败,执行 `git init && git add -A && git commit -m "chore: import legacy system baseline"`。

- [ ] **Step 5: 在老仓库初始化 Spec Kit**

Run: `cd <LEGACY> && specify init . --ai trae`
Expected: 生成 `.spec/` 目录与 AGENTS.md/提示词文件,无报错。

- [ ] **Step 6: 创建反推草稿中转目录**

Run: `mkdir -p <LEGACY>/docs/reverse-engineering`
Expected: 目录创建成功。

- [ ] **Step 7: 提交环境初始化**

```bash
cd <LEGACY>
git add .spec docs/reverse-engineering
git commit -m "chore: init spec-kit for reverse engineering"
```

---

## Task 2: Constitution 反向抽取草稿

**Files:**
- Create: `<LEGACY>/docs/reverse-engineering/constitution-draft.md`

**Spec 映射:** 阶段 1,提示词 1-A。

- [ ] **Step 1: 执行反向抽取提示词**

在 TRAE 中向本 Agent 发送(spec 提示词 1-A 原文):

```
你是逆向工程架构师。扫描当前 Java 仓库,反推项目宪法。输出分 7 节:
1. 架构风格(分层?DDD?微服务?事件驱动?)
2. 技术栈与版本(从构建文件提取,仅记录现状,不作新系统约束)
3. 分层与模块边界(每层职责、依赖方向)
4. 编码规范(命名、包结构、注解约定、异常体系)
5. 横切关注点(事务、日志、安全、缓存策略)
6. 数据访问方式(JPA?MyBatis?JDBC?分库分表?)
7. 现存技术债与风险(为后续重构标靶)

每节附 3-5 条代码证据(文件:行号)。不要猜测,无证据则标注 [待澄清]。
```

Expected: Agent 输出 7 节草稿,每节带代码证据或 `[待澄清]` 标记。

- [ ] **Step 2: 将草稿落盘**

把 Step 1 输出写入 `<LEGACY>/docs/reverse-engineering/constitution-draft.md`。

- [ ] **Step 3: 校验草稿完整性**

检查 `constitution-draft.md` 是否含全部 7 节标题;统计 `[待澄清]` 出现次数并记录(留给 Task 3 的 clarify,但 constitution 阶段的歧义可一并带入后续)。

Run(在仓库根): `grep -c "\[待澄清\]" docs/reverse-engineering/constitution-draft.md`
Expected: 打印一个数字(记录之);若某节完全缺失则补抽。

- [ ] **Step 4: 提交草稿**

```bash
cd <LEGACY>
git add docs/reverse-engineering/constitution-draft.md
git commit -m "docs: add constitution reverse-draft"
```

---

## Task 3: Constitution 正向固化

**Files:**
- Create: `<LEGACY>/.spec/constitution.md`

**Spec 映射:** 阶段 1,提示词 1-B,`/speckit-constitution`。

- [ ] **Step 1: 执行 /speckit-constitution 固化**

在 TRAE 中执行(spec 提示词 1-B,贴入 Task 2 草稿):

```
/speckit-constitution 基于 <贴入 constitution-draft.md 内容> 固化项目宪法。
追加三条强制原则:
- SPEC-TYPE-01: spec 必须技术栈无关,禁止绑定具体框架注解/类名
- DATA-REUSE-01: 数据库表结构视为契约,新系统默认复用,变更需显式 spec
- AMBIGUITY-01: 所有歧义点必须在 /clarify 阶段记录决策与依据
```

Expected: 生成 `.spec/constitution.md`,含抽取内容 + 三条逆向原则。

- [ ] **Step 2: 校验三条逆向原则已写入**

Run: `grep -E "SPEC-TYPE-01|DATA-REUSE-01|AMBIGUITY-01" <LEGACY>/.spec/constitution.md`
Expected: 三条原则各命中一次。

- [ ] **Step 3: 提交 constitution**

```bash
cd <LEGACY>
git add .spec/constitution.md
git commit -m "feat(spec):固化项目宪法与三条逆向原则"
```

---

## Task 4: 业务域切分(DDD 战略)

**Files:**
- Create: `<LEGACY>/docs/reverse-engineering/domain-map.md`

**Spec 映射:** 阶段 2a,提示词 2-A。

- [ ] **Step 1: 执行域切分提示词**

在 TRAE 中发送(spec 提示词 2-A 原文):

```
你是 DDD 战略分析师。基于阶段1的模块边界 + 包结构 + 数据库表归属,把系统切分为
bounded context。每个域输出:
- 域名(中英文)
- 一句话职责
- 核心聚合根(从实体类反推)
- 涉及的数据库表清单
- 与其他域的依赖关系(上下游)
- 建议的 spec feature branch 编号(001-xxx)

目标:每域可独立 /specify,互不阻塞。域数控制在 5-10 个,过大则再切。
```

Expected: 输出 5-10 个域,每个含上述 6 项字段。

- [ ] **Step 2: 落盘域清单**

把 Step 1 输出写入 `<LEGACY>/docs/reverse-engineering/domain-map.md`。

- [ ] **Step 3: 校验域数与依赖无环**

人工核对:域数在 5-10;在 `domain-map.md` 中按域间依赖画一遍,确认无环。若有环,回到 Step 1 要求 Agent 重新切分以打破循环依赖。

- [ ] **Step 4: 提交域清单**

```bash
cd <LEGACY>
git add docs/reverse-engineering/domain-map.md
git commit -m "docs: add domain map for reverse spec"
```

---

## Task 5: 为每个业务域创建 feature branch

**Files:**
- 无文件修改,仅 git 分支操作。

**Spec 映射:** 阶段 2a 末尾。

- [ ] **Step 1: 从 domain-map.md 提取域编号与名称**

读取 `<LEGACY>/docs/reverse-engineering/domain-map.md`,记录每个域的 `NNN-<domain>` 标识。

- [ ] **Step 2: 逐域创建分支**

对每个域执行:

```bash
cd <LEGACY>
git checkout main
git checkout -b NNN-<domain>
```

Expected: 每域一个分支,均从 main 切出。

- [ ] **Step 3: 校验分支清单**

Run: `cd <LEGACY> && git branch`
Expected: 列出 main + 全部 NNN-<domain> 分支,数量与 domain-map.md 域数一致。

---

## Task 6: 单域 spec 反向抽取草稿(对每个域重复执行)

> 本任务是**循环模板**。对 `domain-map.md` 中每个域执行一次。建议每域派一个独立 subagent 并行(见执行方式选择)。

**Files:**
- Create: `<LEGACY>/docs/reverse-engineering/<NNN>-<domain>/spec-draft.md`

**Spec 映射:** 阶段 2b,提示词 2-B。

- [ ] **Step 1: 切到目标域分支**

Run: `cd <LEGACY> && git checkout NNN-<domain>`

- [ ] **Step 2: 创建域草稿目录**

Run: `mkdir -p <LEGACY>/docs/reverse-engineering/NNN-<domain>`

- [ ] **Step 3: 执行反向抽取提示词**

在 TRAE 中发送(spec 提示词 2-B,替换 `<域名>`):

```
你是逆向规格分析师。针对 <域名> 域,扫描相关 controller/service/repository/entity,
反推规格,严格技术栈无关。输出 4 章:

第1章 用户故事:从 controller 端点反推 actor + 目标 + 价值(As a... I want... so that...)
  - 每个端点至少1个故事,标注 HTTP 方法+路径作为证据
第2章 功能需求:按用例组织,每条含 触发条件/输入/处理规则/输出/异常
  - 处理规则从 service 业务逻辑反推,禁止出现类名/注解
第3章 数据契约【最关键】:列出该域所有表
  - 表名、字段名、类型、是否可空、语义说明、业务不变量(唯一/外键/枚举值域)
  - 标注哪些字段是"对外契约字段"(新系统必须保留),哪些是"内部实现字段"(可重构)
  - 此章是新系统复用/迁移 DB 的唯一事实来源
第4章 非功能需求:从事务注解/缓存/限流配置反推 性能/一致性/可用性要求

每条需求附代码证据(文件:行号)。不确定处标 [待澄清]。
```

Expected: 输出 4 章草稿,第 3 章为表格,带代码证据与 `[待澄清]` 标记。

- [ ] **Step 4: 落盘草稿**

把 Step 3 输出写入 `<LEGACY>/docs/reverse-engineering/NNN-<domain>/spec-draft.md`。

- [ ] **Step 5: 校验技术栈无关**

Run: `grep -nE "@(Entity|Controller|Service|Repository|Component|Autowired)|javax\.|org\.springframework" <LEGACY>/docs/reverse-engineering/NNN-<domain>/spec-draft.md`
Expected: 无命中(草稿不得出现框架注解/包名)。若有命中,回到 Step 3 要求 Agent 重写该处为行为描述。

- [ ] **Step 6: 校验第 3 章数据契约字段分级**

人工核对 `spec-draft.md` 第 3 章每张表的每个字段是否标注了"契约字段/内部字段/废弃字段"之一。未标注的字段补标。

- [ ] **Step 7: 统计 [待澄清] 并记录**

Run: `grep -c "\[待澄清\]" <LEGACY>/docs/reverse-engineering/NNN-<domain>/spec-draft.md`
Expected: 打印数字(留给 Task 8 处理)。

- [ ] **Step 8: 提交草稿**

```bash
cd <LEGACY>
git add docs/reverse-engineering/NNN-<domain>/spec-draft.md
git commit -m "docs(NNN-<domain>): add spec reverse-draft"
```

---

## Task 7: 单域 spec 正向固化(对每个域重复执行)

**Files:**
- Create: `<LEGACY>/.spec/specs/NNN-<domain>/spec.md`

**Spec 映射:** 阶段 2b,提示词 2-C,`/speckit-specify`。

- [ ] **Step 1: 确认在目标域分支**

Run: `cd <LEGACY> && git branch --show-current`
Expected: `NNN-<domain>`。

- [ ] **Step 2: 执行 /speckit-specify 固化**

在 TRAE 中执行(spec 提示词 2-C,贴入 Task 6 草稿):

```
/speckit-specify 将 <贴入 spec-draft.md 内容> 结构化为 Spec Kit spec artifact。
要求:
- 描述"系统做什么"而非"代码怎么做"
- 第3章数据契约单独成节,用表格呈现,字段语义必须明确
- 保留所有 [待澄清] 标记,留给下一步 /clarify
```

Expected: 生成 `.spec/specs/NNN-<domain>/spec.md`。

- [ ] **Step 3: 校验技术栈无关(固化后复查)**

Run: `grep -nE "@(Entity|Controller|Service|Repository|Component|Autowired)|javax\.|org\.springframework" <LEGACY>/.spec/specs/NNN-<domain>/spec.md`
Expected: 无命中。

- [ ] **Step 4: 校验 [待澄清] 标记保留**

Run: `grep -c "\[待澄清\]" <LEGACY>/.spec/specs/NNN-<domain>/spec.md`
Expected: 与 Task 6 Step 7 统计数一致(标记应原样保留,留给 clarify)。

- [ ] **Step 5: 提交 spec**

```bash
cd <LEGACY>
git add .spec/specs/NNN-<domain>/spec.md
git commit -m "feat(NNN-<domain>): 固化 spec artifact"
```

---

## Task 8: 单域 clarify 歧义识别(对每个域重复执行)

**Files:**
- Create: `<LEGACY>/docs/reverse-engineering/NNN-<domain>/ambiguities.md`

**Spec 映射:** 阶段 3,提示词 3-A。

- [ ] **Step 1: 执行歧义识别提示词**

在 TRAE 中发送(spec 提示词 3-A):

```
你是规格澄清师。针对 <域名> 的 spec 草稿,执行三比对:
1. 代码 vs 现有文档:列出所有矛盾点(文档说A,代码做B)
2. 代码 vs 代码:同名字段/枚举在不同处语义不一致的情况
3. spec 内部:[待澄清] 标记逐条展开

每条歧义输出:
- ID: AMB-<域>-<序号>
- 位置:涉及文件:行号 + 涉及文档片段
- 现象:矛盾/缺失/多解的具体描述
- 候选解释:2-3 种可能含义
- 影响面:波及哪些需求/数据字段
- 建议决策:基于代码证据的推荐选项+理由
```

Expected: 输出 AMB-<域>-<序号> 编号的歧义清单,每条含 6 个字段。

- [ ] **Step 2: 落盘歧义清单**

把 Step 1 输出写入 `<LEGACY>/docs/reverse-engineering/NNN-<domain>/ambiguities.md`。

- [ ] **Step 3: 校验歧义 ID 连续**

人工核对 `ambiguities.md` 中 AMB-<域>-01、02... 编号连续无缺漏,且每条 6 字段齐全。

- [ ] **Step 4: 提交歧义清单**

```bash
cd <LEGACY>
git add docs/reverse-engineering/NNN-<domain>/ambiguities.md
git commit -m "docs(NNN-<domain>): add ambiguity list for clarify"
```

---

## Task 9: 单域 clarify 逐条确认与正向固化(对每个域重复执行)

**Files:**
- Create: `<LEGACY>/.spec/specs/NNN-<domain>/clarify.md`
- Modify: `<LEGACY>/.spec/specs/NNN-<domain>/spec.md`(清零 `[待澄清]`)

**Spec 映射:** 阶段 3,提示词 3-B,`/speckit-clarify`。

- [ ] **Step 1: 逐条与用户确认决策**

对 `ambiguities.md` 中每条 AMB-*:
- 向用户呈现 现象 + 候选解释 + 影响面 + 建议决策。
- 用户选定(采纳建议或另指定),记录最终决策。
- 若用户无法当场决策,标注"待业务确认"并保留该条 AMB,**不**视为完成,本任务不得进入 Step 2 直至此条解决。

- [ ] **Step 2: 执行 /speckit-clarify 正向固化**

在 TRAE 中执行(spec 提示词 3-B):

```
/speckit-clarify 聚焦 <域名> 的 AMB-* 歧义清单。
对每条:采纳建议决策(或用户指定),写入 clarify artifact,字段含
决策结论/依据/影响范围/回滚提示。
完成后 spec 内不得残留 [待澄清]。
```

Expected: 生成 `.spec/specs/NNN-<domain>/clarify.md`,并同步更新 `spec.md` 清除 `[待澄清]`。

- [ ] **Step 3: 校验 spec 内 [待澄清] 已清零**

Run: `grep -c "\[待澄清\]" <LEGACY>/.spec/specs/NNN-<domain>/spec.md`
Expected: `0`。若非 0,回到 Step 2 要求 Agent 处理残留标记。

- [ ] **Step 4: 校验 clarify 含全部 AMB 决策**

人工核对 `clarify.md` 覆盖 `ambiguities.md` 中全部 AMB-* 编号,每条含 决策结论/依据/影响范围/回滚提示 四字段。

- [ ] **Step 5: 提交 clarify**

```bash
cd <LEGACY>
git add .spec/specs/NNN-<domain>/clarify.md .spec/specs/NNN-<domain>/spec.md
git commit -m "feat(NNN-<domain>): 固化 clarify 并清零待澄清标记"
```

---

## Task 10: 单域最终校验(对每个域重复执行)

**Files:**
- 无新文件,仅校验。

**Spec 映射:** §8 校验清单(单域部分)。

- [ ] **Step 1: 校验无残留 [待澄清]**

Run: `grep -rn "\[待澄清\]" <LEGACY>/.spec/specs/NNN-<domain>/`
Expected: 无输出。

- [ ] **Step 2: 校验技术栈无关**

Run: `grep -rnE "@(Entity|Controller|Service|Repository|Component|Autowired)|javax\.|org\.springframework" <LEGACY>/.spec/specs/NNN-<domain>/`
Expected: 无输出。

- [ ] **Step 3: 校验每张表字段分级完整**

人工核对 `spec.md` 第 3 章每字段均标注 契约/内部/废弃 分级。

- [ ] **Step 4: 校验每条需求有代码证据**

人工核对 `spec.md` 每条用户故事/功能需求附 `文件:行号` 证据。

- [ ] **Step 5: 任一校验失败则回退**

若 Step 1-4 任一失败,回到对应 Task(Task 7 或 Task 9)修正后重新提交。全部通过则该域完成。

---

## Task 11: 全局校验与产出物汇总

**Files:**
- Create: `<LEGACY>/docs/reverse-engineering/delivery-summary.md`

**Spec 映射:** §8 校验清单(全局)+ §9 产出物清单。

- [ ] **Step 1: 合并各域分支到 main**

对每个域:

```bash
cd <LEGACY>
git checkout main
git merge --no-ff NNN-<domain> -m "merge: reverse spec for NNN-<domain>"
```

Expected: 各域分支无冲突合并入 main(若有冲突,说明域间 spec 重叠,人工裁定归属后重做)。

- [ ] **Step 2: 全局校验 — 无残留 [待澄清]**

Run: `grep -rn "\[待澄清\]" <LEGACY>/.spec/`
Expected: 无输出。

- [ ] **Step 3: 全局校验 — 技术栈无关**

Run: `grep -rnE "@(Entity|Controller|Service|Repository|Component|Autowired)|javax\.|org\.springframework" <LEGACY>/.spec/specs/`
Expected: 无输出。

- [ ] **Step 4: 全局校验 — constitution 含三条逆向原则**

Run: `grep -E "SPEC-TYPE-01|DATA-REUSE-01|AMBIGUITY-01" <LEGACY>/.spec/constitution.md`
Expected: 三条各命中一次。

- [ ] **Step 5: 全局校验 — 域间依赖无环**

读取 `domain-map.md`,按域间依赖关系核对无环(与 Task 4 Step 3 一致,合并后复查)。

- [ ] **Step 6: 生成交付汇总**

写入 `<LEGACY>/docs/reverse-engineering/delivery-summary.md`,含:
- 域清单(编号/名称/分支/表数/AMB 数)
- 各域 spec/clarify 文件路径
- 数据库复用/迁移要点(契约字段汇总、废弃字段清单、迁移不变量)
- 校验清单全部勾选状态

- [ ] **Step 7: 提交交付汇总**

```bash
cd <LEGACY>
git add docs/reverse-engineering/delivery-summary.md
git commit -m "docs: add reverse-SDD delivery summary"
```

---

## Task 12: 移交与后续衔接说明

**Files:**
- 无新文件,仅向用户说明。

**Spec 映射:** §10 后续衔接。

- [ ] **Step 1: 向用户交付产出物清单**

向用户报告:
- constitution 路径:`<LEGACY>/.spec/constitution.md`
- 各域 spec/clarify 路径:`<LEGACY>/.spec/specs/NNN-<domain>/`
- 交付汇总:`<LEGACY>/docs/reverse-engineering/delivery-summary.md`

- [ ] **Step 2: 说明后续衔接**

告知用户:当新功能/重构需求到来时,在已有 artifact 基础上新建 feature branch,追加/修改 spec(保持技术栈无关),再走 `/clarify` → `/plan`(此时选定新技术栈)→ `/tasks` → `/analyze` → `/implement`。技术选型集中在 `/plan` 一次性做出。

- [ ] **Step 3: 确认收尾**

确认用户已知产出位置与后续流程,本计划执行结束。

---

## 执行顺序总览

1. Task 1(环境,一次性)
2. Task 2 → Task 3(constitution,一次性)
3. Task 4 → Task 5(域切分+分支,一次性)
4. 对每个域并行:Task 6 → Task 7 → Task 8 → Task 9 → Task 10
5. Task 11(全局汇总,一次性)
6. Task 12(移交,一次性)

constitution 与域切分必须先完成(后续域任务依赖 domain-map.md 与 constitution)。域内任务严格按 6→7→8→9→10 顺序。各域之间无依赖,可并行。
