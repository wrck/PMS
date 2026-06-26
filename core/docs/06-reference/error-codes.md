# core 模块 — 错误处理与返回值规范

> 本文档基于 `com.dp.plat.core.vo.Result`、`com.dp.plat.core.vo.ResultCode`、`com.dp.plat.core.controller.ExceptionController`、`com.dp.plat.core.exception.exceptionHandler.ExceptionHandler` 实际源码编写。
> **重要说明**：经源码核对，core 模块**未采用统一的错误码枚举体系**。`ResultCode` 是一个普通 POJO 类（非 enum），无预定义常量；`Result` 通过 `success` 布尔字段标识成败，`code` 字段为可选的自由字符串。本文档修订自早期版本（早期版本曾虚构 ResultCode enum 与 BusinessException，已删除）。

---

## 1. 错误处理体系概述

core 模块**未建立集中式错误码体系**。错误处理分散在三处：

| 组件 | 实际职责 |
|------|---------|
| `Result` | 通用返回值封装，通过 `success` 布尔字段标识成败，`message` 携带可读消息，`code` 为可选自由字符串 |
| `ExceptionHandler`（`core.exception.exceptionHandler`） | 实现 `HandlerExceptionResolver`，捕获 Controller 层未处理异常，记录 `t_sys_log`，重定向到 `/500.html` |
| `ExceptionController`（`core.controller`） | 普通 `@Controller`，处理 404/500/unauthorized/illegal 等错误**页面**跳转，不做异常到错误码的映射 |

> **重要事实**：core 模块中**不存在** `BusinessException` 类（grep 全模块无匹配）；`ExceptionController` **不是** `@ControllerAdvice`，也**没有** `@ExceptionHandler` 方法。早期文档中"认证异常 → ResultCode 映射表"为臆测内容，已删除。

### 1.1 异常体系实际构成

```
RuntimeException
   └── CustomRuntimeException (implements CustomExceptionInterface)
          ├── CaptchaException          验证码错误（ShiroRealm 抛出）
          ├── UploadException           文件上传异常
          └── ExcelImportException      Excel 导入异常
```

- `CustomExceptionInterface`：自定义异常统一接口；
- 这些异常**不携带错误码字段**，仅携带 message；
- 异常由 Shiro 框架（认证场景）或 Controller 直接 try/catch 处理，转 `Result.fail(message)` 返回。

## 2. Result 与 ResultCode 类定义（实际源码）

### 2.1 Result 统一返回对象

`com.dp.plat.core.vo.Result` 为**非泛型** POJO，字段为 `status / success / data / message / code`：

```java
public class Result {
    private Object status;    // 执行状态（通常与 success 同值）
    private boolean success;  // 执行结果
    private Object data;      // 结果集
    private String message;   // 返回信息
    private String code;      // 返回状态码（可选，自由字符串）

    // 默认构造：success=true, status=true
    public Result() { ... }

    // 多种重载构造：可传入 (success)、(success, data)、(success, data, message)、
    //               (success, data, message, code)、(status, ...)、(code, message)、(ResultCode)
    public Result(boolean success, Object data, String message, String code) { ... }
    public Result(ResultCode rc) { this.code = rc.getCode(); this.message = rc.getMessage(); }

    // 链式 setter：success(boolean)/status(Object)/data(Object)/message(String)/code(String)

    // 静态工厂
    public static Result success() { return new Result(true); }
    public static Result success(Object data) { return new Result(true, data); }
    public static Result fail(String message) { return new Result(false, message); }

    // 序列化为 Map（用于 JSON 输出）
    public Map<String, Object> getMap() { ... }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `success` | boolean | 执行是否成功（核心字段，前端判断依据） |
| `status` | Object | 执行状态，通常与 `success` 同值；可为扩展状态对象 |
| `data` | Object | 成功时返回的数据 |
| `message` | String | 用户可读消息（成功/失败均可用） |
| `code` | String | 可选状态码，自由字符串（无预定义常量） |

> **注意**：`Result` **没有** `fail(ResultCode)` 静态方法，也**没有** `isSuccess()` 检查 `ResultCode.SUCCESS`。前端应通过 `success` 字段判断成败。

### 2.2 ResultCode 类（POJO，非枚举）

`com.dp.plat.core.vo.ResultCode` 是普通 Java 类，**不是 enum**，**无预定义常量**：

```java
public class ResultCode {
    private String code;
    private String message;

