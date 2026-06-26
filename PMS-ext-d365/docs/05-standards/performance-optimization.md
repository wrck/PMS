# 性能优化

> 本文档基于实际源码分析性能特点与优化建议。

---

## 1. Token 缓存

### 1.1 现有实现

`D365Api` 使用 `volatile TokenResponse cachedToken` 缓存访问令牌：

- **缓存命中**：Token 未过期时直接返回缓存，避免重复请求 Azure AD；
- **过期判断**：基于 `expiresOn`（秒级时间戳）或 `expiresIn` + `timestamp` 计算；
- **缓存失效**：过期或解析异常时清空缓存，重新请求。

### 1.2 性能收益

| 场景 | 无缓存 | 有缓存 |
|------|--------|--------|
| 首次调用 | 1 次 Token 请求 + 1 次业务请求 | 1 次 Token 请求 + 1 次业务请求 |
| 后续调用（Token 有效期内） | 1 次 Token 请求 + 1 次业务请求 | 1 次业务请求 |

D365 Token 有效期通常 1 小时，缓存可显著减少 Azure AD 请求。

### 1.3 优化建议

- **当前已实现缓存**，无需额外优化；
- **多实例部署**：每个 JVM 实例独立缓存，首次调用各需 1 次 Token 请求，可接受；
- **提前刷新**：当前为过期后刷新，可考虑提前 5 分钟刷新以避免请求时刚好过期（当前实现已能处理过期后自动刷新，影响不大）。

---

## 2. HTTP 连接

### 2.1 现有实现

使用 Hutool 的 `HttpUtil.createPost`，底层基于 JDK `HttpURLConnection`。

### 2.2 性能特点

- **无连接池**：每次请求新建连接，TCP 握手开销；
- **无超时设置**：当前代码未设置连接超时和读取超时，网络异常时可能长时间阻塞。

### 2.3 优化建议

```java
// 建议在 post 方法中添加超时设置
httpRequest.timeout(30000);  // 30 秒超时
```

如需进一步优化，可切换到连接池实现：
- Apache HttpClient + PoolingHttpClientConnectionManager；
- OkHttp + ConnectionPool。

> ⚠️ 切换 HTTP 客户端需重写 `post` 方法，并更新 pom.xml 依赖。

---

## 3. 数据库操作

### 3.1 批量操作

> ⚠️ 当前 `D365Api.pushPurchaseOrder` 在循环中逐条调用 `insertSelective`，无批量插入。

```java
// 当前实现（逐条插入）
for (PurchaseLine poLine : purchLines) {
    d365Api.purchaseLineService.insertSelective(poLine);  // 每行一次 INSERT
}
```

### 3.2 优化建议

如需批量插入，可扩展 `AbstractBaseMapper`：

```java
// AbstractBaseMapper 新增方法
int insertBatch(List<T> list);
```

```xml
<!-- Mapper XML 新增批量插入 -->
<insert id="insertBatch" parameterType="java.util.List">
    insert into dp_erp_purchase_order_line (headerId, purchId, lineNum, ...)
    values
    <foreach collection="list" item="item" separator=",">
        (#{item.headerId}, #{item.purchId}, #{item.lineNum}, ...)
    </foreach>
</insert>
```

> ⚠️ 批量插入无法通过 `SELECT LAST_INSERT_ID()` 回填每行的 `id`，需调整回填逻辑。当前采购订单行依赖 `headerId`（来自头表回填），不依赖行 `id`，可考虑批量插入。

### 3.3 连接池

PMS 主项目使用 Druid 连接池（core 模块），PMS-ext-d365 通过 MyBatis 复用主项目数据源。连接池配置在主项目 `spring-mybatis.xml` 中，本模块无需单独配置。

---

## 4. JSON 序列化

### 4.1 现有实现

`D365Api.toJSONString` 通过自定义 `SerializeConfig` 禁用字段排序，保留声明顺序：

```java
SerializeConfig serializeConfig = new SerializeConfig(true);  // true = 按声明顺序
serializeConfig.config(clazz, SerializerFeature.SortField, false);
serializeConfig.config(clazz, SerializerFeature.MapSortField, false);
```

### 4.2 性能影响

- 每次调用 `toJSONString` 都新建 `SerializeConfig`，有轻微开销；
- `toJSONMap` 额外做一次 JSON 解析（先 toJSONString 再 parseObject）。

