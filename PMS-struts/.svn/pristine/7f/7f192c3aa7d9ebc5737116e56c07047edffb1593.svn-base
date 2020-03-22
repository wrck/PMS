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
        <s:if test="projectMaintenance.projectType == 10">
            <button onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?project.projectId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
                <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加维护记录</span>
            </button>
        </s:if>
        <s:elseif test="projectMaintenance.projectType == 20">
            <button onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?presales.presalesId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
                <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加维护记录</span>
            </button>
        </s:elseif>
    </s:if>
    <display:table id="maintenanceList" class="table table-striped" style="margin-top:10px;"
        name="maintenanceMapList" pagesize="${maintenanceMapList.size()}" 
        size="${maintenanceMapList.size()}" sort="external" export="true"  requestURI="module/sub/maintenance_projectMaintenance.action?projectMaintenance.projectId=${projectMaintenance.projectId}&projectMaintenance.projectType=${projectMaintenance.projectType}" 
        decorator="com.dp.plat.decorators.MaintenanceDecorator" excludedParams="*"
        partialList="true">
        <display:column title="序号">${maintenanceMapList.size() - maintenanceList_rowNum + 1}</display:column>
        <display:column property="categoryName" titleKey="pm.project.maintenance.category"></display:column>
        <display:column property="subCategoryName" titleKey="pm.project.maintenance.subCategory"></display:column>
        <display:column property="typeName" titleKey="pm.project.maintenance.type"></display:column>
        <display:column property="projectCode" titleKey="pm.project.projectCode" media="excel"></display:column>
        <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
        <display:column property="contractNo" titleKey="pm.project.contractNo" media="excel"></display:column>
        <display:column property="officeName" titleKey="pm.project.officeName" media="excel"></display:column>
        <display:column property="createUser" titleKey="pm.project.maintenance.createUser"></display:column>
        <display:column property="processTime" titleKey="pm.project.maintenance.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
        <display:column property="transitHour" titleKey="pm.project.maintenance.transitHour" media="excel"></display:column>
        <display:column property="processHour" titleKey="pm.project.maintenance.processHour" media="excel"></display:column>
        <display:column property="processDesc" titleKey="pm.project.maintenance.processDesc" media="excel"></display:column>
        <display:column property="processStep" titleKey="pm.project.maintenance.processStep" media="excel"></display:column>
        <display:column property="itemModel" titleKey="pm.project.maintenance.itemModel" media="excel"></display:column>
        <display:column property="softVersion" titleKey="pm.project.maintenance.softVersion" media="excel"></display:column>
        <display:column property="enabledFeatures" titleKey="pm.project.maintenance.enabledFeatures" media="excel"></display:column>
        <display:column property="expendMaintenanceQuesResult" title="${projectMaintenance.questionColumns.tableQuestionHeader}" headerScope="splitCell=true"></display:column>
        <display:column titleKey="pm.project.maintenance.hasReport" media="excel">${maintenanceList.hasReport == true ? '有' : '无'}</display:column>
        <display:column property="expendDeliverFilesURL" title="附件" media="html" style="max-width:350px"></display:column>
        <display:column property="expendDeliverFiles" title="附件" media="excel"></display:column>
        <display:column property="deliverTypes" title="附件类型" media="excel"></display:column>
        <display:column property="createTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <s:if test="%{projectMaintenance.hasPower == true}">
            <s:if test="projectMaintenance.projectType == 10">
                <display:column title="操作" media="html">
                    <a href="javascript:void(0)" onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.id=${maintenanceList.id}&message=isCopy&project.projectId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);">复制</a>
                </display:column>
            </s:if>
            <s:elseif test="projectMaintenance.projectType == 20">
                <display:column title="操作" media="html">
                    <a href="javascript:void(0)" onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.id=${maintenanceList.id}&message=isCopy&presales.presalesId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);">复制</a>
                </display:column>
            </s:elseif>
        </s:if>
        <%-- <s:if test="projectMaintenance.projectType == 10">
            <display:setProperty name="export.excel.filename" value="${project.projectName}维护记录.xls" />
        </s:if>
        <s:elseif test="projectMaintenance.projectType == 20">
            <display:setProperty name="export.excel.filename" value="${presales.projectName}维护记录.xls" />
        </s:elseif> --%>
        <display:setProperty name="export.excel.filename" value="${projectMaintenance.projectName}维护记录.xls" />
    </display:table>
    <div class="backTop">
        <i class='glyphicon glyphicon-arrow-up'></i>
    </div>
    <div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
</body>
</html>