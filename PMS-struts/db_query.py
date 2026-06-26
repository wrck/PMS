# -*- coding: utf-8 -*-
import pymysql
import json
import sys

DB = 'dppms_d365'

def get_connection():
    return pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', database=DB, charset='utf8mb4')

def get_all_base_tables(cur):
    cur.execute(f"SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA='{DB}' AND TABLE_TYPE='BASE TABLE' ORDER BY TABLE_NAME")
    return [r[0] for r in cur.fetchall()]

def get_table_info(cur, table_name):
    """Get table comment, rows, data size"""
    cur.execute(f"""
        SELECT TABLE_COMMENT, TABLE_ROWS, DATA_LENGTH, INDEX_LENGTH
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA='{DB}' AND TABLE_NAME='{table_name}'
    """)
    row = cur.fetchone()
    if row:
        return {
            'comment': row[0] or '',
            'rows': row[1] or 0,
            'data_length': row[2] or 0,
            'index_length': row[3] or 0
        }
    return {'comment': '', 'rows': 0, 'data_length': 0, 'index_length': 0}

def get_columns(cur, table_name):
    """Get column details"""
    cur.execute(f"""
        SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY,
               EXTRA, COLUMN_COMMENT
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA='{DB}' AND TABLE_NAME='{table_name}'
        ORDER BY ORDINAL_POSITION
    """)
    rows = cur.fetchall()
    result = []
    for r in rows:
        result.append({
            'name': r[0],
            'type': r[1],
            'nullable': r[2],
            'default': r[3],
            'key': r[4],
            'extra': r[5],
            'comment': r[6] or ''
        })
    return result

def get_indexes(cur, table_name):
    """Get index details"""
    cur.execute(f"""
        SELECT INDEX_NAME, NON_UNIQUE, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS COLUMNS,
               INDEX_TYPE
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA='{DB}' AND TABLE_NAME='{table_name}'
        GROUP BY INDEX_NAME, NON_UNIQUE, INDEX_TYPE
    """)
    rows = cur.fetchall()
    result = []
    for r in rows:
        result.append({
            'name': r[0],
            'unique': 'UNIQUE' if r[1] == 0 else 'NON-UNIQUE',
            'columns': r[2],
            'type': r[3]
        })
    return result

def format_size(bytes_val):
    if bytes_val >= 1024*1024*1024:
        return f"{bytes_val/(1024*1024*1024):.2f} GB"
    elif bytes_val >= 1024*1024:
        return f"{bytes_val/(1024*1024):.2f} MB"
    elif bytes_val >= 1024:
        return f"{bytes_val/1024:.2f} KB"
    else:
        return f"{bytes_val} Bytes"

def infer_business_meaning(col_name, col_comment, table_name):
    """Infer business meaning from column name and comment"""
    if col_comment:
        return col_comment
    name_lower = col_name.lower()
    common = {
        'id': '主键ID',
        'create_time': '创建时间',
        'update_time': '更新时间',
        'create_by': '创建人',
        'update_by': '更新人',
        'create_date': '创建日期',
        'update_date': '更新日期',
        'del_flag': '删除标记',
        'is_deleted': '是否删除',
        'status': '状态',
        'remark': '备注',
        'description': '描述',
        'name': '名称',
        'code': '编码',
        'type': '类型',
        'order': '排序',
        'sort': '排序',
        'enabled': '是否启用',
        'deleted': '是否删除',
    }
    if name_lower in common:
        return common[name_lower]
    return '业务含义待确认'

def main():
    conn = get_connection()
    cur = conn.cursor()

    tables = get_all_base_tables(cur)
    print(f"Found {len(tables)} BASE TABLEs in {DB}")

    output_lines = []
    output_lines.append(f"# dppms_d365 数据库完整数据字典")
    output_lines.append(f"")
    output_lines.append(f"数据库: {DB}")
    output_lines.append(f"表总数: {len(tables)}")
    output_lines.append(f"生成时间: 2026-06-13")
    output_lines.append(f"")

    for idx, table_name in enumerate(tables, 1):
        print(f"Processing [{idx}/{len(tables)}]: {table_name}")

        info = get_table_info(cur, table_name)
        columns = get_columns(cur, table_name)
        indexes = get_indexes(cur, table_name)

        # Build constraint string for each column
        output_lines.append(f"### {idx} {table_name} -- {info['comment']}")
        output_lines.append(f"")
        output_lines.append(f"| 属性 | 值 |")
        output_lines.append(f"|------|-----|")
        output_lines.append(f"| 对象类型 | BASE TABLE |")
        output_lines.append(f"| 业务含义 | {info['comment'] or '业务含义待确认'} |")
        output_lines.append(f"| 数据量 | ~{info['rows']} 行 |")
        output_lines.append(f"| 数据大小 | {format_size(info['data_length'])} |")
        output_lines.append(f"")
        output_lines.append(f"**字段列表**")
        output_lines.append(f"")
        output_lines.append(f"| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |")
        output_lines.append(f"|--------|----------|------|--------|------|----------|----------|")

        for col in columns:
            constraints = []
            if col['key'] == 'PRI':
                constraints.append('PRI')
            elif col['key'] == 'UNI':
                constraints.append('UNI')
            elif col['key'] == 'MUL':
                constraints.append('MUL')
            if 'auto_increment' in col['extra']:
                constraints.append('auto_increment')
            constraint_str = ', '.join(constraints) if constraints else '-'

            default_val = col['default'] if col['default'] is not None else '-'
            if default_val is not None and isinstance(default_val, str) and len(default_val) > 50:
                default_val = default_val[:50] + '...'

            business = infer_business_meaning(col['name'], col['comment'], table_name)

            output_lines.append(f"| {col['name']} | {col['type']} | {col['nullable']} | {default_val} | {constraint_str} | {col['comment'] or '-'} | {business} |")

        output_lines.append(f"")
        output_lines.append(f"**索引列表**")
        output_lines.append(f"")
        output_lines.append(f"| 索引名 | 索引类型 | 唯一性 | 索引字段 |")
        output_lines.append(f"|--------|----------|--------|----------|")

        for idx_item in indexes:
            output_lines.append(f"| {idx_item['name']} | {idx_item['type']} | {idx_item['unique']} | {idx_item['columns']} |")

        output_lines.append(f"")
        output_lines.append(f"---")
        output_lines.append(f"")

    cur.close()
    conn.close()

    output_path = f'd:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\db_dictionary_dppms_d365.md'
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(output_lines))

    print(f"\nDone! Output written to {output_path}")

if __name__ == '__main__':
    main()
