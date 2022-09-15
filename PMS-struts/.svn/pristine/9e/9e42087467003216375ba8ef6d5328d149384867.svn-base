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
    <s:form method="post" action="module/prob_statistics.action" id="projectForm"
    cssClass="form-horizontal" name="projectForm" enctype="multipart/form-data">
        <div class="form-group">
            <s:hidden name="probStatistic.tabIndex" value="2"/>
            <label for="projectCode" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">项目编码：</label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="projectCode" placeholder="项目编码" name="probStatistic.projectCode" cssClass="form-control" />
            </div>
            <label for="projectName" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">项目名称：</label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="projectName" placeholder="项目名称" name="probStatistic.projectName" cssClass="form-control" />
            </div>
            <label for="officeName" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">办事处：</label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:select cssClass="form-control" name="probStatistic.officeCode" id="officeCode" list="%{departmentList}" listKey="departmentNum" listValue="departmentName" headerKey="" headerValue="-请选择-" theme="simple"></s:select>
            </div>
            <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                <button type="button" id="submit" class="btn btn-default  btn-block btn-sm" onclick="submitProject()"><s:text name='sys.query' /></button>
            </div>
        </div>
    </s:form>
    <display:table name="probProjectList" pagesize="${displayParam.pagesize}"
        export="true" size="${displayParam.totalcount}" sort="external"
        requestURI="module/prob_statistics.action"
        decorator="com.dp.plat.decorators.Wrapper"
        class="table table-striped" partialList="true">
        <display:column property="projectCode" titleKey="pm.project.projectCode" ></display:column>
        <display:column property="projectName" escapeXml="true" media="html" titleKey="pm.project.projectName" url="/module/ProjectModify.action" paramId="project.projectId" paramProperty="projectId"></display:column>
        <display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
        <display:column property="contractNo" titleKey="pm.project.contractNo"></display:column>
        <display:column property="officeName" titleKey="pm.officearea" class="nowrap"></display:column>
    </display:table>
</body>
</html>