<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib  prefix="dp" uri="/myTag" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title"></spring:message> | 500 Error</title>
<cssTag>
    <shiro:guest>
        <link rel="stylesheet" href="http://${header['host']}${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.min.css">
        <dp:link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css" />
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
			<h1>500 错误页面</h1>
			<ul class="breadcrumb">
		        <shiro:authenticated>
		           <li><a href="${pageContext.request.contextPath}/success.html"><i class="fa fa-dashboard"></i> 首页</a></li>
		        </shiro:authenticated>
		        <li class="active">500 Error</li>
	        </ul>
		</section>
    </shiro:authenticated>
	<!-- Main content -->
	<section class="content">
        <div class="box box-danger">
            <div class="error-page box-header with-border mt-0">
				<h2 class="headline text-red">500</h2>
	
				<div class="error-content">
	                <h3>
	                    <i class="fa fa-warning text-red"></i>
	                    <shiro:authenticated>
	                        <shiro:principal property="userName"></shiro:principal>-
	                        <shiro:principal property="realName"></shiro:principal>，
	                    </shiro:authenticated>
	                                                                      出错啦！
	                </h3>
					<p>
					                很抱歉，服务器偷了会懒，发生了错误。<br>
					                错误信息：${error}<br>
					                您可以<a href="JavaScript:void(0)" onclick="window.history.back(-1)">返回上一页</a><shiro:authenticated>或者<a href="${pageContext.request.contextPath}/success.html">返回首页</a></shiro:authenticated><br>
	                                                                        如有疑问请携带ID：<span class="text-red">${errorLogId}</span>联系管理员。
					</p>
				</div>
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