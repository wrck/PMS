#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""查询49个表/视图的字段定义和索引，生成markdown数据字典"""

import pymysql
import json

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '!Q@W3e4r',
    'database': 'dppms_d365',
    'charset': 'utf8mb4',
}

# 49个对象列表（按字母序）
TABLES = [
    'act_evt_log', 'act_ge_bytearray', 'act_ge_property', 'act_hi_actinst',
    'act_hi_attachment', 'act_hi_comment', 'act_hi_detail', 'act_hi_identitylink',
    'act_hi_procinst', 'act_hi_taskinst', 'act_hi_varinst', 'act_id_group',
    'act_id_info', 'act_id_membership', 'act_id_user', 'act_procdef_info',
    'act_re_deployment', 'act_re_model', 'act_re_procdef', 'act_ru_event_subscr',
    'act_ru_execution', 'act_ru_identitylink', 'act_ru_job', 'act_ru_task',
    'act_ru_variable',
    'dp_v_spms_department', 'dp_v_spms_item_basic_info', 'dp_v_spms_rma_remind',
    'ehr_company', 'ehr_department', 'ehr_employee', 'ehr_emp_power', 'ehr_job', 'ehr_login',
    'fb_contract', 'fb_ft_result1', 'fb_ft_result2', 'fb_items', 'fb_items2',
    'fb_market_system', 'fb_office_relationship', 'fb_service', 'fb_shipment',
    'fb_shipment_barcode', 'fb_shipment_barcode_change_log',
    'fb_shipment_barcode_order_line', 'fb_shipment_barcode_relation',
    'fb_soft_version', 'fb_warranty_grade',
]

VIEWS = ['dp_v_spms_department', 'dp_v_spms_item_basic_info', 'dp_v_spms_rma_remind']

# 数据量参考
DATA_STATS = {
    'act_evt_log': ('约6,398行', '17.5MB', '0MB'),
    'act_ge_bytearray': ('约1,210行', '6.5MB', '0MB'),
    'act_ge_property': ('约3行', '0MB', '0MB'),
    'act_hi_actinst': ('约155,827行', '22.5MB', '17.1MB'),
    'act_hi_attachment': ('约0行', '0MB', '0MB'),
    'act_hi_comment': ('约66,552行', '7.5MB', '0MB'),
    'act_hi_detail': ('约0行', '0MB', '0.1MB'),
    'act_hi_identitylink': ('约143,081行', '8.5MB', '12.5MB'),
    'act_hi_procinst': ('约18,833行', '2.5MB', '1.9MB'),
    'act_hi_taskinst': ('约67,971行', '10.5MB', '2.5MB'),
    'act_hi_varinst': ('约204,606行', '18.5MB', '22.6MB'),
    'act_id_group': ('约12行', '0MB', '0MB'),
    'act_id_info': ('约0行', '0MB', '0MB'),
    'act_id_membership': ('约548行', '0MB', '0MB'),
    'act_id_user': ('约201行', '0MB', '0MB'),
    'act_procdef_info': ('约0行', '0MB', '0MB'),
    'act_re_deployment': ('约27行', '0MB', '0MB'),
    'act_re_model': ('约6行', '0MB', '0MB'),
    'act_re_procdef': ('约27行', '0MB', '0MB'),
    'act_ru_event_subscr': ('约0行', '0MB', '0MB'),
    'act_ru_execution': ('约3,554行', '0.4MB', '0.7MB'),
    'act_ru_identitylink': ('约23,196行', '1.5MB', '4.1MB'),
    'act_ru_job': ('约0行', '0MB', '0MB'),
    'act_ru_task': ('约3,400行', '0.5MB', '0.5MB'),
    'act_ru_variable': ('约42,146行', '4.5MB', '8.0MB'),
    'dp_v_spms_department': None,
    'dp_v_spms_item_basic_info': None,
    'dp_v_spms_rma_remind': None,
    'ehr_company': ('约3行', '0MB', '0MB'),
    'ehr_department': ('约517行', '0.1MB', '0.1MB'),
    'ehr_employee': ('约4,831行', '1.5MB', '0.7MB'),
    'ehr_emp_power': ('约127行', '0.1MB', '0MB'),
    'ehr_job': ('约245行', '0MB', '0MB'),
    'ehr_login': ('约3,224行', '0.3MB', '0MB'),
    'fb_contract': ('约104,743行', '23.5MB', '29.1MB'),
    'fb_ft_result1': ('约193,752行', '10.5MB', '6.5MB'),
    'fb_ft_result2': ('约496,626行', '298.8MB', '17.5MB'),
    'fb_items': ('约32,188行', '3.5MB', '6.6MB'),
    'fb_items2': ('约19,357行', '2.5MB', '0MB'),
    'fb_market_system': ('约14行', '0MB', '0MB'),
    'fb_office_relationship': ('约659行', '0.1MB', '0MB'),
    'fb_service': ('约95,878行', '12.5MB', '18.6MB'),
    'fb_shipment': ('约140,962行', '17.5MB', '64.4MB'),
    'fb_shipment_barcode': ('约3,541,100行', '612.0MB', '2369.0MB'),
    'fb_shipment_barcode_change_log': ('约528,607行', '547.0MB', '120.0MB'),
    'fb_shipment_barcode_order_line': ('约2,576,429行', '373.0MB', '568.0MB'),
    'fb_shipment_barcode_relation': ('约55,130行', '7.5MB', '19.1MB'),
    'fb_soft_version': ('约161,015行', '13.5MB', '31.6MB'),
    'fb_warranty_grade': ('约11行', '0MB', '0MB'),
}

