#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
将历史迁移与引擎域中与项目相关的表移至项目管理域
"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')

# 需要移动到项目管理域的表
# 分类依据：与项目维保、保障、发货、渠道等项目管理业务直接相关
TABLES_TO_MOVE = {
    # 维保相关 -> 项目管理域（新增"七、维保管理域"）
    'warranty_info': '七、维保管理域',
    'warranty_change_logs': '七、维保管理域',
    'view_warranty': '七、维保管理域',
    'view_warranty_contract_state': '七、维保管理域',
    'view_warranty_temp': '七、维保管理域',
    'view_warranty_with_presales': '七、维保管理域',
    'fb_warranty_grade': '七、维保管理域',

    # 备件/保障相关 -> 项目管理域（归入"七、维保管理域"）
    'spare_parts': '七、维保管理域',
    'spare_parts_applicant': '七、维保管理域',

    # 安全行业资产/项目关联 -> 项目管理域（归入"一、项目管理域"）
    'af_industry_asset': '一、项目管理域',
    'af_industry_asset_leak_relation': '一、项目管理域',
    'af_industry_asset_project_relation': '一、项目管理域',
    'af_industry_leak': '一、项目管理域',
    'af_industry_leak_warning': '一、项目管理域',

    # 项目渠道/发货辅助 -> 项目管理域（归入"一、项目管理域"）
    'addressee_info': '一、项目管理域',
    'agent_info': '一、项目管理域',
    'back_type': '一、项目管理域',
    'serve_type': '一、项目管理域',
    'tain_type': '一、项目管理域',
    'dptech_v_project_product_config_level_info': '一、项目管理域',
    'find_in_set_help': '一、项目管理域',

    # 工作流信息 -> 项目管理域
    'workflow_info': '一、项目管理域',
}


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')


def find_table_block(lines, tname, start=0):
    """找到表定义块的起止行"""
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


def find_section_insert_point(lines, section_name):
    """找到某个子域的最后一个表结束位置"""
    section_start = None
    for i, line in enumerate(lines):
        l = line.strip()
        if section_name in l and l.startswith('#'):
            section_start = i
            break
    if section_start is None:
        return None

    # 找到下一个同级或更高级章节
    sec_level = len(lines[section_start]) - len(lines[section_start].lstrip('#'))
    section_end = len(lines)
    for i in range(section_start + 1, len(lines)):
        l = lines[i].strip()
        if not l.startswith('#'):
            continue
        level = len(l) - len(l.lstrip('#'))
        if level <= sec_level:
            section_end = i
            break

    # 找最后一个 ---
    last_sep = section_start
    for i in range(section_start, section_end):
        if lines[i].strip() == '---':
            last_sep = i

    return last_sep


def renumber_section(lines, section_name):
    """重新编号某个子域内的表"""
    sec_start = None
    for i, line in enumerate(lines):
        l = line.strip()
        if section_name in l and l.startswith('#'):
            sec_start = i
            break
    if sec_start is None:
        return

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


def main():
    print("=" * 70)
    print("将历史迁移与引擎域中与项目相关的表移至项目管理域")
    print("=" * 70)

    text = read_md(FINAL_FILE)
    lines = text.split('\n')

    # 1. 提取需要移动的表块
    moved_blocks = {}
    for tname in TABLES_TO_MOVE:
        start, end = find_table_block(lines, tname)
        if start is not None:
            moved_blocks[tname] = (start, end, list(lines[start:end]))
            print(f"  找到: {tname} (行 {start}-{end})")
        else:
            print(f"  [未找到] {tname}")

    # 2. 从后往前删除原位置的表块
    sorted_removals = sorted(moved_blocks.values(), key=lambda x: x[0], reverse=True)
    for start, end, block in sorted_removals:
        # 删除块及前面的空行
        del_start = start
        while del_start > 0 and lines[del_start - 1].strip() == '':
            del_start -= 1
        del lines[del_start:end]

    # 3. 按目标子域分组
    target_sections = {}
    for tname, target in TABLES_TO_MOVE.items():
        if tname in moved_blocks:
            target_sections.setdefault(target, []).append(tname)

    # 4. 在目标子域末尾插入
    # 先处理"一、项目管理域"
    if '一、项目管理域' in target_sections:
        insert_point = find_section_insert_point(lines, '一、项目管理域')
        if insert_point is not None:
            insert_lines = []
            for tname in sorted(target_sections['一、项目管理域']):
                if tname in moved_blocks:
                    block = moved_blocks[tname][2]
                    # 去掉编号
                    adjusted = []
                    for bline in block:
                        l = bline.strip()
                        m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
                        if m:
                            adjusted.append(f"### {m.group(2)}")
                        else:
                            adjusted.append(bline)
                    insert_lines.append('')
                    insert_lines.extend(adjusted)
                    insert_lines.append('')
                    print(f"  [插入] {tname} -> 一、项目管理域")
            lines[insert_point + 1:insert_point + 1] = insert_lines

    # 创建"七、维保管理域"（在六、基础平台域之后）
    if '七、维保管理域' in target_sections:
        # 找到"六、基础平台域"的结束位置
        insert_point = find_section_insert_point(lines, '六、基础平台域')
        if insert_point is not None:
            # 找到第二章的开始位置
            ch2_start = None
            for i, line in enumerate(lines):
                if line.strip().startswith('# 第二章'):
                    ch2_start = i
                    break

            insert_lines = ['']
            insert_lines.append('## 七、维保管理域 (warranty/spare)')
            insert_lines.append('')
            insert_lines.append('> 包含维保信息、备件保障、维保视图等与项目维保管理直接相关的业务表。')
            insert_lines.append('')
            insert_lines.append('---')
            insert_lines.append('')

            for tname in sorted(target_sections['七、维保管理域']):
                if tname in moved_blocks:
                    block = moved_blocks[tname][2]
                    adjusted = []
                    for bline in block:
                        l = bline.strip()
                        m = re.match(r'^(#{3,5}\s+)[\d.]+\s+(\S+.*)$', l)
                        if m:
                            adjusted.append(f"### {m.group(2)}")
                        else:
                            adjusted.append(bline)
                    insert_lines.append('')
                    insert_lines.extend(adjusted)
                    insert_lines.append('')
                    print(f"  [插入] {tname} -> 七、维保管理域")

            # 在第二章之前插入
            if ch2_start is not None:
                lines[ch2_start:ch2_start] = insert_lines
            else:
                lines[insert_point + 1:insert_point + 1] = insert_lines

    # 5. 重新编号所有受影响的子域
    for sec_name in ['一、项目管理域', '二、回访管理域', '三、售前管理域', '四、转包管理域',
                     '五、问题管理域', '六、基础平台域', '七、维保管理域',
                     '一、Activiti工作流引擎表', '二、Firebird迁移表', '三、RMA/备件/仓库等业务表']:
        renumber_section(lines, sec_name)

    # 6. 更新目录
    # 找到目录区域并更新
    for i, line in enumerate(lines):
        if line.strip().startswith('- [6. 基础平台'):
            # 在基础平台域后添加维保管理域
            lines[i] = line.rstrip() + '\n- [7. 维保管理 (warranty/spare)](#七维保管理域-warrantyspare)'
            break

    # 7. 写回
    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n移动完成！")
    print(f"  移至一、项目管理域: {len(target_sections.get('一、项目管理域', []))} 张表")
    print(f"  移至七、维保管理域: {len(target_sections.get('七、维保管理域', []))} 张表")


if __name__ == '__main__':
    main()
