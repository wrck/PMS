import re

f = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\database_dict.md'
text = open(f, 'r', encoding='utf-8').read()
lines = text.split('\n')

# 统计标准格式
std_field_headers = text.count('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
std_index_headers = text.count('| 索引名 | 索引类型 | 唯一性 | 索引字段 |')
std_attr_headers = text.count('| 属性 | 值 |')
fk_headers = text.count('| 外键名 | 本表字段 | 引用表 | 引用字段 |')

# 统计旧格式
old_field_6col = text.count('| 字段名 | 数据类型 | 非空 |')
old_field_5col = text.count('| 字段名 | 数据类型 | 非空 | 默认值 | 业务含义 |')
old_field_3col = text.count('| 字段名 | 数据类型 | 业务含义 |')
old_index_5col = text.count('| 索引名 | 列 | 唯一性 | 索引类型 |')
old_index_inline = len(re.findall(r'\*\*索引\*\*[：:]', text))
old_attr_inline = len(re.findall(r'\*\*对象类型\*\*[：:]', text))
constraint_tables = text.count('**约束信息**')

# 统计表/视图数量
tables = re.findall(r'^#{2,5}\s+[\d.]*\s*\S+', text, re.MULTILINE)
table_names = set()
for t in tables:
    m = re.search(r'(\w{3,})', t)
    if m:
        table_names.add(m.group(1))

biz_pending = text.count('业务含义待确认')

print("=" * 60)
print("database_dict.md 最终验证报告")
print("=" * 60)
print(f"\n总行数: {len(lines)}")
print(f"\n--- 标准格式统计 ---")
print(f"7列字段表头: {std_field_headers}")
print(f"4列索引表头: {std_index_headers}")
print(f"2列属性表头: {std_attr_headers}")
print(f"4列外键表头: {fk_headers}")
print(f"\n--- 旧格式残留 ---")
print(f"6列字段表头: {old_field_6col}")
print(f"5列字段表头: {old_field_5col}")
print(f"3列字段表头: {old_field_3col}")
print(f"5列索引表头: {old_index_5col}")
print(f"行内索引格式: {old_index_inline}")
print(f"行内属性格式: {old_attr_inline}")
print(f"约束信息表: {constraint_tables}")
print(f"\n--- 内容统计 ---")
print(f"表/视图定义数: {len(tables)}")
print(f"'业务含义待确认': {biz_pending}")
print(f"\n--- 结论 ---")
if old_field_6col == 0 and old_field_5col == 0 and old_field_3col == 0 and old_index_5col == 0 and old_index_inline == 0 and old_attr_inline == 0 and constraint_tables == 0:
    print("PASS: 全文格式完全统一，无旧格式残留")
else:
    print("FAIL: 仍存在旧格式残留，需要进一步修复")
