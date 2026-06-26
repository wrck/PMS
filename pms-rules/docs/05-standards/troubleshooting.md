# 故障排查指南

> 本文档汇总 pms-rules 模块中 Aviator 表达式引擎的常见问题、故障现象、根因分析和解决方案。

---

## 1. 问题分类索引

### 1.1 按问题类型分类

| 类型 | 常见问题 | 严重程度 | 参见章节 |
|------|----------|----------|----------|
| 语法错误 | 表达式语法不合法、括号不匹配 | 中 | 2.1 |
| 变量问题 | 变量未定义、nil 访问 | 高 | 2.2 |
| 类型异常 | 类型转换失败、数值溢出 | 中 | 2.3 |
| 方法调用 | 方法名拼写错误、方法不存在 | 高 | 2.4 |
| 性能问题 | 表达式执行慢、缓存未命中 | 中 | 2.5 |
| 并发问题 | resetAviator 导致不一致 | 低 | 2.6 |

### 1.2 按异常类型分类

| 异常类 | 常见场景 | 排查方向 |
|--------|----------|----------|
| `ExpressionSyntaxErrorException` | 表达式语法错误 | 检查表达式语法 |
| `CompileExpressionErrorException` | 表达式编译失败 | 检查表达式合法性 |
| `AviatorRuntimeException` | 运行时错误 | 检查变量、类型、方法 |
| `NullPointerException` | nil 变量访问 | 检查变量是否为 nil |
| `ClassCastException` | 类型转换失败 | 检查返回值类型 |
| `ReflectiveOperationException` | 反射方法调用失败 | 检查方法名和参数 |

---

## 2. 常见问题与解决方案

### 2.1 表达式语法错误

#### 问题1：括号不匹配

**现象**：

```
com.googlecode.aviator.exception.ExpressionSyntaxErrorException: could not compile expression
```

**根因**：表达式中括号未成对匹配。

**排查**：

```java
// ❌ 错误：缺少右括号
"entity.entity.amount > 0 && (entity.entity.type == '发票'"

// ✅ 正确
"entity.entity.amount > 0 && (entity.entity.type == '发票')"
```

**解决**：检查括号配对，使用 IDE 的括号匹配功能。

---

#### 问题2：Java 字符串嵌套引号错误

**现象**：表达式编译失败或 Java 代码编译错误。

**根因**：Aviator 表达式同时支持单引号和双引号字符串，但在 Java 字符串字面量中嵌套双引号需要转义，容易出错。

**排查**：

```java
// ❌ 易错：Java 字符串中嵌套双引号需转义，容易遗漏
"entity.name == \"张三\""

// ✅ 推荐：Aviator 表达式中使用单引号，避免 Java 转义
"entity.name == '张三'"
```

**解决**：在 Java 代码中嵌入 Aviator 表达式时，推荐使用单引号定义字符串字面量，避免 Java 转义问题。Aviator 双引号字符串支持 `#{}` 插值，单引号为纯字面量。

---

#### 问题3：运算符使用错误

**现象**：表达式编译失败或结果异常。

**根因**：误用 Java 运算符。

**排查**：

```java
// ❌ 错误：使用 Java 的 &&（Aviator 也支持，但注意 ||）
// Aviator 中逻辑或是 ||，不是 or

// ❌ 错误：使用 == 比较字符串引用
// Aviator 的 == 是值比较，可以用于字符串

// ❌ 错误：使用 ! 进行非空判断
"!entity.name"  // 这是逻辑非，不是非空判断

// ✅ 正确：非空判断
"entity.name != nil"
```

---

### 2.2 变量问题

#### 问题4：变量未定义

**现象**：

```
com.googlecode.aviator.exception.ExpressionRuntimeException: Could not find variable
```

**根因**：表达式中引用的变量未在 env Map 中提供。

**排查**：

```java
// 表达式引用了 amount 变量
"amount > 1000"

// 但 env 中未提供
Map<String, Object> env = new HashMap<>();
env.put("entity", entity);  // ❌ 未提供 amount
AviatorUtils.exceute("amount > 1000", env);  // 报错
```

**解决**：

```java
// ✅ 方案一：在 env 中提供变量
env.put("amount", entity.getAmount());

// ✅ 方案二：修改表达式，通过 entity 访问
"entity.amount > 1000"
```

---

#### 问题5：nil 变量访问

**现象**：`NullPointerException` 或表达式结果异常。

**根因**：变量值为 nil，但表达式未做 nil 判断。

**排查**：

```java
// ❌ 危险：entity 可能为 nil
"entity.amount > 0"

// ✅ 安全：先判断非 nil
"entity != nil && entity.amount > 0"
```

**PMS 实际案例**：

发票判断中 `entity` 是嵌套结构 `{"entity": invoice}`，需通过 `entity.entity.xxx` 访问：

```java
// ❌ 错误：直接访问 entity.invoice_number
"entity.invoice_number != nil"

// ✅ 正确：通过嵌套访问
"entity.entity.invoice_number != nil"
```

---

#### 问题6：工作流变量缺失

