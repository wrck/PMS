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
<meta name="function" content="<s:text name='sys.leftmenu.departmentmanage' />">
<script type="text/javascript">
function checkSubmit(){
	document.getElementById("mainForm").submit();
}

function departmentadd(){
	document.location.href="base/DepartmentRefresh.action";
}
</script>
</head>
<body>
<%-- <div class="listView divHeader" >
		<img src="images/right_zhishi.gif" border="0">
					<s:text name="sys.department.query"></s:text>
</div> --%>
<s:form enctype="multipart/form-data" method="POST" action="base/DepartmentManage.action" id="mainForm" cssClass="form-inline"> 
	<div class="form-group form-group-query" >
		<label for="departmentNum" class="form-text-label"><span class="redmark">*</span><s:text name="sys.department.num" /></label>
    	<s:textfield name="department.departmentNum" id="departmentNum" placeholder="支持模糊搜索" cssClass="form-control" />
	</div>
	<div class="form-group form-group-query" >
		<label for="departmentName" class="form-text-label"><span class="redmark">*</span><s:text name="sys.department.name" /></label>
    	<s:textfield name="department.departmentName" id="departmentName" placeholder="支持模糊搜索" cssClass="form-control" />
	</div>
	<div class="form-group form-group-query" >
		<button type="submit" class="btn btn-default"><s:text name="sys.confirm"></s:text></button>
	</div>
</s:form>
<div class="listView divHeader" >
		<img src="images/right_zhishi.gif" border="0">
					<s:text name="sys.department.list"></s:text>
</div>
<div class="list-button">
	<button type="button" class="btn btn-default"  onclick="departmentadd()"><s:text name="sys.department.refresh"></s:text></button>
</div>
<div>
	<display:table
		name="departmentList" pagesize="${displayParam.pagesize}" export="false"
		size="${displayParam.totalcount}" sort="external"
		requestURI="base/DepartmentManage.action"
		decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
		partialList="true">
		<display:column property="departmentNum" titleKey="sys.department.num"></display:column>
		<display:column property="departmentName" titleKey="sys.department.name"></display:column>
		<display:column property="createTime" titleKey="sys.department.refreshTime" format="{0,date,yyyy-MM-dd}"></display:column>
		<%-- <display:column property="departmentStatus" titleKey="sys.department.status"></display:column>
		<display:column property="editDepartment" titleKey="sys.write"></display:column> --%>
	</display:table>
</div>
<dp:errormsg />
</body>
</html>