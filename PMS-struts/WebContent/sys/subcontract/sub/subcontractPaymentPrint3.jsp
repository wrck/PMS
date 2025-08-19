<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
<script type="text/javascript">
</script>
</head>
<body>
    <style>
        .readLine .uploadWrapper {
            display: none;
        }
        .deliverList {
            /* width: 30vw; */
            max-width: 20vw;
        }
        #subcontractMultiDimInfoTable {
            margin-top: 20px;
        }
        #subcontractMultiDimInfoTable tr {
            height: 3rem;
        }
        #subcontractPaymentPrintTable tr td:first-child {
            max-width: 200px !important;
            width: 200px !important;
            text-align: right;
        }
        #subcontractPaymentInvoiceDeliveryTable tr th {
            white-space: nowrap;
        }
    </style>
    <div style="text-align: left;">
        <div>
            <h3>项目信息</h3>
            <table id="subcontractPaymentPrintTable" class="table table-bordered table-hover table-striped ">
                <tr style="display:none"><td><s:hidden name="subcontract.id"/></td></tr>
                <tr>
                    <td class="nowrap">所属公司:</td>
                    <td class="nowrap"><s:property value="commonMap.company.name"/></td>
                </tr>
                <tr>
                    <td class="nowrap">采购订单号:</td>
                    <td><s:property value="subcontract.customInfo.purchId"/></td>
                </tr>
                <tr>
                    <td class="nowrap"><s:text name="pm.subcontract.subcontractNo"></s:text>:</td>
                    <td><s:property value="subcontract.subcontractNo"/></td>
                </tr>
                <tr>
                
                    <td><s:text name="pm.subcontract.project"></s:text>:</td>
                    <td><s:hidden id="projectIds" name="subcontract.projectIds" cssClass="smEdit canEmpty"></s:hidden>
                        <div id="paymentProjectListDiv">
                           <display:table style="text-align: left;margin-bottom:0;"
                                name="projectList" sort="external" partialList="false"
                                decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" >
                                <display:column property="projectNameWithCodeWarrper" class="transferProjectName" titleKey="pm.project.projectName" media="html"></display:column> 
                                <display:column property="contractNo" titleKey="pm.contract" class="transferContractNo" decorator="com.dp.plat.decorators.ContractNoList"></display:column>
                            </display:table>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>销售合同号:</td>
                    <td><s:property value="subcontract.contractNos" /></td>
                </tr>
                <tr>
                    <td>我司回款情况:</td>
                    <td><s:property value="subcontract.customInfo.collectedAmount" /></td>
                </tr>
                <tr>
                    <td>付款比例（含本次）:</td>
                    <td><span id="paymentSumRatio"><s:property value="commonMap.sumApplyRatio" /></span>%</td>
                </tr>
                <tr>
                    <td>此次付款说明:</td>
                    <td><span id="paymentRemark"></span></td>
                </tr>
            </table>
        </div>
        <div>
            <h3>发票清单</h3>
            <table class="displayTable table table-bordered table-hover table-striped" style="text-align: left;" id="subcontractPaymentInvoiceDeliveryTable">
                <thead>
                    <tr>
                        <th class="text-center">付款</th>
                        <th width="4%" class="text-center">序号</th>
                        <th width="9%">发票类型</th>
                        <th width="9%">开票时间</th>
                        <th width="8%">发票号码</th>
                        <th width="16%">销售方</th>
                        <th width="10%">费用内容</th>
                        <th width="8%">不含税单价</th>
                        <th width="4%">税率</th>
                        <th width="5%">税额</th>
                        <th>价税合计</th>
                        <th width="15%">备注</th>
                    </tr>
                </thead>
            <s:set var="invoiceTotalAmountSum" value="0"></s:set>
            <s:iterator value="subcontractPaymentList" var="payment" status="pstatus">
                <tbody>
                <s:set var="invoiceTotalAmount" value="0"></s:set>
                <s:iterator value="#payment.delivers" var="deliver" status="dstatus">
                    <s:set var="invoice" value="#deliver.customInfo"></s:set>
                    <s:set var="invoiceItems" value="#invoice.itemList"></s:set>
                    <s:set var="invoiceItemsSize" value="#invoiceItems.size()"></s:set>
                    <s:set var="invoiceTotalAmount" value="%{#invoice.total_amount + #invoiceTotalAmount}"></s:set>
                    <tr>
                    <s:if test="#dstatus.index == 0">
                        <td rowspan="${(invoiceItemsSize > 0 ? invoiceItemsSize : 1) + payment.delivers.size()}" class="text-center nowrap">${pstatus.index + 1}</td>
                    </s:if>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-center nowrap">${dstatus.index + 1}</td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}"><s:property value="#invoice.type_desc"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-center nowrap"><s:property value="#invoice.invoice_date.split(' ')[0]"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-center nowrap"><s:property value="#invoice.invoice_number"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}"><s:property value="#invoice.seller_name"/></td>
                    <s:if test="#invoiceItemsSize > 0">
                        <s:iterator value="#invoiceItems" var="invoiceItem" status="istatus" begin="0" end="0">
                        <td><s:property value="#invoiceItem.item_name"/></td>
                        <td><s:property value="#invoiceItem.pre_tax_unit_price"/></td>
                        <td><s:property value="#invoiceItem.tax_rate"/></td>
                        </s:iterator>
                    </s:if>
                    <s:else>
                        <td></td>
                        <td></td>
                        <td></td>
                    </s:else>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}"><s:property value="#invoice.tax_amount"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}"><s:property value="#invoice.total_amount"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}"><s:property value="#invoice.remark" escapeHtml="false" /></td>
                    </tr>
                    <s:if test="#invoiceItemsSize > 0">
                        <s:iterator value="#invoiceItems" var="invoiceItem" status="istatus" begin="1">
                        <td><s:property value="#invoiceItem.item_name"/></td>
                        <td><s:property value="#invoiceItem.pre_tax_unit_price"/></td>
                        <td><s:property value="#invoiceItem.tax_rate"/></td>
                        </s:iterator>
                    </s:if>
                </s:iterator>
                <s:if test="#payment.delivers.size() > 0">
                    <tr>
                        <th colspan="2">发票金额合计：</th>
                        <th colspan="7"></th>
                        <th>${invoiceTotalAmount}</th>
                        <th></th>
                    </tr>
                </s:if>
                <s:set var="invoiceTotalAmountSum" value="%{#invoiceTotalAmountSum + #invoiceTotalAmount}"></s:set>
                </tbody>
            </s:iterator>
            <s:if test="subcontractPaymentList.size() > 0">
                <tfoot>
                    <tr>
                        <th colspan="3">发票金额总计：</th>
                        <th colspan="7"></th>
                        <th>${invoiceTotalAmountSum}</th>
                        <th></th>
                    </tr>
                </tfoot>
            </s:if>
            </table>
        </div>
        <script type="text/javascript">
            /* $(function(){
                function printPayment(payment) {
                    payment = payment || {};
                    $("#paymentProjectListDiv").html($("#projectDisplayTable").clone());
                    $("#paymentSumRatio").text($("#sumRatio").text());
                    $("#paymentRemark").text(payment.remark);
                }
                setTimeout(() => {
                    // printPayment();
                }, 1000);
            }) */
        </script>
    </div>
</body>
</html>