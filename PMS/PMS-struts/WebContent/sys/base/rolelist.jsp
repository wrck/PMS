<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.rolemanage' />">
<script type="text/javascript">
function checkSubmit(){
	document.getElementById("mainForm").submit();
}

function roleadd(){
	document.location.href="base/RoleAdd.action";
}
$(function(){
	$(".table-striped tr td:contains('失效')").parent().css("color","red");
})

</script>
</head>
<body>
<%-- <div class="listView divHeader" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="sys.role.query"></s:text>
</div> --%>
<s:form enctype="multipart/form-data" method="POST" action="base/RoleManage.action" cssClass="form-inline" id="mainForm">
	<div class="form-group form-group-query" >
		<label for="rolename" class="form-text-label"><span class="redmark">*</span><s:text name="sys.role.name" /></label>
    	<s:textfield name="role.roleName" id="rolename" cssClass="form-control" placeholder="支持模糊搜索"/>
	</div>
	<div class="form-group form-group-query">
    	<button type="submit" class="btn btn-default"><s:text name="sys.confirm"></s:text></button>
	</div>
</s:form>
<div class="div-bottom divHeader" >
		<img src="images/right_zhishi.gif" border="0">
					<s:text name="sys.role.list"></s:text>
</div>
<div class="list-button">
	<button type="button" class="btn btn-default" onclick="roleadd()"><s:text name="sys.role.add"></s:text></button>
</div>
<div>
	<display:table
				name="roleList" pagesize="${roleList.size()}" export="true"
				size="${roleList.size()}" sort="external"
				requestURI="base/RoleManage.action"
				decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
				partialList="true">
				<display:column property="roleName" titleKey="sys.role.name" sortable="false"></display:column>
				<display:column property="roleStatus" titleKey="sys.role.status"></display:column>
				<display:column property="effectiveFrom" titleKey="sys.role.effectiveFrom" format="{0,date,yyyy-MM-dd}"></display:column>
				<display:column property="effectiveTo" titleKey="sys.role.effectiveTo" format="{0,date,yyyy-MM-dd}"></display:column>
				<display:column property="editRole" titleKey="sys.write"></display:column>
			</display:table>
</div>
<div>
	<dp:errormsg />
</div>
</body>
</html>