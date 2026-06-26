#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
补全 database_dict.md 中所有不完整的字段列表
- 对比MD与数据库，找出字段不一致的表
- 从数据库查询完整字段列表，替换MD中的字段表
- 保留原有的属性区、索引区、外键区
- 同时更新索引列表
"""
import re
import os
import pymysql

MD_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict.md')
DB_CONFIG = {'host': 'localhost', 'user': 'root', 'password': '!Q@W3e4r', 'database': 'dppms_d365', 'charset': 'utf8mb4'}


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')


def is_valid_table_name(name):
    if not name or len(name) < 2 or re.search(r'[\u4e00-\u9fff]', name) or name.startswith('**'):
        return False
    return True


def get_db_columns(conn, table_name):
    """获取数据库表的完整字段列表"""
    cur = conn.cursor()
    cur.execute(
        "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, "
        "COLUMN_KEY, EXTRA, COLUMN_COMMENT, ORDINAL_POSITION "
        "FROM INFORMATION_SCHEMA.COLUMNS "
        "WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME=%s "
        "ORDER BY ORDINAL_POSITION", (table_name,)
    )
    rows = cur.fetchall()
    cur.close()
    return rows


def get_db_indexes(conn, table_name):
    """获取数据库表的索引列表"""
    cur = conn.cursor()
    cur.execute(
        "SELECT INDEX_NAME, INDEX_TYPE, NON_UNIQUE, "
        "GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS COLUMNS "
        "FROM INFORMATION_SCHEMA.STATISTICS "
        "WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME=%s "
        "GROUP BY INDEX_NAME, INDEX_TYPE, NON_UNIQUE",
        (table_name,)
    )
    rows = cur.fetchall()
    cur.close()
    return rows


def format_field_row(row):
    """格式化一个字段行为MD表格行"""
    col_name, col_type, is_nullable, col_default, col_key, extra, col_comment = row[:7]

    # 约束
    constraint = ''
    if col_key == 'PRI':
        constraint = 'PRI'
    elif col_key == 'UNI':
        constraint = 'UNI'
    elif col_key == 'MUL':
        constraint = 'MUL'
    if extra and 'auto_increment' in extra.lower():
        constraint += ', AUTO_INCREMENT' if constraint else 'AUTO_INCREMENT'

    # 默认值
    default_val = str(col_default) if col_default is not None else '-'
    if default_val == 'None':
        default_val = '-'

    # 可空
    nullable = 'YES' if is_nullable == 'YES' else 'NO'

    # 字段描述
    comment = col_comment or ''

    return f"| {col_name} | {col_type} | {nullable} | {default_val} | {constraint} | {comment} | |"


def format_index_rows(index_rows):
    """格式化索引列表为MD表格"""
    if not index_rows:
        return []
    lines = [
        '',
        '**索引列表**',
        '',
        '| 索引名 | 索引类型 | 唯一性 | 索引字段 |',
        '|--------|----------|--------|----------|',
    ]
    for row in index_rows:
        idx_name, idx_type, non_unique, idx_columns = row
        uniqueness = 'UNIQUE' if not non_unique else 'NON-UNIQUE'
        lines.append(f"| {idx_name} | {idx_type} | {uniqueness} | {idx_columns} |")
    return lines


def find_table_block(lines, table_name, start=0):
    """找到MD中某个表定义的起始和结束行"""
    for i in range(start, len(lines)):
        line = lines[i].strip()
        m = re.match(r'^#{3,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) == table_name:
            # 找到表标题，现在找结束位置
            block_start = i
            block_end = i + 1
            # 跳到下一个同级或更高级标题或分隔线
            for j in range(i + 1, len(lines)):
                l = lines[j].strip()
                if l == '---':
                    block_end = j
                    break
                if re.match(r'^#{1,5}\s+', l) and not re.match(r'^#{3,5}\s+[\d.]*\s*\S+', l):
                    # 子标题（如字段列表标题等）不算
                    pass
                m2 = re.match(r'^#{2,5}\s+[\d.]*\s*\S+', l)
                if m2 and is_valid_table_name(m2.group(1).split()[0] if m2.group(1) else ''):
                    block_end = j
                    break
            return block_start, block_end
    return None, None


def rebuild_table_block(lines, block_start, block_end, db_columns, db_indexes, table_name):
    """重建一个表定义块，保留属性区，替换字段列表和索引列表"""
    old_lines = lines[block_start:block_end]

    # 提取标题行
    title_line = old_lines[0]

    # 提取属性区
    attr_lines = []
    in_attr = False
    for line in old_lines[1:]:
        l = line.strip()
        if l.startswith('|') and '属性' in l and '值' in l:
            in_attr = True
            attr_lines.append(line)
        elif in_attr:
            if l.startswith('|') and '---' not in l:
                attr_lines.append(line)
            elif l.startswith('|') and '---' in l:
                attr_lines.append(line)
                in_attr = False
            else:
                in_attr = False
                break

    # 构建新的表定义块
    new_block = [title_line, '']

    # 属性区
    if attr_lines:
        new_block.extend(attr_lines)
        new_block.append('')

    # 字段列表
    new_block.append('**字段列表**')
    new_block.append('')
    new_block.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
    new_block.append('|--------|----------|------|--------|------|----------|----------|')
    for row in db_columns:
        new_block.append(format_field_row(row))

    # 索引列表
    if db_indexes:
        idx_lines = format_index_rows(db_indexes)
        new_block.extend(idx_lines)

    new_block.append('')

    return new_block


def main():
    print("=" * 70)
    print("补全 database_dict.md 中不完整的字段列表")
    print("=" * 70)

    # 1. 读取MD
    text = read_md(MD_FILE)
    lines = text.split('\n')
    print(f"MD文件行数: {len(lines)}")

    # 2. 解析MD中的表字段
    md_tables = {}
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{3,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not is_valid_table_name(m.group(1)):
            i += 1
            continue
        table_name = m.group(1)
        fields = set()
        j = i + 1
        while j < len(lines):
            l = lines[j].strip()
            if l.startswith('| 字段名'):
                j += 1; j += 1
                while j < len(lines) and lines[j].strip().startswith('|') and '---' not in lines[j]:
                    parts = [p.strip() for p in lines[j].strip().split('|')]
                    parts = [p for p in parts if p]
                    if parts:
                        fields.add(parts[0])
                    j += 1
                break
            elif l.startswith('**索引') or l.startswith('**外键') or l == '---' or re.match(r'^#{2,5}', l):
                break
            else:
                j += 1
        md_tables[table_name] = {'fields': fields, 'line_no': i}
        while j < len(lines):
            l = lines[j].strip()
            if l == '---' or re.match(r'^#{2,5}\s+', l):
                break
            j += 1
        i = j

    # 3. 连接数据库
    conn = pymysql.connect(**DB_CONFIG)

    # 获取所有基表字段
    cur = conn.cursor()
    cur.execute(
        "SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
        "WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME, ORDINAL_POSITION"
    )
    db_cols = {}
    for tname, cname in cur.fetchall():
        if tname.startswith(('temp_', 'tmp_')):
            continue
        db_cols.setdefault(tname, set()).add(cname)
    cur.close()

    # 4. 找出需要补全的表
    common = set(md_tables.keys()) & set(db_cols.keys())
    tables_to_fix = []
    for tname in sorted(common):
        md_f = md_tables[tname]['fields']
        db_f = db_cols.get(tname, set())
        missing = db_f - md_f
        extra = md_f - db_f
        if missing or extra:
            tables_to_fix.append({
                'name': tname,
                'missing': missing,
                'extra': extra,
                'line_no': md_tables[tname]['line_no'],
            })

    print(f"需要补全的表数: {len(tables_to_fix)}")
    print(f"总缺失字段: {sum(len(t['missing']) for t in tables_to_fix)}")
    print(f"总多余字段: {sum(len(t['extra']) for t in tables_to_fix)}")

    # 5. 从后往前替换（避免行号偏移）
    tables_to_fix.sort(key=lambda t: t['line_no'], reverse=True)

    fixed_count = 0
    for tinfo in tables_to_fix:
        tname = tinfo['name']
        line_no = tinfo['line_no']

        # 查询完整字段和索引
        db_columns = get_db_columns(conn, tname)
        db_indexes = get_db_indexes(conn, tname)

        if not db_columns:
            print(f"  [跳过] {tname}: 数据库中无字段")
            continue

        # 找到表定义块的范围
        block_start = line_no
        block_end = line_no + 1
        for j in range(line_no + 1, len(lines)):
            l = lines[j].strip()
            if l == '---':
                block_end = j
                break
            m2 = re.match(r'^#{2,5}\s+[\d.]*\s*\S+', l)
            if m2 and is_valid_table_name(m2.group(1).split()[0] if m2.group(1) else ''):
                block_end = j
                break
        else:
            block_end = len(lines)

        # 重建表定义块
        new_block = rebuild_table_block(lines, block_start, block_end, db_columns, db_indexes, tname)

        # 替换
        lines[block_start:block_end] = new_block
        fixed_count += 1

        missing_count = len(tinfo['missing'])
        extra_count = len(tinfo['extra'])
        print(f"  [已补全] {tname}: 补{missing_count}个字段, 删{extra_count}个多余字段, DB共{len(db_columns)}个字段")

    conn.close()

    # 6. 写回MD
    with open(MD_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n补全完成: {fixed_count} 张表已更新")
    print(f"MD文件已保存: {MD_FILE}")


if __name__ == '__main__':
    main()
