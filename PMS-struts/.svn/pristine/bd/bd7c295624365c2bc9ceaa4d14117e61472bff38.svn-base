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
	<div style="text-align: left;">
		<display:table style="text-align: left;"
			name="shipmentInfoList" pagesize="${shipmentInfoList.size()}" export="false" id="shipmentInfoTable"
			size="${shipmentInfoList.size()}" sort="external" requestURI="module/sub/chooseShipmentInfo.action"
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" 
			partialList="true">
			<display:column property="checkboxWrapper" class="smEdit" titleKey="pm.shipment.check"></display:column>
			<%-- <display:column headerClass="warning" titleKey="pm.shipment.check">
                <input name="selected" type="checkbox" class="smEdit" value="${shipmentInfoTable.barCode}">
			</display:column> --%>
			<display:column property="contractNo" titleKey="pm.shipment.contractNo"></display:column>
			<display:column property="barCodeRelation" titleKey="pm.shipment.barCode"></display:column>
			<display:column property="itemCodeRelation" titleKey="pm.shipment.itemCode"></display:column>
			<display:column property="itemNameRelation" titleKey="pm.shipment.itemName"></display:column>
			<display:column property="transferFlagWrapper" titleKey="pm.project.transferFlag"></display:column>
		</display:table>
    </div>
</body>
</html>