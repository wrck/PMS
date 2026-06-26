# -*- coding: utf-8 -*-
import pymysql
import json

conn = pymysql.connect(
    host='localhost',
    user='root',
    password='!Q@W3e4r',
    database='dppms_d365',
    charset='utf8mb4'
)
cur = conn.cursor()

tables = [
    'act_evt_log', 'act_ge_bytearray', 'act_ge_property', 'act_hi_actinst',
    'act_hi_attachment', 'act_hi_comment', 'act_hi_detail', 'act_hi_identitylink',
    'act_hi_procinst', 'act_hi_taskinst', 'act_hi_varinst', 'act_id_group',
    'act_id_info', 'act_id_membership', 'act_id_user', 'act_procdef_info',
    'act_re_deployment', 'act_re_model', 'act_re_procdef', 'act_ru_event_subscr',
    'act_ru_execution', 'act_ru_identitylink', 'act_ru_job', 'act_ru_task',
    'act_ru_task_callback_task_w04649', 'act_ru_variable'
]

results = {}

# 1. Get table stats
for t in tables:
    cur.execute(f"SELECT TABLE_ROWS, DATA_LENGTH, INDEX_LENGTH, TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME='{t}'")
    row = cur.fetchone()
    if row:
        results[t] = {
            'table_rows': row[0],
            'data_length': row[1],
            'index_length': row[2],
            'table_comment': row[3]
        }

# 2. Get SHOW CREATE TABLE
for t in tables:
    cur.execute(f"SHOW CREATE TABLE `{t}`")
    row = cur.fetchone()
    if row:
        results[t]['create_sql'] = row[1]

# 3. Get DESCRIBE
for t in tables:
    cur.execute(f"DESCRIBE `{t}`")
    rows = cur.fetchall()
    results[t]['columns'] = []
    for r in rows:
        results[t]['columns'].append({
            'field': r[0],
            'type': r[1],
            'null': r[2],
            'key': r[3],
            'default': r[4],
            'extra': r[5]
        })

# 4. Get indexes
for t in tables:
    cur.execute(f"SHOW INDEX FROM `{t}`")
    rows = cur.fetchall()
    results[t]['indexes'] = []
    for r in rows:
        results[t]['indexes'].append({
            'table': r[0],
            'non_unique': r[1],
            'key_name': r[2],
            'seq_in_index': r[3],
            'column_name': r[4],
            'collation': r[5],
            'index_type': r[10]
        })

# 5. Get column comments from information_schema
for t in tables:
    cur.execute(f"SELECT COLUMN_NAME, COLUMN_COMMENT FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME='{t}'")
    rows = cur.fetchall()
    col_comments = {}
    for r in rows:
        col_comments[r[0]] = r[1]
    results[t]['column_comments'] = col_comments

cur.close()
conn.close()

# Write to file
with open(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\act_tables_data.json', 'w', encoding='utf-8') as f:
    json.dump(results, f, ensure_ascii=False, default=str, indent=2)

print("Done! Output written to act_tables_data.json")
