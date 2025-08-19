package com.dp.plat.pms.extend.fp.model;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import com.dp.plat.pms.extend.fp.entity.InvoiceProviderInfo;

/**
 * <p>
 * 档案条目类，用于管理报销单据和发票的相关信息。
 * </p>
 *
 * <table border="1" cellpadding="5" cellspacing="0">
 *     <caption>字段描述</caption>
 *     <tr>
 *         <th>字段名称</th>
 *         <th>是否必填</th>
 *         <th>数据类型</th>
 *         <th>描述</th>
 *         <th>适用范围</th>
 *         <th>枚举值</th>
 *     </tr>
 *     <tr>
 *         <td>archiveType</td>
 *         <td>是</td>
 *         <td>String</td>
 *         <td>档案类型</td>
 *         <td>报销单据、发票</td>
 *         <td>7 : 报销单据<br>1 ：发票</td>
 *     </tr>
 *     <tr>
 *         <td>files</td>
 *         <td>否</td>
 *         <td>MultipartFile[]</td>
 *         <td>附件，支持多文件传输</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>diaryAccount</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>日记账号</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>voucherDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>凭证日期</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>companyId</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>公司主体代码（例如 D001/D002）</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>revenueConfirmDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>收入确认时间</td>
 *         <td>报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>revenueConfirmEvidence</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>收入确认证据</td>
 *         <td>报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>dataDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>日期</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>tradeDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>交易日期</td>
 *         <td>报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>vendorCode</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>供应商编码</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>vendorName</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>供应商名称</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>description</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>描述</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>archiveCode</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>归档编码</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>personNumber</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>提单人工号</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>personName</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>提单人姓名</td>
 *         <td>报销单据、发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>paperExpenseReport</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>是否纸质报销单</td>
 *         <td>报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>invoiceNumber</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>发票号码</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>invoiceCode</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>发票编码</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>purchOrder</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>采购订单号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>oaProcessNumber</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>OA流程号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>oaPurchContent</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>OA采购内容</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>taxInclusive</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>含不含税</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>subcontractCode</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>转包合同号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>projectName</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>项目名称</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>postingPeriod</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>过账期间</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>reimbursementNumber</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>报销单号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>invoiceDate</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>发票日期</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>buyerInformation</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>购买方信息</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>contractCode</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>合同号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>executionOrderNumber</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>执行单号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>packingListNumber</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>装箱单号</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>shipmentDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>发货期</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>dataSource</td>
 *         <td>否</td>
 *         <td>String</td>
 *         <td>数据来源</td>
 *         <td>发票、报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>accountingDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>会计日期</td>
 *         <td>发票、报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>paymentDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>付款日期</td>
 *         <td>发票、报销单据</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>billingDate</td>
 *         <td>否</td>
 *         <td>Date</td>
 *         <td>入账日期</td>
 *         <td>发票</td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td>businessType</td>
 *         <td>是</td>
 *         <td>String</td>
 *         <td>业务类型</td>
 *         <td>发票</td>
 *         <td>1 ：电子发票-原材料/加工费<br>2 ：发票-行政采购（OA）<br>4： 安服<br>5：用服<br>6：美金发票<br>7：手工凭证<br>8：SSE发票-一般报销发票<br>9 ： 增值税发票（销项）-电子票<br>10 ： 增值税发票（销项）-纸质票扫描件</td>
 *     </tr>
 * </table>
 */
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ElectronicInvoiceModel extends InvoiceProviderInfo {
    private static final long serialVersionUID = -4687377697161313220L;

    /**
     * 是否异步
     */
    private boolean async;
    
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 数据ID
     */
    private String dataId;
    /**
     * 发票信息传递列表
     */
    private List<ElectronicInvoiceModel> invoiceList;
    /**
     * 发票信息传递列表
     */
    private List<Object> sourceList;
    /**
     * 附件，可多文件传输报销单据、发票
     */
    private File[] files;
    /**
     * json格式传参数据
     */
    private String jsonData;
    /**
     * 发票编码
     */
    private String invoiceCode;
    /**
     * 发票日期
     */
    private String invoiceDate;
    /**
     * 发票号码
     */
    private String invoiceNumber;
    
}