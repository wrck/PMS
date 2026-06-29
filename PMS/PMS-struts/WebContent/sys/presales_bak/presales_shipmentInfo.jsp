<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<style type="text/css">
legend {
	font: 12px/24px "微软雅黑"
}
</style>
</head>
<body>
	<fieldset>
		<legend><b>发货信息</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<thead>
			<tr class="warning">
				<td><s:text name="pm.shipment.contractNo"></s:text></td>
				<td><s:text name="pm.shipment.barCode"></s:text></td>
				<td><s:text name="pm.shipment.itemCode"></s:text></td>
				<td><s:text name="pm.shipment.itemName"></s:text></td>
				<td><s:text name="pm.shipment.receiveName"></s:text></td>
				<td><s:text name="pm.shipment.emsNum"></s:text></td>
				<td><s:text name="pm.shipment.packdate"></s:text></td>
				<td><s:text name="pm.shipment.emsCompany"></s:text></td>
			</tr>
			</thead>
			<tbody>
			<s:if test="shipmentInfos.size()== 0">
				<tr>
					<td colspan="8">
						无可以显示的数据
					</td>
				</tr>
			</s:if>
			<s:iterator value="shipmentInfos" var="p">
				<tr>
					<td><s:property value="#p.contractNo"/></td>
					<td><s:property value="#p.barCode"/></td>
					<td><s:property value="#p.itemCode"/></td>
					<td><s:property value="#p.itemName"/></td>
					<td><s:property value="#p.receiveName"/></td>
					<td><s:property value="#p.emsNum"/></td>
					<td><s:date name="#p.packdate" format="yyyy-MM-dd" /></td>
					<td><s:property value="#p.emsCompany"/></td>
				</tr>	
			</s:iterator>
			</tbody>
		</table>
	</fieldset>
</body>
</html>
