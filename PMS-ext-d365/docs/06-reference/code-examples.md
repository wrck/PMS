# PMS-ext-d365 代码示例与参考

> ⚠️ **过时警告**：本文档包含虚构的代码示例，与实际源码不符，仅作历史参考保留。
>
> **虚构内容**：
> - `getToken(String username, String password)` — 实际为**无参** `getToken()`
> - `getPurchaseOrders(String token, String filter)` — 实际源码中**不存在**
> - `createPurchaseOrder(String token, PurchaseHeader order)` — 实际签名为 `createPurchaseOrder(Request<Response> request)`
> - `PurchaseServiceImpl` 类 — 实际实现类名为 `PurchaseService`（`@Service("d365PurchaseService")`）
> - `getPurchaseById`、`getPurchaseList`、`savePurchase`、`deletePurchase` 方法 — 实际 `IPurchaseService` 仅继承 `IAbstractBaseService` 的通用方法（`insert`/`insertSelective`/`selectByPrimaryKey` 等）
> - `D365Exception` 类 — 实际异常类为 `CustomRuntimeException`
> - `D365SyncJob` 定时同步任务类 — 实际源码中**不存在**，本模块为推送式同步，无定时任务
>
> **请参考以下准确文档**：
> - [接口模板](interface-template.md) — 基于实际源码的请求/响应模板与调用代码
> - [D365 API 工具类](../02-modules/d365-api.md) — 真实方法签名与使用示例
> - [错误码](error-codes.md) — `CustomRuntimeException` 异常体系
> - [采购订单模块](../02-modules/purchase-order.md) — 真实 Service 层实现

---

## 1. D365Api 工具类示例

### 1.1 获取 Token

```java
public class D365Api {
    
    private static final String TOKEN_URL = "https://d365.dptech.com/api/Token";
    
    public static TokenResponse getToken(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        
        String response = HttpUtil.post(TOKEN_URL, params);
        return JSON.parseObject(response, TokenResponse.class);
    }
}
```

### 1.2 查询采购订单

```java
public static List<PurchaseHeader> getPurchaseOrders(String token, String filter) {
    String url = "https://d365.dptech.com/api/PurchaseOrders";
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + token);
    
    String response = HttpUtil.createGet(url)
        .headerMap(headers, true)
        .form("filter", filter)
        .execute()
        .body();
    
    return JSON.parseArray(response, PurchaseHeader.class);
}
```

### 1.3 创建采购订单

```java
public static PurchaseHeader createPurchaseOrder(String token, PurchaseHeader order) {
    String url = "https://d365.dptech.com/api/PurchaseOrders";
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + token);
    headers.put("Content-Type", "application/json");
    
    String response = HttpUtil.createPost(url)
        .headerMap(headers, true)
        .body(JSON.toJSONString(order))
        .execute()
        .body();
    
    return JSON.parseObject(response, PurchaseHeader.class);
}
```

---

## 2. Service 层示例

### 2.1 PurchaseService

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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurchase(int id) {
        return purchaseMapper.deleteByPrimaryKey(id);
    }
}
```

---

## 3. 数据同步示例

### 3.1 定时同步任务

```java
@Component
public class D365SyncJob {
    
    private static final Logger log = LoggerFactory.getLogger(D365SyncJob.class);
    
    @Autowired
    private IPurchaseService purchaseService;
    
    @Scheduled(cron = "0 0 1 * * ?")  // 每天凌晨1点执行
    public void syncPurchaseOrders() {
        log.info("开始同步D365采购订单");
        
        try {
            // 获取Token
            TokenResponse token = D365Api.getToken(username, password);
            
            // 查询D365采购订单
            List<PurchaseHeader> orders = D365Api.getPurchaseOrders(
                token.getAccessToken(), filter);
            
            // 同步到本地数据库
            for (PurchaseHeader order : orders) {
                Purchase purchase = convertToLocal(order);
                purchaseService.savePurchase(purchase);
            }
            
            log.info("同步完成，共 {} 条记录", orders.size());
        } catch (Exception e) {
            log.error("同步失败", e);
        }
    }
    
    private Purchase convertToLocal(PurchaseHeader d365Order) {
        Purchase purchase = new Purchase();
        purchase.setPurchId(d365Order.getPurchId());
        purchase.setDataAreaId(d365Order.getDataAreaId());
        purchase.setVendorAccount(d365Order.getVendorAccount());
        purchase.setOrderDate(d365Order.getOrderDate());
        return purchase;
    }
}
```

---

## 4. 异常处理示例

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