### 4.3 优化建议

- 当前调用频率不高（仅 push 时调用），性能影响可忽略；
- 如需优化，可缓存 `SerializeConfig` 实例（按 class 缓存）。

---

## 5. 反射开销

### 5.1 现有实现

`D365Api` 多处使用反射：

| 位置 | 反射用途 | 频率 |
|------|----------|------|
| `initConfig` | 设置静态配置字段 | 每次 push 调用 |
| `AbstractBaseService.insert/insertSelective` | 调用 `setCreateBy` | 每次插入 |
| `AbstractBaseService.update*` | 调用 `setUpdateBy` | 每次更新 |
| `AbstractBaseService.getCurrentUsername` | 调用 `UserContext.getCurrentUsername` | 每次插入/更新 |
| `Request` 构造函数 | 获取泛型类型 | 每次 new Request |

### 5.2 优化建议

- `initConfig` 的反射开销可接受（每次 push 仅一次）；
- `AbstractBaseService` 的 `setCreateBy` 反射可缓存 `Method` 对象：

```java
// 优化建议：缓存 Method
private static final Map<Class<?>, Method> SET_CREATE_BY_CACHE = new ConcurrentHashMap<>();

protected void setCreateBy(T record) {
    Method method = SET_CREATE_BY_CACHE.computeIfAbsent(record.getClass(),
        clazz -> {
            try { return clazz.getMethod("setCreateBy", String.class); }
            catch (Exception e) { return null; }
        });
    if (method != null) {
        try { method.invoke(record, getCurrentUsername()); }
        catch (Exception e) { /* 忽略 */ }
    }
}
```

> ⚠️ 此为优化建议，当前实现功能正确，仅在高频写入场景需优化。

---

## 6. 调试输出

### 6.1 现有问题

`D365Api.post` 方法中有多处 `System.out.println`：

```java
System.out.println(url);           // 第 490 行
System.out.println(request);       // 第 505 行
System.out.println(toJSONMap(request));  // 第 508 行（表单）
System.out.println(toJSONString(request)); // 第 511 行（JSON）
System.out.println(body);          // 第 515 行
```

### 6.2 性能影响

- `System.out.println` 是同步 IO 操作，高频调用时有性能开销；
- `toJSONMap` / `toJSONString` 额外序列化请求体，增加 CPU 和内存开销。

### 6.3 优化建议

```java
// 建议替换为 SLF4J 日志，并使用 DEBUG 级别
private static final Logger log = LoggerFactory.getLogger(D365Api.class);

if (log.isDebugEnabled()) {
    log.debug("D365 请求 URL: {}", url);
    log.debug("D365 请求体: {}", toJSONString(request));
    log.debug("D365 响应体: {}", body);
}
```

---

## 7. 数据库索引

详见 [索引分析](../03-database/index-analysis.md)。关键建议：

- 补充 `sourceType + sourceId` 联合索引（按源数据查询）；
- 补充 `otherSysNum` 索引（幂等检查）；
- 补充 `inventTransId` 索引（收货行匹配）。

---

## 8. 事务与一致性

### 8.1 现有问题

> ⚠️ `D365Api.pushPurchaseOrder` / `pushPurchaseReceipt` **未使用 `@Transactional`**，本地多次 `insertSelective` 不在同一事务。

### 8.2 风险

- D365 创建成功，本地头表插入成功，行表插入失败 → 数据不完整；
- 多行插入中途失败 → 部分行未持久化。

### 8.3 优化建议

**方案一：调用方包裹事务**

```java
@Transactional(rollbackFor = Exception.class)
public void pushSubcontract(Subcontract subcontract) {
    D365Api.pushPurchaseOrder(subcontract, ...);
    // 本地 insert 在同一事务
}
```

> ⚠️ D365 调用无法回滚，事务仅覆盖本地数据库。需配合幂等检查。

**方案二：幂等补偿**

- 推送前检查 `otherSysNum` 是否已存在；
- 失败后重试时，根据 D365 返回的 purchId 判断是否已创建。

---

## 9. 相关文档

- [D365 API 架构](../01-architecture/d365-api-architecture.md)
- [索引分析](../03-database/index-analysis.md)
- [故障排查](troubleshooting.md)
- [安全实践](security-practices.md)
