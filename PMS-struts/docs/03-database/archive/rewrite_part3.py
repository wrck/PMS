#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
重写 database_dict_part3.md 为标准格式
数据来源：
1. act_* 表：从第一个代理的输出（含完整业务含义）
2. fb_*/RMA/临时表/视图：从数据库新查询的 db_dictionary_dppms_d365.md（标准7列格式，但业务含义需补全）
3. 原有 Part3 中的业务含义作为补充
"""

import re
import json
import os

# 文件路径
BASE_DIR = os.path.dirname(os.path.abspath(__file__))  # .../docs/03-database
DOCS_DIR = os.path.dirname(BASE_DIR)  # .../docs
PROJECT_DIR = os.path.dirname(DOCS_DIR)  # .../PMS-struts
NEW_DICT_FILE = os.path.join(PROJECT_DIR, 'db_dictionary_dppms_d365.md')
OLD_PART3_FILE = os.path.join(BASE_DIR, 'database_dict_part3.md')
OLD_DICT_FILE = os.path.join(BASE_DIR, 'database_dict.md')  # 合并后的完整字典（含旧格式历史迁移域）
ACT_DICT_FILE = os.path.join(PROJECT_DIR, 'act_tables_dict.txt')
ACT_DATA_FILE = os.path.join(PROJECT_DIR, 'act_tables_data.json')
VIEWS_DESC_FILE = os.path.join(PROJECT_DIR, 'docs', 'temp', 'views_describe.json')
VIEWS_CREATE_FILE = os.path.join(PROJECT_DIR, 'docs', 'temp', 'views_create.json')
TEMP_COLUMNS_FILE = os.path.join(PROJECT_DIR, 'docs', 'temp', 'temp_tables_columns.json')
TEMP_INDEXES_FILE = os.path.join(PROJECT_DIR, 'docs', 'temp', 'temp_tables_indexes.json')
TEMP_BASIC_FILE = os.path.join(PROJECT_DIR, 'docs', 'temp', 'temp_tables_basic.json')
OUTPUT_FILE = os.path.join(BASE_DIR, 'database_dict_part3.md')

# 表分类定义
ACT_TABLES = [
    'act_evt_log', 'act_ge_bytearray', 'act_ge_property', 'act_hi_actinst',
    'act_hi_attachment', 'act_hi_comment', 'act_hi_detail', 'act_hi_identitylink',
    'act_hi_procinst', 'act_hi_taskinst', 'act_hi_varinst', 'act_id_group',
    'act_id_info', 'act_id_membership', 'act_id_user', 'act_procdef_info',
    'act_re_deployment', 'act_re_model', 'act_re_procdef', 'act_ru_event_subscr',
    'act_ru_execution', 'act_ru_identitylink', 'act_ru_job', 'act_ru_task',
    'act_ru_task_callback_task_w04649', 'act_ru_variable'
]

FB_TABLES = [
    'fb_contract', 'fb_ft_result1', 'fb_ft_result2', 'fb_items', 'fb_items2',
    'fb_market_system', 'fb_office_relationship', 'fb_service', 'fb_shipment',
    'fb_shipment_barcode', 'fb_shipment_barcode_change_log', 'fb_shipment_barcode_order_line',
    'fb_shipment_barcode_relation', 'fb_soft_version', 'fb_warranty_grade'
]

TEMP_TABLES = [
    'temp_contract_market_system', 'temp_max_ppfs', 'temp_project_sales_change',
    'temp_query_shipment', 'temp_query_shipment_barcode', 'tmp_tb_contract_shipment',
    'tmp_tb_project_contract', 'tmp_tb_project_filtered', 'tmp_tb_project_shipment',
    'tmp_tb_view_shipment_ems_4_pm', 'tmp_tb_view_shipment_info_4_pm'
]

# RMA/备件/仓库等业务表（从Part3原文件中提取）
RMA_TABLES = [
    'addressee_info', 'af_industry_asset', 'af_industry_asset_leak_relation',
    'af_industry_asset_project_relation', 'af_industry_leak', 'af_industry_leak_warning',
    'agent_info', 'app_accessory_info', 'app_comment', 'app_spare_part',
    'back_type', 'bar', 'brw_app_info', 'brw_spare_info',
    'department', 'dp_erp_purchase_order_header', 'dp_erp_purchase_order_line',
    'dp_erp_purchase_receipt_header', 'dp_erp_purchase_receipt_line',
    'fnd_company', 'firebird_operation_log',
    'rma_applicant', 'rma_app_info', 'rma_bar', 'rma_info2mes_result',
    'rma_repair_report_from_mes', 'rma_spare_info',
    'role', 'serve_type', 'spare_parts', 'spare_parts_applicant',
    'sys_state_or_type', 'tain_type', 'tb_sys_log',
    'tx_info', 'user', 'user_info', 'user_team',
    'warehouse', 'warehouse_info', 'warehouse_info_detail',
    'warranty_change_logs', 'warranty_info', 'workflow_info'
]

# 视图列表（从数据库查询获取）
VIEW_TABLES = []  # 将从JSON文件加载


def read_file(filepath):
    if not os.path.exists(filepath):
        print(f"   [WARN] File not found: {filepath}")
        return ''
    with open(filepath, 'rb') as f:
        data = f.read()
    print(f"   [DEBUG] Read {len(data)} bytes from {filepath}")
    # 统一换行符：处理 \r\n, \r, \n
    text = data.decode('utf-8', errors='replace')
    text = text.replace('\r\n', '\n').replace('\r', '\n')
    return text


def parse_new_dict_tables(content, table_names):
    """从新查询的数据字典中解析指定表的标准格式内容"""
    tables = {}
    lines = content.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        # 匹配 ### 序号 表名 -- 注释
        m = re.match(r'^###\s+\d+\s+(\S+)\s*(?:--\s*(.*))?$', line)
        if not m:
            i += 1
            continue
        table_name = m.group(1)
        if table_name not in table_names:
            i += 1
            continue

        # 收集整个表定义直到下一个 ### 或文件结束
        start = i
        i += 1
        while i < len(lines):
            if re.match(r'^###\s+\d+\s+\S+', lines[i].strip()):
                break
            i += 1

        table_content = '\n'.join(lines[start:i])
        tables[table_name] = table_content

    return tables


def parse_old_part3_biz_meanings(content):
    """从旧Part3中提取每张表的业务含义映射"""
    biz_map = {}  # table_name -> biz_meaning
    field_biz_map = {}  # table_name -> {field_name -> biz_meaning}

    lines = content.split('\n')
    current_table = None
    current_fields = {}

    for line in lines:
        s = line.strip()

        # 匹配表标题
        m = re.match(r'^###\s+(\S+)', s)
        if m:
            # 保存上一个表的字段
            if current_table:
                field_biz_map[current_table] = current_fields
            current_table = m.group(1)
            current_fields = {}

            # 提取业务含义
            m_biz = re.search(r'\*\*业务含义\*\*[：:]\s*(.+?)(?:\s*\|\s*\*\*|$)', s)
            if m_biz:
                biz_map[current_table] = m_biz.group(1).strip()
            continue

        # 匹配行内属性中的业务含义
        if current_table and current_table not in biz_map:
            m_biz = re.search(r'\*\*业务含义\*\*[：:]\s*(.+?)(?:\s*\|\s*\*\*|$)', s)
            if m_biz:
                biz_map[current_table] = m_biz.group(1).strip()

        # 匹配5列字段表中的业务含义
        if current_table and s.startswith('|') and '---' not in s and '字段名' not in s:
            parts = [p.strip() for p in s.split('|')]
            parts = [p for p in parts if p]
            if len(parts) >= 5:
                field_name = parts[0]
                field_biz = parts[-1]  # 业务含义在最后一列
                current_fields[field_name] = field_biz
            elif len(parts) == 3:
                field_name = parts[0]
                field_biz = parts[-1]
                current_fields[field_name] = field_biz

    # 保存最后一个表
    if current_table:
        field_biz_map[current_table] = current_fields

    return biz_map, field_biz_map


def fill_biz_meanings(table_content, old_biz_map, old_field_biz_map, table_name):
    """用旧字典中的业务含义填充新格式中的"业务含义待确认"字段"""
    lines = table_content.split('\n')
    result = []

    # 替换表级业务含义（仅替换属性表中的"业务含义待确认"）
    for line in lines:
        if '业务含义待确认' in line and '| 业务含义 |' in line and table_name in old_biz_map:
            line = line.replace('业务含义待确认', old_biz_map[table_name])
        result.append(line)

    # 替换字段级业务含义
    if table_name in old_field_biz_map:
        field_map = old_field_biz_map[table_name]
        new_result = []
        for line in result:
            stripped = line.strip()
            if stripped.startswith('|') and '---' not in stripped and '字段名' not in stripped and '业务含义待确认' in stripped:
                parts = line.split('|')
                if len(parts) >= 8:  # | 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
                    field_name = parts[1].strip()
                    if field_name in field_map:
                        old_biz = field_map[field_name]
                        if old_biz and old_biz != '-':
                            parts[7] = ' ' + old_biz + ' '
                            line = '|'.join(parts)
            new_result.append(line)
        result = new_result

    return '\n'.join(result)


def generate_act_section(act_data_file, old_biz_map, old_field_biz_map):
    """生成Activiti工作流引擎表的标准格式内容"""
    # 优先使用第一个代理的完整输出
    act_dict_content = read_file(ACT_DICT_FILE)
    if act_dict_content:
        # 代理输出已经是标准格式，直接使用
        # 但需要添加序号前缀
        lines = act_dict_content.split('\n')
        result_lines = []
        counter = 1
        for line in lines:
            # 替换 ### 序号 为标准序号
            m = re.match(r'^###\s+\d+\s+', line)
            if m:
                line = f'### {counter} ' + line[m.end():]
                counter += 1
            result_lines.append(line)
        return '\n'.join(result_lines)

    # 如果代理文件不可用，从JSON数据生成
    if os.path.exists(act_data_file):
        with open(act_data_file, 'r', encoding='utf-8') as f:
            act_data = json.load(f)
        result = []
        for idx, table in enumerate(act_data, 1):
            result.append(generate_table_md(table, idx, old_biz_map, old_field_biz_map))
            result.append('---')
        return '\n'.join(result)

    return ''


def generate_table_md(table_info, seq_num, old_biz_map=None, old_field_biz_map=None):
    """从表信息字典生成标准格式Markdown"""
    name = table_info.get('table_name', '')
    comment = table_info.get('table_comment', '')
    biz_meaning = table_info.get('biz_meaning', '') or comment
    obj_type = table_info.get('obj_type', 'BASE TABLE')
    data_count = table_info.get('data_count', '-')
    data_size = table_info.get('data_size', '-')

    # 用旧数据补充业务含义
    if old_biz_map and name in old_biz_map and (not biz_meaning or biz_meaning == '业务含义待确认'):
        biz_meaning = old_biz_map[name]

    lines = []
    lines.append(f'### {seq_num} {name} -- {comment}' if comment else f'### {seq_num} {name}')
    lines.append('')
    lines.append('| 属性 | 值 |')
    lines.append('|------|-----|')
    lines.append(f'| 对象类型 | {obj_type} |')
    lines.append(f'| 业务含义 | {biz_meaning} |')
    lines.append(f'| 数据量 | {data_count} |')
    lines.append(f'| 数据大小 | {data_size} |')
    lines.append('')
    lines.append('**字段列表**')
    lines.append('')
    lines.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
    lines.append('|--------|----------|------|--------|------|----------|----------|')

    fields = table_info.get('fields', [])
    for field in fields:
        fname = field.get('字段名', field.get('name', ''))
        ftype = field.get('数据类型', field.get('type', ''))
        fnull = field.get('可空', field.get('nullable', 'YES'))
        fdefault = field.get('默认值', field.get('default', '-'))
        fconstraint = field.get('约束', field.get('constraint', '-'))
        fdesc = field.get('字段描述', field.get('comment', '-'))
        fbiz = field.get('业务含义', field.get('biz_meaning', '-'))

        # 用旧数据补充字段业务含义
        if old_field_biz_map and name in old_field_biz_map:
            old_fields = old_field_biz_map[name]
            if fname in old_fields and (fbiz == '-' or fbiz == '业务含义待确认'):
                fbiz = old_fields[fname]

        # 清理显示
        if not fnull or fnull == 'NULL':
            fnull = 'YES'
        if fdefault == 'NULL' or fdefault is None:
            fdefault = '-'
        if not fconstraint:
            fconstraint = '-'
        if not fdesc:
            fdesc = '-'
        if not fbiz:
            fbiz = '-'

        lines.append(f'| {fname} | {ftype} | {fnull} | {fdefault} | {fconstraint} | {fdesc} | {fbiz} |')

    # 索引
    indexes = table_info.get('indexes', [])
    if indexes:
        lines.append('')
        lines.append('**索引列表**')
        lines.append('')
        lines.append('| 索引名 | 索引类型 | 唯一性 | 索引字段 |')
        lines.append('|--------|----------|--------|----------|')
        for idx in indexes:
            iname = idx.get('索引名', idx.get('name', ''))
            itype = idx.get('索引类型', idx.get('type', 'BTREE'))
            iuniq = idx.get('唯一性', idx.get('unique', 'NON-UNIQUE'))
            ifields = idx.get('索引字段', idx.get('columns', ''))
            lines.append(f'| {iname} | {itype} | {iuniq} | {ifields} |')

    # 外键
    fks = table_info.get('foreign_keys', [])
    if fks:
        lines.append('')
        lines.append('**外键列表**')
        lines.append('')
        lines.append('| 外键名 | 本表字段 | 引用表 | 引用字段 |')
        lines.append('|--------|----------|--------|----------|')
        for fk in fks:
            fkname = fk.get('外键名', fk.get('name', ''))
            fkfield = fk.get('外键字段', fk.get('column', ''))
            fkref_table = fk.get('引用表', fk.get('ref_table', ''))
            fkref_field = fk.get('引用字段', fk.get('ref_column', ''))
            lines.append(f'| {fkname} | {fkfield} | {fkref_table} | {fkref_field} |')

    return '\n'.join(lines)


def generate_view_section(views_desc_file, views_create_file, old_biz_map, old_field_biz_map):
    """生成视图的标准格式内容"""
    result = []

    # 加载视图描述数据
    if not os.path.exists(views_desc_file):
        return ''

    with open(views_desc_file, 'r', encoding='utf-8') as f:
        views_desc = json.load(f)

    views_create = {}
    if os.path.exists(views_create_file):
        with open(views_create_file, 'r', encoding='utf-8') as f:
            views_create = json.load(f)

    for idx, (view_name, view_columns) in enumerate(views_desc.items(), 1):
        # view_columns 是一个列表，每个元素是 {Field, Type, Null, Key, Default, Extra}
        if isinstance(view_columns, list):
            columns = view_columns
        elif isinstance(view_columns, dict):
            columns = view_columns.get('columns', [])
        else:
            columns = []

        biz = old_biz_map.get(view_name, '')

        lines = []
        lines.append(f'### {idx} {view_name}')
        lines.append('')
        lines.append('| 属性 | 值 |')
        lines.append('|------|-----|')
        lines.append('| 对象类型 | VIEW |')
        lines.append(f'| 业务含义 | {biz} |')
        lines.append('| 数据量 | - |')
        lines.append('| 数据大小 | - |')
        lines.append('')
        lines.append('**字段列表**')
        lines.append('')
        lines.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
        lines.append('|--------|----------|------|--------|------|----------|----------|')

        for col in columns:
            # 兼容两种格式: {Field, Type, ...} 和 {name, type, ...}
            cname = col.get('Field', col.get('name', ''))
            ctype = col.get('Type', col.get('type', ''))
            cbiz = old_field_biz_map.get(view_name, {}).get(cname, '-')

            lines.append(f'| {cname} | {ctype} | - | - | - | - | {cbiz} |')

        # 添加查询逻辑
        if view_name in views_create:
            create_info = views_create[view_name]
            if isinstance(create_info, dict):
                create_sql = create_info.get('Create View', create_info.get('create_sql', ''))
            elif isinstance(create_info, str):
                create_sql = create_info
            else:
                create_sql = str(create_info)
            if create_sql:
                lines.append('')
                lines.append('**查询逻辑**')
                lines.append('')
                lines.append('```sql')
                lines.append(create_sql.strip())
                lines.append('```')

        result.append('\n'.join(lines))
        result.append('---')

    return '\n'.join(result)


def main():
    print("=" * 60)
    print("重写 database_dict_part3.md 为标准格式")
    print("=" * 60)

    # 1. 读取新查询的数据字典
    print("\n1. 读取新查询的数据字典...")
    new_dict = read_file(NEW_DICT_FILE)
    new_dict_lines = new_dict.split('\n')
    print(f"   文件行数: {len(new_dict_lines)}")
    print(f"   前3行: {[l[:60] for l in new_dict_lines[:3]]}")

    # 2. 读取旧Part3的业务含义
    print("\n2. 读取旧字典的业务含义...")
    old_part3 = read_file(OLD_PART3_FILE)
    old_dict = read_file(OLD_DICT_FILE)

    # 从旧Part3和database_dict.md中提取业务含义（优先使用database_dict.md中的旧格式数据）
    old_biz_map_part3, old_field_biz_map_part3 = parse_old_part3_biz_meanings(old_part3)
    old_biz_map_dict, old_field_biz_map_dict = parse_old_part3_biz_meanings(old_dict)

    # 合并：database_dict.md 优先
    old_biz_map = {**old_biz_map_part3, **old_biz_map_dict}
    old_field_biz_map = {**old_field_biz_map_part3, **old_field_biz_map_dict}

    print(f"   旧表级业务含义: Part3={len(old_biz_map_part3)}, Dict={len(old_biz_map_dict)}, 合并={len(old_biz_map)}")
    print(f"   旧字段级业务含义: Part3={sum(len(v) for v in old_field_biz_map_part3.values())}, Dict={sum(len(v) for v in old_field_biz_map_dict.values())}, 合并={sum(len(v) for v in old_field_biz_map.values())}")

    # 3. 从新字典中提取各分类表
    print("\n3. 从新字典中提取各分类表...")
    all_target_tables = set(ACT_TABLES + FB_TABLES + TEMP_TABLES + RMA_TABLES)
    new_tables = parse_new_dict_tables(new_dict, all_target_tables)
    print(f"   从新字典中找到 {len(new_tables)} 张表")

    # 4. 生成各章节内容
    output_parts = []

    # 文件头
    output_parts.append("""# DPPMS_D365 数据库字典 - 第三部分：历史迁移表、Activiti引擎表、临时表与视图

