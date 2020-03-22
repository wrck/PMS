<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title"></spring:message> | Unauthorized</title>
<cssTag>
    <shiro:guest>
        <link rel="stylesheet" href="http://${header['host']}${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
        <!-- Theme style -->
        <link rel="stylesheet" href="http://${header['host']}${pageContext.request.contextPath}/static/dist/css/AdminLTE.min.css">
        <!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
        <link rel="stylesheet" href="http://${header['host']}${pageContext.request.contextPath}/static/dist/css/skins/_all-skins.min.css">
    </shiro:guest>
    <style type="text/css">
         .box{
             display: flex;
             display: -webkit-flex;
             align-content: center;
             justify-content: center;
         }
     </style>
</cssTag>
</head>
<body>
    <shiro:authenticated>
    <section class="content-header">
	    <h1>禁止访问页面</h1>
	    <ul class="breadcrumb">
	       <shiro:authenticated>
	           <li><a href="${pageContext.request.contextPath}/success.html"><i class="fa fa-dashboard"></i> 首页</a></li>
	       </shiro:authenticated>
	       <li class="active">禁止访问</li>
	    </ul>
    </section>
	</shiro:authenticated>
    <!-- Main content -->
    <section class="content" >
        <div class="box box-primary">
		    <div class="error-page box-header with-border mt-0">
		        <h2 class="headline text-danger headline-ex">Access Forbidden</h2>
		        <div class="error-content">
		            <h3>
		                <i class="fa fa-warning text-danger"></i>
		                <shiro:authenticated>
			                <shiro:principal property="userName"></shiro:principal>-
		                    <shiro:principal property="realName"></shiro:principal>，
	                    </shiro:authenticated>
	                                                                       您没有访问该页面的权限。
		            </h3>
		            <p>
				                很抱歉，您没有权限访问该页面。<shiro:authenticated>您可以<a href="${pageContext.request.contextPath}/success.html">返回首页</a></shiro:authenticated><br>
				                如有疑问请联系管理员。
		            </p>
		        </div>
		        <!-- /.error-content -->
	        </div>
	    </div>
    </section>
</body>
<jsTag>
    <script type="text/javascript">
	    $(function(){
	    	var wrapperHt = $(".content-wrapper").innerHeight();
	    	var contentHeaderHt = $(".content-header").outerHeight();
	    	var contentPt = $(".error-page").parents(".content").css("paddingTop").replace("px", "");
	    	var contentPb = $(".error-page").parents(".content").css("paddingBottom").replace("px", "");
	    	var boxBt = $(".error-page").parents(".box").css("borderTopWidth").replace("px", "");
	    	$(".error-page").outerHeight(wrapperHt - contentHeaderHt - contentPt - contentPb - boxBt);
	    })
    </script>
</jsTag>
</html>