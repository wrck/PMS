# 接口模板

> 本文档提供 D365 集成接口的请求/响应模板，基于实际源码与 `D365Api.main` 测试数据整理。

---

## 1. OAuth2 Token 接口

### 1.1 请求

- **URL**：`https://login.microsoftonline.com/{appId}/oauth2/token`
- **方法**：POST
- **Content-Type**：`application/x-www-form-urlencoded`
- **认证**：无需（`needAuth=false`）

**请求体（form 表单）**：

```
grant_type=client_credentials
&client_id={clientId}
&client_secret={clientSecret}
&resource={serviceUrl}
```

### 1.2 响应

**成功响应**：

```json
{
  "token_type": "Bearer",
  "expires_in": "3600",
  "ext_expires_in": "3600",
  "expires_on": "1655980800",
  "not_before": "1655977200",
  "resource": "https://usnconeboxax1aos.cloud.onebox.dynamics.com",
  "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIs...",
  "timestamp": "1655977200000"
}
```

> `timestamp` 字段由 `D365Api.getToken()` 写入（非 Azure AD 返回），用于本地计算过期时间。

**失败响应**：

```json
{
  "error": "invalid_client",
  "error_description": "AADSTS70002: Error validating credentials...",
  "error_codes": [70002],
  "timestamp": "1655977200",
  "trace_id": "...",
  "correlation_id": "...",
  "error_uri": "https://login.microsoftonline.com/error"
}
```

---

## 2. 创建采购订单接口

### 2.1 请求

- **URL**：`{serviceUrl}/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create`
- **方法**：POST
- **Content-Type**：`application/json`
- **认证**：`Authorization: Bearer {access_token}`

**请求体**：

```json
{
  "request": {
    "dataAreaId": "DPGF",
    "purchTable": {
      "vendAccount": "V00003",
      "inventLocationId": "088",
      "purchName": "采购事项描述",
      "deliveryDate": "2022-06-23",
      "projectName": "项目名称",
      "otherSysNum": "PMS20220623001",
      "purContract": "采购合同号",
      "salesContract": "销售合同号",
      "contractAmount": "10000",
      "subcontractType": "转包类型",
      "subcontStartDate": "2022-06-01",
      "subcontEndDate": "2022-12-31",
      "projectProgress": "项目进度",
      "purchPoolId": "安服转包",
      "workerPurchPlacer": "订货人",
      "applicant": "申请人",
      "remark": "整单备注",
      "payment": "付款条款",
      "paymMode": "付款方式",
      "dlvMode": "交货模式",
      "dlvTerm": "交货条款"
    },
    "purchLine": [
      {
        "lineNum": "1",
        "inventLocationId": "088",
        "itemId": "T0000001",
        "purchQty": 100,
        "purchPrice": 5235.83,
        "taxItemGroup": "税收组",
        "inventSerialId": "厂商型号",
        "officeCode": "办事处编码",
        "multiDimID": "多维度ID",
        "deliveryDate": "2022-06-23",
        "remark": "行备注",
        "investmentProject": "募投项目",
        "dimDepartment": "",
        "dimBankAccount": "",
        "dimCustomer": "",
        "dimVendor": "",
        "dimEmployee": "",
        "dimContract": "",
        "dimBU": "",
        "dimProductLine": "",
        "dimTerritory": "",
        "dimIndustry": "",
        "dimMultiDimID": "16"
      }
    ]
  }
}
```

> ⚠️ 字段顺序由 `D365Api.toJSONString` 保留声明顺序，不可随意调整。

### 2.2 响应

**成功响应**：

```json
{
  "code": 200,
  "message": null,
  "data": [
    {
      "purchTable": {
        "PurchId": "PO000433"
      },
      "purchLine": [
        {
          "lineNum": "1",
          "inventTransId": "DPGF-003674"
        }
      ]
    }
  ]
}
```

> 注意：响应中 `PurchId` 首字母大写，`PurchaseHeader.setPurchId` 通过 `@JSONField(alternateNames = {"PurchId"})` 兼容。

**失败响应**：

