# pms-rules 模块知识库全面审查报告

> 审查时间：2026-06-25
> 审查范围：pms-rules 模块全部文档（24 个文档文件）
> 审查方法：与 `AviatorUtils.java` 源码、`pom.xml`、父 `pom.xml`、PMS 项目调用点源码、Aviator 官方文档交叉验证
> 审查人：自动化审查工具

---

## 1. 审查概述

### 1.1 审查范围

| 文档目录 | 文档数量 | 审查状态 |
|----------|----------|----------|
| 01-architecture/ | 4 | ✅ 已审查 |
| 02-modules/ | 5 | ✅ 已审查 |
| 03-database/ | 2 | ✅ 已审查 |
| 04-mapping/ | 2 | ✅ 已审查 |
| 05-standards/ | 4 | ✅ 已审查 |
| 06-reference/ | 4 | ✅ 已审查 |
| audit/ | 2（含本文档） | ✅ 已审查 |
| README.md | 1 | ✅ 已审查 |
| **合计** | **24** | — |

### 1.2 审查结论

| 维度 | 评级 | 说明 |
|------|------|------|
| 源码一致性 | ✅ 优秀 | AviatorUtils 方法签名、字段、内部结构与源码完全一致 |
| 调用点覆盖 | ✅ 优秀 | 7 个业务集成点（11 个代码调用点）全部覆盖，行号逐一验证准确 |
| 事实准确性 | ✅ 已修正 | 本次修正了 5 处事实错误/遗漏 |
| 依赖说明 | ✅ 优秀 | LiteFlow/Groovy 僵尸依赖已正确说明，版本号与父 pom 一致 |
| 方法名拼写 | ✅ 优秀 | `exceute` 拼写错误已正确记录 |
| Aviator 语法 | ✅ 已修正 | 修正了字符串引号描述的事实错误 |

---

## 2. 源码一致性验证

### 2.1 源文件清单

pms-rules 模块 `src/main/java/` 下仅有一个 Java 文件：

| 文件 | 路径 | 行数 |
|------|------|------|
| `AviatorUtils.java` | `pms-rules/src/main/java/com/dp/plat/rules/util/AviatorUtils.java` | 66 行 |

### 2.2 AviatorUtils 方法对照

| 方法 | 文档签名 | 源码签名（第 32-65 行） | 一致性 |
|------|----------|--------------------------|--------|
| `getInstance()` | `public static AviatorEvaluatorInstance getInstance()` | ✅ 一致（第 28 行） | ✅ |
| `exceute(String, Map)` | `public static Object exceute(String script, Map<String, Object> env)` | ✅ 一致（第 32 行） | ✅ |
| `getCacheSize()` | `public static int getCacheSize()` | ✅ 一致（第 44 行） | ✅ |
| `setCacheSize(int)` | `public static void setCacheSize(int cacheSize)` | ✅ 一致（第 52 行） | ✅ |
| `resetAviator()` | `public static void resetAviator()` | ✅ 一致（第 60 行） | ✅ |

### 2.3 字段与内部类对照

| 项目 | 文档描述 | 源码实际 | 一致性 |
|------|----------|----------|--------|
| `cacheSize` 字段 | `private static int cacheSize = 100` | ✅ 一致（第 14 行） | ✅ |
| `StaticHolder` 内部类 | `private static class StaticHolder` | ✅ 一致（第 16 行） | ✅ |
| `INSTANCE` 字段 | `private static AviatorEvaluatorInstance INSTANCE` | ✅ 一致（第 17 行） | ✅ |
| `newInstance()` 方法 | `private static AviatorEvaluatorInstance newInstance()` | ✅ 一致（第 19 行） | ✅ |
| LRU 缓存配置 | `useLRUExpressionCache(cacheSize)` | ✅ 一致（第 21 行） | ✅ |
| 默认缓存开启 | `setCachedExpressionByDefault(true)` | ✅ 一致（第 22 行） | ✅ |
| FunctionMissing | `JavaMethodReflectionFunctionMissing.getInstance()` | ✅ 一致（第 23 行） | ✅ |
| MD5 实现 | `DigestUtils.md5DigestAsHex(script.getBytes())` | ✅ 一致（第 36 行） | ✅ |

### 2.4 方法名拼写错误记录

源码中方法名为 `exceute`（应为 `execute`，第 32 行），文档已正确记录此历史遗留拼写错误，并说明三处重复定义（pms-rules、core、PMS-struts）均存在此问题。

