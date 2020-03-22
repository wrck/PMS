<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
  <head>
    <script>
    var url = '<s:property value="redirect"/>';
    url = url.replace(new RegExp("&amp;", 'gm') ,"&");//IE跳转正常、其他浏览器会...
    url = url.replace(new RegExp("＆", 'gm') ,"&");
    if (url) {
    	if (url.indexOf("${pageContext.request.contextPath}/") <= 1) {
    		window.parent.location = url;
    	} else {
            window.parent.location = '${pageContext.request.contextPath}/' + url;
    	}
    	// 存在hash值时业务不会跳转，需要进行hash判断进行刷新
    	if (window.parent.location.hash) {
    		window.parent.location.reload();
    	}
    } else {
    	window.parent.location.reload();
    }
    </script>
  </head>
</html>