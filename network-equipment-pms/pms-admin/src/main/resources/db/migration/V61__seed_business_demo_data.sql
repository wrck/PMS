-- =============================================================
-- V61__seed_business_demo_data.sql
-- 为所有业务表补充演示数据（每表 ≥10 条）。
-- 依赖：V1~V26 已建表 + 索引 + 外键 + 乐观锁字段。
-- 外键约束（V25）要求按以下顺序插入：
--   pms_project → pms_asset(p.project_id) → pms_warranty(asset_id)
--   pms_project → pms_milestone / pms_impl_task / pms_punch_list /
--                 pms_change_request / pms_risk / pms_issue
-- create_by 统一使用 'admin'，时间使用 NOW() - INTERVAL N DAY 分散。
-- =============================================================

-- =============================================================
-- 1. pms_project（10 条）
-- =============================================================
INSERT INTO `pms_project`
    (`id`, `project_code`, `project_name`, `project_type`, `status`,
     `customer_name`, `customer_contact`, `customer_phone`, `contract_no`, `contract_amount`,
     `plan_start_date`, `plan_end_date`, `actual_start_date`, `actual_end_date`,
     `project_manager_id`, `project_manager_name`, `description`, `progress`, `priority`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1,  'PMS-2024-0001', '中国工商银行北京分行核心网络改造项目',     'NETWORK_DEVICE', 'COMPLETED',
     '中国工商银行北京分行', '王志强', '13901234567', 'ICBC-BJ-2024-001', 1280000.00,
     NOW() - INTERVAL 200 DAY, NOW() - INTERVAL 100 DAY, NOW() - INTERVAL 198 DAY, NOW() - INTERVAL 95 DAY,
     2, '张明', '工行北京分行核心数据中心网络整体升级，涉及路由器、交换机及防火墙替换', 100, 'HIGH',
     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 95 DAY, 0, 0),
