#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查字段描述和业务含义的空值状态"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

with open(MD_FILE, 'rb') as f:
    text = f.read().decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')
lines = text.split('\n')

total = 0
empty_comment = 0
empty_biz = 0
both_empty = 0
sample_empty_comment = []
sample_both_empty = []

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

            if col_count >= 7:
                total += 1
                fname = stripped[0]
                comment = ' | '.join(stripped[5:-1])
                biz = stripped[-1]
                c_empty = not comment or comment in ('-', 'None', '', '业务含义待确认')
                b_empty = not biz or biz in ('-', 'None', '', '业务含义待确认')

                if c_empty:
                    empty_comment += 1
                    if len(sample_empty_comment) < 15:
                        sample_empty_comment.append(f"  {fname}: comment='{comment}' biz='{biz[:50]}'")
                if b_empty:
                    empty_biz += 1
                if c_empty and b_empty:
                    both_empty += 1
                    if len(sample_both_empty) < 15:
                        sample_both_empty.append(f"  {fname}: comment='{comment}' biz='{biz}'")
            i += 1
    i += 1

print(f"字段数据行总数: {total}")
print(f"空字段描述: {empty_comment}")
print(f"空业务含义: {empty_biz}")
print(f"两者皆空: {both_empty}")

if sample_empty_comment:
    print(f"\n空字段描述样例:")
    for s in sample_empty_comment:
        print(s)

if sample_both_empty:
    print(f"\n两者皆空样例:")
    for s in sample_both_empty:
        print(s)
