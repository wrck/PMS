<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='sys.subcontract.facilitator.management' />">
<style type="text/css">
#mainForm div lable{
	width: 100px;
}
</style>
</head>
<body>
	<!-- 查询 -->
	<s:form id="mainForm" name="mainForm" cssClass="form-inline" action="module/subcontract_facilitatorList.action">
		<div class="form-group form-group-query form-group-width-1">
			<dp:fielderror accesskey="errmsg" onlyone="true" />
			<label for="facilitatorName">&nbsp;&nbsp;&nbsp;<s:text name="pm.subcontract.facilitator" /></label>
			<s:textfield name="subcontractFacilitator.name" id="pm_hide" cssClass="form-control" placeholder="支持模糊查询"></s:textfield>
		</div>
		<div class="form-group form-group-query form-group-width-1">
            <dp:fielderror accesskey="errmsg" onlyone="true" />
            <label for="facilitatorName">&nbsp;&nbsp;&nbsp;<s:text name="pm.subcontract.facilitator.bankInfo" /></label>
            <s:textfield name="subcontractFacilitator.bankInfo" id="pm_hide" cssClass="form-control" placeholder="支持模糊查询"></s:textfield>
        </div>
		<div class="form-group form-group-query form-group-width-1">
			<button class="btn btn-default btn-sm">
				<span class="glyphicon glyphicon-search"></span>
				查询
			</button>
		</div>
	</s:form>
	<!-- 蓝色箭头，项目列表 -->
	<div class="divHeader div-height">
		<img src="images/right_zhishi.gif" border="0">
		<s:text name="pm.subcontract.list"></s:text>
	</div>
	<div>
		<s:if test="user.isHasRole(13) || user.isHasRole(11)">
	        <a href="module/subcontract_facilitatorEdit.action" target="_blank" style="margin-bottom:1rem;" class="btn btn-default btn-sm">新增</a>
	    </s:if>
	</div>
	<div>
		<!-- 分页，项目列表 -->
        <display:table id="facilitatorListTable" name="facilitatorList" pagesize="${facilitatorList.size()}"
            export="true" size="${facilitatorList.size()}" sort="external"
            requestURI="module/subcontract_facilitatorList.action"
            decorator="com.dp.plat.decorators.Wrapper"
            class="table table-striped" partialList="true">
            <display:column property="id" title="ID"></display:column>
            <display:column property="name" titleKey="pm.subcontract.facilitator"></display:column>
            <display:column property="bankInfo" titleKey="pm.subcontract.facilitator.bankInfo"></display:column>
            <display:column property="bankAccount" titleKey="pm.subcontract.facilitator.bankAccount"></display:column>
            <display:column property="receiver" titleKey="pm.subcontract.facilitator.receiver"></display:column>
            <display:column property="email" titleKey="pm.subcontract.facilitator.email"></display:column>
            <display:column property="effectiveFrom" titleKey="fnd.basic.data.effectiveFrom" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
            <display:column property="effectiveTo" titleKey="fnd.basic.data.effectiveTo" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column> --%>
            <display:column property="stateWrapper" titleKey="pm.subcontract.state"></display:column>
            <display:column titleKey="pm.subcontract.operate">
                <a href="module/subcontract_facilitatorEdit.action?subcontractFacilitator.id=${facilitatorListTable.id}">编辑</a>
            </display:column>
        </display:table>
	</div>
</body>
</html>