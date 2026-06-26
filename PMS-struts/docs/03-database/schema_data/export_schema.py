import os
import json
import pymysql

DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '!Q@W3e4r',
    'charset': 'utf8mb4',
    'database': 'dppms_d365',
}

OUTPUT_DIR = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\schema_data'

QUERIES = {
    'objects.json': """
        SELECT TABLE_NAME, TABLE_TYPE, TABLE_COMMENT, ENGINE, TABLE_ROWS, DATA_LENGTH, INDEX_LENGTH
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA='dppms_d365'
        ORDER BY TABLE_TYPE, TABLE_NAME
    """,
    'columns.json': """
        SELECT TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, COLUMN_DEFAULT, IS_NULLABLE,
               COLUMN_TYPE, COLUMN_KEY, EXTRA, COLUMN_COMMENT
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA='dppms_d365'
        ORDER BY TABLE_NAME, ORDINAL_POSITION
    """,
    'constraints.json': """
        SELECT kcu.TABLE_NAME, kcu.COLUMN_NAME, tc.CONSTRAINT_NAME, tc.CONSTRAINT_TYPE,
               kcu.REFERENCED_TABLE_NAME, kcu.REFERENCED_COLUMN_NAME
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
        JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
          ON kcu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
          AND kcu.TABLE_SCHEMA = tc.TABLE_SCHEMA
          AND kcu.TABLE_NAME = tc.TABLE_NAME
        WHERE kcu.TABLE_SCHEMA='dppms_d365'
        ORDER BY tc.CONSTRAINT_TYPE, kcu.TABLE_NAME, kcu.CONSTRAINT_NAME, kcu.ORDINAL_POSITION
    """,
    'indexes.json': """
        SELECT TABLE_NAME, INDEX_NAME, COLUMN_NAME, SEQ_IN_INDEX, NON_UNIQUE, INDEX_TYPE
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA='dppms_d365'
        ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX
    """,
    'foreign_keys.json': """
        SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA='dppms_d365' AND REFERENCED_TABLE_NAME IS NOT NULL
        ORDER BY TABLE_NAME, CONSTRAINT_NAME
    """,
}


def query_to_dicts(cursor, sql):
    cursor.execute(sql)
    columns = [desc[0] for desc in cursor.description]
    rows = cursor.fetchall()
    return [dict(zip(columns, row)) for row in rows]


def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    conn = pymysql.connect(**DB_CONFIG)
    try:
        cursor = conn.cursor()
        for filename, sql in QUERIES.items():
            data = query_to_dicts(cursor, sql)
            filepath = os.path.join(OUTPUT_DIR, filename)
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            print(f"{filename}: {len(data)} rows -> {filepath}")
        cursor.close()
    finally:
        conn.close()

    print("Done.")


if __name__ == '__main__':
    main()
