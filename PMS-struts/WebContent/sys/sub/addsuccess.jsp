<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
  <head>
    <script>
    var url = '<s:property value="redirect"/>';
    url = url.replace(new RegExp("&amp;", 'gm') ,"&");//IE跳转正常、其他浏览器会...
    url = url.replace(new RegExp("＆", 'gm') ,"&");
    top.window.location='${pageContext.request.contextPath}/'+url;
    </script>
  </head>
</html>