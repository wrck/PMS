#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""检查分类到'其他'的表名，优化分类规则"""
import json
import os
import re

SCHEMA_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'schema_data')

def load_json(filename):
    with open(os.path.join(SCHEMA_DIR, filename), 'r', encoding='utf-8') as f:
        return json.load(f)

objects = load_json('objects.json')

# 当前分类规则
DOMAIN_RULES = {
    "项目管理": [
        r'^pm_project_header$', r'^pm_project_contract$', r'^pm_project_group',
        r'^pm_project_member$', r'^pm_project_related_party$', r'^pm_project_milestone',
        r'^pm_project_deliverable$', r'^pm_project_acceptance$', r'^pm_project_closure',
        r'^pm_project_change$', r'^pm_basic', r'^pm_column', r'^pm_common',
        r'^pm_daily', r'^pm_dispatch', r'^pm_notification', r'^pm_product_info',
        r'^pm_cl_', r'^pm_presales_project', r'^pm_presales_lend',
        r'^pm_subcontract', r'^pm_sub_', r'^prob_', r'^fnd_',
    ],
    "系统支撑": [
        r'^ehr_', r'^t_', r'^dp_',
        r'^pm_order_data', r'^pm_order_line', r'^pm_pb_plan', r'^pm_person_from',
        r'^pm_project_property_from', r'^pm_project_property_af',
        r'^pm_project_real_product', r'^pm_project_product_af',
        r'^pm_project_soleagent_lend', r'^pm_project_market_relations',
        r'^project_info_from', r'^pm_project_product_config', r'^pm_project_product_lease',
        r'^pm_project_incident_table', r'^pm_report', r'^pm_workflow', r'^pm_data_refresh',
    ],
    "历史迁移与引擎": [
        r'^act_', r'^fnd_act_', r'^fb_', r'^rma_', r'^warehouse_', r'^spare_', r'^department$',
    ],
}

def classify(tname):
    for domain, patterns in DOMAIN_RULES.items():
        for p in patterns:
            if re.match(p, tname):
                return domain
    return "其他"

others = []
for obj in objects:
    tname = obj['TABLE_NAME']
    ttype = obj['TABLE_TYPE']
    if tname.startswith(('temp_', 'tmp_')):
        continue
    domain = classify(tname)
    if domain == "其他":
        others.append(tname)

print(f"分类到'其他'的表: {len(others)}")
for t in sorted(others):
    print(f"  {t}")