> 生成时间：2026-06-13 | 数据库：dppms_d365 | 基准来源：生产数据库实际结构
> 格式标准：与第一部分、第二部分保持完全一致（7列字段表、4列索引表、属性表格）

---

## 目录索引

### 一、Activiti工作流引擎表（act_*）- 26张
""")

    # Activiti表目录
    act_links = ' | '.join(f'[{t}](#{t})' for t in ACT_TABLES)
    output_parts.append(act_links)
    output_parts.append("""
### 二、Firebird迁移表（fb_*）- 15张
""")
    fb_links = ' | '.join(f'[{t}](#{t})' for t in FB_TABLES)
    output_parts.append(fb_links)
    output_parts.append("""
### 三、RMA/备件/仓库等业务表 - 约45张
""")
    rma_links = ' | '.join(f'[{t}](#{t})' for t in RMA_TABLES)
    output_parts.append(rma_links)
    output_parts.append("""
### 四、临时表（temp_*/tmp_*）- 11张
""")
    temp_links = ' | '.join(f'[{t}](#{t})' for t in TEMP_TABLES)
    output_parts.append(temp_links)
    output_parts.append("""
### 五、视图（VIEW）- 39个
""")

    # 5. 第一章：Activiti工作流引擎表
    print("\n4. 生成Activiti工作流引擎表章节...")
    output_parts.append("""
