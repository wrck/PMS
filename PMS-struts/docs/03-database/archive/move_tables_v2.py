#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
重新分类：将历史迁移与引擎域中与项目相关的表移至项目管理域末尾
不改变项目管理域原有结构顺序，追加在第六子域之后、第二章之前
"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')

# 历史迁移与引擎域中与项目相关的表（需移至项目管理域）
# 分类依据：
# - 维保/备件/保障：与项目维保管理直接相关
# - 安全行业资产/漏洞：与项目安全资产关联
# - 项目辅助表：项目产品配置、工作流等
PROJECT_RELATED = [
    # RMA/备件/仓库域中的项目相关表
    'addressee_info',
    'af_industry_asset',
    'af_industry_asset_leak_relation',
    'af_industry_asset_project_relation',
    'af_industry_leak',
    'af_industry_leak_warning',
    'agent_info',
    'back_type',
    'serve_type',
    'tain_type',
    'spare_parts',
    'spare_parts_applicant',
    'warranty_info',
    'warranty_change_logs',
    'workflow_info',
    'dptech_v_project_product_config_level_info',
    'find_in_set_help',
    'view_warranty',
    'view_warranty_contract_state',
    'view_warranty_temp',
    'view_warranty_with_presales',
    # Firebird域中的项目相关表
    'fb_warranty_grade',
]


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')


def find_table_block(lines, tname, start=0):
    """找到表定义块的起止行（含前置空行和---分隔线）"""
    for i in range(start, len(lines)):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) == tname:
            block_start = i
            block_end = i + 1
            for j in range(i + 1, len(lines)):
                lj = lines[j].strip()
                if lj == '---':
                    block_end = j + 1
                    break
                m2 = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', lj)
                if m2 and m2.group(1) and len(m2.group(1)) >= 2 and not re.search(r'[\u4e00-\u9fff]', m2.group(1)):
                    block_end = j
                    break
            else:
                block_end = len(lines)
            return block_start, block_end
    return None, None


