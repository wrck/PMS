import re

filepath = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md'

with open(filepath, 'r', encoding='utf-8') as f:
    lines = f.readlines()

current_table = ''
empty_fields = []
total_fields = 0

for line in lines:
    line = line.rstrip('\n')

    # Detect table name from ### heading
    m = re.match(r'^###\s+(.+)', line)
    if m:
        current_table = m.group(1).strip()
        continue

    # Match field definition rows: starts with | and has YES/NO in 3rd column
    if re.match(r'^\|\s*\S+.*\|\s*(YES|NO)\s*\|', line):
        total_fields += 1
        cols = line.split('|')
        # cols[0] is empty (before first |), cols[1] is field name, ..., cols[-1] is empty (after last |) or last column
        # The business meaning is the second-to-last element after split
        # Actually: | a | b | c | d | e | f |
        # split by | gives: ['', ' a ', ' b ', ' c ', ' d ', ' e ', ' f ', '']
        # So last real column is cols[-2]
        if len(cols) >= 7:
            business_meaning = cols[-2].strip() if cols[-1].strip() == '' else cols[-1].strip()
            # If the line ends with ' |', then cols[-1] is '' and cols[-2] is the business meaning
            # If the line ends with '| something', then cols[-1] is ' something'
            # Standard format: | ... | business_meaning |
            # After split: [..., ' business_meaning ', '']
            # So business_meaning is cols[-2]
            field_name = cols[1].strip()

            if business_meaning == '' or business_meaning == '待确认':
                empty_fields.append({
                    'table': current_table,
                    'field': field_name,
                    'meaning': business_meaning if business_meaning else '(空)'
                })

print(f"总字段行数: {total_fields}")
print(f"空业务含义字段数: {len(empty_fields)}")
print()
print("--- 空业务含义字段列表 ---")
print(f"{'序号':<5} {'表名':<45} {'字段名':<35} {'业务含义':<10}")
print("-" * 100)
for i, f in enumerate(empty_fields, 1):
    print(f"{i:<5} {f['table']:<45} {f['field']:<35} {f['meaning']:<10}")
