# 接口模板

> 本文档提供 pms-rules 模块中 AviatorUtils 工具类的标准化接口文档模板和使用示例。

---

## 1. AviatorUtils 接口文档模板

### 1.1 接口基本信息

| 项目 | 内容 |
|------|------|
| **接口名称** | 执行 Aviator 表达式 |
| **类名** | `AviatorUtils` |
| **全限定名** | `com.dp.plat.rules.util.AviatorUtils` |
| **方法名** | `exceute` |
| **方法签名** | `public static Object exceute(String script, Map<String, Object> env)` |
| **功能描述** | 编译并执行 Aviator 表达式，返回计算结果 |
| **线程安全** | 是 |
| **缓存** | LRU 缓存，默认容量 100 |

---

### 1.2 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `script` | `String` | 是 | Aviator 表达式字符串，不可为 null |
| `env` | `Map<String, Object>` | 是 | 变量环境 Map，可为空 Map |

#### env 约定变量

| 变量名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `entity` | `Object` / `Map` | 否 | 业务实体，表达式通过 `entity.字段名` 访问 |
| `config` | `Map` | 否 | 当前规则/脚本配置 |
| `configs` | `Map` | 否 | 完整配置集合 |
| `context` | `Object` | 否 | 调用方上下文，可通过 FunctionMissing 调用其方法 |
| `taskVars` | `VariableScope` | 否 | 工作流变量作用域（仅工作流场景） |

---

### 1.3 返回值

| 类型 | 说明 |
|------|------|
| `Object` | 表达式计算结果，类型取决于表达式 |

#### 返回值类型对照

| 表达式类型 | 返回 Java 类型 | 示例 |
|------------|----------------|------|
| 整数运算 | `Long` | `1 + 2` → `3L` |
| 浮点运算 | `Double` | `1.0 + 2` → `3.0` |
| 逻辑运算 | `Boolean` | `1 > 2` → `false` |
| 字符串字面量 | `String` | `'hello'` → `"hello"` |
| List 字面量 | `List`（ArrayList） | `[1, 2, 3]` |
| Map 字面量 | `Map`（HashMap） | `{'a': 1}` |
| nil | `null` | `nil` |

---

### 1.4 异常

| 异常类 | 触发条件 | 处理建议 |
|--------|----------|----------|
| `ExpressionSyntaxErrorException` | 表达式语法错误 | 检查括号、引号、运算符 |
| `CompileExpressionErrorException` | 表达式编译失败 | 检查表达式是否为空 |
| `ExpressionRuntimeException` | 运行时错误 | 检查变量、类型 |
| `FunctionNotFoundException` | 函数未找到 | 检查函数名或注册函数 |
| `ReflectorException` | 反射调用失败 | 检查 context 方法签名 |

---

### 1.5 使用示例

#### 示例1：条件判断

```java
// 场景：判断用户是否成年
String expression = "age >= 18 && age <= 60";

Map<String, Object> env = new HashMap<>();
env.put("age", 25);

Object result = AviatorUtils.exceute(expression, env);
boolean isAdult = Boolean.TRUE.equals(result);
// isAdult = true
```

#### 示例2：数学计算

```java
// 场景：计算折扣后价格
String expression = "price * quantity * (1 - discount)";

Map<String, Object> env = new HashMap<>();
env.put("price", 100);
env.put("quantity", 5);
env.put("discount", 0.1);

Object result = AviatorUtils.exceute(expression, env);
// result = 450.0 (Double)
```

#### 示例3：发票类型判断（PMS 实际场景）

```java
// 场景：判断文件是否为发票类型
String condition = "entity.entity.invoice_number != nil && entity.entity.amount > 0";

Map<String, Object> invoice = new HashMap<>();
invoice.put("invoice_number", "INV001");
invoice.put("amount", 1000);

Map<String, Object> env = new HashMap<>();
env.put("entity", Collections.singletonMap("entity", invoice));

Object result = AviatorUtils.exceute(condition, env);
boolean isInvoice = Boolean.TRUE.equals(result);
// isInvoice = true
```

#### 示例4：调用 context 方法（PMS 实际场景）

```java
// 场景：售前项目自动设置项目类型
String script = "setProjectType(entity.presales, '销售测试')";

Map<String, Object> entity = new HashMap<>();
entity.put("presales", presales);

Map<String, Object> env = new HashMap<>();
env.put("entity", entity);
env.put("config", config);
env.put("context", this);  // this 为 AutoStartPresalesProjectJob

Object result = AviatorUtils.exceute(script, env);
// 调用 this.setProjectType(presales, "销售测试")
```

#### 示例5：预编译表达式

```java
// 场景：高频调用，预编译提升性能
String expression = "entity.amount > threshold";

// 预编译（一次）
Expression compiled = AviatorUtils.getInstance().compile(expression, true);

// 多次执行
Map<String, Object> env1 = new HashMap<>();
env1.put("entity", entity1);
env1.put("threshold", 1000);
Object result1 = compiled.execute(env1);

Map<String, Object> env2 = new HashMap<>();
env2.put("entity", entity2);
env2.put("threshold", 2000);
Object result2 = compiled.execute(env2);
```

