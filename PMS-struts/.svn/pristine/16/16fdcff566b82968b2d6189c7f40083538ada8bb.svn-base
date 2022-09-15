<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='prob.manage' />">
<style>
    .btn-link{
        margin-right:0.5rem;
    }
    table thead th, .nowrap{
        white-space: nowrap;
    }
</style>
<script type="text/javascript">
    $(function(){
        date_picker("startTime");
        date_picker("endTime");
    });
</script>
</head>
<body>
    <div class="container-flux">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="text-align: left;">
                <div id="myTabContent" class="tab-content">
                    <nav class="navbar navbar-default" role="navigation">
                        <ul id="myTab" class="nav navbar-nav">
                            <li class="active"><a href="#statistic" data-toggle="tab" class="tab-bg-primary"><s:text name='prob.statistic.tab.list' /></a></li>
                            <li><a href="#projectList" data-toggle="tab" class="tab-bg-primary"><s:text name='prob.statistic.tab.report' /></a></li>
                            <li><a href="#projectList" data-toggle="tab" class="tab-bg-primary"><s:text name='prob.statistic.tab.project' /></a></li>
                        </ul>
                    </nav>
                    <s:form method="get" action="module/prob_statistics.action" id="mainForm"
                    cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="executeTime" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">统计日期：</label>
                            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                <s:textfield id="startTime" placeholder="开始时间" name="probStatistic.startTime" cssClass="form-control" >
                                    <s:param name="value">
                                        <s:date name="probStatistic.startTime" format="yyyy-MM-dd"/>
                                    </s:param>
                                </s:textfield>
                            </div>
                            <div class="pull-left"><span style="margin: 8px 5px;display:inline-block;">----</span></div>
                            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                <s:textfield id="endTime" placeholder="结束时间"  name="probStatistic.endTime" cssClass="form-control">
                                    <s:param name="value">
                                        <s:date name="probStatistic.endTime" format="yyyy-MM-dd"></s:date>
                                    </s:param>
                                </s:textfield>
                            </div>
                            <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                <button type="submit" id="submit"  class="btn btn-default  btn-block btn-sm"><s:text name='sys.query' /></button>
                            </div>
                            <div class="pull-left">
                                <label for="mainForm_probStatistic_autoAdjust" style="margin: 4px 5px;">
                                    <s:checkbox name="probStatistic.autoAdjust"></s:checkbox><s:text name="prob.statistic.autoAdjust"></s:text>
                                </label>
                            </div>
                        </div>
                    </s:form>
                    <div class="tab-pane fade in active" id="statistic">
                        <display:table name="${probStatisticList}" pagesize="${displayParam.pagesize}"
                            export="true" size="${displayParam.totalcount}" sort="external"
                            requestURI="module/prob_statistics.action"
                            decorator="com.dp.plat.decorators.Wrapper"
                            class="table table-striped" partialList="true" >
                            <display:column property="projectCode"  media="excel" titleKey="pm.project.projectCode" ></display:column>
                            <display:column property="projectName" escapeXml="true" media="html" titleKey="pm.project.projectName" url="/module/ProjectModify.action" paramId="project.projectId" paramProperty="projectId"></display:column>
                            <display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
                            <display:column property="itemName" titleKey="pm.officearea"></display:column>
                            <display:column property="softInfo" titleKey="prob.statistic.softInfo" style="white-space: pre-line;word-break: break-word;word-wrap: break-word;"></display:column>
                            <display:column property="officeName" titleKey="pm.officearea" class="nowrap"></display:column>
                            <display:column property="serviceManagerName" titleKey="pm.project.serviceManager" class="nowrap"></display:column>
                            <display:column property="programManagerNameA" titleKey="pm.project.programManagerA" class="nowrap"></display:column>
                            <display:column property="programManagerNameB" titleKey="pm.project.programManagerB" class="nowrap"></display:column>
                            <display:column property="updateCount" titleKey="prob.statistic.updateCount"></display:column>
                            <display:column property="executeTime" titleKey="prob.statistic.executeTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
                            <display:column property="probTheme" media="html" titleKey="prob.statistic.probTheme" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
                            <display:column property="probTheme" media="excel" titleKey="prob.statistic.probTheme" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
                        </display:table>
                    </div>
                    <div class="tab-pane" id="projectList">
                        <display:table name="${probStatisticList}" pagesize="${displayParam.pagesize }"
                            export="true" size="${displayParam.totalcount }" sort="external"
                            requestURI="module/prob_statistics.action"
                            decorator="com.dp.plat.decorators.Wrapper"
                            class="table table-striped" partialList="true" >
                            <display:column property="projectCode"  media="excel" titleKey="pm.project.projectCode" ></display:column>
                            <display:column property="projectName" escapeXml="true" media="html" titleKey="pm.project.projectName" url="/module/ProjectModify.action" paramId="project.projectId" paramProperty="projectId"></display:column>
                            <display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
                            <display:column property="itemName" titleKey="pm.officearea"></display:column>
                            <display:column property="softInfo" titleKey="prob.statistic.softInfo" style="white-space: pre-line;word-break: break-word;word-wrap: break-word;"></display:column>
                            <display:column property="officeName" titleKey="pm.officearea" class="nowrap"></display:column>
                            <display:column property="serviceManagerName" titleKey="pm.project.serviceManager" class="nowrap"></display:column>
                            <display:column property="programManagerNameA" titleKey="pm.project.programManagerA" class="nowrap"></display:column>
                            <display:column property="programManagerNameB" titleKey="pm.project.programManagerB" class="nowrap"></display:column>
                            <display:column property="updateCount" titleKey="prob.statistic.updateCount"></display:column>
                            <display:column property="executeTime" titleKey="prob.statistic.executeTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
                            <display:column property="probTheme" media="html" titleKey="prob.statistic.probTheme" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
                            <display:column property="probTheme" media="excel" titleKey="prob.statistic.probTheme" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
                        </display:table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>