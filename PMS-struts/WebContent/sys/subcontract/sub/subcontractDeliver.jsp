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
	<div style="text-align: left;">
		<display:table style="text-align: left;"
            name="subcontractDeliverList" pagesize="${subcontractDeliverList.size()}" export="false" id="subcontractDeliverTable"
            size="${subcontractDeliverList.size()}" sort="external"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" 
            partialList="true" >
            <display:column property="subcontractFileName" titleKey="file.name"></display:column>
            <display:column property="typeName" titleKey="pm.subcontract.deliver.type"></display:column>
            <display:column property="uploadName" titleKey="file.uploadby"></display:column>
            <display:column property="uploadTime" titleKey="file.uploadtime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
        </display:table>
	</div>
</body>
</html>