# ============================================================================
# mock-fp Dockerfile — FP Mock 服务（多阶段构建 + 非 root + HEALTHCHECK）
# Task 21.5 安全加固
# 构建上下文：./mock-fp（docker-compose.app.yml 中 build: ./mock-fp）
# ============================================================================

# ---------------------------- 阶段 1: Maven 编译 ----------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# 1. 先复制 pom 文件利用 Docker 层缓存
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# 2. 复制源码并编译打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------------------------- 阶段 2: 运行时 ----------------------------
FROM eclipse-temurin:17-jre-jammy

# 3. 安装 curl 用于健康检查
RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# 4. 创建非 root 用户 mock（UID/GID 1002），无登录 shell
RUN groupadd -r mock -g 1002 && \
    useradd -r -g mock -u 1002 -m -d /home/mock -s /sbin/nologin mock

WORKDIR /app

# 5. 复制构建产物
COPY --from=builder /build/target/*.jar /app/app.jar
RUN chown -R mock:mock /app

# 6. 切换非 root 用户
USER mock

# 7. 暴露端口（与 docker-compose.app.yml 端口映射 8091:8091 一致；
#    通过 SERVER_PORT 覆盖 application.yml 的 8092）
ENV SERVER_PORT=8091
EXPOSE 8091

# 8. 健康检查（mock 服务未引入 actuator，使用 /api 端点返回 {"status":"ok"}）
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8091/api || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
