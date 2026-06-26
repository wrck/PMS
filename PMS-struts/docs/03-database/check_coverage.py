#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查 final.md 中字段描述和业务含义的覆盖情况"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_fields(text):
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
        fields = []
        obj_type = ''
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 对象类型'):
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    obj_type = parts[1]
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 7:
                        fields.append({
                            'name': parts[0],
                            'comment': parts[5],
                            'biz': parts[6],
                        })
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

text = read_md(os.path.join(BASE, 'database_dict final.md'))
tables = parse_fields(text)

# 过滤 temp/tmp/视图
filtered = {k: v for k, v in tables.items() if not k.startswith(('temp_','tmp_')) and v['obj_type'] != 'VIEW'}

total_fields = 0
empty_comment = 0
empty_biz = 0
both_empty = 0
has_comment = 0
has_biz = 0

for tname, data in sorted(filtered.items()):
    for f in data['fields']:
        total_fields += 1
        c = f['comment']
        b = f['biz']
        if not c or c == '-':
            empty_comment += 1
        else:
            has_comment += 1
        if not b or b == '-' or b == '业务含义待确认':
            empty_biz += 1
        else:
            has_biz += 1
        if (not c or c == '-') and (not b or b == '-' or b == '业务含义待确认'):
            both_empty += 1

print(f"总表数(过滤后): {len(filtered)}")
print(f"总字段数: {total_fields}")
print(f"有字段描述: {has_comment} ({has_comment*100//total_fields}%)")
print(f"无字段描述: {empty_comment} ({empty_comment*100//total_fields}%)")
print(f"有业务含义: {has_biz} ({has_biz*100//total_fields}%)")
print(f"无业务含义(含待确认): {empty_biz} ({empty_biz*100//total_fields}%)")
print(f"两者都无: {both_empty}")

# 列出两者都为空的字段
print("\n两者都为空的字段（前20个）:")
shown = 0
for tname, data in sorted(filtered.items()):
    for f in data['fields']:
        c = f['comment']
        b = f['biz']
        if (not c or c == '-') and (not b or b == '-' or b == '业务含义待确认'):
            print(f"  {tname}.{f['name']}")
            shown += 1
            if shown >= 20:
                break
    if shown >= 20:
        break
