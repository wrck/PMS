# PMS 备份恢复与 DR 演练脚本

本目录包含 PMS 项目的备份、恢复、清理及灾难恢复（DR）演练脚本，支持 Docker 部署模式和直连模式。

## 脚本概览

| 脚本 | 用途 | 调用频率 |
|------|------|----------|
| `backup.sh` | 全量/增量备份（MySQL + binlog + Redis RDB） | 每日全量 + 中午增量 |
| `restore.sh` | 从备份归档恢复 MySQL + Redis | 故障时手动执行 |
| `dr-drill.sh` | 灾难恢复演练（端到端验证） | 每月 1 次 |
| `backup-cleanup.sh` | 清理过期备份（保留周一/月初） | 每日 |
| `crontab.example` | crontab 定时任务配置示例 | 部署时配置 |
| `init-db.sh` | 数据库初始化（已有，不属于本模块） | 一次性 |

## 快速开始

### 1. 配置环境变量

将密码等敏感信息放入 `/etc/pms-backup.env`（权限 600）：

```bash
sudo tee /etc/pms-backup.env > /dev/null <<'EOF'
#!/bin/bash
export BACKUP_DIR=/data/backups/pms
export MYSQL_HOST=127.0.0.1
export MYSQL_PORT=3306
export MYSQL_USER=root
export MYSQL_PASSWORD=<your_real_password>
export MYSQL_DATABASE=pms
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_PASSWORD=<your_real_redis_password>
# Docker 模式（推荐）
export USE_DOCKER=true
export MYSQL_CONTAINER=pms-mysql
export REDIS_CONTAINER=pms-redis
# 保留策略
export RETENTION_DAYS=30
# 通知（可选）
export WEBHOOK_URL=https://hooks.example.com/services/pms-backup
EOF
sudo chmod 600 /etc/pms-backup.env
```

### 2. 执行首次备份

```bash
source /etc/pms-backup.env
./scripts/backup.sh --type full
```

### 3. 配置定时任务

参考 `crontab.example`，使用 `crontab -e` 添加定时任务。

## 配置参数说明

### 通用配置

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `BACKUP_DIR` | `/data/backups/pms` | 备份文件存储目录 |
| `RETENTION_DAYS` | `30` | 备份保留天数（清理脚本使用） |
| `KEEP_RECENT_DAYS` | `7` | 最近多少天内的备份不参与清理 |
| `WEBHOOK_URL` | (空) | 通知 webhook（支持飞书/钉钉/企业微信等） |
| `USE_DOCKER` | `false` | 是否使用 Docker 模式（通过 docker exec 调用容器内命令） |
| `MIN_DISK_FREE_MB` | `2048` | 备份前最低可用磁盘 MB（不足则中止） |

### MySQL 配置

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `MYSQL_HOST` | `127.0.0.1` | MySQL 主机 |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `MYSQL_USER` | `root` | MySQL 用户名 |
| `MYSQL_PASSWORD` | (空) | MySQL 密码（**必填**，从环境变量读取） |
| `MYSQL_DATABASE` | `pms` | 要备份的数据库名 |
| `MYSQL_CONTAINER` | `pms-mysql` | MySQL 容器名（Docker 模式） |

### Redis 配置

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `REDIS_HOST` | `127.0.0.1` | Redis 主机 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | (空) | Redis 密码 |
| `REDIS_CONTAINER` | `pms-redis` | Redis 容器名（Docker 模式） |
| `REDIS_RDB_PATH_IN_CONTAINER` | `/data/dump.rdb` | Redis RDB 文件在容器内的路径 |

### DR 演练专用

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `DR_ENV` | `test` | 演练环境（禁止设为 `prod`/`production`） |
| `DR_DIR` | `/tmp/dr_drill_<timestamp>` | 演练工作目录 |
| `VERIFY_TABLES` | `pms_project pms_asset pms_impl_task pms_settlement sys_user` | 校验表列表 |

## 备份恢复流程

### 备份流程

```
backup.sh --type full
    │
    ├── 1. 磁盘空间检查（不足 2GB 中止）
    ├── 2. 依赖工具检查
    ├── 3. MySQL 全量备份（mysqldump --single-transaction --master-data=2）
    ├── 4. MySQL binlog 备份（mysqlbinlog --read-from-remote-server --raw）
    ├── 5. Redis RDB 备份（BGSAVE + docker cp）
    ├── 6. 写入 manifest.txt 元数据
    ├── 7. tar.gz 打包 + md5 校验
    └── 8. 清理临时目录 + 通知
```

**备份归档结构**：
```
pms_YYYYMMDD_HHMMSS.tar.gz
├── pms_YYYYMMDD_HHMMSS/
│   ├── mysql_full.sql          # MySQL 全量 SQL
│   ├── mysql_binlog.meta       # binlog 坐标元数据
│   ├── mysql-bin.000123        # binlog 文件（如有权限拉取）
│   ├── redis_dump.rdb          # Redis RDB 快照
│   └── manifest.txt            # 备份元数据
└── pms_YYYYMMDD_HHMMSS.tar.gz.md5   # md5 校验文件
```

