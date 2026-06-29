<%@ page import="com.dp.plat.util.Base64Util"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<body>
	<display:table name="probRestoreList" pagesize="${probRestoreList.size()}" export="true"
		size="${probRestoreList.size()}" sort="external" id="probRestoreList"
		decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-striped"
		partialList="true">
		<display:column property="projectCheckBox" titleKey="prob.info.checkbox"></display:column>
		<display:column property="serialNum" titleKey="prob.info.serial.num"></display:column>
		<display:column property="itemModel" titleKey="prob.info.product.type"></display:column>
		<display:column property="conp" titleKey="prob.info.conp"></display:column>
		<display:column property="cpld" titleKey="prob.info.cpld"></display:column>
		<display:column property="boot" titleKey="prob.info.boot"></display:column>
		<display:column property="pcb" titleKey="prob.info.pcb"></display:column>
		<display:column property="projectNamea" titleKey="pm.project.projectName"></display:column>
        <display:column property="projectCode" titleKey="pm.project.projectCode"></display:column>
		<display:column property="contractNo" titleKey="pm.contract"></display:column>
		<display:column property="officeName" titleKey="pm.officearea"></display:column>
        <display:column property="marketName" titleKey="pm.presales.marketName"></display:column>
        <display:column property="systemName" titleKey="pm.presales.systemName"></display:column>
        <display:column property="expendName" titleKey="pm.presales.expendName"></display:column>
        <display:column property="industryName" titleKey="pm.presales.industryName"></display:column>
	</display:table>
	<%-- <table class="table table-striped">
		<s:if test="probRestoreList == null">
			<tr>
				<th><s:text name="prob.info.checkbox"></s:text></th>
				<th><s:text name="prob.info.serial.num"></s:text></th>
				<th><s:text name="prob.info.product.type"></s:text></th>
				<th><s:text name="prob.info.conp"></s:text></th>
				<th><s:text name="prob.info.boot"></s:text></th>
				<th><s:text name="prob.info.cpld"></s:text></th>
				<th><s:text name="prob.info.pcb"></s:text></th>
				<th><s:text name="pm.project.projectName"></s:text></th>
				<th><s:text name="pm.contract"></s:text></th>
				<th><s:text name="pm.officearea"></s:text></th>
			</tr>
			<tr><td colspan="10">请选择查询条件进行查询</td></tr>
		</s:if>
		<s:else>
			<thead>
				<tr><td colspan="8">共查询到<s:property value="probRestoreList.size()"/>条数据记录</td></tr>
			</thead>
			<tr>
				<th><s:text name="prob.info.checkbox"></s:text></th>
				<th><s:text name="prob.info.serial.num"></s:text></th>
				<th><s:text name="prob.info.product.type"></s:text></th>
				<th><s:text name="prob.info.conp"></s:text></th>
				<th><s:text name="prob.info.boot"></s:text></th>
				<th><s:text name="prob.info.cpld"></s:text></th>
				<th><s:text name="prob.info.pcb"></s:text></th>
				<th><s:text name="pm.project.projectName"></s:text></th>
				<th><s:text name="pm.contract"></s:text></th>
				<th><s:text name="pm.officearea"></s:text></th>
			</tr>
			<s:iterator value="probRestoreList" var="restore" status="index">
			<tr>
				<td>
					<input type="checkbox" name="probRestoreTaskList[<s:property value='#index.index'/>].ischecked" value="0">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].serialNum" value="<s:property value='#restore.serialNum'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].itemModel" value="<s:property value='#restore.itemModel'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].conp" value="<s:property value='#restore.conp'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].boot" value="<s:property value='#restore.boot'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].cpld" value="<s:property value='#restore.cpld'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].pcb" value="<s:property value='#restore.pcb'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].projectName" value="<s:property value='#restore.projectName'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].contractNo" value="<s:property value='#restore.contractNo'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].officeCode" value="<s:property value='#restore.officeCode'/>">
					<input type="hidden" name="probRestoreTaskList[<s:property value='#index.index'/>].projectId" value="<s:property value='#restore.projectId'/>">
					
				</td>
				<td><s:property value="#restore.serialNum"/></td>
				<td><s:property value="#restore.itemModel"/></td>
				<td><s:property value="#restore.conp"/></td>
				<td><s:property value="#restore.boot"/></td>
				<td><s:property value="#restore.cpld"/></td>
				<td><s:property value="#restore.pcb"/></td>
				<td>
					<a target="blank" href="${pageContext.request.contextPath }/module/ProjectModify.action?project.projectId=<s:property value="#restore.projectId" />&result=310">
						<s:property value="#restore.projectName"/>
					</a>
				</td>
				<td><s:property value="#restore.contractNo"/></td>
				<td><s:property value="#restore.officeName"/></td>
			</tr>
			</s:iterator>
		</s:else>
	</table> --%>
</body>
</html>