import sys
sys.stdout = open('d:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\db_stdout.txt', 'w', encoding='utf-8')
sys.stderr = open('d:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\db_stderr.txt', 'w', encoding='utf-8')

try:
    import pymysql
    print("pymysql imported OK")
    conn = pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', database='dppms_d365', charset='utf8mb4')
    print("DB connected OK")
    cur = conn.cursor()
    cur.execute("SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_TYPE='BASE TABLE'")
    count = cur.fetchone()[0]
    print(f"Table count: {count}")
    cur.close()
    conn.close()
    print("Done!")
except Exception as e:
    import traceback
    print(f"ERROR: {e}")
    traceback.print_exc()

sys.stdout.close()
sys.stderr.close()
