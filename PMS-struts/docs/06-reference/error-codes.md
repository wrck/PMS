# 错误处理与消息机制

> ⚠️ **修订说明**：本文档早期版本虚构了 `ResultCode` 类、`P1001`/`W2001` 等业务错误码、`BusinessException`/`SystemException` 异常类以及 `Result.error(code, message)` 方法。经源码交叉验证，上述内容在 PMS-struts 源码中均不存在。本文档已基于实际源码全面重写，反映 PMS 项目真实的错误处理机制。
>
> 验证来源：`com.dp.plat.util.MessageUtil`、`com.dp.plat.action.BaseAction`、`com.dp.plat.service.BaseService`、`com.dp.plat.exception.CustomRuntimeException`、`com.dp.plat.data.vo.Result`

---

## 1. 错误处理体系概述

PMS 系统基于 Struts2 + iBATIS 架构，**未采用统一的字符串错误码体系**（如 `P1001`/`W2001` 等），而是通过以下机制处理错误：

| 机制 | 位置 | 说明 |
|------|------|------|
| Struts2 视图返回值 | Action 层 | 通过返回 `INPUT`/`SUCCESS`/`ERROR` 等逻辑视图名控制流程跳转 |
| 字段错误消息 | `BaseAction` | 调用 `addFieldError(fieldName, message)` 收集错误消息，前端通过 `<s:fielderror/>` 展示 |
| Service 错误传递 | `BaseService` | Service 层通过 `errmsg`/`warnmsg` 消息列表向上传递错误，Action 层通过 `setErrmsg(BaseService)` 同步 |
| 状态码常量 | `MessageUtil` | 整数状态码 `ERR_CODE=2`/`SUCC_CODE=1`，以及业务状态常量（项目状态、成员角色等） |
| 自定义异常 | `CustomRuntimeException` | 继承 `RuntimeException`，仅封装消息，无错误码 |
| 返回值对象 | `Result` | 仅在 `SubcontractServiceImpl` 等少数场景使用，非全局统一封装 |

> **重要**：PMS 项目**不存在** `ResultCode` 类、`BusinessException`/`SystemException` 异常类，也**不存在** `P1001`/`W2001`/`U3001`/`F4001`/`S5001` 等业务错误码。前端 AJAX 错误处理基于 `addFieldError` 写入的 `errmsg` 字段或 `Result` 对象的 `success` 布尔标志。

---

## 2. MessageUtil 状态码与消息常量

`com.dp.plat.util.MessageUtil` 是 PMS 的常量中心，记录所有 hard code 的状态码与消息文本。

### 2.1 通用结果码

```java
public class MessageUtil {
    public static final int ERR_CODE = 2;    // 错误
    public static final int SUCC_CODE = 1;   // 成功
    public static final String SAVE_FAILED = "保存失败";
    public static final String SAVE_SUCCESS = "保存成功";
}
```

| 常量 | 值 | 用途 |
|------|-----|------|
| `ERR_CODE` | `2` (int) | Service 层标识操作失败（`isError()` 判断依据） |
| `SUCC_CODE` | `1` (int) | Service 层标识操作成功 |
| `SAVE_FAILED` | `"保存失败"` | 通用保存失败提示 |
| `SAVE_SUCCESS` | `"保存成功"` | 通用保存成功提示 |

### 2.2 业务状态常量（节选）

MessageUtil 中定义了大量业务状态常量，这些是实际的"编码"，但属于**状态码**而非**错误码**，详细清单见 [data-dictionary.md](./data-dictionary.md)。

