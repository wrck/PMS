#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查仍为空的字段描述"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

text = read_md(os.path.join(BASE, 'database_dict final.md'))
lines = text.split('\n')

current_table = ''
empty_fields = []

i = 0
while i < len(lines):
    line = lines[i].strip()
    m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
    if m and m.group(1) and len(m.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m.group(1)) and not m.group(1).startswith('**'):
        current_table = m.group(1)

    if line.startswith('|') and '---' not in line and not line.startswith('| 字段名') and not line.startswith('| 属性') and not line.startswith('| 索引') and not line.startswith('| 外键'):
        in_field_table = False
        for j in range(i-1, max(i-10, 0), -1):
            if lines[j].strip().startswith('| 字段名'):
                in_field_table = True
                break
            if lines[j].strip().startswith('| 索引') or lines[j].strip().startswith('| 外键') or lines[j].strip().startswith('| 属性'):
                break

        if in_field_table:
            all_parts = line.split('|')
            if all_parts and all_parts[0].strip() == '':
                all_parts = all_parts[1:]
            if all_parts and all_parts[-1].strip() == '':
                all_parts = all_parts[:-1]
            stripped = [p.strip() for p in all_parts]

            if len(stripped) >= 7:
                fname = stripped[0]
                comment = ' | '.join(stripped[5:-1])
                biz = stripped[-1]
            elif len(stripped) >= 5:
                fname = stripped[0]
                comment = stripped[-2] if len(stripped) >= 6 else ''
                biz = stripped[-1]
            else:
                i += 1
                continue

            if not comment or comment in ('-', 'None', ''):
                empty_fields.append(f"{current_table}.{fname} (biz={biz})")

    i += 1

print(f"仍为空的字段: {len(empty_fields)}")
# 按表统计
table_count = {}
for f in empty_fields:
    tname = f.split('.')[0]
    table_count[tname] = table_count.get(tname, 0) + 1

print(f"\n按表分布（前20个）:")
for tname, count in sorted(table_count.items(), key=lambda x: -x[1])[:20]:
    print(f"  {tname}: {count}")

print(f"\n示例（前30个）:")
for f in empty_fields[:30]:
    print(f"  {f}")
