# Step 3: Precise VIEW annotation verification
$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$docContent = Get-Content $docFile -Encoding UTF8

# Extract table name and its object type from document
$docTableTypes = @{}
$currentTable = $null
foreach ($line in $docContent) {
    if ($line -match '^### (\S+)$') {
        $tname = $Matches[1]
        if ($tname -notmatch '^\d') {
            $currentTable = $tname
        }
    } elseif ($line -match '^\*\*对象类型\*\*：(.+)$') {
        if ($currentTable) {
            $typeStr = $Matches[1].Trim()
            if ($typeStr -like '*VIEW*') {
                $docTableTypes[$currentTable] = 'VIEW'
            } else {
                $docTableTypes[$currentTable] = 'BASE TABLE'
            }
        }
    }
}

# Get DB views
$dbLines = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT TABLE_NAME, TABLE_TYPE FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'dppms_d365' AND NOT (TABLE_NAME LIKE 'temp_%' OR TABLE_NAME LIKE 'tmp_%' OR TABLE_NAME LIKE '%_temp' OR TABLE_NAME LIKE '%_tmp') ORDER BY TABLE_NAME" 2>$null

$dbTableTypes = @{}
$dbLines | Select-Object -Skip 1 | ForEach-Object {
    $parts = $_.Trim() -split '\s+'
    $name = $parts[0]
    $type = $parts[1]
    if ($type -eq 'VIEW') {
        $dbTableTypes[$name] = 'VIEW'
    } else {
        $dbTableTypes[$name] = 'BASE TABLE'
    }
}

# Compare
$docViewCount = ($docTableTypes.GetEnumerator() | Where-Object { $_.Value -eq 'VIEW' }).Count
$dbViewCount = ($dbTableTypes.GetEnumerator() | Where-Object { $_.Value -eq 'VIEW' }).Count

Write-Output "Doc VIEW count: $docViewCount"
Write-Output "DB VIEW count: $dbViewCount"
Write-Output ""

# Check for mismatches
Write-Output "=== Type mismatches (DB type != Doc type) ==="
$mismatchCount = 0
foreach ($t in ($dbTableTypes.Keys | Sort-Object)) {
    $dbType = $dbTableTypes[$t]
    $docType = $docTableTypes[$t]
    if ($dbType -ne $docType) {
        Write-Output "[MISMATCH] $t : DB=$dbType, Doc=$docType"
        $mismatchCount++
    }
}
if ($mismatchCount -eq 0) {
    Write-Output "None - all types match!"
}

# Check for tables without type annotation
Write-Output ""
Write-Output "=== Tables without type annotation in doc ==="
$noAnnotation = 0
foreach ($t in ($dbTableTypes.Keys | Sort-Object)) {
    if (-not $docTableTypes.ContainsKey($t)) {
        Write-Output "[MISSING] $t"
        $noAnnotation++
    }
}
if ($noAnnotation -eq 0) {
    Write-Output "None - all tables have type annotations!"
}
