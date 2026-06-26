# D365 API 工具类

> 本文档基于 `com.dp.plat.pms.extend.d365.util.D365Api` 实际源码编写（共 696 行）。
> 注意：早期文档中的 `getToken(String username, String password)`、`getPurchaseOrders(String token, String filter)`、`createPurchaseOrder(String token, PurchaseHeader order)` 等方法**均不存在**，属虚构内容。

---

## 1. 类概览

- **全限定名**：`com.dp.plat.pms.extend.d365.util.D365Api`
- **注解**：`@Component("d365Api")`
- **职责**：封装 D365 REST API 调用，包括 OAuth2 Token 获取、采购订单/收货推送、合同验收节点回写
- **设计模式**：静态方法 + Spring 注入桥接（通过 `@PostConstruct` 将注入的 Service 暴露给静态方法）

---

## 2. 字段清单

### 2.1 静态配置字段（String 类型，由 `initConfig` 反射设置）

| 字段 | 说明 |
|------|------|
| `appId` | Azure AD 应用 ID（用于填充 tokenUrl 模板） |
| `clientSecret` | 应用密钥 |
| `clientId` | 应用（客户端）ID |
| `resource` | 目标资源（可选） |
| `grantType` | 授权类型（`client_credentials`） |
| `tokenUrl` | Token 端点（含 `%s`，由 appId 填充） |
| `serviceUrl` | D365 服务基础 URL |
| `createPOUrl` | 创建采购订单接口路径 |
| `receiptPOUrl` | 创建采购收货接口路径 |

### 2.2 静态状态字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `cachedToken` | `volatile TokenResponse` | 缓存的访问令牌 |

### 2.3 Spring 注入字段（实例字段）

| 字段 | 类型 | 说明 |
|------|------|------|
| `purchaseService` | `IPurchaseService` | 采购订单服务 |
| `purchaseLineService` | `IPurchaseLineService` | 采购订单行服务 |
| `purchaseReceiptService` | `IPurchaseReceiptService` | 采购收货服务 |
| `purchaseReceiptLineService` | `IPurchaseReceiptLineService` | 采购收货行服务 |
| `d365Api` | `static D365Api` | 静态实例引用（@PostConstruct 赋值） |

---

## 3. 方法清单

### 3.1 配置与初始化

#### `initConfig(Map<String, Object> config)`
- **签名**：`public static void initConfig(Map<String, Object> config)`
- **功能**：通过反射将 config 中的值设置到 D365Api 的 String 类型静态字段，并执行 `tokenUrl = String.format(tokenUrl, appId)`
- **调用时机**：每次 `pushPurchaseOrder` / `pushPurchaseReceipt` / `pushContractAcceptanceDeliveryInfo` 调用前
- **注意**：仅处理 String 类型字段；非 String 字段（如注入的 Service）不受影响

#### `init()`（@PostConstruct）
- **签名**：`public void init()`
- **功能**：将当前 Spring 实例赋值给静态字段 `d365Api`，并复制注入的 Service 引用，使静态方法能访问 Service

#### 构造方法
- `D365Api()`：无参构造
- `D365Api(Map<String, Object> config)`：调用 `initConfig(config)`

---

### 3.2 Token 管理

#### `getToken()`
- **签名**：`public static TokenResponse getToken()`
- **功能**：获取 OAuth2 访问令牌（带缓存）
- **参数**：无（使用静态配置字段的 clientId/clientSecret 等）
- **返回**：`TokenResponse`（成功含 accessToken，失败含 error）
- **缓存逻辑**：
  1. 检查 `cachedToken` 是否存在且未过期（基于 expiresOn/expiresIn）；
  2. 未过期则直接返回缓存；
  3. 过期或不存在则构造 `TokenRequest`，通过 `postForm(tokenUrl, request, false)` 请求新 Token；
  4. 成功（error==null 且 accessToken!=null）则缓存并写入 timestamp；
  5. 失败则清空缓存，返回含 error 的 TokenResponse。

> ⚠️ 早期文档中的 `getToken(String username, String password)` **不存在**。实际为无参方法，使用 client_credentials 模式。

#### `initAuthorization(Request<?> request)`（@Deprecated）
- **签名**：`public static void initAuthorization(Request<?> request)`
- **功能**：获取 Token 并设置到 request 的 headers 中
- **状态**：已废弃，注释说明"直接在 post 中进行"
- **实际使用**：当前由 `post` 方法内部自动处理认证，无需手动调用

---

### 3.3 采购订单接口

#### `createPurchaseOrder(Request<Response> request)`
- **签名**：`public static Response createPurchaseOrder(Request<Response> request)`
- **功能**：创建采购订单（直接调用 D365 接口，不持久化、不回填）
- **参数**：`request` - 已构造好的请求对象（request 字段为 `PurchaseRequestBody`）
- **返回**：`Response`（D365 原始响应）
- **实现**：`postBody(serviceUrl + createPOUrl, request)`

