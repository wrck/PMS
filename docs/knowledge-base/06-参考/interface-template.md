# 接口文档模板

> 本模板用于规范 PMS / SPMS 项目的接口文档编写。使用时请将 `<...>` 占位符替换为实际内容，并删除不需要的示例说明行。

---

## 1. 接口名称

<接口的中文名称，如：备件申请单创建>

- **接口标识**：`<interface-key，如 spareApply.create>`
- **所属模块**：`<模块名，如 PMS-struts / PMS-springmvc / SPMS-spare>`

## 2. 接口描述

<用 1-3 句话说明该接口的业务用途、调用场景与整体行为。例如：申请人提交备件申请单，系统校验库存与权限后生成申请记录并触发审批流程。>

## 3. 请求 URL

- **完整路径**：`<http(s)://<host>:<port>/<contextPath>/<namespace>/<action>.action>`
- **命名空间（namespace）**：`<Struts2/Spring MVC 命名空间，如 /spare/apply>`
- **Action 名称**：`<如 create>`
- **说明**：`<命名空间与 Action 的来源配置文件，如 struts-spare.xml>`

## 4. 请求方法

- [ ] GET
- [ ] POST
- [ ] PUT
- [ ] DELETE

> 默认推荐 POST（涉及数据写入）。如为查询类接口可使用 GET。

## 5. 请求参数

### 5.1 请求头

| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| Content-Type | string | 是 | - | `application/x-www-form-urlencoded` 或 `application/json` |
| Authorization | string | 是 | - | Shiro/CAS 认证后的 Token 或 Cookie |
| `<其他头>` | `<类型>` | `<是否必填>` | `<默认值>` | `<说明>` |

### 5.2 请求参数表

| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| `<paramName>` | `<String/Integer/Date/...>` | `<是/否>` | `<默认值>` | `<业务含义、取值范围、格式（如 yyyy-MM-dd）>` |
| `<paramName2>` | `<类型>` | `<是/否>` | `<默认值>` | `<说明>` |

### 5.3 请求示例

```json
{
  "<paramName>": "<value>",
  "<paramName2>": "<value>"
}
```

## 6. 响应格式

### 6.1 响应 JSON 示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "<field>": "<value>"
  }
}
```

### 6.2 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200 表示成功 |
| message | String | 状态描述信息 |
| data | Object | 业务数据载体 |
| data.`<field>` | `<类型>` | `<业务含义>` |

## 7. 权限要求

- **所需角色**：`<如：申请人 / 仓库管理员 / 管理员>`
- **所需权限（Shiro 权限串）**：`<如 spare:apply:create>`
- **认证方式**：`<Shiro + CAS 单点登录 / Session>`
- **是否需要数据权限过滤**：`<是/否，如按部门/团队过滤>`

## 8. 错误码

| 错误码 | 含义 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 参数校验失败 | 检查必填项与参数格式 |
| 401 | 未登录或会话过期 | 重新登录获取凭证 |
| 403 | 无权限 | 联系管理员分配对应角色/权限 |
| 500 | 服务端异常 | 查看服务端日志定位 |
| `<业务错误码>` | `<含义>` | `<处理建议>` |

## 9. 调用示例

### 9.1 cURL

```bash
curl -X POST "http://<host>:<port>/<contextPath>/<namespace>/<action>.action" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Cookie: JSESSIONID=<sessionId>" \
  -d "<paramName>=<value>&<paramName2>=<value>"
```

### 9.2 JavaScript（前端 AJAX）

```javascript
$.ajax({
  url: '<contextPath>/<namespace>/<action>.action',
  type: 'POST',
  data: { <paramName>: '<value>', <paramName2>: '<value>' },
  success: function (res) {
    if (res.code === 200) {
      // 处理成功
    }
  }
});
```

## 10. 备注

<补充说明：如事务边界、并发处理、关联接口、变更历史、已知问题等。>

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | `<yyyy-MM-dd>` | `<作者>` | 初始版本 |
