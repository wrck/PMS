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
            <span>
                <label class="tag tag-must"><s:text name="pm.project.executionState"/>:</label>
                <s:select list="projectExecutionStateList" id="projectExecutionState" name="projectMaintenance.projectExecutionState" listKey="basicDataId" listValue="basicDataName" cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 180px;display: inline-block;" />
                <button onclick="javascript:updateProjectExecutionState(<s:property value='projectMaintenance.projectId'/>)" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
                    <span class="glyphicon glyphicon-saved" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;更新项目实施状态</span>
                </button>
            </span>
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
    <s:elseif test="%{(user.isHasRole(10) || user.isHasRole(13)) && projectMaintenance.projectType == 10}">
        <span>
            <label class="tag tag-must"><s:text name="pm.project.executionState"/>:</label>
            <s:select list="projectExecutionStateList" id="projectExecutionState" name="projectMaintenance.projectExecutionState" listKey="basicDataId" listValue="basicDataName" cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 180px;display: inline-block;" />
            <button onclick="javascript:updateProjectExecutionState(<s:property value='projectMaintenance.projectId'/>)" value="pmAddPrjMaintenanceButton" type="button" class="btn btn-default" style="margin-right:4px;">
                <span class="glyphicon glyphicon-saved" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;更新项目实施状态</span>
            </button>
        </span>
    </s:elseif>
    <display:table id="maintenanceList" class="table table-striped" style="margin-top:10px;"
        name="maintenanceMapList" pagesize="${maintenanceMapList.size()}" 
        size="${maintenanceMapList.size()}" sort="external" export="true"  requestURI="module/sub/maintenance_projectMaintenance.action?projectMaintenance.projectId=${projectMaintenance.projectId}&projectMaintenance.projectType=${projectMaintenance.projectType}" 
        decorator="com.dp.plat.decorators.MaintenanceDecorator" excludedParams="*"
        partialList="true">
        <display:column title="序号">${maintenanceMapList.size() - maintenanceList_rowNum + 1}</display:column>
        <display:column property="categoryName" titleKey="pm.project.maintenance.category"></display:column>
        <display:column property="subCategoryName" titleKey="pm.project.maintenance.subCategory"></display:column>
        <display:column property="typeName" titleKey="pm.project.maintenance.type" media="excel"></display:column>
        <display:column property="projectExecutionStateName" titleKey="pm.project.executionState"></display:column>
        <display:column property="projectCode" titleKey="pm.project.projectCode" media="excel"></display:column>
        <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
        <display:column property="contractNo" titleKey="pm.project.contractNo" media="excel"></display:column>
        <display:column property="warrantyStatusName" title="维保状态" media="excel"></display:column>
        <display:column property="warrantyGradeName" titleKey="pm.project.warrantyGrade" media="excel"></display:column>
        <display:column property="wafServiceName" titleKey="pm.project.wafService" media="excel"></display:column>
        <display:column property="officeName" titleKey="pm.project.officeName" media="excel"></display:column>
        <display:column property="marketName" titleKey="pm.project.marketName" media="excel"></display:column>
        <display:column property="systemName" titleKey="pm.project.systemName" media="excel"></display:column>
        <display:column property="expendName" titleKey="pm.project.expendName" media="excel"></display:column>
        <display:column property="industryName" titleKey="pm.project.industryName" media="excel"></display:column>
        <display:column property="salerName" titleKey="pm.project.usernamec" media="excel"></display:column>
        <display:column property="finalCustomerName" titleKey="pm.project.finalCustomerName" media="excel"></display:column>
        
        <display:column property="processDesc" titleKey="pm.project.maintenance.processDesc" maxLength="90" style="max-width:250px"></display:column>
        <display:column property="processStep" titleKey="pm.project.maintenance.processStep" maxLength="90" style="max-width:250px"></display:column>
        <display:column property="processTime" titleKey="pm.project.maintenance.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
        <display:column property="transitHour" titleKey="pm.project.maintenance.transitHour" style="width:52px"></display:column>
        <display:column property="processHour" titleKey="pm.project.maintenance.processHour" style="width:52px"></display:column>
        <display:column property="itemModel" titleKey="pm.project.maintenance.itemModel" media="excel"></display:column>
        <display:column property="softVersion" titleKey="pm.project.maintenance.softVersion" media="excel"></display:column>
        <display:column property="enabledFeatures" titleKey="pm.project.maintenance.enabledFeatures" media="excel"></display:column>
        <display:column property="expendMaintenanceQuesResult" title="${projectMaintenance.questionColumns.tableQuestionHeader}" headerScope="splitCell=true" media="excel"></display:column>
        <display:column titleKey="pm.project.maintenance.hasReport" media="excel">${maintenanceList.hasReport == true ? '有' : '无'}</display:column>
        <display:column property="expendDeliverFilesURL" title="附件" media="html" style="max-width:350px"></display:column>
        <display:column property="expendDeliverFiles" title="附件" media="excel"></display:column>
        <display:column property="deliverTypes" title="附件类型" media="excel"></display:column>
        <display:column property="createUser" titleKey="pm.project.maintenance.createUser"></display:column>
        <%-- <display:column property="createTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>--%>
        <display:column property="operateTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <s:if test="projectMaintenance.hideWarranty == false">
            <display:column property="warrantyStatusName" titleKey="pm.project.maintenance.warrantyStatus" media="html" style="max-width:360px;word-break: keep-all;"></display:column>
            <display:column property="warrantyGradeName" titleKey="pm.project.warrantyGrade" media="html" style="max-width:360px;word-break: keep-all;"></display:column>
            <display:column property="wafServiceName" titleKey="pm.project.wafService" media="html" style="max-width:360px;word-break: keep-all;"></display:column>
        </s:if>
        <s:if test="%{projectMaintenance.hasPower == true}">
            <display:column title="操作" media="html">
                <s:if test="projectMaintenance.projectType == 10">
                    <a href="javascript:void(0)" style="margin:2px 0" class='btn btn-${maintenanceList.createBy == user.username ? "info" : "primary"} btn-xs' onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.id=${maintenanceList.id}&projectMaintenance.maxId=<s:property value='projectMaintenance.maxId'/>&project.projectId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);">${maintenanceList.createBy == user.username ? "修改" : "查看"}</a>
                    <a href="javascript:void(0)" style="margin:2px 0" class="btn btn-success btn-xs" onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.id=${maintenanceList.id}&message=isCopy&project.projectId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);">复制</a>
                </s:if>
                <s:elseif test="projectMaintenance.projectType == 20">
                    <a href="javascript:void(0)" style="margin:2px 0" class='btn btn-${maintenanceList.createBy == user.username ? "info" : "primary"} btn-xs' onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.id=${maintenanceList.id}&projectMaintenance.maxId=<s:property value='projectMaintenance.maxId'/>&presales.presalesId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);">${maintenanceList.createBy == user.username ? "修改" : "查看"}</a>
                    <a href="javascript:void(0)" style="margin:2px 0" class="btn btn-success btn-xs" onclick="javascript:popWindow('module/sub/maintenance_createProjectMaintenance.action?projectMaintenance.id=${maintenanceList.id}&message=isCopy&presales.presalesId=<s:property value='projectMaintenance.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.maintenance.management"></s:text>', 'BudgetUpload', true);">复制</a>
                </s:elseif>
            </display:column>
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