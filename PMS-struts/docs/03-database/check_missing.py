#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查 database_dict final.md 中字段描述和业务含义的缺失情况"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')
DICT_FILE = os.path.join(BASE, 'database_dict.md')

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
        i += 1
        while i < len(lines):
            l = lines[i].strip()
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
        tables[tname] = fields
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables

# 解析两个文件
final_fields = parse_fields(read_md(FINAL_FILE))
dict_fields = parse_fields(read_md(DICT_FILE))

# 统计 final.md 中的缺失
total_fields = 0
empty_comment = 0
empty_biz = 0
both_empty = 0
comment_diff_dict = 0
biz_diff_dict = 0

for tname in sorted(final_fields.keys()):
    for f in final_fields[tname]:
        total_fields += 1
        c = f['comment']
        b = f['biz']
        if not c or c == '-':
            empty_comment += 1
        if not b or b == '-' or b == '业务含义待确认':
            empty_biz += 1
        if (not c or c == '-') and (not b or b == '-' or b == '业务含义待确认'):
            both_empty += 1

        # 对比 dict.md
        if tname in dict_fields:
            for df in dict_fields[tname]:
                if df['name'] == f['name']:
                    if df['comment'] and df['comment'] != c:
                        comment_diff_dict += 1
                    if df['biz'] and df['biz'] != b and df['biz'] != '业务含义待确认':
                        biz_diff_dict += 1
                    break

print(f"总字段数: {total_fields}")
print(f"字段描述为空: {empty_comment} ({empty_comment*100/total_fields:.1f}%)")
print(f"业务含义为空/待确认: {empty_biz} ({empty_biz*100/total_fields:.1f}%)")
print(f"两者都为空: {both_empty}")
print(f"与dict.md字段描述不一致: {comment_diff_dict}")
print(f"与dict.md业务含义不一致: {biz_diff_dict}")

# 列出缺失最多的表
print("\n缺失最多的表（前20）:")
table_missing = []
for tname in sorted(final_fields.keys()):
    miss = 0
    for f in final_fields[tname]:
        if (not f['comment'] or f['comment'] == '-') and (not f['biz'] or f['biz'] == '-' or f['biz'] == '业务含义待确认'):
            miss += 1
    if miss > 0:
        table_missing.append((tname, miss, len(final_fields[tname])))
table_missing.sort(key=lambda x: -x[1])
for tname, miss, total in table_missing[:20]:
    print(f"  {tname}: {miss}/{total} 字段缺失描述和含义")
