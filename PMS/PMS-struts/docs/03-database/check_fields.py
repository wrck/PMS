#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查 final.md 和 full.md 中所有表的字段列表是否与数据库一致"""
import re, os, pymysql, json

BASE = os.path.dirname(os.path.abspath(__file__))
SCHEMA_DIR = os.path.join(BASE, 'schema_data')
DB_CONFIG = {'host':'localhost','user':'root','password':'!Q@W3e4r','database':'dppms_d365','charset':'utf8mb4'}

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_md_fields(text):
    """解析MD中每个表的字段集合，返回 {table_name: {field_name: (line_no, biz_meaning)}}"""
    tables = {}
    lines = text.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
            i += 1
            continue
        tname = m.group(1)
        fields = {}
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('| 字段名'):
                i += 1; i += 1  # skip header + separator
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 1:
                        fname = parts[0]
                        biz = parts[6] if len(parts) >= 7 else ''
                        fields[fname] = (i, biz)
                    i += 1
                break
            elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                break
            else:
                i += 1
        tables[tname] = fields
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables

def main():
    # 1. 读取数据库字段
    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor()
    cur.execute("SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY, EXTRA, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME, ORDINAL_POSITION")
    db_cols = {}
    for row in cur.fetchall():
        tname, cname = row[0], row[1]
        if tname.startswith(('temp_','tmp_')): continue
        db_cols.setdefault(tname, {})[cname] = {
            'type': row[2], 'nullable': row[3], 'default': row[4],
            'key': row[5], 'extra': row[6], 'comment': row[7]
        }
    cur.close(); conn.close()

    # 2. 解析两个MD文件
    final_text = read_md(os.path.join(BASE, 'database_dict final.md'))
    full_text = read_md(os.path.join(BASE, 'database_dict_full.md'))
    final_tables = parse_md_fields(final_text)
    full_tables = parse_md_fields(full_text)

    # 3. 对比
    for label, md_tables in [('final.md', final_tables), ('full.md', full_tables)]:
        print(f"\n{'='*70}")
        print(f"  {label} 字段检查")
        print(f"{'='*70}")
        issue_count = 0
        total_missing = 0
        total_extra = 0
        for tname in sorted(set(md_tables.keys()) & set(db_cols.keys())):
            md_f = set(md_tables[tname].keys())
            db_f = set(db_cols[tname].keys())
            missing = db_f - md_f
            extra = md_f - db_f
            if missing or extra:
                issue_count += 1
                total_missing += len(missing)
                total_extra += len(extra)
                print(f"  {tname}: 缺{len(missing)} 多{len(extra)} (缺:{','.join(sorted(missing)[:5])}{'...' if len(missing)>5 else ''})")
        print(f"\n  有差异的表: {issue_count}, 缺失字段总数: {total_missing}, 多余字段总数: {total_extra}")

if __name__ == '__main__':
    main()
