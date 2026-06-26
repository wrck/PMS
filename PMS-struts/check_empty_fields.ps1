$filepath = 'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md'
$lines = Get-Content $filepath -Encoding UTF8
$currentTable = ''
$suspiciousFields = [System.Collections.ArrayList]::new()
$totalFields = 0

# Collect all unique business meanings for analysis
$allMeanings = [System.Collections.Generic.HashSet[string]]::new()

foreach ($line in $lines) {
    if ($line -match '^###\s+(.+)') {
        $currentTable = $Matches[1].Trim()
        continue
    }

    if ($line -match '^\|\s*\S+.*\|\s*(YES|NO)\s*\|') {
        $totalFields++
        $cols = $line -split '\|'
        $businessMeaning = $cols[-2].Trim()
        $fieldName = $cols[1].Trim()

        [void]$allMeanings.Add($businessMeaning)

        # Check for empty, placeholder, or suspicious values
        if ($businessMeaning -eq '' -or $businessMeaning -eq '-' -or $businessMeaning -eq '--' -or $businessMeaning -eq 'N/A' -or $businessMeaning -eq 'n/a' -or $businessMeaning -eq 'TBD' -or $businessMeaning -eq 'TODO') {
            [void]$suspiciousFields.Add([PSCustomObject]@{
                Table = $currentTable
                Field = $fieldName
                Meaning = "EMPTY_OR_PLACEHOLDER:$businessMeaning"
            })
        }
    }
}

Write-Output "Total field rows: $totalFields"
Write-Output "Suspicious fields count: $($suspiciousFields.Count)"
Write-Output ""

# Show all unique business meanings that are short (<=5 chars) to find placeholders
Write-Output "--- Short business meanings (<=5 chars, unique) ---"
$shortMeanings = $allMeanings | Where-Object { $_.Length -le 5 -and $_ -ne '' } | Sort-Object
foreach ($m in $shortMeanings) {
    Write-Output "  '$m'"
}

Write-Output ""
Write-Output "--- Suspicious fields ---"
$i = 1
foreach ($f in $suspiciousFields) {
    Write-Output "$i | $($f.Table) | $($f.Field) | $($f.Meaning)"
    $i++
}
