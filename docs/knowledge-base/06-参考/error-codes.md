# 错误码说明

> 本文档从 PMS 与 SPMS 源码中提取错误码、异常信息与状态码定义。系统未采用统一的数字错误码体系，而是通过 `Result` 对象、异常类、业务状态常量三种机制传递错误信息。本文档如实记录源码中的实际定义，不做虚构。

---

## 1. 错误处理机制概述

PMS 与 SPMS 系统采用以下三种错误处理机制：

| 机制 | 载体 | 使用场景 | 源码位置 |
|------|------|---------|---------|
| Result 对象 | `com.dp.plat.core.vo.Result` | Spring MVC 接口返回 | `core/src/main/java/com/dp/plat/core/vo/Result.java` |
| 异常抛出 | 各类自定义异常 | 业务逻辑错误 | `core/src/main/java/com/dp/plat/core/exception/` |
| 业务状态常量 | `MessageUtil` 常量 | 业务状态标识 | `PMS-struts/src/com/dp/plat/util/MessageUtil.java` |

**说明**：系统未定义 `P1001`、`W2001` 等结构化错误码常量类。接口返回错误通过 `Result.success=false` + `message` 文本传递；业务异常通过抛出携带中文消息的异常类传递。

---

## 2. Result 对象定义

### 2.1 核心字段

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\vo\Result.java`

```java
public class Result {
    private Object status;    // 执行状态
    private boolean success;  // 执行结果（true/false）
    private Object data;      // 结果集
    private String message;   // 返回信息
    private String code;      // 返回状态码（可选）
}
```

### 2.2 静态工厂方法

| 方法签名 | 说明 |
|---------|------|
| `Result.success()` | 成功返回（无数据） |
| `Result.success(Object data)` | 成功返回（带数据） |
| `Result.fail(String message)` | 失败返回（带错误消息） |

### 2.3 ResultCode 类

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\vo\ResultCode.java`

```java
public class ResultCode {
    private String code;      // 错误码
    private String message;   // 错误消息
}
```

**说明**：`ResultCode` 为通用 POJO，未定义任何静态常量。调用方通过 `new ResultCode(code, message)` 构造实例。

---

## 3. 异常类清单

