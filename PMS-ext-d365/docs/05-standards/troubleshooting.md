# 故障排查

> 本文档基于实际源码分析常见故障与排查方法。

---

## 1. Token 过期

### 1.1 现象

- D365 接口调用返回 401 Unauthorized；
- `response.isSuccess()` 返回 false；
- 抛出 `CustomRuntimeException`，message 含 D365 返回的认证错误信息。

### 1.2 原因分析

`D365Api.getToken()` 的缓存过期判断逻辑（第 122-139 行）：

```java
if (cachedToken != null) {
    String expiresOn = cachedToken.getExpiresOn();
    if (StringUtils.isBlank(expiresOn) && cachedToken.getExpiresIn() != null) {
        // 按 expiresIn + timestamp 计算 expiresOn
        long timeInMillis = cachedToken.getTimestamp() != null
            ? Long.parseLong(cachedToken.getTimestamp())
            : Calendar.getInstance().getTimeInMillis();
        long expiresIn = Long.parseLong(cachedToken.getExpiresIn());
        expiresOn = String.valueOf(timeInMillis / 1000 + expiresIn);
        cachedToken.setExpiresOn(expiresOn);
    }
    long expiresOnTimeInMillis = Long.parseLong(expiresOn) * 1000;
    if (expiresOnTimeInMillis >= Calendar.getInstance().getTimeInMillis()) {
        return cachedToken;  // 未过期
    }
}
```

可能的问题：
1. **时钟漂移**：本地系统时间与 Azure AD 时间不一致，导致过期判断错误；
2. **timestamp 未写入**：首次缓存时 `setTimestamp` 在缓存成功后执行，若中间异常可能导致 timestamp 为 null（代码有兜底，使用当前时间）；
3. **expiresOn 解析异常**：`Long.parseLong(expiresOn)` 失败时清空缓存（catch 块），重新获取。

### 1.3 排查步骤

1. **检查系统时间**：`date` 命令确认本地时间与标准时间一致；
2. **查看 Token 响应**：在 `getToken()` 中添加日志，输出 `expiresOn`、`expiresIn`、`timestamp`；
3. **验证 Token 有效性**：用 `curl` 手动调用 D365 接口，附带 Token，确认是否真的过期；
4. **检查 Azure AD 配置**：确认应用未被禁用、密钥未过期。

### 1.4 解决方案

- **自动恢复**：当前实现已支持过期后自动重新获取 Token，下次调用会自动恢复；
- **手动清除缓存**：如需立即刷新，可通过反射清除 `cachedToken`：
  ```java
  Field field = D365Api.class.getDeclaredField("cachedToken");
  field.setAccessible(true);
  field.set(null, null);
  ```
- **时钟同步**：配置 NTP 服务保持系统时间同步。

---

## 2. 接口调用失败

### 2.1 现象

- `response.isSuccess()` 返回 false（code != 200）；
- 抛出 `CustomRuntimeException(response.getMessage() 或 "接口调用异常！")`。

### 2.2 常见原因

