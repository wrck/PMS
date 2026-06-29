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
<meta name="function" content="<s:text name='procDefDelegate.title' />">
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
	<table class="queryControl" width="100%" cellSpacing="0" cellPadding="0">
		<tr>
			<td width="60%">
				<!-- 
					<table>
					<tr>
						<td class="normalText" width="70px"><s:text name="sys.channellist.channelCode"></s:text>
						</td>
						<td width="200px"><s:textfield name="channel.channelCode"
							cssClass="normalInput" cssStyle="width:110px" placeholder="%{getText(\"sys.channellist.channelCode\")}"></s:textfield></td>
						<td class="normalText" width="70px"><s:text name="sys.channellist.channelName"></s:text>
						</td>
						<td width="200px"><s:textfield name="channel.channelName"
							cssClass="normalInput" cssStyle="width:200px" placeholder="%{getText(\"sys.channellist.channelName\")}"></s:textfield></td>
					</tr>
					<tr>
						<td colspan="2">
							<s:text name="sys.channellist.level"></s:text>&nbsp;&nbsp;&nbsp;
							<s:select name="channel.level" list="#{null:'请选择',1:'区域分销商',2:'金牌代理商',3:'认证代理商',4:'非认证代理商'}"
							cssClass="normalInput" cssStyle="width:103px"></s:select>
						</td>
						<td colspan="2">
							<s:text name="sys.customer.officeCode"></s:text>&nbsp;&nbsp;&nbsp;
							
							<s:text name="sys.customer.market"></s:text>&nbsp;&nbsp;&nbsp;
							
						</td>
					</tr>
				</table>
				 -->
			</td>
			<td valign="bottom">
				<!-- <s:submit value="%{getText('button.find')}" cssClass="buttonNormal"></s:submit> -->
			</td>
		</tr>
	</table>
	<table class="listView" cellSpacing="0" cellPadding="0" style="margin-top: 15px;">
		<tbody>
			<TR>
				<TH class="tableHeader"><img src="images/right_zhishi.gif"
					border="0"><s:text name="procDefDelegate.list"></s:text></TH>
			</TR>
			<TR>
				<TD class="border2">
				<TABLE class="pageinfo">
					<TBODY>
						<TR>
							<td>
							<table>
								<tr>
									<TD><INPUT class="smallbuttonBold" 
										onclick="doAction('ChannelManage','add', 'work/AddProcDefDelegate.action');"
										type="button" value="<s:text name='procDefDelegate.add'/>" />
									</TD>
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
					name="pdlist" pagesize="${displayParam.pagesize}" export="false"
					size="${displayParam.totalcount}" sort="external"
					requestURI="module/ChannelManage.action"
					decorator="com.dp.plat.decorators.Wrapper" class="displayTable"
					partialList="true">
					<display:column property="owner" titleKey="procDefDelegate.owner"></display:column>
					<display:column property="ownerName" titleKey="procDefDelegate.ownerName"></display:column>
					<display:column property="assignee" titleKey="procDefDelegate.assignee"></display:column>
					<display:column property="assigneeName" titleKey="procDefDelegate.assigneeName"></display:column>
					<display:column property="startTime" format="{0,date,yyyy-MM-dd}"
						titleKey="procDefDelegate.startTime"></display:column>
					<display:column property="endTime" format="{0,date,yyyy-MM-dd}"
						titleKey="procDefDelegate.endTime"></display:column>
					<display:column property="procDefDelegateStatus"
						titleKey="sys.project.isapply"></display:column>
					<display:column property="procDefDelegateOperator" titleKey="display.operate"
							media="html"></display:column>
				</display:table></td>
			</tr>
		</tbody>
	</table>
	<dp:errormsg />
</s:form>
</body>
</html>