### 3.1 核心异常类

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\exception\`

| 异常类 | 父类 | 说明 | 源码位置 |
|--------|------|------|---------|
| `CustomRuntimeException` | RuntimeException | 自定义运行异常（实现 `CustomExceptionInterface`） | `core/.../exception/CustomRuntimeException.java` |
| `UploadException` | RuntimeException | 文件上传异常 | `core/.../exception/UploadException.java` |
| `ExcelImportException` | RuntimeException | Excel 导入异常（含 `progress` 字段） | `core/.../exception/ExcelImportException.java` |
| `CaptchaException` | RuntimeException | 验证码异常 | `core/.../exception/CaptchaException.java` |
| `CsrfValidateFailedException` | RuntimeException | CSRF 校验失败异常 | `core/.../security/csrf/CsrfValidateFailedException.java` |

### 3.2 PMS-struts 异常类

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-struts\src\com\dp\plat\exception\`

| 异常类 | 父类 | 说明 |
|--------|------|------|
| `CustomRuntimeException` | RuntimeException | 自定义运行异常（PMS-struts 版本，未实现接口） |
| `UploadException` | RuntimeException | 文件上传异常 |
| `SubcontractException` | CustomRuntimeException | 项目转包异常 |

### 3.3 PMS-activiti 异常类

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\process\exception\`

| 异常类 | 父类 | 说明 |
|--------|------|------|
| `CustomActivitiException` | ActivitiException | 自定义 Activiti 异常（实现 `CustomExceptionInterface`） |

### 3.4 PMS-ext-d365 异常类

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-ext-d365\src\main\java\com\dp\plat\pms\extend\d365\exception\`

| 异常类 | 父类 | 说明 |
|--------|------|------|
| `CustomRuntimeException` | RuntimeException | D365 集成自定义异常 |

---

## 4. 异常消息清单（从源码提取）

以下异常消息直接从源码 `throw new XxxException("...")` 语句中提取，按模块分类。

### 4.1 认证与权限模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\realms\ShiroRealm.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `AccountException` | "用户名称不允许为空!" | 登录时用户名为空 |
| `CaptchaException` | "验证码错误！" | 登录时验证码错误 |
| `UnknownAccountException` | "用户名或密码错误！" | 用户名不存在或密码错误 |
| `DisabledAccountException` | "用户已被锁定！" | 用户账户被锁定 |
| `DisabledAccountException` | "用户已被禁用！" | 用户账户被禁用 |

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\security\csrf\CsrfInterceptor.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `CsrfValidateFailedException` | "csrf token validate failed" | CSRF Token 校验失败 |

### 4.2 工作流模块（PMS-activiti）

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\service\impl\ProcessService.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `ActivitiIllegalArgumentException` | "转办后的办理人相同！" | 转办任务时办理人与原办理人相同 |
| `ActivitiObjectNotFoundException` | "此任务不存在！转办任务失败！" | 转办时任务不存在 |
| `ActivitiObjectNotFoundException` | "任务不存在！" | 查询任务不存在 |

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\process\cmd\WithdrawTaskCmd.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `CustomActivitiException` | e.getMessage() | 撤回任务时发生异常 |
| `ActivitiException` | "Couldn't execute listener" | 执行监听器失败 |

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-springmvc\src\main\java\com\dp\plat\pms\springmvc\listener\SubcontractInspectionListener.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `CustomActivitiException` | "验收审批关系不能为空！" | 转包验收审批关系为空 |

### 4.3 文件上传模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\util\UploadUtils.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `UploadException` | "contentType类型错误" | 上传文件 contentType 不合法 |

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\util\FileUtil.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `UploadException` | "不允许上传类型为【.{suffix}】的文件！" | 上传文件后缀不在白名单 |
| `UploadException` | "不允许上传类型为空的文件！" | 上传文件类型为空 |

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-struts\src\com\dp\plat\util\UploadFileUtil.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `UploadException` | "不允许上传类型为空的文件！" | 上传文件类型为空 |
| `UploadException` | "不允许上传类型为【.{suffix}】的文件！" | 上传文件后缀不在白名单 |

### 4.4 项目转包模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-struts\src\com\dp\plat\subcontract\service\impl\SubcontractServiceImpl.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `SubcontractException` | "项目转包申请流程正在进行，本次不再重复发起！" | 转包流程重复发起 |
| `SubcontractException` | "发起项目转包申请失败！" | 转包流程启动失败 |

### 4.5 外部系统集成模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-ext-d365\src\main\java\com\dp\plat\pms\extend\d365\util\D365Api.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `CustomRuntimeException` | `StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！")` | D365 接口调用失败 |

源码位置：`d:\常规软件\QoderCode\workspace\PMS\pms-ext-fp\src\main\java\com\dp\plat\pms\extend\fp\util\FPApi.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `RuntimeException` | "Error pushing data" | FP 数据推送异常 |
| `IllegalArgumentException` | "URL 构建失败" | URL 构建失败 |
| `IllegalArgumentException` | "不支持的HTTP方法: " + method | 不支持的 HTTP 方法 |

### 4.6 数据操作模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\core\src\main\java\com\dp\plat\core\controller\DataOperationController.java`

| 返回方式 | 消息 | 触发场景 |
|---------|------|---------|
| `new Result(false, ...)` | "参数不合法！" | SQL 注入检测命中 |
| `new Result(false, ...)` | "没有权限访问以下表{tables}！" | 无权限访问数据表 |
| `new Result(false, ...)` | "不允许为空！" | 参数为空 |