| 原因 | 表现 | 排查 |
|------|------|------|
| Token 无效 | 401 Unauthorized | 见 [Token 过期](#1-token-过期) |
| 配置错误 | 404 Not Found | 检查 serviceUrl、createPOUrl、receiptPOUrl |
| 权限不足 | 403 Forbidden | 检查 Azure AD 应用权限 |
| 请求格式错误 | 400 Bad Request | 检查请求体 JSON 结构、字段顺序 |
| D365 服务异常 | 500 Internal Server Error | 联系 D365 管理员 |
| 网络不通 | 连接超时 | 检查网络、防火墙、代理 |
| dataAreaId 错误 | 业务错误 | 检查账套是否在 D365 中存在 |

### 2.3 排查步骤

1. **查看响应详情**：在 `post` 方法中已有 `System.out.println(body)`，查看 D365 返回的完整响应；
2. **检查请求 URL**：`System.out.println(url)` 输出实际请求 URL；
3. **检查请求体**：`System.out.println(toJSONString(request))` 输出请求 JSON；
4. **手动复现**：用 `curl` 或 Postman 手动调用 D365 接口：
   ```bash
   # 1. 获取 Token
   curl -X POST "https://login.microsoftonline.com/{appId}/oauth2/token" \
     -d "grant_type=client_credentials" \
     -d "client_id={clientId}" \
     -d "client_secret={clientSecret}" \
     -d "resource={serviceUrl}"

   # 2. 调用 D365 接口
   curl -X POST "{serviceUrl}{createPOUrl}" \
     -H "Authorization: Bearer {access_token}" \
     -H "Content-Type: application/json" \
     -d '{请求体JSON}'
   ```
5. **检查 D365 日志**：D365 管理员查看 D365 侧的接口调用日志。

### 2.4 常见请求格式问题

- **字段顺序错误**：D365 Custom Service 对字段顺序敏感，`D365Api.toJSONString` 已保留声明顺序；
- **PurchId 大小写**：D365 响应中可能为 `PurchId`（大写），`PurchaseHeader.setPurchId` 已通过 `alternateNames` 兼容；
- **dataAreaId 缺失**：请求体必须含 `dataAreaId`，否则 D365 无法定位账套。

---

## 3. 数据回填异常

### 3.1 采购订单行 inventTransId 未回填

#### 现象
`purchLines` 中的 `inventTransId` 为 null，本地 `dp_erp_purchase_order_line` 表的 `inventTransId` 字段为空。

#### 原因
`pushPurchaseOrder` 第 237-245 行按 `lineNum` 匹配响应行：

```java
for (PurchaseLine poLine : purchLines) {
    for (PurchaseLine line : lines) {  // lines 来自响应
        String lineNum = line.getLineNum();
        inventTransId = line.getInventTransId();
        if (poLine.getLineNum().equals(lineNum)) {
            poLine.setInventTransId(inventTransId);
            break;
        }
    }
}
```

匹配失败的可能原因：
1. **lineNum 不一致**：请求中的 `lineNum` 与 D365 返回的 `lineNum` 不一致（如 D365 重新编号）；
2. **lineNum 为 null**：请求时未设置 `lineNum`，导致 `poLine.getLineNum().equals(lineNum)` 抛 NPE 或匹配失败；
3. **响应 lines 为空**：D365 未返回行信息。

#### 排查步骤
1. 检查请求中 `purchLines` 的 `lineNum` 是否已设置；
2. 查看响应中 `lines` 的 `lineNum` 和 `inventTransId`；
3. 确认 D365 是否按请求的 `lineNum` 原样返回。

#### 解决方案
- 确保请求时设置 `lineNum`；
- 如 D365 重新编号，需调整匹配逻辑（如按行顺序匹配）。

### 3.2 采购收货行回填字段为空

#### 现象
`receiptLines` 的字段未被回填。

#### 原因
> ⚠️ `pushPurchaseReceipt` 第 339-346 行的回填逻辑为**预留空逻辑**：

```java
for (PurchaseReceiptLine line : lines) {
    String inventTransIdTemp = line.getInventTransId();
    if (poLine.getInventTransId().equals(inventTransIdTemp)) {
        // 需要回填的数据，预留
        break;
    }
}
```

匹配成功后仅 `break`，未实际回填任何字段。

#### 解决方案
如需回填，在此处补充回填逻辑，例如：

```java
if (poLine.getInventTransId().equals(inventTransIdTemp)) {
    // 按需回填 D365 返回的字段
    poLine.setQty(line.getQty());
    poLine.setPrice(line.getPrice());
    break;
}
```

### 3.3 customInfo 未透传到业务对象

#### 现象
`pushPurchaseOrder` 返回的业务对象 `customInfo` 中无 `purchId` 等字段。

#### 原因
1. **业务对象非 BaseEntity 子类**：`BeanUtils.copyProperties(subcontract, baseEntity)` 要求业务对象有 `customInfo` 属性；
2. **customInfo 为 null**：业务对象的 `customInfo` 初始为 null，`setCustomInfoByKey` 会自动创建，但 `getCustomInfoByKey("purchIds", new ArrayList<>())` 在 null 时返回默认值，不会累加；
3. **BeanUtils 属性丢失**：`copyProperties` 仅复制同名属性，若业务对象的 `customInfo` 类型不兼容会丢失。

#### 排查步骤
1. 确认业务对象继承 `BaseEntity` 或含 `customInfo`（`Map<String, Object>`）属性；
2. 在 `pushPurchaseOrder` 中添加日志，检查 `baseEntity.getCustomInfo()` 是否为 null；
3. 检查 `BeanUtils.copyProperties` 后 `subcontract.getCustomInfo()` 是否含预期 key。

---

## 4. 本地持久化失败

### 4.1 现象

- D365 调用成功，但本地 `insertSelective` 抛异常；
- 抛出 MyBatis 异常（如 `BadSqlGrammarException`、`DataIntegrityViolationException`）。

### 4.2 常见原因

| 原因 | 异常 | 排查 |
|------|------|------|
| 表不存在 | `BadSqlGrammarException` | 确认 `dp_erp_*` 表已在数据库创建 |
| 字段不存在 | `BadSqlGrammarException` | 确认表结构与 Mapper XML 一致 |
| 字段类型不匹配 | `TypeMismatchException` | 检查 `customInfo`（JSON）字段类型 |
| 主键冲突 | `DuplicateKeyException` | 检查 `purchId`/`packingSlipId` 唯一性 |
| 连接池耗尽 | `CannotGetJdbcConnectionException` | 检查 Druid 连接池配置 |
| UserContext 获取失败 | 无异常，但 createBy 为 null | 检查 `com.dp.plat.core.context.UserContext` |

### 4.3 排查步骤

1. **查看异常堆栈**：定位具体 SQL 和参数；
2. **检查表结构**：`DESC dp_erp_purchase_order_header` 确认字段存在；
3. **检查数据源**：确认 PMS-ext-d365 使用的数据源指向正确的数据库；
4. **检查 UserContext**：`AbstractBaseService.getCurrentUsername()` 反射失败时返回 null，不影响插入，但 `createBy` 为空。

---

## 5. 配置初始化异常

### 5.1 现象

调用 `pushPurchaseOrder` 时抛出 `IllegalFormatException` 或 NPE。

### 5.2 常见原因

| 原因 | 异常 | 排查 |
|------|------|------|
| tokenUrl 缺少 `%s` | `IllegalFormatException` | 确认 tokenUrl 含 `%s` 占位符 |
| config 为 null | NPE | 确认传入非 null config |
| 必要配置缺失 | 接口调用失败 | 确认 config 含 serviceUrl、createPOUrl 等 |

### 5.3 排查步骤

1. 检查 `config` Map 是否含所有必要 key；
2. 检查 `tokenUrl` 格式（必须含 `%s`）；
3. 在 `initConfig` 中添加日志，输出各配置字段值。

---

## 6. Spring 注入失败

### 6.1 现象

- `D365Api.pushPurchaseOrder` 抛 NPE，`d365Api.purchaseService` 为 null；
- 静态方法调用时 Service 未注入。

### 6.2 原因

`D365Api` 使用静态字段 + `@PostConstruct` 桥接模式：

```java
@PostConstruct
public void init() {
    d365Api = this;
    d365Api.purchaseService = this.purchaseService;
    // ...
}
```

若 Spring 容器未初始化 `D365Api` Bean，`d365Api` 静态字段为 null。

### 6.3 排查步骤

1. **确认组件扫描**：Spring 配置的 `context:component-scan` 包含 `com.dp.plat.pms.extend.d365`；
2. **确认 @PostConstruct 执行**：在 `init()` 中添加日志，确认被调用；
3. **单元测试**：测试时需手动初始化或使用 Spring Test 上下文。

### 6.4 解决方案

```java
// 单元测试中手动初始化
D365Api api = new D365Api();
api.purchaseService = mockPurchaseService;
api.purchaseLineService = mockPurchaseLineService;
// 通过反射设置 d365Api 静态字段
Field field = D365Api.class.getDeclaredField("d365Api");
field.setAccessible(true);
field.set(null, api);
```

---

## 7. 调试技巧

### 7.1 启用调试输出

`D365Api.post` 方法已含 `System.out.println`，输出：
- 请求 URL
- 请求对象（toString）
- 请求体（JSON 或 form）
- 响应体

生产环境建议替换为 SLF4J DEBUG 日志。

### 7.2 使用 main 方法测试

`D365Api.main` 方法含完整的测试流程（Token 获取 + 创建采购订单 + 创建采购收货），可用于独立测试：

```bash
# 编译后运行（需配置正确的凭据）
java -cp pms-ext-d365.jar com.dp.plat.pms.extend.d365.util.D365Api
```

> ⚠️ `main` 方法中的凭据为测试凭据，生产环境需替换。`main2` 方法用于测试 JSON 解析。

### 7.3 日志增强建议

```java
// 在关键位置添加日志
log.info("开始推送采购订单, dataAreaId={}", dataAreaId);
log.info("D365 响应: code={}, message={}", response.getCode(), response.getMessage());
log.info("回填 purchId={}, inventTransIds={}", purchId, inventTransIds);
```

---

## 8. 相关文档

- [D365 API 架构](../01-architecture/d365-api-architecture.md)
- [数据同步架构](../01-architecture/data-sync-architecture.md)
- [错误码](../06-reference/error-codes.md)
- [性能优化](performance-optimization.md)
- [安全实践](security-practices.md)
