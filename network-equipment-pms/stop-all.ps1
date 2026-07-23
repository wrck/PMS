<#
.SYNOPSIS
    network-equipment-pms one-click stop script
.DESCRIPTION
    Stops:
      1. [Optional] Nginx container (if running) - always stopped
      2. Frontend Vite process (port $FrontendPort) - only if Nginx was disabled
      3. Backend Spring Boot process (port $BackendPort)
      4. Optional: Redis container (stopped only with -StopRedis switch)
    底层已迁移到 yudao framework，后端 pms-admin 通过 spring-boot:run 启动，
    本脚本通过端口定位 Java 进程并停止，同时清理残留 mvn 进程。

    端口 / 容器名等配置统一在 env.ps1 中管理。
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\stop-all.ps1
      powershell -ExecutionPolicy Bypass -File .\stop-all.ps1 -StopRedis
#>

param(
    [switch]$StopRedis
)

# 引入统一配置（端口 / 容器名）
. "$PSScriptRoot\env.ps1"

function Write-Step  { param($msg) Write-Host "`n[STEP] $msg" -ForegroundColor Cyan }
function Write-Ok    { param($msg) Write-Host "  [OK]   $msg" -ForegroundColor Green }
function Write-Warn2 { param($msg) Write-Host "  [WARN] $msg" -ForegroundColor Yellow }

function Stop-PortProcesses {
    param([int]$Port, [string]$Name)
    $conns = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if (-not $conns) {
        Write-Warn2 "$Name is not listening on port $Port"
        return
    }
    foreach ($conn in $conns) {
        $proc = Get-Process -Id $conn.OwningProcess -ErrorAction SilentlyContinue
        if ($proc) {
            try {
                Stop-Process -Id $proc.Id -Force -ErrorAction Stop
                Write-Ok "Stopped $Name process: $($proc.Name) (PID $($proc.Id))"
            } catch {
                Write-Warn2 "Failed to stop PID $($proc.Id): $_"
            }
        }
    }
}

function Stop-DockerContainer {
    param([string]$Name)
    $running = docker ps --filter "name=$Name" --filter "status=running" --format "{{.Names}}" 2>$null
    if ($running -eq $Name) {
        docker stop $Name 2>$null | Out-Null
        Write-Ok "Stopped Docker container: $Name"
    } else {
        Write-Warn2 "Docker container $Name is not running"
    }
}

Write-Host "================================================" -ForegroundColor White
Write-Host "  network-equipment-pms one-click stop" -ForegroundColor White
Write-Host "  FrontendPort : $FrontendPort (Vite dev)" -ForegroundColor DarkGray
Write-Host "  BackendPort  : $BackendPort"  -ForegroundColor DarkGray
Write-Host "  NginxPort    : $NginxPort (container: $NginxContainerName)" -ForegroundColor DarkGray
Write-Host "  RedisPort    : $RedisPort (container: $RedisContainerName)" -ForegroundColor DarkGray
Write-Host "================================================" -ForegroundColor White

# ---------- 1. Stop Nginx container (always) ----------
Write-Step "Stop Nginx container ($NginxContainerName)"
Stop-DockerContainer -Name $NginxContainerName

# ---------- 2. Stop frontend Vite process ----------
Write-Step "Stop frontend service (port $FrontendPort)"
Stop-PortProcesses -Port $FrontendPort -Name "Frontend Vite"

# ---------- 3. Stop backend Spring Boot process ----------
Write-Step "Stop backend service (port $BackendPort)"
Stop-PortProcesses -Port $BackendPort -Name "Backend Spring Boot"
# Clean up residual mvn processes related to this project
$mvnProcs = Get-Process -Name "mvn" -ErrorAction SilentlyContinue
foreach ($p in $mvnProcs) {
    try { Stop-Process -Id $p.Id -Force; Write-Ok "Stopped residual mvn process (PID $($p.Id))" }
    catch { Write-Warn2 "Failed to stop mvn PID $($p.Id): $_" }
}

# ---------- 4. Stop Redis container (optional) ----------
if ($StopRedis) {
    Write-Step "Stop Redis container ($RedisContainerName)"
    Stop-DockerContainer -Name $RedisContainerName
} else {
    Write-Step "Keep Redis container running (use -StopRedis switch to stop it)"
}

Write-Host "`n================================================" -ForegroundColor Green
Write-Host "  Stop completed" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