### 4.7 外派结算模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-springmvc\src\main\java\com\dp\plat\pms\springmvc\service\impl\DispatchSettlementService.java`

| 返回方式 | 消息 | 触发场景 |
|---------|------|---------|
| `Result.fail(...)` | "未找到对应的转包结算记录！" | 转包结算记录不存在 |

### 4.8 工作流任务撤回模块

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\service\impl\ProcessService.java`

| 返回方式 | 消息 | 触发场景 |
|---------|------|---------|
| `new Result(false, "不可撤回", ...)` | "该任务已经被签收或者办理，无法撤回，请查看流程明细" | 任务已签收或办理 |
| `new Result(false, "撤回异常", ...)` | "任务撤回发生异常,异常原因：" + ex.getMessage() | 撤回异常 |
| `new Result(false, null, ...)` | "已办理，不可撤回" | 任务已办理 |
| `new Result(false, null, ...)` | "任务被签收或办理，不可撤回" | 任务被签收或办理 |

### 4.9 SPMS 模块

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\plus\spms2mes\service\SPMS2MESServiceImpl.java`

| 返回方式 | 消息 | 触发场景 |
|---------|------|---------|
| `materialResult.setType("E")` + `setMessage(...)` | "接口未启用，请联系管理员！" | MES 接口未启用 |

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\action\SparePartsApplicantAction.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `RuntimeException` | "产品编码【{itemCode}】不存在" | 备件产品编码不存在 |

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\action\RmaApplicantAction.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `RuntimeException` | "没有找到故障定位角色用户" | RMA 故障定位角色用户未找到 |

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\service\RmaApplicantServiceImpl.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `RuntimeException` | "序列号[{newSpareNum}]不存在！" | RMA 备件序列号不存在 |

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\task\AbstractSynchronizeTask.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `RuntimeException` | "重试被中断" | 同步任务重试被中断 |
| `RuntimeException` | "重试完成，数据同步失败！" | 同步任务重试完成仍失败 |
| `RuntimeException` | "批量插入失败" | 批量插入数据失败 |

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\plus\certificate\service\CertificateServiceImpl.java`

| 异常类 | 消息 | 触发场景 |
|--------|------|---------|
| `RuntimeException` | "上传印章登记表失败，" + e.getMessage() | 印章登记表上传失败 |

---

## 5. HTTP 状态码（外部接口）

### 5.1 D365 接口响应码

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-ext-d365\src\main\java\com\dp\plat\pms\extend\d365\model\Response.java`

| 状态码 | 含义 | 判断方式 |
|--------|------|---------|
| `200` | 成功 | `SUCCESS_CODE.equals(this.code)` |

```java
private static final Integer SUCCESS_CODE = 200;
public boolean isSuccess() {
    return SUCCESS_CODE.equals(this.code);
}
```

### 5.2 FP 接口响应码

源码位置：`d:\常规软件\QoderCode\workspace\PMS\pms-ext-fp\src\main\java\com\dp\plat\pms\extend\fp\model\Response.java`

| 状态码 | 含义 | 判断方式 |
|--------|------|---------|
| `0` | 成功 | `Arrays.asList(SUCCESS_CODE).contains(this.code)` |
| `200` | 成功 | 同上 |

```java
private static final Integer[] SUCCESS_CODE = new Integer[] {0, 200};
public boolean isSuccess() {
    return Boolean.TRUE.equals(getIsSuccess()) || this.code != null && Arrays.asList(SUCCESS_CODE).contains(this.code);
}
```

### 5.3 FP 接口失败消息

源码位置：`d:\常规软件\QoderCode\workspace\PMS\pms-ext-fp\src\main\java\com\dp\plat\pms\extend\fp\util\FPApi.java`

| 消息 | 触发场景 |
|------|---------|
| "当前系统繁忙，请稍候再试！" | 线程池队列已满（RejectedExecutionException） |
| "请求超时" | 请求超时（TimeoutException） |
| "没有指定URL！" | 请求 URL 为空 |
| "响应内容不是Json格式！{body}" | 响应内容非 JSON |
| "反序列化发生异常！错误信息：{msg}" | JSON 反序列化异常 |
| "响应内容为空！" | 响应内容为空（重试后仍为空） |

