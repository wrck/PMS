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
    <display:table id="notificationList" class="table table-striped"
        name="notificationList" pagesize="${notificationList.size()}" 
        size="${notificationList.size()}" sort="external" export="false"
        partialList="true">
        <display:column property="notifySubject" titleKey="pm.notification.subject"></display:column>
        <display:column property="notifyContent" titleKey="pm.notification.content"></display:column>
        <display:column property="createBy" titleKey="sys.create.by"></display:column>
        <display:column property="createTime" titleKey="sys.create.time" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
    </display:table>
    <div class="backTop">
        <i class='glyphicon glyphicon-arrow-up'></i>
    </div>
    <div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
</body>
</html>