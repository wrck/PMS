# 登录与认证模块 测试用例

> **模块**: 登录与认证  |  **用例数**: 6  |  **更新日期**: 2026-07-24

> **测试账号**: admin / admin123  
> **公共请求头**: `tenant-id: 1`（yudao 公共 API）

### TC-1.1 登录页面加载

| 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|-----|--------|------|------|
| /login | 访问 `http://localhost:5000/login` | - | - | 登录表单正确渲染，用户名/密码输入框、登录按钮可见 | 页面加载性能、表单元素可见性 |

### TC-1.2 登录成功

| 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|-----|--------|------|------|
| /login | 输入用户名 `admin`、密码 `admin123`，点击登录 | POST /admin-api/system/auth/login | `{"username":"admin","password":"admin123"}` | 登录成功，跳转至 `/dashboard`，localStorage 存储 token | token 存储、路由跳转、用户信息缓存 |

### TC-1.3 登录失败

| 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|-----|--------|------|------|
| /login | 输入错误密码（如 `wrong`），点击登录 | POST /admin-api/system/auth/login | `{"username":"admin","password":"wrong"}` | 显示错误提示信息，停留在登录页不跳转 | 错误提示文案、不跳转、表单内容保留 |

### TC-1.4 未登录访问保护页面

| 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|-----|--------|------|------|
| /project/list | 清除 localStorage token 后直接访问 | - | - | 自动跳转至 `/login?redirect=%2Fproject%2Flist` | 路由守卫、token 校验、redirect 重定向参数 |

### TC-1.5 获取用户权限信息

| 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|-----|--------|------|------|
| /dashboard | 登录后系统自动拉取权限信息 | GET /admin-api/system/auth/get-permission-info | - | 返回用户角色、权限列表、菜单树数据 | Header: `Authorization: Bearer {token}`、`tenant-id: 1`；权限数据完整性 |

### TC-1.6 退出登录

| 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|-----|--------|------|------|
| /dashboard | 点击右上角用户头像 → 退出登录 | POST /admin-api/system/auth/logout | - | 清除本地 token 与用户状态，跳转至登录页 | token 清除、用户状态重置、跳转登录页 |

---
<!-- 后续用例在此追加 -->
