#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""验证MD与数据库字段一致性"""
import json, os, re

SCHEMA_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'schema_data')
def load_json(f):
    with open(os.path.join(SCHEMA_DIR, f), 'r', encoding='utf-8') as fh:
        return json.load(fh)

columns = load_json('columns.json')

# 构建数据库字段集合
db_cols = {}
for col in columns:
    tname = col['TABLE_NAME']
    if tname.startswith(('temp_', 'tmp_')):
        continue
    db_cols.setdefault(tname, set()).add(col['COLUMN_NAME'])

# 解析MD
MD_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict_full.md')
with open(MD_FILE, 'rb') as f:
    text = f.read().decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')

md_tables = {}
lines = text.split('\n')
i = 0
while i < len(lines):
    line = lines[i].strip()
    m = re.match(r'^#{1,3}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
    if not m:
        i += 1
        continue
    tname = m.group(1)
    if not tname or len(tname) < 2 or re.search(r'[\u4e00-\u9fff]', tname) or tname.startswith('**') or tname.startswith('第'):
        i += 1
        continue
    fields = set()
    i += 1
    while i < len(lines):
        l = lines[i].strip()
        if l.startswith('| 字段名'):
            i += 1; i += 1
            while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                parts = [p.strip() for p in lines[i].strip().split('|')]
                parts = [p for p in parts if p]
                if parts:
                    fields.add(parts[0])
                i += 1
            break
        elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l == '---' or re.match(r'^#{1,3}', l):
            break
        else:
            i += 1
    if fields:
        md_tables[tname] = fields

# 对比
common = set(md_tables.keys()) & set(db_cols.keys())
issues = 0
for tname in sorted(common):
    md_f = md_tables[tname]
    db_f = db_cols.get(tname, set())
    missing = db_f - md_f
    extra = md_f - db_f
    if missing or extra:
        issues += 1
        print(f"  {tname}: 缺{len(missing)} 多{len(extra)} (缺:{','.join(sorted(missing)[:3])})")

md_only = set(md_tables.keys()) - set(db_cols.keys())
db_only = set(db_cols.keys()) - set(md_tables.keys())

print(f"\n公共表数: {len(common)}")
print(f"有差异的表: {issues}")
print(f"仅在MD中: {len(md_only)}")
print(f"仅在DB中: {len(db_only)}")
if db_only:
    print(f"  仅DB: {', '.join(sorted(db_only)[:10])}")
