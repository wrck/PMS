# -*- coding: utf-8 -*-
import pymysql
import sys
import traceback

try:
    conn = pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', database='dppms_d365', charset='utf8mb4')
    cur = conn.cursor()
    cur.execute("SELECT 1")
    result = cur.fetchone()
    with open('d:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\db_test_result.txt', 'w', encoding='utf-8') as f:
        f.write(f"Connection OK: {result}\n")
    cur.close()
    conn.close()
except Exception as e:
    with open('d:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\db_test_result.txt', 'w', encoding='utf-8') as f:
        f.write(f"Error: {traceback.format_exc()}\n")