```json
{
  "code": 500,
  "message": "供应商账号 V00003 不存在",
  "data": []
}
```

---

## 3. 创建采购收货接口

### 3.1 请求

- **URL**：`{serviceUrl}/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create`
- **方法**：POST
- **Content-Type**：`application/json`
- **认证**：`Authorization: Bearer {access_token}`

**请求体**：

```json
{
  "request": {
    "dataAreaId": "DPGF",
    "deliveryDate": "2022-06-23",
    "documentDate": "2022-06-23 10:00:00",
    "packingSlipId": "PO000330_1",
    "packingSlipRemark": "收货备注",
    "projectProgress": "项目进度",
    "lines": [
      {
        "inventSiteId": "S1",
        "inventLocationId": "088",
        "inventTransId": "DPGF-003674",
        "lineNum": "1",
        "purchId": "PO000329",
        "qty": 16.57,
        "wmsLocationId": ""
      }
    ]
  }
}
```

> 与采购订单不同，收货请求体直接使用 `PurchaseReceiptHeader`（含 `lines` 嵌套），非独立的 RequestBody 子类。

### 3.2 响应

**成功响应**：

```json
{
  "code": 200,
  "message": null,
  "data": [
    {
      "dataAreaId": "DPGF",
      "deliveryDate": "2022-06-23",
      "documentDate": "2022-06-23 10:00:00",
      "packingSlipId": "PO000330_1",
      "packingSlipRemark": "收货备注",
      "projectProgress": "项目进度",
      "lines": [
        {
          "inventTransId": "DPGF-003674",
          "qty": 16.57
        }
      ]
    }
  ]
}
```

---

## 4. 合同验收交付接口

### 4.1 请求

- **URL**：`{serviceUrl}{paymentSchedUrl}`
- **方法**：POST
- **Content-Type**：`application/json`
- **认证**：`Authorization: Bearer {access_token}`

**请求体**：

```json
{
  "request": {
    "dataAreaId": "DPGF",
    "contract": "HT2022001",
    "line": [
      {
        "lineNum": 1,
        "acceptanceDate": "2022-06-23",
        "amount": 10000,
        "progress": "已验收"
      }
    ]
  }
}
```

> 注意：使用原生 `HashMap` 构造，`line` 为 key（非 `lines`）。

### 4.2 响应

**成功响应**：

```json
{
  "code": 200,
  "message": null,
  "data": []
}
```

---

## 5. 配置模板

### 5.1 完整配置（config Map）

```json
{
  "enablePushPurchaseOrder": true,
  "appId": "1402f304-d45a-48fa-8ad7-920a9acd8800",
  "clientId": "69d7585c-1665-4013-a8fe-08c9eff4f287",
  "clientSecret": "F-58Q~ZZ.qmLzJC-cL_4ziMYPa40TboDdluRZaH-",
  "grantType": "client_credentials",
  "tokenUrl": "https://login.microsoftonline.com/%s/oauth2/token",
  "serviceUrl": "https://usnconeboxax1aos.cloud.onebox.dynamics.com",
  "createPOUrl": "/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create",
  "receiptPOUrl": "/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create",
  "paymentSchedUrl": "/api/services/...",
  "purchPoolId": "安服转包",
  "itemId": "S0000001",
  "sysTag": "PMS2#",
  "inventSiteId": "S1",
  "inventLocationId": "088",
  "qtyScale": "2",
  "priceScale": "2",
  "purchUnitBase": "price",
  "purchPriceBase": "1.00",
  "purchQtyBase": "1.00"
}
```

### 5.2 配置项说明

