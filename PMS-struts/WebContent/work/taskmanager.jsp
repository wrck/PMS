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
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.task' />">
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
</script>
</head>
<body>
<s:form method="post" action="module/ChannelManage.action" id="ChannelManage"
	onsubmit="return sys_submit(this);">
	<table class=queryControl width="100%" cellSpacing=0 cellPadding=0>
		<tr>
			<td width="70%">
			<table>
				
			</table>
			</td>
		</tr>
	</table>
	<table class="listView" cellSpacing="0" cellPadding="0">
		<tbody>
			<TR>
				<TH class="tableHeader"><img src="images/right_zhishi.gif"
					border="0"><s:text name="workflow.selftask.list"></s:text></TH>
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
					name="plist" pagesize="${displayParam.pagesize}" export="false"
					size="${displayParam.totalcount}" sort="external"
					requestURI="module/ChannelManage.action"
					decorator="com.dp.plat.decorators.Wrapper" class="displayTable"
					partialList="true">
					<display:column property="id" titleKey="workflow.id"></display:column>
					<display:column property="description" titleKey="workflow.description"></display:column>
					<display:column property="name" titleKey="workflow.selftask.name"></display:column>
					<display:column property="createTime" format="{0,date,yyyy-MM-dd HH:mm:ss}" titleKey="workflow.startTime"></display:column>
					<display:column property="assignee" titleKey="workflow.transactor"></display:column>
					<display:column property="selfTaskOperator" titleKey="display.operate" media="html"></display:column>
					
					<display:setProperty name="export.excel.filename"
						value='<%=StringEscUtil.getText("workflow.selftask.list")%>'>
					</display:setProperty>
				</display:table></td>
			</tr>
			
			
			<tr>
				<td width="100%" nowrap align="right"><display:table
					name="phislist" pagesize="${displayParam.pagesize}" export="false"
					size="${displayParam.totalcount}" sort="external"
					requestURI="module/ChannelManage.action"
					decorator="com.dp.plat.decorators.Wrapper" class="displayTable"
					partialList="true">
					<display:column property="id" titleKey="workflow.id"></display:column>
					<display:column property="startTime" format="{0,date,yyyy-MM-dd HH:mm:ss}" titleKey="workflow.startTime"></display:column>
					<display:column property="startUserId" titleKey="workflow.transactor"></display:column>
					<display:column property="taskOperator" titleKey="display.operate" media="html"></display:column>
					
					<display:setProperty name="export.excel.filename"
						value='<%=StringEscUtil.getText("workflow.selftask.list")%>'>
					</display:setProperty>
				</display:table></td>
			</tr>
		</tbody>
	</table>
	<dp:errormsg />
</s:form>
</body>
</html>
