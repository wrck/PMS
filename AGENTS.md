# AGENTS.md

This file provides guidance to Lingma (lingma.aliyun.com) when working with code in this repository.

# PMS 项目开发指南

## 项目概述

这是一个多模块的 Java EE 企业应用系统，包含两个主要项目：
- **PMS**：基于 Maven 的多模块项目（8+ 个子模块），使用 Struts2/Spring MVC + MyBatis/iBATIS
- **SPMS**：备件管理系统（Spare Parts Management System），独立部署但与 PMS 共用底层数据库

## 构建与运行

```bash
# PMS 完整构建（从 PMS/ 根目录）
mvn clean package

# PMS 指定环境构建
mvn clean package -P dev       # 本地开发（默认）
mvn clean package -P release   # 生产环境
mvn clean package -P test      # 测试环境

# PMS-springmvc 构建 PMS3 版本
mvn clean package -P dev,pms3

# PMS-struts 构建 YFPMS 版本
mvn clean package -P dev,yfpms

# 单模块构建
mvn clean package -pl PMS-struts
mvn clean package -pl PMS-springmvc -P dev,pms3

# SPMS 构建（非 Maven 项目，使用 Eclipse/Ant）
# SPMS 是传统 Eclipse Web 项目，通过 Eclipse Export WAR 或 Ant 构建
```

## 模块结构

### PMS 项目（Maven 多模块）

8 个模块，基础包名 `com.dp.plat`，JDK 1.8：

| 模块 | 包名 | 打包类型 | 用途 |
|------|------|----------|------|
| `core` (pms-mvc-core) | `com.dp.plat.core` | war+jar | 共享框架：Spring、MyBatis、Shiro、Quartz、安全、工具类 |
| `PMS-struts` | `com.dp.plat.*`（35 个子包） | war+jar | 遗留 Struts2 Web 应用 — **非标准源码目录：`src/`** |
| `PMS-springmvc` | `com.dp.plat.pms.springmvc` | war+jar | 较新的 Spring MVC Web 应用（默认：`pms2` 配置） |
| `PMS-activiti` | `com.dp.plat.activiti` | war+jar | Activiti 5.23.0 工作流引擎 + 设计器 |
| `PMS-ext-d365` | `com.dp.plat.pms.extend` | jar | D365 集成扩展 |
| `PMS-security` | `com.dp.plat.*` | jar | 安全组件：CSRF 过滤器、XSS 拦截器、Druid SQL 过滤 |
| `pms-rules` | `com.dp.plat.*` | jar | 规则引擎（Aviator、LiteFlow、Groovy） |
| `pms-ext-fp` | `com.dp.plat.*` | jar | FP 集成扩展（依赖 pms-rules） |

### SPMS 项目（备件管理系统）

**独立部署的传统 Web 项目**，与 PMS 共用底层数据库：
- **技术栈**：Struts2 + Spring + iBATIS（非 MyBatis）
- **源码目录**：`src/com/dp/plat/`（非 Maven 标准结构）
- **配置文件**：`config/`、`config-ibaits/`、`config-spring/`
- **Web 内容**：`WebContent/`（JSP 页面、静态资源）
- **核心功能**：RMA 申请、备件借用、备件转移、库房管理、发运管理、质保查询等
- **数据源**：MySQL（主库）、SQL Server（MES/SAP/D365 集成）

## 模块依赖关系

```
pms-rules → pms-ext-fp
core → PMS-activiti → PMS-springmvc
core → PMS-struts (jar) → PMS-springmvc
PMS-ext-d365 → PMS-struts, PMS-springmvc
PMS-security → PMS-struts
pms-rules → PMS-struts
```

PMS-springmvc 依赖 PMS-activiti（war+jar）和 PMS-struts（classifier 为 `core` 的 jar）。

## Maven 配置

**PMS-struts**：`dev`（默认）、`test`、`release`、`pms`（默认构建）、`yfpms`、`pms2`、`pms3`
**PMS-springmvc**：`dev`（默认）、`test`、`release`、`pms2`（默认构建）、`pms3`

环境配置文件位置：`config/profiles/<env>/`（struts）或 `src/main/resources/profiles/<env>/`（springmvc）。配置文件通过 profile 激活资源过滤 — **请勿提交真实凭据**。

## 源码目录特殊说明

- **PMS-struts**：源码目录为 `src/`（非 `src/main/java`），配置目录为 `config/`（非 `src/main/resources`）。在 pom.xml 的 `<sourceDirectory>` 中配置。
- **PMS-struts**：有 system 作用域依赖：`echarts-utils` 位于 `WebContent/WEB-INF/lib/Utils-v0.1.jar`。
- **所有模块**：MyBatis XML 映射文件与 Java 文件一起放在 `com/dp/plat/**/mapping/*.xml`，通过 maven-resources-plugin 复制到 classpath。
- **PMS-springmvc**：使用 `maven-resources-plugin` 配置 `<overwrite>true</overwrite>` — profile 配置会覆盖基础配置。

## 核心技术栈

