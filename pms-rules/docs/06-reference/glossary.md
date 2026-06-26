# 术语表

> 本文档解释 pms-rules 模块及规则引擎相关的技术术语和业务概念。

---

## 1. 规则引擎术语

### Aviator（表达式引擎）

Aviator 是一个高性能、轻量级的 Java 表达式求值器，由 killme2008（林子鼎）开发。pms-rules 模块使用 Aviator 5.4.3 版本作为规则引擎，封装为 `AviatorUtils` 工具类。Aviator 支持算术运算、逻辑判断、字符串处理、集合操作和自定义函数扩展。

### AviatorEvaluator（求值器）

Aviator 的核心类，提供表达式编译和执行的全局入口。`AviatorEvaluator.getInstance()` 返回全局单例实例。AviatorUtils 通过 `StaticHolder` 持有此实例并配置 LRU 缓存和 FunctionMissing。

### AviatorEvaluatorInstance（求值器实例）

Aviator 5.x 引入的求值器实例类，支持创建多个独立配置的实例。AviatorUtils 使用全局单例实例，配置了 LRU 缓存（容量 100）和 `JavaMethodReflectionFunctionMissing`。

### Expression（表达式对象）

Aviator 编译后的表达式对象，可重复执行。通过 `AviatorEvaluatorInstance.compile(script)` 获取。支持 `execute(env)` 执行和 `getVariableNames()` 提取变量名。

### LRU 缓存（LRU Cache）

最近最少使用缓存策略。AviatorUtils 默认启用 LRU 表达式缓存，容量 100。当缓存满时，淘汰最近最少使用的编译后表达式。

### FunctionMissing（函数缺失回调）

Aviator 的扩展机制。当表达式中调用的函数未注册时，触发 FunctionMissing 回调。AviatorUtils 配置了 `JavaMethodReflectionFunctionMissing`，通过反射查找并调用 Java 静态方法。

### JavaMethodReflectionFunctionMissing

Aviator 内置的 FunctionMissing 实现，通过 Java 反射机制调用静态方法。当表达式中调用 `Math.max(a, b)` 时，会反射调用 `java.lang.Math.max`。仅支持静态方法，不支持实例方法。

### env（变量环境）

Aviator 表达式执行时的变量环境，类型为 `Map<String, Object>`。表达式中的变量名从 env 中查找对应值。PMS 中约定 env 包含 `entity`（业务实体）、`config`（规则配置）、`context`（调用方上下文）等变量。

### 表达式编译（Compile）

将表达式字符串解析为可执行的 Expression 对象的过程。编译开销较高，因此 AviatorUtils 启用 LRU 缓存避免重复编译。

### 表达式执行（Execute）

对已编译的 Expression 对象传入 env 并计算结果的过程。执行开销低，可并发调用。

---

## 2. PMS 业务术语

### 规则（Rule）

PMS 中用于描述业务条件的配置项。规则包含 `condition`（启用条件表达式）和 `script`（执行脚本表达式）。规则存储在系统参数表或业务配置 JSON 中。

### 条件判断（Condition Check）

使用 Aviator 表达式进行布尔判断的业务场景。表达式返回 Boolean 值，决定规则是否启用。PMS 中有 7 个条件判断调用点（发票判断、状态更新条件、审批人条件等）。

### 脚本执行（Script Execution）

使用 Aviator 表达式执行业务操作的场景。表达式可调用 `context` 对象的方法（通过 FunctionMissing 反射），如 `setProjectType(presales, '销售测试')`。PMS 中有 3 个脚本执行调用点。

### 规则配置（Rule Config）

存储规则表达式的 JSON 配置。典型结构：

```json
{
  "scripts": {
    "script1": {
      "condition": "entity.projectState == 30",
      "script": "entity.projectState = 31"
    }
  }
}
```

### 系统参数（System Parameter）

存储在 `fnd_basic_data` 表中的配置项。部分规则表达式通过系统参数配置，如分包发票判断条件 `SUBCONTRACT_INSPECTION_DELIVERY_CHECK_INVOICE_CONDITION`。

### 发票判断（Invoice Check）

