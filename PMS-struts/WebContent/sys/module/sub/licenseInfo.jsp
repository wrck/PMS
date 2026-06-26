<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
</head>
<body>
    <style>
        #licenseInfoTable.displayTable thead tr th {
            white-space: nowrap;
        }
    </style>
	<display:table
		name="commonList" pagesize="${commonList.size()}" id="licenseInfoTable"
		size="${commonList.size()}" sort="external" export="true"
		class="displayTable table"
		requestURI="module/sub/projectSub_licenseInfo.action"
		partialList="true">
        <display:column property="contract"     title="合同号"></display:column>
        <display:column property="contractType" title="合同类型"></display:column>
        <display:column property="licenseCode"  title="授权码"></display:column>
        <display:column property="sn"           title="序列号"></display:column>
        <display:column property="item"         title="产品编码"></display:column>
        <display:column property="specModel"    title="产品型号"></display:column>
        <display:column property="status"       title="状态"></display:column>
		<display:setProperty name="export.excel.filename" value='项目License授权信息.xls'/>
	</display:table>
</body>
</html>
