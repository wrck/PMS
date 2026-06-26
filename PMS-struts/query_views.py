import pymysql
import json

# Database connection config for dppms_d365
conn = pymysql.connect(
    host='localhost',
    user='root',
    password='!Q@W3e4r',
    database='dppms_d365',
    charset='utf8mb4',
    cursorclass=pymysql.cursors.DictCursor
)

views = [
    "view_contract_collection_plan_4_crm",
    "view_current_task",
    "view_distinct_contract",
    "view_ehr_department",
    "view_ehr_department_struct",
    "view_ehr_employee",
    "view_ems_info_4_pm",
    "view_pm_deliverable_4_sms",
    "view_presales_project_duration",
    "view_presales_project_duration_temp",
    "view_prj_is_has_plan",
    "view_project_created_list",
    "view_project_info_4_ts",
    "view_project_info_list",
    "view_project_maintenance_4_ts",
    "view_project_shipment_4_license",
    "view_project_task_4_oss",
    "view_project_task_default_4_oss",
    "view_project_waiting_list",
    "view_relation4contractno_marketcode",
    "view_rma_txinfo",
    "view_service",
    "view_service_max",
    "view_shipment_4_sms",
    "view_shipment_ems_4_pm",
    "view_shipment_info_4_pm",
    "view_soft_version",
    "view_subcontract_project_4_sse",
    "view_txinfo",
    "view_warranty_info_4_ts",
    "view_warranty_source",
]

# Sort views alphabetically
views.sort()

results = {}

try:
    with conn:
        with conn.cursor() as cursor:
            for view_name in views:
                try:
                    cursor.execute(f"DESCRIBE `{view_name}`")
                    columns = cursor.fetchall()
                    results[view_name] = columns
                    print(f"\n### {view_name}")
                    print("VIEW字段列表：")
                    print("| Field | Type | Null | Key | Default | Extra |")
                    print("|-------|------|------|-----|---------|-------|")
                    for col in columns:
                        field = col.get('Field', '')
                        type_ = col.get('Type', '')
                        null = col.get('Null', '')
                        key = col.get('Key', '')
                        default = col.get('Default', '')
                        if default is None:
                            default = 'NULL'
                        extra = col.get('Extra', '')
                        print(f"| {field} | {type_} | {null} | {key} | {default} | {extra} |")
                except Exception as e:
                    print(f"\n### {view_name}")
                    print(f"ERROR: {str(e)}")
                    results[view_name] = None
finally:
    conn.close()

print("\n\n=== QUERY COMPLETE ===")
print(f"Total views queried: {len(views)}")
print(f"Successful: {sum(1 for v in results.values() if v is not None)}")
print(f"Failed: {sum(1 for v in results.values() if v is None)}")
