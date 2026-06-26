import os

f = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\db_dictionary_dppms_d365.md'
with open(f, 'rb') as fh:
    data = fh.read()
text = data.decode('utf-8', errors='replace')
text = text.replace('\r\n', '\n').replace('\r', '\n')
lines = text.split('\n')
print(f'Lines: {len(lines)}')
print(f'Line 0: [{lines[0][:80]}]')
print(f'Line 1: [{lines[1][:80]}]')
print(f'Line 6: [{lines[6][:80]}]')

# Test regex
import re
count = 0
for line in lines:
    m = re.match(r'^###\s+\d+\s+(\S+)\s*(?:--\s*(.*))?$', line.strip())
    if m:
        count += 1
        if count <= 3:
            print(f'Found table: {m.group(1)}')
print(f'Total tables found: {count}')
