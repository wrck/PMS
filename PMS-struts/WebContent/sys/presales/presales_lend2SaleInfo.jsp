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
		<legend><b>借转销信息</b></legend>
		<table class="table table-condensed table-hover table-striped">
			<thead>
			<tr class="warning">
                <td><s:text name="pm.ps.pro.fisrtname"></s:text></td>
                <td><s:text name="pm.ps.pro.typename"></s:text></td>
				<td><s:text name="pm.ps.pro.itemcode"></s:text></td>
                <td><s:text name="pm.ps.pro.itemmodel"></s:text></td>
                <td><s:text name="pm.ps.pro.itemdesc"></s:text></td>
                <td><s:text name="pm.ps.pro.num"></s:text></td>
                <td><s:text name="pm.ps.pro.transferNum"></s:text></td>
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
                    <td><s:property value="#p.productfirstName"/></td>
                    <td><s:property value="#p.productName"/></td>
					<td><s:property value="#p.productSubCode"/></td>
					<td><s:property value="#p.productSubModel"/></td>
					<td><s:property value="#p.productSubName"/></td>
                    <td><s:property value="#p.num"/></td>
					<td><s:property value="#p.borrowNum"/></td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</fieldset> --%>
    <fieldset>
        <legend><b>借转销信息</b></legend>
        <!-- pagesize="${commonList.size() }" -->
        <display:table id="lend2SaleTable" name="commonList" 
            export="false" size="${commonList.size() }" sort="external"
            requestURI="module/presales_lend2Sale.action"
            class="table table-condensed table-hover table-striped" partialList="false">
            <display:column headerClass="warning" property="productfirstName" titleKey="pm.ps.pro.fisrtname"></display:column>
            <display:column headerClass="warning" property="productName" titleKey="pm.ps.pro.typename"></display:column>
            <display:column headerClass="warning" property="productSubCode" titleKey="pm.ps.pro.itemcode"></display:column>
            <display:column headerClass="warning" property="productSubModel" titleKey="pm.ps.pro.itemmodel"></display:column>
            <display:column headerClass="warning" property="productSubName" titleKey="pm.ps.pro.itemdesc"></display:column>
            <%-- <display:column headerClass="warning" property="num" titleKey="pm.ps.pro.num"></display:column> --%>
            <display:column headerClass="warning" property="borrowNum" titleKey="pm.ps.pro.transferNum"></display:column>
            <display:column headerClass="warning" property="memo" titleKey="pm.ps.pro.remark"></display:column>
        </display:table>
    </fieldset>
</body>
</html>