---

## 3. 调用点验证

### 3.1 调用点搜索结果

通过全项目搜索 `AviatorUtils`（Grep `*.java`），共找到 12 个文件：

| 文件 | 模块 | 类型 |
|------|------|------|
| `AviatorUtils.java` (pms-rules) | pms-rules | 定义 |
| `AviatorUtils.java` (core) | core | 重复定义 |
| `AviatorUtils.java` (PMS-struts) | PMS-struts | 重复定义 |
| `InvoiceUtil.java` | pms-ext-fp | 业务调用 |
| `ProjectStateUpdateAspect.java` | PMS-struts | 业务调用 |
| `AutoStartPresalesProjectJob.java` | PMS-struts | 业务调用 |
| `SubcontractUtil.java` | PMS-struts | 业务调用 |
| `SubcontractInspectionListener.java` | PMS-struts | 业务调用 |
| `WorkflowUtil.java` | PMS-struts | 业务调用 |
| `DispatchSettlementUpdateAspect.java` | PMS-springmvc | 业务调用 |
| `SubcontractTest.java` | PMS-struts (test) | 测试调用 |
| `AutoStartPresalesProjectJobTest.java` | PMS-struts (test) | 测试调用 |

### 3.2 调用点行号验证（全量）

通过 Grep `AviatorUtils\.(exceute|getInstance)` 获取所有调用行号，与文档逐一比对：

| 序号 | 调用类 | 方法 | 文档行号 | 实际行号 | 一致性 |
|------|--------|------|----------|----------|--------|
| 1 | `InvoiceUtil` | `checkFileInvoiceType` | 117 | 117 | ✅ |
| 2 | `InvoiceUtil` | `checkFileInvoiceStatus` | 147 | 147 | ✅ |
| 3 | `ProjectStateUpdateAspect` | `checkRule` | 259 | 259 | ✅ |
| 4 | `ProjectStateUpdateAspect` | `execScripts` | 354 | 354 | ✅ |
| 5 | `AutoStartPresalesProjectJob` | `execScripts` | 248 | 248 | ✅ |
| 6 | `SubcontractUtil` | `checkDeliveryInvoiceType` | 51 | 51 | ✅ |
| 7 | `SubcontractUtil` | `checkDeliveryInvoiceStatus` | 72 | 72 | ✅ |
| 8 | `SubcontractInspectionListener` | `checkAssignee` | 668 | 668 | ✅ |
| 9 | `WorkflowUtil` | `callBackProcess` | 114 | 114 | ✅ |
| 10 | `DispatchSettlementUpdateAspect` | `checkRule` | 292 | 292 | ✅ |
| 11 | `DispatchSettlementUpdateAspect` | `execScripts` | 390 | 390 | ✅ |

### 3.3 调用点覆盖结论

文档记录的 7 个业务集成点（11 个代码调用点）与源码搜索结果完全一致，**11 个行号全部准确无误**。

> **注**：`AutoStartPresalesProjectJob.java` 中另有多行被注释的 `AviatorUtils.exceute` 调用（第 386、390、395、399、403 行），文档未将其计入调用点，处理正确。

---

## 4. 三处重复定义验证

### 4.1 重复定义确认

通过 Glob 和 Grep 确认，AviatorUtils 在三处定义：

| 模块 | 包路径 | 文件路径 | 验证结果 |
|------|--------|----------|----------|
| **pms-rules** | `com.dp.plat.rules.util` | `pms-rules/src/main/java/com/dp/plat/rules/util/AviatorUtils.java` | ✅ 已读取源码 |
| **core** | `com.dp.plat.core.util` | `core/src/main/java/com/dp/plat/core/util/AviatorUtils.java` | ✅ 已读取源码 |
| **PMS-struts** | `com.dp.plat.util` | `PMS-struts/src/com/dp/plat/util/AviatorUtils.java` | ✅ 已读取源码 |

### 4.2 MD5 实现差异验证

三处源码已逐一读取，`exceute` 方法中缓存 Key 的 MD5 实现差异确认如下：

| 版本 | 文档描述 | 源码实际（行号） | 一致性 |
|------|----------|------------------|--------|
| pms-rules | `DigestUtils.md5DigestAsHex(script.getBytes())` | ✅ 第 36 行一致 | ✅ |
| core | `PasswordUtil.encryptMD5Password(script)` | ✅ 第 34 行一致 | ✅ |
| PMS-struts | `Md5Util.getMD5(script.getBytes())` | ✅ 第 34 行一致 | ✅ |

