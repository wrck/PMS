<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.workflowmanage' />">
<meta name="function" content="<s:text name='workflow.selfhistask.list' />">
<script>
$(function(){
	var applyNum = $("#applyNum").val();
	if(applyNum == 0){
		$("#applyNum").val("");
	}
});
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
</script>
</head>
<body>
<s:form method="post" action="work/WorkFlowSelfHisTaskManager.action" id="SelfHisTaskManager"
	onsubmit="return sys_submit(this);">
	<table class=queryControl width="100%" cellSpacing=0 cellPadding=0>
		<tr>
			<td width="100%">
				<table border="0">
					<tr>
						<td class="normalText"><s:text name="dpActProcDesc.desc"></s:text></td>
						<td>
							<s:select name="dpActProcDesc.procType" list="daptlist"
							headerKey="0" headerValue="请选择" listKey="id" listValue="desc"
							cssClass="normalInput" cssStyle="width:110px"></s:select>
						</td>
							
						<td class="normalText"><s:text name="sys.project.code"></s:text></td>
						<td>
							<s:textfield name="dpActProcDesc.projectCode"
							cssClass="normalInput" cssStyle="width:150px"></s:textfield>
						</td>
							
						<td class="normalText"><s:text name="sys.project.name"></s:text></td>
						<td>
							<s:textfield name="dpActProcDesc.projectName"
							cssClass="normalInput" cssStyle="width:150px"></s:textfield>
						</td>
					</tr>
					<tr>
						<td class="normalText"><s:text name="dpActProcDesc.username"></s:text></td>
						<td>
							<s:textfield name="dpActProcDesc.username"
							cssClass="normalInput" cssStyle="width:106px"></s:textfield>
						</td>
							
						<td class="normalText"><s:text name="dpActProcDesc.realName"></s:text></td>
						<td>
							<s:textfield name="dpActProcDesc.realName"
							cssClass="normalInput" cssStyle="width:150px"></s:textfield>
						</td>
							
						<td class="normalText"><s:text name="dpActProcDesc.num"></s:text></td>
						<td>
							<s:textfield name="dpActProcDesc.applyNum" id="applyNum"
							cssClass="normalInput" cssStyle="width:150px"></s:textfield>
						</td>
						
						<td valign="bottom"><s:submit value="%{getText('button.find')}"
							cssClass="buttonNormal"></s:submit></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table class="listView" cellSpacing="0" cellPadding="0">
		<tbody>
			<TR>
				<TH class="tableHeader"><img src="images/right_zhishi.gif"
					border="0"><s:text name="workflow.selfhistask.list"></s:text></TH>
			</TR>
			<TR>
				<TD class="border2">
				<TABLE class="pageinfo">
					<TBODY>
						<TR>
							<td>
							<table>
								<tr>
									
								</tr>
							</table>
							</td>
							<td class="Range" noWrap width="25%"><s:text
								name="sys.list.pagesize"></s:text> <dp:pagesize
								displayParam="displayParam" formid="ChannelManage" /></td>
						</TR>
					</TBODY>
				</TABLE>
				</TD>
			</TR>
			<tr>
					<td width="100%" nowrap align="right"><display:table
						name="dapdlist" pagesize="${displayParam.pagesize}" export="false"
						size="${displayParam.totalcount}" sort="external"
						requestURI="work/WorkFlowSelfHisTaskManager.action"
						decorator="com.dp.plat.decorators.Wrapper" class="displayTable"
						partialList="true">
						<display:column property="applyNum" titleKey="dpActProcDesc.num"></display:column>
						<display:column property="procTypeName" titleKey="dpActProcDesc.desc"></display:column>
						<display:column property="projectCode" titleKey="sys.project.code"></display:column>
						<display:column property="projectName" titleKey="sys.project.name"></display:column>
						<display:column property="username" titleKey="dpActProcDesc.username"></display:column>
						<display:column property="realName" titleKey="dpActProcDesc.realName"></display:column>
						
						
						<display:column property="name" titleKey="workflow.selftask.name"></display:column>
						<display:column property="startTime" format="{0,date,yyyy-MM-dd HH:mm}" titleKey="workflow.startTime"></display:column>
						<display:column property="endTime" format="{0,date,yyyy-MM-dd HH:mm}" titleKey="workflow.endTime"></display:column>
						<display:column property="assignee" titleKey="workflow.transactor"></display:column>
						<display:column property="seeSelfHidOperteTask" titleKey="display.operate"></display:column>
					</display:table></td>
			</tr>
		</tbody>
	</table>
	<dp:errormsg />
</s:form>
</body>
</html>
