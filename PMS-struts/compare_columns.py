import re
import subprocess
import sys

# Step 1: Query database for column counts
mysql_path = r"E:\mysql-8.0.38-winx64\bin\mysql.exe"
db_result = subprocess.run(
    [mysql_path, "-u", "root", "-p!Q@W3e4r", "-s", "-N", "-e",
     "SELECT TABLE_NAME, COUNT(*) as column_count FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_NAME NOT REGEXP '^(temp_|tmp_)' AND TABLE_NAME NOT REGEXP '(_temp$|_tmp$)' GROUP BY TABLE_NAME ORDER BY TABLE_NAME",
     "dppms_d365"],
    capture_output=True, text=True, encoding='utf-8'
)

db_counts = {}
for line in db_result.stdout.strip().split('\n'):
    if line.strip():
        parts = line.strip().split('\t')
        if len(parts) == 2:
            db_counts[parts[0].strip()] = int(parts[1].strip())

# Step 2: Parse document for column counts
doc_path = r"d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md"
with open(doc_path, 'r', encoding='utf-8') as f:
    content = f.read()

lines = content.split('\n')
doc_counts = {}
current_table = None
in_field_section = False
field_count = 0

for line in lines:
    # Match table header
    m = re.match(r'^### (\S+)', line)
    if m:
        if current_table and field_count > 0:
            doc_counts[current_table] = field_count
        current_table = m.group(1)
        in_field_section = False
        field_count = 0
    elif '字段列表' in line and '**' in line:
        in_field_section = True
        field_count = 0
    elif in_field_section and line.startswith('|'):
        # Skip header row and separator row
        if re.match(r'^\|[-\s|:]+\|$', line):
            continue
        if re.match(r'^\|\s*字段名\s*\|', line):
            continue
        # Count data rows
        if re.match(r'^\| .+ \|', line):
            field_count += 1
    elif in_field_section and line.startswith('**') and '字段' not in line:
        in_field_section = False

# Don't forget the last table
if current_table and field_count > 0:
    doc_counts[current_table] = field_count

# Step 3: Compare
all_tables = sorted(set(list(db_counts.keys()) + list(doc_counts.keys())))

match_count = 0
mismatch_list = []
only_in_db = []
only_in_doc = []

for table in all_tables:
    db_c = db_counts.get(table)
    doc_c = doc_counts.get(table)

    if db_c is not None and doc_c is not None:
        if db_c == doc_c:
            match_count += 1
        else:
            mismatch_list.append((table, doc_c, db_c))
    elif db_c is not None and doc_c is None:
        only_in_db.append((table, db_c))
    elif db_c is None and doc_c is not None:
        only_in_doc.append((table, doc_c))

# Step 4: Output report
print("=" * 80)
print("数据字典 vs 数据库 字段数对比报告")
print("=" * 80)
print()
print(f"数据库表数量: {len(db_counts)}")
print(f"文档表数量: {len(doc_counts)}")
print(f"字段数完全一致的表数量: {match_count}")
print(f"字段数不一致的表数量: {len(mismatch_list)}")
print(f"仅存在于数据库的表: {len(only_in_db)}")
print(f"仅存在于文档的表: {len(only_in_doc)}")
print()

total_compared = match_count + len(mismatch_list)
if total_compared > 0:
    consistency_rate = match_count / total_compared * 100
else:
    consistency_rate = 0
print(f"总体一致率: {consistency_rate:.1f}% ({match_count}/{total_compared})")
print()

if mismatch_list:
    print("-" * 80)
    print("字段数不一致的表:")
    print(f"{'表名':<55} {'文档':>6} {'数据库':>6} {'差异':>6}")
    print("-" * 80)
    for table, doc_c, db_c in mismatch_list:
        diff = doc_c - db_c
        diff_str = f"+{diff}" if diff > 0 else str(diff)
        print(f"{table:<55} {doc_c:>6} {db_c:>6} {diff_str:>6}")
    print()

if only_in_db:
    print("-" * 80)
    print("仅存在于数据库的表（文档中缺失）:")
    for table, db_c in only_in_db:
        print(f"  {table} (数据库字段数: {db_c})")
    print()

if only_in_doc:
    print("-" * 80)
    print("仅存在于文档的表（数据库中缺失）:")
    for table, doc_c in only_in_doc:
        print(f"  {table} (文档字段数: {doc_c})")
    print()

# Step 5: For mismatched tables, find specific field differences
if mismatch_list:
    print("=" * 80)
    print("不一致表的详细字段差异:")
    print("=" * 80)

    for table, doc_c, db_c in mismatch_list:
        print(f"\n### {table} (文档: {doc_c}, 数据库: {db_c})")

        # Get database columns for this table
        db_col_result = subprocess.run(
            [mysql_path, "-u", "root", "-p!Q@W3e4r", "-s", "-N", "-e",
             f"SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_NAME = '{table}' ORDER BY ORDINAL_POSITION",
             "dppms_d365"],
            capture_output=True, text=True, encoding='utf-8'
        )
        db_columns = set()
        for line in db_col_result.stdout.strip().split('\n'):
            if line.strip():
                db_columns.add(line.strip())

        # Get document columns for this table
        # Find the table section in the document
        doc_columns = set()
        in_section = False
        in_fields = False
        for line in lines:
            if re.match(r'^### ' + re.escape(table) + r'\s*$', line):
                in_section = True
                in_fields = False
            elif in_section and re.match(r'^### ', line) and not re.match(r'^### ' + re.escape(table), line):
                break
            elif in_section and '字段列表' in line and '**' in line:
                in_fields = True
                continue
            elif in_fields and line.startswith('|'):
                if re.match(r'^\|[-\s|:]+\|$', line):
                    continue
                if re.match(r'^\|\s*字段名\s*\|', line):
                    continue
                if re.match(r'^\| .+ \|', line):
                    # Extract column name (first field after |)
                    col_name = line.split('|')[1].strip()
                    if col_name:
                        doc_columns.add(col_name)
            elif in_fields and line.startswith('**') and '字段' not in line:
                in_fields = False

        only_in_doc_cols = doc_columns - db_columns
        only_in_db_cols = db_columns - doc_columns

        if only_in_doc_cols:
            print(f"  文档中多出的字段（数据库中不存在）: {sorted(only_in_doc_cols)}")
        if only_in_db_cols:
            print(f"  数据库中多出的字段（文档中缺失）: {sorted(only_in_db_cols)}")
