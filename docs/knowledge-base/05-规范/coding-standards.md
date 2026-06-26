# 编码规范文档

> 本文档基于 PMS 与 SPMS 项目实际源码提炼，涵盖命名约定、代码注释标准、版本控制策略及项目结构规范。所有规范均来自现有代码实践，并标注避坑指南与最佳实践。

---

## 目录

1. [命名约定](#1-命名约定)
2. [代码注释标准](#2-代码注释标准)
3. [版本控制策略](#3-版本控制策略)
4. [项目结构规范](#4-项目结构规范)
5. [避坑指南与最佳实践](#5-避坑指南与最佳实践)

---

## 1. 命名约定

### 1.1 包命名规范

PMS 与 SPMS 共用基础包名 `com.dp.plat`，按模块和层级划分子包：

| 层级/模块 | 包名 | 说明 |
|-----------|------|------|
| 根包 | `com.dp.plat` | 所有代码的基础包 |
| core 模块 | `com.dp.plat.core` | 共享框架：Spring、MyBatis、Shiro、工具类 |
| PMS-springmvc | `com.dp.plat.pms.springmvc` | Spring MVC Web 应用 |
| PMS-activiti | `com.dp.plat.activiti` | 工作流引擎 |
| PMS-ext-d365 | `com.dp.plat.pms.extend` | D365 集成扩展 |
| pms-ext-fp | `com.dp.plat.pms.extend.fp` | FP 财务平台集成 |
| PMS-security | `com.dp.plat.security` | 安全组件 |
| pms-rules | `com.dp.plat.*`（规则引擎相关） | 规则引擎 |

**core 模块内部包层级**：

```
com.dp.plat.core
├── config/          # 配置类（SystemConfig、RoutingDataSource）
├── controller/      # 控制器（AbstractController、BaseController）
├── service/         # 服务接口与实现
├── dao/             # MyBatis Mapper 接口
├── pojo/            # 实体类
├── vo/              # 视图对象
├── param/           # 请求参数对象
├── realms/          # Shiro Realm（ShiroRealm、CasRealm）
├── filter/          # 过滤器
├── interceptor/     # 拦截器
├── context/         # 上下文（UserContext、HttpContext）
├── factory/         # 工厂类
├── listener/        # 监听器
├── aop/             # 切面
└── util/            # 工具类
```

**PMS-struts 包层级**（遗留 iBATIS 架构）：

```
com.dp.plat
├── action/          # Struts2 Action（继承 BaseAction）
├── service/         # Service 接口 + ServiceImpl 实现
├── dao/             # iBATIS DAO（继承 BaseDao）
├── data/bean/       # 实体与查询对象
├── ibatis/          # iBATIS 扩展（缓存、TypeHandler）
├── security/        # 安全相关
├── tags/            # 自定义 JSP 标签
└── util/            # 工具类
```

> **避坑指南**：PMS-struts 中存在两个功能相同但包路径不同的 `JsonCustomInfo` 类（`com.dp.plat.data.bean.JsonCustomInfo` 和 `com.dp.plat.subcontract.entity.JsonCustomInfo`），iBatis 映射时需用 `javaType` 指定正确的全限定类名。

### 1.2 类命名规范

#### 1.2.1 Web 层类命名

| 类型 | 命名规则 | 示例 | 说明 |
|------|----------|------|------|
| Struts2 Action | `{业务名}Action` | `ProjectAction`、`RmaApplicantAction` | 继承 `BaseAction`，Spring 配置 `scope="prototype"` |
| Spring MVC Controller | `{业务名}Controller` | `ProjectController`、`WorkFlowController` | 继承 `AbstractController` 或 `BaseController`，使用 `@Controller` 注解 |
| BaseController | `BaseController` | - | PMS-springmvc 控制器基类 |
| AbstractController | `AbstractController<Service, T, V>` | - | 泛型控制器基类，提供 CRUD 模板 |

#### 1.2.2 Service 层类命名

PMS-springmvc（接口 + 实现分离）：

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Service 接口 | `I{业务名}Service` | `IProjectService`、`IPmWorkFlowService` |
| Service 实现 | `{业务名}ServiceImpl` | `ProjectServiceImpl` |

PMS-struts / SPMS（接口 + 实现 + 事务代理三元组）：

| 类型 | 命名规则 | 示例 | 说明 |
|------|----------|------|------|
| Service 接口 | `{业务名}Service` | `ProjectService` | 继承 `BaseService` |
| Service 实现 | `{业务名}ServiceImpl` | `ProjectServiceImpl` | 继承 `BaseServiceImpl` |
| 事务代理 Bean | `{业务名}ServiceAgent` | `ProjectServiceAgent` | 继承 `transactionBaseService`，包装真实 Service |

> **关键规则**：PMS-struts/SPMS 中，Action 层必须注入 `*ServiceAgent`（事务代理），而非直接注入 `*Service`，否则事务不生效。

#### 1.2.3 DAO 层类命名

| 项目 | ORM | 接口命名 | 实现命名 | 基类 |
|------|-----|----------|----------|------|
| PMS-springmvc / core | MyBatis | `{业务名}Mapper` | （无需实现，MyBatis 动态代理） | 无 |
| PMS-struts | iBATIS | （通常无接口） | `{业务名}DaoImpl` | `BaseDao` |
| SPMS | iBATIS | （通常无接口） | `{业务名}DaoImpl` | `BaseDao` |

#### 1.2.4 实体与 VO 命名

| 类型 | 命名规则 | 示例 | 说明 |
|------|----------|------|------|
| 数据库实体 | `{表名去前缀}Bean` 或 `{业务名}` | `Project`、`SystemVariable` | 对应数据库表 |
| 视图对象 | `{业务名}VO` | `ProjectVO`、`MemberVO`、`PmWorkFlowVO` | 用于 Controller 层返回 |
| 查询对象 | `{业务名}Query` | `ProjectQuery` | 封装查询条件 |
| 参数对象 | `{业务名}Param` 或 `PageParam` | `PageParam` | 分页与请求参数 |

### 1.3 方法命名规范

#### 1.3.1 查询方法

| 前缀 | 语义 | 返回类型 | 示例 |
|------|------|----------|------|
| `query*` | 通用查询 | `List` / `Map` | `queryProjectList()`、`querySystemVariables()` |
| `get*` | 单条查询 | `Object` | `getProjectById()`、`getUserByName()` |
| `find*` | 条件查询 | `List` | `findProjectList()`、`findUserByRole()` |
| `list*` | 列表查询 | `List` | `listAllMenus()` |
| `search*` | 搜索查询 | `List` | `searchProjects()` |
| `count*` | 计数查询 | `int` | `countProjectByState()` |

#### 1.3.2 写入方法

| 前缀 | 语义 | 事务行为 | 示例 |
|------|------|----------|------|
| `save*` | 保存（新增或更新） | ✅ 开启事务 | `saveProject()`、`saveConfig()` |
| `insert*` | 插入 | ✅ 开启事务 | `insertProject()`、`insertUser()` |
| `add*` | 添加 | ✅ 开启事务 | `addMember()`、`addRole()` |
| `update*` | 更新 | ✅ 开启事务 | `updatePassword()`、`updateStatus()` |
| `modify*` | 修改 | ✅ 开启事务 | `modifyProject()` |
| `delete*` | 删除 | ✅ 开启事务 | `deleteRole()`、`deleteMenu()` |
| `remove*` | 移除 | ✅ 开启事务 | `removeMember()` |

#### 1.3.3 业务操作方法

| 前缀 | 语义 | 事务行为 | 示例 |
|------|------|----------|------|
| `do*` | 执行业务操作 | ✅ 开启事务 | `doApprove()`、`doCallback()` |
| `start*` | 启动流程 | ✅ 开启事务 | `startProcess()`、`startWorkflow()` |
| `submit*` | 提交 | ✅ 开启事务 | `submitApproval()` |
| `parse*` | 解析 | ✅ 开启事务 | `parseExcel()`、`parseXML()` |
| `keep*` | 维持 | ✅ 开启事务 | `keepAlive()` |

> **避坑指南**：PMS-struts/SPMS 的事务由方法名前缀匹配决定（`transactionBaseService` 配置）。不匹配上述前缀的方法（如 `find*`、`get*`、`query*`）**不开启事务**，以只读方式执行。新增写操作方法时务必使用事务前缀。

#### 1.3.4 Action/Controller 方法

| 方法名 | 语义 | 返回值 |
|--------|------|--------|
| `start()` | 页面初始化（PMS-struts） | `INPUT` |
| `list()` | 列表页面 | `SUCCESS` |
| `save()` | 保存操作 | `SUCCESS` / `ERROR` |
| `delete()` | 删除操作 | `SUCCESS` / `ERROR` |
| `detail()` | 详情页面 | 视图名称 |
| `importPreview()` | 导入预览 | 视图名称 |
| `importSubmit()` | 导入提交 | 视图名称 |

### 1.4 变量命名规范

| 类型 | 规则 | 示例 |
|------|------|------|
| 普通变量 | 小驼峰（lowerCamelCase） | `projectCode`、`officeCode` |
| 常量 | 全大写下划线（UPPER_SNAKE_CASE） | `TEMPLATE_NAMESPACE`、`MAX_PAGE_SIZE` |
| 静态变量 | 全大写下划线 | `systemVariables`（特例：SystemConfig 的静态字段） |
| 布尔变量 | `is` 前缀或语义化 | `isHasRole()`、`useTemplate` |
| 集合变量 | 复数形式 | `projectList`、`roleIds` |
| ThreadLocal | `local` 前缀 | `localVariables` |

### 1.5 数据库命名规范

#### 1.5.1 表名前缀约定

PMS 与 SPMS 共用 MySQL 数据库 `dppms_d365`，通过表名前缀划分业务归属：

| 前缀 | 业务域 | 项目 | 示例表 |
|------|--------|------|--------|
| `pm_` | 项目管理 | PMS | `pm_project_header`、`pm_project_member`、`pm_project_state` |
| `fnd_` | 基础数据/系统管理 | PMS-struts | `fnd_user_info`、`fnd_menus`、`fnd_basic_data`、`fnd_operate_log` |
| `t_` | 通用业务表 | PMS-springmvc/core | `t_user`、`t_role`、`t_permission`、`t_menu`、`t_sys_variable` |
| `act_` | Activiti 工作流 | PMS-activiti | `act_ru_task`、`act_re_deployment`、`act_hi_procinst` |
| `rma_` | RMA 备件申请 | SPMS | `rma_applicant`、`rma_oa` |
| `brw_` | 备件借用 | SPMS | `brw_applicant` |
| `dp_act_` | 工作流扩展 | PMS | `dp_act_unify_task` |
| `mes_` | MES 系统集成 | SPMS | `mes_seal_info` |
| `fb_` | 发运条码 | SPMS | `fb_shipment_barcode` |

> **避坑指南**：PMS-springmvc 使用 `t_` 前缀（如 `t_user`、`t_role`），而 PMS-struts 使用 `fnd_` 前缀（如 `fnd_user_info`）。两套用户体系表结构不同，不可混淆。

#### 1.5.2 字段命名规范

| 规则 | 说明 | 示例 |
|------|------|------|
| 下划线式 | 数据库字段使用 snake_case | `project_code`、`create_time` |
| 主键 | `id` 或 `{表名}_id` | `id`、`project_id`、`menu_id` |
| 外键 | 关联表的主键名 | `role_id`、`menu_id`、`user_id` |
| 软删除 | `effective_from` / `effective_to` | 见下方软删除模式 |
| 审计字段 | `create_by`、`create_time`、`update_by`、`update_time` | 统一审计字段 |

> **注意**：PMS-struts 的 iBATIS 映射中，数据库字段使用 camelCase（如 `projectCode`），与 Java 属性名一致。PMS-springmvc 的 MyBatis 映射中，数据库字段使用 snake_case（如 `project_code`），通过 `resultMap` 或驼峰映射转换。

#### 1.5.3 effectiveFrom / effectiveTo 软删除模式

系统采用 `effectiveFrom` / `effectiveTo` 字段实现软删除和历史版本管理：

| 字段 | 含义 | 有效记录条件 |
|------|------|------------|
| `effectiveFrom` | 生效开始时间 | `effectiveFrom <= NOW()` |
| `effectiveTo` | 生效结束时间 | `effectiveTo IS NULL` 或 `effectiveTo > NOW()` |

**查询有效记录**（统一使用 `effectiveTo IS NULL`，索引效率更高）：

```sql
SELECT * FROM pm_project_header WHERE effectiveTo IS NULL;
```

**软删除操作**：

```sql
UPDATE pm_project_member
SET effectiveTo = NOW()
WHERE projectId = #projectId# AND memberRole = #memberRole#
AND effectiveTo IS NULL;
```

> **最佳实践**：所有含 `effectiveTo` 的表建议建立索引，查询条件统一使用 `effectiveTo IS NULL` 而非 `effectiveTo > NOW()`，前者索引利用率更高。

#### 1.5.4 公共审计字段

所有业务实体继承基类，包含以下公共字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `createBy` / `create_by` | String | 创建人（默认取当前用户名） |
| `createTime` / `create_time` | Date | 创建时间 |
| `updateBy` / `update_by` | String | 更新人 |
| `updateTime` / `update_time` | Date | 更新时间 |
| `effectiveFrom` / `effective_from` | Date | 生效开始时间 |
| `effectiveTo` / `effective_to` | Date | 生效结束时间（NULL 表示当前有效） |

---

## 2. 代码注释标准

### 2.1 类注释

所有公开类应包含 Javadoc 类注释，说明类的职责、作者和版本：

```java
/**
 * 通用控制器基类，提供 CRUD 操作、权限检查、视图渲染等模板方法。
 * <p>
 * 泛型参数：
 * <ul>
 *   <li>Service - 服务接口，需继承 IAbstractBaseService</li>
 *   <li>T - 实体类型</li>
 *   <li>V - 视图对象类型</li>
 * </ul>
 *
 * @author w02611
 * @param <Service> 服务接口类型
 * @param <T> 实体类型
 * @param <V> 视图对象类型
 */
public abstract class AbstractController<Service extends IAbstractBaseService<T>, T, V>
        extends BaseController {
    // ...
}
```

### 2.2 方法注释

公开方法应包含 Javadoc 注释，说明功能、参数、返回值和异常：

```java
/**
 * 查询项目列表（分页）
 *
 * @param pageParam 分页参数
 * @param v 查询条件
 * @param model Spring MVC Model
 * @return 视图名称
 */
@RequestMapping("/list")
public String list(PageParam pageParam, T v, Model model) {
    // ...
}
```

### 2.3 字段注释

常量和重要字段应添加注释：

```java
public abstract class AbstractController<Service extends IAbstractBaseService<T>, T, V> {
    /** 模板视图命名空间 */
    private static final String TEMPLATE_NAMESPACE = "template/";

    /** URL 命名空间 */
    protected String URL_NAMESPACE;

    /** 视图模型名称 */
    protected String viewModel;
}
```

### 2.4 配置文件注释

XML 配置文件中关键 Bean 和属性应添加注释说明：

```xml
<!-- 控制定时任务单线程执行，防止上次同步未执行完，本次又开始执行 -->
<property name="concurrent" value="false" />

<!-- Shiro 权限缓存：10 分钟过期 -->
<cache name="org.apache.shiro.realm.SimpleAccountRealm.authorization"
       maxEntriesLocalHeap="10000"
       eternal="false"
       timeToLiveSeconds="600"
       overflowToDisk="false"/>
```

### 2.5 TODO / FIXME 标记规范

| 标记 | 语义 | 使用场景 |
|------|------|----------|
| `TODO` | 待实现功能 | 功能尚未实现，计划后续补充 |
| `FIXME` | 待修复缺陷 | 已知缺陷，需尽快修复 |
| `XXX` | 待优化实现 | 功能可用但实现需改进 |
| `HACK` | 临时方案 | 临时绕过问题，需后续重构 |

**格式要求**：

```java
// TODO: 增加导出行数限制，避免大数据量 OOM
// FIXME: PermissionTag 权限检查逻辑已注释，当前总是返回 EVAL_BODY_INCLUDE
// XXX: 此处使用 synchronized，集群环境需改为分布式锁
```

> **避坑指南**：项目中存在大量被注释的代码（如 `DataOperationController` 中的 SQL 注入过滤逻辑、`PermissionTag` 的权限检查逻辑）。提交代码前应清理无用的注释代码，或使用 `FIXME` 标注保留原因。

---

## 3. 版本控制策略

### 3.1 版本控制系统

| 项目 | 版本控制 | 说明 |
|------|----------|------|
| PMS | Git | Maven 多模块项目，根目录有 `.git/` |
| SPMS | SVN | 传统 Eclipse 项目，`.svn/` 目录存在 |

> **避坑指南**：SPMS 使用 SVN 而非 Git，两个项目的代码不可混用版本控制操作。跨项目协作时注意同步策略。

### 3.2 PMS 分支管理策略

PMS 使用 Maven Profile 管理多环境配置，分支策略相对简单：

| 分支/Profile | 用途 | 配置文件位置 |
|--------------|------|-------------|
| `dev`（默认） | 本地开发 | `config/profiles/dev/`（struts）、`src/main/resources/profiles/dev/`（springmvc） |
| `test` | 测试环境 | `config/profiles/test/` |
| `release` | 生产环境 | `config/profiles/release/` |
| `pms`（默认构建） | PMS 默认版本 | - |
| `yfpms` | YFPMS 版本 | - |
| `pms2` | PMS2 版本（springmvc 默认） | - |
| `pms3` | PMS3 版本 | - |

**构建命令**：

```bash
# 完整构建
mvn clean package

# 指定环境
mvn clean package -P dev        # 开发环境（默认）
mvn clean package -P release    # 生产环境
mvn clean package -P test       # 测试环境

# 指定版本
mvn clean package -P dev,pms3   # PMS3 版本
mvn clean package -P dev,yfpms  # YFPMS 版本

# 单模块构建
mvn clean package -pl PMS-struts
mvn clean package -pl PMS-springmvc -P dev,pms3
```

### 3.3 提交信息规范

提交信息应清晰描述变更内容和原因，推荐格式：

```
<类型>: <简要描述>

<详细说明>
```

**类型约定**：

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | 缺陷修复 |
| `refactor` | 重构（不改变功能） |
| `docs` | 文档变更 |
| `config` | 配置变更 |
| `chore` | 构建/工具变更 |

### 3.4 敏感信息管理

> **安全红线**：配置文件（`jdbc.properties`、`jdbc_dev.properties`、`jdbc_release.properties`）通过 Profile 资源过滤管理，**不应提交真实凭据**。

- 开发环境配置使用占位符或本地配置
- 生产环境凭据由运维通过环境变量或外部配置注入
- `.gitignore` 应排除含敏感信息的本地配置文件
- SPMS 的 `config/jdbc.properties` 含多个数据源连接信息，SVN 提交时需注意脱敏

---

## 4. 项目结构规范

### 4.1 PMS Maven 多模块结构

```
PMS/
├── pom.xml                    # 父 POM，定义所有子模块
├── AGENTS.md                  # 项目开发指南
├── core/                      # 共享核心框架（war+jar）
│   ├── pom.xml
│   ├── src/main/java/         # 标准 Maven 源码目录
│   ├── src/main/resources/    # 配置文件（spring*.xml、ehcache.xml、jdbc.properties）
│   └── src/main/webapp/       # Web 资源
├── PMS-struts/                # 遗留 Struts2 Web 应用（war+jar）
│   ├── pom.xml
│   ├── src/                   # ⚠️ 非标准源码目录（非 src/main/java）
│   ├── config/                # ⚠️ 非标准配置目录（非 src/main/resources）
│   ├── WebContent/            # Web 资源（JSP、CSS、JS）
│   └── bin/                   # 编译输出（Eclipse 风格）
├── PMS-springmvc/             # Spring MVC Web 应用（war+jar）
│   ├── pom.xml
│   ├── src/main/java/
│   ├── src/main/resources/    # 含 profiles/ 多环境配置
│   └── src/main/webapp/
├── PMS-activiti/              # Activiti 工作流（war+jar）
├── PMS-ext-d365/              # D365 集成扩展（jar）
├── PMS-security/              # 安全组件（jar）
├── pms-rules/                 # 规则引擎（jar）
└── pms-ext-fp/                # FP 集成扩展（jar）
```

### 4.2 源码目录规范

#### 4.2.1 标准模块（core、PMS-springmvc 等）

使用标准 Maven 目录结构：

```
src/main/java/         # Java 源码
src/main/resources/    # 配置文件
src/main/webapp/       # Web 资源
src/test/java/         # 测试代码
```

#### 4.2.2 PMS-struts 非标准目录

> **关键陷阱**：PMS-struts 源码目录为 `src/`（非 `src/main/java`），配置目录为 `config/`（非 `src/main/resources`）。在 `pom.xml` 的 `<sourceDirectory>` 中配置：

```xml
<build>
    <sourceDirectory>src</sourceDirectory>
    <!-- 配置文件目录为 config/ -->
</build>
```

**PMS-struts 目录结构**：

```
PMS-struts/
├── src/                       # Java 源码（非标准）
│   └── com/dp/plat/
├── config/                    # 配置文件（非标准）
│   ├── profiles/              # 多环境配置
│   │   ├── dev/
│   │   ├── test/
│   │   └── yfpms/
│   ├── beans-quartz.xml       # Quartz 定时任务
│   ├── ehcache.xml            # EhCache 配置
│   ├── struts.xml             # Struts2 主配置
│   ├── struts-sys.xml         # Struts2 业务配置
│   └── system.properties      # 系统参数
├── WebContent/                # Web 资源
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── lib/               # 依赖 JAR（含 system 作用域的 Utils-v0.1.jar）
│   ├── sys/                   # JSP 页面
│   ├── css/
│   └── js/
└── bin/                       # 编译输出（Eclipse 风格）
```

#### 4.2.3 SPMS 非 Maven 项目结构

> **关键陷阱**：SPMS 是传统 Eclipse 项目，**无法使用 `mvn` 命令构建**，通过 Eclipse Export WAR 或 Ant 脚本构建。

```
SPMS/
├── src/com/dp/plat/           # Java 源码
├── config/                    # Struts2 配置、jdbc.properties
├── config-ibaits/             # iBATIS SqlMap 配置
├── config-spring/             # Spring Bean 配置
├── WebContent/                # Web 资源
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── lib/               # 依赖 JAR（手动管理）
│   └── sys/                   # JSP 页面
└── build/classes/             # 编译输出
```

### 4.3 配置文件目录规范

| 项目 | 配置类型 | 目录 | 说明 |
|------|----------|------|------|
| PMS-springmvc | Spring 配置 | `src/main/resources/` | `spring-shiro-cas.xml`、`spring-mvc.xml` |
| PMS-springmvc | 多环境配置 | `src/main/resources/profiles/<env>/` | `quartz-job.xml`、`config.properties` |
| PMS-struts | Spring 配置 | `config/`、`config-spring/` | `applicationContext*.xml` |
| PMS-struts | 多环境配置 | `config/profiles/<env>/` | `web.xml` |
| PMS-struts | iBATIS 配置 | `bin/`、`config/` | `sql-map-config.xml` |
| SPMS | Spring 配置 | `config-spring/`、`build/classes/` | `applicationContext*.xml` |
| SPMS | iBATIS 配置 | `config-ibaits/`、`build/classes/` | `sql-map-config.xml` |

> **避坑指南**：SPMS 的配置文件存在双份（`config-ibaits/`、`config-spring/` 与 `build/classes/` 下都有），修改时需同步更新两处。

### 4.4 MyBatis / iBATIS 映射文件规范

#### 4.4.1 MyBatis 映射文件（PMS-springmvc / core）

- **位置**：与 Java 文件同目录，`com/dp/plat/**/mapping/*.xml`
- **根元素**：`<mapper>`
- **占位符**：`#{var}`
- **字符串拼接**：`${var}`
- **通过 maven-resources-plugin 复制到 classpath**

```xml
<mapper namespace="com.dp.plat.core.dao.SystemVariableMapper">
    <select id="querySystemVariables" resultType="java.util.HashMap">
        SELECT code, var FROM t_sys_variable
        WHERE effectiveFrom < now() AND now() < IFNULL(effectiveTo, '9999-12-31 23:59:59')
    </select>
</mapper>
```

#### 4.4.2 iBATIS 映射文件（PMS-struts / SPMS）

- **根元素**：`<sqlMap>`
- **占位符**：`#var#`
- **字符串拼接**：`$var$`
- **参数属性**：`parameterClass`
- **结果属性**：`resultClass` / `resultMap`

```xml
<sqlMap namespace="business">
    <select id="findProjectList" parameterClass="com.dp.plat.data.bean.ProjectQuery"
        resultClass="com.dp.plat.data.bean.Project">
        SELECT projectId, projectCode, projectName
        FROM pm_project_header
        WHERE effectiveTo IS NULL
    </select>
</sqlMap>
```

> **避坑指南**：iBATIS 2.x（SPMS/PMS-struts）与 MyBatis 3.x（PMS-springmvc/core）语法**不可混用**。修改 SQL 映射时必须确认所属 ORM 框架。

### 4.5 模块依赖关系

```
core → PMS-struts → PMS-springmvc
core → PMS-activiti → PMS-springmvc
PMS-ext-d365 → PMS-struts, PMS-springmvc
PMS-security → PMS-struts
pms-rules → PMS-struts, pms-ext-fp
```

**WAR+JAR 打包模式**：core、PMS-struts、PMS-springmvc、PMS-activiti 打包为 `war`，同时通过 `classifier` 生成 JAR 供其他模块依赖：

| Classifier | 说明 |
|------------|------|
| `core` | PMS-struts 的核心 JAR |
| `api` | PMS-activiti 的 API JAR |

---

## 5. 避坑指南与最佳实践

### 5.1 避坑指南

| 陷阱 | 影响 | 解决方案 |
|------|------|----------|
| PMS-struts 源码目录非标准 | IDE 无法识别源码 | 在 pom.xml 中配置 `<sourceDirectory>src</sourceDirectory>` |
| PMS-springboot 模块不存在 | 构建失败 | pom.xml 中列出了该模块但磁盘无此目录，构建时需排除 |
| Struts2 版本不一致 | 类冲突 | PMS-struts 用 2.3.35，PMS-springmvc 用 2.5.30，不可混用 API |
| system 作用域依赖 | 构建环境依赖 | PMS-struts 的 `echarts-utils` 位于 `WebContent/WEB-INF/lib/Utils-v0.1.jar` |
| SPMS 非 Maven 项目 | 无法 mvn 构建 | 使用 Eclipse Export WAR 或 Ant 脚本 |
| iBATIS 与 MyBatis 混用 | SQL 映射语法错误 | 确认模块所属 ORM，使用对应语法 |
| Action 未设 scope=prototype | 数据串扰 | Struts2 Action 有状态，必须 `scope="prototype"` |
| Service 注入错误 | 事务不生效 | PMS-struts/SPMS 中 Action 必须注入 `*ServiceAgent` |
| 事务方法前缀不匹配 | 写操作无事务 | 使用 `insert*/update*/delete*/save*/add*/do*` 等前缀 |
| 配置文件双份（SPMS） | 修改不生效 | `config-*/` 与 `build/classes/` 需同步更新 |

### 5.2 最佳实践

1. **新增模块时**：遵循标准 Maven 目录结构（`src/main/java`、`src/main/resources`），避免 PMS-struts 的非标准结构
2. **新增 Service 方法时**：写操作务必使用事务前缀（`save*`、`insert*`、`update*`、`delete*`），查询操作使用非事务前缀（`find*`、`get*`、`query*`）
3. **新增数据库表时**：按业务域使用正确前缀（`pm_`、`fnd_`、`t_`、`rma_`、`brw_`），包含审计字段和 `effectiveFrom/effectiveTo`
4. **新增 Action/Controller 时**：继承对应基类（`BaseAction` / `AbstractController`），复用模板方法
5. **跨项目修改时**：确认所属项目（PMS Git / SPMS SVN）和 ORM 框架（MyBatis / iBATIS），使用正确的语法和版本控制操作
6. **配置环境时**：使用 Maven Profile 管理多环境配置，不提交真实凭据
7. **提交代码前**：清理注释代码，检查 TODO/FIXME 标记，确认配置文件已脱敏
