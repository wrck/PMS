# Use simple string matching to avoid encoding issues
$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$lines = [System.IO.File]::ReadAllLines($docFile, [System.Text.Encoding]::UTF8)

$docTableTypes = @{}
$currentTable = $null

for ($i = 0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    if ($line.StartsWith("### ") -and $line.Length -gt 4) {
        $tname = $line.Substring(4).Trim()
        if ($tname -notmatch '^\d') {
            $currentTable = $tname
        }
    }
    if ($currentTable) {
        if ($line.Contains("VIEW") -and $line.Contains("**") -and $line.Contains("**")) {
            # Check if this is the object type line
            $idx = $line.IndexOf("**")
            $idx2 = $line.IndexOf("**", $idx + 2)
            if ($idx -ge 0 -and $idx2 -gt $idx) {
                $between = $line.Substring($idx, $idx2 - $idx + 2)
                # This should be **something** format
                $afterColon = $line.Substring($idx2 + 2).Trim()
                if ($afterColon.StartsWith(":") -or $afterColon.StartsWith("\uFF1A")) {
                    $typePart = $afterColon.Substring(1).Trim()
                    if ($typePart -eq "VIEW") {
                        $docTableTypes[$currentTable] = 'VIEW'
                        $currentTable = $null
                    } elseif ($typePart.Contains("BASE TABLE")) {
                        $docTableTypes[$currentTable] = 'BASE TABLE'
                        $currentTable = $null
                    }
                }
            }
        }
    }
}

# Count
$docViewCount = 0
$docBaseCount = 0
foreach ($kv in $docTableTypes.GetEnumerator()) {
    if ($kv.Value -eq 'VIEW') { $docViewCount++ }
    else { $docBaseCount++ }
}

Write-Output "Doc: VIEW=$docViewCount, BASE TABLE=$docBaseCount, Total=$($docTableTypes.Count)"

# Get DB table types
$dbResult = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT TABLE_NAME, TABLE_TYPE FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'dppms_d365' AND NOT (TABLE_NAME LIKE 'temp_%' OR TABLE_NAME LIKE 'tmp_%' OR TABLE_NAME LIKE '%_temp' OR TABLE_NAME LIKE '%_tmp') ORDER BY TABLE_NAME" 2>$null

$dbTableTypes = @{}
$dbResult | Select-Object -Skip 1 | ForEach-Object {
    $parts = $_.Trim() -split '\s+'
    $name = $parts[0]
    $type = $parts[1]
    $dbTableTypes[$name] = $type
}

$dbViewCount = 0
$dbBaseCount = 0
foreach ($kv in $dbTableTypes.GetEnumerator()) {
    if ($kv.Value -eq 'VIEW') { $dbViewCount++ }
    else { $dbBaseCount++ }
}
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