---

# 一、Activiti工作流引擎表（act_*）

> Activiti引擎标准表，支撑PMS系统的审批工作流。表结构遵循Activiti 5.x/6.x标准设计。
> 外键关系概览：act_ge_bytearray -> act_re_deployment; act_id_membership -> act_id_group, act_id_user; act_re_model -> act_ge_bytearray, act_re_deployment; act_ru_execution -> act_re_procdef; act_ru_identitylink -> act_ru_execution, act_ru_task, act_re_procdef; act_ru_task -> act_ru_execution, act_re_procdef; act_ru_variable -> act_ge_bytearray, act_ru_execution

---
""")

    # 使用第一个代理的完整act_*输出
    act_content = generate_act_section(ACT_DATA_FILE, old_biz_map, old_field_biz_map)
    if act_content:
        output_parts.append(act_content)
        print(f"   Activiti表: 使用代理输出，{len(act_content.split(chr(10)))} 行")
    else:
        # 从新字典中提取act_*表并补充业务含义
        for idx, tname in enumerate(ACT_TABLES, 1):
            if tname in new_tables:
                content = fill_biz_meanings(new_tables[tname], old_biz_map, old_field_biz_map, tname)
                # 替换序号
                content = re.sub(r'^###\s+\d+\s+', f'### {idx} ', content, count=1)
                output_parts.append(content)
                output_parts.append('---')
        print(f"   Activiti表: 使用新字典+旧业务含义")

    # 6. 第二章：Firebird迁移表
    print("\n5. 生成Firebird迁移表章节...")
    output_parts.append("""

