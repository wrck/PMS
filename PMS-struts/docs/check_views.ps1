# Step 3: VIEW type annotation verification
$dbLines = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT TABLE_NAME, TABLE_TYPE FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_TYPE = 'VIEW' AND NOT (TABLE_NAME LIKE 'temp_%' OR TABLE_NAME LIKE 'tmp_%' OR TABLE_NAME LIKE '%_temp' OR TABLE_NAME LIKE '%_tmp') ORDER BY TABLE_NAME" 2>$null

$dbViews = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$dbLines | Select-Object -Skip 1 | ForEach-Object {
    $parts = $_.Trim() -split '\s+'
    $dbViews.Add($parts[0]) | Out-Null
}

Write-Output "=== Database VIEWs ($($dbViews.Count)) ==="
$dbViews | Sort-Object | ForEach-Object { Write-Output $_ }

$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$docContent = Get-Content $docFile -Encoding UTF8 -Raw

# Check each DB VIEW has "**对象类型**：VIEW" annotation
Write-Output ""
Write-Output "=== Checking VIEW annotations ==="
foreach ($v in ($dbViews | Sort-Object)) {
    # Find the section for this view
    $pattern = "### $v`r?`n.*?(?=###|\z)"
    $match = [regex]::Match($docContent, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) {
        $section = $match.Value
        if ($section -match '\*\*对象类型\*\*：VIEW') {
            Write-Output "[OK] $v - VIEW annotation found"
        } else {
            Write-Output "[ERROR] $v - Missing VIEW annotation!"
        }
    } else {
        Write-Output "[WARN] $v - Section not found in document"
    }
}

# Check if any BASE TABLE is incorrectly annotated as VIEW
Write-Output ""
Write-Output "=== Checking for incorrectly annotated BASE TABLEs ==="
$dbBaseLines = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_TYPE = 'BASE TABLE' AND NOT (TABLE_NAME LIKE 'temp_%' OR TABLE_NAME LIKE 'tmp_%' OR TABLE_NAME LIKE '%_temp' OR TABLE_NAME LIKE '%_tmp') ORDER BY TABLE_NAME" 2>$null

$dbBaseTables = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$dbBaseLines | Select-Object -Skip 1 | ForEach-Object { $dbBaseTables.Add($_.Trim()) | Out-Null }

foreach ($t in ($dbBaseTables | Sort-Object)) {
    $pattern = "### $t`r?`n.*?(?=###|\z)"
    $match = [regex]::Match($docContent, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) {
        $section = $match.Value
        if ($section -match '\*\*对象类型\*\*：VIEW') {
            Write-Output "[ERROR] $t - BASE TABLE incorrectly annotated as VIEW!"
        }
    }
}
Write-Output "=== Done ==="
