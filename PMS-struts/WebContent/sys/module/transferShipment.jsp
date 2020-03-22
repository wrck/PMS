<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
<style type="text/css">
    #transferForm button {
        width: 74px;
    }
</style>
<script type="text/javascript">
$(function () {
	triggerTransferPointProjectLabel('<s:property value="transferType" default="1"/>');
	$("#transferForm button.btn-info").click(function() {
		$(this).bootstrapBtn("loading");
	})
	$("#transferForm input[type='radio'][name='transferType']").change(function(){
		triggerTransferPointProjectLabel($(this).val());
	})
})

/*
保存安装地址		
*/
function transferSubmit(btn){
	$(btn).bootstrapBtn("loading");
	if (checkshipform(btn)) {
		$("#transferForm input#result").val(2);
		return true;
	}
	window.event.preventDefault();
	$(btn).bootstrapBtn("reset");
    return false;
}

function checkshipform(){
	var sel = "#transferForm input[name='selected']:checked";
	var chProjectId = $("#transferForm input#project_projectId").val();
    var transferProjectId = $("#transferForm input#transferProject_projectId").val();
    if (transferProjectId == chProjectId) {
    	alert("转出项目与转入项目相同，请重新选择！");
    	return false;
    } 
    if ($(sel).length > 0){
        return true;
    }
	alert("请至少选择一条序列号信息");
	return false;
}

function nextTransfer(btn){
	$(btn).bootstrapBtn("loading");
	var alertText = "请选择转入的目标项目！";
	var $transferProject = $("#transferForm input[name='selected'][type='radio']:checked");
	var transferType = $("#transferForm input[type='radio'][name='transferType']:checked").val();
    if ($transferProject.length > 0) {
    	// 1：转出，0：转入
    	var currentProjectId = $("#transferForm #projectId").val();
    	var currentContractNo = window.parent.$("#contractNo").val();
    	var currentProjectCode = window.parent.$("#projectCode").val();
    	var currentProjectName = window.parent.$("#projectName").val();
    	var $parent = $transferProject.parents("tr");
    	if (transferType == "1") {
            $("#transferForm input#contractNo").val(currentContractNo);
            $("#transferForm input#project_projectId").val(currentProjectId);
            $("#transferForm input#project_projectCode").val(currentProjectCode);
            $("#transferForm input#project_projectName").val(currentProjectName);
            
            $("#transferForm input#transferProject_projectId").val($transferProject.val());
            $("#transferForm input#transferProject_projectCode").val($parent.find(".transferProjectCode").text());
            $("#transferForm input#transferProject_projectName").val($parent.find(".transferProjectName .updateMark").text());
    	} else {
            $("#transferForm input#contractNo").val();
            $("#transferForm input#project_projectId").val($transferProject.val());
            $("#transferForm input#project_projectCode").val($parent.find(".transferProjectCode").text());
            $("#transferForm input#project_projectName").val($parent.find(".transferProjectName .updateMark").text());
            
            $("#transferForm input#transferProject_projectId").val(currentProjectId);
            $("#transferForm input#transferProject_projectCode").val(currentProjectCode);
            $("#transferForm input#transferProject_projectName").val(currentProjectName);
    	}
    	var chProjectId = $("#transferForm input#project_projectId").val();
        var transferProjectId = $("#transferForm input#transferProject_projectId").val();
        if (transferProjectId != chProjectId) {
        	$("#transferForm input#result").val(1);
            return true;
        } else {
        	alertText = "转出项目与转入项目相同，请重新选择！";
        }
    }
    alert(alertText);
    $(btn).bootstrapBtn("reset");
    window.event.preventDefault();
    return false;
}

function prevTransfer(btn){
	$(btn).bootstrapBtn("loading");
    $("#transferForm input#result").val(0);
}