| 常量类别 | 示例 | 说明 |
|---------|------|------|
| 项目类型 | `PROJECT_TYPE_AFTERSALES="10"`, `PROJECT_TYPE_PRESALES="20"` | 售后/售前项目类型 |
| 项目状态 | `PROJECT_STATE_CREATING="10"`, `PROJECT_STATE_DENY="20"`, `PROJECT_STATE_CLOSEDLOOP="100"` | 存储在 `projectState` 字段 |
| 项目创建状态 | `PROJECT_CREATE_STATE30`~`PROJECT_CREATE_STATE50` | 存储在 `isback` 字段，标识回退流程状态 |
| 项目计划状态 | `PROJECT_PLAN_STATE_40`~`PROJECT_PLAN_STATE_48` | 项目计划阶段状态 |
| 数据类型 | `DATATYPE_CODE03_10/20/30`, `DATATYPE_CODE07_10/20/30` | 基础数据类型编码 |

---

## 3. BaseAction / BaseService 错误消息机制

### 3.1 BaseService 接口

`com.dp.plat.service.BaseService` 定义了 Service 层的错误消息收集接口：

```java
public interface BaseService {
    List<String> getErrmsg();           // 获取错误消息列表
    void addErrmsg(String errmsg);      // 追加错误消息
    boolean isError();                  // 是否发生错误
    void clearErrMsg();                 // 清空错误消息
    List<String> getWarnmsg();          // 获取警告消息列表
}
```

### 3.2 BaseAction 错误同步

`com.dp.plat.action.BaseAction` 继承 Struts2 `ActionSupport`，将 Service 层错误同步到 Struts2 字段错误：

```java
public class BaseAction extends ActionSupport
        implements ServletContextAware, ServletRequestAware, ServletResponseAware {

    // 将 Service 的错误/警告消息同步到 Action 的 FieldError
    protected void setErrmsg(BaseService service) {
        for (String msg : service.getErrmsg()) {
            this.addFieldError("errmsg", msg);     // 写入 errmsg 字段错误
        }
        for (String warnMsg : service.getWarnmsg()) {
            this.addFieldError("warnmsg", warnMsg); // 写入 warnmsg 字段错误
        }
        service.clearErrMsg();
    }

    // 直接设置错误消息
    public void setErrmsg(String errmsg) {
        if (errmsg != null && !errmsg.trim().isEmpty()) {
            this.addFieldError("errmsg", errmsg);
        }
    }
}
```

### 3.3 典型错误处理流程

```java
// Action 层典型模式
public String insertProject() {
    user = UserContext.getUserContext().getUser();
    if (checkProjectNull(project)) {
        return INPUT;                    // 首次进入，展示表单
    }
    projectService.insertProject(project);
    setErrmsg(projectService);           // 同步 Service 错误到 Action
    if (hasFieldErrors()) {
        return INPUT;                    // 有错误，返回输入页
    }
    return SUCCESS;                      // 成功
}
```

### 3.4 前端展示

Struts2 标签库自动渲染字段错误：

```jsp
<!-- 展示 errmsg 字段的所有错误消息 -->
<s:fielderror fieldName="errmsg"/>
<!-- 展示 warnmsg 字段的所有警告消息 -->
<s:fielderror fieldName="warnmsg"/>
```

---

## 4. CustomRuntimeException 自定义异常

`com.dp.plat.exception.CustomRuntimeException` 是 PMS 唯一的自定义异常类，继承 `RuntimeException`，**仅封装消息文本，不携带错误码**。

```java
public class CustomRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -7682210354187194944L;

    public CustomRuntimeException() {}
    public CustomRuntimeException(String message) { super(message); }
    public CustomRuntimeException(String message, Throwable cause) { super(message, cause); }
    public CustomRuntimeException(Throwable cause) { super(cause); }
}
```

> **注意**：PMS 项目**不存在** `BusinessException` 或 `SystemException`。源码中仅存在 `CustomRuntimeException`，用于包装不可恢复的运行时异常。

---

## 5. Result 返回值对象（有限使用）

`com.dp.plat.data.vo.Result` 是一个返回值封装对象，**仅在 `SubcontractServiceImpl` 等少数场景使用**，并非全局统一的接口返回封装。

### 5.1 类定义

