# PMS 部署手册

> Task 23 配套文档：蓝绿部署、回滚、健康检查、CI/CD 流水线说明。

## 目录

1. [部署架构概览](#1-部署架构概览)
2. [蓝绿部署原理](#2-蓝绿部署原理)
3. [脚本用法](#3-脚本用法)
4. [CI/CD 流水线](#4-cicd-流水线)
5. [配置参数说明](#5-配置参数说明)
6. [故障恢复流程](#6-故障恢复流程)
7. [Nginx 接入说明](#7-nginx-接入说明)
8. [首次部署 Checklist](#8-首次部署-checklist)

---

## 1. 部署架构概览

```
                    ┌────────────────────────────────────────┐
                    │              GitHub Actions            │
                    │  push main/tag → build → push GHCR     │
                    └────────────────────┬───────────────────┘
                                         │ SSH (key auth)
                                         ▼
┌────────────────────────────────────────────────────────────────────┐
│                       部署服务器                                     │
│                                                                    │
│   ┌──────────────┐    ┌─────────────────────────────────────────┐ │
│   │   Nginx      │───▶│ upstream pms_backend {                  │ │
│   │   :80 / :443 │    │   server 127.0.0.1:8081; # blue         │ │
│   └──────────────┘    │   server 127.0.0.1:8082; # green        │ │
│                       │ }                                        │ │
│                       │ /etc/nginx/conf.d/pms-upstream.conf      │ │
│                       └─────────────────────────────────────────┘ │
│                                                                    │
│   ┌───────────────────┐   ┌───────────────────┐                  │
│   │ pms-backend-blue  │   │ pms-backend-green │                  │
│   │ :8081 → 8080      │   │ :8082 → 8080      │                  │
│   │ (active 或 stop)  │   │ (active 或 stop)  │                  │
│   └────────┬──────────┘   └────────┬──────────┘                  │
│            │                       │                              │
│            └───────────┬───────────┘                              │
│                        ▼                                          │
│   ┌────────────────────────────────────┐  ┌────────────────────┐ │
│   │ pms-mysql  (:3306)                 │  │ pms-redis (:6379)  │ │
│   │ docker-compose.infra.yml 管理       │  │                    │ │
│   └────────────────────────────────────┘  └────────────────────┘ │
│                                                                    │
│   状态文件: /var/lib/pms/active-env                                │
│   (active=blue|green, previous=..., active_image=...)              │
└────────────────────────────────────────────────────────────────────┘
```

### 组件说明

| 组件 | 管理方式 | 说明 |
|------|----------|------|
| MySQL / Redis | `docker-compose.infra.yml` | 基础设施，独立启动，长期运行 |
| backend (blue/green) | `deploy.sh` 通过 `docker run` | 蓝绿双实例，仅一个活跃 |
| frontend (nginx) | `docker-compose.app.yml` 或宿主 nginx | 反向代理到活跃 backend |
| 可观测性栈 | `docker-compose.observe.yml` | Prometheus / Grafana / Jaeger |

---

## 2. 蓝绿部署原理

### 核心思想

维护两套完全相同的应用环境（blue / green），任意时刻只有一套对外提供服务。新版本部署到非活跃环境，验证通过后再切换流量，实现**零停机**发布。

### 部署流程（`deploy.sh`）

```
┌──────────────┐
│ 1. 预检查    │  docker / nginx / 镜像 / 网络可用性
└──────┬───────┘
       ▼
┌──────────────┐
│ 2. 备份状态  │  cp active-env active-env.bak.<timestamp>
└──────┬───────┘
       ▼
┌──────────────┐
│ 3. 确定目标  │  active=blue → new=green；active=green → new=blue
└──────┬───────┘
       ▼
┌──────────────┐
│ 4. 启动新环境│  docker run pms-backend-<new> :<new_port>
│   (不切流量) │
└──────┬───────┘
       ▼
┌──────────────┐
│ 5. 健康检查  │  curl :<new_port>/actuator/health (最长 5 分钟)
│   失败 → 回滚│  失败则清理新容器 + 触发 rollback.sh
└──────┬───────┘
       ▼
┌──────────────┐
│ 6. 切换流量  │  写 pms-upstream.conf → nginx -s reload
└──────┬───────┘
       ▼
┌──────────────┐
│ 7. 综合健康  │  ./scripts/health-check.sh
│   检查       │
└──────┬───────┘
       ▼
┌──────────────┐
│ 8. 停止旧环境│  docker stop pms-backend-<old>（保留容器，用于回滚）
└──────┬───────┘
       ▼
┌──────────────┐
│ 9. 更新状态  │  active=<new> previous=<old> active_image=<tag>
└──────────────┘
```

### 蓝绿环境映射

| 环境 | 容器名 | 宿主端口 | 内部端口 |
|------|--------|----------|----------|
| blue | `pms-backend-blue` | 8081 | 8080 |
| green | `pms-backend-green` | 8082 | 8080 |

### 状态文件示例

`/var/lib/pms/active-env`:

```
# PMS 蓝绿部署状态文件（由 deploy.sh 维护，请勿手动编辑）
updated=2026-07-06T10:30:00Z
active=green
previous=blue
active_image=ghcr.io/org/network-equipment-pms/pms-backend:v1.2.3
previous_image=ghcr.io/org/network-equipment-pms/pms-backend:v1.2.2
```

---

## 3. 脚本用法

### 3.1 `deploy.sh` — 蓝绿部署

```bash
# 部署 latest 镜像
./scripts/deploy.sh

# 部署指定 tag
IMAGE_TAG=v1.2.3 ./scripts/deploy.sh

# 部署失败不自动回滚（用于调试）
ROLLBACK_ON_FAILURE=false ./scripts/deploy.sh

# 指定镜像仓库（私有仓库）
IMAGE_REPO=registry.internal.com/pms-backend IMAGE_TAG=v1.2.3 ./scripts/deploy.sh
```

**关键行为：**
- 首次部署：active=none → 部署到 blue
- 健康检查超时 5 分钟（`HEALTH_CHECK_TIMEOUT=300`）
- 旧容器**停止但保留**（`docker stop`，不 `docker rm`），便于秒级回滚
- 失败时若 `ROLLBACK_ON_FAILURE=true`，自动调用 `rollback.sh --no-stop-current`

### 3.2 `rollback.sh` — 回滚

```bash
# 蓝绿互换（秒级回滚，零额外构建）
./scripts/rollback.sh

# 回滚到指定镜像 tag
./scripts/rollback.sh v1.0.5

# 回滚后保留当前容器（调试用，不停止）
./scripts/rollback.sh --keep-current

# 仅切换流量，不停止当前（deploy.sh 失败时自动调用）
./scripts/rollback.sh --no-stop-current

# 演练模式（仅打印动作，不实际执行）
./scripts/rollback.sh --dry-run
```

**回滚策略：**
1. **默认（无参数）**：蓝绿互换，启动已停止的旧容器，切换流量。秒级生效。
2. **指定 tag**：拉取指定镜像，以 inactive 环境重新创建容器，切换流量。用于旧容器已被删除的场景。
3. **回滚失败处理**：若回滚目标健康检查失败，**当前活跃环境不受影响**，仍在线服务，需人工介入。

### 3.3 `health-check.sh` — 综合健康检查

```bash
# 默认检查 localhost:8080
./scripts/health-check.sh

# 检查指定端口（如检查 blue 环境）
HEALTH_CHECK_URL=http://localhost:8081/actuator/health \
API_BASE_URL=http://localhost:8081 \
./scripts/health-check.sh

# 自定义 MySQL/Redis 连接
MYSQL_HOST=10.0.0.1 MYSQL_PASSWORD=xxx \
REDIS_HOST=10.0.0.1 REDIS_PASSWORD=yyy \
./scripts/health-check.sh
```

**检查项：**

| # | 检查项 | 失败影响 |
|---|--------|----------|
| 1 | Actuator `/actuator/health` | 服务不可用 |
| 2 | 登录验证码接口 `/api/auth/captcha` | 业务接口不可用 |
| 3 | 项目列表接口（HTTP 401/403 视为在线） | 业务接口不可用 |
| 4 | MySQL 连接 + 关键表行数 | 数据库不可用 |
| 5 | Redis PING | 缓存不可用 |
| 6 | 磁盘空间（阈值 90%） | 可能导致服务崩溃 |
| 7 | 内存使用（阈值 90%） | 可能 OOM |

退出码：`0` 全部通过，`1` 至少一项失败。

---

## 4. CI/CD 流水线

### 4.1 触发条件

| 事件 | 目标环境 | 镜像 tag |
|------|----------|----------|
| push 到 `main` | staging | commit sha 前 7 位 |
| push 到 `release/*` | staging | commit sha 前 7 位 |
| push tag `v*` | production | tag 名（如 `v1.2.3`） |
| 手动触发 | 可选 staging/production | 可手动指定 |

### 4.2 流水线阶段

```
┌─────────┐     ┌──────────────────┐     ┌─────────────────────┐     ┌────────┐
│  build  │────▶│ deploy-staging   │     │ deploy-production   │     │ notify │
│         │     │ (main/release)   │     │ (v* tag)            │     │        │
└─────────┘     └──────────────────┘     └─────────────────────┘     └────────┘
   │                  │                          │                       │
   │                  │                          │                       │
   ▼                  ▼                          ▼                       ▼
构建后端 JAR      SSH 到 staging            SSH 到 production        汇总结果
构建前端 dist     git reset --hard          git reset --hard         写入 Summary
推送 GHCR 镜像    deploy.sh                 deploy.sh                失败告警
                  health-check.sh           health-check.sh
```

### 4.3 必需 Secrets

在 GitHub 仓库 **Settings → Secrets and variables → Actions** 中配置：

| Secret | 说明 | 示例 |
|--------|------|------|
| `DEPLOY_HOST` | 部署服务器 IP/域名 | `1.2.3.4` |
| `DEPLOY_USER` | SSH 用户 | `deploy` |
| `DEPLOY_SSH_KEY` | SSH 私钥（ed25519） | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `DEPLOY_PORT` | SSH 端口（可选，默认 22） | `22` |
| `DEPLOY_PATH` | 服务器项目根目录 | `/opt/pms` |
| `MYSQL_PASSWORD` | 生产 MySQL 密码 | `********` |
| `REDIS_PASSWORD` | 生产 Redis 密码 | `********` |
| `APP_ENCRYPT_KEY` | 字段加密密钥（32 字节 Base64） | `base64encoded...` |
| `DEPLOY_NOTIFY_WEBHOOK` | 通知 webhook（可选） | `https://hooks.slack.com/...` |

### 4.4 Environment 隔离（推荐）

在 **Settings → Environments** 中创建 `staging` 和 `production`：

- `staging`：可自由部署，用于测试
- `production`：
  - 开启 **Required reviewers**（需人工审批）
  - 限制 **Deployment branches** 为 `v*` tag
  - 配置独立的 `DEPLOY_HOST` / `DEPLOY_USER` / `DEPLOY_SSH_KEY`

---

## 5. 配置参数说明

所有参数均可通过环境变量覆盖，以下为默认值：

### 5.1 镜像与蓝绿

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `REGISTRY` | `ghcr.io` | 镜像仓库 |
| `IMAGE_REPO` | `${REGISTRY}/network-equipment-pms/pms-backend` | 镜像仓库地址 |
| `IMAGE_TAG` | `latest` | 镜像 tag |
| `BACKEND_IMAGE` | `${IMAGE_REPO}:${IMAGE_TAG}` | 完整镜像地址（覆盖 IMAGE_REPO/IMAGE_TAG） |
| `BLUE_CONTAINER` | `pms-backend-blue` | blue 容器名 |
| `GREEN_CONTAINER` | `pms-backend-green` | green 容器名 |
| `BLUE_PORT` | `8081` | blue 宿主端口 |
| `GREEN_PORT` | `8082` | green 宿主端口 |

### 5.2 状态与 Nginx

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `STATE_DIR` | `/var/lib/pms` | 状态文件目录 |
| `STATE_FILE` | `${STATE_DIR}/active-env` | 状态文件路径 |
| `NGINX_UPSTREAM_CONF` | `/etc/nginx/conf.d/pms-upstream.conf` | nginx upstream 配置路径 |
| `NGINX_CONTAINER` | （空） | 容器化 nginx 名（如 `pms-frontend`），留空则用宿主 nginx |
| `NGINX_UPSTREAM_NAME` | `pms_backend` | upstream 块名称 |

### 5.3 健康检查

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `HEALTH_CHECK_URL` | `http://localhost:8080/actuator/health` | actuator 端点 |
| `HEALTH_CHECK_TIMEOUT` | `300` | 健康检查超时（秒） |
| `HEALTH_CHECK_INTERVAL` | `5` | 重试间隔（秒） |
| `TRAFFIC_SWITCH_WAIT` | `30` | 流量切换后等待时间（秒） |
| `ROLLBACK_ON_FAILURE` | `true` | 部署失败是否自动回滚 |

### 5.4 应用配置（传给容器）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring profile |
| `MYSQL_HOST` | `mysql` | MySQL 主机（Docker 网络内 DNS） |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `MYSQL_DATABASE` | `pms` | 数据库名 |
| `MYSQL_USER` | `pms` | 数据库用户 |
| `MYSQL_PASSWORD` | `pmspassword` | 数据库密码（**生产必须覆盖**） |
| `REDIS_HOST` | `redis` | Redis 主机 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | `redispassword` | Redis 密码（**生产必须覆盖**） |
| `APP_ENCRYPT_KEY` | （空） | 字段加密密钥（**生产必须配置**） |
| `OTEL_ENDPOINT` | `http://jaeger:4317` | OpenTelemetry OTLP 端点 |
| `JAVA_OPTS` | `-Xms512m -Xmx2g ...` | JVM 参数 |
| `DOCKER_NETWORK` | （自动检测） | Docker 网络名 |
| `NOTIFY_WEBHOOK` | （空） | 通知 webhook URL |

### 5.5 health-check.sh 专属

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `API_BASE_URL` | `http://localhost:8080` | 业务接口基址 |
| `DISK_THRESHOLD` | `90` | 磁盘使用率告警阈值（%） |
| `MEM_THRESHOLD` | `90` | 内存使用率告警阈值（%） |

---

## 6. 故障恢复流程

### 6.1 部署失败（自动回滚）

`deploy.sh` 在新环境健康检查失败时，若 `ROLLBACK_ON_FAILURE=true`：

1. 清理失败的新容器
2. 自动调用 `rollback.sh --no-stop-current`
3. 流量切回原活跃环境
4. 退出码 1，触发 CI 告警

### 6.2 手动回滚

```bash
# 场景：上线后发现业务异常，需立即回滚
ssh deploy@production-server
cd /opt/pms
./scripts/rollback.sh

# 验证回滚结果
./scripts/health-check.sh
```

### 6.3 回滚到历史版本

```bash
# 场景：蓝绿互换已无法解决（两个环境都有问题），需回到特定历史版本
./scripts/rollback.sh v1.0.5

# 若本地无该镜像，脚本会自动 docker pull
```

### 6.4 回滚也失败（严重事故）

若回滚目标健康检查也失败：

1. **当前活跃环境仍在线**（rollback.sh 不会停止当前环境）
2. 立即人工介入：
   ```bash
   # 查看两个环境的状态
   docker ps -a | grep pms-backend
   docker logs --tail 100 pms-backend-blue
   docker logs --tail 100 pms-backend-green

   # 查看状态文件
   cat /var/lib/pms/active-env

   # 手动启动一个已知可用的镜像
   docker run -d --name pms-backend-emergency \
     --network <network> -p 8080:8080 \
     ghcr.io/org/network-equipment-pms/pms-backend:v1.0.0

   # 手动切换 nginx upstream
   echo 'upstream pms_backend { server 127.0.0.1:8080; }' | sudo tee /etc/nginx/conf.d/pms-upstream.conf
   sudo nginx -s reload
   ```
3. 通知相关部门，启动应急预案

### 6.5 状态文件损坏恢复

`/var/lib/pms/active-env` 损坏或丢失时：

```bash
# 通过容器运行状态推断
docker ps --format '{{.Names}} {{.Status}}' | grep pms-backend

# 手动重建状态文件
sudo mkdir -p /var/lib/pms
sudo tee /var/lib/pms/active-env <<EOF
active=blue
previous=green
active_image=ghcr.io/org/network-equipment-pms/pms-backend:v1.2.3
previous_image=ghcr.io/org/network-equipment-pms/pms-backend:v1.2.2
EOF
```

### 6.6 备份恢复

数据库与 Redis 备份恢复见 `scripts/backup.sh` / `scripts/restore.sh`（Task 22）。

---

## 7. Nginx 接入说明

### 7.1 现有 nginx.conf 适配

项目根目录的 `nginx.conf`（前端容器使用）当前直接 `proxy_pass http://backend:8080`。要支持蓝绿部署，需让 nginx 通过 `upstream` 块引用可切换的后端。

**方案 A（推荐）：宿主 nginx 做边缘代理**

在部署服务器宿主 nginx 中引入 `pms-upstream.conf`：

```nginx
# /etc/nginx/conf.d/pms.conf
include /etc/nginx/conf.d/pms-upstream.conf;

server {
    listen 80;
    server_name pms.example.com;

    location /api/ {
        proxy_pass http://pms_backend/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location / {
        proxy_pass http://127.0.0.1:80;  # 前端容器
        proxy_set_header Host $host;
    }
}
```

`pms-upstream.conf` 由 `deploy.sh` 自动生成：

```nginx
upstream pms_backend {
    server 127.0.0.1:8081 max_fails=3 fail_timeout=30s;  # 当前指向 blue
    keepalive 32;
}
```

**方案 B：容器化 nginx（前端容器）**

设置 `NGINX_CONTAINER=pms-frontend`，`NGINX_UPSTREAM_CONF=/etc/nginx/conf.d/pms-upstream.conf`。`deploy.sh` 会通过 `docker exec` 写入配置并 reload。需确保前端容器挂载了 `pms-upstream.conf` 并在 `nginx.conf` 中 `include` 它。

### 7.2 验证流量切换

```bash
# 部署前
curl -s http://localhost:8081/actuator/health  # blue
curl -s http://localhost:8082/actuator/health  # green（未启动）

# 部署后
cat /etc/nginx/conf.d/pms-upstream.conf  # 应指向 green:8082
curl -s http://localhost/actuator/health  # 经 nginx，应返回 green
```

---

## 8. 首次部署 Checklist

### 8.1 服务器准备

- [ ] 安装 Docker + docker-compose
- [ ] 安装 nginx（宿主）或确认前端容器 nginx
- [ ] 安装 mysql / redis 客户端（用于 health-check.sh）
- [ ] 创建部署用户 `deploy`，配置 sudo 免密（docker / nginx 命令）
- [ ] 配置 SSH 公钥认证（GitHub Actions 用私钥）
- [ ] 创建 `/var/lib/pms` 目录并赋权

### 8.2 项目部署

- [ ] `git clone` 项目到 `/opt/pms`
- [ ] `cp .env.example .env` 并填入生产凭据
- [ ] `docker-compose -f docker-compose.infra.yml up -d` 启动 MySQL/Redis
- [ ] `docker-compose -f docker-compose.observe.yml up -d` 启动可观测性栈
- [ ] 验证 `./scripts/health-check.sh`（基础设施层）

### 8.3 首次应用部署

- [ ] 配置 GitHub Secrets（见 [4.3](#43-必需-secrets)）
- [ ] 推送代码到 `main` 分支触发流水线，或手动 `workflow_dispatch`
- [ ] 确认 `ghcr.io` 镜像构建成功
- [ ] 确认 SSH 部署成功，`deploy.sh` 输出 "部署完成"
- [ ] 验证 `cat /var/lib/pms/active-env` 显示 `active=blue`
- [ ] 验证 `./scripts/health-check.sh` 全部通过
- [ ] 验证 `curl http://localhost/actuator/health` 返回 `{"status":"UP"}`

### 8.4 第二次部署（验证蓝绿）

- [ ] 再次推送代码触发部署
- [ ] 确认部署到 green，blue 容器停止但保留
- [ ] 验证 `cat /var/lib/pms/active-env` 显示 `active=green previous=blue`
- [ ] 验证 `docker ps -a | grep pms-backend` 显示 blue 为 Exited、green 为 Up

### 8.5 回滚演练

- [ ] 执行 `./scripts/rollback.sh --dry-run` 确认逻辑正确
- [ ] 执行 `./scripts/rollback.sh` 实际回滚
- [ ] 验证 active 切回 blue，green 停止
- [ ] 验证 `./scripts/health-check.sh` 通过

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `scripts/deploy.sh` | 蓝绿部署脚本 |
| `scripts/rollback.sh` | 回滚脚本 |
| `scripts/health-check.sh` | 综合健康检查脚本 |
| `scripts/init-db.sh` | MySQL 初始化脚本 |
| `scripts/backup.sh` | 备份脚本（Task 22） |
| `scripts/restore.sh` | 恢复脚本（Task 22） |
| `.github/workflows/deploy.yml` | CI/CD 部署流水线 |
| `.github/workflows/ci.yml` | CI 构建/测试流水线 |
| `docker-compose.infra.yml` | 基础设施（MySQL/Redis） |
| `docker-compose.app.yml` | 应用层（backend/frontend） |
| `docker-compose.observe.yml` | 可观测性栈 |
| `Dockerfile.backend` | 后端镜像构建 |
| `Dockerfile.frontend` | 前端镜像构建 |