(2,  'PMS-2024-0002', '中国建设银行上海数据中心网络升级项目',     'DATACENTER',     'IN_PROGRESS',
     '中国建设银行上海数据中心', '李建国', '13902345678', 'CCB-SH-2024-002', 2560000.00,
     NOW() - INTERVAL 150 DAY, NOW() + INTERVAL 30 DAY, NOW() - INTERVAL 145 DAY, NULL,
     3, '刘伟', '建行上海张江数据中心网络架构升级，含核心交换机替换与负载均衡部署', 65, 'HIGH',
     'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0, 0),
(3,  'PMS-2024-0003', '中国农业银行深圳分行网络安全加固项目',     'SECURITY',       'IN_PROGRESS',
     '中国农业银行深圳分行', '陈志远', '13903456789', 'ABC-SZ-2024-003', 880000.00,
     NOW() - INTERVAL 120 DAY, NOW() + INTERVAL 45 DAY, NOW() - INTERVAL 115 DAY, NULL,
     4, '赵琳', '农行深圳分行下一代防火墙部署与入侵检测系统建设', 55, 'HIGH',
     'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0, 0),
(4,  'PMS-2024-0004', '招商银行广州分行数据中心建设项目',         'DATACENTER',     'IN_PROGRESS',
     '招商银行广州分行', '黄晓东', '13904567890', 'CMB-GZ-2024-004', 3650000.00,
     NOW() - INTERVAL 90 DAY, NOW() + INTERVAL 90 DAY, NOW() - INTERVAL 85 DAY, NULL,
     5, '孙磊', '招行广州分行新建数据中心网络规划与设备部署', 40, 'HIGH',
     'admin', NOW() - INTERVAL 90 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0, 0),
(5,  'PMS-2024-0005', '中国银行杭州分行网络设备更新项目',         'NETWORK_DEVICE', 'FINAL_ACCEPTANCE',
     '中国银行杭州分行', '周文博', '13905678901', 'BOC-HZ-2024-005', 1580000.00,
     NOW() - INTERVAL 180 DAY, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 175 DAY, NOW() - INTERVAL 25 DAY,
     6, '吴婷', '中行杭州分行全辖网络设备国产化替换与验收', 95, 'NORMAL',
     'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 20 DAY, 0, 0),
(6,  'PMS-2024-0006', '交通银行成都分行网络安全扩容项目',         'SECURITY',       'INITIAL_ACCEPTANCE',
     '交通银行成都分行', '徐建华', '13906789012', 'BCOM-CD-2024-006', 980000.00,
     NOW() - INTERVAL 110 DAY, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 105 DAY, NOW() - INTERVAL 12 DAY,
     7, '郑昊', '交行成都分行安全设备扩容与等保三级整改', 90, 'NORMAL',
     'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0, 0),
(7,  'PMS-2024-0007', '中国邮政储蓄银行武汉分行网络改造项目',     'NETWORK_DEVICE', 'APPROVED',
     '中国邮政储蓄银行武汉分行', '胡军', '13907890123', 'PSBC-WH-2024-007', 1320000.00,
     NOW() - INTERVAL 30 DAY, NOW() + INTERVAL 150 DAY, NULL, NULL,
     2, '张明', '邮储银行武汉分行核心网络改造与分支网点设备替换', 5, 'NORMAL',
     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0, 0),
(8,  'PMS-2024-0008', '中信银行南京分行数据中心迁移项目',         'DATACENTER',     'PENDING',
     '中信银行南京分行', '朱国良', '13908901234', 'CITIC-NJ-2024-008', 4280000.00,
     NOW() + INTERVAL 15 DAY, NOW() + INTERVAL 200 DAY, NULL, NULL,
     3, '刘伟', '中信银行南京分行数据中心整体迁移与网络重建', 0, 'HIGH',
     'admin', NOW() - INTERVAL 10 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0, 0),
(9,  'PMS-2024-0009', '民生银行西安分行网络升级项目',             'NETWORK_DEVICE', 'REJECTED',
     '民生银行西安分行', '何志强', '13909012345', 'CMBC-XA-2024-009', 760000.00,
     NOW() - INTERVAL 60 DAY, NOW() + INTERVAL 120 DAY, NULL, NULL,
     4, '赵琳', '民生银行西安分行分支网点网络设备更新，因预算调整被驳回', 0, 'LOW',
     'admin', NOW() - INTERVAL 60 DAY, 'admin', NOW() - INTERVAL 40 DAY, 0, 0),
(10, 'PMS-2024-0010', '华夏银行青岛分行安全设备部署项目',           'SECURITY',       'CLOSED',
     '华夏银行青岛分行', '罗建明', '13901234500', 'HXB-QD-2024-010', 1100000.00,
     NOW() - INTERVAL 300 DAY, NOW() - INTERVAL 150 DAY, NOW() - INTERVAL 295 DAY, NOW() - INTERVAL 145 DAY,
     5, '孙磊', '华夏银行青岛分行全辖安全设备部署与运维交接完成', 100, 'NORMAL',
     'admin', NOW() - INTERVAL 300 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0, 0);

-- =============================================================
-- 2. pms_asset_category（10 条）
-- =============================================================
INSERT INTO `pms_asset_category`
    (`id`, `parent_id`, `category_name`, `category_code`, `sort_order`, `status`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  0, '网络设备',     'NETWORK',        1,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(2,  1, '路由器',       'ROUTER',         1,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(3,  1, '交换机',       'SWITCH',         2,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(4,  1, '防火墙',       'FIREWALL',       3,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(5,  1, '负载均衡',     'LOAD_BALANCER',  4,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(6,  0, '安全设备',     'SECURITY',       2,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(7,  6, '入侵检测系统', 'IDS',            1,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(8,  6, '入侵防御系统', 'IPS',            2,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(9,  0, '数据中心设备', 'DATACENTER',     3,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0),
(10, 9, '服务器',       'SERVER',         1,  1, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 0);

-- =============================================================
-- 3. pms_asset_model（10 条）
-- =============================================================
INSERT INTO `pms_asset_model`
    (`id`, `category_id`, `model_name`, `model_code`, `brand`, `spec_params`, `standard_price`, `unit`, `status`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  2,  'DPtech DPI-3000 路由器',         'DPI-3000',   'DPtech',  '{"ports":"4*10GE","throughput":"20Gbps"}',       68000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(2,  3,  'H3C S5560-EI 交换机',             'S5560-EI',   'H3C',     '{"ports":"48*1GE+4*10GE","stack":"IRF"}',          32000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(3,  4,  'DPtech DPX8000 防火墙',           'DPX8000',    'DPtech',  '{"throughput":"40Gbps","concurrent":"5M"}',      156000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(4,  5,  'F5 BIG-IP i5800 负载均衡',        'BIG-IP-5800','F5',      '{"throughput":"20Gbps","ssl_tps":"100K"}',        220000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(5,  2,  'Cisco ISR4451-X 路由器',          'ISR4451-X',  'Cisco',   '{"ports":"2*10GE+4*1GE","wan":"dual"}',           88000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(6,  3,  'Huawei CE6865 交换机',            'CE6865',     'Huawei',  '{"ports":"48*25GE+8*100GE","spine":"yes"}',      128000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(7,  7,  'DPtech IPS-2000 入侵检测系统',    'IPS-2000',   'DPtech',  '{"throughput":"10Gbps","signatures":"50K"}',      58000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(8,  8,  'Cisco Firepower 4112 入侵防御',   'FP-4112',    'Cisco',   '{"throughput":"12Gbps","throughput_ssl":"6G"}',   105000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(9,  10, 'Dell PowerEdge R750 服务器',      'R750',       'Dell',    '{"cpu":"2*XeonGold6338","mem":"512GB"}',          96000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(10, 10, 'Huawei 2288H V5 服务器',          '2288H-V5',   'Huawei',  '{"cpu":"2*XeonSilver4314","mem":"256GB"}',        65000.00, '台', 1, 'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0);

-- =============================================================
-- 4. pms_agent（10 条）— 服务商/合作伙伴
-- =============================================================
INSERT INTO `pms_agent`
    (`id`, `agent_name`, `agent_code`, `contact_person`, `contact_phone`, `contact_email`, `address`, `qualification`,
     `status`, `overall_score`, `cert_level`, `ccie_count`, `specializations`, `cert_expiry_date`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  '杭州迪普科技股份有限公司北京分公司', 'DPTECH-BJ', '马志强', '13801001001', 'mazq@dptech.com',     '北京市海淀区中关村软件园9号楼',       '系统集成一级/网络设备原厂资质', 1, 8.5, 'GOLD',    5, '["网络设备","安全设备","数据中心"]', NOW() + INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 30 DAY, 0),
(2,  '北京华三通信技术有限公司',           'H3C-BJ',    '林建华', '13801001002', 'linjh@h3c.com',       '北京市朝阳区望京SOHO T1',            '系统集成一级/H3C代理商',         1, 8.8, 'PREMIER', 4, '["网络设备","交换机","路由器"]',     NOW() + INTERVAL 300 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 60 DAY, 0),
(3,  '思科(中国)信息技术服务有限公司',     'CISCO-CN',  '吴伟',   '13801001003', 'wuw@cisco.com',       '上海市浦东新区张江高科技园区',       'Cisco金牌代理商/Cisco认证服务商', 1, 9.2, 'GOLD',    8, '["网络设备","安全设备"]',           NOW() + INTERVAL 400 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 20 DAY, 0),
(4,  '华为技术有限公司北京代表处',         'HUAWEI-BJ',  '郑涛',   '13801001004', 'zhengt@huawei.com',   '北京市海淀区华为大厦',               '华为企业业务金牌经销商',          1, 9.0, 'GOLD',    6, '["网络设备","服务器","数据中心"]', NOW() + INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 15 DAY, 0),
(5,  '北京神州数码有限公司',               'DIGITAL-CN','王海涛', '13801001005', 'wanght@digitalchina.com','北京市海淀区中关村大街1号',         '系统集成特一级/多品牌代理',       1, 7.6, 'SILVER',  3, '["网络设备","系统集成"]',           NOW() + INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 90 DAY, 0),
(6,  '上海天融信网络安全技术有限公司',     'TOPSEC-SH', '陈军',   '13801001006', 'chenj@topsec.com.cn', '上海市闵行区紫月路1188号',           '安全服务一级/天融信代理',         1, 7.9, 'PREMIER', 2, '["安全设备","防火墙","入侵检测"]',   NOW() + INTERVAL 250 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 45 DAY, 0),
(7,  '北京东华软件股份公司',               'DHCC-BJ',   '杨光',   '13801001007', 'yangg@dhcc.com.cn',   '北京市海淀区紫金数码园3层',           '系统集成一级/软件开发资质',       1, 7.2, 'SELECT',  1, '["系统集成","运维服务"]',           NOW() + INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(8,  '深圳市深信服科技有限公司北京分公司','SANGFOR-BJ','刘洋',   '13801001008', 'liuy@sangfor.com.cn','北京市朝阳区建国门外大街8号',        '安全服务二级/深信服代理',         1, 7.5, 'SILVER',  2, '["安全设备","VPN","上网行为管理"]', NOW() + INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 80 DAY, 0),
(9,  '北京太极计算机股份有限公司',         'TAIJI-BJ',  '徐刚',   '13801001009', 'xug@taiji.com.cn',   '北京市朝阳区北辰东路8号汇欣大厦',     '系统集成特一级/国家保密局资质',   1, 8.1, 'PREMIER', 3, '["系统集成","数据中心","安全"]',    NOW() + INTERVAL 280 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 50 DAY, 0),
(10, '上海宝信软件股份有限公司',           'BAOSIGHT',  '黄磊',   '13801001010', 'huangl@baosight.com','上海市浦东新区张江高科技园区',        '系统集成一级/软件成熟度CMMI5',    1, 7.8, 'SELECT',  2, '["数据中心","服务器","存储"]',      NOW() + INTERVAL 220 DAY, 'admin', NOW() - INTERVAL 365 DAY, 'admin', NOW() - INTERVAL 70 DAY, 0);

-- =============================================================
-- 5. pms_asset（20 条）— 资产实例（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_asset`
    (`id`, `serial_no`, `model_id`, `category_id`, `asset_name`, `status`,
     `warehouse`, `location`, `project_id`, `inbound_time`, `outbound_time`, `remarks`,
     `mac_address`, `management_ip`, `hostname`, `data_center`, `rack`, `po_no`, `invoice_no`, `warranty_contract_no`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  'DP-2024-00001', 1, 2,  'DPtech DPI-3000 路由器(北京工行)',     'IN_PRODUCTION', '北京中心库', '工行北京分行核心机房-A1-01', 1,  NOW() - INTERVAL 195 DAY, NOW() - INTERVAL 190 DAY, '工行核心网骨干路由器',     '00:1e:08:aa:00:01', '10.10.1.1',   'bj-icbc-rtr-01', '工行北京数据中心', 'A1-01', 'PO-2024-0001', 'INV-2024-0001', 'WC-2024-0001', 'admin', NOW() - INTERVAL 196 DAY, 'admin', NOW() - INTERVAL 90 DAY, 0),
(2,  'DP-2024-00002', 1, 2,  'DPtech DPI-3000 路由器(北京工行备)',   'IN_PRODUCTION', '北京中心库', '工行北京分行核心机房-A1-02', 1,  NOW() - INTERVAL 195 DAY, NOW() - INTERVAL 190 DAY, '工行核心网备用路由器',     '00:1e:08:aa:00:02', '10.10.1.2',   'bj-icbc-rtr-02', '工行北京数据中心', 'A1-02', 'PO-2024-0001', 'INV-2024-0001', 'WC-2024-0001', 'admin', NOW() - INTERVAL 196 DAY, 'admin', NOW() - INTERVAL 90 DAY, 0),
(3,  'DP-2024-00003', 2, 3,  'H3C S5560-EI 交换机(工行接入)',         'IN_PRODUCTION', '北京中心库', '工行北京分行接入机房-B2-03', 1,  NOW() - INTERVAL 193 DAY, NOW() - INTERVAL 188 DAY, '工行接入层交换机',         '00:23:24:bb:00:03', '10.10.2.3',   'bj-icbc-sw-03',  '工行北京数据中心', 'B2-03', 'PO-2024-0002', 'INV-2024-0002', 'WC-2024-0002', 'admin', NOW() - INTERVAL 194 DAY, 'admin', NOW() - INTERVAL 88 DAY, 0),
(4,  'DP-2024-00004', 3, 4,  'DPtech DPX8000 防火墙(工行边界)',       'IN_PRODUCTION', '北京中心库', '工行北京分行安全边界-C1-04', 1,  NOW() - INTERVAL 192 DAY, NOW() - INTERVAL 187 DAY, '工行边界防火墙',           '00:1e:08:cc:00:04', '10.10.3.4',   'bj-icbc-fw-04',  '工行北京数据中心', 'C1-04', 'PO-2024-0003', 'INV-2024-0003', 'WC-2024-0003', 'admin', NOW() - INTERVAL 193 DAY, 'admin', NOW() - INTERVAL 87 DAY, 0),
(5,  'DP-2024-00005', 2, 3,  'H3C S5560-EI 交换机(建行核心)',         'INSTALLED',     '上海中心库', '建行上海数据中心核心机房-D1', 2,  NOW() - INTERVAL 140 DAY, NOW() - INTERVAL 100 DAY, '建行核心交换机',           '00:23:24:bb:00:05', '10.20.1.5',   'sh-ccb-sw-05',   '建行上海数据中心', 'D1-05', 'PO-2024-0004', 'INV-2024-0004', 'WC-2024-0004', 'admin', NOW() - INTERVAL 141 DAY, 'admin', NOW() - INTERVAL 4 DAY, 0),
(6,  'DP-2024-00006', 4, 5,  'F5 BIG-IP i5800 负载均衡(建行)',         'INSTALLED',     '上海中心库', '建行上海数据中心负载均衡区-D2', 2, NOW() - INTERVAL 138 DAY, NOW() - INTERVAL 95 DAY, '建行负载均衡器',           '00:01:d7:dd:00:06', '10.20.2.6',   'sh-ccb-lb-06',   '建行上海数据中心', 'D2-06', 'PO-2024-0005', 'INV-2024-0005', 'WC-2024-0005', 'admin', NOW() - INTERVAL 139 DAY, 'admin', NOW() - INTERVAL 3 DAY, 0),
(7,  'DP-2024-00007', 3, 4,  'DPtech DPX8000 防火墙(农行)',           'INSTALLED',     '深圳中心库', '农行深圳分行机房-E1-07',     3,  NOW() - INTERVAL 110 DAY, NOW() - INTERVAL 80 DAY,  '农行边界防火墙',           '00:1e:08:cc:00:07', '10.30.3.7',   'sz-abc-fw-07',   '农行深圳数据中心', 'E1-07', 'PO-2024-0006', 'INV-2024-0006', 'WC-2024-0006', 'admin', NOW() - INTERVAL 111 DAY, 'admin', NOW() - INTERVAL 2 DAY, 0),
(8,  'DP-2024-00008', 7, 7,  'DPtech IPS-2000 入侵检测(农行)',        'INSTALLED',     '深圳中心库', '农行深圳分行安全区-E2-08',   3,  NOW() - INTERVAL 108 DAY, NOW() - INTERVAL 75 DAY,  '农行入侵检测设备',         '00:1e:08:ee:00:08', '10.30.4.8',   'sz-abc-ips-08',  '农行深圳数据中心', 'E2-08', 'PO-2024-0007', 'INV-2024-0007', 'WC-2024-0007', 'admin', NOW() - INTERVAL 109 DAY, 'admin', NOW() - INTERVAL 1 DAY, 0),
(9,  'DP-2024-00009', 5, 2,  'Cisco ISR4451-X 路由器(招行)',         'STAGED',        '广州中心库', '招行广州分行暂存区-F1-09',   4,  NOW() - INTERVAL 80 DAY,  NULL,                       '招行分支路由器(暂存)',     '00:1b:54:ff:00:09', '10.40.1.9',   NULL,             '招行广州数据中心', NULL,   'PO-2024-0008', 'INV-2024-0008', 'WC-2024-0008', 'admin', NOW() - INTERVAL 81 DAY,  'admin', NOW() - INTERVAL 1 DAY, 0),
(10, 'DP-2024-00010', 6, 3,  'Huawei CE6865 交换机(招行核心)',        'STAGED',        '广州中心库', '招行广州数据中心暂存区-F2',  4,  NOW() - INTERVAL 78 DAY,  NULL,                       '招行核心交换机(待安装)',   '00:0c:29:bb:00:0a', '10.40.2.10',  NULL,             '招行广州数据中心', NULL,   'PO-2024-0009', 'INV-2024-0009', 'WC-2024-0009', 'admin', NOW() - INTERVAL 79 DAY,  'admin', NOW() - INTERVAL 1 DAY, 0),
(11, 'DP-2024-00011', 2, 3,  'H3C S5560-EI 交换机(中行杭州)',         'IN_PRODUCTION', '杭州中心库', '中行杭州分行机房-G1-11',     5,  NOW() - INTERVAL 170 DAY, NOW() - INTERVAL 160 DAY, '中行接入交换机',           '00:23:24:bb:00:0b', '10.50.2.11',  'hz-boc-sw-11',   '中行杭州数据中心', 'G1-11', 'PO-2024-0010', 'INV-2024-0010', 'WC-2024-0010', 'admin', NOW() - INTERVAL 171 DAY, 'admin', NOW() - INTERVAL 30 DAY, 0),
(12, 'DP-2024-00012', 3, 4,  'DPtech DPX8000 防火墙(中行杭州)',       'IN_PRODUCTION', '杭州中心库', '中行杭州分行安全边界-G2-12', 5,  NOW() - INTERVAL 168 DAY, NOW() - INTERVAL 158 DAY, '中行边界防火墙',           '00:1e:08:cc:00:0c', '10.50.3.12',  'hz-boc-fw-12',   '中行杭州数据中心', 'G2-12', 'PO-2024-0011', 'INV-2024-0011', 'WC-2024-0010', 'admin', NOW() - INTERVAL 169 DAY, 'admin', NOW() - INTERVAL 30 DAY, 0),
(13, 'DP-2024-00013', 8, 8,  'Cisco Firepower 4112 入侵防御(交行)',   'IN_PRODUCTION', '成都中心库', '交行成都分行安全区-H1-13',   6,  NOW() - INTERVAL 100 DAY, NOW() - INTERVAL 90 DAY,  '交行入侵防御设备',         '00:1b:54:cc:00:0d', '10.60.4.13',  'cd-bcom-ips-13', '交行成都数据中心', 'H1-13', 'PO-2024-0012', 'INV-2024-0012', 'WC-2024-0012', 'admin', NOW() - INTERVAL 101 DAY, 'admin', NOW() - INTERVAL 12 DAY, 0),
(14, 'DP-2024-00014', 1, 2,  'DPtech DPI-3000 路由器(交行分支)',      'IN_PRODUCTION', '成都中心库', '交行成都分行分支机房-H2-14', 6,  NOW() - INTERVAL 98 DAY,  NOW() - INTERVAL 88 DAY,  '交行分支路由器',           '00:1e:08:aa:00:0e', '10.60.1.14',  'cd-bcom-rtr-14', '交行成都数据中心', 'H2-14', 'PO-2024-0013', 'INV-2024-0013', 'WC-2024-0013', 'admin', NOW() - INTERVAL 99 DAY,  'admin', NOW() - INTERVAL 11 DAY, 0),
(15, 'DP-2024-00015', 9, 10, 'Dell PowerEdge R750 服务器(华夏青岛)',   'IN_PRODUCTION', '青岛中心库', '华夏青岛分行数据中心-I1-15', 10, NOW() - INTERVAL 280 DAY, NOW() - INTERVAL 270 DAY, '华夏青岛应用服务器',       '00:15:c5:aa:00:0f', '10.70.1.15',  'qd-hxb-srv-15',  '华夏青岛数据中心', 'I1-15', 'PO-2024-0014', 'INV-2024-0014', 'WC-2024-0014', 'admin', NOW() - INTERVAL 281 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(16, 'DP-2024-00016', 10, 10,'Huawei 2288H V5 服务器(华夏青岛)',       'IN_PRODUCTION', '青岛中心库', '华夏青岛分行数据中心-I2-16', 10, NOW() - INTERVAL 278 DAY, NOW() - INTERVAL 268 DAY, '华夏青岛数据库服务器',     '00:0c:29:bb:00:10', '10.70.2.16',  'qd-hxb-srv-16',  '华夏青岛数据中心', 'I2-16', 'PO-2024-0015', 'INV-2024-0015', 'WC-2024-0014', 'admin', NOW() - INTERVAL 279 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(17, 'DP-2024-00017', 2, 3,  'H3C S5560-EI 交换机(库存)',             'RECEIVED',      '北京中心库', '北京中心库货架-J1-17',       NULL, NOW() - INTERVAL 30 DAY, NULL,                       '通用库存交换机',           '00:23:24:bb:00:11', NULL,         NULL,             NULL,                NULL,    'PO-2024-0016', 'INV-2024-0016', NULL,           'admin', NOW() - INTERVAL 31 DAY,  'admin', NOW() - INTERVAL 30 DAY, 0),
(18, 'DP-2024-00018', 1, 2,  'DPtech DPI-3000 路由器(库存)',         'RECEIVED',      '上海中心库', '上海中心库货架-J2-18',       NULL, NOW() - INTERVAL 25 DAY, NULL,                       '通用库存路由器',           '00:1e:08:aa:00:12', NULL,         NULL,             NULL,                NULL,    'PO-2024-0017', 'INV-2024-0017', NULL,           'admin', NOW() - INTERVAL 26 DAY,  'admin', NOW() - INTERVAL 25 DAY, 0),
(19, 'DP-2024-00019', 3, 4,  'DPtech DPX8000 防火墙(返修)',          'RMA',           '北京中心库', '北京中心库返修区-K1-19',     1,  NOW() - INTERVAL 190 DAY, NOW() - INTERVAL 60 DAY,  '工行防火墙故障返修中',     '00:1e:08:cc:00:13', NULL,         NULL,             NULL,                NULL,    'PO-2024-0018', 'INV-2024-0018', NULL,           'admin', NOW() - INTERVAL 191 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0),
(20, 'DP-2024-00020', 9, 10, 'Dell PowerEdge R750 服务器(退役)',     'DECOMMISSIONED','青岛中心库', '青岛中心库退役区-K2-20',     10, NOW() - INTERVAL 360 DAY, NOW() - INTERVAL 150 DAY, '华夏青岛旧服务器退役',     '00:15:c5:aa:00:14', NULL,         NULL,             NULL,                NULL,    'PO-2023-0020', 'INV-2023-0020', NULL,           'admin', NOW() - INTERVAL 361 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0);

-- =============================================================
-- 6. pms_project_member（20 条）— 项目成员
-- =============================================================
INSERT INTO `pms_project_member`
    (`id`, `project_id`, `user_id`, `role_type`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1, 2, 'PM',       'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 200 DAY, 0),
(2,  1, 7, 'ENGINEER', 'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(3,  1, 8, 'ENGINEER', 'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(4,  1, 9, 'QA',       'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(5,  2, 3, 'PM',       'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0),
(6,  2, 7, 'ENGINEER', 'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 5 DAY,   0),
(7,  2, 8, 'ENGINEER', 'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 5 DAY,   0),
(8,  3, 4, 'PM',       'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 120 DAY, 0),
(9,  3, 8, 'ENGINEER', 'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 3 DAY,   0),
(10, 3, 9, 'QA',       'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 3 DAY,   0),
(11, 4, 5, 'PM',       'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 90 DAY,  0),
(12, 4, 7, 'ENGINEER', 'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 2 DAY,   0),
(13, 4, 10,'OBSERVER', 'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 2 DAY,   0),
(14, 5, 6, 'PM',       'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 180 DAY, 0),
(15, 5, 8, 'ENGINEER', 'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(16, 6, 7, 'PM',       'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 110 DAY, 0),
(17, 6, 9, 'QA',       'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(18, 7, 2, 'PM',       'admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 30 DAY,  0),
(19, 8, 3, 'PM',       'admin', NOW() - INTERVAL 10 DAY,  'admin', NOW() - INTERVAL 10 DAY,  0),
(20, 10,5, 'PM',       'admin', NOW() - INTERVAL 300 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0);

-- =============================================================
-- 7. pms_milestone（20 条）— 里程碑（依赖 project_id 外键）
--    使用 V10 扩展后的 12 节点 PPDIOO 里程碑类型。
-- =============================================================
INSERT INTO `pms_milestone`
    (`id`, `project_id`, `milestone_name`, `milestone_type`, `ppdioo_phase`, `plan_date`, `actual_date`, `status`, `description`, `sort_order`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  '现场勘查',         'SITE_SURVEY',     'PREPARE',   NOW() - INTERVAL 198 DAY, NOW() - INTERVAL 196 DAY, 'COMPLETED',   '工行北京分行机房勘查完成', 1,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 196 DAY, 0),
(2,  1,  '网络设计',         'NETWORK_DESIGN', 'PLAN',      NOW() - INTERVAL 190 DAY, NOW() - INTERVAL 188 DAY, 'COMPLETED',   '工行核心网拓扑设计完成',   2,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 188 DAY, 0),
(3,  1,  '设备到货',         'ARRIVAL',         'IMPLEMENT', NOW() - INTERVAL 180 DAY, NOW() - INTERVAL 178 DAY, 'COMPLETED',   '路由器/交换机/防火墙到货', 3,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 178 DAY, 0),
(4,  1,  '安装实施',         'INSTALLATION',    'IMPLEMENT', NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 155 DAY, 'COMPLETED',   '设备上架安装完成',         4,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 155 DAY, 0),
(5,  1,  '联调测试',         'TESTING',         'IMPLEMENT', NOW() - INTERVAL 130 DAY, NOW() - INTERVAL 120 DAY, 'COMPLETED',   '设备联调与功能测试',       5,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 120 DAY, 0),
(6,  1,  '现场验收',         'SAT',             'IMPLEMENT', NOW() - INTERVAL 105 DAY, NOW() - INTERVAL 100 DAY, 'COMPLETED',   '现场验收通过',             6,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(7,  1,  '终验',             'FINAL_ACCEPTANCE','OPERATE',   NOW() - INTERVAL 95 DAY,  NOW() - INTERVAL 95 DAY,  'COMPLETED',   '项目终验完成',             7,  'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(8,  2,  '现场勘查',         'SITE_SURVEY',     'PREPARE',   NOW() - INTERVAL 145 DAY, NOW() - INTERVAL 143 DAY, 'COMPLETED',   '建行上海数据中心勘查',     1,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 143 DAY, 0),
(9,  2,  '网络设计',         'NETWORK_DESIGN', 'PLAN',      NOW() - INTERVAL 130 DAY, NOW() - INTERVAL 125 DAY, 'COMPLETED',   '建行核心网设计',           2,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 125 DAY, 0),
(10, 2,  '设备到货',         'ARRIVAL',         'IMPLEMENT', NOW() - INTERVAL 100 DAY, NOW() - INTERVAL 98 DAY,  'COMPLETED',   '建行设备到货',             3,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 98 DAY,  0),
(11, 2,  '安装实施',         'INSTALLATION',    'IMPLEMENT', NOW() - INTERVAL 60 DAY,  NULL,                     'IN_PROGRESS', '建行设备安装中',           4,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 5 DAY,   0),
(12, 2,  '联调测试',         'TESTING',         'IMPLEMENT', NOW() - INTERVAL 20 DAY,  NULL,                     'PENDING',     '建行设备联调测试',         5,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0),
(13, 3,  '现场勘查',         'SITE_SURVEY',     'PREPARE',   NOW() - INTERVAL 115 DAY, NOW() - INTERVAL 113 DAY, 'COMPLETED',   '农行深圳机房勘查',         1,  'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 113 DAY, 0),
(14, 3,  '设备到货',         'ARRIVAL',         'IMPLEMENT', NOW() - INTERVAL 90 DAY,  NOW() - INTERVAL 88 DAY,  'COMPLETED',   '农行安全设备到货',         2,  'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 88 DAY,  0),
(15, 3,  '安装实施',         'INSTALLATION',    'IMPLEMENT', NOW() - INTERVAL 40 DAY,  NULL,                     'IN_PROGRESS', '农行设备安装中',           3,  'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 3 DAY,   0),
(16, 5,  '终验',             'FINAL_ACCEPTANCE','OPERATE',   NOW() - INTERVAL 25 DAY,  NULL,                     'OVERDUE',     '中行杭州终验申请待提交',   1,  'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 25 DAY,  0),
(17, 6,  '现场验收',         'SAT',             'IMPLEMENT', NOW() - INTERVAL 12 DAY,  NOW() - INTERVAL 12 DAY,  'COMPLETED',   '交行成都现场验收',         1,  'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 12 DAY,  0),
(18, 6,  '用户验收测试',     'UAT',             'OPERATE',   NOW() - INTERVAL 5 DAY,   NULL,                     'BLOCKED',     '交行UAT受客户配合阻塞',    2,  'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 1 DAY,   0),
(19, 4,  '设备采购',         'PROCUREMENT',     'PLAN',      NOW() - INTERVAL 60 DAY,  NOW() - INTERVAL 55 DAY,  'COMPLETED',   '招行设备采购完成',         1,  'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 55 DAY,  0),
(20, 4,  '工厂验收测试',     'FAT',            'DESIGN',    NOW() - INTERVAL 30 DAY,  NOW() - INTERVAL 25 DAY,  'COMPLETED',   '招行设备FAT',              2,  'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0);

-- =============================================================
-- 8. pms_final_acceptance（10 条）— 终验记录
-- =============================================================
INSERT INTO `pms_final_acceptance`
    (`id`, `project_id`, `apply_time`, `apply_user_id`, `apply_user_name`, `status`,
     `acceptance_report`, `acceptance_opinion`, `accept_user_id`, `accept_user_name`, `accept_time`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  NOW() - INTERVAL 100 DAY, 2, '张明', 'APPROVED', '工行北京分行核心网络改造终验报告：设备全部上线，业务正常。', '验收通过，符合合同要求。', 1, 'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(2,  5,  NOW() - INTERVAL 25 DAY,  6, '吴婷', 'PENDING', '中行杭州分行网络设备更新终验申请：设备已部署完成。', NULL, NULL, NULL, NULL, 'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(3,  6,  NOW() - INTERVAL 12 DAY,  7, '郑昊', 'PENDING', '交行成都分行安全扩容终验申请：安全设备已部署。', NULL, NULL, NULL, NULL, 'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(4,  10, NOW() - INTERVAL 145 DAY, 5, '孙磊', 'APPROVED', '华夏青岛分行安全设备部署终验报告：所有设备运行正常。', '验收通过，运维已交接。', 1, 'admin', NOW() - INTERVAL 140 DAY, 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(5,  2,  NOW() - INTERVAL 5 DAY,   3, '刘伟', 'PENDING', '建行上海数据中心网络升级阶段终验申请。', NULL, NULL, NULL, NULL, 'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0),
(6,  3,  NOW() - INTERVAL 3 DAY,   4, '赵琳', 'PENDING', '农行深圳分行网络安全加固终验申请。', NULL, NULL, NULL, NULL, 'admin', NOW() - INTERVAL 3 DAY,   'admin', NOW() - INTERVAL 3 DAY,   0),
(7,  4,  NOW() - INTERVAL 2 DAY,   5, '孙磊', 'PENDING', '招行广州分行数据中心建设阶段终验申请。', NULL, NULL, NULL, NULL, 'admin', NOW() - INTERVAL 2 DAY,   'admin', NOW() - INTERVAL 2 DAY,   0),
(8,  7,  NOW() - INTERVAL 5 DAY,   2, '张明', 'REJECTED', '邮储武汉分行网络改造终验申请：部分分支网点设备未到位。', '材料不完整，需补充分支网点交付清单后重新申请。', 1, 'admin', NOW() - INTERVAL 4 DAY,  'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 4 DAY,   0),
(9,  9,  NOW() - INTERVAL 40 DAY,  4, '赵琳', 'REJECTED', '民生西安分行网络升级终验申请。', '项目已被驳回，终验不通过。', 1, 'admin', NOW() - INTERVAL 40 DAY, 'admin', NOW() - INTERVAL 40 DAY, 'admin', NOW() - INTERVAL 40 DAY, 0),
(10, 8, NOW() - INTERVAL 8 DAY,    3, '刘伟', 'PENDING', '中信南京分行数据中心迁移预终验申请。', NULL, NULL, NULL, NULL, 'admin', NOW() - INTERVAL 8 DAY,   'admin', NOW() - INTERVAL 8 DAY,   0);

-- =============================================================
-- 9. pms_deliverable（20 条）— 交付物
-- =============================================================
INSERT INTO `pms_deliverable`
    (`id`, `project_id`, `deliverable_name`, `deliverable_type`, `file_path`, `status`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  '工行北京网络改造设计方案',       'DOCUMENT', '/upload/2024/icbc/design.pdf',        'CONFIRMED', 'admin', NOW() - INTERVAL 188 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(2,  1,  '工行北京设备配置清单',           'CONFIG',   '/upload/2024/icbc/config.xlsx',        'CONFIRMED', 'admin', NOW() - INTERVAL 188 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(3,  1,  '工行北京联调测试报告',           'REPORT',   '/upload/2024/icbc/test-report.pdf',   'CONFIRMED', 'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(4,  1,  '工行北京项目验收报告',           'REPORT',   '/upload/2024/icbc/acceptance.pdf',     'CONFIRMED', 'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 95 DAY,  0),
(5,  2,  '建行上海数据中心网络拓扑设计',   'DOCUMENT', '/upload/2024/ccb/topology.pdf',       'CONFIRMED', 'admin', NOW() - INTERVAL 125 DAY, 'admin', NOW() - INTERVAL 5 DAY,   0),
(6,  2,  '建行上海设备配置文件',           'CONFIG',   '/upload/2024/ccb/config.zip',         'SUBMITTED', 'admin', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 5 DAY,   0),
(7,  2,  '建行上海FAT报告',               'REPORT',   '/upload/2024/ccb/fat.pdf',             'CONFIRMED', 'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(8,  3,  '农行深圳安全加固方案',           'DOCUMENT', '/upload/2024/abc/security-plan.pdf',  'CONFIRMED', 'admin', NOW() - INTERVAL 113 DAY, 'admin', NOW() - INTERVAL 3 DAY,   0),
(9,  3,  '农行深圳安全策略配置',           'CONFIG',   '/upload/2024/abc/policy.cfg',          'SUBMITTED', 'admin', NOW() - INTERVAL 50 DAY,  'admin', NOW() - INTERVAL 3 DAY,   0),
(10, 3,  '农行深圳渗透测试报告',           'REPORT',   '/upload/2024/abc/pen-test.pdf',        'PENDING',   'admin', NOW() - INTERVAL 10 DAY,  'admin', NOW() - INTERVAL 10 DAY,  0),
(11, 4,  '招行广州数据中心建设方案',         'DOCUMENT', '/upload/2024/cmb/datacenter-plan.pdf','SUBMITTED', 'admin', NOW() - INTERVAL 85 DAY,  'admin', NOW() - INTERVAL 2 DAY,   0),
(12, 4,  '招行广州设备到货清单',           'OTHER',    '/upload/2024/cmb/arrival-list.xlsx',   'CONFIRMED', 'admin', NOW() - INTERVAL 55 DAY,  'admin', NOW() - INTERVAL 55 DAY,  0),
(13, 5,  '中行杭州设备更新实施方案',       'DOCUMENT', '/upload/2024/boc/impl-plan.pdf',      'CONFIRMED', 'admin', NOW() - INTERVAL 175 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(14, 5,  '中行杭州设备配置清单',           'CONFIG',   '/upload/2024/boc/config.xlsx',        'CONFIRMED', 'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(15, 5,  '中行杭州验收测试报告',           'REPORT',   '/upload/2024/boc/sat-report.pdf',     'SUBMITTED', 'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(16, 6,  '交行成都安全扩容方案',           'DOCUMENT', '/upload/2024/bcom/expand-plan.pdf',   'CONFIRMED', 'admin', NOW() - INTERVAL 105 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(17, 6,  '交行成都等保整改报告',           'REPORT',   '/upload/2024/bcom/compliance.pdf',     'SUBMITTED', 'admin', NOW() - INTERVAL 15 DAY,  'admin', NOW() - INTERVAL 15 DAY,  0),
(18, 10, '华夏青岛安全设备部署方案',       'DOCUMENT', '/upload/2024/hxb/deploy-plan.pdf',    'CONFIRMED', 'admin', NOW() - INTERVAL 290 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(19, 10, '华夏青岛运维交接文档',           'DOCUMENT', '/upload/2024/hxb/handover.docx',      'CONFIRMED', 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(20, 7,  '邮储武汉网络改造初版方案',       'DOCUMENT', '/upload/2024/psbc/draft-plan.pdf',     'PENDING',   'admin', NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 20 DAY,  0);

-- =============================================================
-- 10. pms_deliverable_checklist（20 条）— 终验交付物检查清单
-- =============================================================
INSERT INTO `pms_deliverable_checklist`
    (`id`, `project_id`, `deliverable_type`, `required`, `uploaded`, `attachment_id`, `checked_at`, `checked_by`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  'AS_BUILT',         1, 1, 1,  NOW() - INTERVAL 95 DAY,  'admin', 'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(2,  1,  'TEST_REPORT',      1, 1, 3,  NOW() - INTERVAL 95 DAY,  'admin', 'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(3,  1,  'ACCEPTANCE_CERT',  1, 1, 4,  NOW() - INTERVAL 95 DAY,  'admin', 'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(4,  1,  'ASSET_REGISTER',  1, 1, NULL, NOW() - INTERVAL 95 DAY, 'admin', 'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(5,  1,  'WARRANTY_CERT',    1, 1, NULL, NOW() - INTERVAL 95 DAY, 'admin', 'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(6,  5,  'AS_BUILT',         1, 0, NULL, NULL,                   NULL,    'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(7,  5,  'TEST_REPORT',      1, 0, NULL, NULL,                   NULL,    'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(8,  5,  'ACCEPTANCE_CERT', 1, 0, NULL, NULL,                   NULL,    'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(9,  5,  'TRAINING_RECORD', 0, 1, NULL, NOW() - INTERVAL 30 DAY, 'admin', 'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 30 DAY,  0),
(10, 5,  'WARRANTY_CERT',   1, 0, NULL, NULL,                   NULL,    'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(11, 6,  'AS_BUILT',         1, 1, NULL, NOW() - INTERVAL 12 DAY, 'admin', 'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(12, 6,  'TEST_REPORT',      1, 1, NULL, NOW() - INTERVAL 12 DAY, 'admin', 'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(13, 6,  'ACCEPTANCE_CERT', 1, 0, NULL, NULL,                   NULL,    'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(14, 6,  'OPERATION_MANUAL',1, 1, NULL, NOW() - INTERVAL 12 DAY, 'admin', 'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(15, 10, 'AS_BUILT',         1, 1, 18, NOW() - INTERVAL 140 DAY, 'admin', 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(16, 10, 'ACCEPTANCE_CERT', 1, 1, 19, NOW() - INTERVAL 140 DAY, 'admin', 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(17, 10, 'WARRANTY_CERT',   1, 1, NULL, NOW() - INTERVAL 140 DAY, 'admin', 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(18, 10, 'SPARE_PARTS_LIST',0, 1, NULL, NOW() - INTERVAL 140 DAY, 'admin', 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(19, 2,  'AS_BUILT',         1, 0, NULL, NULL,                   NULL,    'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0),
(20, 2,  'TEST_REPORT',      1, 1, 7,  NOW() - INTERVAL 5 DAY,   'admin', 'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0);

-- =============================================================
-- 11. pms_delivery_plan（10 条）— 交付计划
-- =============================================================
INSERT INTO `pms_delivery_plan`
    (`id`, `project_id`, `plan_name`, `plan_content`, `plan_date`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  '工行北京一期交付计划', '核心路由器与交换机部署，含联调测试', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0),
(2,  1,  '工行北京二期交付计划', '防火墙部署与安全策略配置',         NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 110 DAY, 0),
(3,  2,  '建行上海一期交付计划', '核心交换机与负载均衡部署',         NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 90 DAY,  0),
(4,  2,  '建行上海二期交付计划', '安全设备部署与联调',               NOW() - INTERVAL 10 DAY,  'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(5,  3,  '农行深圳交付计划',     '防火墙与入侵检测系统部署',         NOW() - INTERVAL 50 DAY,  'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 50 DAY,  0),
(6,  4,  '招行广州交付计划',     '数据中心网络建设分阶段交付',         NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 30 DAY,  0),
(7,  5,  '中行杭州交付计划',     '国产化设备替换与验收',             NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 180 DAY, 'admin', NOW() - INTERVAL 30 DAY,  0),
(8,  6,  '交行成都交付计划',     '安全设备扩容与等保整改',           NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(9,  10, '华夏青岛交付计划',     '安全设备部署与运维交接',           NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 300 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0),
(10, 7,  '邮储武汉交付计划',     '核心网络改造分批交付',             NOW() + INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 30 DAY,  0);

-- =============================================================
-- 12. pms_baseline_history（10 条）— 基线历史
-- =============================================================
INSERT INTO `pms_baseline_history`
    (`id`, `project_id`, `change_request_id`, `cr_no`, `change_type`, `field_name`, `old_value`, `new_value`, `description`, `changed_at`, `changed_by`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1, NULL, NULL,              'SCOPE',    'deliverables',   '6 项交付物', '7 项交付物', '新增运维交接文档作为交付物', NOW() - INTERVAL 150 DAY, 'admin', 'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0),
(2,  2, 1,    'CR-2024-0001',     'SCHEDULE', 'plan_end_date',  DATE_FORMAT(NOW() + INTERVAL 30 DAY, '%Y-%m-%d'), DATE_FORMAT(NOW() + INTERVAL 45 DAY, '%Y-%m-%d'), '建行项目延期15天', NOW() - INTERVAL 60 DAY, 'admin', 'admin', NOW() - INTERVAL 60 DAY, 'admin', NOW() - INTERVAL 60 DAY, 0),
(3,  2, 1,    'CR-2024-0001',     'COST',     'contract_amount', 2560000.00, 2680000.00, '建行项目追加负载均衡设备预算', NOW() - INTERVAL 58 DAY, 'admin', 'admin', NOW() - INTERVAL 58 DAY, 'admin', NOW() - INTERVAL 58 DAY, 0),
(4,  3, 2,    'CR-2024-0002',     'SCOPE',    'assets',          '4 台安全设备', '6 台安全设备', '农行新增2台入侵检测设备', NOW() - INTERVAL 70 DAY, 'admin', 'admin', NOW() - INTERVAL 70 DAY, 'admin', NOW() - INTERVAL 70 DAY, 0),
(5,  5, 3,    'CR-2024-0003',     'SCHEDULE', 'plan_end_date',   DATE_FORMAT(NOW() - INTERVAL 30 DAY, '%Y-%m-%d'), DATE_FORMAT(NOW() - INTERVAL 20 DAY, '%Y-%m-%d'), '中行项目终验延期', NOW() - INTERVAL 40 DAY, 'admin', 'admin', NOW() - INTERVAL 40 DAY, 'admin', NOW() - INTERVAL 40 DAY, 0),
(6,  6, 4,    'CR-2024-0004',     'SCOPE',    'assets',          '3 台安全设备', '4 台安全设备', '交行新增1台入侵防御设备', NOW() - INTERVAL 50 DAY, 'admin', 'admin', NOW() - INTERVAL 50 DAY, 'admin', NOW() - INTERVAL 50 DAY, 0),
(7,  4, 5,    'CR-2024-0005',     'SCHEDULE', 'plan_end_date',   DATE_FORMAT(NOW() + INTERVAL 90 DAY, '%Y-%m-%d'), DATE_FORMAT(NOW() + INTERVAL 110 DAY, '%Y-%m-%d'), '招行项目延期20天', NOW() - INTERVAL 20 DAY, 'admin', 'admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 20 DAY, 0),
(8,  4, 5,    'CR-2024-0005',     'COST',     'contract_amount', 3650000.00, 3720000.00, '招行项目追加服务器预算', NOW() - INTERVAL 18 DAY, 'admin', 'admin', NOW() - INTERVAL 18 DAY, 'admin', NOW() - INTERVAL 18 DAY, 0),
(9,  10,6,    'CR-2024-0006',     'SCOPE',    'deliverables',   '4 项交付物', '5 项交付物', '华夏新增备件清单', NOW() - INTERVAL 200 DAY, 'admin', 'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 200 DAY, 0),
(10, 7, 7,    'CR-2024-0007',     'SCHEDULE', 'plan_start_date', DATE_FORMAT(NOW() + INTERVAL 15 DAY, '%Y-%m-%d'), DATE_FORMAT(NOW() - INTERVAL 0 DAY, '%Y-%m-%d'), '邮储项目提前启动', NOW() - INTERVAL 10 DAY, 'admin', 'admin', NOW() - INTERVAL 10 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0);

-- =============================================================
-- 13. pms_asset_allocation（15 条）— 资产分配
-- =============================================================
INSERT INTO `pms_asset_allocation`
    (`id`, `asset_id`, `project_id`, `model_id`, `quantity`, `allocate_time`, `allocate_user_id`, `allocate_user_name`, `status`, `return_time`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  1,  1,  1, NOW() - INTERVAL 190 DAY, 2, '张明', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 90 DAY,  0),
(2,  2,  1,  1,  1, NOW() - INTERVAL 190 DAY, 2, '张明', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 90 DAY,  0),
(3,  3,  1,  2,  1, NOW() - INTERVAL 188 DAY, 2, '张明', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 188 DAY, 'admin', NOW() - INTERVAL 88 DAY,  0),
(4,  4,  1,  3,  1, NOW() - INTERVAL 187 DAY, 2, '张明', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 187 DAY, 'admin', NOW() - INTERVAL 87 DAY,  0),
(5,  5,  2,  2,  1, NOW() - INTERVAL 100 DAY, 3, '刘伟', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 4 DAY,   0),
(6,  6,  2,  4,  1, NOW() - INTERVAL 95 DAY,  3, '刘伟', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 3 DAY,   0),
(7,  7,  3,  3,  1, NOW() - INTERVAL 80 DAY,  4, '赵琳', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 80 DAY,  'admin', NOW() - INTERVAL 2 DAY,   0),
(8,  8,  3,  7,  1, NOW() - INTERVAL 75 DAY,  4, '赵琳', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 75 DAY,  'admin', NOW() - INTERVAL 1 DAY,   0),
(9,  11, 5,  2,  1, NOW() - INTERVAL 160 DAY, 6, '吴婷', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(10, 12, 5,  3,  1, NOW() - INTERVAL 158 DAY, 6, '吴婷', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 158 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(11, 13, 6,  8,  1, NOW() - INTERVAL 90 DAY,  7, '郑昊', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(12, 14, 6,  1,  1, NOW() - INTERVAL 88 DAY,  7, '郑昊', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 88 DAY,  'admin', NOW() - INTERVAL 11 DAY,  0),
(13, 15, 10, 9,  1, NOW() - INTERVAL 270 DAY, 5, '孙磊', 'RETURNED', NOW() - INTERVAL 145 DAY,  'admin', NOW() - INTERVAL 270 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(14, 16, 10, 10, 1, NOW() - INTERVAL 268 DAY, 5, '孙磊', 'RETURNED', NOW() - INTERVAL 145 DAY,  'admin', NOW() - INTERVAL 268 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(15, 9,  4,  5,  1, NOW() - INTERVAL 30 DAY,  5, '孙磊', 'ACTIVE',   NULL,                      'admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 1 DAY,   0);

-- =============================================================
-- 14. pms_asset_lifecycle_log（20 条）— 资产生命周期日志
-- =============================================================
INSERT INTO `pms_asset_lifecycle_log`
    (`id`, `asset_id`, `action_type`, `from_project_id`, `to_project_id`, `operator_id`, `operator_name`, `action_time`, `remarks`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 195 DAY, '设备入库北京中心库',     'admin', NOW() - INTERVAL 195 DAY, 'admin', NOW() - INTERVAL 195 DAY, 0),
(2,  1,  'ALLOCATE', NULL, 1,    2, '张明',  NOW() - INTERVAL 190 DAY, '分配至工行北京项目',     'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 190 DAY, 0),
(3,  2,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 195 DAY, '设备入库北京中心库',     'admin', NOW() - INTERVAL 195 DAY, 'admin', NOW() - INTERVAL 195 DAY, 0),
(4,  2,  'ALLOCATE', NULL, 1,    2, '张明',  NOW() - INTERVAL 190 DAY, '分配至工行北京项目',     'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 190 DAY, 0),
(5,  3,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 193 DAY, '交换机入库北京中心库',   'admin', NOW() - INTERVAL 193 DAY, 'admin', NOW() - INTERVAL 193 DAY, 0),
(6,  3,  'ALLOCATE', NULL, 1,    2, '张明',  NOW() - INTERVAL 188 DAY, '分配至工行北京项目',     'admin', NOW() - INTERVAL 188 DAY, 'admin', NOW() - INTERVAL 188 DAY, 0),
(7,  4,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 192 DAY, '防火墙入库北京中心库',   'admin', NOW() - INTERVAL 192 DAY, 'admin', NOW() - INTERVAL 192 DAY, 0),
(8,  4,  'ALLOCATE', NULL, 1,    2, '张明',  NOW() - INTERVAL 187 DAY, '分配至工行北京项目',     'admin', NOW() - INTERVAL 187 DAY, 'admin', NOW() - INTERVAL 187 DAY, 0),
(9,  5,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 140 DAY, '交换机入库上海中心库',   'admin', NOW() - INTERVAL 140 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(10, 5,  'ALLOCATE', NULL, 2,    3, '刘伟',  NOW() - INTERVAL 100 DAY, '分配至建行上海项目',     'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(11, 6,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 138 DAY, '负载均衡入库上海中心库', 'admin', NOW() - INTERVAL 138 DAY, 'admin', NOW() - INTERVAL 138 DAY, 0),
(12, 6,  'ALLOCATE', NULL, 2,    3, '刘伟',  NOW() - INTERVAL 95 DAY,  '分配至建行上海项目',     'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 95 DAY,  0),
(13, 7,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 110 DAY, '防火墙入库深圳中心库',   'admin', NOW() - INTERVAL 110 DAY, 'admin', NOW() - INTERVAL 110 DAY, 0),
(14, 7,  'ALLOCATE', NULL, 3,    4, '赵琳',  NOW() - INTERVAL 80 DAY,  '分配至农行深圳项目',     'admin', NOW() - INTERVAL 80 DAY,  'admin', NOW() - INTERVAL 80 DAY,  0),
(15, 8,  'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 108 DAY, 'IDS入库深圳中心库',      'admin', NOW() - INTERVAL 108 DAY, 'admin', NOW() - INTERVAL 108 DAY, 0),
(16, 8,  'ALLOCATE', NULL, 3,    4, '赵琳',  NOW() - INTERVAL 75 DAY,  '分配至农行深圳项目',     'admin', NOW() - INTERVAL 75 DAY,  'admin', NOW() - INTERVAL 75 DAY,  0),
(17, 19, 'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 190 DAY, '防火墙入库北京中心库',   'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 190 DAY, 0),
(18, 19, 'TRANSFER', 1,   1,     2, '张明',  NOW() - INTERVAL 60 DAY,  '防火墙故障转入RMA返修',   'admin', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 60 DAY,  0),
(19, 20, 'INBOUND',  NULL, NULL, 1, 'admin', NOW() - INTERVAL 360 DAY, '服务器入库青岛中心库',   'admin', NOW() - INTERVAL 360 DAY, 'admin', NOW() - INTERVAL 360 DAY, 0),
(20, 20, 'SCRAP',    10,  NULL,  5, '孙磊',  NOW() - INTERVAL 150 DAY, '服务器退役报废',         'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0);

-- =============================================================
-- 15. pms_asset_transfer（10 条）— 资产转移
-- =============================================================
INSERT INTO `pms_asset_transfer`
    (`id`, `asset_id`, `from_project_id`, `to_project_id`, `transfer_reason`, `status`,
     `apply_user_id`, `apply_user_name`, `apply_time`, `approve_user_id`, `approve_user_name`, `approve_time`, `approve_opinion`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  19, 1,    NULL,  '防火墙硬件故障，转RMA返修流程',                 'APPROVED', 2, '张明', NOW() - INTERVAL 60 DAY, 1, 'admin', NOW() - INTERVAL 58 DAY, '同意转入RMA返修', 'admin', NOW() - INTERVAL 60 DAY, 'admin', NOW() - INTERVAL 58 DAY, 0),
(2,  17, NULL, 4,    '通用库存交换机调拨至招行广州项目',                'APPROVED', 5, '孙磊', NOW() - INTERVAL 25 DAY,  1, 'admin', NOW() - INTERVAL 23 DAY, '同意调拨',         'admin', NOW() - INTERVAL 25 DAY, 'admin', NOW() - INTERVAL 23 DAY, 0),
(3,  18, NULL, 7,    '通用库存路由器调拨至邮储武汉项目',                'PENDING',  2, '张明', NOW() - INTERVAL 5 DAY,   NULL, NULL,   NULL,                  NULL,              'admin', NOW() - INTERVAL 5 DAY,  'admin', NOW() - INTERVAL 5 DAY,  0),
(4,  9,  4,    2,    '招行项目延期，路由器暂调建行项目使用',             'PENDING',  5, '孙磊', NOW() - INTERVAL 3 DAY,   NULL, NULL,   NULL,                  NULL,              'admin', NOW() - INTERVAL 3 DAY,  'admin', NOW() - INTERVAL 3 DAY,  0),
(5,  10, 4,    2,    '招行项目延期，交换机暂调建行项目使用',             'REJECTED', 5, '孙磊', NOW() - INTERVAL 10 DAY,  1, 'admin', NOW() - INTERVAL 8 DAY,  '该型号与建行项目不兼容，驳回', 'admin', NOW() - INTERVAL 10 DAY, 'admin', NOW() - INTERVAL 8 DAY,  0),
(6,  14, 6,    3,    '交行项目闲置路由器调拨至农行深圳项目',           'APPROVED', 7, '郑昊', NOW() - INTERVAL 20 DAY,  1, 'admin', NOW() - INTERVAL 18 DAY, '同意调拨',         'admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 18 DAY, 0),
(7,  15, 10,   NULL, '华夏青岛项目结束后设备退库',                      'APPROVED', 5, '孙磊', NOW() - INTERVAL 145 DAY, 1, 'admin', NOW() - INTERVAL 143 DAY, '同意退库',         'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 143 DAY, 0),
(8,  16, 10,   NULL, '华夏青岛项目结束后设备退库',                      'APPROVED', 5, '孙磊', NOW() - INTERVAL 145 DAY, 1, 'admin', NOW() - INTERVAL 143 DAY, '同意退库',         'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 143 DAY, 0),
(9,  11, 5,    1,    '中行项目剩余交换机调拨至工行补充',               'PENDING',  6, '吴婷', NOW() - INTERVAL 15 DAY,  NULL, NULL,   NULL,                  NULL,              'admin', NOW() - INTERVAL 15 DAY, 'admin', NOW() - INTERVAL 15 DAY, 0),
(10, 13, 6,    3,    '交行入侵防御设备调拨至农行项目扩容',             'APPROVED', 7, '郑昊', NOW() - INTERVAL 30 DAY,  1, 'admin', NOW() - INTERVAL 28 DAY, '同意调拨扩容',     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 28 DAY, 0);

-- =============================================================
-- 16. pms_change_request（10 条）— 变更请求（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_change_request`
    (`id`, `cr_no`, `project_id`, `project_name`, `title`, `description`, `requester_id`, `requester_name`, `request_date`,
     `impact_scope`, `impact_schedule`, `impact_cost`, `impact_quality`, `priority`, `status`,
     `approver_id`, `approver_name`, `process_instance_id`, `baseline_updated`, `approved_at`, `closed_at`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1, 'CR-2024-0001', 2, '中国建设银行上海数据中心网络升级项目', '建行项目延期及追加负载均衡预算',     '因客户机房改造延期，需追加1台F5负载均衡设备，合同金额增加12万元，工期延长15天。', 3, '刘伟', NOW() - INTERVAL 62 DAY,
     '建行上海数据中心核心区', '工期延长15天', '合同金额增加120000元', '不影响交付质量', 'HIGH', 'CLOSED',
     1, 'admin', 'pi-cr-2024-0001', 1, NOW() - INTERVAL 58 DAY, NOW() - INTERVAL 50 DAY,
     'admin', NOW() - INTERVAL 62 DAY, 'admin', NOW() - INTERVAL 50 DAY, 0, 0),
(2, 'CR-2024-0002', 3, '中国农业银行深圳分行网络安全加固项目', '农行新增入侵检测设备',             '为加强安全监测能力，新增2台DPtech IPS-2000入侵检测设备，不影响工期。',             4, '赵琳', NOW() - INTERVAL 72 DAY,
     '农行深圳分行安全区', '不影响工期', '合同金额增加116000元', '提升安全监测能力', 'MEDIUM', 'CLOSED',
     1, 'admin', 'pi-cr-2024-0002', 1, NOW() - INTERVAL 70 DAY, NOW() - INTERVAL 65 DAY,
     'admin', NOW() - INTERVAL 72 DAY, 'admin', NOW() - INTERVAL 65 DAY, 0, 0),
(3, 'CR-2024-0003', 5, '中国银行杭州分行网络设备更新项目',     '中行项目终验延期',                 '因客户验收人员出差，终验申请延期10天。',                                         6, '吴婷', NOW() - INTERVAL 42 DAY,
     '中行杭州分行', '终验延期10天', '不增加成本', '不影响质量', 'LOW', 'CLOSED',
     1, 'admin', 'pi-cr-2024-0003', 1, NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 35 DAY,
     'admin', NOW() - INTERVAL 42 DAY, 'admin', NOW() - INTERVAL 35 DAY, 0, 0),
(4, 'CR-2024-0004', 6, '交通银行成都分行网络安全扩容项目',     '交行新增入侵防御设备',             '为满足等保三级要求，新增1台Cisco Firepower 4112入侵防御设备。',                    7, '郑昊', NOW() - INTERVAL 52 DAY,
     '交行成都分行安全区', '不影响工期', '合同金额增加105000元', '提升合规能力', 'HIGH', 'CLOSED',
     1, 'admin', 'pi-cr-2024-0004', 1, NOW() - INTERVAL 50 DAY, NOW() - INTERVAL 45 DAY,
     'admin', NOW() - INTERVAL 52 DAY, 'admin', NOW() - INTERVAL 45 DAY, 0, 0),
(5, 'CR-2024-0005', 4, '招商银行广州分行数据中心建设项目',     '招行项目延期及追加服务器预算',     '因设备到货延迟及客户需求调整，工期延长20天，追加7万元服务器预算。',               5, '孙磊', NOW() - INTERVAL 22 DAY,
     '招行广州数据中心', '工期延长20天', '合同金额增加70000元', '不影响质量', 'HIGH', 'IMPLEMENTING',
     1, 'admin', 'pi-cr-2024-0005', 1, NOW() - INTERVAL 18 DAY, NULL,
     'admin', NOW() - INTERVAL 22 DAY, 'admin', NOW() - INTERVAL 10 DAY, 0, 0),
(6, 'CR-2024-0006', 10,'华夏银行青岛分行安全设备部署项目',     '华夏新增备件清单交付物',           '应客户要求，新增备件清单作为项目交付物之一。',                                   5, '孙磊', NOW() - INTERVAL 210 DAY,
     '华夏青岛分行', '不影响工期', '不增加成本', '提升运维便利性', 'LOW', 'CLOSED',
     1, 'admin', 'pi-cr-2024-0006', 1, NOW() - INTERVAL 205 DAY, NOW() - INTERVAL 200 DAY,
     'admin', NOW() - INTERVAL 210 DAY, 'admin', NOW() - INTERVAL 200 DAY, 0, 0),
(7, 'CR-2024-0007', 7, '中国邮政储蓄银行武汉分行网络改造项目', '邮储项目提前启动',                 '因客户业务需要，项目启动时间提前15天。',                                         2, '张明', NOW() - INTERVAL 12 DAY,
     '邮储武汉分行', '提前启动15天', '不增加成本', '不影响质量', 'MEDIUM', 'UNDER_REVIEW',
     NULL, NULL, 'pi-cr-2024-0007', 0, NULL, NULL,
     'admin', NOW() - INTERVAL 12 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0, 0),
(8, 'CR-2024-0008', 1, '中国工商银行北京分行核心网络改造项目', '工行新增运维交接文档',             '项目终验前补充运维交接文档作为交付物。',                                         2, '张明', NOW() - INTERVAL 160 DAY,
     '工行北京分行', '不影响工期', '不增加成本', '提升运维质量', 'LOW', 'CLOSED',
     1, 'admin', 'pi-cr-2024-0008', 1, NOW() - INTERVAL 158 DAY, NOW() - INTERVAL 150 DAY,
     'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 150 DAY, 0, 0),
(9, 'CR-2024-0009', 8, '中信银行南京分行数据中心迁移项目',     '中信项目范围调整',                 '数据中心迁移范围新增一个机房，需评估工期与成本影响。',                           3, '刘伟', NOW() - INTERVAL 5 DAY,
     '中信南京数据中心', '待评估', '待评估', '待评估', 'CRITICAL', 'SUBMITTED',
     NULL, NULL, NULL, 0, NULL, NULL,
     'admin', NOW() - INTERVAL 5 DAY, 'admin', NOW() - INTERVAL 5 DAY, 0, 0),
(10,'CR-2024-0010', 9, '民生银行西安分行网络升级项目',         '民生项目范围缩减',                 '因预算调整，项目范围缩减，部分分支网点暂不纳入本次改造。',                       4, '赵琳', NOW() - INTERVAL 45 DAY,
     '民生西安分行', '不影响工期', '合同金额减少150000元', '不影响已交付部分质量', 'MEDIUM', 'CCB_REJECTED',
     1, 'admin', 'pi-cr-2024-0010', 0, NULL, NULL,
     'admin', NOW() - INTERVAL 45 DAY, 'admin', NOW() - INTERVAL 40 DAY, 0, 0);

-- =============================================================
-- 17. pms_issue（10 条）— 问题日志（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_issue`
    (`id`, `issue_no`, `project_id`, `description`, `raised_by`, `raised_by_name`, `assignee_id`, `assignee_name`,
     `priority`, `target_resolve_date`, `status`, `source_risk_id`, `source_risk_no`, `source_change_id`, `source_cr_no`,
     `resolved_at`, `closed_at`, `resolution`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1, 'ISSUE-2024-0001', 1,  '工行核心路由器与现有设备OSPF邻居无法建立',           2, '张明', 7, '工程师甲', 'HIGH',     NOW() - INTERVAL 185 DAY, 'RESOLVED', NULL, NULL, NULL, NULL, NOW() - INTERVAL 183 DAY, NOW() - INTERVAL 180 DAY, '调整OSPF区域配置，恢复邻居关系', 'admin', NOW() - INTERVAL 185 DAY, 'admin', NOW() - INTERVAL 180 DAY, 0),
(2, 'ISSUE-2024-0002', 1,  '工行防火墙策略与业务系统冲突，部分交易延迟',           2, '张明', 8, '工程师乙', 'CRITICAL', NOW() - INTERVAL 140 DAY, 'CLOSED',  NULL, NULL, NULL, NULL, NOW() - INTERVAL 138 DAY, NOW() - INTERVAL 135 DAY, '优化防火墙策略，开放必要端口',   'admin', NOW() - INTERVAL 140 DAY, 'admin', NOW() - INTERVAL 135 DAY, 0),
(3, 'ISSUE-2024-0003', 2,  '建行核心交换机IRF堆叠分裂',                            3, '刘伟', 7, '工程师甲', 'HIGH',     NOW() - INTERVAL 80 DAY,  'RESOLVED', NULL, NULL, NULL, NULL, NOW() - INTERVAL 78 DAY,  NULL,                      '更换堆叠线缆，恢复IRF',         'admin', NOW() - INTERVAL 80 DAY,  'admin', NOW() - INTERVAL 78 DAY,  0),
(4, 'ISSUE-2024-0004', 2,  '建行F5负载均衡健康检查误报',                          3, '刘伟', 8, '工程师乙', 'MEDIUM',   NOW() - INTERVAL 40 DAY,  'IN_PROGRESS', NULL, NULL, NULL, NULL, NULL,                    NULL,                      NULL,                              'admin', NOW() - INTERVAL 40 DAY,  'admin', NOW() - INTERVAL 5 DAY,   0),
(5, 'ISSUE-2024-0005', 3,  '农行入侵检测系统误报率高',                             4, '赵琳', 8, '工程师乙', 'MEDIUM',   NOW() - INTERVAL 50 DAY,  'OPEN',     NULL, NULL, NULL, NULL, NULL,                    NULL,                      NULL,                              'admin', NOW() - INTERVAL 50 DAY,  'admin', NOW() - INTERVAL 50 DAY,  0),
(6, 'ISSUE-2024-0006', 5,  '中行国产化设备命令行兼容性问题',                       6, '吴婷', 7, '工程师甲', 'HIGH',     NOW() - INTERVAL 100 DAY, 'RESOLVED', NULL, NULL, NULL, NULL, NOW() - INTERVAL 95 DAY,  NOW() - INTERVAL 90 DAY,  '编写兼容命令映射表，完成迁移', 'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 90 DAY,  0),
(7, 'ISSUE-2024-0007', 6,  '交行UAT测试客户配合阻塞',                             7, '郑昊', 9, 'QA工程师', 'HIGH',     NOW() - INTERVAL 5 DAY,   'IN_PROGRESS', NULL, NULL, NULL, NULL, NULL,                    NULL,                      NULL,                              'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 1 DAY,   0),
(8, 'ISSUE-2024-0008', 4,  '招行设备到货延迟影响工期',                             5, '孙磊', 7, '工程师甲', 'HIGH',     NOW() - INTERVAL 30 DAY,  'RESOLVED', NULL, NULL, 5,    'CR-2024-0005', NOW() - INTERVAL 22 DAY, NULL,                      '通过变更请求延长工期并追加预算','admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 22 DAY,  0),
(9, 'ISSUE-2024-0009', 10, '华夏青岛服务器硬件故障',                               5, '孙磊', 8, '工程师乙', 'CRITICAL', NOW() - INTERVAL 200 DAY, 'CLOSED',  NULL, NULL, NULL, NULL, NOW() - INTERVAL 195 DAY, NOW() - INTERVAL 190 DAY, '更换服务器硬盘并恢复数据',     'admin', NOW() - INTERVAL 200 DAY, 'admin', NOW() - INTERVAL 190 DAY, 0),
(10,'ISSUE-2024-0010', 7,  '邮储分支网点设备配置不一致',                           2, '张明', 7, '工程师甲', 'LOW',      NOW() - INTERVAL 10 DAY,  'OPEN',     NULL, NULL, NULL, NULL, NULL,                    NULL,                      NULL,                              'admin', NOW() - INTERVAL 10 DAY,  'admin', NOW() - INTERVAL 10 DAY,  0);

-- =============================================================
-- 18. pms_risk（10 条）— 风险登记册（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_risk`
    (`id`, `risk_no`, `project_id`, `description`, `category`, `likelihood`, `impact`, `score`, `priority`, `mitigation`, `contingency_plan`, `owner_id`, `owner_name`, `status`, `review_date`, `source_issue_id`, `identified_at`, `closed_at`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1, 'RISK-2024-0001', 1,  '工行核心路由器割接导致业务中断风险',           'TECHNICAL',     3, 5, 15, 'HIGH',     'MITIGATE', '准备回退方案，割接窗口选择业务低峰期', 2, '张明', 'CLOSED',      NOW() - INTERVAL 150 DAY, NULL,    NOW() - INTERVAL 195 DAY, NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 195 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(2, 'RISK-2024-0002', 2,  '建行数据中心机房改造延期风险',                   'EXTERNAL',      4, 4, 16, 'HIGH',     'MITIGATE', '与客户确认机房交付时间，准备备选方案', 3, '刘伟', 'IN_PROGRESS', NOW() + INTERVAL 15 DAY,  NULL,    NOW() - INTERVAL 145 DAY, NULL,                      'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 5 DAY,   0),
(3, 'RISK-2024-0003', 3,  '农行安全设备与现有网络兼容性风险',               'TECHNICAL',     3, 4, 12, 'MEDIUM',   'MITIGATE', '提前进行PoC测试，准备兼容方案',         4, '赵琳', 'CLOSED',      NOW() - INTERVAL 60 DAY,  NULL,    NOW() - INTERVAL 115 DAY, NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 115 DAY, 'admin', NOW() - INTERVAL 60 DAY,  0),
(4, 'RISK-2024-0004', 4,  '招行设备供应链交付延迟风险',                     'EXTERNAL',      4, 5, 20, 'HIGH',     'MITIGATE', '提前下单，与供应商签订交付SLA',         5, '孙磊', 'CLOSED',      NOW() - INTERVAL 20 DAY,  8,      NOW() - INTERVAL 85 DAY,  NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 85 DAY,  'admin', NOW() - INTERVAL 20 DAY,  0),
(5, 'RISK-2024-0005', 5,  '中行国产化设备技术支持能力不足风险',             'ORGANIZATIONAL',2, 4, 8,  'MEDIUM',   'MITIGATE', '加强原厂技术培训，建立专家支持通道',     6, '吴婷', 'IN_PROGRESS', NOW() + INTERVAL 30 DAY,  NULL,    NOW() - INTERVAL 170 DAY, NULL,                      'admin', NOW() - INTERVAL 170 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(6, 'RISK-2024-0006', 6,  '交行等保三级整改进度滞后风险',                   'PM',            3, 4, 12, 'MEDIUM',   'MITIGATE', '增加人力投入，与合规部门保持沟通',      7, '郑昊', 'OPEN',        NOW() + INTERVAL 10 DAY,  NULL,    NOW() - INTERVAL 100 DAY, NULL,                      'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(7, 'RISK-2024-0007', 7,  '邮储分支网点数量多，部署周期可能超期',           'PM',            3, 3, 9,  'MEDIUM',   'MITIGATE', '分批部署，优先核心网点',                2, '张明', 'OPEN',        NOW() + INTERVAL 60 DAY,  NULL,    NOW() - INTERVAL 25 DAY,  NULL,                      'admin', NOW() - INTERVAL 25 DAY,  'admin', NOW() - INTERVAL 25 DAY,  0),
(8, 'RISK-2024-0008', 8,  '中信数据中心迁移期间数据丢失风险',               'TECHNICAL',     2, 5, 10, 'HIGH',     'AVOID',   '迁移前完整备份，迁移后校验数据完整性', 3, '刘伟', 'OPEN',        NOW() + INTERVAL 90 DAY,  NULL,    NOW() - INTERVAL 8 DAY,   NULL,                      'admin', NOW() - INTERVAL 8 DAY,   'admin', NOW() - INTERVAL 8 DAY,   0),
(9, 'RISK-2024-0009', 10, '华夏青岛运维交接不彻底导致故障响应慢',           'ORGANIZATIONAL',2, 3, 6,  'LOW',      'ACCEPT',  '交接清单逐项确认，保留原厂支持窗口',    5, '孙磊', 'CLOSED',      NOW() - INTERVAL 140 DAY, NULL,    NOW() - INTERVAL 290 DAY, NOW() - INTERVAL 140 DAY, 'admin', NOW() - INTERVAL 290 DAY, 'admin', NOW() - INTERVAL 140 DAY, 0),
(10,'RISK-2024-0010', 2,  '建行负载均衡设备选型变更风险',                   'TECHNICAL',     2, 3, 6,  'LOW',      'TRANSFER', '选型变更由原厂承担技术责任',            3, '刘伟', 'ESCALATED',   NOW() + INTERVAL 5 DAY,   NULL,    NOW() - INTERVAL 60 DAY,  NULL,                      'admin', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 3 DAY,   0);

-- =============================================================
-- 19. pms_punch_list（10 条）— 消缺清单（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_punch_list`
    (`id`, `project_id`, `milestone_id`, `severity`, `title`, `description`, `walkdown_stage`, `assignee_id`, `assignee_name`, `deadline`, `status`, `resolved_at`, `verified_at`, `verified_by`, `verified_by_name`, `attachment_ids`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1, 1, 6, 'FUNCTIONAL', '工行核心路由器OSPF区域配置未优化',   '现场验收发现OSPF区域0路由数量过多，需优化区域划分',         'FORMAL',   7, '工程师甲', NOW() - INTERVAL 102 DAY, 'VERIFIED', NOW() - INTERVAL 100 DAY, NOW() - INTERVAL 98 DAY, 9, 'QA工程师', NULL, 'admin', NOW() - INTERVAL 105 DAY, 'admin', NOW() - INTERVAL 98 DAY,  0),
(2, 1, 6, 'COSMETIC',    '工行机房标签不规范',                 '部分设备标签未按规范粘贴，需重新制作',                     'PRE_PUNCH', 8, '工程师乙', NOW() - INTERVAL 102 DAY, 'RESOLVED', NOW() - INTERVAL 99 DAY,  NULL,                    NULL, NULL,    NULL, 'admin', NOW() - INTERVAL 105 DAY, 'admin', NOW() - INTERVAL 99 DAY,  0),
(3, 1, 7, 'FUNCTIONAL', '工行防火墙日志未集中收集',           '防火墙日志未接入SOC，需配置syslog转发',                    'FORMAL',   8, '工程师乙', NOW() - INTERVAL 97 DAY,  'VERIFIED', NOW() - INTERVAL 96 DAY,  NOW() - INTERVAL 95 DAY,  9, 'QA工程师', NULL, 'admin', NOW() - INTERVAL 98 DAY,  'admin', NOW() - INTERVAL 95 DAY,  0),
(4, 2, 11,'SAFETY',     '建行设备上架未做防倾倒固定',         '机柜内交换机未安装防倾倒支架，存在安全隐患',                 'PRE_PUNCH', 7, '工程师甲', NOW() - INTERVAL 55 DAY,  'OPEN',     NULL,                    NULL,                    NULL, NULL,    NULL, 'admin', NOW() - INTERVAL 58 DAY,  'admin', NOW() - INTERVAL 58 DAY,  0),
(5, 2, 11,'FUNCTIONAL', '建行IRF堆叠线缆冗余不足',           'IRF堆叠仅使用单链路，建议增加冗余链路',                     'FORMAL',   7, '工程师甲', NOW() - INTERVAL 55 DAY,  'IN_PROGRESS', NOW() - INTERVAL 10 DAY, NULL,                  NULL, NULL,    NULL, 'admin', NOW() - INTERVAL 58 DAY,  'admin', NOW() - INTERVAL 10 DAY,  0),
(6, 5, 16,'COSMETIC',    '中行设备序列号清单与实物不符',       '资产清单中2台设备序列号登记错误，需核对更正',               'FORMAL',   8, '工程师乙', NOW() - INTERVAL 23 DAY,  'OPEN',     NULL,                    NULL,                    NULL, NULL,    NULL, 'admin', NOW() - INTERVAL 24 DAY,  'admin', NOW() - INTERVAL 24 DAY,  0),
(7, 6, 17,'FUNCTIONAL', '交行入侵防御策略未覆盖全部子网',     'IPS策略未覆盖新增分支机构子网，需补充策略',                 'FORMAL',   8, '工程师乙', NOW() - INTERVAL 10 DAY,  'OPEN',     NULL,                    NULL,                    NULL, NULL,    NULL, 'admin', NOW() - INTERVAL 11 DAY,  'admin', NOW() - INTERVAL 11 DAY,  0),
(8, 10,7, 'FUNCTIONAL', '华夏青岛防火墙规则冗余',             '防火墙存在20条冗余规则，需清理优化',                       'PRE_PUNCH', 8, '工程师乙', NOW() - INTERVAL 148 DAY, 'VERIFIED', NOW() - INTERVAL 146 DAY, NOW() - INTERVAL 145 DAY, 9, 'QA工程师', NULL, 'admin', NOW() - INTERVAL 150 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(9, 3, 15,'FUNCTIONAL', '农行入侵检测特征库版本过旧',         'IPS特征库版本落后，需更新至最新版本',                       'FORMAL',   8, '工程师乙', NOW() - INTERVAL 30 DAY,  'RESOLVED', NOW() - INTERVAL 5 DAY,  NULL,                    NULL, NULL,    NULL, 'admin', NOW() - INTERVAL 75 DAY,  'admin', NOW() - INTERVAL 5 DAY,   0),
(10,4, 19,'COSMETIC',    '招行设备到货外包装破损',             '2台设备外包装在运输中破损，需确认设备完好',                 'PRE_PUNCH', 7, '工程师甲', NOW() - INTERVAL 50 DAY,  'VERIFIED', NOW() - INTERVAL 48 DAY,  NOW() - INTERVAL 47 DAY,  9, 'QA工程师', NULL, 'admin', NOW() - INTERVAL 52 DAY,  'admin', NOW() - INTERVAL 47 DAY,  0);

-- =============================================================
-- 20. pms_impl_task（15 条）— 实施任务（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_impl_task`
    (`id`, `project_id`, `milestone_id`, `task_name`, `task_type`, `agent_id`, `engineer_id`, `engineer_name`,
     `plan_start_date`, `plan_end_date`, `actual_start_date`, `actual_end_date`, `status`, `progress`, `work_description`,
     `accept_opinion`, `accept_user_id`, `accept_user_name`, `accept_time`,
     `customer_contact`, `service_address`, `service_type`, `sop_steps`, `material_list`, `planned_hours`, `skill_level`, `safety_ppe`, `evidence_checkpoints`, `sign_off_required`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1,  1, 4, '工行核心路由器安装实施',   'OEM',   NULL,2,  '张明',     NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 155 DAY, NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 155 DAY, 'COMPLETED', 100, '路由器上架、加电、基础配置', '验收通过', 1, 'admin', NOW() - INTERVAL 100 DAY, '王志强', '工行北京分行核心机房',       'INSTALL',    '1.上架 2.加电 3.基础配置',     '路由器1台,线缆若干', 8,  'SENIOR', 'PPE',     '1.安装照片 2.配置截图', 1, 'admin', NOW() - INTERVAL 162 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0, 0),
(2,  1, 4, '工行交换机安装实施',       'OEM',   NULL,7,  '工程师甲', NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 155 DAY, NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 155 DAY, 'COMPLETED', 100, '交换机上架与VLAN配置',         '验收通过', 1, 'admin', NOW() - INTERVAL 100 DAY, '王志强', '工行北京分行接入机房',       'INSTALL',    '1.上架 2.加电 3.VLAN配置',     '交换机1台',          6,  'SENIOR', 'PPE',     '1.安装照片 2.配置截图', 1, 'admin', NOW() - INTERVAL 162 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0, 0),
(3,  1, 4, '工行防火墙部署实施',       'AGENT', 1,   NULL,NULL,       NOW() - INTERVAL 158 DAY, NOW() - INTERVAL 150 DAY, NOW() - INTERVAL 158 DAY, NOW() - INTERVAL 148 DAY, 'COMPLETED', 100, '防火墙上架与安全策略配置',     '验收通过', 1, 'admin', NOW() - INTERVAL 95 DAY,  '王志强', '工行北京分行安全边界',       'INSTALL',    '1.上架 2.加电 3.策略配置',     '防火墙1台',          10, 'EXPERT', 'PPE',     '1.安装照片 2.策略截图', 1, 'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0, 0),
(4,  2, 11,'建行核心交换机安装',       'AGENT', 2,   NULL,NULL,       NOW() - INTERVAL 65 DAY,  NOW() - INTERVAL 55 DAY,  NOW() - INTERVAL 65 DAY,  NULL,                       'IN_PROGRESS',80, '交换机上架与IRF堆叠配置',     NULL,        NULL, NULL,   NULL,                  '李建国', '建行上海数据中心核心机房',   'INSTALL',    '1.上架 2.IRF 3.配置',          '交换机1台',          12, 'EXPERT', 'PPE',     '1.安装照片 2.IRF状态',   1, 'admin', NOW() - INTERVAL 70 DAY,  'admin', NOW() - INTERVAL 5 DAY,   0, 0),
(5,  2, 11,'建行负载均衡部署',         'AGENT', 1,   NULL,NULL,       NOW() - INTERVAL 50 DAY,  NOW() - INTERVAL 40 DAY,  NOW() - INTERVAL 50 DAY,  NULL,                       'IN_PROGRESS',60, 'F5负载均衡上架与pool配置',     NULL,        NULL, NULL,   NULL,                  '李建国', '建行上海数据中心负载均衡区', 'INSTALL',    '1.上架 2.pool 3.monitor',      '负载均衡1台',        10, 'EXPERT', 'PPE',     '1.安装照片 2.pool截图', 1, 'admin', NOW() - INTERVAL 55 DAY,  'admin', NOW() - INTERVAL 5 DAY,   0, 0),
(6,  3, 15,'农行防火墙部署',           'AGENT', 6,   NULL,NULL,       NOW() - INTERVAL 45 DAY,  NOW() - INTERVAL 35 DAY,  NOW() - INTERVAL 45 DAY,  NOW() - INTERVAL 35 DAY,  'COMPLETED', 100, '防火墙部署与策略配置',         '验收通过', 4, '赵琳', NOW() - INTERVAL 30 DAY,    '陈志远', '农行深圳分行机房',           'INSTALL',    '1.上架 2.策略 3.联调',          '防火墙1台',          8,  'SENIOR', 'PPE',     '1.安装照片 2.策略截图', 1, 'admin', NOW() - INTERVAL 50 DAY,  'admin', NOW() - INTERVAL 30 DAY,  0, 0),
(7,  3, 15,'农行入侵检测部署',         'AGENT', 6,   NULL,NULL,       NOW() - INTERVAL 40 DAY,  NOW() - INTERVAL 30 DAY,  NOW() - INTERVAL 40 DAY,  NULL,                       'IN_PROGRESS',70, 'IDS部署与特征库更新',           NULL,        NULL, NULL,   NULL,                  '陈志远', '农行深圳分行安全区',         'INSTALL',    '1.上架 2.特征库 3.联调',        'IDS 1台',            8,  'SENIOR', 'PPE',     '1.安装照片 2.版本截图', 1, 'admin', NOW() - INTERVAL 45 DAY,  'admin', NOW() - INTERVAL 3 DAY,   0, 0),
(8,  5, 16,'中行设备国产化替换',       'OEM',   NULL,7,  '工程师甲', NOW() - INTERVAL 165 DAY, NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 165 DAY, NOW() - INTERVAL 158 DAY, 'COMPLETED', 100, '国产化设备替换与配置迁移',     '验收通过', 6, '吴婷', NOW() - INTERVAL 25 DAY,    '周文博', '中行杭州分行机房',           'INSTALL',    '1.替换 2.配置迁移 3.联调',      '交换机1台,防火墙1台',10, 'EXPERT', 'PPE',     '1.替换照片 2.配置截图', 1, 'admin', NOW() - INTERVAL 170 DAY, 'admin', NOW() - INTERVAL 25 DAY,  0, 0),
(9,  6, 17,'交行入侵防御部署',         'AGENT', 3,   NULL,NULL,       NOW() - INTERVAL 30 DAY,  NOW() - INTERVAL 20 DAY,  NOW() - INTERVAL 30 DAY,  NOW() - INTERVAL 20 DAY,  'COMPLETED', 100, 'IPS部署与策略配置',             '验收通过', 7, '郑昊', NOW() - INTERVAL 12 DAY,    '徐建华', '交行成都分行安全区',         'INSTALL',    '1.上架 2.策略 3.联调',          'IPS 1台',            8,  'SENIOR', 'PPE',     '1.安装照片 2.策略截图', 1, 'admin', NOW() - INTERVAL 35 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0, 0),
(10, 6, 18,'交行分支路由器部署',       'OEM',   NULL,7,  '工程师甲', NOW() - INTERVAL 25 DAY,  NOW() - INTERVAL 15 DAY,  NOW() - INTERVAL 25 DAY,  NOW() - INTERVAL 15 DAY,  'CONFIRMED', 100, '分支路由器部署与配置',         '验收通过', 7, '郑昊', NOW() - INTERVAL 12 DAY,    '徐建华', '交行成都分行分支机房',       'INSTALL',    '1.上架 2.配置 3.联调',          '路由器1台',          6,  'SENIOR', 'PPE',     '1.安装照片 2.配置截图', 1, 'admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0, 0),
(11, 4, 19,'招行设备现场勘查',         'AGENT', 5,   NULL,NULL,       NOW() - INTERVAL 80 DAY,  NOW() - INTERVAL 78 DAY,  NOW() - INTERVAL 80 DAY,  NOW() - INTERVAL 78 DAY,  'COMPLETED', 100, '招行数据中心机房勘查',         '验收通过', 5, '孙磊', NOW() - INTERVAL 75 DAY,    '黄晓东', '招行广州分行数据中心',       'SITE_SURVEY','1.机房勘测 2.供电 3.网络',     '无',                 4,  'JUNIOR', 'PPE',     '1.勘查照片 2.勘测报告', 1, 'admin', NOW() - INTERVAL 85 DAY,  'admin', NOW() - INTERVAL 75 DAY,  0, 0),
(12, 4, 20,'招行设备FAT',             'OEM',   NULL,8,  '工程师乙', NOW() - INTERVAL 30 DAY,  NOW() - INTERVAL 25 DAY,  NOW() - INTERVAL 30 DAY,  NOW() - INTERVAL 25 DAY,  'COMPLETED', 100, '工厂验收测试',                 '验收通过', 5, '孙磊', NOW() - INTERVAL 22 DAY,    '黄晓东', '原厂FAT测试中心',           'DEBUG',      '1.加电 2.FAT 3.报告',           '测试设备若干',        8,  'SENIOR', 'PPE',     '1.FAT照片 2.FAT报告',   1, 'admin', NOW() - INTERVAL 35 DAY,  'admin', NOW() - INTERVAL 22 DAY,  0, 0),
(13, 7, NULL,'邮储网络设计勘察',        'OEM',   NULL,2,  '张明',     NOW() + INTERVAL 0 DAY,   NOW() + INTERVAL 5 DAY,   NULL,                    NULL,                       'PENDING',   0,   '邮储核心网勘察与设计',         NULL,        NULL, NULL,   NULL,                  '胡军',   '邮储武汉分行',             'SITE_SURVEY','1.勘察 2.设计',                '无',                 6,  'SENIOR', 'PPE',     NULL,                    1, 'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0, 0),
(14, 8, NULL,'中信数据中心迁移规划',    'OEM',   NULL,3,  '刘伟',     NOW() + INTERVAL 15 DAY,  NOW() + INTERVAL 30 DAY,   NULL,                    NULL,                       'PENDING',   0,   '数据中心迁移方案规划',         NULL,        NULL, NULL,   NULL,                  '朱国良', '中信南京分行',             'SITE_SURVEY','1.现状评估 2.迁移方案',        '无',                 10, 'EXPERT', 'PPE',     NULL,                    1, 'admin', NOW() - INTERVAL 8 DAY,   'admin', NOW() - INTERVAL 8 DAY,   0, 0),
(15, 10,7, '华夏青岛安全设备部署',     'AGENT', 9,   NULL,NULL,       NOW() - INTERVAL 295 DAY, NOW() - INTERVAL 280 DAY, NOW() - INTERVAL 295 DAY, NOW() - INTERVAL 278 DAY, 'COMPLETED', 100, '安全设备部署与联调',           '验收通过', 5, '孙磊', NOW() - INTERVAL 145 DAY,   '罗建明', '华夏青岛分行机房',           'INSTALL',    '1.上架 2.策略 3.联调',          '防火墙2台',          12, 'EXPERT', 'PPE',     '1.安装照片 2.策略截图', 1, 'admin', NOW() - INTERVAL 300 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0, 0);

-- =============================================================
-- 21. pms_impl_progress（15 条）— 实施进度日志
-- =============================================================
INSERT INTO `pms_impl_progress`
    (`id`, `task_id`, `progress_percent`, `work_log`, `photo_urls`, `report_user_id`, `report_user_name`, `report_time`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  30, '路由器到货，完成开箱检查',           '/upload/2024/impl/p1-30.jpg', 7, '工程师甲', NOW() - INTERVAL 159 DAY, 'admin', NOW() - INTERVAL 159 DAY, 'admin', NOW() - INTERVAL 159 DAY, 0),
(2,  1,  80, '路由器上架完成，开始基础配置',         '/upload/2024/impl/p1-80.jpg', 7, '工程师甲', NOW() - INTERVAL 157 DAY, 'admin', NOW() - INTERVAL 157 DAY, 'admin', NOW() - INTERVAL 157 DAY, 0),
(3,  1,  100,'路由器基础配置完成，联调通过',         '/upload/2024/impl/p1-100.jpg',2, '张明',     NOW() - INTERVAL 155 DAY, 'admin', NOW() - INTERVAL 155 DAY, 'admin', NOW() - INTERVAL 155 DAY, 0),
(4,  3,  40, '防火墙上架完成，开始策略配置',         '/upload/2024/impl/p3-40.jpg', 1, 'admin',    NOW() - INTERVAL 155 DAY, 'admin', NOW() - INTERVAL 155 DAY, 'admin', NOW() - INTERVAL 155 DAY, 0),
(5,  3,  100,'防火墙策略配置完成，联调通过',         '/upload/2024/impl/p3-100.jpg',1, 'admin',    NOW() - INTERVAL 148 DAY, 'admin', NOW() - INTERVAL 148 DAY, 'admin', NOW() - INTERVAL 148 DAY, 0),
(6,  4,  50, '建行核心交换机上架完成',               '/upload/2024/impl/p4-50.jpg', 7, '工程师甲', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 60 DAY,  0),
(7,  4,  80, 'IRF堆叠配置完成，进行测试',             '/upload/2024/impl/p4-80.jpg', 7, '工程师甲', NOW() - INTERVAL 10 DAY,  'admin', NOW() - INTERVAL 10 DAY,  'admin', NOW() - INTERVAL 10 DAY,  0),
(8,  5,  60, 'F5负载均衡上架与pool配置',             '/upload/2024/impl/p5-60.jpg', 1, 'admin',    NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 20 DAY,  0),
(9,  6,  100,'农行防火墙部署完成',                   '/upload/2024/impl/p6-100.jpg',6, 'admin',    NOW() - INTERVAL 35 DAY,  'admin', NOW() - INTERVAL 35 DAY,  'admin', NOW() - INTERVAL 35 DAY,  0),
(10, 7,  70, '农行IDS部署完成，特征库更新中',         '/upload/2024/impl/p7-70.jpg', 6, 'admin',   NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0),
(11, 8,  100,'中行国产化设备替换完成',               '/upload/2024/impl/p8-100.jpg',7, '工程师甲', NOW() - INTERVAL 158 DAY, 'admin', NOW() - INTERVAL 158 DAY, 'admin', NOW() - INTERVAL 158 DAY, 0),
(12, 9,  100,'交行IPS部署完成',                     '/upload/2024/impl/p9-100.jpg', 3, 'admin',   NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 20 DAY,  0),
(13, 11, 100,'招行设备现场勘查完成',                 '/upload/2024/impl/p11-100.jpg',5,'admin',   NOW() - INTERVAL 78 DAY,  'admin', NOW() - INTERVAL 78 DAY,  'admin', NOW() - INTERVAL 78 DAY,  0),
(14, 13, 0,   '邮储网络设计勘察任务创建，待启动',     NULL,                         2, '张明',     NOW() - INTERVAL 1 DAY,   'admin', NOW() - INTERVAL 1 DAY,   'admin', NOW() - INTERVAL 1 DAY,   0),
(15, 15, 100,'华夏青岛安全设备部署完成',             '/upload/2024/impl/p15-100.jpg',9,'admin',   NOW() - INTERVAL 278 DAY, 'admin', NOW() - INTERVAL 278 DAY, 'admin', NOW() - INTERVAL 278 DAY, 0);

-- =============================================================
-- 22. pms_agent_score（10 条）— 服务商评分
-- =============================================================
INSERT INTO `pms_agent_score`
    (`id`, `agent_id`, `task_id`, `response_speed_score`, `construction_quality_score`, `document_completeness_score`, `overall_score`, `comment`, `evaluator_id`, `evaluator_name`, `evaluate_time`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1, 1, 3,  9, 9, 8, 8.7, '工行防火墙部署响应及时，施工规范，文档齐全',         1, 'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 95 DAY,  0),
(2, 2, 4,  8, 9, 8, 8.3, '建行核心交换机部署质量高，响应略慢',                 3, '刘伟',  NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0),
(3, 1, 5,  9, 8, 7, 8.0, '建行负载均衡部署完成，文档待补充',                   3, '刘伟',  NOW() - INTERVAL 4 DAY,   'admin', NOW() - INTERVAL 4 DAY,   'admin', NOW() - INTERVAL 4 DAY,   0),
(4, 6, 6,  7, 8, 9, 8.0, '农行防火墙部署质量良好，响应稍慢',                   4, '赵琳',  NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 30 DAY,  0),
(5, 6, 7,  7, 8, 8, 7.7, '农行IDS部署进行中，初步评估',                         4, '赵琳',  NOW() - INTERVAL 3 DAY,   'admin', NOW() - INTERVAL 3 DAY,   'admin', NOW() - INTERVAL 3 DAY,   0),
(6, 3, 9,  9, 10,9, 9.3, '交行IPS部署响应迅速，施工规范，文档完整',           7, '郑昊',  NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(7, 5, 11, 8, 7, 8, 7.7, '招行设备勘查完成，文档规范',                         5, '孙磊',  NOW() - INTERVAL 75 DAY,  'admin', NOW() - INTERVAL 75 DAY,  'admin', NOW() - INTERVAL 75 DAY,  0),
(8, 9, 15, 8, 9, 9, 8.7, '华夏青岛安全设备部署质量高，文档齐全',               5, '孙磊',  NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(9, 2, 4,  9, 9, 9, 9.0, '建行核心交换机二次评估，整体优秀',                   3, '刘伟',  NOW() - INTERVAL 2 DAY,   'admin', NOW() - INTERVAL 2 DAY,   'admin', NOW() - INTERVAL 2 DAY,   0),
(10,1, 3,  10,9, 9, 9.3, '工行防火墙部署二次评估，响应迅速，质量优秀',         1, 'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 90 DAY,  0);

-- =============================================================
-- 23. pms_settlement（10 条）— 结算（依赖 project_id 外键）
-- =============================================================
INSERT INTO `pms_settlement`
    (`id`, `task_id`, `agent_id`, `project_id`, `settlement_no`, `total_amount`, `tax_rate`, `tax_amount`, `total_with_tax`, `status`,
     `apply_user_id`, `apply_user_name`, `apply_time`, `approve_user_id`, `approve_user_name`, `approve_time`, `approve_opinion`,
     `push_status`, `push_time`, `push_response`, `invoice_no`, `payment_status`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `version`)
VALUES
(1,  3, 1, 1,  'SET-2024-001', 156000.00, 13.00, 20280.00,  176280.00, 'APPROVED', 2, '张明', NOW() - INTERVAL 95 DAY,  1, 'admin', NOW() - INTERVAL 90 DAY,  '审批通过',         'SUCCESS', NOW() - INTERVAL 85 DAY,  '{"code":0,"msg":"ok"}',         'INV-2024-001', 'PAID',     'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 85 DAY,  0, 0),
(2,  4, 2, 2,  'SET-2024-002', 128000.00, 13.00, 16640.00,  144640.00, 'PENDING',  3, '刘伟', NOW() - INTERVAL 5 DAY,   NULL, NULL,   NULL,                  NULL,               NULL,      NULL,                  NULL,                          NULL,           NULL,       'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 5 DAY,   0, 0),
(3,  5, 1, 2,  'SET-2024-003', 220000.00, 13.00, 28600.00,  248600.00, 'PENDING',  3, '刘伟', NOW() - INTERVAL 4 DAY,   NULL, NULL,   NULL,                  NULL,               NULL,      NULL,                  NULL,                          NULL,           NULL,       'admin', NOW() - INTERVAL 4 DAY,   'admin', NOW() - INTERVAL 4 DAY,   0, 0),
(4,  6, 6, 3,  'SET-2024-004', 58000.00,  13.00, 7540.00,   65540.00,  'APPROVED', 4, '赵琳', NOW() - INTERVAL 30 DAY,  1, 'admin', NOW() - INTERVAL 25 DAY,  '审批通过',         'SUCCESS', NOW() - INTERVAL 20 DAY,  '{"code":0,"msg":"ok"}',         'INV-2024-002', 'PAID',     'admin', NOW() - INTERVAL 30 DAY,  'admin', NOW() - INTERVAL 20 DAY,  0, 0),
(5,  7, 6, 3,  'SET-2024-005', 46000.00,  13.00, 5980.00,   51980.00,  'PENDING',  4, '赵琳', NOW() - INTERVAL 3 DAY,   NULL, NULL,   NULL,                  NULL,               NULL,      NULL,                  NULL,                          NULL,           NULL,       'admin', NOW() - INTERVAL 3 DAY,   'admin', NOW() - INTERVAL 3 DAY,   0, 0),
(6,  9, 3, 6,  'SET-2024-006', 105000.00, 13.00, 13650.00,  118650.00, 'APPROVED', 7, '郑昊', NOW() - INTERVAL 12 DAY,  1, 'admin', NOW() - INTERVAL 10 DAY,  '审批通过',         'SUCCESS', NOW() - INTERVAL 8 DAY,   '{"code":0,"msg":"ok"}',         'INV-2024-003', 'PAID',     'admin', NOW() - INTERVAL 12 DAY,  'admin', NOW() - INTERVAL 8 DAY,   0, 0),
(7,  11,5, 4,  'SET-2024-007', 25000.00,  13.00, 3250.00,   28250.00,  'APPROVED', 5, '孙磊', NOW() - INTERVAL 75 DAY,  1, 'admin', NOW() - INTERVAL 70 DAY,  '审批通过',         'SUCCESS', NOW() - INTERVAL 65 DAY,  '{"code":0,"msg":"ok"}',         'INV-2024-004', 'PAID',     'admin', NOW() - INTERVAL 75 DAY,  'admin', NOW() - INTERVAL 65 DAY,  0, 0),
(8,  15,9, 10, 'SET-2024-008', 312000.00, 13.00, 40560.00,  352560.00, 'APPROVED', 5, '孙磊', NOW() - INTERVAL 145 DAY, 1, 'admin', NOW() - INTERVAL 140 DAY, '审批通过',         'SUCCESS', NOW() - INTERVAL 135 DAY, '{"code":0,"msg":"ok"}',         'INV-2024-005', 'PAID',     'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 135 DAY, 0, 0),
(9,  8, 1,   5,  'SET-2024-009', 290000.00, 13.00, 37700.00, 327700.00, 'REJECTED', 6, '吴婷', NOW() - INTERVAL 20 DAY,  1, 'admin', NOW() - INTERVAL 18 DAY,  '结算明细不完整，驳回补充', NULL,      NULL,                  NULL,                          NULL,           NULL,       'admin', NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 18 DAY,  0, 0),
(10, 12,1, 4,  'SET-2024-010', 68000.00,  13.00, 8840.00,   76840.00,  'PENDING',  5, '孙磊', NOW() - INTERVAL 2 DAY,   NULL, NULL,   NULL,                  NULL,               NULL,      NULL,                  NULL,                          NULL,           NULL,       'admin', NOW() - INTERVAL 2 DAY,   'admin', NOW() - INTERVAL 2 DAY,   0, 0);

-- =============================================================
-- 24. pms_settlement_detail（20 条）— 结算明细
-- =============================================================
INSERT INTO `pms_settlement_detail`
    (`id`, `settlement_id`, `item_name`, `work_quantity`, `unit`, `unit_price`, `amount`, `remarks`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1, 'DPtech DPX8000 防火墙部署服务',   1.00, '台', 130000.00, 130000.00, '防火墙上架与策略配置',     'admin', NOW() - INTERVAL 95 DAY, 'admin', NOW() - INTERVAL 85 DAY, 0),
(2,  1, '现场联调测试服务',               1.00, '次', 26000.00,  26000.00,  '设备联调与功能测试',       'admin', NOW() - INTERVAL 95 DAY, 'admin', NOW() - INTERVAL 85 DAY, 0),
(3,  2, 'H3C S5560 交换机部署服务',       1.00, '台', 98000.00,  98000.00,  '交换机上架与IRF配置',      'admin', NOW() - INTERVAL 5 DAY,  'admin', NOW() - INTERVAL 5 DAY,  0),
(4,  2, '现场实施工时',                   10.00,'人日',3000.00,   30000.00,  '10人日实施工时',           'admin', NOW() - INTERVAL 5 DAY,  'admin', NOW() - INTERVAL 5 DAY,  0),
(5,  3, 'F5 BIG-IP 负载均衡部署服务',     1.00, '台', 180000.00, 180000.00, '负载均衡上架与pool配置',   'admin', NOW() - INTERVAL 4 DAY,  'admin', NOW() - INTERVAL 4 DAY,  0),
(6,  3, '高级工程师服务费',               8.00,'人日',5000.00,   40000.00,  '8人日高级工程师服务',      'admin', NOW() - INTERVAL 4 DAY,  'admin', NOW() - INTERVAL 4 DAY,  0),
(7,  4, 'DPtech DPX8000 防火墙部署',      1.00, '台', 45000.00,  45000.00,  '农行防火墙部署',           'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 20 DAY, 0),
(8,  4, '安全策略配置服务',               1.00, '项', 13000.00,  13000.00,  '安全策略设计与配置',       'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 20 DAY, 0),
(9,  5, 'DPtech IPS-2000 部署',           1.00, '台', 38000.00,  38000.00,  'IDS部署与特征库更新',      'admin', NOW() - INTERVAL 3 DAY,  'admin', NOW() - INTERVAL 3 DAY,  0),
(10, 5, '现场实施工时',                   2.67,'人日',3000.00,   8000.00,   '约2.67人日实施工时',        'admin', NOW() - INTERVAL 3 DAY,  'admin', NOW() - INTERVAL 3 DAY,  0),
(11, 6, 'Cisco Firepower 4112 部署',      1.00, '台', 85000.00,  85000.00,  'IPS部署与策略配置',        'admin', NOW() - INTERVAL 12 DAY, 'admin', NOW() - INTERVAL 8 DAY,  0),
(12, 6, '联调测试服务',                   1.00, '次', 20000.00,  20000.00,  '设备联调测试',             'admin', NOW() - INTERVAL 12 DAY, 'admin', NOW() - INTERVAL 8 DAY,  0),
(13, 7, '招行设备勘查服务',               1.00, '项', 25000.00,  25000.00,  '现场勘查与方案设计',       'admin', NOW() - INTERVAL 75 DAY, 'admin', NOW() - INTERVAL 65 DAY, 0),
(14, 8, '华夏青岛安全设备部署',           2.00, '台', 130000.00, 260000.00, '2台防火墙部署',            'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 135 DAY, 0),
(15, 8, '现场实施工时',                   13.00,'人日',4000.00,  52000.00,  '13人日实施工时',           'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 135 DAY, 0),
(16, 9, '中行国产化设备替换服务',         1.00, '项', 290000.00, 290000.00, '设备替换与配置迁移',       'admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 18 DAY, 0),
(17, 9, '联调测试服务',                   1.00, '项', 0.00,      0.00,      '联调测试(含在替换服务中)','admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 18 DAY, 0),
(18, 10,'招行设备FAT测试服务',           1.00, '项', 50000.00,  50000.00,  '工厂验收测试',             'admin', NOW() - INTERVAL 2 DAY,  'admin', NOW() - INTERVAL 2 DAY,  0),
(19, 10,'测试报告编写',                   1.00, '份', 12000.00,  12000.00,  'FAT测试报告编写',          'admin', NOW() - INTERVAL 2 DAY,  'admin', NOW() - INTERVAL 2 DAY,  0),
(20, 10,'现场实施工时',                   2.00,'人日',3000.00,   6000.00,   '2人日实施工时',            'admin', NOW() - INTERVAL 2 DAY,  'admin', NOW() - INTERVAL 2 DAY,  0);

-- =============================================================
-- 25. pms_warranty（10 条）— 质保（依赖 asset_id 外键，asset 已插入）
-- =============================================================
INSERT INTO `pms_warranty`
    (`id`, `asset_id`, `start_date`, `end_date`, `duration_months`, `sla_level`, `contract_no`, `project_id`, `notes`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  1,  NOW() - INTERVAL 190 DAY, NOW() + INTERVAL 540 DAY, 24, 'PLATINUM', 'WC-2024-0001', 1,  '工行核心路由器原厂质保，7x24响应',       'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 90 DAY,  0),
(2,  2,  NOW() - INTERVAL 190 DAY, NOW() + INTERVAL 540 DAY, 24, 'PLATINUM', 'WC-2024-0001', 1,  '工行备用路由器原厂质保',                 'admin', NOW() - INTERVAL 190 DAY, 'admin', NOW() - INTERVAL 90 DAY,  0),
(3,  3,  NOW() - INTERVAL 188 DAY, NOW() + INTERVAL 542 DAY, 24, 'PREMIUM',  'WC-2024-0002', 1,  '工行接入交换机质保，5x8响应',           'admin', NOW() - INTERVAL 188 DAY, 'admin', NOW() - INTERVAL 88 DAY,  0),
(4,  4,  NOW() - INTERVAL 187 DAY, NOW() + INTERVAL 543 DAY, 24, 'PLATINUM', 'WC-2024-0003', 1,  '工行边界防火墙原厂质保',                 'admin', NOW() - INTERVAL 187 DAY, 'admin', NOW() - INTERVAL 87 DAY,  0),
(5,  5,  NOW() - INTERVAL 100 DAY, NOW() + INTERVAL 630 DAY, 24, 'PREMIUM',  'WC-2024-0004', 2,  '建行核心交换机质保',                     'admin', NOW() - INTERVAL 100 DAY, 'admin', NOW() - INTERVAL 4 DAY,   0),
(6,  6,  NOW() - INTERVAL 95 DAY,  NOW() + INTERVAL 635 DAY, 24, 'PLATINUM', 'WC-2024-0005', 2,  '建行负载均衡原厂质保，7x24响应',         'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 3 DAY,   0),
(7,  7,  NOW() - INTERVAL 80 DAY,  NOW() + INTERVAL 650 DAY, 24, 'PREMIUM',  'WC-2024-0006', 3,  '农行防火墙质保',                         'admin', NOW() - INTERVAL 80 DAY,  'admin', NOW() - INTERVAL 2 DAY,   0),
(8,  11, NOW() - INTERVAL 160 DAY, NOW() + INTERVAL 570 DAY, 24, 'BASIC',    'WC-2024-0010', 5,  '中行接入交换机质保，next-business-day',  'admin', NOW() - INTERVAL 160 DAY, 'admin', NOW() - INTERVAL 20 DAY,  0),
(9,  13, NOW() - INTERVAL 90 DAY,  NOW() + INTERVAL 640 DAY, 24, 'PREMIUM',  'WC-2024-0012', 6,  '交行入侵防御设备质保',                   'admin', NOW() - INTERVAL 90 DAY,  'admin', NOW() - INTERVAL 12 DAY,  0),
(10, 15, NOW() - INTERVAL 270 DAY, NOW() + INTERVAL 90 DAY,  12, 'BASIC',    'WC-2024-0014', 10, '华夏青岛应用服务器质保（即将到期）',     'admin', NOW() - INTERVAL 270 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0);

-- 关联资产到质保记录，便于快速查询
UPDATE `pms_asset` SET `warranty_id` = 1  WHERE `id` = 1;
UPDATE `pms_asset` SET `warranty_id` = 2  WHERE `id` = 2;
UPDATE `pms_asset` SET `warranty_id` = 3  WHERE `id` = 3;
UPDATE `pms_asset` SET `warranty_id` = 4  WHERE `id` = 4;
UPDATE `pms_asset` SET `warranty_id` = 5  WHERE `id` = 5;
UPDATE `pms_asset` SET `warranty_id` = 6  WHERE `id` = 6;
UPDATE `pms_asset` SET `warranty_id` = 7  WHERE `id` = 7;
UPDATE `pms_asset` SET `warranty_id` = 8  WHERE `id` = 11;
UPDATE `pms_asset` SET `warranty_id` = 9  WHERE `id` = 13;
UPDATE `pms_asset` SET `warranty_id` = 10 WHERE `id` = 15;

-- =============================================================
-- 26. pms_rma（10 条）— RMA 退货（依赖 project_id 外键，project 已插入）
-- =============================================================
INSERT INTO `pms_rma`
    (`id`, `rma_no`, `asset_id`, `sn`, `fault_description`, `fault_photos`, `ticket_status`, `warranty_status`, `project_id`,
     `registered_at`, `warranty_checked_at`, `rma_issued_at`, `returning_at`, `inspected_at`, `closed_at`,
     `register_user_id`, `register_user_name`, `resolution`, `inspector_notes`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1, 'RMA-2024-001', 19, 'DP-2024-00019', '防火墙电源模块故障，设备无法加电启动',         '/upload/2024/rma/1.jpg', 'CLOSED',          'IN_WARRANTY',     1,  NOW() - INTERVAL 60 DAY, NOW() - INTERVAL 58 DAY, NOW() - INTERVAL 55 DAY, NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 10 DAY, 2, '张明',  '更换电源模块，设备恢复正常',         '检测确认电源模块损坏，已更换原厂备件', 'admin', NOW() - INTERVAL 60 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(2, 'RMA-2024-002', 4,  'DP-2024-00004', '防火墙风扇异响，温度告警',                     '/upload/2024/rma/2.jpg', 'INSPECTED',       'IN_WARRANTY',     1,  NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 2 DAY,  NULL,                    2, '张明',  NULL,                                '风扇模块检测中，初步判断需更换',     'admin', NOW() - INTERVAL 30 DAY, 'admin', NOW() - INTERVAL 2 DAY,   0),
(3, 'RMA-2024-003', 5,  'DP-2024-00005', '交换机端口故障，2个GE端口无响应',               '/upload/2024/rma/3.jpg', 'RETURNING',       'IN_WARRANTY',     2,  NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 2 DAY,   NULL,                    NULL,                    3, '刘伟',  NULL,                                NULL,                                  'admin', NOW() - INTERVAL 20 DAY, 'admin', NOW() - INTERVAL 2 DAY,   0),
(4, 'RMA-2024-004', 6,  'DP-2024-00006', '负载均衡设备硬盘故障，日志丢失',                 '/upload/2024/rma/4.jpg', 'RMA_ISSUED',      'IN_WARRANTY',     2,  NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 13 DAY, NOW() - INTERVAL 10 DAY, NULL,                    NULL,                    NULL,                    3, '刘伟',  NULL,                                NULL,                                  'admin', NOW() - INTERVAL 15 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(5, 'RMA-2024-005', 7,  'DP-2024-00007', '防火墙策略引擎异常，部分策略不生效',             '/upload/2024/rma/5.jpg', 'WARRANTY_CHECKED','IN_WARRANTY',     3,  NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 10 DAY, NULL,                    NULL,                    NULL,                    NULL,                    4, '赵琳',  NULL,                                NULL,                                  'admin', NOW() - INTERVAL 12 DAY, 'admin', NOW() - INTERVAL 10 DAY,  0),
(6, 'RMA-2024-006', 8,  'DP-2024-00008', '入侵检测系统误报频繁，特征库更新失败',           '/upload/2024/rma/6.jpg', 'REGISTERED',      'IN_WARRANTY',     3,  NOW() - INTERVAL 8 DAY,  NULL,                    NULL,                    NULL,                    NULL,                    NULL,                    4, '赵琳',  NULL,                                NULL,                                  'admin', NOW() - INTERVAL 8 DAY,   'admin', NOW() - INTERVAL 8 DAY,   0),
(7, 'RMA-2024-007', 11, 'DP-2024-00011', '交换机背板告警，疑似硬件故障',                   '/upload/2024/rma/7.jpg', 'RETURNING',       'IN_WARRANTY',     5,  NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 23 DAY, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 5 DAY,   NULL,                    NULL,                    6, '吴婷',  NULL,                                NULL,                                  'admin', NOW() - INTERVAL 25 DAY, 'admin', NOW() - INTERVAL 5 DAY,   0),
(8, 'RMA-2024-008', 13, 'DP-2024-00013', '入侵防御设备内存告警，频繁重启',                 '/upload/2024/rma/8.jpg', 'INSPECTED',       'IN_WARRANTY',     6,  NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 16 DAY, NOW() - INTERVAL 13 DAY, NOW() - INTERVAL 3 DAY,  NOW() - INTERVAL 1 DAY,  NULL,                    7, '郑昊',  NULL,                                '内存条故障，已更换并压力测试通过',     'admin', NOW() - INTERVAL 18 DAY, 'admin', NOW() - INTERVAL 1 DAY,   0),
(9, 'RMA-2024-009', 15, 'DP-2024-00015', '服务器硬盘故障告警，RAID降级',                   '/upload/2024/rma/9.jpg', 'CLOSED',          'OUT_OF_WARRANTY', 10, NOW() - INTERVAL 195 DAY, NOW() - INTERVAL 193 DAY, NULL,                    NULL,                    NOW() - INTERVAL 190 DAY, NOW() - INTERVAL 188 DAY, 5, '孙磊',  '过保付费维修，更换硬盘并重建RAID', '确认硬盘损坏，客户付费更换',         'admin', NOW() - INTERVAL 195 DAY, 'admin', NOW() - INTERVAL 188 DAY, 0),
(10,'RMA-2024-010', 14, 'DP-2024-00014', '路由器风扇模块故障告警',                         '/upload/2024/rma/10.jpg','WARRANTY_CHECKED','IN_WARRANTY',     6,  NOW() - INTERVAL 5 DAY,  NOW() - INTERVAL 3 DAY,  NULL,                    NULL,                    NULL,                    NULL,                    7, '郑昊',  NULL,                                NULL,                                  'admin', NOW() - INTERVAL 5 DAY,   'admin', NOW() - INTERVAL 3 DAY,   0);

-- =============================================================
-- 27. pms_notification（20 条）— 通知
-- =============================================================
INSERT INTO `pms_notification`
    (`id`, `user_id`, `title`, `content`, `category`, `biz_type`, `biz_id`, `read_status`, `channel`, `created_at`, `created_by`)
VALUES
(1,  2, '里程碑逾期提醒：工行北京分行核心网络改造项目 - 现场勘查', '项目 工行北京分行核心网络改造项目 的里程碑「现场勘查」已逾期，请尽快推进。', 'MILESTONE',  'MILESTONE_OVERDUE',     1,  'READ',   'IN_APP', NOW() - INTERVAL 195 DAY, 1),
(2,  7, '新任务分派：工行核心路由器安装实施',                   '您已被分派任务「工行核心路由器安装实施」（项目：工行北京分行核心网络改造项目），请及时跟进。', 'TASK',       'TASK_ASSIGNED',         1,  'READ',   'IN_APP', NOW() - INTERVAL 162 DAY, 1),
(3,  8, '新任务分派：工行交换机安装实施',                       '您已被分派任务「工行交换机安装实施」（项目：工行北京分行核心网络改造项目），请及时跟进。', 'TASK',       'TASK_ASSIGNED',         2,  'READ',   'IN_APP', NOW() - INTERVAL 162 DAY, 1),
(4,  1, '待办审批：建行项目延期及追加负载均衡预算',             '您有新的审批待办「建行项目延期及追加负载均衡预算」，提交人 刘伟，请尽快处理。',           'APPROVAL',   'CHANGE_REQUEST_CCB',    1,  'READ',   'IN_APP', NOW() - INTERVAL 62 DAY,  1),
(5,  3, '尾项清单到期提醒：工行核心路由器OSPF区域配置未优化',   '尾项「工行核心路由器OSPF区域配置未优化」将于近期到期，当前状态 VERIFIED，请及时闭环。',  'PUNCH_LIST', 'PUNCH_LIST_DEADLINE',   1,  'READ',   'IN_APP', NOW() - INTERVAL 102 DAY, 1),
(6,  2, '质保即将到期（剩余 30 天）：华夏青岛应用服务器',       '设备 华夏青岛应用服务器(编号 DP-2024-00015)的质保将于近期到期，剩余 30 天，请尽快联系续保。', 'WARRANTY',   'WARRANTY_EXPIRE_30',    15, 'UNREAD', 'IN_APP', NOW() - INTERVAL 5 DAY,   1),
(7,  5, '质保即将到期（剩余 90 天）：华夏青岛应用服务器',       '设备 华夏青岛应用服务器(编号 DP-2024-00015)的质保将于近期到期，剩余 90 天，请关注续保事宜。', 'WARRANTY',   'WARRANTY_EXPIRE_90',    15, 'READ',   'IN_APP', NOW() - INTERVAL 65 DAY,  1),
(8,  2, 'RMA 状态变更：RMA-2024-001',                         'RMA 申请 RMA-2024-001 的状态已变更为 CLOSED，请留意后续处理。',                          'RMA',        'RMA_STATUS_CHANGE',     1,  'READ',   'IN_APP', NOW() - INTERVAL 10 DAY,  1),
(9,  4, 'RMA 状态变更：RMA-2024-006',                         'RMA 申请 RMA-2024-006 的状态已变更为 REGISTERED，请留意后续处理。',                     'RMA',        'RMA_STATUS_CHANGE',     6,  'UNREAD', 'IN_APP', NOW() - INTERVAL 8 DAY,   1),
(10, 1, '结算已审批通过：SET-2024-001',                       '结算单 SET-2024-001 已审批通过，结算金额 176280.00，请查看详情。',                       'SETTLEMENT', 'SETTLEMENT_APPROVED',   1,  'READ',   'IN_APP', NOW() - INTERVAL 90 DAY,  1),
(11, 3, '变更请求待 CCB 评审：CR-2024-0001',                 '变更请求 CR-2024-0001「建行项目延期及追加负载均衡预算」待 CCB 评审，请及时处理。',       'APPROVAL',   'CHANGE_REQUEST_CCB',    1,  'READ',   'IN_APP', NOW() - INTERVAL 62 DAY,  1),
(12, 3, '风险升级提醒：建行负载均衡设备选型变更风险',         '风险「建行负载均衡设备选型变更风险」已升级为 HIGH 等级，请关注并制定应对措施。',         'APPROVAL',   'RISK_ESCALATED',        10, 'UNREAD', 'IN_APP', NOW() - INTERVAL 3 DAY,   1),
(13, 7, '里程碑逾期提醒：中行杭州分行网络设备更新项目 - 终验', '项目 中行杭州分行网络设备更新项目 的里程碑「终验」计划完成日期已过，现已逾期。',       'MILESTONE',  'MILESTONE_OVERDUE',     16, 'UNREAD', 'IN_APP', NOW() - INTERVAL 25 DAY,  1),
(14, 7, '任务转派给您：交行分支路由器部署',                   'admin 将任务「交行分支路由器部署」（项目：交通银行成都分行网络安全扩容项目）转派给您，请于近期完成。', 'TASK',   'TASK_DELEGATED',        10, 'READ',   'IN_APP', NOW() - INTERVAL 30 DAY,  1),
(15, 8, '新任务分派：农行入侵检测部署',                       '您已被分派任务「农行入侵检测部署」（项目：中国农业银行深圳分行网络安全加固项目），请及时跟进。', 'TASK',   'TASK_ASSIGNED',         7,  'READ',   'IN_APP', NOW() - INTERVAL 45 DAY,  1),
(16, 5, '尾项清单到期提醒：建行设备上架未做防倾倒固定',       '尾项「建行设备上架未做防倾倒固定」将于近期到期，当前状态 OPEN，请及时闭环。',             'PUNCH_LIST', 'PUNCH_LIST_DEADLINE',   4,  'UNREAD', 'IN_APP', NOW() - INTERVAL 55 DAY,  1),
(17, 6, '质保即将到期（剩余 60 天）：华夏青岛应用服务器',     '设备 华夏青岛应用服务器(编号 DP-2024-00015)的质保将于近期到期，剩余 60 天，请关注续保事宜。', 'WARRANTY', 'WARRANTY_EXPIRE_60',    15, 'READ',   'IN_APP', NOW() - INTERVAL 35 DAY,  1),
(18, 1, '结算已审批通过：SET-2024-008',                       '结算单 SET-2024-008 已审批通过，结算金额 352560.00，请查看详情。',                       'SETTLEMENT', 'SETTLEMENT_APPROVED',   8,  'READ',   'IN_APP', NOW() - INTERVAL 140 DAY, 1),
(19, 5, 'RMA 状态变更：RMA-2024-009',                         'RMA 申请 RMA-2024-009 的状态已变更为 CLOSED，请留意后续处理。',                          'RMA',        'RMA_STATUS_CHANGE',     9,  'READ',   'IN_APP', NOW() - INTERVAL 188 DAY, 1),
(20, 3, '变更请求待 CCB 评审：CR-2024-0009',                 '变更请求 CR-2024-0009「中信项目范围调整」待 CCB 评审，请及时处理。',                     'APPROVAL',   'CHANGE_REQUEST_CCB',    9,  'UNREAD', 'IN_APP', NOW() - INTERVAL 5 DAY,   1);

-- =============================================================
-- 28. pms_attachment（10 条）— 附件
-- =============================================================
INSERT INTO `pms_attachment`
    (`id`, `biz_type`, `biz_id`, `file_name`, `file_size`, `mime_type`, `upload_user_id`, `upload_user_name`, `upload_time`, `md5`, `storage_path`, `storage_type`, `gps_latitude`, `gps_longitude`, `photo_taken_at`, `geo_fence_status`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  'DELIVERABLE',    1,  '工行北京网络改造设计方案.pdf',  2048576, 'application/pdf',                 2, '张明',     NOW() - INTERVAL 188 DAY, 'a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6', '/upload/2024/icbc/design.pdf',         'LOCAL', NULL,           NULL,           NULL,                    NULL,    'admin', NOW() - INTERVAL 188 DAY, 'admin', NOW() - INTERVAL 100 DAY, 0),
(2,  'DELIVERABLE',    3,  '工行北京联调测试报告.pdf',      1536000, 'application/pdf',                 7, '工程师甲', NOW() - INTERVAL 120 DAY, 'b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7', '/upload/2024/icbc/test-report.pdf',     'LOCAL', NULL,           NULL,           NULL,                    NULL,    'admin', NOW() - INTERVAL 120 DAY, 'admin', NOW() - INTERVAL 95 DAY,  0),
(3,  'DELIVERABLE',    4,  '工行北京项目验收报告.pdf',      1024000, 'application/pdf',                 2, '张明',     NOW() - INTERVAL 95 DAY,  'c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8', '/upload/2024/icbc/acceptance.pdf',      'LOCAL', NULL,           NULL,           NULL,                    NULL,    'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 95 DAY,  0),
(4,  'PUNCH_LIST',     1,  '工行OSPF缺陷现场照片.jpg',        512000, 'image/jpeg',                       7, '工程师甲', NOW() - INTERVAL 105 DAY, 'd4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9', '/upload/2024/punch/p1.jpg',             'LOCAL', 39.904200,      116.407400,    NOW() - INTERVAL 105 DAY, 'NORMAL',  'admin', NOW() - INTERVAL 105 DAY, 'admin', NOW() - INTERVAL 98 DAY,  0),
(5,  'RMA',            1,  '防火墙电源故障照片.jpg',          768000, 'image/jpeg',                       2, '张明',     NOW() - INTERVAL 60 DAY,  'e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0', '/upload/2024/rma/1.jpg',                'LOCAL', 39.904200,      116.407400,    NOW() - INTERVAL 60 DAY,  'NORMAL', 'admin', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 60 DAY,  0),
(6,  'IMPL_PROGRESS',  6,  '建行交换机上架照片.jpg',          614400, 'image/jpeg',                       7, '工程师甲', NOW() - INTERVAL 60 DAY,  'f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1', '/upload/2024/impl/p4-50.jpg',            'LOCAL', 31.230400,      121.473700,    NOW() - INTERVAL 60 DAY,  'NORMAL', 'admin', NOW() - INTERVAL 60 DAY,  'admin', NOW() - INTERVAL 60 DAY,  0),
(7,  'IMPL_PROGRESS',  11, '中行设备替换照片.jpg',            819200, 'image/jpeg',                       7, '工程师甲', NOW() - INTERVAL 158 DAY, 'a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2', '/upload/2024/impl/p8-100.jpg',          'LOCAL', 30.274100,      120.155100,    NOW() - INTERVAL 158 DAY, 'NORMAL', 'admin', NOW() - INTERVAL 158 DAY, 'admin', NOW() - INTERVAL 158 DAY, 0),
(8,  'DELIVERABLE',    18, '华夏青岛运维交接文档.docx',       256000, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 5, '孙磊', NOW() - INTERVAL 145 DAY, 'b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3', '/upload/2024/hxb/handover.docx',     'LOCAL', 36.067100,      120.382600,    NULL,                    NULL,    'admin', NOW() - INTERVAL 145 DAY, 'admin', NOW() - INTERVAL 145 DAY, 0),
(9,  'ACCEPTANCE',     1,  '工行终验报告附件.pdf',            512000, 'application/pdf',                 2, '张明',     NOW() - INTERVAL 95 DAY,  'c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4', '/upload/2024/acceptance/icbc.pdf',      'LOCAL', NULL,           NULL,           NULL,                    NULL,    'admin', NOW() - INTERVAL 95 DAY,  'admin', NOW() - INTERVAL 95 DAY,  0),
(10, 'IMPL_PROGRESS',  15, '华夏青岛安全设备部署照片.jpg',     700000, 'image/jpeg',                       9, 'admin',    NOW() - INTERVAL 278 DAY, 'd0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5', '/upload/2024/impl/p15-100.jpg',         'LOCAL', 36.067100,      120.382600,    NOW() - INTERVAL 278 DAY, 'NORMAL', 'admin', NOW() - INTERVAL 278 DAY, 'admin', NOW() - INTERVAL 278 DAY, 0);

-- =============================================================
-- 29. pms_integration_log（10 条）— 集成日志
-- =============================================================
INSERT INTO `pms_integration_log`
    (`id`, `log_type`, `business_type`, `business_id`, `request_url`, `request_body`, `response_status`, `response_body`, `error_message`, `retry_count`, `max_retry`, `next_retry_time`,
     `create_by`, `create_time`, `update_by`, `update_time`, `deleted`)
VALUES
(1,  'D365', 'SETTLEMENT',       '1', 'https://d365.example.com/api/settlement/push',  '{"settlementNo":"SET-2024-001","amount":176280.00}', 'SUCCESS', '{"code":0,"msg":"ok","d365Id":"D365-SET-001"}',     NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 85 DAY,  'admin', NOW() - INTERVAL 85 DAY,  0),
(2,  'D365', 'INVOICE',          '1', 'https://d365.example.com/api/invoice/sync',     '{"invoiceNo":"INV-2024-001","amount":176280.00}',     'SUCCESS', '{"code":0,"msg":"ok","d365Id":"D365-INV-001"}',     NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 80 DAY,  'admin', NOW() - INTERVAL 80 DAY,  0),
(3,  'D365', 'PURCHASE_RECEIPT', '1', 'https://d365.example.com/api/receipt/push',     '{"receiptNo":"R-2024-001","poNo":"PO-2024-0001"}',   'SUCCESS', '{"code":0,"msg":"ok","d365Id":"D365-RC-001"}',      NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 195 DAY, 'admin', NOW() - INTERVAL 195 DAY, 0),
(4,  'FP',   'PAYMENT',           '1', 'https://fp.example.com/api/payment/callback',    '{"settlementNo":"SET-2024-001","status":"PAID"}',      'SUCCESS', '{"code":0,"msg":"ok"}',                              NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 82 DAY,  'admin', NOW() - INTERVAL 82 DAY,  0),
(5,  'D365', 'SETTLEMENT',       '4', 'https://d365.example.com/api/settlement/push',  '{"settlementNo":"SET-2024-004","amount":65540.00}',   'SUCCESS', '{"code":0,"msg":"ok","d365Id":"D365-SET-004"}',     NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 20 DAY,  'admin', NOW() - INTERVAL 20 DAY,  0),
(6,  'D365', 'INVOICE',          '2', 'https://d365.example.com/api/invoice/sync',     '{"invoiceNo":"INV-2024-002","amount":65540.00}',     'FAILED',  NULL,                                                'Connection timeout after 10000ms',    1, 3, NOW() + INTERVAL 1 HOUR,     'admin', NOW() - INTERVAL 18 DAY,  'admin', NOW() - INTERVAL 17 DAY, 0),
(7,  'FP',   'PAYMENT',           '4', 'https://fp.example.com/api/payment/callback',    '{"settlementNo":"SET-2024-004","status":"PAID"}',      'SUCCESS', '{"code":0,"msg":"ok"}',                              NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 18 DAY,  'admin', NOW() - INTERVAL 18 DAY, 0),
(8,  'OA',   'APPROVAL',         '1', 'https://oa.example.com/api/approval/create',     '{"crNo":"CR-2024-0001","title":"建行项目延期"}',       'SUCCESS', '{"code":0,"msg":"ok","oaId":"OA-2024-001"}',        NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 62 DAY,  'admin', NOW() - INTERVAL 62 DAY, 0),
(9,  'D365', 'PURCHASE_ORDER',   '1', 'https://d365.example.com/api/po/sync',           '{"poNo":"PO-2024-0001","vendor":"DPtech"}',           'SUCCESS', '{"code":0,"msg":"ok","d365Id":"D365-PO-001"}',      NULL,                                  0, 3, NULL,                       'admin', NOW() - INTERVAL 196 DAY, 'admin', NOW() - INTERVAL 196 DAY, 0),
(10, 'SMS', 'NOTIFICATION',      '1', 'https://sms.example.com/api/sms/send',           '{"phone":"13901234567","content":"工行项目终验通过"}', 'PENDING', NULL,                                                NULL,                                  0, 3, NOW() + INTERVAL 30 MINUTE, 'admin', NOW() - INTERVAL 1 HOUR,  'admin', NOW() - INTERVAL 1 HOUR, 0);