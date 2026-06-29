<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title"></spring:message></title>
</head>
<body>
	<section class="content-header">
		<h1>
			首页<small>欢迎页面</small>
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i>首页</a></li>
			<li class="active"><a href="#">欢迎页面</a></li>
		</ol>
	</section>
	<!-- Main content -->
	<section class="content"> <!-- Default box -->
		<div class="box box-primary">
			<div class="box-header with-border">
				<h2>
					<spring:message code="welcome"/>
					<shiro:principal property="userName"></shiro:principal>-
					<shiro:principal property="realName"></shiro:principal>
				</h2>
			</div>
		</div>
	</section>
</body>
</html>