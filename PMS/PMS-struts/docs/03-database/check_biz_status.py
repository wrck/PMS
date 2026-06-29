#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""精确检查字段行的列数和业务含义状态"""
import re
import os

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

with open(MD_FILE, 'rb') as f:
    text = f.read().decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')
lines = text.split('\n')

col_dist = {}
total_field_rows = 0
empty_biz = 0
sample_empty = []
sample_ok = []

# 找到所有字段表头行，然后统计其下方的数据行
i = 0
while i < len(lines):
    s = lines[i].strip()
    if s.startswith('| 字段名') and '数据类型' in s:
        i += 1  # skip header
        i += 1  # skip separator
        while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
            raw = lines[i].strip()
            all_parts = raw.split('|')
            if all_parts and all_parts[0].strip() == '':
                all_parts = all_parts[1:]
            if all_parts and all_parts[-1].strip() == '':
                all_parts = all_parts[:-1]
            stripped = [p.strip() for p in all_parts]
            col_count = len(stripped)

            col_dist[col_count] = col_dist.get(col_count, 0) + 1
            total_field_rows += 1

            if col_count >= 7:
                biz = stripped[-1]
                comment = ' | '.join(stripped[5:-1])
                if biz in ('', '-', 'None', '业务含义待确认'):
                    empty_biz += 1
                    if len(sample_empty) < 10:
                        sample_empty.append(f"  [col={col_count}] biz='{biz}' comment='{comment[:40]}' | {raw[:100]}")
                elif len(sample_ok) < 3:
                    sample_ok.append(f"  [col={col_count}] biz='{biz[:30]}' | {raw[:100]}")
            elif col_count < 7:
                empty_biz += 1
                if len(sample_empty) < 10:
                    sample_empty.append(f"  [col={col_count}] | {raw[:100]}")
            i += 1
    i += 1

print(f"字段数据行总数: {total_field_rows}")
print(f"\n列数分布:")
for k in sorted(col_dist.keys()):
    print(f"  {k}列: {col_dist[k]} 行")
print(f"\n空业务含义行数: {empty_biz}")
print(f"有业务含义行数: {total_field_rows - empty_biz}")
if sample_ok:
    print(f"\n有业务含义样例:")
    for s in sample_ok:
        print(s)
if sample_empty:
    print(f"\n空业务含义样例:")
    for s in sample_empty:
        print(s)
