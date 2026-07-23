<#
.SYNOPSIS
    network-equipment-pms one-click start script (dev environment)
.DESCRIPTION
    Starts in order:
      1. MySQL check (port $MysqlPort, database $MysqlDatabase)
      2. Redis container (Docker, port $RedisPort)
      3. Backend service pms-admin (Spring Boot, port $BackendPort)
      4. [Optional] Nginx container (Docker, port $NginxPort) - if $EnableNginx = $true
      5. Frontend service (Vite dev server port $FrontendPort, or skipped if Nginx enabled)
    MySQL must be started locally by the user.

    端口 / 凭据 / Nginx 开关等配置统一在 env.ps1 中管理。
    修改端口只需编辑 env.ps1，无需改动本脚本。

    底层已迁移到 yudao framework，首次运行或修改底层模块后请先执行 .\rebuild-common.ps1。
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\start-all.ps1
#>

# 引入统一配置（端口 / 凭据 / JDK 路径）
. "$PSScriptRoot\env.ps1"

# ========== Helper Functions ==========
function Write-Step  { param($msg) Write-Host "`n[STEP] $msg" -ForegroundColor Cyan }
function Write-Ok    { param($msg) Write-Host "  [OK]   $msg" -ForegroundColor Green }
function Write-Warn2 { param($msg) Write-Host "  [WARN] $msg" -ForegroundColor Yellow }
function Write-Err   { param($msg) Write-Host "  [ERR]  $msg" -ForegroundColor Red }

function Test-PortListening {
    param([int]$Port)
    try {
        $tcp = New-Object System.Net.Sockets.TcpClient
        $iar = $tcp.BeginConnect("127.0.0.1", $Port, $null, $null)
        $success = $iar.AsyncWaitHandle.WaitOne(500)
        if ($success) { $tcp.EndConnect($iar); $tcp.Close(); return $true }
        $tcp.Close()
        return $false
    } catch { return $false }
}

function Wait-PortReady {
    param([int]$Port, [int]$TimeoutSec = 120, [string]$ServiceName)
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        if (Test-PortListening -Port $Port) {
            Write-Ok "$ServiceName port $Port is ready"
            return $true
        }
        Start-Sleep -Seconds 2
        Write-Host "." -NoNewline
    }
    Write-Host ""
    return $false
}

# ========== Main Flow ==========

Write-Host "================================================" -ForegroundColor White
Write-Host "  network-equipment-pms one-click start" -ForegroundColor White
Write-Host "  Project root: $ProjectRoot" -ForegroundColor White
Write-Host "================================================" -ForegroundColor White
Write-Host "  BackendPort  : $BackendPort"  -ForegroundColor DarkGray
Write-Host "  FrontendPort : $FrontendPort" -ForegroundColor DarkGray
Write-Host "  MysqlPort    : $MysqlPort ($MysqlDatabase)" -ForegroundColor DarkGray
Write-Host "  RedisPort    : $RedisPort"    -ForegroundColor DarkGray
Write-Host "  NginxPort    : $NginxPort (EnableNginx=$EnableNginx)" -ForegroundColor DarkGray
Write-Host "================================================" -ForegroundColor White

# ---------- 1. MySQL check ----------
Write-Step "Check MySQL (port $MysqlPort, database $MysqlDatabase)"
if (Test-PortListening -Port $MysqlPort) {
    Write-Ok "MySQL is listening on port $MysqlPort"
} else {
    Write-Err "MySQL is NOT listening on port $MysqlPort. Please start local MySQL first (database $MysqlDatabase)."
    Write-Host "    Hint: E:\mysql-8.0.16-winx64\bin\mysqld --defaults-file=E:\mysql-8.0.16-winx64\my.ini" -ForegroundColor DarkGray
    exit 1
}

