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
    step = sys.argv[1] if len(sys.argv) > 1 else 'test'
    conn = get_connection()
    
    if step == 'test':
        result = run_query(conn, "SELECT COUNT(*) as cnt FROM information_schema.TABLES WHERE TABLE_SCHEMA=%s", ('dppms_d365',))
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '1':  # 列信息
        sql = """SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY, EXTRA, COLUMN_COMMENT 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME NOT LIKE 'act_%' AND TABLE_NAME NOT LIKE 'dp_v_%' AND TABLE_NAME NOT LIKE 'fb_%' AND TABLE_NAME NOT LIKE 'ehr_%'
ORDER BY TABLE_NAME, ORDINAL_POSITION"""
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '2':  # 索引信息
        sql = """SELECT TABLE_NAME, INDEX_NAME, COLUMN_NAME, NON_UNIQUE, SEQ_IN_INDEX, INDEX_TYPE 
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME NOT LIKE 'act_%' AND TABLE_NAME NOT LIKE 'fb_%' AND TABLE_NAME NOT LIKE 'ehr_%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX"""
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '3':  # 数据量
        sql = """SELECT TABLE_NAME, TABLE_ROWS, DATA_LENGTH, INDEX_LENGTH 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME NOT LIKE 'act_%' AND TABLE_NAME NOT LIKE 'fb_%' AND TABLE_NAME NOT LIKE 'ehr_%' AND TABLE_TYPE='BASE TABLE'
ORDER BY TABLE_ROWS DESC"""
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '4':  # 枚举值分布
        sql = """SELECT 'projectType' AS field, projectType AS val, COUNT(*) AS cnt FROM dppms_d365.pm_project GROUP BY projectType
UNION ALL
SELECT 'projectState', projectState, COUNT(*) FROM dppms_d365.pm_project GROUP BY projectState
UNION ALL
SELECT 'memberRole', memberRole, COUNT(*) FROM dppms_d365.pm_project_member GROUP BY memberRole
UNION ALL
SELECT 'applyState_presales', CAST(applyState AS CHAR), COUNT(*) FROM dppms_d365.pm_presales_project_header GROUP BY applyState
UNION ALL
SELECT 'status_prob', status, COUNT(*) FROM dppms_d365.prob_main GROUP BY status
UNION ALL
SELECT 'state_subcontract', CAST(state AS CHAR), COUNT(*) FROM dppms_d365.pm_subcontract_project_header GROUP BY state
UNION ALL
SELECT 'source_order', source, COUNT(*) FROM dppms_d365.pm_order_data_from_erp_source GROUP BY source
UNION ALL
SELECT 'orderType', CAST(orderType AS CHAR), COUNT(*) FROM dppms_d365.pm_order_data_from_erp_source GROUP BY orderType
UNION ALL
SELECT 'isparam_dept', CAST(isparam AS CHAR), COUNT(*) FROM dppms_d365.fnd_department GROUP BY isparam
UNION ALL
SELECT 'salesType', salesType, COUNT(*) FROM dppms_d365.pm_project GROUP BY salesType"""
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '5':  # pm_column_of_relationship
        sql = "SELECT * FROM dppms_d365.pm_column_of_relationship ORDER BY projectType, columnCode"
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '6':  # fnd_basic_data_type
        sql = "SELECT * FROM dppms_d365.fnd_basic_data_type ORDER BY id"
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    elif step == '7':  # 样例数据
        tables = ['pm_project', 'pm_project_state', 'pm_project_member', 'pm_presales_project_header',
                  'pm_cl_callback', 'pm_subcontract_project_header', 'prob_main', 'pm_project_maintenance',
                  'pm_order_data_from_erp_source', 'fnd_user_info']
        all_data = {}
        for t in tables:
            try:
                result = run_query(conn, f"SELECT * FROM dppms_d365.{t} LIMIT 3")
                all_data[t] = result
            except Exception as e:
                all_data[t] = str(e)
        print(json.dumps(all_data, ensure_ascii=False, default=str))
    
    elif step == '8':  # 外键关系
        sql = """SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME 
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA='dppms_d365' AND REFERENCED_TABLE_NAME IS NOT NULL AND TABLE_NAME NOT LIKE 'act_%'"""
        result = run_query(conn, sql)
        print(json.dumps(result, ensure_ascii=False, default=str))
    
    conn.close()

if __name__ == '__main__':
    main()
