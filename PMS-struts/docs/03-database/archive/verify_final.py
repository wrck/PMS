#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""用与导出脚本相同的解析逻辑验证 MD 文件覆盖率"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_field_row(raw_line):
    """与导出脚本相同的字段行解析逻辑"""
    all_parts = raw_line.split('|')
    if all_parts and all_parts[0].strip() == '':
        all_parts = all_parts[1:]
    if all_parts and all_parts[-1].strip() == '':
        all_parts = all_parts[:-1]
    stripped = [p.strip() for p in all_parts]
    col_count = len(stripped)

    if col_count >= 7:
        return {
            '字段名': stripped[0], '数据类型': stripped[1], '可空': stripped[2],
            '默认值': stripped[3], '约束': stripped[4],
            '字段描述': ' | '.join(stripped[5:-1]), '业务含义': stripped[-1],
        }
    elif col_count == 6:
        fifth = stripped[4]
        if fifth in ('PRI', 'UNI', 'MUL', 'AUTO_INCREMENT') or 'auto_increment' in fifth.lower() or fifth in ('-', ''):
            return {
                '字段名': stripped[0], '数据类型': stripped[1], '可空': stripped[2],
                '默认值': stripped[3], '约束': fifth,
                '字段描述': stripped[5], '业务含义': '',
            }
        else:
            return {
                '字段名': stripped[0], '数据类型': stripped[1], '可空': stripped[2],
                '默认值': stripped[3], '约束': '',
                '字段描述': ' | '.join(stripped[4:-1]),
                '业务含义': stripped[-1] if stripped[-1] != stripped[4] else '',
            }
    elif col_count == 5:
        return {
            '字段名': stripped[0], '数据类型': stripped[1], '可空': stripped[2],
            '默认值': stripped[3], '约束': '',
            '字段描述': stripped[4], '业务含义': '',
        }
    return None

text = read_md(os.path.join(BASE, 'database_dict final.md'))
lines = text.split('\n')

total_fields = 0
has_comment = 0
has_biz = 0
empty_comment = 0
empty_biz = 0
table_count = 0

i = 0
while i < len(lines):
    line = lines[i].strip()
    m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
    if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
        i += 1
        continue
    tname = m.group(1)
    obj_type = ''
    i += 1
    while i < len(lines):
        l = lines[i].strip()
        if l.startswith('| 对象类型'):
            parts = [p.strip() for p in l.split('|') if p.strip()]
            if len(parts) >= 2:
                obj_type = parts[1]
        if l.startswith('| 字段名'):
            i += 1; i += 1
            while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                field = parse_field_row(lines[i].strip())
                if field:
                    total_fields += 1
                    c = field['字段描述']
                    b = field['业务含义']
                    if c and c not in ('-', 'None'):
                        has_comment += 1
                    else:
                        empty_comment += 1
                    if b and b not in ('-', 'None', '', '业务含义待确认'):
                        has_biz += 1
                    else:
                        empty_biz += 1
                i += 1
            if not tname.startswith(('temp_','tmp_')) and obj_type != 'VIEW':
                table_count += 1
            break
        elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
            if not tname.startswith(('temp_','tmp_')) and obj_type != 'VIEW':
                table_count += 1
            break
        else:
            i += 1
    while i < len(lines):
        l = lines[i].strip()
        if l=='---' or re.match(r'^#{1,5}\s+',l):
            break
        i += 1

print(f"表数(过滤后): {table_count}")
print(f"总字段数: {total_fields}")
print(f"有字段描述: {has_comment} ({has_comment*100//total_fields}%)")
print(f"无字段描述: {empty_comment} ({empty_comment*100//total_fields}%)")
print(f"有业务含义: {has_biz} ({has_biz*100//total_fields}%)")
print(f"无业务含义(含待确认): {empty_biz} ({empty_biz*100//total_fields}%)")
