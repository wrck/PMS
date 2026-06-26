import pymysql

conn = pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', database='dppms_d365', charset='utf8mb4')
cur = conn.cursor()

# Count view_ prefix BASE TABLEs
cur.execute("""SELECT TABLE_NAME, TABLE_TYPE FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'dppms_d365'
AND TABLE_NAME LIKE 'view_%%'
AND NOT (TABLE_NAME LIKE 'temp_%%' OR TABLE_NAME LIKE 'tmp_%%' OR TABLE_NAME LIKE '%%_temp' OR TABLE_NAME LIKE '%%_tmp')
ORDER BY TABLE_NAME""")
print("=== view_前缀的所有对象 ===")
view_base = []
view_view = []
for row in cur.fetchall():
    print(f"  {row[0]} -> {row[1]}")
    if row[1] == 'BASE TABLE':
        view_base.append(row[0])
    else:
        view_view.append(row[0])

print(f"\nview_前缀BASE TABLE数量: {len(view_base)}")
print(f"view_前缀VIEW数量: {len(view_view)}")
print(f"view_前缀对象总数: {len(view_base) + len(view_view)}")

# Count dp_v_ prefix VIEWs
cur.execute("""SELECT TABLE_NAME FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_NAME LIKE 'dp_v_%%'
AND NOT (TABLE_NAME LIKE 'temp_%%' OR TABLE_NAME LIKE 'tmp_%%' OR TABLE_NAME LIKE '%%_temp' OR TABLE_NAME LIKE '%%_tmp')""")
dp_v_views = cur.fetchall()
print(f"\ndp_v_前缀VIEW数量: {len(dp_v_views)}")
for v in dp_v_views:
    print(f"  {v[0]}")

# Count pm_ prefix VIEWs
cur.execute("""SELECT TABLE_NAME FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'dppms_d365' AND TABLE_NAME LIKE 'pm_%%' AND TABLE_TYPE = 'VIEW'
AND NOT (TABLE_NAME LIKE 'temp_%%' OR TABLE_NAME LIKE 'tmp_%%' OR TABLE_NAME LIKE '%%_temp' OR TABLE_NAME LIKE '%%_tmp')""")
pm_views = cur.fetchall()
print(f"\npm_前缀VIEW数量: {len(pm_views)}")
for v in pm_views:
    print(f"  {v[0]}")

# Check document annotations for view_ prefix BASE TABLEs
with open(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md', 'r', encoding='utf-8') as f:
    content = f.read()

print("\n=== view_前缀BASE TABLE的文档标注情况 ===")
for table_name in view_base:
    section_start = content.find(f'### {table_name}')
    if section_start == -1:
        print(f"  {table_name}: 文档中未找到")
        continue
    next_section = content.find('\n### ', section_start + 1)
    if next_section == -1:
        section_content = content[section_start:]
    else:
        section_content = content[section_start:next_section]

    has_view_annotation = '**对象类型**：VIEW' in section_content
    has_base_annotation = '**对象类型**：BASE TABLE' in section_content
    has_base_in_desc = 'BASE TABLE' in section_content

    print(f"  {table_name}:")
    print(f"    有'**对象类型**：VIEW'标注: {has_view_annotation}")
    print(f"    有'**对象类型**：BASE TABLE'标注: {has_base_annotation}")
    print(f"    描述中提到BASE TABLE: {has_base_in_desc}")

conn.close()
