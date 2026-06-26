# pms-rules 编码规范文档

---

## 1. 规则定义规范

### 1.1 表达式命名

- 使用驼峰命名：`priceCalculation`, `discountRule`
- 表达式简洁明了：`price * quantity * (1 - discount)`
- 避免复杂嵌套：单个表达式不超过 100 个字符

### 1.2 变量命名

- 使用驼峰命名：`price`, `quantity`, `discount`
- 布尔变量：`isApproved`, `isComplete`
- 时间变量：`startTime`, `endTime`

---

## 2. 规则执行规范

### 2.1 缓存管理

```java
// 设置缓存大小
AviatorUtils.setCacheSize(200);

// 清空缓存
AviatorUtils.resetAviator();
```

### 2.2 异常处理

```java
try {
    Object result = AviatorUtils.exceute(expression, env);
} catch (Exception e) {
    log.error("规则执行失败: expression={}", expression, e);
    throw new BusinessException("规则计算失败");
}
```

---

## 3. 性能优化规范

### 3.1 表达式编译

```java
// 预编译表达式
Expression compiled = AviatorUtils.getInstance().compile(expression, true);

// 多次执行
Object result1 = compiled.execute(env1);
Object result2 = compiled.execute(env2);
```

### 3.2 缓存策略

- 使用 LRU 缓存策略
- 默认缓存大小：100
- 缓存 Key：表达式的 MD5 值

---

## 4. 安全规范

### 4.1 输入验证

```java
// 表达式非空校验
if (StringUtils.isBlank(expression)) {
    throw new IllegalArgumentException("表达式不能为空");
}

// 表达式长度限制（防止过长表达式导致编译开销过大）
if (expression.length() > 500) {
    throw new IllegalArgumentException("表达式长度超限");
}

// 执行表达式（异常由调用方处理）
try {
    Object result = AviatorUtils.exceute(expression, env);
} catch (ExpressionSyntaxErrorException | CompileExpressionErrorException e) {
    log.error("表达式语法/编译错误: expression={}", expression, e);
    throw new BusinessException("规则表达式无效");
}
```

> ⚠️ Aviator 表达式由 `JavaMethodReflectionFunctionMissing` 提供反射调用 Java 静态方法的能力，配置人员可通过表达式调用 `Math.*` 等方法。表达式来源应受信任（系统参数表或业务配置 JSON），不应接受终端用户直接输入。详见 `security-practices.md`。

### 4.2 权限控制

```java
// 检查用户权限
if (!user.hasPermission("rule:execute")) {
    throw new PermissionException("无权限执行规则");
}
```
