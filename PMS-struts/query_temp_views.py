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
    step = sys.argv[1] if len(sys.argv) > 1 else 'all'

    if step == 'views_list':
        # Get complete view list
        sql = "SELECT TABLE_NAME FROM information_schema.VIEWS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME"
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))

    elif step == 'temp_tables_list':
        # Verify which temp tables exist
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
        print(json.dumps(result, ensure_ascii=False, default=str))

    elif step == 'temp_describe':
        # DESCRIBE each temp table
        temp_tables = [
            'temp_contract_market_system', 'temp_max_ppfs', 'temp_project_sales_change',
            'temp_query_shipment', 'temp_query_shipment_barcode',
            'tmp_tb_contract_shipment', 'tmp_tb_project_contract',
            'tmp_tb_project_filtered', 'tmp_tb_project_shipment',
            'tmp_tb_view_shipment_ems_4_pm', 'tmp_tb_view_shipment_info_4_pm'
        ]
        results = {}
        for t in temp_tables:
            try:
                result = run_query(conn, f"DESCRIBE `{t}`")
                results[t] = result
            except Exception as e:
                results[t] = f"ERROR: {str(e)}"
        print(json.dumps(results, ensure_ascii=False, default=str))

    elif step == 'temp_create':
        # SHOW CREATE TABLE for each temp table
        temp_tables = [
            'temp_contract_market_system', 'temp_max_ppfs', 'temp_project_sales_change',
            'temp_query_shipment', 'temp_query_shipment_barcode',
            'tmp_tb_contract_shipment', 'tmp_tb_project_contract',
            'tmp_tb_project_filtered', 'tmp_tb_project_shipment',
            'tmp_tb_view_shipment_ems_4_pm', 'tmp_tb_view_shipment_info_4_pm'
        ]
        results = {}
        for t in temp_tables:
            try:
                result = run_query(conn, f"SHOW CREATE TABLE `{t}`")
                # The key might be 'Create Table' or 'CREATE TABLE'
                create_stmt = None
                for row in result:
                    for k, v in row.items():
                        if 'create' in k.lower():
                            create_stmt = v
                            break
                results[t] = create_stmt
            except Exception as e:
                results[t] = f"ERROR: {str(e)}"
        print(json.dumps(results, ensure_ascii=False, default=str))

    elif step == 'temp_indexes':
        # Get indexes for temp tables
        temp_tables = [
            'temp_contract_market_system', 'temp_max_ppfs', 'temp_project_sales_change',
            'temp_query_shipment', 'temp_query_shipment_barcode',
            'tmp_tb_contract_shipment', 'tmp_tb_project_contract',
            'tmp_tb_project_filtered', 'tmp_tb_project_shipment',
            'tmp_tb_view_shipment_ems_4_pm', 'tmp_tb_view_shipment_info_4_pm'
        ]
        placeholders = ','.join(['%s'] * len(temp_tables))
        sql = f"""SELECT TABLE_NAME, INDEX_NAME, COLUMN_NAME, NON_UNIQUE, SEQ_IN_INDEX, INDEX_TYPE
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME IN ({placeholders})
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX"""
        result = run_query(conn, sql, temp_tables)
        print(json.dumps(result, ensure_ascii=False, default=str))

    elif step == 'view_describe':
        # Get all view names first
        sql = "SELECT TABLE_NAME FROM information_schema.VIEWS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME"
        views = [r['TABLE_NAME'] for r in run_query(conn, sql)]
        results = {}
        for v in views:
            try:
                result = run_query(conn, f"DESCRIBE `{v}`")
                results[v] = result
            except Exception as e:
                results[v] = f"ERROR: {str(e)}"
        print(json.dumps(results, ensure_ascii=False, default=str))

    elif step == 'view_create':
        # SHOW CREATE VIEW for all views
        sql = "SELECT TABLE_NAME FROM information_schema.VIEWS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME"
        views = [r['TABLE_NAME'] for r in run_query(conn, sql)]
        results = {}
        for v in views:
            try:
                result = run_query(conn, f"SHOW CREATE VIEW `{v}`")
                create_stmt = None
                for row in result:
                    for k, v2 in row.items():
                        if 'create' in k.lower():
                            create_stmt = v2
                            break
                results[v] = create_stmt
            except Exception as e:
                results[v] = f"ERROR: {str(e)}"
        print(json.dumps(results, ensure_ascii=False, default=str))

    elif step == 'temp_columns_info':
        # Get detailed column info from information_schema for temp tables
        temp_tables = [
            'temp_contract_market_system', 'temp_max_ppfs', 'temp_project_sales_change',
            'temp_query_shipment', 'temp_query_shipment_barcode',
            'tmp_tb_contract_shipment', 'tmp_tb_project_contract',
            'tmp_tb_project_filtered', 'tmp_tb_project_shipment',
            'tmp_tb_view_shipment_ems_4_pm', 'tmp_tb_view_shipment_info_4_pm'
        ]
        placeholders = ','.join(['%s'] * len(temp_tables))
        sql = f"""SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY, EXTRA, COLUMN_COMMENT,
ORDINAL_POSITION, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME IN ({placeholders})
ORDER BY TABLE_NAME, ORDINAL_POSITION"""
        result = run_query(conn, sql, temp_tables)
        print(json.dumps(result, ensure_ascii=False, default=str))

    elif step == 'view_columns_info':
        # Get detailed column info from information_schema for views
        sql = """SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY, EXTRA, COLUMN_COMMENT,
ORDINAL_POSITION
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME IN (SELECT TABLE_NAME FROM information_schema.VIEWS WHERE TABLE_SCHEMA='dppms_d365')
ORDER BY TABLE_NAME, ORDINAL_POSITION"""
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))

    conn.close()

if __name__ == '__main__':
    main()