```java
public class Result {
    private Object status;    // 执行状态
    private boolean success;  // 是否成功
    private Object data;      // 结果集
    private String message;   // 返回信息
    private String code;      // 返回状态码（可选）

    // 静态工厂方法
    public static Result success() { return new Result(true); }
    public static Result success(Object data) { return new Result(true, data); }
    public static Result fail(String message) { return new Result(false, message); }
}
```

### 5.2 实际使用范围

经源码检索，`Result.success` / `Result.fail` 仅在以下位置使用：

| 文件 | 使用情况 |
|------|---------|
| `SubcontractServiceImpl.java` | 3 处 `Result.success(...)` 调用 |

> **重要**：`Result` 类**不存在** `Result.error(String code, String message)` 方法，实际失败方法为 `Result.fail(String message)`。`Result` 类的 `code` 字段为可选字段，未在项目中被赋予统一编码体系。

---

## 6. HTTP 状态码使用

PMS 作为 Struts2 传统 Web 应用，HTTP 状态码由容器和 Struts2 框架管理，不通过业务错误码映射：

| 场景 | HTTP 状态 | 说明 |
|------|----------|------|
| 正常请求 | 200 | Struts2 Action 正常返回视图 |
| 未登录访问 | 302 | 由 `UserCheckFilter` 重定向到登录页 |
| 404 页面 | 200 | 由 `/404.action` (`LoginAction.error404`) 渲染页面，非真实 HTTP 404 |
| 服务器异常 | 500 | 未捕获异常由容器处理 |

> **注意**：PMS 未实现基于 HTTP 状态码的 RESTful 错误码体系。404 页面通过 `LoginAction.error404()` 返回逻辑视图，HTTP 状态仍为 200。

---

## 7. 前端 AJAX 错误处理

PMS 前端 AJAX 请求（基于 jQuery）通过以下方式判断错误：

### 7.1 基于 Struts2 字段错误（主流模式）

```javascript
// AJAX 请求返回包含 fieldErrors 的 JSON 或 HTML 片段
$.post(url, params, function(data) {
    // 检查返回内容中是否包含 errmsg 错误信息
    if (data.indexOf('errmsg') > -1 || hasError(data)) {
        // 展示错误消息
        showErrorMsg(data);
    } else {
        // 成功处理
        navTab.reload(flag);
    }
});
```

### 7.2 基于 Result 对象（少数场景）

```javascript
// SubcontractServiceImpl 返回的 Result 对象
$.post(url, params, function(result) {
    if (result.success) {
        // 成功，result.data 为返回数据
        alert('操作成功');
    } else {
        // 失败，result.message 为错误消息
        alert(result.message);
    }
}, 'json');
```

> **注意**：PMS 前端**不存在**基于 `response.code === '401'` 或 `response.code === '403'` 的统一错误码分支处理。早期文档中描述的 `handleError(response)` 函数按 `401`/`403` 错误码分支跳转的模式为虚构内容，不符合实际实现。

---

## 8. 与早期文档的差异说明

| 早期文档内容 | 实际情况 | 修正说明 |
|-------------|---------|---------|
| `ResultCode` 类（含 code/message 字段） | **不存在** | PMS 无此类，已删除 |
| `P1001`/`P1002`/`W2001`/`U3001`/`F4001`/`S5001` 等业务错误码 | **不存在** | 源码中无任何此类字符串错误码，已删除 |
| `BusinessException`/`SystemException` 异常类 | **不存在** | 实际仅有 `CustomRuntimeException`，已修正 |
| `Result.error("P1001", "项目编码已存在")` | **不存在** | 实际为 `Result.fail(message)`，无错误码参数，已修正 |
| `Result.success(data)`/`Result.error(...)` 全局统一封装 | **不存在** | `Result` 仅在 `SubcontractServiceImpl` 使用，非全局模式，已修正 |
| 前端按 `401`/`403` 错误码分支处理 | **不存在** | 前端基于 `errmsg`/`success` 判断，已修正 |
