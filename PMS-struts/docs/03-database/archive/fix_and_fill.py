#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
修复 database_dict final.md 的表格格式 + 重新填充空字段描述
1. 修复所有字段数据行为标准7列格式
2. 用业务含义/数据库COMMENT/字段名推断填充空字段描述
"""
import re, os, pymysql

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

# 字段名推断映射
FIELD_MAP = {
    'id': '主键ID', 'weeklyId': '周报ID', 'feedbacker': '反馈人', 'feedbackTime': '反馈时间',
    'feedback': '反馈内容', 'maintenanceId': '维保ID', 'projectType': '项目类型',
    'serviceType': '服务类型', 'processTime': '处理时间', 'year': '年度', 'quarter': '季度',
    'username': '用户名', 'updateType': '更新类型',
    'warrantyStartTime': '维保开始时间', 'warrantyEndTime': '维保结束时间',
    'warrantyTimes': '维保次数', 'newId': '新ID', 'comBarCode': '公司条码',
    'old_warrantyEndTime': '原维保结束时间', 'diff': '差值', 'old_diff': '原差值',
    'warrantyStatusName': '维保状态名称', 'warrantyGrade': '维保等级',
    'warrantyGradeStartTime': '维保等级开始时间', 'warrantyGradeEndTime': '维保等级结束时间',
    'create_time': '创建时间', 'createTime': '创建时间', 'update_time': '更新时间',
    'updateTime': '更新时间', 'create_by': '创建人', 'creator': '创建人',
    'update_by': '更新人', 'modifier': '修改人', 'del_flag': '删除标志',
    'is_deleted': '是否删除', 'is_valid': '是否有效', 'status': '状态', 'state': '状态',
    'remark': '备注', 'remarks': '备注', 'description': '描述', 'name': '名称',
    'code': '编码', 'type': '类型', 'sort': '排序', 'version': '版本',
    'enable': '是否启用', 'enabled': '是否启用', 'deleted': '是否删除',
    'tenant_id': '租户ID', 'org_id': '组织ID', 'dept_id': '部门ID',
    'user_id': '用户ID', 'company_id': '公司ID', 'project_id': '项目ID',
    'contract_no': '合同编号', 'contract_id': '合同ID',
    'customer_id': '客户ID', 'customer_name': '客户名称',
    'product_id': '产品ID', 'product_name': '产品名称', 'product_code': '产品编码',
    'barcode': '条码', 'bar_code': '条码', 'serial_no': '序列号',
    'amount': '金额', 'quantity': '数量', 'qty': '数量', 'price': '价格',
    'phone': '电话', 'email': '邮箱', 'address': '地址',
    'content': '内容', 'title': '标题', 'category': '分类',
    'level': '级别', 'priority': '优先级', 'start_time': '开始时间',
    'end_time': '结束时间', 'parent_id': '父级ID', 'pid': '父级ID',
    'source': '来源', 'action': '操作', 'method': '方法', 'module': '模块',
    'permission': '权限', 'role_id': '角色ID', 'menu_id': '菜单ID',
    'operator': '操作人', 'extra': '扩展信息', 'is_default': '是否默认',
    'is_active': '是否激活', 'order_id': '订单ID', 'line_no': '行号',
    'item_code': '物料编码', 'item_name': '物料名称',
    'warehouse_id': '仓库ID', 'location': '位置', 'region': '区域',
    'country': '国家', 'province': '省份', 'city': '城市',
    'contact': '联系人', 'contact_phone': '联系电话',
    'approval_status': '审批状态', 'approve_time': '审批时间',
    'assignee': '处理人', 'assignee_id': '处理人ID',
    'process_id': '流程ID', 'task_id': '任务ID', 'task_name': '任务名称',
    'execution_id': '执行ID', 'proc_inst_id': '流程实例ID',
    'business_key': '业务主键', 'suspension_state': '挂起状态',
    'revision': '版本号', 'is_suspended': '是否挂起',
    'sync_time': '同步时间', 'sync_status': '同步状态',
    'batch_no': '批次号', 'ref_id': '关联ID', 'ref_no': '关联编号',
    'old_value': '原值', 'new_value': '新值', 'change_type': '变更类型',
    'change_time': '变更时间', 'log_type': '日志类型',
    'error_msg': '错误信息', 'error_code': '错误码', 'retry_count': '重试次数',
}

WORD_MAP = {
    'project': '项目', 'contract': '合同', 'customer': '客户',
    'product': '产品', 'order': '订单', 'line': '行',
    'item': '物料', 'material': '物料', 'shipment': '发货',
    'delivery': '交付', 'maintenance': '维保', 'warranty': '维保',
    'presales': '售前', 'lend': '借出', 'return': '归还',
    'supervision': '督导', 'implement': '实施', 'close': '闭环',
    'plan': '计划', 'acceptance': '验收', 'callback': '回访',
    'state': '状态', 'time': '时间', 'date': '日期',
    'name': '名称', 'code': '编码', 'no': '编号', 'type': '类型',
    'id': 'ID', 'num': '数量', 'count': '计数', 'qty': '数量',
    'amount': '金额', 'desc': '描述', 'flag': '标志', 'rate': '比率',
    'start': '开始', 'end': '结束', 'begin': '开始', 'finish': '完成',
    'create': '创建', 'update': '更新', 'delete': '删除', 'modify': '修改',
    'user': '用户', 'dept': '部门', 'org': '组织', 'company': '公司',
    'role': '角色', 'menu': '菜单', 'permission': '权限',
    'status': '状态', 'remark': '备注', 'description': '描述',
    'version': '版本', 'sort': '排序', 'enable': '启用', 'disable': '禁用',
    'visible': '可见', 'deleted': '删除', 'default': '默认',
    'primary': '主键', 'unique': '唯一', 'index': '索引',
    'key': '键', 'value': '值', 'data': '数据', 'info': '信息',
    'detail': '明细', 'log': '日志', 'history': '历史', 'record': '记录',
    'config': '配置', 'setting': '设置', 'param': '参数',
    'category': '分类', 'level': '级别', 'grade': '等级',
    'priority': '优先级', 'source': '来源', 'target': '目标',
    'from': '来源', 'to': '目标', 'old': '原', 'new': '新',
    'diff': '差值', 'change': '变更', 'quarter': '季度', 'year': '年度',
    'month': '月份', 'week': '周', 'day': '天',
    'total': '合计', 'sum': '合计', 'serial': '序列', 'sequence': '序列',
    'batch': '批次', 'group': '分组', 'team': '团队', 'member': '成员',
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
    'counter': '计数器', 'proc': '流程', 'inst': '实例',
    'def': '定义', 'deploy': '部署', 'resource': '资源',
    'byte': '字节', 'digest': '摘要', 'generated': '生成',
    'lock': '锁', 'lockowner': '锁拥有者', 'locktime': '锁时间',
    'isactive': '是否激活', 'issuspended': '是否挂起',
    'concurrency': '并发', 'tenant': '租户', 'claim': '认领',
    'delegation': '委派', 'follow': '跟进', 'followup': '跟进',
    'last': '最后', 'lastupdated': '最后更新', 'due': '到期',
    'duedate': '到期日期', 'suspension': '挂起', 'failed': '失败',
    'retries': '重试', 'error': '错误', 'exception': '异常',
    'stack': '堆栈', 'full': '完整', 'act': '活动',
    'ru': '运行时', 'hi': '历史', 're': '仓库', 'ge': '通用',
    'fb': 'Firebird', 'rma': 'RMA退货', 'sms': 'SMS系统',
    'erp': 'ERP系统', 'sap': 'SAP系统', 'crm': 'CRM系统',
    'spms': 'SPMS系统', 'd365': 'D365系统', 'ofst': '偏移',
    'dispatch': '派工', 'settlement': '结算', 'market': '市场',
    'relation': '关系', 'property': '属性', 'config': '配置',
    'industry': '行业', 'asset': '资产', 'leak': '漏洞',
    'warning': '预警', 'af': '安全', 'hexiao': '核销',
    'transnum': '流水号', 'warehouse': '仓库', 'spare': '备件',
    'parts': '部件', 'oqc': '出货检验', 'mes': '制造执行',
    'seal': '封条', 'com': '公司', 'bar': '条码',
    'rev': '版本', 'bytes': '字节', 'procdef': '流程定义',
    'self': '自身', 'ref': '引用', 'super': '父级',
    'susp': '挂起', 'cam': 'Camunda',
}

def infer_comment(fname):
    """从字段名推断描述"""
    if fname in FIELD_MAP:
        return FIELD_MAP[fname]
    lower = fname.lower()
    if lower in FIELD_MAP:
        return FIELD_MAP[lower]

    # 后缀推断
    suffixes = [
        ('Id', 'ID'), ('Name', '名称'), ('Code', '编码'), ('No', '编号'),
        ('Time', '时间'), ('Date', '日期'), ('State', '状态'), ('Type', '类型'),
        ('Num', '数量'), ('Count', '计数'), ('Qty', '数量'), ('Amount', '金额'),
        ('Desc', '描述'), ('Flag', '标志'), ('Rate', '比率'), ('Grade', '等级'),
    ]
    for suffix, label in suffixes:
        if fname.endswith(suffix) and len(fname) > len(suffix):
            base = fname[:-len(suffix)]
            return f"{base}{label}"

    # 下划线后缀
    for suffix, label in [('_id', 'ID'), ('_name', '名称'), ('_code', '编码'),
                           ('_time', '时间'), ('_date', '日期'), ('_state', '状态'),
                           ('_type', '类型'), ('_no', '编号'), ('_num', '数量')]:
        if suffix in lower:
            idx = lower.rfind(suffix)
            base = fname[:idx]
            return f"{base}{label}"

    # 前缀
    if lower.startswith('is_'):
        return fname[3:] + "标识"
    if lower.startswith('has_'):
        return "是否有" + fname[4:]
    if lower.startswith('old_'):
        return "原" + fname[4:]

    # 驼峰拆分翻译
    parts = re.findall(r'[A-Z]?[a-z]+|[A-Z]+(?=[A-Z]|$)', fname)
    if not parts:
        parts = fname.split('_')
    translated = []
    for part in parts:
        plower = part.lower()
        if plower in WORD_MAP:
            translated.append(WORD_MAP[plower])
        elif part in WORD_MAP:
            translated.append(WORD_MAP[part])
        else:
            translated.append(part)
    result = ''.join(translated)
    return result if result and len(result) > 1 else ''


def biz_to_comment(biz):
    """将业务含义转为简洁字段描述"""
    if not biz or biz in ('-', '业务含义待确认', 'None'):
        return ''
    if '=' in biz:
        parts = biz.split('=')
        prefix = parts[0].strip()
        if prefix and not prefix.isdigit():
            return prefix
    return biz[:40] if len(biz) > 40 else biz


def main():
    print("=" * 60)
    print("修复 database_dict final.md 表格格式 + 填充空字段描述")
    print("=" * 60)

    text = read_md(MD_FILE)
    lines = text.split('\n')

    # 1. 从数据库获取 COMMENT
    print("\n1. 获取数据库COMMENT...")
    db_comments = {}
    try:
        conn = pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', database='dppms_d365', charset='utf8mb4')
        cursor = conn.cursor()
        cursor.execute(
            "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_COMMENT "
            "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='dppms_d365' AND COLUMN_COMMENT != ''"
        )
        for row in cursor.fetchall():
            key = f"{row[0]}.{row[1]}"
            db_comments[key] = row[2]
        cursor.close(); conn.close()
        print(f"   获取到 {len(db_comments)} 个字段COMMENT")
    except Exception as e:
        print(f"   数据库连接失败: {e}")

    # 2. 修复格式 + 填充
    print("\n2. 修复表格格式并填充空字段描述...")
    current_table = ''
    in_field_table = False
    fixed_format = 0
    filled_by_biz = 0
    filled_by_db = 0
    filled_by_infer = 0
    still_empty = 0

    i = 0
    while i < len(lines):
        line = lines[i].strip()

        # 检测表名
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) and len(m.group(1))>=2 and not re.search(r'[\u4e00-\u9fff]',m.group(1)) and not m.group(1).startswith('**'):
            current_table = m.group(1)

        # 检测字段表头
        if line.startswith('| 字段名'):
            in_field_table = True
            i += 1
            continue

        # 检测分隔行
        if line.startswith('|---') or line.startswith('| --------'):
            i += 1
            continue

        # 检测退出字段表
        if in_field_table and (not line.startswith('|') or line.startswith('| 索引') or line.startswith('| 外键') or line.startswith('| 属性') or line.startswith('| 约束')):
            in_field_table = False
            i += 1
            continue

        # 处理字段数据行
        if in_field_table and line.startswith('|'):
            # 解析原始行 - 按 | 分割保留空列
            raw_parts = line.split('|')
            # 去掉首尾空段
            if raw_parts and raw_parts[0].strip() == '':
                raw_parts = raw_parts[1:]
            if raw_parts and raw_parts[-1].strip() == '':
                raw_parts = raw_parts[:-1]

            stripped = [p.strip() for p in raw_parts]
            col_count = len(stripped)

            # 提取7列数据
            if col_count >= 7:
                fname = stripped[0]
                dtype = stripped[1]
                nullable = stripped[2]
                default_val = stripped[3]
                constraint = stripped[4]
                # 第6列到倒数第2列合并为字段描述，最后1列为业务含义
                comment = ' | '.join(stripped[5:-1])
                biz = stripped[-1]
            elif col_count == 6:
                fname = stripped[0]
                dtype = stripped[1]
                nullable = stripped[2]
                default_val = stripped[3]
                fifth = stripped[4]
                if fifth in ('PRI', 'UNI', 'MUL', 'AUTO_INCREMENT') or 'auto_increment' in fifth.lower() or fifth in ('-', ''):
                    constraint = fifth
                    comment = stripped[5]
                    biz = ''
                else:
                    constraint = ''
                    comment = fifth
                    biz = stripped[5]
            elif col_count == 5:
                fname = stripped[0]
                dtype = stripped[1]
                nullable = stripped[2]
                default_val = stripped[3]
                constraint = ''
                comment = stripped[4]
                biz = ''
            else:
                i += 1
                continue

            # 填充空字段描述
            if not comment or comment in ('-', 'None', ''):
                new_comment = ''

                # 策略1: 用业务含义
                if biz and biz not in ('-', '业务含义待确认', 'None', ''):
                    new_comment = biz_to_comment(biz)
                    if new_comment:
                        filled_by_biz += 1

                # 策略2: 用数据库COMMENT
                if not new_comment and current_table:
                    db_key = f"{current_table}.{fname}"
                    if db_key in db_comments:
                        new_comment = db_comments[db_key]
                        filled_by_db += 1

                # 策略3: 从字段名推断
                if not new_comment:
                    new_comment = infer_comment(fname)
                    if new_comment:
                        filled_by_infer += 1

                if new_comment:
                    comment = new_comment
                else:
                    still_empty += 1

            # 重建标准7列行
            new_line = f"| {fname} | {dtype} | {nullable} | {default_val} | {constraint} | {comment} | {biz} |"
            if new_line != lines[i].rstrip():
                fixed_format += 1
            lines[i] = new_line

        i += 1

    # 3. 写回文件
    with open(MD_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n修复完成:")
    print(f"  修复/重建行数: {fixed_format}")
    print(f"  用业务含义填充: {filled_by_biz}")
    print(f"  用数据库COMMENT填充: {filled_by_db}")
    print(f"  从字段名推断填充: {filled_by_infer}")
    print(f"  仍为空: {still_empty}")
    print(f"  总填充: {filled_by_biz + filled_by_db + filled_by_infer}")

if __name__ == '__main__':
    main()
