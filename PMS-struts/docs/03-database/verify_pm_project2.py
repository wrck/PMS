#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""用合并脚本的解析逻辑验证pm_project字段"""
import re, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from merge_data_element import parse_match_xlsx, parse_base_xlsx, NEW_COLS

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
BASE_XLSX = os.path.join(BASE_DIR, 'database_dict_flat_final_match.xlsx')
MATCH_XLSX = os.path.join(BASE_DIR, '项目管理表数据元.xlsx')

match_data = parse_match_xlsx(MATCH_XLSX)
base_data = parse_base_xlsx(BASE_XLSX)

# pm_project 匹配表字段
pm_match = match_data.get('pm_project', [])
print(f"匹配表 pm_project 字段数: {len(pm_match)}")

# pm_project 基表字段
pm_base = None
for t in base_data.get('项目管理', []):
    if t['table_name'] == 'pm_project':
        pm_base = t
        break

if pm_base:
    base_fnames = set(f['fname'] for f in pm_base['fields'])
    print(f"基表 pm_project 字段数: {len(pm_base['fields'])}")
else:
    base_fnames = set()
    print("基表未找到 pm_project")

print("\n匹配表字段详情:")
for i, mf in enumerate(pm_match):
    fname = mf['fname']
    comment = mf['comment'][:20] if mf['comment'] else ''
    if fname and fname in base_fnames:
        status = "匹配"
    elif fname:
        status = f"新增(有字段名)"
    else:
        status = "新增(无字段名)"

    new_cols = []
    for c in NEW_COLS:
        if mf.get(c):
            new_cols.append(f"{c}={mf[c][:15]}")
    new_info = ' | '.join(new_cols[:3]) if new_cols else ''

    print(f"  {i+1:2d}. fname='{fname:25s}' comment='{comment:20s}' {status:12s} {new_info}")
