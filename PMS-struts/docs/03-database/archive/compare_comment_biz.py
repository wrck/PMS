#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""对比 database_dict.md 和 database_dict final.md 的字段描述和业务含义差异"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_fields(text):
    """解析MD中每个表的字段描述和业务含义，返回 {table_name: {field_name: (comment, biz)}}"""
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
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 7:
                        fname = parts[0]
                        comment = parts[5] if len(parts) >= 6 else ''
                        biz = parts[6] if len(parts) >= 7 else ''
                        fields[fname] = (comment, biz)
                    i += 1
                break
            elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                break
            else:
                i += 1
        tables[tname] = fields
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables

dict_text = read_md(os.path.join(BASE, 'database_dict.md'))
final_text = read_md(os.path.join(BASE, 'database_dict final.md'))

dict_fields = parse_fields(dict_text)
final_fields = parse_fields(final_text)

common_tables = set(dict_fields.keys()) & set(final_fields.keys())
print(f"dict.md 表数: {len(dict_fields)}")
print(f"final.md 表数: {len(final_fields)}")
print(f"公共表数: {len(common_tables)}")

# 对比
comment_diff = 0
biz_diff = 0
comment_empty_final = 0
biz_empty_final = 0
comment_dict_has = 0
biz_dict_has = 0

for tname in sorted(common_tables):
    for fname, (dc, db) in dict_fields[tname].items():
        if fname in final_fields[tname]:
            fc, fb = final_fields[tname][fname]
            if dc and dc != fc:
                comment_diff += 1
                if not fc:
                    comment_empty_final += 1
                if dc:
                    comment_dict_has += 1
            if db and db != fb:
                biz_diff += 1
                if not fb:
                    biz_empty_final += 1
                if db:
                    biz_dict_has += 1

print(f"\n字段描述差异: {comment_diff} (final为空: {comment_empty_final}, dict有值: {comment_dict_has})")
print(f"业务含义差异: {biz_diff} (final为空: {biz_empty_final}, dict有值: {biz_dict_has})")

# 显示部分差异示例
print("\n字段描述差异示例（前15个）:")
shown = 0
for tname in sorted(common_tables):
    for fname in sorted(dict_fields[tname].keys()):
        if fname in final_fields[tname]:
            dc, db = dict_fields[tname][fname]
            fc, fb = final_fields[tname][fname]
            if dc and dc != fc:
                print(f"  {tname}.{fname}:")
                print(f"    dict comment:  {dc[:60]}")
                print(f"    final comment: {fc[:60]}")
                shown += 1
                if shown >= 15:
                    break
    if shown >= 15:
        break

print("\n业务含义差异示例（前15个）:")
shown = 0
for tname in sorted(common_tables):
    for fname in sorted(dict_fields[tname].keys()):
        if fname in final_fields[tname]:
            dc, db = dict_fields[tname][fname]
            fc, fb = final_fields[tname][fname]
            if db and db != fb:
                print(f"  {tname}.{fname}:")
                print(f"    dict biz:  {db[:60]}")
                print(f"    final biz: {fb[:60]}")
                shown += 1
                if shown >= 15:
                    break
    if shown >= 15:
        break
