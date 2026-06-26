import pymysql

# Try test environment first - disable SSL
configs = [
    {'host': 'spmstest.dptech.com', 'port': 3306, 'user': 'spms', 'password': '!2y8qtpW4', 'database': 'dpspms'},
    {'host': 'spmstest.dptech.com', 'port': 3306, 'user': 'pms', 'password': 'LfWCFkeb1', 'database': 'dpspms'},
    {'host': '172.16.200.26', 'port': 3306, 'user': 'pms_readonly', 'password': 'pms_readonly_2024', 'database': 'pms'},
]

conn = None
for cfg in configs:
    try:
        print(f"Trying {cfg['user']}@{cfg['host']}:{cfg['port']}/{cfg['database']}...")
        conn = pymysql.connect(
            host=cfg['host'], port=cfg['port'], user=cfg['user'],
            password=cfg['password'], database=cfg['database'],
            charset='utf8mb4', connect_timeout=10,
            ssl={'ssl_disabled': True}
        )
        print(f"Connected successfully!")
        break
    except Exception as e:
        print(f"Failed: {e}")
        conn = None

if not conn:
    # Try with ssl=False explicitly
    for cfg in configs:
        try:
            print(f"Retrying {cfg['user']}@{cfg['host']}:{cfg['port']}/{cfg['database']} with ssl=False...")
            conn = pymysql.connect(
                host=cfg['host'], port=cfg['port'], user=cfg['user'],
                password=cfg['password'], database=cfg['database'],
                charset='utf8mb4', connect_timeout=10,
                ssl=False
            )
            print(f"Connected successfully!")
            break
        except Exception as e:
            print(f"Failed: {e}")
            conn = None

if not conn:
    print("All connections failed!")
    exit(1)

cursor = conn.cursor()
tables = ['pm_project_maintenance', 'pm_project_soft_version', 'sms_ofst_contract_head_sap', 'pm_dispatch_project_header', 'pm_subcontract_project_header']
for t in tables:
    print(f'\n===== {t} =====')
    try:
        cursor.execute(f'DESCRIBE {t}')
        for row in cursor.fetchall():
            print(f'{row[0]}|{row[1]}|{row[2]}|{row[3]}|{row[4]}|{row[5]}')
    except Exception as e:
        print(f'Error: {e}')

cursor.close()
conn.close()
