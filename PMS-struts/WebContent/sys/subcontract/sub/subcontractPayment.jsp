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
    </style>
    <div style="text-align: left;">
        <s:form enctype="multipart/form-data" id="subcontractPaymentForm" action="%{namespace}/subcontract_savePayment.action">
            <table id="subcontractInfoTable" class="table table-bordered table-hover table-striped ">
                <tr style="display:none"><td><s:hidden name="subcontract.id"/></td></tr>
                <tr>
                    <td class="nowrap"><s:text name="pm.subcontract.subcontractNo"></s:text>:</td>
                    <td class="col-sm-3"><s:textfield name="subcontract.subcontractNo" cssClass="form-control taskOnly task-generateContractTask" placeholder='请输入转包合同号'/></td>
                    <%-- <td class="nowrap"><s:text name="pm.subcontract.accrued"></s:text>:</td>
                    <td class="nowrap"><s:radio name="subcontract.isAccrued" list="#{true:'是',false:'否'}"/></td>
                    <td class="nowrap"><s:text name="pm.subcontract.invoiced"></s:text>:</td>
                    <td class="nowrap"><s:radio name="subcontract.isInvoiced" list="#{true:'是',false:'否'}"/></td> --%>
                    <td class="nowrap">税率:</td>
                    <td class="nowrap">
                        <%-- <s:textfield type="number" name="subcontract.customStrInfo.taxRate" id="taxRate" cssClass="form-control" style="display:inline-block;width: 100px;" autocomplete="off" placeholder="税率"/> --%>
                        <s:select id="subcontractTax" list="taxList" name="subcontract.customStrInfo.taxItemGroup" cssClass="form-control taskOnly task-generateContractTask" listKey="basicDataId" listValueKey="basicDataName" headerKey="" headerValue="--请选择--"/>
                    </td>
                    <td class="nowrap">公司:</td>
                    <td class="nowrap">
                        <%-- <s:textfield type="number" name="subcontract.customStrInfo.taxRate" id="taxRate" cssClass="form-control" style="display:inline-block;width: 100px;" autocomplete="off" placeholder="税率"/> --%>
                        <s:select id="subcontractOrgId" list="compList" name="subcontract.orgId" cssClass="form-control taskOnly task-generateContractTask" listKey="id" listValueKey="abbr" headerKey="" headerValue="--请选择--"/>
                    </td>
                    <td class="nowrap">转包周期:</td>
                    <td>
                        <div class="display-inline-flex">
                            <s:textfield type="text" name="subcontract.customStrInfo.subcontStartDate" id="subcontStartDate" cssClass="form-control taskOnly task-generateContractTask" style="display:inline-block;width: 100px;" autocomplete="off" placeholder="开始日期"/>
                            <s:textfield type="text" name="subcontract.customStrInfo.subcontEndDate" id="subcontEndDate" cssClass="form-control taskOnly task-generateContractTask" style="display:inline-block;width: 100px;" autocomplete="off" placeholder="结束日期"/>
                        </div>
                    </td>
                    <td class="nowrap"><s:text name="pm.subcontract.deliver"></s:text>:</td>
                    <td>
                        <span id="finalContracts">
                            <s:text name="pm.subcontract.deliver.contract.final"/>
                            <span class="finalContract">
                                <s:file label="File" name="uploadDeliverList[2].uploads" cssClass="form-control" />
                                <s:hidden name="uploadDeliverList[2].type" value="2"/>
                            </span>
                        </span>
                    </td>
                </tr>
            </table>
            <display:table style="text-align: left;"
                name="subcontractPaymentList" export="false" id="subcontractPaymentTable"
                size="${subcontractPaymentList.size()}" sort="external" requestURI="module/sub/querySubcontractPayment.action"
                decorator="com.dp.plat.subcontract.decorators.SubcontractDecorator" class="displayTable table table-condensed table-hover table-striped">
                <s:set value="%{column.extData}" var="extData"></s:set>
                
                <display:column title="序号" headerClass="text-center nowrap" class="text-center rowNum nowrap">
                    ${subcontractPaymentTable_rowNum}
                </display:column>
                <display:column property="id" headerClass="hidden" class="text-input id hidden" title="id" media="html"></display:column>
                <display:column property="subcontractId" headerClass="hidden" class="text-input subcontractId hidden ignore" title="subcontractId" media="html"></display:column>
                <display:column property="ratio" class="text-input ratio nowrap" titleKey="pm.subcontract.ratio"></display:column>
                <display:column property="amount" class="text-input amount nowrap" titleKey="pm.subcontract.amount"></display:column>
                <%-- <s:if test="%{workflowCommonParam.outcome == 'acceptanceTask'}"> --%>
                <display:column property="paymentApprovedAmountWrapper" class="approvedAmount nowrap"  title="审批金额" media="html">
                    <input name="subcontractPaymentList[0].customStrInfo.approvedAmount" value="${subcontractPaymentTable.customStrInfo.approvedAmount != null ? subcontractPaymentTable.customStrInfo.approvedAmount : subcontract.subcontractAmount}" class="form-control" >
                </display:column>
                <%-- </s:if> --%>
                <%-- <c:if test="${subcontractPaymentTable.paymentTime != null}">
                <display:column class="paiedAmount nowrap ${subcontractPaymentTable.paymentTime != null ? '' : 'hidden'}" headerClass="${subcontractPaymentTable.paymentTime != null ? '' : 'hidden'}" title="已付金额" media="html">
                    ${subcontractPaymentTable.customStrInfo.paiedAmount != null ? subcontractPaymentTable.customStrInfo.paiedAmount : subcontractPaymentTable.amount}
                </display:column>
                </c:if> --%>
                <%-- <display:column class="paiedAmount nowrap " headerClass="nowrap" title="已付金额" media="html">
                    ${subcontractPaymentTable.customStrInfo.paidAmount != null && subcontractPaymentTable.customStrInfo.paidAmount != 0 ? subcontractPaymentTable.customStrInfo.paidAmount : subcontractPaymentTable.amount}
                </display:column> --%>
                <display:column property="paidAmountWrapper" class="paidAmount nowrap" headerClass="nowrap" title="已付金额" media="html"></display:column>
                <display:column property="confirmTime" headerClass="nowrap" class="confirmTime nowrap" titleKey="pm.subcontract.confirmTime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
                <display:column property="paymentTime" headerClass="nowrap" class="paymentTime nowrap" titleKey="pm.subcontract.paymentTime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
                <display:column property="remark" class="text-input remark" titleKey="pm.subcontract.remark"></display:column>
                <display:column class="text-input customStrInfo.invoiceNumber" title="发票号码">${subcontractPaymentTable.customStrInfo.invoiceNumber}</display:column>
                <display:column class="status nowrap" title="状态">${subcontractPaymentTable.customStrInfo.status}${subcontractPaymentTable.customStrInfo.paystate != null ? '，'.concat(subcontractPaymentTable.customStrInfo.paystate) : ''}</display:column>
                <display:column property="deliverableName" class="deliverList" title="附件"></display:column>
                <s:if test="%{workflowCommonParam.outcome == 'applyPaymentTask'}">
                <display:column title="操作" media="html">
                    <span class="glyphicon glyphicon-plus text-success"></span>
                    <span class="glyphicon glyphicon-minus text-danger"></span>
                </display:column>
                </s:if>
                <display:column property="customInfo.readonly" headerClass="hidden" class="readonly hidden" title=""></display:column>
                <display:footer>
                    <%-- <s:if test="%{subcontractPaymentList.size() == 0}"> --%>
                        <tr class="empty-template hidden inputLine">
                            <td class="text-center rowNum">1</td>
                            <td class="text-input subcontractId hidden ignore"><input name="subcontractPaymentList[0].subcontractId" value='<s:property value="subcontract.id"/>' class="form-control ignore"></td>
                            <td class="text-input ratio"><input name="subcontractPaymentList[0].ratio" class="form-control" readonly="readonly"></td>
                            <td class="text-input amount"><input name="subcontractPaymentList[0].amount" class="form-control" ></td>
                            <td class="approvedAmount"><!-- <input name="subcontractPaymentList[0].approvedAmount" class="form-control"> --></td>
                            <td class="paidAmount"><!-- <input name="subcontractPaymentList[0].paidAmount" class="form-control"> --></td>
                            <td class="confirmTime"><!-- <input name="subcontractPaymentList[0].confirmTime" class="form-control"> --></td>
                            <td class="paymentTime"><!-- <input name="subcontractPaymentList[0].paymentTime" class="form-control"> --></td>
                            <td class="text-input remark"><input name="subcontractPaymentList[0].remark" class="form-control"></td>
                            <td class="text-input customStrInfo.invoiceNumber"><input name="subcontractPaymentList[0].customStrInfo.invoiceNumber" class="form-control" placeholder="发票号码"></td>
                            <td class="status"><input name="subcontractPaymentList[0].customStrInfo.status" class="form-control hidden ignore" value="待审批"></td>
                            <td class="deliverList">
                                <span class="uploadWrapper">
		                            <%-- <s:text name="验收材料"/> --%>
		                            <span class="inspection">
		                                <s:file label="File" name="paymentDeliverList[0].uploads" cssClass="form-control" multiple="true" />
		                                <s:hidden class="ignore" name="paymentDeliverList[0].type" value="验收材料"/>
		                            </span>
		                        </span>
                            </td>
                            <td>
                                <span class="glyphicon glyphicon-plus text-success" ></span>
                                <span class="glyphicon glyphicon-minus text-danger" style="display:none;"></span>
                            </td>
                        </tr>
                    <%-- </s:if> --%>
                    <tr id="sumTr" class='text-danger' style='font-weight: 800;'>
                        <td colspan='1' class='text-right'>合计</td>
                        <td colspan='1' id="sumRatio"></td>
                        <td colspan='1' ><div id="sumAmount"></div><div id="paiedAmount"></div></td>
                        <s:if test="%{workflowCommonParam.outcome == 'acceptanceTask'}">
                        <td colspan='1' ><div id="sumApprovedAmount"></div><div id="approvingAmount"></div></td>
                        </s:if>
                        <td  id="delIds"></td>
                        <td class="mergeTdBorder">
                            <s:if test="%{(user.isHasRole(10) || user.isHasRole(13) || workflowCommonParam.outcome == 'applyPaymentTask') && subcontract.state != 100}">
                               <button id="paymentSubmitBtn" type="submit" class="btn btn-success"><span class="glyphicon glyphicon-floppy-save" style="font-size:12px;"></span> 保存</button>
                            </s:if>
                        </td>
                        <td class="mergeTdBorder"></td>
                        <td class="mergeTdBorder"></td>
                        <td class="mergeTdBorder"></td>
                    </tr>
                </display:footer>
            </display:table>
            
            <s:if test="%{workflowCommonParam.taskId != null}">
            <div id="emApproveDiv" style="margin-bottom:20px;">
                <fieldset class="hidden">
                    <s:hidden name="workflowCommonParam.instId"></s:hidden>
                    <s:hidden name="workflowCommonParam.taskId"></s:hidden>
                    <s:hidden name="workflowCommonParam.objId"></s:hidden>
                    <s:hidden name="workflowCommonParam.outcome"></s:hidden>
                </fieldset>
                <s:if test="%{workflowCommonParam.outcome == 'generateContractTask'}">
                    <s:hidden name="workflowCommonParam.approveStatus" value="5"></s:hidden>
                    <s:hidden name="workflowCommonParam.flag" value="0"></s:hidden>
                    <button id="generateContractBtn" type="submit" class="btn btn-info taskSubmitBtn"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 生成合同</button>
                    <script type="text/javascript">
                        $(function(){
                            $("#generateContractBtn").appendTo($("#subcontractPaymentTable tfoot #delIds"));
                        });
                    </script>
                </s:if>
                <s:if test="%{workflowCommonParam.outcome == 'applyPaymentTask'}">
                    <s:hidden name="workflowCommonParam.flag" value="0"></s:hidden>
                    <button id="applyPaymentBtn" type="submit" class="btn btn-info taskSubmitBtn"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 申请付款</button>
                    <%-- <script type="text/javascript">
                        $(function(){
                            $("#applyPaymentBtn").appendTo($("#subcontractPaymentTable tfoot #delIds"));
                            $("#applyPaymentBtn").click(function(e) {
                                // 有申请付款按钮，则允许编辑付款信息
                                var $flag = $(this).data("target");
                                var oldValue = $(this).data("value");
                                if ($(".emptyLine .ratio input").length > 0) {
                                    $(".emptyLine .ratio input").focus();
                                    alert("请输入付款比例！");
                                    if ($flag) {
                                    	$flag.val(oldValue);
                                    }
                                    e.preventDefault();
                                    e.stopPropagation();
                                } else if ($(".inputLine:not(.empty-template)").length == 0) {
                                	alert("请新增付款行！");
                                	e.preventDefault();
                                    e.stopPropagation();
                                }
                                var $btn = $(this);
                                setTimeout(function() {
                                    $btn.bootstrapBtn("reset");
                                }, 10);
                            })
                        });
                    </script> --%>
                </s:if>
                <s:if test="%{workflowCommonParam.outcome == 'approvePaymentTask'}">
                    <fieldset class="form-group">
                        <s:hidden name="workflowCommonParam.flag" value="0"></s:hidden>
                        <label>审批意见：</label><s:radio list="#{'5':'部分付款', '2':'可以闭环','-1':'闭环驳回'}" name="workflowCommonParam.approveStatus"  listCssStyle="margin-right:1rem;"></s:radio>
                        <s:textarea name="workflowCommonParam.comment" cssClass="form-control" placeholder="审批意见"/>
                    </fieldset>
                    <button id="approvePaymentBtn" class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
                </s:if>
                <s:if test="%{workflowCommonParam.outcome == 'acceptanceTask'}">
                    <fieldset class="form-group">
                        <s:hidden name="workflowCommonParam.flag" value="0"></s:hidden>
                        <label>审批意见：</label><s:radio list="#{'1':'通过', '-1':'不通过'}" name="workflowCommonParam.approveStatus"  listCssStyle="margin-right:1rem;"></s:radio>
                        <s:textarea name="workflowCommonParam.comment" cssClass="form-control" placeholder="审批意见"/>
                        <s:hidden id="taskApproveData" name="workflowCommonParam.customStrInfo.approveData"/>
                    </fieldset>
                    <button id="acceptanceBtn" class="btn btn-info taskSubmitBtn"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
                </s:if>
            </s:if>
            </div>
        </s:form>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.maskMoney.min.js"></script>
        <script type="text/javascript">
            $(function(){
            	// 事件绑定前置
        	<s:if test="%{workflowCommonParam.outcome == 'applyPaymentTask'}">
                $("#applyPaymentBtn").appendTo($("#subcontractPaymentTable tfoot #delIds"));
                $("#applyPaymentBtn").click(function(e) {
                	if (!checkPaymentForm()) {
                        var $btn = $(this);
                        setTimeout(function() {
                            $btn.bootstrapBtn("reset");
                        }, 10);
                        e.preventDefault();
                        e.stopPropagation();
                        return false;
                    }
                    // 有申请付款按钮，则允许编辑付款信息
                    var $flag = $(this).data("target");
                    var oldValue = $(this).data("value");
                    if ($(".emptyLine .ratio input").length > 0) {
                        $(".emptyLine .ratio input").focus();
                        alert("请输入付款比例！");
                        if ($flag) {
                            $flag.val(oldValue);
                        }
                        e.preventDefault();
                        e.stopPropagation();
                    } else if ($(".inputLine:not(.empty-template)").length == 0) {
                        alert("请新增付款行！");
                        e.preventDefault();
                        e.stopPropagation();
                    }
                    var $btn = $(this);
                    setTimeout(function() {
                        $btn.bootstrapBtn("reset");
                    }, 10);
                })
            </s:if>
            	var taskKey = "<s:property value='workflowCommonParam.outcome'/>" || "";
                $(document).on("submit", "#subcontractPaymentForm", function() {
                    if (taskKey == "generateContractTask") {
                        var subcontractNo = $.trim($("#subcontractPaymentForm input[name='subcontract.subcontractNo']").val());
                        if (!subcontractNo) {
                            alert("请输入转包合同号！");
                            var $btn = $(this).find("button");
                            $("#subcontractPaymentForm input[name='subcontract.subcontractNo']").focus();
                            setTimeout(function() {
                                $btn.bootstrapBtn("reset");
                            }, 10);
                            return false;
                        }
                        var subcontractTax = $.trim($("#subcontractPaymentForm #subcontractTax").val());
                        if (subcontractTax == '') {
                            alert("请选择转包税率！");
                            var $btn = $(this).find("button");
                            $("#subcontractPaymentForm #subcontractTax").focus();
                            setTimeout(function() {
                                $btn.bootstrapBtn("reset");
                            }, 10);
                            return false;
                        }
                    }
                    var subcontractType = $("#subcontractType").val();
                    if (("<s:property value='result'/>" || "") && subcontractType == '30') {
                        alert("<s:property value='result'/>");
                        var $btn = $(this).find("button");
                        setTimeout(function() {
                            $btn.bootstrapBtn("reset");
                        }, 10);
                        return false;
                    }
                    if (!checkPaymentForm()) {
                        var $btn = $(this).find("button");
                        setTimeout(function() {
                            $btn.bootstrapBtn("reset");
                        }, 10);
                        return false;
                    }
                    return true;
                    //return checkPaymentForm();
                });
                
                /* $(document).on("click", "#paymentSubmitBtn", function() {
                    $("input[name='workflowCommonParam.approveStatus'][value='5']", $("#subcontractPaymentForm")).prop("checked","checked");
                }); */
                $(document).off("click", "#approvePaymentBtn, .taskSubmitBtn");
                $(document).on("click", "#approvePaymentBtn, .taskSubmitBtn", function() {
                    var $flag = $("input[name='workflowCommonParam.flag']", $("#subcontractPaymentForm"));
                    $flag.data("value", $flag.val()).val("1");
                    $(this).data("target", $flag);
                    
                    // 封装任务的自定义字段
                    var customInfo = {};
                    $(".task-" + taskKey + ".task-customInfo").each(function() {
                    	var type = $(this).data("type");
                    	var id = $(this).data("id");
                    	var key = $(this).data("key");
                    	var value = $.trim($(this).val()) || $.trim($(this).data("value")) || '';
                    	var typeData = customInfo[type] || {};
                    	var dataInfo = customInfo[id] || {};
                    	dataInfo[key] = value;
                    	typeData[id] = dataInfo;
                    	customInfo[type] = typeData;
                    })
                    $("#taskApproveData", $("#subcontractPaymentForm")).val(JSON.stringify(customInfo));
                });
                
                $(document).off("click", ".glyphicon-plus:not([disabled])");
                $(document).on("click", ".glyphicon-plus:not([disabled])", function(){
                    //var $sourceTr = $(this).parents("tr:first");
                    var $sourceTr = $("tr.empty-template");
                    $sourceTr.find(".glyphicon-minus").show();
                    var $targetTr = $sourceTr.clone().removeClass("empty-template hidden");
                    // $sourceTr.after($targetTr);
                    var $tbody = $sourceTr.parents("table:first").find("tbody");
                    $tbody.append($targetTr);
                    
                    var $siblings = $targetTr.siblings()
                    if ($siblings.length == 0){
                        $targetTr.find(".glyphicon-minus").hide();
                    }
                    $targetTr.find("input[name]").removeAttr("disabled").not(".ignore").val("");
                    $targetTr.find("input[name*='Time']").removeAttr("id").removeClass("hasDatepicker").datepicker({
                        changeMonth: true,
                        changeYear: true,
                        option: {
                            dateFormat:"yy-mm-dd"
                        }
                    });
                    refreshListIndex($tbody);
                    //calcRatio();
                })
                
                $(document).off("click", ".glyphicon-minus:not([disabled])");
                $(document).on("click", ".glyphicon-minus:not([disabled])", function(){
                    var $sourceTr = $(this).parents("tr:first");
                    var $siblings =  $sourceTr.siblings();
                    if ($siblings.length > 0) {
                        var $delId = $sourceTr.find("input[name*='id']");
                        if ($delId.val()) {
                            $delId.attr("name", "delIds").attr("type", "hidden").appendTo($("#delIds"));
                        }
                        $sourceTr.remove();
                    } 
                    if ($siblings.length == 1){
                        $siblings.find(".glyphicon-minus").hide();
                    }
                    var $tbody = $sourceTr.parents("table:first").find("tbody");
                    refreshListIndex($tbody);
                    if (calcRatio()) {
                        calcAmount();
                    };
                });
                
                $(document).off("change", ".ratio input[name*='ratio']:not([readonly])");
                $(document).on("change", ".ratio input[name*='ratio']:not([readonly])", function(){
                    if (calcRatio()) {
                        var subcontractAmount = $.trim($("#subcontractInfoTable #subcontractAmount").text());
                        subcontractAmount = Number(subcontractAmount.replace(/,/g, ""));
                        
                        var amount = 0;
                        if (!isNaN(subcontractAmount)) {
                            amount = subcontractAmount * this.value / 100;
                        }
                        amount = Number(amount.toFixed(2));
                        //$(this).parent().next().children().val(amount.toFixed(2)).maskMoney({defaultZero: true,allowZero:true,precision:2});
                        //$(this).parent().next().children().maskMoney({defaultZero: true,allowZero:true,precision:2});
                        //$(this).parent().next().children().maskMoney("mask", amount.toFixed(2));
                        var $amount = $(this).parent().next().children();
                        $amount.maskMoney(maskMoneyCfg);
                        $amount.maskMoney("mask", amount);
                        calcAmount();
                    };
                });
                
                $(document).off("change", ".amount input[name*='amount']:not([readonly])");
                $(document).on("change", ".amount input[name*='amount']:not([readonly])", function(){
                    if (calcAmount()) {
                        var subcontractAmount = $.trim($("#subcontractInfoTable #subcontractAmount").text());
                        subcontractAmount = Number(subcontractAmount.replace(/,/g, ""));
                        
                        var amountRatio = 0;
                        var $amount = $(this);
                        var $ratio = $(this).parents(".amount:first").siblings(".ratio");
                        var amount = Number(($.trim($amount.text()) || $.trim($amount.val()) || $.trim($amount.find("input").val()) || "0").replace(/,/g, ""));
                        if (!isNaN(subcontractAmount)) {
                            amountRatio = Number((amount / subcontractAmount * 100).toFixed("2"));
                        }
                        $ratio.find("input").val(amountRatio); 
                        calcRatio();
                    };
                });
                
                $(document).off("change", "#subcontractPaymentTable tbody .approvedAmount input[name*='approvedAmount']");
                $(document).on("change", "#subcontractPaymentTable tbody .approvedAmount input[name*='approvedAmount']", function(){
                    calcAmount();
                });
                
                $(document).off("change", "#subcontractPaymentTable tbody .amount");
                $(document).on("change", "#subcontractPaymentTable tbody .amount", function(){
                    calcRatio();
                    calcAmount();
                });
            	
                // 初始化和方法定义
                date_picker3("subcontStartDate");
                date_picker3("subcontEndDate");
                var ratioRegx = /^-?(100(\.0{1,2})?|(([1-9]\d|\d)(\.\d{1,2})?))$/;
                function calcRatio() {
                    var sumRatio = 0;
                    var isValid = true;
                    var subcontractAmount = $.trim($("#subcontractInfoTable #subcontractAmount").text());
                    subcontractAmount = Number(subcontractAmount.replace(/,/g, ""));
                    var sumAmount = 0;
                    var $prevRatio = $("#subcontractPaymentTable tbody .ratio").first();
                    $("#subcontractPaymentTable tbody .ratio").each(function(){
                    	var $ratio = $(this);
                        var ratioStr = $.trim($(this).text()) || $.trim($(this).find("input").val());
                        if (ratioStr != '' && ratioStr.match(ratioRegx) == null) {
                            isValid = false;
                            alert("请输入有效的比例!");
                            return;
                        }
                        /* var amountRatio = 0;
                        var subcontractAmount = $.trim($("#subcontractInfoTable #subcontractAmount").text());
                        subcontractAmount = Number(subcontractAmount.replace(/,/g, ""));
                        
                        var $amount = $(this).siblings(".amount");
                        var amount = Number(($.trim($amount.text()) || $.trim($amount.find("input").val()) || "0").replace(/,/g, ""));
                        if (!isNaN(subcontractAmount)) {
                            amountRatio = Number((amount / subcontractAmount * 100).toFixed("2"));
                        }
                    
                        var ratio = Number(ratioStr) || amountRatio;
                        $(this).find("input").val(ratio); */
                        var ratio = Number(ratioStr) || 0;
                        
                        if (!isNaN(ratio)) {
                            sumRatio += ratio;
                            sumRatio = Number(sumRatio.toFixed("2"));
                            $prevRatio = $ratio;
                        }
                        
                        // 求总金额
                        var $amount = $(this).siblings(".amount");
                        var amount = Number(($.trim($amount.text()) || $.trim($amount.find("input").val()) || "0").replace(/,/g, ""));
                        if (!isNaN(amount)) {
                            sumAmount += amount;
                        }
                    });
                    if (String(sumRatio).match(ratioRegx) == null) {
                    	sumAmount = Number(sumAmount.toFixed("2"));
                    	subcontractAmount = Number(subcontractAmount.toFixed("2"));
                    	// 如果总比例无效，但是总计正常，则忽略。部分金额付款比例四舍五入会出现总比例大于100的情况
                    	if (sumAmount > subcontractAmount) {
                            alert("总比例无效，请检查!");
                            return false;
                    	} else {
                    		// 根据总金额重新计算比例
                    		var amountRatio = Number((sumAmount / subcontractAmount * 100).toFixed("2"));
                    		// 获取最后一个付款比例
                    		var ratioStr = $.trim($prevRatio.text()) || $.trim($prevRatio.find("input").val());
                    		var ratio = Number(ratioStr) || 0;
                    		// 计算最后一个付款比例之前的总比例
                    		var prevSumRatio = Number((sumRatio - ratio).toFixed("2"));
                    		// 重新计算最后一个付款比例处理尾差
                    		var newRatio = amountRatio - prevSumRatio;
                    		$prevRatio.find("input").val(newRatio);
                    		sumRatio = amountRatio;
                    	}
                    }
                    $("#sumRatio").text(sumRatio);
                    return isValid;
                }
                calcRatio();
                //$("#subcontractPaymentTable tbody").append("<tr class='text-danger' style='font-weight: 800;'><td colspan='1' class='text-right'>合计</td><td colspan='2'>" + sumRatio + "</td></tr>");
                
                var $emptyTr = $("#subcontractPaymentTable tbody .empty");
                if ($emptyTr.length == 1) {
                    $emptyTr.remove();
                    $(".empty-template .glyphicon-plus").click();
                    //$(".empty-template").appendTo($("#subcontractPaymentTable tbody")).removeClass("hidden empty-template");
                } else {
                     $(".empty-template :input").attr("disabled", true);
                }
                // 将导出按钮移至最后
                $(".exportlinks", $("#subcontractPaymentForm")).appendTo($("#subcontractPaymentForm"));
                
                var maskMoneyCfg = {defaultZero: true, allowZero:true, precision:2};
                function initTableInput(allowInput) {
                	// 部分仅任务编辑的都设置为disabled,在指定任务开启编辑
                	$(".taskOnly:input").attr("disabled", true);
                	$(".task-" + taskKey).removeAttr("disabled");
                    $(".empty-template :input").each(function() {
                        $(this).attr("disabled", true);
                    });
                	var $tbody = $("#subcontractPaymentTable tbody");
                    $(".readonly", $tbody).each(function() {
                        var readonly = "true" == ($(this).text() || $(this).val() || "false");
                        var $tr = $(this).parents("tr:first");
                        $tr.addClass(readonly ? "readLine" : "inputLine");
                    });
                    refreshListIndex($tbody);
                    // 是否允许编辑
                    if (!allowInput) {
                        return;
                    }
                    $("tr.readLine .glyphicon-minus", $tbody).hide();
                    $("tr.inputLine .text-input", $tbody).each(function(index) {
                        var value = $.trim($(this).text()) || $.trim($(this).find("input").val()) || "";
                        var name = $.trim($.trim($(this).attr('class')).replace(/(\btext-input\b)|(\bhidden\b)|(\bnowrap\b)/g, ""));
                        var cssClass = "";
                        if (name.indexOf("ignore") > -1) {
                            cssClass = "ignore";
                            name = $.trim(name.replace("ignore", ""));
                        }
                        var rowNum = $(this).parent().index();
                        var perfix = "subcontractPaymentList[" + rowNum + "].";
                        var html = "<input name='" + perfix + name + "' value='" + value + "' class='form-control " + cssClass +"'>";
                        $(this).html(html);
                        if (name.indexOf("Time") > -1) {
                            $(this).find("input").datepicker({
                                changeMonth: true,
                                changeYear: true,
                                option: {
                                    dateFormat:"yy-mm-dd"
                                }
                            });
                        }
                        if (name.indexOf("amount") > -1) {
                            $(this).find("input").maskMoney(maskMoneyCfg);
                        }
                    });
                    // 只允许修改比例
                    // $(".amount").find("input").attr("readonly", true);
                    // 只允许修改金额
                    $(".ratio").find("input").attr("readonly", true);
                }
                initTableInput();
                
                function checkPaymentForm() {
                   /*  var isEm = <s:property value='user.isHasRole(13)'/> || false;
                    var isEmLeader = <s:property value='user.isHasRole(10)'/> || false; */
                    var selectors = "#subcontractPaymentForm tbody :input, .glyphicon-plus, .glyphicon-minus";
                    var $inputs = $(selectors).filter(":not(.empty-template :input)")
                    if (!(((<s:property value='user.isHasRole(13)'/> || false) || (<s:property value='user.isHasRole(10)'/> || false))/*  || (state == "0" || state == '') */)) {
                        $inputs.attr("disabled", true);
                        $("#paymentSubmitBtn").hide();
                        // 申请付款允许保存草稿
                        if ($("#applyPaymentBtn:visible").length != 0) {
                            $("#paymentSubmitBtn").show();
                        }
                        if ($("#applyPaymentBtn:visible, .taskSubmitBtn:visible").length == 0) {
                    	    return false;
                        } else {
                            //$("#subcontractPaymentForm input[type='hidden']").removeAttr("disabled");
                            $inputs.filter("[type='hidden']").removeAttr("disabled");
                        }
                    }
                    // 有申请付款按钮，则允许编辑付款信息
                    if ($("#applyPaymentBtn:visible, .taskSubmitBtn:visible").length != 0) {
                        initTableInput(true);
                        $("#subcontractPaymentTable :input, .glyphicon-plus, .glyphicon-minus").filter(":not(.empty-template :input)").removeAttr("disabled");
                    }
                    if (state == "100") {
                        //$("#subcontractPaymentForm input").not("input[type='file']").not("input[type='hidden']").attr("disabled", true);
                        $inputs.attr("disabled", true);
                    }
                    
                    $(".inputLine:not(.empty-template)").each(function() {
                        var ratio = Number($(this).find(".ratio input").val()) || 0;
                        var amount = Number($.trim($(this).find(".amount input").val()).replace(/,/g, "")) || 0;
                        if (!ratio || !amount) {
                        	$(this).addClass("emptyLine")
                            $(this).find(".glyphicon-minus").click();
                        } else {
                        	$(this).removeClass("emptyLine")
                        }
                    })
                    
                    if (!calcRatio()) {
                        return false;
                    }
                    return true;
                }
                
                checkPaymentForm();
                
                function refreshListIndex($tbody) {
                    $(".rowNum", $tbody).each(function(index){
                        $(this).text(index + 1);
                        $(this).parent().find("input[name]").each(function () {
                            var name = $(this).attr("name");
                            $(this).attr("name", name.replace(/\[\d+\]/, "[" + index + "]"));
                        });
                    });
                }
                
                function calcAmount() {
                    var sumAmount = 0;
                    var paiedAmount = 0;
                    $("#subcontractPaymentTable tbody .amount").each(function(){
                    	var $tr = $(this).parents("tr:first");
                        var amount = Number(($.trim($(this).text()) || $.trim($(this).find("input").val())).replace(/,/g, "")) || 0;
                        var $paidAmount = $tr.find(".paidAmount");
                        var paidAmt = Number(($.trim($paidAmount.text()) || $.trim($paidAmount.find("input").val())).replace(/,/g, "")) || amount;
                        if (!isNaN(amount)) {
                            sumAmount += amount;
                            try {
                                var $paymentTime = $tr.find(".paymentTime");
                                var paymentTime = $.trim($paymentTime.text()) || $.trim($paymentTime.find("input").val()) || "";
                                if (paymentTime) {
                                       paiedAmount += paidAmt;
                                }
                            } catch(e) {}
                        }
                    });
                    sumAmount = sumAmount.toLocaleString();
                    paiedAmount = paiedAmount.toLocaleString();
                    if (sumAmount.indexOf(".") == -1) {
                        sumAmount += ".00";
                    } else if (sumAmount.indexOf(".") + 2 == sumAmount.length) {
                        sumAmount += "0";
                    }
                    if (paiedAmount.indexOf(".") == -1) {
                        paiedAmount += ".00";
                    } else if (paiedAmount.indexOf(".") + 2 == paiedAmount.length) {
                        paiedAmount += "0";
                    }
                    $("#sumAmount").text("已提：" +sumAmount);
                    $("#paiedAmount").text("已付：" + paiedAmount);
                    
                    var sumApprovedAmount = 0;
                    var sumApprovingAmount = 0;
                    $("#subcontractPaymentTable tbody .approvedAmount").each(function(){
                        var amount = Number(($.trim($(this).text()) || $.trim($(this).find("input").val()) || $.trim($(this).val())).replace(/,/g, "")) || 0;
                        var approvingAmount = Number(($.trim($(this).find("input").val()) || $.trim($(this).val())).replace(/,/g, "")) || 0;
                        if (!isNaN(amount)) {
                        	sumApprovedAmount += amount;
                        	sumApprovingAmount += approvingAmount;
                        }
                    });
                    sumApprovedAmount = sumApprovedAmount.toLocaleString();
                    sumApprovingAmount = sumApprovingAmount.toLocaleString();
                    if (sumApprovedAmount.indexOf(".") == -1) {
                    	sumApprovedAmount += ".00";
                    } else if (sumApprovedAmount.indexOf(".") + 2 == sumApprovedAmount.length) {
                    	sumApprovedAmount += "0";
                    }
                    if (sumApprovingAmount.indexOf(".") == -1) {
                    	sumApprovingAmount += ".00";
                    } else if (sumApprovingAmount.indexOf(".") + 2 == sumApprovingAmount.length) {
                    	sumApprovingAmount += "0";
                    }
                    $("#sumApprovedAmount").text("合计：" + sumApprovedAmount);
                    $("#approvingAmount").text("本次：" + sumApprovingAmount);
                    return true;
                }
                calcAmount();
            })
        </script>
    </div>
</body>
</html>