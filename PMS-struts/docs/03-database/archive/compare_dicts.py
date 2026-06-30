#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""对比两个MD文件，找出final.md中缺失的表"""
import re, os

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')

def extract_table_names(text):
    """提取MD中所有表名"""
    tables = set()
    lines = text.split('\n')
    for line in lines:
        line = line.strip()
        # 匹配 ### 1.1 table_name -- 注释
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m:
            tname = m.group(1)
            # 过滤非表名
            if tname and len(tname) >= 2 and not re.search(r'[\u4e00-\u9fff]', tname) and not tname.startswith('**') and not tname.startswith('第') and not tname.startswith('附录'):
                tables.add(tname)
    return tables

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)))
final_text = read_md(os.path.join(BASE, 'database_dict final.md'))
full_text = read_md(os.path.join(BASE, 'database_dict_full.md'))

final_tables = extract_table_names(final_text)
full_tables = extract_table_names(full_text)

missing = full_tables - final_tables
extra = final_tables - full_tables

print(f"final.md 表数: {len(final_tables)}")
print(f"full.md 表数: {len(full_tables)}")
print(f"final.md 缺失的表: {len(missing)}")
print(f"final.md 多余的表: {len(extra)}")

if missing:
    print(f"\n缺失的表列表:")
    for t in sorted(missing):
        print(f"  {t}")

if extra:
    print(f"\n多余的表列表:")
    for t in sorted(extra):
        print(f"  {t}")
