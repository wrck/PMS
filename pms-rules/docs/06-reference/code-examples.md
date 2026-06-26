# pms-rules 代码示例与参考

---

## 1. AviatorUtils 工具类示例

### 1.1 基本使用

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

### 1.2 数学计算

```java
// 数学计算
Object result = AviatorUtils.exceute("1 + 2 * 3", new HashMap<>());
// 结果: 7
```

### 1.3 逻辑判断

```java
// 逻辑判断
Map<String, Object> env = new HashMap<>();
env.put("age", 25);
Object result = AviatorUtils.exceute("age >= 18 && age <= 60", env);
// 结果: true
```

### 1.4 变量替换

```java
// 变量替换
Map<String, Object> env = new HashMap<>();
env.put("price", 100);
env.put("quantity", 5);
Object result = AviatorUtils.exceute("price * quantity", env);
// 结果: 500
```

### 1.5 字符串处理

```java
// 字符串处理
Map<String, Object> env = new HashMap<>();
env.put("name", "张三");
Object result = AviatorUtils.exceute("string.join('你好, ', name)", env);
// 结果: "你好, 张三"
```

### 1.6 自定义函数

```java
// 注册自定义函数
AviatorUtils.getInstance().addFunction(new AbstractFunction() {
    @Override
    public String getName() {
        return "myFunc";
    }
    
    @Override
    public AviatorObject invoke(AviatorEvaluatorInstance instance, 
                                 Map<String, Object> env, 
                                 AviatorObject... args) {
        String arg1 = (String) args[0].getValue(env);
        return new AviatorString("结果: " + arg1);
    }
});

// 调用自定义函数
Object result = AviatorUtils.exceute("myFunc('test')", new HashMap<>());
```

---

## 2. 缓存管理示例

### 2.1 设置缓存大小

```java
// 设置缓存大小为 200
AviatorUtils.setCacheSize(200);
```

### 2.2 清空缓存

```java
// 清空缓存
AviatorUtils.resetAviator();
```

---

## 3. 异常处理示例

```java
try {
    Object result = AviatorUtils.exceute(expression, env);
} catch (Exception e) {
    log.error("规则执行失败: expression={}", expression, e);
    throw new BusinessException("规则计算失败");
}
```

---

## 4. 性能优化示例

### 4.1 预编译表达式

```java
// 预编译表达式
Expression compiled = AviatorUtils.getInstance().compile(expression, true);

// 多次执行
Object result1 = compiled.execute(env1);
Object result2 = compiled.execute(env2);
```

### 4.2 批量计算

```java
// 批量计算
List<Map<String, Object>> dataList = getDataList();
List<Object> results = new ArrayList<>();

for (Map<String, Object> data : dataList) {
    Object result = AviatorUtils.exceute(expression, data);
    results.add(result);
}
```
