<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<%@page import="com.dp.plat.supervision.vo.ProjectSupervisionVO"%>
<html>
<dp:base />
<head>
</head>
<body>
    <s:if test="%{projectSupervision.hasPower == true}">
        <button onclick="javascript:popWindow('module/sub/projectSub_createProjectSupervision.action?project.projectId=<s:property value='projectSupervision.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<%=StringEscUtil.getText("pm.cl.cl") %>', 'BudgetUpload', true);" value="pmAddPrjSupervisionButton" type="button" class="btn btn-default" style="margin-right:4px;">
            <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加督查记录</span>
        </button>
    </s:if>
    <display:table id="supervisionList" class="table table-striped"
        name="supervisionMapList" pagesize="${supervisionMapList.size()}" 
        size="${supervisionMapList.size()}" sort="external" export="true"  requestURI="module/sub/projectSub_projectSupervision.action?projectSupervision.projectId=${projectSupervision.projectId}" 
        decorator="com.dp.plat.decorators.SupervisionDecorator" excludedParams="*"
        partialList="true">
        <display:column property="typeName" titleKey="pm.project.supervision.type"></display:column>
        <display:column property="processTime" titleKey="pm.project.supervision.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
        <display:column titleKey="pm.project.supervision.state">${supervisionList.state == true ? '有' : '无'}</display:column>
        <display:column property="createUser" titleKey="sys.create.by"></display:column>
        <display:column property="createTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <display:column property="expendSupervisionQuesResult" title="${projectSupervision.questionColumns.tableQuestionHeader}" headerScope="splitCell=true"></display:column>
        <display:column property="expendDeliverFilesURL" title="附件" media="html"></display:column>
        <display:column property="expendDeliverFiles" title="附件" media="excel"></display:column>
        
        <display:setProperty name="export.excel.filename" value="${project.projectName}督查记录.xlsx" />
    </display:table>
    <div class="backTop">
        <i class='glyphicon glyphicon-arrow-up'></i>
    </div>
    <div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
</body>
</html>