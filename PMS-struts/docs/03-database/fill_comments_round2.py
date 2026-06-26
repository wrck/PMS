#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""第二轮填充：对剩余空字段描述进行更深入的推断"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

# 更完整的字段名映射
FIELD_MAP = {
    # 通用
    'id': '主键ID', 'weeklyId': '周报ID', 'feedbacker': '反馈人', 'feedbackTime': '反馈时间',
    'maintenanceId': '维保ID', 'projectType': '项目类型', 'serviceType': '服务类型',
    'processTime': '处理时间', 'year': '年度', 'quarter': '季度',
    'username': '用户名', 'updateType': '更新类型',
    'warrantyStartTime': '维保开始时间', 'warrantyEndTime': '维保结束时间',
    'warrantyTimes': '维保次数', 'newId': '新ID', 'comBarCode': '公司条码',
    'old_warrantyEndTime': '原维保结束时间', 'diff': '差值',
    'old_diff': '原差值', 'warrantyStatusName': '维保状态名称',
    'warrantyGrade': '维保等级', 'warrantyGradeStartTime': '维保等级开始时间',
    'warrantyGradeEndTime': '维保等级结束时间',
    # 项目管理
    'projectId': '项目ID', 'projectNo': '项目编号', 'projectName': '项目名称',
    'contractId': '合同ID', 'contractNo': '合同编号', 'contractName': '合同名称',
    'customerId': '客户ID', 'customerName': '客户名称',
    'productLine': '产品线', 'productLineName': '产品线名称',
    'productId': '产品ID', 'productName': '产品名称', 'productCode': '产品编码',
    'barcode': '条码', 'itemCode': '物料编码', 'itemName': '物料名称',
    'shipmentState': '发货状态', 'shipmentTime': '发货时间',
    'deliverQuantity': '发货数量', 'openQuantity': '未发货数量',
    'supervisionState': '督导状态', 'supervisionTime': '督导时间',
    'implementState': '实施状态', 'implementTime': '实施时间',
    'closeState': '闭环状态', 'closeTime': '闭环时间',
    'planState': '计划状态', 'planTime': '计划时间',
    'acceptanceState': '验收状态', 'acceptanceTime': '验收时间',
    'callbackState': '回访状态', 'callbackTime': '回访时间',
    'maintenanceState': '维保状态', 'maintenanceTime': '维保时间',
    'presalesId': '售前ID', 'presalesNo': '售前编号',
    'lendTime': '借出时间', 'returnTime': '归还时间',
    'lendState': '借出状态', 'returnState': '归还状态',
    'applicant': '申请人', 'applicantId': '申请人ID',
    'approver': '审批人', 'approverId': '审批人ID',
    'assignee': '处理人', 'assigneeId': '处理人ID',
    'assigneeName': '处理人名称',
    # 通用后缀
    'State': '状态', 'Time': '时间', 'Date': '日期', 'Name': '名称',
    'Code': '编码', 'No': '编号', 'Id': 'ID', 'Type': '类型',
    'Num': '数量', 'Count': '计数', 'Qty': '数量', 'Amount': '金额',
    'Desc': '描述', 'Flag': '标志', 'Rate': '比率',
}

