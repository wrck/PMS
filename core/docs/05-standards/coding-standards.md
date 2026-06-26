# core 模块 — 编码规范与最佳实践

> 基于 core 源码提炼，涵盖分层规范、命名约定、复用策略、并发控制与安全防护。

---

## 1. 分层编码规范

### 1.1 Controller 层
- 所有 Controller 加 `@Controller` + `@RequestMapping("/前缀")`；
- 通用基类 `BaseController` 提供弹窗/详情页跳转，业务 Controller 继承复用；
- 返回统一用 `Result`（`@ResponseBody`），页面跳转返回视图名字符串；
- 业务逻辑禁止写在 Controller，只做参数接收与 Service 调用。

### 1.2 Service 层
- 接口命名 `IXxxService`，实现 `XxxServiceImpl`；
- 泛型 CRUD 继承 `AbstractBaseService<T>`，**避免重复实现** delete/insert/select/update；
- 需要多数据源时在类或方法加 `@DataSource("key")`；
- 事务由 Spring 声明式管理（`@Transactional`），与数据源切换配合。

### 1.3 DAO 层
- MyBatis Mapper 命名 `XxxMapper`，XML 与接口同包（`com.dp.plat.core.mapping`）；
- 复杂查询写 XML，简单 CRUD 用基类；
- `resultMap` 显式映射（驼峰 property ⇄ 下划线/混合 column）。

### 1.4 实体层
- 业务实体继承 `BaseEntity`，自动获得 id/createBy/createTime/updateBy/updateTime/orgId/customInfo；
- 日期字段加 `@JsonSerialize(using=JsonSerializer.class)` 统一格式化；
- 实体可序列化（`implements Serializable`）。

---

## 2. 命名约定

| 类型 | 约定 | 示例 |
|------|------|------|
| Controller | `XxxController` | `LoginController` |
| Service 接口 | `IXxxService` | `IUserService` |
| Service 实现 | `XxxServiceImpl` | `UserServiceImpl` |
| Mapper | `XxxMapper` | `UserMapper` |
| 实体 | 单数名词 | `User`、`Role` |
| 数据库表 | `t_` 前缀 + 下划线 | `t_user_info` |
| 列名 | 业务混合（部分驼峰如 needChangePwd，部分下划线如 user_id） | — |
| 注解 | `@XxxLog` / `@DataSource` | `@SystemControllerLog` |

> **注意命名不一致**：core 表列存在驼峰（`needChangePwd`、`isSysUser`、`userCustom1`）与下划线（`user_id`、`create_by`）混用，新表统一用下划线。

---

## 3. 代码复用策略（core 的核心价值）

| 复用点 | 机制 | 收益 |
|--------|------|------|
| CRUD 样板 | `AbstractBaseService<T>` + `AbstractBaseMapper<T>` | 新实体零代码获 CRUD |
| 审计字段 | `BaseEntity` | 自动填充创建/更新信息 |
| 多公司隔离 | `BaseEntity.orgId` + `UserContext` | 自动数据范围控制 |
| 统一返回 | `Result`/`ResultCode` | 前端契约一致 |
| 分页 | `PageParam<T>` | 统一分页参数与结果 |
| 日志 | `@SystemControllerLog` AOP | 声明式操作审计 |
| 数据源 | `@DataSource` AOP | 声明式多库切换 |
| Excel 导出 | `ExcelView` | 复用导出视图 |
| 工具 | `util/*` 19 个工具类 | 避免重复造轮子 |

---

## 4. 并发控制

| 场景 | 方案 | 实现 |
|------|------|------|
| 异步任务线程池 | `RequestThreadPoolExecutor` | 复制请求上下文（用户/数据源）到子线程 |
| 上下文传递 | `ContextCopyingDecorator` | 装饰 Runnable，传递 ThreadLocal |
| 多数据源切换 | ThreadLocal | 必须在 `@After` 清理，防线程池串号 |
| 登录错误计数 | DB 原子更新 | `loginErrorCount` 累加（高并发需乐观锁） |

```java
// 多数据源线程池场景的正确用法：上下文必须显式传递
RequestThreadPoolExecutor executor = ...;
executor.submit(ContextCopyingDecorator.decorate(() -> {
    // 子线程中 ThreadLocal 已复制，数据源切换正确
    service.crossDbQuery();
}));
```

> **避坑**：异步线程中使用 `@DataSource` 时，DataSourceAspect 的 `@After` 清理只清当前线程。若线程池线程复用且上下文未正确传递/清理，会读到错误数据源。务必用 `ContextCopyingDecorator`。

---

## 5. 安全防护

| 防护点 | 实现 | 说明 |
|--------|------|------|
| 密码存储 | MD5 + 用户名盐 + 1024 次迭代 | `PasswordUtil.encryptMD5Password`，不可逆 |
| 验证码 | `UsernamePasswordCaptchaToken` | 防暴力破解，可按环境开关 |
| 账号锁定 | `loginErrorCount` + `status=2` | 错误次数达阈值自动锁 |
| XSS | `JsoupUtil` 清洗 HTML | 输入过滤（细化见 PMS-security） |
| CSRF | `CsrfTokenScriptTag` + Token | 表单携带 Token 校验 |
| SQL 注入 | MyBatis 参数化 + `SQLParser` | 禁止字符串拼接 SQL |
| 访问控制 | Shiro 过滤器链 + `t_resource` | URL 级 + 权限串级双重 |
| 数据隔离 | 公司 comp_id + orgId | 越权访问防护 |
| DES 加密 | `DESSecurityUtils` | 敏感数据传输加密 |
| 操作审计 | `@SystemControllerLog` | 全操作留痕 |

---

## 6. 性能优化

| 优化点 | 措施 |
|--------|------|
| 权限缓存 | Shiro 授权结果缓存到会话，避免每次查库 |
| 系统参数缓存 | `SystemConfig.systemVariables` 启动加载到内存，全局读不查库 |
| 菜单缓存 | 按角色缓存菜单树 |
| 日志异步 | 操作日志可异步写入，不阻塞主流程 |
| 索引 | t_user.user_name UNIQUE、各外键 MUL 索引 |
| 日志归档 | t_sys_log/t_user_login_record 定期归档 |

---

## 7. 异常处理规范

- 业务异常继承 `CustomRuntimeException` 并实现 `CustomExceptionInterface`，约定错误码；
- 已定义专用异常：`CaptchaException`、`UploadException`、`ExcelImportException`；
- Service 层抛异常，Controller 不 try-catch，由 `ExceptionController` 统一转 `Result`；
- 禁止吞异常（catch 后空处理），至少记录日志。

---

## 相关文档

- [01-architecture 系统架构](../01-architecture/system-architecture.md)
- [06-reference 代码示例](../06-reference/code-examples.md)
- 安全细化：[PMS-security/05-standards](../../PMS-security/docs/05-standards/coding-standards.md)
