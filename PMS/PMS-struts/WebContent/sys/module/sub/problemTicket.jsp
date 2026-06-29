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
    <style>
        #problemTicketTable.displayTable thead tr th {
            white-space: nowrap;
        }
    </style>
	<display:table 
		name="commonList" pagesize="${commonList.size()}" id="problemTicketTable"
		size="${commonList.size()}" sort="external" export="true"
		class="displayTable table"
		requestURI="module/sub/projectSub_problemTicket.action"
		partialList="true">
        <!-- decorator="com.dp.plat.decorators.Wrapper"  -->
        <display:column property="ticketNo"       title="问题单号" media="excel" ></display:column>
        <display:column                           title="问题单号" media="html" >
            <a href="${cbForm.itrBaseUrl}/${problemTicketTable.incidentId}" target="_blank">${problemTicketTable.ticketNo}</a>
        </display:column>
        <display:column property="statusName"     title="工单状态"></display:column>
        <display:column property="caseTopic"      title="问题单主题"></display:column>
        <display:column property="memo"           title="描述"></display:column>
        <%-- 
        <display:column property="principal"      title="责任人工号" media="excel"></display:column>
        <display:column property="principalName"  title="责任人" media="excel"></display:column>
        <display:column                           title="责任人" media="html">${problemTicketTable.principal}-${problemTicketTable.principalName}</display:column>
         --%>
        <display:column property="accepter"       title="受理人工号" media="excel"></display:column>
        <display:column property="accepterName"   title="受理人" media="excel"></display:column>
        <display:column                           title="受理人" media="html">${problemTicketTable.accepter}-${problemTicketTable.accepterName}</display:column>
        <display:column property="processor"      title="处理人工号"  media="excel"></display:column>
        <display:column property="processorName"  title="处理人"  media="excel"></display:column>
        <display:column                           title="处理人" media="html">${problemTicketTable.processor}-${problemTicketTable.processorName}</display:column>
        <display:column property="supplied"       title="是否上报"></display:column>
        <display:column property="questionType"   title="问题类型"></display:column>
        <display:column property="questionLevel"  title="问题级别"></display:column>
        <%-- <display:column property="title"          title="工单标题"></display:column> --%>
        <display:column property="acceptTime"     title="受理时间"></display:column>
        <display:column property="productType"    title="设备类型"></display:column>
        <display:column property="productModel"   title="设备型号"></display:column>
        <%-- <display:column property="progress"       title="处理进展" headerClass="hidden" class="hidden"></display:column> --%>
        <display:column property="questionReason" title="问题根因"></display:column>
        <%-- <display:column property="solutionType"   title="解决方式 "></display:column> --%>
        <display:column property="solutions"      title="解决方案"></display:column>
        <%-- <display:column property="rmaNo"          title="RMA单号 "></display:column> --%>
        <display:column property="accidentNo"     title="事故单号"></display:column>
        <display:column property="caseType"       title="Case类型 " headerClass="hidden" class="hidden"></display:column>
        <display:column property="projectNo"      title="项目编码" headerClass="hidden" class="hidden"></display:column>
        <display:column property="contractNo"     title="合同号" headerClass="hidden" class="hidden"></display:column>
        <display:column property="barcode"        title="序列号" ></display:column>
        <display:column property="bulletinNo"     title="技术公告编号 " headerClass="hidden" class="hidden"></display:column>
        <display:column property="bugNo"          title="Bug单" headerClass="hidden" class="hidden"></display:column>
        <display:column property="productLine"    title="产品线" headerClass="hidden" class="hidden"></display:column>
		<display:setProperty name="export.excel.filename" value='项目问题单记录.xls'/>
	</display:table>
</body>
</html>