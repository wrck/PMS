<#
.SYNOPSIS
    重建底层公共模块（yudao framework + pms-common + 依赖链）
.DESCRIPTION
    底层迁移到 yudao framework 后，构建顺序必须先 yudao 后 PMS：
      1. yudao-dependencies（BOM）
      2. yudao-framework 全部子模块（pms-common 直接依赖）
      3. pms-common（兼容层 Result/BaseEntity/BusinessException 等）
      4. 依赖 pms-common 的业务模块：pms-system、pms-notification、pms-lowcode、pms-admin
    使用 -am（also make）自动构建上游依赖。

    端口 / 凭据 / JDK 路径等配置统一在 env.ps1 中管理。
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\rebuild-common.ps1
#>

# 引入统一配置（端口 / 凭据 / JDK 路径）
. "$PSScriptRoot\env.ps1"

$env:JAVA_HOME = $JavaHome
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Maven 选项：
#   - 默认使用系统 Maven settings（~/.m2/settings.xml），用户可在其中配置本地仓库 / 镜像 / 代理。
#   - 如需使用项目自带沙箱专用配置（仅 Linux 沙箱环境），可手动改为：
#       $MvnArgs = @("-s", "maven-settings.sandbox.xml") + $MvnArgs
#     注意：maven-settings.sandbox.xml 内含 127.0.0.1:18080 代理与 Linux 本地仓库路径，
#     在 Windows 或其他无该代理的环境下会因 Connection refused 导致依赖无法下载。
$MvnArgs = @("-Dmaven.test.skip=true", "-q")

# ---------- 1. yudao framework 底层模块 ----------
Write-Host "Rebuilding yudao framework (yudao-dependencies + yudao-framework)..."
& mvn install -pl yudao-dependencies,yudao-framework -am @MvnArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "yudao framework build failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

# ---------- 2. pms-common 兼容层 ----------
Write-Host "Rebuilding pms-common..."
& mvn install -pl pms-common -am @MvnArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "pms-common build failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

# ---------- 3. 依赖 pms-common 的业务模块 ----------
Write-Host "Rebuilding pms-system, pms-notification, pms-lowcode, pms-admin..."
& mvn install -pl pms-system,pms-notification,pms-lowcode,pms-admin -am @MvnArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "Business modules build failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "All modules built successfully." -ForegroundColor Green
