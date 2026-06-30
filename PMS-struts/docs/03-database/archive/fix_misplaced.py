#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
修正 database_dict final.md 中错位的表
将 pm_presales_lend_*_from_* 和 pm_project_property_from_sms_history_bak
从第一章移到第二章数据同步中间表域
"""
import re
import os

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')


def main():
    text = read_md(FINAL_FILE)
    lines = text.split('\n')

    # 需要从售前管理域移到数据同步中间表域的表
    tables_to_move = [
        'pm_presales_lend_2_rma_from_sms_history',
        'pm_presales_lend_2_sale_from_sms_history',
        'pm_presales_lend_info_from_crm',
        'pm_presales_lend_info_from_sms_history',
        'pm_presales_lend_order_from_sms',
        'pm_presales_lend_order_from_sms_history',
        'pm_presales_lend_product_from_sms_history',
    ]

    # 需要从项目管理域移到数据同步中间表域的表
    tables_to_move_2 = [
        'pm_project_property_from_sms_history_bak',
    ]

    all_tables_to_move = set(tables_to_move + tables_to_move_2)

    # 1. 找到并提取这些表的完整块
    moved_blocks = {}  # tname -> (start_line, end_line, block_lines)
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m:
            i += 1
            continue
        tname = m.group(1)
        if tname not in all_tables_to_move:
            i += 1
            continue

        block_start = i
        block_end = i + 1
        for j in range(i + 1, len(lines)):
            l = lines[j].strip()
            if l == '---':
                block_end = j + 1
                break
            m2 = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
            if m2 and m2.group(1) and len(m2.group(1)) >= 2 and not re.search(r'[\u4e00-\u9fff]', m2.group(1)):
                block_end = j
                break
        else:
            block_end = len(lines)

        moved_blocks[tname] = (block_start, block_end, lines[block_start:block_end])
        i = block_end

    print(f"找到需要移动的表: {len(moved_blocks)}")
    for tname, (start, end, _) in sorted(moved_blocks.items(), key=lambda x: x[1][0]):
        print(f"  {tname}: 行 {start}-{end}")

    # 2. 从后往前删除这些块（避免行号偏移）
    sorted_blocks = sorted(moved_blocks.values(), key=lambda x: x[0], reverse=True)
    for start, end, block in sorted_blocks:
        # 删除块，同时清理前后空行
        del lines[start:end]
        # 清理前面的空行
        while start > 0 and lines[start - 1].strip() == '':
            del lines[start - 1]
            start -= 1

    # 3. 找到数据同步中间表域的最后一个表结束位置
    # 搜索 "三、数据同步中间表域" 标题
    sync_section_start = None
    for i, line in enumerate(lines):
        if '数据同步中间表域' in line and line.strip().startswith('#'):
            sync_section_start = i
            break

    if sync_section_start is None:
        print("未找到数据同步中间表域！")
        return

    # 找到该域中最后一个 --- 分隔线（在下一个同级章节之前）
    sync_level = len(lines[sync_section_start]) - len(lines[sync_section_start].lstrip('#'))
    sync_section_end = len(lines)
    for i in range(sync_section_start + 1, len(lines)):
        l = lines[i].strip()
        if not l.startswith('#'):
            continue
        level = len(l) - len(l.lstrip('#'))
        if level <= sync_level:
            sync_section_end = i
            break

    # 找最后一个 ---
    last_sep = sync_section_start
    for i in range(sync_section_start, sync_section_end):
        if lines[i].strip() == '---':
            last_sep = i

    print(f"\n数据同步中间表域: 行 {sync_section_start}-{sync_section_end}")
    print(f"最后分隔线: 行 {last_sep}")

    # 4. 在最后分隔线后插入移动的表
    insert_blocks = []
    for tname in sorted(all_tables_to_move):
        if tname in moved_blocks:
            _, _, block = moved_blocks[tname]
            # 去掉编号
            adjusted = []
            for bline in block:
                l = bline.strip()
                m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
                if m:
                    adjusted.append(f"### {m.group(2)}")
                else:
                    adjusted.append(bline)
            insert_blocks.append(adjusted)

    # 插入
    insert_lines = []
    for block in insert_blocks:
        insert_lines.append('')
        insert_lines.extend(block)
        insert_lines.append('')

    lines[last_sep + 1:last_sep + 1] = insert_lines

    # 5. 重新编号所有章节
    sections = [
        '一、项目管理域',
        '二、回访管理域',
        '三、售前管理域',
        '四、转包管理域',
        '五、问题管理域',
        '六、基础平台域',
        '一、EHR组织架构域',
        '二、系统权限域',
        '三、数据同步中间表域',
        '四、其他辅助表',
        '一、Activiti工作流引擎表',
        '二、Firebird迁移表',
        '三、RMA/备件/仓库等业务表',
    ]

    for sec_name in sections:
        sec_start = None
        for i, line in enumerate(lines):
            if sec_name in line and line.strip().startswith('#'):
                sec_start = i
                break
        if sec_start is None:
            continue

        sec_level = len(lines[sec_start]) - len(lines[sec_start].lstrip('#'))
        sec_end = len(lines)
        for i in range(sec_start + 1, len(lines)):
            l = lines[i].strip()
            if not l.startswith('#'):
                continue
            level = len(l) - len(l.lstrip('#'))
            if level <= sec_level:
                sec_end = i
                break

        counter = 0
        for i in range(sec_start, sec_end):
            l = lines[i].strip()
            m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
            if m:
                counter += 1
                lines[i] = f"### {counter} {m.group(2)}"

    # 6. 写回
    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n修正完成！已移动 {len(moved_blocks)} 张表到数据同步中间表域")
    print(f"文件已保存: {FINAL_FILE}")


if __name__ == '__main__':
    main()
