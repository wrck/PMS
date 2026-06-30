#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查 final.md 中所有字段表的列数分布"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

text = read_md(os.path.join(BASE, 'database_dict final.md'))
lines = text.split('\n')

col_counts = {}
total_field_rows = 0
for i, line in enumerate(lines):
    l = line.strip()
    if l.startswith('|') and '---' not in l and not l.startswith('| 字段名') and not l.startswith('| 属性'):
        # 可能是字段数据行
        parts = [p.strip() for p in l.split('|')]
        parts = [p for p in parts if p]
        if len(parts) >= 4:
            # 检查是否在字段表头之后
            for j in range(i-1, max(i-5, 0), -1):
                if lines[j].strip().startswith('| 字段名'):
                    col_counts[len(parts)] = col_counts.get(len(parts), 0) + 1
                    total_field_rows += 1
                    break

print(f"总字段数据行: {total_field_rows}")
print(f"列数分布:")
for cols, count in sorted(col_counts.items()):
    print(f"  {cols}列: {count}行")

# 检查7列字段表中字段描述和业务含义是否为空
empty_comment_7col = 0
empty_biz_7col = 0
total_7col = 0
for i, line in enumerate(lines):
    l = line.strip()
    if l.startswith('|') and '---' not in l and not l.startswith('| 字段名') and not l.startswith('| 属性'):
        for j in range(i-1, max(i-5, 0), -1):
            if lines[j].strip().startswith('| 字段名'):
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 7:
                    total_7col += 1
                    if not parts[5] or parts[5] == '-':
                        empty_comment_7col += 1
                    if not parts[6] or parts[6] == '-' or parts[6] == '业务含义待确认':
                        empty_biz_7col += 1
                break

print(f"\n7列字段表统计:")
print(f"  总行数: {total_7col}")
print(f"  字段描述为空: {empty_comment_7col}")
print(f"  业务含义为空/待确认: {empty_biz_7col}")