---

## 6. MessageUtil 业务状态常量

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-struts\src\com\dp\plat\util\MessageUtil.java`

**说明**：以下常量为业务状态标识，非错误码。用于业务流程状态判断与角色权限控制。

### 6.1 通用结果码

| 常量 | 值 | 说明 |
|------|-----|------|
| `ERR_CODE` | 2 | 错误码 |
| `SUCC_CODE` | 1 | 成功码 |
| `SAVE_FAILED` | "保存失败" | 保存失败消息 |
| `SAVE_SUCCESS` | "保存成功" | 保存成功消息 |

### 6.2 项目状态常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `PROJECT_STATE_CREATING` | "10" | 工程管理部待创建项目 |
| `PROJECT_STATE_DENY` | "20" | 工程管理部不予跟踪 |
| `PROJECT_STATE_30` | "30" | 待指定服务经理 |
| `PROJECT_STATE_31` | "31" | 待指派项目经理 |
| `PROJECT_STATE_32` | "32" | 待制定工程计划（项目经理跟踪） |
| `PROJECT_STATE_33` | "33" | 售前项目回访阶段 |
| `PROJECT_STATE_CLOSEDLOOP` | "100" | 项目已关闭 |

### 6.3 项目计划状态常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `PROJECT_PLAN_STATE_40` | "40" | 尚未制定计划 |
| `PROJECT_PLAN_STATE_41` | "41" | 工程启动会 |
| `PROJECT_PLAN_STATE_42` | "42" | 工程准备 |
| `PROJECT_PLAN_STATE_43` | "43" | 到货验收 |
| `PROJECT_PLAN_STATE_44` | "44" | 安装调试 |
| `PROJECT_PLAN_STATE_45` | "45" | 初验 |
| `PROJECT_PLAN_STATE_46` | "46" | 终验 |
| `PROJECT_PLAN_STATE_47` | "47" | 闭环申请 |
| `PROJECT_PLAN_STATE_48` | "48" | 项目闭环 |

### 6.4 项目闭环状态常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `PROJECT_CLOSE_PROCESS_STATE_10` | "10" | 项目跟踪 |
| `PROJECT_CLOSE_PROCESS_STATE_15` | "15" | 闭环申请 |
| `PROJECT_CLOSE_PROCESS_STATE_20` | "20" | 服务经理审批 |
| `PROJECT_CLOSE_PROCESS_STATE_30` | "30" | 回访 |
| `PROJECT_CLOSE_PROCESS_STATE_40` | "40" | 工程人员审核 |
| `PROJECT_CLOSE_PROCESS_STATE_50` | "50" | 项目闭环 |

### 6.5 项目实施状态常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `PROJECT_EXECUTION_STATE_80` | "80" | 项目闭环 |
| `PROJECT_CREATE_STATE30` | "30" | 工程管理部创建项目，待指定服务经理 |
| `PROJECT_CREATE_STATE32` | "32" | 服务经理指定项目经理 |
| `PROJECT_CREATE_STATE34` | "34" | 项目经理填写项目信息 |
| `PROJECT_CREATE_STATE36` | "36" | 需工程管理部同意回退 |
| `PROJECT_CREATE_STATE38` | "38" | 需服务经理同意回退 |
| `PROJECT_CREATE_STATE40` | "40" | 工程管理部不予跟踪处理 |
| `PROJECT_CREATE_STATE42` | "42" | 项目经理选择不予跟踪 |
| `PROJECT_CREATE_STATE50` | "50" | 服务经理将不与跟踪的项目返回工程管理部 |

### 6.6 角色常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `ROLE_ADMIN` | 1 | 管理员 |
| `ROLE_COMMON` | 3 | 普通用户 |
| `ROLE_PROJECT_ADMIN` | 5 | 项目管理员 |
| `ROLE_PROJECT_VIEWER` | 6 | 项目查阅人员 |
| `ROLE_AREA_LEADER` | 9 | 办事处主任 |
| `ROLE_ENGINEEMANAGER_LEADER` | 10 | 工程管理部主管 |
| `ROLE_SERVICEMANAGER` | 11 | 服务经理 |
| `ROLE_PROGRAMMANAGER` | 12 | 项目经理 |
| `ROLE_ENGINEEMANAGER` | 13 | 工程管理部 |
| `ROLE_CALLBACKPER` | 14 | 回访人员 |
| `ROLE_SALESPEOPLE` | 15 | 销售代表 |
| `ROLE_FINANCIAL_STAFF` | 16 | 财务人员 |
| `ROLE_PRESALES_STAFF` | 17 | 售前专员 |
| `ROLE_PROB_ADMIN` | 18 | 技术公告管理员 |
| `ROLE_PROB_SUPPORTER` | 19 | 技术支持人员 |
| `ROLE_PROB_RD` | 20 | 研发人员 |
| `ROLE_WARRANTY_CALLBACKER` | 21 | 维保回访人员 |
| `ROLE_COMPONENT_ADMIN` | 22 | 产品组件管理人员 |

### 6.7 项目类型常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `PROJECT_TYPE_AFTERSALES` | "10" | 售后项目类型 |
| `PROJECT_TYPE_PRESALES` | "20" | 售前测试项目类型 |
| `PROJECT_TYPE_NORMAL` | "10" | 普通项目类型 |
| `PROJECT_TYPE_ENGINEE` | "20" | 工程项目类型 |

### 6.8 项目实施方式常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `IMPL_WAY_ALL` | -1 | 所有实施方式 |
| `IMPL_WAY_0` | 0 | 原厂直服 |
| `IMPL_WAY_1` | 1 | 原厂督导 |
| `IMPL_WAY_3` | 3 | 代理商自服 |

### 6.9 通知模板编码常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `NOTIFICATION_CODE_WEEKLY_SUBMIT` | "01" | 周报提交邮件模板 |
| `NOTIFICATION_CODE_WEEKLY_PISHI` | "02" | 周报批复邮件模板 |
| `NOTIFICATION_CODE_INSTRUCTION` | "03" | 项目留言邮件模板 |
| `NOTIFICATION_CODE_DENY_PRJ` | "04" | 项目不予跟踪邮件模板 |
| `NOTIFICATION_CODE_CONTINUE_PRJ` | "05" | 项目继续跟踪邮件模板 |
| `NOTIFICATION_CODE_SURE_PRJ` | "06" | 项目确认继续跟踪 |
| `NOTIFICATION_CODE_DENY_PRJ_42` | "07" | 项目经理选择不予跟踪 |
| `NOTIFICATION_CODE_DENY_PRJ_SURE` | "08" | 工程管理确认不予跟踪 |
| `NOTIFICATION_CODE_CREATEPRJ_NORMAL` | "09" | 项目立项通知-普通类 |
| `NOTIFICATION_CODE_CREATEPRJ_ENGINEE` | "10" | 项目立项通知-工程类 |
| `NOTIFICATION_CODE_PMNOMINATE_NORMAL` | "11" | 项目经理任命通知-普通类 |
| `NOTIFICATION_CODE_PMNOMINATE_ENGINEE` | "12" | 项目经理任命通知-工程类 |
| `NOTIFICATION_CODE_PROJECT_VALIDATE` | "13" | 项目组成立 |
| `NOTIFICATION_CODE_PROJECT_BACK` | "14" | 回退邮件模版 |
| `NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP` | "29" | 项目计划到期提醒 |
| `NOTIFICATION_CODE_PROJECT_UPLOAD_DELIVER` | "30" | 项目计划上传交付件提醒 |
| `NOTIFICATION_CODE_PROB` | "50" | 技术公告邮件模板 |
| `NOTIFICATION_CODE_101`~`NOTIFICATION_CODE_120` | "101"~"120" | 项目通知模板（创建/不予跟踪/指定服务经理/指定项目经理/需要跟踪/确认跟踪/回退/同意回退/上传交付件/制定工程计划/增加项目干系人/增加设备安装地址/修改工程计划/修改项目干系人/删除工程交付件/提交工程周报/项目闭环/项目设备转移） |

### 6.10 项目相关人角色常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `MEMBER_SM` | "20" | 服务经理 |
| `MEMBER_SALESMAN` | "10" | 销售人员 |
| `MEMBER_PM` | "30" | 项目经理 |
| `MEMBER_PARTY` | "40" | 团队成员 |
| `MEMBER_SERVICE_CHANNEL` | "50" | 出货代理商/服务渠道工程师 |
| `MEMBER_CUSTOMER` | "60" | 最终客户 |
| `MEMBER_TECH_MANMER` | "70" | 技术经理 |

### 6.11 基础数据类型常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `BASIC_DATA_MEMBER_ROLE` | "03" | 项目成员角色 |
| `BASIC_DATA_NAV_TAB` | "10" | 项目维护界面选项卡 |
| `BASIC_DATA_NAV_WORK_TAB` | "12" | 工作台页面选项卡 |
| `BASIC_DATA_NAV_MERGE_TAB` | "16" | 拆分页面选项卡 |
| `BASIC_DATA_NAV_DATA_TAB` | "18" | 数据统计界面选项卡 |
| `BASIC_DATA_PRJ_PHASE` | "09" | 项目阶段划分 |
| `BASIC_DATA_SERVICE_TYPE` | "15" | 项目实施方式划分 |
| `BASIC_DATA_PROTYPE` | "06" | 项目类别 |
| `BASIC_DATA_PRORANK` | "05" | 项目类型 |
| `BASIC_DATA_DELIVERSTATE` | "20" | 订单发货状态 |
| `BASIC_DATA_ENGINEERSTATE` | "22" | 项目工程状态 |
| `BASIC_DATA_PORJECT_TIME` | "24" | 项目查询条件-时间点集合 |
| `BASIC_DATA_PROJECT_TYPE` | "29" | 系统项目分类 |

### 6.12 周报常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `WEEKLY_STATE_RAFT` | 0 | 草稿 |
| `WEEKLY_STATE_SUBMIT` | 1 | 已提交 |
| `WEEKLY_STATE_ALL` | -1 | 全部 |
| `OPTION_TYPE_WORK` | 1 | 周报内容类型-work |
| `OPTION_TYPE_RISK` | 2 | 周报内容类型-risk |
| `OPTION_TYPE_HELP` | 3 | 周报内容类型-help |
| `OPTION_TYPE_PROPGRESS` | 4 | 周报内容类型-progress |
| `OPTION_TYPE_PLAN` | 5 | 周报内容类型-plan |
| `OPTION_TYPE_FILE` | 6 | 周报内容类型-file |
| `OPTION_TYPE_MAIL` | 7 | 周报内容类型-mail |

---

## 7. SPMS 角色与状态常量

### 7.1 SPMS 角色常量

源码位置：SPMS 各模块文档（从源码提取）

| 角色值 | 说明 |
|--------|------|
| `role=3` | 技服（只能查询自己办事处的数据） |
| `role=4` | 管理员 |
| `RMA_ROLE=6` | RMA 角色 |
| `QA_ROLE=7` | QA 角色 |

### 7.2 SPMS 库房编码规则

| 编码前缀 | 说明 |
|---------|------|
| `111` 开头 | 中央库 |
| `111111` | 主库 |
| `111112` | 好件库 |

### 7.3 SPMS 备件状态常量

| 状态字段 | 值 | 说明 |
|---------|-----|------|
| `isBack` | "0" | 未返回 |
| `isBack` | "1" | 已返回 |
| `isHexiao` | "0" | 未核销 |
| `isHexiao` | "1" | 已核销 |
| `takePlace` | "2" | 已转移 |
| `isyb` | 1 | 已续保 |

### 7.4 SPMS MaterialResult 类型

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\src\com\dp\plat\plus\spms2mes\service\SPMS2MESServiceImpl.java`

