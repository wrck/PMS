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
                size="${subcontractPaymentList.size()}" sort="external" 
                decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped">
                <display:column title="序号" headerClass="text-center" class="text-center rowNum">
                    ${subcontractPaymentTable_rowNum}
                </display:column>
                <display:column property="id" headerClass="hidden" class="text-input id hidden" title="hidden"></display:column>
                <display:column property="subcontractId" headerClass="hidden" class="text-input subcontractId hidden ignore" title="hidden"></display:column>
                <display:column property="ratio" class="text-input ratio" titleKey="pm.subcontract.ratio"></display:column>
                <display:column property="amount" class="text-input amount" titleKey="pm.subcontract.amount"></display:column>
                <display:column property="confirmTime" class="text-input confirmTime" titleKey="pm.subcontract.confirmTime" format="{0,date,yyyy-MM-dd}"></display:column>
                <display:column property="paymentTime" class="text-input paymentTime" titleKey="pm.subcontract.paymentTime" format="{0,date,yyyy-MM-dd}"></display:column>
                <display:column property="remark" class="text-input remark" titleKey="pm.subcontract.remark" media="html"></display:column>
                <display:column title="操作">
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
                        <td colspan='1' id="sumAmount"></td>
                        <td  id="delIds"></td>
                        <td class="mergeTdBorder">
                            <s:if test="%{(user.isHasRole(10) || user.isHasRole(13)) && subcontract.state != 100}">
                               <button id="paymentSubmitBtn" type="submit" class="btn btn-success"><span class="glyphicon glyphicon-floppy-save" style="font-size:12px;"></span> 确定</button>
                            </s:if>
                        </td>
                        <td class="mergeTdBorder"></td>
                        <td class="mergeTdBorder"></td>
                    </tr>
                </display:footer>
            </display:table>
        </s:form>
        <s:if test="%{taskParam.taskId != null}">
            <s:form id="subcontractPaymentForm" action="module/subcontract_close.action">
                <fieldset class="hidden">
                    <s:hidden name="subcontract.id"></s:hidden>
                    <s:hidden name="taskParam.subcontractId"></s:hidden>
                    <s:hidden name="taskParam.instId"></s:hidden>
                    <s:hidden name="taskParam.taskId"></s:hidden>
                    <s:hidden name="taskParam.objId"></s:hidden>
                </fieldset>
                <fieldset class="form-group">
                    <label>闭环意见：</label><s:radio list="#{'2':'可以闭环','-2':'无法闭环'}" name="taskParam.result"  listCssStyle="margin-right:1rem;"></s:radio>
                    <s:textarea name="taskParam.message" cssClass="form-control" placeholder="闭环意见"/>
                </fieldset>
                <button class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
            </s:form>
        </s:if>
        <s:if test="%{workflowCommonParam.taskId != null}">
            <s:form id="subcontractPaymentForm" action="module/subcontract_close.action">
                <fieldset class="hidden">
                    <s:hidden name="subcontract.id"></s:hidden>
                    <s:hidden name="workflowCommonParam.instId"></s:hidden>
                    <s:hidden name="workflowCommonParam.taskId"></s:hidden>
                    <s:hidden name="workflowCommonParam.objId"></s:hidden>
                    <s:hidden name="workflowCommonParam.outcome"></s:hidden>
                </fieldset>
                <fieldset class="form-group">
                    <label>闭环意见：</label><s:radio list="#{'2':'可以闭环','-2':'无法闭环'}" name="workflowCommonParam.approveStatus"  listCssStyle="margin-right:1rem;"></s:radio>
                    <s:textarea name="workflowCommonParam.comment" cssClass="form-control" placeholder="闭环意见"/>
                </fieldset>
                <button class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
            </s:form>
        </s:if>
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
                });
                
                 function checkPaymentForm() {
                   /*  var isEm = <s:property value='user.isHasRole(13)'/> || false;
                    var isEmLeader = <s:property value='user.isHasRole(10)'/> || false; */
                    if (!(((<s:property value='user.isHasRole(13)'/> || false) || (<s:property value='user.isHasRole(10)'/> || false)) || (state == "0" || state == ''))) {
                        $("#subcontractPaymentForm input").attr("disabled", true);
                        $("#paymentSubmitBtn").hide();
                        return false;
                    }
                    if (state == "100") {
                        //$("#subcontractPaymentForm input").not("input[type='file']").not("input[type='hidden']").attr("disabled", true);
                        $("#subcontractPaymentForm input").attr("disabled", true);
                    }
                    return true;
                }
                
                checkPaymentForm();
                $(document).on("submit", "#subcontractPaymentForm", function() {
                    return checkPaymentForm();
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
                        $(this).parent().next().children().val(amount.toFixed(2)).maskMoney({defaultZero: true,allowZero:true,precision:2});
                        calcAmount();
                    };
                });
                
                function calcAmount() {
                    var sumAmount = 0;
                    $("#subcontractPaymentTable tbody .amount").each(function(){
                        var amount = Number($.trim($(this).text())) || Number($.trim($(this).find("input").val())) || 0;
                        if (!isNaN(amount)) {
                            sumAmount += amount;
                        }
                    });
                    $("#sumAmount").text(sumAmount.toLocaleString());
                }
                calcAmount();
            })
        </script>
    </div>
</body>
</html>