$tables = @('act_ru_task', 'fb_shipment', 'ehr_employee', 'pm_project', 'fnd_user_info')
$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$lines = [System.IO.File]::ReadAllLines($docFile, [System.Text.Encoding]::UTF8)

foreach ($table in $tables) {
    Write-Output "=== $table ==="

    # Get DB columns
    $dbResult = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_NAME = '$table' ORDER BY ORDINAL_POSITION" 2>$null

    $dbCols = @{}
    $dbColOrder = @()
    $dbResult | Select-Object -Skip 1 | ForEach-Object {
        $parts = $_.Trim() -split '\t'
        if ($parts.Count -ge 5) {
            $colName = $parts[0]
            $colType = $parts[1]
            $nullable = $parts[2]
            $default = $parts[3]
            $key = $parts[4]
            $dbCols[$colName] = @{type=$colType; nullable=$nullable; default=$default; key=$key}
            $dbColOrder += $colName
        }
    }

    # Get doc columns
    $docCols = @{}
    $docColOrder = @()
    $inSection = $false
    $inFieldTable = $false
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        if ($line -match "^### $table`$") {
            $inSection = $true
            continue
        }
        if ($inSection -and $line -match '^### ') {
            break
        }
        if ($inSection -and $line -match '^\| (\S+) \| (\S+) \| (\S+) \|') {
            $colName = $Matches[1]
            if ($colName -ne '字段名' -and $colName -ne '---') {
                $colType = $Matches[2]
                $nullable = $Matches[3]
                $docCols[$colName] = @{type=$colType; nullable=$nullable}
                $docColOrder += $colName
            }
        }
    }

    Write-Output "DB columns: $($dbColOrder.Count)"
    Write-Output "Doc columns: $($docColOrder.Count)"

    # Check for missing/extra columns
    $dbSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $dbColOrder | ForEach-Object { $dbSet.Add($_) | Out-Null }
    $docSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $docColOrder | ForEach-Object { $docSet.Add($_) | Out-Null }

    $inDocNotInDb = @()
    $inDbNotInDoc = @()
    foreach ($c in $docColOrder) { if (-not $dbSet.Contains($c)) { $inDocNotInDb += $c } }
    foreach ($c in $dbColOrder) { if (-not $docSet.Contains($c)) { $inDbNotInDoc += $c } }

    if ($inDocNotInDb.Count -gt 0) { Write-Output "  Doc has but DB doesn't: $($inDocNotInDb -join ', ')" }
    if ($inDbNotInDoc.Count -gt 0) { Write-Output "  DB has but Doc doesn't: $($inDbNotInDoc -join ', ')" }

    # Check type mismatches
    foreach ($c in $dbColOrder) {
        if ($docCols.ContainsKey($c) -and $dbCols.ContainsKey($c)) {
            $dbType = $dbCols[$c].type
            $docType = $docCols[$c].type
            if ($dbType -ne $docType) {
                Write-Output "  [TYPE_MISMATCH] $c : DB=$dbType, Doc=$docType"
            }
            $dbNull = $dbCols[$c].nullable
            $docNull = $docCols[$c].nullable
            if ($dbNull -ne $docNull) {
                Write-Output "  [NULLABLE_MISMATCH] $c : DB=$dbNull, Doc=$docNull"
            }
        }
    }

    Write-Output ""
}
