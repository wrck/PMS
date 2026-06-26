#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
从 database_dict.md 提取所有表的字段业务含义信息，保存为 JSON。
"""

import re
import json
import os

MD_PATH = os.path.join(os.path.dirname(__file__), 'database_dict.md')
OUT_DIR = os.path.join(os.path.dirname(__file__), 'schema_data')
OUT_PATH = os.path.join(OUT_DIR, 'existing_biz_meanings.json')


def read_md(path):
    with open(path, 'rb') as f:
        raw = f.read()
    text = raw.decode('utf-8', errors='replace')
    # 统一换行符
    text = text.replace('\r\n', '\n').replace('\r', '\n')
    return text


def parse_tables(text):
    result = {}

    # 按 ### x.y table_name -- 表注释 分割各表
    # 匹配表标题行
    table_heading_re = re.compile(
        r'^###\s+\d+\.\d+\s+(\S+)\s+--\s+(.+)$', re.MULTILINE
    )

    headings = list(table_heading_re.finditer(text))

    for idx, m in enumerate(headings):
        table_name = m.group(1).strip()
        table_comment = m.group(2).strip()

        # 取当前标题到下一个标题之间的内容
        start = m.end()
        end = headings[idx + 1].start() if idx + 1 < len(headings) else len(text)
        section = text[start:end]

        # 提取表级业务含义：属性表中 | 业务含义 | xxx | 行
        table_biz = ''
        biz_match = re.search(r'\|\s*业务含义\s*\|\s*(.+?)\s*\|', section)
        if biz_match:
            table_biz = biz_match.group(1).strip()

        # 提取字段列表
        fields = {}

        # 找到 **字段列表** 之后的内容，直到 **索引列表** 或下一个 ### 或 ---
        field_section_match = re.search(
            r'\*\*字段列表\*\*\s*\n', section
        )
        if not field_section_match:
            continue

        field_section_start = field_section_match.end()
        # 截止到索引列表或章节结束
        field_section_end_match = re.search(
            r'\*\*索引列表\*\*', section[field_section_start:]
        )
        if field_section_end_match:
            field_section = section[field_section_start:field_section_start + field_section_end_match.start()]
        else:
            field_section = section[field_section_start:]

        # 解析字段表行：| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
        # 跳过表头行和分隔行
        for line in field_section.split('\n'):
            line = line.strip()
            if not line or not line.startswith('|'):
                continue
            # 跳过分隔行 |---|---|
            if re.match(r'^\|[\s\-:|]+\|$', line):
                continue
            # 跳过表头行（第一行含"字段名"）
            if '字段名' in line:
                continue

            cells = [c.strip() for c in line.split('|')]
            # cells 首尾为空（因为 | 开头和结尾）
            # 有效列从 cells[1] 开始
            if len(cells) < 8:
                continue

            field_name = cells[1].strip()
            biz_meaning = cells[7].strip() if len(cells) > 7 else ''

            # 只保存业务含义非空的字段
            if field_name and biz_meaning:
                fields[field_name] = biz_meaning

        # 只要有表级业务含义或字段业务含义就记录
        if table_biz or fields:
            result[table_name] = {
                'table_biz_meaning': table_biz,
                'fields': fields
            }

    return result


def main():
    text = read_md(MD_PATH)
    data = parse_tables(text)

    os.makedirs(OUT_DIR, exist_ok=True)
    with open(OUT_PATH, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    # 统计
    total_tables = len(data)
    tables_with_biz = sum(1 for t in data.values() if t['table_biz_meaning'])
    total_fields_with_biz = sum(len(t['fields']) for t in data.values())

    print(f'总表数: {total_tables}')
    print(f'有表级业务含义的表数: {tables_with_biz}')
    print(f'有字段业务含义的字段总数: {total_fields_with_biz}')
    print(f'JSON 已保存至: {OUT_PATH}')


if __name__ == '__main__':
    main()