**现象**：Activiti 多实例节点完成时报变量缺失错误。

**根因**：多实例完成条件表达式中引用的变量未在流程变量中提供。

**排查**：`WorkflowUtil.callBackProcess` 中通过 AviatorUtils 提取变量名并填充 null：

```java
// 提取表达式中的变量名
Expression expr = AviatorUtils.getInstance().compile(expressionText);
List<String> vars = expr.getVariableNames();
// 为所有变量填充 null，避免 Activiti 报错
for (String var : vars) {
    map.put(var, null);
}
```

**解决**：确保 `WorkflowUtil` 的变量提取逻辑正常工作。

---

### 2.3 类型异常

#### 问题7：类型转换失败

**现象**：`ClassCastException` 或结果类型异常。

**根因**：Aviator 返回值类型与预期不符。

**排查**：

```java
// Aviator 整数运算返回 Long
Object result = AviatorUtils.exceute("1 + 2", env);
// result 是 Long(3)，不是 Integer(3)

// 强转为 Integer 会报错
Integer i = (Integer) result;  // ❌ ClassCastException

// ✅ 正确处理
Long l = (Long) result;
// 或
int i = ((Number) result).intValue();
```

**类型对照表**：

| 表达式 | 返回类型 |
|--------|----------|
| `1 + 2` | `Long` |
| `1.0 + 2` | `Double` |
| `1 > 2` | `Boolean` |
| `'hello'` | `String` |
| `[1, 2, 3]` | `List`（ArrayList） |
| `{'a': 1}` | `Map`（HashMap） |

---

#### 问题8：数值溢出

**现象**：大数计算结果不正确。

**根因**：long 类型溢出。

**排查**：

```java
// ❌ long 溢出
"9223372036854775807 + 1"  // Long.MAX_VALUE + 1，溢出为负数

// ✅ 使用 BigInteger
"9223372036854775807N + 1N"  // 使用 N 后缀
```

---

### 2.4 方法调用问题

#### 问题9：方法名拼写错误 `exceute`

**现象**：编译错误，找不到方法 `execute`。

**根因**：AviatorUtils 的方法名 `exceute` 是历史遗留拼写错误（应为 `execute`）。

**排查**：

```java
// ❌ 错误：使用正确拼写
AviatorUtils.execute(script, env);  // 编译错误

// ✅ 正确：使用错误拼写（历史遗留）
AviatorUtils.exceute(script, env);
```

> **注意**：此拼写错误在三处 AviatorUtils 定义中均存在，修正需同步修改所有调用点。详见 `01-architecture/dependency-analysis.md`。

---

#### 问题10：FunctionMissing 反射方法不存在

**现象**：

```
com.googlecode.aviator.exception.FunctionNotFoundException
```

**根因**：表达式中调用的 Java 静态方法不存在或参数不匹配。

**排查**：

```java
// ❌ 错误：方法名拼写错误
"Math.maxx(3, 5)"  // Math.maxx 不存在

// ✅ 正确
"Math.max(3, 5)"

// ❌ 错误：参数类型不匹配
"Math.round('hello')"  // String 无法转为 double

// ✅ 正确
"Math.round(3.6)"
```

**PMS 实际案例**：

售前项目自动启动脚本中调用 `setProjectType(presales, projectType)`，依赖 `context` 对象有此方法：

```java
// 表达式
"setProjectType(presales, '销售测试')"

// context 对象（AutoStartPresalesProjectJob）需有 public setProjectType 方法
// 若方法不存在或参数不匹配，FunctionMissing 反射会失败
```

---

#### 问题11：调用实例方法失败

**现象**：FunctionMissing 无法调用实例方法。

**根因**：`JavaMethodReflectionFunctionMissing` 仅支持静态方法反射。

**排查**：

```java
// ❌ 错误：尝试调用实例方法
"presales.setProjectType('销售测试')"  // setProjectType 是实例方法

// ✅ 正确：通过 context 调用（context 作为隐式接收者）
"setProjectType(presales, '销售测试')"
// 等价于 context.setProjectType(presales, '销售测试')
```

---

### 2.5 性能问题

#### 问题12：表达式执行慢

**现象**：表达式执行耗时异常高。

**根因**：缓存未命中，每次都重新编译。

**排查**：

```java
// 检查是否动态拼接表达式（每次不同）
// ❌ 每次拼接不同字符串
String condition = "amount > " + threshold;  // 每次不同，缓存未命中

// ✅ 参数化
String condition = "amount > threshold";
env.put("threshold", threshold);  // 缓存命中
```

**解决**：参数化表达式，避免动态拼接。详见 `05-standards/performance-optimization.md`。

---

#### 问题13：缓存命中率低

**现象**：表达式缓存频繁淘汰。

**根因**：缓存容量不足或表达式种类过多。

**排查**：

```java
// 检查缓存大小
int cacheSize = AviatorUtils.getCacheSize();  // 默认 100

// 若表达式种类 > 100，需增大缓存
AviatorUtils.setCacheSize(200);
```

---

### 2.6 并发问题

#### 问题14：resetAviator 导致不一致