pms-ext-fp 模块的功能，通过 Aviator 表达式判断文件是否为发票类型及发票状态是否满足要求。表达式通过 `entity.entity.xxx` 嵌套结构访问发票字段。

### 项目状态更新（Project State Update）

PMS-struts 模块的功能，通过 AOP 切面 `ProjectStateUpdateAspect` 在项目状态变更时执行规则条件判断和脚本。

### 售前项目自动启动（Presales Auto Start）

PMS-struts 模块的定时任务 `AutoStartPresalesProjectJob`，在售前项目自动启动时执行配置的脚本（如自动设置项目类型）。

### 分包验收（Subcontract Inspection）

PMS-struts 模块的功能，通过 `SubcontractUtil` 和 `SubcontractInspectionListener` 实现分包交付件的发票判断和验收审批人条件判断。

### 派工结算更新（Dispatch Settlement Update）

PMS-springmvc 模块的功能，通过 AOP 切面 `DispatchSettlementUpdateAspect` 在派工结算变更时执行规则条件判断和脚本。

### 工作流多实例变量提取（Workflow Multi-instance Variable Extraction）

PMS-struts 模块 `WorkflowUtil` 中的功能，使用 Aviator 编译 Activiti 多实例节点的完成条件表达式，提取变量名并填充 null 值，避免 Activiti 执行时报变量缺失错误。

---

## 3. 技术术语

### 静态内部类持有者模式（Holder Pattern）

单例模式的实现方式，通过静态内部类延迟初始化。AviatorUtils 使用 `StaticHolder` 类持有 `AviatorEvaluatorInstance` 实例，JVM 保证类加载时的线程安全。

### 僵尸依赖（Zombie Dependency）

在 pom.xml 中声明但代码中未使用的依赖。pms-rules 模块的 `liteflow-spring` 和 `groovy` 依赖属于僵尸依赖。

### 表达式注入（Expression Injection）

安全风险，攻击者通过修改规则配置注入恶意表达式，通过 FunctionMissing 反射调用危险方法。

### 变量沙箱（Variable Sandbox）

安全机制，限制表达式可访问的变量和方法。AviatorUtils 当前未实现变量沙箱，表达式可访问 env 中的所有对象。

### 表达式预编译（Pre-compile）

在执行前预先编译表达式为 Expression 对象，避免每次执行时编译开销。适用于高频调用的固定表达式。

### 缓存命中率（Cache Hit Rate）

表达式缓存命中次数与总调用次数的比率。高命中率表示缓存有效，低命中率可能因表达式动态拼接导致。

---

## 4. 模块与包路径术语

### pms-rules

PMS 项目的规则引擎模块，Maven 模块，打包为 jar。包含 `AviatorUtils` 工具类。被 pms-ext-fp 依赖。

### com.dp.plat.rules.util

pms-rules 模块中 AviatorUtils 的包路径。

### com.dp.plat.core.util

core 模块中 AviatorUtils 副本的包路径。使用 `PasswordUtil.encryptMD5Password` 生成缓存 Key。

### com.dp.plat.util

PMS-struts 模块中 AviatorUtils 副本的包路径。使用 `Md5Util.getMD5` 生成缓存 Key。

### exceute

AviatorUtils 的核心方法名，为 `execute` 的历史遗留拼写错误。三处 AviatorUtils 定义均存在此拼写错误。

---

## 5. 缩写对照

| 缩写 | 全称 | 说明 |
|------|------|------|
| LRU | Least Recently Used | 最近最少使用缓存策略 |
| MD5 | Message Digest Algorithm 5 | 消息摘要算法，用于生成缓存 Key |
| AOP | Aspect-Oriented Programming | 面向切面编程 |
| AST | Abstract Syntax Tree | 抽象语法树 |
| CVE | Common Vulnerabilities and Exposures | 通用漏洞披露 |
| ADR | Architecture Decision Record | 架构决策记录 |
| DAO | Data Access Object | 数据访问对象 |
| DTO | Data Transfer Object | 数据传输对象 |
| JVM | Java Virtual Machine | Java 虚拟机 |
| GC | Garbage Collection | 垃圾回收 |
| PermGen | Permanent Generation | 永久代（JVM 内存区域） |
| Metaspace | Metadata Space | 元空间（JVM 内存区域，Java 8+） |