### 恢复流程

```
restore.sh <backup.tar.gz>
    │
    ├── 1. md5 完整性校验
    ├── 2. 解压到临时目录
    ├── 3. 用户确认（破坏性操作）
    ├── 4. 停止应用容器（避免数据冲突）
    ├── 5. MySQL 恢复（全量 SQL 导入 + 增量 binlog 重放）
    ├── 6. Redis 恢复（停 Redis → 替换 dump.rdb → 启 Redis）
    ├── 7. 启动应用容器
    └── 8. 数据校验（关键表行数 + Redis 连接性）
```

**恢复选项**：
- `--skip-mysql`：跳过 MySQL 恢复
- `--skip-redis`：跳过 Redis 恢复
- `--skip-app-stop`：跳过停止应用（不推荐）
- `--yes, -y`：跳过确认提示（用于自动化）

## 各脚本详细用法

### backup.sh — 备份脚本

```bash
# 全量备份（默认）
./backup.sh --type full

# 增量备份（仅 binlog + Redis RDB，跳过 mysqldump）
./backup.sh --type incremental

# 通过环境变量配置
BACKUP_DIR=/data/backups/pms USE_DOCKER=true ./backup.sh
```

**备份类型说明**：
- `full`：完整备份，包含 mysqldump 全量 SQL + binlog 坐标 + Redis RDB
- `incremental`：增量备份，仅包含 binlog 坐标 + Redis RDB（用于缩短 RPO）

### restore.sh — 恢复脚本

```bash
# 恢复指定备份文件（会要求确认）
./restore.sh /data/backups/pms/pms_20240101_020000.tar.gz

# 跳过确认（自动化场景）
./restore.sh pms_20240101_020000.tar.gz --yes

# 仅恢复 MySQL
./restore.sh pms_20240101_020000.tar.gz --skip-redis --yes

# 仅恢复 Redis
./restore.sh pms_20240101_020000.tar.gz --skip-mysql --yes
```

**binlog 重放规则**：
- **全量备份**：跳过 binlog 重放（mysqldump --single-transaction 已含一致快照，重放会导致重复键）
- **增量备份**：自动重放 binlog 文件

### dr-drill.sh — DR 演练脚本

```bash
# 在测试环境执行 DR 演练
DR_ENV=test ./dr-drill.sh

# 跳过确认 + 跳过 Redis 演练
./dr-drill.sh --yes --skip-redis

# 指定演练环境
./dr-drill.sh --env staging --yes
```

**演练流程**：
1. 创建全量备份（验证备份流程）
2. 记录故障前数据快照（关键表行数）
3. 创建独立测试库 `${MYSQL_DATABASE}_dr` 并恢复备份到该库
4. 校验恢复数据（对比生产库 vs 测试库行数）
5. Redis DR 演练（备份 RDB + 在 Redis DB 15 复制 key 验证）
6. 清理测试资源（删除测试库 + FLUSHDB 演练 DB）
7. 生成 Markdown 演练报告

**安全保证**：
- 禁止在生产环境执行（`DR_ENV=prod` 会直接拒绝）
- 使用独立测试库后缀 `_dr`，绝不触碰生产库
- Redis 演练使用 DB 15，不影响业务数据（DB 0）
- 所有破坏性操作需用户确认（除非 `--yes`）

### backup-cleanup.sh — 清理脚本

```bash
# 清理过期备份（默认 30 天，保留周一/月初）
./backup-cleanup.sh

# dry-run 模式（仅打印不删除）
./backup-cleanup.sh --dry-run

# 自定义保留天数
RETENTION_DAYS=60 ./backup-cleanup.sh
```

**清理规则**：
1. 最近 `KEEP_RECENT_DAYS`（默认 7）天内的备份始终保留
2. 未过期的备份（`< RETENTION_DAYS` 天）保留
3. 已过期但属于**周一**的备份保留（周备份）
4. 已过期但属于**每月 1 号**的备份保留（月备份）
5. 其余已过期备份删除

**安全检查**：
- 备份目录不存在时拒绝执行
- 无任何备份文件时拒绝执行（避免误操作）
- 最新备份超过 7 天时发出警告，自动放宽清理阈值至 60 天

## 故障恢复流程

### 场景 1：数据误删除

```bash
# 1. 立即停止应用，防止数据继续被修改
docker stop pms-backend

# 2. 查找最新可用备份
ls -lt /data/backups/pms/pms_*.tar.gz | head -5

# 3. 执行恢复
source /etc/pms-backup.env
./scripts/restore.sh /data/backups/pms/pms_20240101_020000.tar.gz --yes

# 4. 验证数据
mysql -h 127.0.0.1 -u root -p pms -e "SELECT COUNT(*) FROM pms_project;"

# 5. 重启应用
docker start pms-backend
```

### 场景 2：基于 binlog 的点恢复（PITR）

如果需要恢复到故障发生前的某个时间点：

