f = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\database_dict_part3.md'
text = open(f, 'r', encoding='utf-8').read()
count = text.count('业务含义待确认')
print(f'业务含义待确认: {count}')

# Count tables
import re
tables = re.findall(r'^### \d+ \S+', text, re.MULTILINE)
print(f'Total tables/views: {len(tables)}')

# Count by section
act_count = sum(1 for t in tables if 'act_' in t)
fb_count = sum(1 for t in tables if 'fb_' in t)
temp_count = sum(1 for t in tables if t.strip().startswith('###') and ('temp_' in t or 'tmp_' in t))
view_count = sum(1 for t in tables if 'view_' in t or 'dp_v_' in t or 'pm_order_data_from_sap' in t or 'pm_project_header' in t)
print(f'act_*: {act_count}, fb_*: {fb_count}, temp_*: {temp_count}, views: {view_count}, others: {len(tables)-act_count-fb_count-temp_count-view_count}')
