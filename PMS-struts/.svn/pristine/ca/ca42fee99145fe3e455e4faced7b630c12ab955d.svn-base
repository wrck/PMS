<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<%  
String realPath = "http://" + request.getServerName() +
	":" + request.getServerPort() + request.getContextPath() + "/";
%> 
<script>
/* 	var status = '<s:property value="param.approveStatus"/>';
	if(status == 1){
		alert("审批完成");
	} */
	var home = "<%=realPath%>";
	var url = '<s:property value="param.formUrl"/>';
	url = url.replace(new RegExp("&amp;", 'gm') ,"&");
	window.location.href=home+url;	
</script>
</head>
</html>
