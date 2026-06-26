#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
用同名字段已维护的字段描述和业务含义填充空值
优先级：同名字段已有值 > 数据库COMMENT > 字段名推断
"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def main():
    print("=" * 60)
    print("用同名字段已维护的描述/含义填充空值")
    print("=" * 60)

    text = read_md(MD_FILE)
    lines = text.split('\n')

    # 第一遍：收集每个字段名已有的字段描述和业务含义
    print("\n1. 收集同名字段已有的描述和含义...")
    # {field_name: {'comment': best_comment, 'biz': best_biz}}
    field_cache = {}
    current_table = ''
    in_field_table = False

    for i, line in enumerate(lines):
        l = line.strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
        if m and m.group(1) and len(m.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m.group(1)) and not m.group(1).startswith('**'):
            current_table = m.group(1)

        if l.startswith('| 字段名'):
            in_field_table = True
            continue
        if l.startswith('|---'):
            continue
        if in_field_table and (not l.startswith('|') or l.startswith('| 索引') or l.startswith('| 外键') or l.startswith('| 属性') or l.startswith('| 约束')):
            in_field_table = False
            continue

        if in_field_table and l.startswith('|'):
            parts = [p.strip() for p in l.split('|')]
            parts = [p for p in parts if p]
            if len(parts) >= 7:
                fname = parts[0]
                comment = parts[5]
                biz = parts[6]

                # 只收集有值的
                if fname and fname not in ('字段名',):
                    if fname not in field_cache:
                        field_cache[fname] = {'comment': '', 'biz': '', 'comment_src': '', 'biz_src': ''}

                    # 优先保留更详细的描述
                    if comment and comment not in ('-', 'None', ''):
                        existing = field_cache[fname]['comment']
                        if not existing or len(comment) > len(existing):
                            field_cache[fname]['comment'] = comment
                            field_cache[fname]['comment_src'] = current_table

                    if biz and biz not in ('-', 'None', '', '业务含义待确认'):
                        existing = field_cache[fname]['biz']
                        if not existing or len(biz) > len(existing):
                            field_cache[fname]['biz'] = biz
                            field_cache[fname]['biz_src'] = current_table

    print(f"   收集到 {len(field_cache)} 个字段名的描述/含义缓存")

    # 统计缓存中有描述和含义的
    has_comment = sum(1 for v in field_cache.values() if v['comment'])
    has_biz = sum(1 for v in field_cache.values() if v['biz'])
    print(f"   有字段描述: {has_comment}, 有业务含义: {has_biz}")

    # 第二遍：用缓存填充空值
    print("\n2. 用同名字段缓存填充空值...")
    filled_comment = 0
    filled_biz = 0
    filled_both = 0
    current_table = ''
    in_field_table = False

    for i, line in enumerate(lines):
        l = line.strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', l)
        if m and m.group(1) and len(m.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m.group(1)) and not m.group(1).startswith('**'):
            current_table = m.group(1)

        if l.startswith('| 字段名'):
            in_field_table = True
            continue
        if l.startswith('|---'):
            continue
        if in_field_table and (not l.startswith('|') or l.startswith('| 索引') or l.startswith('| 外键') or l.startswith('| 属性') or l.startswith('| 约束')):
            in_field_table = False
            continue

        if in_field_table and l.startswith('|'):
            parts = [p.strip() for p in l.split('|')]
            parts = [p for p in parts if p]
            if len(parts) >= 7:
                fname = parts[0]
                comment = parts[5]
                biz = parts[6]

                if fname in field_cache:
                    cache = field_cache[fname]
                    new_comment = comment
                    new_biz = biz
                    changed = False

                    # 填充空字段描述
                    if (not comment or comment in ('-', 'None', '')) and cache['comment']:
                        new_comment = cache['comment']
                        filled_comment += 1
                        changed = True

                    # 填充空业务含义
                    if (not biz or biz in ('-', 'None', '', '业务含义待确认')) and cache['biz']:
                        new_biz = cache['biz']
                        filled_biz += 1
                        changed = True

                    if changed:
                        filled_both += 1
                        # 重建行
                        dtype = parts[1]
                        nullable = parts[2]
                        default_val = parts[3]
                        constraint = parts[4]
                        lines[i] = f"| {fname} | {dtype} | {nullable} | {default_val} | {constraint} | {new_comment} | {new_biz} |"

    # 写回
    with open(MD_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n填充完成:")
    print(f"  填充字段描述: {filled_comment}")
    print(f"  填充业务含义: {filled_biz}")
    print(f"  涉及行数: {filled_both}")

    # 第三遍：统计剩余空值
    print("\n3. 统计剩余空值...")
    empty_comment = 0
    empty_biz = 0
    total = 0
    in_field_table = False

    for line in lines:
        l = line.strip()
        if l.startswith('| 字段名'):
            in_field_table = True
            continue
        if l.startswith('|---'):
            continue
        if in_field_table and (not l.startswith('|') or l.startswith('| 索引') or l.startswith('| 外键') or l.startswith('| 属性') or l.startswith('| 约束')):
            in_field_table = False
            continue
        if in_field_table and l.startswith('|'):
            parts = [p.strip() for p in l.split('|')]
            parts = [p for p in parts if p]
            if len(parts) >= 7:
                total += 1
                if not parts[5] or parts[5] in ('-', 'None', ''):
                    empty_comment += 1
                if not parts[6] or parts[6] in ('-', 'None', '', '业务含义待确认'):
                    empty_biz += 1

    print(f"  总字段数: {total}")
    print(f"  字段描述为空: {empty_comment} ({empty_comment*100//total}%)")
    print(f"  业务含义为空: {empty_biz} ({empty_biz*100//total}%)")

if __name__ == '__main__':
    main()
