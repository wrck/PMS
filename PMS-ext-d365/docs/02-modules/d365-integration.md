# PMS-ext-d365 D365集成模块详细文档

> ⚠️ **过时警告**：本文档包含大量虚构内容，与实际源码不符，仅作历史参考保留。
>
> **虚构内容**：
> - `getToken(String username, String password)` — 实际为**无参** `getToken()`
> - `getPurchaseOrders(String token, String filter)` — 实际源码中**不存在**
> - `createPurchaseOrder(String token, PurchaseHeader order)` — 实际签名为 `createPurchaseOrder(Request<Response> request)`
> - `D365Exception` 异常类（含 code 字段） — 实际异常类为 `CustomRuntimeException`（**无 code 字段**）
> - 表名 `purchase_order` 等 — 实际表名带 `dp_erp_` 前缀
> - 字段 `purchaseType`、`vendorName`、`orderDate`、`status`、`currencyCode`、`totalAmount`、`itemNumber`、`itemName`、`quantity`、`unitPrice`、`lineAmount`、`receiptId`、`receiptDate` 等 — 实际源码中**不存在**这些字段
>
> **请参考以下准确文档**：
> - [D365 API 工具类](d365-api.md) — 真实方法签名与使用说明
> - [采购订单模块](purchase-order.md) — 真实实体、Model、DAO、Service
> - [采购收货模块](purchase-receipt.md)
> - [错误码](../06-reference/error-codes.md) — `CustomRuntimeException` 异常体系
> - [ER 图](../03-database/er-diagram.md) — 真实表名与字段

---

> 本文档深度分析 PMS-ext-d365 D365集成模块的所有 Service、DAO、Entity、Model 类。

---

## 1. 模块概述

PMS-ext-d365 是 PMS 系统的 D365（Dynamics 365）集成扩展模块，提供与 D365 系统的数据交互能力。

### 涉及的 Service 类列表

| Service 接口 | 实现类 | 职责 |
|-------------|--------|------|
| `IPurchaseService` | `PurchaseService` | 采购订单服务 |
| `IPurchaseLineService` | `PurchaseLineService` | 采购订单行服务 |
| `IPurchaseReceiptService` | `PurchaseReceiptService` | 采购收货服务 |
| `IPurchaseReceiptLineService` | `PurchaseReceiptLineService` | 采购收货行服务 |
| `IAbstractBaseService` | `AbstractBaseService` | 基础服务 |

### 涉及的 DAO 类列表

| DAO 接口 | 对应表 | 职责 |
|----------|--------|------|
| `PurchaseMapper` | `purchase_order` | 采购订单 CRUD |
| `PurchaseLineMapper` | `purchase_order_line` | 采购订单行 CRUD |
| `PurchaseReceiptMapper` | `purchase_receipt` | 采购收货 CRUD |
| `PurchaseReceiptLineMapper` | `purchase_receipt_line` | 采购收货行 CRUD |

### 涉及的 Entity 类列表

| Entity 类 | 对应表 | 说明 |
|-----------|--------|------|
| `Purchase` | `purchase_order` | 采购订单实体 |
| `PurchaseLine` | `purchase_order_line` | 采购订单行实体 |
| `PurchaseReceipt` | `purchase_receipt` | 采购收货实体 |
| `PurchaseReceiptLine` | `purchase_receipt_line` | 采购收货行实体 |

---

## 2. API 工具类详细说明

### 2.1 D365Api

**职责**：封装 D365 REST API 调用。

**核心方法**：

#### `TokenResponse getToken(String username, String password)`
- **功能**：获取访问令牌
- **参数**：`username` - 用户名, `password` - 密码
- **返回值**：TokenResponse - Token 响应

#### `List<PurchaseHeader> getPurchaseOrders(String token, String filter)`
- **功能**：查询采购订单
- **参数**：`token` - 访问令牌, `filter` - 过滤条件
- **返回值**：List<PurchaseHeader> - 采购订单列表

#### `PurchaseHeader createPurchaseOrder(String token, PurchaseHeader order)`
- **功能**：创建采购订单
- **参数**：`token` - 访问令牌, `order` - 采购订单
- **返回值**：PurchaseHeader - 创建结果

---

## 3. 数据库表详细说明

### 3.1 purchase_order（采购订单表）

| 字段名 | 类型 | 约束 | 业务含义 |
|--------|------|------|----------|
| `id` | INT | PK, 自增 | 主键ID |
| `purchId` | VARCHAR(50) | UNIQUE | 采购订单号 |
| `dataAreaId` | VARCHAR(50) | - | 数据区域ID |
| `purchaseType` | VARCHAR(50) | - | 采购类型 |
| `vendorAccount` | VARCHAR(50) | - | 供应商账号 |
| `vendorName` | VARCHAR(200) | - | 供应商名称 |
| `orderDate` | DATE | - | 订单日期 |
| `deliveryDate` | DATE | - | 交货日期 |
| `status` | VARCHAR(20) | - | 状态 |
| `currencyCode` | VARCHAR(10) | - | 货币代码 |
| `totalAmount` | DECIMAL(18,2) | - | 总金额 |
| `createTime` | DATETIME | NOT NULL | 创建时间 |
| `updateTime` | DATETIME | - | 更新时间 |

### 3.2 purchase_order_line（采购订单行表）

| 字段名 | 类型 | 约束 | 业务含义 |
|--------|------|------|----------|
| `id` | INT | PK, 自增 | 主键ID |
| `headerId` | INT | FK | 关联订单头ID |
| `purchId` | VARCHAR(50) | - | 采购订单号 |
| `lineNum` | INT | - | 行号 |
| `itemNumber` | VARCHAR(50) | - | 物料编号 |
| `itemName` | VARCHAR(200) | - | 物料名称 |
| `quantity` | DECIMAL(18,2) | - | 数量 |
| `unitPrice` | DECIMAL(18,2) | - | 单价 |
| `lineAmount` | DECIMAL(18,2) | - | 行金额 |
| `deliveryDate` | DATE | - | 交货日期 |
| `status` | VARCHAR(20) | - | 状态 |

---

## 4. 异常处理

### 4.1 CustomRuntimeException

> ⚠️ **注意**：早期文档中描述的 `D365Exception` 异常类（含 `code` 字段）**不存在**，为虚构内容。实际异常处理使用 `CustomRuntimeException`。

实际异常类为 `com.dp.plat.pms.extend.d365.exception.CustomRuntimeException`，继承自 `RuntimeException`，**无 `code` 字段**。

**使用方式**（源自 D365Api.java 实际源码）：

```java
// 推送采购订单失败时抛出
if (!response.isSuccess()) {
    throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));
}
```

**调用位置**：
- `pushPurchaseOrder()` 第 211 行
- `pushPurchaseReceipt()` 第 314 行
- `pushContractAcceptanceDeliveryInfo()` 第 378 行
