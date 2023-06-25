<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<head>
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='sys.leftmenu.usermanage' />">
<script type="text/javascript">
function checkSubmit(){
	document.getElementById("mainForm").submit();
}

function useradd(){
	document.location.href="base/UserAdd.action";
}

$(function(){
	/*
		为失效数据标注红色
	*/
	$(".table-striped tr td:contains('失效')").parent().css("color","red");
})

</script>
</head>
<body>
<%-- <div class="listView divHeader" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="sys.user.query"></s:text>
</div> --%>
<s:form enctype="multipart/form-data" method="POST" action="base/UserManage.action" cssClass="form-inline" id="mainForm">
	<div class="form-group form-group-query">
		<dp:fielderror accesskey="errmsg" onlyone="true" />
		<label for="username" class="form-text-label"><span class="redmark">*</span><s:text name="sys.user.name" /></label>
    	<s:textfield name="user.username" id="username" placeholder="支持模糊搜索" cssClass="form-control" />
	</div>
	<div class="form-group form-group-query" >
		<label for="roleName" class="form-text-label"><span class="redmark">*</span><s:text name="sys.user.rolename" /></label>
    	<s:select name="user.roleids" id="roleName" list="rolelist" listKey="id" listValue="roleName" headerKey="" headerValue="请选择" cssClass="form-control"> </s:select>
	</div>
    <div class="form-group form-group-query" >
        <label for="dpNo" class="form-text-label"><span class="redmark">*</span><s:text name="sys.user.department" /></label>
        <s:select name="user.dpNo" id="dpNo" list="departments" listKey="departmentNum" listValue="departmentName" headerKey="" headerValue="请选择" cssClass="form-control"> </s:select>
    </div>
	<div class="form-group form-group-query" >
		<button type="button" onclick="checkSubmit()" class="btn btn-default btn-block"><s:text name="sys.confirm"></s:text></button>
	</div>
</s:form>
<div class="listView divHeader" >
	<img src="images/right_zhishi.gif" border="0">
				<s:text name="sys.user.list"></s:text>
</div>
<div class="list-button">
	<button type="button" onclick="useradd()" class="btn btn-default"><s:text name='sys.user.add'/></button>
</div>
<div>
	<display:table
				name="userlist" pagesize="${displayParam.pagesize}" export="true"
				size="${displayParam.totalcount}" sort="external"
				requestURI="base/UserManage.action"
				decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
				partialList="true">
				<display:column property="username" titleKey="sys.user.name" sortable="false"></display:column>
				<display:column property="realName" titleKey="sys.user.realname"></display:column>
				<display:column property="roleName" titleKey="sys.user.rolename"></display:column>
				<display:column property="statusWrapper" titleKey="sys.user.status"></display:column>
				<display:column property="userWriteWrapper" titleKey="sys.write"></display:column>
			</display:table>
</div>
</body>
</html>