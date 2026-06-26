# pms-rules 模块架构文档

## 1. 模块概述

pms-rules 是 PMS 系统的规则引擎模块，提供基于 Aviator 的规则计算能力。

> ⚠️ pom.xml 中声明了 LiteFlow 和 Groovy 依赖，但源码中均未实际使用，属僵尸依赖。详见 `dependency-analysis.md` 和 `rule-engine-comparison.md`。

- **包名**：`com.dp.plat.rules`
- **打包类型**：jar
- **职责**：规则定义、规则执行、规则管理

---

## 2. 技术栈

| 技术 | 版本 | 用途 | 实际使用 |
|------|------|------|----------|
| **Aviator** | 5.4.3 | 轻量级表达式引擎 | ✅ 已使用（AviatorUtils） |
| **LiteFlow** | 2.15.0 | 规则编排引擎 | ❌ 声明未使用（僵尸依赖） |
| **Groovy** | 3.0.19 | 动态脚本语言 | ❌ 声明未使用（僵尸依赖） |

---

## 3. 目录结构

```
pms-rules/src/main/java/com/dp/plat/rules/
└── util/
    └── AviatorUtils.java    # Aviator 规则引擎工具
```

---

## 4. 核心功能

### 4.1 Aviator 规则引擎

**AviatorUtils**：封装 Aviator 表达式引擎，支持：
- 数学表达式计算
- 逻辑表达式判断
- 字符串处理
- 自定义函数扩展

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
    
    public static Object exceute(String script, Map<String, Object> env) {
        AviatorEvaluatorInstance evaluatorInstance = getInstance();
        String cacheKey = DigestUtils.md5DigestAsHex(script.getBytes());
        Expression expression = evaluatorInstance.compile(cacheKey, script, true);
        Object result = expression.execute(env);
        return result;
    }
    
    public static int getCacheSize() {
        return cacheSize;
    }
    
    public static void setCacheSize(int cacheSize) {
        AviatorUtils.cacheSize = cacheSize;
        getInstance().useLRUExpressionCache(cacheSize);
    }
    
    public static void resetAviator() {
        getInstance().clearExpressionCache();
        StaticHolder.INSTANCE = StaticHolder.newInstance();
    }
}
```

---

## 5. 使用示例

### 5.1 数学计算

```java
// 数学计算
Object result = AviatorUtils.exceute("1 + 2 * 3", new HashMap<>());
// 结果: 7
```

### 5.2 逻辑判断

```java
// 逻辑判断
Map<String, Object> env = new HashMap<>();
env.put("age", 25);
Object result = AviatorUtils.exceute("age >= 18 && age <= 60", env);
// 结果: true
```

### 5.3 变量替换

```java
// 变量替换
Map<String, Object> env = new HashMap<>();
env.put("price", 100);
env.put("quantity", 5);
Object result = AviatorUtils.exceute("price * quantity", env);
// 结果: 500
```

### 5.4 字符串处理

```java
// 字符串处理
Map<String, Object> env = new HashMap<>();
env.put("name", "张三");
Object result = AviatorUtils.exceute("string.join('你好, ', name)", env);
// 结果: "你好, 张三"
```

---

## 6. 与其他模块集成

pms-rules 被 pms-ext-fp 依赖：

```xml
<dependency>
    <groupId>com.dp.plat</groupId>
    <artifactId>pms-rules</artifactId>
    <version>${project.version}</version>
</dependency>
```
