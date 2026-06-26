import os, re

BASE_DIR = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database'
PROJECT_DIR = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts'
NEW_DICT_FILE = os.path.join(PROJECT_DIR, 'db_dictionary_dppms_d365.md')

# Read file
with open(NEW_DICT_FILE, 'rb') as f:
    data = f.read()
text = data.decode('utf-8', errors='replace')
text = text.replace('\r\n', '\n').replace('\r', '\n')
lines = text.split('\n')
print(f'Total lines: {len(lines)}')

# Test table names
ACT_TABLES = ['act_evt_log', 'act_ge_bytearray']
all_target = set(ACT_TABLES)
print(f'Target tables: {all_target}')

# Parse
found = 0
for i, line in enumerate(lines):
    stripped = line.strip()
    m = re.match(r'^###\s+\d+\s+(\S+)\s*(?:--\s*(.*))?$', stripped)
    if m:
        tname = m.group(1)
        if tname in all_target:
            found += 1
            print(f'Found: {tname} at line {i}')

print(f'Total found: {found}')
