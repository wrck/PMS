# 数据流向图

> 本文档基于实际源码编写，描述采购订单推送、采购收货推送、合同验收同步的数据流。
> 注意：实际为推送式同步（PMS → D365），非拉取式。

---

## 1. 总体数据流

```mermaid
graph TB
    subgraph PMS 业务层
        BIZ[业务对象<br/>Subcontract/Dispatch]
    end
    subgraph PMS-ext-d365
        API[D365Api]
        SVC[Service 层]
        DAO[MyBatis Mapper]
    end
    subgraph 外部系统
        AAD[(Azure AD<br/>OAuth2)]
        D365[(D365<br/>Custom Service)]
    end
    subgraph 数据库
        DB[(MySQL<br/>dp_erp_*)]
    end

    BIZ -->|构造请求| API
    API -->|1.获取 Token| AAD
    AAD -->|Token| API
    API -->|2.POST JSON| D365
    D365 -->|Response| API
    API -->|3.回填+持久化| SVC
    SVC -->|4.自动填充 createBy| DAO
    DAO -->|5.INSERT| DB
    API -->|6.customInfo 透传| BIZ
```

---

## 2. 采购订单推送数据流

```mermaid
sequenceDiagram
    autonumber
    participant BIZ as 业务层<br/>(Subcontract)
    participant API as D365Api
    participant AAD as Azure AD
    participant D365 as D365
    participant PSVC as purchaseService
    participant LSVC as purchaseLineService
    participant DB as MySQL

    BIZ->>API: pushPurchaseOrder(subcontract, dataAreaId,<br/>purchTable, purchLines, config)

    rect rgb(240, 248, 255)
        Note over API: 配置初始化
        API->>API: initConfig(config)<br/>(反射设置静态字段)
    end

    rect rgb(255, 245, 238)
        Note over API,D365: 调用 D365 创建采购订单
        API->>API: 构造 PurchaseRequestBody<br/>(dataAreaId + purchTable + purchLine)
        API->>API: 构造 Request<Response>
        API->>AAD: postForm(tokenUrl, TokenRequest, needAuth=false)
        AAD-->>API: TokenResponse (access_token)
        API->>D365: postBody(serviceUrl+createPOUrl, request)<br/>Header: Authorization: Bearer {token}
        D365-->>API: Response {code:200, data:[{purchTable, purchLine}]}
    end

    rect rgb(240, 255, 240)
        Note over API,DB: 回填与持久化
        API->>API: 校验 response.isSuccess()
        loop 遍历响应 dataList
            API->>API: 解析 purchId, lines
            API->>API: purchTable.setPurchId(purchId)
            API->>PSVC: insertSelective(purchTable)
            PSVC->>PSVC: setCreateBy(UserContext)
            PSVC->>DB: INSERT dp_erp_purchase_order_header<br/>SELECT LAST_INSERT_ID()
            DB-->>PSVC: headerId (自增主键)
            PSVC-->>API: headerId
            loop 遍历 purchLines (入参)
                API->>API: poLine.setHeaderId(headerId)
                API->>API: poLine.setPurchId(purchId)
                loop 按 lineNum 匹配响应 lines
                    API->>API: poLine.setInventTransId(inventTransId)
                end
                API->>LSVC: insertSelective(poLine)
                LSVC->>LSVC: setCreateBy(UserContext)
                LSVC->>DB: INSERT dp_erp_purchase_order_line
            end
        end
    end

    rect rgb(255, 240, 245)
        Note over API,BIZ: customInfo 透传
        API->>API: BeanUtils.copyProperties(subcontract, baseEntity)
        API->>API: 读取已有 purchIds/inventTransIds
        API->>API: addAll 新结果
        API->>API: setCustomInfoByKey(purchId/purchIds/...)
        API->>API: BeanUtils.copyProperties(baseEntity, subcontract)
    end

    API-->>BIZ: subcontract (已回填 customInfo)
```

### 2.1 数据流转明细

| 步骤 | 源 | 目标 | 数据 | 方向 |
|------|-----|------|------|------|
| 构造请求 | 业务层 | D365Api | purchTable + purchLines + dataAreaId | PMS → API |
| 获取 Token | D365Api | Azure AD | client_id + client_secret + grant_type | API → AAD |
| 创建订单 | D365Api | D365 | PurchaseRequestBody (JSON) | PMS → D365 |
| 响应回执 | D365 | D365Api | Response {purchId, inventTransId} | D365 → PMS |
| 回填头 | D365Api | purchTable | purchId | API → 内存 |
| 持久化头 | D365Api | DB | Purchase → dp_erp_purchase_order_header | PMS → DB |
| 回填行 | D365Api | purchLines | headerId, purchId, inventTransId | API → 内存 |
| 持久化行 | D365Api | DB | PurchaseLine → dp_erp_purchase_order_line | PMS → DB |
| customInfo | D365Api | 业务对象 | purchId/purchIds/inventTransId/inventTransIds | API → BIZ |

---

## 3. 采购收货推送数据流

