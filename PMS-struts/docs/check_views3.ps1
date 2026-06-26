# Step 3: Precise VIEW annotation verification - v3
$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$lines = [System.IO.File]::ReadAllLines($docFile, [System.Text.Encoding]::UTF8)

$docTableTypes = @{}
$currentTable = $null
for ($i = 0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    if ($line -match '^### (\S+)$') {
        $tname = $Matches[1]
        if ($tname -notmatch '^\d') {
            $currentTable = $tname
        }
    } elseif ($currentTable -and $line -match '\*\*对象类型\*\*') {
        if ($line -match 'VIEW') {
            $docTableTypes[$currentTable] = 'VIEW'
        } else {
            $docTableTypes[$currentTable] = 'BASE TABLE'
        }
        $currentTable = $null
    }
}

# Get DB table types
$dbResult = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT TABLE_NAME, TABLE_TYPE FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'dppms_d365' AND NOT (TABLE_NAME LIKE 'temp_%' OR TABLE_NAME LIKE 'tmp_%' OR TABLE_NAME LIKE '%_temp' OR TABLE_NAME LIKE '%_tmp') ORDER BY TABLE_NAME" 2>$null

$dbTableTypes = @{}
$dbResult | Select-Object -Skip 1 | ForEach-Object {
    $parts = $_.Trim() -split '\s+'
    $name = $parts[0]
    $type = $parts[1]
    if ($type -eq 'VIEW') {
        $dbTableTypes[$name] = 'VIEW'
    } else {
        $dbTableTypes[$name] = 'BASE TABLE'
    }
}

$docViewCount = ($docTableTypes.GetEnumerator() | Where-Object { $_.Value -eq 'VIEW' }).Count
$dbViewCount = ($dbTableTypes.GetEnumerator() | Where-Object { $_.Value -eq 'VIEW' }).Count
$docBaseCount = ($docTableTypes.GetEnumerator() | Where-Object { $_.Value -eq 'BASE TABLE' }).Count
$dbBaseCount = ($dbTableTypes.GetEnumerator() | Where-Object { $_.Value -eq 'BASE TABLE' }).Count

Write-Output "Doc VIEW count: $docViewCount"
Write-Output "DB VIEW count: $dbViewCount"
Write-Output "Doc BASE TABLE count: $docBaseCount"
Write-Output "DB BASE TABLE count: $dbBaseCount"
Write-Output "Doc total annotated: $($docTableTypes.Count)"
Write-Output "DB total: $($dbTableTypes.Count)"
Write-Output ""

# Check for type mismatches
Write-Output "=== Type mismatches (DB type != Doc type) ==="
$mismatchCount = 0
foreach ($t in ($dbTableTypes.Keys | Sort-Object)) {
    $dbType = $dbTableTypes[$t]
    if ($docTableTypes.ContainsKey($t)) {
        $docType = $docTableTypes[$t]
        if ($dbType -ne $docType) {
            Write-Output "[MISMATCH] $t : DB=$dbType, Doc=$docType"
            $mismatchCount++
        }
    } else {
        Write-Output "[NO_ANNOTATION] $t : DB=$dbType, Doc=NONE"
        $mismatchCount++
    }
}
if ($mismatchCount -eq 0) {
    Write-Output "None - all types match!"
}
