<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html lang="zh-CN">
<head>
<dp:base />
<meta name="menu" content="SysMenuGroupStaticDataManage">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.basemanage' />">
<meta name="function" content="<s:text name='fnd.basic.data' />">
<script type="text/javascript">
	function submit(){
		window.location.reload();
	}
	
	function updateBasicData(obj){
		window.location.href="base/BasicdataUpdate.action?basicData.id="+obj;
	}
	function add(){
		document.location.href="base/BasicdataInsert.action";
	}
</script>
</head>
<body>
	<s:form cssClass="form-inline"  id="mainForm">
		<div class="form-group form-group-query">
			<label for="dataType" class="form-text-label"><s:text name="fnd.basic.data.type"></s:text></label>
			<s:select name="basicData.basicDataTypeCode" list="basicDataTypeList" onchange="submit()" listKey="basicDataTypeCode" listValue="basicDataTypeName" 
				cssClass="form-control"
				headerKey="" headerValue="--请选择--">
			</s:select>
		</div>
		<div class="div-bottom divHeader" >
			<img src="images/right_zhishi.gif" border="0" />
				<s:text name="fnd.basic.data.query"></s:text>
		</div>
		<div>
			<button type="button" onclick="add()" class="btn btn-default"><s:text name='fnd.basic.data.add'/></button>
		</div>
		<div>
			<display:table
				name="basicDataList" pagesize="${basicDataList.size()}" export="true"
				size="${basicDataList.size()}" sort="external"
				requestURI="base/BasicdataManage.action"
				decorator="com.dp.plat.decorators.Wrapper" class="table table-striped"
				partialList="true">
				<display:column property="basicDataId" titleKey="fnd.basic.data.code" sortable="false"></display:column>
				<display:column property="basicDataName" titleKey="fnd.basic.data.name"></display:column>
				<display:column property="basicDataTypeName" titleKey="fnd.basic.data.type"></display:column>
				<display:column property="effectiveFrom" format="{0,date,yyyy-MM-dd}" titleKey="fnd.basic.data.effectiveFrom"></display:column>
				<display:column property="effectiveTo" format="{0,date,yyyy-MM-dd}"  titleKey="fnd.basic.data.effectiveTo"></display:column>
				<display:column property="basicDataWrapper" titleKey="sys.write"></display:column>
			</display:table>
		</div>
	</s:form>
</body>
</html>