**现象**：resetAviator 调用后，短暂出现表达式执行失败或性能下降。

**根因**：`resetAviator` 重新创建实例，`StaticHolder.INSTANCE` 非 volatile，其他线程可能暂时使用旧实例（缓存已清空）。

**排查**：

```java
// resetAviator 实现
public static void resetAviator() {
    getInstance().clearExpressionCache();      // 清空旧实例缓存
    StaticHolder.INSTANCE = StaticHolder.newInstance();  // 创建新实例
    // ⚠️ 其他线程可能仍持有旧 INSTANCE 引用
}
```

**解决**：
- 仅在维护窗口调用 `resetAviator`
- 或将 `StaticHolder.INSTANCE` 改为 `volatile`

---

## 3. 排查工具与方法

### 3.1 表达式调试

```java
// 打印表达式和 env 用于调试
public static Object debugExceute(String script, Map<String, Object> env) {
    log.debug("执行表达式: {}", script);
    log.debug("变量环境: {}", env);
    try {
        Object result = AviatorUtils.exceute(script, env);
        log.debug("执行结果: {} ({})", result, result != null ? result.getClass() : "null");
        return result;
    } catch (Exception e) {
        log.error("表达式执行失败: script={}", script, e);
        throw e;
    }
}
```

### 3.2 表达式验证

```java
// 表达式保存前验证
public void validateExpression(String expression) {
    try {
        // 尝试编译（不执行）
        AviatorUtils.getInstance().compile(expression, true);
    } catch (ExpressionSyntaxErrorException e) {
        throw new IllegalArgumentException("表达式语法错误: " + e.getMessage());
    } catch (CompileExpressionErrorException e) {
        throw new IllegalArgumentException("表达式编译失败: " + e.getMessage());
    }
}
```

### 3.3 变量检查

```java
// 提取表达式引用的变量名
Expression expr = AviatorUtils.getInstance().compile(expression);
List<String> requiredVars = expr.getVariableNames();
log.debug("表达式需要的变量: {}", requiredVars);

// 检查 env 是否提供所有变量
for (String var : requiredVars) {
    if (!env.containsKey(var)) {
        log.warn("变量未提供: {}", var);
    }
}
```

---

## 4. PMS 业务故障案例

### 案例1：发票判断条件不生效

**现象**：发票类型判断始终返回默认值，配置的条件表达式未生效。

**根因**：`entity` 是嵌套结构 `{"entity": invoice}`，但表达式中直接使用了 `entity.invoice_number`。

**解决**：修改表达式为 `entity.entity.invoice_number != nil`。

---

### 案例2：项目状态更新脚本执行报错

**现象**：`ProjectStateUpdateAspect.execScripts` 抛出 `CustomRuntimeException`。

**根因**：配置的脚本中调用了 `context` 不存在的方法。

**排查步骤**：
1. 查看日志中的 `logError("规则脚本执行发生错误：", e)`
2. 确认 `context`（ProjectStateUpdateAspect）是否有表达式中调用的方法
3. 确认方法参数类型是否匹配

---

### 案例3：售前项目类型未自动设置

**现象**：售前项目自动启动后，项目类型未被正确设置。

**根因**：脚本中 `setProjectType(presales, projectType)` 的 `presales` 变量未在 env 中提供。

**排查**：

```java
// 检查 env 构建
Map<String, Object> env = new HashMap<>();
env.put("entity", entity);  // entity 包含 presales
env.put("config", config);
env.put("context", this);

// 表达式中应通过 entity.presales 访问
// ❌ "setProjectType(presales, '销售测试')"  // presales 未定义
// ✅ "setProjectType(entity.presales, '销售测试')"
```

---

### 案例4：工作流多实例节点报变量缺失

**现象**：Activiti 多实例节点完成时报 `Unknown property used in expression`。

**根因**：`WorkflowUtil` 的变量提取逻辑未执行或失败。

**排查**：
1. 确认 `WorkflowUtil.callBackProcess` 是否被调用
2. 检查 `completionCondition` 表达式是否正确提取变量名
3. 确认提取的变量是否已填充 null 值

---

## 5. FAQ

### Q1：为什么方法名是 `exceute` 而不是 `execute`？

**A**：历史遗留拼写错误，三处 AviatorUtils 定义均存在。修正需同步修改所有调用点（11 个代码调用点 + 测试代码），风险较高，暂未修正。

### Q2：为什么 pms-rules 有 LiteFlow 和 Groovy 依赖但未使用？

**A**：规划阶段预留的规则编排和动态脚本能力，最终被 Aviator 替代。详见 `01-architecture/rule-engine-comparison.md`。

### Q3：为什么有三处 AviatorUtils 定义？

**A**：pms-rules、core、PMS-struts 各有一份副本，唯一差异是 MD5 实现方式。详见 `01-architecture/dependency-analysis.md`。

### Q4：表达式执行失败会影响业务吗？

**A**：取决于调用点的异常处理。条件判断类（checkRule/checkAssignee）失败返回 false，脚本执行类（execScripts）失败抛异常中断业务。详见 `04-mapping/rule-usage-matrix.md`。
