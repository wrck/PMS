$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.9"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3307/dpspms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:MYSQL_USER = "root"
$env:MYSQL_PASSWORD = "!Q@W3e4r"
$env:REDIS_PASSWORD = ""
$env:JWT_SECRET = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEtMjU2LWJpdC11c2FnZQ=="
$env:APP_ENCRYPT_KEY = "MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY="
Write-Host "Step 1: Installing changed modules (pms-system, pms-admin)..."
mvn install "-Dmaven.test.skip=true" -pl pms-system,pms-notification,pms-admin -am -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "Module install failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
Write-Host "Step 2: Starting pms-admin..."
mvn spring-boot:run -pl pms-admin "-Dmaven.test.skip=true" "-Dspring-boot.run.jvmArguments=-Dotel.sdk.disabled=true -Dotel.traces.exporter=none -Dotel.metrics.exporter=none -Dotel.logs.exporter=none"
