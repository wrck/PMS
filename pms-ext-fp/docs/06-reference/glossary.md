# 术语表

> 本文档定义 pms-ext-fp 模块及 FP 财务平台相关的术语和缩写。

---

## 1. FP 平台相关术语

| 术语 | 英文/缩写 | 说明 |
|------|----------|------|
| FP | Financial Platform | 财务平台，DPtech 内部的财务管理系统，提供电子发票管理能力 |
| FP 平台 | FP Platform | 即 FP 财务平台，pms-ext-fp 模块的集成目标 |
| 电子发票 | Electronic Invoice | 以电子方式生成、存储和传输的发票，具有与纸质发票同等的法律效力 |
| 发票归档 | Invoice Archive | 将发票信息归档保存到 FP 平台的过程 |
| 发票查验 | Invoice Verification | 对发票真伪进行验证的过程 |
| 发票识别 | Invoice Identification | 识别交付件是否为发票以及发票类型的过程 |
| 发票验真 | Invoice Identify and Verify | 发票识别与验真的组合操作 |

---

## 2. 认证相关术语

| 术语 | 英文/缩写 | 说明 |
|------|----------|------|
| Token | Access Token | 访问令牌，用于 FP 平台 API 认证 |
| OAuth2 | OAuth 2.0 | 开放授权协议，FP 平台使用的认证框架 |
| Bearer Token | - | 持有者令牌，一种 OAuth2 令牌类型 |
| appId | Application ID | 应用标识，FP 平台分配的应用 ID |
| clientSecret | Client Secret | 客户端密钥，用于 OAuth2 客户端认证 |
| clientId | Client ID | 客户端标识 |
| grantType | Grant Type | 授权类型，OAuth2 中的授权方式 |
| openId | Open ID | 用户唯一标识 |
| provider | Provider | 发票来源/提供者 |
| SSO | Single Sign-On | 单点登录 |
| __RequestVerificationToken | - | FP 平台特有的 Token 字段名（TokenResponse.accessToken 的 JSON 名称） |

---

## 3. 发票业务术语

| 术语 | 英文/缩写 | 说明 |
|------|----------|------|
| 发票代码 | Invoice Code | 发票的编码，用于唯一标识发票类型 |
| 发票号码 | Invoice Number | 发票的号码，与发票代码组合唯一标识一张发票 |
| 唯一发票编号 | Unique Invoice Number | 发票代码与发票号码的组合（用 `-` 拼接），或预生成的唯一编号 |
| 增值税发票 | VAT Invoice | 增值税专用发票，可抵扣进项税额 |
| 增值税普通发票 | VAT General Invoice | 增值税普通发票，不可抵扣 |
| 销项发票 | Output VAT Invoice | 销售方开具的增值税发票 |
| 进项发票 | Input VAT Invoice | 购买方取得的增值税发票 |
| 报销单据 | Reimbursement Document | 报销申请的单据 |
| 交付件 | Deliverable | 项目交付的文件材料，可能包含发票 |
| 验收材料 | Inspection Material | 项目验收的材料 |
| 发票状态 | Invoice Status | 发票的当前状态（有效/无效/作废等） |
| 发票类型 | Invoice Type | 发票的类型分类 |
| 业务类型 | Business Type | 发票的业务分类（1-10 种类型） |
| 档案类型 | Archive Type | 归档类型（1=发票，7=报销单据） |

### 3.1 业务类型枚举

| 值 | 含义 |
|----|------|
| 1 | 电子发票-原材料/加工费 |
| 2 | 发票-行政采购（OA） |
| 4 | 安服 |
| 5 | 用服 |
| 6 | 美金发票 |
| 7 | 手工凭证 |
| 8 | SSE发票-一般报销发票 |
| 9 | 增值税发票（销项）-电子票 |
| 10 | 增值税发票（销项）-纸质票扫描件 |

---

## 4. 技术术语

| 术语 | 英文/缩写 | 说明 |
|------|----------|------|
| pms-ext-fp | - | PMS 的 FP 扩展模块，本模块 |
| pms-rules | - | PMS 的规则引擎模块，提供 AviatorUtils |
| Aviator | - | 高性能 Java 表达式引擎，pms-rules 使用版本 5.4.3 |
| AviatorUtils | - | pms-rules 模块提供的 Aviator 工具类，注意方法名拼写为 `exceute` |
| multipart/form-data | - | HTTP 多部分表单上传的 Content-Type |
| MultipartBodyBuilder | - | pms-ext-fp 的表单构建器，支持 OkHttp 和 Apache HttpClient |
| OkHttp | - | Square 公司的 HTTP 客户端，支持 HTTP/2 和连接池 |
| Apache HttpClient | - | Apache 的 HTTP 客户端组件，支持连接池 |
| Hutool | - | Java 工具类库，pms-ext-fp 使用其 HTTP 和工具方法 |
| FastJSON | - | 阿里巴巴的 JSON 序列化框架 |
| Jackson | - | 流行的 JSON 序列化框架 |
| Lombok | - | Java 代码生成工具，通过注解减少样板代码 |
| ConnectionPool | - | 连接池，复用 HTTP 连接 |
| Dispatcher | - | OkHttp 的调度器，控制并发请求 |
| ReentrantReadWriteLock | - | 可重入读写锁，FPApi 用于 Token 缓存保护 |
| DisposableBean | - | Spring 生命周期接口，销毁回调 |
| @PostConstruct | - | JSR-250 注解，Spring 初始化回调 |
| @Component | - | Spring 组件注解，声明 Bean |

