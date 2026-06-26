#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""第三轮补充填充"""
import os

MD_PATH = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md'

# 额外规则
EXTRA = {
    'xorder': '排序', 'packdate': '包装日期', 'item2': '项目2',
    'barcode2': '条码2', 'uuid': 'UUID标识', 'lasted': '持续时间',
    'sn1': '序列号1', 'item1': '项目1', 'sn2': '序列号2',
    'createtime': '创建时间', 'updatetime': '更新时间',
    'conp': 'CONP版本', 'cpld': 'CPLD版本', 'boot': 'BOOT版本', 'pcb': 'PCB版本',
    'gradecode': '等级编码', 'gradename': '等级名称', 'gradestatus': '等级状态',
    'enable': '是否启用', 'back': '返回标识',
    'var': '变量值', 'pwdoverdue': '密码过期时间',
    'areapower': '区域权限', 'field1': '扩展字段1', 'field2': '扩展字段2',
    'field3': '扩展字段3', 'field4': '扩展字段4', 'field5': '扩展字段5',
    'field6': '扩展字段6', 'field7': '扩展字段7', 'field8': '扩展字段8',
    'field9': '扩展字段9', 'field10': '扩展字段10',
    'businessunit': '业务单元', 'office': '办事处', 'dutyperson': '责任人',
    'canceled': '是否取消', 'memo': '备注', 'pspm': 'PSPM编号',
    'BU': '业务单元', 'year': '年份', 'quarter': '季度', 'month': '月份',
    'deliveried': '是否已交付', 'feedbacker': '反馈人',
    'totaljine': '总金额', 'opinion': '意见', 'serve': '服务标识',
    'usernamec2': '用户名2', 'isSure': '是否确认',
    'tain': '培训标识', 'ACTION': '操作', 'RESULT': '结果', 'INFO': '信息',
    'turnovertimes': '周转次数', 'allottimes': '分配次数',
    '单据编号': '单据编号', '过帐日期': '过帐日期', '物料代码': '物料代码',
    '物料/服务描述': '物料/服务描述', '未核销数量': '未核销数量',
    '设备序列号': '设备序列号', '注释': '注释', '合同号': '合同号',
    '责任部门': '责任部门', '工程师技术能力': '工程师技术能力',
    '服务及时性': '服务及时性', '服务水平及规范性': '服务水平及规范性',
}

with open(MD_PATH, 'r', encoding='utf-8') as f:
    content = f.read()

lines = content.split('\n')
new_lines = []
filled = 0

for line in lines:
    stripped = line.strip()
    if not stripped.startswith('|'):
        new_lines.append(line)
        continue

    parts = stripped.split('|')
    if len(parts) < 8:
        new_lines.append(line)
        continue

    col_name = parts[1].strip()
    meaning = parts[6].strip()

    if meaning == '待确认' and col_name in EXTRA:
        parts[6] = f' {EXTRA[col_name]} '
        new_lines.append('|'.join(parts))
        filled += 1
    else:
        new_lines.append(line)

print(f"第三轮补充填充: {filled}")

with open(MD_PATH, 'w', encoding='utf-8') as f:
    f.write('\n'.join(new_lines))

# 统计剩余待确认
remaining = content.count('待确认')  # 旧内容
new_content = '\n'.join(new_lines)
new_remaining = sum(1 for l in new_lines if '| 待确认 |' in l or '|待确认|' in l)
print(f"剩余待确认字段数: {new_remaining}")
