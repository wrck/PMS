import re

f = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\database_dict.md'
with open(f, 'rb') as fh:
    data = fh.read()
text = data.decode('utf-8', errors='replace').replace('\r\n', '\n').replace('\r', '\n')

# Find fb_contract section
lines = text.split('\n')
in_fb_contract = False
for i, line in enumerate(lines):
    if '### fb_contract' in line:
        in_fb_contract = True
    if in_fb_contract:
        print(f'{i}: {line}')
        if line.startswith('### ') and 'fb_contract' not in line:
            break
    if in_fb_contract and i > 6380:
        break
