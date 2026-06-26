# -*- coding: utf-8 -*-
import pymysql
import json
import sys

def get_connection():
    return pymysql.connect(
        host='localhost',
        user='root',
        password='!Q@W3e4r',
        database='dppms_d365',
        charset='utf8mb4',
        cursorclass=pymysql.cursors.DictCursor
    )

def run_query(conn, sql, params=None):
    with conn.cursor() as cur:
        cur.execute(sql, params)
        return cur.fetchall()

def main():
    conn = get_connection()
    output_dir = r"d:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\temp"

    # 1. Views list
    sql = "SELECT TABLE_NAME FROM information_schema.VIEWS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME"
    views = [r['TABLE_NAME'] for r in run_query(conn, sql)]
    with open(f"{output_dir}\\views_list.json", 'w', encoding='utf-8') as f:
        json.dump(views, f, ensure_ascii=False, default=str)

    # 2. Temp tables basic info
    temp_tables = [
        'temp_contract_market_system', 'temp_max_ppfs', 'temp_project_sales_change',
        'temp_query_shipment', 'temp_query_shipment_barcode',
        'tmp_tb_contract_shipment', 'tmp_tb_project_contract',
        'tmp_tb_project_filtered', 'tmp_tb_project_shipment',
        'tmp_tb_view_shipment_ems_4_pm', 'tmp_tb_view_shipment_info_4_pm'
    ]
    placeholders = ','.join(['%s'] * len(temp_tables))
    sql = f"SELECT TABLE_NAME, TABLE_TYPE, TABLE_COMMENT, TABLE_ROWS, DATA_LENGTH, INDEX_LENGTH FROM information_schema.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME IN ({placeholders})"
    result = run_query(conn, sql, temp_tables)
    with open(f"{output_dir}\\temp_tables_basic.json", 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, default=str)

    # 3. Temp tables column info
    sql = f"""SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY, EXTRA, COLUMN_COMMENT,
ORDINAL_POSITION, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME IN ({placeholders})
ORDER BY TABLE_NAME, ORDINAL_POSITION"""
    result = run_query(conn, sql, temp_tables)
    with open(f"{output_dir}\\temp_tables_columns.json", 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, default=str)

    # 4. Temp tables indexes
    sql = f"""SELECT TABLE_NAME, INDEX_NAME, COLUMN_NAME, NON_UNIQUE, SEQ_IN_INDEX, INDEX_TYPE
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME IN ({placeholders})
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX"""
    result = run_query(conn, sql, temp_tables)
    with open(f"{output_dir}\\temp_tables_indexes.json", 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, default=str)

    # 5. Temp tables CREATE TABLE
    temp_create = {}
    for t in temp_tables:
        try:
            result = run_query(conn, f"SHOW CREATE TABLE `{t}`")
            for row in result:
                for k, v in row.items():
                    if 'create' in k.lower():
                        temp_create[t] = v
                        break
        except Exception as e:
            temp_create[t] = f"ERROR: {str(e)}"
    with open(f"{output_dir}\\temp_tables_create.json", 'w', encoding='utf-8') as f:
        json.dump(temp_create, f, ensure_ascii=False, default=str)

    # 6. View DESCRIBE
    view_describe = {}
    for v in views:
        try:
            result = run_query(conn, f"DESCRIBE `{v}`")
            view_describe[v] = result
        except Exception as e:
            view_describe[v] = f"ERROR: {str(e)}"
    with open(f"{output_dir}\\views_describe.json", 'w', encoding='utf-8') as f:
        json.dump(view_describe, f, ensure_ascii=False, default=str)

    # 7. View CREATE VIEW
    view_create = {}
    for v in views:
        try:
            result = run_query(conn, f"SHOW CREATE VIEW `{v}`")
            for row in result:
                for k, v2 in row.items():
                    if 'create' in k.lower():
                        view_create[v] = v2
                        break
        except Exception as e:
            view_create[v] = f"ERROR: {str(e)}"
    with open(f"{output_dir}\\views_create.json", 'w', encoding='utf-8') as f:
        json.dump(view_create, f, ensure_ascii=False, default=str)

    conn.close()
    print("All data saved successfully!")

if __name__ == '__main__':
    main()
