<#
.SYNOPSIS
    启动后端 pms-admin 服务（spring-boot:run）
.DESCRIPTION
    底层已迁移到 yudao framework，运行前请确保以下模块已 install 到本地仓库：
      - yudao-dependencies / yudao-framework（底层框架）
      - pms-common（兼容层）
      - pms-system 及其他业务模块
    首次运行或修改底层模块后，请先执行 .\rebuild-common.ps1。

    端口 / 凭据 / JDK 路径等配置统一在 env.ps1 中管理。
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\start-backend.ps1
#>

# 引入统一配置（端口 / 凭据 / JDK 路径）并设置环境变量
. "$PSScriptRoot\env.ps1"
Set-PmsEnvironment

Write-Host "Starting pms-admin (spring-boot:run) on port $BackendPort..." -ForegroundColor Cyan
Write-Host "  MySQL  : localhost:$MysqlPort/$MysqlDatabase" -ForegroundColor DarkGray
Write-Host "  Redis   : localhost:$RedisPort" -ForegroundColor DarkGray
Write-Host "  Backend : localhost:$BackendPort" -ForegroundColor DarkGray
Write-Host "NOTE: Skips 'mvn install'. If you changed yudao-framework / pms-common / pms-system etc.," -ForegroundColor Yellow
Write-Host "      run '.\rebuild-common.ps1' first, then re-run this script." -ForegroundColor Yellow

# Maven 选项：默认使用系统 Maven settings（~/.m2/settings.xml）。
# 如需使用项目自带沙箱专用配置（仅 Linux 沙箱），可手动改为：
#   $MvnArgs = @("-s", "maven-settings.sandbox.xml") + $MvnArgs
# 注意：maven-settings.sandbox.xml 内含 127.0.0.1:18080 代理与 Linux 本地仓库路径，
# 在 Windows 或其他无该代理的环境下会因 Connection refused 导致依赖无法下载。
$MvnArgs = @("spring-boot:run", "-pl", "pms-admin", "-Dmaven.test.skip=true",
             "-Dspring-boot.run.jvmArguments=-Dotel.sdk.disabled=true -Dotel.traces.exporter=none -Dotel.metrics.exporter=none -Dotel.logs.exporter=none")

& mvn @MvnArgs
