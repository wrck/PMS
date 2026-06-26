$dbLines = & "E:\mysql-5.7.26-winx64\bin\mysql.exe" -u root -p"!Q@W3e4r" -h localhost -e "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'dppms_d365' AND NOT (TABLE_NAME LIKE 'temp_%' OR TABLE_NAME LIKE 'tmp_%' OR TABLE_NAME LIKE '%_temp' OR TABLE_NAME LIKE '%_tmp') ORDER BY TABLE_NAME" 2>$null
$dbSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$dbLines | Select-Object -Skip 1 | ForEach-Object { $dbSet.Add($_.Trim()) | Out-Null }

$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$docLines = Get-Content $docFile -Encoding UTF8
$docSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
foreach ($line in $docLines) {
    if ($line -match '^### (\S+)$') {
        $tname = $Matches[1]
        if ($tname -notmatch '^\d') {
            $docSet.Add($tname) | Out-Null
        }
    }
}

$inDocNotInDb = [System.Collections.ArrayList]::new()
foreach ($t in $docSet) {
    if (-not $dbSet.Contains($t)) { [void]$inDocNotInDb.Add($t) }
}

$inDbNotInDoc = [System.Collections.ArrayList]::new()
foreach ($t in $dbSet) {
    if (-not $docSet.Contains($t)) { [void]$inDbNotInDoc.Add($t) }
}

Write-Output "=== 统计 ==="
Write-Output "数据库表总数: $($dbSet.Count)"
Write-Output "文档表总数: $($docSet.Count)"
Write-Output ""
Write-Output "=== 文档有但数据库没有的表 ($($inDocNotInDb.Count)) ==="
$inDocNotInDb | Sort-Object | ForEach-Object { Write-Output $_ }
Write-Output ""
Write-Output "=== 数据库有但文档没有的表 ($($inDbNotInDoc.Count)) ==="
$inDbNotInDoc | Sort-Object | ForEach-Object { Write-Output $_ }
