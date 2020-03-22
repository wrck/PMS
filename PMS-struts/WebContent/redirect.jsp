<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<script>
if('<s:property value="redirecturl"/>'==""){
	top.window.location= 'sys/Password.action';
}else{
	top.window.location='<s:property value="redirecturl"/>';	
}

</script>
</head>
</html>