def main():
    print("=" * 70)
    print("重新分类：历史迁移与引擎域 → 项目管理域（追加在末尾）")
    print("=" * 70)

    text = read_md(FINAL_FILE)
    lines = text.split('\n')

    # 1. 找到第二章的起始位置（项目管理域的末尾）
    ch2_start = None
    for i, line in enumerate(lines):
        if line.strip().startswith('# 第二章'):
            ch2_start = i
            break
    if ch2_start is None:
        print("错误：未找到第二章起始位置")
        return
    print(f"第二章起始行: {ch2_start}")

    # 2. 提取需要移动的表块
    moved_blocks = {}
    for tname in PROJECT_RELATED:
        start, end = find_table_block(lines, tname)
        if start is not None:
            # 提取块内容，去掉编号
            block = []
            for bline in lines[start:end]:
                l = bline.strip()
                m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
                if m:
                    block.append(f"### {m.group(2)}")
                else:
                    block.append(bline)
            moved_blocks[tname] = (start, end, block)
            print(f"  找到: {tname} (行 {start}-{end})")
        else:
            print(f"  [未找到] {tname}")

    # 3. 从后往前删除原位置的表块（避免索引偏移）
    sorted_removals = sorted(moved_blocks.values(), key=lambda x: x[0], reverse=True)
    for start, end, block in sorted_removals:
        # 删除块及前面的空行
        del_start = start
        while del_start > 0 and lines[del_start - 1].strip() == '':
            del_start -= 1
        del lines[del_start:end]

    # 4. 重新定位第二章（因为删除了行）
    ch2_start = None
    for i, line in enumerate(lines):
        if line.strip().startswith('# 第二章'):
            ch2_start = i
            break

    # 5. 在第二章之前插入新子域和表
    # 按业务分组
    warranty_tables = ['warranty_info', 'warranty_change_logs', 'spare_parts', 'spare_parts_applicant',
                       'view_warranty', 'view_warranty_contract_state', 'view_warranty_temp',
                       'view_warranty_with_presales', 'fb_warranty_grade']
    asset_tables = ['af_industry_asset', 'af_industry_asset_leak_relation',
                    'af_industry_asset_project_relation', 'af_industry_leak', 'af_industry_leak_warning']
    aux_tables = ['addressee_info', 'agent_info', 'back_type', 'serve_type', 'tain_type',
                  'workflow_info', 'dptech_v_project_product_config_level_info', 'find_in_set_help']

    insert_lines = []

    # 七、维保管理域
    insert_lines.append('')
    insert_lines.append('## 七、维保管理域 (warranty/spare)')
    insert_lines.append('')
    insert_lines.append('> 包含维保信息、备件保障、维保视图等与项目维保管理直接相关的业务表。')
    insert_lines.append('')
    insert_lines.append('---')
    for tname in warranty_tables:
        if tname in moved_blocks:
            insert_lines.append('')
            insert_lines.extend(moved_blocks[tname][2])
            print(f"  [插入] {tname} -> 七、维保管理域")

    # 八、安全行业资产管理域
    insert_lines.append('')
    insert_lines.append('## 八、安全行业资产管理域 (af_industry)')
    insert_lines.append('')
    insert_lines.append('> 包含安全行业资产、漏洞预警及项目关联等业务表。')
    insert_lines.append('')
    insert_lines.append('---')
    for tname in asset_tables:
        if tname in moved_blocks:
            insert_lines.append('')
            insert_lines.extend(moved_blocks[tname][2])
            print(f"  [插入] {tname} -> 八、安全行业资产管理域")

    # 九、项目辅助表
    insert_lines.append('')
    insert_lines.append('## 九、项目辅助表')
    insert_lines.append('')
    insert_lines.append('> 包含项目渠道、发货、工作流等辅助业务表。')
    insert_lines.append('')
    insert_lines.append('---')
    for tname in aux_tables:
        if tname in moved_blocks:
            insert_lines.append('')
            insert_lines.extend(moved_blocks[tname][2])
            print(f"  [插入] {tname} -> 九、项目辅助表")

    insert_lines.append('')

    # 在第二章之前插入
    lines[ch2_start:ch2_start] = insert_lines

    # 6. 重新编号项目管理域内所有子域的表
    # 找到第一章的所有子域
    ch1_start = None
    ch1_end = None
    for i, line in enumerate(lines):
        if line.strip().startswith('# 第一章'):
            ch1_start = i
        elif line.strip().startswith('# 第二章'):
            ch1_end = i
            break

    # 对每个子域重新编号
    sub_sections = []
    for i in range(ch1_start, ch1_end):
        l = lines[i].strip()
        if re.match(r'^## [一二三四五六七八九十]+、', l):
            sub_sections.append(i)

    for sec_start in sub_sections:
        # 找到子域结束
        sec_idx = sub_sections.index(sec_start)
        if sec_idx + 1 < len(sub_sections):
            sec_end = sub_sections[sec_idx + 1]
        else:
            sec_end = ch1_end

        counter = 0
        for i in range(sec_start, sec_end):
            l = lines[i].strip()
            m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
            if m:
                counter += 1
                lines[i] = f"### {counter} {m.group(2)}"

    # 7. 更新目录
    for i, line in enumerate(lines):
        if line.strip().startswith('- [6. 基础平台'):
            # 在基础平台域后添加新子域
            new_lines = [
                line.rstrip(),
                '- [7. 维保管理 (warranty/spare)](#七维保管理域-warrantyspare)',
                '- [8. 安全行业资产管理 (af_industry)](#八安全行业资产管理域-af_industry)',
                '- [9. 项目辅助表](#九项目辅助表)',
            ]
            lines[i:i+1] = new_lines
            break

    # 8. 写回
    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n移动完成！")
    print(f"  七、维保管理域: {sum(1 for t in warranty_tables if t in moved_blocks)} 张表")
    print(f"  八、安全行业资产管理域: {sum(1 for t in asset_tables if t in moved_blocks)} 张表")
    print(f"  九、项目辅助表: {sum(1 for t in aux_tables if t in moved_blocks)} 张表")
    print(f"  原有子域结构未改变")


if __name__ == '__main__':
    main()
