#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""直接对比 MD 解析与 Excel 导出的字段数量"""
import re, os
from openpyxl import load_workbook

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_all_tables(text):
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
        obj_type = ''
        fields = []
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 对象类型'):
                parts = [p.strip() for p in l.split('|') if p.strip()]
                if len(parts) >= 2:
                    obj_type = parts[1]
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    non_empty = [p.strip() for p in lines[i].strip().split('|') if p.strip()]
                    if len(non_empty) >= 7:
                        fname = non_empty[0]
                        comment = ' | '.join(non_empty[5:-1])
                        biz = non_empty[-1]
                        fields.append({'name': fname, 'comment': comment, 'biz': biz})
                    elif len(non_empty) >= 5:
                        fname = non_empty[0]
                        comment = non_empty[5] if len(non_empty) >= 6 else ''
                        biz = non_empty[-1] if len(non_empty) >= 7 else ''
                        fields.append({'name': fname, 'comment': comment, 'biz': biz})
                    i += 1
                break
            elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                break
            else:
                i += 1
        tables[tname] = {'obj_type': obj_type, 'fields': fields}
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables

# MD 统计
text = read_md(os.path.join(BASE, 'database_dict final.md'))
md_tables = parse_all_tables(text)
filtered = {k: v for k, v in md_tables.items() if not k.startswith(('temp_','tmp_')) and v['obj_type'] != 'VIEW'}

md_total = sum(len(v['fields']) for v in filtered.values())
md_has_comment = sum(1 for v in filtered.values() for f in v['fields'] if f['comment'] and f['comment'] != '-')
md_has_biz = sum(1 for v in filtered.values() for f in v['fields'] if f['biz'] and f['biz'] not in ('-', '业务含义待确认'))

print(f"MD文件（过滤后）:")
print(f"  表数: {len(filtered)}")
print(f"  字段数: {md_total}")
print(f"  有字段描述: {md_has_comment} ({md_has_comment*100//md_total}%)")
print(f"  有业务含义: {md_has_biz} ({md_has_biz*100//md_total}%)")

# Excel 统计
print(f"\nExcel导出:")
print(f"  表数: 264")
print(f"  字段数: 3977")

# 对比
print(f"\n差异: {3977 - md_total} 字段")
if md_total == 3977:
    print("MD与Excel字段数一致!")
else:
    # 找出差异
    print(f"MD: {md_total}, Excel: 3977")