# ---------- 2. Redis container ----------
Write-Step "Check Redis container ($RedisContainerName, port $RedisPort)"
$redisRunning = docker ps --filter "name=$RedisContainerName" --filter "status=running" --format "{{.Names}}" 2>$null
if ($redisRunning -eq $RedisContainerName) {
    Write-Ok "Redis container $RedisContainerName is running"
} else {
    $redisExists = docker ps -a --filter "name=$RedisContainerName" --format "{{.Names}}" 2>$null
    if ($redisExists -eq $RedisContainerName) {
        Write-Warn2 "Container exists but stopped, starting..."
        docker start $RedisContainerName | Out-Null
    } else {
        Write-Warn2 "Container not found, creating and starting..."
        docker run -d --name $RedisContainerName `
            -p "${RedisPort}:6379" `
            --restart unless-stopped `
            redis:7.2-alpine `
            redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru | Out-Null
    }
    if (-not (Wait-PortReady -Port $RedisPort -TimeoutSec 30 -ServiceName "Redis")) {
        Write-Err "Redis failed to start"
        exit 1
    }
}

# ---------- 3. Backend service ----------
Write-Step "Check backend service pms-admin (port $BackendPort)"
if (Test-PortListening -Port $BackendPort) {
    Write-Ok "Backend is listening on port $BackendPort, skip starting"
} else {
    Write-Warn2 "Backend not running, starting in a new window..."

    # Build commands to execute in the new window
    # NOTE: skip "mvn install" to avoid Maven repository write issues in sandbox/restricted environments.
    # Modules are already installed in local repo from previous builds; spring-boot:run compiles as needed.
    # 底层已迁移到 yudao framework：如修改 yudao-framework / pms-common / pms-system 等，
    # 请先在主窗口执行 .\rebuild-common.ps1，再运行 start-all.ps1。
    # Maven 选项：默认使用系统 Maven settings（~/.m2/settings.xml）。
    # 如需使用项目自带沙箱专用配置（仅 Linux 沙箱），可手动改为 $settingsArg = '-s maven-settings.sandbox.xml'。
    # 注意：maven-settings.sandbox.xml 内含 127.0.0.1:18080 代理与 Linux 本地仓库路径，
    # 在 Windows 或其他无该代理的环境下会因 Connection refused 导致依赖无法下载。
    $settingsArg = ""
    $backendCmd = @"
`$env:JAVA_HOME = '$JavaHome'
`$env:Path = "`$env:JAVA_HOME\bin;`$env:Path"
`$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:$MysqlPort/${MysqlDatabase}?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
`$env:MYSQL_USER      = '$MysqlUser'
`$env:MYSQL_PASSWORD  = '$MysqlPassword'
`$env:REDIS_PASSWORD  = '$RedisPassword'
`$env:JWT_SECRET      = '$JwtSecret'
`$env:APP_ENCRYPT_KEY = '$AppEncryptKey'
Set-Location '$ProjectRoot'
Write-Host 'Starting pms-admin (spring-boot:run) on port $BackendPort...'
Write-Host 'NOTE: If you changed yudao-framework / pms-common / pms-system, run .\rebuild-common.ps1 first.'
mvn $settingsArg spring-boot:run -pl pms-admin '-Dmaven.test.skip=true' '-Dspring-boot.run.jvmArguments=-Dotel.sdk.disabled=true -Dotel.traces.exporter=none -Dotel.metrics.exporter=none -Dotel.logs.exporter=none'
pause
"@

    # -Command reparses and can strip nested quotes from the generated script.
    # EncodedCommand preserves the script text exactly across the process boundary.
    $encodedBackendCmd = [Convert]::ToBase64String([Text.Encoding]::Unicode.GetBytes($backendCmd))
    Start-Process powershell -ArgumentList @("-NoExit", "-EncodedCommand", $encodedBackendCmd)
    Write-Warn2 "Backend starting (new window), waiting up to 180s for port ready..."
    if (-not (Wait-PortReady -Port $BackendPort -TimeoutSec 180 -ServiceName "Backend")) {
        Write-Err "Backend startup timeout, check the backend window logs"
        exit 1
    }
}