| 类型值 | 说明 |
|--------|------|
| `"E"` | 错误（Error） |
| `"S"` | 成功（Success） |

---

## 8. Struts2 全局结果码

源码位置：`d:\常规软件\QoderCode\workspace\PMS\PMS-struts\config\struts.xml`

| 结果名 | 跳转页面 | 说明 |
|--------|---------|------|
| `redirect1` | 重定向 | 通用重定向 |
| `globalLogin` | `/index.jsp` | 未登录，重定向到登录页 |
| `globalAdminLogin` | 管理员登录页 | 管理员未登录 |
| `errorRole` | `/error403.jsp` | 无权限，重定向到 403 页面 |
| `error` | `/error.jsp` | 系统异常，重定向到错误页 |

源码位置：`d:\常规软件\QoderCode\workspace\SPMS\build\classes\struts.xml`

SPMS 全局结果与 PMS 类似，含登录、错误页面重定向。

---

## 9. 错误处理最佳实践

### 9.1 PMS-springmvc 接口错误返回

```java
// 成功返回
return Result.success(data);

// 失败返回
return Result.fail("未找到对应的转包结算记录！");
```

### 9.2 PMS-activiti 工作流错误返回

```java
// 带状态码的失败返回
return new Result(false, "不可撤回", "该任务已经被签收或者办理，无法撤回，请查看流程明细");
```

