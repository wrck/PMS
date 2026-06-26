#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""对比 database_dict.md 和 database_dict final.md 的业务含义差异"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_biz_meanings(text):
    """解析MD中每个表的字段业务含义，返回 {table_name: {field_name: biz_meaning}}"""
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
        fields = {}
        table_biz = ''
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            # 表级业务含义
            if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    table_biz = parts[1]
            # 字段列表
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 7:
                        fname = parts[0]
                        biz = parts[6] if len(parts) >= 7 else ''
                        fields[fname] = biz
                    i += 1
                break
            elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                break
            else:
                i += 1
        tables[tname] = {'table_biz': table_biz, 'fields': fields}
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables

dict_text = read_md(os.path.join(BASE, 'database_dict.md'))
final_text = read_md(os.path.join(BASE, 'database_dict final.md'))

dict_biz = parse_biz_meanings(dict_text)
final_biz = parse_biz_meanings(final_text)

# 统计
common_tables = set(dict_biz.keys()) & set(final_biz.keys())
print(f"database_dict.md 表数: {len(dict_biz)}")
print(f"database_dict final.md 表数: {len(final_biz)}")
print(f"公共表数: {len(common_tables)}")

# 对比字段业务含义
diff_count = 0
diff_fields = 0
restorable = 0
empty_in_final = 0
for tname in sorted(common_tables):
    dict_fields = dict_biz[tname]['fields']
    final_fields = final_biz[tname]['fields']
    table_diff = False
    for fname, dict_val in dict_fields.items():
        if fname in final_fields:
            final_val = final_fields[fname]
            if dict_val and dict_val != final_val:
                diff_fields += 1
                table_diff = True
                if not final_val:
                    empty_in_final += 1
                restorable += 1
    if table_diff:
        diff_count += 1

# 对比表级业务含义
table_biz_diff = 0
for tname in sorted(common_tables):
    dict_tb = dict_biz[tname]['table_biz']
    final_tb = final_biz[tname]['table_biz']
    if dict_tb and dict_tb != final_tb:
        table_biz_diff += 1

print(f"\n字段业务含义有差异的表: {diff_count}")
print(f"有差异的字段数: {diff_fields}")
print(f"final.md中为空可恢复的: {empty_in_final}")
print(f"表级业务含义有差异: {table_biz_diff}")

# 显示部分差异示例
print("\n差异示例（前20个）:")
shown = 0
for tname in sorted(common_tables):
    dict_fields = dict_biz[tname]['fields']
    final_fields = final_biz[tname]['fields']
    for fname in sorted(dict_fields.keys()):
        if fname in final_fields:
            dv = dict_fields[fname]
            fv = final_fields[fname]
            if dv and dv != fv:
                print(f"  {tname}.{fname}:")
                print(f"    dict:  {dv[:60]}")
                print(f"    final: {fv[:60]}")
                shown += 1
                if shown >= 20:
                    break
    if shown >= 20:
        break