function triggerTransferPointProjectLabel(transferType) {
	if (transferType == "1") {
        $("#tranferPointProjectLabel").text("转出至某项目：");
    } else {
        $("#tranferPointProjectLabel").text("从某项目转入：");
    }
}
</script>
</head>
<body>
	<div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="panel panel-default">
					<form method="post" action="${pageContext.request.contextPath }/module/sub/transferShipment.action" id="transferForm" class="form-horizontal" name="transferForm">
						<s:hidden name="result"></s:hidden>
						<s:hidden name="contractNo"></s:hidden>
						<s:hidden name="projectId"></s:hidden>
						<s:hidden name="project.projectId"></s:hidden>
						<s:hidden name="project.projectName"></s:hidden>
						<s:hidden name="project.projectCode"></s:hidden>
						<s:hidden name="transferProject.projectId"></s:hidden>
                        <s:hidden name="transferProject.projectName"></s:hidden>
                        <s:hidden name="transferProject.projectCode"></s:hidden>
						<s:if test="result == 0">
                            <div class="panel-body">
                                <div class="form-group">
                                    <label for="projectCode" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label" style="width: 120px;padding-right:0px;">转移方式：</label>
                                    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
                                        <s:radio list="#{0:'转入',1:'转出'}" id='transferType' name='transferType' cssClass="pull-left"></s:radio>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label id="tranferPointProjectLabel" for="projectCode" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label" style="width: 120px;padding-right:0px;">转入目标项目：</label>
                                    <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
                                        <s:textfield id="projectCode" name="projectCode" cssClass="form-control" placeholder='请输入项目编码/合同号'></s:textfield>
                                    </div>
                                    <button type="submit" id="submit"  class="btn btn-info" data-loading-text="查询中..."><s:text name='sys.query' /></button>
                                    <button type="submit" onclick="nextTransfer(this)" class="btn btn-success" style="margin-left:1rem;" data-loading-text="处理中...">确定</button>
                                </div>
                                <div>
                                    <display:table style="text-align: left;"
                                        name="projectlist" pagesize="${projectlist.size()}" export="false" id="projectDisplay"
                                        size="${projectlist.size()}" sort="external" 
                                        decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
                                        partialList="true">
                                        <display:column property="projectRadioWrapper" title="请选择" media="html"></display:column>
                                        <display:column property="projectCode" titleKey="pm.project.projectCode" class="transferProjectCode"></display:column>
                                        <display:column property="projectNameWarrper" style="width:240px" class="transferProjectName" titleKey="pm.project.projectName" media="html"></display:column> 
                                        <display:column property="contractNo" titleKey="pm.contract" class="transferContractNo" decorator="com.dp.plat.decorators.ContractNoList"></display:column>
                                    </display:table>
                                </div>
                                <!-- <div class="form-group">
                                    <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                        <button type="submit" onclick="nextTransfer()" class="btn btn-default btn-block form-group-query" style="width: 60px;display: inline-block;">下一步</button>
                                    </div>
                                </div> -->
                            </div>
                        </s:if>
						<s:if test="result == 1">
	                        <s:hidden name="projectCode"></s:hidden>
	                        <s:hidden name="transferType"></s:hidden>
							<div class="panel-body">
	                            <div class="form-group">
	                                <label for="contractNo" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label"><s:text name="pm.project.contractNo"></s:text></label>
	                                <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
	                                    <%-- <s:textfield id="contractNo" name="project.contractNo" cssClass="form-control"></s:textfield>
	                                 --%>
	                                   <%-- <select id="projectContractNos" class="form-control">
	                                       <option>-请选择-</option>
	                                   </select> --%>
	                                   <s:select name="project.contractNo" list="contractNoList" cssClass="form-control" headerKey="''" headerValue="-请选择-"></s:select>
	                                </div>
	                                <button type="submit" id="submit" class="btn btn-info" data-loading-text="查询中..."><s:text name='sys.query'/></button>
                                    <button type="submit" onclick="transferSubmit(this)" class="btn btn-success" style="margin-left:1rem;" data-loading-text="转移中...">转移</button>
                                    <button type="submit" onclick="prevTransfer(this)" class="btn btn-default" style="margin-left:1rem;" data-loading-text="返回中...">返回</button>
                                </div>
								<div style="text-align: right;">
									<display:table style="text-align: left;"
										name="shipmentInfoList" pagesize="${shipmentInfoList.size()}" export="false" id="shipmentInfoDisplay"
										size="${shipmentInfoList.size()}" sort="external" 
										decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
										partialList="true">
										<display:column property="checkboxWrapper" titleKey="pm.shipment.check" media="html"></display:column>
										<display:column property="contractNo" titleKey="pm.shipment.contractNo"></display:column>
										<display:column property="barCodeRelation" titleKey="pm.shipment.barCode"></display:column>
										<display:column property="itemCodeRelation" titleKey="pm.shipment.itemCode"></display:column>
										<display:column property="itemNameRelation" titleKey="pm.shipment.itemName"></display:column>
									</display:table>
								</div>
	                        </div>
	                    </s:if>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>