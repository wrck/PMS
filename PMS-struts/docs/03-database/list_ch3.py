#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""列出第三章各子域的表名"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')

with open(FINAL_FILE, 'rb') as f:
    text = f.read().decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

lines = text.split('\n')

# 找到第三章的各子域
sections = {}
current_section = None
for i, line in enumerate(lines):
    l = line.strip()
    if l.startswith('# 一、Activiti'):
        current_section = '一、Activiti工作流引擎表'
    elif l.startswith('# 二、Firebird'):
        current_section = '二、Firebird迁移表'
    elif l.startswith('# 三、RMA/备件/仓库'):
        current_section = '三、RMA/备件/仓库等业务表'
    elif l.startswith('# 四、临时表'):
        current_section = '四、临时表'
    elif l.startswith('# 五、视图'):
        current_section = '五、视图'
    elif current_section:
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
        if m and m.group(1) and len(m.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m.group(1)) and not m.group(1).startswith('**') and not m.group(1).startswith('第') and not m.group(1).startswith('附录') and m.group(1)!='DPPMS':
            sections.setdefault(current_section, []).append((m.group(1), m.group(2) or ''))

for sec, tables in sections.items():
    print(f"\n{sec} ({len(tables)} 张表):")
    for tname, tcomment in tables:
        print(f"  {tname} -- {tcomment[:40]}")
