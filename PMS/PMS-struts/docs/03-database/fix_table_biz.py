#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""补全 final.md 中缺失的表级业务含义"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
DICT_FILE = os.path.join(BASE, 'database_dict.md')
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')

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

dict_biz = parse_table_biz(read_md(DICT_FILE))
final_lines = read_md(FINAL_FILE).split('\n')

restored = 0
i = 0
while i < len(final_lines):
    line = final_lines[i].strip()
    m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
    if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
        i += 1
        continue
    tname = m.group(1)

    if tname in dict_biz and dict_biz[tname]:
        # 找到属性区中的业务含义行
        j = i + 1
        while j < len(final_lines):
            l = final_lines[j].strip()
            if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                parts = [p.strip() for p in final_lines[j].split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    current_biz = parts[1]
                    if not current_biz:
                        # 替换空业务含义
                        final_lines[j] = final_lines[j].replace('|  |', f'| {dict_biz[tname]} |', 1)
                        restored += 1
                        print(f"  [补全] {tname}: {dict_biz[tname][:50]}")
                break
            elif l.startswith('| 字段名') or l.startswith('**') or l == '---':
                # 属性区中没有业务含义行，需要插入
                # 找到属性区开始位置（| 属性 | 值 | 行之后）
                if l.startswith('| 字段名'):
                    # 在字段列表之前插入业务含义行
                    # 先找属性区的分隔线
                    k = j - 1
                    while k > i:
                        if final_lines[k].strip().startswith('|') and '---' in final_lines[k]:
                            # 在分隔线后插入
                            final_lines.insert(k+1, f'| 业务含义 | {dict_biz[tname]} |')
                            restored += 1
                            print(f"  [插入] {tname}: {dict_biz[tname][:50]}")
                            break
                        k -= 1
                break
            j += 1

    i += 1
    while i < len(final_lines):
        l = final_lines[i].strip()
        if l=='---' or re.match(r'^#{1,5}\s+',l):
            break
        i += 1

with open(FINAL_FILE, 'w', encoding='utf-8') as f:
    f.write('\n'.join(final_lines))

print(f"\n补全完成: {restored} 个表级业务含义")
