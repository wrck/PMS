#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""诊断：找出解析后字段列表为空的表"""
import re, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from export_dict_to_excel import read_md, find_chapter_boundaries, find_subdomain_boundaries, parse_tables, DOMAINS

MD_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict.md')
text = read_md(MD_FILE)
lines = text.split('\n')

chapters = find_chapter_boundaries(lines)

for domain_name, chapter_key, subdomain_keywords in DOMAINS:
    if chapter_key not in chapters:
        continue
    ch_start, ch_end = chapters[chapter_key]
    subdomains = find_subdomain_boundaries(lines, ch_start, ch_end)

    target_subdomains = []
    for sd_name, sd_start, sd_end in subdomains:
        for kw in subdomain_keywords:
            if kw in sd_name or sd_name.startswith(kw):
                target_subdomains.append((sd_name, sd_start, sd_end))
                break

    if not target_subdomains:
        target_subdomains = [(chapter_key, ch_start, ch_end)]

    all_tables = []
    for sd_name, sd_start, sd_end in target_subdomains:
        tables = parse_tables(lines, sd_start, sd_end)
        all_tables.extend(tables)

    empty_tables = [t for t in all_tables if not t['fields']]
    if empty_tables:
        print(f'\n[{domain_name}] 空字段表 ({len(empty_tables)}):')
        for t in empty_tables:
            print(f'  {t["table_name"]} - {t["table_comment"]}')
    else:
        print(f'[{domain_name}] 所有表都有字段数据')
