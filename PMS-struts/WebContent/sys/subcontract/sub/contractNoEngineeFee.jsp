<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
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
	<div style="text-align: left;">
	   <s:form id="subcontractAmountForm" action="module/subcontract_audit.action">
		<display:table style="text-align: left;"
            name="subcontractPriceList" pagesize="${subcontractPriceList.size()}" export="false" id="engineeFeeTable"
            size="${subcontractPriceList.size()}" sort="external"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped">
            <display:column property="id" title="ID" class="id hidden type-hide" headerClass="hidden"></display:column>
            <display:column property="subcontractId" title="ID" class="subcontractId hidden type-hide" headerClass="hidden"></display:column>
            <display:column property="objId" title="ID" class="objId hidden type-hide" headerClass="hidden"></display:column>
            <display:column property="procType" title="ID" class="procType hidden type-hide" headerClass="hidden"></display:column>
            <display:column property="projectCode" title="ID" class="projectCode hidden type-hide" headerClass="hidden"></display:column>

            <display:column property="contractNo" titleKey="pm.subcontract.contractNos" class="contractNo type-hide"></display:column>
            <display:column property="orderExecNumber" titleKey="pm.subcontract.orderExecNumber" class="orderExecNumber type-hide"></display:column><!--  url="https://sms.dptech.com/module/BusinessView43.action" paramId="param.objId" paramProperty="objId" paramId="dpActProcDesc.procType" paramProperty="applyTye" -->
            <display:column property="engineeFee" titleKey="pm.subcontract.engineeFee"  class="engineeFee type-hide" decorator="com.dp.plat.decorators.AmountThousandth"></display:column>
            <display:column property="price" titleKey="pm.subcontract.price" class="price text-input"></display:column>
            <display:column property="smsTargetUrl" title="SMS链接" media="html"></display:column>
        </display:table>
        <s:if test="%{workflowCommonParam.taskId != null}">
                <fieldset class="hidden">
                    <s:hidden name="subcontract.id"></s:hidden>
                    <s:hidden name="workflowCommonParam.instId"></s:hidden>
                    <s:hidden name="workflowCommonParam.taskId"></s:hidden>
                    <s:hidden name="workflowCommonParam.outcome"></s:hidden>
                    <s:hidden name="workflowCommonParam.objId"></s:hidden>
                </fieldset>
                <fieldset class="form-group">
                    <label>审批意见：</label><s:radio id="approveStatus" list="#{'1':'通过','-1':'驳回'}" name="workflowCommonParam.approveStatus"  listCssStyle="margin-right:1rem;"></s:radio>
                    <s:textarea name="workflowCommonParam.comment" cssClass="form-control" placeholder="审批意见"/>
                </fieldset>
                <button class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
            <s:if test="%{workflowCommonParam.outcome == 'approveTask'}">
	            <script type="text/javascript" src="js/jquery.maskMoney.min.js"></script>
	            <script type="text/javascript">
		            $(function() {
		                $("#subcontractTotalPrice").maskMoney();
		                $("#subcontractAmountForm input[type='radio'][id^='approveStatus']").change(function() {
		                    if(this.value == "1") {
		                        /* $("#subcontractAmountDiv").removeClass("hidden");
		                        $("#subcontractAmountDiv").show(); */
		                        initForm();
		                    } else {
		                    	initForm(true);
		                        /* $("#subcontractTotalPrice").val(null);
		                        $("#subcontractAmountDiv").hide(); */
		                    }
		                })
		                
		                $(document).on("change", "#engineeFeeTable tbody input[name*='.price']", function() {
		                	calcPrice();
	                    })
		                //initForm();
		            })
		            
		            function initForm(reset) {
		            	if (reset) {
		            		$(".text-input, .type-hide","#engineeFeeTable tbody").find("input[type='hidden']").remove();
		            		$(".text-input, #totalPrice","#engineeFeeTable tbody").each(function(){
		            			$(this).text($(this).children().data("bakvalue"));
		            		});
		            		return;
		            	}
		            	$(".text-input, .type-hide","#engineeFeeTable tbody").each(function(index) {
		                    var value = $.trim($(this).text()) || $.trim($(this).find("input").val()) || "";
		                    var name = $.trim($.trim($(this).attr('class')).replace(/(\btext-input\b)|(\bhidden\b)/g, ""));
		                    var cssClass = "";
		                    if (name.indexOf("ignore") > -1) {
		                        cssClass = "ignore";
		                        name = $.trim(name.replace("ignore", ""));
		                    }
		                    if (name.indexOf("type-hide") > -1) {
	                            type = "hidden";
	                            name = $.trim(name.replace("type-hide", ""));
	                        } else {
	                        	type = "text";
	                        }
		                    var rowNum = $(this).parent().index();
		                    var perfix = "subcontractPriceList[" + rowNum + "].";
		                    var html = "<input type='" + type + "' name='" + perfix + name + "' value='" + value + "' data-bakValue='" + value + "' class='form-control " + cssClass +"'>";
		                    if (type== "hidden") {
		                    	$(this).append(html);
		                    } else {
		                    	$(this).html(html);
		                    }
		                    if (name.indexOf("Time") > -1) {
		                        $(this).find("input").datepicker({
		                            changeMonth: true,
		                            changeYear: true,
		                            option: {
		                                dateFormat:"yy-mm-dd"
		                            }
		                        });
		                    }
		                });
		            	var totalPrice = $("#totalPrice").text();
		            	var html = "<input id='subcontractTotalPrice' name='subcontract.subcontractAmount' value='" + totalPrice + "' data-bakValue='" + totalPrice + "' class='form-control'>";
		                $("#totalPrice").html(html);
		            	$("input[name*='.price']").maskMoney({defaultZero: true,allowZero:true,precision:0});
		            	$("#subcontractTotalPrice").maskMoney({defaultZero: true,allowZero:true,precision:0});
		            }
	            </script>
            </s:if>
        </s:if>
        </s:form>
        <script type="text/javascript">
	        $(function(){
	        	var sum = 0;
	        	var totalPrice = 0;
	        	$("#engineeFeeTable tbody .engineeFee").each(function(){
	        		var price = $.trim(this.text).replace(/,/g, "");
	        		var fee = Number(price);
	        		if (!isNaN(fee)) {
	        			sum += fee;
	        		}
	        		this.text = (fee).toLocaleString('en-US');
	        	});
	        	$("#engineeFeeTable tbody .price").each(function(){
	        		var price = $.trim(this.text).replace(/,/g, "");
                    var fee = Number(price);
                    if (!isNaN(fee)) {
                    	totalPrice += fee;
                    }
                    this.text = (fee).toLocaleString('en-US');
                });
	        	sum = (sum).toLocaleString('en-US');
	        	totalPrice = (totalPrice).toLocaleString('en-US');
	        	$("#engineeFeeTable tbody").append("<tr class='text-danger' style='font-weight: 800;'><td colspan='2' class='text-right'>合计</td><td>" + sum + "</td><td id='totalPrice'>" + totalPrice + "</td><td></td></tr>");
	        })
	        function calcPrice() {
	        	var totalPrice = 0;
	        	$("#engineeFeeTable tbody input[name*='.price']").each(function(){
                    var price = $.trim(this.value).replace(/,/g, "");
                    var fee = Number(price);
                    if (!isNaN(fee)) {
                    	totalPrice += fee;
                    }
                });
	        	$("#subcontractTotalPrice").maskMoney("mask", totalPrice);
	        }
        </script>
	</div>
</body>
</html>