> **注**：core 版本的 `PasswordUtil` 和 PMS-struts 版本的 `Md5Util` 均未显式 import，说明它们位于同包内（`com.dp.plat.core.util` 和 `com.dp.plat.util`），文档对此描述准确。

### 4.3 其他差异验证

三处版本的类结构、方法签名、StaticHolder 单例模式、`exceute` 拼写错误**完全一致**，仅 MD5 实现不同。文档描述准确。

---

## 5. 依赖声明验证

### 5.1 pom.xml 依赖对照

通过读取 `pms-rules/pom.xml` 和父 `PMS/pom.xml` 逐一验证：

| 依赖 | 文档描述版本 | pom.xml 实际版本 | 版本来源 | 一致性 |
|------|-------------|------------------|----------|--------|
| Aviator | 5.4.3 | `<aviator.version>5.4.3</aviator.version>` | 父 pom 第 129 行 | ✅ |
| LiteFlow | 2.15.0 | `<liteflow.version>2.15.0</liteflow.version>` | 父 pom 第 130 行 | ✅ |
| Groovy | 3.0.19 | `<version>3.0.19</version>` | pms-rules pom 第 43 行 | ✅ |
| spring-context | 父 pom 管理 | `<spring.version>5.3.19</spring.version>` | 父 pom 第 29 行 | ✅ |
| fastjson | 父 pom 管理 | `<fastjson.version>1.2.83</fastjson.version>` | 父 pom 第 97 行 | ✅ |
| jackson-databind | 父 pom 管理 | `<jackson-databind.version>2.13.1</jackson-databind.version>` | 父 pom 第 102 行 | ✅ |
| mockito-inline | 4.11.0 | `<mockito.version>4.11.0</mockito.version>` | pms-rules pom 第 25 行 | ✅ |
| lombok | 1.18.24 | `<version>1.18.24</version>` | pms-rules pom 第 71 行 | ✅ |

### 5.2 LiteFlow/Groovy 僵尸依赖验证

- **LiteFlow**：在 pms-rules 源码（仅 AviatorUtils.java）中无任何 LiteFlow API 调用，确认为僵尸依赖 ✅
- **Groovy**：在 pms-rules 源码中无任何 Groovy API 调用，确认为僵尸依赖 ✅
- 文档 `dependency-analysis.md` 对此描述准确

---

## 6. Aviator 语法验证

### 6.1 Aviator 版本

父 `PMS/pom.xml` 第 129 行：`<aviator.version>5.4.3</aviator.version>`，与文档描述一致 ✅

### 6.2 字符串引号语法（本次修正）

**修正前**：`aviator-syntax.md` 和 `troubleshooting.md` 声称"Aviator 字符串仅支持单引号，不支持双引号"。

**验证结果**：通过 Aviator 官方文档和 GitHub README 确认，Aviator 5.x **同时支持单引号和双引号**字符串：
- 单引号 `'hello'`：纯字面量字符串
- 双引号 `"hello"`：支持 `#{}` 插值的字符串

Aviator GitHub README 快速示例中直接使用了双引号：`p("Hello, AviatorScript!");`

**修正后**：已修正 `aviator-syntax.md` 和 `troubleshooting.md` 中的描述。

> **注**：`aviator-syntax.md` 原文存在内部矛盾——声称"不支持双引号"，但同文档 Map 创建示例（`{"name": "张三"}`）和 string 函数示例（`string.length("hello")`）均使用了双引号。

---

## 7. 本次修正的问题

### 7.1 修正清单

| 序号 | 文档 | 问题类型 | 问题 | 修正内容 |
|------|------|----------|------|----------|
| 1 | `02-modules/aviator-syntax.md` | 事实错误 | 第 16 行声称字符串"不支持双引号" | 改为"单引号/双引号"，注明双引号支持 `#{}` 插值 |
| 2 | `05-standards/troubleshooting.md` | 事实错误 | 问题2 声称"Aviator 字符串仅支持单引号，不支持双引号" | 改为说明 Aviator 支持两种引号，单引号推荐用于 Java 嵌入避免转义 |
| 3 | `02-modules/rules-engine.md` | 误导描述 | 第 9 行仍称"提供基于 Aviator、LiteFlow、Groovy 的规则计算能力" | 改为"提供基于 Aviator"，添加僵尸依赖提示 |
| 4 | `02-modules/rules-engine.md` | 签名错误 | 自定义函数示例中 `invoke` 签名多了 `AviatorEvaluatorInstance instance` 参数 | 改为正确的 `invoke(Map<String, Object> env, AviatorObject... args)` |
| 5 | `01-architecture/system-architecture.md` | 代码遗漏 | AviatorUtils 代码示例缺少 `getCacheSize()` 方法 | 补充 `getCacheSize()` 方法 |

