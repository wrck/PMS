<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<head>
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.workflowmanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.deploymanage' />">
<script>
function doActionCheck(formid, action)
{
	if("invalid"==action || "valid" == action)
	{
		if(checkSelect()==false)
		{
			alert("<s:text name='sys.listinvalid.selected'/>");
			return false;
		} 
		if("invalid"==action && false==confirm("<s:text name='sys.listinvalid.confirm'/>"))
		{
			return false;
		}
	}
	return true;  
}
function delDeployment(deploymentId){
	if(confirm("确认删除？删除后不可恢复！")){
		window.location.href = "work/WorkFlowDelDeployment.action?param.deploymentId="+deploymentId;
	}
}


function checkSubmit(){
	var filename =  $("#filename").val();
	var file = $("#file").val();
	if(filename == "" || file == ""){
		alert("流程名称与流程文件均不能为空，谢谢！");
		return false;
	}
	document.getElementById("deployForm").submit();
	
}
</script>
</head>
<body>
<div class="listView divHeader" >
		<img src="images/right_zhishi.gif" border="0">
					<s:text name="workflow.add"></s:text>
</div>
<s:form enctype="multipart/form-data" method="POST" action="work/WorkFlowNewDeploy.action" id="deployForm" cssClass="form-inline">
	<div class="form-group form-group-query">
		<dp:fielderror accesskey="errmsg" onlyone="true" />
		<label for="filename" class="form-text-label"><span class="redmark">*</span><s:text name="workflow.name" /></label>
		<s:textfield name="filename" id="filename"
									cssClass="form-control" ></s:textfield>
	</div>
	<div class="form-group" >
		<label for="file" class="form-text-label"><span class="redmark">*</span><s:text name="workflow.file" /></label>
		
	</div>
	<div class="form-group form-group-query">
		<s:file name="file" id="file"  cssClass="normalInput" cssStyle="font-size:14px;width:200px;height: 20px;"/>
	</div>
	<div class="form-group form-group-query" >
		<button type="button" class="btn btn-default" onclick="checkSubmit()" ><s:text name="sys.confirm"></s:text></button>
	</div>
</s:form>
<div class="listView divHeader div-height" >
		<img src="images/right_zhishi.gif" border="0">
					<s:text name="deployinfo.manager"></s:text>
</div>
	<div>
<s:form method="post" action="work/WorkFlowAction.action" onsubmit="return sys_submit(this);" >

		<display:table name="listDeployment" pagesize="${displayParam.pagesize}" export="false"
			size="${displayParam.totalcount}" sort="external"
			requestURI="work/WorkFlowAction.action"
			decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
			partialList="true">
			<display:column property="id" titleKey="workflow.id"></display:column>
			<display:column property="name" titleKey="workflow.name"></display:column>
			<display:column property="deploymentTime" titleKey="workflow.release.time" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
			<display:column property="workFlowDelDeployment" titleKey="display.operate"></display:column>
		</display:table>
	<dp:errormsg />
</s:form>
</div>
<div class="listView divHeader div-height" >
		<img src="images/right_zhishi.gif" border="0">
					<s:text name="workflow.define.manager"></s:text>
</div>
<div>
	<s:form method="post" action="work/WorkFlowAction.action" onsubmit="return sys_submit(this);">
		<display:table
				name="listProcessDefinition" pagesize="${displayParam.pagesize}" export="false"
				size="${displayParam.totalcount}" sort="external"
				requestURI="work/WorkFlowAction.action"
				decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
				partialList="true">
				
				<display:column property="id" titleKey="workflow.id" sortable="true"></display:column>
				<display:column property="name" titleKey="workflow.deploy.name"></display:column>
				
				<display:column property="key" titleKey="workflow.key"></display:column>
				<display:column property="version" titleKey="workflow.versions"></display:column>
				
				<display:column property="resourceName" titleKey="workflow.rule.filename"></display:column>
				<display:column property="diagramResourceName" titleKey="workflow.rule.imgname"></display:column>
				
				<display:column property="deploymentId" titleKey="workflow.deploy.id"></display:column>
				
				<display:column property="workFlowViewImage" titleKey="display.operate"></display:column>
			</display:table>
		<dp:errormsg />
	</s:form>
</div>
</body>
</html>