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
	<display:table
		name="commonList" pagesize="${commonList.size()}" id="projectLeaseLineListTable"
		size="${commonList.size()}" sort="external" export="true"
		decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
		requestURI="module/sub/projectSub_projectLeaseLine.action"
		partialList="true">
		<display:column property="projectCode" titleKey="pm.project.projectCode"></display:column>
		<display:column property="productSubCode" titleKey="pm.orderdata.itemCode"></display:column>
		<display:column property="productSubModel" titleKey="pm.orderdata.model"></display:column>
		<display:column property="productSubName" titleKey="pm.orderdata.itemName"></display:column>
		<display:column property="num" titleKey="pm.orderdata.orderQuantity"></display:column>
        <display:column property="leaseDuration" title="租赁期限"></display:column>
		<display:setProperty name="export.excel.filename" value='租赁清单.xls'/>
	</display:table>
</body>
</html>