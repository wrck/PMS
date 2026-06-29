#!/usr/bin/env python
# -*- coding: utf-8 -*-
import json, os, re
SCHEMA_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'schema_data')
def load_json(f):
    with open(os.path.join(SCHEMA_DIR, f), 'r', encoding='utf-8') as fh:
        return json.load(fh)

objects = load_json('objects.json')

DOMAIN_RULES = [
    ("项目管理", [
        r'^pm_project_(header|contract|group|member|related|milestone|deliverable|acceptance|closure|change|property|real_product|product|soleagent|market|incident|maintenance|license_info|log|notification|state|supervision|task|warranty|weekly|spot_check|soft_|shipment|instruction)$',
        r'^pm_project$', r'^pm_basic', r'^pm_column', r'^pm_common', r'^pm_daily', r'^pm_dispatch',
        r'^pm_notification', r'^pm_product_info', r'^pm_facilitator', r'^pm_cl_', r'^pm_presales',
        r'^pm_subcontract', r'^pm_sub_', r'^prob_', r'^fnd_',
        r'^(addressee_info|agent_info|back_type|serve_type|tain_type)$',
    ]),
    ("系统支撑", [
        r'^ehr_', r'^t_', r'^dp_',
        r'^(role|user|user_info|user_modules|user_permissions|user_team|tb_sys_log)$',
        r'^pm_order_data', r'^pm_order_line', r'^pm_pb_plan', r'^pm_person_from',
        r'^pm_presales_lend', r'^pm_project_property_from', r'^pm_project_property_af',
        r'^pm_project_real_product', r'^pm_project_product_af', r'^pm_project_soleagent_lend',
        r'^pm_project_market_relations', r'^project_info_from', r'^pm_project_product_config',
        r'^pm_project_product_lease', r'^pm_project_incident_table', r'^sms_ofst_contract',
        r'^pm_report', r'^pm_workflow', r'^pm_data_refresh',
        r'^(data_field_relation|hexiao|transnum|sys_state_or_type|firebird_operation_log)$',
    ]),
    ("历史迁移与引擎", [
        r'^act_', r'^fnd_act_', r'^fb_', r'^rma_', r'^warehouse$', r'^spare_', r'^department$',
        r'^af_industry_', r'^brw_', r'^app_', r'^mes_', r'^warranty_', r'^(tx_info|bar|workflow_info)$',
        r'^shipment_barcode', r'^dptech_v_',
    ]),
]

def classify(tname):
    for domain, patterns in DOMAIN_RULES:
        for p in patterns:
            if re.match(p, tname):
                return domain
    return "其他"

others = []
for obj in objects:
    tname = obj['TABLE_NAME']
    if tname.startswith(('temp_', 'tmp_')):
        continue
    if obj['TABLE_TYPE'] == 'VIEW':
        continue
    if classify(tname) == "其他":
        others.append(tname)

print(f"其他域: {len(others)} 张表")
for t in sorted(others):
    print(f"  {t}")
