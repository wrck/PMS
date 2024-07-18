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
    <div class='pull-right' style="margin-right: 15px;">
        <a id="orderDataChangeBtn" onclick="changeOrderData()" class="btn btn-success" href="javascript:void(0)">设备明细清单</a>
        <script>
            function changeOrderData(e) {
            	var idx = $(".orderDataListTableDiv .displayTable").is(":visible");
            	$("#orderDataChangeBtn").text(idx ? "设备汇总清单" : "设备明细清单");
            	$("#orderDataListTable").parent().toggle();
            	$("#orderDataDetailListTable").parent().toggle();
            }
        </script>
    </div>
    <!-- 设备汇总清单 -->
    <div class="orderDataListTableDiv" >
        <display:table name="orderDataList" pagesize="${orderDataList.size()}" id="orderDataListTable"
        size="${orderDataList.size()}" sort="external" export="true"
        decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
        requestURI="module/sub/checkOrderData.action"
        partialList="true">
            <display:column property="contractNo" titleKey="pm.orderdata.contractNo"></display:column>
            <display:column property="itemCode" titleKey="pm.orderdata.itemCode"></display:column>
            <display:column property="model" titleKey="pm.orderdata.model"></display:column>
            <display:column property="itemName" titleKey="pm.orderdata.itemName"></display:column>
            <display:column property="projectQuantity" titleKey="project.product.quantity"></display:column>
            <display:column property="orderQuantity" titleKey="pm.orderdata.orderQuantity"></display:column>
            <display:column property="deliverQuantity" titleKey="pm.orderdata.deliverQuantity"></display:column>
            <display:column property="openQuantity" titleKey="pm.orderdata.openQuantity"></display:column>
            <display:column property="barcode" titleKey="pm.orderdata.barcode" media="html"></display:column>
            <display:setProperty name="export.excel.filename" value='设备汇总清单.xls'></display:setProperty>
        </display:table>
	</div>
    <!-- 设备明细清单 -->
    <div class="orderDataDetailListTableDiv"  style="display:none;">
        <display:table name="cbForm.orderDataDetailList" pagesize="${cbForm.orderDataDetailList.size()}" id="orderDataDetailListTable"
        size="${cbForm.orderDataDetailList.size()}" sort="external" export="true"
        decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
        requestURI="module/sub/checkOrderData.action"
        partialList="true">
            <display:column property="contractNo" titleKey="pm.orderdata.contractNo"></display:column>
            <display:column property="itemCode" titleKey="pm.orderdata.itemCode"></display:column>
            <display:column property="model" titleKey="pm.orderdata.model"></display:column>
            <display:column property="itemName" titleKey="pm.orderdata.itemName"></display:column>
            <display:column property="projectQuantity" titleKey="project.product.quantity"></display:column>
            <display:column property="orderQuantity" titleKey="pm.orderdata.orderQuantity"></display:column>
            <display:column property="deliverQuantity" titleKey="pm.orderdata.deliverQuantity"></display:column>
            <display:column property="openQuantity" titleKey="pm.orderdata.openQuantity"></display:column>
            <display:column property="barcode" titleKey="pm.orderdata.barcode" media="html"></display:column>
            <display:setProperty name="export.excel.filename" value='设备明细清单.xls'></display:setProperty>
        </display:table>
    </div>
    
</body>
</html>