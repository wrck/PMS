# PMS 容器化部署指南

本文档说明如何使用 Docker / Docker Compose 一键构建并部署 PMS-springmvc 应用及其依赖的 MySQL 数据库。

## 目录

- [环境要求](#环境要求)
- [Profile 说明](#profile-说明)
- [目录结构](#目录结构)
- [快速开始](#快速开始)
- [构建命令](#构建命令)
- [启动与停止](#启动与停止)
- [配置项](#配置项)
- [数据库初始化](#数据库初始化)
- [构建产物（WAR）说明](#构建产物war说明)
- [常见排错](#常见排错)

---

## 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Docker | 20.10+ | 用于构建镜像与运行容器 |
| Docker Compose | v2（`docker compose`）或 v1.27+（`docker-compose`） | 用于多服务编排 |
| 构建环境 | JDK 8 + Maven 3.6+ | **仅在容器外手动构建时需要**；容器内构建由 `maven:3.8-jdk-8` 镜像完成 |
| 宿主机内存 | 建议 ≥ 4GB | Maven 构建与 Tomcat 运行均较吃内存 |
| 磁盘空间 | ≥ 5GB | 镜像层 + Maven 本地仓库 + MySQL 数据卷 |

> 容器内构建会从公网 Maven 中央仓库下载依赖，首次构建需要联网且耗时较长（视网络情况 10~30 分钟）。后续构建命中 Docker 层缓存会显著加快。

---

## Profile 说明

PMS 是多模块 Maven 项目，构建时通过两组 profile 控制产物与配置：

### 1. 产品线 profile（决定 WAR 产物名）

| Profile | 模块 | 产物 WAR | 说明 |
|---------|------|----------|------|
| `pms2`（默认） | PMS-springmvc | `PMS2.war` | Spring MVC 版本，默认构建目标 |
| `pms3` | PMS-springmvc | `AFPMS3.war` | PMS3 版本 |
| `pms`（默认） | PMS-struts | `PMS.war` | 遗留 Struts2 版本（WAR 名 `PMS.war`） |
| `yfpms` | PMS-struts | `YFPMS.war` | YFPMS 版本 |

### 2. 环境 profile（决定 `config/profiles/<env>` 资源过滤）

| Profile | 说明 |
|---------|------|
| `dev`（默认） | 本地开发环境 |
| `test` | 测试环境 |
| `release` | 生产环境 |

> 资源过滤目录：
> - PMS-springmvc：`PMS-springmvc/src/main/resources/profiles/<env>/`
> - PMS-struts：`PMS-struts/config/profiles/<env>/`
>
> **请勿在 profile 配置文件中提交真实凭据。**

本部署默认组合为 `dev,pms2`，产物 `PMS2.war`。如需切换为 PMS3，请通过构建参数覆盖（见 [配置项](#配置项)）。

---

## 目录结构

```
/workspace
├── Dockerfile              # 多阶段构建（Maven 构建 → Tomcat 运行）
├── docker-compose.yml      # db + app 编排
├── scripts/
│   └── deploy.sh           # 部署辅助脚本（需 chmod +x）
├── docs/
│   └── deployment.md       # 本文档
├── docker/db/init/         # 可选：MySQL 初始化脚本挂载点（自动创建）
└── PMS-springmvc/          # 主应用模块（产物 PMS2.war）
```

---

## 快速开始

```bash
# 1. 给部署脚本加可执行权限（首次）
chmod +x scripts/deploy.sh

# 2. 构建并启动（首次会构建镜像，耗时较长）
./scripts/deploy.sh up

# 3. 查看日志
./scripts/deploy.sh logs

# 4. 访问应用
#    http://localhost:8080/

# 5. 停止
./scripts/deploy.sh down
```

---

## 构建命令

### 方式一：使用部署脚本（推荐）

```bash
./scripts/deploy.sh build      # 仅构建应用镜像
```

### 方式二：直接使用 docker-compose

```bash
docker-compose build app
```

### 方式三：在容器外手动构建 WAR

如需在本地先用 Maven 构建 WAR，再拷贝进镜像：

```bash
# PMS-springmvc（pms2）
mvn clean package -P dev,pms2 -DskipTests -pl PMS-springmvc -am
# 产物：PMS-springmvc/target/PMS2.war

# PMS-springmvc（pms3）
mvn clean package -P dev,pms3 -DskipTests -pl PMS-springmvc -am
# 产物：PMS-springmvc/target/AFPMS3.war

# PMS-struts（默认 pms）
mvn clean package -P dev -DskipTests -pl PMS-struts -am
# 产物：PMS-struts/target/PMS.war
```

> `-pl <模块> -am` 表示仅构建该模块及其上游依赖模块，避免构建无关 WAR，加快速度。

---

## 启动与停止

```bash
./scripts/deploy.sh up        # 后台启动 db + app
./scripts/deploy.sh down      # 停止并移除容器与网络（保留数据卷）
./scripts/deploy.sh restart   # 重启全部服务
./scripts/deploy.sh logs      # 跟踪日志（Ctrl+C 退出）
```

等价的 docker-compose 命令：

```bash
docker-compose up -d          # 启动
docker-compose down           # 停止
docker-compose logs -f        # 日志
docker-compose restart        # 重启
```

启动后：

- 应用：`http://localhost:8080/`（部署为 ROOT 上下文）
- 数据库：`localhost:3306`，库名 `dppms_d365`

---

## 配置项

所有配置项均可通过环境变量覆盖默认值，在调用脚本前设置即可。

### 端口

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `APP_PORT` | `8080` | 应用对外端口 |
| `MYSQL_PORT` | `3306` | 数据库对外端口 |

### 数据库

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MYSQL_ROOT_PASSWORD` | `root123` | root 密码 |
| `MYSQL_DATABASE` | `dppms_d365` | 数据库名 |
| `MYSQL_USER` | `pms` | 应用账号 |
| `MYSQL_PASSWORD` | `pms123` | 应用账号密码 |

### 构建与运行

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `BUILD_PROFILE` | `pms2` | 产品线 profile（可选 `pms3`） |
| `WAR_NAME` | `PMS2.war` | WAR 产物名（`pms3` 时为 `AFPMS3.war`） |
| `ENV_PROFILE` | `dev` | 环境 profile（可选 `test`/`release`） |
| `JAVA_OPTS` | `-Xms512m -Xmx2048m` | Tomcat JVM 参数 |

### 示例

```bash
# 切换为 PMS3 版本
BUILD_PROFILE=pms3 WAR_NAME=AFPMS3.war ./scripts/deploy.sh up

# 自定义端口与密码
APP_PORT=9090 MYSQL_ROOT_PASSWORD=secret ./scripts/deploy.sh up
```

### 关于 JDBC 连接

`docker-compose.yml` 中 `app` 服务已通过环境变量 `JDBC_URL`/`JDBC_USERNAME`/`JDBC_PASSWORD` 指向 compose 内的 `db:3306`。

> **注意**：PMS 应用实际从 profile 配置文件（`PMS-springmvc/src/main/resources/profiles/<env>/jdbc.properties`）读取数据库连接。若上述环境变量未被应用直接消费，请将对应 profile 目录下的 `jdbc.properties` 中数据库主机改为 `db`（compose 服务名），例如：
>
> ```properties
> jdbc.url=jdbc:mysql://db:3306/dppms_d365?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
> jdbc.username=pms
> jdbc.password=pms123
> ```
>
> 修改后重建镜像即可生效。

---

## 数据库初始化

- compose 启动时会自动创建数据库 `dppms_d365` 及应用账号。
- **表结构与应用数据不在本次自动化范围内**，需自行准备。两种方式：
  1. **自动初始化**：将 `*.sql` / `*.sh` 脚本放入 `docker/db/init/` 目录，MySQL 容器首次启动时会按文件名顺序自动执行。
  2. **手动导入**：服务启动后，用客户端连接 `localhost:3306` 导入表结构与基础数据：
     ```bash
     mysql -h 127.0.0.1 -P 3306 -uroot -p dppms_d365 < your_schema.sql
     ```
- 数据持久化在命名卷 `pms-mysql-data` 中，`./scripts/deploy.sh down` **不会**删除数据卷。如需彻底清除：
  ```bash
  docker volume rm pms-mysql-data
  ```

---

## 构建产物（WAR）说明

Dockerfile 默认构建 **PMS-springmvc** 模块（`pms2` profile），产物为 `PMS2.war`，位于 `PMS-springmvc/target/`。

WAR 名称来源（已核实 pom）：

- **PMS-springmvc**：`<finalName>${profile.build.name}</finalName>`
  - `pms2` → `profile.build.name=PMS2` → **`PMS2.war`**
  - `pms3` → `profile.build.name=AFPMS3` → `AFPMS3.war`
- **PMS-struts**：`<warName>${profile.build.name}</warName>`
  - `pms`（默认）→ `profile.build.name=PMS` → `PMS.war`
  - `yfpms` → `profile.build.name=YFPMS` → `YFPMS.war`

Dockerfile 阶段 1 构建后会校验 `PMS-springmvc/target/${WAR_NAME}` 存在；阶段 2 将其拷贝到 Tomcat `webapps/ROOT.war`，应用以 ROOT 上下文（`/`）部署。

> 若应用依赖固定上下文路径（如 `/PMS2`），请修改 Dockerfile 中 COPY 目标为 `/usr/local/tomcat/webapps/${WAR_NAME}`，并以 `http://localhost:8080/PMS2/` 访问。

---

## 常见排错

### 1. 端口冲突（8080 / 3306 被占用）

报错示例：`Bind for 0.0.0.0:8080 failed: port is already allocated`

解决：换一个对外端口启动。

```bash
APP_PORT=9090 MYSQL_PORT=3307 ./scripts/deploy.sh up
```

### 2. MySQL 连接被拒（app 启动后报无法连接数据库）

可能原因：

- `db` 服务尚未就绪：compose 已配置 `depends_on: db: condition: service_healthy`，正常会等待。若仍失败，检查 db 健康检查日志：`docker logs pms-mysql`。
- 应用 `jdbc.properties` 中数据库主机仍为本地地址：将其改为 `db`（见 [关于 JDBC 连接](#关于-jdbc-连接)）。
- 账号/密码不匹配：确认 `MYSQL_USER`/`MYSQL_PASSWORD` 与 `jdbc.properties` 一致。

### 3. Profile 不匹配导致 WAR 名错误

报错示例：Dockerfile 阶段 1 末尾 `ls` 校验失败，找不到 `PMS2.war`。

解决：`BUILD_PROFILE` 与 `WAR_NAME` 必须对应：

| BUILD_PROFILE | WAR_NAME |
|---------------|----------|
| `pms2` | `PMS2.war` |
| `pms3` | `AFPMS3.war` |

```bash
BUILD_PROFILE=pms3 WAR_NAME=AFPMS3.war ./scripts/deploy.sh build
```

### 4. Maven 构建时找不到私有制品

报错示例：`Could not resolve dependencies ... com.dp.plat:crm-util:0.0.1-SNAPSHOT` / `erms-plugin` / `MyBatisGenerator` 等。

原因：PMS 依赖部分私有制品，公网中央仓库无。解决：

- 在构建机配置可访问私有 Maven 仓库的 `settings.xml`，并在 Dockerfile 阶段 1 中通过 `COPY settings.xml /root/.m2/` 拷入（需自行添加该步骤）。
- 或将私有制品预装到本地仓库后，将 `~/.m2/repository` 挂载进构建容器（仅限本地调试）。

### 5. 内存不足 / OOM

- Maven 构建阶段 OOM：增大 Docker 可用内存，或在 Dockerfile 构建命令追加 `MAVEN_OPTS=-Xmx1024m`。
- Tomcat 运行 OOM：调整 `JAVA_OPTS`，例如 `JAVA_OPTS="-Xms512m -Xmx3072m" ./scripts/deploy.sh up`。

### 6. 容器外手动构建报“Child module does not exist”

根 pom 模块路径区分大小写（Linux）。模块目录为 `PMS-struts`/`PMS-activiti`/`PMS-springmvc` 等，命令行大小写须与磁盘一致。

### 7. 中文乱码

确保 MySQL 使用 utf8mb4（compose 已默认配置），并检查应用 `jdbc.url` 是否带 `characterEncoding=utf8`。

---

## 附：常用命令速查

```bash
chmod +x scripts/deploy.sh          # 首次授权
./scripts/deploy.sh build           # 构建镜像
./scripts/deploy.sh up              # 启动
./scripts/deploy.sh logs            # 日志
./scripts/deploy.sh restart         # 重启
./scripts/deploy.sh down            # 停止
docker volume rm pms-mysql-data     # 彻底删除数据卷
docker-compose ps                   # 查看服务状态
docker logs -f pms-app              # 单独查看应用日志
docker exec -it pms-mysql mysql -uroot -p   # 进入 MySQL
```
