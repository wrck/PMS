#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""快速检查 final.md 中字段描述为空的数量"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

text = read_md(os.path.join(BASE, 'database_dict final.md'))
lines = text.split('\n')

current_table = ''
empty_count = 0
total_fields = 0
empty_examples = []

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

            total_fields += 1
            if not comment or comment in ('-', 'None', ''):
                empty_count += 1
                if len(empty_examples) < 20:
                    empty_examples.append(f"{current_table}.{fname}")
    i += 1

print(f"总字段数: {total_fields}")
print(f"字段描述为空: {empty_count}")
if empty_examples:
    print(f"示例: {', '.join(empty_examples[:10])}")