# 二、Firebird迁移表（fb_*）

> 从Firebird发货系统迁移的数据表，包含合同、发货、条码、物料、维保等历史业务数据。
> fb_shipment_barcode 相关三张表合计约1.5GB，是全库数据量最大的部分。

---
""")

    for idx, tname in enumerate(FB_TABLES, 1):
        if tname in new_tables:
            content = fill_biz_meanings(new_tables[tname], old_biz_map, old_field_biz_map, tname)
            content = re.sub(r'^###\s+\d+\s+', f'### {idx} ', content, count=1)
            output_parts.append(content)
            output_parts.append('---')
        else:
            output_parts.append(f'### {idx} {tname}\n\n> 数据待补充\n\n---')

    # 7. 第三章：RMA/备件/仓库等业务表
    print("\n6. 生成RMA/备件/仓库等业务表章节...")
    output_parts.append("""

# 三、RMA/备件/仓库等业务表

> 包含RMA返修、备件管理、仓库管理、维保信息、系统日志等业务表。
> 部分表（如user、role、department）为旧版系统遗留表，与fnd_*、t_*系列表存在功能重叠。

---
""")

    for idx, tname in enumerate(RMA_TABLES, 1):
        if tname in new_tables:
            content = fill_biz_meanings(new_tables[tname], old_biz_map, old_field_biz_map, tname)
            content = re.sub(r'^###\s+\d+\s+', f'### {idx} ', content, count=1)
            output_parts.append(content)
            output_parts.append('---')
        else:
            output_parts.append(f'### {idx} {tname}\n\n> 数据待补充\n\n---')

    # 8. 第四章：临时表
    print("\n7. 生成临时表章节...")
    output_parts.append("""