---

## 2. 缓存管理接口模板

### 2.1 setCacheSize

| 项目 | 内容 |
|------|------|
| **方法签名** | `public static void setCacheSize(int cacheSize)` |
| **功能** | 设置表达式缓存容量 |
| **调用时机** | 仅建议应用启动时调用 |
| **线程安全** | ⚠️ 不安全 |

```java
// 应用启动时配置
AviatorUtils.setCacheSize(200);
```

### 2.2 getCacheSize

| 项目 | 内容 |
|------|------|
| **方法签名** | `public static int getCacheSize()` |
| **功能** | 获取当前缓存容量 |
| **返回值** | 当前缓存容量 |

```java
int size = AviatorUtils.getCacheSize();
// 默认 100
```

### 2.3 resetAviator

| 项目 | 内容 |
|------|------|
| **方法签名** | `public static void resetAviator()` |
| **功能** | 重置 Aviator 实例，清空缓存 |
| **调用时机** | 仅建议维护窗口调用 |
| **线程安全** | ⚠️ 不安全 |

```java
// 维护窗口重置
AviatorUtils.resetAviator();
```

---

## 3. 标准调用模板

### 3.1 条件判断模板

```java
/**
 * 规则条件判断模板
 * @param condition 条件表达式
 * @param entity 业务实体
 * @param config 规则配置
 * @return 条件是否满足
 */
public boolean checkRule(String condition, Object entity, Map<String, Object> config) {
    if (StringUtils.isBlank(condition)) {
        return true;  // 无条件默认启用
    }
    
    Map<String, Object> env = new HashMap<>();
    env.put("entity", entity);
    env.put("config", config);
    env.put("context", this);
    
    try {
        Object result = AviatorUtils.exceute(condition, env);
        return Boolean.TRUE.equals(result);
    } catch (Exception e) {
        log.error("规则条件判断失败: condition={}", condition, e);
        return false;  // 异常时默认不启用
    }
}
```

### 3.2 脚本执行模板

```java
/**
 * 规则脚本执行模板
 * @param scripts 脚本配置 JSON
 * @param entity 业务实体
 * @return 执行结果列表
 */
public List<Object> execScripts(String scripts, Map<String, Object> entity) {
    if (StringUtils.isBlank(scripts)) {
        return Collections.emptyList();
    }
    
    Map<String, Object> env = new HashMap<>();
    env.put("entity", entity);
    env.put("context", this);
    
    Map<String, Object> scriptMap = JSON.parseObject(scripts, MapTypeReference);
    List<Object> results = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    
    for (Object value : scriptMap.values()) {
        Map<String, Object> script = (Map<String, Object>) value;
        try {
            if (ObjectUtils.isNotEmpty(script.get("script"))) {
                // 检查条件
                boolean enable = !script.containsKey("condition") || 
                    checkRule((String) script.get("condition"), entity, script);
                if (enable) {
                    env.put("config", script);
                    Object result = AviatorUtils.exceute(
                        String.valueOf(script.get("script")), env);
                    results.add(result);
                }
            }
        } catch (Exception e) {
            log.error("脚本执行失败: script={}", script.get("script"), e);
            errors.add(e.getMessage());
        }
    }
    
    if (!errors.isEmpty()) {
        throw new CustomRuntimeException("规则脚本执行发生错误，请检查日志！");
    }
    
    return results;
}
```

### 3.3 变量提取模板

```java
/**
 * 表达式变量名提取模板
 * @param expressionText 表达式文本
 * @return 变量名列表
 */
public List<String> extractVariableNames(String expressionText) {
    Expression expr = AviatorUtils.getInstance().compile(expressionText);
    return expr.getVariableNames();
}
```

---

## 4. 异常处理模板

### 4.1 完整异常处理

```java
public Object safeExceute(String script, Map<String, Object> env) {
    try {
        return AviatorUtils.exceute(script, env);
    } catch (ExpressionSyntaxErrorException e) {
        log.error("表达式语法错误: script={}", script, e);
        throw new BusinessException("规则表达式语法错误");
    } catch (CompileExpressionErrorException e) {
        log.error("表达式编译失败: script={}", script, e);
        throw new BusinessException("规则表达式编译失败");
    } catch (ExpressionRuntimeException e) {
        log.error("表达式运行时错误: script={}, env={}", script, env, e);
        throw new BusinessException("规则执行失败: " + e.getMessage());
    } catch (Exception e) {
        log.error("规则执行未知错误: script={}", script, e);
        throw new BusinessException("规则执行未知错误");
    }
}
```

### 4.2 容错异常处理（PMS 常用模式）

```java
public boolean safeCheck(String condition, Map<String, Object> env) {
    try {
        Object result = AviatorUtils.exceute(condition, env);
        return Boolean.TRUE.equals(result);
    } catch (Exception e) {
        log.error("规则判断失败，回退到默认值: condition={}", condition, e);
        return false;  // 容错：异常时返回 false
    }
}
```
