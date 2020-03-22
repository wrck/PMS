<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
  String errmsg = (String)request.getParameter("errmsg");  
%>
<script>
top.window.location= 'Login!start.action<%=errmsg==null?"":"?errmsg="+errmsg%>';
</script>