# 业务含义
BUSINESS_DESC = {
    'act_evt_log': 'Activiti事件日志表 - 记录流程引擎的事件日志',
    'act_ge_bytearray': 'Activiti通用字节数组表 - 存储流程定义、资源文件等二进制数据',
    'act_ge_property': 'Activiti通用属性表 - 存储流程引擎的属性配置',
    'act_hi_actinst': 'Activiti历史活动实例表 - 记录流程中所有活动节点的历史执行信息',
    'act_hi_attachment': 'Activiti历史附件表 - 记录流程实例的附件信息',
    'act_hi_comment': 'Activiti历史评论表 - 记录流程实例的审批意见和评论',
    'act_hi_detail': 'Activiti历史详情表 - 记录流程变量的变更详情',
    'act_hi_identitylink': 'Activiti历史身份关联表 - 记录流程中参与者与流程实例的历史关联',
    'act_hi_procinst': 'Activiti历史流程实例表 - 记录流程实例的历史执行信息',
    'act_hi_taskinst': 'Activiti历史任务实例表 - 记录任务的历史执行信息',
    'act_hi_varinst': 'Activiti历史变量实例表 - 记录流程变量的历史值',
    'act_id_group': 'Activiti身份组表 - 存储用户组信息',
    'act_id_info': 'Activiti身份信息表 - 存储用户扩展信息',
    'act_id_membership': 'Activiti身份成员关系表 - 存储用户与组的关联关系',
    'act_id_user': 'Activiti身份用户表 - 存储用户基本信息',
    'act_procdef_info': 'Activiti流程定义信息表 - 存储流程定义的额外信息',
    'act_re_deployment': 'Activiti部署表 - 记录流程部署信息',
    'act_re_model': 'Activiti模型表 - 存储流程模型信息',
    'act_re_procdef': 'Activiti流程定义表 - 存储流程定义信息',
    'act_ru_event_subscr': 'Activiti运行时事件订阅表 - 记录运行时的事件订阅信息',
    'act_ru_execution': 'Activiti运行时执行实例表 - 记录流程的运行时执行路径',
    'act_ru_identitylink': 'Activiti运行时身份关联表 - 记录运行时参与者与任务的关联',
    'act_ru_job': 'Activiti运行时作业表 - 记录定时器和异步作业',
    'act_ru_task': 'Activiti运行时任务表 - 记录当前待办任务',
    'act_ru_variable': 'Activiti运行时变量表 - 记录流程的运行时变量',
    'dp_v_spms_department': '数据平台SPMS部门视图 - 提供部门信息的统一查询视图',
    'dp_v_spms_item_basic_info': '数据平台SPMS物料基本信息视图 - 提供物料基本信息的统一查询视图',
    'dp_v_spms_rma_remind': '数据平台SPMS RMA提醒视图 - 提供RMA退货提醒信息的统一查询视图',
    'ehr_company': 'EHR公司表 - 存储公司组织信息',
    'ehr_department': 'EHR部门表 - 存储部门组织架构信息',
    'ehr_employee': 'EHR员工表 - 存储员工基本信息',
    'ehr_emp_power': 'EHR员工权限表 - 存储员工系统权限配置',
    'ehr_job': 'EHR岗位表 - 存储岗位信息',
    'ehr_login': 'EHR登录表 - 存储用户登录信息',
    'fb_contract': 'Firebird迁移合同表 - 从Firebird迁移的合同数据',
    'fb_ft_result1': 'Firebird迁移FT结果1表 - 从Firebird迁移的FT测试结果数据1',
    'fb_ft_result2': 'Firebird迁移FT结果2表 - 从Firebird迁移的FT测试结果数据2',
    'fb_items': 'Firebird迁移物料表 - 从Firebird迁移的物料数据',
    'fb_items2': 'Firebird迁移物料2表 - 从Firebird迁移的物料补充数据',
    'fb_market_system': 'Firebird迁移市场体系表 - 从Firebird迁移的市场体系数据',
    'fb_office_relationship': 'Firebird迁移办事处关系表 - 从Firebird迁移的办事处关系数据',
    'fb_service': 'Firebird迁移服务表 - 从Firebird迁移的服务数据',
    'fb_shipment': 'Firebird迁移发货表 - 从Firebird迁移的发货数据',
    'fb_shipment_barcode': 'Firebird迁移发货条码表 - 从Firebird迁移的发货条码数据',
    'fb_shipment_barcode_change_log': 'Firebird迁移发货条码变更日志表 - 从Firebird迁移的条码变更日志',
    'fb_shipment_barcode_order_line': 'Firebird迁移发货条码订单行表 - 从Firebird迁移的条码订单行数据',
    'fb_shipment_barcode_relation': 'Firebird迁移发货条码关联表 - 从Firebird迁移的条码关联数据',
    'fb_soft_version': 'Firebird迁移软件版本表 - 从Firebird迁移的软件版本数据',
    'fb_warranty_grade': 'Firebird迁移维保等级表 - 从Firebird迁移的维保等级数据',
}


