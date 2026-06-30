# PMS-ext-d365 Service 方法级参考文档

> 本文档深度分析 PMS-ext-d365 所有 Service 接口、实现类及 D365Api 工具类的完整方法签名、源码行号、事务逻辑和异常处理机制。
> 与 PMS-springmvc 不同，**PMS-ext-d365 不包含 Controller 类**，故无对应的 controller-methods-reference.md。
> 本文档基于实际源码编写（截至 2026-06-30），所有方法签名与源码行号均经源码取证核验。

---

## 目录

1. [IAbstractBaseService — 基础服务接口](#1-iabstractbaseservice--基础服务接口)
2. [AbstractBaseService — 基础服务实现](#2-abstractbaseservice--基础服务实现)
3. [IPurchaseService — 采购订单服务](#3-ipurchaseservice--采购订单服务)
4. [IPurchaseLineService — 采购订单行服务](#4-ipurchaselineservice--采购订单行服务)
5. [IPurchaseReceiptService — 采购收货服务](#5-ipurchasereceiptservice--采购收货服务)
6. [IPurchaseReceiptLineService — 采购收货行服务](#6-ipurchasereceiptlineservice--采购收货行服务)
7. [D365Api — D365 集成工具类](#7-d365api--d365-集成工具类)

---

## 1. IAbstractBaseService — 基础服务接口

### 类概述
- 接口：`abstract interface IAbstractBaseService<T>`（泛型接口）
- 全限定名：`com.dp.plat.pms.extend.d365.service.IAbstractBaseService`
- 注解：无
- 作者：w02611
- 源码位置：`service/IAbstractBaseService.java`
- 子接口：`IPurchaseService`、`IPurchaseLineService`、`IPurchaseReceiptService`、`IPurchaseReceiptLineService`

### 方法列表

#### `int deleteByPrimaryKey(Object pk)`
- **功能**：按主键删除记录
- **事务类型**：无显式事务声明（事务由调用方或 Spring 默认策略决定）
- **源码行号**：`IAbstractBaseService.java:10`

#### `int insert(T t)`
- **功能**：全字段插入记录
- **事务类型**：无显式事务声明
- **源码行号**：`IAbstractBaseService.java:12`

#### `int insertSelective(T t)`
- **功能**：选择性插入（仅插入非空字段）
- **事务类型**：无显式事务声明
- **源码行号**：`IAbstractBaseService.java:14`

#### `T selectByPrimaryKey(Object pk)`
- **功能**：按主键查询记录
- **事务类型**：无事务（只读）
- **源码行号**：`IAbstractBaseService.java:16`

#### `int updateByPrimaryKeySelective(T t)`
- **功能**：选择性更新（仅更新非空字段）
- **事务类型**：无显式事务声明
- **源码行号**：`IAbstractBaseService.java:18`

#### `int updateByPrimaryKey(T t)`
- **功能**：全字段更新
- **事务类型**：无显式事务声明
- **源码行号**：`IAbstractBaseService.java:20`

#### `long countBySelective(T t)`
- **功能**：查询满足条件的记录条数
- **事务类型**：无事务（只读）
- **源码行号**：`IAbstractBaseService.java:28`
- **Javadoc**：「查询满足条件的记录条数记录」

#### `List<T> selectBySelective(T t)`
- **功能**：查询满足条件的所有记录
- **事务类型**：无事务（只读）
- **源码行号**：`IAbstractBaseService.java:36`
- **Javadoc**：「查询满足条件的所有记录」

---

## 2. AbstractBaseService — 基础服务实现

### 类概述
- 类：`abstract class AbstractBaseService<Mapper extends AbstractBaseMapper<T>, T> implements IAbstractBaseService<T>`
- 全限定名：`com.dp.plat.pms.extend.d365.service.impl.AbstractBaseService`
- 注解：无类级注解（具体子类使用 `@Service`）
- 作者：w02611
- 源码位置：`service/impl/AbstractBaseService.java`
- 依赖 DAO：`AbstractBaseMapper<T>`（通过 `@Autowired` 注入到 `protected Mapper dao`）
- 核心设计：**通过反射自动填充审计字段**（createBy/updateBy），并兼容 `UserContext` 类的方法变更

### 字段

| 字段 | 类型 | 注入方式 | 说明 |
|------|------|----------|------|
| `dao` | `Mapper`（继承自 `AbstractBaseMapper<T>`） | `@Autowired` | MyBatis Mapper 实例，子类可直接访问 |

### 方法列表

#### `int deleteByPrimaryKey(Object pk)`
- **功能**：直接委托给 DAO 删除
- **事务类型**：无事务包装
- **实现**：`return dao.deleteByPrimaryKey(pk);`
- **源码行号**：`AbstractBaseService.java:21-23`

#### `int insert(T record)`
- **功能**：插入记录前，通过反射调用 `setCreateBy(String)` 填充创建人
- **事务类型**：无 `@Transactional` 注解
- **审计字段填充**：`setCreateBy(getCurrentUsername())`
- **异常处理**：反射调用失败时**静默吞掉异常**（空 catch 块），继续执行 `dao.insert(record)`
- **实现**：
  ```java
  Class<?> objClass = record.getClass();
  Method method = objClass.getMethod("setCreateBy", String.class);
  method.invoke(record, getCurrentUsername());
  return dao.insert(record);
  ```
- **源码行号**：`AbstractBaseService.java:26-34`

#### `int insertSelective(T record)`
- **功能**：选择性插入（仅非空字段），填充创建人
- **审计字段填充**：`setCreateBy(getCurrentUsername())`
- **异常处理**：反射失败时静默吞掉
- **实现**：`return dao.insertSelective(record);`
- **源码行号**：`AbstractBaseService.java:37-45`

#### `T selectByPrimaryKey(Object pk)`
- **功能**：按主键查询
- **事务类型**：无事务（只读）
- **实现**：`return dao.selectByPrimaryKey(pk);`
- **源码行号**：`AbstractBaseService.java:48-50`

#### `int updateByPrimaryKey(T record)`
- **功能**：全字段更新，填充更新人
- **审计字段填充**：`setUpdateBy(getCurrentUsername())`
- **异常处理**：反射失败时静默吞掉
- **实现**：`return dao.updateByPrimaryKey(record);`
- **源码行号**：`AbstractBaseService.java:53-61`

#### `int updateByPrimaryKeySelective(T record)`
- **功能**：选择性更新，填充更新人
- **审计字段填充**：`setUpdateBy(getCurrentUsername())`
- **异常处理**：反射失败时静默吞掉
- **实现**：`return dao.updateByPrimaryKeySelective(record);`
- **源码行号**：`AbstractBaseService.java:64-72`

#### `long countBySelective(T t)`
- **功能**：条件计数
- **实现**：`return dao.countBySelective(t);`
- **源码行号**：`AbstractBaseService.java:74-76`

#### `List<T> selectBySelective(T record)`
- **功能**：条件查询
- **Javadoc**：「查询满足条件的所有记录」
- **实现**：`return dao.selectBySelective(record);`
- **源码行号**：`AbstractBaseService.java:84-86`

#### `String getCurrentUsername()`
- **功能**：反射获取当前用户上下文的用户名
- **签名**：`protected String getCurrentUsername()`
- **可见性**：protected（仅供子类使用）
- **实现逻辑**：
  1. 通过 `Class.forName("com.dp.plat.core.context.UserContext")` 加载核心模块的 `UserContext` 类
  2. 优先调用 `getCurrentUsername()` 静态方法
  3. 失败则回退调用 `getUsername()` 静态方法
  4. 全部失败返回 `null`
- **异常处理**：捕获 `Throwable`，返回 `null`
- **源码行号**：`AbstractBaseService.java:92-105`

### 设计说明

| 方法 | 自动填充字段 | 反射方法 |
|------|-------------|----------|
| `insert` | `createBy` | `setCreateBy(String)` |
| `insertSelective` | `createBy` | `setCreateBy(String)` |
| `updateByPrimaryKey` | `updateBy` | `setUpdateBy(String)` |
| `updateByPrimaryKeySelective` | `updateBy` | `setUpdateBy(String)` |
| `deleteByPrimaryKey` | — | — |
| `selectByPrimaryKey` | — | — |
| `countBySelective` | — | — |
| `selectBySelective` | — | — |

> ⚠️ **静默异常风险**：所有反射调用均使用空 catch 块吞掉异常。若实体类未提供 `setCreateBy`/`setUpdateBy` 方法（如某些非 `BaseEntity` 子类），审计字段填充将被静默跳过，不会抛出异常。

---

## 3. IPurchaseService — 采购订单服务

### 类概述
- 接口：`IPurchaseService extends IAbstractBaseService<Purchase>`
- 全限定名：`com.dp.plat.pms.extend.d365.service.IPurchaseService`
- 实现类：`PurchaseService`
- 依赖 DAO：`PurchaseMapper`（对应表 `dp_erp_purchase_order_header`）
- 生成方式：CodeGenerator 自动生成
- 源码位置：`service/IPurchaseService.java`

### 方法列表

`IPurchaseService` 仅声明 `extends IAbstractBaseService<Purchase>`，**无自定义方法**。

继承的方法（8 个，详见 [第 1 节](#1-iabstractbaseservice--基础服务接口)）：

| 方法 | 泛型实参 | 源码行号 |
|------|---------|----------|
| `int deleteByPrimaryKey(Object pk)` | `Purchase` | `IAbstractBaseService.java:10` |
| `int insert(Purchase t)` | `Purchase` | `IAbstractBaseService.java:12` |
| `int insertSelective(Purchase t)` | `Purchase` | `IAbstractBaseService.java:14` |
| `Purchase selectByPrimaryKey(Object pk)` | `Purchase` | `IAbstractBaseService.java:16` |
| `int updateByPrimaryKeySelective(Purchase t)` | `Purchase` | `IAbstractBaseService.java:18` |
| `int updateByPrimaryKey(Purchase t)` | `Purchase` | `IAbstractBaseService.java:20` |
| `long countBySelective(Purchase t)` | `Purchase` | `IAbstractBaseService.java:28` |
| `List<Purchase> selectBySelective(Purchase t)` | `Purchase` | `IAbstractBaseService.java:36` |

### 实现类 PurchaseService

- **全限定名**：`com.dp.plat.pms.extend.d365.service.impl.PurchaseService`
- **注解**：`@Service("d365PurchaseService")`
- **继承**：`AbstractBaseService<PurchaseMapper, Purchase> implements IPurchaseService`
- **作者/创建时间**：w02611 / 2022-07-01 18:10:45
- **源码位置**：`service/impl/PurchaseService.java:14-16`
- **方法实现**：空类体（全部继承自 `AbstractBaseService`）

---

## 4. IPurchaseLineService — 采购订单行服务

### 类概述
- 接口：`IPurchaseLineService extends IAbstractBaseService<PurchaseLine>`
- 全限定名：`com.dp.plat.pms.extend.d365.service.IPurchaseLineService`
- 实现类：`PurchaseLineService`
- 依赖 DAO：`PurchaseLineMapper`（对应表 `dp_erp_purchase_order_line`）
- 泛型实参：`com.dp.plat.pms.extend.d365.model.PurchaseLine`（注意是 **model 包**，非 entity 包）
- 生成方式：CodeGenerator 自动生成
- 源码位置：`service/IPurchaseLineService.java`

> ⚠️ **泛型特殊点**：`IPurchaseLineService` 使用的是 `model.PurchaseLine`（继承自 `entity.PurchaseLine`，提供链式 setter），而非 `entity.PurchaseLine`。这与 MyBatis XML 的 `resultMap` 类型（`entity.PurchaseLine`）不同，但因继承关系可正常映射。

### 方法列表

`IPurchaseLineService` 仅声明 `extends IAbstractBaseService<PurchaseLine>`，**无自定义方法**。

继承的方法（8 个，泛型实参为 `model.PurchaseLine`）：

| 方法 | 泛型实参 | 源码行号 |
|------|---------|----------|
| `int deleteByPrimaryKey(Object pk)` | `model.PurchaseLine` | `IAbstractBaseService.java:10` |
| `int insert(model.PurchaseLine t)` | `model.PurchaseLine` | `IAbstractBaseService.java:12` |
| `int insertSelective(model.PurchaseLine t)` | `model.PurchaseLine` | `IAbstractBaseService.java:14` |
| `model.PurchaseLine selectByPrimaryKey(Object pk)` | `model.PurchaseLine` | `IAbstractBaseService.java:16` |
| `int updateByPrimaryKeySelective(model.PurchaseLine t)` | `model.PurchaseLine` | `IAbstractBaseService.java:18` |
| `int updateByPrimaryKey(model.PurchaseLine t)` | `model.PurchaseLine` | `IAbstractBaseService.java:20` |
| `long countBySelective(model.PurchaseLine t)` | `model.PurchaseLine` | `IAbstractBaseService.java:28` |
| `List<model.PurchaseLine> selectBySelective(model.PurchaseLine t)` | `model.PurchaseLine` | `IAbstractBaseService.java:36` |

### 实现类 PurchaseLineService

- **全限定名**：`com.dp.plat.pms.extend.d365.service.impl.PurchaseLineService`
- **注解**：`@Service("d365PurchaseLineService")`
- **继承**：`AbstractBaseService<PurchaseLineMapper, model.PurchaseLine> implements IPurchaseLineService`
- **作者/创建时间**：w02611 / 2022-07-01 18:10:45
- **源码位置**：`service/impl/PurchaseLineService.java:14-16`
- **方法实现**：空类体（全部继承自 `AbstractBaseService`）

---

## 5. IPurchaseReceiptService — 采购收货服务

### 类概述
- 接口：`IPurchaseReceiptService extends IAbstractBaseService<PurchaseReceipt>`
- 全限定名：`com.dp.plat.pms.extend.d365.service.IPurchaseReceiptService`
- 实现类：`PurchaseReceiptService`
- 依赖 DAO：`PurchaseReceiptMapper`（对应表 `dp_erp_purchase_receipt_header`）
- 生成方式：CodeGenerator 自动生成
- 源码位置：`service/IPurchaseReceiptService.java`

### 方法列表

`IPurchaseReceiptService` 仅声明 `extends IAbstractBaseService<PurchaseReceipt>`，**无自定义方法**。

继承的方法（8 个，泛型实参为 `entity.PurchaseReceipt`）：

| 方法 | 泛型实参 | 源码行号 |
|------|---------|----------|
| `int deleteByPrimaryKey(Object pk)` | `PurchaseReceipt` | `IAbstractBaseService.java:10` |
| `int insert(PurchaseReceipt t)` | `PurchaseReceipt` | `IAbstractBaseService.java:12` |
| `int insertSelective(PurchaseReceipt t)` | `PurchaseReceipt` | `IAbstractBaseService.java:14` |
| `PurchaseReceipt selectByPrimaryKey(Object pk)` | `PurchaseReceipt` | `IAbstractBaseService.java:16` |
| `int updateByPrimaryKeySelective(PurchaseReceipt t)` | `PurchaseReceipt` | `IAbstractBaseService.java:18` |
| `int updateByPrimaryKey(PurchaseReceipt t)` | `PurchaseReceipt` | `IAbstractBaseService.java:20` |
| `long countBySelective(PurchaseReceipt t)` | `PurchaseReceipt` | `IAbstractBaseService.java:28` |
| `List<PurchaseReceipt> selectBySelective(PurchaseReceipt t)` | `PurchaseReceipt` | `IAbstractBaseService.java:36` |

### 实现类 PurchaseReceiptService

- **全限定名**：`com.dp.plat.pms.extend.d365.service.impl.PurchaseReceiptService`
- **注解**：`@Service("d365PurchaseReceiptService")`
- **继承**：`AbstractBaseService<PurchaseReceiptMapper, PurchaseReceipt> implements IPurchaseReceiptService`
- **作者/创建时间**：w02611 / 2022-07-01 18:10:45
- **源码位置**：`service/impl/PurchaseReceiptService.java:14-16`
- **方法实现**：空类体（全部继承自 `AbstractBaseService`）

---

## 6. IPurchaseReceiptLineService — 采购收货行服务

### 类概述
- 接口：`IPurchaseReceiptLineService extends IAbstractBaseService<PurchaseReceiptLine>`
- 全限定名：`com.dp.plat.pms.extend.d365.service.IPurchaseReceiptLineService`
- 实现类：`PurchaseReceiptLineService`
- 依赖 DAO：`PurchaseReceiptLineMapper`（对应表 `dp_erp_purchase_receipt_line`）
- 泛型实参：`entity.PurchaseReceiptLine`（与 DAO 泛型一致，与 XML resultMap 一致）
- 生成方式：CodeGenerator 自动生成
- 源码位置：`service/IPurchaseReceiptLineService.java`

> ℹ️ **与 PurchaseLineService 的差异**：`IPurchaseLineService` 使用 `model.PurchaseLine`，而 `IPurchaseReceiptLineService` 使用 `entity.PurchaseReceiptLine`。原因是收货行的 Model 类（`model.PurchaseReceiptLine`）作为嵌套列表存在于 `PurchaseReceiptHeader.lines` 字段中，而无需作为独立泛型参数传入 Service。

### 方法列表

`IPurchaseReceiptLineService` 仅声明 `extends IAbstractBaseService<PurchaseReceiptLine>`，**无自定义方法**。

继承的方法（8 个，泛型实参为 `entity.PurchaseReceiptLine`）：

| 方法 | 泛型实参 | 源码行号 |
|------|---------|----------|
| `int deleteByPrimaryKey(Object pk)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:11` |
| `int insert(PurchaseReceiptLine t)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:13` |
| `int insertSelective(PurchaseReceiptLine t)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:15` |
| `PurchaseReceiptLine selectByPrimaryKey(Object pk)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:17` |
| `int updateByPrimaryKeySelective(PurchaseReceiptLine t)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:18` |
| `int updateByPrimaryKey(PurchaseReceiptLine t)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:20` |
| `long countBySelective(PurchaseReceiptLine t)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:28` |
| `List<PurchaseReceiptLine> selectBySelective(PurchaseReceiptLine t)` | `PurchaseReceiptLine` | `IAbstractBaseService.java:36` |

### 实现类 PurchaseReceiptLineService

- **全限定名**：`com.dp.plat.pms.extend.d365.service.impl.PurchaseReceiptLineService`
- **注解**：`@Service("d365PurchaseReceiptLineService")`
- **继承**：`AbstractBaseService<PurchaseReceiptLineMapper, PurchaseReceiptLine> implements IPurchaseReceiptLineService`
- **作者/创建时间**：w02611 / 2022-07-01 18:10:45
- **源码位置**：`service/impl/PurchaseReceiptLineService.java:14-16`
- **方法实现**：空类体（全部继承自 `AbstractBaseService`）

---

## 7. D365Api — D365 集成工具类

### 类概述
- 类：`D365Api`（具体类，非抽象）
- 全限定名：`com.dp.plat.pms.extend.d365.util.D365Api`
- 注解：`@Component("d365Api")`
- 源码位置：`util/D365Api.java`（共 696 行）
- 设计模式：**静态方法 + Spring 注入桥接**（通过 `@PostConstruct` 将注入的 Service 暴露给静态字段 `d365Api`）
- 依赖 Service：`IPurchaseService`、`IPurchaseLineService`、`IPurchaseReceiptService`、`IPurchaseReceiptLineService`

> ℹ️ D365Api 虽非 Service 接口实现，但作为业务编排核心（业务逻辑层），其方法与 Service 紧耦合，故纳入本文档。详细使用说明请参考 [d365-api.md](d365-api.md)。

### 字段清单

#### 静态配置字段（由 `initConfig` 反射设置）

| 字段 | 类型 | 说明 |
|------|------|------|
| `appId` | String | Azure AD 应用 ID（用于填充 tokenUrl 模板） |
| `clientSecret` | String | 应用密钥 |
| `clientId` | String | 应用（客户端）ID |
| `resource` | String | 目标资源（可选） |
| `grantType` | String | 授权类型（`client_credentials`） |
| `tokenUrl` | String | Token 端点（含 `%s` 占位符，由 appId 填充） |
| `serviceUrl` | String | D365 服务基础 URL |
| `createPOUrl` | String | 创建采购订单接口路径 |
| `receiptPOUrl` | String | 创建采购收货接口路径 |

#### 静态状态字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `cachedToken` | `volatile TokenResponse` | 缓存的访问令牌（保证可见性） |

#### Spring 注入字段（实例字段）

| 字段 | 类型 | 说明 |
|------|------|------|
| `purchaseService` | `IPurchaseService` | 采购订单服务 |
| `purchaseLineService` | `IPurchaseLineService` | 采购订单行服务 |
| `purchaseReceiptService` | `IPurchaseReceiptService` | 采购收货服务 |
| `purchaseReceiptLineService` | `IPurchaseReceiptLineService` | 采购收货行服务 |
| `d365Api` | `static D365Api` | 静态实例引用（@PostConstruct 赋值，用于静态方法访问注入字段） |

### 方法列表

#### 7.1 配置与初始化

##### `void init()`
- **签名**：`public void init()`
- **功能**：将当前 Spring 实例赋值给静态字段 `d365Api`，并复制注入的 Service 引用，使静态方法能访问 Service
- **注解**：`@PostConstruct`
- **调用时机**：Spring 容器初始化后自动调用一次
- **源码行号**：`D365Api.java:82-89`

##### `D365Api()`
- **签名**：`public D365Api()`
- **功能**：无参构造方法
- **实现**：`super();`
- **源码行号**：`D365Api.java:91-93`

##### `D365Api(Map<String, Object> config)`
- **签名**：`public D365Api(Map<String, Object> config)`
- **功能**：带配置的构造方法
- **实现**：调用 `initConfig(config)`
- **源码行号**：`D365Api.java:95-98`

##### `void initConfig(Map<String, Object> config)`
- **签名**：`public static void initConfig(Map<String, Object> config)`
- **功能**：通过反射将 config 中的值设置到 D365Api 的 String 类型静态字段，并执行 `tokenUrl = String.format(tokenUrl, appId)`
- **调用时机**：每次 `pushPurchaseOrder` / `pushPurchaseReceipt` / `pushContractAcceptanceDeliveryInfo` 调用前
- **注意**：仅处理 String 类型字段；非 String 字段（如注入的 Service）不受影响；`tokenUrl` 必须含 `%s` 占位符
- **源码行号**：`D365Api.java:100-116`

#### 7.2 Token 管理

##### `TokenResponse getToken()`
- **签名**：`public static TokenResponse getToken()`
- **功能**：获取 OAuth2 访问令牌（带缓存）
- **参数**：无（使用静态配置字段的 clientId/clientSecret 等）
- **返回值**：`TokenResponse`（成功含 accessToken，失败含 error）
- **缓存逻辑**：
  1. 检查 `cachedToken` 是否存在且未过期（基于 `expiresOn`/`expiresIn` 计算）
  2. 未过期则直接返回缓存
  3. 过期或不存在则构造 `TokenRequest`，通过 `postForm(tokenUrl, request, false)` 请求新 Token
  4. 成功（error==null 且 accessToken!=null）则缓存并写入 timestamp
  5. 失败则清空缓存，返回含 error 的 TokenResponse
- **事务类型**：无事务（HTTP 调用）
- **源码行号**：`D365Api.java:122-154`

##### `void initAuthorization(Request<?> request)` ⚠️ 已废弃
- **签名**：`public static void initAuthorization(Request<?> request)`
- **功能**：获取 Token 并设置到 request 的 headers 中
- **注解**：`@Deprecated`
- **状态**：已废弃，注释说明「直接在 post 中进行」
- **实际使用**：当前由 `post` 方法内部自动处理认证，无需手动调用
- **源码行号**：`D365Api.java:413-423`

#### 7.3 采购订单接口

##### `Response createPurchaseOrder(Request<Response> request)`
- **签名**：`public static Response createPurchaseOrder(Request<Response> request)`
- **功能**：创建采购订单（直接调用 D365 接口，不持久化、不回填）
- **参数**：`request` - 已构造好的请求对象（request 字段为 `PurchaseRequestBody`）
- **返回值**：`Response`（D365 原始响应）
- **实现**：`return postBody(serviceUrl + createPOUrl, request);`
- **事务类型**：无事务（HTTP 调用）
- **源码行号**：`D365Api.java:161-164`

##### `<T> T pushPurchaseOrder(T subcontract, String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **签名**：`public static <T> T pushPurchaseOrder(T subcontract, String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **功能**：推送采购订单到 D365，持久化到本地，并将回填结果透传到业务对象的 customInfo
- **参数**：
  | 参数名 | 类型 | 业务含义 |
  |--------|------|----------|
  | subcontract | T | 业务对象（需为 BaseEntity 子类，含 customInfo） |
  | dataAreaId | String | 账套 |
  | purchTable | PurchaseHeader | 采购订单头 |
  | purchLines | List<PurchaseLine> | 采购订单行列表（model.PurchaseLine） |
  | config | Map<String, Object> | 配置 Map |
- **返回值**：传入的 `subcontract`（已回填 customInfo）
- **回填的 customInfo key**：`purchId`、`purchIds`、`inventTransId`、`inventTransIds`
- **调用的 Service 方法**：`purchaseService.insertSelective(purchTable)`、`purchaseLineService.insertSelective(poLine)`
- **事务类型**：无 `@Transactional`（多次本地 insert 不在同一事务）
- **异常处理**：响应失败时抛出 `CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"))`
- **源码行号**：`D365Api.java:176-190`

##### `Map<String, Object> pushPurchaseOrder(String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **签名**：`public static Map<String, Object> pushPurchaseOrder(String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config)`
- **功能**：推送采购订单到 D365 并持久化（不透传到业务对象）
- **返回值**：`Map<String, Object>`（含 purchId/purchIds/inventTransId/inventTransIds）
- **核心步骤**：
  1. `initConfig(config)` 初始化配置
  2. 构造 `Request<Response>` + `PurchaseRequestBody`（设置 dataAreaId、purchTable、purchLine）
  3. `postBody(config.get("createPOUrl"), request)` 调用 D365
  4. 响应失败抛 `CustomRuntimeException`
  5. 解析响应 `data` 为 `List<PurchaseRequestBody>`
  6. 回填 `purchTable.purchId`，调用 `purchaseService.insertSelective(purchTable)`
  7. 按行号 `lineNum` 匹配回填 `inventTransId`，调用 `purchaseLineService.insertSelective(poLine)`
  8. 收集 purchIds/inventTransIds 返回
- **异常处理**：`CustomRuntimeException`（默认消息「接口调用异常！」）
- **源码行号**：`D365Api.java:200-257`

#### 7.4 采购收货接口

##### `Response receiptPurchaseOrder(Request<Response> request)`
- **签名**：`public static Response receiptPurchaseOrder(Request<Response> request)`
- **功能**：创建采购收货（直接调用 D365 接口，不持久化、不回填）
- **实现**：`return postBody(serviceUrl + receiptPOUrl, request);`
- **事务类型**：无事务（HTTP 调用）
- **源码行号**：`D365Api.java:264-267`

##### `<T> T pushPurchaseReceipt(T subcontract, String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **签名**：`public static <T> T pushPurchaseReceipt(T subcontract, String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **功能**：推送采购收货到 D365，持久化到本地，透传 customInfo
- **回填的 customInfo key**：`packingSlipId`、`purchId`、`purchIds`、`inventTransId`、`inventTransIds`
- **调用的 Service 方法**：`purchaseReceiptService.insertSelective(receipt)`、`purchaseReceiptLineService.insertSelective(poLine)`
- **事务类型**：无 `@Transactional`
- **源码行号**：`D365Api.java:279-294`

##### `Map<String, Object> pushPurchaseReceipt(String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **签名**：`public static Map<String, Object> pushPurchaseReceipt(String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config)`
- **功能**：推送采购收货到 D365 并持久化（不透传到业务对象）
- **核心步骤**：
  1. `initConfig(config)` 初始化配置
  2. 构造 `Request<Response>`，request 字段直接使用 `PurchaseReceiptHeader`（含 `lines`）
  3. `postBody(config.get("receiptPOUrl"), request)` 调用 D365
  4. 响应失败抛 `CustomRuntimeException`
  5. 解析响应 `data` 为 `List<PurchaseReceiptHeader>`
  6. 调用 `purchaseReceiptService.insertSelective(receipt)`
  7. 按 `inventTransId` 匹配行（**当前为预留空逻辑**，匹配后仅 `break`，未实际回填字段）
  8. 调用 `purchaseReceiptLineService.insertSelective(poLine)`
- **异常处理**：`CustomRuntimeException`（默认消息「接口调用异常！」）
- **源码行号**：`D365Api.java:305-358`

> ⚠️ **收货行回填逻辑预留**：第 339-346 行的循环匹配到 `inventTransId` 后仅 `break`，未实际回填任何字段，为预留扩展点。

#### 7.5 合同验收接口

##### `Response pushContractAcceptanceDeliveryInfo(String dataAreaId, String contractNo, List<Map<String, Object>> lines, Map<String, Object> config)`
- **签名**：`public static Response pushContractAcceptanceDeliveryInfo(String dataAreaId, String contractNo, List<Map<String, Object>> lines, Map<String, Object> config)`
- **功能**：推送合同收款计划的验收交付节点信息到 D365
- **参数**：
  | 参数名 | 类型 | 业务含义 |
  |--------|------|----------|
  | dataAreaId | String | 账套 |
  | contractNo | String | 合同号 |
  | lines | List<Map<String, Object>> | 验收节点列表 |
  | config | Map<String, Object> | 配置 Map（需含 `paymentSchedUrl`） |
- **返回值**：`Response`（不持久化到本地数据库）
- **请求体**：`HashMap`（`dataAreaId` + `contract` + `line`）
- **异常处理**：响应失败抛 `CustomRuntimeException`（默认消息「接口调用异常！」）
- **源码行号**：`D365Api.java:368-381`

#### 7.6 辅助工具方法

##### `<T> T fillPurchaseUnitBase(T subcontract, Map<String, Object> config)`
- **签名**：`public static <T> T fillPurchaseUnitBase(T subcontract, Map<String, Object> config)`
- **功能**：填充采购订单的基准单位（数量/价格基准）
- **处理逻辑**：
  1. `subcontract == null` 时直接返回
  2. 转换为 `BaseEntity`
  3. 从 customInfo 读取 `purchUnitBase`、`purchPriceBase`、`purchQtyBase`（缺省取 config 默认值）
  4. 按配置的 `qtyScale`（默认 2）和 `priceScale`（默认 2）设置精度（`RoundingMode.HALF_UP`）
  5. 写回 customInfo，拷贝回业务对象
- **config 默认值**：
  | key | 默认值 |
  |-----|--------|
  | `qtyScale` | `"2"` |
  | `priceScale` | `"2"` |
  | `purchUnitBase` | `"price"` |
  | `purchPriceBase` | `"1.00"` |
  | `purchQtyBase` | `"1.00"` |
- **源码行号**：`D365Api.java:389-407`

#### 7.7 HTTP 工具方法

##### `T postForm(String url, Request<T> params)`
- **签名**：`public static <T> T postForm(String url, Request<T> params)`
- **功能**：表单提交（`isForm=true`），默认需要 Auth 认证
- **实现**：`return postForm(url, params, true);`
- **源码行号**：`D365Api.java:432-434`

##### `T postForm(String url, Request<T> params, boolean needAuth)`
- **签名**：`public static <T> T postForm(String url, Request<T> params, boolean needAuth)`
- **功能**：表单提交，指定是否需要 Auth 认证
- **实现**：`return post(url, params, true, needAuth);`
- **源码行号**：`D365Api.java:443-445`

##### `T postBody(String url, Request<T> params)`
- **签名**：`public static <T> T postBody(String url, Request<T> params)`
- **功能**：JSON Body 提交（`isForm=false`），默认需要 Auth 认证
- **实现**：`return postBody(url, params, true);`
- **源码行号**：`D365Api.java:454-456`

##### `T postBody(String url, Request<T> params, boolean needAuth)`
- **签名**：`public static <T> T postBody(String url, Request<T> params, boolean needAuth)`
- **功能**：JSON Body 提交，指定是否需要 Auth 认证
- **实现**：`return post(url, params, false, needAuth);`
- **源码行号**：`D365Api.java:465-467`

##### `T post(String url, Request<T> request, boolean isForm, boolean needAuth)`
- **签名**：`public static <T> T post(String url, Request<T> request, boolean isForm, boolean needAuth)`
- **功能**：核心 HTTP POST 方法，支持表单/JSON Body 两种提交方式
- **参数**：
  | 参数名 | 类型 | 业务含义 |
  |--------|------|----------|
  | url | String | 请求 URL（若为相对路径，拼接 serviceUrl） |
  | request | Request<T> | 请求对象（含 responseType、headers、request body） |
  | isForm | boolean | true 表单提交，false JSON Body 提交 |
  | needAuth | boolean | true 自动添加 Authorization 头 |
- **返回值**：反序列化为 `request.getResponseType()` 指定的类型
- **核心流程**：
  1. `request == null` 时创建空 Request
  2. `url` 为空时返回空对象（`JSON.parseObject("{}", responseType)`）
  3. `uri.getHost() == null` 时拼接 `serviceUrl + url`
  4. 设置 headers
  5. `needAuth == true` 时调用 `getToken()` 获取 token，设置 `Authorization` 头
  6. `isForm == true`：`httpRequest.form(toJSONMap(request))`
  7. `isForm == false`：`httpRequest.body(toJSONString(request))`
  8. 执行请求，将响应体反序列化为 `T`
  9. 响应为 null 时返回空对象
- **调试输出**：方法内有多处 `System.out.println`（URL、请求体、响应体），生产环境需注意日志量
- **源码行号**：`D365Api.java:478-521`

#### 7.8 JSON 工具方法

##### `String toJSONString(Object object)`
- **签名**：`public static String toJSONString(Object object)`
- **功能**：将对象序列化为 JSON 字符串，**保留字段声明顺序**（禁用 Fastjson 的 SortField 和 MapSortField）
- **null 处理**：返回 `"null"`
- **实现**：
  ```java
  int features = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
  SerializeConfig serializeConfig = new SerializeConfig(true);
  serializeConfig.config(clazz, SerializerFeature.SortField, false);
  serializeConfig.config(clazz, SerializerFeature.MapSortField, false);
  return JSON.toJSONString(object, serializeConfig, null, null, features);
  ```
- **源码行号**：`D365Api.java:543-554`

##### `Map<String, Object> toJSONMap(Object object)`
- **签名**：`public static Map<String, Object> toJSONMap(Object object)`
- **功能**：将对象转为 `LinkedHashMap`，**保留字段顺序**（使用 `Feature.OrderedField`）
- **null 处理**：返回 `null`
- **实现**：
  ```java
  String json = toJSONString(object);
  LinkedHashMap<String, Object> map = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, Object>>() {}, Feature.OrderedField);
  return map;
  ```
- **源码行号**：`D365Api.java:528-536`

#### 7.9 测试入口方法（非生产）

##### `void main(String[] args)`
- **签名**：`public static void main(String[] args)`
- **功能**：本地测试入口，演示完整的「初始化配置 → 获取 Token → 创建采购订单 → 收货」流程
- **包含示例**：硬编码的 config、PurchaseHeader、PurchaseLine、PurchaseReceiptHeader 等 JSON 字符串
- **源码行号**：`D365Api.java:556-679`

##### `void main2(String[] args)`
- **签名**：`public static void main2(String[] args)`
- **功能**：备用测试入口（手动改名避免冲突），演示 `PurchaseRequestBody` 响应解析
- **源码行号**：`D365Api.java:681-695`

---

## 8. 注意事项

1. **无 Controller 层**：PMS-ext-d365 作为 D365 集成扩展模块，仅暴露 Service 接口供外部模块（PMS-struts、PMS-springmvc）调用，自身不提供 Web Controller，故无 `controller-methods-reference.md`。
2. **审计字段填充依赖反射**：`AbstractBaseService` 通过反射调用 `setCreateBy`/`setUpdateBy`，若实体非 `BaseEntity` 子类或方法缺失，会静默跳过，不抛异常。
3. **无事务管理**：`AbstractBaseService` 和 `D365Api.push*` 方法均未声明 `@Transactional`，多次本地 insert 不在同一事务，D365 调用与本地持久化不在同一事务边界。
4. **静态方法依赖 Spring 容器**：`D365Api` 的静态方法依赖 `d365Api` 静态字段（由 `@PostConstruct` 赋值），单元测试时需手动初始化或使用 Spring Test。
5. **config 必须完整**：每次 push 调用都会执行 `initConfig`，覆盖静态配置字段，需传入完整 config。`tokenUrl` 必须含 `%s` 占位符。
6. **无重试机制**：网络抖动或 D365 短暂不可用会导致推送失败并抛 `CustomRuntimeException`。
7. **收货行回填逻辑预留**：`pushPurchaseReceipt` 第 339-346 行匹配到 `inventTransId` 后仅 `break`，未实际回填字段。

---

## 9. 相关文档

- [D365 API 工具类](d365-api.md) — D365Api 详细使用说明
- [采购订单模块](purchase-order.md) — Purchase / PurchaseLine 模块概述
- [采购收货模块](purchase-receipt.md) — PurchaseReceipt / PurchaseReceiptLine 模块概述
- [DAO/SQL 参考](dao-sql-reference.md) — DAO 层方法与 SQL 映射
- [数据同步架构](../01-architecture/data-sync-architecture.md) — 推送/回填机制
- [D365 API 架构](../01-architecture/d365-api-architecture.md) — OAuth2、HTTP 客户端详解
- [错误码](../06-reference/error-codes.md) — `CustomRuntimeException` 异常体系
- [ER 图](../03-database/er-diagram.md) — 真实表名与字段
