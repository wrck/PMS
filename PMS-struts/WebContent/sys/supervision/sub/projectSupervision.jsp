<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
</head>
<body>
    <s:if test="%{projectSupervision.hasPower == true}">
        <button onclick="javascript:popWindow('module/sub/supervision_createProjectSupervision.action?project.projectId=<s:property value='projectSupervision.projectId'/>&redirect=<s:property value='redirect'/>', 1000, 650,'<s:text name="sys.project.supervision.management"></s:text>', 'BudgetUpload', true);" value="pmAddPrjSupervisionButton" type="button" class="btn btn-default" style="margin-right:4px;">
            <span class="glyphicon glyphicon-plus" style="font-size:12px; color:#428bca;"></span><span style="font-size:12px;">&nbsp;&nbsp;添加督查记录</span>
        </button>
    </s:if>
    <display:table id="supervisionList" class="table table-striped"
        name="supervisionMapList" pagesize="${supervisionMapList.size()}" 
        size="${supervisionMapList.size()}" sort="external" export="true"  requestURI="module/sub/supervision_projectSupervision.action?projectSupervision.projectId=${projectSupervision.projectId}" 
        decorator="com.dp.plat.supervision.decorators.SupervisionDecorator" excludedParams="*"
        partialList="true">
        <display:column title="序号">${supervisionMapList.size() - supervisionList_rowNum + 1}</display:column>
        <%-- <display:column property="typeName" class="nowrap" titleKey="pm.project.supervision.type"></display:column> --%>
        <display:column property="channel" titleKey="pm.project.supervision.channel"></display:column>
        <display:column property="processTime" class="nowrap" titleKey="pm.project.supervision.processTime" format="{0,date,yyyy-MM-dd}"></display:column>
        <%-- <display:column titleKey="pm.project.supervision.state">${supervisionList.state == true ? '有' : '无'}</display:column> --%>
        <display:column property="createUser" class="nowrap" titleKey="sys.create.by"></display:column>
        <display:column property="expendSupervisionQuesResult" title="${projectSupervision.questionColumns.tableQuestionHeader}" headerScope="splitCell=true"></display:column>
        <display:column property="expendDeliverFilesURL" title="督查表" media="html"></display:column>
        <display:column property="expendDeliverFiles" title="督查表" media="excel"></display:column>
        <display:column property="createTime" class="nowrap" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <%-- <s:if test="user.isHasRole(10) || user.isHasRole(13) || user.isHasRole(14)"> --%>
            <display:column property="operateUrl" class="nowrap" title="操作" media="html"></display:column>
        <%-- </s:if> --%>
        <%-- <display:column title="操作">
            ${supervisionList.state == false ? "<a>填写问卷</a>" : ""}
           ${supervisionList.state == false ? ("<a href='/module/sub/supervision_createProjectSupervision.action?project.projectId=" + projectSupervision.projectId + "&projectSupervision.id=" + projectSupervision.id + "'>填写问卷</a>") : ""}
       
            ${supervisionList.state == false && (user.isHasRole(10) || user.isHasRole(13) || user.isHasRole(14)) ? "<a href='javascript:openQuesTask(".concat(supervisionList.id).concat(")'>办理</a>") : ""}
            ${supervisionList.state == false ? "<a href='javascript:popWindow(module/sub/supervision_createProjectSupervision.action?project.projectId=".concat(projectSupervision.projectId).concat("&projectSupervision.id=").concat(supervisionList.id).concat("'>办理</a>") : ''}
        </display:column>
        </s:if> --%>
        <display:setProperty name="export.excel.filename" value="${project.projectName}督查记录.xls" />
    </display:table>
    <div class="backTop">
        <i class='glyphicon glyphicon-arrow-up'></i>
    </div>
    <div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
    <script type="text/javascript">
        function openQuesTask(supervisionId) {
        	popWindow('module/sub/supervision_createProjectSupervision.action?project.projectId=<s:property value="projectSupervision.projectId"/>&projectSupervision.id=' + supervisionId +'&redirect=<s:property value="redirect"/>', 1000, 650,'<s:text name="sys.project.supervision.management"></s:text>', 'BudgetUpload', true);
    	}
        function deleteSupervision(supervisionId) {
        	$.ajax({
        		url: 'ajax/supervisionAjax_deleteProjectSupervision.action',
        		data: {"projectSupervision.id" : supervisionId},
        		success: function(data) {
        			data = data || {};
        			alert(data.message || (data.result == "success" ? "处理成功" : "处理失败"));
    				if(data.result == "success") {
    					window.location.reload();
        			}
        		}
        	})
        }
    </script>
</body>
</html>