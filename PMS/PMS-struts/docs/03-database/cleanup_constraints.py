import re, os

DICT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'database_dict.md')

with open(DICT_FILE, 'r', encoding='utf-8') as f:
    text = f.read()

lines = text.split('\n')
result = []
i = 0
removed = 0

while i < len(lines):
    line = lines[i]
    stripped = line.strip()

    # 检测约束信息表残留: |----------|----------|------|--------|----------|------|
    # 后面跟着 | PRIMARY KEY | ... | 或 | UNIQUE | ... | 等行
    if stripped.startswith('|----------|----------|------|--------|----------|------|'):
        # 检查前一行是否是空行（字段表结束后的空行）
        # 检查下一行是否是约束信息行
        if i + 1 < len(lines):
            next_line = lines[i + 1].strip()
            if next_line.startswith('|') and ('PRIMARY KEY' in next_line or 'UNIQUE' in next_line or 'FOREIGN KEY' in next_line or 'KEY' in next_line):
                # 跳过分隔行和约束信息行
                i += 1  # skip separator
                while i < len(lines) and lines[i].strip().startswith('|') and '---' not in lines[i]:
                    i += 1  # skip constraint rows
                removed += 1
                continue

    result.append(line)
    i += 1

new_text = '\n'.join(result)
with open(DICT_FILE, 'w', encoding='utf-8') as f:
    f.write(new_text)

print(f'Removed {removed} constraint info table remnants')
print(f'Lines: {len(lines)} -> {len(result)}')
