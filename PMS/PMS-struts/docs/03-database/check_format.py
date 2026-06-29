#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查 final.md 中表格格式问题"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

text = read_md(os.path.join(BASE, 'database_dict final.md'))
lines = text.split('\n')

# 检查字段数据行的格式问题
broken_lines = 0
missing_trailing_pipe = 0
wrong_col_count = 0
total_field_lines = 0
in_field_table = False

for i, line in enumerate(lines):
    l = line.strip()
    if l.startswith('| 字段名'):
        in_field_table = True
        continue
    if l.startswith('|---'):
        continue
    if in_field_table:
        if not l.startswith('|') or l.startswith('| 索引') or l.startswith('| 外键') or l.startswith('| 属性'):
            in_field_table = False
            continue
        total_field_lines += 1
        
        # 检查行尾是否缺少 |
        if not l.endswith('|'):
            missing_trailing_pipe += 1
        
        # 检查列数
        parts = [p.strip() for p in l.split('|') if p.strip()]
        if len(parts) != 7:
            wrong_col_count += 1
            if wrong_col_count <= 10:
                print(f"  行{i+1}: {len(parts)}列: {l[:80]}")

print(f"总字段数据行: {total_field_lines}")
print(f"行尾缺少|: {missing_trailing_pipe}")
print(f"列数不为7: {wrong_col_count}")
