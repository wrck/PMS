#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""对剩余空业务含义用字段描述填充"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

text = read_md(MD_FILE)
lines = text.split('\n')

filled = 0
in_field_table = False

for i, line in enumerate(lines):
    l = line.strip()
    if l.startswith('| 字段名'):
        in_field_table = True
        continue
    if l.startswith('|---'):
        continue
    if in_field_table and (not l.startswith('|') or l.startswith('| 索引') or l.startswith('| 外键') or l.startswith('| 属性') or l.startswith('| 约束')):
        in_field_table = False
        continue
    if in_field_table and l.startswith('|'):
        parts = [p.strip() for p in l.split('|')]
        parts = [p for p in parts if p]
        if len(parts) >= 7:
            fname = parts[0]
            comment = parts[5]
            biz = parts[6]
            if (not biz or biz in ('-', 'None', '', '业务含义待确认')) and comment and comment not in ('-', 'None', ''):
                # 用字段描述作为业务含义
                new_biz = comment
                dtype = parts[1]
                nullable = parts[2]
                default_val = parts[3]
                constraint = parts[4]
                lines[i] = f"| {fname} | {dtype} | {nullable} | {default_val} | {constraint} | {comment} | {new_biz} |"
                filled += 1

with open(MD_FILE, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print(f"用字段描述填充业务含义: {filled}")
