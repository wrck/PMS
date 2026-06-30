#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查 dppms_b365 数据库"""
import pymysql

conn = pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', charset='utf8mb4')
cur = conn.cursor()

# 列出所有dppms数据库
cur.execute("SHOW DATABASES LIKE 'dppms%'")
dbs = [r[0] for r in cur.fetchall()]
print(f"dppms 数据库列表: {dbs}")

# 检查 dppms_b365
if 'dppms_b365' in dbs:
    cur.execute("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_b365'")
    count = cur.fetchone()[0]
    print(f"\ndppms_b365 表/视图数: {count}")

    cur.execute("SELECT TABLE_TYPE, COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_b365' GROUP BY TABLE_TYPE")
    for row in cur.fetchall():
        print(f"  {row[0]}: {row[1]}")

    # 对比 d365 和 b365 的表数量
    cur.execute("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_TYPE='BASE TABLE'")
    d365_count = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_b365' AND TABLE_TYPE='BASE TABLE'")
    b365_count = cur.fetchone()[0]
    print(f"\ndppms_d365 基表数: {d365_count}")
    print(f"dppms_b365 基表数: {b365_count}")

    # 找出 b365 有但 d365 没有的表
    cur.execute("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_b365' AND TABLE_TYPE='BASE TABLE'")
    b365_tables = set(r[0] for r in cur.fetchall())
    cur.execute("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_TYPE='BASE TABLE'")
    d365_tables = set(r[0] for r in cur.fetchall())

    only_b365 = b365_tables - d365_tables
    only_d365 = d365_tables - b365_tables
    print(f"\n仅在 b365 中: {len(only_b365)} 张表")
    if only_b365:
        for t in sorted(only_b365)[:10]:
            print(f"  {t}")
    print(f"\n仅在 d365 中: {len(only_d365)} 张表")
    if only_d365:
        for t in sorted(only_d365)[:10]:
            print(f"  {t}")
else:
    print("dppms_b365 数据库不存在！")

cur.close()
conn.close()