### 9.3 业务异常抛出

```java
// 认证异常
throw new UnknownAccountException("用户名或密码错误！");

// 业务异常
throw new SubcontractException("项目转包申请流程正在进行，本次不再重复发起！");

// 文件上传异常
throw new UploadException("不允许上传类型为【." + suffix + "】的文件！");
```

### 9.4 外部接口调用异常

```java
// D365 接口调用失败
throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));

// FP 接口失败返回（不抛异常）
return Response.failure("当前系统繁忙，请稍候再试！", responseType);
```

### 9.5 SPMS MaterialResult 返回

```java
MaterialResult result = new MaterialResult();
result.setType("E");  // 错误类型
result.setMessage("接口未启用，请联系管理员！");
return result;
```

---

## 10. 错误码使用建议

### 10.1 现状分析

- 系统未定义结构化错误码常量类（如 `ErrorCode.P1001`）
- 错误信息以中文消息字符串形式传递，无统一编码
- `Result.code` 字段可选，多数场景未填充
- 业务状态使用 `MessageUtil` 中的字符串常量标识

### 10.2 改进建议

1. **建立错误码枚举**：建议新增 `ErrorCode` 枚举类，按模块定义错误码常量
2. **统一错误码格式**：建议采用 `{模块前缀}{4位数字}` 格式，如 `PROJ1001`、`WORK2001`
3. **填充 Result.code**：所有 `Result.fail()` 调用应填充错误码
4. **异常消息国际化**：建议将中文异常消息抽取为 properties 文件，支持国际化
5. **错误码文档维护**：新增错误码时同步更新本文档

---

## 11. 相关文档索引

| 文档 | 路径 |
|------|------|
| 接口总目录 | [interface-catalog.md](interface-catalog.md) |
| 接口文档模板 | [interface-template.md](interface-template.md) |
| PMS-struts 错误码参考（旧版，含建议码） | [../../PMS-struts/docs/06-reference/error-codes.md](../../PMS-struts/docs/06-reference/error-codes.md) |
| 术语表 | [glossary.md](glossary.md) |