    public ResultCode() {}
    public ResultCode(String code, String message) { this.code = code; this.message = message; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
```

- 调用方需自行 `new ResultCode("xxx", "yyy")` 构造实例；
- core 源码中**未发现** `SUCCESS`、`SYS_ERR`、`AUTH_FAIL` 等任何预定义常量；
- `Result(ResultCode rc)` 构造器仅复制 `code` 与 `message`，不设置 `success`/`data`。

---

## 3. core 模块无统一错误码体系（重要事实）

> **重要更正**：经源码核对（`ResultCode.java`、`Result.java`、`ExceptionController.java`、`ExceptionHandler.java`），core 模块**未建立任何预定义错误码常量**。早期文档中列出的 `SUCCESS`/`SYS_ERR`/`PARAM_ERROR`/`UNAUTHORIZED`/`AUTH_FAIL`/`CAPTCHA_ERR` 等常量**在源码中均不存在**，已删除。

### 3.1 实际错误处理方式

core 模块的错误处理分散在以下组件，**均不依赖统一错误码枚举**：

| 场景 | 处理方式 | 涉及组件 |
|------|---------|---------|
| Shiro 认证失败 | 抛 Shiro 标准异常（`CaptchaException`/`UnknownAccountException`/`DisabledAccountException`/`IncorrectCredentialsException` 等） | `ShiroRealm` |
| Shiro 授权失败 | 抛 `UnauthorizedException` | Shiro 框架 |
| Controller 业务错误 | 直接 `return Result.fail(message)` 或 `new Result(false, data, message)` | 各 Controller |
| 文件上传异常 | 抛 `UploadException`，Controller 捕获后转 `Result.fail` | `UploaderController` |
| Excel 导入异常 | 抛 `ExcelImportException`，Controller 捕获后转 `Result.fail` | 导入相关 Controller |
| 未捕获异常 | 由 `ExceptionHandler`（`HandlerExceptionResolver`）捕获，记录 `t_sys_log`，重定向到 `/500.html` | `ExceptionHandler` |
| 404/500 页面 | `ExceptionController` 处理错误页面跳转（`/500`、`/404`、`/to500`、`/to404`、`/unauthorized`、`/illegal`） | `ExceptionController` |

### 3.2 ExceptionController 真实职责

经源码核对，`ExceptionController` 是普通 `@Controller`（**非** `@ControllerAdvice`），**没有** `@ExceptionHandler` 方法。它仅处理错误**页面跳转**：

```java
@Controller
public class ExceptionController {
    @RequestMapping("/500")
    public String error500(String errorLogId, String error, Model model) { ... }

    @RequestMapping("/404")
    public String error404() { ... }

    @RequestMapping("/to500")
    public ModelAndView to500(HttpServletRequest request, HttpServletResponse response) { ... }

    @RequestMapping("/to404")
    public ModelAndView to404(HttpServletRequest request, HttpServletResponse response) { ... }

    @RequestMapping("/unauthorized")
    public String unauthorized() { ... }

    @RequestMapping("/illegal")
    @SystemControllerLog(description = "【违规操作】$user.realName$$illegalName$")
    public String errorIllegal(HttpServletRequest request, HttpServletResponse response, Model model) { ... }
}
```

### 3.3 不存在的类与常量

经全模块 grep 验证，以下内容**在 core 源码中不存在**：

| 不存在项 | 早期文档错误描述 | 实际情况 |
|---------|----------------|---------|
| `BusinessException` 类 | "自定义业务异常，携带 ResultCode" | core 无此类，业务错误直接抛 RuntimeException 或自定义 `CustomRuntimeException` 子类 |
| `ResultCode.SUCCESS`/`SYS_ERR` 等常量 | "预定义错误码常量" | `ResultCode` 是 POJO，无任何静态常量 |
| `Result.fail(ResultCode)` 静态方法 | "按错误码构造失败 Result" | `Result` 仅有 `fail(String message)` 静态方法 |
| `@ControllerAdvice` ExceptionController | "全局异常拦截 + 错误码映射" | `ExceptionController` 是 `@Controller`，仅处理页面跳转 |
| `FileTypeException`/`FileSizeException`/`DataNotFoundException`/`DataSourceException` | "文件/数据相关异常类" | core 无这些类 |
| `TicketValidationException` 关联 `CAS_TICKET_ERR` | "CAS 票据错误码" | 该异常由 CAS 客户端抛出，core 未定义错误码 |

---

## 4. 实际异常类清单（源自源码）

core 模块实际定义的异常类（`com.dp.plat.core.exception` 包）：

| 异常类 | 父类 | 说明 |
|--------|------|------|
| `CustomRuntimeException` | `RuntimeException` implements `CustomExceptionInterface` | 自定义运行时异常基类 |
| `CaptchaException` | `CustomRuntimeException` | 验证码错误（`ShiroRealm` 抛出） |
| `UploadException` | `CustomRuntimeException` | 文件上传异常 |
| `ExcelImportException` | `CustomRuntimeException` | Excel 导入异常 |
| `CustomExceptionInterface` | - | 自定义异常统一接口（约定接口） |

> **说明**：这些异常**不携带错误码字段**，仅携带 message。`CustomExceptionInterface` 是接口约定，`CustomRuntimeException` 实现该接口。

---

## 5. Result 使用规范（基于实际源码）

### 5.1 Controller 层使用

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    @SystemControllerLog(description = "创建用户")
    public Result create(@RequestBody User user) {
        // 1. 参数校验（直接返回 fail，不抛异常）
        if (StringUtils.isBlank(user.getUserName())) {
            return Result.fail("用户名不能为空");
        }

        // 2. 业务校验
        User existing = userService.selectByUserName(user.getUserName());
        if (existing != null) {
            return Result.fail("用户名已存在");
        }

        // 3. 业务执行
        try {
            userService.insert(user);
            return Result.success(user.getUserId());
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.fail("创建用户失败：" + e.getMessage());
        }
    }
}
```

### 5.2 Service 层使用

```java
@Service
public class UserServiceImpl implements IUserService {

    public void insert(User user) {
        // 业务校验失败，直接抛 RuntimeException 或 CustomRuntimeException
        if (existsByName(user.getUserName())) {
            throw new CustomRuntimeException("用户名已存在");
        }

        try {
            userMapper.insert(user);
        } catch (Exception e) {
            log.error("数据库异常", e);
            throw new CustomRuntimeException("数据保存失败：" + e.getMessage());
        }
    }
}
```

### 5.3 前端处理建议

```javascript
// 前端统一处理 Result（基于实际 Result 结构）
axios.interceptors.response.use(
    response => {
        const result = response.data;
        // 实际 Result 通过 success 字段判断成败（无 code='0' 约定）
        if (result.success === true || result.status === true) {
            return result.data;
        } else {
            // 失败时展示 message
            Message.error(result.message || '操作失败');
            return Promise.reject(result);
        }
    },
    error => {
        Message.error('网络异常');
        return Promise.reject(error);
    }
);
```

---

## 6. ResultCode 扩展规范

### 6.1 业务模块扩展错误码（可选）

上层业务模块如需建立自有错误码体系，可基于 `ResultCode` POJO 自行扩展：

```java
// 项目管理模块错误码（业务模块自定义，core 未提供基类）
public class ProjectResultCode {
    public static final ResultCode PROJECT_NOT_FOUND = new ResultCode("PROJECT_NOT_FOUND", "项目不存在");
    public static final ResultCode PROJECT_STATE_ERR = new ResultCode("PROJECT_STATE_ERR", "项目状态不允许操作");
    public static final ResultCode PROJECT_CODE_DUPLICATE = new ResultCode("PROJECT_CODE_DUPLICATE", "项目编码已存在");
}
```

> **注意**：core 本身**未定义**任何 `ResultCode` 静态常量。上述扩展由业务模块自行实现，core 不强制约定。

### 6.2 错误码命名规范（建议）

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 通用错误 | `大写_大写` | `SYS_ERR`、`PARAM_ERROR` |
| 认证错误 | `认证相关_大写` | `AUTH_FAIL`、`CAPTCHA_ERR` |
| 文件错误 | `FILE_大写` | `FILE_NOT_FOUND`、`FILE_TYPE_ERR` |
| 业务错误 | `模块_大写` | `PROJECT_NOT_FOUND`、`USER_DUPLICATE` |

> **避坑**：业务模块错误码需加模块前缀，避免与其他模块冲突。core 不维护统一错误码登记表。

---

## 7. 错误处理速查表

### 7.1 按场景速查（基于实际处理方式）

| 场景 | 实际处理方式 | 处理位置 |
|------|------------|----------|
| 登录密码错误 | Shiro 抛 `IncorrectCredentialsException`，Controller 转为 `Result.fail` | `ShiroRealm` + `LoginController` |
| 验证码错误 | 抛 `CaptchaException`，Controller 转为 `Result.fail` | `ShiroRealm` |
| 账号被锁 | 抛 `DisabledAccountException`，Controller 转为 `Result.fail` | `ShiroRealm` |
| 未登录访问 | Shiro 重定向到 `loginUrl` | `ShiroFilter` |
| 无权限访问 | Shiro 抛 `UnauthorizedException` 或重定向到 `unauthorizedUrl` | `ShiroFilter` |
| 参数缺失 | Controller 直接 `return Result.fail(message)` | 各 Controller |
| 文件上传失败 | 抛 `UploadException`，Controller 捕获转 `Result.fail` | `UploaderController` |
| Excel 导入失败 | 抛 `ExcelImportException`，Controller 捕获转 `Result.fail` | 导入 Controller |
| 系统异常 | `ExceptionHandler` 捕获并记录 `t_sys_log`，重定向 `/500.html` | `ExceptionHandler` |

### 7.2 Result 字段速查

| 字段 | 类型 | 实际用法 |
|------|------|---------|
| `success` | boolean | 核心判断字段，前端依据此判断成败（`Result.success()` 设 true，`Result.fail()` 设 false） |
| `status` | Object | 通常与 `success` 同值；可携带扩展状态对象 |
| `data` | Object | 成功时返回的数据 |
| `message` | String | 用户可读消息（成功/失败均可用） |
| `code` | String | 可选状态码，自由字符串（core 未定义常量） |

---

## 8. 相关文档

- [代码示例](code-examples.md) — Result/ResultCode 使用示例
- [接口模板](interface-template.md) — 接口文档标准模板
- [故障排查](../05-standards/troubleshooting.md) — 错误码相关故障案例
- [安全实践](../05-standards/security-practices.md) — 安全相关错误处理
- [Shiro 架构](../01-architecture/shiro-architecture.md) — 认证异常原理
- [用户管理](../02-modules/user-management.md) — 用户相关错误处理
- [文件管理](../02-modules/file-management.md) — 文件相关错误处理