### PMS 项目
- **Web 层**：Struts2 2.3.35（PMS-struts）/ 2.5.30（PMS-springmvc）/ Spring MVC 5.3.19（PMS-springmvc）
- **ORM 层**：iBATIS（遗留，PMS-struts）+ MyBatis 3.5.9（PMS-springmvc、core）
- **数据库**：MySQL 8.0.16、SQL Server（sqljdbc4）、PostgreSQL 42.7.0（PMS-struts）
- **连接池**：Druid 1.2.8（主）、commons-dbcp2
- **数据源路由**：`com.dp.plat.core.config.RoutingDataSource` — 支持 6+ 数据库（local、PMS、SMS、EHR、D365、CRM）
- **安全**：Shiro 1.8.0 + CAS 3.2.2
- **工作流**：Activiti 5.23.0
- **定时任务**：Quartz 2.3.2
- **JSON**：Fastjson 1.2.83、Jackson 2.13.1
- **Excel**：Apache POI 5.2.0、EasyExcel 3.1.1
- **日志**：Logback（core、springmvc）、Log4j2 2.17.1（PMS-struts）
- **模板引擎**：Velocity 1.6.4、FreeMarker（仅 springmvc）
- **规则引擎**：Aviator 5.4.3、LiteFlow 2.15.0、Groovy 3.0.19

### SPMS 项目
- **Web 层**：Struts2 2.3.35（传统配置）
- **ORM 层**：iBATIS 2.3.0.677（SqlMapClient）
- **数据库**：MySQL（主库 `dppms_d365`，329张表）、SQL Server（MES/SAP/D365 集成）
- **连接池**：commons-dbcp（BasicDataSource）
- **事务管理**：Spring JDBC DataSourceTransactionManager
- **日志**：Log4j
- **前端**：JSP + jQuery + DisplayTag

## 测试

- JUnit 4、Mockito 4.11.0（`mockito-inline`）
- 测试用例较少且多为临时性 — 非完整测试套件
- MyBatisGenerator 仅在 test 作用域（`com.dp.plat:MyBatisGenerator:0.0.1-RELEASE`）
- 运行测试：`mvn test`（从模块目录或根目录）

## 常见陷阱

- PMS-struts 和 PMS-springmvc 使用**不同的 Struts2 版本**（2.3.35 vs 2.5.30）— 不要假设版本一致。
- `pms-ext-fp` 的 pom.xml 中 `project.build.name` 有拼写错误：`${project.name}}`（多了一个 `}`）。
- 组件扫描基础包：`com.dp.plat`（在 `spring-mybatis.xml` 中配置）。
- MyBatis Mapper 扫描路径：`com.dp.plat.**.dao`。
- 仓库中未找到 CI 配置（Jenkinsfile、GitHub Actions）。
- 这是项目首个指令文件，此前无 AGENTS.md 或 CLAUDE.md。
- `pom.xml` 列出了 `PMS-springboot` 模块，但该模块目录不存在于磁盘上 — 构建会因找不到该模块而失败。
- **SPMS 是非 Maven 项目**：无法使用 `mvn` 命令构建，需通过 Eclipse 或 Ant 导出 WAR。
- **SPMS 与 PMS 共用数据库**：两者都访问 MySQL `dppms_d365` 数据库，注意表结构兼容性。
- **SPMS 使用 iBATIS 而非 MyBatis**：XML 映射文件格式不同（`<sqlMap>` vs `<mapper>`），不要混淆。

## 代码规范

- 基础包名：`com.dp.plat`
- 模块子包遵循模块名：`com.dp.plat.activiti`、`com.dp.plat.pms.springmvc`、`com.dp.plat.core`
- WAR 文件包含带 `-core` 或 `-api` classifier 的 JAR 用于模块间依赖
- Properties 文件按环境区分，不应包含真实密钥
- 使用 Spring XML 配置（非注解驱动）— 查找 `applicationContext*.xml`、`spring*.xml`

## SPMS 项目特殊说明

### 项目结构
```
SPMS/
├── src/com/dp/plat/          # Java 源码（Action、Service、DAO）
├── config/                   # Struts2 配置、jdbc.properties
├── config-ibaits/            # iBATIS SqlMap 配置
├── config-spring/            # Spring Bean 配置
├── WebContent/               # JSP 页面、CSS、JS、图片
└── build/classes/            # 编译输出
```

### 关键配置文件
- **Struts2**：`config/struts.xml`、`config/struts-sys.xml`
- **Spring**：`config-spring/applicationContext*.xml`
- **iBATIS**：`config-ibaits/sql-map-config.xml`（主映射文件）
- **数据库**：`config/jdbc.properties`（含多个数据源配置）

### 数据源配置
SPMS 配置了多个数据源用于系统集成：
- **mysql**：主数据库 `dppms_d365`（备件管理核心数据）
- **firebird/mes**：MES 系统（SQL Server `R2EMES5SQL`）
- **sap**：SAP 系统（SQL Server `DIPULive`）
- **d365**：D365 系统（SQL Server `AXDB`）
- **sms**：SMS 系统（MySQL `dpsms`）

### 核心业务模块
- **用户管理**：Login、UserManage、DepartmentManager
- **备件申请**：RMA、SpareParts、Change（备件转移）
- **库房管理**：Warehouse、Inventory、StorageLocation
- **发运管理**：Shipment、Certificate
- **质保管理**：WarrantyQuery、TainType、ServeType
- **马甲系统**：mark/ 命名空间下的独立业务流程