```mermaid
sequenceDiagram
    autonumber
    participant BIZ as 业务层<br/>(Subcontract)
    participant API as D365Api
    participant AAD as Azure AD
    participant D365 as D365
    participant RSVC as purchaseReceiptService
    participant RLSVC as purchaseReceiptLineService
    participant DB as MySQL

    BIZ->>API: pushPurchaseReceipt(subcontract, dataAreaId,<br/>receipt, receiptLines, config)

    rect rgb(240, 248, 255)
        Note over API: 配置初始化
        API->>API: initConfig(config)
    end

    rect rgb(255, 245, 238)
        Note over API,D365: 调用 D365 创建采购收货
        API->>API: receipt.setDataAreaId(dataAreaId)
        API->>API: receipt.setLines(receiptLines)
        API->>API: 构造 Request<Response>
        API->>AAD: getToken()
        AAD-->>API: TokenResponse
        API->>D365: postBody(serviceUrl+receiptPOUrl, request)
        D365-->>API: Response {code:200, data:[{...PurchaseReceiptHeader}]}
    end

    rect rgb(240, 255, 240)
        Note over API,DB: 回填与持久化
        API->>API: 校验 response.isSuccess()
        API->>API: purchId = receipt.getPurchId()
        loop 遍历响应 dataList
            API->>RSVC: insertSelective(receipt)
            RSVC->>RSVC: setCreateBy(UserContext)
            RSVC->>DB: INSERT dp_erp_purchase_receipt_header
            DB-->>RSVC: headerId
            RSVC-->>API: headerId
            loop 遍历 receiptLines (入参)
                API->>API: poLine.setReceiptId(headerId)
                API->>API: poLine.setPurchId(purchId)
                loop 按 inventTransId 匹配响应 lines
                    API->>API: 预留回填 (当前空逻辑)
                end
                API->>RLSVC: insertSelective(poLine)
                RLSVC->>DB: INSERT dp_erp_purchase_receipt_line
            end
        end
    end

    rect rgb(255, 240, 245)
        Note over API,BIZ: customInfo 透传
        API->>API: BeanUtils.copyProperties(subcontract, baseEntity)
        API->>API: setCustomInfoByKey(packingSlipId/purchId/...)
        API->>API: BeanUtils.copyProperties(baseEntity, subcontract)
    end

    API-->>BIZ: subcontract (已回填 customInfo)
```

### 3.1 与采购订单数据流的差异

| 维度 | 采购订单 | 采购收货 |
|------|----------|----------|
| 请求体 | PurchaseRequestBody（purchTable + purchLine 分离） | PurchaseReceiptHeader（含 lines 嵌套） |
| 头持久化对象 | purchTable（回填 purchId 后） | receipt（入参对象） |
| 行匹配键 | lineNum | inventTransId |
| 行回填字段 | inventTransId | 预留（空逻辑） |
| customInfo 额外 key | — | packingSlipId |

---

## 4. 合同验收同步数据流

```mermaid
sequenceDiagram
    autonumber
    participant BIZ as 业务层
    participant API as D365Api
    participant AAD as Azure AD
    participant D365 as D365

    BIZ->>API: pushContractAcceptanceDeliveryInfo(<br/>dataAreaId, contractNo, lines, config)

    rect rgb(240, 248, 255)
        Note over API: 配置初始化
        API->>API: initConfig(config)
    end

    rect rgb(255, 245, 238)
        Note over API,D365: 调用 D365 推送验收节点
        API->>API: 构造 HashMap<br/>(dataAreaId + contract + line)
        API->>API: 构造 Request<Response>
        API->>AAD: getToken()
        AAD-->>API: TokenResponse
        API->>D365: postBody(paymentSchedUrl, request)
        D365-->>API: Response
    end

    API->>API: 校验 response.isSuccess()
    API-->>BIZ: Response (不持久化本地)
```

### 4.1 特点

- **不持久化到本地数据库**，仅返回 Response；
- 请求体为原生 `HashMap`（无专用 model 类）；
- 失败抛 `CustomRuntimeException`。

---

## 5. Token 获取数据流

```mermaid
flowchart TD
    A[业务调用 push 方法] --> B[initConfig]
    B --> C[构造请求]
    C --> D{needAuth?}
    D -- 是 --> E[getToken]
    D -- 否 --> F[直接请求]
    E --> G{cachedToken 有效?}
    G -- 是 --> H[返回 cachedToken]
    G -- 否 --> I[构造 TokenRequest]
    I --> J[postForm tokenUrl<br/>needAuth=false]
    J --> K{error==null 且<br/>accessToken!=null?}
    K -- 是 --> L[缓存 + 写入 timestamp]
    K -- 否 --> M[清空缓存]
    L --> N[设置 Authorization 头]
    M --> O[返回含 error 的 TokenResponse]
    O --> F
    N --> F
    F --> P[POST D365 接口]
    P --> Q[解析 Response]
```

---

## 6. 失败数据流

```mermaid
flowchart TD
    A[调用 push 方法] --> B{D365 调用成功?}
    B -- 否 --> C[抛出 CustomRuntimeException<br/>message = response.message 或 接口调用异常！]
    B -- 是 --> D{本地持久化成功?}
    D -- 否 --> E[抛出 RuntimeException<br/>MyBatis 异常]
    D -- 是 --> F[返回 customInfo]

    C --> G[业务层捕获处理]
    E --> G

    G --> H{是否重试?}
    H -- 是 --> A
    H -- 否 --> I[记录失败日志<br/>人工介入]
```

> ⚠️ 当前源码**无自动重试**，失败后由业务层决定是否重试。D365 侧已创建的单据需通过 `otherSysNum` 幂等键避免重复创建。

---

## 7. 数据流涉及的数据表

| 数据流 | 写入表 | 读取表 |
|--------|--------|--------|
| 采购订单推送 | dp_erp_purchase_order_header, dp_erp_purchase_order_line | — |
| 采购收货推送 | dp_erp_purchase_receipt_header, dp_erp_purchase_receipt_line | — |
| 合同验收同步 | —（不写本地） | — |
| Token 获取 | — | —（内存缓存） |

---

## 8. 相关文档

- [数据同步架构](../01-architecture/data-sync-architecture.md)
- [D365 API 架构](../01-architecture/d365-api-architecture.md)
- [CRUD 矩阵](crud-matrix.md)
- [采购订单模块](../02-modules/purchase-order.md)
- [采购收货模块](../02-modules/purchase-receipt.md)
- [ER 图](../03-database/er-diagram.md)
