<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<%@page import="com.dp.plat.data.vo.ProjectMaintenanceVO"%>
<html>
<dp:base />
<head>
</head>
<body>
    <s:if test="%{projectMaintenance.hasPower == true}">
        <button onclick="javascript:popWindow('module/sub/projectSub_createProjectMaintenance.action?project.projectId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<%=StringEscUtil.getText("pm.cl.cl") %>', 'BudgetUpload', true);" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
            <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加维护记录</span>
        </button>
    </s:if>
    <display:table id="maintenanceList" class="table table-striped"
        name="maintenanceMapList" pagesize="${maintenanceMapList.size()}" 
        size="${maintenanceMapList.size()}" sort="external" export="true"  requestURI="module/sub/projectSub_projectMaintenance.action?projectMaintenance.projectId=${projectMaintenance.projectId}" 
        decorator="com.dp.plat.decorators.MaintenanceDecorator" excludedParams="*"
        partialList="true">
        <display:column property="typeName" titleKey="pm.project.maintenance.type"></display:column>
        <display:column property="processTime" titleKey="pm.project.maintenance.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
        <display:column titleKey="pm.project.maintenance.hasReport">${maintenanceList.hasReport == true ? '有' : '无'}</display:column>
        <display:column property="createUser" titleKey="sys.create.by"></display:column>
        <display:column property="createTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <display:column property="expendMaintenanceQuesResult" title="${projectMaintenance.questionColumns.tableQuestionHeader}" headerScope="splitCell=true"></display:column>
        <display:column property="expendDeliverFilesURL" title="附件" media="html"></display:column>
        <display:column property="expendDeliverFiles" title="附件" media="excel"></display:column>
        
        <display:setProperty name="export.excel.filename" value="${project.projectName}维护记录.xlsx" />
    </display:table>
    <div class="backTop">
        <i class='glyphicon glyphicon-arrow-up'></i>
    </div>
    <div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
</body>
</html>