#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
将 database_dict final.md 中的字段描述和业务含义还原为 database_dict.md 的原始值
"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
DICT_FILE = os.path.join(BASE, 'database_dict.md')
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')


def parse_fields(text):
    """解析MD中每个表的字段描述和业务含义"""
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
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 7:
                        fname = parts[0]
                        comment = parts[5]
                        biz = parts[6]
                        fields[fname] = (comment, biz)
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


def main():
    print("=" * 70)
    print("还原 final.md 中的字段描述和业务含义（以 dict.md 为准）")
    print("=" * 70)

    dict_text = read_md(DICT_FILE)
    final_lines = read_md(FINAL_FILE).split('\n')
    dict_fields = parse_fields(dict_text)

    comment_restored = 0
    biz_restored = 0

    i = 0
    while i < len(final_lines):
        line = final_lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
            i += 1
            continue
        tname = m.group(1)

        if tname in dict_fields:
            dict_f = dict_fields[tname]
            # 找到字段列表
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
                            if fname in dict_f:
                                dc, db = dict_f[fname]
                                current_comment = parts[5]
                                current_biz = parts[6]

                                need_update = False
                                new_comment = current_comment
                                new_biz = current_biz

                                if dc and dc != current_comment:
                                    new_comment = dc
                                    comment_restored += 1
                                    need_update = True
                                if db and db != current_biz:
                                    new_biz = db
                                    biz_restored += 1
                                    need_update = True

                                if need_update:
                                    # 重建行：按原始分隔符格式
                                    raw = final_lines[j]
                                    # 找到所有 | 的位置，按列替换第6列和第7列
                                    raw_parts = raw.split('|')
                                    # raw_parts: ['', ' col1 ', ' col2 ', ..., '']
                                    # 需要替换第6个和第7个非空列
                                    col_idx = 0
                                    for k in range(len(raw_parts)):
                                        stripped = raw_parts[k].strip()
                                        if stripped != '' or (k > 0 and k < len(raw_parts)-1):
                                            col_idx += 1
                                            if col_idx == 6:  # 字段描述
                                                raw_parts[k] = f' {new_comment} '
                                            elif col_idx == 7:  # 业务含义
                                                raw_parts[k] = f' {new_biz} '
                                    final_lines[j] = '|'.join(raw_parts)
                        j += 1
                    break
                elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                    break
                else:
                    j += 1

        i += 1
        while i < len(final_lines):
            l = final_lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1

    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(final_lines))

    print(f"\n还原完成:")
    print(f"  字段描述还原: {comment_restored} 个")
    print(f"  业务含义还原: {biz_restored} 个")


if __name__ == '__main__':
    main()
