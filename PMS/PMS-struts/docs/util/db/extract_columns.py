import json

with open(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\util\db\step1_columns.json', 'rb') as f:
    raw = f.read()
    # Remove BOM if present
    if raw[:2] == b'\xff\xfe':
        text = raw[2:].decode('utf-16-le')
    elif raw[:3] == b'\xef\xbb\xbf':
        text = raw[3:].decode('utf-8')
    else:
        text = raw.decode('utf-8', errors='replace')
    columns = json.loads(text)

target_tables = ['pm_project_maintenance', 'pm_project_soft_version', 'sms_ofst_contract_head_sap', 'pm_dispatch_project_header', 'pm_subcontract_project_header']

for t in target_tables:
    print(f'\n===== {t} =====')
    table_cols = [c for c in columns if c['TABLE_NAME'] == t]
    if not table_cols:
        print(f'NOT FOUND in JSON!')
        continue
    for c in table_cols:
        default = c.get('COLUMN_DEFAULT')
        if default is None:
            default = 'NULL'
        print(f"{c['COLUMN_NAME']}|{c['COLUMN_TYPE']}|{c['IS_NULLABLE']}|{default}|{c['COLUMN_KEY']}|{c.get('EXTRA','')}")
