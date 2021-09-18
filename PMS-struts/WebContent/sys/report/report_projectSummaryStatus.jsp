<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='pm.data.analysis' />">
<head>
<style type="text/css">
    #projectSummaryStatusTable.text-center th{
        text-align: center;
    }
</style>
</head>
<body>
    <s:if test="%{data == 'info'}">
        <display:table name="dataList" pagesize="${dataList.size()}" id="projectSummaryStatusTable"
            size="${dataList.size()}" sort="external" export="true"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
            requestURI="module/sub/report_projectSummaryStatus.action"
            partialList="true">
            <display:column property="projectCode" titleKey="pm.project.projectCode"></display:column>
            <display:column property="contractNo" titleKey="pm.contract" decorator="com.dp.plat.decorators.ContractNoList" media="html"></display:column>
            <display:column property="contractNo" titleKey="pm.contract" media="excel"></display:column>
            <display:column property="projectNameWarrper" titleKey="pm.project.projectName" media="html"></display:column>
            <display:column property="projectName" titleKey="pm.project.projectName" media="excel"></display:column>
            <%-- <display:column property="compName" style="width:65px;" titleKey="pm.project.company"></display:column> --%>
            <display:column property="officeName" titleKey="pm.officearea"></display:column>
            <%-- <display:column property="projectStateName" titleKey="pm.project.state"></display:column>
            <display:column property="executionStateName" titleKey="pm.project.executionState"></display:column>
            <display:column property="closeProcessStateName" titleKey="pm.project.closeProcessState"></display:column>
            <display:column property="projectPlanStateName" titleKey="pm.current.task"></display:column> --%>
            <display:setProperty name="export.excel.filename" value='项目状态清单.xls'></display:setProperty>
        </display:table>
        
        <script>
            function updateProject(obj){
            	popWindow("module/sub/ProjectModify.action?project.paramId="+obj, "90vw", 650,'项目状态清单', 'SummaryProject', true);
            }
        </script>
    </s:if>
    <s:else>
        <nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">
            <div>
                <ul class="nav navbar-nav">
                    <s:iterator value="navTabList" var="nav" status="index">
                        <s:if test="%{#index.index == 2}">
                            <li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','<s:property value='#nav.basicDataAttri1'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                        </s:if>
                        <s:else>
                            <li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','<s:property value='#nav.basicDataAttri1'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                        </s:else>
                    </s:iterator>
                </ul>
            </div>
        </nav>
        <display:table name="dataList" pagesize="${dataList.size()}" id="projectSummaryStatusTable"
            size="${dataList.size()}" sort="external" export="true"
            decorator="com.dp.plat.decorators.Wrapper" class="displayTable table text-center"
            requestURI="module/report_projectSummaryStatus.action"
            partialList="true">
            <display:column property="officeName" titleKey="pm.project.officeName"></display:column>
            <display:column property="expendSummaryStatus" headerClass="hidden" class="hidden" title="${dataJson}" headerScope="splitCell=true" media="excel"></display:column>
            <display:column property="expendSummaryStatusHtml" headerClass="hidden" class="hidden" title="${dataJson}" headerScope="splitCell=true" media="html"></display:column>
            <display:setProperty name="export.excel.filename" value='项目状态统计.xlsx'></display:setProperty>
        </display:table>
        
        <script type="text/javascript">
            function summaryInfo(_this) {
            	var query = $(_this).data("query") || {};
            	var search = $.param({data: "info", dataJson:JSON.stringify(query)});
            	popWindow('module/sub/report_projectSummaryStatus.action?' + search, "90vw", 650,'项目状态查询', 'SummaryStatus', true);
            }
        </script>
    </s:else>
</body>
</html>