# ---------- 4. Nginx container (optional) ----------
if ($EnableNginx) {
    Write-Step "Check Nginx container ($NginxContainerName, host port $NginxPort)"
    $nginxRunning = docker ps --filter "name=$NginxContainerName" --filter "status=running" --format "{{.Names}}" 2>$null
    if ($nginxRunning -eq $NginxContainerName) {
        Write-Ok "Nginx container $NginxContainerName is running"
    } else {
        $nginxExists = docker ps -a --filter "name=$NginxContainerName" --format "{{.Names}}" 2>$null
        if ($nginxExists -eq $NginxContainerName) {
            Write-Warn2 "Container exists but stopped, starting..."
            docker start $NginxContainerName | Out-Null
        } else {
            Write-Warn2 "Container not found, creating and starting..."
            # 生成临时 nginx.conf（基于 env.ps1 中的端口配置）
            $tempNginxConf = Join-Path $env:TEMP "pms-nginx-generated.conf"
            New-NginxConfig -OutputPath $tempNginxConf

            # 前端 dist 目录（如已构建）
            $frontendDist = Join-Path $FrontendDir "dist"
            if (-not (Test-Path $frontendDist)) {
                Write-Warn2 "Frontend dist not found at $frontendDist"
                Write-Warn2 "Nginx will serve empty directory. Run 'npm run build' in pms-frontend first."
                $frontendDist = $FrontendDir  # fallback
            }

            docker run -d --name $NginxContainerName `
                -p "${NginxPort}:80" `
                -v "${tempNginxConf}:/etc/nginx/conf.d/default.conf:ro" `
                -v "${frontendDist}:/usr/share/nginx/html:ro" `
                --restart unless-stopped `
                nginx:1.25-alpine | Out-Null
        }
        if (-not (Wait-PortReady -Port $NginxPort -TimeoutSec 30 -ServiceName "Nginx")) {
            Write-Err "Nginx failed to start"
            exit 1
        }
    }
} else {
    Write-Step "Nginx disabled (EnableNginx=$false in env.ps1), using Vite dev server for frontend"
}

# ---------- 5. Frontend service (skipped if Nginx enabled) ----------
if ($EnableNginx) {
    Write-Step "Skip Vite dev server (Nginx is serving frontend on port $NginxPort)"
} else {
    Write-Step "Check frontend service pms-frontend (port $FrontendPort)"
    if (Test-PortListening -Port $FrontendPort) {
        Write-Ok "Frontend is listening on port $FrontendPort, skip starting"
    } else {
        if (-not (Test-Path (Join-Path $FrontendDir "node_modules"))) {
            Write-Warn2 "First run, installing frontend dependencies (may take a few minutes)..."
            Push-Location $FrontendDir
            npm install
            $npmExit = $LASTEXITCODE
            Pop-Location
            if ($npmExit -ne 0) { Write-Err "npm install failed"; exit 1 }
        }
        Write-Warn2 "Frontend not running, starting in a new window..."
        $frontendCmd = @"
Set-Location '$FrontendDir'
npm run dev -- --port $FrontendPort
pause
"@
        Start-Process powershell -ArgumentList @("-NoExit", "-Command", $frontendCmd)
        Write-Warn2 "Frontend starting (new window), waiting up to 60s for port ready..."
        if (-not (Wait-PortReady -Port $FrontendPort -TimeoutSec 60 -ServiceName "Frontend")) {
            Write-Err "Frontend startup timeout, check the frontend window logs"
            exit 1
        }
    }
}

# ---------- 6. Health check and summary ----------
Write-Step "Health check"
try {
    $health = curl.exe -s "http://localhost:$BackendPort/actuator/health"
    Write-Ok "Backend health: $health"
} catch {
    Write-Warn2 "Backend health check failed: $_"
}

Write-Host "`n================================================" -ForegroundColor Green
Write-Host "  All services started successfully" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
if ($EnableNginx) {
    Write-Host "  Frontend (Nginx): http://localhost:$NginxPort/" -ForegroundColor White
} else {
    Write-Host "  Frontend (Vite):  http://localhost:$FrontendPort/" -ForegroundColor White
}
Write-Host "  Backend API:      http://localhost:$BackendPort/api/..." -ForegroundColor White
Write-Host "  Health:           http://localhost:$BackendPort/actuator/health" -ForegroundColor White
Write-Host "  Swagger:          http://localhost:$BackendPort/doc.html" -ForegroundColor White
Write-Host "================================================" -ForegroundColor Green
Write-Host "  To stop: close the popup PowerShell windows" -ForegroundColor DarkGray
Write-Host "  Or run:  .\stop-all.ps1" -ForegroundColor DarkGray
Write-Host "  Config: .\env.ps1 (edit ports/credentials here)" -ForegroundColor DarkGray
Write-Host "================================================" -ForegroundColor Green
