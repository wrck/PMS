#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""最终验证：检查final.md的完整性"""
import re, os

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')

def extract_tables(text):
    tables = set()
    for line in text.split('\n'):
        line = line.strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) and len(m.group(1)) >= 2 and not re.search(r'[\u4e00-\u9fff]', m.group(1)) and not m.group(1).startswith('**') and not m.group(1).startswith('第') and not m.group(1).startswith('附录') and m.group(1) != 'DPPMS':
            tables.add(m.group(1))
    return tables

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)))
final_text = read_md(os.path.join(BASE, 'database_dict final.md'))
full_text = read_md(os.path.join(BASE, 'database_dict_full.md'))

final_tables = extract_tables(final_text)
full_tables = extract_tables(full_text)

# 排除临时表和dppms_d365
full_tables = {t for t in full_tables if not t.startswith(('temp_', 'tmp_')) and t != 'dppms_d365'}
final_tables_no_temp = {t for t in final_tables if not t.startswith(('temp_', 'tmp_'))}

missing = full_tables - final_tables_no_temp
extra = final_tables_no_temp - full_tables

print(f"final.md 有效表数(排除temp/tmp): {len(final_tables_no_temp)}")
print(f"full.md 有效表数(排除temp/tmp): {len(full_tables)}")
print(f"final.md 缺失的有效表: {len(missing)}")
print(f"final.md 多余的有效表: {len(extra)}")

if missing:
    print("\n缺失的表:")
    for t in sorted(missing):
        print(f"  {t}")

# 检查章节结构
print("\n章节结构:")
for line in final_text.split('\n'):
    line = line.strip()
    if re.match(r'^#{1,3}\s+(第[一二三四五六七八九十]+章|一、|二、|三、|四、|五、|六、)', line):
        print(f"  {line[:60]}")
