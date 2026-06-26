# pms-ext-fp 数据流图

## 1. 发票平台整体数据流

### 1.1 发票识别提交流程

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as Controller
    participant I as InvoiceUtil
    participant F as FPApi
    participant T as Token 缓存
    participant FP as 发票平台(外部)
    participant DB as Database

    U->>C: 上传发票图片
    C->>I: 调用 InvoiceUtil
    I->>F: 调用 FPApi
    F->>T: 获取 Token
    alt Token 存在且未过期
        T-->>F: 返回缓存 Token
    else Token 不存在或已过期
        F->>FP: 请求新 Token
        FP-->>F: 返回 Token
        F->>T: 缓存 Token
    end
    F->>FP: 提交发票识别请求
    FP-->>F: 返回识别结果
    F-->>I: 返回识别数据
    I->>DB: 保存发票信息
    I-->>C: 返回处理结果
    C-->>U: 返回响应
```

### 1.2 Multipart 构建数据流

```mermaid
graph TB
    A[发票文件] --> B[MultipartBuilder]
    B --> C[设置 Boundary]
    C --> D[添加文件部分]
    D --> E[添加表单字段]
    E --> F[构建 HttpEntity]
    F --> G[HTTP POST 请求]
    G --> H[发票平台 API]
```

## 2. Token 管理数据流

```mermaid
stateDiagram-v2
    [*] --> 无Token
    无Token --> 请求Token: 首次调用
    请求Token --> 有Token: 获取成功
    有Token --> 验证Token: API 调用前
    验证Token --> 有Token: Token 有效
    验证Token --> 无Token: Token 过期
    有Token --> [*]: 系统重启
```

## 3. 发票识别详细数据流

```mermaid
sequenceDiagram
    participant I as InvoiceUtil
    participant A as AviatorUtils
    participant F as FPApi
    participant FP as 发票平台

    I->>A: exceute(发票校验规则, env)
    A-->>I: 校验结果
    alt 校验通过
        I->>F: recognizeInvoice(file, config)
        F->>FP: POST /api/invoice/recognize
        FP-->>F: 返回发票信息
        F-->>I: 发票实体
        I->>I: 保存发票信息
    else 校验失败
        I-->>I: 抛出 CustomRuntimeException
    end
```

## 4. 实体模型数据流

```mermaid
graph TB
    subgraph 请求实体
        A[Request]
        A --> B[Header]
        A --> C[Body]
    end

    subgraph 响应实体
        D[Response]
        D --> E[code<br/>状态码]
        D --> F[message<br/>消息]
        D --> G[data<br/>业务数据]
    end

    subgraph 发票实体
        H[Invoice]
        H --> I[invoiceCode<br/>发票代码]
        H --> J[invoiceNumber<br/>发票号码]
        H --> K[invoiceDate<br/>开票日期]
        H --> L[amount<br/>金额]
        H --> M[taxAmount<br/>税额]
    end

    C -->|序列化| FP[发票平台]
    FP -->|返回| D
    D --> G --> H
```

## 5. 跨模块调用关系

```mermaid
graph TB
    subgraph pms-ext-fp 模块
        F[FPApi]
        I[InvoiceUtil]
        MB[MultipartBuilder]
    end

    subgraph 调用方
        PMS_STRUTS[PMS-struts]
        PMS_SPRINGMVC[PMS-springmvc]
    end

    subgraph 外部系统
        FP_PLATFORM[发票平台]
    end

    subgraph 规则引擎
        A[AviatorUtils]
    end

    PMS_STRUTS -->|调用| I
    PMS_SPRINGMVC -->|调用| I
    I -->|使用规则| A
    I -->|调用| F
    F -->|构建请求| MB
    F -->|HTTP 请求| FP_PLATFORM
    FP_PLATFORM -->|返回结果| F
    F -->|返回| I
```

## 6. 异常处理数据流

```mermaid
graph TB
    A[FPApi 调用] --> B{调用成功?}
    B -->|是| C[返回响应]
    B -->|否| D{异常类型}
    D -->|网络异常| E[CustomRuntimeException<br/>网络连接失败]
    D -->|Token 失效| F[重新获取 Token]
    D -->|接口错误| G[CustomRuntimeException<br/>接口返回错误]
    F --> H[重试调用]
    H --> A
    E --> I[向上抛出]
    G --> I
```

## 7. 数据库交互

pms-ext-fp 模块无独立数据库表，发票数据存储在调用方模块的表中。

```mermaid
graph LR
    A[pms-ext-fp] -->|发票识别结果| B[PMS-struts]
    A -->|发票识别结果| C[PMS-springmvc]
    B --> D[pm_*_invoice 表]
    C --> D
```
