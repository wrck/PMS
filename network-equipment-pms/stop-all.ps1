<#
.SYNOPSIS
    network-equipment-pms one-click stop script
.DESCRIPTION
    Stops:
      1. Frontend Vite process (port 3000)
      2. Backend Spring Boot process (port 8080)
      3. Optional: Redis container (stopped only with -StopRedis switch)
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\stop-all.ps1
      powershell -ExecutionPolicy Bypass -File .\stop-all.ps1 -StopRedis
#>

param(
    [switch]$StopRedis
)

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

Write-Host "================================================" -ForegroundColor White
Write-Host "  network-equipment-pms one-click stop" -ForegroundColor White
Write-Host "================================================" -ForegroundColor White

Write-Step "Stop frontend service (port 3000)"
Stop-PortProcesses -Port 3000 -Name "Frontend Vite"

Write-Step "Stop backend service (port 8080)"
Stop-PortProcesses -Port 8080 -Name "Backend Spring Boot"
# Clean up residual mvn processes related to this project
$mvnProcs = Get-Process -Name "mvn" -ErrorAction SilentlyContinue
foreach ($p in $mvnProcs) {
    try { Stop-Process -Id $p.Id -Force; Write-Ok "Stopped residual mvn process (PID $($p.Id))" }
    catch { Write-Warn2 "Failed to stop mvn PID $($p.Id): $_" }
}

if ($StopRedis) {
    Write-Step "Stop Redis container (pms-redis)"
    docker stop pms-redis 2>$null | Out-Null
    Write-Ok "Redis container stopped"
} else {
    Write-Step "Keep Redis container running (use -StopRedis switch to stop it)"
}

Write-Host "`n================================================" -ForegroundColor Green
Write-Host "  Stop completed" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
