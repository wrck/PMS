import os
PROJECT_DIR = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts'
NEW_DICT_FILE = os.path.join(PROJECT_DIR, 'db_dictionary_dppms_d365.md')
print(f'Path: {NEW_DICT_FILE}')
print(f'Exists: {os.path.exists(NEW_DICT_FILE)}')
if os.path.exists(NEW_DICT_FILE):
    with open(NEW_DICT_FILE, 'rb') as f:
        data = f.read()
    print(f'Size: {len(data)} bytes')
    text = data.decode('utf-8', errors='replace')
    text = text.replace('\r\n', '\n').replace('\r', '\n')
    lines = text.split('\n')
    print(f'Lines: {len(lines)}')
    print(f'First line: [{lines[0][:60]}]')
