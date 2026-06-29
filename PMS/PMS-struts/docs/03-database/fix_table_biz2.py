#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""补全 final.md 中缺失的表级业务含义（插入缺失的业务含义行）"""
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
        has_biz_line = False
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    table_biz = parts[1]
                    has_biz_line = True
                break
            elif l.startswith('| 字段名') or l.startswith('**') or l == '---':
                break
            i += 1
        tables[tname] = {'biz': table_biz, 'has_biz_line': has_biz_line}
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

    if tname in dict_biz and dict_biz[tname]['biz']:
        target_biz = dict_biz[tname]['biz']
        # 搜索属性区
        j = i + 1
        found_biz_line = False
        biz_line_idx = -1
        attr_end_idx = -1  # 属性区结束位置

        while j < len(final_lines):
            l = final_lines[j].strip()
            if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                found_biz_line = True
                biz_line_idx = j
                # 检查值是否为空
                parts = [p.strip() for p in final_lines[j].split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2 and not parts[1]:
                    # 空值，替换
                    final_lines[j] = f"| 业务含义 | {target_biz} |"
                    restored += 1
                    print(f"  [替换空值] {tname}")
                elif len(parts) >= 2 and parts[1] != target_biz:
                    # 有值但不同，以dict为准
                    final_lines[j] = f"| 业务含义 | {target_biz} |"
                    restored += 1
                    print(f"  [替换不同值] {tname}: '{parts[1][:30]}' -> '{target_biz[:30]}'")
                break
            elif l.startswith('**字段列表**') or l.startswith('**约束'):
                attr_end_idx = j
                break
            elif l.startswith('**') or l == '---' or re.match(r'^#{1,5}', l):
                attr_end_idx = j
                break
            j += 1

        # 如果没有业务含义行，在属性区末尾插入
        if not found_biz_line and attr_end_idx > 0:
            # 找属性区最后一个 | xxx | yyy | 行
            last_attr_line = attr_end_idx - 1
            while last_attr_line > i and not final_lines[last_attr_line].strip().startswith('|'):
                last_attr_line -= 1
            # 在最后一个属性行后插入
            insert_idx = last_attr_line + 1
            final_lines.insert(insert_idx, f"| 业务含义 | {target_biz} |")
            restored += 1
            print(f"  [插入] {tname}: {target_biz[:50]}")
            i += 1  # 因为插入了一行

    i += 1
    while i < len(final_lines):
        l = final_lines[i].strip()
        if l=='---' or re.match(r'^#{1,5}\s+',l):
            break
        i += 1

with open(FINAL_FILE, 'w', encoding='utf-8') as f:
    f.write('\n'.join(final_lines))

print(f"\n补全完成: {restored} 个表级业务含义")
