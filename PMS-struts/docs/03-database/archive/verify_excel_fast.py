#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""对比 Excel 和 MD 的字段描述/业务含义 - 高效版"""
import re, os, zipfile, xml.etree.ElementTree as ET

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')
XLSX_FILE = os.path.join(BASE, 'database_dict_final_v2.xlsx')


def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')


def parse_md_fields(text):
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
        obj_type = 'BASE TABLE'
        i += 1
        while i < len(lines):
            l = lines[i].strip()
            if l.startswith('|') and '对象类型' in l:
                parts = [p.strip() for p in l.split('|')]
                parts = [p for p in parts if p]
                if len(parts) >= 2:
                    obj_type = parts[1]
            if l.startswith('| 字段名'):
                i += 1; i += 1
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    parts = [p.strip() for p in lines[i].strip().split('|')]
                    parts = [p for p in parts if p]
                    if len(parts) >= 7:
                        fname = parts[0]
                        fields[fname] = (parts[5], parts[6])  # (字段描述, 业务含义)
                    i += 1
                break
            elif l.startswith('**约束') or l.startswith('**索引') or l.startswith('**外键') or l=='---' or re.match(r'^#{1,5}',l):
                break
            else:
                i += 1
        tables[tname] = {'obj_type': obj_type, 'fields': fields}
        while i < len(lines):
            l = lines[i].strip()
            if l=='---' or re.match(r'^#{1,5}\s+',l):
                break
            i += 1
    return tables


def parse_xlsx_fast(xlsx_file):
    """快速解析xlsx - 直接读sharedStrings和sheet xml"""
    tables = {}
    ns = {'s': 'http://schemas.openxmlformats.org/spreadsheetml/2006/main'}

    with zipfile.ZipFile(xlsx_file, 'r') as zf:
        # 读取共享字符串
        shared = []
        if 'xl/sharedStrings.xml' in zf.namelist():
            tree = ET.parse(zf.open('xl/sharedStrings.xml'))
            for si in tree.findall('.//s:si', ns):
                texts = []
                for t in si.findall('.//s:t', ns):
                    if t.text:
                        texts.append(t.text)
                shared.append(''.join(texts))

        def get_val(c_elem):
            v = c_elem.find('s:v', ns)
            t_attr = c_elem.get('t', '')
            if v is not None and v.text:
                if t_attr == 's':
                    idx = int(v.text)
                    return shared[idx] if idx < len(shared) else ''
                return v.text
            return ''

        # 读取每个sheet
        sheet_names = [n for n in zf.namelist() if n.startswith('xl/worksheets/sheet') and n.endswith('.xml')]
        for sn in sheet_names:
            tree = ET.parse(zf.open(sn))
            rows = tree.findall('.//s:sheetData/s:row', ns)

            # 构建行数据
            row_data = {}
            for row_elem in rows:
                row_num = int(row_elem.get('r'))
                cells = {}
                for c in row_elem.findall('s:c', ns):
                    ref = c.get('r')
                    col = re.match(r'([A-Z]+)', ref).group(1)
                    cells[col] = get_val(c)
                row_data[row_num] = cells

            # 解析表结构
            all_rows = sorted(row_data.keys())
            ri = 0
            while ri < len(all_rows):
                r = all_rows[ri]
                cells = row_data[r]
                a_val = cells.get('A', '').strip()

                # 检测表名标题行
                m = re.match(r'^(\S+)(?:\s+——\s+(.+))?$', a_val)
                if m and a_val and not a_val.startswith('字段名') and not a_val.startswith('索引') and not a_val.startswith('外键') and not a_val.startswith('对象类型'):
                    tname = m.group(1)
                    # 跳过属性区（4行）
                    ri += 1
                    while ri < len(all_rows):
                        nr = all_rows[ri]
                        nc = row_data[nr]
                        if nc.get('A', '').strip() == '字段名':
                            break
                        ri += 1

                    if ri >= len(all_rows):
                        break

                    # 字段表头行，跳过
                    ri += 1
                    fields = {}
                    while ri < len(all_rows):
                        nr = all_rows[ri]
                        nc = row_data[nr]
                        fname = nc.get('A', '').strip()
                        if not fname or fname.startswith('索引') or fname.startswith('外键') or '——' in fname:
                            break
                        comment = nc.get('F', '').strip()
                        biz = nc.get('G', '').strip()
                        fields[fname] = (comment, biz)
                        ri += 1
                    tables[tname] = fields
                else:
                    ri += 1

    return tables


def main():
    print("对比 Excel vs database_dict final.md")
    print("=" * 70)

    md_text = read_md(MD_FILE)
    md_tables = parse_md_fields(md_text)
    xlsx_tables = parse_xlsx_fast(XLSX_FILE)

    # 过滤MD中的temp/tmp/视图
    filtered_md = {}
    for tname, tdata in md_tables.items():
        if tname.startswith(('temp_', 'tmp_')):
            continue
        if tdata['obj_type'] == 'VIEW':
            continue
        filtered_md[tname] = tdata['fields']

    print(f"MD 有效表数: {len(filtered_md)}")
    print(f"Excel 表数: {len(xlsx_tables)}")

    # 表级别
    md_only = set(filtered_md.keys()) - set(xlsx_tables.keys())
    xlsx_only = set(xlsx_tables.keys()) - set(filtered_md.keys())
    common = set(filtered_md.keys()) & set(xlsx_tables.keys())

    print(f"仅在MD中: {len(md_only)}")
    if md_only:
        for t in sorted(md_only):
            print(f"  {t}")
    print(f"仅在Excel中: {len(xlsx_only)}")
    if xlsx_only:
        for t in sorted(xlsx_only):
            print(f"  {t}")
    print(f"公共表: {len(common)}")

    # 字段级别
    field_missing = 0
    field_extra = 0
    comment_diff = 0
    biz_diff = 0
    diffs = []

    for tname in sorted(common):
        md_f = filtered_md[tname]
        xlsx_f = xlsx_tables[tname]
        md_names = set(md_f.keys())
        xlsx_names = set(xlsx_f.keys())

        missing = md_names - xlsx_names
        extra = xlsx_names - md_names
        field_missing += len(missing)
        field_extra += len(extra)
        if missing:
            diffs.append(f"  {tname}: Excel缺字段 {sorted(missing)}")
        if extra:
            diffs.append(f"  {tname}: Excel多字段 {sorted(extra)}")

        for fname in md_names & xlsx_names:
            mc, mb = md_f[fname]
            xc, xb = xlsx_f[fname]
            if mc != xc:
                comment_diff += 1
                diffs.append(f"  {tname}.{fname} 描述: MD='{mc[:50]}' XLSX='{xc[:50]}'")
            if mb != xb:
                biz_diff += 1
                diffs.append(f"  {tname}.{fname} 含义: MD='{mb[:50]}' XLSX='{xb[:50]}'")

    print(f"\n--- 字段级别 ---")
    print(f"Excel缺失字段: {field_missing}")
    print(f"Excel多余字段: {field_extra}")
    print(f"字段描述差异: {comment_diff}")
    print(f"业务含义差异: {biz_diff}")

    md_total = sum(len(v) for v in filtered_md.values())
    xlsx_total = sum(len(v) for v in xlsx_tables.values())
    print(f"\nMD总字段数: {md_total}")
    print(f"Excel总字段数: {xlsx_total}")

    if diffs:
        print(f"\n差异详情（前30条）:")
        for d in diffs[:30]:
            print(d)
    else:
        print(f"\n完全一致，无任何差异！")


if __name__ == '__main__':
    main()
