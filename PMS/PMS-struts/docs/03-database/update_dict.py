#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
用新的Part3内容替换 database_dict.md 中的历史迁移与引擎域部分
"""

import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DICT_FILE = os.path.join(BASE_DIR, 'database_dict.md')
PART3_FILE = os.path.join(BASE_DIR, 'database_dict_part3.md')

def read_file_binary(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    text = data.decode('utf-8', errors='replace')
    text = text.replace('\r\n', '\n').replace('\r', '\n')
    return text

def main():
    print("读取 database_dict.md...")
    dict_text = read_file_binary(DICT_FILE)
    dict_lines = dict_text.split('\n')
    print(f"  总行数: {len(dict_lines)}")

    print("读取新的 Part3...")
    part3_text = read_file_binary(PART3_FILE)
    part3_lines = part3_text.split('\n')
    print(f"  Part3行数: {len(part3_lines)}")

    # 找到第三章的起始行
    ch3_start = None
    for i, line in enumerate(dict_lines):
        if line.strip().startswith('# 第三章') or line.strip().startswith('# 第三章：'):
            ch3_start = i
            break

    if ch3_start is None:
        print("错误: 未找到第三章起始位置")
        return

    print(f"  第三章起始行: {ch3_start}")

    # 找到Part3中实际表定义的起始位置（跳过文件头和目录）
    # Part3中 "# 一、Activiti工作流引擎表" 是实际内容开始
    part3_content_start = None
    for i, line in enumerate(part3_lines):
        if line.strip().startswith('# 一、Activiti'):
            part3_content_start = i
            break

    if part3_content_start is None:
        print("错误: 未找到Part3内容起始位置")
        return

    print(f"  Part3内容起始行: {part3_content_start}")

    # 构建新的database_dict.md
    # 保留第一章和第二章，替换第三章
    new_lines = dict_lines[:ch3_start]  # 第一章+第二章
    new_lines.append('')  # 空行
    new_lines.append('# 第三章：历史迁移与引擎域')
    new_lines.append('')
    new_lines.append('> 覆盖范围：Activiti工作流引擎(act_*)、Firebird迁移表(fb_*)、RMA/备件/仓库业务表、临时表(temp_*/tmp_*)、视图(VIEW)')
    new_lines.append('> 格式标准：与第一章、第二章保持完全一致（7列字段表、4列索引表、属性表格）')
    new_lines.append('')
    new_lines.append('---')
    new_lines.append('')

    # 添加Part3的内容（从"# 一、Activiti"开始）
    new_lines.extend(part3_lines[part3_content_start:])

    # 写入文件
    new_text = '\n'.join(new_lines)
    with open(DICT_FILE, 'w', encoding='utf-8') as f:
        f.write(new_text)

    print(f"\n写入完成: {DICT_FILE}")
    print(f"  新行数: {len(new_lines)}")

    # 统计
    table_count = new_text.count('### ')
    field_count = len([l for l in new_lines if l.strip().startswith('|') and '---' not in l and '字段名' not in l and '属性' not in l and '索引名' not in l and '外键名' not in l])
    print(f"  表/视图数: {table_count}")
    print(f"  字段定义行: {field_count}")

if __name__ == '__main__':
    main()