def get_columns(cursor, table_name):
    """获取表的字段列表"""
    cursor.execute(f"DESCRIBE `{table_name}`")
    rows = cursor.fetchall()
    columns = []
    for row in rows:
        # Field, Type, Null, Key, Default, Extra
        field = row[0]
        col_type = row[1]
        nullable = 'YES' if row[2] == 'YES' else 'NO'
        default = row[4] if row[4] is not None else ''
        key = row[3] if row[3] else ''
        extra = row[5] if row[5] else ''
        columns.append({
            'field': field,
            'type': col_type,
            'nullable': nullable,
            'default': str(default) if default != '' else '',
            'key': key,
            'extra': extra,
        })
    return columns


def get_indexes(cursor, table_name):
    """获取表的索引列表"""
    cursor.execute("""
        SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE, INDEX_TYPE
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = %s AND TABLE_NAME = %s
        ORDER BY INDEX_NAME, SEQ_IN_INDEX
    """, ('dppms_d365', table_name))
    rows = cursor.fetchall()

    # 合并同一索引的多列
    index_map = {}
    for row in rows:
        idx_name = row[0]
        col_name = row[1]
        non_unique = row[2]
        idx_type = row[3]
        if idx_name not in index_map:
            index_map[idx_name] = {
                'columns': [],
                'non_unique': non_unique,
                'index_type': idx_type,
            }
        index_map[idx_name]['columns'].append(col_name)

    indexes = []
    for idx_name, info in index_map.items():
        indexes.append({
            'name': idx_name,
            'columns': ', '.join(info['columns']),
            'unique': '否' if info['non_unique'] == 1 else '是',
            'type': info['index_type'],
        })
    return indexes


def generate_table_md(table_name, columns, indexes, stats, desc):
    """生成BASE TABLE的markdown"""
    rows_str, data_size, index_size = stats
    lines = []
    lines.append(f'### {table_name}')
    lines.append('')
    lines.append(f'**业务含义**：{desc}')
    lines.append('')
    lines.append(f'**数据量**：{rows_str} | 数据大小：{data_size} | 索引大小：{index_size}')
    lines.append('')
    lines.append('**字段列表**：')
    lines.append('')
    lines.append('| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |')
    lines.append('|--------|------|------|--------|-----|----------|')
    for col in columns:
        default_val = col['default']
        key_val = col['key']
        lines.append(f'| {col["field"]} | {col["type"]} | {col["nullable"]} | {default_val} | {key_val} |  |')
    lines.append('')
    lines.append('**索引列表**：')
    lines.append('')
    lines.append('| 索引名 | 列 | 唯一性 | 索引类型 |')
    lines.append('|--------|-----|--------|----------|')
    for idx in indexes:
        lines.append(f'| {idx["name"]} | {idx["columns"]} | {idx["unique"]} | {idx["type"]} |')
    lines.append('')
    lines.append('---')
    lines.append('')
    return '\n'.join(lines)


def generate_view_md(table_name, columns, desc):
    """生成VIEW的markdown"""
    lines = []
    lines.append(f'### {table_name}')
    lines.append('')
    lines.append('**对象类型**：VIEW')
    lines.append('')
    lines.append(f'**业务含义**：{desc}')
    lines.append('')
    lines.append('**字段列表**：')
    lines.append('')
    lines.append('| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |')
    lines.append('|--------|------|------|--------|-----|----------|')
    for col in columns:
        default_val = col['default']
        key_val = col['key']
        lines.append(f'| {col["field"]} | {col["type"]} | {col["nullable"]} | {default_val} | {key_val} |  |')
    lines.append('')
    lines.append('---')
    lines.append('')
    return '\n'.join(lines)


def main():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    all_md = []
    for table_name in TABLES:
        print(f'Processing: {table_name}')
        try:
            columns = get_columns(cursor, table_name)
        except Exception as e:
            print(f'  ERROR getting columns for {table_name}: {e}')
            continue

        desc = BUSINESS_DESC.get(table_name, '')

        if table_name in VIEWS:
            md = generate_view_md(table_name, columns, desc)
        else:
            try:
                indexes = get_indexes(cursor, table_name)
            except Exception as e:
                print(f'  ERROR getting indexes for {table_name}: {e}')
                indexes = []
            stats = DATA_STATS.get(table_name, ('约0行', '0MB', '0MB'))
            if stats is None:
                stats = ('约0行', '0MB', '0MB')
            md = generate_table_md(table_name, columns, indexes, stats, desc)

        all_md.append(md)

    cursor.close()
    conn.close()

    # 输出到文件
    output_path = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\_generated_dict.md'
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(all_md))

    print(f'\nDone! Output written to: {output_path}')
    print(f'Total tables/views processed: {len(all_md)}')


if __name__ == '__main__':
    main()
