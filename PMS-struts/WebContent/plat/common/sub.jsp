<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib prefix="decorator"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<dp:base />
<!--sub 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.3.4-dist/css/bootstrap.min.css"/>
<!-- 引入对Bootstrap的自扩展文件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-ex.css"/>
<!-- 可选的Bootstrap主题文件（一般不用引入） -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.3.4-dist/css/bootstrap-theme.min.css"/>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery-ui-1.10.4.custom.min.css"/>

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-2.1.4.min.js" ></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/bootstrap-3.3.4-dist/js/bootstrap.min.js"></script>
<!-- 解决jQueryUI button 与 bootstrap button 的冲突 -->
<script type="text/javascript"> $.fn.bootstrapBtn =$.fn.button.noConflict(); </script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.ui.datepicker-zh-CN.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.10.4.custom.min.js"></script>

<!-- 引入额外扩展js文件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap-ex.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dp-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zxml.src.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zxmlqueue.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>

<!-- 下拉多选 -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/multiselect/jquery.multiselect.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/multiselect/jquery.multiselect.js"></script>
<decorator:head></decorator:head>
</head>
<body>
	<decorator:body></decorator:body>
</body>
</html>
