#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
修复 database_dict.md 第一章中13个补充表的旧格式
将6列字段表转为7列，5列索引表转为4列，移除约束信息表
"""

import re
import os

DICT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict.md')

def read_file(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    text = data.decode('utf-8', errors='replace')
    text = text.replace('\r\n', '\n').replace('\r', '\n')
    return text

def fix_old_format_tables(text):
    """修复旧格式的表定义"""
    lines = text.split('\n')
    result = []
    i = 0

    while i < len(lines):
        line = lines[i]

        # 检测6列字段表头: | 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
        if '| 字段名 | 数据类型 | 非空 |' in line:
            # 替换为7列标准表头
            result.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
            i += 1
            # 跳过分隔行
            if i < len(lines) and '|--------' in lines[i]:
                result.append('|--------|----------|------|--------|------|----------|----------|')
                i += 1
            # 处理字段行：6列 -> 7列
            while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                parts = [p.strip() for p in lines[i].split('|')]
                parts = [p for p in parts if p]
                if len(parts) == 6:
                    # 6列: 字段名|数据类型|非空|默认值|字段注释|业务含义
                    # -> 7列: 字段名|数据类型|可空|默认值|约束|字段描述|业务含义
                    fname = parts[0]
                    ftype = parts[1]
                    fnullable_raw = parts[2]  # 非空: YES/NO
                    # "非空"列: YES=非空(NO nullable), NO=可空(YES nullable)
                    fnullable = 'NO' if fnullable_raw == 'YES' else 'YES'
                    fdefault = parts[3]
                    fcomment = parts[4]  # 字段注释 -> 字段描述
                    fbiz = parts[5]  # 业务含义

                    # 从字段注释中提取约束信息
                    fconstraint = '-'
                    if 'PRI' in fcomment or '主键' in fcomment:
                        fconstraint = 'PRI'
                        if 'auto_increment' in fdefault or '自增' in fcomment:
                            fconstraint = 'PRI, auto_increment'
                    elif 'MUL' in fcomment:
                        fconstraint = 'MUL'

                    result.append(f'| {fname} | {ftype} | {fnullable} | {fdefault} | {fconstraint} | {fcomment} | {fbiz} |')
                else:
                    result.append(lines[i])
                i += 1
            continue

        # 检测约束信息表: **约束信息**
        if lines[i].strip() == '**约束信息**':
            # 跳过整个约束信息表
            i += 1
            # 跳过表头和分隔行
            while i < len(lines) and (lines[i].strip().startswith('|') or lines[i].strip() == ''):
                if not lines[i].strip().startswith('|'):
                    i += 1
                    continue
                parts = [p.strip() for p in lines[i].split('|')]
                parts = [p for p in parts if p]
                # 如果不是约束信息表的行，停止跳过
                if len(parts) >= 6 and parts[0] in ('约束类型', 'PRIMARY KEY', 'UNIQUE', 'FOREIGN KEY', 'KEY', 'INDEX'):
                    i += 1
                    continue
                else:
                    break
            continue

        # 检测5列索引表头: | 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
        if '| 索引名 | 列 | 唯一性 | 索引类型 |' in line:
            # 替换为4列标准表头
            result.append('| 索引名 | 索引类型 | 唯一性 | 索引字段 |')
            i += 1
            # 跳过分隔行
            if i < len(lines) and '|--------' in lines[i]:
                result.append('|--------|----------|--------|----------|')
                i += 1
            # 处理索引行：5列 -> 4列
            while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                parts = [p.strip() for p in lines[i].split('|')]
                parts = [p for p in parts if p]
                if len(parts) == 5:
                    # 5列: 索引名|列|唯一性|索引类型|用途说明
                    # -> 4列: 索引名|索引类型|唯一性|索引字段
                    iname = parts[0]
                    icolumns = parts[1]
                    iunique = parts[2]
                    itype = parts[3]
                    # iusage = parts[4]  # 用途说明，丢弃
                    result.append(f'| {iname} | {itype} | {iunique} | {icolumns} |')
                else:
                    result.append(lines[i])
                i += 1
            continue

        result.append(line)
        i += 1

    return '\n'.join(result)


def main():
    print("读取 database_dict.md...")
    text = read_file(DICT_FILE)
    print(f"  原始行数: {len(text.split(chr(10)))}")

    # 统计旧格式数量
    old_field_count = text.count('| 字段名 | 数据类型 | 非空 |')
    old_index_count = text.count('| 索引名 | 列 | 唯一性 | 索引类型 |')
    constraint_count = text.count('**约束信息**')
    print(f"  旧6列字段表: {old_field_count}")
    print(f"  旧5列索引表: {old_index_count}")
    print(f"  约束信息表: {constraint_count}")

    print("\n修复旧格式...")
    fixed_text = fix_old_format_tables(text)

    # 统计修复后
    new_field_count = fixed_text.count('| 字段名 | 数据类型 | 非空 |')
    new_index_count = fixed_text.count('| 索引名 | 列 | 唯一性 | 索引类型 |')
    new_constraint_count = fixed_text.count('**约束信息**')
    print(f"  修复后旧6列字段表: {new_field_count}")
    print(f"  修复后旧5列索引表: {new_index_count}")
    print(f"  修复后约束信息表: {new_constraint_count}")

    print("\n写入文件...")
    with open(DICT_FILE, 'w', encoding='utf-8') as f:
        f.write(fixed_text)
    print(f"  写入行数: {len(fixed_text.split(chr(10)))}")


if __name__ == '__main__':
    main()
