#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
将 database_dict.md 中的原始业务含义还原到 database_dict final.md
- 字段级业务含义：逐字段对比，以 dict.md 为准
- 表级业务含义：以 dict.md 为准
"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
DICT_FILE = os.path.join(BASE, 'database_dict.md')
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')


def parse_biz_meanings(text):
    """解析MD中每个表的字段业务含义"""
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
        table_biz = ''
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    table_biz = parts[1]
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 7:
                        fname = parts[0]
                        biz = parts[6]
                        fields[fname] = biz
                    i += 1
                break
            elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                break
            else:
                i += 1
        tables[tname] = {'table_biz': table_biz, 'fields': fields}
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables


def main():
    print("=" * 70)
    print("还原 database_dict final.md 中的原始业务含义")
    print("数据源: database_dict.md")
    print("=" * 70)

    # 1. 解析两个文件的业务含义
    dict_text = read_md(DICT_FILE)
    final_text = read_md(FINAL_FILE)

    dict_biz = parse_biz_meanings(dict_text)
    final_lines = final_text.split('\n')

    common_tables = set(dict_biz.keys())
    print(f"dict.md 表数: {len(dict_biz)}")

    # 2. 逐表还原字段业务含义
    field_restored = 0
    table_biz_restored = 0

    # 构建行号到表名的映射
    i = 0
    while i < len(final_lines):
        line = final_lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
            i += 1
            continue
        tname = m.group(1)

        if tname in dict_biz:
            dict_fields = dict_biz[tname]['fields']
            dict_table_biz = dict_biz[tname]['table_biz']

            # 还原表级业务含义
            j = i + 1
            while j < len(final_lines):
                l = final_lines[j].strip()
                if l.startswith('| 业务含义 |') or l.startswith('| 业务含义|'):
                    if dict_table_biz:
                        parts = [p.strip() for p in final_lines[j].split('|')]
                        parts = [p for p in parts if p]
                        if len(parts) >= 2:
                            old_biz = parts[1]
                            if old_biz != dict_table_biz:
                                # 替换业务含义值
                                final_lines[j] = final_lines[j].replace(f'| {old_biz} |', f'| {dict_table_biz} |', 1)
                                table_biz_restored += 1
                    break
                elif l.startswith('| 字段名') or l.startswith('**') or l == '---':
                    break
                j += 1

            # 还原字段业务含义
            j = i + 1
            while j < len(final_lines):
                l = final_lines[j].strip()
                if l.startswith('| 字段名'):
                    j += 1; j += 1  # skip header + separator
                    while j < len(final_lines) and final_lines[j].strip().startswith('|') and '---' not in final_lines[j]:
                        parts = [p.strip() for p in final_lines[j].strip().split('|')]
                        parts = [p for p in parts if p]
                        if len(parts) >= 7:
                            fname = parts[0]
                            if fname in dict_fields:
                                dict_val = dict_fields[fname]
                                current_val = parts[6]
                                if dict_val and dict_val != current_val:
                                    # 替换第7列（业务含义）
                                    # 构建新行
                                    new_parts = [p for p in final_lines[j].split('|')]
                                    # 找到第7个非空列的位置
                                    non_empty_idx = 0
                                    for k in range(len(new_parts)):
                                        if new_parts[k].strip() != '':
                                            non_empty_idx += 1
                                            if non_empty_idx == 7:
                                                new_parts[k] = f' {dict_val} '
                                                break
                                    final_lines[j] = '|'.join(new_parts)
                                    field_restored += 1
                        j += 1
                    break
                elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                    break
                else:
                    j += 1

        # 跳到下一个表
        i += 1
        while i < len(final_lines):
            l = final_lines[i].strip()
            if l == '---' or re.match(r'^#{1,5}\s+', l):
                break
            i += 1

    # 3. 写回
    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(final_lines))

    print(f"\n还原完成:")
    print(f"  字段业务含义还原: {field_restored} 个")
    print(f"  表级业务含义还原: {table_biz_restored} 个")
    print(f"文件已保存: {FINAL_FILE}")


if __name__ == '__main__':
    main()