| 配置项 | 必填 | 说明 |
|--------|------|------|
| appId | 是 | Azure AD 应用 ID（填充 tokenUrl） |
| clientId | 是 | 应用客户端 ID |
| clientSecret | 是 | 应用密钥 |
| grantType | 是 | 固定 `client_credentials` |
| tokenUrl | 是 | Token 端点（含 `%s`） |
| serviceUrl | 是 | D365 服务基础 URL |
| createPOUrl | 是 | 创建采购订单路径 |
| receiptPOUrl | 是 | 创建采购收货路径 |
| paymentSchedUrl | 否 | 合同验收路径（仅合同验收用） |
| resource | 否 | 目标资源（缺省取 serviceUrl） |
| enablePushPurchaseOrder | 否 | 业务开关（由调用方使用） |
| purchPoolId | 否 | 默认采购订单池（由调用方使用） |
| itemId | 否 | 默认物料编码（由调用方使用） |
| sysTag | 否 | 系统标签（由调用方使用） |
| inventSiteId | 否 | 默认站点（由调用方使用） |
| inventLocationId | 否 | 默认仓库（由调用方使用） |
| qtyScale | 否 | 数量精度（fillPurchaseUnitBase 用，默认 2） |
| priceScale | 否 | 价格精度（fillPurchaseUnitBase 用，默认 2） |
| purchUnitBase | 否 | 采购单位基准（默认 "price"） |
| purchPriceBase | 否 | 价格基准（默认 "1.00"） |
| purchQtyBase | 否 | 数量基准（默认 "1.00"） |

---

## 6. 调用代码模板

### 6.1 推送采购订单

```java
// 1. 准备配置
Map<String, Object> config = new HashMap<>();
config.put("appId", "1402f304-d45a-48fa-8ad7-920a9acd8800");
config.put("clientId", "69d7585c-1665-4013-a8fe-08c9eff4f287");
config.put("clientSecret", "F-58Q~...");
config.put("grantType", "client_credentials");
config.put("tokenUrl", "https://login.microsoftonline.com/%s/oauth2/token");
config.put("serviceUrl", "https://usnconeboxax1aos.cloud.onebox.dynamics.com");
config.put("createPOUrl", "/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create");
config.put("receiptPOUrl", "/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create");

// 2. 构造采购订单头
PurchaseHeader purchTable = new PurchaseHeader()
    .vendAccount("V00003")
    .inventLocationId("088")
    .purchName("采购事项")
    .deliveryDate("2022-06-23")
    .purchPoolId("安服转包")
    .otherSysNum("PMS20220623001");

// 3. 构造采购订单行
List<com.dp.plat.pms.extend.d365.model.PurchaseLine> purchLines = new ArrayList<>();
purchLines.add(new com.dp.plat.pms.extend.d365.model.PurchaseLine()
    .lineNum("1")
    .itemId("T0000001")
    .purchQty(new BigDecimal("100"))
    .purchPrice(new BigDecimal("5235.83"))
    .inventLocationId("088")
    .deliveryDate("2022-06-23"));

// 4. 推送（泛型回填版）
Subcontract result = D365Api.pushPurchaseOrder(
    subcontract, "DPGF", purchTable, purchLines, config);

// 5. 获取回填结果
BaseEntity base = new BaseEntity();
BeanUtils.copyProperties(result, base);
String purchId = (String) base.getCustomInfoByKey("purchId");
```

### 6.2 推送采购收货

```java
PurchaseReceiptHeader receipt = new PurchaseReceiptHeader()
    .packingSlipId("PO000330_1")
    .deliveryDate("2022-06-23")
    .documentDate("2022-06-23 10:00:00")
    .packingSlipRemark("收货备注");

List<com.dp.plat.pms.extend.d365.model.PurchaseReceiptLine> receiptLines = new ArrayList<>();
receiptLines.add(new com.dp.plat.pms.extend.d365.model.PurchaseReceiptLine()
    .purchId("PO000329")
    .inventTransId("DPGF-003674")
    .inventSiteId("S1")
    .inventLocationId("088")
    .qty(new BigDecimal("16.57")));

Subcontract result = D365Api.pushPurchaseReceipt(
    subcontract, "DPGF", receipt, receiptLines, config);
```

### 6.3 推送合同验收节点

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

## 7. 相关文档

- [D365 API 工具类](../02-modules/d365-api.md) — 方法清单
- [数据映射与转换](../02-modules/data-mapping.md) — 字段映射
- [错误码](error-codes.md)
- [术语表](glossary.md)
- [D365 API 架构](../01-architecture/d365-api-architecture.md)