#### `pushPurchaseOrder(T subcontract, String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **签名**：`public static <T> T pushPurchaseOrder(T subcontract, String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **功能**：推送采购订单到 D365，持久化到本地，并将回填结果透传到业务对象的 customInfo
- **参数**：
  - `subcontract` - 业务对象（需为 BaseEntity 子类，含 customInfo）
  - `dataAreaId` - 账套
  - `purchTable` - 采购订单头（PurchaseHeader）
  - `purchLines` - 采购订单行列表（model.PurchaseLine）
  - `config` - 配置 Map
- **返回**：传入的 `subcontract`（已回填 customInfo）
- **回填的 customInfo key**：`purchId`、`purchIds`、`inventTransId`、`inventTransIds`

#### `pushPurchaseOrder(String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **签名**：`public static Map<String, Object> pushPurchaseOrder(String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **功能**：推送采购订单到 D365 并持久化（不透传到业务对象）
- **返回**：`Map<String, Object>`（含 purchId/purchIds/inventTransId/inventTransIds）

---

### 3.4 采购收货接口

#### `receiptPurchaseOrder(Request<Response> request)`
- **签名**：`public static Response receiptPurchaseOrder(Request<Response> request)`
- **功能**：创建采购收货（直接调用 D365 接口，不持久化、不回填）
- **实现**：`postBody(serviceUrl + receiptPOUrl, request)`

#### `pushPurchaseReceipt(T subcontract, String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **签名**：`public static <T> T pushPurchaseReceipt(T subcontract, String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **功能**：推送采购收货到 D365，持久化到本地，透传 customInfo
- **回填的 customInfo key**：`packingSlipId`、`purchId`、`purchIds`、`inventTransId`、`inventTransIds`

#### `pushPurchaseReceipt(String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **签名**：`public static Map<String, Object> pushPurchaseReceipt(String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **功能**：推送采购收货到 D365 并持久化（不透传到业务对象）
- **返回**：`Map<String, Object>`

> ⚠️ 收货行回填逻辑（第 339-346 行）当前为预留空逻辑，匹配到 inventTransId 后仅 break，未实际回填字段。

---

### 3.5 合同验收接口

#### `pushContractAcceptanceDeliveryInfo(String dataAreaId, String contractNo, List<Map<String, Object>> lines, Map<String, Object> config)`
- **签名**：`public static Response pushContractAcceptanceDeliveryInfo(String dataAreaId, String contractNo, List<Map<String, Object>> lines, Map<String, Object> config)`
- **功能**：推送合同收款计划的验收交付节点信息到 D365
- **参数**：
  - `dataAreaId` - 账套
  - `contractNo` - 合同号
  - `lines` - 验收节点列表（`List<Map<String, Object>>`）
  - `config` - 配置 Map（需含 `paymentSchedUrl`）
- **返回**：`Response`（不持久化到本地数据库）
- **请求体**：`HashMap`（dataAreaId + contract + line）

---

### 3.6 辅助工具方法

#### `fillPurchaseUnitBase(T subcontract, Map<String, Object> config)`
- **签名**：`public static <T> T fillPurchaseUnitBase(T subcontract, Map<String, Object> config)`
- **功能**：填充采购订单的基准单位（数量/价格基准）
- **处理逻辑**：
  1. 将业务对象转为 BaseEntity；
  2. 从 customInfo 读取 `purchUnitBase`、`purchPriceBase`、`purchQtyBase`（缺省取 config 默认值）；
  3. 按配置的 `qtyScale`（默认 2）和 `priceScale`（默认 2）设置精度（HALF_UP）；
  4. 写回 customInfo，拷贝回业务对象。
- **config 默认值**：
  - `qtyScale`：`"2"`
  - `priceScale`：`"2"`
  - `purchUnitBase`：`"price"`
  - `purchPriceBase`：`"1.00"`
  - `purchQtyBase`：`"1.00"`

---

### 3.7 HTTP 工具方法

#### `post(String url, Request<T> request, boolean isForm, boolean needAuth)`
- **签名**：`public static <T> T post(String url, Request<T> request, boolean isForm, boolean needAuth)`
- **功能**：核心 HTTP POST 方法
- **参数**：
  - `url` - 请求 URL（若为相对路径，拼接 serviceUrl）
  - `request` - 请求对象（含 responseType、headers、request body）
  - `isForm` - true 表单提交，false JSON Body 提交
  - `needAuth` - true 自动添加 Authorization 头
