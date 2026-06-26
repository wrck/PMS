# 术语表

> 本文档解释 PMS-ext-d365 模块及 D365 集成相关的术语。

---

## 1. D365 相关术语

### 1.1 D365（Dynamics 365）

| 术语 | 说明 |
|------|------|
| **D365** | Microsoft Dynamics 365，微软的企业级 ERP/CRM 系统 |
| **D365 F&O** | Dynamics 365 Finance and Operations，D365 的财务与运营模块（本模块集成对象） |
| **Custom Service** | D365 自定义服务，通过 X++ 开发的 REST API，路径格式 `/api/services/{ServiceGroup}/{Service}/{Operation}` |
| **IWS_InterfaceInboundServiceGroup** | D365 侧的服务组名称，包含采购订单、采购收货等入站接口 |
| **Azure AD** | Microsoft Entra ID（原 Azure Active Directory），微软的云身份认证服务 |
| **OAuth2** | 开放授权协议 2.0，本模块使用 client_credentials 授权模式 |
| **client_credentials** | OAuth2 授权模式之一，应用以自身身份（非用户）获取 Token |
| **Bearer Token** | 持有者令牌，通过 `Authorization: Bearer {token}` 头传递 |
| **dataAreaId** | D365 的账套/法人标识（如 DIPU、DPGF），用于定位数据所属公司 |

### 1.2 D365 业务术语

| 术语 | D365 字段 | 说明 |
|------|-----------|------|
| **PurchId** | purchId | 采购订单号，D365 自动生成 |
| **PurchTable** | purchTable | 采购订单头表（D365 表名） |
| **PurchLine** | purchLine | 采购订单行表（D365 表名） |
| **VendAccount** | vendAccount | 供应商账号 |
| **PurchPoolId** | purchPoolId | 采购订单池，用于分类采购订单 |
| **PurchName** | purchName | 采购事项/采购名称 |
| **ItemId** | itemId | 物料编码 |
| **InventSiteId** | inventSiteId | 库存站点 |
| **InventLocationId** | inventLocationId | 仓库 |
| **WmsLocationId** | wmsLocationId | 库位（仓库内的具体位置） |
| **InventTransId** | inventTransId | 库存批次号，D365 生成，用于追踪库存交易 |
| **InventSerialId** | inventSerialId | 库存序列号（本模块复用为厂商型号） |
| **PackingSlipId** | packingSlipId | 包装单/收货单号，PMS 生成 |
| **TaxItemGroup** | taxItemGroup | 税收组 |
| **DataAreaId** | dataAreaId | 账套/法人 |
| **DlvMode** | dlvMode | 交货模式 |
| **DlvTerm** | dlvTerm | 交货条款 |
| **PaymMode** | paymMode | 付款方式 |
| **Payment** | payment | 付款条款 |

---

## 2. PMS 业务术语

| 术语 | 说明 |
|------|------|
| **PMS** | 项目管理系统（Project Management System），本仓库主项目 |
| **Subcontract** | 转包单，PMS 业务单据类型之一 |
| **Dispatch** | 外派单，PMS 业务单据类型之一 |
| **SubcontractPayment** | 转包付款，收货的源类型之一 |
| **DispatchSettlement** | 发运结算，收货的源类型之一 |
| **sourceType** | 源数据类型，标识 PMS 业务单据类型（Subcontract/Dispatch） |
| **sourceId** | 源数据ID，PMS 业务单据的主键 |
| **sourceOrderType** | 订单源数据类型（收货表） |
| **sourceReceiptType** | 收货源类型（SubcontractPayment/DispatchSettlement） |
| **otherSysNum** | 外部系统编号，PMS 生成，用于 D365 侧幂等去重 |
| **customInfo** | 自定义扩展信息，`Map<String, Object>`，用于在业务对象与 D365Api 间透传数据 |

---

## 3. 技术术语

| 术语 | 说明 |
|------|------|
| **BaseEntity** | 基础实体类，含 id、createBy、createTime、updateBy、updateTime、customInfo |
| **AbstractBaseMapper** | 基础 Mapper 接口，定义通用 CRUD 方法 |
| **AbstractBaseService** | 基础 Service 抽象类，封装审计字段自动填充 |
| **D365Api** | D365 API 工具类，封装 OAuth2、HTTP、推送逻辑 |
| **Request<T>** | 泛型请求模型，含 responseType、headers、request body |
| **Response** | 响应模型，含 code、message、data |
| **TokenRequest** | OAuth2 Token 请求模型 |
| **TokenResponse** | OAuth2 Token 响应模型 |
| **PurchaseRequestBody** | 采购订单请求体，含 dataAreaId、purchTable、purchLine |
| **PurchaseHeader** | 采购订单头 Model（继承 Purchase） |
| **PurchaseReceiptHeader** | 采购收货头 Model（继承 PurchaseReceipt，含 lines） |
| **BeanUtils.copyProperties** | Spring 的属性拷贝工具，用于业务对象与 BaseEntity 互转 |
| **UserContext** | 用户上下文（`com.dp.plat.core.context.UserContext`），提供当前登录用户名 |
| **RoutingDataSource** | 多数据源路由（core 模块），支持 local、PMS、SMS、EHR、D365、CRM 等 |
| **Druid** | 阿里巴巴数据库连接池 |
| **Fastjson** | 阿里巴巴 JSON 库 |
| **Hutool** | Java 工具库，本模块使用其 HTTP 客户端（hutool-http） |

---

## 4. 数据库表术语

| 表名 | 说明 |
|------|------|
| **dp_erp_purchase_order_header** | 采购订单头表（注意带 `dp_erp_` 前缀） |
| **dp_erp_purchase_order_line** | 采购订单行表 |
| **dp_erp_purchase_receipt_header** | 采购收货头表 |
| **dp_erp_purchase_receipt_line** | 采购收货行表 |

> ⚠️ 早期文档中的 `purchase_order`、`purchase_receipt` 等表名有误，实际表名带 `dp_erp_` 前缀。

---

## 5. 财务维度术语

采购订单行（PurchaseLine）含 10 个财务维度字段：

| 字段 | 说明 |
|------|------|
| **dimBankAccount** | 维度-银行账户 |
| **dimCustomer** | 维度-客户 |
| **dimVendor** | 维度-供应商 |
| **dimEmployee** | 维度-员工 |
| **dimContract** | 维度-合同号 |
| **dimDepartment** | 维度-部门 |
| **dimBU** | 维度-BU（业务单元） |
| **dimProductLine** | 维度-产品线 |
| **dimTerritory** | 维度-区域 |
| **dimIndustry** | 维度-行业 |
| **dimMultiDimID** | 维度-多维度ID |
| **multiDimID** | 行多维度ID |

> D365 的财务维度用于成本归集、利润分析等，PMS 在推送时透传这些维度。

---

## 6. 接口路径术语

| 配置项 | 路径 | 说明 |
|--------|------|------|
| **tokenUrl** | `https://login.microsoftonline.com/%s/oauth2/token` | Azure AD Token 端点（`%s` 由 appId 填充） |
| **createPOUrl** | `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create` | 创建采购订单 |
| **receiptPOUrl** | `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create` | 创建采购收货 |
| **paymentSchedUrl** | 配置项 | 合同付款计划（验收交付） |
| **serviceUrl** | `https://usnconeboxax1aos.cloud.onebox.dynamics.com` | D365 服务基础 URL |

---

## 7. 相关文档

- [D365 API 架构](../01-architecture/d365-api-architecture.md)
- [数据映射与转换](../02-modules/data-mapping.md)
- [错误码](error-codes.md)
- [接口模板](interface-template.md)
