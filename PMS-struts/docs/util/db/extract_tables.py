import json

def load_json(path):
    for enc in ['utf-8', 'utf-8-sig', 'utf-16', 'gbk']:
        try:
            with open(path, 'r', encoding=enc) as f:
                return json.load(f)
        except (UnicodeDecodeError, json.JSONDecodeError):
            continue
    raise Exception(f"Cannot load {path}")

data = load_json(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\util\db\step1_columns.json')
idx_data = load_json(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\util\db\step2_indexes.json')

tables = ['pm_cl_callback','pm_cl_evaluation_header','pm_presales_project_header',
          'prob_main','prob_restore',
          'pm_subcontract_project_header','pm_subcontract_project_line',
          'pm_subcontract_project_payment','pm_facilitator']

for t in tables:
    cols = [c for c in data if c['TABLE_NAME'] == t]
    print(f'\n=== {t} ({len(cols)} columns) ===')
    for c in cols:
        nullable = 'YES' if c['IS_NULLABLE'] == 'YES' else 'NO'
        default = c['COLUMN_DEFAULT']
        key = c['COLUMN_KEY']
        extra = c['EXTRA']
        name = c['COLUMN_NAME']
        ctype = c['COLUMN_TYPE']
        comment = c['COLUMN_COMMENT']
        print(f'  {name} | {ctype} | nullable={nullable} | default={default} | key={key} | extra={extra} | {comment}')

    idxs = [i for i in idx_data if i['TABLE_NAME'] == t]
    if idxs:
        print(f'  --- INDEXES ---')
        for i in idxs:
            uniq = 'UNIQUE' if i['NON_UNIQUE'] == 0 else 'NON-UNIQUE'
            print(f'  {i["INDEX_NAME"]} | {i["COLUMN_NAME"]} | {uniq}')