```bash
# 1. 先恢复最近的全量备份
./scripts/restore.sh pms_20240101_020000.tar.gz --skip-redis --yes

# 2. 从全量备份的 manifest 或 SQL 注释中获取 binlog 起始位置
grep "CHANGE MASTER" /tmp/pms-restore-*/pms_*/mysql_full.sql
# 输出: -- CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000123', MASTER_LOG_POS=12345

# 3. 重放 binlog 到指定时间点
mysqlbinlog --start-position=12345 \
    --stop-datetime="2024-01-15 10:30:00" \
    mysql-bin.000123 mysql-bin.000124 \
    | mysql -h 127.0.0.1 -u root -p pms
```

### 场景 3：Redis 数据丢失

```bash
# 1. 停止 Redis
docker stop pms-redis

# 2. 替换 RDB 文件
docker cp /data/backups/pms/pms_20240101_020000/redis_dump.rdb pms-redis:/data/dump.rdb

# 3. 启动 Redis
docker start pms-redis

# 4. 验证
redis-cli -a <password> DBSIZE
```

### 场景 4：完整灾难恢复

当整个环境需要重建时：

```bash
# 1. 启动基础设施（空数据库）
docker-compose -f docker-compose.infra.yml up -d
# 等待 MySQL/Redis 健康检查通过

# 2. 恢复 MySQL 全量备份
source /etc/pms-backup.env
./scripts/restore.sh /data/backups/pms/pms_latest.tar.gz --skip-app-stop --yes

# 3. 启动应用
docker-compose -f docker-compose.app.yml up -d

# 4. 验证应用健康
curl http://localhost:8080/actuator/health

# 5. 执行 DR 演练验证恢复结果
DR_ENV=test ./scripts/dr-drill.sh --yes
```

## 监控与告警

### 推荐监控项

1. **备份成功率**：检查 `/var/log/pms-backup/backup.log` 是否有 `备份成功` 关键字
2. **备份文件存在性**：`/data/backups/pms/` 下是否有当天的 `pms_YYYYMMDD_*.tar.gz`
3. **备份文件大小**：突然变小可能表示数据丢失
4. **磁盘空间**：`/data` 分区可用空间低于 20% 时告警
5. **清理任务执行**：检查 `cleanup.log` 是否有异常

### Webhook 通知

所有脚本支持通过 `WEBHOOK_URL` 环境变量发送通知，payload 格式：

```json
{
    "event": "backup|restore|dr_drill|backup_cleanup",
    "status": "success|error|info",
    "message": "人类可读的消息",
    "backupName": "pms_20240101_020000",
    "host": "hostname",
    "timestamp": "2024-01-01T02:00:00+08:00"
}
```

## 安全注意事项

1. **密码不要硬编码**：所有密码通过环境变量传入，推荐放入 `/etc/pms-backup.env`（权限 600）
2. **DR 演练隔离**：`dr-drill.sh` 使用独立测试库 `${MYSQL_DATABASE}_dr`，绝不触碰生产库
3. **恢复前停应用**：`restore.sh` 会自动停止 `pms-backend` 容器，避免恢复期间数据冲突
4. **磁盘空间检查**：`backup.sh` 备份前检查磁盘空间，不足 2GB 时中止
5. **md5 校验**：备份生成 md5 校验文件，恢复时自动校验完整性
6. **日志审计**：所有操作写入日志文件，便于审计追溯

## 常见问题

### Q1: binlog 备份失败怎么办？

`backup.sh` 中 binlog 拉取需要 MySQL 用户具备 `REPLICATION SLAVE` 权限。如果失败，脚本会记录告警但不会中断备份流程（仍会记录 binlog 坐标元数据）。

授权命令：
```sql
GRANT REPLICATION SLAVE ON *.* TO 'backup_user'@'%';
FLUSH PRIVILEGES;
```

### Q2: Docker 模式下 Redis RDB 备份失败？

确保 Redis 容器的数据卷已正确挂载，且 RDB 文件路径配置正确（默认 `/data/dump.rdb`）。

### Q3: 恢复后应用无法启动？

检查：
1. MySQL 连接是否正常：`mysql -h <host> -u <user> -p<password> -e "SELECT 1"`
2. Redis 连接是否正常：`redis-cli -h <host> -p <port> -a <password> PING`
3. 应用日志：`docker logs pms-backend`

### Q4: 如何验证备份文件可用？

定期执行 DR 演练：
```bash
DR_ENV=test ./scripts/dr-drill.sh --yes
```

或手动验证：
```bash
# 校验 md5
md5sum -c /data/backups/pms/pms_20240101_020000.tar.gz.md5

# 解压检查内容
tar -tzf /data/backups/pms/pms_20240101_020000.tar.gz
```

### Q5: 清理脚本误删了备份怎么办？

清理脚本默认保留最近 7 天 + 周一 + 月初的备份，且最新备份超过 7 天时会自动放宽阈值。如仍误删：
1. 检查 `cleanup_*.log` 确认删除了哪些文件
2. 从对象存储/异地备份恢复
3. 使用 `--dry-run` 模式预先验证