def infer_comment(fname):
    """从字段名推断描述"""
    # 精确匹配
    if fname in FIELD_MAP:
        return FIELD_MAP[fname]
    lower = fname.lower()
    if lower in FIELD_MAP:
        return FIELD_MAP[lower]

    # 驼峰拆分
    parts = re.findall(r'[A-Z]?[a-z]+|[A-Z]+(?=[A-Z]|$)', fname)
    if not parts:
        parts = fname.split('_')

    # 后缀推断
    if fname.endswith('Id'):
        base = fname[:-2]
        return f"{base}ID"
    if fname.endswith('Name'):
        base = fname[:-4]
        return f"{base}名称"
    if fname.endswith('Code'):
        base = fname[:-4]
        return f"{base}编码"
    if fname.endswith('No'):
        base = fname[:-2]
        return f"{base}编号"
    if fname.endswith('Time'):
        base = fname[:-4]
        return f"{base}时间"
    if fname.endswith('Date'):
        base = fname[:-4]
        return f"{base}日期"
    if fname.endswith('State'):
        base = fname[:-5]
        return f"{base}状态"
    if fname.endswith('Type'):
        base = fname[:-4]
        return f"{base}类型"
    if fname.endswith('Num') or fname.endswith('Count'):
        base = fname[:-3] if fname.endswith('Num') else fname[:-5]
        return f"{base}数量"
    if fname.endswith('Qty'):
        base = fname[:-3]
        return f"{base}数量"
    if fname.endswith('Amount'):
        base = fname[:-6]
        return f"{base}金额"
    if fname.endswith('Desc'):
        base = fname[:-4]
        return f"{base}描述"
    if fname.endswith('Flag'):
        base = fname[:-4]
        return f"{base}标志"
    if fname.endswith('Rate'):
        base = fname[:-4]
        return f"{base}比率"

    # 下划线后缀
    if '_id' in lower:
        idx = lower.rfind('_id')
        base = fname[:idx]
        return f"{base}ID"
    if '_name' in lower:
        idx = lower.rfind('_name')
        base = fname[:idx]
        return f"{base}名称"
    if '_code' in lower:
        idx = lower.rfind('_code')
        base = fname[:idx]
        return f"{base}编码"
    if '_time' in lower:
        idx = lower.rfind('_time')
        base = fname[:idx]
        return f"{base}时间"
    if '_date' in lower:
        idx = lower.rfind('_date')
        base = fname[:idx]
        return f"{base}日期"
    if '_state' in lower:
        idx = lower.rfind('_state')
        base = fname[:idx]
        return f"{base}状态"
    if '_type' in lower:
        idx = lower.rfind('_type')
        base = fname[:idx]
        return f"{base}类型"
    if '_no' in lower:
        idx = lower.rfind('_no')
        base = fname[:idx]
        return f"{base}编号"
    if '_num' in lower:
        idx = lower.rfind('_num')
        base = fname[:idx]
        return f"{base}数量"

    # 前缀
    if lower.startswith('is_'):
        return fname[3:] + "标识"
    if lower.startswith('has_'):
        return "是否有" + fname[4:]
    if lower.startswith('old_'):
        return "原" + fname[4:]

    # 用驼峰拆分拼接
    if parts:
        # 简单翻译常见词
        word_map = {
            'project': '项目', 'contract': '合同', 'customer': '客户',
            'product': '产品', 'order': '订单', 'line': '行',
            'item': '物料', 'material': '物料', 'shipment': '发货',
            'delivery': '交付', 'maintenance': '维保', 'warranty': '维保',
            'presales': '售前', 'lends': '借出', 'lend': '借出',
            'return': '归还', 'supervision': '督导', 'implement': '实施',
            'close': '闭环', 'plan': '计划', 'acceptance': '验收',
            'callback': '回访', 'state': '状态', 'time': '时间',
            'date': '日期', 'name': '名称', 'code': '编码',
            'no': '编号', 'type': '类型', 'id': 'ID',
            'num': '数量', 'count': '计数', 'qty': '数量',
            'amount': '金额', 'desc': '描述', 'flag': '标志',
            'rate': '比率', 'start': '开始', 'end': '结束',
            'begin': '开始', 'finish': '完成', 'create': '创建',
            'update': '更新', 'delete': '删除', 'modify': '修改',
            'user': '用户', 'dept': '部门', 'org': '组织',
            'company': '公司', 'role': '角色', 'menu': '菜单',
            'permission': '权限', 'module': '模块', 'action': '操作',
            'method': '方法', 'status': '状态', 'remark': '备注',
            'description': '描述', 'version': '版本', 'sort': '排序',
            'order': '排序', 'enable': '启用', 'disable': '禁用',
            'visible': '可见', 'deleted': '删除', 'default': '默认',
            'primary': '主键', 'unique': '唯一', 'index': '索引',
            'key': '键', 'value': '值', 'data': '数据',
            'info': '信息', 'detail': '明细', 'log': '日志',
            'history': '历史', 'record': '记录', 'config': '配置',
            'setting': '设置', 'param': '参数', 'template': '模板',
            'category': '分类', 'level': '级别', 'grade': '等级',
            'priority': '优先级', 'source': '来源', 'target': '目标',
            'from': '来源', 'to': '目标', 'old': '原', 'new': '新',
            'diff': '差值', 'change': '变更', 'update': '更新',
            'grade': '等级', 'quarter': '季度', 'year': '年度',
            'month': '月份', 'week': '周', 'day': '天',
            'hour': '小时', 'minute': '分钟', 'second': '秒',
            'total': '合计', 'sum': '合计', 'avg': '平均',
            'max': '最大', 'min': '最小', 'count': '计数',
            'serial': '序列', 'sequence': '序列', 'batch': '批次',
            'group': '分组', 'team': '团队', 'member': '成员',
            'leader': '负责人', 'manager': '经理', 'owner': '负责人',
            'creator': '创建人', 'modifier': '修改人', 'operator': '操作人',
            'assignee': '处理人', 'applicant': '申请人', 'approver': '审批人',
            'feedback': '反馈', 'process': '流程', 'task': '任务',
            'activity': '活动', 'execution': '执行', 'instance': '实例',
            'variable': '变量', 'identity': '身份', 'link': '关联',
            'job': '作业', 'timer': '定时器', 'message': '消息',
            'signal': '信号', 'event': '事件', 'boundary': '边界',
            'sub': '子', 'parent': '父', 'child': '子',
            'suspended': '挂起', 'active': '激活', 'completed': '完成',
            'revision': '版本号', 'seq': '序列', 'counter': '计数器',
            'proc': '流程', 'inst': '实例', 'def': '定义',
            'deploy': '部署', 'resource': '资源', 'byte': '字节',
            'digest': '摘要', 'generated': '生成', 'lock': '锁',
            'lockowner': '锁拥有者', 'locktime': '锁时间',
            'isactive': '是否激活', 'issuspended': '是否挂起',
            'concurrency': '并发', 'tenant': '租户',
            'callback': '回调', 'claim': '认领', 'delegation': '委派',
            'follow': '跟进', 'followup': '跟进',
            'last': '最后', 'lastupdated': '最后更新',
            'due': '到期', 'duedate': '到期日期',
            'category': '分类', 'suspension': '挂起',
            'camunda': 'Camunda', 'failed': '失败', 'retries': '重试',
            'error': '错误', 'exception': '异常', 'stack': '堆栈',
            'full': '完整', 'act': '活动', 'ru': '运行时',
            'hi': '历史', 're': '仓库', 'ge': '通用',
            'fb': 'Firebird', 'rma': 'RMA退货',
            'sms': 'SMS系统', 'erp': 'ERP系统', 'sap': 'SAP系统',
            'crm': 'CRM系统', 'spms': 'SPMS系统', 'd365': 'D365系统',
            'ofst': '偏移', 'dispatch': '派工', 'settlement': '结算',
            'presales': '售前', 'market': '市场', 'relation': '关系',
            'property': '属性', 'config': '配置', 'level': '级别',
            'info': '信息', 'industry': '行业', 'asset': '资产',
            'leak': '漏洞', 'warning': '预警', 'af': '安全',
            'hexiao': '核销', 'transnum': '流水号',
            'warehouse': '仓库', 'spare': '备件', 'parts': '部件',
            'oqc': '出货检验', 'mes': '制造执行', 'seal': '封条',
            'warranty': '维保', 'grade': '等级',
            'barcode': '条码', 'relation': '关系',
            'change': '变更', 'log': '日志',
            'com': '公司', 'bar': '条码',
        }
        translated = []
        for part in parts:
            plower = part.lower()
            if plower in word_map:
                translated.append(word_map[plower])
            elif part in word_map:
                translated.append(word_map[part])
            else:
                translated.append(part)
        result = ''.join(translated)
        if result and len(result) > 1:
            return result

    return ''