---

## 5. 限流与并发术语

| 术语 | 英文/缩写 | 说明 |
|------|----------|------|
| 限流 | Rate Limiting | 控制请求发送频率，避免超过 FP 平台限制 |
| rateLimit | - | 限流频率参数，表示每分钟允许的请求次数 |
| SINGLE | - | 单次模式，逐个同步发送请求 |
| MINUTE | - | 分钟模式，通过调度池按固定延迟发送 |
| MULTIPLE | - | 多并发模式，通过线程池并发发送 |
| splitToList | - | 是否将列表拆分为子列表提交 |
| scheduler | - | 调度线程池（单线程），用于 MINUTE 模式 |
| fixedExecutor | - | 固定线程池（10 线程），用于 MULTIPLE 模式 |
| CountDownLatch | - | 同步辅助类，等待所有任务完成 |
| Future | - | 异步计算结果，MULTIPLE 模式用于保持顺序 |

---

## 6. 配置术语

| 术语 | 英文/缩写 | 说明 |
|------|----------|------|
| configSupplier | - | 配置供应器，动态提供配置 Map |
| configFunction | - | 配置函数，按 key 查询配置 |
| authType | - | 认证类型（bearer/header/query/cookie） |
| authKey | - | 认证键名 |
| enableCookie | - | 是否启用 Cookie 认证 |
| cookieKey | - | Cookie 键名 |
| serviceUrl | - | FP 服务基础地址 |
| tokenUrl | - | Token 获取地址（支持 `%s` appId 占位符） |
| archiveUrl | - | 发票归档地址 |
| ssoUrl | - | SSO 地址 |
| enableRetry | - | 是否启用请求重试 |
| postByForm | - | 是否以表单形式提交 |
| debug | - | 是否开启调试日志 |

---

## 7. 模型类术语

| 术语 | 说明 |
|------|------|
| BaseEntity | 基础实体类，包含 id/createBy/createTime 等通用字段和 customInfo 自定义信息 |
| InvoiceProviderInfo | 发票提供者信息，继承 BaseEntity，包含发票来源、电子签名、文件信息等 |
| ElectronicInvoiceModel | 电子发票模型，继承 InvoiceProviderInfo，包含推送 FP 平台所需的全部字段 |
| Request<T> | 通用请求模型，泛型 T 为响应类型 |
| Response<T> | 通用响应模型，泛型 T 为数据类型，包含 code/message/data/extend |
| TokenRequest | Token 请求模型，继承 Request<TokenResponse> |
| TokenResponse | Token 响应模型，继承 Response<Object>，包含 accessToken/expiresIn 等 |
| ElectronicInvoiceResponse | 电子发票响应，继承 Response<InvoiceProviderInfo> |
| RequestBody | 请求体模型，包含 func（方法名）和 data（推送数据） |

---

## 8. 字段命名风格说明

模块中存在两种字段命名风格，需注意区分：

| 风格 | 示例 | 使用位置 | 说明 |
|------|------|----------|------|
| 驼峰（camelCase） | `invoiceCode`、`invoiceNumber` | Java 实体/模型字段 | Java 标准命名 |
| 下划线（snake_case） | `invoice_code`、`invoice_number` | InvoiceUtil 操作的 Map 数据 | 数据库/接口原始数据 |
| 混合 | `uniqueInvoiceNumber` | InvoiceUtil 的 Map key | 驼峰风格的 Map key |

> **注意**：InvoiceUtil 操作的是 Map（来自数据库或接口），使用下划线风格；ElectronicInvoiceModel 是 Java Bean，使用驼峰风格。两者混用时需注意字段名转换。

---

## 9. 缩写速查

| 缩写 | 全称 | 说明 |
|------|------|------|
| FP | Financial Platform | 财务平台 |
| PMS | Project Management System | 项目管理系统 |
| SSE | - | 业务类型 8 的发票类型 |
| OA | Office Automation | 办公自动化（行政采购） |
| VAT | Value Added Tax | 增值税 |
| SSO | Single Sign-On | 单点登录 |
| API | Application Programming Interface | 应用程序接口 |
| HTTP | HyperText Transfer Protocol | 超文本传输协议 |
| HTTPS | HTTP Secure | 安全的 HTTP |
| JSON | JavaScript Object Notation | 轻量级数据交换格式 |
| REST | Representational State Transfer | 表述性状态转移 |
| LRU | Least Recently Used | 最近最少使用（缓存策略） |
| JMX | Java Management Extensions | Java 管理扩展 |
| JDK | Java Development Kit | Java 开发工具包 |
| SDK | Software Development Kit | 软件开发工具包 |
