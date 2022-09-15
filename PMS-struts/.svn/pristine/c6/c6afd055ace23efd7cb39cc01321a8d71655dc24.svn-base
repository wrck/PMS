<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
</head>
<body>
<div style="text-align: right;">
		<a onclick="softversion(this)" class="btn btn-default" href="javascript:void(0)">编辑</a>
		<a id="updateSoftVersion" class="btn btn-default" href="javascript:void(0)" onclick="updateSoftVersion()" style="display: none">保存</a>
		<a onclick="checkhistsoftversion()" class="btn btn-default" href="javascript:void(0)">查看历史版本</a>
		<!-- <a onclick="checkShipmentInfo1()" class="btn btn-default" href="javascript:void(0)">查看发货序列号</a> -->
	</div>	
	<div id="softversionSee">
		<display:table name="softversionList" pagesize="${softversionList.size()}" export="true" id="softversionList"
			size="${softversionList.size()}" sort="external" requestURI="module/sub/checkSoftVersion.action" 
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
			partialList="true" >
			<display:column property="contractNo" titleKey="pm.shipment.contractNo"></display:column>
			<display:column property="barCodeRelation" titleKey="pm.shipment.barCode"></display:column>
			<display:column property="itemCodeRelation" titleKey="pm.shipment.itemCode"></display:column>
			<display:column property="itemNameRelation" titleKey="pm.shipment.itemName"></display:column>
			<display:column property="conp" titleKey="prob.info.conp"></display:column>	
			<display:column property="cpld" titleKey="prob.info.cpld"></display:column>	
			<display:column property="boot" titleKey="prob.info.boot"></display:column>	
			<display:column property="pcb" titleKey="prob.info.pcb"></display:column>
            <display:setProperty name="export.excel.filename" value="版本信息.xls" />
		</display:table>
	</div>
	<div id="softversionEdit" style="display: none">
		<s:form id="softversionForm" >
		<table class="table">
			<tr>
				<th><s:text name="pm.shipment.contractNo"></s:text></th>
				<th><s:text name="pm.shipment.barCode"></s:text></th>
			<%-- 	<th><s:text name="pm.shipment.itemCode"></s:text></th> --%>
				<th><s:text name="pm.shipment.itemName"></s:text></th>
				<th><s:text name="prob.info.conp"></s:text></th>
				<th width="100px"><s:text name="prob.info.cpld"></s:text></th>
				<th width="100px"><s:text name="prob.info.boot"></s:text></th>
				<th width="100px"><s:text name="prob.info.pcb"></s:text></th>
				<th><s:textfield placeholder="执行更新时间" title="软件版本升级执行时间，若默认今天，可不填" name="executeTimeHeader" cssClass="form-control softUpdateExecuteTime"/></th>
			</tr>
			<s:iterator value="softversionList" var="bar" status="index">
				<tr class="itemCode_<s:property value="itemCode"/>">
					<td>
						<s:property value="contractNo"/> 
						<s:hidden name="softversionList[%{#index.index}].projectId" value="%{project.projectId}"></s:hidden>
					</td>
					<td><s:property value="barCode"/> 
                        <s:if test="#bar.barCode2 != null">
                            <br><span class='text-danger'>(<s:property value="#bar.barCode2"/>)</span>
                        </s:if>
						<s:hidden name="softversionList[%{#index.index}].barCode" value="%{#bar.barCode}"></s:hidden>
					</td>
					<%-- <td><s:property value="itemCode"/> </td> --%>
					<td><s:property value="itemName"/> </td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].conp"  bakValue="%{#bar.conp}"  value="%{#bar.conp}" cssClass="form-control software clearconp"/> 
						<s:hidden name="softversionList[%{#index.index}].conpChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.conpChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].conpBak" value="%{#bar.conpBak}->%{#bar.conp}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].conpBak" value="%{#bar.conp}"></s:hidden>
						</s:else>
					</td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].cpld" bakValue="%{#bar.cpld}" value="%{#bar.cpld}" cssClass="form-control software"/>  
						<s:hidden name="softversionList[%{#index.index}].cpldChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.cpldChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].cpldBak" value="%{#bar.cpldBak}->%{#bar.cpld}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].cpldBak" value="%{#bar.cpld}"></s:hidden>
						</s:else>
					</td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].boot" bakValue="%{#bar.boot}" value="%{#bar.boot}" cssClass="form-control software"/>  
						<s:hidden name="softversionList[%{#index.index}].bootChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.bootChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].bootBak" value="%{#bar.bootBak}->%{#bar.boot}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].bootBak" value="%{#bar.boot}"></s:hidden>
						</s:else>
					</td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].pcb" bakValue="%{#bar.pcb}" value="%{#bar.pcb}" cssClass="form-control software"/>  
						<s:hidden name="softversionList[%{#index.index}].pcbChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.pcbChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].pcbBak" value="%{#bar.pcbBak}->%{#bar.pcb}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].pcbBak" value="%{#bar.pcb}"></s:hidden>
						</s:else>
					</td>
					<td><s:textfield name="softversionList[%{#index.index}].executeTime" cssClass="form-control softUpdateExecuteTime"/></td>
				</tr>
			</s:iterator>
			<tr>
				<!-- 更新说明 -->
				<td><s:text name="prob.info.change.remark"></s:text></td>
				<td colspan="3">
					<s:hidden name="softChangeLog.projectId" value="%{project.projectId}"></s:hidden>
					<s:textarea rows="5" name="softChangeLog.changeRemark" cssClass="form-control" ></s:textarea>
				</td>
				<td colspan="4"><span id = "changeRemarkMsg"></span></td>
			</tr>
			<tr>
				<!-- 简化更新方式 -->
				<td>批量更新</td>
				<td colspan="4">
				    <input style="width: 120px;display: inline;" type="text" id="softItemCode" class="form-control" placeholder='产品编码（选填）'/>
					<select style="width: 130px;display: inline;" class="form-control" id="softType">
						<option value="conp">软件版本号</option>
						<option value="cpld">CPLD版本号</option>
						<option value="boot">ConBoot版本号</option>
						<option value="pcb">PCB版本号</option>
					</select>
					<input style="width: 200px;display: inline;" type="text" id="oldSoftVersion" class="form-control"/>
					<span style="display: inline;">----></span>
					<input style="width: 200px;display: inline;" type="text" id="newSoftVersion" class="form-control"/>
				    <a href="javascript:void(0)" onclick="bacthUpdate()">批量操作</a>
				    <a href="javascript:void(0)" onclick="bacthReset()" style="margin-left:1rem">重置</a>
				</td>
				<td colspan="4"></td>
			</tr>
		</table>
		</s:form>
	</div>
	<div class="backTop">
	    <i class='glyphicon glyphicon-arrow-up'></i>
	</div>
	<div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
</body>
</html>