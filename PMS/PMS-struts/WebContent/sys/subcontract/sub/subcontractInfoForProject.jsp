<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
<style type="text/css">
#mainForm div lable{
	width: 100px;
}
</style>
</head>
<body>
	<div>
		<!-- 查看售前测试项目流程图 -->
		<%-- <div style="text-align: right;">
			<a id="querysubcontractView" target="_blank" href="">
				<span class="panel-heading">查看售前测试项目流程图&nbsp;</span><span class="glyphicon glyphicon-picture" style="font-size:12px; color:#428bca;"></span>
			</a>
		</div> --%>
		<!-- 分页，项目列表 -->
        <display:table name="subcontractVOList" pagesize="${subcontractVOList.size()}"
            export="true" size="${subcontractVOList.size()}" sort="external"
            decorator="com.dp.plat.decorators.Wrapper"
            class="table table-striped" partialList="true">
            <display:column property="subcontractNo" titleKey="pm.subcontract.subcontractNo"></display:column>
            <display:column property="subcontractName" titleKey="pm.subcontract.subcontractName" href="${namespace}/subcontract_input.action" paramProperty="id" paramId="subcontract.id"></display:column>
            <display:column property="contractNos" titleKey="pm.subcontract.contractNos" decorator="com.dp.plat.decorators.ContractNoList "></display:column>
            <display:column property="officeName" titleKey="pm.subcontract.officeName"></display:column>
            <display:column property="facilitatorName" titleKey="pm.subcontract.facilitator"></display:column>
            <display:column property="createName" titleKey="pm.subcontract.createName"></display:column>
            <display:column property="createTime" titleKey="pm.subcontract.createTime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
            <display:column property="stateName" titleKey="pm.subcontract.state"></display:column>
        </display:table>
	</div>
</body>
</html>