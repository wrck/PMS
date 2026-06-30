#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查表级业务含义差异"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_table_biz(text):
    tables = {}
    lines = text.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
            i += 1
            continue
        tname = m.group(1)
        table_biz = ''
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    table_biz = parts[1]
                break
            elif l.startswith('| 字段名') or l.startswith('**') or l == '---':
                break
            i += 1
        tables[tname] = table_biz
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables

dict_biz = parse_table_biz(read_md(os.path.join(BASE, 'database_dict.md')))
final_biz = parse_table_biz(read_md(os.path.join(BASE, 'database_dict final.md')))

common = set(dict_biz.keys()) & set(final_biz.keys())
diff = 0
for tname in sorted(common):
    dv = dict_biz.get(tname, '')
    fv = final_biz.get(tname, '')
    if dv and dv != fv:
        diff += 1
        print(f"  {tname}:")
        print(f"    dict:  {dv[:80]}")
        print(f"    final: {fv[:80]}")
print(f"\n表级业务含义差异: {diff}")
