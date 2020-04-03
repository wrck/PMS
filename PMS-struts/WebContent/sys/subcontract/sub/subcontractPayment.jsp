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
        <s:form enctype="multipart/form-data" id="subcontractPaymentForm" action="module/subcontract_savePayment.action">
            <table id="subcontractInfoTable" class="table table-bordered table-hover table-striped ">
                <tr style="display:none"><td><s:hidden name="subcontract.id"/></td></tr>
                <tr>
                    <td><s:text name="pm.subcontract.subcontractNo"></s:text>:</td>
                    <td class="col-sm-4"><s:textfield name="subcontract.subcontractNo" cssClass="form-control" placeholder='请输入转包合同号'/></td>
                    <td><s:text name="pm.subcontract.accrued"></s:text>:</td>
                    <td><s:radio name="subcontract.isAccrued" list="#{true:'是',false:'否'}"/></td>
                    <td><s:text name="pm.subcontract.invoiced"></s:text>:</td>
                    <td><s:radio name="subcontract.isInvoiced" list="#{true:'是',false:'否'}"/></td>
                    <td><s:text name="pm.subcontract.deliver"></s:text>:</td>
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
	            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped">
                <display:column title="序号" headerClass="text-center" class="text-center rowNum">
                    ${subcontractPaymentTable_rowNum}
                </display:column>
                <display:column property="id" headerClass="hidden" class="text-input id hidden" title="hidden" media="html"></display:column>
                <display:column property="subcontractId" headerClass="hidden" class="text-input subcontractId hidden ignore" title="hidden" media="html"></display:column>
                <display:column property="ratio" class="text-input ratio" titleKey="pm.subcontract.ratio"></display:column>
                <display:column property="amount" class="text-input amount" titleKey="pm.subcontract.amount"></display:column>
                <display:column property="confirmTime" class="text-input confirmTime" titleKey="pm.subcontract.confirmTime" format="{0,date,yyyy-MM-dd}"></display:column>
                <display:column property="paymentTime" class="text-input paymentTime" titleKey="pm.subcontract.paymentTime" format="{0,date,yyyy-MM-dd}"></display:column>
                <display:column property="remark" class="text-input remark" titleKey="pm.subcontract.remark"></display:column>
                <display:column title="操作" media="html">
                    <span class="glyphicon glyphicon-plus text-success"></span>
                    <span class="glyphicon glyphicon-minus text-danger"></span>
                </display:column>
	            <display:footer>
	                <s:if test="%{subcontractPaymentList.size() == 0}">
	                    <tr class="empty-template hidden">
	                        <td class="text-center rowNum">1</td>
	                        <td class="text-input subcontractId hidden ignore"><input name="subcontractPaymentList[0].subcontractId" value='<s:property value="subcontract.id"/>' class="form-control"></td>
	                        <td class="text-input ratio"><input name="subcontractPaymentList[0].ratio" class="form-control"></td>
	                        <td class="text-input amount"><input name="subcontractPaymentList[0].amount" class="form-control"></td>
                            <td class="text-input confirmTime"><input name="subcontractPaymentList[0].confirmTime" class="form-control"></td>
	                        <td class="text-input paymentTime"><input name="subcontractPaymentList[0].paymentTime" class="form-control"></td>
	                        <td class="text-input remark"><input name="subcontractPaymentList[0].remark" class="form-control"></td>
	                        <td>
	                            <span class="glyphicon glyphicon-plus text-success"></span>
	                            <span class="glyphicon glyphicon-minus text-danger" style="display:none;"></span>
	                        </td>
	                    </tr>
	                </s:if>
	                <tr id="sumTr" class='text-danger' style='font-weight: 800;'>
	                    <td colspan='1' class='text-right'>合计</td>
	                    <td colspan='1' id="sumRatio"></td>
	                    <td colspan='1' ><div id="sumAmount"></div><div id="paiedAmount"></div></td>
	                    <td  id="delIds"></td>
	                    <td class="mergeTdBorder">
	                        <s:if test="%{(user.isHasRole(10) || user.isHasRole(13)) && subcontract.state != 100}">
	                           <button id="paymentSubmitBtn" type="submit" class="btn btn-success"><span class="glyphicon glyphicon-floppy-save" style="font-size:12px;"></span> 保存</button>
	                        </s:if>
	                    </td>
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
                    <s:hidden name="workflowCommonParam.flag" value="1"></s:hidden>
                </s:if>
                <s:if test="%{workflowCommonParam.outcome == 'applyPaymentTask'}">
                    <s:hidden name="workflowCommonParam.flag" value="1"></s:hidden>
	                <button id="applyPaymentBtn" type="submit" class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 申请付款</button>
	                <script type="text/javascript">
	                    $(function(){
	                    	$("#applyPaymentBtn").appendTo($("#subcontractPaymentTable tfoot #delIds"));
	                    });
	                </script>
                </s:if>
                <s:if test="%{workflowCommonParam.outcome == 'approvePaymentTask'}">
                    <fieldset class="form-group">
                        <s:hidden name="workflowCommonParam.flag" value="0"></s:hidden>
                        <label>审批意见：</label><s:radio list="#{'5':'部分付款', '2':'可以闭环','-1':'闭环驳回'}" name="workflowCommonParam.approveStatus"  listCssStyle="margin-right:1rem;"></s:radio>
                        <s:textarea name="workflowCommonParam.comment" cssClass="form-control" placeholder="审批意见"/>
                    </fieldset>
                    <button id="approvePaymentBtn" class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
                </s:if>
            </s:if>
	        </div>
        </s:form>
        <script type="text/javascript" src="js/jquery.maskMoney.min.js"></script>
        <script type="text/javascript">
	        $(function(){
	        	var ratioRegx = /^-?(100(\.0{1,2})?|(([1-9]\d|\d)(\.\d{1,2})?))$/;
	        	function calcRatio() {
	        		var sumRatio = 0;
	        		var isValid = true;
	                $("#subcontractPaymentTable tbody .ratio").each(function(){
	                	var str = $.trim($(this).text()) || $.trim($(this).find("input").val());
	                	if (str != '' && str.match(ratioRegx) == null) {
	                		isValid = false;
	                		alert("请输入有效的比例!");
	                		return;
	                	}
	                    var ratio = Number($.trim($(this).text())) || Number($.trim($(this).find("input").val())) || 0;
	                    if (!isNaN(ratio)) {
	                        sumRatio += ratio;
	                    }
	                });
	                if (String(sumRatio).match(ratioRegx) == null) {
                        alert("总比例无效，请检查!");
                        return false;
	                }
	                $("#sumRatio").text(sumRatio);
	                return isValid;
	        	}
	        	calcRatio();
	        	//$("#subcontractPaymentTable tbody").append("<tr class='text-danger' style='font-weight: 800;'><td colspan='1' class='text-right'>合计</td><td colspan='2'>" + sumRatio + "</td></tr>");
	        	
	        	var $emptyTr = $("#subcontractPaymentTable tbody .empty");
                if ($emptyTr.length == 1) {
                    $emptyTr.remove();
                    $(".empty-template").appendTo($("#subcontractPaymentTable tbody")).removeClass("hidden empty-template");
                }
                // 将导出按钮移至最后
                $(".exportlinks", $("#subcontractPaymentForm")).appendTo($("#subcontractPaymentForm"));
                
                var maskMoneyCfg = {defaultZero: true, allowZero:true, precision:2};
	        	$("#subcontractPaymentTable tbody .text-input").each(function(index) {
	        		var value = $.trim($(this).text()) || $.trim($(this).find("input").val()) || "";
	        		var name = $.trim($.trim($(this).attr('class')).replace(/(\btext-input\b)|(\bhidden\b)/g, ""));
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
	        	
	        	function checkPaymentForm() {
                   /*  var isEm = <s:property value='user.isHasRole(13)'/> || false;
                    var isEmLeader = <s:property value='user.isHasRole(10)'/> || false; */
                    if (!(((<s:property value='user.isHasRole(13)'/> || false) || (<s:property value='user.isHasRole(10)'/> || false))/*  || (state == "0" || state == '') */)) {
                        $("#subcontractPaymentForm input").attr("disabled", true);
                        $("#paymentSubmitBtn").hide();
                        if ($("#applyPaymentBtn:visible").length == 0) {
                        	return false;
                        } else {
                        	$("#subcontractPaymentForm input[type='hidden']").removeAttr("disabled");
                        }
                    }
                    if (state == "100") {
                    	//$("#subcontractPaymentForm input").not("input[type='file']").not("input[type='hidden']").attr("disabled", true);
                    	$("#subcontractPaymentForm input").attr("disabled", true);
                    }
                    if (!calcRatio()) {
                    	return false;
                    }
                    return true;
                }
                
	        	checkPaymentForm();
	        	
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
	        	$(document).on("click", "#approvePaymentBtn", function() {
                    $("input[name='workflowCommonParam.flag']", $("#subcontractPaymentForm")).val("1");
                });
	        	
	        	$(document).on("click", ".glyphicon-plus", function(){
	        		var $sourceTr = $(this).parents("tr:first");
	        		$sourceTr.find(".glyphicon-minus").show();
	        		var $targetTr = $sourceTr.clone();
	        		$sourceTr.after($targetTr);
	        		$targetTr.find("input[name]").not(".ignore").val("");
	        		$targetTr.find("input[name*='Time']").removeAttr("id").removeClass("hasDatepicker").datepicker({
                        changeMonth: true,
                        changeYear: true,
                        option: {
                            dateFormat:"yy-mm-dd"
                        }
                    });
	        		$(".rowNum").each(function(index){
	        			$(this).text(index + 1);
	        			$(this).parent().find("input[name]").each(function () {
	        				var name = $(this).attr("name");
	        				$(this).attr("name", name.replace(/\[\d+\]/, "[" + index + "]"));
	        			});
	        		});
	        		//calcRatio();
	        	})
	        	
	        	$(document).on("click", ".glyphicon-minus", function(){
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
                    $(".rowNum").each(function(index){
                        $(this).text(index + 1);
                        $(this).parent().find("input[name]").each(function () {
                            var name = $(this).attr("name");
                            $(this).attr("name", name.replace(/\[\d+\]/, "[" + index + "]"));
                        });
                    });
                    if (calcRatio()) {
                    	calcAmount();
                    };
                })
                
                $(document).on("change", ".ratio input[name*='ratio']", function(){
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
                
                function calcAmount() {
	        		var sumAmount = 0;
	        		var paiedAmount = 0;
                    $("#subcontractPaymentTable tbody .amount").each(function(){
                        var amount = Number(($.trim($(this).text()) || $.trim($(this).find("input").val())).replace(/,/g, "")) || 0;
                        if (!isNaN(amount)) {
                        	sumAmount += amount;
                        	try {
		                        var $paymentTime = $(this).parents("tr:first").find(".paymentTime");
		                        var paymentTime = $.trim($paymentTime.text()) || $.trim($paymentTime.find("input").val()) || "";
	                            if (paymentTime) {
	                            	   paiedAmount += amount;
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
	        	}
                calcAmount();
                
                $(document).on("change", "#subcontractPaymentTable tbody .amount", function(){
                	calcAmount();
                });
	        })
        </script>
	</div>
</body>
</html>