- **返回**：反序列化为 `request.getResponseType()` 指定的类型
- **流程**：详见 [D365 API 架构 - HTTP 客户端](../01-architecture/d365-api-architecture.md#33-请求处理流程post-方法)

#### `postForm(String url, Request<T> params)` / `postForm(String url, Request<T> params, boolean needAuth)`
- **功能**：表单提交（`isForm=true`）
- **默认**：`needAuth=true`

#### `postBody(String url, Request<T> params)` / `postBody(String url, Request<T> params, boolean needAuth)`
- **功能**：JSON Body 提交（`isForm=false`）
- **默认**：`needAuth=true`

---

### 3.8 JSON 工具方法

#### `toJSONString(Object object)`
- **签名**：`public static String toJSONString(Object object)`
- **功能**：将对象序列化为 JSON 字符串，**保留字段声明顺序**（禁用 Fastjson 的 SortField 和 MapSortField）
- **null 处理**：返回 `"null"`

#### `toJSONMap(Object object)`
- **签名**：`public static Map<String, Object> toJSONMap(Object object)`
- **功能**：将对象转为 `LinkedHashMap`，**保留字段顺序**（使用 `Feature.OrderedField`）

---

## 4. 使用示例

### 4.1 推送采购订单（泛型回填版）

```java
// 1. 准备配置
Map<String, Object> config = new HashMap<>();
config.put("serviceUrl", "https://usnconeboxax1aos.cloud.onebox.dynamics.com");
config.put("tokenUrl", "https://login.microsoftonline.com/%s/oauth2/token");
config.put("appId", "1402f304-d45a-48fa-8ad7-920a9acd8800");
config.put("clientId", "69d7585c-1665-4013-a8fe-08c9eff4f287");
config.put("clientSecret", "F-58Q~...");
config.put("grantType", "client_credentials");
config.put("createPOUrl", "/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create");
config.put("receiptPOUrl", "/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create");

// 2. 构造采购订单头
PurchaseHeader purchTable = new PurchaseHeader()
    .vendAccount("V00003")
    .inventLocationId("088")
    .purchName("采购事项")
    .deliveryDate("2022-06-23")
    .purchPoolId("安服转包")
    .otherSysNum("PMS20220623001");  // 幂等键

// 3. 构造采购订单行
List<PurchaseLine> purchLines = new ArrayList<>();
purchLines.add(new PurchaseLine()
    .lineNum("1")
    .itemId("T0000001")
    .purchQty(new BigDecimal("100"))
    .purchPrice(new BigDecimal("5235.83"))
    .inventLocationId("088")
    .deliveryDate("2022-06-23"));

// 4. 推送（subcontract 为业务对象，需含 customInfo）
Subcontract result = D365Api.pushPurchaseOrder(
    subcontract, "DPGF", purchTable, purchLines, config);

// 5. 从 customInfo 获取回填结果
BaseEntity base = new BaseEntity();
BeanUtils.copyProperties(result, base);
String purchId = (String) base.getCustomInfoByKey("purchId");
List<Object> purchIds = (List<Object>) base.getCustomInfoByKey("purchIds", new ArrayList<>());
```

### 4.2 推送采购收货

```java
PurchaseReceiptHeader receipt = new PurchaseReceiptHeader()
    .packingSlipId("PO000330_1")
    .deliveryDate("2022-06-23")
    .documentDate("2022-06-23 10:00:00")
    .packingSlipRemark("收货备注");

List<PurchaseReceiptLine> receiptLines = new ArrayList<>();
receiptLines.add(new PurchaseReceiptLine()
    .purchId("PO000329")
    .inventTransId("DPGF-003674")
    .inventSiteId("S1")
    .inventLocationId("088")
    .qty(new BigDecimal("16.57")));

Subcontract result = D365Api.pushPurchaseReceipt(
    subcontract, "DPGF", receipt, receiptLines, config);
```

### 4.3 推送合同验收节点

```java
List<Map<String, Object>> lines = new ArrayList<>();
Map<String, Object> line = new HashMap<>();
line.put("lineNum", 1);
line.put("acceptanceDate", "2022-06-23");
line.put("amount", new BigDecimal("10000"));
lines.add(line);

Response response = D365Api.pushContractAcceptanceDeliveryInfo(
    "DPGF", "HT2022001", lines, config);
```

---

## 5. 注意事项

1. **必须先初始化 Spring 容器**：静态方法依赖 `d365Api` 静态字段（由 `@PostConstruct` 赋值），单元测试时需手动初始化或使用 Spring Test。
2. **config 需完整**：每次 push 调用都会执行 `initConfig`，覆盖静态配置字段，需传入完整 config。
3. **tokenUrl 必须含 `%s`**：`initConfig` 末尾执行 `String.format(tokenUrl, appId)`，缺少占位符会抛异常。
4. **无重试机制**：网络抖动或 D365 短暂不可用会导致推送失败并抛 `CustomRuntimeException`。
5. **无事务**：push 方法未使用 `@Transactional`，本地多次 insert 不在同一事务。
6. **System.out.println 调试输出**：`post` 方法中有多处 `System.out.println`（URL、请求体、响应体），生产环境需注意日志量。

---

## 6. 相关文档

- [D365 API 架构](../01-architecture/d365-api-architecture.md) — OAuth2、HTTP 客户端详解
- [数据同步架构](../01-architecture/data-sync-architecture.md) — 推送/回填机制
- [采购订单模块](purchase-order.md)
- [采购收货模块](purchase-receipt.md)
- [数据映射与转换](data-mapping.md)
- [错误码](../06-reference/error-codes.md)
