# pms-rules 规则引擎模块详细文档

> 本文档深度分析 pms-rules 规则引擎模块的工具类。

---

## 1. 模块概述

pms-rules 是 PMS 系统的规则引擎模块，提供基于 Aviator 的规则计算能力。

> ⚠️ pom.xml 中声明了 LiteFlow 和 Groovy 依赖，但源码中均未实际使用，属僵尸依赖。详见 `01-architecture/dependency-analysis.md`。

### 涉及的工具类列表

| 工具类 | 职责 |
|--------|------|
| `AviatorUtils` | Aviator 规则引擎工具 |

---

## 2. AviatorUtils 详细说明

### 2.1 类结构

```java
public class AviatorUtils {
    private static int cacheSize = 100;
    
    private static class StaticHolder {
        private static AviatorEvaluatorInstance INSTANCE = newInstance();
        
        private static AviatorEvaluatorInstance newInstance() {
            AviatorEvaluatorInstance instance = AviatorEvaluator.getInstance();
            instance.useLRUExpressionCache(cacheSize);
            instance.setCachedExpressionByDefault(true);
            instance.setFunctionMissing(JavaMethodReflectionFunctionMissing.getInstance());
            return instance;
        }
    }
    
    public static AviatorEvaluatorInstance getInstance() {
        return StaticHolder.INSTANCE;
    }
}
```

### 2.2 核心方法

#### `AviatorEvaluatorInstance getInstance()`
- **功能**：获取 Aviator 求值器单例实例

#### `Object exceute(String script, Map<String, Object> env)`
- **功能**：执行 Aviator 表达式
- **参数**：`script` - 表达式, `env` - 变量环境
- **返回值**：Object - 计算结果
- **缓存策略**：使用 LRU 缓存，默认大小 100

> ⚠️ 方法名 `exceute` 为历史遗留拼写错误（应为 `execute`），三处重复定义均存在此问题。

#### `int getCacheSize()`
- **功能**：获取当前缓存容量配置
- **返回值**：int - 当前缓存大小（默认 100）

#### `void setCacheSize(int cacheSize)`
- **功能**：设置缓存大小

#### `void resetAviator()`
- **功能**：重置 Aviator 实例，清空缓存并创建新实例

### 2.3 使用示例

#### 数学计算

```java
Object result = AviatorUtils.exceute("1 + 2 * 3", new HashMap<>());
// 结果: 7
```

#### 逻辑判断

```java
Map<String, Object> env = new HashMap<>();
env.put("age", 25);
Object result = AviatorUtils.exceute("age >= 18 && age <= 60", env);
// 结果: true
```

#### 变量替换

```java
Map<String, Object> env = new HashMap<>();
env.put("price", 100);
env.put("quantity", 5);
Object result = AviatorUtils.exceute("price * quantity", env);
// 结果: 500
```

#### 字符串处理

```java
Map<String, Object> env = new HashMap<>();
env.put("name", "张三");
Object result = AviatorUtils.exceute("string.join('你好, ', name)", env);
// 结果: "你好, 张三"
```

#### 自定义函数

```java
AviatorUtils.getInstance().addFunction(new AbstractFunction() {
    @Override
    public String getName() {
        return "myFunc";
    }
    
    @Override
    public AviatorObject invoke(Map<String, Object> env, 
                                 AviatorObject... args) {
        String arg1 = (String) args[0].getValue(env);
        return new AviatorString("结果: " + arg1);
    }
});

Object result = AviatorUtils.exceute("myFunc('test')", new HashMap<>());
```

---

## 3. 缓存管理

### 3.1 缓存配置

```java
AviatorUtils.setCacheSize(200);
AviatorUtils.resetAviator();
```

### 3.2 缓存策略

- 使用 LRU（最近最少使用）缓存策略
- 默认缓存大小：100
- 缓存 Key：表达式的 MD5 值
- 缓存自动刷新

---

## 4. 异常处理

### 4.1 异常处理示例

```java
try {
    Object result = AviatorUtils.exceute(expression, env);
} catch (Exception e) {
    log.error("规则执行失败: expression={}", expression, e);
    throw new BusinessException("规则计算失败");
}
```

---

## 5. 性能优化

### 5.1 表达式预编译

```java
Expression compiled = AviatorUtils.getInstance().compile(expression, true);
Object result1 = compiled.execute(env1);
Object result2 = compiled.execute(env2);
```

### 5.2 批量计算

```java
List<Map<String, Object>> dataList = getDataList();
List<Object> results = new ArrayList<>();
for (Map<String, Object> data : dataList) {
    Object result = AviatorUtils.exceute(expression, data);
    results.add(result);
}
```
