<#
.SYNOPSIS
    network-equipment-pms 统一环境配置文件
.DESCRIPTION
    所有端口、凭据、路径集中在此处配置，4 个脚本（rebuild-common / start-backend /
    start-all / stop-all）通过 dot-source 引入本文件，实现端口统一调整。

    修改端口只需编辑本文件，无需改动 4 个脚本。

    支持 Nginx 反向代理（可选）：
      - 本地开发默认用 Vite dev server (端口 $FrontendPort) 代理 /api 到后端
      - 如需用 Nginx 作为前端服务器（生产风格），设置 $EnableNginx = $true，
        start-all.ps1 会启动 Nginx 容器并映射 $NginxPort 到主机，
        nginx.conf 中的 proxy_pass 端口会根据 $BackendPort 动态生成。

.NOTES
    Usage (在 4 个脚本中):
      . "$PSScriptRoot\env.ps1"

    用户直接编辑本文件调整端口即可，例如:
      $BackendPort  = 9090    # 后端 Spring Boot 端口
      $FrontendPort = 5173    # 前端 Vite dev server 端口
      $NginxPort    = 8088    # Nginx 主机映射端口
#>

# ============================================================================
# 路径配置
# ============================================================================
$ProjectRoot  = $PSScriptRoot                                # 项目根目录（env.ps1 所在目录）
$FrontendDir  = Join-Path $ProjectRoot "pms-frontend"         # 前端源码目录
$NginxConfSrc = Join-Path $ProjectRoot "nginx.conf"           # nginx.conf 源文件（模板）

# ============================================================================
# 端口配置（核心：修改这里即可统一调整所有脚本）
# ============================================================================

# 后端 Spring Boot 服务端口（对应 application.yml 中的 server.port）
$BackendPort  = 9080

# 前端 Vite dev server 端口（对应 pms-frontend/vite.config.ts 中的 server.port）
$FrontendPort = 5000

# MySQL 端口（本地 MySQL 服务端口，非 Docker 容器端口）
$MysqlPort    = 3307

# Redis 端口（Docker 容器映射到主机的端口）
$RedisPort    = 6379

# Nginx 主机映射端口（Docker 容器内固定 80，映射到主机的端口可配置）
$NginxPort    = 8088

# ============================================================================
# Nginx 开关（本地开发默认关闭，用 Vite dev server）
# ============================================================================
# $true  = start-all.ps1 启动 Nginx 容器（生产风格，前端走 Nginx 静态资源 + API 反向代理）
# $false = start-all.ps1 跳过 Nginx，前端走 Vite dev server（开发风格，Vite 代理 /api 到后端）
$EnableNginx  = $false

# ============================================================================
# 凭据配置
# ============================================================================
$MysqlDatabase = "dpspms"                                    # MySQL 数据库名
$MysqlUser     = "root"                                       # MySQL 用户名
$MysqlPassword = "!Q@W3e4r"                                   # MySQL 密码

$RedisPassword = ""                                           # Redis 密码（空表示无密码）

# ============================================================================
# JWT / 加密密钥（开发环境默认值，生产环境必须覆盖）
# ============================================================================
$JwtSecret    = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEtMjU2LWJpdC11c2FnZQ=="
$AppEncryptKey = "MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY="

# ============================================================================
# JDK / Maven 配置
# ============================================================================
$JavaHome     = "C:\Program Files\Java\jdk-17.0.9"           # JDK 17 安装路径

# ============================================================================
# Docker 容器名配置（用于 start-all / stop-all 识别容器）
# ============================================================================
$RedisContainerName  = "pms-redis"
$NginxContainerName   = "pms-nginx"

# ============================================================================
# 辅助函数：设置通用环境变量（后端启动用）
# ============================================================================
function Set-PmsEnvironment {
    <#
    .SYNOPSIS
        设置后端 Spring Boot 运行所需的环境变量
    .DESCRIPTION
        start-backend.ps1 和 start-all.ps1 都调用此函数，确保环境变量一致。
        避免在两个脚本中重复维护环境变量定义。
    #>
    $env:JAVA_HOME = $JavaHome
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"

    $env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:$MysqlPort/$MysqlDatabase?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
    $env:MYSQL_USER      = $MysqlUser
    $env:MYSQL_PASSWORD  = $MysqlPassword
    $env:REDIS_PASSWORD  = $RedisPassword
    $env:JWT_SECRET      = $JwtSecret
    $env:APP_ENCRYPT_KEY = $AppEncryptKey
}

# ============================================================================
# 辅助函数：生成 nginx.conf（基于当前端口配置）
# ============================================================================
function New-NginxConfig {
    <#
    .SYNOPSIS
        基于 env.ps1 中的端口配置生成 nginx.conf
    .DESCRIPTION
        将 nginx.conf 模板中的 backend:8080 替换为 backend:$BackendPort。
        生成的文件输出到 stdout（调用者可重定向到文件）。
    .PARAMETER OutputPath
        输出文件路径。如不指定则输出到 stdout。
    #>
    param(
        [string]$OutputPath
    )

    if (-not (Test-Path $NginxConfSrc)) {
        Write-Error "nginx.conf 模板不存在: $NginxConfSrc"
        return
    }

    $content = Get-Content $NginxConfSrc -Raw
    # 将 nginx.conf 中的 backend:8080 替换为 backend:$BackendPort
    # 注意：Docker 网络内 backend 是服务名，端口需与后端实际监听端口一致
    $content = $content -replace 'backend:8080', "backend:$BackendPort"

    if ($OutputPath) {
        $content | Out-File -FilePath $OutputPath -Encoding utf8 -NoNewline
        Write-Host "Generated nginx.conf -> $OutputPath (backend proxy port: $BackendPort)" -ForegroundColor DarkGray
    } else {
        $content
    }
}

# ============================================================================
# 辅助函数：打印当前配置（调试用）
# ============================================================================
function Show-PmsConfig {
    Write-Host "============ PMS Configuration ============" -ForegroundColor Cyan
    Write-Host "  ProjectRoot  : $ProjectRoot"                  -ForegroundColor White
    Write-Host "  FrontendDir  : $FrontendDir"                  -ForegroundColor White
    Write-Host "  ---- Ports ----"                               -ForegroundColor Yellow
    Write-Host "  BackendPort  : $BackendPort"                  -ForegroundColor White
    Write-Host "  FrontendPort : $FrontendPort"                 -ForegroundColor White
    Write-Host "  MysqlPort    : $MysqlPort ($MysqlDatabase)"   -ForegroundColor White
    Write-Host "  RedisPort    : $RedisPort"                    -ForegroundColor White
    Write-Host "  NginxPort    : $NginxPort (EnableNginx=$EnableNginx)" -ForegroundColor White
    Write-Host "  ---- Credentials ----"                        -ForegroundColor Yellow
    Write-Host "  MysqlUser    : $MysqlUser"                    -ForegroundColor White
    Write-Host "  JavaHome     : $JavaHome"                     -ForegroundColor White
    Write-Host "==========================================" -ForegroundColor Cyan
}
