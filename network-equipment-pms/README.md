# 网络设备工程项目管理系统

网络设备工程项目管理系统（network-equipment-pms），基于 Spring Boot 3.2.5 + Vue 3 + TypeScript 的多模块企业应用。

## CI/CD 流水线

### PR 触发流水线 (.github/workflows/ci.yml)
- **backend job**: Maven 编译 + 单元测试 + 上传 JAR 产物
- **frontend job**: npm ci + vue-tsc 类型检查 + npm run build + 上传 dist 产物
- **sonar job**: 依赖 backend+frontend 通过后触发 SonarQube 静态扫描
- **docker job**: 仅 main 分支触发，构建并推送镜像到 ghcr.io

### 部署流水线 (.github/workflows/deploy.yml)
- main 分支 push 触发（占位，按实际环境补充）

### 质量门禁规则
- 代码覆盖率 ≥ 70%
- 重复代码率 ≤ 3%
- 严重漏洞 = 0
- 圈复杂度 ≤ 15

### 本地开发
1. 启动依赖：`docker-compose up -d mysql redis`
2. 后端：`cd network-equipment-pms && mvn spring-boot:run -pl pms-admin`
3. 前端：`cd network-equipment-pms/pms-frontend && npm install && npm run dev`
4. Swagger 文档：http://localhost:8080/swagger-ui.html
5. SonarQube：`docker-compose up -d sonarqube`，访问 http://localhost:9000（默认 admin/admin）
