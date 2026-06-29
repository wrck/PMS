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
    <s:form method="post" action="prob_readLog.action" id="probReadLogForm"
    cssClass="form-horizontal" name="probReadLogForm" enctype="multipart/form-data">
        <div class="form-group">
            <s:hidden name="probReadLog.probId" />
            <label for="reader" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">阅读人：</label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="reader" placeholder="阅读人" name="probReadLog.reader" cssClass="form-control" />
            </div>
            <label for="readStatus" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">阅读状态：</label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:select cssClass="form-control" name="probReadLog.status" id="readStatus" list='#{0: "未确认", 1: "已确认"}' headerKey="" headerValue="-请选择-" theme="simple"></s:select>
            </div>
            <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                <button id="submit" class="btn btn-default  btn-block btn-sm" ><s:text name='sys.query' /></button>
            </div>
        </div>
    </s:form>
    <display:table id="readLogList" name="readLogList" pagesize="${displayParam.pagesize}"
        export="true" size="${displayParam.totalcount}" sort="external"
        requestURI="prob_readLog.action" decorator="com.dp.plat.prob.descorators.Wrapper"
        class="table table-striped" partialList="true">
        <display:column property="readerName" title="阅读人" ></display:column>
        <display:column property="readTime" title="阅读时间" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <display:column property="commitTime" title="确认时间" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
        <display:column title="阅读状态">${readLogList.status == 1 ? "已确认" : "未确认"}</display:column>
        <display:column property="probReadCommitInterval" title="确认时长" ></display:column>
    </display:table>
</body>
</html>