$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.9"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Write-Host "Rebuilding pms-common..."
mvn install -pl pms-common -am "-Dmaven.test.skip=true" -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "pms-common build failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
Write-Host "Rebuilding pms-system, pms-notification, pms-lowcode, pms-admin..."
mvn install -pl pms-system,pms-notification,pms-lowcode,pms-admin -am "-Dmaven.test.skip=true" -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "Modules build failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
Write-Host "All modules built successfully."
