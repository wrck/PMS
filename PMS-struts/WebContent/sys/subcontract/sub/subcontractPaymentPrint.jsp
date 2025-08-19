<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
    <!-- 打印付款信息 -->
    <div id="subcontractPaymentPrintDiv" style="text-align: left;">
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
            #subcontractPaymentPrintTable > tbody > tr > td:first-child {
                max-width: max-content !important;
                width: 12em !important;
                white-space: nowrap;
                text-align: right;
            }
            #subcontractPaymentInvoiceDeliveryTable tr th {
                white-space: nowrap;
            }
            .text-amount {
                text-align: right;
            }
            .print-hide {
                display: block;
            }
            .print-hide-inline {
                display: inline;
            }
            .print-show {
                display: none;
            }
            .print h4 {
                margin: 0.5rem 0;
            }
        </style>
        <style media="print" type="text/css">
            @media print {
                thead { display: table-header-group!important; }
                table { page-break-inside: auto; }
                tr    { page-break-inside: auto; page-break-after: auto; }
                tfoot { display: table-row-group!important; }
                td[rowspan="1"], th[rowspan="1"] {
                    page-break-inside: avoid !important;
                    break-inside: avoid !important;
                }
                /* 可选：根据需要调整边距 */
                body { margin: 0; }
                table { margin: 0; }
            }
            /* 纵向打印样式 */
            @media print and (orientation: portrait) {
                /* 更多纵向打印样式 */
            }
            
            /* 横向打印样式 */
            @media print and (orientation: landscape) {
                /* 更多横向打印样式 */
            }
            
            .print-hide {
                display: none!important;
            }
            .print-show {
                display: block!important;
            }
            .print-show-large td, .print-show-large th, .print-show-large span, .print-show-large a {
                font-size: 1.5rem;
            }
            .print h4 {
                font-size: 18px;
                margin: 1rem 0;
            }
        </style>
        <div class="print print-hide">
            <!-- <div>
                <button id="printBtn" type="button" class="btn btn-info btn-sm">选择付款行打印付款单</button>
            </div> -->
        </div>
        <div class="print print-show print-show-large">
            <h4><b>项目信息</b></h4>
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
                    <td class="nowrap"><s:text name="pm.subcontract.subcontractName"></s:text>:</td>
                    <td><s:property value="subcontract.subcontractName"/></td>
                </tr>
                <%-- <tr>
                
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
                </tr> --%>
                <tr>
                    <td class="nowrap"><s:text name="pm.subcontract.type"></s:text>:</td>
                    <td><s:property value="subcontract.typeName"/></td>
                </tr>
                <tr>
                    <td class="nowrap"><s:text name="pm.subcontract.subcontractAmount"></s:text>:</td>
                    <td><s:property value="subcontract.subcontractAmount"/></td>
                </tr>
                <%-- <tr>
                    <td>销售合同号:</td>
                    <td class="word-break-wbr"><s:property value="subcontract.contractNos" /></td>
                </tr> --%>
                <tr>
                    <td>我司回款情况:</td>
                    <td><s:property value="subcontract.customInfo.collectedAmount" /></td>
                </tr>
                <tr>
                    <td class="nowrap">已付款比例:</td>
                    <td><span id="paidSumRatio"><s:property value="commonMap.sumPaidRatio" /></span>%</td>
                </tr>
                <tr>
                    <td class="nowrap">本次付款比例:</td>
                    <td><span id="currentPayRatio"></span>%</td>
                </tr>
                <%-- <tr>
                    <td>付款比例（含本次）:</td>
                    <td><span id="paymentSumRatio"><s:property value="commonMap.sumApplyRatio" /></span>%</td>
                </tr>
                <tr>
                    <td>此次付款说明:</td>
                    <td><span id="paymentRemark"></span></td>
                </tr> --%>
            </table>
        </div>
        <div class="print ${commonMap.identifyInvoiceCount > 0 ? '' : 'print-show'}" style="break-before: page;">
            <h4><b>发票清单</b></h4>
            <table class="displayTable table table-bordered table-hover" style="text-align: left;" id="subcontractPaymentInvoiceDeliveryTable">
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
            <s:set var="AmountFormat" value="commonMap.AmountFormat" />
            <s:iterator value="subcontractPaymentList" var="payment" status="pstatus">
                <tbody class="paymentInvoiceTable paymentInvoiceTable${payment.id}">
                <s:set var="invoiceTotalAmount" value="0"></s:set>
                <s:set var="invoiceDelivers" value="#payment.invoiceDelivers"></s:set>
                <s:set var="invoiceCount" value="#invoiceDelivers.size()"></s:set>
                <s:iterator value="#invoiceDelivers" var="deliver" status="dstatus">
                    <s:set var="invoice" value="#deliver.customInfo"></s:set>
                    <s:set var="invoiceItems" value="#invoice.itemList"></s:set>
                    <s:set var="invoiceItemsSize" value="#invoiceItems.size()"></s:set>
                    <s:set var="invoiceTotalAmount" value="%{#invoice.total_amount + #invoiceTotalAmount}"></s:set>
                    <s:set var="invoiceVerified" value="%{!#invoice.needVerify || #invoice.verified_status}" />
                    <tr class="${invoiceVerified ? '' : 'bg-danger'}">
                    <s:if test="#dstatus.index == 0">
                        <td rowspan="${(invoiceItemsSize > 0 ? invoiceItemsSize : 1) + invoiceCount}" class="text-center nowrap">付款${pstatus.count}</td>
                    </s:if>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-center nowrap">${dstatus.count}</td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}"><s:property value="#invoice.type_desc"/><br><span class="print-hide print-hide-inline label label-${invoiceVerified ? 'success' : 'danger'}"><s:property value="#invoiceVerified ? '' : #invoice.verified_result_desc"/></span></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-center nowrap"><s:property value="#invoice.invoice_date.split(' ')[0]"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-center nowrap"><s:property value="#invoice.uniqueInvoiceNumber || #invoice.invoice_number"/></td>
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
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-amount"><s:property value="#AmountFormat.format(#invoice.tax_amount)"/></td>
                        <td rowspan="${invoiceItemsSize > 0 ? invoiceItemsSize : 1}" class="text-amount"><s:property value="#AmountFormat.format(#invoice.total_amount)"/></td>
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
                <s:if test="#invoiceCount > 0">
                    <tr>
                        <th colspan="2" class="text-right">发票金额合计：</th>
                        <th colspan="7"></th>
                        <th class="text-amount invoiceTotalAmount">${AmountFormat.format(invoiceTotalAmount)}</th>
                        <th></th>
                    </tr>
                </s:if>
                <s:set var="invoiceTotalAmountSum" value="%{#invoiceTotalAmountSum + #invoiceTotalAmount}"></s:set>
                </tbody>
            </s:iterator>
            <s:if test="subcontractPaymentList.size() > 0">
                <tfoot>
                    <tr>
                        <th colspan="3" class="text-right">发票金额总计：</th>
                        <th colspan="7"></th>
                        <th class="text-amount invoiceTotalAmountSum">${AmountFormat.format(invoiceTotalAmountSum)}</th>
                        <th></th>
                    </tr>
                </tfoot>
            </s:if>
            </table>
        </div>
        <script type="text/javascript">
            function printSubcontractPayment(paymentIds) {
            	paymentIds = paymentIds || [];
            	var result = true;
            	try {
                	if (paymentIds.length == 0) {
                        alert("请选择付款申请行！");
                        return result = false;
                    }
                	
                	$(".paymentInvoiceTable").hide();
                	for (let paymentId of paymentIds) {
                		$(".paymentInvoiceTable" + paymentId).show();
    				}
                	// 重新计算需要打印的付款行的发票总计
                	calcPaymentAmount();
                	calcInvoiceAmount();
                	
                    var mode = "iframe";//popup 或者 iframe
                    var close = mode == "popup" && true;//当mode为popup起作用
                    var extraCss = '';
                    var print = "#subcontractPaymentPrintDiv";//可打印的区域，为选择器
                    
                    var keepAttr = ["id", "class", "style", "class", "on"];
                    var headElements = "<scr"+"ipt type=\"text/javascript\" src=\"${pageContext.request.contextPath}/js/jquery-2.1.4.min.js\" ></scr"+ "ipt>";
                    var options = { mode : mode, popClose : close, extraCss : extraCss, retainAttr : keepAttr, extraHead : headElements };
                    $( print ).show().printArea( options );
                    return result = true;
            	} finally {
                    setTimeout(function() {
    					$("#printBtn").bootstrapBtn("reset");
    					$(".paymentInvoiceTable").show();
    					// 重新计算付款行的发票总计
    					calcInvoiceAmount();
    				}, result ? 1000 : 200);
                }
            	return false;
            }
            
            function calcPaymentAmount() {
                var sumApplyAmount = 0;
                var sumApplyRatio = 0;
                var sumApprovedAmount = 0;
                var sumApprovedRatio = 0;
                var sumPaidAmount = 0;
                var sumPaidRatio = 0;
                var getNumber = function($el) {
                	var number = Number(($.trim($el.text()) || $.trim($el.find("input").val()) || $.trim($el.val())).replace(/,/g, "")) || 0;
                    return number;
                }
            	$(".paymentIds:checked").each(function() {
            		var $tr = $(this).parents("tr:first");
            		var $approvedAmount = $(".approvedAmount", $tr);
            		var $paidAmount = $(".paidAmount", $tr);
            		var $applyAmount = $(".amount", $tr);
            		var $ratio = $(".ratio", $tr);
            		
            		var approvedAmount = getNumber($approvedAmount);
            		var paidAmount = getNumber($paidAmount);
            		var applyAmount = getNumber($applyAmount);
            		var ratio = getNumber($ratio);
                    if (!isNaN(applyAmount)) {
                        sumApplyAmount += applyAmount;
                        sumApprovedAmount += approvedAmount;
                        sumPaidAmount += paidAmount;
                        
                        sumApplyRatio += ratio;
                        sumApprovedRatio += approvedAmount > 0 ? ratio : 0;
                        sumPaidRatio += paidAmount > 0 ? ratio : 0;
                    }
            	});
            	sumPaidRatio = sumPaidRatio.toLocaleString(undefined, {'minimumFractionDigits':2,'maximumFractionDigits':2});
            	sumApplyRatio = sumApplyRatio.toLocaleString(undefined, {'minimumFractionDigits':2,'maximumFractionDigits':2});
            	/* $("#paidSumRatio").text(sumPaidRatio); */
            	$("#currentPayRatio").text(sumApplyRatio);
            }
            
            function calcInvoiceAmount() {
            	var invoiceTotalAmountSum = 0;
                $(".invoiceTotalAmount", ".paymentInvoiceTable:visible").each(function() {
                    invoiceTotalAmountSum += Number($.trim($(this).text().replace(/,/g, "")));
                })
                invoiceTotalAmountSum = invoiceTotalAmountSum.toLocaleString(undefined, {'minimumFractionDigits':2,'maximumFractionDigits':2});
                /*if (invoiceTotalAmountSum.indexOf(".") == -1) {
                    invoiceTotalAmountSum += ".00";
                } else if (invoiceTotalAmountSum.indexOf(".") + 2 == invoiceTotalAmountSum.length) {
                    invoiceTotalAmountSum += "0";
                }*/
                $(".invoiceTotalAmountSum").text(invoiceTotalAmountSum);
            }
            
            function verifyPaymentDeliver(paymentIds) {
                paymentIds = paymentIds || [];
                var isAjax = false;
                var result = true;
                try {
                    if (paymentIds.length == 0) {
                    	alert("请选择付款申请行！");
                    	return result = false;
                    }
                    
                    var subcontractId = $("#subcontractId").val();
                    var params = [];
                    params.push($.param({"subcontract.id":subcontractId}));
                    for (let paymentId of paymentIds) {
                    	params.push($.param({
                            "selected": paymentId
                        }));
                    }
                    
                    isAjax = true;
                    $.ajax({
                        url :"module/s/subcontractAjax_verifyPaymentDeliver.action",
                        type :"post",
                        dataType :"json",
                        data : params.join("&"),
                        success:function(data){
                        	console.log(data);
                            if(data.deliverid != 0){
                                $(".nav li[name='navli'].active").dblclick();
                            } else {
                                alert("识别失败！");
                            }
                        }, 
                        complete: function() {
                             setTimeout(function() {
                                 $("#verifyBtn").bootstrapBtn("reset");
                             }, 200);
                        }
                    });
                    return result = true;
                } finally {
                	if (!isAjax) {
                        setTimeout(function() {
                            $("#verifyBtn").bootstrapBtn("reset");
                        }, result ? 1000 : 200);
                	}
                }
                return result = false;
            }
            $(function(){
            	var $paymentPrintContainer = $("#subcontractPaymentPrintDiv");
            	var $printArea = $("#subcontractPaymentPrintDiv,#subcontractPaymentTable");
            	var $paymentContainer = $("#subcontractPaymentDiv");
            	var $invoiceDeliveryTable = $("#subcontractPaymentInvoiceDeliveryTable");
            	var btnSelectorIds = "#printBtn, #verifyBtn, .paymentDeliveryBtn";
            	if ($(".paymentInvoiceTable", $invoiceDeliveryTable).length == 0) {
            		$paymentPrintContainer.hide();
            		$(btnSelectorIds, $printArea).hide();
            	} else {
            		$paymentPrintContainer.show();
            		$(btnSelectorIds, $printArea).show();
            	}
            	
            	$(".word-break-wbr", $printArea).each(function() {
            		var text = $(this).html();
            		$(this).html(text.replace(/,/g, ",<wbr>"));
            	})
            	
            	var prevText = '确认打印已选择的付款行';
            	$(btnSelectorIds, $printArea).click(function() {
        		    var $btn = $(this);
                    var type = $btn.data("type") || "select";
                    var btnType = $btn.data("btnType") || type;
                    var text = $btn.text();
                    var prevText = $btn.data("changeTitle");
                    // resetText 是bootstrapBtn("reset")的临时变量
                    var result = true;
                    if (type == "select") {
                        $(".paymentIds", $paymentContainer).removeClass("hidden").removeAttr("disabled");
                        $(`#\${type}CancelBtn`, $printArea).show();
                        $(this).data("type", `submit`);
                    } else {
                        var paymentIds = [];
                        $(".paymentIds input:checked").not("[id='checkall'],.checkall").each(function() {
                        	paymentIds.push(this.value);
                        });
                     
                        if (btnType == 'print') {
                        	result = printSubcontractPayment(paymentIds);
                        } else if (btnType == 'verify') {
                        	result = verifyPaymentDeliver(paymentIds);
                        } else {
                        	alert("未知操作");
                        }
                        if (result) {
                    	    $(".paymentIds", $paymentContainer).addClass("hidden").attr("disabled", true);
                            $(this).data("type", "select");
                            $(`#\${type}CancelBtn`, $printArea).hide();
                        }
                    }
                    if (result) {
                    	$btn.text(prevText).data("resetText", prevText);
                        prevText = text;
                        $btn.data("changeTitle", text);
                    }
                    if (type == "select") {
                        setTimeout(function() {
                        	$btn.bootstrapBtn("reset");
                        }, 200);
                    }
            	});
            	$(".paymentDeliveryCancelBtn", $printArea).click(function() {
            	 	var $cancelBtn = $(this);
            	 	var $btn = $("#" + $btn.attr("id").replace("Cancel", ""), $printArea);
                    var text = $btn.text();
                    var prevText = $btn.data("changeTitle");
                    $btn.text(prevText);
                    prevText = text;
                    
                    $(".paymentIds", $paymentContainer).addClass("hidden");
                    $btn.data("type", "select").data("changeTitle", prevText);
                    $(this).hide();
                });
                
                $("#checkall,.checkall", $paymentContainer).click(function() {
                    if ($(this).is(":checked")) {
                        $(".paymentIds input", $paymentContainer).prop("checked", true);
                    } else {
                        $(".paymentIds input", $paymentContainer).prop("checked", false);
                    }
                });
            })
        </script>
    </div>