#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
用 database_dict_full.md 的内容补充 database_dict final.md 中缺失的表
- 保留 final.md 的格式和结构
- 从 full.md 提取缺失表的完整定义
- 按章节分类插入到正确位置
"""
import re
import os
import json

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')
FULL_FILE = os.path.join(BASE, 'database_dict_full.md')
SCHEMA_DIR = os.path.join(BASE, 'schema_data')


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')


def extract_table_blocks(text):
    """提取MD中每个表的完整定义块，返回 {table_name: (start_line, end_line, block_lines)}"""
    lines = text.split('\n')
    blocks = {}
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m:
            i += 1
            continue
        tname = m.group(1)
        if not tname or len(tname) < 2 or re.search(r'[\u4e00-\u9fff]', tname) or tname.startswith('**') or tname.startswith('第') or tname.startswith('附录'):
            i += 1
            continue

        block_start = i
        # 找到块结束：下一个 --- 或下一个表标题
        block_end = i + 1
        for j in range(i + 1, len(lines)):
            l = lines[j].strip()
            if l == '---':
                block_end = j + 1  # 包含 ---
                break
            m2 = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
            if m2 and m2.group(1) and len(m2.group(1)) >= 2 and not re.search(r'[\u4e00-\u9fff]', m2.group(1)):
                block_end = j
                break
        else:
            block_end = len(lines)

        blocks[tname] = (block_start, block_end, lines[block_start:block_end])
        i = block_end

    return blocks


def classify_table_to_section(table_name):
    """根据表名确定在final.md中应插入的章节位置"""
    # 第一章：项目管理域
    if re.match(r'^pm_project_', table_name):
        return ('第一章', '一、项目管理域')
    if re.match(r'^pm_cl_', table_name):
        return ('第一章', '二、回访管理域')
    if re.match(r'^pm_presales', table_name):
        return ('第一章', '三、售前管理域')
    if re.match(r'^pm_subcontract', table_name) or re.match(r'^pm_sub_', table_name):
        return ('第一章', '四、转包管理域')
    if re.match(r'^prob_', table_name):
        return ('第一章', '五、问题管理域')
    if re.match(r'^fnd_', table_name):
        return ('第一章', '六、基础平台域')

    # 第二章：系统支撑域
    if re.match(r'^ehr_', table_name):
        return ('第二章', '一、EHR组织架构域')
    if re.match(r'^t_', table_name) or table_name in ('role', 'user', 'user_info', 'user_modules', 'user_permissions', 'user_team', 'tb_sys_log'):
        return ('第二章', '二、系统权限域')
    if re.match(r'^pm_order_data', table_name) or re.match(r'^pm_order_line', table_name) or \
       re.match(r'^pm_pb_plan', table_name) or re.match(r'^pm_person_from', table_name) or \
       re.match(r'^pm_presales_lend', table_name) or re.match(r'^pm_project_property_from', table_name) or \
       re.match(r'^pm_project_property_af', table_name) or re.match(r'^pm_project_real_product', table_name) or \
       re.match(r'^pm_project_product_af', table_name) or re.match(r'^pm_project_soleagent_lend', table_name) or \
       re.match(r'^pm_project_market_relations', table_name) or re.match(r'^project_info_from', table_name) or \
       re.match(r'^pm_project_product_config', table_name) or re.match(r'^pm_project_product_lease', table_name) or \
       re.match(r'^pm_project_incident_table', table_name) or re.match(r'^sms_ofst_contract', table_name) or \
       re.match(r'^shipment_barcode', table_name):
        return ('第二章', '三、数据同步中间表域')
    if table_name in ('data_field_relation', 'hexiao', 'transnum', 'sys_state_or_type', 'firebird_operation_log') or \
       re.match(r'^pm_report', table_name) or re.match(r'^pm_workflow', table_name) or re.match(r'^pm_data_refresh', table_name):
        return ('第二章', '四、其他辅助表')

    # 第三章：历史迁移与引擎域
    if re.match(r'^act_', table_name) or re.match(r'^fnd_act_', table_name):
        return ('第三章', '一、Activiti工作流引擎表')
    if re.match(r'^fb_', table_name):
        return ('第三章', '二、Firebird迁移表')
    if re.match(r'^rma_', table_name) or re.match(r'^warehouse', table_name) or \
       re.match(r'^spare_', table_name) or table_name == 'department' or \
       re.match(r'^af_industry_', table_name) or re.match(r'^brw_', table_name) or \
       re.match(r'^app_', table_name) or re.match(r'^mes_', table_name) or \
       re.match(r'^warranty_', table_name) or table_name in ('tx_info', 'bar', 'workflow_info') or \
       re.match(r'^dptech_v_', table_name) or re.match(r'^view_warranty', table_name) or \
       table_name == 'find_in_set_help':
        return ('第三章', '三、RMA/备件/仓库等业务表')

    return ('第三章', '三、RMA/备件/仓库等业务表')  # 默认


def find_section_end(lines, section_header, start=0):
    """找到某个章节的结束位置（下一个同级或更高级章节的开始）"""
    # 先找到章节标题行
    section_start = None
    for i in range(start, len(lines)):
        l = lines[i].strip()
        if section_header in l and (l.startswith('#') or l.startswith('##')):
            section_start = i
            break

    if section_start is None:
        return None

    # 找到下一个同级或更高级章节
    # 判断当前章节的级别
    current_level = len(lines[section_start]) - len(lines[section_start].lstrip('#'))

    for i in range(section_start + 1, len(lines)):
        l = lines[i].strip()
        if not l.startswith('#'):
            continue
        level = len(l) - len(l.lstrip('#'))
        if level <= current_level and l.startswith('#'):
            return i

    return len(lines)


def find_last_table_in_section(lines, section_header, start=0):
    """找到某个章节中最后一个表定义的结束位置"""
    section_start = None
    for i in range(start, len(lines)):
        l = lines[i].strip()
        if section_header in l and (l.startswith('#') or l.startswith('##')):
            section_start = i
            break

    if section_start is None:
        return None

    # 判断当前章节级别
    current_level = len(lines[section_start]) - len(lines[section_start].lstrip('#'))

    # 找到下一个同级或更高级章节
    section_end = len(lines)
    for i in range(section_start + 1, len(lines)):
        l = lines[i].strip()
        if not l.startswith('#'):
            continue
        level = len(l) - len(l.lstrip('#'))
        if level <= current_level:
            section_end = i
            break

    # 在章节范围内找最后一个 --- 分隔线
    last_separator = section_start
    for i in range(section_start, section_end):
        if lines[i].strip() == '---':
            last_separator = i

    return last_separator


def renumber_section(lines, section_start, section_end):
    """重新编号一个章节内的表"""
    counter = 0
    for i in range(section_start, section_end):
        l = lines[i].strip()
        m = re.match(r'^(#{1,5}\s+)([\d.]+\s+)(\S+\s*(?:--.*)?)$', l)
        if m:
            counter += 1
            lines[i] = f"{m.group(1)}{counter} {m.group(3)}"
    return lines


def main():
    print("=" * 70)
    print("补充 database_dict final.md 中缺失的表")
    print("=" * 70)

    # 1. 读取两个文件
    final_text = read_md(FINAL_FILE)
    full_text = read_md(FULL_FILE)

    final_lines = final_text.split('\n')
    full_blocks = extract_table_blocks(full_text)

    # 2. 找出final.md中已有的表
    final_tables = set()
    for line in final_lines:
        line = line.strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) and len(m.group(1)) >= 2 and not re.search(r'[\u4e00-\u9fff]', m.group(1)) and not m.group(1).startswith('**') and not m.group(1).startswith('第') and not m.group(1).startswith('附录'):
            final_tables.add(m.group(1))

    # 3. 找出缺失的表
    missing_tables = set(full_blocks.keys()) - final_tables
    # 排除临时表和 dppms_d365 (数据库名)
    missing_tables = {t for t in missing_tables if not t.startswith(('temp_', 'tmp_')) and t != 'dppms_d365'}

    print(f"final.md 现有表数: {len(final_tables)}")
    print(f"full.md 表数: {len(full_blocks)}")
    print(f"缺失的表数: {len(missing_tables)}")

    if not missing_tables:
        print("无需补充")
        return

    # 4. 按章节分组缺失的表
    missing_by_section = {}
    for tname in sorted(missing_tables):
        chapter, section = classify_table_to_section(tname)
        key = (chapter, section)
        missing_by_section.setdefault(key, []).append(tname)

    # 5. 逐章节插入缺失的表
    for (chapter, section), tables in sorted(missing_by_section.items()):
        print(f"\n  [{chapter} - {section}] 缺失 {len(tables)} 张表:")
        for t in tables:
            print(f"    - {t}")

    # 6. 从后往前插入（避免行号偏移）
    # 先收集所有需要插入的位置
    insertions = []  # (insert_after_line, block_lines)

    for (chapter, section), tables in sorted(missing_by_section.items(), reverse=True):
        # 找到该章节在final.md中的位置
        last_sep = find_last_table_in_section(final_lines, section)
        if last_sep is None:
            print(f"  [警告] 未找到章节: {section}")
            continue

        # 为每个缺失表构建插入块
        for tname in sorted(tables, reverse=True):
            if tname not in full_blocks:
                print(f"  [警告] full.md 中未找到: {tname}")
                continue

            _, _, block = full_blocks[tname]

            # 调整格式：full.md 用 ### 1.1 格式，final.md 用 ### 1.1 格式
            # 去掉编号，后面统一重新编号
            adjusted_block = []
            for bline in block:
                l = bline.strip()
                m = re.match(r'^(#{1,5}\s+)[\d.]+\s+(\S+.*)$', l)
                if m:
                    adjusted_block.append(f"{m.group(1)}{m.group(2)}")
                else:
                    adjusted_block.append(bline)

            # 确保以 --- 结尾
            if adjusted_block and adjusted_block[-1].strip() != '---':
                adjusted_block.append('---')

            insertions.append((last_sep, adjusted_block, tname))

    # 按插入位置从后往前排序
    insertions.sort(key=lambda x: x[0], reverse=True)

    # 执行插入
    for insert_after, block, tname in insertions:
        final_lines[insert_after:insert_after] = [''] + block + ['']

    # 7. 重新编号各章节
    # 找到各章节范围并重新编号
    sections_info = [
        ('一、项目管理域', '第一章'),
        ('二、回访管理域', '第一章'),
        ('三、售前管理域', '第一章'),
        ('四、转包管理域', '第一章'),
        ('五、问题管理域', '第一章'),
        ('六、基础平台域', '第一章'),
        ('一、EHR组织架构域', '第二章'),
        ('二、系统权限域', '第二章'),
        ('三、数据同步中间表域', '第二章'),
        ('四、其他辅助表', '第二章'),
        ('一、Activiti工作流引擎表', '第三章'),
        ('二、Firebird迁移表', '第三章'),
        ('三、RMA/备件/仓库等业务表', '第三章'),
    ]

    for section_name, chapter in sections_info:
        # 找到章节开始
        sec_start = None
        for i, line in enumerate(final_lines):
            if section_name in line and line.strip().startswith('#'):
                sec_start = i
                break
        if sec_start is None:
            continue

        # 找到章节结束（下一个同级##或#标题）
        sec_end = len(final_lines)
        sec_level = len(final_lines[sec_start]) - len(final_lines[sec_start].lstrip('#'))
        for i in range(sec_start + 1, len(final_lines)):
            l = final_lines[i].strip()
            if not l.startswith('#'):
                continue
            level = len(l) - len(l.lstrip('#'))
            if level <= sec_level:
                sec_end = i
                break

        # 重新编号
        counter = 0
        for i in range(sec_start, sec_end):
            l = final_lines[i].strip()
            m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
            if m:
                counter += 1
                final_lines[i] = f"### {counter} {m.group(2)}"

    # 8. 写回文件
    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(final_lines))

    print(f"\n补充完成！已插入 {len(insertions)} 张表")
    print(f"文件已保存: {FINAL_FILE}")


if __name__ == '__main__':
    main()
