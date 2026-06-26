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
    }
    # Only match the exact object type line
    if ($currentTable -and $line -match '^\*\*对象类型\*\*：(.+)$') {
        $typeStr = $Matches[1].Trim()
        if ($typeStr -eq 'VIEW') {
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

$docViewCount = 0
$docBaseCount = 0
foreach ($kv in $docTableTypes.GetEnumerator()) {
    if ($kv.Value -eq 'VIEW') { $docViewCount++ }
    else { $docBaseCount++ }
}

$dbViewCount = 0
$dbBaseCount = 0
foreach ($kv in $dbTableTypes.GetEnumerator()) {
    if ($kv.Value -eq 'VIEW') { $dbViewCount++ }
    else { $dbBaseCount++ }
}

Write-Output "Doc: VIEW=$docViewCount, BASE TABLE=$docBaseCount, Total annotated=$($docTableTypes.Count)"
Write-Output "DB:  VIEW=$dbViewCount, BASE TABLE=$dbBaseCount, Total=$($dbTableTypes.Count)"
Write-Output ""

# Check for type mismatches
$mismatchCount = 0
$noAnnotation = 0
foreach ($t in ($dbTableTypes.Keys | Sort-Object)) {
    $dbType = $dbTableTypes[$t]
    if ($docTableTypes.ContainsKey($t)) {
        $docType = $docTableTypes[$t]
        if ($dbType -ne $docType) {
            Write-Output "[MISMATCH] $t : DB=$dbType, Doc=$docType"
            $mismatchCount++
        }
    } else {
        Write-Output "[NO_ANNOTATION] $t : DB=$dbType"
        $noAnnotation++
    }
}
Write-Output ""
Write-Output "Mismatches: $mismatchCount"
Write-Output "No annotation: $noAnnotation"
