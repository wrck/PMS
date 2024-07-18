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
    <s:if test="%{workflowCommonParam.taskId != null}">
	    <s:form enctype="multipart/form-data" id="profitSmApproveForm" action="%{namespace}/subcontract_savePayment.action">
	        <div id="profitSmApproveDiv" style="margin-bottom:20px;">
	           <fieldset class="hidden">
	                <s:hidden name="subcontract.id"></s:hidden>
	                <s:hidden name="workflowCommonParam.instId"></s:hidden>
	                <s:hidden name="workflowCommonParam.taskId"></s:hidden>
	                <s:hidden name="workflowCommonParam.outcome"></s:hidden>
	                <s:hidden name="workflowCommonParam.objId"></s:hidden>
	                <s:hidden name="workflowCommonParam.flag" value="1"></s:hidden>
	            </fieldset>
	            <fieldset class="form-group">
	                <label><s:property value="workflowCommonParam.customInfo.taskDesc" default="受益部门服务经理"/>审批</label>
	            </fieldset>
	            <fieldset class="form-group">
	                <label>审批意见：</label><s:radio id="approveStatus" list="#{'1':'通过','-1':'驳回'}" name="workflowCommonParam.approveStatus"  listCssStyle="margin-right:1rem;"></s:radio>
	                <s:textarea name="workflowCommonParam.comment" cssClass="form-control" placeholder="审批意见"/>
	            </fieldset>
	            <button class="btn btn-info"><span class="glyphicon glyphicon-ok" style="font-size:12px;"></span> 提交审批</button>
	        </div>
	    </s:form>
    </s:if>
    <div style="position: relative;">
        <s:if test="%{(subcontract.state > 0 && subcontract.state < 100 ) && (user.isHasRole(1) || user.isHasRole(10) || user.isHasRole(13))}">
            <button id="terminateBtn" type="button" class="btn btn-danger"><span class="glyphicon glyphicon-off" style="font-size:12px;"></span> 终止流程</button>
        </s:if>
        <a id="querySubcontractView" class="pull-right" target="_blank" href="" style=" position: relative; right: 0;">
            <span class="glyphicon glyphicon-picture" style="font-size:12px; color:#428bca;"><span class="panel-heading">查看项目转包流程图&nbsp;</span></span>
        </a>
        <script type="text/javascript">
        $("#querySubcontractView").click(function(){
            $.ajax({
                url:"viewDeployment.action",
                type:"post",
                dataType:"json",
                data:{procdefKey:"Subcontract"},
                success:function(data){
                    window.open("work/WorkFlowViewImage.action?param.deploymentId="+data.deploymentId+"&param.imageName="+data.imageName);
                },
                error:function(){
                    alert('error!');
                }
            });
            return false;
        });
        $("#terminateBtn").click(function(){
        	var subcontractId = $("#subcontractId").val();
        	console.log(subcontractId);
        	var comment = prompt("终止流程说明", "终止流程");
        	if (comment) {
        		$.ajax({
                    url:"module/s/subcontractAjax_terminateWorkFlow.action",
                    type:"post",
                    dataType:"json",
                    data:{"subcontract.id": subcontractId, "workflowCommonParam.comment": comment},
                    success:function(data){
                        if (data.result == "1") {
                            alert('终止流程成功');
                            window.location.reload();
                            //getTargetFunc("subcontractCommentListDiv");
                        } else {
                            alert(data.result);
                        }
                    },
                    error:function(XMLHttpRequest,textStatus,errorThrown){
                        alert("Error");
                    }
                });
        	}
			return false;
        });
        </script>
    </div>
	<div style="text-align: left;">
		<display:table style="text-align: left;"
            name="subcontractCommentList" export="false" id="commentListTable"
            size="${subcontractCommentList.size()}" sort="external"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped">
            <display:column property="processName" titleKey="pm.subcontract.processName"></display:column>
            <display:column property="taskName" titleKey="pm.subcontract.taskName"></display:column>
            <display:column property="assigneeName" titleKey="pm.subcontract.assigneeName"></display:column>
            <display:column property="statusName" titleKey="pm.subcontract.statusName"></display:column>
            <display:column property="comment" titleKey="pm.subcontract.comment"></display:column> 
            <display:column property="assigneeTime" titleKey="pm.subcontract.assigneeTime"></display:column> 
            <display:column property="nextAssigneeName" title="下一步办理人"></display:column>
            <display:column property="subcontractSeeQuesnaireLink" titleKey="pm.subcontract.operate"></display:column>
        </display:table>
	</div>
</body>
</html>