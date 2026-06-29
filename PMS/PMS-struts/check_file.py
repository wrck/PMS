import os
f = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\db_dictionary_dppms_d365.md'
data = open(f, 'rb').read()
print(f'File size: {len(data)} bytes')
print(f'Newline count: {data.count(b"\\n")}')
print(f'CRLF count: {data.count(b"\\r\\n")}')
# Try reading as utf-8
text = data.decode('utf-8', errors='replace')
lines = text.split('\n')
print(f'Lines after split: {len(lines)}')
print(f'First 3 lines:')
for l in lines[:3]:
    print(f'  [{l[:80]}]')
