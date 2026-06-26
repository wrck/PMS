# core 模块 — 代码示例、接口模板与术语表

---

## 1. 代码示例

### 1.1 新增业务实体的完整套路（复用 core 基类）

```java
// 1) 实体：继承 BaseEntity 获得 id/审计字段/orgId/customInfo
public class MyEntity extends BaseEntity {
    private String name;
    // getter/setter...
}

// 2) Mapper：继承 AbstractBaseMapper<T>，获得标准 CRUD
public interface MyEntityMapper extends AbstractBaseMapper<MyEntity> {
    // 仅写自定义查询；CRUD 由基类提供
}

// 3) Service 接口：继承 IAbstractBaseService<T>
public interface IMyEntityService extends IAbstractBaseService<MyEntity> { }

// 4) Service 实现：继承 AbstractBaseService<T>，注入 Mapper
@Service
public class MyEntityServiceImpl extends AbstractBaseService<MyEntity>
        implements IMyEntityService {
    @Resource
    private MyEntityMapper mapper;
    // 基类已实现 insert/select/update/delete/count/分页
}

// 5) 多数据源：加注解即可切换
@DataSource("sap")   // 类级或方法级
public List<SapOrder> queryFromSap() { ... }
```
**应用场景**：新增任何主从业务表的标准模式，零样板获得全套 CRUD 与分页。

### 1.2 操作日志声明

```java
@Controller
@RequestMapping("/project")
public class ProjectController {
    @SystemControllerLog(description = "创建项目")  // AOP 自动落 t_sys_log
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(Project p) {
        service.insert(p);
        return Result.success(p.getId());
    }
}
```

### 1.3 统一返回与错误码

```java
public Result update(User u) {
    if (u.getUserId() == null)
        return Result.fail(ResultCode.PARAM_ERROR);  // 参数错误
    service.updateByPrimaryKeySelective(u);
    return Result.success();
}
// Result 结构：{ code, message, data }
```

### 1.4 密码加密（与 ShiroRealm 一致）

```java
// 登录认证时，DB 密码需与如下计算结果比对
String hashed = PasswordUtil.encryptMD5Password(
    rawPassword, userName, 1024);  // 明文 + 用户名盐 + 1024 次迭代
```

### 1.5 动态 Shiro 过滤器链

```java
// FilterChainDefinitionMapBuilder 读取 t_resource 构建：
//   /admin/** = authc,roles[admin]
//   /project/create = authc,perms[project:create]
// 修改 t_resource 后刷新即可生效，无需重启
```

---

## 2. 接口文档模板（core 通用 Controller 接口）

| 项目 | 内容 |
|------|------|
| 接口名称 | [中文名] |
| Controller | [如 LoginController] |
| 方法 / URL | [如 POST /login] |
| 功能描述 | [一句话] |
| 权限要求 | [如 authc / roles[admin]] |
| 请求参数 | [字段表：名/类型/必填/说明] |
| 返回格式 | `Result{code,message,data}` |
| 错误码 | 见错误码表 |
| 涉及表 | [如 t_user, t_sys_log] |

### 2.1 登录接口示例

| 项目 | 内容 |
|------|------|
| 接口名称 | 用户登录 |
| URL | `POST /login` |
| 参数 | userName(String,必填), password(String,必填), captcha(String,条件必填) |
| 返回 | `Result{code:"0",message:"成功",data:Principal}` |
| 错误码 | `CAPTCHA_ERR` 验证码错误；`UNKNOWN_ACCOUNT` 用户不存在；`DISABLED` 已禁用/锁定 |
| 涉及表 | t_user(R/U), t_user_login_record(C), t_sys_log(C), t_user_role/t_role_permission(R) |

---

## 3. 错误码（core 体系）

| 错误码 | 含义 | 触发场景 |
|--------|------|----------|
| 0 / SUCCESS | 成功 | 正常返回 |
| PARAM_ERROR | 参数错误 | 必填项缺失 |
| CAPTCHA_ERR | 验证码错误 | ShiroRealm 校验失败 |
| UNKNOWN_ACCOUNT | 用户不存在 | queryUserByName 为空 |
| DISABLED | 账号禁用/锁定 | status=0/2 |
| AUTH_FAIL | 认证失败 | 密码不匹配 |
| UNAUTHORIZED | 无权限 | Shiro 授权不通过 |
| UPLOAD_ERR | 上传失败 | UploadException |
| EXCEL_IMPORT_ERR | Excel 导入失败 | ExcelImportException |
| SYS_ERR | 系统异常 | 未捕获异常 |

> 错误码统一定义在 `ResultCode`，由 `ExceptionController` 映射异常→错误码。

---

## 4. 术语表

| 术语 | 解释 |
|------|------|
| core | PMS 底层基础框架模块，提供认证/多数据源/AOP/工具等横切能力 |
| Shiro | Apache Shiro，认证授权框架；core 用自定义 Realm 实现 |
| CAS | Central Authentication Service，单点登录协议；core 支持 CAS SSO |
| Realm | Shiro 中连接数据源的认证授权组件（ShiroRealm/CasRealm） |
| Principal | 认证主体，登录后封装用户/角色/权限的核心对象 |
| AbstractRoutingDataSource | Spring 动态数据源抽象类，core 据此实现多库切换 |
| ThreadLocal | 线程局部变量，core 用其持有当前数据源 Key |
| AOP | 面向切面编程；core 用于日志、数据源、异常切面 |
| BaseEntity | core 实体基类，含审计字段与 customInfo 扩展 |
| customInfo | JSON 扩展字段模式，业务实体动态存取属性 |
| orgId | 组织/公司ID，用于多公司数据隔离 |
| comp_id | 公司ID，t_user_role 中实现"同人不同公司不同角色" |
| isSysUser | 系统用户标记，授权时取 compId=-1 即跨公司全权限 |
| 工号 workNo | 员工工号，t_user_info 标识，与 EHR/OA 同步 |
| areaPower | 区域权限，t_user_info.custom5，控制用户可管辖地域 |
| officeCode | 办事处编码，t_user_info.custom3，关联 fnd_department |

---

## 相关文档

- [01-architecture 系统架构](../01-architecture/system-architecture.md)
- [02-modules 公共组件](../02-modules/common-components.md)
- [05-standards 编码规范](../05-standards/coding-standards.md)
