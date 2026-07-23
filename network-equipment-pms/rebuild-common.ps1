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
.NOTES
    Usage:
      powershell -ExecutionPolicy Bypass -File .\rebuild-common.ps1
#>

$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.9"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 项目根目录存在 maven-settings.xml 时自动启用（本地仓库 + 镜像配置）
$MvnArgs = @("-Dmaven.test.skip=true", "-q")
if (Test-Path "$PSScriptRoot\maven-settings.xml") {
    $MvnArgs = @("-s", "maven-settings.xml") + $MvnArgs
    Write-Host "Using maven-settings.xml" -ForegroundColor DarkGray
}

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
