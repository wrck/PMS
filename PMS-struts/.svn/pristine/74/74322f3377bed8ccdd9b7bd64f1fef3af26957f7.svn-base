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
	<p class="redMark">因市场原因，此项目实际发货与订单不一致，现场施工以此清单为准，如有疑问请联系此项目销售确认。</p><br>
	<display:table
		name="realOrderDataList" pagesize="${realOrderDataList.size()}" id="displaytable5"
		size="${realOrderDataList.size()}" sort="external" export="true"
		decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
		requestURI="module/sub/checkRealOrderData.action"
		partialList="true">
		<display:column property="contractNo" titleKey="pm.orderdata.contractNo"></display:column>
		<display:column property="productSubCode" titleKey="pm.orderdata.itemCode"></display:column>
		<display:column property="productSubModel" titleKey="pm.orderdata.model"></display:column>
		<display:column property="productSubName" titleKey="pm.orderdata.itemName"></display:column>
		<display:column property="num" titleKey="pm.orderdata.orderQuantity"></display:column>
		<display:setProperty name="export.excel.filename" value='实际发货清单.xls'/>
	</display:table>
</body>
</html>