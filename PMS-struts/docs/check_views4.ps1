$docFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$lines = [System.IO.File]::ReadAllLines($docFile, [System.Text.Encoding]::UTF8)

$viewCount = 0
$baseCount = 0
$noType = 0
$currentTable = $null
$hasType = $false
$tables = @()

for ($i = 0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    if ($line -match '^### (\S+)$') {
        $tname = $Matches[1]
        if ($tname -notmatch '^\d') {
            if ($currentTable -and -not $hasType) {
                $noType++
                $tables += "$currentTable (NO_TYPE)"
            }
            $currentTable = $tname
            $hasType = $false
        }
    }
    if ($currentTable -and -not $hasType -and $line -match 'VIEW') {
        $viewCount++
        $hasType = $true
    } elseif ($currentTable -and -not $hasType -and $line -match 'BASE TABLE') {
        $baseCount++
        $hasType = $true
    }
}
# Check last table
if ($currentTable -and -not $hasType) {
    $noType++
    $tables += "$currentTable (NO_TYPE)"
}

Write-Output "VIEW annotated: $viewCount"
Write-Output "BASE TABLE annotated: $baseCount"
Write-Output "No type annotation: $noType"
Write-Output "Total annotated: $($viewCount + $baseCount)"
if ($noType -gt 0) {
    Write-Output ""
    Write-Output "Tables without type:"
    $tables | ForEach-Object { Write-Output $_ }
}
