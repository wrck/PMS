#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""全面验证：对比MD文件与数据库的字段列表，找出缺失字段 - 仅输出汇总"""
import re, os, pymysql

MD_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict.md')
DB_CONFIG = {'host':'localhost','user':'root','password':'!Q@W3e4r','database':'dppms_d365','charset':'utf8mb4'}

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    text = data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')
    return text

def is_valid_table_name(name):
    if not name or len(name)<2 or re.search(r'[\u4e00-\u9fff]',name) or name.startswith('**'):
        return False
    return True

def parse_md_tables(lines):
    tables = {}
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{3,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not is_valid_table_name(m.group(1)):
            i += 1
            continue
        table_name = m.group(1)
        fields = set()
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if parts:
                        fields.add(parts[0])
                    i += 1
                break
            elif l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{2,5}',l):
                break
            else:
                i += 1
        tables[table_name] = fields
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{2,5}\s+',l):
                break
            i += 1
    return tables

def main():
    text = read_md(MD_FILE)
    lines = text.split('\n')
    md_tables = parse_md_tables(lines)

    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor()
    cur.execute("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_TYPE='BASE TABLE' AND TABLE_NAME NOT LIKE 'temp_%%' AND TABLE_NAME NOT LIKE 'tmp_%%'")
    db_table_names = set(r[0] for r in cur.fetchall())

    cur.execute("SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME, ORDINAL_POSITION")
    db_cols = {}
    for tname, cname in cur.fetchall():
        if tname.startswith(('temp_','tmp_')):
            continue
        db_cols.setdefault(tname, set()).add(cname)
    cur.close(); conn.close()

    common = set(md_tables.keys()) & db_table_names
    issues = []
    for tname in sorted(common):
        md_f = md_tables[tname]
        db_f = db_cols.get(tname, set())
        missing = db_f - md_f
        extra = md_f - db_f
        if missing or extra:
            issues.append((tname, len(missing), len(extra), sorted(missing)[:5]))

    print(f"MD表数: {len(md_tables)}, DB基表数: {len(db_table_names)}, 公共表数: {len(common)}")
    print(f"有差异的表数: {len(issues)}")
    total_missing = sum(x[1] for x in issues)
    total_extra = sum(x[2] for x in issues)
    print(f"MD缺失字段总数: {total_missing}")
    print(f"MD多余字段总数: {total_extra}")
    print()
    for tname, mc, ec, sample in issues:
        print(f"  {tname}: 缺{mc} 多{ec} (缺:{','.join(sample)}{'...' if mc>5 else ''})")

if __name__ == '__main__':
    main()
