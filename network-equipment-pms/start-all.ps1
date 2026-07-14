<#
.SYNOPSIS
    network-equipment-pms one-click start script (dev environment)
.DESCRIPTION
    Starts in order:
      1. Redis container (Docker)
      2. Backend service pms-admin (Spring Boot, port 8080)
      3. Frontend service pms-frontend (Vite, port 3000)
    MySQL must be started locally by the user (port 3307, database dpspms).
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\start-all.ps1
#>

# ========== Paths and Ports ==========
$ProjectRoot  = $PSScriptRoot
$FrontendDir  = Join-Path $ProjectRoot "pms-frontend"
$BackendPort  = 8080
$FrontendPort = 3000
$RedisPort    = 6379
$MysqlPort    = 3307

# ========== Environment Variables (keep in sync with start-backend.ps1) ==========
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.9"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:$MysqlPort/dpspms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:MYSQL_USER      = "root"
$env:MYSQL_PASSWORD  = "!Q@W3e4r"
$env:REDIS_PASSWORD  = ""
$env:JWT_SECRET      = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEtMjU2LWJpdC11c2FnZQ=="
$env:APP_ENCRYPT_KEY = "MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY="

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

# ---------- 1. MySQL check ----------
Write-Step "Check MySQL (port $MysqlPort)"
if (Test-PortListening -Port $MysqlPort) {
    Write-Ok "MySQL is listening on port $MysqlPort"
} else {
    Write-Err "MySQL is NOT listening on port $MysqlPort. Please start local MySQL first (database dpspms)."
    Write-Host "    Hint: E:\mysql-8.0.16-winx64\bin\mysqld --defaults-file=E:\mysql-8.0.16-winx64\my.ini" -ForegroundColor DarkGray
    exit 1
}

# ---------- 2. Redis container ----------
Write-Step "Check Redis container (pms-redis, port $RedisPort)"
$redisRunning = docker ps --filter "name=pms-redis" --filter "status=running" --format "{{.Names}}" 2>$null
if ($redisRunning -eq "pms-redis") {
    Write-Ok "Redis container pms-redis is running"
} else {
    $redisExists = docker ps -a --filter "name=pms-redis" --format "{{.Names}}" 2>$null
    if ($redisExists -eq "pms-redis") {
        Write-Warn2 "Container exists but stopped, starting..."
        docker start pms-redis | Out-Null
    } else {
        Write-Warn2 "Container not found, creating and starting..."
        docker run -d --name pms-redis `
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
    $backendCmd = @"
`$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17.0.9'
`$env:Path = "`$env:JAVA_HOME\bin;`$env:Path"
`$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:$MysqlPort/dpspms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
`$env:MYSQL_USER      = 'root'
`$env:MYSQL_PASSWORD  = '!Q@W3e4r'
`$env:REDIS_PASSWORD  = ''
`$env:JWT_SECRET      = 'dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEtMjU2LWJpdC11c2FnZQ=='
`$env:APP_ENCRYPT_KEY = 'MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY='
Set-Location '$ProjectRoot'
Write-Host 'Starting pms-admin (spring-boot:run)...'
mvn spring-boot:run -pl pms-admin '-Dmaven.test.skip=true' '-Dspring-boot.run.jvmArguments=-Dotel.sdk.disabled=true -Dotel.traces.exporter=none -Dotel.metrics.exporter=none -Dotel.logs.exporter=none'
pause
"@

    Start-Process powershell -ArgumentList @("-NoExit", "-Command", $backendCmd)
    Write-Warn2 "Backend starting (new window), waiting up to 180s for port ready..."
    if (-not (Wait-PortReady -Port $BackendPort -TimeoutSec 180 -ServiceName "Backend")) {
        Write-Err "Backend startup timeout, check the backend window logs"
        exit 1
    }
}

# ---------- 4. Frontend service ----------
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
npm run dev
pause
"@
    Start-Process powershell -ArgumentList @("-NoExit", "-Command", $frontendCmd)
    Write-Warn2 "Frontend starting (new window), waiting up to 60s for port ready..."
    if (-not (Wait-PortReady -Port $FrontendPort -TimeoutSec 60 -ServiceName "Frontend")) {
        Write-Err "Frontend startup timeout, check the frontend window logs"
        exit 1
    }
}

# ---------- 5. Health check and summary ----------
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
Write-Host "  Frontend:    http://localhost:$FrontendPort/" -ForegroundColor White
Write-Host "  Backend API: http://localhost:$BackendPort/api/..." -ForegroundColor White
Write-Host "  Health:      http://localhost:$BackendPort/actuator/health" -ForegroundColor White
Write-Host "  Swagger:     http://localhost:$BackendPort/doc.html" -ForegroundColor White
Write-Host "================================================" -ForegroundColor Green
Write-Host "  To stop: close the two popup PowerShell windows" -ForegroundColor DarkGray
Write-Host "  Or run:  .\stop-all.ps1" -ForegroundColor DarkGray
Write-Host "================================================" -ForegroundColor Green