# 四、临时表（temp_*/tmp_*）

> 查询优化用临时表，用于报表查询性能提升。部分临时表是对应视图的数据物化缓存。
> 注意：临时表应定期清理，避免占用过多存储空间。

---
""")

    for idx, tname in enumerate(TEMP_TABLES, 1):
        if tname in new_tables:
            content = fill_biz_meanings(new_tables[tname], old_biz_map, old_field_biz_map, tname)
            content = re.sub(r'^###\s+\d+\s+', f'### {idx} ', content, count=1)
            output_parts.append(content)
            output_parts.append('---')
        else:
            output_parts.append(f'### {idx} {tname}\n\n> 数据待补充\n\n---')

    # 9. 第五章：视图
    print("\n8. 生成视图章节...")
    output_parts.append("""

# 五、视图（VIEW）

> 数据库视图，提供对基础表的查询封装，简化业务查询逻辑。
> 视图字段的可空、默认值、约束、字段描述列以"-"填充（视图无物理约束）。

---
""")

    view_content = generate_view_section(VIEWS_DESC_FILE, VIEWS_CREATE_FILE, old_biz_map, old_field_biz_map)
    if view_content:
        output_parts.append(view_content)
        print(f"   视图: 从JSON数据生成")
    else:
        # 从新字典中查找视图
        # 视图在新字典中可能以VIEW类型出现
        print(f"   视图: JSON数据不可用，尝试从新字典提取")

    # 10. 写入文件
    print("\n9. 写入文件...")
    full_content = '\n'.join(output_parts)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(full_content)
    print(f"   写入 {OUTPUT_FILE}")
    print(f"   总行数: {len(full_content.split(chr(10)))}")

    # 统计
    table_count = full_content.count('### ')
    field_count = full_content.count('| 字段名 |') - full_content.count('|--------|')
    index_count = full_content.count('| 索引名 |') - full_content.count('|--------|')
    fk_count = full_content.count('| 外键名 |') - full_content.count('|--------|')
    print(f"\n统计:")
    print(f"  表/视图数: {table_count}")
    print(f"  字段定义行: {field_count}")
    print(f"  索引定义行: {index_count}")
    print(f"  外键定义行: {fk_count}")


if __name__ == '__main__':
    main()
