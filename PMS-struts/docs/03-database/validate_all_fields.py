#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""全面验证：对比MD文件与数据库的字段列表，找出缺失字段"""
import re
import os
import pymysql

MD_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict.md')
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '!Q@W3e4r',
    'database': 'dppms_d365',
    'charset': 'utf8mb4',
}

# 需要排除的表（临时表和视图）
EXCLUDE_PREFIXES = ('temp_', 'tmp_')


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    text = data.decode('utf-8', errors='replace')
    text = text.replace('\r\n', '\n').replace('\r', '\n')
    return text


def parse_md_tables(lines):
    """解析MD中所有表的字段列表，返回 {table_name: set(field_names)}"""
    tables = {}
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        # 匹配表标题
        m = re.match(r'^#{3,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not is_valid_table_name(m.group(1)):
            i += 1
            continue

        table_name = m.group(1)
        # 跳过属性表，找字段列表
        fields = set()
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 字段名'):
                i += 1  # skip header
                i += 1  # skip separator
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if parts:
                        fields.add(parts[0])
                    i += 1
                break
            elif l.startswith('**索引') or l.startswith('**外键') or l == '---' or re.match(r'^#{2,5}', l):
                break
            elif re.match(r'^与\s+\S+\s+结构', l):
                # 引用表，标记为需要后续处理
                break
            else:
                i += 1

        tables[table_name] = fields
        # 跳到下一个表
        while i < len(lines):
            l = lines[i].strip()
            if l == '---' or re.match(r'^#{2,5}\s+', l):
                break
            i += 1

    return tables


def is_valid_table_name(name):
    if not name or len(name) < 2:
        return False
    if re.search(r'[\u4e00-\u9fff]', name):
        return False
    if name.startswith('**'):
        return False
    return True


def get_db_tables(conn):
    """获取数据库中所有基表（排除temp_*/tmp_*和视图）的字段列表"""
    cur = conn.cursor()

    # 获取所有基表名
    cur.execute(
        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
        "WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_TYPE='BASE TABLE' "
        "AND TABLE_NAME NOT LIKE 'temp_%%' AND TABLE_NAME NOT LIKE 'tmp_%%'"
    )
    table_names = [r[0] for r in cur.fetchall()]

    # 获取所有字段
    cur.execute(
        "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, "
        "COLUMN_KEY, EXTRA, COLUMN_COMMENT, ORDINAL_POSITION "
        "FROM INFORMATION_SCHEMA.COLUMNS "
        "WHERE TABLE_SCHEMA='dppms_d365' "
        "ORDER BY TABLE_NAME, ORDINAL_POSITION"
    )
    all_columns = cur.fetchall()

    db_tables = {}
    for row in all_columns:
        tname = row[0]
        if tname.startswith(EXCLUDE_PREFIXES):
            continue
        if tname not in db_tables:
            db_tables[tname] = {}
        col_name = row[1]
        db_tables[tname][col_name] = {
            'COLUMN_TYPE': row[2],
            'IS_NULLABLE': row[3],
            'COLUMN_DEFAULT': row[4],
            'COLUMN_KEY': row[5],
            'EXTRA': row[6],
            'COLUMN_COMMENT': row[7],
        }

    cur.close()
    return db_tables, set(table_names)


def main():
    print("=" * 70)
    print("全面验证：MD文件 vs 数据库 字段对比")
    print("=" * 70)

    # 1. 读取MD
    text = read_md(MD_FILE)
    lines = text.split('\n')
    print(f"MD文件行数: {len(lines)}")

    # 2. 解析MD中的表字段
    md_tables = parse_md_tables(lines)
    print(f"MD中解析到表数: {len(md_tables)}")

    # 3. 连接数据库
    conn = pymysql.connect(**DB_CONFIG)
    db_tables, db_table_names = get_db_tables(conn)
    print(f"数据库中基表数(排除temp/tmp): {len(db_table_names)}")

    # 4. 对比
    issues = []

    # 4a. MD中有但数据库没有的表
    md_only = set(md_tables.keys()) - db_table_names
    if md_only:
        print(f"\n[仅在MD中存在] {len(md_only)} 张表:")
        for t in sorted(md_only):
            print(f"  {t}")

    # 4b. 数据库中有但MD没有的表
    db_only = db_table_names - set(md_tables.keys())
    if db_only:
        print(f"\n[仅在数据库中存在] {len(db_only)} 张表:")
        for t in sorted(db_only):
            print(f"  {t}")

    # 4c. 两边都有但字段不一致的表
    common_tables = set(md_tables.keys()) & db_table_names
    missing_fields_count = 0
    extra_fields_count = 0

    print(f"\n[字段对比] 共 {len(common_tables)} 张公共表:")
    for tname in sorted(common_tables):
        md_fields = md_tables[tname]
        db_fields = set(db_tables[tname].keys())

        missing = db_fields - md_fields  # 数据库有但MD没有
        extra = md_fields - db_fields    # MD有但数据库没有

        if missing or extra:
            issue = {'table': tname, 'missing': missing, 'extra': extra}
            issues.append(issue)
            if missing:
                missing_fields_count += len(missing)
            if extra:
                extra_fields_count += len(extra)
            print(f"\n  {tname}:")
            if missing:
                print(f"    MD缺失字段({len(missing)}): {', '.join(sorted(missing))}")
            if extra:
                print(f"    MD多余字段({len(extra)}): {', '.join(sorted(extra))}")

    print(f"\n{'=' * 70}")
    print(f"汇总:")
    print(f"  有差异的表数: {len(issues)}")
    print(f"  MD缺失字段总数: {missing_fields_count}")
    print(f"  MD多余字段总数: {extra_fields_count}")
    print(f"  仅在MD中的表: {len(md_only)}")
    print(f"  仅在数据库中的表: {len(db_only)}")

    # 5. 输出需要补全的表详细信息
    if issues:
        print(f"\n{'=' * 70}")
        print("需要补全字段的表（详细）:")
        for issue in issues:
            tname = issue['table']
            if issue['missing']:
                print(f"\n  {tname} - 缺失字段详情:")
                for fname in sorted(issue['missing']):
                    col_info = db_tables[tname][fname]
                    print(f"    {fname} | {col_info['COLUMN_TYPE']} | {col_info['IS_NULLABLE']} | "
                          f"{col_info['COLUMN_DEFAULT']} | {col_info['COLUMN_KEY']} | "
                          f"{col_info['EXTRA']} | {col_info['COLUMN_COMMENT']}")

    conn.close()
    return issues, db_tables, md_tables


if __name__ == '__main__':
    main()
