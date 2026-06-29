import json
f = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\temp\views_describe.json'
with open(f, 'r', encoding='utf-8') as fh:
    data = json.load(fh)
print(f'Type: {type(data)}')
if isinstance(data, dict):
    keys = list(data.keys())[:3]
    for k in keys:
        v = data[k]
        print(f'  Key: {k}, Type: {type(v)}, Preview: {str(v)[:100]}')
elif isinstance(data, list):
    print(f'  Length: {len(data)}')
    if data:
        print(f'  First item type: {type(data[0])}')
        print(f'  First item: {str(data[0])[:200]}')
