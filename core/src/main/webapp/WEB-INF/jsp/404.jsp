<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title"></spring:message> | 404 Error</title>
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
			<h1>404错误页面</h1>
			<ul class="breadcrumb">
	            <shiro:authenticated>
	               <li><a href="${pageContext.request.contextPath}/success.html"><i class="fa fa-dashboard"></i> 首页</a></li>
	            </shiro:authenticated>
	            <li class="active">404 Error</li>
	        </ul>
		</section>
    </shiro:authenticated>
	<!-- Main content -->
	<section class="content">
		<div class="box box-warning">
            <div class="error-page box-header with-border mt-0">
				<h2 class="headline text-yellow  headline-ex">404</h2>
				<div class="error-content mt-3">
					<h3>
						<i class="fa fa-warning text-yellow"></i>
						<shiro:authenticated>
                            <shiro:principal property="userName"></shiro:principal>-
                            <shiro:principal property="realName"></shiro:principal>，
                        </shiro:authenticated>
                                                                                     页面走丢了！
					</h3>
					<p>
						很抱歉，没有找到您要访问的页面. <br>
						您可以<a href="JavaScript:void(0)" onclick="window.history.back(-1)">返回上一页</a><shiro:authenticated>或者<a href="${pageContext.request.contextPath}/success.html">返回首页</a></shiro:authenticated><br>
					</p>
				</div>
				<!-- /.error-content -->
			</div>
			<!-- /.error-page -->
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
            $(".error-page").parents(".box").outerHeight(wrapperHt - contentHeaderHt - contentPt - contentPb - boxBt);
        })
    </script> 
</jsTag>
</html>