def main():
    text = read_md(MD_FILE)
    lines = text.split('\n')

    filled = 0
    still_empty = 0
    current_table = ''

    i = 0
    while i < len(lines):
        line = lines[i].strip()
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) and len(m.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m.group(1)) and not m.group(1).startswith('**'):
            current_table = m.group(1)

        if line.startswith('|') and '---' not in line and not line.startswith('| 字段名') and not line.startswith('| 属性') and not line.startswith('| 索引') and not line.startswith('| 外键'):
            in_field_table = False
            for j in range(i-1, max(i-10, 0), -1):
                if lines[j].strip().startswith('| 字段名'):
                    in_field_table = True
                    break
                if lines[j].strip().startswith('| 索引') or lines[j].strip().startswith('| 外键') or lines[j].strip().startswith('| 属性'):
                    break

            if in_field_table:
                all_parts = line.split('|')
                if all_parts and all_parts[0].strip() == '':
                    all_parts = all_parts[1:]
                if all_parts and all_parts[-1].strip() == '':
                    all_parts = all_parts[:-1]
                stripped = [p.strip() for p in all_parts]
                col_count = len(stripped)

                if col_count >= 7:
                    fname = stripped[0]
                    comment = ' | '.join(stripped[5:-1])
                    biz = stripped[-1]
                elif col_count >= 5:
                    fname = stripped[0]
                    comment = stripped[-2] if col_count >= 6 else ''
                    biz = stripped[-1]
                else:
                    i += 1
                    continue

                if not comment or comment in ('-', 'None', ''):
                    new_comment = infer_comment(fname)
                    if new_comment:
                        # 替换
                        raw = lines[i]
                        raw_parts = raw.split('|')
                        if raw_parts[0].strip() == '':
                            raw_parts = raw_parts[1:]
                        if raw_parts[-1].strip() == '':
                            trailing = [raw_parts.pop()]
                        else:
                            trailing = []

                        if col_count >= 7:
                            raw_parts[5] = f' {new_comment} '
                            if len(raw_parts) > 7:
                                del raw_parts[6:-1]
                        elif col_count == 6:
                            raw_parts[5] = f' {new_comment} '
                        elif col_count == 5:
                            raw_parts[4] = f' {new_comment} '

                        lines[i] = '|' + '|'.join(raw_parts) + ('|'.join(trailing) if trailing else '|')
                        filled += 1
                    else:
                        still_empty += 1
                        if still_empty <= 20:
                            print(f"  仍为空: {current_table}.{fname}")
        i += 1

    with open(MD_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n第二轮填充完成:")
    print(f"  填充: {filled}")
    print(f"  仍为空: {still_empty}")

if __name__ == '__main__':
    main()
