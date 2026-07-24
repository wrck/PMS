# 测试环境配置

## 后端环境变量

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.9"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3307/dpspms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull"
$env:MYSQL_USER = "root"
$env:MYSQL_PASSWORD = "!Q@W3e4r"
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:JWT_SECRET = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEtMjU2LWJpdC11c2FnZQ=="
$env:APP_ENCRYPT_KEY = "MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY="
$env:SERVER_PORT = "9080"
```

## 服务启动顺序

1. MySQL (端口 3307)
2. Redis (端口 6379)
3. 后端 (端口 9080)
4. 前端 (端口 5000)

## 服务验证

| 服务 | 验证命令 |
|------|----------|
| 后端 | `GET http://localhost:9080/actuator/health` → `{"status":"UP"}` |
| 前端 | `GET http://localhost:5000/` → 页面正常加载 |

## API 请求头

### PMS 业务 API (前缀 `/api`)

```
Authorization: Bearer {token}
```

### yudao 公共 API (前缀 `/admin-api`)

```
Authorization: Bearer {token}
tenant-id: 1
```

## 测试注意事项

1. **后端代码变更后必须重新打包**: `mvn package -DskipTests` 生成新 JAR 后重启服务
2. **前端类型检查**: 使用 `npm run build`（非 `npm run dev`）验证 TypeScript 类型
3. **JDK 版本**: 后端编译必须使用 JDK 17
4. **乐观锁**: Project 实体使用 `@Version` 注解，更新操作需携带 version 字段
5. **状态机**: 交付件状态必须按 7 状态机顺序流转，不可跳跃
