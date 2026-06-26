# PMS-ext-d365 编码规范文档

> ⚠️ **过时警告**：本文档包含虚构内容，与实际源码不符，仅作历史参考保留。
>
> **虚构内容**：
> - `D365Api.getToken(username, password)` — 实际为**无参** `getToken()`
> - `D365Api.getPurchaseOrders(token, filter)` — 实际源码中**不存在**
> - `D365Api.createPurchaseOrder(token, orderData)` — 实际签名为 `createPurchaseOrder(Request<Response> request)`
> - `IPurchaseService` 接口定义 `getPurchaseById`/`getPurchaseList`/`savePurchase`/`deletePurchase` — 实际 `IPurchaseService` 仅继承 `IAbstractBaseService`，无自定义方法
> - `PurchaseServiceImpl` 类 — 实际实现类名为 `PurchaseService`
> - `D365Exception` 类 — 实际异常类为 `CustomRuntimeException`（无 code 字段）
> - `SyncLog` 类 — 实际源码中**不存在**
>
> **请参考以下准确文档**：
> - [错误码](../06-reference/error-codes.md) — `CustomRuntimeException` 异常体系与正确用法
> - [D365 API 工具类](../02-modules/d365-api.md) — 真实方法签名
> - [安全实践](security-practices.md) — 基于实际源码的安全建议
> - [采购订单模块](../02-modules/purchase-order.md) — 真实 Service 层接口与实现

---

## 1. API 调用规范

### 1.1 Token 管理

```java
// 获取 Token
TokenResponse token = D365Api.getToken(username, password);

// 检查 Token 是否有效
if (token != null && token.getAccessToken() != null) {
    // 使用 Token 调用 API
}
```

### 1.2 HTTP 请求规范

```java
// GET 请求
List<PurchaseHeader> orders = D365Api.getPurchaseOrders(token, filter);

// POST 请求
D365Api.createPurchaseOrder(token, orderData);
```

---

## 2. Service 层规范

### 2.1 接口定义

```java
public interface IPurchaseService {
    Purchase getPurchaseById(int id);
    List<Purchase> getPurchaseList(PurchaseQuery query);
    int savePurchase(Purchase purchase);
    int deletePurchase(int id);
}
```

### 2.2 实现规范

```java
@Service
public class PurchaseServiceImpl implements IPurchaseService {
    
    @Autowired
    private PurchaseMapper purchaseMapper;
    
    @Override
    public Purchase getPurchaseById(int id) {
        return purchaseMapper.selectByPrimaryKey(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int savePurchase(Purchase purchase) {
        if (purchase.getId() == null) {
            return purchaseMapper.insert(purchase);
        } else {
            return purchaseMapper.updateByPrimaryKeySelective(purchase);
        }
    }
}
```

---

## 3. 数据同步规范

### 3.1 同步策略

- 增量同步：只同步变更数据
- 全量同步：定期全量同步
- 异步同步：使用定时任务

### 3.2 同步日志

```java
public class SyncLog {
    private String syncType;  // 同步类型
    private String syncTime;  // 同步时间
    private int syncCount;    // 同步数量
    private String status;    // 状态
    private String message;   // 消息
}
```

---

## 4. 异常处理规范

> ⚠️ **注意**：`D365Exception` 类不存在，为虚构内容。实际异常类为 `CustomRuntimeException`（无 code 字段）。

```java
// 实际异常类：CustomRuntimeException
// 源码位置：com.dp.plat.pms.extend.d365.exception.CustomRuntimeException

// 异常处理（源自 D365Api.java 实际源码）
if (!response.isSuccess()) {
    throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));
}
```

---

## 5. 日志规范

```java
// 使用 SLF4J
private static final Logger log = LoggerFactory.getLogger(D365SyncService.class);

public void syncPurchaseOrders() {
    log.info("开始同步采购订单");
    try {
        List<PurchaseHeader> orders = D365Api.getPurchaseOrders(token, filter);
        log.info("同步完成，共 {} 条记录", orders.size());
    } catch (Exception e) {
        log.error("同步失败", e);
        throw e;
    }
}
```