### 7.2 修正原则

- 所有修正均基于源码事实和 Aviator 官方文档，未引入新的虚构内容
- 保持文档原有结构和风格
- 修正后的内容与 `aviator-utils.md`、`class-reference.md` 等权威文档一致

---

## 8. 已验证准确的关键文档

以下文档经交叉验证确认准确，无需修正：

| 文档 | 验证内容 | 结论 |
|------|----------|------|
| `02-modules/aviator-utils.md` | 5 个方法签名、StaticHolder 代码、exceute 内部实现、setCacheSize/resetAviator 实现 | ✅ 全部与源码一致 |
| `02-modules/class-reference.md` | 类定义、字段、内部类、方法签名、import 清单、完整源码、三处版本差异 | ✅ 全部准确 |
| `02-modules/rule-business-integration.md` | 7 个集成点、11 个代码调用点、行号、变量环境、异常处理 | ✅ 全部准确 |
| `01-architecture/aviator-engine.md` | Aviator 5.4.3 版本、Holder 单例、FunctionMissing 机制、缓存 Key 生成 | ✅ 全部准确 |
| `01-architecture/dependency-analysis.md` | 9 个依赖声明、LiteFlow/Groovy 僵尸依赖、三处重复定义、MD5 实现差异 | ✅ 全部准确 |
| `05-standards/troubleshooting.md` | `exceute` 拼写错误说明、11 个调用点引用 | ✅ 准确（引号描述已修正） |
| `04-mapping/rule-usage-matrix.md` | 11 个调用点矩阵、行号、变量环境、异常处理 | ✅ 全部准确 |

---

## 9. 文档覆盖完整性

### 9.1 文档与源文件对应关系

| 源文件 | 文档覆盖 |
|--------|----------|
| `AviatorUtils.java` | ✅ `aviator-utils.md`（详解）、`class-reference.md`（参考）、`rules-engine.md`（概述）、`code-examples.md`（示例）、`system-architecture.md`（架构） |

### 9.2 知识库结构

pms-rules 模块仅有 1 个 Java 源文件（66 行），知识库共 24 个文档文件，覆盖全面，无遗漏。

---

## 10. 已知遗留问题（源码层面，非文档问题）

以下为源码层面的问题，文档已正确记录，但未修改源码：

| 问题 | 严重程度 | 文档记录位置 |
|------|----------|--------------|
| 方法名 `exceute` 拼写错误 | 低 | `aviator-utils.md`、`class-reference.md`、`rules-engine.md`、`troubleshooting.md` |
| 三处 AviatorUtils 重复定义 | 中 | `dependency-analysis.md`、`class-reference.md` |
| LiteFlow/Groovy 僵尸依赖 | 中 | `dependency-analysis.md`、`rule-engine-comparison.md`、`system-architecture.md` |
| `cacheSize` 非 volatile | 低 | `aviator-utils.md`、`class-reference.md` |
| `StaticHolder.INSTANCE` 非 volatile | 低 | `aviator-utils.md`、`class-reference.md` |
| 调用方多处使用 `e.printStackTrace()` | 中 | `rule-business-integration.md`、`troubleshooting.md` |

---

## 11. 审查签字

| 审查项 | 状态 | 日期 |
|--------|------|------|
| 源码一致性验证 | ✅ 通过 | 2026-06-25 |
| 调用点覆盖验证（11 个行号全量比对） | ✅ 通过 | 2026-06-25 |
| 三处重复定义验证（源码逐一读取） | ✅ 通过 | 2026-06-25 |
| 依赖声明验证（pom.xml + 父 pom） | ✅ 通过 | 2026-06-25 |
| Aviator 语法验证（官方文档对照） | ✅ 通过 | 2026-06-25 |
| 事实错误修正（5 处） | ✅ 完成 | 2026-06-25 |
| 文档覆盖完整性 | ✅ 通过 | 2026-06-25 |
