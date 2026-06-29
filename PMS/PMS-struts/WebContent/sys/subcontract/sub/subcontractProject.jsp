<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
<script type="text/javascript">
</script>
</head>
<body>
	<div style="text-align: left;">
		<display:table style="text-align: left;"
            name="projectList" pagesize="${projectList.size()}" export="false" id="projectDisplayTable"
            size="${projectList.size()}" sort="external"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table table-condensed table-hover table-striped" 
            partialList="true" >
            <display:column property="projectCheckWrapper" titleKey="pm.shipment.check"></display:column>
            <%-- <display:column property="column001" class="hidden transferOfficeCode" headerClass="hidden" media="html"></display:column> --%>
            <display:column class="hidden " headerClass="hidden" media="html">
                <input class="transferOfficeCode" value="${projectDisplayTable.column001}" data-type="${projectDisplayTable.salesType}" />
            </display:column> 
            <%-- <display:column property="p hideDivrojectCode" titleKey="pm.project.projectCode" class="transferProjectCode"></display:column> --%>
            <display:column property="projectNameWithCodeWarrper" class="transferProjectName" titleKey="pm.project.projectName" media="html"></display:column> 
            <display:column property="contractNo" titleKey="pm.contract" class="transferContractNo" decorator="com.dp.plat.decorators.ContractNoList"></display:column>
        </display:table>
	</div>
</body>
</html>