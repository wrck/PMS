<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<head>
<s:head />
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='sys.subcontract.facilitator.management'/>">
<style type="text/css">
.buttonDiv{
	float:left;
	margin-left: 50px;
	margin-bottom: 30px;
	margin-top: 50px;	
}
.ui-multiselect {
padding: 2px 0 2px 4px;
text-align: left;
min-height: 35px;
min-width: 382px;
}
</style>
<script type="text/javascript">
$(function(){
    date_picker("effectiveTo");
    date_picker("effectiveFrom");
});
function checkallsub(){	
	var submitButtonObj=$("#submitButton");
	submitButtonObj.attr("disabled", "disabled");	
	if(minxueCheckSubmit($("#facilitatorName").val(),"服务商名称",submitButtonObj,new Array(1,1))){
		return false;
	}
	if(minxueCheckSubmit($("#bankInfo").val(),"开户行信息",submitButtonObj,new Array(1,1))){
		return false;
	}
	if(minxueCheckSubmit($("#bankAccount").val(),"付款账户",submitButtonObj,new Array(1,0))){
		return false;
	}
	if(minxueCheckSubmit($("#email").val(),"邮箱地址",submitButtonObj,new Array(0,1))){
        return false;
    }
	if(!confirm("确定保存服务商？")) {
		$("#submitButton").removeAttr("disabled");
		return false;
	}
	return true;
}
</script>
</head>
<body>
<div class="divHeader div-bottom" >
	<img src="images/right_zhishi.gif" border="0">服务商信息
</div>
<s:form method="post" action="module/subcontract_facilitatorEdit.action" id="mainForm" onsubmit="return checkallsub();" cssClass="form-horizontal" name="mainForm">
    <s:hidden name="subcontractFacilitator.id" id="facilitatorId"></s:hidden>
	<div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group">
                <label for="facilitatorName"  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="pm.subcontract.facilitator"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.name" id="facilitatorName" cssClass="form-control" placeholder="请输入服务商名称" />
                </div>
            </div>
            <div class="form-group">
                <label for="bankInfo"  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="pm.subcontract.facilitator.bankInfo"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.bankInfo" id="bankInfo" cssClass="form-control" placeholder="请输入服务商开户行" />
                </div>
            </div>
            <div class="form-group">
                <label for="bankAccount"  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="pm.subcontract.facilitator.bankAccount"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.bankAccount" id="bankAccount" cssClass="form-control" placeholder="请输入服务商付款账号" />
                </div>
            </div>
            <div class="form-group">
                <label for="receiver"  class="col-sm-1 control-label"><span class="redmark"></span><s:text name="pm.subcontract.facilitator.receiver"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.receiver" id="receiver" cssClass="form-control" placeholder="请输入服务商邮箱收件人" />
                </div>
            </div>
            <div class="form-group">
                <label for="email"  class="col-sm-1 control-label"><span class="redmark"></span><s:text name="pm.subcontract.facilitator.email"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.email" id="email" cssClass="form-control" placeholder="请输入服务商邮箱地址" />
                </div>
            </div>
            <div class="form-group">
                <label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.effectiveFrom"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.effectiveFrom" id="effectiveFrom" cssClass="form-control" placeholder="有效开始时间"/>
                </div>
            </div>
            <div class="form-group">
                <label  class="col-sm-1 control-label"><s:text name="fnd.basic.data.effectiveTo"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:textfield name="subcontractFacilitator.effectiveTo" id="effectiveTo" cssClass="form-control" placeholder="有效结束时间"/>
                </div>
            </div>
            <div class="form-group">
                <label for="facilitatorState"  class="col-sm-1 control-label"><span class="redmark">*</span><s:text name="pm.subcontract.state"></s:text><span>:</span></label>
                <div class="col-sm-4">
                    <s:radio name="subcontractFacilitator.state" list="#{true:'有效', false:'失效'}" id="facilitatorState" placeholder="请输入服务商付款账号" />
                </div>
            </div>
        </div>
    </div>
	<div class="form-group">
        <label  class="col-sm-1 control-label">&nbsp;</label>
		<div class="col-sm-1">
			<button type="submit" id="submitButton" style="width: 80px" class="btn btn-default  btn-block btn-sm"><s:text name='sys.confirm' /></button>
	    </div>
	    <div class="col-sm-1">
	        <button type="button" style="width: 80px" class="btn btn-default btn-block btn-sm" onclick="javaScript:window.location.href='module/subcontract_facilitatorList.action'"><s:text name='sys.back' /></button>
	    </div>
    </div>
</s:form>
</body>
</html>