#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
用 full.md 的完整字段列表替换 final.md 中字段不一致的表定义
保留 final.md 的章节结构、属性区格式、业务含义
"""
import re, os, copy, json

BASE = os.path.dirname(os.path.abspath(__file__))
FINAL_FILE = os.path.join(BASE, 'database_dict final.md')
FULL_FILE = os.path.join(BASE, 'database_dict_full.md')
SCHEMA_DIR = os.path.join(BASE, 'schema_data')
DB_CONFIG = {'host':'localhost','user':'root','password':'!Q@W3e4r','database':'dppms_d365','charset':'utf8mb4'}


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')


def load_json(filename):
    with open(os.path.join(SCHEMA_DIR, filename), 'r', encoding='utf-8') as f:
        return json.load(f)


def extract_table_blocks(text):
    """提取MD中每个表的完整块，返回 {table_name: (start_line, end_line, block_lines)}"""
    lines = text.split('\n')
    blocks = {}
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if not m or not m.group(1) or len(m.group(1))<2 or re.search(r'[\u4e00-\u9fff]',m.group(1)) or m.group(1).startswith('**') or m.group(1).startswith('第') or m.group(1).startswith('附录') or m.group(1)=='DPPMS':
            i += 1
            continue
        tname = m.group(1)
        block_start = i
        block_end = i + 1
        for j in range(i+1, len(lines)):
            l = lines[j].strip()
            if l == '---':
                block_end = j + 1
                break
            m2 = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
            if m2 and m2.group(1) and len(m2.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m2.group(1)):
                block_end = j
                break
        else:
            block_end = len(lines)
        blocks[tname] = (block_start, block_end, lines[block_start:block_end])
        i = block_end
    return blocks


def parse_md_fields(text):
    """解析MD中每个表的字段集合"""
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


def rebuild_table_block_from_full(full_block, existing_biz, code_biz, db_columns, db_indexes, table_name):
    """用full.md的块重建，但保留final.md的业务含义"""
    lines = list(full_block)

    # 提取full.md中的属性区（保留）
    # 提取full.md中的字段列表（替换为数据库最新数据）
    # 提取full.md中的约束/索引/外键（保留）

    # 找到字段列表区域
    field_start = None
    field_end = None
    for idx, line in enumerate(lines):
        l = line.strip()
        if l.startswith('| 字段名') and field_start is None:
            field_start = idx
        if field_start is not None and field_end is None:
            if not l.startswith('|') or l.startswith('**') or l == '---':
                field_end = idx
                break

    if field_start is None or not db_columns:
        return lines  # 无法替换，返回原块

    # 构建新字段列表
    new_field_lines = []
    new_field_lines.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
    new_field_lines.append('|--------|----------|------|--------|------|----------|----------|')

    for row in db_columns:
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
            constraint += ', auto_increment' if constraint else 'auto_increment'

        # 默认值
        default_val = str(col_default) if col_default is not None else '-'
        if default_val == 'None':
            default_val = '-'

        # 可空
        nullable = is_nullable

        # 字段描述
        comment = col_comment or ''

        # 业务含义：优先 final.md已有 > 代码 > 数据库注释
        biz = ''
        if table_name in existing_biz:
            fields_biz = existing_biz[table_name].get('fields', {})
            if col_name in fields_biz and fields_biz[col_name]:
                biz = fields_biz[col_name]
        if not biz and table_name in code_biz:
            fields_biz = code_biz[table_name].get('fields', {})
            if col_name in fields_biz and fields_biz[col_name]:
                biz = fields_biz[col_name]
        if not biz and comment:
            biz = comment

        # 转义管道符
        comment_esc = comment.replace('|', '\\|')
        biz_esc = biz.replace('|', '\\|')

        new_field_lines.append(f"| {col_name} | {col_type} | {nullable} | {default_val} | {constraint} | {comment_esc} | {biz_esc} |")

    # 替换字段区域
    if field_end is None:
        field_end = len(lines)

    new_lines = lines[:field_start] + new_field_lines + lines[field_end:]
    return new_lines


def main():
    print("=" * 70)
    print("补充 final.md 中缺失的字段列表")
    print("=" * 70)

    # 1. 读取文件
    final_text = read_md(FINAL_FILE)
    full_text = read_md(FULL_FILE)

    # 2. 解析字段
    import pymysql
    conn = pymysql.connect(**DB_CONFIG)

    # 获取数据库字段
    cur = conn.cursor()
    cur.execute("SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='dppms_d365' ORDER BY TABLE_NAME, ORDINAL_POSITION")
    db_cols = {}
    for tname, cname in cur.fetchall():
        if tname.startswith(('temp_','tmp_')): continue
        db_cols.setdefault(tname, set()).add(cname)
    cur.close()

    final_fields = parse_md_fields(final_text)

    # 3. 找出字段不一致的表
    issues = []
    for tname in sorted(set(final_fields.keys()) & set(db_cols.keys())):
        md_f = final_fields[tname]
        db_f = db_cols.get(tname, set())
        missing = db_f - md_f
        extra = md_f - db_f
        if missing or extra:
            issues.append(tname)

    print(f"字段不一致的表: {len(issues)}")

    if not issues:
        print("所有表字段一致，无需补充")
        conn.close()
        return

    # 4. 加载业务含义
    existing_biz = load_json('existing_biz_meanings.json')
    code_biz = load_json('code_biz_meanings.json')

    # 5. 提取full.md的表块
    full_blocks = extract_table_blocks(full_text)

    # 6. 替换final.md中的表块
    final_lines = final_text.split('\n')

    # 从后往前替换
    for tname in sorted(issues, reverse=True):
        # 找到final.md中该表的位置
        table_start = None
        table_end = None
        for i, line in enumerate(final_lines):
            l = line.strip()
            m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
            if m and m.group(1) == tname:
                table_start = i
                # 找到块结束
                for j in range(i+1, len(final_lines)):
                    lj = final_lines[j].strip()
                    if lj == '---':
                        table_end = j + 1
                        break
                    m2 = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', lj)
                    if m2 and m2.group(1) and len(m2.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m2.group(1)):
                        table_end = j
                        break
                else:
                    table_end = len(final_lines)
                break

        if table_start is None or table_end is None:
            print(f"  [跳过] {tname}: 未在final.md中找到")
            continue

        # 获取数据库字段和索引
        db_columns = get_db_columns(conn, tname)
        db_indexes = get_db_indexes(conn, tname)

        # 用full.md的块重建
        if tname in full_blocks:
            full_block = full_blocks[tname][2]
            new_block = rebuild_table_block_from_full(full_block, existing_biz, code_biz, db_columns, db_indexes, tname)
        else:
            # full.md也没有，从数据库构建
            new_block = build_from_db(tname, db_columns, db_indexes, existing_biz, code_biz)

        # 保留final.md的标题格式（编号）
        old_title = final_lines[table_start].strip()
        m_title = re.match(r'^(#{1,5}\s+)([\d.]+\s+)(\S+.*)$', old_title)
        if m_title:
            new_block[0] = f"{m_title.group(1)}{m_title.group(2)}{m_title.group(3)}"

        # 确保以 --- 结尾
        if new_block and new_block[-1].strip() != '---':
            new_block.append('---')

        # 替换
        final_lines[table_start:table_end] = new_block

        missing_count = len(db_cols.get(tname, set()) - final_fields.get(tname, set()))
        print(f"  [已替换] {tname}: DB共{len(db_columns)}个字段, 补{missing_count}个缺失字段")

    conn.close()

    # 7. 写回
    with open(FINAL_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(final_lines))

    print(f"\n补充完成！已替换 {len(issues)} 张表")
    print(f"文件已保存: {FINAL_FILE}")


def build_from_db(tname, db_columns, db_indexes, existing_biz, code_biz):
    """从数据库构建完整的表定义块"""
    # 查表注释
    import pymysql
    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor()
    cur.execute("SELECT TABLE_COMMENT, TABLE_TYPE, ENGINE, TABLE_ROWS, DATA_LENGTH+INDEX_LENGTH FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_NAME=%s", (tname,))
    row = cur.fetchone()
    cur.close(); conn.close()

    tcomment = row[0] if row else ''
    ttype = row[1] if row else 'BASE TABLE'
    engine = row[2] if row else ''
    rows = row[3] if row else 0
    size = row[4] if row else 0

    if size > 1024*1024:
        size_str = f"{size/1024/1024:.1f} MB"
    elif size > 1024:
        size_str = f"{size/1024:.1f} KB"
    else:
        size_str = f"{size} B"

    # 表级业务含义
    table_biz = ''
    if tname in existing_biz:
        table_biz = existing_biz[tname].get('table_biz_meaning', '')
    if not table_biz and tcomment:
        table_biz = tcomment

    lines = []
    title = f"### {tname}"
    if tcomment:
        title += f" -- {tcomment}"
    lines.append(title)
    lines.append('')
    lines.append('| 属性 | 值 |')
    lines.append('|------|-----|')
    lines.append(f"| 对象类型 | {ttype} |")
    if table_biz:
        lines.append(f"| 业务含义 | {table_biz} |")
    if rows:
        lines.append(f"| 数据量 | ~{rows:,} 行 |")
    if size:
        lines.append(f"| 数据大小 | {size_str} |")
    lines.append('')

    # 字段列表
    lines.append('**字段列表**')
    lines.append('')
    lines.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
    lines.append('|--------|----------|------|--------|------|----------|----------|')

    for col in db_columns:
        col_name, col_type, is_nullable, col_default, col_key, extra, col_comment = col[:7]
        constraint = ''
        if col_key == 'PRI': constraint = 'PRI'
        elif col_key == 'UNI': constraint = 'UNI'
        elif col_key == 'MUL': constraint = 'MUL'
        if extra and 'auto_increment' in extra.lower():
            constraint += ', auto_increment' if constraint else 'auto_increment'
        default_val = str(col_default) if col_default is not None else '-'
        if default_val == 'None': default_val = '-'
        comment = col_comment or ''
        biz = ''
        if tname in existing_biz:
            fb = existing_biz[tname].get('fields', {})
            if col_name in fb and fb[col_name]: biz = fb[col_name]
        if not biz and tname in code_biz:
            fb = code_biz[tname].get('fields', {})
            if col_name in fb and fb[col_name]: biz = fb[col_name]
        if not biz and comment: biz = comment
        comment_esc = comment.replace('|', '\\|')
        biz_esc = biz.replace('|', '\\|')
        lines.append(f"| {col_name} | {col_type} | {is_nullable} | {default_val} | {constraint} | {comment_esc} | {biz_esc} |")

    lines.append('')

    # 索引列表
    if db_indexes:
        lines.append('**索引列表**')
        lines.append('')
        lines.append('| 索引名 | 索引类型 | 唯一性 | 索引字段 |')
        lines.append('|--------|----------|--------|----------|')
        for idx in db_indexes:
            idx_name, idx_type, non_unique, idx_columns = idx
            uniqueness = 'UNIQUE' if not non_unique else 'NON-UNIQUE'
            lines.append(f"| {idx_name} | {idx_type} | {uniqueness} | {idx_columns} |")
        lines.append('')

    lines.append('---')
    return lines


if __name__ == '__main__':
    main()
