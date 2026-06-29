# PMS DB Table Compare Script
$mysqlExe = "E:\mysql-5.7.26-winx64\bin\mysql.exe"
$mysqlPwd = "!Q@W3e4r"
$outFile = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\compare_result.txt"

# Extract doc tables
$docPath = "d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
$docContent = Get-Content $docPath -Encoding utf8
$docTables = @()
foreach ($line in $docContent) {
    if ($line -match '^### ([a-z][a-z0-9_]+)$') {
        $docTables += $Matches[1]
    }
}

# Query all databases
$databases = @('dppms','dppms_prob','dppms_d365','dppms_mvc','dppms_mo','dppms_org','dpehr','dpspms','dpbms')
$dbSchema = @{}

foreach ($db in $databases) {
    $tables = & $mysqlExe -h 127.0.0.1 -u root -p"$mysqlPwd" -N -e "SELECT TABLE_NAME, TABLE_TYPE FROM information_schema.TABLES WHERE TABLE_SCHEMA = '$db' AND TABLE_NAME NOT LIKE 'temp_%%' AND TABLE_NAME NOT LIKE 'tmp_%%' AND TABLE_NAME NOT LIKE '%%_temp' AND TABLE_NAME NOT LIKE '%%_tmp' ORDER BY TABLE_NAME;" 2>$null
    $dbSchema[$db] = @{}
    foreach ($row in $tables) {
        $parts = $row -split "`t"
        if ($parts.Count -ge 2) {
            $tname = $parts[0].Trim()
            $ttype = $parts[1].Trim()
            if ($tname) { $dbSchema[$db][$tname] = $ttype }
        }
    }
}

$lines = @()
$lines += "============================================================"
$lines += "PMS Database Table Comparison Report"
$lines += "============================================================"
$lines += ""

# Section 1: Per-database stats
$lines += "=== 1. Table Count per Database ==="
$lines += ""
foreach ($db in $databases) {
    $count = $dbSchema[$db].Count
    $baseCount = 0
    $viewCount = 0
    foreach ($k in $dbSchema[$db].Keys) {
        if ($dbSchema[$db][$k] -eq 'BASE TABLE') { $baseCount++ }
        else { $viewCount++ }
    }
    $lines += "  $db : $count (BASE TABLE: $baseCount, VIEW: $viewCount)"
}
$lines += ""

# Section 2: Doc vs dppms_d365
$lines += "=== 2. Document vs dppms_d365 ==="
$lines += ""
$d365Set = @{}
foreach ($k in $dbSchema['dppms_d365'].Keys) { $d365Set[$k] = $true }
$docSet = @{}
foreach ($t in $docTables) { $docSet[$t] = $true }

$inDocNotD365 = @()
foreach ($t in $docSet.Keys) { if (-not $d365Set.ContainsKey($t)) { $inDocNotD365 += $t } }
$inD365NotDoc = @()
foreach ($t in $d365Set.Keys) { if (-not $docSet.ContainsKey($t)) { $inD365NotDoc += $t } }

$lines += "  Doc table count: $($docSet.Count)"
$lines += "  dppms_d365 table count: $($d365Set.Count)"
$lines += "  In doc but NOT in dppms_d365: $($inDocNotD365.Count)"
foreach ($t in ($inDocNotD365 | Sort-Object)) { $lines += "    - $t" }
$lines += "  In dppms_d365 but NOT in doc: $($inD365NotDoc.Count)"
foreach ($t in ($inD365NotDoc | Sort-Object)) { $lines += "    - $t" }
$lines += ""

# Section 3: Doc tables distribution
$lines += "=== 3. Document Tables Distribution Across Databases ==="
$lines += ""
$onlyInD365 = @()
$inMultipleDb = @()
foreach ($t in ($docSet.Keys | Sort-Object)) {
    $foundIn = @()
    foreach ($db in $databases) {
        if ($dbSchema[$db].ContainsKey($t)) { $foundIn += $db }
    }
    if ($foundIn.Count -eq 1 -and $foundIn[0] -eq 'dppms_d365') {
        $onlyInD365 += $t
    } elseif ($foundIn.Count -gt 1) {
        $inMultipleDb += "$t -> $($foundIn -join ', ')"
    }
}
$lines += "  Tables ONLY in dppms_d365 (not in any other DB): $($onlyInD365.Count)"
foreach ($t in $onlyInD365) { $lines += "    - $t" }
$lines += ""
$lines += "  Tables in multiple databases: $($inMultipleDb.Count)"
foreach ($t in $inMultipleDb) { $lines += "    - $t" }
$lines += ""

# Section 4: Tables in other DBs but not in doc/d365
$lines += "=== 4. Tables in Other DBs but NOT in dppms_d365 (Missing from Doc) ==="
$lines += ""
foreach ($db in $databases) {
    if ($db -eq 'dppms_d365') { continue }
    $extraTables = @()
    foreach ($t in $dbSchema[$db].Keys) {
        if (-not $d365Set.ContainsKey($t)) { $extraTables += $t }
    }
    $extraSorted = $extraTables | Sort-Object
    $lines += "  $db unique tables (not in dppms_d365): $($extraSorted.Count)"
    foreach ($t in $extraSorted) { $lines += "    - $t ($($dbSchema[$db][$t]))" }
    $lines += ""
}

# Section 5: Cross-DB unique table aggregation
$lines += "=== 5. Cross-Database Unique Table Names ==="
$lines += ""
$allTables = @{}
foreach ($db in $databases) {
    foreach ($t in $dbSchema[$db].Keys) {
        if (-not $allTables.ContainsKey($t)) { $allTables[$t] = @() }
        $allTables[$t] += $db
    }
}
$lines += "  Total unique table names across 9 databases: $($allTables.Count)"
$lines += "  Of which in document: $($docSet.Count)"
$notInDocCount = $allTables.Count - $docSet.Count
$lines += "  Of which NOT in document: $notInDocCount"
$lines += ""

$notInDoc = @()
foreach ($t in $allTables.Keys) {
    if (-not $docSet.ContainsKey($t)) { $notInDoc += $t }
}
$lines += "  Tables NOT in document ($($notInDoc.Count)):"
foreach ($t in ($notInDoc | Sort-Object)) {
    $lines += "    - $t (in: $($allTables[$t] -join ', '))"
}

$lines | Out-File $outFile -Encoding utf8
Write-Output "Report saved to $outFile"
Write-Output "Done!"
