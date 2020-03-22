<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
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
	<%-- <fieldset>
		<legend><b>核销信息</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<thead>
			<tr class="warning">
                <td>执行单</td>
                <td>审批时间</td>
                <td>借货类型</td>
                <td>合同号</td>
				<td><s:text name="pm.ps.pro.itemcode"></s:text></td>
                <td><s:text name="pm.ps.pro.itemdesc"></s:text></td>
                <td>订单数</td>
                <td>发货数</td>
                <td><s:text name="pm.ps.pro.hexiaoNum"></s:text></td>
                <td><s:text name="pm.ps.pro.weihexiaoNum"></s:text></td>
                <td>是否取消</td>
			</tr>
			</thead>
			<tbody>
			<s:if test="commonList.size()== 0">
				<tr>
					<td colspan="8">
						无可以显示的数据
					</td>
				</tr>
			</s:if>
			<s:iterator value="commonList" var="p">
				<tr>
                    <td><s:property value="#p.ppliCode"/></td>
                    <td><s:date name="#p.createDate" format="yyyy-MM-dd"/></td>
                    <td><s:property value="#p.orderType"/></td>
                    <td><s:property value="#p.contract"/></td>
					<td><s:property value="#p.itemcode"/></td>
                    <td><s:property value="#p.description"/></td>
                    <td><s:property value="#p.orderQty"/></td>
                    <td><s:property value="#p.dlvQty"/></td>
					<td><s:property value="#p.rmaQty"/></td>
                    <td><s:property value="#p.dlvQty - #p.rmaQty"/></td>
                    <td><s:property value="#p.canceled"/></td>
				</tr>	
			</s:iterator>
			</tbody>
		</table>
	</fieldset> --%>
    <fieldset>
        <legend><b>核销信息</b></legend>
        <!-- pagesize="${commonList.size() }" style="margin-bottom: 5px;" -->
        <display:table id="lend2RmaTable" name="commonList" 
            export="false" size="${commonList.size() }" sort="external"
            requestURI="module/presales_lend2Rma.action" decorator="com.dp.plat.decorators.PresalesDecorator"
            class="table table-condensed displatTable" partialList="false">
            <display:column headerClass="warning" class="pc_${lend2RmaTable.ppliCode}" property="ppliCode" title="执行单"></display:column>
            <display:column headerClass="warning" property="createDate" title="审批时间"></display:column>
            <%-- <display:column headerClass="warning" property="orderType" title="借货类型"></display:column>
            <display:column headerClass="warning" property="contract" title="合同号"></display:column> --%>
            <display:column headerClass="warning" property="itemcode" titleKey="pm.ps.pro.itemcode"></display:column>
            <display:column headerClass="warning" property="description" titleKey="pm.ps.pro.itemdesc"></display:column>
            <display:column headerClass="warning" property="orderQty" title="订单数"></display:column>
            <display:column headerClass="warning" property="dlvQty" title="发货数"></display:column>
            <display:column headerClass="warning" property="rmaQty" titleKey="pm.ps.pro.hexiaoNum"></display:column>
            <display:column headerClass="warning" titleKey="pm.ps.pro.weihexiaoNum">
                ${lend2RmaTable.dlvQty - lend2RmaTable.rmaQty}
            </display:column>
            <display:column headerClass="warning" property="deliveryDate" titleKey="pm.ps.pro.deliveryDate" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column headerClass="warning" property="rmaDate" titleKey="pm.ps.pro.rmaDate" format="{0,date,yyyy-MM-dd}"></display:column>
            <display:column headerClass="warning" property="lendPeriod" titleKey="pm.ps.pro.lendPeriod"></display:column>
            <display:column headerClass="warning" property="canceled" title="是否取消"></display:column>
        </display:table>
    </fieldset>
</body>
</html>
